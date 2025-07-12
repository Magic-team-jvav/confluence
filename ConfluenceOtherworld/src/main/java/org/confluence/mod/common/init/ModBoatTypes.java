package org.confluence.mod.common.init;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.BoatItems;

import java.util.function.Supplier;

public final class ModBoatTypes {
    public static final EnumProxy<Boat.Type> ASH = register(
            NatureBlocks.ASH_LOG_BLOCKS.getPlanks(),
            "ash",
            BoatItems.ASH_BOAT,
            BoatItems.ASH_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> BAOBAB = register(
            NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(),
            "baobab",
            BoatItems.BAOBAB_BOAT,
            BoatItems.BAOBAB_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> EBONY = register(
            NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(),
            "ebony",
            BoatItems.EBONY_BOAT,
            BoatItems.EBONY_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> GLOWING_MUSHROOM = register(
            NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.getPlanks(),
            "glowing_mushroom",
            BoatItems.GLOWING_MUSHROOM_CHEST_BOAT,
            BoatItems.GLOWING_MUSHROOM_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> LIVING = register(
            NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(),
            "living",
            BoatItems.LIVING_BOAT,
            BoatItems.LIVING_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> LIVING_MAHOGANY = register(
            NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getPlanks(),
            "living_mahogany",
            BoatItems.LIVING_MAHOGANY_BOAT,
            BoatItems.LIVING_MAHOGANY_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> PALM = register(
            NatureBlocks.PALM_LOG_BLOCKS.getPlanks(),
            "palm",
            BoatItems.PALM_BOAT,
            BoatItems.PALM_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> SHADOW = register(
            NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(),
            "shadow",
            BoatItems.SHADOW_BOAT,
            BoatItems.SHADOW_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> SPOOKY = register(
            NatureBlocks.SPOOKY_LOG_BLOCKS.getPlanks(),
            "spooky",
            BoatItems.SPOOKY_BOAT,
            BoatItems.SPOOKY_CHEST_BOAT,
            () -> Items.STICK,
            false
    );
    public static final EnumProxy<Boat.Type> YELLOW_WILLOW = register(
            NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(),
            "yellow_willow",
            BoatItems.YELLOW_WILLOW_BOAT,
            BoatItems.YELLOW_WILLOW_CHEST_BOAT,
            () -> Items.STICK,
            false
    );

    private static EnumProxy<Boat.Type> register(Supplier<Block> planks, String name, Supplier<BoatItem> boatItem, Supplier<BoatItem> chestBoatItem, Supplier<Item> stickItem, boolean raft) {
        return new EnumProxy<>(Boat.Type.class, planks, Confluence.MODID + ':' + name, boatItem, chestBoatItem, stickItem, raft);
    }
}
