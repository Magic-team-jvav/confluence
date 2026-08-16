package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 接近目标后膨胀爆裂并散播血肿瘤的血腥芽孢。
///
/// <p>引信进度由服务端同步，客户端渲染器据此平滑膨胀；目标在爆炸前离开时会取消引信。
/// 爆炸本身不破坏方块，随后生成二至三个血肿瘤并给它们不同的抛射方向。</p>
public class BloodySpore extends BaseMonster {
    private static final EntityDataAccessor<Integer> SWELL =
            SynchedEntityData.defineId(
                    BloodySpore.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("move.walk");
    private int oldSwell;

    public BloodySpore(EntityType<? extends BloodySpore> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SWELL, 0);
    }

    @Override
    public void tick() {
        oldSwell = getSwell();
        super.tick();
    }

    public int getSwell() {
        return entityData.get(SWELL);
    }

    private void setSwell(int swell) {
        entityData.set(SWELL, Math.max(0, Math.min(30, swell)));
    }

    public float getSwelling(float partialTick) {
        return (oldSwell + (getSwell() - oldSwell) * partialTick)
                / 28.0F;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BloodySpore.this),
                                new MoveToTargetAction(BloodySpore.this, 1.0, 3.0),
                                new SwellAndBurstAction()),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(BloodySpore.this, 0.4, 8)));
            }
        };
    }

    private final class SwellAndBurstAction extends BTNode {
        private static final int FUSE_TICKS = 30;
        private int fuse;

        @Override
        public void start() {
            fuse = 0;
            setSwell(0);
        }

        @Override
        public BTStatus execute() {
            var target = getTarget();
            if (target == null || !target.isAlive() || distanceToSqr(target) > 16.0) {
                setSwell(0);
                return BTStatus.FAILURE;
            }
            if (++fuse == 1) {
                playSound(
                        ModSoundEvents.BLOODY_SPORE_FUSE.get(),
                        1.0F,
                        0.5F);
            }
            setSwell(fuse);
            if (fuse < FUSE_TICKS) return BTStatus.RUNNING;
            burst();
            return BTStatus.SUCCESS;
        }

        @Override
        public void stop() {
            if (isAlive()) {
                setSwell(0);
            }
        }
    }

    private void burst() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        serverLevel.explode(this, getX(), getY(), getZ(), 4.2F, Level.ExplosionInteraction.NONE);
        int count = 2 + random.nextInt(2);
        double offset = random.nextDouble() * Math.PI * 2.0;
        for (int index = 0; index < count; index++) {
            Entity tumor = MonsterEntities.BLOOD_TUMORS.get().create(serverLevel);
            if (tumor == null) continue;
            tumor.setPos(position());
            double angle = offset + Math.PI * 2.0 * index / count;
            tumor.setDeltaMovement(new Vec3(Math.sin(angle) * 0.3,
                    random.nextDouble() * 0.5 + 0.2, Math.cos(angle) * 0.3));
            serverLevel.addFreshEntity(tumor);
        }
        discard();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.BLOODY_SPORE_HIT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BLOODY_SPORE_DEATH.get();
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "movement",
                4,
                state -> state.isMoving()
                        ? state.setAndContinue(WALK)
                        : PlayState.STOP));
    }
}
