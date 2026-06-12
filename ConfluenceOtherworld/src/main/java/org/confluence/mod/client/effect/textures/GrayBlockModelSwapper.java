package org.confluence.mod.client.effect.textures;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.confluence.mod.client.effect.connected.BakedModelWrapperWithData;
import org.confluence.mod.client.effect.connected.BakedQuadHelper;
import org.confluence.mod.common.data.saved.BrushData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrayBlockModelSwapper extends BakedModelWrapperWithData {
    protected static final ModelProperty<int[]> COLOR_PROPERTY = new ModelProperty<>();

    public GrayBlockModelSwapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        int @Nullable [] colors = LocalBrushData.getColors(pos);
        if (colors != null) {
            return builder.with(COLOR_PROPERTY, colors);
        }
        return builder;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        int[] colors = extraData.get(COLOR_PROPERTY);
        if (colors == null) return quads;
        int color = colors[(side == null ? Direction.WEST : side).get3DDataValue()];
        if (color == BrushData.EMPTY_COLOR || color == BrushData.ILLUMINANT_COLOR || color == BrushData.ECHO_COLOR)
            return quads;
        quads = new ArrayList<>(quads);

        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            GraySpriteShifterEntry entry = GraySpriteShifterEntry.ALL.get(quad.getSprite().contents().name());
            if (entry != null) {
                TextureAtlasSprite sprite = color == BrushData.NEGATIVE_COLOR ? entry.negative() : entry.gray();
                BakedQuad bakedQuad = BakedQuadHelper.clone(quad);
                int[] vertexData = bakedQuad.getVertices();

                for (int vertex = 0; vertex < 4; vertex++) {
                    float u = BakedQuadHelper.getU(vertexData, vertex);
                    float v = BakedQuadHelper.getV(vertexData, vertex);
                    BakedQuadHelper.setU(vertexData, vertex, entry.getTargetU(sprite, u));
                    BakedQuadHelper.setV(vertexData, vertex, entry.getTargetV(sprite, v));
                }
                quads.set(i, bakedQuad);
            }
        }
        return quads;
    }
}
