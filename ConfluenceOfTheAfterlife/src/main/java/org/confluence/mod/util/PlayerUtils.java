package org.confluence.mod.util;

import com.xiaohunao.heaven_destiny_moment.common.moment.MomentManager;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.GetCustomDiggingPowerEvent;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.network.s2c.GamePhasePacketS2C;
import org.confluence.mod.network.s2c.ManaPacketS2C;
import org.confluence.mod.network.s2c.StarPhasesPacketS2C;
import org.confluence.mod.network.s2c.WindSpeedPacketS2C;
import org.confluence.terra_curio.util.TCUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.mod.common.attachment.ExtraInventory.COINS_START;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;

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
        case 3 -> ModItems.PLATINUM_COIN.get();
        default -> ModItems.EMERALD_COIN.get();
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
        boolean notMove = Math.abs(serverPlayer.xCloak - serverPlayer.xCloakO) < 1.0E-7;
        if (delay > 0) {
            if (manaStorage.isArcaneCrystalUsed()) delay = (int) ((float) delay * (notMove ? 0.975F : 0.95F));
            if (delay > 20 && serverPlayer.hasEffect(ModEffects.MANA_REGENERATION)) delay = 20;
            int delayReduce = notMove ? 2 : 1;
            if (manaStorage.isFastManaRegeneration()) delayReduce += 1;
            manaStorage.setRegenerateDelay(delay - delayReduce);
            return;
        }

        IntSupplier receive = () -> {
            // 1.0F / 7.0F = 0.14285715F
            float a = manaStorage.getMaxMana() * 0.14285715F + (manaStorage.isFastManaRegeneration() ? 25 : 0) + 1;
            float b = manaStorage.getCurrentMana() * 0.8F / manaStorage.getMaxMana() + 0.2F;
            if (notMove) a += manaStorage.getMaxMana() * 0.5F;
            return Math.max(Math.round(a * b * 0.0115F), 1);
        };

        if (manaStorage.receiveMana(receive)) syncMana2Client(serverPlayer, manaStorage);
    }

    public static boolean extractMana(ServerPlayer serverPlayer, IntSupplier sup) {
        if (serverPlayer.gameMode.isCreative()) return true;
        boolean success = false;
        ManaStorage manaStorage = serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE);
        if (manaStorage.extractMana(sup, serverPlayer)) {
            success = true;
            manaStorage.setRegenerateDelay((int) Math.ceil(0.7F * ((1 - (float) manaStorage.getCurrentMana() / manaStorage.getMaxMana()) * 240 + 45)));
            syncMana2Client(serverPlayer, manaStorage);
        }
        return success;
    }

    public static void receiveMana(ServerPlayer serverPlayer, IntSupplier sup) {
        ManaStorage manaStorage = serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE);
        if (manaStorage.receiveMana(sup)) syncMana2Client(serverPlayer, manaStorage);
    }

    public static void syncSavedData(ServerPlayer serverPlayer) {
        ConfluenceData data = ConfluenceData.get(serverPlayer.serverLevel());
        WindSpeedPacketS2C.sendToClient(serverPlayer, data.getWindSpeedX(), data.getWindSpeedZ());
        GamePhasePacketS2C.sendToClient(serverPlayer, data.getGamePhase());
        StarPhasesPacketS2C.sendToClient(serverPlayer, data.getStarPhases());
    }

    public static float getFishingPower(ServerPlayer player) {
        float base = TCUtils.getAccessoriesValue(player, AccessoryItems.FISHING$POWER);
        if (player.getData(ModAttachmentTypes.EVER_BENEFICIAL).isGummyWormUsed()) base += 3.0F;
        Level level = player.level();
        long dayTime = level.dayTime() % 24000; // [0, 23999]
        if (level.isRaining()) base *= 1.1F;
        else if (level.isThundering()) base *= 1.2F;
        if (dayTime >= 22500 || dayTime == 0) base *= 1.3F; // 04:30 -> 06:00
        else if (dayTime >= 3000 && dayTime <= 9000) base *= 0.8F; // 09:00 -> 15:00
        else if (dayTime >= 12000 && dayTime <= 13500) base *= 1.3F; // 18:00 -> 19:30
        else if (dayTime >= 15300 && dayTime <= 20200) base *= 0.8F; // 21:18 -> 02:12
        base *= switch (level.getMoonPhase()) {
            case 0 -> 1.1F; // 满月
            case 1, 7 -> 1.05F; // 凸月
            case 5 -> 0.95F; // 眉月
            case 4 -> 0.9F; // 新月
            default -> 1.0F;
        };
        if (MomentManager.of(level).hasMoment(TMMoments.BLOOD_MOON)) {
            base *= 1.1F;
        }
        return base + player.getLuck();
    }

    public static Tuple<ItemStack, Integer> getMaxDiggingPowerItem(Player player) {
        int max = 0;
        ItemStack ret = ItemStack.EMPTY;
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof PickaxeItem pickaxeItem) {
                Tier tier = pickaxeItem.getTier();
                if (tier instanceof ModTiers.PoweredTier poweredTier) {
                    if (poweredTier.getPower() > max) {
                        max = poweredTier.getPower();
                        ret = itemStack;
                        continue;
                    }
                } else if (tier instanceof Tiers tiers) {
                    int power = ModTiers.getPowerForVanillaTiers(tiers);
                    if (power > max) {
                        max = power;
                        ret = itemStack;
                        continue;
                    }
                }
            }
            GetCustomDiggingPowerEvent e = NeoForge.EVENT_BUS.post(new GetCustomDiggingPowerEvent(itemStack, max));
            if (e.getPower() > max) {
                max = e.getPower();
                ret = itemStack;
            }
        }
        return new Tuple<>(ret, max);
    }

    public static void awardAchievement(ServerPlayer serverPlayer, String path) {
        CompoundTag data = serverPlayer.getPersistentData();
        String key = Confluence.MODID + ":" + path;
        if (!data.getBoolean(key)) {
            AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(Confluence.asResource("achievements/" + path));
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, "never");
            }
            data.putBoolean(key, true);
        }
    }

    public static int getItemCount(List<ItemStack> have, Item item) {
        int count = 0;
        for (ItemStack itemStack : have) {
            if (!itemStack.isEmpty() && itemStack.is(item)) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

    public static void consumeItemCount(List<ItemStack> have, Item item, int consumeCount) {
        AtomicInteger count = new AtomicInteger();
        have.forEach(stack -> {
            if (stack.is(item) && count.get() < consumeCount) {
                int toConsume = Math.min(stack.getCount(), consumeCount - count.get());
                stack.shrink(toConsume);
                count.addAndGet(toConsume);
            }
        });
    }

    public static int[] getCoins(Player player) {
        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        int[] coins = new int[SIZE_COINS];
        for (int i = 0; i < SIZE_COINS; i++) {
            ItemStack stack = extraInventory.getCoins(i);
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    coins[index] += stack.getCount();
                }
            }
        }
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(ModTags.Items.COINS)) {
                int index = COIN_2_INDEX.applyAsInt(stack.getItem());
                if (index != -1) {
                    coins[index] += stack.getCount();
                }
            }
        }
        return coins;
    }

    public static long getMoney(Player player) {
        int[] coins = getCoins(player);
        long res = 0;
        for (int i = 0; i < SIZE_COINS; i++) {
            res += (int) (coins[i] * Math.pow(99, 3 - i));
        }
        return res;
    }

    public static boolean tryCostMoney(Player player, long cost) {
        long have = getMoney(player);
        if (have < cost) return false;

        for (ItemStack itemStack : player.getInventory().items) {
            if (!itemStack.isEmpty() && itemStack.is(ModTags.Items.COINS)) {
                itemStack.setCount(0);
            }
        }

        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        for (int i = 0; i < SIZE_COINS; i++) {
            extraInventory.getCoins(i).setCount(0);
        }
        int[] coins = decodeCoin(have - cost);

        for (int i = 0; i < SIZE_COINS; i++) {
            player.getInventory().add(new ItemStack(INDEX_2_COIN.apply(i), coins[i]));
        }
        return true;
    }

    public static int[] decodeCoin(long money) {
        int[] coins = new int[SIZE_COINS];
        int multiple = 99;
        for (int i = 0; i < SIZE_COINS; i++) {
            long num = money % multiple;
            if (num > 0) {
                coins[i] = (int) num;
            }
            money /= multiple;
        }
        return coins;
    }

    public static void sortCoins(Player player) {
        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        Object2IntOpenHashMap<Integer> map = new Object2IntOpenHashMap<>();
        for (int i = 0; i < SIZE_COINS; i++) {
            ItemStack coins = extraInventory.getCoins(i);
            if (coins.isEmpty() || !coins.is(ModTags.Items.COINS)) continue;
            upgradesCoin(map, COIN_2_INDEX.applyAsInt(coins.getItem()), i, coins.getCount(), extraInventory);
        }
        for (int i = 0, j = 0; i < SIZE_COINS; i++) {
            int count = map.getInt(i);
            if (count <= 0) continue;
            CoinItem coinItem = INDEX_2_COIN.apply(3 - i);
            while (count > 99) {
                extraInventory.setItem(COINS_START + j++, new ItemStack(coinItem, 99));
                count -= 99;
            }
            if (count > 0) {
                extraInventory.setItem(COINS_START + j++, new ItemStack(coinItem, count));
            }
        }
    }

    private static void upgradesCoin(Object2IntOpenHashMap<Integer> map, int index, int slot, int count, ExtraInventory extraInventory) {
        if (map.addTo(index, count) + count < 99) return;
        Supplier<CoinItem> upgrade = INDEX_2_COIN.apply(3 - index).upgrade;
        if (upgrade == null) return;
        extraInventory.setItem(COINS_START + slot, ItemStack.EMPTY);
        upgradesCoin(map, COIN_2_INDEX.applyAsInt(upgrade.get()), index, 1, extraInventory);
        map.addTo(index, -99);
    }
}
