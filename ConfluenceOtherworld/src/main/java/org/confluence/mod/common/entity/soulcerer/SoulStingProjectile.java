package org.confluence.mod.common.entity.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;

/// 自然之惩戒发射的毒刺弹幕。
///
/// 外观复用黄蜂毒刺的模型与贴图，弹道与命中结算是独立的玩家阵营直线弹幕
/// （黄蜂那套绑定在 `Mob` 发射者上，玩家不能直接用）。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulStingProjectile extends Projectile {
    private static final String DAMAGE_TAG = "Damage";
    private static final String POISON_TAG = "PoisonDuration";
    private float damage;
    private int poisonDuration = SoulSkillParameters.NATURES_WRATH_POISON_DURATION_TICKS;

    public SoulStingProjectile(EntityType<? extends SoulStingProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 从发射者眼位沿视线方向射出一枚毒刺。
    public SoulStingProjectile(LivingEntity owner, float damage, float speed) {
        this(owner, owner.getEyePosition(), owner.getLookAngle(), damage, speed);
    }

    /// 用明确出生点与方向射出，供多发扇形齐射使用。
    public SoulStingProjectile(LivingEntity owner, Vec3 origin, Vec3 direction, float damage, float speed) {
        this(ModEntities.SOUL_STING.get(), owner.level());
        setOwner(owner);
        setPos(origin);
        setDeltaMovement(direction.normalize().scale(speed));
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {}

    /// 直线飞行，命中实体结算灵魂伤害并附带中毒。
    @Override
    public void tick() {
        super.tick();
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!level().isClientSide && hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            if (hit instanceof EntityHitResult entityHit) {
                Entity rawTarget = entityHit.getEntity();
                LivingEntity target = LibEntityUtils.effectTarget(rawTarget);
                if (rawTarget.hurt(LibDamageTypes.soul(level(), this, getOwner()), damage)
                        && poisonDuration > 0 && target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, 0), getOwner());
                }
            }
            discard();
            return;
        }
        Vec3 movement = getDeltaMovement();
        setPos(position().add(movement));
        if (!level().isClientSide && (tickCount > SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS || distanceToSqr(getOwner() == null ? position() : getOwner().position()) > 3600.0)) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat(DAMAGE_TAG);
        poisonDuration = tag.getInt(POISON_TAG);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && LibEntityUtils.canHit(getOwner(), target);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat(DAMAGE_TAG, damage);
        tag.putInt(POISON_TAG, poisonDuration);
    }
}
