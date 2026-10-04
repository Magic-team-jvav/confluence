package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.RibbonTrailContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

/**
 * 泰拉棱镜渲染器：灰底贴图配合逐实例色相做色调控制，攻击时额外绘制丝带拖尾。
 */
public class TerraprismaRenderer extends AbstractAttachmentEntityRenderer<TerraprismaMinion> {

    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/model/terraprisma_gray.png");
    private TerraprismaModel model;

    @Override
    protected RenderContext<TerraprismaMinion> createContext(TerraprismaMinion prism, PathNode visualNode, float partialTick, int packedLight) {
        int color = prism.getColor(partialTick);
        return new RenderContext<>(prism, visualNode, partialTick, packedLight)
                .color(color)
                .trail(new RibbonTrailContext<TerraprismaMinion>()
                        .upOffset(1.015F)
                        .timer(prism.trailTimer)
                        .colorRGB(color)
                        .segmentsPerNode(4)
                        .historyLength(6))
                .model(new ModelContext()
                        .scale(1.5F)
                        .translateOffset(0, 0, 0.3F)
                        .rotationOffset(180, 0, 90)
                        .alphaDistanceFactor(1.5F));
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        DynamicLightDispatcher.INSTANCE.addLightSource(context.visualNode.pos(), 0.5f);
        if (model == null) {
            model = new TerraprismaModel(Minecraft.getInstance().getEntityModels().bakeLayer(TerraprismaModel.LAYER_LOCATION));
        }
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, context.color.getRedFloat(), context.color.getGreenFloat(), context.color.getBlueFloat(), context.color.getAlphaFloat());
    }
}
