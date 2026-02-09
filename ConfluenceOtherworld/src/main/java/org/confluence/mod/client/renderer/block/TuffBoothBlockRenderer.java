package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.block.functional.TuffBoothBlock;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import static org.confluence.lib.util.RenderUtils.drawCube;

public class TuffBoothBlockRenderer implements BlockEntityRenderer<TuffBoothBlock.TuffBoothBlockEntity> {
    private final Font font;
    private final ItemRenderer itemRenderer;

    public TuffBoothBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(TuffBoothBlock.TuffBoothBlockEntity boothEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = boothEntity.getLevel();
        if (level == null) return;

        Player player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) return;

        BlockPos pos = boothEntity.getBlockPos();
        boolean isGuiHidden = net.minecraft.client.Minecraft.getInstance().options.hideGui;

        Vec3 exactHitLocation = null;
        HitResult hitResult = net.minecraft.client.Minecraft.getInstance().hitResult;
        if (hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHit && blockHit.getBlockPos().equals(pos)) {
            exactHitLocation = blockHit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        }

        if (!isGuiHidden && exactHitLocation != null && !player.isSpectator()) {
            VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());
            renderSelectionUI(poseStack, lineBuffer, exactHitLocation, player.getAbilities().mayBuild);
        }

        ItemStack itemStack = boothEntity.getItemHandler().getStackInSlot(0);
        if (itemStack.isEmpty() && exactHitLocation == null) return;

        double time = level.getGameTime() + partialTick;
        float yOffset = (float) (Math.sin(time * 0.1) * 0.08);
        float rotationAngle = (float) (time * 0.05);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.25 + yOffset, 0.5);
        poseStack.mulPose(Axis.YP.rotation(rotationAngle));

        if (!player.getMainHandItem().isEmpty() && exactHitLocation != null && exactHitLocation.y > 0.5) {
            float cubeAlpha = (float) (Math.sin(time * 0.15) + 1.0) / 2.0F;
            drawCube(poseStack, bufferSource, 0.52, 255, 255, 255, (int)(cubeAlpha * 40),
                    new Vector3d(pos.getX(), pos.getY(), pos.getZ()), new Vector3d(0, 0.125, 0), true, Math.PI * 0.25, 0);
        }

        if (!itemStack.isEmpty()) {
            this.itemRenderer.renderStatic(
                    itemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay,
                    poseStack, bufferSource, level, 0
            );
        }
        poseStack.popPose();

        if (!itemStack.isEmpty() && boothEntity.getBlockState().getValue(TuffBoothBlock.SHOW_NAME)) {
            renderFloatingText(poseStack, bufferSource, itemStack, yOffset);
        }
    }

    private void renderSelectionUI(PoseStack poseStack, VertexConsumer buffer, Vec3 hit, boolean canBuild) {
        float r = 1, g = 1, b = 1, a = 0.7F;
        if (hit.y > 0.5) {
            LevelRenderer.renderLineBox(poseStack, buffer, 0.186, 0.5, 0.186, 0.814, 0.814, 0.814, r, g, b, a);
            LevelRenderer.renderLineBox(poseStack, buffer, -0.001, 0.811, -0.001, 1.001, 1.001, 1.001, r, g, b, a);
        } else {
            LevelRenderer.renderLineBox(poseStack, buffer, 0.186, 0.188, 0.186, 0.814, 0.5, 0.814, r, g, b, a);
            LevelRenderer.renderLineBox(poseStack, buffer, 0.062, -0.001, 0.062, 0.938, 0.188, 0.938, r, g, b, a);
        }

        if (canBuild) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotation(Mth.PI * 0.25F));
            LevelRenderer.renderLineBox(poseStack, buffer, -0.56, 0.56, -0.12, 0.56, 0.81, 0.12, 0, 0, 0, 0.4F);
            poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
            LevelRenderer.renderLineBox(poseStack, buffer, -0.56, 0.56, -0.12, 0.56, 0.81, 0.12, 0, 0, 0, 0.4F);
            poseStack.popPose();
        }
    }

    private void renderFloatingText(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack, float yOffset) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.83 + yOffset, 0.5);

        poseStack.mulPose(net.minecraft.client.Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.02F, -0.02F, 0.02F);

        Matrix4f matrix = poseStack.last().pose();
        Component text = stack.getHoverName();
        int width = this.font.width(text);
        float x = -width / 2.0F;

        VertexConsumer bg = bufferSource.getBuffer(RenderType.textBackground());
        bg.addVertex(matrix, x - 1, -1, 0).setColor(0, 0, 0, 64);
        bg.addVertex(matrix, x - 1, 8, 0).setColor(0, 0, 0, 64);
        bg.addVertex(matrix, x + width + 1, 8, 0).setColor(0, 0, 0, 64);
        bg.addVertex(matrix, x + width + 1, -1, 0).setColor(0, 0, 0, 64);

        ModRarity rarity = ModRarity.getRarity(stack);
        int color = (rarity != null) ? rarity.color() : 0xFFFFFF;

        this.font.drawInBatch(text, x, 0, color, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        poseStack.popPose();
    }
}
