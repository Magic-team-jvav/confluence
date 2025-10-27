package org.confluence.mod.util;

import com.google.common.collect.Iterables;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.network.s2c.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.integration.bettercombat.BetterCombatHelper;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terraentity.api.entity.Boss;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class PlayerUtils {
    public static final ToIntFunction<Item> COIN_2_INDEX = coin -> {
        if (coin == ModItems.PLATINUM_COIN.get()) return 0;
        if (coin == ModItems.GOLD_COIN.get()) return 1;
        if (coin == ModItems.SILVER_COIN.get()) return 2;
        if (coin == ModItems.COPPER_COIN.get()) return 3;
        return -1;
    };
    public static final IntFunction<CoinItem> INDEX_2_COIN = index -> switch (index) {
        case 0 -> ModItems.COPPER_COIN.get();
        case 1 -> ModItems.SILVER_COIN.get();
        case 2 -> ModItems.GOLD_COIN.get();
        default -> ModItems.PLATINUM_COIN.get();
    };

    public static void syncMana2Client(ServerPlayer player, ManaStorage manaStorage) {
        PacketDistributor.sendToPlayer(player, new ManaPacketS2C(manaStorage.getMaxMana(), manaStorage.getCurrentMana()));
    }

    public static void syncMana2Client(ServerPlayer player) {
        syncMana2Client(player, ManaStorage.of(player));
    }

    public static void regenerateMana(ServerPlayer player) {
        ManaStorage manaStorage = ManaStorage.of(player);

        int delay = manaStorage.getRegenerateDelay();
        boolean notMove = Math.abs(player.walkDist - player.walkDistO) < Mth.EPSILON;
        if (delay > 0) {
            if (manaStorage.isArcaneCrystalUsed()) delay = (int) ((float) delay * (notMove ? 0.975F : 0.95F));
            if (delay > 20 && player.hasEffect(ModEffects.MANA_REGENERATION)) delay = 20;
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
            return a * b * 0.0115F * EnchantmentUtils.processManaRegeneration(player);
        };

        if (manaStorage.receiveMana(receive)) syncMana2Client(player, manaStorage);
    }

    public static boolean extractMana(ServerPlayer player, ItemStack itemStack, FloatSupplier sup) {
        if (player.isCreative()) return true;
        return extractAndSync(
                ManaStorage.of(player),
                EnchantmentUtils.processEfficientMagic(sup, player),
                player
        );
    }

    public static boolean extractAndSync(ManaStorage manaStorage, FloatSupplier sup, ServerPlayer player) {
        if (manaStorage.extractMana(sup, player)) {
            syncMana2Client(player, manaStorage);
            return true;
        }
        return false;
    }

    public static void receiveMana(ServerPlayer player, FloatSupplier sup) {
        ManaStorage manaStorage = ManaStorage.of(player);
        if (manaStorage.receiveMana(sup)) syncMana2Client(player, manaStorage);
    }

    public static void syncSavedData(ServerPlayer player) {
        ConfluenceData data = ConfluenceData.get(player.serverLevel());
        WindSpeedPacketS2C.sendToClient(player, data.getWindSpeedX(), data.getWindSpeedZ());
        if (CommonConfigs.STAR_PHASE.get()) {
            StarPhasesPacketS2C.sendToClient(player, data.getStarPhases());
        }
        KillBoardSyncPacketS2C.sendToClient(player);
        MeteoriteLocationPacketS2C.sendToAll(data.getMeteoriteLocation(), 0);
        BestiarySyncPacketS2C.syncEntries(player);
    }

    public static float getFishingPower(ServerPlayer player) {
        float base = TCUtils.getAccessoriesValue(player, AccessoryItems.FISHING$POWER);
        if (EverBeneficial.of(player).isGummyWormUsed()) base += 3.0F;
        if (player.isInFluidType() && TCUtils.hasAccessoriesType(player, TCItems.FLOAT$ON$LIQUID$SURFACE)) base += 5.0F;
        if (player.hasEffect(ModEffects.TIPSY)) base += 5.0F;
        Level level = player.level();
        if (level.isRaining()) base *= 1.1F;
        else if (level.isThundering()) base *= 1.2F;
        int dayTime = LibDateUtils.getDayTime(level);
        if (LibDateUtils.isWithinDayTime(LibDateUtils._04$30, LibDateUtils._06$00, dayTime)) base *= 1.3F;
        else if (LibDateUtils.isWithinDayTime(9, 0, 15, 0, dayTime)) base *= 0.8F;
        else if (LibDateUtils.isWithinDayTime(LibDateUtils.getDayTime(18, 0), LibDateUtils._19$30, dayTime)) base *= 1.3F;
        else if (LibDateUtils.isWithinDayTime(21, 18, 2, 12, dayTime)) base *= 0.8F;
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

    public static void consumeItemCount(Iterable<ItemStack> have, Item item, int consumeCount) {
        int count = 0;
        for (ItemStack stack : have) {
            if (stack.is(item) && count < consumeCount) {
                int toConsume = Math.min(stack.getCount(), consumeCount - count);
                stack.shrink(toConsume);
                count += toConsume;
            }
        }
    }

    public static Coins getCoins(Player player, boolean withPiggyBank) {
        Coins coins = withPiggyBank ? decodeCoin(PlayerPiggyBankContainer.of(player).getTotalMoney()) : Coins.createEmpty();
        for (ItemStack stack : Iterables.concat(player.getInventory().items, ExtraInventory.of(player).getAllCoins())) {
            if (!stack.isEmpty() && stack.getItem() instanceof CoinItem coin) {
                coins.increase(coin, stack.getCount());
            }
        }
        return coins;
    }

    @Deprecated(since = "1.2.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    public static int[] getCoins(Player player) {
        Coins coins = getCoins(player, true);
        return new int[]{coins.platinum(), coins.gold(), coins.silver(), coins.copper()};
    }

    public static long getMoney(Player player, boolean withPiggyBank) {
        long res = withPiggyBank ? PlayerPiggyBankContainer.of(player).getTotalMoney() : 0;
        for (ItemStack stack : Iterables.concat(player.getInventory().items, ExtraInventory.of(player).getAllCoins())) {
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    res += (long) (stack.getCount() * Math.pow(UPGRADES_COUNT, 3 - index));
                }
            }
        }
        return res;
    }

    @Deprecated(since = "1.2.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    public static long getMoney(Player player) {
        return getMoney(player, true);
    }

    public static boolean tryCostMoney(Player player, long cost, boolean withPiggyBank) {
        return tryCostMoney(getMoney(player, withPiggyBank), player, cost, withPiggyBank);
    }

    @Deprecated(since = "1.2.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    public static boolean tryCostMoney(Player player, long cost) {
        return tryCostMoney(player, cost, true);
    }

    public static boolean tryCostMoney(long have, Player player, long cost, boolean withPiggyBank) {
        if (have < cost) return false;

        if (withPiggyBank) {
            long res = PlayerPiggyBankContainer.of(player).tryCostMoney(cost);
            if (res == 0) return true;
            have -= cost - res;
            cost = res;
        }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }

        ExtraInventory extraInventory = ExtraInventory.of(player);
        for (int i = 0; i < SIZE_COINS; i++) {
            extraInventory.setCoins(i, ItemStack.EMPTY);
        }
        Coins coins = decodeCoin(have - cost);

        for (Object2IntMap.Entry<CoinItem> entry : coins.copper2PlatinumEntries()) {
            int coin = entry.getIntValue();
            if (coin <= 0) continue;
            CoinItem coinItem = entry.getKey();
            while (coin > UPGRADES_COUNT) {
                inventory.add(new ItemStack(coinItem, UPGRADES_COUNT));
                coin -= UPGRADES_COUNT;
            }
            inventory.add(new ItemStack(coinItem, coin));
        }
        return true;
    }

    @Deprecated(since = "1.2.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.3.0")
    public static boolean tryCostMoney(long have, Player player, long cost) {
        return tryCostMoney(have, player, cost, true);
    }

    public static Coins decodeCoin(long money) {
        if (money < 0) throw new IllegalArgumentException("Money cannot be negative");

        // jit自动优化
        int p = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT)); // 铂金
        money %= UPGRADES_COUNT * UPGRADES_COUNT * UPGRADES_COUNT;
        int g = (int) (money / (UPGRADES_COUNT * UPGRADES_COUNT)); // 金币
        money %= UPGRADES_COUNT * UPGRADES_COUNT;
        int s = (int) (money / UPGRADES_COUNT); // 银币
        money %= UPGRADES_COUNT;
        int c = (int) money; // 铜币

        return new Coins(c, s, g, p);
    }

    public static Coins decodeCoin(int money) {
        int copper_count = money % UPGRADES_COUNT;
        int i = ((money - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        return new Coins(copper_count, silver_count, golden_count, k);
    }

    public static void sortCoins(Player player) {
        ExtraInventory extraInventory = ExtraInventory.of(player);
        Object2IntOpenHashMap<Integer> map = new Object2IntOpenHashMap<>();
        for (int i = 0; i < SIZE_COINS; i++) {
            ItemStack coins = extraInventory.getCoins(i);
            if (coins.isEmpty() || !coins.is(ModTags.Items.COINS)) continue;
            extraInventory.setCoins(i, ItemStack.EMPTY);
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
                extraInventory.setCoins(j++, new ItemStack(coinItem, MAX_STACK_SIZE));
                count -= MAX_STACK_SIZE;
            }
            if (count > 0) {
                extraInventory.setCoins(j++, new ItemStack(coinItem, count));
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
    public static int getRespawnWaitTime(ServerPlayer player) {
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
    public static boolean applyAutoGetMana(ServerPlayer player, float currentMana, float extract) {
        if (currentMana < extract) {
            if (!TCUtils.hasAccessoriesType(player, AccessoryItems.AUTO$GET$MANA)) return true;
            ItemStack toUse = null;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof ManaPotionItem manaPotion) {
                    int amount = manaPotion.getAmount();
                    if (currentMana + amount < extract) continue;
                    if (toUse == null || amount < ((ManaPotionItem) toUse.getItem()).getAmount()) toUse = itemStack;
                    if (amount == 50) break;
                }
            }
            if (toUse == null) return true;
            toUse.finishUsingItem(player.level(), player);
        }
        return false;
    }

    public static boolean couldPerformEmptyTargetSweep(Player player) {
        if (!player.isAutoSpinAttack()) {
            ItemStack stack = player.getMainHandItem();
            if (BetterCombatHelper.hasWeaponAttributes(stack)) return false;
            return stack.canPerformAction(ItemAbilities.SWORD_SWEEP) && stack.getItem() instanceof BaseSwordItem sword && sword.modifier != null && sword.modifier.specialSweep;
        }
        return false;
    }

    // TODO: 这是飞龙、波涌之刃的发剑气方式，还要写泰拉刃的
    public static void swordProjectile(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BaseSwordItem sword && !player.getCooldowns().isOnCooldown(sword)) {
            SwordProjectileComponent data = stack.get(ModDataComponentTypes.SWORD_PROJECTILE);
            if (data != null) {
                sword.genProjectile(player, stack);
                player.getCooldowns().addCooldown(sword, data.getAttackSpeed(player));
            }
        }
    }
}
