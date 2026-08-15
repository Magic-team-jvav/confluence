package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.WormPartGeoModel;
import org.confluence.mod.common.entity.boss.BossWormPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * Boss 蠕虫共用体节渲染器；毁灭者体节额外应用滚转角。
 */
public final class BossWormPartRenderer extends BossGeoRenderer<BossWormPart> {
    public BossWormPartRenderer(EntityRendererProvider.Context context) {
        super(context, new WormPartGeoModel<>(
                Confluence.asResource("geo/entity/boss/eater_of_worlds_segment.geo.json"),
                Confluence.asResource("textures/entity/boss/eater_of_worlds_segment.png"),
                Confluence.asResource("geo/entity/boss/eater_of_worlds_tail.geo.json"),
                Confluence.asResource("textures/entity/boss/eater_of_worlds_tail.png")));
        withScale(2.2F);
    }

    @Override
    protected void adjustPose(
            PoseStack poseStack,
            BossWormPart segment,
            BakedGeoModel model,
            float partialTick) {
        if (!(segment.getOwner() instanceof TheDestroyer)) return;
        Vec3 axis = segment.getLookAngle();
        if (axis.lengthSqr() <= 1.0E-7) return;
        float roll = Mth.lerp(
                partialTick,
                segment.getPreviousSegmentRoll(),
                segment.getSegmentRoll());
        poseStack.mulPose(Axis.of(new Vector3f(
                (float) axis.x,
                (float) axis.y,
                (float) axis.z)).rotationDegrees(roll));
    }
}
