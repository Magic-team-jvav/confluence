package org.confluence.mod.common.entity.flail;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.init.ModDamageTypes;

/**
 * 锚的直接发射、重力和落点冲击行为。
 */
public final class AnchorFlailEntity extends LaunchedFlailEntity {
    public AnchorFlailEntity(
            EntityType<? extends AnchorFlailEntity> type,
            Level level
    ) {
        super(type, level, 0.05);
    }

    @Override
    protected void onLaunchedBlockImpact(
            Player player,
            FlailComponent component,
            BlockHitResult hit
    ) {
        if (tickCount <= 4) {
            return;
        }
        float damage = component.damageFactor()
                * (float) player.getAttributeValue(
                net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                * 0.6F;
        AABB area = getBoundingBox().inflate(2.0, 0.5, 2.0);
        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                candidate -> LibEntityUtils.canHitEntity(
                        candidate, this))) {
            target.hurt(
                    ModDamageTypes.of(
                            level(),
                            ModDamageTypes.SWORD_PROJECTILE,
                            this,
                            player),
                    damage);
        }
        level().playSound(
                null,
                blockPosition(),
                SoundEvents.ANVIL_LAND,
                SoundSource.PLAYERS,
                0.8F,
                0.9F + random.nextFloat() * 0.2F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(
                            ParticleTypes.BLOCK,
                            Blocks.COBBLESTONE.defaultBlockState()),
                    getX(),
                    getY() + 0.5,
                    getZ(),
                    60,
                    2.0,
                    0.5,
                    2.0,
                    0.2);
        }
    }
}
