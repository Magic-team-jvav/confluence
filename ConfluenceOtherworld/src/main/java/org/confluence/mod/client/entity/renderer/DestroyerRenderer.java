package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * 为毁灭者头部应用沿运动轴线的滚转动画。
 */
public final class DestroyerRenderer extends BossGeoRenderer<TheDestroyer> {
    public DestroyerRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/eater_of_worlds"));
        withScale(2.2F);
    }

    @Override
    protected void adjustPose(
            PoseStack poseStack,
            TheDestroyer destroyer,
            BakedGeoModel model,
            float partialTick) {
        Vec3 axis = destroyer.getLookAngle();
        if (axis.lengthSqr() <= 1.0E-7) return;
        float roll = Mth.lerp(
                partialTick,
                destroyer.getPreviousBodyRoll(),
                destroyer.getBodyRoll());
        poseStack.mulPose(Axis.of(new Vector3f(
                (float) axis.x,
                (float) axis.y,
                (float) axis.z)).rotationDegrees(roll));
    }
}
