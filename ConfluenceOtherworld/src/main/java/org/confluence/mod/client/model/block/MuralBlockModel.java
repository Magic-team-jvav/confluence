package org.confluence.mod.client.model.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.common.block.common.MuralBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MuralBlockModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<BlockState> DISPLAY_STATE = new ModelProperty<>(state -> state != null);

    public MuralBlockModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        ModelData.Builder builder = super.getModelData(level, pos, state, modelData).derive();
        if (level.getBlockEntity(pos) instanceof MuralBlock.BEntity muralEntity) {
            builder.with(DISPLAY_STATE, muralEntity.getDisplayState());
        }
        return builder.build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        BlockState displayState = extraData.get(DISPLAY_STATE);
        if (displayState == null) return super.getQuads(state, side, rand, extraData, renderType);
        return getDisplayModel(displayState).getQuads(displayState, side, rand, ModelData.EMPTY, renderType);
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        BlockState displayState = data.get(DISPLAY_STATE);
        if (displayState == null) return super.useAmbientOcclusion(state, data, renderType);
        return getDisplayModel(displayState).useAmbientOcclusion(displayState, ModelData.EMPTY, renderType);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        BlockState displayState = data.get(DISPLAY_STATE);
        if (displayState == null) return super.getParticleIcon(data);
        return getDisplayModel(displayState).getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        BlockState displayState = data.get(DISPLAY_STATE);
        if (displayState == null) return super.getRenderTypes(state, rand, data);
        return getDisplayModel(displayState).getRenderTypes(displayState, rand, ModelData.EMPTY);
    }

    private static BakedModel getDisplayModel(BlockState displayState) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(displayState);
    }
}
