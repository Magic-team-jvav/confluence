package org.confluence.mod.client.effect.connected.custom;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.confluence.mod.client.effect.connected.CTModel;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;

public class RandomizeCTModel extends CTModel {
    private final int width;

    public RandomizeCTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour, int width) {
        super(originalModel, behaviour);
        this.width = width;
    }

    @Override
    protected int getTargetIndex(Direction side, RandomSource random) {
        return random.nextInt(width);
    }
}
