package org.confluence.mod.common.init.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.confluence.terra_curio.common.init.TCItems;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.ParametersAreNonnullByDefault;

public class TreasureBagItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<TreasureBagItem> KING_SLIME_TREASURE_BAG = ITEMS.register("king_slime_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/king_slime"), ModRarity.BLUE));
    public static final PortDeferredItem<TreasureBagItem> EYE_OF_CTHULHU_TREASURE_BAG = ITEMS.register("eye_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eye_of_cthulhu"), ModRarity.BLUE, (level, pos) -> {
        String difficulty = LibUtils.switchByDifficulty(level, pos, "/classic", "/expert", "/master");
        long secretFlag = IMinecraftServer.of(level.getServer()).confluence$getSecretFlag();
        String biome;
        if (IMinecraftServer.equalsSecretFlag(secretFlag, IWorldOptions.DOUBLE_EVIL) || !IMinecraftServer.matchesSecretFlag(secretFlag, IWorldOptions.DOUBLE_EVIL)) {
            biome = "_double_evil";
        } else if (IMinecraftServer.matchesSecretFlag(secretFlag, IWorldOptions.THE_CORRUPTION)) {
            biome = "_corruption";
        } else if (IMinecraftServer.matchesSecretFlag(secretFlag, IWorldOptions.THE_CRIMSON)) {
            biome = "_crimson";
        } else {
            biome = "";
        }
        return difficulty + biome;
    }));
    public static final PortDeferredItem<TreasureBagItem> EATER_OF_WORLDS_TREASURE_BAG = ITEMS.register("eater_of_worlds_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eater_of_worlds"), ModRarity.GREEN));
    public static final PortDeferredItem<TreasureBagItem> BRAIN_OF_CTHULHU_TREASURE_BAG = ITEMS.register("brain_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/brain_of_cthulhu"), ModRarity.GREEN));
    public static final PortDeferredItem<TreasureBagItem> QUEEN_BEE_TREASURE_BAG = ITEMS.register("queen_bee_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/queen_bee"), ModRarity.ORANGE));
    public static final PortDeferredItem<TreasureBagItem> DEERCLOPS_TREASURE_BAG = ITEMS.register("deerclops_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/deerclops"), ModRarity.ORANGE));
    public static final PortDeferredItem<TreasureBagItem> SKELETRON_TREASURE_BAG = ITEMS.register("skeletron_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/skeletron"), ModRarity.ORANGE));
    public static final PortDeferredItem<TreasureBagItem> WALL_OF_FLESH_TREASURE_BAG = ITEMS.register("wall_of_flesh_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/wall_of_flesh"), ModRarity.LIGHT_RED) {
        @ParametersAreNonnullByDefault
        @Override
        protected void collectItems(ServerLevel serverLevel, Player player, ItemStack itemStack, ObjectArrayList<ItemStack> items) {
            if (LibUtils.isAtLeastExpert(serverLevel, player.blockPosition())) {
                CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
                    ICurioStacksHandler iCurioStacksHandler = iCuriosItemHandler.getCurios().get(TerraCurio.CURIO_SLOT);
                    if (TCCommonConfigs.MAX_ACCESSORIES.get() - iCurioStacksHandler.getSlots() > 0) {
                        items.add(TCItems.DEMON_HEART.toStack());
                    }
                });
            }
        }
    });
    public static final PortDeferredItem<TreasureBagItem> HILL_OF_FLESH_TREASURE_BAG = ITEMS.register("hill_of_flesh_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/hill_of_flesh"), ModRarity.LIGHT_RED) {
        @ParametersAreNonnullByDefault
        @Override
        protected void collectItems(ServerLevel serverLevel, Player player, ItemStack itemStack, ObjectArrayList<ItemStack> items) {
            if (LibUtils.isAtLeastExpert(serverLevel, player.blockPosition())) {
                CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
                    ICurioStacksHandler iCurioStacksHandler = iCuriosItemHandler.getCurios().get(TerraCurio.CURIO_SLOT);
                    if (TCCommonConfigs.MAX_ACCESSORIES.get() - iCurioStacksHandler.getSlots() > 0) {
                        items.add(TCItems.DEMON_HEART.toStack());
                    }
                });
            }
        }
    });
    public static final PortDeferredItem<TreasureBagItem> THE_TWINS_TREASURE_BAG = ITEMS.register("the_twins_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/the_twins"), ModRarity.PINK));
    public static final PortDeferredItem<TreasureBagItem> SKELETRON_PRIME_TREASURE_BAG = ITEMS.register("skeletron_prime_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/skeletron_prime"), ModRarity.PINK));
}
