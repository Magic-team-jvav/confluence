package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.GeoNormalModel;
import org.confluence.mod.common.entity.boss.BaseLivingBossPart;
import org.confluence.mod.common.entity.boss.Plantera;
import org.confluence.mod.common.entity.boss.PlanteraHook;
import org.confluence.mod.common.entity.boss.PlanteraTentacle;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;

public class PlanteraPartRenderer<T extends BaseLivingBossPart<Plantera> & GeoEntity> extends BossGeoRenderer<T> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/boss/plantera_parts.png");
    private static final ResourceLocation VINE = Confluence.asResource("geo/entity/boss/plantera_vine_segment.geo.json");
    private static final double SEGMENT_LENGTH = Plantera.MODEL_SCALE;
    // 花钩抓牢后向物块内推进的渲染距离，单位为格。
    private static final double HOOK_EMBED_DEPTH = 0.65;

    public PlanteraPartRenderer(EntityRendererProvider.Context context, String name) {
        super(context, new GeoNormalModel<T>(Confluence.asResource("boss/" + name), false) {
            @Override
            public ResourceLocation getTextureResource(T entity) {
                return TEXTURE;
            }
        }, false, 1.0F, 0.0F);
    }

    @Override
    protected boolean usesInterpolatedLight(T entity) {
        return true;
    }

    @Override
    public void render(T part, float yaw, float partialTick, PoseStack poses, MultiBufferSource buffers, int light) {
        super.render(part, yaw, partialTick, poses, buffers, light);
        Plantera boss = part.getOwner();
        if (boss == null) return;

        Vec3 anchor;
        if (part instanceof PlanteraHook hook) {
            anchor = boss.hookAttachment(hook.getHookIndex(), partialTick);
        } else {
            PlanteraTentacle tentacle = (PlanteraTentacle) part;
            anchor = tentacle.isPlanted() ? tentacle.getPlantedAnchor() : boss.tentacleAnchorForRender(tentacle.getSlot(), partialTick);
        }
        Vec3 start = new Vec3(0, part.getBbHeight() * 0.5, 0);
        if (part instanceof PlanteraHook hook) {
            // 花钩的藤柄在模型底部，沿钩身反方向从中心退到藤柄内侧。
            start = start.add(hookEmbedOffset(hook, partialTick))
                    .subtract(hook.getViewVector(partialTick).scale(0.5 * Plantera.MODEL_SCALE * hook.getScale()));
        }
        Vec3 offset = anchor.subtract(part.getPosition(partialTick)).subtract(start);
        double length = offset.length();
        if (length < 0.01) return;
        int count = Mth.ceil(length / SEGMENT_LENGTH);
        float stretch = (float) (length / count / SEGMENT_LENGTH);
        Quaternionf rotation = new Quaternionf().rotationTo(new Vector3f(0, -1, 0), offset.normalize().toVector3f());
        var model = getGeoModel().getBakedModel(VINE);
        RenderType type = RenderType.entityCutoutNoCull(TEXTURE);
        var vertices = buffers.getBuffer(type);
        int overlay = getPackedOverlay(part, 0, partialTick);
        for (int index = 0; index < count; index++) {
            Vec3 position = start.add(offset.scale((double) index / count));
            // 每节藤蔓取中点光照，使一条跨越明暗区域的藤蔓能逐段过渡。
            Vec3 midpoint = part.getPosition(partialTick).add(position).add(offset.scale(0.5 / count));
            int segmentLight = EntityLightSampler.sample(midpoint, pos -> getBlockLightLevel(part, pos), pos -> getSkyLightLevel(part, pos));
            poses.pushPose();
            poses.translate(position.x, position.y, position.z);
            poses.mulPose(rotation);
            poses.scale(Plantera.MODEL_SCALE, stretch * Plantera.MODEL_SCALE, Plantera.MODEL_SCALE);
            for (var bone : model.topLevelBones()) {
                renderRecursively(poses, part, bone, type, buffers, vertices, true, partialTick, segmentLight, overlay, 0xFFFFFFFF);
            }
            poses.popPose();
        }
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        Vec3 embedOffset = animatable instanceof PlanteraHook hook ? hookEmbedOffset(hook, partialTick) : Vec3.ZERO;
        poseStack.translate(embedOffset.x, animatable.getBbHeight() * 0.5 + embedOffset.y, embedOffset.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-getRenderYaw(animatable, partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(90 + getRenderPitch(animatable, partialTick)));
        poseStack.scale(Plantera.MODEL_SCALE, Plantera.MODEL_SCALE, Plantera.MODEL_SCALE);
        poseStack.translate(0, -0.5, 0);
    }

    private static Vec3 hookEmbedOffset(PlanteraHook hook, float partialTick) {
        BlockPos anchor = hook.getAnchor();
        if (anchor == null || !hook.hasReachedAnchor()) return Vec3.ZERO;
        // 钩身会随藤蔓倾斜，入块方向以锚定物块为准。
        Vec3 center = hook.getPosition(partialTick).add(0, hook.getBbHeight() * 0.5, 0);
        return Vec3.atCenterOf(anchor).subtract(center).normalize().scale(HOOK_EMBED_DEPTH);
    }
}
