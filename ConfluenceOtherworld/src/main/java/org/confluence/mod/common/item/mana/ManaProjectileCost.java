package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.api.projectile.PreparedProjectileCost;
import org.confluence.lib.api.projectile.ProjectileCost;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.util.TCUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/// Otherworld 魔力武器共用的可回滚弹幕成本。
///
/// <p>本类复用旧法杖的魔力前缀、高效魔法、饰品减耗、自动魔力药水和魔力修补语义，但把它们
/// 拆成可补偿的两个阶段：准备阶段只计算并锁定准确成本；提交阶段才扣魔力和药水；弹幕生成失败时精确
/// 恢复；药水负面效果、空瓶和魔力修补只在整次动作成功后发生。</p>
///
/// <p>实例只能服务一次武器动作，不得缓存在物品单例或玩家全局状态中。</p>
public final class ManaProjectileCost implements ProjectileCost {
    private final float baseManaCost;
    private final Predicate<ProjectileFireContext> freeCondition;
    private PreparedState preparedState;
    private PreparedProjectileCost preparedCost;
    private boolean successFinished;

    /// 创建一笔以当前武器前缀和玩家魔力能力为准的成本。
    public ManaProjectileCost(float baseManaCost, Predicate<ProjectileFireContext> freeCondition) {
        if (!Float.isFinite(baseManaCost) || baseManaCost < 0.0F) {
            throw new IllegalArgumentException("Base mana cost must be finite and non-negative");
        }
        this.baseManaCost = baseManaCost;
        this.freeCondition = Objects.requireNonNull(freeCondition, "Mana free condition must not be null");
    }

    @Override
    public Optional<PreparedProjectileCost> prepare(ProjectileFireContext context) {
        Objects.requireNonNull(context, "Projectile fire context must not be null");
        if (preparedCost != null || preparedState != null) {
            throw new IllegalStateException("Mana projectile cost may only be prepared once");
        }

        ServerPlayer player = context.player();
        if (player.isCreative() || freeCondition.test(context)) {
            preparedState = PreparedState.free(player);
            preparedCost = PreparedProjectileCost.none();
            return Optional.of(preparedCost);
        }

        float amount = resolveManaCost(context);
        ManaStorage storage = ManaStorage.of(player);
        float currentMana = storage.getCurrentMana();
        int regenerateDelay = storage.getRegenerateDelay();
        PotionSelection potion = selectAutomaticPotion(player, storage, currentMana, amount);
        if (currentMana < amount && potion == null) {
            return Optional.empty();
        }

        float potionMana = potion == null ? 0.0F : potion.amount();
        if (Math.min(storage.getMaxMana(), currentMana + potionMana) < amount) {
            return Optional.empty();
        }
        PreparedState state = new PreparedState(
                player, storage, currentMana, regenerateDelay, amount, potion,
                potion == null ? ItemStack.EMPTY : potion.stack().copy());
        preparedState = state;
        preparedCost = PreparedProjectileCost.once(state::commit, state::rollback);
        return Optional.of(preparedCost);
    }

    /// 完成只属于成功动作的副作用。
    ///
    /// <p>必须由动作的成功回调调用；若成本尚未提交、已经回滚或重复调用，则不会产生副作用。</p>
    public void finishSuccessfulAction() {
        if (successFinished || preparedState == null || preparedCost == null
                || !preparedCost.isCommitted() || preparedCost.isRolledBack()) {
            return;
        }
        successFinished = true;
        preparedState.finishSuccess();
    }

    private float resolveManaCost(ProjectileFireContext context) {
        float prefixed = PrefixUtils.calculateManaCost(context.weapon(), baseManaCost);
        float efficient = EnchantmentUtils.processEfficientMagic(() -> prefixed, context.player()).getAsFloat();
        float reduction = TCUtils.getValue(context.player(), AccessoryItems.MANA$USE$REDUCE);
        float resolved = efficient * Math.max(0.0F, 1.0F - reduction);
        if (!Float.isFinite(resolved) || resolved < 0.0F) {
            throw new IllegalArgumentException("Resolved mana cost must be finite and non-negative");
        }
        return resolved;
    }

    /// 按旧自动喝药规则选择能够覆盖本次成本的最小魔力药水。
    private static PotionSelection selectAutomaticPotion(
            ServerPlayer player,
            ManaStorage storage,
            float currentMana,
            float amount
    ) {
        if (currentMana >= amount) {
            return null;
        }
        if (!TCUtils.hasType(player, AccessoryItems.AUTO$GET$MANA)) {
            return null;
        }
        PotionSelection selected = null;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ManaPotionItem potion)) {
                continue;
            }
            int potionAmount = potion.getAmount();
            if (Math.min(storage.getMaxMana(), currentMana + potionAmount) < amount) {
                continue;
            }
            if (selected == null || potionAmount < selected.amount()) {
                selected = new PotionSelection(stack, potionAmount);
            }
        }
        return selected;
    }

    private record PotionSelection(ItemStack stack, int amount) {
        private PotionSelection {
            Objects.requireNonNull(stack, "Mana potion stack must not be null");
            if (stack.isEmpty() || !(stack.getItem() instanceof ManaPotionItem)) {
                throw new IllegalArgumentException("Mana potion selection must contain a mana potion");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Mana potion amount must be positive");
            }
        }
    }

    /// 准备阶段冻结的请求局部状态；所有布尔标记只由服务端主线程访问。
    private static final class PreparedState {
        private final ServerPlayer player;
        private final ManaStorage storage;
        private final float previousMana;
        private final int previousRegenerateDelay;
        private final float amount;
        private final PotionSelection potion;
        private final ItemStack expectedPotion;
        private boolean manaCommitted;
        private boolean potionConsumed;
        private final boolean free;

        private PreparedState(
                ServerPlayer player,
                ManaStorage storage,
                float previousMana,
                int previousRegenerateDelay,
                float amount,
                PotionSelection potion,
                ItemStack expectedPotion
        ) {
            this.player = Objects.requireNonNull(player, "Mana cost player must not be null");
            this.storage = Objects.requireNonNull(storage, "Mana storage must not be null");
            this.previousMana = previousMana;
            this.previousRegenerateDelay = previousRegenerateDelay;
            this.amount = amount;
            this.potion = potion;
            this.expectedPotion = Objects.requireNonNull(expectedPotion, "Expected mana potion must not be null");
            this.free = false;
        }

        private PreparedState(ServerPlayer player) {
            this.player = Objects.requireNonNull(player, "Mana cost player must not be null");
            this.storage = ManaStorage.of(player);
            this.previousMana = storage.getCurrentMana();
            this.previousRegenerateDelay = storage.getRegenerateDelay();
            this.amount = 0.0F;
            this.potion = null;
            this.expectedPotion = ItemStack.EMPTY;
            this.free = true;
        }

        private static PreparedState free(ServerPlayer player) {
            return new PreparedState(player);
        }

        private void commit() {
            if (free) {
                return;
            }
            float restored = 0.0F;
            if (potion != null) {
                ItemStack stack = potion.stack();
                if (stack.isEmpty() || stack.getCount() < 1
                        || !ItemStack.isSameItemSameTags(stack, expectedPotion)) {
                    throw new IllegalStateException("Prepared automatic mana potion changed before commit");
                }
                stack.shrink(1);
                potionConsumed = true;
                restored = potion.amount();
            }
            if (!storage.commitProjectileCost(previousMana, restored, amount)) {
                throw new IllegalStateException("Prepared mana state changed before commit");
            }
            manaCommitted = true;
            PlayerUtils.syncMana2Client(player, storage);
        }

        private void rollback() {
            if (free) {
                return;
            }
            if (manaCommitted) {
                storage.restoreProjectileCostState(previousMana, previousRegenerateDelay);
                manaCommitted = false;
                PlayerUtils.syncMana2Client(player, storage);
            }
            if (potionConsumed) {
                potion.stack().grow(1);
                potionConsumed = false;
            }
        }

        private void finishSuccess() {
            if (free) {
                return;
            }
            if (potionConsumed) {
                ManaPotionItem.applyAutomaticUseEffects(player);
                if (CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
                    ItemStack bottle = PotionItems.BOTTLE.toStack();
                    if (!player.getInventory().add(bottle)) {
                        player.drop(bottle, false);
                    }
                }
            }
            if (amount > 0.0F) {
                EnchantmentUtils.repairPlayerItems(player, amount);
            }
        }
    }
}
