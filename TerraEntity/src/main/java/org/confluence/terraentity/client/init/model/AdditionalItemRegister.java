package org.confluence.terraentity.client.init.model;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.confluence.terraentity.TerraEntity;
import org.jetbrains.annotations.Nullable;


public class AdditionalItemRegister extends AbstractModelRegister<Item> {

    private static AdditionalItemRegister instance;
    public static AdditionalItemRegister getInstance() {
        if(instance == null) {
            instance = new AdditionalItemRegister();
        }
        return instance;
    }

    public static ModelResourceLocation FINCH_STAFF_MODEL = ModelResourceLocation.standalone(TerraEntity.space("item/finch_staff_empty"));

    public void register(ModelEvent.RegisterAdditional event){
        event.register(FINCH_STAFF_MODEL);


    }

    public @Nullable ModelResourceLocation process(ResourceLocation location){
        return null;
    }

    @Override
    protected String getFolder() {
        return "item";
    }

}
