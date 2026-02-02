package org.confluence.terraentity.client.util;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.confluence.terraentity.client.entity.renderer.proj.ProjRenderer;
import org.confluence.terraentity.entity.proj.BaseProj;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegisterUtils {
    private static ModelLayerLocation getModelDefine(Class<? extends Model> clz){
        Field field2;
        try{
            field2  = clz.getDeclaredField("LAYER_LOCATION");
        }catch (Exception e){ throw new RuntimeException();}
        field2.setAccessible(true);

        try{
            return (ModelLayerLocation) field2.get(null);
        }catch (Exception e){ throw new RuntimeException();}

    }

    private static Supplier<LayerDefinition> getLayerDefinition(Class<? extends Model> clz){
        return  ()-> {
            try {
                return (LayerDefinition) clz.getMethod("createBodyLayer").invoke(null);
            } catch (Exception e) {throw new RuntimeException(e);}
        };
    }

    public static void registerModel(EntityRenderersEvent.RegisterLayerDefinitions evt, Class<? extends Model> clz){
        evt.registerLayerDefinition(getModelDefine(clz), getLayerDefinition(clz));
    }

    public static <T extends BaseProj>void registerBaseProjRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityModel<T>> model){
        event.registerEntityRenderer(entityType, (dispatcher)-> new ProjRenderer<>(dispatcher, model.apply(dispatcher),1,0));
    }

    public static <T extends BaseProj>void registerBaseProjRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityModel<T>> model, float size, float offsetY){
        event.registerEntityRenderer(entityType, (dispatcher)-> new ProjRenderer<>(dispatcher, model.apply(dispatcher),size,offsetY));
    }
}
