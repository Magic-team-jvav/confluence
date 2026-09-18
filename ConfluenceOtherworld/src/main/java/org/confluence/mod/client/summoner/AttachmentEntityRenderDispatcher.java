package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.confluence.mod.common.summoner.attachmentEntity.*;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附件实体渲染调度器，统一管理所有附件实体（仆从、射弹）的渲染。
 * <p>
 * 在客户端渲染事件中调用，遍历当前世界的所有附件实体并调用对应的渲染器进行渲染。
 * </p>
 * <h2>第一人称透明度调整</h2>
 * <p>
 * 当玩家处于第一人称视角时，附件实体可能会遮挡玩家视野。为解决此问题，
 * 根据附件实体与玩家眼睛的距离动态调整透明度。
 * </p>
 */
public class AttachmentEntityRenderDispatcher {

    /**
     * 渲染器映射表，按实体类型存储对应的渲染器
     */
    private static final Map<AttachmentEntityType<?>, IAttachmentEntityRenderer<?>> renderers = new HashMap<>();

    /**
     * 渲染当前世界的所有附件实体。
     *
     * @param level        当前客户端世界
     * @param camera       摄像机
     * @param poseStack    矩阵栈
     * @param bufferSource 渲染缓冲源
     * @param partialTick  部分 tick 插值进度
     */
    public static void render(ClientLevel level, Camera camera, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
        List<AbstractClientPlayer> players = level.players();
        for (AbstractClientPlayer player : players) {
            List<AttachmentEntity> entities = player.getData(SummonerAttachmentTypes.ENTITY_DATA).getRenderCache();
            Vec3 cameraPos = camera.getPosition();
            boolean showHitboxes = Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes();
            VertexConsumer debugConsumer = showHitboxes ? bufferSource.getBuffer(RenderType.lines()) : null;
            for (AttachmentEntity entity : entities) {
                poseStack.pushPose();
                PathNode renderNode = entity.getRenderNode(partialTick);
                Vec3 pos = renderNode.pos();
                poseStack.translate(pos.x() - cameraPos.x(), pos.y() - cameraPos.y(), pos.z() - cameraPos.z());
                int lightCoords = getLightCoords(level, BlockPos.containing(pos));
                // 渲染实体模型
                IAttachmentEntityRenderer<AttachmentEntity> renderer = getRenderer(entity);
                if (renderer != null) {
                    renderer.render(entity, poseStack, bufferSource, partialTick, lightCoords, renderNode);
                }
                if (SummonerRenderConfig.DebugMode) {
                    debugRender(poseStack, entity, showHitboxes, renderNode, debugConsumer);
                }
                poseStack.popPose();
            }
        }
    }

    /**
     * 取世界坐标处的真实光照(pack 布局,等价 26.2 LightCoordsUtil.getLightCoords 的 1.21.1 适配):
     * 方块亮度(含方块自发光)+ 天空亮度,再叠加动态光源(实体路径增强)。
     */
    private static int getLightCoords(Level level, BlockPos pos) {
        int sky = level.getBrightness(LightLayer.SKY, pos);
        int block = Math.max(level.getBrightness(LightLayer.BLOCK, pos), level.getBlockState(pos).getLightEmission(level, pos));
        int packed = LightTexture.pack(block, sky);
        return DynamicLightDispatcher.getDynamicLight(Vec3.atCenterOf(pos), packed);
    }

    private static void debugRender(PoseStack poseStack, AttachmentEntity entity, boolean showHitboxes, PathNode renderNode, VertexConsumer debugConsumer) {
        // 调试渲染（使用原始缓冲源，不受透明度影响）
        if (showHitboxes) {
            LevelRenderer.renderLineBox(poseStack, debugConsumer, -0.001, -0.001, -0.001, 0.001, 0.001, 0.001, 1.0F, 1.0F, 0.0F, 1.0F);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YN.rotationDegrees(renderNode.yaw()));
            poseStack.mulPose(Axis.XP.rotationDegrees(renderNode.pitch()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(renderNode.roll()));
            LevelRenderer.renderLineBox(poseStack, debugConsumer, -0.0001, -0.0001, 0, 0.0001, 0.0001, 2, 0, 0, 1, 1.0F);
            LevelRenderer.renderLineBox(poseStack, debugConsumer, -0.0001, 0, -0.0001, 0.0001, 0.5, 0.0001, 0, 0, 1, 1.0F);
            if (entity instanceof IEntityCollision<?> iCollideAttack) {
                if (iCollideAttack.renderHitbox()) {
                    LevelRenderer.renderLineBox(poseStack, debugConsumer, iCollideAttack.getHitbox(), 1.0F, 0.0F, 0.0F, 1.0F);
                }
            }
            poseStack.popPose();
            if (entity instanceof IBlockCollision<?> iBlockCollision) {
                LevelRenderer.renderLineBox(poseStack, debugConsumer, iBlockCollision.getBlockCollisionBox(), 0.0F, 1.0F, 0.0F, 1.0F);
            }
        }
    }

    /**
     * 获取附件实体对应的渲染器。
     *
     * @param entity 附件实体实例
     * @return 对应的渲染器，若未注册则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T extends AttachmentEntity> IAttachmentEntityRenderer<T> getRenderer(T entity) {
        AttachmentEntityType<T> type = (AttachmentEntityType<T>) entity.getType();
        return (IAttachmentEntityRenderer<T>) renderers.get(type);
    }

    /**
     * 注册附件实体类型的渲染器。
     * <p>
     * 每种实体类型只能注册一个渲染器，重复注册将被忽略。
     * </p>
     *
     * @param type     实体类型
     * @param renderer 渲染器实例
     */
    public static <T extends AttachmentEntity> void register(AttachmentEntityType<T> type, IAttachmentEntityRenderer<T> renderer) {
        if (!renderers.containsKey(type)) {
            renderers.put(type, renderer);
        }
    }
}
