package org.confluence.mod.client.effect.connected.custom;

import net.minecraft.client.resources.model.BakedModel;
import org.confluence.mod.client.effect.connected.CTModel;
import org.confluence.mod.client.effect.connected.CTType;

public class PillarCTModel extends CTModel {
    public PillarCTModel(BakedModel originalModel, CTType type, String blockTextureName, String connectedTextureName) {
        super(originalModel, new PillarCTBehaviour(type, blockTextureName, connectedTextureName));
    }

    public PillarCTModel(BakedModel originalModel, CTType type, String blockTextureName) {
        super(originalModel, new PillarCTBehaviour(type, blockTextureName));
    }
}
