package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.model.entity.projectile.TitaniumShardsProjectileModel;
import org.confluence.mod.common.entity.projectile.TitaniumShardsProjectile;
import org.joml.Vector3d;

public class TitaniumShardsProjectileRenderer extends EntityRenderer<TitaniumShardsProjectile> {
    private static final RenderType[] RENDER_TYPES = new RenderType[]{
            RenderType.entityTranslucent(Confluence.asResource("textures/entity/titanium_shards/_0.png"), false),
            RenderType.entityTranslucent(Confluence.asResource("textures/entity/titanium_shards/_1.png"), false),
            RenderType.entityTranslucent(Confluence.asResource("textures/entity/titanium_shards/_2.png"), false)
    };
    private final TitaniumShardsProjectileModel model;

    public TitaniumShardsProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TitaniumShardsProjectileModel(context.bakeLayer(TitaniumShardsProjectileModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(TitaniumShardsProjectile entity) {
        return ModClientSetups.VANILLA_BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(TitaniumShardsProjectile livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(TitaniumShardsProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int color = Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0x55FFFFFF : -1;
        Player owner = entity.getOwner();
        if (owner != null) { // 补偿与玩家的坐标差值
            poseStack.pushPose();
            poseStack.translate(
                    (float) Mth.lerp(partialTick, owner.xOld - entity.xOld, owner.getX() - entity.getX()),
                    (float) Mth.lerp(partialTick, owner.yOld - entity.yOld, owner.getY() - entity.getY()),
                    (float) Mth.lerp(partialTick, owner.zOld - entity.zOld, owner.getZ() - entity.getZ())
            );
        }
        for (int i = 0; i < entity.shardPos.size(); i++) {
            Vector3d start = entity.shardPosO.get(i);
            Vector3d end = entity.shardPos.get(i);
            double x = Mth.lerp(partialTick, start.x, end.x);
            double y = Mth.lerp(partialTick, start.y, end.y);
            double z = Mth.lerp(partialTick, start.z, end.z);
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(Axis.YN.rotation((entity.tickCount + partialTick) * Mth.DEG_TO_RAD + i));
            poseStack.scale(0.8F, -0.8F, 0.8F);
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RENDER_TYPES[i % 3]), packedLight, OverlayTexture.NO_OVERLAY, color);
            poseStack.popPose();
        }
        if (owner != null) {
            poseStack.popPose();
        }
    }
}
