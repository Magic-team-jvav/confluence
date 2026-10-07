package org.confluence.mod.client.entity.model;

import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.mount.RideableUnicornMountEntity;
import software.bernie.geckolib.animation.AnimationState;

/// 独角兽坐骑保留步态动画，额外让头部先于身体响应转向。
public final class UnicornMountModel extends ExplicitGeoModel<RideableUnicornMountEntity> {
    public UnicornMountModel() {
        super(Confluence.asResource("geo/entity/rideable/unicorn.geo.json"),
                Confluence.asResource("textures/entity/rideable/unicorn.png"),
                Confluence.asResource("animations/entity/unicorn.animation.json"));
    }

    @Override
    public void setCustomAnimations(RideableUnicornMountEntity mount, long instanceId, AnimationState<RideableUnicornMountEntity> state) {
        super.setCustomAnimations(mount, instanceId, state);
        var head = getAnimationProcessor().getBone("head");
        if (head != null) {
            /// 动画保留俯仰；偏航从初始姿态计算，避免每帧叠加造成旋转累积。
            head.setRotY(head.getInitialSnapshot().getRotY() + mount.getRenderHeadYaw(state.getPartialTick()) * Mth.DEG_TO_RAD);
        }
    }
}
