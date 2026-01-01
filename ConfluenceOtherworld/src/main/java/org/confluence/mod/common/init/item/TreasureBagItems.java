package org.confluence.mod.common.init.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.common.TreasureBagItem;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.confluence.terra_curio.common.init.TCItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.ParametersAreNonnullByDefault;

public class TreasureBagItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<TreasureBagItem> KING_SLIME_TREASURE_BAG = ITEMS.register("king_slime_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/king_slime"), ModRarity.BLUE));
    public static final DeferredItem<TreasureBagItem> EYE_OF_CTHULHU_TREASURE_BAG = ITEMS.register("eye_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eye_of_cthulhu"), ModRarity.BLUE, (level, pos) -> {
        String difficulty = LibUtils.switchByDifficulty(level, pos, "/classic", "/expert", "/master");
        long secretFlag = IMinecraftServer.of(level.getServer()).confluence$getSecretFlag();
        String biome;
        if ((secretFlag & IWorldOptions.DOUBLE_EVIL) == IWorldOptions.DOUBLE_EVIL || (secretFlag & IWorldOptions.DOUBLE_EVIL) == 0) {
            biome = "_double_evil";
        } else if ((secretFlag & IWorldOptions.THE_CORRUPTION) != 0) {
            biome = "_corruption";
        } else if ((secretFlag & IWorldOptions.THE_CRIMSON) != 0) {
            biome = "_crimson";
        } else {
            biome = "";
        }
        return difficulty + biome;
    }));
    public static final DeferredItem<TreasureBagItem> EATER_OF_WORLDS_TREASURE_BAG = ITEMS.register("eater_of_worlds_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/eater_of_worlds"), ModRarity.GREEN));
    public static final DeferredItem<TreasureBagItem> BRAIN_OF_CTHULHU_TREASURE_BAG = ITEMS.register("brain_of_cthulhu_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/brain_of_cthulhu"), ModRarity.GREEN));
    public static final DeferredItem<TreasureBagItem> QUEEN_BEE_TREASURE_BAG = ITEMS.register("queen_bee_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/queen_bee"), ModRarity.ORANGE));
    public static final DeferredItem<TreasureBagItem> DEERCLOPS_TREASURE_BAG = ITEMS.register("deerclops_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/deerclops"), ModRarity.ORANGE));
    public static final DeferredItem<TreasureBagItem> SKELETRON_TREASURE_BAG = ITEMS.register("skeletron_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/skeletron"), ModRarity.ORANGE));
    public static final DeferredItem<TreasureBagItem> WALL_OF_FLESH_TREASURE_BAG = ITEMS.register("wall_of_flesh_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/wall_of_flesh"), ModRarity.LIGHT_RED) {
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
    public static final DeferredItem<TreasureBagItem> HILL_OF_FLESH_TREASURE_BAG = ITEMS.register("hill_of_flesh_treasure_bag", () -> new TreasureBagItem(Confluence.asResource("treasure_bag/hill_of_flesh"), ModRarity.LIGHT_RED) {
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
    //public static final DeferredItem<TreasureBagItem> QUEEN_SLIME_TREASURE_BAG =
}
