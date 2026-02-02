package org.confluence.terraentity.client.gui.renderer.chat.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;
import org.confluence.terraentity.registries.chat.variant.StringChatElement;

public enum ChatComponentRenderer implements IChatRenderer<StringChatElement> {
    INSTANCE;

    @Override
    public void render(StringChatElement chat, ChatArranger chatArranger, float x, float y, PoseStack poseStack, GuiGraphics guiGraphics, int packedLight, int packedOverlay, Level level, MultiBufferSource.BufferSource bufferSource) {
        guiGraphics.drawString(Minecraft.getInstance().font, chat.getContent(), (int) x, (int) y,0xffffffff);

    }
}
