package org.confluence.mod.common.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.OreBlocks;

import static org.confluence.mod.Confluence.MODID;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        OreBlocks.BLOCKS.getEntries().forEach(block -> simpleBlock(block.get()));
        DecorativeBlocks.BLOCKS.getEntries().forEach(block -> {
            // todo
            simpleBlock(block.get());
        });

        for (LogBlockSet logBlockSet : LogBlockSet.LOG_BLOCK_SETS) {
            simpleBlock(logBlockSet.PLANKS.get());
            String id = logBlockSet.id;
            if (logBlockSet.LOG.isBound()) axisBlock(logBlockSet.LOG.get(), Confluence.asResource("block/" + id + "_log"));
            if (logBlockSet.STRIPPED_LOG.isBound()) axisBlock(logBlockSet.STRIPPED_LOG.get(), Confluence.asResource("block/stripped_" + id + "_log"));
            if (logBlockSet.LEAVES.isBound()) {
                try {
                    ConfiguredModel configuredModel = new ConfiguredModel(models()
                            .withExistingParent(id + "_leaves", "block/leaves").texture("all", Confluence.asResource("block/" + id + "_leaves")));
                    getVariantBuilder(logBlockSet.LEAVES.get()).partialState().setModels(configuredModel);
                } catch (Exception ignored) {}
            }
            if (logBlockSet.WOOD.isBound()) {
                ResourceLocation side = Confluence.asResource("block/" + id + "_log_side");
                axisBlock(logBlockSet.WOOD.get(), side, side);
            }
            if (logBlockSet.STRIPPED_WOOD.isBound()) {
                ResourceLocation side = Confluence.asResource("block/stripped_" + id + "_log_side");
                axisBlock(logBlockSet.STRIPPED_WOOD.get(), side, side);
            }
            ResourceLocation planks = Confluence.asResource("block/" + id + "_planks");
            if (logBlockSet.BUTTON.isBound()) {
                try {
                    buttonBlock(logBlockSet.BUTTON.get(), planks);
                    models().withExistingParent(id + "_button_inventory", "block/button_inventory").texture("texture", planks);
                } catch (Exception ignored) {}
            }
            if (logBlockSet.FENCE.isBound()) {
                try {
                    fenceBlock(logBlockSet.FENCE.get(), planks);
                    models().withExistingParent(id + "_fence_inventory", "block/fence_inventory").texture("texture", planks);
                } catch (Exception ignored) {}
            }
            if (logBlockSet.FENCE_GATE.isBound()) fenceGateBlock(logBlockSet.FENCE_GATE.get(), planks);
            if (logBlockSet.PRESSURE_PLATE.isBound()) pressurePlateBlock(logBlockSet.PRESSURE_PLATE.get(), planks);
            if (logBlockSet.SLAB.isBound()) slabBlock(logBlockSet.SLAB.get(), planks, planks);
            if (logBlockSet.STAIRS.isBound()) stairsBlock(logBlockSet.STAIRS.get(), planks);
            if (logBlockSet.SIGN.isBound()) signBlock(logBlockSet.SIGN.get(), logBlockSet.WALL_SIGN.get(), planks);
            if (logBlockSet.TRAPDOOR.isBound()) trapdoorBlock(logBlockSet.TRAPDOOR.get(), Confluence.asResource("block/" + id + "_trapdoor"), true);
            if (logBlockSet.DOOR.isBound()) doorBlock(logBlockSet.DOOR.get(), Confluence.asResource("block/" + id + "_door_bottom"), Confluence.asResource("block/" + id + "_door_top"));
            if (logBlockSet.HANGING_SIGN.isBound()) hangingSignBlock(logBlockSet.HANGING_SIGN.get(), logBlockSet.WALL_HANGING_SIGN.get(), planks);
            if (logBlockSet.CHISELED_PLANKS.isBound()) simpleBlock(logBlockSet.CHISELED_PLANKS.get());
        }
    }

    @Override
    public void simpleBlock(Block block) {
        try {
            super.simpleBlock(block);
        } catch (Exception ignored) {}
    }

    @Override
    public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        try {
            super.axisBlock(block, side, end);
        } catch (Exception ignored) {}
    }

    @Override
    public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
        try {
            super.fenceGateBlock(block, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
        try {
            super.pressurePlateBlock(block, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
        try {
            super.slabBlock(block, doubleslab, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void stairsBlock(StairBlock block, ResourceLocation texture) {
        try {
            super.stairsBlock(block, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ResourceLocation texture) {
        try {
            super.signBlock(signBlock, wallSignBlock, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        try {
            super.trapdoorBlock(block, texture, orientable);
        } catch (Exception ignored) {}
    }

    @Override
    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        try {
            super.doorBlock(block, bottom, top);
        } catch (Exception ignored) {}
    }

    @Override
    public void hangingSignBlock(CeilingHangingSignBlock hangingSignBlock, WallHangingSignBlock wallHangingSignBlock, ResourceLocation texture) {
        try {
            super.hangingSignBlock(hangingSignBlock, wallHangingSignBlock, texture);
        } catch (Exception ignored) {}
    }
}
