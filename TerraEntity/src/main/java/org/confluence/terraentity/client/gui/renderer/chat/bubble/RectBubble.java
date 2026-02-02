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

public enum RectBubble implements IBubbleRenderer {
    INSTANCE;

    final ResourceLocation texture = TerraEntity.space("textures/gui/chat_rect.png");

    @Override
    public void renderBubble(GuiGraphics guiGraphics, ChatArranger chat, PoseStack poseStack, Level level, MultiBufferSource.BufferSource bufferSource, float scale, float width, float height , int packedLight, int packedOverlay) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1);


        int left = 8;
        int top = 20;
        int right = 80;
        int bottom = 80;
        int w = right - left - 7;
        int h = bottom - top - 7;

        guiGraphics.blit(texture, left ,top,0,0,0,7,7,32, 32);
        guiGraphics.blit(texture, left ,bottom,0,0,16,7,7,32, 32);
        guiGraphics.blit(texture, right ,top,0,16,0,7,7,32, 32);
        guiGraphics.blit(texture, right ,bottom,0,16,16,9,9,32, 32);

        guiGraphics.blit(texture, left+7 ,top,w, 7,8,0,7,7,32, 32);
        guiGraphics.blit(texture, left ,top+7,7, h,0,8,7,7,32, 32);
        guiGraphics.blit(texture, left+7 ,bottom,w, 7,8,16,7,7,32, 32);
        guiGraphics.blit(texture, right,top+7,7, h,16,8,7,7,32, 32);

        guiGraphics.blit(texture, left+7,top+7,w, h,8,8,7,7,32, 32);


//        guiGraphics.blit(texture, 1,1,100,100,0,0,100,100,100, 100);

        guiGraphics.pose().popPose();

        Map<IChatElement, Vec2> elements = chat.elementPositions;
        for (var element: elements.entrySet()) {
            Vec2 pos = element.getValue();
            if(element.getKey().getRenderer() != null){
                element.getKey().getRenderer().render(element.getKey(), chat, width + pos.x, height - 6  + pos.y, poseStack, guiGraphics, packedLight, packedOverlay, level, bufferSource);
            }

        }

    }

    @Override
    public void adjustPose(PoseStack poseStack , float scale, float width, float height){
        poseStack.translate(0.5,0,0);
    }
}
