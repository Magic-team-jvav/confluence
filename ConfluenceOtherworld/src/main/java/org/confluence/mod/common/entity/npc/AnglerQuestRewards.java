package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.attachment.PlayerVoidVaultContainer;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.accessory.FishingBobber;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.CuriosUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// 渔夫交鱼奖励，判定顺序与概率取自 Terraria 的 Player.GetAnglerReward。
class AnglerQuestRewards {
    // 每个门槛各判断一次是否额外给一份鱼饵。
    private static final int[] BAIT_QUANTITY_ROLLS = {25, 50, 100, 150, 200, 250};

    private AnglerQuestRewards() {}

    static void grant(ServerPlayer player, Item questFish, int count, float priceAdjustment) {
        boolean hardmode = KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.WALL_OF_FLESH);
        boolean expert = LibUtils.isAtLeastExpert(player.serverLevel());
        float rarity = rarityMultiplier(count) * ((priceAdjustment + 1.0F) / 2.0F);
        RandomSource random = player.getRandom();

        give(player, mainReward(player, questFish, count, hardmode, rarity, random));
        // TODO 补齐泰拉的 19 种装饰后接入独立奖励池，触发概率为 1 - rarity * (1 - min(1, count / 100.0))。
        give(player, moneyReward(count, hardmode, expert, random));
        give(player, baitReward(count, rarity, random));
    }

    private static ItemStack mainReward(ServerPlayer player, Item questFish, int count, boolean hardmode, float rarity, RandomSource random) {
        ItemStack milestone = switch (count) {
            case 5 -> MountItems.FUZZY_CARROT.toStack();
            case 10 -> ArmorItems.ANGLER_HAT.toStack();
            case 15 -> ArmorItems.ANGLER_VEST.toStack();
            case 20 -> ArmorItems.ANGLER_PANTS.toStack();
            case 25 -> ToolItems.BOTTOMLESS_WATER_BUCKET.toStack();
            case 30 -> FishingPoleItems.GOLDEN_FISHING_ROD.toStack();
            default -> ItemStack.EMPTY;
        };
        if (!milestone.isEmpty()) return milestone;

        // 主奖励逐项独立判定，首个命中结束本次主奖励抽取。
        if (questFish == QuestedFishes.BUMBLEBEE_TUNA.get() && (hardmode || random.nextBoolean())) {
            return random.nextBoolean() ? ToolItems.BOTTOMLESS_HONEY_BUCKET.toStack() : ToolItems.HONEY_ABSORBANT_SPONGE.toStack();
        }
        if (count > 75 && roll(random, 250, rarity))
            return FishingPoleItems.GOLDEN_FISHING_ROD.toStack();
        if (hardmode && count > 25 && roll(random, 100, rarity))
            return FishingPoleItems.HOTLINE_FISHING_HOOK.toStack();
        if (hardmode && count > 10 && roll(random, 70, rarity)) return TCItems.FIN_WINGS.toStack();
        if (count > 10 && roll(random, 70, rarity))
            return ToolItems.BOTTOMLESS_WATER_BUCKET.toStack();
        if (count > 10 && roll(random, 70, rarity))
            return ToolItems.SUPER_ABSORBANT_SPONGE.toStack();
        if (roll(random, 80, rarity)) return ToolItems.GOLDEN_BUG_NET.toStack();
        if (roll(random, 60, rarity)) return HookItems.FISH_HOOK.toStack();
        if (roll(random, 60, rarity)) return MinecartItems.MINECARP.toStack();
        // 两套未实装的时装各保留一次判定，命中后仍结束主奖励抽取。
        if (roll(random, 80, rarity) || roll(random, 80, rarity)) return ItemStack.EMPTY;
        if (random.nextDouble() < accessoryChance(rarity)) return accessoryReward(player, random);

        int potion = random.nextInt(3);
        int quantity = 2 + random.nextInt(4);
        return switch (potion) {
            case 0 -> PotionItems.FISHING_POTION.toStack(quantity);
            // 声呐药水尚未实装，保留三种药水各占三分之一的概率。
            case 1 -> ItemStack.EMPTY;
            default -> PotionItems.CRATE_POTION.toStack(quantity);
        };
    }

    private static ItemStack accessoryReward(ServerPlayer player, RandomSource random) {
        List<Item> tackle = List.of(AccessoryItems.HIGH_TEST_FISHING_LINE.get(), TCItems.ANGLER_EARRING.get(), AccessoryItems.TACKLE_BOX.get());
        List<Item> information = List.of(TCItems.FISHERMANS_POCKET_GUIDE.get(), TCItems.WEATHER_RADIO.get(), TCItems.SEXTANT.get());
        Item bobber = AccessoryItems.FISHING_BOBBER.get();

        Set<Item> owned = new HashSet<>();
        collectItems(owned, player.getInventory());
        collectItems(owned, player.getEnderChestInventory());
        collectItems(owned, PlayerPiggyBankContainer.of(player));
        collectItems(owned, PlayerVoidVaultContainer.of(player));
        for (ItemStack stack : CuriosUtils.getCurios(player)) owned.add(stack.getItem());

        List<Item> candidates = new ArrayList<>(7);
        if (!owned.contains(AccessoryItems.ANGLER_TACKLE_BAG.get()) && !owned.contains(AccessoryItems.LAVAPROOF_TACKLE_BAG.get())) {
            for (Item item : tackle) {
                if (!owned.contains(item)) candidates.add(item);
            }
        }
        if (!owned.contains(TCItems.FISH_FINDER.get()) && !owned.contains(TCItems.PDA.get()) && !owned.contains(TCItems.CELL_PHONE.get())) {
            for (Item item : information) {
                if (!owned.contains(item)) candidates.add(item);
            }
        }
        if (owned.stream().noneMatch(FishingBobber.class::isInstance)) candidates.add(bobber);
        // 凑齐全部饰品后，恢复从完整候选池等概率抽取。
        if (candidates.isEmpty()) {
            candidates.addAll(tackle);
            candidates.addAll(information);
            candidates.add(bobber);
        }
        return new ItemStack(candidates.get(random.nextInt(candidates.size())));
    }

    private static ItemStack moneyReward(int count, boolean hardmode, boolean expert, RandomSource random) {
        int silver = (int) (((float) (count + 50) / 2) * (50 + random.nextInt(151)) * 0.015F);
        silver = (int) (silver * 1.5);
        if (hardmode) silver *= 2;
        if (expert) silver *= 2;
        return silver > 100 ? ModItems.GOLD_COIN.toStack(Math.min(10, silver / 100)) : ModItems.SILVER_COIN.toStack(Math.min(99, Math.max(1, silver)));
    }

    private static ItemStack baitReward(int count, float rarity, RandomSource random) {
        if (random.nextInt(rollBound(100, rarity)) > 50) return ItemStack.EMPTY;
        Item bait = roll(random, 15, rarity) ? BaitItems.MASTER_BAIT.get() : roll(random, 5, rarity) ? BaitItems.JOURNEYMAN_BAIT.get() : BaitItems.APPRENTICE_BAIT.get();
        int quantity = 1;
        for (int bound : BAIT_QUANTITY_ROLLS) {
            if (random.nextInt(bound) <= count) quantity++;
        }
        return new ItemStack(bait, quantity);
    }

    private static float rarityMultiplier(int count) {
        if (count <= 50) return (1.0F - count * 0.01F) * 0.9F;
        if (count <= 100) return (0.5F - (count - 50) * 0.005F) * 0.9F;
        if (count <= 150) return (0.25F - (count - 100) * 0.002F) * 0.9F;
        return 0.15F * 0.9F;
    }

    private static float accessoryChance(float rarity) {
        float miss = 1.0F;
        for (int i = 0; i < 3; i++) miss *= 1.0F - 1.0F / rollBound(40, rarity);
        for (int i = 0; i < 3; i++) miss *= 1.0F - 1.0F / rollBound(30, rarity);
        miss *= 1.0F - 1.0F / rollBound(25, rarity);
        return (1.0F - miss) * 0.8F;
    }

    private static boolean roll(RandomSource random, int base, float rarity) {
        return random.nextInt(rollBound(base, rarity)) == 0;
    }

    private static int rollBound(int base, float rarity) {
        // Terraria 的随机上界先截断，Next(0) 返回 0，等价于这里的 nextInt(1)。
        return Math.max(1, (int) (base * rarity));
    }

    private static void collectItems(Set<Item> items, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) items.add(stack.getItem());
        }
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }
}
