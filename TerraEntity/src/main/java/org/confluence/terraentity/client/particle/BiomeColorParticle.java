package org.confluence.terraentity.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeColorParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    float _quadSize;
    public BiomeColorParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.lifetime = (int) (20 + Math.random() * 60);
        this.gravity = 0.04F;
        this.friction = 0.95F;
        Vec3 color = Vec3.fromRGB24(level.getBiome(new BlockPos.MutableBlockPos(x, y, z)).value().getFoliageColor());
        setColor((float) color.x, (float) color.y, (float) color.z);
        setSpriteFromAge(sprites);
        this._quadSize = quadSize;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(this.sprites);
        this.quadSize = this._quadSize * (1 - this.age / (float) this.lifetime);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BiomeColorParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
