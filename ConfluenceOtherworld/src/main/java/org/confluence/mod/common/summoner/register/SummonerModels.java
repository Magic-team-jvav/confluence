package org.confluence.mod.common.summoner.register;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.confluence.mod.Confluence;

public class SummonerModels {

    public static final ModelResourceLocation FINCH_STAFF_EMPTY_MODEL = new ModelResourceLocation(Confluence.asResource("finch_staff_empty"), "inventory");

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(FINCH_STAFF_EMPTY_MODEL);
    }
}
