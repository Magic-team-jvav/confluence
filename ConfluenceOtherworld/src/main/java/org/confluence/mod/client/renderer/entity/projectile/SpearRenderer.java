package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;

public class SpearRenderer extends EntityRenderer<ThrowableDropSelfProjectile> {
    private final double zRotate;

    public SpearRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.zRotate = -Math.PI * 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowableDropSelfProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(ThrowableDropSelfProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        poseStack.pushPose();
        Vec3 v = entity.getDeltaMovement();
        float yaw = (float) Math.atan2(v.z, v.x);
        float pitch = (float) (Math.atan2(v.y, Math.sqrt(v.x * v.x + v.z * v.z)) + zRotate);
        poseStack.mulPose(Axis.YN.rotation((yaw + (float) Math.PI)));
        poseStack.mulPose(Axis.ZN.rotation(pitch));
        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.getItem(),
                ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, entity.level(), 0);
        poseStack.popPose();
    }
}