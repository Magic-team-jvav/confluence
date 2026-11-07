package org.confluence.mod.common.effect.harmful;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.projectile.mana.LittleSunEntity;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

/// 极昼灼烧：极昼弓命中的敌怪身上留下的印记，分两级。
///
/// 1 级：以自身为中心产生跟随自身的 3x3 光环，每秒对范围内敌怪造成 10% 武器攻击力伤害。
/// 2 级：在 1 级的基础上，每秒迸发出一发日光弹幕（50% 武器攻击力），索敌附近的其它敌怪。
public class PolarMarkEffect extends MobEffect {
    /// 施加印记时记录的武器攻击力与施加者，供印记自身结算伤害
    public static final String DAMAGE_KEY = "ConfluencePolarMarkDamage";
    public static final String OWNER_KEY = "ConfluencePolarMarkOwner";
    /// 2 级印记累计被命中的次数（达到阈值后给予极昼环）
    public static final String HITS_KEY = "ConfluencePolarMarkHits";
    /// 3x3 光环半径
    private static final double AURA_RADIUS = 1.5;
    /// 光环每秒伤害倍率（10%）
    public static final float AURA_FACTOR = 0.1F;
    /// 2 级迸发的日光弹幕伤害倍率（50%）
    public static final float BOLT_FACTOR = 0.5F;
    /// 迸发弹幕的初始速度
    private static final double BOLT_SPEED = 0.7;

    public PolarMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFD966);
    }

    /// 施加 / 覆盖印记，并记下武器攻击力与施加者
    public static void apply(LivingEntity target, @Nullable LivingEntity owner, float weaponDamage, int duration, int amplifier) {
        if (target.level().isClientSide) return;
        CompoundTag data = target.getPersistentData();
        data.putFloat(DAMAGE_KEY, weaponDamage);
        if (owner != null) data.putUUID(OWNER_KEY, owner.getUUID());
        target.removeEffect(ModEffects.POLAR_MARK.get());
        target.addEffect(new MobEffectInstance(ModEffects.POLAR_MARK.get(), duration, amplifier), owner);
    }

    @Override
    public void applyEffectTick(LivingEntity marked, int amplifier) {
        Level level = marked.level();
        if (level.isClientSide) return;
        CompoundTag data = marked.getPersistentData();
        float weaponDamage = data.getFloat(DAMAGE_KEY);
        LivingEntity owner = resolveOwner(level, data);
        if (weaponDamage > 0.0F) {
            DamageSource source = level.damageSources().indirectMagic(marked, owner);
            for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, marked.getBoundingBox().inflate(AURA_RADIUS))) {
                if (!victim.isAlive() || victim.isSpectator()) continue;
                if (owner != null && !LibEntityUtils.canHit(owner, victim)) continue;
                victim.invulnerableTime = 0;
                victim.hurt(source, weaponDamage * AURA_FACTOR);
            }
        }
        if (amplifier >= 1 && owner != null && weaponDamage > 0.0F) {
            spawnSunBolt(marked, owner, weaponDamage);
        }
    }

    private static @Nullable LivingEntity resolveOwner(Level level, CompoundTag data) {
        if (!data.hasUUID(OWNER_KEY) || !(level instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getEntity(data.getUUID(OWNER_KEY)) instanceof LivingEntity living ? living : null;
    }

    /// 在印记拥有者头顶迸发出一发索敌的日光弹幕，不附着也不回打宿主
    private static void spawnSunBolt(LivingEntity marked, LivingEntity owner, float weaponDamage) {
        if (!(marked.level() instanceof ServerLevel serverLevel)) return;
        LittleSunEntity bolt = new LittleSunEntity(owner, weaponDamage * BOLT_FACTOR);
        bolt.setInert(true);
        bolt.setHoming(true);
        bolt.setSeekExclude(marked);
        // 从头顶略高的位置迸出，避免一出生就撞在宿主身上
        double x = marked.getX();
        double y = marked.getY() + marked.getBbHeight() + 0.35;
        double z = marked.getZ();
        bolt.setPos(x, y, z);
        Vec3 burst = new Vec3(
                marked.getRandom().nextDouble() - 0.5,
                marked.getRandom().nextDouble() * 0.8 + 0.4,
                marked.getRandom().nextDouble() - 0.5).normalize();
        bolt.setDeltaMovement(burst.scale(BOLT_SPEED));
        bolt.setYRot((float) (Math.atan2(burst.x, burst.z) * (180.0 / Math.PI)));
        serverLevel.addFreshEntity(bolt);
        serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 6, 0.2, 0.2, 0.2, 0.02);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
