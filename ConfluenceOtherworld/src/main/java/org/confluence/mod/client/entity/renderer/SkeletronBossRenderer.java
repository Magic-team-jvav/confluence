package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.Skeletron;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * 只绘制骷髅王本体头部的渲染器。
 *
 * <p>模型中的 {@code bone3} 是双手分支，而手具有独立实体、碰撞箱和渲染器。如果本体仍绘制
 * 该分支，会出现重影并掩盖手部死亡状态，所以在递归入口直接跳过整棵子树。这里不修改
 * GeckoLib 缓存骨骼的隐藏属性，避免共享烘焙模型把可见性泄漏到后续实体或帧。</p>
 */
public class SkeletronBossRenderer extends BossGeoRenderer<Skeletron> {
    public SkeletronBossRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/skeletron"));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Skeletron boss, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (bone.getName().equals("bone3")) {
            return;
        }
        super.renderRecursively(poseStack, boss, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
