package com.lithiumcraft.dimension_expansion.util;

public class ParticleColorUtil {
    public static float[] getRGBComponents(int rgbDecimal) {
        float r = ((rgbDecimal >> 16) & 0xFF) / 255.0f;
        float g = ((rgbDecimal >> 8) & 0xFF) / 255.0f;
        float b = (rgbDecimal & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }
}