package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.entity.EnemyDamageRules;

import java.util.function.IntFunction;

/// 通用敌对镭射弹幕。
///
/// 原先的“激光枪手射线”与“火星走妖激光”在此合并为同一实体，
/// 仅由 {@link Variant} 区分伤害、速度、散布、寿命与三层配色；
/// 变种随发射写入同步数据，客户端据此还原外观。
public class MonsterLaser extends StraightMonsterProjectile {
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MonsterLaser.class, EntityDataSerializers.INT);
    private static final int OWNER_GRACE_TICKS = 40;
    private static final String VARIANT_KEY = "LaserVariant";

    private int missingOwnerTicks;

    public MonsterLaser(EntityType<? extends MonsterLaser> type, Level level) {
        super(type, level);
    }

    /// 镭射变种：一次射击的伤害、速度、散布、寿命与三层配色。
    public enum Variant {
        /// 专家射弹伤害 × 26% 向上取整：120 → 32、140 → 37、96 → 25。
        RAY_GUNNER(32.0F, 5.25F, 0.0F, 20, 0xFFFFFF, 0xFF5B4D, 0xB50000),
        MARTIAN_WALKER(37.0F, 3.5F, 1.5F, 30, 0xFFFFFF, 0x73DFFF, 0x0877FF),
        BRAIN_SCRAMBLER(25.0F, 3.5F, 0.0F, 30, 0xE8FFE9, 0x68FF72, 0x0B9431);

        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), RAY_GUNNER);

        private final float damage;
        private final float speed;
        private final float inaccuracy;
        private final int lifetime;
        private final int innerColor;
        private final int middleColor;
        private final int outerColor;

        Variant(float damage, float speed, float inaccuracy, int lifetime, int innerColor, int middleColor, int outerColor) {
            this.damage = damage;
            this.speed = speed;
            this.inaccuracy = inaccuracy;
            this.lifetime = lifetime;
            this.innerColor = innerColor;
            this.middleColor = middleColor;
            this.outerColor = outerColor;
        }

        public int innerColor() { return innerColor; }

        public int middleColor() { return middleColor; }

        public int outerColor() { return outerColor; }
    }

    /// 当前变种；客户端由同步数据还原。
    public Variant variant() { return Variant.BY_ID.apply(entityData.get(DATA_VARIANT)); }

    /// 按给定出生点、目标与变种配置本次射击。
    public void configureShot(Mob owner, LivingEntity target, Vec3 origin, Variant variant) {
        if (owner == null || target == null || origin == null || variant == null) return;
        configureAimed(owner, origin, target.getEyePosition().subtract(origin),
                variant.damage, variant.speed, variant.inaccuracy, variant.lifetime);
        entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_VARIANT, Variant.RAY_GUNNER.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) return;
        if (getOwner() == null) {
            if (++missingOwnerTicks > OWNER_GRACE_TICKS) discard();
        } else {
            missingOwnerTicks = 0;
        }
    }

    /// 敌怪阵营不互伤：镭射穿过所有同类敌怪，只命中玩家、玩家召唤物等敌对目标。
    @Override
    public boolean canHitEntity(Entity target) {
        return target != null && !EnemyDamageRules.isEnemy(target) && super.canHitEntity(target);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant() == Variant.BRAIN_SCRAMBLER && random.nextInt(3) == 0) {
            target.addEffect(new MobEffectInstance(LibEffects.CONFUSED, 100));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MissingOwnerTicks", missingOwnerTicks);
        tag.putInt(VARIANT_KEY, entityData.get(DATA_VARIANT));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        missingOwnerTicks = tag.contains("MissingOwnerTicks")
                ? Mth.clamp(tag.getInt("MissingOwnerTicks"), 0, OWNER_GRACE_TICKS) : 0;
        entityData.set(DATA_VARIANT, tag.contains(VARIANT_KEY)
                ? Mth.clamp(tag.getInt(VARIANT_KEY), 0, Variant.values().length - 1) : Variant.RAY_GUNNER.ordinal());
    }
}
