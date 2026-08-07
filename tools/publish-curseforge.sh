#!/usr/bin/env bash
#
# Upload a built jar to CurseForge.
#
# Why this exists instead of ./gradlew publishUnified:
#   CurseForge requires every file to carry a version from its "environment" group (Client and/or
#   Server) and rejects the upload with error 1021 without one. The cursegradle bundled inside
#   unified-publishing 0.1.13 predates that version type (75208) and filters it out of the list it
#   validates against, so passing "Client" fails before anything is uploaded. There is no fix at
#   that plugin version. This talks to the documented upload API directly.
#
#   It also sends the Java version, which the Gradle path never did -- that is the field you would
#   otherwise have to set by hand on the site after every release.
#
# Version names are resolved to ids from the live API rather than hardcoded, so this keeps working
# when CurseForge renumbers things.
#
# Usage:
#   tools/publish-curseforge.sh --dry-run    # resolve ids, build metadata, upload nothing
#   tools/publish-curseforge.sh              # upload for real
#
set -euo pipefail

cd "$(dirname "$0")/.."

DRY_RUN=0
[[ "${1:-}" == "--dry-run" ]] && DRY_RUN=1

# Trim the ends only. Stripping every space would turn "Stuff and Things" into "StuffandThings".
prop() {
    grep -E "^[[:space:]]*$1[[:space:]]*=" "$2" | head -1 | cut -d= -f2- | tr -d '\r' \
        | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'
}

MOD_VERSION=$(prop mod_version gradle.properties)
MC_VERSION=$(prop minecraft_version gradle.properties)
PROJECT_ID=$(prop curseforge_id gradle.properties)
MOD_NAME=$(prop mod_name gradle.properties)
MOD_ID=$(prop mod_id gradle.properties)
JAVA_VERSION="Java 21"
LOADER="NeoForge"

# Comma-separated CurseForge slugs, empty or absent when the mod has no dependencies. The Gradle
# path declares these for Modrinth; CurseForge needs them in this payload or the file uploads with
# no dependency listed.
CF_REQUIRED_DEPS=$(prop curseforge_required_deps gradle.properties)

if [[ ! -f secrets.properties ]]; then
    echo "error: secrets.properties not found; cf_token must live there" >&2
    exit 1
fi
CF_TOKEN=$(prop cf_token secrets.properties)

JAR="build/libs/${MOD_ID}-neoforge-${MC_VERSION}-${MOD_VERSION}.jar"
CHANGELOG="changelog/${MC_VERSION}-${MOD_VERSION}.md"

[[ -f "$JAR" ]]       || { echo "error: jar not found: $JAR (run ./gradlew build)" >&2; exit 1; }
[[ -f "$CHANGELOG" ]] || { echo "error: changelog not found: $CHANGELOG" >&2; exit 1; }

WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

echo "resolving game version ids from CurseForge..."
HTTP=$(curl -s -o "$WORK/versions.json" -w "%{http_code}" \
    -H "X-Api-Token: $CF_TOKEN" \
    https://minecraft.curseforge.com/api/game/versions)

if [[ "$HTTP" != "200" ]]; then
    echo "error: CurseForge API returned HTTP $HTTP" >&2
    case "$HTTP" in
        400) echo "  The token is the wrong kind. cf_token must be the UUID-style upload token from" >&2
             echo "  legacy.curseforge.com/account/api-tokens, not the \$2a\$ Core API key from" >&2
             echo "  console.curseforge.com." >&2 ;;
        403) echo "  The token was rejected -- most likely revoked or replaced. Generate a new one at" >&2
             echo "  legacy.curseforge.com/account/api-tokens." >&2 ;;
    esac
    head -c 300 "$WORK/versions.json" >&2; echo >&2
    exit 1
fi

python3 - "$WORK" "$MC_VERSION" "$LOADER" "$JAVA_VERSION" "$MOD_NAME" "$MOD_VERSION" "$CHANGELOG" "$CF_REQUIRED_DEPS" <<'PY'
import json, sys

work, mc, loader, java, mod_name, mod_version, changelog_path, required_deps = sys.argv[1:9]
versions = json.load(open(f"{work}/versions.json"))

# The Minecraft version appears under several types (Addons, None, the real Minecraft one).
# Take it from the "Minecraft <major>" type, which is the one that shows on the file listing.
MINECRAFT_TYPES = {v["gameVersionTypeID"] for v in versions
                   if v["name"] == mc and v["gameVersionTypeID"] > 70000}

def find(name, want_minecraft_type=False):
    hits = [v for v in versions if v["name"] == name]
    if want_minecraft_type and MINECRAFT_TYPES:
        hits = [v for v in hits if v["gameVersionTypeID"] in MINECRAFT_TYPES] or hits
    if not hits:
        raise SystemExit(f"error: CurseForge has no game version named {name!r}")
    return hits[0]["id"]

ids = {
    mc:       find(mc, want_minecraft_type=True),
    loader:   find(loader),
    "Client": find("Client"),
    "Server": find("Server"),
    java:     find(java),
}
for name, i in ids.items():
    print(f"  {name:10} -> {i}")

meta = {
    "changelog": open(changelog_path, encoding="utf-8").read(),
    "changelogType": "markdown",
    "displayName": f"{mod_name} - {mc} - {mod_version}",
    "gameVersions": sorted(ids.values()),
    "releaseType": "release",
}

slugs = [s.strip() for s in required_deps.split(",") if s.strip()]
if slugs:
    meta["relations"] = {"projects": [{"slug": s, "type": "requiredDependency"} for s in slugs]}
    for s in slugs:
        print(f"  requires   -> {s}")

open(f"{work}/metadata.json", "w", encoding="utf-8").write(json.dumps(meta))
PY

echo
echo "  jar        : $JAR ($(du -h "$JAR" | cut -f1))"
echo "  project    : $PROJECT_ID"
echo "  displayName: ${MOD_NAME} - ${MC_VERSION} - ${MOD_VERSION}"

if [[ $DRY_RUN -eq 1 ]]; then
    echo
    echo "dry run: nothing uploaded."
    exit 0
fi

echo
echo "uploading..."
HTTP=$(curl -s -o "$WORK/response.json" -w "%{http_code}" \
    -H "X-Api-Token: $CF_TOKEN" \
    -F "metadata=<$WORK/metadata.json" \
    -F "file=@$JAR" \
    "https://minecraft.curseforge.com/api/projects/${PROJECT_ID}/upload-file")

echo "HTTP $HTTP"
cat "$WORK/response.json"; echo
[[ "$HTTP" == "200" ]] || exit 1
