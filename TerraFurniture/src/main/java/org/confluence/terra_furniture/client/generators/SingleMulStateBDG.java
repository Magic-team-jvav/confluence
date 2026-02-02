package org.confluence.terra_furniture.client.generators;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;
import org.confluence.terra_furniture.common.block.func.MulStateGetter;

import java.util.HashMap;
import java.util.Map;

public abstract class SingleMulStateBDG<S extends Enum<S> & StringRepresentable, T extends Block & BlockSetGetter<T> & MulStateGetter<S>> extends HorizontalBDG<T> {
    @Override
    public void buildBlockWithTemplate(T block, BlockStateProvider builderProvider, ExistingFileHelper helper) {
        isBlockValid = true; // Reversed state
        Map<S, ModelFile> propModelMap = new HashMap<>();
        for (S prop : block.getEnumPropertyObjects()) {
            propModelMap.put(prop, fullGenBlock(block, builderProvider.models(), helper, prop.getSerializedName(), true));
        }

        propModelMap.values().forEach(modelFile -> {
            if (modelFile == null) isBlockValid = false;
        });
        if (!isBlockValid) return;

        forAllStates(builderProvider.getVariantBuilder(block), state -> ConfiguredModel.builder()
                .modelFile(propModelMap.get(state.getValue(block.getContainer())))
                .rotationY(toY(state))
                .build()
        );
    }

    public static boolean isBase(BlockState state) {
        return state.getValue(StateProperties.FORWARD_TWO_PART).isBase();
    }
}
