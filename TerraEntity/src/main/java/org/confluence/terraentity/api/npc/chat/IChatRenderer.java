package org.confluence.terraentity.api.npc.chat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;

/**
 * 渲染单个聊天的元素
 * @param <E> element 类型
 */
public interface IChatRenderer<E extends IChatElement<?>> {

    void render(E chat, ChatArranger chatArranger, float x, float y, PoseStack poseStack, GuiGraphics guiGraphics, int packedLight, int packedOverlay, Level level, MultiBufferSource.BufferSource bufferSource);


}
