package org.confluence.mod.common.entity.storage;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.item.storage.StorageCompanionItem;
import org.confluence.mod.common.menu.PiggyBankMenu;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 随身储物入口实体的公共生命周期。
///
/// <p>实体只保存所有者，并负责跟随、交互和动画；真实物品数据仍保存在玩家自己的
/// {@link PlayerPiggyBankContainer} 中。这样方块存钱罐、切斯特和飞行存钱罐会打开同一份固定槽位数据，
/// 实体消失、换维度或重新召唤时不会复制出第二份库存。</p>
public abstract class StorageCompanionEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(StorageCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleep");
    private static final RawAnimation OPEN_ANIMATION = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSE_ANIMATION = RawAnimation.begin().thenPlay("close");
    private static final int OWNER_GRACE_TICKS = 40;
    private static final double FOLLOW_START_DISTANCE = 3.0;
    private static final double TELEPORT_DISTANCE = 24.0;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private @Nullable ServerPlayer opener;
    private int missingOwnerTicks;
    private int closeAnimationTicks;

    protected StorageCompanionEntity(EntityType<? extends StorageCompanionEntity> type, Level level) {
        super(type, level);
        xpReward = 0;
    }

    /// 由召唤物品设置唯一所有者。
    public final void initializeOwner(ServerPlayer owner) {
        tame(owner);
    }

    public final boolean belongsTo(Player player) {
        return getOwnerUUID() != null && getOwnerUUID().equals(player.getUUID());
    }

    protected abstract boolean flies();

    /// 是否像切斯特一样持续跟随所有者。
    protected boolean followsOwner() {
        return true;
    }

    /// 是否允许该玩家通过实体打开自己的随身储物。
    protected boolean canOpenFor(Player player) {
        return belongsTo(player);
    }

    protected Component menuTitle() {
        return Component.translatable("container.confluence.piggy_bank");
    }

    protected double followSpeed() {
        return flies() ? 1.25 : 1.05;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OPEN, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (closeAnimationTicks > 0) {
                closeAnimationTicks--;
            }
            return;
        }
        if (followsOwner()) {
            if (!(level() instanceof ServerLevel) || !(getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.isSpectator() || owner.level() != level()) {
                if (++missingOwnerTicks >= OWNER_GRACE_TICKS) {
                    discard();
                }
                return;
            }
            missingOwnerTicks = 0;

            if (distanceToSqr(owner) > TELEPORT_DISTANCE * TELEPORT_DISTANCE) {
                moveTo(owner.getX(), owner.getY() + (flies() ? 1.5 : 0.0), owner.getZ(), owner.getYRot(), 0.0F);
                getNavigation().stop();
            } else if (distanceTo(owner) > FOLLOW_START_DISTANCE) {
                getNavigation().moveTo(owner, followSpeed());
            } else {
                getNavigation().stop();
            }
        }

        if (opener != null && !(opener.containerMenu instanceof PiggyBankMenu)) {
            setOpen(false);
            opener = null;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // 手持对应召唤物品时必须把交互交还给物品处理。
        // 否则准星落在实体上时，潜行右键会优先打开容器，无法执行取消召唤；普通右键也无法可靠地重新放置入口。
        if (player.getItemInHand(hand).getItem() instanceof StorageCompanionItem<?>) {
            return InteractionResult.PASS;
        }
        if (!canOpenFor(player)) {
            return InteractionResult.FAIL;
        }
        if (level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(new SimpleMenuProvider((id, inventory, menuPlayer) -> {
            PiggyBankMenu menu = new PiggyBankMenu(id, inventory);
            PlayerPiggyBankContainer container = PlayerPiggyBankContainer.of(menuPlayer);
            container.setActiveContainer(null);
            container.startOpen(menuPlayer);
            return menu;
        }, menuTitle()));
        setOpen(true);
        opener = (ServerPlayer) player;
        return InteractionResult.CONSUME;
    }

    private void setOpen(boolean open) {
        if (entityData.get(OPEN) == open) {
            return;
        }
        entityData.set(OPEN, open);
        if (!open) {
            closeAnimationTicks = 10;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (OPEN.equals(key) && !entityData.get(OPEN)) {
            closeAnimationTicks = 10;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.core.animation.AnimationController<>(
                this, "storage_companion", 5, state -> {
            // 飞行存钱罐的资源只定义持续飞行动画；开关盖动画属于切斯特模型。
            if (flies()) {
                return state.setAndContinue(FLY);
            }
            if (entityData.get(OPEN)) {
                return state.setAndContinue(OPEN_ANIMATION);
            }
            if (closeAnimationTicks > 0) {
                return state.setAndContinue(CLOSE_ANIMATION);
            }
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            if (level().getDayTime() % 24000L > 13000L) {
                return state.setAndContinue(SLEEP);
            }
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    /// 随身储物入口不是普通生物，不应该被战斗、环境或意外碰撞销毁。
    ///
    /// <p>仍保留原版 kill 伤害，方便命令和开发环境可靠清理实体。</p>
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypes.GENERIC_KILL) && super.hurt(source, amount);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /// 储物入口只作为玩家可点击的交互目标，不参与骑乘关系。
    ///
    /// <p>这也避免其他实体把切斯特或飞行存钱罐当作临时载具。</p>
    @Override
    public boolean startRiding(Entity vehicle, boolean force) {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.FLYING_SPEED, 0.45)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
