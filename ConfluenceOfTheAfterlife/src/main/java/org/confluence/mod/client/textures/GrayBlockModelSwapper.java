package org.confluence.mod.client.textures;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.confluence.mod.client.connected.BakedModelWrapperWithData;
import org.confluence.mod.client.connected.BakedQuadHelper;
import org.confluence.mod.client.event.ModClientSetups;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.confluence.mod.client.connected.SpriteShiftEntry.getUnInterpolatedU;
import static org.confluence.mod.client.connected.SpriteShiftEntry.getUnInterpolatedV;

public class GrayBlockModelSwapper extends BakedModelWrapperWithData {
    protected static final ModelProperty<ColorData> COLOR_PROPERTY = new ModelProperty<>();

    public GrayBlockModelSwapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
        return builder.with(COLOR_PROPERTY, new ColorData(0xFF0000));
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        ColorData data = extraData.get(COLOR_PROPERTY);
        if (data == null) return quads;
        quads = new ArrayList<>(quads);
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            TextureAtlasSprite target = ModClientSetups.getGraySpritesUploader().getSprite(quad.getSprite().contents().name());
            if (target != null) {
                BakedQuad bakedQuad = BakedQuadHelper.clone(quad);
                int[] vertexData = bakedQuad.getVertices();

                for (int vertex = 0; vertex < 4; vertex++) {
                    float u = BakedQuadHelper.getU(vertexData, vertex);
                    float v = BakedQuadHelper.getV(vertexData, vertex);
                    BakedQuadHelper.setU(vertexData, vertex, target.getU(getUnInterpolatedU(quad.getSprite(), u)));
                    BakedQuadHelper.setV(vertexData, vertex, target.getV(getUnInterpolatedV(quad.getSprite(), v)));
                }
                quads.set(i, bakedQuad);
            }
        }
        return quads;
    }

    private static BakedQuad getGrayBakedQuad(BakedQuad quad) {
        TextureAtlasSprite sprite = ModClientSetups.getGraySpritesUploader().getSprite(quad.getSprite().contents().name());
        if (sprite == null) return quad;
        return new BakedQuad(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    public record ColorData(int color) {}
}
