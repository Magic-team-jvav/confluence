package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;

/**
 * 诅咒焰弹幕。
 *
 * <p>除公共魔法弹幕状态外，本类还保存已经消耗的穿透次数。该计数决定下一次命中是否销毁，
 * 属于服务端伤害预算；当前 1.20 格式不读取旧扁平字段，缺失、类型错误或越界时会在再次
 * 命中前通过公共战斗状态安全失效。</p>
 */
public class CursedFlamesProjectile extends AbstractManaProjectile {
    private static final String RUNTIME_TAG = "ConfluenceCursedFlamesRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int MAX_SAVED_PENETRATE_COUNT = 1;

    private int penetrateCount = 0;

    public CursedFlamesProjectile(EntityType<CursedFlamesProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("cursed_flames"));
    }

    public CursedFlamesProjectile(LivingEntity living) {
        this(ModEntities.CURSED_FLAMES.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (!level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.baseTick();
        doBouncyMove(true, () -> doCollisionCheck(5), vec3 -> vec3.scale(0.99));
        doAgeCheck(1200);
    }

    @Override
    public double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO.get(), 140));
        }
        doHurtAndKnockback(entity, 0.6, 0.2);
        if (this.penetrateCount++ >= 1) { // 击中就算一次
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid Cursed Flames runtime state");
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("PenetrateCount", Tag.TAG_INT)) {
            combatState().invalidate("Malformed Cursed Flames runtime state");
            return;
        }
        int restoredPenetrateCount = runtime.getInt("PenetrateCount");
        if (restoredPenetrateCount < 0 || restoredPenetrateCount > MAX_SAVED_PENETRATE_COUNT) {
            combatState().invalidate("Cursed Flames penetration count is outside the supported range");
            return;
        }
        penetrateCount = restoredPenetrateCount;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (penetrateCount < 0 || penetrateCount > MAX_SAVED_PENETRATE_COUNT) {
            throw new IllegalStateException("Cursed Flames penetration count is outside the supported range");
        }
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putInt("PenetrateCount", penetrateCount);
        compound.put(RUNTIME_TAG, runtime);
    }
}
