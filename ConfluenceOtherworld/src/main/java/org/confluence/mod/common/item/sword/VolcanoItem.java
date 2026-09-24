package org.confluence.mod.common.item.sword;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.mixed.Immunity;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;

public class VolcanoItem extends BaseSwordItem {
    private static final int FIRE_DURATION = 60;
    private static final float EXPLOSION_DAMAGE_FACTOR = 0.75F;
    private static final double EXPLOSION_RADIUS = 5.0;
    private final Map<LivingEntity, Long> lastExplosionTick = new WeakHashMap<>();

    public VolcanoItem() {
        super(ModTiers.UNBREAKABLE, ModRarity.ORANGE, 25, 1.2F, SwordDefinition.builder()
                        .tooltipImage()
                        .attribute(Attributes.ENTITY_INTERACTION_RANGE, 4.0F, PortAttributeModifier.Operation.ADD_VALUE)
                        .attribute(Attributes.ATTACK_KNOCKBACK, 0.5F, PortAttributeModifier.Operation.ADD_VALUE)
                .specialSweep(0.8F));
    }

    @Override
    protected void onDamage(ItemStack weapon, LivingEntity attacker, LivingEntity victim, DamageSource source) {
        if (attacker.getRandom().nextBoolean()) victim.setRemainingFireTicks(FIRE_DURATION);
    }

    /// 在主攻击结算后触发，避免爆炸先打中目标而吞掉剑本身的伤害。
    public void afterSuccessfulDamage(LivingEntity attacker, LivingEntity victim) {
        if (!(attacker.level() instanceof ServerLevel level)) return;
        long tick = level.getGameTime();
        if (lastExplosionTick.getOrDefault(attacker, Long.MIN_VALUE) == tick) return;
        lastExplosionTick.put(attacker, tick);

        DamageSource explosion = LibDamageTypes.of(level, LibDamageTypes.SWORD_PROJECTILE, attacker, attacker);
        float damage = (float) attacker.getAttributeValue(LibAttributes.getAttackDamage()) * EXPLOSION_DAMAGE_FACTOR;
        Immunity localHit = new Immunity() {
            @Override
            public Type confluence$getImmunityType() {return Type.LOCAL;}

            @Override
            public int confluence$getImmunityDuration(DamageSource source) {return 0;}
        };
        /// 命中目标加最多两个附近目标；局部结算不会清掉主攻击的原版受伤帧。
        hitExplosion(localHit, victim, explosion, damage);
        level.getEntitiesOfClass(LivingEntity.class, new AABB(victim.position(), victim.position()).inflate(EXPLOSION_RADIUS),
                        other -> other != victim && other.isAlive() && ProjectileHitRules.canHit(attacker, other))
                .stream().sorted(Comparator.comparingDouble(other -> other.distanceToSqr(victim)))
                .limit(2).forEach(other -> hitExplosion(localHit, other, explosion, damage));
        level.sendParticles(ParticleTypes.EXPLOSION, victim.getX(), victim.getY(0.5), victim.getZ(), 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.FLAME, victim.getX(), victim.getY(0.5), victim.getZ(), 16, 1.0, 1.0, 1.0, 0.04);
        level.playSound(null, victim.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.6F, 1.2F);
    }

    private static void hitExplosion(Immunity cause, LivingEntity target, DamageSource source, float damage) {
        if (Immunity.hurt(cause, target, source, damage))
            target.setRemainingFireTicks(FIRE_DURATION);
    }
}
