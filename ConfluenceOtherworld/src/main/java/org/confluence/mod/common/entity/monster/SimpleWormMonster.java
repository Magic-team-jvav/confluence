package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.leaf.WormMovementAction;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnerTracker;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// 只需配置体节数量的通用蠕虫实体。
///
/// <p>普通自然生成变种没有 Boss 所有者；由血肉类 Boss 召唤的血蛭可以显式绑定精确
/// UUID，并在区块反向加载后恢复双向关系。是否作为从属完全由实例数据决定，不需要为
/// 同一种血蛭再注册一套重复实体类型。</p>
public class SimpleWormMonster extends BaseWormMonster {
    private static final int OWNER_RESOLVE_GRACE_TICKS = 100;

    private final int segments;
    private final Role role;
    private final BossOwnerTracker<BaseBoss> ownerTracker = new BossOwnerTracker<>(BaseBoss.class);
    private int unresolvedOwnerTicks;

    public SimpleWormMonster(EntityType<? extends SimpleWormMonster> type, Level level, int segments) {
        this(type, level, segments, Role.UNDERGROUND);
    }

    public SimpleWormMonster(EntityType<? extends SimpleWormMonster> type, Level level, int segments, Role role) {
        super(type, level);
        this.segments = segments;
        this.role = role;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWormMonster.createWormAttributes();
    }

    @Override
    protected int getSegmentCount() {
        return segments;
    }

    @Override
    protected float segmentSpacing() {
        return role == Role.BONE_SERPENT ? 2.5F : role == Role.FLYING ? 1.0F : 1.6F;
    }

    @Override
    protected WormMovementAction.Profile movementProfile() {
        return switch (role) {
            case UNDERGROUND -> WormMovementAction.Profile.underground();
            case SURFACE -> WormMovementAction.Profile.surface();
            case BONE_SERPENT -> WormMovementAction.Profile.boneSerpent();
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

    public void setBossOwner(BaseBoss owner) {
        ownerTracker.bind(this, owner);
        unresolvedOwnerTicks = 0;
    }

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
        super.tick();
        if (level().isClientSide || getBossOwnerUUID() == null) {
            return;
        }
        BaseBoss owner = getBossOwner();
        if (owner != null && owner.isAlive()) {
            unresolvedOwnerTicks = 0;
            if (getTarget() == null && owner.getTarget() != null && owner.getTarget().isAlive()) {
                setTarget(owner.getTarget());
            }
            return;
        }
        setTarget(null);
        if (++unresolvedOwnerTicks > OWNER_RESOLVE_GRACE_TICKS) {
            discard();
        }
    }

    /// 血肉阵营蠕虫不得攻击同阵营实体；有明确所有者时还需服从所有者的目标过滤。
    @Override
    public boolean canAttack(LivingEntity target) {
        if (getType().is(ModTags.EntityTypes.FLESH_ALLIANCE) && target.getType().is(ModTags.EntityTypes.FLESH_ALLIANCE)) {
            return false;
        }
        BaseBoss owner = getBossOwner();
        return (owner == null || owner.canAttack(target))
                && super.canAttack(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        unresolvedOwnerTicks = 0;
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    /// 注册项选择实体自身已有的蠕虫行为族，不把运动参数散落到注册表。
    public enum Role {
        UNDERGROUND,
        SURFACE,
        BONE_SERPENT,
        FLYING
    }
}
