package org.confluence.terraentity.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.client.buffer.DebugBlocksHelper;
import org.confluence.terraentity.client.buffer.DebugEntityHelper;
import org.confluence.terraentity.client.buffer.NPCChatBubbleBuffer;
import org.confluence.terraentity.client.gui.CustomizeBossHealthBar;
import org.confluence.terraentity.client.post.BossSpawnCameraManager;
import org.confluence.terraentity.client.post.BrainTranslucent;
import org.confluence.terraentity.client.post.TongueRenderer;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.item.BaseWhipItem;
import org.confluence.terraentity.item.YoyosItem;

import static org.confluence.terraentity.TerraEntity.MODID;
import static org.confluence.terraentity.config.ClientConfig.bossBarStyle;

@EventBusSubscriber(modid = MODID,value = Dist.CLIENT)
public class RenderEvent {
    @SubscribeEvent
    public static void guiEvent( RenderGuiLayerEvent.Pre event){


    }

    @SubscribeEvent
    public static void drawBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
//        String name = ((TranslatableContents)event.getBossEvent().getName().getContents()).getKey().split("\\.",2)[1];
        if(bossBarStyle != 0){
            try{
                CustomizeBossHealthBar bar = CustomizeBossHealthBar.getBossHealthBars((event.getBossEvent().getName().getString()));
                if(bar!= null)
                    bar.render(event);
            }catch (Exception e){
                TerraEntity.LOGGER.warn(e.getLocalizedMessage());
            }

        }
    }

    public static boolean isIrisShader = false;
    public static boolean isAfterSky = false;


    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            BrainTranslucent.render(event);
            DebugBlocksHelper.Singleton().render(event);
            NPCChatBubbleBuffer.getInstance().render(event);
            isAfterSky = false;
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            TongueRenderer.renderFirstPerson(event);

        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            isAfterSky = true;
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            isIrisShader = ModChecker.iris.isLoaded() && RenderSystem.getShader() instanceof ExtendedShader;
            DebugEntityHelper.INSTANCE.render(event);
        }
    }

    @SubscribeEvent
    public static void RenderLiving(RenderLivingEvent.Pre<Player, EntityModel<Player>> event) {
        TongueRenderer.render(event);
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        if(BossSpawnCameraManager.INSTANCE.isAnimating()){
            event.setCanceled(true);
        }

        ItemStack stack = event.getItemStack();
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null){
            return;
        }
        if (event.getHand() == InteractionHand.MAIN_HAND && stack.getItem() instanceof BaseWhipItem item) {
            // 右手使用鞭子时取消渲染
            if (player.getCooldowns().isOnCooldown(item)) {
//                ci.cancel();
                float progress = (player.tickCount - BaseWhipItem.clickTime + event.getPartialTick());
                int cooldown = BaseWhipItem.cooldownTime;
                progress = Math.min(progress, cooldown) / cooldown;

                progress = progress > 0.5? 2 - progress * 2 : progress * 2;
                event.getPoseStack().translate(0, -progress  ,0);
            }
        }
//        Minecraft.getInstance().getBlockRenderer().renderBatched();
        if (event.getHand() == InteractionHand.MAIN_HAND) {

            Item item = player.getMainHandItem().getItem();
            // 使用有悠悠球时渲染手臂
            if (item instanceof YoyosItem && WeaponStorage.of(player).yoyosEntity != null) {

                PlayerRenderer playerrenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
                PoseStack poseStack = event.getPoseStack();
                poseStack.pushPose();
                poseStack.translate(0.3, 0.1, -0.5);


//            poseStack.translate(0.5,-0.2,-1.2);
                var buffer = event.getMultiBufferSource();
                int packedLight = event.getPackedLight();
                float f = 1.0F;
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(f * -60.0F));
                poseStack.translate(f * 0.3F, -1.1F, 0.45F);
                poseStack.translate(0.8, 1.0, 0.3);

                playerrenderer.renderRightHand(poseStack, buffer, packedLight, player);

                poseStack.popPose();
            }
        }
    }


    @SubscribeEvent
    public static void calculateCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        BossSpawnCameraManager.INSTANCE.update(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
    }
}
