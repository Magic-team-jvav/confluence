package org.confluence.mod.client.renderer.entity.yoyo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.entity.TetherRenderHelper;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/// 悠悠球模型与绳线渲染器。
///
/// 1.21 侧的悠悠球不会直接使用实体默认朝向来摆放模型，而是让模型在玩家视角平面中
/// 自转，并把绳线锚到球体上方的同一个视觉中心。这样可以避免实体运动角度影响模型，
/// 造成“球绕着绳线旋转”的错误观感。
public final class YoyoRenderer extends GeoEntityRenderer<YoyoEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/yoyos.geo.json");

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
            poseStack.scale(0.3F, 0.3F, 0.3F);
            poseStack.translate(-0.5, -0.5, -0.5);
            /// 平衡锤专用美术未提供，先用对应颜色的原版方块显示独立弹体。
            BlockState block = switch (entity.counterweightColor()) {
                case 1 -> Blocks.BLACK_CONCRETE.defaultBlockState();
                case 2 -> Blocks.BLUE_CONCRETE.defaultBlockState();
                case 3 -> Blocks.GREEN_CONCRETE.defaultBlockState();
                case 4 -> Blocks.PURPLE_CONCRETE.defaultBlockState();
                case 5 -> Blocks.RED_CONCRETE.defaultBlockState();
                case 6 -> Blocks.YELLOW_CONCRETE.defaultBlockState();
                default -> Blocks.PINK_CONCRETE.defaultBlockState();
            };
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(block, poseStack, buffers, packedLight, OverlayTexture.NO_OVERLAY);
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
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(YoyoEntity animatable) {
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
