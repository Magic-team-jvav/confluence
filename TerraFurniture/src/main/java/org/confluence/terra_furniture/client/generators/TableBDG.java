package org.confluence.terra_furniture.client.generators;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.common.block.misc.TableBlock;

public abstract class TableBDG extends DefaultBlockDataGenerator<TableBlock> {


    public void buildTemplate1(TableBlock block, BlockModelProvider provider, MultiPartBlockStateBuilder builder, ExistingFileHelper helper) {
        isBlockValid = true; // Reversed state
        BlockModelBuilder top =  fullGenBlock(block, provider, helper, "top", true);
        BlockModelBuilder side = fullGenBlock(block, provider, helper, "side", true);
        BlockModelBuilder leg = fullGenBlock(block, provider, helper, "leg", true);
        BlockModelBuilder leg_layer = fullGenBlock(block, provider, helper, "leg_layer", true);
        BlockModelBuilder corner = fullGenBlock(block, provider, helper, "corner", true);
        BlockModelBuilder layer = fullGenBlock(block, provider, helper, "layer", true);
        BlockModelBuilder layer_corner = fullGenBlock(block, provider, helper, "layer_corner", true);
        BlockModelBuilder layer_corner_y = fullGenBlock(block, provider, helper, "layer_corner_y", true);
        BlockModelBuilder layer_only = fullGenBlock(block, provider, helper, "layer_only", true);

        if (
                top == null || side == null || leg == null || leg_layer == null || corner == null ||layer == null || layer_corner == null
                        || layer_corner_y == null || layer_only == null
        ) {
            isBlockValid = false;
            return;
        }

        builder.part().modelFile(top).addModel();
        processStepFacing(builder, side);
        processStepFacingBi(builder, corner);
        processStepFacingBi(builder, leg);
        processStepFacingBi(builder, leg_layer);
        processStepFacingTri(builder, layer_only, false, false);
        processStepFacingTri(builder, layer, true, true);
        processStepFacingTri(builder, layer_corner, true, false);
        processStepFacingTri(builder, layer_corner_y , false, true);
    }

    public static void processStepFacing(MultiPartBlockStateBuilder builder, ModelFile file) {
        builder.part().modelFile(file).addModel().condition(BlockStateProperties.NORTH, false);
        builder.part().modelFile(file).rotationY(90).addModel().condition(BlockStateProperties.EAST, false);
        builder.part().modelFile(file).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, false);
        builder.part().modelFile(file).rotationY(270).addModel().condition(BlockStateProperties.WEST, false);
    }

    public static void processStepFacingBi(MultiPartBlockStateBuilder builder, ModelFile file) {
        builder.part().modelFile(file).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.WEST, false);
        builder.part().modelFile(file).rotationY(90).addModel()
                .condition(BlockStateProperties.EAST, false)
                .condition(BlockStateProperties.NORTH, false);
        builder.part().modelFile(file).rotationY(180).addModel()
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.EAST, false);
        builder.part().modelFile(file).rotationY(270).addModel()
                .condition(BlockStateProperties.WEST, false)
                .condition(BlockStateProperties.SOUTH, false);
    }

    public static void processStepFacingTri(MultiPartBlockStateBuilder builder, ModelFile file, boolean control2, boolean control3) {
        builder.part().modelFile(file).addModel()
                .condition(BlockStateProperties.NORTH, false)
                .condition(BlockStateProperties.EAST, control2)
                .condition(BlockStateProperties.WEST, control3);
        builder.part().modelFile(file).rotationY(90).addModel()
                .condition(BlockStateProperties.EAST, false)
                .condition(BlockStateProperties.SOUTH, control2)
                .condition(BlockStateProperties.NORTH, control3);
        builder.part().modelFile(file).rotationY(180).addModel()
                .condition(BlockStateProperties.SOUTH, false)
                .condition(BlockStateProperties.WEST, control2)
                .condition(BlockStateProperties.EAST, control3);
        builder.part().modelFile(file).rotationY(270).addModel()
                .condition(BlockStateProperties.WEST, false)
                .condition(BlockStateProperties.NORTH, control2)
                .condition(BlockStateProperties.SOUTH, control3);
    }

    @Override
    public void buildBlockWithTemplate(TableBlock block, BlockStateProvider builderProvider, ExistingFileHelper helper) {
        buildTemplate1(block, builderProvider.models(), builderProvider.getMultipartBuilder(block), helper);
    }

}
