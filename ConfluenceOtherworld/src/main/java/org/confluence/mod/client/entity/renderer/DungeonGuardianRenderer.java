package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DungeonGuardian;
import software.bernie.geckolib.cache.object.GeoBone;

/// 地牢守卫复用骷髅王头部资源，但没有独立手臂实体。
/// 共享模型中的手臂根分支必须跳过，否则会在守卫周围额外绘制静止肢体。
public class DungeonGuardianRenderer extends BossGeoRenderer<DungeonGuardian> {
    public DungeonGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/skeletron"), true, 1.0F, 0.0F);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, DungeonGuardian guardian, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (bone.getName().equals("bone3")) {
            return;
        }
        super.renderRecursively(poseStack, guardian, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
