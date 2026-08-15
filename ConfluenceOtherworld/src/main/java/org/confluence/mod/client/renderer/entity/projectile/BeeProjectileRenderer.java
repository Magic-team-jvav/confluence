package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.BeeProjectileModel;
import org.confluence.terra_curio.common.entity.BeeProjectile;

/**
 * 蜜蜂类弹幕的通用渲染器。
 *
 * <p>渲染角度按 1.21 侧的实体旋转插值处理，不额外改用看向相机的公告板渲染。这样蜜蜂会沿实体
 * 自身同步到客户端的朝向飞行，而不会全部正面朝向玩家。</p>
 */
public final class BeeProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE =
            Confluence.asResource("textures/entity/bee_projectile.png");
    private final BeeProjectileModel<T> model;

    public BeeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BeeProjectileModel<>(
                context.bakeLayer(BeeProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    public void render(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.125F, -0.125F);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
        if (entity instanceof BeeProjectile bee && bee.isGiant()) {
            poseStack.scale(1.5F, 1.5F, 1.5F);
        }
        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(model.renderType(TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
