package com.lithiumcraft.dimension_expansion.block.property;

import net.minecraft.util.StringRepresentable;

public enum WoodType implements StringRepresentable {
    OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CHERRY, MANGROVE;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}


//package com.lithiumcraft.dimension_expansion.block.property;
//
//import net.minecraft.util.StringRepresentable;
//
//public enum WoodType implements StringRepresentable {
//    OAK("oak"),
//    SPRUCE("spruce"),
//    BIRCH("birch"),
//    JUNGLE("jungle"),
//    ACACIA("acacia"),
//    DARK_OAK("dark_oak"),
//    CHERRY("cherry"),
//    MANGROVE("mangrove");
//
//    private final String name;
//
//    WoodType(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public String getSerializedName() {
//        return this.name;
//    }
//}
