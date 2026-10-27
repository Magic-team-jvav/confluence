package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.ExplicitGeoModel;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import software.bernie.geckolib.animatable.GeoEntity;


import java.util.Map;
import java.util.WeakHashMap;

/// 姿态完全读取实体自己的同步数据，客户端尚未收到本体时体节仍可正常显示。
public final class DestroyerRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/destroyer_rebuilt.png");
    private static final ResourceLocation HEAD = model("head"), BODY = model("body"), CONNECTOR = model("connector"), TAIL = model("tail");

    private static final double MODEL_CENTER_Y = 14.0 / 16.0;
    private static final double BODY_CENTER_Z = 35.61025 / 16.0;
    private static final double CONNECTOR_CENTER_Z = 66.61025 / 16.0;
    private static final double TAIL_CENTER_Z = 190.61025 / 16.0;


    private final Map<T, RotationFrames> rotations = new WeakHashMap<>();

    public DestroyerRenderer(EntityRendererProvider.Context context) {
        super(context, new ExplicitGeoModel<T>(HEAD, TEXTURE, null) {
            @Override
            public ResourceLocation getModelResource(T entity) {
                if (entity instanceof DestroyerPart part)
                    return part.isTail() ? TAIL : part.getIndex() % 2 == 0 ? CONNECTOR : BODY;
                return HEAD;
            }
        }, false, TheDestroyer.MODEL_SCALE, 0);
    }

    private static ResourceLocation model(String section) {
        return Confluence.asResource("geo/entity/boss/destroyer_rebuilt_" + section + ".geo.json");
    }

    /// 头部与每个体节分别采样相邻格点光照，平滑跨越明暗边界。
    @Override
    protected boolean usesInterpolatedLight(T entity) {
        return true;
    }

    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    protected void applyRotations(T entity, PoseStack pose, float age, float yaw, float partialTick, float scale) {
        pose.translate(0, entity.getBbHeight() / (2.0 * TheDestroyer.MODEL_SCALE), 0);
        float roll = entity instanceof TheDestroyer head ? head.getRoll(1) : entity instanceof DestroyerPart part ? part.getRoll(1) : 0;
        PathNode current = new PathNode(entity.position(), entity.getYRot() - 180, -entity.getXRot(), roll);
        RotationFrames frames = rotations.computeIfAbsent(entity, ignored -> new RotationFrames(current, entity.tickCount));
        if (frames.tick != entity.tickCount) {
            frames.previous = entity.tickCount == frames.tick + 1 ? frames.current : current;
            frames.current = current;
            frames.tick = entity.tickCount;
        }
        pose.mulPose(frames.previous.toQuaternion().slerp(frames.current.toQuaternion(), partialTick));
        double centerZ = entity instanceof DestroyerPart part ? part.isTail() ? TAIL_CENTER_Z : part.getIndex() % 2 == 0 ? CONNECTOR_CENTER_Z : BODY_CENTER_Z : 0;
        pose.translate(0, -MODEL_CENTER_Y, -centerZ);
    }

    /// 快照只保存姿态，不引用实体；弱键缓存不会保留已经卸载的体节。
    private static final class RotationFrames {
        private PathNode previous;
        private PathNode current;
        private int tick;

        private RotationFrames(PathNode pose, int tick) {
            previous = current = pose;
            this.tick = tick;
        }
    }
}
