package org.confluence.mod.client.renderer.entity.bullet;

import net.minecraft.resources.ResourceLocation;

/// Client-side presentation settings for one Terraria-style projectile trail.
record BulletTrailStyle(ResourceLocation trailTexture, ResourceLocation headTexture,
                        float headWidth, float tailWidth, float headSize, float opacity,
                        int maxPoints, boolean additive) {
    BulletTrailStyle {
        if (trailTexture == null || headTexture == null) {
            throw new IllegalArgumentException("Trail textures are required");
        }
        if (!Float.isFinite(headWidth) || headWidth <= 0.0F) {
            throw new IllegalArgumentException("Trail head width must be positive");
        }
        if (!Float.isFinite(tailWidth) || tailWidth < 0.0F || tailWidth > headWidth) {
            throw new IllegalArgumentException("Trail tail width must be in [0, headWidth]");
        }
        if (!Float.isFinite(headSize) || headSize <= 0.0F) {
            throw new IllegalArgumentException("Trail head size must be positive");
        }
        if (!Float.isFinite(opacity) || opacity < 0.0F || opacity > 1.0F) {
            throw new IllegalArgumentException("Trail opacity must be in [0, 1]");
        }
        if (maxPoints < 2) {
            throw new IllegalArgumentException("Trail max points must be at least 2");
        }
    }
}
