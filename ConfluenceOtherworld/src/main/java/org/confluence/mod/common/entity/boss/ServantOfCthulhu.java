package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/// 克苏鲁之眼生成的短命仆从。
///
/// <p>仆从拥有独立实体类型以便 1.20 的所有权追踪和区块恢复，但战斗数值仍与
/// 1.21 侧临时恶魔眼一致。它优先继承主人的目标，主人暂时卸载时
/// 保留精确 UUID，不能误绑定到附近另一个同类 Boss。</p>
public class ServantOfCthulhu extends BaseFlyingMonster {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(ServantOfCthulhu.class, EntityDataSerializers.OPTIONAL_UUID);
    private final BossOwnerTracker<EyeOfCthulhu> ownerTracker =
            new BossOwnerTracker<>(EyeOfCthulhu.class);

    public ServantOfCthulhu(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
    }

    public void setMaster(EyeOfCthulhu master) {
        ownerTracker.bind(this, master);
        entityData.set(OWNER_UUID, Optional.of(master.getUUID()));
    }

    public @Nullable EyeOfCthulhu getMaster() {
        return ownerTracker.resolve(this);
    }

    public @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CreatureAttributeBuilder.creature(10.0, 1.0, 3.0, 30.0, 0.5, 0.3).flying();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(ServantOfCthulhu.this),
                                new ChargeAttackAction(ServantOfCthulhu.this, 0.4)),
                        SequenceNode.of(new WaitAction(10),
                                new FlyWanderAction(ServantOfCthulhu.this, 0.2, 5))
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            EyeOfCthulhu master = getMaster();
            if (master != null && master.isAlive()) {
                LivingEntity masterTarget = master.getTarget();
                if (masterTarget != null && masterTarget.isAlive() && getTarget() != masterTarget) {
                    setTarget(masterTarget);
                }
            }
        }
        if (!level().isClientSide && getTarget() == null && tickCount % 20 == 0) {
            Player nearest = level().getNearestPlayer(this, 32);
            if (nearest != null) setTarget(nearest);
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof EyeOfCthulhu)
                && !(target instanceof ServantOfCthulhu)
                && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        EyeOfCthulhu master = getMaster();
        if (master != null && source.getEntity() == master) return false;
        return super.hurt(source, amount);
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
        entityData.set(OWNER_UUID, Optional.ofNullable(ownerTracker.getOwnerUUID()));
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }
}
