package org.confluence.terraentity.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.model.CabbageProjModel;
import org.confluence.terraentity.client.entity.model.CrownOfKingSlimeModel;
import org.confluence.terraentity.client.entity.renderer.CrownOfKingSlimeModelRenderer;
import org.confluence.terraentity.client.entity.renderer.ProjRenderer;
import org.confluence.terraentity.entity.proj.BaseProj;
import org.confluence.terraentity.init.TEEntities;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.terraentity.init.TEEntities.CABBAGE_PROJ;
import static org.confluence.terraentity.init.TEEntities.CROWN_OF_KING_SLIME_MODEL;


@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = TerraEntity.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClient {
/*
    public static final BlockColor HALLOW_LEAVES_COLOR = (blockState, getter, pos, tint) -> {
        if (pos == null) return -1;

        IntegerRGB x = hallowMixture(Math.abs(pos.getX()) % 12);
        IntegerRGB y = hallowMixture(Math.abs(pos.getY()) % 12);
        IntegerRGB z = hallowMixture(Math.abs(pos.getZ()) % 12);

        return x.mixture(y, 0.5F).mixture(z, 0.5F).getPrefab();
    };
    public static final ItemColor SIMPLE = (pStack, pTintIndex) -> ColoredItem.getColor(pStack);

    private static IntegerRGB hallowMixture(int m) {
        if (m <= 4) return IntegerRGB.HALLOW_A.mixture(IntegerRGB.HALLOW_B, m * 0.25F);
        if (m <= 8) return IntegerRGB.HALLOW_B.mixture(IntegerRGB.HALLOW_C, (m - 4) * 0.25F);
        return IntegerRGB.HALLOW_C.mixture(IntegerRGB.HALLOW_A, (m - 8) * 0.25F);
    }
*/

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientConfig.load();

        });
    }

    @SubscribeEvent
    public static void configChanged(ModConfigEvent event) {
        if(event.getConfig().getSpec() == ClientConfig.SPEC){
            ClientConfig.load();
        }
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(CrownOfKingSlimeModel.LAYER_LOCATION, CrownOfKingSlimeModel::createBodyLayer);
        registerModel(event, CrownOfKingSlimeModel.class);
        registerModel(event, CabbageProjModel.class);

    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CROWN_OF_KING_SLIME_MODEL.get(), CrownOfKingSlimeModelRenderer::new);

        registerProj(event,CABBAGE_PROJ.get(),c->new CabbageProjModel<>(c.bakeLayer(CabbageProjModel.LAYER_LOCATION)));

        TEEntities.registerRenderers(event);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {

    }






//    @SubscribeEvent
//    public static void registerTextureAtlasSpriteLoaders(RegisterTextureAtlasSpriteLoadersEvent event) {
//        event.register("still_fluid", new ITextureAtlasSpriteLoader() {
//            @Override
//            public SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta) {
//                return new SpriteContents(name, frameSize, image, animationMeta, forgeMeta);
//            }
//
//            @Override
//            public @NotNull TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
//                return new TextureAtlasSprite(atlasName, contents, 16, 512, 0, 0) {
//                };
//            }
//        });
//    }


    public static ModelLayerLocation getModelDefine(Class<? extends Model> clz){
        Field field2;
        try{
            field2  = clz.getDeclaredField("LAYER_LOCATION");
        }catch (Exception e){ throw new RuntimeException();}
        field2.setAccessible(true);

        try{
            return (ModelLayerLocation) field2.get(null);
        }catch (Exception e){ throw new RuntimeException();}

    }

    public static Supplier<LayerDefinition> getLayerDefinition(Class<? extends Model> clz){
        return  ()-> {
            try {
                return (LayerDefinition) clz.getMethod("createBodyLayer").invoke(null);
            } catch (Exception e) {throw new RuntimeException(e);}
        };
    }

    public static void registerModel(EntityRenderersEvent.RegisterLayerDefinitions evt, Class<? extends Model> clz){
        evt.registerLayerDefinition(getModelDefine(clz), getLayerDefinition(clz));
    }

    public static <T extends BaseProj>void registerProj(EntityRenderersEvent.RegisterRenderers event, EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityModel<T>> model){
        event.registerEntityRenderer(entityType, (dispatcher)-> new ProjRenderer<>(dispatcher, model.apply(dispatcher),1,0));
    }

    public static <T extends BaseProj>void registerProj(EntityRenderersEvent.RegisterRenderers event, EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityModel<T>> model, float size, float offsetY){
        event.registerEntityRenderer(entityType, (dispatcher)-> new ProjRenderer<>(dispatcher, model.apply(dispatcher),size,offsetY));
    }
}
