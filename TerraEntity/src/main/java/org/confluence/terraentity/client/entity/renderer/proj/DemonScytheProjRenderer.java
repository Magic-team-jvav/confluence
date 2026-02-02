package org.confluence.terraentity.client.entity.renderer.proj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.proj.DemonScytheProj;

public class DemonScytheProjRenderer extends ProjRenderer<DemonScytheProj> {
    public DemonScytheProjRenderer(EntityRendererProvider.Context pContext, EntityModel<DemonScytheProj> pModel, float size, float offsetY) {
        super(pContext, pModel, size, offsetY);
    }

    public DemonScytheProjRenderer(EntityRendererProvider.Context pContext, EntityModel<DemonScytheProj> pModel) {
        super(pContext, pModel);
    }

    @Override
    protected void adjustPosePost(PoseStack poseStack, DemonScytheProj entity, float partialTick){
        Vec3 v = entity.getDeltaMovement();
        float progress = Math.min(1, (entity.tickCount + partialTick) / 10);
        progress = (float) Math.sqrt(progress);
        poseStack.scale(progress,progress,progress);
        float yaw = (float) Math.atan2(v.z, v.x);
        poseStack.mulPose(Axis.YN.rotation((float) (yaw + Math.PI/2) * progress));
        poseStack.translate(0,0.75,0);
        poseStack.mulPose(Axis.XN.rotation(-Mth.lerp(partialTick, entity.rotate.old, entity.rotate.neo)));
    }
}
