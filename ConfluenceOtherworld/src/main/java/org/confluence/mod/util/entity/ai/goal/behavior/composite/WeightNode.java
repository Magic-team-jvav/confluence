package org.confluence.mod.util.entity.ai.goal.behavior.composite;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

import java.util.ArrayList;

public class WeightNode extends CompositeNode {
    private final IntList weights;
    private BTNode currentChild;

    public WeightNode() {
        this.children = new ArrayList<>();
        this.weights = new IntArrayList();
    }

    public WeightNode addChild(int weight, BTNode child) {
        this.children.add(child);
        this.weights.add(weight);
        return this;
    }

    @Override
    public void start() {
        super.start();
        this.currentChild = ModUtils.getRandomByWeightInt(children, weights);
        this.currentChild.start();
    }

    @Override
    public BTStatus execute() {
        return this.currentChild.execute();
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        if (this.currentChild != null) {
            this.currentChild.stop();
        }
        this.currentChild = null;
    }
}
