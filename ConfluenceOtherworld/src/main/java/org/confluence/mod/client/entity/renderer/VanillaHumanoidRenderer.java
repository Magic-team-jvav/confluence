package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

/// 给复用原版肢体动画的人形 Geo 模型补上手持物品渲染。
/// 骨骼命名沿用 1.20 侧的 V 前缀，因此这里按前缀查找左右手；实际物品位姿保持和 1.21 的手持层一致。
public final class VanillaHumanoidRenderer<T extends Mob & GeoEntity> extends GeoNormalRenderer<T> {
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";

    public VanillaHumanoidRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        addRenderLayer(new HeldItemLayer<>(this, LEFT_ARM, RIGHT_ARM));
    }

    /// 可由怪物和 NPC 共用的手持物层；骨骼名称由模型族显式提供。
    public static final class HeldItemLayer<T extends Mob & GeoEntity> extends BlockAndItemGeoLayer<T> {
        private final String leftArm;
        private final String rightArm;

        public HeldItemLayer(software.bernie.geckolib.renderer.GeoRenderer<T> renderer,
                             String leftArm, String rightArm) {
            super(renderer);
            this.leftArm = leftArm;
            this.rightArm = rightArm;
        }

        @Nullable
        @Override
        protected ItemStack getStackForBone(GeoBone bone, T entity) {
            String name = bone.getName();
            if (name.startsWith(rightArm)) {
                return entity.isLeftHanded() ? entity.getOffhandItem() : entity.getMainHandItem();
            }
            if (name.startsWith(leftArm)) {
                return entity.isLeftHanded() ? entity.getMainHandItem() : entity.getOffhandItem();
            }
            return null;
        }

        @Override
        protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T entity) {
            return bone.getName().startsWith(leftArm) ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        }

        @Override
        public void renderForBone(PoseStack poseStack, T entity, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                                  int packedLight, int packedOverlay) {
            ItemStack stack = getStackForBone(bone, entity);
            if (stack == null || stack.isEmpty()) return;
            poseStack.pushPose();
            RenderUtils.translateToPivotPoint(poseStack, bone);
            renderStackForBone(poseStack, bone, stack, entity, bufferSource, partialTick, packedLight, packedOverlay);
            bufferSource.getBuffer(renderType);
            poseStack.popPose();
        }

        @Override
        protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T entity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
            poseStack.translate(0.0F, 0.0F, -0.0625F);
            poseStack.translate(0.0F, -0.0625F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.translate(0.03F, 0.05F, -0.5F);
            super.renderStackForBone(poseStack, bone, stack, entity, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }
}
