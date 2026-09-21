package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.IAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.EyeLaserTurretMinion;

/** 眼球激光塔：基座和眼球使用独立 Geo 渲染器组合，基座固定，眼球绕自身枢轴朝向目标。 */
public class EyeLaserTurretRenderer implements IAttachmentEntityRenderer<EyeLaserTurretMinion> {

    private final EyeLaserTurretBaseRenderer baseRenderer = new EyeLaserTurretBaseRenderer();
    private final EyeLaserTurretHeadRenderer headRenderer = new EyeLaserTurretHeadRenderer();

    @Override
    public void render(EyeLaserTurretMinion turret, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, PathNode visualNode) {
        baseRenderer.render(turret, poseStack, bufferSource, partialTick, packedLight, new PathNode(visualNode.pos(), 0.0F, 0.0F, 0.0F));
        headRenderer.render(turret, poseStack, bufferSource, partialTick, packedLight, visualNode);
    }

    private static final class EyeLaserTurretBaseRenderer extends AbstractAttachmentEntityGeoRenderer<EyeLaserTurretMinion> {

        private EyeLaserTurretBaseRenderer() {
            super(Confluence.asResource("entity/summon/eye_laser_turret_base"));
        }

        @Override
        protected RenderContext<EyeLaserTurretMinion> createContext(EyeLaserTurretMinion turret, float partialTick) {
            return RenderContext.<EyeLaserTurretMinion>builder()
                    .model(new ModelConfig<EyeLaserTurretMinion>()
                            .translateOffset(0.0F, -1.699f, 0.0F))
                    .build();
        }
    }

    private static final class EyeLaserTurretHeadRenderer extends AbstractAttachmentEntityGeoRenderer<EyeLaserTurretMinion> {

        private EyeLaserTurretHeadRenderer() {
            super(Confluence.asResource("entity/summon/eye_laser_turret_head"));
        }

        @Override
        protected RenderContext<EyeLaserTurretMinion> createContext(EyeLaserTurretMinion turret, float partialTick) {
            return RenderContext.<EyeLaserTurretMinion>builder()
                    .model(new ModelConfig<EyeLaserTurretMinion>()
                            .translateOffset(0.0F, -1.81f, 0.0F)
                            .rotationOffset(180, 0, 0))
                    .build();
        }
    }
}
