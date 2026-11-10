package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.TheDestroyerProbe;
import software.bernie.geckolib.model.GeoModel;

/// 毁灭者探测器使用独立模型与贴图。
public final class DestroyerProbeRenderer extends GeoNormalRenderer<TheDestroyerProbe> {
    /// 模型前后外沿的中心，单位为方块。
    private static final float MODEL_CENTER_Z = 0.22F;

    public DestroyerProbeRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(TheDestroyerProbe entity) {
                return Confluence.asResource("geo/entity/boss/destroyer_probe.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(TheDestroyerProbe entity) {
                return Confluence.asResource("textures/entity/boss/destroyer_probe.png");
            }

            @Override
            public ResourceLocation getAnimationResource(TheDestroyerProbe entity) {
                return Confluence.asResource("animations/entity/boss/destroyer.animation.json");
            }
        }, true, 1.0F, 0.0F);
    }

    @Override
    protected boolean shouldRotateAlongPitch(TheDestroyerProbe probe) {return false;}

    @Override
    protected float getRenderYaw(TheDestroyerProbe probe, float partialTick) {
        return Mth.rotLerp(partialTick, probe.yHeadRotO, probe.yHeadRot);
    }

    @Override
    protected void applyRotations(TheDestroyerProbe probe, PoseStack pose, float age, float yaw, float partialTick) {
        // 将模型中心放到碰撞箱中心，再围绕同一中心俯仰。
        super.applyRotations(probe, pose, age, getRenderYaw(probe, partialTick), partialTick);
        pose.translate(0, probe.getBbHeight() / (2.0F * getEffectiveModelScale(probe) * probe.getScale()), 0);
        pose.mulPose(Axis.XP.rotationDegrees(-getRenderPitch(probe, partialTick)));
        pose.translate(0, 0, -MODEL_CENTER_Z);
    }
}
