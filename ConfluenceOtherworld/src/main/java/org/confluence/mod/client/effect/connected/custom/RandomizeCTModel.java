package org.confluence.mod.client.effect.connected.custom;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.confluence.mod.client.effect.connected.BakedQuadHelper;
import org.confluence.mod.client.effect.connected.CTModel;
import org.confluence.mod.client.effect.connected.CTSpriteShiftEntry;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RandomizeCTModel extends CTModel {
    private final int width;

    public RandomizeCTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour, int width) {
        super(originalModel, behaviour);
        this.width = width;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        List<BakedQuad> quads = originalModel.getQuads(state, side, rand, extraData, renderType);
        CTData data = extraData.get(CT_PROPERTY);
        if (data == null) return quads;
        int selected = rand.nextInt(width);
        quads = new ArrayList<>(quads);

        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);

            int index = data.get(quad.getDirection());
            if (index == -1)
                continue;

            CTSpriteShiftEntry spriteShift = behaviour.getShift(state, quad.getDirection(), quad.getSprite());
            if (spriteShift == null)
                continue;
            if (quad.getSprite() != spriteShift.getOriginal())
                continue;

            BakedQuad newQuad = BakedQuadHelper.clone(quad);
            int[] vertexData = newQuad.getVertices();

            for (int vertex = 0; vertex < 4; vertex++) {
                float u = BakedQuadHelper.getU(vertexData, vertex);
                float v = BakedQuadHelper.getV(vertexData, vertex);
                BakedQuadHelper.setU(vertexData, vertex, spriteShift.getSelectedTargetU(u, index, selected, width));
                BakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v, index));
            }

            quads.set(i, newQuad);
        }

        return quads;
    }
}
