package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;

/// 生成本体植物、材料与通用水果的堆肥数据。
public final class CompostableSubProvider {
    private static final float STANDARD_COMPOST_CHANCE = 0.45F;
    private static final float FRUIT_COMPOST_CHANCE = 0.65F;

    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<Compostable, Item>> appender) {
        DataMapProvider.Builder<Compostable, Item> builder = appender.create();
        Compostable standard = new Compostable(STANDARD_COMPOST_CHANCE);
        builder.add(FoodItems.STELLAR_BLOSSOM_SEED, standard, false)
                .add(FoodItems.CLOUDWEAVER_SEED, standard, false)
                .add(FoodItems.FLOATING_WHEAT_SEED, standard, false)
                .add(FoodItems.WATERLEAF_SEED, standard, false)
                .add(FoodItems.FIREBLOSSOM_SEED, standard, false)
                .add(FoodItems.MOONGLOW_SEED, standard, false)
                .add(FoodItems.BLINKROOT_SEED, standard, false)
                .add(FoodItems.SHIVERTHORN_SEED, standard, false)
                .add(FoodItems.DAYBLOOM_SEED, standard, false)
                .add(FoodItems.DEATHWEED_SEED, standard, false)
                .add(ModItems.GRASS_SEED, standard, false)
                .add(ModItems.JUNGLE_GRASS_SEED, standard, false)
                .add(ModItems.MUSHROOM_GRASS_SEED, standard, false)
                .add(ModItems.CORRUPT_SEED, standard, false)
                .add(ModItems.CRIMSON_SEED, standard, false)
                .add(ModItems.HALLOWED_SEED, standard, false)
                .add(ModItems.ASH_GRASS_SEED, standard, false)
                .add(MaterialItems.WATERLEAF, standard, false)
                .add(MaterialItems.FIREBLOSSOM, standard, false)
                .add(MaterialItems.MOONGLOW, standard, false)
                .add(MaterialItems.BLINKROOT, standard, false)
                .add(MaterialItems.SHIVERTHORN, standard, false)
                .add(MaterialItems.DAYBLOOM, standard, false)
                .add(MaterialItems.DEATHWEED, standard, false)
                .add(MaterialItems.STAR_PETALS, standard, false)
                .add(MaterialItems.FLOATING_WHEAT_HEADS, standard, false)
                .add(MaterialItems.WEAVING_CLOUD_COTTON, standard, false)
                .add(MaterialItems.ROTTEN_CHUNK, standard, false)
                .add(MaterialItems.VERTEBRA, standard, false)
                .add(NatureBlocks.EBONY_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.LIVING_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.PALM_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.SHADOW_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.getId(), standard, false)
                .add(MaterialItems.VICIOUS_MUSHROOM, standard, false)
                .add(MaterialItems.VILE_MUSHROOM, standard, false)
                .add(MaterialItems.GLOWING_MUSHROOM, standard, false)
                .add(MaterialItems.LIFE_MUSHROOM, standard, false)
                .add(MaterialItems.JUNGLE_SPORE, standard, false)
                .add(NatureBlocks.JUNGLE_ROSE.getId(), standard, false)
                .add(NatureBlocks.CORRUPT_GRASS.getId(), standard, false)
                .add(NatureBlocks.CRIMSON_GRASS.getId(), standard, false)
                .add(NatureBlocks.HALLOW_GRASS.getId(), standard, false);
        builder.add(TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/fruit")), new Compostable(FRUIT_COMPOST_CHANCE), false);
    }
}
