package org.confluence.mod.client.renderer.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AltImageTooltip implements ClientTooltipComponent {
    private static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
    private static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
    private final ItemStack stack;

    public AltImageTooltip(ItemStack stack) {
        this.stack = stack;
    }

    public AltImageTooltip(AltImageComponent component) {
        this(component.stack());
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
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack pose = guiGraphics.pose();
        BakedModel bakedModel = minecraft.getItemRenderer().getModel(stack, minecraft.level, minecraft.player, 20251001);
        pose.pushPose();
        pose.translate(x + 16, y + 40, 150);

        try {
            pose.scale(-32.0F, -32.0F, 32.0F);
            boolean flag = !bakedModel.usesBlockLight();
            if (flag) {
                Matrix4f matrix4f = new Matrix4f().rotationY(-Mth.PI / 8.0F).rotateX(-Mth.PI * 3.0F / 4.0F);
                GlStateManager.setupLevelDiffuseLighting(DIFFUSE_LIGHT_1, DIFFUSE_LIGHT_0, matrix4f);
            }

            minecraft.getItemRenderer().render(stack, ItemDisplayContext.HEAD, false, pose, guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
            guiGraphics.flush();
            if (flag) {
                Lighting.setupFor3DItems();
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
            crashreportcategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
            crashreportcategory.setDetail("Item Components", () -> String.valueOf(stack.getComponents()));
            crashreportcategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
            throw new ReportedException(crashreport);
        }

        pose.popPose();
    }
}
