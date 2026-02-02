package org.confluence.terraentity.client.gui.renderer.chat.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.npc.chat.IChatRenderer;
import org.confluence.terraentity.entity.npc.chat.ChatArranger;
import org.confluence.terraentity.registries.chat.variant.SpriteChatElement;

public enum SpriteRenderer implements IChatRenderer<SpriteChatElement> {
    INSTANCE;

    @Override
    public void render(SpriteChatElement chat, ChatArranger chatArranger, float x, float y, PoseStack poseStack, GuiGraphics guiGraphics, int packedLight, int packedOverlay, Level level, MultiBufferSource.BufferSource bufferSource) {

        int index = (int) (level.getGameTime() / 20 % chat.getContent().size());

        float scale = chat.getScale();
        int offset = (int)(6 * scale - 6);
        int size = 12 + 2 * offset;
        guiGraphics.blit(chat.getContent().get(index), (int)(x - offset),(int)(y - offset), 0,0,0,size,size,size,size);


    }
}
