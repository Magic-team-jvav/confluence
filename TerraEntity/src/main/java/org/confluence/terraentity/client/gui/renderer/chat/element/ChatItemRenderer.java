package org.confluence.terraentity.client.gui.renderer.chat.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;
import org.confluence.terraentity.registries.chat.variant.ItemChatElement;

public enum ChatItemRenderer implements IChatRenderer<ItemChatElement> {
    INSTANCE;

    final float scale = 10f;

    @Override
    public void render(ItemChatElement chat, ChatArranger chatArranger, float x, float y, PoseStack poseStack, GuiGraphics guiGraphics, int packedLight, int packedOverlay, Level level, MultiBufferSource.BufferSource bufferSource) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 6, y + 6,0f);
        float scale = this.scale * chat.getScale();
        guiGraphics.pose().scale(-scale,-scale,-scale);
//            guiGraphics.renderItem(Items.BOW.getDefaultInstance(), 0,0);
        Minecraft.getInstance().getItemRenderer().renderStatic(chat.getContent(), ItemDisplayContext.FIXED,
                0xF000F0,packedOverlay, guiGraphics.pose(), bufferSource, level, 0
        );
        guiGraphics.pose().popPose();
    }
}
