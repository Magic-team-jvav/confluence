package org.confluence.mod.common.entity.projectile.mana;

import PortLib.extensions.net.minecraft.world.entity.projectile.ProjectileUtil.PortProjectileUtilExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.mixed.Immunity;

/**
 * 血雨与普通雨滴共用的下落子弹幕。
 *
 * <p>雨滴离开母云后就是独立的可保存实体；最大穿透次数不能在重载时重新向母云查询。
 * 当前格式只接受版本化的雨滴运行状态，缺失、版本不符或越界时复用战斗状态的安全失效通道，
 * 保证实体在造成伤害前销毁。</p>
 */
public class RainProjectile extends AbstractManaProjectile implements Immunity {
    private static final String RUNTIME_TAG = "ConfluenceRainRuntime";
    private static final int RUNTIME_VERSION = 1;

    private int maxPenetrate = 2;

    public RainProjectile(EntityType<? extends RainProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public RainProjectile(EntityType<? extends RainProjectile> entityType, LivingEntity living, Vec3 position) {
        this(entityType, living.level());
        setOwner(living);
        setPos(position);
    }

    /**
     * 设置本枚雨滴的总穿透上限；异常文本保持英文以便日志和外部调用方诊断。
     */
    public void setMaxPenetrate(int maxPenetrate) {
        if (maxPenetrate < 1) {
            throw new IllegalArgumentException("Rain penetration must be positive");
        }
        this.maxPenetrate = maxPenetrate;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid rain projectile runtime state");
            return;
        }

        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("MaxPenetrate", Tag.TAG_INT)) {
            combatState().invalidate("Malformed rain projectile runtime state");
            return;
        }
        int savedMaxPenetrate = runtime.getInt("MaxPenetrate");
        if (savedMaxPenetrate < 1) {
            combatState().invalidate("Rain penetration is outside the supported range");
            return;
        }
        maxPenetrate = savedMaxPenetrate;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putInt("MaxPenetrate", maxPenetrate);
        compound.put(RUNTIME_TAG, runtime);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        setDeltaMovement(doSimpleMove().add(0, -0.08, 0));
        doAgeCheck(200);
    }

    @Override
    protected void doHitCheck() {
        HitResult hitResult = PortProjectileUtilExtension.getHitResult(position(), this, this::canHitEntity, getDeltaMovement(), level(), 0.6F, ClipContext.Block.COLLIDER);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitResult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitResult);
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitResult);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0, 0);
            doDiscardInMaxPenetrate(maxPenetrate);
        }
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 3;
    }
}
