package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.entity.monster.Demon;

public class DemonModel extends GeoNormalModel<Demon> {

    public DemonModel(ResourceLocation path) {
        super(path,true);
    }

    protected String getHeadName(){
        return "head";
    }
}
