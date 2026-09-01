package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

/// 只绘制骷髅王本体头部的渲染器。
///
/// 模型中的 {@code bone3} 是双手分支，而手具有独立实体、碰撞箱和渲染器。如果本体仍绘制
/// 该分支，会出现重影并掩盖手部死亡状态，所以在递归入口直接跳过整棵子树。这里不修改
/// GeckoLib 缓存骨骼的隐藏属性，避免共享烘焙模型把可见性泄漏到后续实体或帧。
public class SkeletronBossRenderer extends BossGeoRenderer<Skeletron> {
    public SkeletronBossRenderer(EntityRendererProvider.Context context) {
        // 头部同时跟随水平方向和俯仰，才能在玩家位于上下方时仍然正对目标。
        super(context, Confluence.asResource("boss/skeletron"), true, 1.0F, 0.0F);
    }

    @Override
    protected void applyRotations(Skeletron boss, PoseStack poseStack, float ageInTicks,
                                  float rotationYaw, float partialTick) {
        // 身体角度不是独立同步数据。直接使用实体网络角度，保证客户端在玩家绕到
        // 侧面或背面后立刻看到头部转向，而不是停留在生成时的身体朝向。
        float synchronizedYaw = boss.getFacingYaw(partialTick);
        super.applyRotations(boss, poseStack, ageInTicks, synchronizedYaw, partialTick);
    }

    @Override
    protected void adjustPose(PoseStack poseStack, Skeletron boss, BakedGeoModel model, float partialTick) {
        if (!boss.isSpinning()) return;
        poseStack.translate(0.0F, 1.15F, 0.0F);
        float yaw = boss.getFacingYaw(partialTick) * net.minecraft.util.Mth.DEG_TO_RAD;
        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw)))
                .rotationDegrees((boss.tickCount + partialTick) * 36.0F));
        poseStack.translate(0.0F, -1.15F, 0.0F);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Skeletron boss, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (bone.getName().equals("bone3")) {
            return;
        }
        super.renderRecursively(poseStack, boss, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
