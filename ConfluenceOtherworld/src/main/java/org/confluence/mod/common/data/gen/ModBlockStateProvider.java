package org.confluence.mod.common.data.gen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
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

        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            simpleBlock(blockSet.PLANKS.get());
            String id = blockSet.id;
            if (blockSet.LOG.isBound()) logBlock(blockSet.LOG.get());
            if (blockSet.STRIPPED_LOG.isBound()) logBlock(blockSet.STRIPPED_LOG.get());
            if (blockSet.LEAVES.isBound()) {
                try {
                    ConfiguredModel configuredModel = new ConfiguredModel(models()
                            .withExistingParent(id + "_leaves", "block/leaves").texture("all", Confluence.asResource("block/" + id + "_leaves")));
                    getVariantBuilder(blockSet.LEAVES.get()).partialState().setModels(configuredModel);
                } catch (Exception ignored) {}
            }
            if (blockSet.WOOD.isBound()) {
                try {
                    ResourceLocation log = Confluence.asResource("block/" + id + "_log");
                    ModelFile model = models().cubeColumn(id + "_wood", log, log);
                    axisBlock(blockSet.WOOD.get(), model, model);
                } catch (Exception ignored) {}
            }
            if (blockSet.STRIPPED_WOOD.isBound()) {
                try {
                    ResourceLocation log = Confluence.asResource("block/stripped_" + id + "_log");
                    ModelFile model = models().cubeColumn("stripped_" + id + "_wood", log, log);
                    axisBlock(blockSet.STRIPPED_WOOD.get(), model, model);
                } catch (Exception ignored) {}
            }
            ResourceLocation planks = Confluence.asResource("block/" + id + "_planks");
            if (blockSet.BUTTON.isBound()) {
                try {
                    buttonBlock(blockSet.BUTTON.get(), planks);
                    models().withExistingParent(id + "_button_inventory", "block/button_inventory").texture("texture", planks);
                } catch (Exception ignored) {}
            }
            if (blockSet.FENCE.isBound()) {
                try {
                    fenceBlock(blockSet.FENCE.get(), planks);
                    models().withExistingParent(id + "_fence_inventory", "block/fence_inventory").texture("texture", planks);
                } catch (Exception ignored) {}
            }
            if (blockSet.FENCE_GATE.isBound())
                fenceGateBlock(blockSet.FENCE_GATE.get(), planks);
            if (blockSet.PRESSURE_PLATE.isBound())
                pressurePlateBlock(blockSet.PRESSURE_PLATE.get(), planks);
            if (blockSet.SLAB.isBound()) slabBlock(blockSet.SLAB.get(), planks, planks);
            if (blockSet.STAIRS.isBound()) stairsBlock(blockSet.STAIRS.get(), planks);
            if (blockSet.SIGN.isBound())
                signBlock(blockSet.SIGN.get(), blockSet.WALL_SIGN.get(), planks);
            if (blockSet.TRAPDOOR.isBound())
                trapdoorBlockWithRenderType(blockSet.TRAPDOOR.get(), Confluence.asResource("block/" + id + "_trapdoor"), true, "cutout");
            if (blockSet.DOOR.isBound()) {
                doorBlockWithRenderType(blockSet.DOOR.get(), Confluence.asResource("block/" + id + "_door_bottom"), Confluence.asResource("block/" + id + "_door_top"), "cutout");
            }
            if (blockSet.HANGING_SIGN.isBound())
                hangingSignBlock(blockSet.HANGING_SIGN.get(), blockSet.WALL_HANGING_SIGN.get(), planks);
            if (blockSet.CHISELED_PLANKS.isBound())
                simpleBlock(blockSet.CHISELED_PLANKS.get());
            if (blockSet.SAPLING.isBound()) {
                try {
                    models().withExistingParent(id + "_sapling", "block/cross").texture("cross", Confluence.asResource("block/" + id + "_sapling"));
                } catch (Exception ignored) {}
            }
        }

        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            ResourceLocation full = Confluence.asResource("block/" + blockSet.id);
            try {
                simpleBlock(blockSet.FULL.get(), models().cubeAll(blockSet.id, full));
            } catch (Exception ignored) {}
            stairsBlock(blockSet.STAIRS.get(), full);
            slabBlock(blockSet.SLAB.get(), full, full);
            wallBlock(blockSet.WALL.get(), full);
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
    public void wallBlock(WallBlock block, ResourceLocation texture) {
        try {
            super.wallBlock(block, texture);
            models().withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath() + "_inventory", ResourceLocation.withDefaultNamespace("block/wall_inventory"))
                    .texture("wall", texture);
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
    public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType) {
        try {
            super.trapdoorBlockWithRenderType(block, texture, orientable, renderType);
        } catch (Exception ignored) {}
    }

    @Override
    public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType) {
        try {
            super.doorBlockWithRenderType(block, bottom, top, renderType);
        } catch (Exception ignored) {}
    }

    @Override
    public void hangingSignBlock(CeilingHangingSignBlock hangingSignBlock, WallHangingSignBlock wallHangingSignBlock, ResourceLocation texture) {
        try {
            super.hangingSignBlock(hangingSignBlock, wallHangingSignBlock, texture);
        } catch (Exception ignored) {}
    }

    @Override
    public void logBlock(RotatedPillarBlock block) {
        try {
            super.logBlock(block);
        } catch (Exception ignored) {}
    }
}
