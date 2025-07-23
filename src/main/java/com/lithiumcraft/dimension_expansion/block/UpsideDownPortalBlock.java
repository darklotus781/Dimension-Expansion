package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.lithiumcraft.dimension_expansion.util.UpsideDownPortalShape;
import com.lithiumcraft.dimension_expansion.util.UpsideDownPortalTeleporter;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

public class UpsideDownPortalBlock extends NetherPortalBlock {

    public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final VoxelShape X_SHAPE = Block.box(0, 0, 6, 16, 16, 10);
    private static final VoxelShape Z_SHAPE = Block.box(6, 0, 0, 10, 16, 16);

    public UpsideDownPortalBlock() {
        super(BlockBehaviour.Properties.of()
                .noCollission()
                .randomTicks()
                .pushReaction(PushReaction.BLOCK)
                .strength(-1.0F)
                .noLootTable()
                .sound(SoundType.GLASS)
                .lightLevel(state -> 11)
                .isValidSpawn((state, level, pos, type) -> false)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    public static void portalSpawn(Level level, BlockPos pos) {
        UpsideDownPortalShape.findEmptyPortalShape(level, pos, Axis.X).ifPresent(UpsideDownPortalShape::createPortalBlocks);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction neighborDir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos fromPos) {
        Axis axis = state.getValue(AXIS);
        boolean invalid = neighborDir.getAxis() != axis && neighborDir.getAxis().isHorizontal();

        boolean portalBroken = !neighborState.is(this) && !new UpsideDownPortalShape(level, pos, axis).isComplete();

        return invalid && portalBroken
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, neighborDir, neighborState, level, pos, fromPos);
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        int delay;
        if (entity instanceof Player player) {
            delay = Math.max(1, level.getGameRules().getInt(player.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY));
        } else {
            delay = 0;
        }

        return delay;
    }

//    @Override
//    public Portal.Transition getLocalTransition() {
//        return Transition.CONFUSION;
//    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel sourceLevel, Entity entity, BlockPos portalPos) {
        boolean isInUpsideDown = sourceLevel.dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN);
        ResourceKey<Level> targetKey = isInUpsideDown ? Level.OVERWORLD : DimensionExpansionDimensions.UPSIDE_DOWN;
        ServerLevel targetLevel = sourceLevel.getServer().getLevel(targetKey);

        if (targetLevel == null) return null;

        boolean goingToUpsideDown = targetKey.equals(DimensionExpansionDimensions.UPSIDE_DOWN);
        double scale = DimensionType.getTeleportationScale(sourceLevel.dimensionType(), targetLevel.dimensionType());

        BlockPos targetOrigin = targetLevel.getWorldBorder().clampToBounds(
                entity.getX() * scale,
                entity.getY(),
                entity.getZ() * scale
        );

        return getExitPortal(targetLevel, entity, portalPos, targetOrigin, goingToUpsideDown, targetLevel.getWorldBorder());
    }

    @Nullable
    private DimensionTransition getExitPortal(
            ServerLevel targetLevel, Entity entity, BlockPos sourcePortalPos, BlockPos targetPos, boolean toUpsideDown, WorldBorder border
    ) {
        UpsideDownPortalTeleporter teleporter = new UpsideDownPortalTeleporter(targetLevel);
        Optional<BlockPos> existing = teleporter.findClosestPortalPosition(targetPos, toUpsideDown, border);

        BlockUtil.FoundRectangle portalShape;
        DimensionTransition.PostDimensionTransition postTransition;

        if (existing.isPresent()) {
            BlockPos existingPortal = existing.get();
            BlockState state = targetLevel.getBlockState(existingPortal);
            Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            portalShape = BlockUtil.getLargestRectangleAround(existingPortal, axis, 21, Axis.Y, 21, pos -> targetLevel.getBlockState(pos) == state);
            postTransition = playCustomPortalSound().then(pt -> pt.placePortalTicket(existingPortal));
        } else {
            Axis axis = entity.level().getBlockState(sourcePortalPos).getOptionalValue(AXIS).orElse(Axis.X);
            Optional<BlockUtil.FoundRectangle> created = teleporter.createPortal(targetPos, axis);

            if (created.isEmpty()) {
                DimensionExpansion.LOGGER.error("Unable to create return portal at {}", targetPos);
                return null;
            }

            portalShape = created.get();
            postTransition = playCustomPortalSound().then(DimensionTransition.PLACE_PORTAL_TICKET);
        }

        return createTransition(entity, sourcePortalPos, portalShape, targetLevel, postTransition);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.dimensionType().natural()
                && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                && random.nextInt(2000) < level.getDifficulty().getId()) {

            while (level.getBlockState(pos).is(this)) {
                pos = pos.below();
            }

            if (level.getBlockState(pos).isValidSpawn(level, pos, EntityType.SKELETON)) {
                Entity entity = EntityType.SKELETON.spawn(level, pos.above(), MobSpawnType.STRUCTURE);
                if (entity instanceof Mob mob) {
                    EquipmentSlot slot = EquipmentSlot.HEAD;

                    // Equip helmet
                    ItemStack helmet = new ItemStack(Items.IRON_HELMET);
                    helmet.setDamageValue(random.nextInt(helmet.getMaxDamage()));
                    mob.setItemSlot(slot, helmet);

                    // Prevent it from dropping
                    mob.setDropChance(slot, 0.0F);

                    // Optional: fire resistance (internal fire only)
                    mob.setRemainingFireTicks(-1);

                    mob.setPortalCooldown();
                }
            }
        }
    }


    private static DimensionTransition createTransition(
            Entity entity,
            BlockPos oldPortalPos,
            BlockUtil.FoundRectangle targetPortal,
            ServerLevel targetLevel,
            DimensionTransition.PostDimensionTransition post
    ) {
        BlockState sourceState = entity.level().getBlockState(oldPortalPos);
        Axis sourceAxis = sourceState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Axis.X);
        Axis actualAxis;
        Vec3 relPos;

        if (sourceState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            actualAxis = sourceAxis;
            BlockUtil.FoundRectangle existing = BlockUtil.getLargestRectangleAround(
                    oldPortalPos, sourceAxis, 21, Axis.Y, 21, pos -> entity.level().getBlockState(pos) == sourceState
            );
            relPos = entity.getRelativePortalPosition(sourceAxis, existing);
        } else {
            actualAxis = Axis.X;
            relPos = new Vec3(0.5, 0.0, 0.0);
        }

        BlockPos corner = targetPortal.minCorner;
        BlockState frameState = targetLevel.getBlockState(corner);
        Axis portalAxis = frameState.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Axis.X);

        double width = targetPortal.axis1Size;
        double height = targetPortal.axis2Size;
        EntityDimensions size = entity.getDimensions(entity.getPose());
        int yawOffset = actualAxis == portalAxis ? 0 : 90;
        Vec3 velocity = actualAxis == portalAxis ? entity.getDeltaMovement() : new Vec3(entity.getDeltaMovement().z, entity.getDeltaMovement().y, -entity.getDeltaMovement().x);

        double offsetX = size.width() / 2.0 + (width - size.width()) * relPos.x();
        double offsetY = (height - size.height()) * relPos.y();
        double offsetZ = 0.5 + relPos.z();

        boolean isX = portalAxis == Axis.X;
        Vec3 teleportTarget = new Vec3(
                corner.getX() + (isX ? offsetX : offsetZ),
                corner.getY() + offsetY,
                corner.getZ() + (isX ? offsetZ : offsetX)
        );

        Vec3 safeTarget = UpsideDownPortalShape.findCollisionFreePosition(teleportTarget, targetLevel, entity, size);
        return new DimensionTransition(targetLevel, safeTarget, velocity, entity.getYRot() + yawOffset, entity.getXRot(), post);
    }

    @Override
    public boolean isPortalFrame(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide) return;

        if (random.nextInt(800) == 0) {
            level.playLocalSound((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, ModSounds.UPSIDE_DOWN_DIMENSION_PORTAL_AMBIENT.get(), SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        int color = DimensionExpansion.PORTAL_COLOR;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double dx = (random.nextDouble() - 0.5) * 0.2;
            double dy = (random.nextDouble() - 0.5) * 0.2;
            double dz = (random.nextDouble() - 0.5) * 0.2;

            level.addParticle(ParticleTypes.RAID_OMEN, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private static DimensionTransition.PostDimensionTransition playCustomPortalSound() {
        return entity -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                ResourceLocation soundId = ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "upside_down_dimension_portal_activate");
                ResourceKey<SoundEvent> soundKey = ResourceKey.create(Registries.SOUND_EVENT, soundId);

                Holder<SoundEvent> soundHolder = serverPlayer.registryAccess().registryOrThrow(Registries.SOUND_EVENT).getHolderOrThrow(soundKey);

                serverPlayer.connection.send(new ClientboundSoundPacket(
                        soundHolder,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        1.0f, 1.0f,
                        serverPlayer.level().getRandom().nextLong()
                ));
            }
        };
    }
}
