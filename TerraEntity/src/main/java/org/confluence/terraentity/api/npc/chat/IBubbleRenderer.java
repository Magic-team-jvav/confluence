package org.confluence.terraentity.api.npc.chat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;

public interface IBubbleRenderer {

    /**
     *
     * @param guiGraphics 使用gui绘制画布
     * @param chat 对话内容
     * @param scale {@link ChatArranger} 计算的缩放
     * @param width {@link ChatArranger} 计算的宽度
     * @param height {@link ChatArranger} 计算的高度
     */
    void renderBubble(GuiGraphics guiGraphics, ChatArranger chat, PoseStack poseStack, Level level, MultiBufferSource.BufferSource bufferSource, float scale, float width, float height , int packedLight, int packedOverlay);


    /**
     * 渲染广告牌后根据相对坐标调整位置
     */
    default void adjustPose(PoseStack poseStack , float scale, float width, float height){

    }

}
