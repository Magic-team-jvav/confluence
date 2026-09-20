package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.gameevent.SandstormGameEvent;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.UUID;

/// 只需配置体节数量的通用蠕虫实体。
///
/// 普通自然生成变种没有 Boss 所有者；由血肉类 Boss 召唤的血蛭可以显式绑定精确
/// UUID，并在区块反向加载后恢复双向关系。是否作为从属完全由实例数据决定，不需要为
/// 同一种血蛭再注册一套重复实体类型。
public class SimpleWormMonster extends BaseWormMonster implements BossOwnedEntity {
    private int segments;
    private final Role role;
    private final @Nullable Anatomy anatomy;
    private final BossOwnerTracker<BaseBoss> ownerTracker = new BossOwnerTracker<>(BaseBoss.class);

    public SimpleWormMonster(EntityType<? extends SimpleWormMonster> type, Level level, int segments, Role role, EntityType<BaseWormPart> segmentType) {
        super(type, level, segmentType);
        this.segments = segments;
        this.role = role;
        this.anatomy = null;
    }

    public SimpleWormMonster(EntityType<? extends SimpleWormMonster> type, Level level, Role role, Anatomy anatomy, EntityType<BaseWormPart> segmentType) {
        super(type, level, segmentType);
        this.role = role;
        this.anatomy = anatomy;
        this.segments = anatomy.minSegments() + random.nextInt(anatomy.maxSegments() - anatomy.minSegments() + 1);
    }

    @Override
    protected int getSegmentCount() {
        return segments;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return role == Role.CORRUPTION || role == Role.UNDERGROUND_DESERT || role == Role.UNDERWORLD
                || role == Role.BONE_SERPENT || role == Role.FLYING ? 0.0F : super.getWalkTargetValue(pos, level);
    }

    @Override
    protected float segmentSpacing() {
        if (anatomy != null) return anatomy.spacing();
        // 沙虫根骨骼绕 X 旋转 90°，轴向长度是原模型的 12 像素，渲染倍率为 2。
        if (getType() == MonsterEntities.TOMB_CRAWLER.get()) return 12.0F / 16.0F * 2.0F;
        return role == Role.BONE_SERPENT ? 2.5F : role == Role.FLYING ? 1.25F : 1.6F;
    }

    @Override
    public EntityDimensions segmentDimensions(boolean tail) {
        return anatomy == null ? super.segmentDimensions(tail) : tail ? anatomy.tailDimensions() : anatomy.bodyDimensions();
    }

    @Override
    protected double segmentDamageMultiplier(boolean tail) {
        return anatomy == null ? super.segmentDamageMultiplier(tail) : tail ? anatomy.tailDamage() : anatomy.bodyDamage();
    }

    @Override
    protected double segmentArmorMultiplier(boolean tail) {
        return anatomy == null ? super.segmentArmorMultiplier(tail) : tail ? anatomy.tailArmor() : anatomy.bodyArmor();
    }

    @Override
    protected WormMovementAction.Profile movementProfile() {
        return switch (role) {
            case UNDERGROUND, UNDERGROUND_DESERT -> WormMovementAction.Profile.underground();
            case CORRUPTION -> WormMovementAction.Profile.corruption();
            case UNDERWORLD, BONE_SERPENT -> WormMovementAction.Profile.boneSerpent();
            case FLYING -> WormMovementAction.Profile.flying();
        };
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (role == Role.BONE_SERPENT && source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return role != Role.FLYING && super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    public boolean displayFireAnimation() {
        return role != Role.BONE_SERPENT && super.displayFireAnimation();
    }

    @Override
    public boolean fireImmune() {
        return role == Role.BONE_SERPENT || super.fireImmune();
    }

    public void setBossOwner(BaseBoss owner) {
        ownerTracker.bind(this, owner);
        setTarget(owner.getTarget());
        BossMinionCoordinator.faceTargetImmediately(this, getTarget());
    }

    @Override
    public @Nullable BaseBoss getBossOwner() {
        return ownerTracker.resolve(this);
    }

    public @Nullable UUID getBossOwnerUUID() {
        return ownerTracker.getOwnerUUID();
    }

    public boolean isOwnedBy(BaseBoss owner) {
        return ownerTracker.isOwnedBy(owner.getUUID());
    }

    @Override
    public void tick() {
        LivingEntity inheritedTarget = null;
        boolean owned = !level().isClientSide && getBossOwnerUUID() != null;
        if (owned) {
            ownerTracker.tickDependent(this, true, 100);
            inheritedTarget = getTarget();
            if (isRemoved()) return;
        }
        super.tick();
        if (owned && getTarget() != inheritedTarget) {
            setTarget(inheritedTarget);
        }
    }

    @Override
    public void aiStep() {
        if (level().isClientSide && lerpSteps > 0) {
            // 原版生物俯仰插值不跨角度边界，头部需与体节一样走最短角路径。
            lerpXRot = getXRot() + Mth.wrapDegrees(lerpXRot - getXRot());
        }
        super.aiStep();
    }

    /// 血肉阵营蠕虫不得攻击同阵营实体；有明确所有者时还需服从所有者的目标过滤。
    @Override
    public boolean canAttack(LivingEntity target) {
        if (getType().is(ModTags.EntityTypes.FLESH_ALLIANCE) && target.getType().is(ModTags.EntityTypes.FLESH_ALLIANCE)) {
            return false;
        }
        if (!isInsideActivityRegion(target.blockPosition())) return false;
        BaseBoss owner = getBossOwner();
        return (owner == null || owner.canAttack(target))
                && super.canAttack(target);
    }

    @Override
    public boolean isInsideActivityRegion(BlockPos pos) {
        return switch (role) {
            case UNDERGROUND -> pos.getY() < OverworldUtils.getSurfaceY();
            case UNDERGROUND_DESERT ->
                    level().getBiome(pos).is(PortTags.Biomes.IS_DESERT) && (pos.getY() < OverworldUtils.getSurfaceY() || SandstormGameEvent.INSTANCE.started());
            case CORRUPTION -> OverworldUtils.isCorruption(level().getBiome(pos));
            case UNDERWORLD, BONE_SERPENT -> level().dimension() == OverworldUtils.underworld();
            case FLYING -> pos.getY() >= OverworldUtils.getSurfaceY();
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        if (anatomy != null) tag.putInt("SegmentCount", segments);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        if (anatomy != null && tag.contains("SegmentCount"))
            segments = Mth.clamp(tag.getInt("SegmentCount"), anatomy.minSegments(), anatomy.maxSegments());
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    public record Anatomy(int minSegments, int maxSegments, float spacing,
                          EntityDimensions bodyDimensions, EntityDimensions tailDimensions,
                          double bodyDamage, double tailDamage, double bodyArmor,
                          double tailArmor) {
        public static final Anatomy GIANT_WORM = simple(5, 7, 2.0F);
        public static final Anatomy DIGGER = new Anatomy(7, 12, 0.8F, EntityDimensions.scalable(1.05F, 0.7F), EntityDimensions.scalable(1.05F, 0.7F), 28.0 / 45.0, 26.0 / 45.0, 2.0, 3.0);
        public static final Anatomy TOMB_CRAWLER = simple(6, 9, 1.5F);
        public static final Anatomy DEVOURER = simple(9, 13, 1.6F);
        public static final Anatomy WORLD_FEEDER = new Anatomy(21, 26, 0.9F, EntityDimensions.scalable(1.25F, 0.95F), EntityDimensions.scalable(1.65F, 0.6F), 55.0 / 70.0, 40.0 / 70.0, 40.0 / 36.0, 44.0 / 36.0);
        public static final Anatomy BONE_SERPENT = simple(13, 19, 2.5F);
        public static final Anatomy LEECH = simple(9, 13, 1.6F);

        private static Anatomy simple(int minSegments, int maxSegments, float spacing) {
            EntityDimensions dimensions = EntityDimensions.scalable(1.5F, 1.5F);
            return new Anatomy(minSegments, maxSegments, spacing, dimensions, dimensions,
                    1.0, 1.0, 1.0, 1.0);
        }
    }

    /// 注册项选择实体自身已有的蠕虫行为族，不把运动参数散落到注册表。
    public enum Role {
        UNDERGROUND,
        UNDERGROUND_DESERT,
        CORRUPTION,
        UNDERWORLD,
        BONE_SERPENT,
        FLYING
    }
}
