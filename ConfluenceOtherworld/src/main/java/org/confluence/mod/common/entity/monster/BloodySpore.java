package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
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
    private static final EntityDataAccessor<Integer> SWELL = SynchedEntityData.defineId(BloodySpore.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(BloodySpore.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IGNITED = SynchedEntityData.defineId(BloodySpore.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private int oldSwell;
    private int swellDirection;

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
        entityData.define(POWERED, false);
        entityData.define(IGNITED, false);
    }

    @Override
    public void tick() {
        if (isAlive()) {
            oldSwell = getSwell();
            if (entityData.get(IGNITED)) swellDirection = 1;
            if (swellDirection > 0 && getSwell() == 0) {
                playSound(ModSoundEvents.BLOODY_SPORE_FUSE.get(), 1.0F, 0.5F);
                gameEvent(GameEvent.PRIME_FUSE);
            }
            setSwell(getSwell() + swellDirection);
            if (getSwell() >= 30) burst();
        }
        super.tick();
    }

    public int getSwell() {
        return entityData.get(SWELL);
    }

    private void setSwell(int swell) {
        entityData.set(SWELL, Math.max(0, Math.min(30, swell)));
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        super.thunderHit(level, lightning);
        entityData.set(POWERED, true);
    }

    public float getSwelling(float partialTick) {
        return (oldSwell + (getSwell() - oldSwell) * partialTick) / 28.0F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("powered", entityData.get(POWERED));
        tag.putBoolean("ignited", entityData.get(IGNITED));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(POWERED, tag.getBoolean("powered"));
        entityData.set(IGNITED, tag.getBoolean("ignited"));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.FLINT_AND_STEEL)) return super.mobInteract(player, hand);
        level().playSound(player, getX(), getY(), getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.HOSTILE, 1.0F, random.nextFloat() * 0.4F + 0.8F);
        if (!level().isClientSide) {
            entityData.set(IGNITED, true);
            stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
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
        @Override
        public void start() {
            swellDirection = 1;
        }

        @Override
        public BTStatus execute() {
            var target = getTarget();
            if (target == null || !target.isAlive() || distanceToSqr(target) > 49.0 || !hasLineOfSight(target)) {
                swellDirection = -1;
                return BTStatus.FAILURE;
            }
            swellDirection = 1;
            return BTStatus.RUNNING;
        }

        @Override
        public void stop() {
            if (isAlive() && !entityData.get(IGNITED)) swellDirection = -1;
        }
    }

    private void burst() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        int multiplier = entityData.get(POWERED) ? 2 : 1;
        dead = true;
        serverLevel.explode(this, getX(), getY(), getZ(), 4.2F * multiplier, Level.ExplosionInteraction.NONE);
        int count = random.nextInt(2, 4) * multiplier;
        float offset = random.nextFloat() * 2.0F;
        for (int index = 0; index < count; index++) {
            Entity tumor = MonsterEntities.BLOOD_TUMORS.get().create(serverLevel);
            if (tumor == null) continue;
            tumor.setPos(position());
            double angle = offset * index * Math.PI;
            tumor.setDeltaMovement(new Vec3(Math.sin(angle) * 0.3, random.nextDouble() * 0.5 + 0.2, Math.cos(angle) * 0.3));
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP));
    }
}
