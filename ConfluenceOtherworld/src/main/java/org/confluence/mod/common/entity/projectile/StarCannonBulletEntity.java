package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.entity.bullet.CustomBulletEntity;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class StarCannonBulletEntity extends CustomBulletEntity {
    private ParticleEmitter emitter;

    public StarCannonBulletEntity(EntityType<? extends BaseBulletEntity> type, Level level) {
        super(type, level);
    }

    public StarCannonBulletEntity(LivingEntity owner, float gravity, ItemStack bullet) {
        super(ModEntities.STAR_CANNON_BULLET.get(), owner, gravity, bullet);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && emitter == null) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("falling_star"));
            emitter.attachEntity(this);
            PSGameClient.LOADER.addEmitter(emitter, false);
        }
    }
}
