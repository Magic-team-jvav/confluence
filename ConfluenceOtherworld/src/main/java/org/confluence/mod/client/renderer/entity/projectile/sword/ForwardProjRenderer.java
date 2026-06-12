package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.entity.renderer.BaseEntityRenderer;

public class ForwardProjRenderer<T extends Entity, S extends Entity, M extends EntityModel<S>> extends BaseEntityRenderer<T, S, M> {
    private final ResourceLocation texture;

    protected boolean rotateZ;
    protected float rotateZSpeed;

    public ForwardProjRenderer(EntityRendererProvider.Context context, M model, ResourceLocation texture, float size, float offsetY, float rotateZSpeed) {
        super(context, model, size, offsetY);
        this.texture = texture;
        this.rotateZ = rotateZSpeed > 0;
        this.rotateZSpeed = rotateZSpeed;
    }

    public ForwardProjRenderer(EntityRendererProvider.Context context, M model, ResourceLocation texture, float size, float offsetY) {
        this(context, model, texture, size, offsetY, 0);
    }

    public ForwardProjRenderer(EntityRendererProvider.Context context, M model, ResourceLocation texture) {
        this(context, model, texture, 1, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(T swordProjectile) {
        return texture;
    }

    @Override
    public void preRender(T entity, float pEntityYaw, float partialTick, PoseStack poseStack, int packedLight) {
        super.preRender(entity, pEntityYaw, partialTick, poseStack, packedLight);
        float f = Math.min((entity.tickCount + partialTick) * 0.1F, 1);
        poseStack.scale(f, f, f);

        Vec3 v = entity.getDeltaMovement();

        float yaw = (float) Math.atan2(v.z, v.x);
        poseStack.mulPose(Axis.YN.rotation(yaw + Mth.HALF_PI));
        float pitch = -(float) Math.atan2(v.y, v.horizontalDistance());
        poseStack.mulPose(Axis.XN.rotation(pitch));
        if (rotateZ) {
            poseStack.mulPose(Axis.ZN.rotation((entity.tickCount + partialTick) * rotateZSpeed));
        }


    }
}
