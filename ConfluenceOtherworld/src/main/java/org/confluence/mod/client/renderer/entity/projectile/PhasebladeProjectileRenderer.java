package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.confluence.mod.client.renderer.item.PhasebladeRenderer;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;

public final class PhasebladeProjectileRenderer extends EntityRenderer<PhasebladeProjectile> {
    private final PhasebladeRenderer bladeRenderer = new PhasebladeRenderer(true);

    public PhasebladeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PhasebladeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        if (entity.tickCount > 1) {
            poseStack.pushPose();
            poseStack.translate(0, entity.getBbHeight() * 0.5F, 0);
            poseStack.mulPose(Axis.YN.rotation(entity.visualYaw()));
            poseStack.mulPose(Axis.ZP.rotation(entity.visualRoll(partialTick)));
            BasePhasebladeItem.ProjectileGeometry geometry = entity.projectileGeometry();
            poseStack.translate(-geometry.centerX() - 0.5F, -geometry.centerY() - 0.51F, -geometry.centerZ() - 0.5F);
            bladeRenderer.renderByItem(entity.getRenderItem(), ItemDisplayContext.NONE, poseStack, buffers, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PhasebladeProjectile entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
