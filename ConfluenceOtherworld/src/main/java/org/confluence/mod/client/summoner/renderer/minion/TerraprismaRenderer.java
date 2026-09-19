package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.client.summoner.trail.RibbonTrailConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

/**
 * 泰拉棱镜渲染器：灰底贴图配合逐实例色相做色调控制，攻击时额外绘制丝带拖尾。
 */
public class TerraprismaRenderer extends AbstractAttachmentEntityRenderer<TerraprismaMinion> {

    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/model/terraprisma_gray.png");
    private TerraprismaModel model;

    @Override
    protected RenderContext<TerraprismaMinion> createContext(TerraprismaMinion prism, float partialTick) {
        return RenderContext.<TerraprismaMinion>builder()
                .trail(new RibbonTrailConfig<TerraprismaMinion>()
                        .timer(prism.trailTimer)
                        .colorRGB(prism.getColor(partialTick))
                        .segmentsPerNode(4)
                        .historyLength(6)
                        .upOffset(1.015F))
                .model(new ModelConfig<TerraprismaMinion>()
                        .scale(1.5f)
                        .translateOffset(0, 0, 0.3f)
                        .rotationOffset(180, 0, 90)
                        .alphaDistanceFactor(1.5F))
                .build();
    }

    @Override
    protected void render(TerraprismaMinion prism, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<TerraprismaMinion> context, float partialTick, int packedLight, float alpha) {
        int color = prism.getColor(partialTick);
        if (model == null) {
            model = new TerraprismaModel(Minecraft.getInstance().getEntityModels().bakeLayer(TerraprismaModel.LAYER_LOCATION));
        }
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F, FastColor.ARGB32.blue(color) / 255.0F, alpha);
    }
}
