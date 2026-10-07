package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.mount.AbstractMountEntity;
import org.confluence.mod.common.entity.mount.RideableSlimeMountEntity;
import org.confluence.mod.common.entity.mount.RideableUnicornMountEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;

public final class MountGeoRenderer<T extends AbstractMountEntity & GeoEntity> extends GeoSpecialSlimeRenderer<T> {
    public MountGeoRenderer(EntityRendererProvider.Context context, ExplicitGeoModel<T> model) {
        super(context, model);
    }

    @Override
    protected boolean usesSlimeLayers(T entity) {
        return entity instanceof RideableSlimeMountEntity;
    }

    @Override
    protected boolean isShellCube(GeoBone bone, int index) {
        return super.isShellCube(bone, index) || bone.getName().equals("bone") && index == 0;
    }

    @Override
    protected float getRenderYaw(T mount, float partialTick) {
        if (mount instanceof RideableUnicornMountEntity unicorn)
            return unicorn.getRenderBodyYaw(partialTick);
        return mount.getFirstPassenger() instanceof Player player ? player.getViewYRot(partialTick)
                : Mth.rotLerp(partialTick, mount.yRotO, mount.getYRot());
    }

    /// geckolib 4.8.4 的 5 参 `applyRotations` 已 `@Deprecated(forRemoval)`，基类渲染路径调用的是
    /// **6 参**版（`GeoEntityRenderer.java:250` → `:376`），所以覆写 6 参并把 `nativeScale` 透传下去。
    @Override
    protected void applyRotations(T mount, PoseStack poseStack, float ageInTicks, float ignoredBodyYaw, float partialTick, float nativeScale) {
        super.applyRotations(mount, poseStack, ageInTicks, getRenderYaw(mount, partialTick), partialTick, nativeScale);
        if (mount.tiltsWithMovement()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.rotLerp(partialTick, mount.xRotO, mount.getXRot())));
        }
        if (mount instanceof RideableUnicornMountEntity unicorn) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(unicorn.getRenderBank(partialTick)));
        }
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
