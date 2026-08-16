package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.SkeletronHand;
import software.bernie.geckolib.cache.object.GeoBone;

/// 从骷髅王共享模型中只绘制手部分支的部件渲染器。
///
/// <p>{@code bone2} 对应本体头部，递归时跳过后只留下手部骨骼。手实体仍复用原始纹理，
/// 但位置、朝向、伤害反馈和生死状态都由各自实体控制。采用逐次递归过滤而非修改缓存骨骼，
/// 保证多个手实体同帧渲染时互不污染。</p>
public class SkeletronBossHandRenderer extends BossGeoRenderer<SkeletronHand> {
    public SkeletronBossHandRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<>(
                Confluence.asResource("geo/entity/boss/skeletron.geo.json"),
                Confluence.asResource("textures/entity/boss/skeletron.png"),
                null
        ));
        this.shadowRadius = 0.5F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SkeletronHand hand, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (bone.getName().equals("bone2")) {
            return;
        }
        super.renderRecursively(poseStack, hand, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
