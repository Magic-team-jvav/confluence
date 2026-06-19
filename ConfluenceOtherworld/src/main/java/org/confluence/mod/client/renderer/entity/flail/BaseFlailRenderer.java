package org.confluence.mod.client.renderer.entity.flail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.util.HandPositionUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * <h1>连枷渲染器</h1>
 * 使用 Geo 模型渲染弹球；链条使用 {@link BlockRenderDispatcher#renderSingleBlock} 逐段渲染。
 */
public class BaseFlailRenderer extends GeoEntityRenderer<BaseFlailEntity> {
    private static final ResourceLocation DEFAULT_BALL_TEXTURE = Confluence.asResource("textures/entity/flail/flail.png");
    private static final ResourceLocation DEFAULT_BALL_MODEL = Confluence.asResource("geo/entity/flail/flail.geo.json");

    private final BlockRenderDispatcher dispatcher;

    public BaseFlailRenderer(EntityRendererProvider.Context context) {
        super(context, new FlailGeoModel(DEFAULT_BALL_MODEL, DEFAULT_BALL_TEXTURE));
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    /** 解析弹球 Geo 模型路径（优先使用 FlailComponent 自定义值） */
    private static ResourceLocation resolveBallModel(@Nullable FlailComponent comp) {
        if (comp != null && comp.modelLocation().isPresent()) {
            return comp.modelLocation().get();
        }
        return DEFAULT_BALL_MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(BaseFlailEntity entity) {
        FlailComponent comp = entity.getComponent();
        if (comp != null && comp.ballTexture() != null) {
            return comp.ballTexture();
        }
        return DEFAULT_BALL_TEXTURE;
    }

    /**
     * 子类可覆写以自定义链条方块外观
     */
    protected BlockState getChain(BaseFlailEntity entity) {
        return Blocks.CHAIN.defaultBlockState();
    }

    @Override
    public boolean shouldRender(BaseFlailEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double camX, double camY, double camZ) {
        if (super.shouldRender(entity, frustum, camX, camY, camZ)) {
            return true;
        }
        Entity owner = entity.getOwner();
        if (owner instanceof Player player) {
            Vec3 handPos = HandPositionUtils.getPalmPosition(player, 1.0F);
            Vec3 ballPos = entity.getBoundingBox().getCenter();
            return frustum.isVisible(new AABB(ballPos.x, ballPos.y, ballPos.z, handPos.x, handPos.y, handPos.z));
        }
        return false;
    }

    @Override
    public void render(BaseFlailEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        Player owner = (Player) entity.getOwner();
        if (owner == null) return;

        FlailComponent component = entity.getComponent();
        if (component == null) return;

        // ── 渲染弹球（Geo 模型） ──
        poseStack.pushPose();
        poseStack.translate(0, 0.25F, 0);

        int phase = entity.getPhase();
        if (phase == BaseFlailEntity.PHASE_SPIN || phase == BaseFlailEntity.PHASE_THROWN) {
            poseStack.mulPose(new Quaternionf().rotateAxis(entity.spinAngle, entity.getSpinAxis()));
        } else {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.lengthSqr() > 0.001) {
                float yRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(motion.x, motion.z)));
                float xRot = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(-motion.y,
                        Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
                poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
            }
        }

        FlailGeoModel model = (FlailGeoModel) getGeoModel();
        model.model = resolveBallModel(component);
        model.texture = getTextureLocation(entity);

        // 预验证 Geo 模型是否存在，缺失时退回默认
        try {
            model.getBakedModel(model.model);
        } catch (RuntimeException e) {
            model.model = DEFAULT_BALL_MODEL;
            model.texture = DEFAULT_BALL_TEXTURE;
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        // ── 渲染链条 ──
        renderChain(entity, owner, poseStack, bufferSource, packedLight, partialTick, phase);
    }

    /**
     * 自适应非均匀分段链条渲染
     * <p>
     * 数学恒等式：distance = (fullSegments) × 1.0 + M × (remainder / M)
     */
    private void renderChain(BaseFlailEntity entity, Player owner, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, float partialTick, int phase) {
        // 使用插值后的渲染位置计算偏移（与 PoseStack 原点一致，避免链条抖动）
        Vec3 renderPos = entity.getPosition(partialTick);
        Vec3 ballPos = entity.getBoundingBox().getCenter();

        // 链条起点：根据阶段使用不同偏移
        Vec3 chainOffset = phase == BaseFlailEntity.PHASE_SPIN ? new Vec3(0.25, 0.25, -0.2) : new Vec3(0.0, 0.25, -0.2);
        Vec3 chainStart = HandPositionUtils.getPalmPosition(owner, partialTick, chainOffset);

        Vec3 diff = ballPos.subtract(chainStart);
        double distance = diff.length();
        if (distance < 0.2) return;

        Vec3 dir = diff.normalize();

        // 帧间方向平滑：防止球体瞬移导致链条手部端抖动（如 THROWN↔STAY 切换时）
        if (entity.smoothedChainDir != null) {
            dir = entity.smoothedChainDir.lerp(dir, 0.35);
        }
        entity.smoothedChainDir = dir;
        BlockState chain = getChain(entity);
        int skyLight = LightTexture.pack(10, LightTexture.sky(packedLight));

        poseStack.pushPose();

        // 平移到链条起点（renderPos 与 PoseStack 当前原点一致）
        Vec3 offset = chainStart.subtract(renderPos);
        poseStack.translate(offset.x, offset.y, offset.z);

        // 对齐局部 +Y 轴到链条方向（hand → ball）
        poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(dir.z, dir.x)));
        poseStack.mulPose(Axis.XP.rotation((float) Math.acos(Mth.clamp(dir.y, -1.0, 1.0))));
        poseStack.translate(-0.5, 0.0, -0.5);

        float segLength = 1.0f;
        int fullSegments = (int) distance;
        float remainder = (float) (distance - fullSegments);

        // 阈值处理：余量太小时向整数段借位，避免末端过于细碎
        if (remainder < 0.1F && fullSegments > 0) {
            fullSegments--;
            remainder += segLength;
        }

        // ========== 手部末端密集填充（密度大，每段独立 pushPose/popPose 隔离） ==========
        int microSegments = Math.max(2, (int) (remainder / 0.25f));
        float microLength = remainder / microSegments;

        for (int i = 0; i < microSegments; i++) {
            poseStack.pushPose();
            poseStack.translate(0.0, i * microLength, 0.0);
            poseStack.scale(1.0F, microLength / segLength, 1.0F);
            dispatcher.renderSingleBlock(chain, poseStack, bufferSource, skyLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        // 跨越微段区域，进入主体段起点
        poseStack.translate(0.0, remainder, 0.0);

        // ========== 中间主体段（密度小，累积平移无需 pushPose） ==========
        for (int i = 0; i < fullSegments; i++) {
            dispatcher.renderSingleBlock(chain, poseStack, bufferSource, skyLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0.0, segLength, 0.0);
        }

        poseStack.popPose();
    }

    // ── 内部 GeoModel（贴图可动态更新） ──

    private static class FlailGeoModel extends GeoModel<BaseFlailEntity> {
        ResourceLocation model;
        ResourceLocation texture;

        FlailGeoModel(ResourceLocation model, ResourceLocation texture) {
            this.model = model;
            this.texture = texture;
        }

        @Override
        public ResourceLocation getModelResource(BaseFlailEntity animatable) {
            return model;
        }

        @Override
        public ResourceLocation getTextureResource(BaseFlailEntity animatable) {
            return texture;
        }

        @Override
        public ResourceLocation getAnimationResource(BaseFlailEntity animatable) {
            return null;
        }
    }
}
