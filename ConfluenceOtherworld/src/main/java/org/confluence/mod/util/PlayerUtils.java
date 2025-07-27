package org.confluence.mod.util;

import com.google.common.collect.Iterables;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.network.s2c.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.api.entity.Boss;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.common.attachment.ExtraInventory.COINS_START;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class PlayerUtils {
    public static final ToIntFunction<Item> COIN_2_INDEX = coin -> {
        if (coin == ModItems.PLATINUM_COIN.get()) return 0;
        if (coin == ModItems.GOLDEN_COIN.get()) return 1;
        if (coin == ModItems.SILVER_COIN.get()) return 2;
        if (coin == ModItems.COPPER_COIN.get()) return 3;
        return -1;
    };
    public static final IntFunction<CoinItem> INDEX_2_COIN = index -> switch (index) {
        case 0 -> ModItems.COPPER_COIN.get();
        case 1 -> ModItems.SILVER_COIN.get();
        case 2 -> ModItems.GOLDEN_COIN.get();
        default -> ModItems.PLATINUM_COIN.get();
    };

    public static void syncMana2Client(ServerPlayer serverPlayer, ManaStorage manaStorage) {
        PacketDistributor.sendToPlayer(serverPlayer, new ManaPacketS2C(manaStorage.getMaxMana(), manaStorage.getCurrentMana()));
    }

    public static void syncMana2Client(ServerPlayer serverPlayer) {
        syncMana2Client(serverPlayer, serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE));
    }

    public static void regenerateMana(ServerPlayer serverPlayer) {
        ManaStorage manaStorage = serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE);

        int delay = manaStorage.getRegenerateDelay();
        boolean notMove = Math.abs(serverPlayer.walkDist - serverPlayer.walkDistO) < Mth.EPSILON;
        if (delay > 0) {
            if (manaStorage.isArcaneCrystalUsed()) delay = (int) ((float) delay * (notMove ? 0.975F : 0.95F));
            if (delay > 20 && serverPlayer.hasEffect(ModEffects.MANA_REGENERATION)) delay = 20;
            int delayReduce = notMove ? 2 : 1;
            if (manaStorage.isFastManaRegeneration()) delayReduce += 1;
            manaStorage.setRegenerateDelay(delay - delayReduce);
            return;
        }

        FloatSupplier receive = () -> {
            // 1.0F / 7.0F = 0.14285715F
            float a = manaStorage.getMaxMana() * 0.14285715F + (manaStorage.isFastManaRegeneration() ? 25 : 0) + 1;
            if (notMove) a += manaStorage.getMaxMana() * 0.5F;
            float b = manaStorage.getCurrentMana() * 0.8F / manaStorage.getMaxMana() + 0.2F;
            return a * b * 0.0115F * EnchantmentUtils.processManaRegeneration(serverPlayer);
        };

        if (manaStorage.receiveMana(receive)) syncMana2Client(serverPlayer, manaStorage);
    }

    public static boolean extractMana(ServerPlayer serverPlayer, ItemStack itemStack, FloatSupplier sup) {
        if (serverPlayer.isCreative()) return true;
        return extractAndDelayAndSync(
                serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE),
                HookMapManager.postHooks(
                        ModHookTypes.MANA_CONSUME.get(),
                        (owner, hook, original) -> hook.onManaConsume(owner, itemStack, original),
                        serverPlayer,
                        () -> sup.getAsFloat() * EnchantmentUtils.processEfficientMagic(serverPlayer)
                ),
                serverPlayer
        );
    }

    public static boolean extractAndDelayAndSync(ManaStorage manaStorage, FloatSupplier sup, ServerPlayer serverPlayer) {
        if (manaStorage.extractMana(sup, serverPlayer)) {
            manaStorage.setRegenerateDelay();
            syncMana2Client(serverPlayer, manaStorage);
            return true;
        }
        return false;
    }

    public static void receiveMana(ServerPlayer serverPlayer, FloatSupplier sup) {
        ManaStorage manaStorage = serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE);
        if (manaStorage.receiveMana(sup)) syncMana2Client(serverPlayer, manaStorage);
    }

    public static void syncSavedData(ServerPlayer serverPlayer) {
        ConfluenceData data = ConfluenceData.get(serverPlayer.serverLevel());
        WindSpeedPacketS2C.sendToClient(serverPlayer, data.getWindSpeedX(), data.getWindSpeedZ());
        if (CommonConfigs.STAR_PHASE.get()) {
            StarPhasesPacketS2C.sendToClient(serverPlayer, data.getStarPhases());
        }
        GamePhasePacketS2C.sendToClient(serverPlayer, KillBoard.INSTANCE.getGamePhase());
        MeteoriteLocationPacketS2C.sendToAll(data.getMeteoriteLocation(), 0);
    }

    public static float getFishingPower(ServerPlayer player) {
        float base = TCUtils.getAccessoriesValue(player, AccessoryItems.FISHING$POWER);
        if (player.getData(ModAttachmentTypes.EVER_BENEFICIAL).isGummyWormUsed()) base += 3.0F;
        if (player.isInFluidType() && TCUtils.hasAccessoriesType(player, TCItems.FLOAT$ON$LIQUID$SURFACE)) base += 5.0F;
        if (player.hasEffect(ModEffects.TIPSY)) base += 5.0F;
        Level level = player.level();
        if (level.isRaining()) base *= 1.1F;
        else if (level.isThundering()) base *= 1.2F;
        int dayTime = DateUtils.getDayTime(level);
        if (DateUtils.isWithinDayTime(DateUtils._04$30, DateUtils._06$00, dayTime)) base *= 1.3F;
        else if (DateUtils.isWithinDayTime(9, 0, 15, 0, dayTime)) base *= 0.8F;
        else if (DateUtils.isWithinDayTime(DateUtils.getDayTime(18, 0), DateUtils._19$30, dayTime)) base *= 1.3F;
        else if (DateUtils.isWithinDayTime(21, 18, 2, 12, dayTime)) base *= 0.8F;
        base *= switch (level.getMoonPhase()) {
            case 0 -> 1.1F; // 满月
            case 1, 7 -> 1.05F; // 凸月
            case 5 -> 0.95F; // 眉月
            case 4 -> 0.9F; // 新月
            default -> 1.0F;
        };
        if (MomentInstanceManager.of(level).hasMoment(TMMoments.BLOOD_MOON.getKey())) {
            base *= 1.1F;
        }
        base = HookMapManager.postHooks(ModHookTypes.FISHING_POWER.get(), (owner, hook, original) -> hook.modifyFishingPower(owner, player, original), player, base);
        return base;
    }

    public static Tuple<ItemStack, Integer> getMaxDiggingPowerItem(Player player) {
        int max = -1;
        ItemStack ret = ItemStack.EMPTY;
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.isEmpty() || !itemStack.is(ItemTags.PICKAXES)) continue;
            int power = DiggingPower.getPower(itemStack);
            if (power > max) {
                max = power;
                ret = itemStack;
            }
        }
        return new Tuple<>(ret, max);
    }

    public static void consumeItemCount(List<ItemStack> have, Item item, int consumeCount) {
        int count = 0;
        for (ItemStack stack : have) {
            if (stack.is(item) && count < consumeCount) {
                int toConsume = Math.min(stack.getCount(), consumeCount - count);
                stack.shrink(toConsume);
                count += toConsume;
            }
        }
    }

    public static int[] getCoins(Player player, boolean withPiggyBank) {
        int[] coins = new int[SIZE_COINS];
        if (withPiggyBank) {
            int[] ints = decodeCoin(PlayerPiggyBankContainer.of(player).getTotalMoney());
            coins[0] = ints[3];
            coins[1] = ints[2];
            coins[2] = ints[1];
            coins[3] = ints[0];
        }
        for (ItemStack stack : Iterables.concat(player.getInventory().items, player.getData(ModAttachmentTypes.EXTRA_INVENTORY).getCoins())) {
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    coins[index] += stack.getCount();
                }
            }
        }
        return coins;
    }

    @Deprecated
    public static int[] getCoins(Player player) {
        return getCoins(player, true);
    }

    public static long getMoney(Player player, boolean withPiggyBank) {
        long res = withPiggyBank ? PlayerPiggyBankContainer.of(player).getTotalMoney() : 0;
        for (ItemStack stack : Iterables.concat(player.getInventory().items, player.getData(ModAttachmentTypes.EXTRA_INVENTORY).getCoins())) {
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    res += (long) (stack.getCount() * Math.pow(UPGRADES_COUNT, 3 - index));
                }
            }
        }
        return res;
    }

    @Deprecated
    public static long getMoney(Player player) {
        return getMoney(player, true);
    }

    public static boolean tryCostMoney(Player player, long cost, boolean withPiggyBank) {
        return tryCostMoney(getMoney(player, withPiggyBank), player, cost, withPiggyBank);
    }

    @Deprecated
    public static boolean tryCostMoney(Player player, long cost) {
        return tryCostMoney(player, cost, true);
    }

    public static boolean tryCostMoney(long have, Player player, long cost, boolean withPiggyBank) {
        if (have < cost) return false;

        if (withPiggyBank && (cost = PlayerPiggyBankContainer.of(player).tryCostMoney(cost)) == 0) return true;

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }

        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        for (int i = COINS_START; i < COINS_START + SIZE_COINS; i++) {
            extraInventory.setItem(i, ItemStack.EMPTY);
        }
        int[] coins = decodeCoin(have - cost);

        for (int i = 0; i < SIZE_COINS; i++) {
            int coin = coins[i];
            if (coin <= 0) continue;
            CoinItem coinItem = INDEX_2_COIN.apply(i);
            while (coin > UPGRADES_COUNT) {
                inventory.add(new ItemStack(coinItem, UPGRADES_COUNT));
                coin -= UPGRADES_COUNT;
            }
            inventory.add(new ItemStack(coinItem, coin));
        }
        return true;
    }

    @Deprecated
    public static boolean tryCostMoney(long have, Player player, long cost) {
        return tryCostMoney(have, player, cost, true);
    }

    public static int[] decodeCoin(long money) {
        int[] coins = new int[SIZE_COINS];
        if (money < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }

        // jit自动优化
        coins[3] = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT)); // 铂金
        money %= UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT;
        coins[2] = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT)); // 金币
        money %= UPGRADES_COUNT * UPGRADES_COUNT;
        coins[1] = (int) (money / UPGRADES_COUNT); // 银币
        money %= UPGRADES_COUNT;
        coins[0] = (int) money; // 铜币

        return coins;
    }

    public static int[] decodeCoin(int money) {
        int copper_count = money % UPGRADES_COUNT;
        int i = ((money - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        return new int[]{copper_count, silver_count, golden_count, k};
    }

    public static void sortCoins(Player player) {
        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        Object2IntOpenHashMap<Integer> map = new Object2IntOpenHashMap<>();
        for (int i = 0; i < SIZE_COINS; i++) {
            ItemStack coins = extraInventory.getCoins(i);
            if (coins.isEmpty() || !coins.is(ModTags.Items.COINS)) continue;
            extraInventory.setItem(COINS_START + i, ItemStack.EMPTY);
            int index = COIN_2_INDEX.applyAsInt(coins.getItem());
            int count = coins.getCount();
            Supplier<CoinItem> upgrade;
            while (map.addTo(index, count) + count >= UPGRADES_COUNT && (upgrade = INDEX_2_COIN.apply(3 - index).upgrade) != null) {
                map.addTo(index, -UPGRADES_COUNT);
                index = COIN_2_INDEX.applyAsInt(upgrade.get());
                count = 1;
            }
        }
        for (int i = 0, j = 0; i < SIZE_COINS; i++) {
            int count = map.getInt(i);
            if (count <= 0) continue;
            CoinItem coinItem = INDEX_2_COIN.apply(3 - i);
            while (count > MAX_STACK_SIZE) {
                extraInventory.setItem(COINS_START + j++, new ItemStack(coinItem, MAX_STACK_SIZE));
                count -= MAX_STACK_SIZE;
            }
            if (count > 0) {
                extraInventory.setItem(COINS_START + j++, new ItemStack(coinItem, count));
            }
        }
    }

    /**
     * @see PlayerDeathInfoPacketS2C#replaceCombatKillPacket(ServerPlayer, Component)
     */
    public static void dropMoney(Player player) {
        long money = getMoney(player, false);
        long drops;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            int ratio = LibUtils.switchByDifficulty(player.level(), player.blockPosition(), 2, 3, 4);
            drops = money * ratio / 4;
        } else {
            drops = money;
        }
        tryCostMoney(money, player, drops, false);
        ModUtils.dropMoney(drops, player.getX(), player.getY(), player.getZ(), player.level());

        if (CommonConfigs.SHOW_MONEY_DROPS.get()) {
            LibUtils.getOrCreatePersistedData(player).putLong("confluence:drops_money", drops);
        }
    }

    /**
     * 获取玩家复活时间
     *
     * @param player 玩家
     * @return 复活时间
     */
    public static int getRespawnWaitTime(Player player) {
        AABB aabb = new AABB(player.blockPosition()).inflate(Short.MAX_VALUE);
        int min, max;
        if (player.level().getEntitiesOfClass(LivingEntity.class, aabb, living -> living instanceof Boss).isEmpty()) {
            min = CommonConfigs.DEFAULT_RESPAWN_TIME_MIN.get();
            max = CommonConfigs.DEFAULT_RESPAWN_TIME_MAX.get();
        } else {
            min = CommonConfigs.BOSS_RESPAWN_TIME_MIN.get();
            max = CommonConfigs.BOSS_RESPAWN_TIME_MAX.get();
        }
        if (min == max) return min;
        return player.getRandom().nextInt(Math.min(min, max), Math.max(min, max));
    }

    /**
     * @return true表示魔力值不够
     */
    public static boolean applyAutoGetMana(ServerPlayer serverPlayer, float currentMana, float extract) {
        if (currentMana < extract) {
            if (!TCUtils.hasAccessoriesType(serverPlayer, AccessoryItems.AUTO$GET$MANA)) return true;
            ItemStack toUse = null;
            for (ItemStack itemStack : serverPlayer.getInventory().items) {
                if (itemStack.getItem() instanceof ManaPotionItem manaPotion) {
                    int amount = manaPotion.getAmount();
                    if (currentMana + amount < extract) continue;
                    if (toUse == null || amount < ((ManaPotionItem) toUse.getItem()).getAmount()) toUse = itemStack;
                    if (amount == 50) break;
                }
            }
            if (toUse == null) return true;
            toUse.finishUsingItem(serverPlayer.level(), serverPlayer);
        }
        return false;
    }
}
