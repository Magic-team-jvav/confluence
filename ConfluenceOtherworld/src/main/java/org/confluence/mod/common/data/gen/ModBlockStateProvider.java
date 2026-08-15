package org.confluence.mod.common.data.gen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.OreBlocks;

import java.io.FileNotFoundException;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.MODID;

/**
 * 生成结构固定、能够由注册信息完整推导的方块状态和模型。
 *
 * <p>主资源目录中的手写 JSON 始终拥有优先权。随机变体、特殊旋转、自定义几何等复杂资源
 * 不在这里重新描述，避免每增加一种复杂方块都继续扩充专用名单和分支。</p>
 */
public final class ModBlockStateProvider extends BlockStateProvider {
    private final ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        OreBlocks.BLOCKS.getEntries().forEach(entry -> simpleBlockIfAbsent(entry.get()));
        DecorativeBlocks.BLOCKS.getEntries().forEach(entry -> simpleBlockIfAbsent(entry.get()));

        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            registerLogSet(blockSet);
        }
        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            registerDecorationSet(blockSet);
        }
    }

    private void registerLogSet(LogBlockSet blockSet) {
        simpleBlockIfAbsent(blockSet.PLANKS.get());
        String id = blockSet.id;

        ResourceLocation logSide = Confluence.asResource("block/" + id + "_log");
        ResourceLocation logTop = Confluence.asResource("block/" + id + "_log_top");
        if (blockSet.LOG.isBound()
                && shouldGenerate(blockSet.LOG.get())
                && hasTexture(logSide)
                && hasTexture(logTop)) {
            logBlock(blockSet.LOG.get());
        }
        ResourceLocation strippedLogSide = Confluence.asResource("block/stripped_" + id + "_log");
        ResourceLocation strippedLogTop = Confluence.asResource("block/stripped_" + id + "_log_top");
        if (blockSet.STRIPPED_LOG.isBound()
                && shouldGenerate(blockSet.STRIPPED_LOG.get())
                && hasTexture(strippedLogSide)
                && hasTexture(strippedLogTop)) {
            logBlock(blockSet.STRIPPED_LOG.get());
        }
        ResourceLocation leavesTexture = Confluence.asResource("block/" + id + "_leaves");
        if (blockSet.LEAVES.isBound()
                && shouldGenerate(blockSet.LEAVES.get())
                && hasTexture(leavesTexture)) {
            ModelFile leaves = models().withExistingParent(id + "_leaves", "block/leaves")
                    .texture("all", leavesTexture);
            getVariantBuilder(blockSet.LEAVES.get()).partialState()
                    .setModels(new ConfiguredModel(leaves));
        }
        if (blockSet.WOOD.isBound()
                && shouldGenerate(blockSet.WOOD.get())
                && hasTexture(logSide)) {
            ModelFile model = models().cubeColumn(id + "_wood", logSide, logSide);
            axisBlock(blockSet.WOOD.get(), model, model);
        }
        if (blockSet.STRIPPED_WOOD.isBound()
                && shouldGenerate(blockSet.STRIPPED_WOOD.get())
                && hasTexture(strippedLogSide)) {
            ModelFile model = models().cubeColumn(
                    "stripped_" + id + "_wood",
                    strippedLogSide,
                    strippedLogSide);
            axisBlock(blockSet.STRIPPED_WOOD.get(), model, model);
        }

        ResourceLocation planks = Confluence.asResource("block/" + id + "_planks");
        if (blockSet.BUTTON.isBound() && shouldGenerate(blockSet.BUTTON.get())) {
            buttonBlock(blockSet.BUTTON.get(), planks);
            models().withExistingParent(id + "_button_inventory", "block/button_inventory")
                    .texture("texture", planks);
        }
        if (blockSet.FENCE.isBound() && shouldGenerate(blockSet.FENCE.get())) {
            fenceBlock(blockSet.FENCE.get(), planks);
            models().withExistingParent(id + "_fence_inventory", "block/fence_inventory")
                    .texture("texture", planks);
        }
        if (blockSet.FENCE_GATE.isBound() && shouldGenerate(blockSet.FENCE_GATE.get())) {
            fenceGateBlock(blockSet.FENCE_GATE.get(), planks);
        }
        if (blockSet.PRESSURE_PLATE.isBound() && shouldGenerate(blockSet.PRESSURE_PLATE.get())) {
            pressurePlateBlock(blockSet.PRESSURE_PLATE.get(), planks);
        }
        if (blockSet.SLAB.isBound() && shouldGenerate(blockSet.SLAB.get())) {
            slabBlock(blockSet.SLAB.get(), planks, planks);
        }
        if (blockSet.STAIRS.isBound() && shouldGenerate(blockSet.STAIRS.get())) {
            stairsBlock(blockSet.STAIRS.get(), planks);
        }
        if (blockSet.SIGN.isBound()
                && shouldGenerate(blockSet.SIGN.get())
                && shouldGenerate(blockSet.WALL_SIGN.get())) {
            signBlock(blockSet.SIGN.get(), blockSet.WALL_SIGN.get(), planks);
        }
        ResourceLocation trapdoor = Confluence.asResource("block/" + id + "_trapdoor");
        if (blockSet.TRAPDOOR.isBound()
                && shouldGenerate(blockSet.TRAPDOOR.get())
                && hasTexture(trapdoor)) {
            trapdoorBlockWithRenderType(
                    blockSet.TRAPDOOR.get(),
                    trapdoor,
                    true,
                    "cutout");
        }
        ResourceLocation doorBottom = Confluence.asResource("block/" + id + "_door_bottom");
        ResourceLocation doorTop = Confluence.asResource("block/" + id + "_door_top");
        if (blockSet.DOOR.isBound()
                && shouldGenerate(blockSet.DOOR.get())
                && hasTexture(doorBottom)
                && hasTexture(doorTop)) {
            doorBlockWithRenderType(
                    blockSet.DOOR.get(),
                    doorBottom,
                    doorTop,
                    "cutout");
        }
        if (blockSet.HANGING_SIGN.isBound()
                && shouldGenerate(blockSet.HANGING_SIGN.get())
                && shouldGenerate(blockSet.WALL_HANGING_SIGN.get())) {
            hangingSignBlock(blockSet.HANGING_SIGN.get(), blockSet.WALL_HANGING_SIGN.get(), planks);
        }
        if (blockSet.CHISELED_PLANKS.isBound()) {
            simpleBlockIfAbsent(blockSet.CHISELED_PLANKS.get());
        }
        ResourceLocation sapling = Confluence.asResource("block/" + id + "_sapling");
        if (blockSet.SAPLING.isBound()
                && !hasHandwrittenModel(sapling)
                && hasTexture(sapling)) {
            models().withExistingParent(id + "_sapling", "block/cross")
                    .texture("cross", sapling);
        }
    }

    private void registerDecorationSet(DecoBlockSet blockSet) {
        ResourceLocation texture = Confluence.asResource("block/" + blockSet.id);
        if (!hasTexture(texture)) {
            return;
        }

        ModelFile full = modelOrGenerate(
                blockSet.id,
                () -> models().cubeAll(blockSet.id, texture));
        ModelFile stairs = modelOrGenerate(
                blockSet.id + "_stairs",
                () -> models().stairs(blockSet.id + "_stairs", texture, texture, texture));
        ModelFile stairsInner = modelOrGenerate(
                blockSet.id + "_stairs_inner",
                () -> models().stairsInner(
                        blockSet.id + "_stairs_inner", texture, texture, texture));
        ModelFile stairsOuter = modelOrGenerate(
                blockSet.id + "_stairs_outer",
                () -> models().stairsOuter(
                        blockSet.id + "_stairs_outer", texture, texture, texture));
        ModelFile slab = modelOrGenerate(
                blockSet.id + "_slab",
                () -> models().slab(blockSet.id + "_slab", texture, texture, texture));
        ModelFile slabTop = modelOrGenerate(
                blockSet.id + "_slab_top",
                () -> models().slabTop(
                        blockSet.id + "_slab_top", texture, texture, texture));
        ModelFile slabFull = modelOrGenerate(
                blockSet.id + "_slab_full",
                () -> models().cubeAll(blockSet.id + "_slab_full", texture));
        ModelFile wallPost = modelOrGenerate(
                blockSet.id + "_wall_post",
                () -> models().wallPost(blockSet.id + "_wall_post", texture));
        ModelFile wallSide = modelOrGenerate(
                blockSet.id + "_wall_side",
                () -> models().wallSide(blockSet.id + "_wall_side", texture));
        ModelFile wallSideTall = modelOrGenerate(
                blockSet.id + "_wall_side_tall",
                () -> models().wallSideTall(blockSet.id + "_wall_side_tall", texture));
        modelOrGenerate(
                blockSet.id + "_wall_inventory",
                () -> models().withExistingParent(
                                blockSet.id + "_wall_inventory", "block/wall_inventory")
                        .texture("wall", texture));

        if (shouldGenerate(blockSet.FULL.get())) {
            simpleBlock(blockSet.FULL.get(), full);
        }
        if (shouldGenerate(blockSet.STAIRS.get())) {
            stairsBlock(blockSet.STAIRS.get(), stairs, stairsInner, stairsOuter);
        }
        if (shouldGenerate(blockSet.SLAB.get())) {
            slabBlock(blockSet.SLAB.get(), slab, slabTop, slabFull);
        }
        if (shouldGenerate(blockSet.WALL.get())) {
            wallBlock(blockSet.WALL.get(), wallPost, wallSide, wallSideTall);
        }
    }

    /**
     * 获取已有手写模型，或生成一个结构可由方块组定义完整推导的常规模型。
     * 方块状态和模型分别判断所有权，部分手写时不会再把同组的其他模型一起跳过。
     */
    private ModelFile modelOrGenerate(String path, Supplier<ModelFile> factory) {
        ResourceLocation location = Confluence.asResource("block/" + path);
        return hasHandwrittenModel(location)
                ? models().getExistingFile(location)
                : factory.get();
    }

    private void simpleBlockIfAbsent(Block block) {
        if (!shouldGenerate(block)) {
            return;
        }
        ResourceLocation model = Confluence.asResource(
                "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath());
        if (hasHandwrittenModel(model)) {
            simpleBlock(block, models().getExistingFile(model));
        } else if (hasTexture(model)) {
            /*
             * 只有存在同名纹理时才能从注册名完整推导 cube_all 模型。
             * 没有同名纹理的方块通常复用其他纹理或需要复杂状态，应继续保留手写资源，
             * 不能让通用生成规则猜测模型并阻断整个 DataGen。
             */
            simpleBlock(block);
        }
    }

    public void hangingSignBlock(
            CeilingHangingSignBlock hangingSign,
            WallHangingSignBlock wallHangingSign,
            ResourceLocation texture
    ) {
        ModelFile model = models().sign(
                ForgeRegistries.BLOCKS.getKey(hangingSign).getPath(), texture);
        simpleBlock(hangingSign, model);
        simpleBlock(wallHangingSign, model);
    }

    private boolean shouldGenerate(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        try {
            existingFileHelper.getResource(id, PackType.CLIENT_RESOURCES, ".json", "blockstates");
            return false;
        } catch (FileNotFoundException ignored) {
            return true;
        }
    }

    private boolean hasHandwrittenModel(ResourceLocation model) {
        try {
            existingFileHelper.getResource(model, PackType.CLIENT_RESOURCES, ".json", "models");
            return true;
        } catch (FileNotFoundException ignored) {
            return false;
        }
    }

    private boolean hasTexture(ResourceLocation model) {
        try {
            existingFileHelper.getResource(
                    model,
                    PackType.CLIENT_RESOURCES,
                    ".png",
                    "textures");
            return true;
        } catch (FileNotFoundException ignored) {
            return false;
        }
    }
}
