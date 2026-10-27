package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DestroyerPart;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Quaternionf;
import org.mesdag.particlestorm.api.geckolib.RegisterLocatorPreTransformerEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/// 姿态完全读取实体自己的同步数据，客户端尚未收到本体时体节仍可正常显示。
public final class DestroyerRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/destroyer.png");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/boss/destroyer.animation.json");
    private static final ResourceLocation HEAD = model("head"), BODY = model("body"), CONNECTOR = model("connector"), TAIL = model("tail");

    private static final float MODEL_CENTER_Y = 14.0F / 16.0F;
    private static final float BODY_CENTER_Z = 35.61025F / 16.0F;
    private static final float CONNECTOR_CENTER_Z = 66.61025F / 16.0F;
    private static final float TAIL_CENTER_Z = 190.61025F / 16.0F;
    public static final RegisterLocatorPreTransformerEvent.Transformer<DestroyerPart> TRANSFORMER = (bone, part, mat, partialTick) -> {
        mat.scale(TheDestroyer.MODEL_SCALE);
        applyRotations(part, y -> mat.translate(0, y, 0), mat::rotate, z -> mat.translate(0, -MODEL_CENTER_Y, z), partialTick);
        RegisterLocatorPreTransformerEvent.Transformer.defaultTransformer(bone, part, mat, partialTick);
    };

    private static final Map<Object, RotationFrames> rotations = new WeakHashMap<>();

    public DestroyerRenderer(EntityRendererProvider.Context context) {
        super(context, new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(T entity) {
                if (entity instanceof DestroyerPart part)
                    return part.isTail() ? TAIL : part.getIndex() % 2 == 0 ? CONNECTOR : BODY;
                return HEAD;
            }

            @Override
            public ResourceLocation getTextureResource(T t) {
                return TEXTURE;
            }

            @Override
            public ResourceLocation getAnimationResource(T t) {
                return ANIMATION;
            }
        }, false, TheDestroyer.MODEL_SCALE, 0);
    }

    private static ResourceLocation model(String section) {
        return Confluence.asResource("geo/entity/boss/destroyer_" + section + ".geo.json");
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
    protected void applyRotations(T entity, PoseStack pose, float age, float yaw, float partialTick) {
        applyRotations(entity, y -> pose.translate(0, y, 0), pose::mulPose, z -> pose.translate(0, -MODEL_CENTER_Y, z), partialTick);
    }

    public static void applyRotations(Entity entity, FloatConsumer y, Consumer<Quaternionf> rotate, FloatConsumer z, float partialTick) {
        y.accept(entity.getBbHeight() / (2.0F * TheDestroyer.MODEL_SCALE));
        float roll = entity instanceof TheDestroyer head ? head.getRoll(1) : entity instanceof DestroyerPart part ? part.getRoll(1) : 0;
        PathNode current = new PathNode(entity.position(), entity.getYRot() - 180, -entity.getXRot(), roll);
        RotationFrames frames = rotations.computeIfAbsent(entity, ignored -> new RotationFrames(current, entity.tickCount));
        if (frames.tick != entity.tickCount) {
            frames.previous = entity.tickCount == frames.tick + 1 ? frames.current : current;
            frames.current = current;
            frames.tick = entity.tickCount;
        }
        rotate.accept(frames.previous.toQuaternion().slerp(frames.current.toQuaternion(), partialTick));
        float centerZ = entity instanceof DestroyerPart part
                ? part.isTail() ? TAIL_CENTER_Z : part.getIndex() % 2 == 0 ? CONNECTOR_CENTER_Z : BODY_CENTER_Z
                : 0;
        z.accept(-centerZ);
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
