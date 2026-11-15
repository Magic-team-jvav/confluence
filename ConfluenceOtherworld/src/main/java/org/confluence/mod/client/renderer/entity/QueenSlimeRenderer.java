package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.ModRenderTypes;
import org.confluence.mod.common.entity.boss.QueenSlime;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtil;

/// 身体、纹理和动画根据同步生命值切换到同一阶段。
public class QueenSlimeRenderer extends GeoNormalRenderer<QueenSlime> {
    public QueenSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(QueenSlime queen) {
                return Confluence.asResource("geo/entity/boss/queen_slime.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(QueenSlime queen) {
                return Confluence.asResource("textures/entity/queen_slime/queen_slime.png");
            }

            @Override
            public ResourceLocation getAnimationResource(QueenSlime queen) {
                return Confluence.asResource("animations/entity/boss/queen_slime.animation.json");
            }
        });
    }

    @Override
    protected float getEffectiveModelScale(QueenSlime queen) {
        return queen.getType().getWidth() * 2.0F;
    }


    private boolean shellPass;

    /// 内核和饰物先绘制，外壳单独使用半透明材质。
    @Override
    public void actuallyRender(PoseStack poses, QueenSlime queen, BakedGeoModel model, RenderType type,
                               MultiBufferSource buffers, VertexConsumer vertices, boolean reRender,
                               float partialTick, int light, int overlay, int colour) {
        ResourceLocation texture = getTextureLocation(queen);
        RenderType solid = RenderType.entityCutout(texture);
        RenderType shell = ModRenderTypes.entityTranslucentCullOverlay(texture);
        shellPass = false;
        super.actuallyRender(poses, queen, model, solid, buffers, buffers.getBuffer(solid), reRender,
                partialTick, light, overlay, colour);
        if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(solid);
        shellPass = true;
        try {
            super.actuallyRender(poses, queen, model, shell, buffers, buffers.getBuffer(shell), true,
                    partialTick, light, overlay, colour);
        } finally {
            shellPass = false;
        }
    }

    @Override
    public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer vertices, int light, int overlay, int colour) {
        if (bone.isHidden()) return;
        for (int i = 0; i < bone.getCubes().size(); i++) {
            var cube = bone.getCubes().get(i);
            boolean shell = bone.getName().equals("outer") && cube.size().x == 8 && cube.size().z == 8;
            if (shell != shellPass) continue;
            poses.pushPose();
            renderCube(poses, cube, vertices, light, overlay, colour);
            poses.popPose();
        }
    }

    /// 先在动画后的壳顶绘制刚性王冠，再由史莱姆渲染器绘制内核和透明外壳。
    @Override
    public void renderRecursively(PoseStack poses, QueenSlime queen, GeoBone bone, RenderType type,
                                  MultiBufferSource buffers, VertexConsumer vertices, boolean reRender,
                                  float partialTick, int light, int overlay, int colour) {
        if (bone.getName().equals(queen.isFlyingPhase() ? "first" : "wings")) return;
        if (!reRender && bone.getParent() == null) {
            var outer = getGeoModel().getBone("outer").orElseThrow();
            PoseStack attachment = new PoseStack();
            applyBoneChain(attachment, outer);
            Vector3f top = attachment.last().pose().transformPosition(new Vector3f(0, 0.5F, 0));
            poses.pushPose();
            poses.translate(top.x, top.y, top.z);
            poses.scale(0.13F, 0.13F, 0.13F);
            var crown = GeckoLibCache.getBakedModels().get(Confluence.asResource("geo/entity/boss/queen_slime_crown.geo.json"));
            var material = RenderType.entityCutout(Confluence.asResource("textures/entity/queen_slime/queen_slime_crown.png"));
            for (GeoBone root : crown.topLevelBones()) {
                super.renderRecursively(poses, queen, root, material, buffers, buffers.getBuffer(material), true, partialTick, light, overlay, colour);
            }
            poses.popPose();
            if (buffers instanceof MultiBufferSource.BufferSource source) source.endBatch(material);
            vertices = buffers.getBuffer(type);
        }
        super.renderRecursively(poses, queen, bone, type, buffers, vertices, reRender, partialTick, light, overlay, colour);
    }

    private static void applyBoneChain(PoseStack poses, GeoBone bone) {
        if (bone.getParent() != null) applyBoneChain(poses, bone.getParent());
        RenderUtil.prepMatrixForBone(poses, bone);
    }
}
