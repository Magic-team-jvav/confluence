package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.SkeletronPrimeArm;
import software.bernie.geckolib.cache.object.GeoBone;

/// 从机械骷髅王共享模型中绘制单个机械臂武器分支。
///
/// 四类机械臂共用一个实体类和一份模型，通过同步的 {@code armType} 只放行激光、锯刃、
/// 钳子或火炮中的对应骨骼。头部与未使用分支始终跳过。非激光武器在原始组合模型中沿 Z 轴
/// 分层摆放，渲染独立实体时需要抵消该层偏移；矩阵严格成对入栈/出栈，不能把偏移传给
/// 后续骨骼。
public class SkeletronPrimeArmRenderer extends BossGeoRenderer<SkeletronPrimeArm> {
    private static final String[] WEAPON_BONES = {
            "prime_laser", "prime_saw", "prime_vice", "prime_cannon"
    };

    public SkeletronPrimeArmRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<>(Confluence.asResource("geo/entity/boss/skeletron_prime.geo.json"), Confluence.asResource("textures/entity/boss/skeletron_prime.png"), null));
        this.shadowRadius = 0.5F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronPrimeArm arm, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        String boneName = bone.getName();
        if (boneName.equals("bone2") || boneName.equals("bone7")) {
            return;
        }
        for (int armType = 0; armType < WEAPON_BONES.length; armType++) {
            if (boneName.equals(WEAPON_BONES[armType])) {
                if (arm.getArmType() != armType) {
                    return;
                }
                if (armType == SkeletronPrimeArm.LASER) {
                    break;
                }
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, weaponModelOffset(armType));
                super.renderRecursively(poseStack, arm, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                poseStack.popPose();
                return;
            }
        }
        super.renderRecursively(poseStack, arm, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    static double weaponModelOffset(int armType) {
        // 该间距来自共享 geo 模型的四层武器布局，测试负责锁定与资源文件的契约。
        return armType * 2.0D;
    }
}
