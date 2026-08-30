package org.confluence.mod.client.effect.connected.custom;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import org.confluence.mod.client.effect.connected.CTModel;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;

public class WeightedCTModel extends CTModel {
    private final int[] weights;
    private final int totalWeight;
    private final int width;

    public WeightedCTModel(BakedModel originalModel, ConnectedTextureBehaviour behaviour, int[] weights) {
        super(originalModel, behaviour);
        this.weights = weights;
        long total = 0L;
        for (int weight : weights) {
            total += weight;
        }
        if (total > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            this.totalWeight = (int) total;
        }
        this.width = weights.length;
    }

    @Override
    protected int getTargetIndex(RandomSource random) {
        int weightedIndex = random.nextInt(totalWeight);
        for (int i = 0; i < width; i++) {
            weightedIndex -= weights[i];
            if (weightedIndex < 0) {
                return i;
            }
        }
        return -1;
    }
}
