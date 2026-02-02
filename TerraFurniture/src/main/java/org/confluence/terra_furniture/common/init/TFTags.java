package org.confluence.terra_furniture.common.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.confluence.terra_furniture.TerraFurniture;

public class TFTags {
    /* 原版材质 */
    public static final TagKey<Block> OAK_FURNITURE = register("oak_furniture");
    public static final TagKey<Block> SPRUCE_FURNITURE = register("spruce_furniture");
    public static final TagKey<Block> BIRCH_FURNITURE = register("birch_furniture");
    public static final TagKey<Block> ACACIA_FURNITURE = register("acacia_furniture");
    public static final TagKey<Block> CHERRY_FURNITURE = register("cherry_furniture");
    public static final TagKey<Block> JUNGLE_FURNITURE = register("jungle_furniture");
    public static final TagKey<Block> DARK_OAK_FURNITURE = register("dark_oak_furniture");
    public static final TagKey<Block> CRIMSON_FURNITURE = register("crimson_furniture");
    public static final TagKey<Block> WARPED_FURNITURE = register("warped_furniture");
    public static final TagKey<Block> MANGROVE_FURNITURE = register("mangrove_furniture");
    public static final TagKey<Block> BAMBOO_FURNITURE = register("bamboo_furniture");
    public static final TagKey<Block> IRON_FURNITURE = register("iron_furniture");
    public static final TagKey<Block> COPPER_FURNITURE = register("copper_furniture");
    public static final TagKey<Block> GOLD_FURNITURE = register("gold_furniture");
    public static final TagKey<Block> STONE_FURNITURE = register("stone_furniture");
    public static final TagKey<Block> POLISHED_BLACKSTONE_FURNITURE = register("polished_blackstone_furniture");

    /* 额外材质 */
    public static final TagKey<Block> OBSIDIAN_FURNITURE = register("obsidian_furniture");
    public static final TagKey<Block> BLUE_DUNGEON_FURNITURE = register("blue_dungeon_furniture");
    public static final TagKey<Block> GREEN_DUNGEON_FURNITURE = register("green_dungeon_furniture");
    public static final TagKey<Block> PINK_DUNGEON_FURNITURE = register("pink_dungeon_furniture");
    public static final TagKey<Block> GOTHIC_FURNITURE = register("gothic_furniture");
    public static final TagKey<Block> BONE_FURNITURE = register("bone_furniture");
    public static final TagKey<Block> LESION_FURNITURE = register("lesion_furniture");
    public static final TagKey<Block> FLESH_FURNITURE = register("flesh_furniture");
    public static final TagKey<Block> GLASS_FURNITURE = register("glass_furniture");
    public static final TagKey<Block> HONEY_FURNITURE = register("honey_furniture");
    public static final TagKey<Block> FROZEN_FURNITURE = register("frozen_furniture");
    public static final TagKey<Block> LIHZAHRD_FURNITURE = register("lihzahrd_furniture");
    public static final TagKey<Block> LIVING_WOOD_FURNITURE = register("living_wood_furniture");
    public static final TagKey<Block> SKYWARE_FURNITURE = register("skyware_furniture");
    public static final TagKey<Block> SLIME_FURNITURE = register("slime_furniture");
    public static final TagKey<Block> STEAMPUNK_FURNITURE = register("steampunk_furniture");
    public static final TagKey<Block> ASH_WOOD_FURNITURE = register("ash_wood_furniture");
    public static final TagKey<Block> BALLOON_FURNITURE = register("balloon_furniture");
    public static final TagKey<Block> CACTUS_FURNITURE = register("cactus_furniture");
    public static final TagKey<Block> CRYSTAL_FURNITURE = register("crystal_furniture");
    public static final TagKey<Block> DYNASTY_FURNITURE = register("dynasty_furniture");
    public static final TagKey<Block> EBONWOOD_FURNITURE = register("ebonwood_furniture");
    public static final TagKey<Block> GRANITE_FURNITURE = register("granite_furniture");
    public static final TagKey<Block> MARBLE_FURNITURE = register("marble_furniture");
    public static final TagKey<Block> MARTIAN_FURNITURE = register("martian_furniture");
    public static final TagKey<Block> METEORITE_FURNITURE = register("meteorite_furniture");
    public static final TagKey<Block> MUSHROOM_FURNITURE = register("mushroom_furniture");
    public static final TagKey<Block> PALM_WOOD_FURNITURE = register("palm_wood_furniture");
    public static final TagKey<Block> PEARLWOOD_FURNITURE = register("pearlwood_furniture");
    public static final TagKey<Block> PINE_FURNITURE = register("pine_furniture");
    public static final TagKey<Block> PUMPKIN_FURNITURE = register("pumpkin_furniture");
    public static final TagKey<Block> REEF_FURNITURE = register("reef_furniture");
    public static final TagKey<Block> SANDSTONE_FURNITURE = register("sandstone_furniture");
    public static final TagKey<Block> SHADEWOOD_FURNITURE = register("shadewood_furniture");
    public static final TagKey<Block> SPIDER_FURNITURE = register("spider_furniture");
    public static final TagKey<Block> SPOOKY_FURNITURE = register("spooky_furniture");
    public static final TagKey<Block> NEBULA_FURNITURE = register("nebula_furniture");
    public static final TagKey<Block> SOLAR_FURNITURE = register("solar_furniture");
    public static final TagKey<Block> STARDUST_FURNITURE = register("stardust_furniture");
    public static final TagKey<Block> VORTEX_FURNITURE = register("vortex_furniture");
    public static final TagKey<Block> DUNGEON_FURNITURE = register("dungeon_furniture");
    public static final TagKey<Block> WOODEN_FURNITURE = register("wooden_furniture");



    public static final TagKey<Block> SINKS = register("sinks");
    public static final TagKey<Block> BATHTUBS = register("bathtubs");
    public static final TagKey<Block> HOUSE_CHAIR = register("house_chair");
    public static final TagKey<Block> HOUSE_TABLE = register("house_table");

    private static TagKey<Block> register(String id) {
        return BlockTags.create(TerraFurniture.asResource(id));
    }
}
