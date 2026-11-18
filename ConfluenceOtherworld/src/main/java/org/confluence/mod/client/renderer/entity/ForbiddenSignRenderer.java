package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.model.entity.ForbiddenSignModel;
import org.confluence.mod.common.entity.projectile.ForbiddenSignEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 禁戒标记的渲染器。
///
/// 实体本身贴在玩家坐标（见 `ForbiddenSignEntity#baseTick`），这里再按玩家**身体**朝向把模型推到身后，
/// 偏移与朝向都用原版角度插值（`rotLerp`），所以和玩家模型的转向完全同步，不会抖。
/// 本地玩家第一人称直接跳过（标记在身后，第一人称不该看见）。
public class ForbiddenSignRenderer extends GeoNormalRenderer<ForbiddenSignEntity> {
    /// 玩家身体后方的水平距离。
    private static final double BACK_DISTANCE = 1.15;
    /// 基础悬浮高度（相对眼睛）。
    private static final double BASE_HEIGHT = 0.35;

    public ForbiddenSignRenderer(EntityRendererProvider.Context context) {
        super(context, ForbiddenSignModel.create(ModEntities.FORBIDDEN_SIGN.getId()));
        /// 发光体不投阴影。
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(ForbiddenSignEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Player owner = entity.getFollowingPlayer();
        if (owner == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.getCameraType().isFirstPerson() && minecraft.player == owner) return;

        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        double yaw = Math.toRadians(bodyYaw);
        // MC 的朝向向量是 (-sin, 0, cos)，所以身体正后方是 (+sin, 0, -cos)。
        double backX = Mth.sin((float) yaw) * BACK_DISTANCE;
        double backZ = -Mth.cos((float) yaw) * BACK_DISTANCE;

        poseStack.pushPose();
        poseStack.translate(backX, owner.getEyeHeight() + BASE_HEIGHT, backZ);
        /// 满亮渲染：不跟随世界光照，标记在任何亮度下都是自发光的。
        super.render(entity, bodyYaw, partialTick, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }
}
