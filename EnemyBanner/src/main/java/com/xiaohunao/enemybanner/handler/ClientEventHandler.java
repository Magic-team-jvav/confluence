package com.xiaohunao.enemybanner.handler;

import com.xiaohunao.enemybanner.EnemyBanner;
import com.xiaohunao.enemybanner.blocks.BlockRegister;
import com.xiaohunao.enemybanner.renderer.EnemyBannerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = EnemyBanner.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(EnemyBannerRenderer.LAYER_LOCATION, EnemyBannerRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(BlockRegister.ENEMY_BANNER_ENTITY.get(), EnemyBannerRenderer::new);
    }

//    @SubscribeEvent
//    public static void registerEffectMenu(RegisterClientExtensionsEvent event){
//        event.registerMobEffect(new IClientMobEffectExtensions() {
//            @Override
//            public boolean isVisibleInInventory(@NotNull MobEffectInstance instance) {
//                return true;
//            }
//
//            @Override
//            public boolean isVisibleInGui(@NotNull MobEffectInstance instance) {
//                return true;
//            }
//
//            @Override
//            public boolean renderInventoryIcon(@NotNull MobEffectInstance instance, @NotNull EffectRenderingInventoryScreen<?> screen, @NotNull GuiGraphics guiGraphics, int x, int y, int blitOffset) {
//                int offs = 4;
//                guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(EnemyBanner.MODID, "textures/gui/icon/enemybanner_effect.png"), x + blitOffset, y + blitOffset, 0, 0, 18, 18, 18, 18);
//                return true;
//            }
//
//            @Override
//            public boolean renderInventoryText(@NotNull MobEffectInstance instance, @NotNull EffectRenderingInventoryScreen<?> screen, @NotNull GuiGraphics guiGraphics, int x, int y, int blitOffset) {
//                return IClientMobEffectExtensions.super.renderInventoryText(instance, screen, guiGraphics, x, y, blitOffset);
//            }
//
//            @Override
//            public boolean renderGuiIcon(@NotNull MobEffectInstance instance, @NotNull Gui gui, @NotNull GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
//                guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(EnemyBanner.MODID, "textures/gui/icon/enemybanner_effect.png"), x, y, 0, 0, 18, 18, 18, 18);
//                return true;
//            }
//        }, EnemyBanner.ENEMY_BANNER_EFFECT);
//    }
}
