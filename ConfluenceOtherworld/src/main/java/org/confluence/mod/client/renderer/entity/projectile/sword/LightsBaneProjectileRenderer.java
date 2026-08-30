package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.projectile.EnchantedSwordProjectileModel;
import org.confluence.mod.common.entity.projectile.sword.LightBaneProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.joml.Vector3f;

public class LightsBaneProjectileRenderer extends ForwardProjRenderer<LightBaneProjectile, SwordProjectile,  EnchantedSwordProjectileModel> {

    public LightsBaneProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new EnchantedSwordProjectileModel(context.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION)),
                Confluence.asResource("textures/entity/lights_bane.png"));
    }

    @Override
    public void preRender(LightBaneProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, int packedLight) {
        float totalTicks = entity.tickCount + partialTick;
        float lifetime = (float) entity.lifetime;
        float scale;
        if (totalTicks < 10) {
            scale = totalTicks / 10.0f;
        } else {
            scale = Math.max(1.0f - (totalTicks - 10) / (lifetime - 10), 0.0f);
        }
        poseStack.scale(scale, scale, scale);

        poseStack.translate(0.00F, 0.125F, -0.125F);

        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.getYRot(), entity.getYRot())));
        float yRot = entity.getYHeadRot();
        float rad = yRot * Mth.DEG_TO_RAD;
        float xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.of(new Vector3f(Mth.cos(rad), 0, Mth.sin(rad))).rotationDegrees(xRot));

    }

    @Override
    public RenderType getRenderType(LightBaneProjectile entity, float partialTicks) {
        return RenderType.energySwirl(getTextureLocation(entity), (float) Math.sin(entity.tickCount * 0.1F), (float) Math.sin(entity.tickCount * 0.2F));
    }

}
