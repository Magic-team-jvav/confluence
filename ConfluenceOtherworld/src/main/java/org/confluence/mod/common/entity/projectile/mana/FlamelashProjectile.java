package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.mod.common.item.mana.BaseDraggingStaffItem;
import org.confluence.mod.common.item.mana.FlamelashItem;
import org.mesdag.portlib.wrapper.common.PortTags;

/// 烈焰火鞭弹幕。
///
/// <p>第一次命中造成直接伤害，之后的命中改为爆炸，因此 {@link #penetrated} 是不可丢失的
/// 服务端阶段状态。它与拖拽/已发射阶段分开保存，便于各层独立校验；1.20 不读取旧扁平字段。</p>
public class FlamelashProjectile extends BaseDraggingProjectile {
    private static final String RUNTIME_TAG = "ConfluenceFlamelashRuntime";
    private static final int RUNTIME_VERSION = 1;

    public static final double RANGE = 6.0 * 2 / 3;
    public static final double KNOCKBACK = 0.65;

    private boolean penetrated;

    public FlamelashProjectile(EntityType<? extends FlamelashProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FlamelashProjectile(LivingEntity living) {
        this(ModEntities.FLAMELASH.get(), living.level());
    }

    @Override
    protected BaseDraggingStaffItem<?> getDraggingStaff() {
        return ManaWeaponItems.FLAMELASH.get();
    }

    @Override
    protected int getCooldown() {
        return FlamelashItem.COOLDOWN;
    }

    @Override
    protected ResourceLocation getParticleId() {
        return Confluence.asResource("flamelash_projectile");
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doFluidCheck(fluidState -> fluidState.is(PortTags.Fluids.WATER) || fluidState.is(PortTags.Fluids.HONEY));
        doAgeCheck(1200);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        doExplosion(RANGE, KNOCKBACK);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;
        if (penetrated) {
            doExplosion(RANGE, KNOCKBACK);
        } else {
            this.penetrated = true;
            doHurtAndKnockback(result.getEntity(), KNOCKBACK, 0.2);
        }
    }

    @Override
    protected boolean doHurtAndKnockback(Entity target, double knockbackStrength, double knockbackMotionY) {
        if (super.doHurtAndKnockback(target, knockbackStrength, knockbackMotionY)) {
            if (random.nextBoolean()) {
                target.igniteForTicks(Mth.randomBetweenInclusive(random, 80, 160));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putBoolean("Penetrated", penetrated);
        compound.put(RUNTIME_TAG, runtime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid Flamelash runtime state");
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Penetrated", Tag.TAG_BYTE)) {
            combatState().invalidate("Malformed Flamelash runtime state");
            return;
        }
        penetrated = runtime.getBoolean("Penetrated");
    }
}
