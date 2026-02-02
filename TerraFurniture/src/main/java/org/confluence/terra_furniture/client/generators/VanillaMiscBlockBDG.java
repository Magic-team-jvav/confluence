package org.confluence.terra_furniture.client.generators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;

/**
 * 处理门板子的类
 * 计划还能处理栅栏、活板门等
 * 注意：多种方块已被增强。
 */
public abstract class VanillaMiscBlockBDG<T extends Block & BlockSetGetter<T>> extends DefaultBlockDataGenerator<T> {
    public static final String DOOR = "door";
    @Override
    public void buildBlockWithTemplate(T block, BlockStateProvider builderProvider, ExistingFileHelper helper) {
        if (getTemplateType(block).equals(DOOR)) door(block, builderProvider, helper);
    }

    public void door(T block, BlockStateProvider provider, ExistingFileHelper helper) {
        isBlockValid = true;
        ModelFile bottom = fullGenBlock(block, provider.models(), helper, "bottom", true);
        ModelFile bottomOpen = fullGenBlock(block, provider.models(), helper, "bottom_open", true);
        ModelFile top = fullGenBlock(block, provider.models(), helper, "top", true);
        ModelFile topOpen = fullGenBlock(block, provider.models(), helper, "top_open", true);
        if (bottom == null || bottomOpen == null || top == null || topOpen == null) {
            isBlockValid = false;
            return;
        }
        provider.doorBlock((DoorBlock) block, bottom, bottomOpen, bottomOpen, bottom, top, topOpen, topOpen, top);
    }
/*
    public ModelFile trapdoorBottom(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_bottom", texture);
    }

    public ModelFile trapdoorTop(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_top", texture);
    }

    public ModelFile trapdoorOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_open", texture);
    }

    public ModelFile trapdoorOrientableBottom(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_bottom", texture);
    }

    public ModelFile trapdoorOrientableTop(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_top", texture);
    }

    public ModelFile trapdoorOrientableOpen(String name, ResourceLocation texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_open", texture);
    }*/
}
