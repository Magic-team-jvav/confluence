package org.confluence.terraentity.client.buffer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.client.post.BossSpawnCameraManager;
import org.confluence.terraentity.item.BossSummonsItem;


/**
 * 用于显示Debug实体
 */
public enum DebugEntityHelper{
    INSTANCE;

    int color = 0xFFFFFF;
    Entity e;

    DebugEntityHelper() {
    }

    private boolean shouldRender() {
        if(BossSpawnCameraManager.INSTANCE.isAnimating()){
            return false;
        }
        ItemStack it = Minecraft.getInstance().player.getMainHandItem();
        if(it.getItem() instanceof BossSummonsItem<?> summonsItem) {
            if(summonsItem.hasSpecificSummonPos()){
                return false;
            }
            color = 0xFFFFFF;
            if(e == null || summonsItem.getEntityType() != e.getType()){
                e = summonsItem.getEntityType().create(Minecraft.getInstance().level);
            }
            Vec3 eyePos = summonsItem.getSummonPos(Minecraft.getInstance().player, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
            e.setPos(eyePos);
            if(!summonsItem.canSummon(eyePos, Minecraft.getInstance().player)){
                return false;
            }
            return true;
        }
        return false;
    }

    public void render(RenderLevelStageEvent event){
        if(!shouldRender()){
            return;
        }
        PoseStack poseStack = event.getPoseStack();
        Vec3 playerPos = event.getCamera().getPosition();
        var render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(e);
        if(render instanceof GeoNormalRenderer geoRenderer){
            poseStack.pushPose();
            poseStack.translate(e.getX() - playerPos.x(), e.getY() - playerPos.y() , e.getZ() - playerPos.z());
            geoRenderer.setConsumeColor(new software.bernie.geckolib.util.Color(0x80FFFFFF));
            geoRenderer.render(e, 0, 0, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), 15<<20| 15 << 4);
            poseStack.popPose();
        }else{
            poseStack.pushPose();
            poseStack.translate(e.getX() - playerPos.x(), e.getY() - playerPos.y() , e.getZ() - playerPos.z());

            render.render(e, 0, 0, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), 15<<20| 15 << 4);
            poseStack.popPose();
        }

    }
}
