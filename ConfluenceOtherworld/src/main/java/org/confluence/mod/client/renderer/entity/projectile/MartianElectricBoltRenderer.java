package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.SpearProjectileModels;
import org.confluence.mod.common.entity.projectile.MartianElectricBolt;

/** Tesla shots reuse the Storm Spear mesh and texture with full-bright emissive rendering. */
public final class MartianElectricBoltRenderer extends EntityRenderer<MartianElectricBolt> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/storm_spear_shot_projectile.png");
    private static final RenderType GLOW = RenderType.entityTranslucentEmissive(TEXTURE);
    private final ModelPart model;

    public MartianElectricBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = context.bakeLayer(SpearProjectileModels.STORM);
    }

    @Override
    public ResourceLocation getTextureLocation(MartianElectricBolt entity) {
        return TEXTURE;
    }

    @Override
    public void render(MartianElectricBolt entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        model.render(poseStack, buffer.getBuffer(GLOW), 0xF000F0, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
