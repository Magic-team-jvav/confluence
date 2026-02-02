package org.confluence.terraentity.client.gui.renderer.chat.bubble;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.chat.IBubbleRenderer;
import org.confluence.terraentity.api.npc.chat.IChatElement;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;

import java.util.Map;

public enum CloudBubble implements IBubbleRenderer {
    INSTANCE;

    final ResourceLocation texture = TerraEntity.space("textures/gui/chat_bubble.png");

    @Override
    public void renderBubble(GuiGraphics guiGraphics, ChatArranger chat, PoseStack poseStack, Level level, MultiBufferSource.BufferSource bufferSource, float scale, float width, float height , int packedLight, int packedOverlay) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1);
        guiGraphics.setColor(0.3f,0.3f,0.3f,0.2f);
        guiGraphics.blit(texture, 1,1,100,100,0,0,100,100,100, 100);
        guiGraphics.setColor(1,1,1,1);
        guiGraphics.blit(texture, 0,0,100,100,0,0,100,100,100, 100);
        guiGraphics.pose().popPose();

        Map<IChatElement, Vec2> elements = chat.elementPositions;
        for (var element: elements.entrySet()) {
            Vec2 pos = element.getValue();
            if(element.getKey().getRenderer() != null){
                element.getKey().getRenderer().render(element.getKey(), chat , width + pos.x, height - 6  + pos.y, poseStack, guiGraphics, packedLight, packedOverlay, level, bufferSource);
            }

        }
    }
}
