package org.confluence.mod.integration.mrcrayfish.furniture;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.confluence.terraentity.init.TETags;

import java.util.function.Function;

public class MrCrayfishFurnitureHelper {
    public static ResourceLocation resource(String name) {
        return ResourceLocation.fromNamespaceAndPath("refurbished_furniture", name);
    }

    public static void blockTags(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> consumer) {
        consumer.apply(TETags.Blocks.NPC_HOUSE_CHAIR)
                .addOptional(resource("oak_chair"))
                .addOptional(resource("spruce_chair"))
                .addOptional(resource("birch_chair"))
                .addOptional(resource("jungle_chair"))
                .addOptional(resource("acacia_chair"))
                .addOptional(resource("dark_oak_chair"))
                .addOptional(resource("mangrove_chair"))
                .addOptional(resource("cherry_chair"))
                .addOptional(resource("crimson_chair"))
                .addOptional(resource("warped_chair"))

                .addOptional(resource("white_sofa"))
                .addOptional(resource("orange_sofa"))
                .addOptional(resource("magenta_sofa"))
                .addOptional(resource("light_blue_sofa"))
                .addOptional(resource("yellow_sofa"))
                .addOptional(resource("lime_sofa"))
                .addOptional(resource("pink_sofa"))
                .addOptional(resource("gray_sofa"))
                .addOptional(resource("light_gray_sofa"))
                .addOptional(resource("cyan_sofa"))
                .addOptional(resource("purple_sofa"))
                .addOptional(resource("blue_sofa"))
                .addOptional(resource("brown_sofa"))
                .addOptional(resource("green_sofa"))
                .addOptional(resource("red_sofa"))
                .addOptional(resource("black_sofa"))

                .addOptional(resource("white_stool"))
                .addOptional(resource("orange_stool"))
                .addOptional(resource("magenta_stool"))
                .addOptional(resource("light_blue_stool"))
                .addOptional(resource("yellow_stool"))
                .addOptional(resource("lime_stool"))
                .addOptional(resource("pink_stool"))
                .addOptional(resource("gray_stool"))
                .addOptional(resource("light_gray_stool"))
                .addOptional(resource("cyan_stool"))
                .addOptional(resource("purple_stool"))
                .addOptional(resource("blue_stool"))
                .addOptional(resource("brown_stool"))
                .addOptional(resource("green_stool"))
                .addOptional(resource("red_stool"))
                .addOptional(resource("black_stool"))
                .addOptional(resource("pink_stool"))
        ;

        consumer.apply(TETags.Blocks.NPC_HOUSE_TABLE)
                .addOptional(resource("oak_table"))
                .addOptional(resource("spruce_table"))
                .addOptional(resource("birch_table"))
                .addOptional(resource("jungle_table"))
                .addOptional(resource("acacia_table"))
                .addOptional(resource("dark_oak_table"))
                .addOptional(resource("mangrove_table"))
                .addOptional(resource("cherry_table"))
                .addOptional(resource("crimson_table"))
                .addOptional(resource("warped_table"))

                .addOptional(resource("oak_desk"))
                .addOptional(resource("spruce_desk"))
                .addOptional(resource("birch_desk"))
                .addOptional(resource("jungle_desk"))
                .addOptional(resource("acacia_desk"))
                .addOptional(resource("dark_oak_desk"))
                .addOptional(resource("mangrove_desk"))
                .addOptional(resource("cherry_desk"))
                .addOptional(resource("crimson_desk"))
                .addOptional(resource("warped_desk"))
        ;
    }
}
