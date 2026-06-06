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
 * 使用 Geo 模型渲染弹球；链条使用 {@link BlockRenderDispatcher} 以方块形式逐段渲染，
 * 对齐 {@link org.confluence.mod.client.renderer.entity.hook.AbstractHookRenderer}。
 */
public class BaseFlailRenderer extends GeoEntityRenderer<BaseFlailEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = Confluence.asResource("textures/entity/flail/flail.png");
    private static final ResourceLocation DEFAULT_MODEL = Confluence.asResource("geo/entity/flail/flail.geo.json");

    private final BlockRenderDispatcher dispatcher;

    public BaseFlailRenderer(EntityRendererProvider.Context context) {
        super(context, new FlailGeoModel(DEFAULT_MODEL, DEFAULT_TEXTURE));
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    /** 根据 FlailComponent 解析模型路径：优先 item 专用模型，否则默认 */
    private static ResourceLocation resolveModel(@Nullable FlailComponent comp) {
        if (comp != null && comp.modelLocation().isPresent()) {
            return comp.modelLocation().get();
        }
        return DEFAULT_MODEL;
    }

    /**
     * 获取链条使用的方块状态，子类可覆写以自定义链条外观
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
        if (owner != null) {
            Vec3 handPos = HandPositionUtils.getPalmPosition((Player) owner, 1.0F);
            Vec3 ballPos = entity.getBoundingBox().getCenter();
            return frustum.isVisible(new AABB(ballPos.x, ballPos.y, ballPos.z, handPos.x, handPos.y, handPos.z));
        }
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(BaseFlailEntity entity) {
        FlailComponent comp = entity.getComponent();
        if (comp != null && comp.chainTexture() != null) return comp.chainTexture();
        return DEFAULT_TEXTURE;
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
        if (phase == BaseFlailEntity.PHASE_SPIN) {
            // 自转：实体层提供的纯线段方向（玩家面朝水平方向），不含公转偏移
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

        // 动态更新模型+贴图并渲染
        FlailGeoModel model = (FlailGeoModel) getGeoModel();
        model.model = resolveModel(component);
        model.texture = getTextureLocation(entity);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        // ── 渲染链条 ──
        renderChain(entity, owner, poseStack, bufferSource, packedLight, partialTick);
    }

    private void renderChain(BaseFlailEntity entity, Player owner, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, float partialTick) {
        int skyLight = LightTexture.pack(10, LightTexture.sky(packedLight));
        Vec3 handPos = HandPositionUtils.getPalmPosition(owner, 1.0F);
        Vec3 ballPos = getCenter(entity, partialTick);
        Vec3 diff = handPos.subtract(ballPos);
        double distance = diff.length();
        if (distance < 0.2) return;

        Vec3 dir = diff.normalize();
        BlockState chain = getChain(entity);

        poseStack.pushPose();
        // EntityRenderer 已平移到实体插值位置（Projectile 位置即几何中心）

        // 旋转使 Y 轴对齐链条方向
        poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI - (float) Math.atan2(dir.z, dir.x)));
        poseStack.mulPose(Axis.XP.rotation((float) Math.acos(dir.y)));
        poseStack.translate(-0.5, 0.0, -0.75);

        int floor = (int) distance;
        for (int i = 0; i < floor; i++) {
            dispatcher.renderSingleBlock(chain, poseStack, bufferSource, skyLight, OverlayTexture.NO_OVERLAY);
            poseStack.translate(0.0, 1.0, 0.0);
        }

        // 末段平滑缩放
        float delta = (float) (distance - floor);
        if (entity.lastDelta - delta > 0.5F || entity.lastDelta == 0.0) entity.lastDelta = delta;
        delta = Mth.lerp(partialTick, entity.lastDelta, delta);
        poseStack.scale(1.0F, delta, 1.0F);
        entity.lastDelta = delta;
        dispatcher.renderSingleBlock(chain, poseStack, bufferSource, skyLight, OverlayTexture.NO_OVERLAY);
        //好像没有可替代的方法

        poseStack.popPose();
    }

    /** 实体位置插值 */
    private static Vec3 getCenter(Entity entity, float partialTick) {
        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        return new Vec3(x, y, z);
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