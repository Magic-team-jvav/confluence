package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

/// 给复用原版肢体动画的人形 Geo 模型补上手持物品渲染。
/// 骨骼命名沿用 1.20 侧的 V 前缀，因此这里按前缀查找左右手；实际物品位姿保持和 1.21 的手持层一致。
public final class VanillaHumanoidRenderer<T extends Mob & GeoEntity> extends GeoNormalRenderer<T> {
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";

    public VanillaHumanoidRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        addRenderLayer(new HeldItemLayer<>(this));
    }

    private static final class HeldItemLayer<T extends Mob & GeoEntity> extends BlockAndItemGeoLayer<T> {
        private HeldItemLayer(VanillaHumanoidRenderer<T> renderer) {
            super(renderer);
        }

        @Nullable
        @Override
        protected ItemStack getStackForBone(GeoBone bone, T entity) {
            String name = bone.getName();
            if (name.startsWith(RIGHT_ARM)) {
                return entity.isLeftHanded() ? entity.getOffhandItem() : entity.getMainHandItem();
            }
            if (name.startsWith(LEFT_ARM)) {
                return entity.isLeftHanded() ? entity.getMainHandItem() : entity.getOffhandItem();
            }
            return null;
        }

        @Override
        protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T entity) {
            return bone.getName().startsWith(LEFT_ARM) ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        }

        @Override
        protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T entity, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
            // 保持和 1.21 侧人形怪手持层一致的手掌基准，再交给物品自己的 display 变换。
            poseStack.translate(0.0F, 0.0F, -0.0625F);
            poseStack.translate(0.0F, -0.0625F, 0.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.translate(0.03F, 0.05F, -0.5F);
            super.renderStackForBone(poseStack, bone, stack, entity, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }
}
