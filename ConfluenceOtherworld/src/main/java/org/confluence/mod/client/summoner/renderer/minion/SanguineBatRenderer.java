package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.SanguineBatMinion;

public class SanguineBatRenderer extends AbstractAttachmentEntityGeoRenderer<SanguineBatMinion> {

    public SanguineBatRenderer() {
        super(Confluence.asResource("entity/summon/sanguine_bat"));
    }

    @Override
    protected RenderContext<SanguineBatMinion> createContext(SanguineBatMinion bat, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(bat, visualNode, partialTick, packedLight)
                // 旧实现把 LightTexture.FULL_BRIGHT 当作 packedLight 传给模型提交，这里等价地在上下文里覆盖
                .packedLight(LightTexture.FULL_BRIGHT)
                .model(new ModelContext()
                        .scale(0.5f)
                        .rotationOffset(180, 0, 0)
                        .translateOffset(0, -0.5f, 0)
                        .alphaDistanceFactor(1.5F));
    }

    /** 模型提交之后再注册动态光源，与旧实现的调用顺序一致 */
    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        DynamicLightDispatcher.INSTANCE.addLightSource(context.visualNode.pos(), 0.5f);
    }
}
