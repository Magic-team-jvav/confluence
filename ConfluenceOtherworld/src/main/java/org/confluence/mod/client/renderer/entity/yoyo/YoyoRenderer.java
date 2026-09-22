package org.confluence.mod.client.renderer.entity.yoyo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.entity.TetherRenderHelper;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 悠悠球模型与绳线渲染器。
public final class YoyoRenderer extends GeoEntityRenderer<YoyoEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/yoyos.geo.json");
    private static final ResourceLocation COUNTERWEIGHT_MODEL = Confluence.asResource("geo/entity/proj/counterweight.geo.json");
    private static final ResourceLocation[] COUNTERWEIGHT_TEXTURES = {
            Confluence.asResource("textures/entity/projectile/counterweight/black_counterweight.png"),
            Confluence.asResource("textures/entity/projectile/counterweight/blue_counterweight.png"),
            Confluence.asResource("textures/entity/projectile/counterweight/green_counterweight.png"),
            Confluence.asResource("textures/entity/projectile/counterweight/purple_counterweight.png"),
            Confluence.asResource("textures/entity/projectile/counterweight/red_counterweight.png"),
            Confluence.asResource("textures/entity/projectile/counterweight/yellow_counterweight.png")
    };

    public YoyoRenderer(EntityRendererProvider.Context context) {
        super(context, new Model());
        shadowRadius = 0.15F;
    }

    @Override
    public boolean shouldRender(YoyoEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return entity.getOwner() != null;
    }

    @Override
    public void render(YoyoEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        YoyoItem item = entity.getYoyoItem();
        if (item == null || !(entity.getOwner() instanceof Player player)) {
            return;
        }

        poseStack.pushPose();
        if (entity.isCounterweight()) {
            poseStack.translate(0, 0.25, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTick) * 24));
            poseStack.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTick) * 16));
            /// 模型中心在 Y=6 像素，先归中再自转，绳线接点沿用实体中心。
            poseStack.translate(0, -6.0 / 16.0, 0);
            super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, player.yHeadRotO, player.yHeadRot)));
            poseStack.translate(0.0F, 0.25F, 0.0F);
            poseStack.mulPose(Axis.XN.rotationDegrees((entity.tickCount + partialTick) * 45.0F));
            poseStack.translate(0.0F, -0.5F, 0.0F);
            super.render(entity, entityYaw, partialTick, poseStack, buffers, item.fullBright() ? 0xF000F0 : packedLight);
        }
        poseStack.popPose();

        if (!entity.isDetached())
            TetherRenderHelper.renderMainHandString(entityRenderDispatcher, entity, player, 0.25F, entity.stringColor(), partialTick, poseStack, buffers);
    }

    private static final class Model extends GeoModel<YoyoEntity> {
        @Override
        public ResourceLocation getModelResource(YoyoEntity animatable) {
            return animatable.isCounterweight() ? COUNTERWEIGHT_MODEL : MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(YoyoEntity animatable) {
            if (animatable.isCounterweight()) {
                int color = animatable.counterweightColor();
                /// 额外品种尚无专用贴图，沿用紫色。
                return COUNTERWEIGHT_TEXTURES[color >= 1 && color <= COUNTERWEIGHT_TEXTURES.length ? color - 1 : 3];
            }
            YoyoItem item = animatable.getYoyoItem();
            ResourceLocation id =
                    item == null ? null : ForgeRegistries.ITEMS.getKey(item);
            String name = id == null ? "wooden_yoyo" : id.getPath();
            return Confluence.asResource("textures/entity/yoyos/" + name + ".png");
        }

        @Override
        public ResourceLocation getAnimationResource(YoyoEntity animatable) {
            return null;
        }
    }
}
