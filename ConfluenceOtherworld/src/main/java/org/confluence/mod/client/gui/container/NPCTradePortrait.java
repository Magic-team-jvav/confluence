package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/// 商店独立头像：外观快照、待机动画、鼠标追踪及灯光；不运行世界实体的 AI。
public final class NPCTradePortrait {
    /// 位置相对商店左上角；调整取景和转头范围只改此处。
    private static final int PORTRAIT_HEIGHT = 52;
    private static final int PORTRAIT_CENTER_X = 49;
    private static final int PORTRAIT_TOP = -51;
    private static final int PORTRAIT_WIDTH = 84;
    private static final float PORTRAIT_LOOK_DISTANCE = 40.0F;
    private static final float PORTRAIT_MAX_YAW = 45.0F;
    private static final float PORTRAIT_MAX_PITCH = 30.0F;
    private static final float PORTRAIT_HEIGHT_FRACTION = 0.55F;
    private static final float PORTRAIT_EYE_OFFSET_FRACTION = 0.40F;
    private static final Vector3f PORTRAIT_TOP_LIGHT = new Vector3f(0.2F, -1.0F, 0.7F).normalize();
    private static final Vector3f PORTRAIT_FILL_LIGHT = new Vector3f(-0.2F, -0.2F, 1.0F).normalize();

    private final BaseNPC npc;

    public NPCTradePortrait(BaseNPC npc) {
        this.npc = npc;
        npc.setNoAi(true);
        npc.setInvisible(false);
        npc.setCustomNameVisible(false);
        npc.getAnimatableInstanceCache().getManagerForId(npc.getId()).getAnimationControllers().values().forEach(controller -> controller.transitionLength(0));
    }

    public void tick() {
        npc.tickCount++;
    }

    public void render(GuiGraphics graphics, int leftPos, int topPos, int mouseX, int mouseY) {
        float height = npc.getBbHeight() / npc.getScale();
        int scale = (int) (PORTRAIT_HEIGHT / (height * PORTRAIT_HEIGHT_FRACTION));
        float offset = npc.getEyeHeight() / npc.getScale() - height * PORTRAIT_EYE_OFFSET_FRACTION;
        int centerX = leftPos + PORTRAIT_CENTER_X;
        int portraitTop = topPos + PORTRAIT_TOP;
        float eyeY = portraitTop + PORTRAIT_HEIGHT / 2.0F + scale * (height / 2.0F + offset - npc.getEyeHeight() / npc.getScale());
        float yaw = (float) (Math.atan((centerX - mouseX) / PORTRAIT_LOOK_DISTANCE) * 2 / Math.PI) * PORTRAIT_MAX_YAW;
        float pitch = (float) (Math.atan((mouseY - eyeY) / PORTRAIT_LOOK_DISTANCE) * 2 / Math.PI) * PORTRAIT_MAX_PITCH;
        npc.yBodyRot = npc.yBodyRotO = 180.0F;
        npc.yHeadRot = npc.yHeadRotO = 180.0F + yaw;
        npc.setYRot(180.0F + yaw);
        npc.yRotO = npc.getYRot();
        npc.setXRot(pitch);
        npc.xRotO = pitch;
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        Quaternionf cameraOrientation = new Quaternionf(dispatcher.cameraOrientation());
        graphics.flush();
        graphics.enableScissor(centerX - PORTRAIT_WIDTH / 2, portraitTop, centerX + PORTRAIT_WIDTH / 2, portraitTop + PORTRAIT_HEIGHT);
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(centerX, portraitTop + PORTRAIT_HEIGHT / 2.0F, 50.0F);
            float actualScale = scale / npc.getScale();
            graphics.pose().scale(actualScale, actualScale, -actualScale);
            graphics.pose().translate(0, npc.getBbHeight() / 2.0F + offset * npc.getScale(), 0);
            graphics.pose().mulPose(new Quaternionf().rotateZ((float) Math.PI));
            RenderSystem.setShaderLights(PORTRAIT_TOP_LIGHT, PORTRAIT_FILL_LIGHT);
            dispatcher.overrideCameraOrientation(new Quaternionf().rotateY((float) Math.PI));
            dispatcher.setRenderShadow(false);
            RenderSystem.runAsFancy(() -> dispatcher.render(npc, 0, 0, 0, 0, 1, graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT));
            graphics.flush();
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
            dispatcher.overrideCameraOrientation(cameraOrientation);
            dispatcher.setRenderShadow(true);
            graphics.pose().popPose();
            graphics.disableScissor();
            Lighting.setupFor3DItems();
        }
    }

}

