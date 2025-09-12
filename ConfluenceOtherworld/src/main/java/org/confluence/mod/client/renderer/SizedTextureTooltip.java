package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.item.SizedTextureComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SizedTextureTooltip implements ClientTooltipComponent {
    private final int width;
    private final int height;
    private final ResourceLocation sprite;
    private final float scale;

    public SizedTextureTooltip(int width, int height, ResourceLocation sprite) {
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.scale = width == 32 && height == 32 ? 1.0F : 32.0F / Math.max(width, height);
    }

    public SizedTextureTooltip(SizedTextureComponent component) {
        this(component.width(), component.height(), component.location());
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public int getWidth(Font font) {
        return 32;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (scale == 1.0F) {
            guiGraphics.blitSprite(sprite, x, y, 0, width, height);
        } else  {
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scale, scale, scale);
            guiGraphics.blitSprite(sprite, 0, 0, 0, width, height);
            pose.popPose();
        }
    }
}
