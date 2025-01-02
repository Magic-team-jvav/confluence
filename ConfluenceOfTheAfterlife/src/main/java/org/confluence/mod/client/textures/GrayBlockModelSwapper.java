package org.confluence.mod.client.textures;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.confluence.mod.client.connected.BakedModelWrapperWithData;
import org.confluence.mod.client.connected.BakedQuadHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrayBlockModelSwapper extends BakedModelWrapperWithData {
    protected static final ModelProperty<Unit> COLOR_PROPERTY = new ModelProperty<>();

    public GrayBlockModelSwapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        if (LocalData.hasColor(pos)) {
            return builder.with(COLOR_PROPERTY, Unit.INSTANCE);
        }
        return builder;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        if (!extraData.has(COLOR_PROPERTY)) return quads;
        quads = new ArrayList<>(quads);
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            GraySpriteShifterEntry entry = GraySpriteShifterEntry.ALL.get(quad.getSprite().contents().name());
            if (entry != null) {
                BakedQuad bakedQuad = BakedQuadHelper.clone(quad);
                int[] vertexData = bakedQuad.getVertices();

                for (int vertex = 0; vertex < 4; vertex++) {
                    float u = BakedQuadHelper.getU(vertexData, vertex);
                    float v = BakedQuadHelper.getV(vertexData, vertex);
                    BakedQuadHelper.setU(vertexData, vertex, entry.getTargetU(u));
                    BakedQuadHelper.setV(vertexData, vertex, entry.getTargetV(v));
                }
                quads.set(i, bakedQuad);
            }
        }
        return quads;
    }
}
