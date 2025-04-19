package org.confluence.mod.mixed;

import net.minecraft.client.model.geom.ModelPart;

public interface IModelPart {
    ModelPart confluence$root(ModelPart... root);

    boolean confluence$isSkull(boolean... isSkull);

    static IModelPart of(ModelPart modelPart) {
        return (IModelPart) (Object) modelPart;
    }
}
