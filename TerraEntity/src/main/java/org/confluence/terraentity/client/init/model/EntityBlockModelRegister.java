package org.confluence.terraentity.client.init.model;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TEEntities;
import org.jetbrains.annotations.Nullable;

public class EntityBlockModelRegister extends AbstractModelRegister<EntityType<?>> {
    private static EntityBlockModelRegister instance;
    public static EntityBlockModelRegister getInstance() {
        if(instance == null) {
            instance = new EntityBlockModelRegister();
        }
        return instance;
    }
    public static ModelResourceLocation SNATCHER_LEAF = ModelResourceLocation.standalone(TerraEntity.space("item/entity/snatcher_leaf"));

    @Override
    protected @Nullable ModelResourceLocation process(ResourceLocation location) {

        String name = location.getNamespace() + ":" +location.getPath().substring(19).replace("_leaf.json", "");
        for(var entity: TEEntities.ENTITIES.getEntries()){
            String entityName = entity.getId().toString();
            if(entityName.equals(name)){
                ModelResourceLocation modelResourceLocation = ModelResourceLocation.standalone(TerraEntity.space(location.getPath().substring(7).replace(".json", "")));
                this.put(entity.get(), modelResourceLocation);
                return modelResourceLocation;
            }
        }
        return null;
    }

    @Override
    protected String getFolder() {
        return "item/entity";
    }


}
