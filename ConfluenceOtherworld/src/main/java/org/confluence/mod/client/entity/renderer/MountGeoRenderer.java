package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;
import software.bernie.geckolib.animatable.GeoEntity;

/// 为 1.20 轻量坐骑实体补齐 GeckoLib 的水平朝向渲染。
///
/// <p>1.21 坐骑继承自 {@code Mob}，GeckoLib 会自动从 {@code yBodyRot} 读取模型
/// 朝向。1.20 重写后的临时坐骑继承自普通 {@code Entity}，GeckoLib 4.8.3 对这类
/// 实体把身体朝向固定为零，即使实体的 {@code YRot} 已正确同步，模型也不会转动。
/// 本渲染器只为坐骑补回身体朝向，不改变通用实体渲染器，避免影响已经自行处理旋转的
/// 弹幕和其他非生物实体。存在玩家乘客时优先读取玩家已经过帧间插值的视角朝向，防止
/// 本地连续鼠标输入与每 tick 更新的坐骑实体朝向互相追赶；没有乘客时才退回实体朝向。</p>
public final class MountGeoRenderer<T extends AbstractMountEntity & GeoEntity>
        extends GeoNormalRenderer<T> {
    public MountGeoRenderer(
            EntityRendererProvider.Context context,
            ExplicitGeoModel<T> model
    ) {
        super(context, model);
    }

    @Override
    protected void applyRotations(
            T mount,
            PoseStack poseStack,
            float ageInTicks,
            float ignoredBodyYaw,
            float partialTick
    ) {
        Entity passenger = mount.getFirstPassenger();
        float bodyYaw = passenger instanceof Player player
                ? player.getViewYRot(partialTick)
                : Mth.rotLerp(
                partialTick,
                mount.yRotO,
                mount.getYRot());
        super.applyRotations(
                mount,
                poseStack,
                ageInTicks,
                bodyYaw,
                partialTick);
    }

    @Override
    public MountGeoRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    @Override
    public MountGeoRenderer<T> setShadowRadius(float shadowRadius) {
        super.setShadowRadius(shadowRadius);
        return this;
    }
}
