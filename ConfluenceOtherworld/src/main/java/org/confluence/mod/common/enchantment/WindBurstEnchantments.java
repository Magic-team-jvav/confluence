package org.confluence.mod.common.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * <h1>暴风锤 — 风爆附魔效果</h1>
 * 连枷命中实体时触发：GUST_EMITTER 粒子 + WIND_CHARGE_BURST 音效 + 类似风弹的击退效果。
 * <p>
 * 仅实体命中触发，方块命中不执行。
 */
public final class WindBurstEnchantments {

    public record WindBurstAtHitEffect() implements EnchantmentEntityEffect {
        public static final MapCodec<WindBurstAtHitEffect> CODEC = MapCodec.unit(new WindBurstAtHitEffect());

        /** 击退半径 */
        private static final double KNOCKBACK_RADIUS = 3.0;
        /** 水平击退力度 */
        private static final double KNOCKBACK_HORIZONTAL = 1.2;
        /** 垂直击退力度 */
        private static final double KNOCKBACK_VERTICAL = 0.6;

        @Override
        public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity affected, Vec3 origin) {
            @Nullable Entity owner = item.owner();

            // ── 粒子：GUST_EMITTER ──
            level.sendParticles(ParticleTypes.GUST_EMITTER_LARGE,
                    origin.x, origin.y, origin.z,
                    1, 0.0, 0.0, 0.0, 0.05);

            // ── 音效：WIND_CHARGE_BURST ──
            level.playSound(null, origin.x, origin.y, origin.z,
                    SoundEvents.WIND_CHARGE_BURST, affected.getSoundSource(),
                    1.5F, 0.8F + level.random.nextFloat() * 0.4F);

            // ── 风弹式击退 ──
            double power = 1.0 + enchantmentLevel * 0.6;
            AABB area = new AABB(
                    origin.x - KNOCKBACK_RADIUS, origin.y - KNOCKBACK_RADIUS, origin.z - KNOCKBACK_RADIUS,
                    origin.x + KNOCKBACK_RADIUS, origin.y + KNOCKBACK_RADIUS, origin.z + KNOCKBACK_RADIUS
            );

            for (Entity entity : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != owner && e.isAlive())) {
                Vec3 entityCenter = entity.position().add(0, entity.getBbHeight() / 2.0, 0);
                double dist = entityCenter.distanceTo(origin);
                if (dist < 0.01 || dist > KNOCKBACK_RADIUS) continue;

                double decay = 1.0 - dist / KNOCKBACK_RADIUS;
                Vec3 dir = entityCenter.subtract(origin).normalize();
                double knock = decay * power;

                entity.push(
                        dir.x * KNOCKBACK_HORIZONTAL * knock,
                        KNOCKBACK_VERTICAL * knock,
                        dir.z * KNOCKBACK_HORIZONTAL * knock
                );
                entity.hurtMarked = true;
                entity.fallDistance = 0.0F;
            }
        }

        @Override
        public MapCodec<WindBurstAtHitEffect> codec() {
            return CODEC;
        }
    }
}
