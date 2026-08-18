package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.model.entity.projectile.EnchantedSwordProjectileModel;
import org.confluence.mod.client.model.entity.projectile.IceBladeSwordProjectileModel;
import org.confluence.mod.client.model.entity.projectile.SwordProjectileGeoModel;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import org.confluence.mod.common.entity.projectile.sword.GeoSwordProjectile;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 按剑气组件选择 Geo、实体模型、物品或交叉面片表现。
public final class SwordProjectileRenderer<T extends SwordProjectile> extends EntityRenderer<T> {
    private final Map<ResourceLocation, EntityModel<SwordProjectile>> models = new HashMap<>();
    private final GeoRenderer geoRenderer;

    public SwordProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        models.put(Confluence.asResource("ice_blade_sword_projectile"),
                new IceBladeSwordProjectileModel(context.bakeLayer(IceBladeSwordProjectileModel.LAYER_LOCATION)));
        models.put(Confluence.asResource("enchanted_sword_projectile"),
                new EnchantedSwordProjectileModel(context.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION)));
        geoRenderer = new GeoRenderer(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getProjectileComponent() == null) return;
        SwordProjectileAppearance appearance = entity.getProjectileComponent().appearance();
        if (appearance instanceof SwordProjectileAppearance.Geo && entity instanceof GeoSwordProjectile geoProjectile) {
            geoRenderer.render(geoProjectile, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }
        if (appearance instanceof SwordProjectileAppearance.Model model)
            renderModel(entity, model, partialTick, poseStack, bufferSource, packedLight);
        else if (appearance instanceof SwordProjectileAppearance.Item item)
            renderItem(entity, item, partialTick, poseStack, bufferSource, packedLight);
        else if (appearance instanceof SwordProjectileAppearance.Cross cross)
            renderCross(entity, cross, partialTick, poseStack, bufferSource, packedLight);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderModel(T entity, SwordProjectileAppearance.Model appearance, float partialTick, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight) {
        EntityModel<SwordProjectile> model = models.get(appearance.model());
        if (model == null) return;
        poseStack.pushPose();
        float scale = appearance.scale() * lifecycleScale(entity, appearance.lifecycle(), partialTick);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, appearance.offsetY(), 0.0F);
        orientAlongMotion(entity, partialTick, appearance.rollSpeed(), poseStack);
        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, entity.getYRot(), entity.getXRot());
        model.renderToBuffer(poseStack, bufferSource.getBuffer(renderType(appearance, entity.tickCount + partialTick)), packedLight,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private void renderItem(T entity, SwordProjectileAppearance.Item appearance, float partialTick, PoseStack poseStack,
                            MultiBufferSource bufferSource, int packedLight) {
        ItemStack weapon = entity.getWeaponItem();
        if (weapon == null || weapon.isEmpty()) return;
        boolean nightEdgeEffect = appearance.effect().filter(Confluence.asResource("nights_edge")::equals).isPresent() && entity instanceof NightEdgeProjectile;
        if (nightEdgeEffect) {
            NightEdgeProjectile nightEdge = (NightEdgeProjectile) entity;
            float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
            renderNightEdgeTrail(nightEdge, age, poseStack, bufferSource, 0xB4570EFD, 0.32F, 0.0F);
            renderNightEdgeTrail(nightEdge, age, poseStack, bufferSource, 0x80E4E0FF, 0.18F, 0.14F);
        }
        poseStack.pushPose();
        poseStack.scale(appearance.scale(), appearance.scale(), appearance.scale());
        if (appearance.transform() == SwordProjectileAppearance.Transform.OWNER_SWING && entity instanceof NightEdgeProjectile nightEdge) {
            float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
            Vec3 point = rotateForOwner(nightEdge, NightEdgeProjectile.sampleLocalPoint(age));
            poseStack.translate(point.x, point.y, point.z);
            Entity owner = entity.getOwner();
            float ownerYaw = owner == null ? entity.getYRot() : Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
            poseStack.mulPose(Axis.YP.rotationDegrees(-ownerYaw + 70.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(NightEdgeProjectile.sampleRoll(age)));
        } else {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.lengthSqr() > 1.0E-10) {
                poseStack.mulPose(Axis.YN.rotation((float) Math.atan2(motion.z, motion.x) + Mth.PI));
                poseStack.mulPose(Axis.ZN.rotation((float) Math.atan2(motion.y, motion.horizontalDistance()) - Mth.PI * 0.25F));
            }
            poseStack.mulPose(Axis.ZP.rotation((entity.tickCount + partialTick) * 0.25F));
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(weapon, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
    }

    private void renderCross(T entity, SwordProjectileAppearance.Cross appearance, float partialTick, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float age = entity.tickCount + partialTick;
        float scale = Math.min(age * 0.2F, 1.0F) * appearance.scale();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * appearance.spinSpeed()));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(appearance.texture()));
        renderPlane(consumer, poseStack.last(), appearance.fullBright() ? 0xF000F0 : packedLight, appearance.color());
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderPlane(consumer, poseStack.last(), appearance.fullBright() ? 0xF000F0 : packedLight, appearance.color());
        poseStack.popPose();
    }

    private static float lifecycleScale(SwordProjectile entity, SwordProjectileAppearance.Lifecycle lifecycle, float partialTick) {
        float age = entity.tickCount + partialTick;
        if (age < 10.0F) return age * 0.1F;
        if (lifecycle == SwordProjectileAppearance.Lifecycle.GROW_FADE)
            return Math.max(1.0F - (age - 10.0F) / Math.max(entity.getLifetime() - 10.0F, 1.0F), 0.0F);
        return 1.0F;
    }

    private static RenderType renderType(SwordProjectileAppearance.Model appearance, float age) {
        if (appearance.material() == SwordProjectileAppearance.Material.ENERGY_SWIRL) {
            return RenderType.energySwirl(appearance.texture(), (float) Math.sin(age * 0.1F), (float) Math.sin(age * 0.2F));
        }
        return RenderType.entityCutoutNoCull(appearance.texture());
    }

    private static void orientAlongMotion(SwordProjectile entity, float partialTick, float rollSpeed, PoseStack poseStack) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() <= 1.0E-10) motion = entity.getProjectileDirection();
        if (motion.lengthSqr() > 1.0E-10) {
            poseStack.mulPose(Axis.YN.rotation((float) Math.atan2(motion.z, motion.x) + Mth.HALF_PI));
            poseStack.mulPose(Axis.XN.rotation(-(float) Math.atan2(motion.y, motion.horizontalDistance())));
        }
        if (rollSpeed != 0.0F)
            poseStack.mulPose(Axis.ZN.rotation((entity.tickCount + partialTick) * rollSpeed));
    }

    private static void renderPlane(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, int color) {
        vertex(consumer, pose, packedLight, color, 0.0F, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, packedLight, color, 1.0F, 0.0F, 1.0F, 1.0F);
        vertex(consumer, pose, packedLight, color, 1.0F, 1.0F, 1.0F, 0.0F);
        vertex(consumer, pose, packedLight, color, 0.0F, 1.0F, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, int color, float x, float y, float u, float v) {
        consumer.vertex(pose.pose(), x - 0.5F, y - 0.5F, 0.0F).color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void renderNightEdgeTrail(NightEdgeProjectile entity, float age, PoseStack poseStack,
                                             MultiBufferSource bufferSource, int argb, float width, float yOffset) {
        List<Vec3> points = new ArrayList<>();
        float start = Math.max(0.0F, age - 5.0F);
        for (float time = start; time < age; time += 0.5F)
            points.add(rotateForOwner(entity, NightEdgeProjectile.sampleLocalPoint(time)).add(0.0, yOffset, 0.0));
        points.add(rotateForOwner(entity, NightEdgeProjectile.sampleLocalPoint(age)).add(0.0, yOffset, 0.0));
        if (points.size() < 2) return;
        VertexConsumer consumer = bufferSource.getBuffer(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        Matrix4f matrix = poseStack.last().pose();
        for (int index = 1; index < points.size(); index++) {
            Vec3 previous = points.get(index - 1);
            Vec3 current = points.get(index);
            Vec3 segment = current.subtract(previous);
            if (segment.lengthSqr() <= 1.0E-10) continue;
            float progress = index / (float) (points.size() - 1);
            Vec3 side = segment.normalize().cross(new Vec3(0.0, 1.0, 0.0));
            if (side.lengthSqr() <= 1.0E-10) side = new Vec3(1.0, 0.0, 0.0);
            side = side.normalize().scale(width * progress);
            int color = (argb & 0x00FFFFFF) | Math.round(((argb >>> 24) & 0xFF) * progress) << 24;
            trailVertex(consumer, matrix, previous.add(side), color);
            trailVertex(consumer, matrix, previous.subtract(side), color);
            trailVertex(consumer, matrix, current.subtract(side), color);
            trailVertex(consumer, matrix, current.add(side), color);
        }
    }

    private static Vec3 rotateForOwner(NightEdgeProjectile entity, Vec3 point) {
        Entity owner = entity.getOwner();
        return NightEdgeProjectile.rotateLocalPoint(owner == null ? entity.getYRot() : owner.getYRot(), point);
    }

    private static void trailVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 point, int argb) {
        consumer.vertex(matrix, (float) point.x, (float) point.y, (float) point.z).color(argb).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity.getProjectileComponent() == null) return TextureAtlas.LOCATION_BLOCKS;
        SwordProjectileAppearance appearance = entity.getProjectileComponent().appearance();
        if (appearance instanceof SwordProjectileAppearance.Geo geo) return geo.texture();
        if (appearance instanceof SwordProjectileAppearance.Model model) return model.texture();
        if (appearance instanceof SwordProjectileAppearance.Cross cross) return cross.texture();
        return TextureAtlas.LOCATION_BLOCKS;
    }

    private static final class GeoRenderer extends GeoEntityRenderer<GeoSwordProjectile> {
        private GeoRenderer(EntityRendererProvider.Context context) {
            super(context, new SwordProjectileGeoModel());
            shadowRadius = 0.0F;
        }

        @Override
        public RenderType getRenderType(GeoSwordProjectile entity, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
            SwordProjectileAppearance.Geo appearance = (SwordProjectileAppearance.Geo) entity.getProjectileComponent().appearance();
            if (appearance.material() == SwordProjectileAppearance.Material.ENERGY_SWIRL) {
                float age = entity.tickCount + partialTick;
                return RenderType.energySwirl(texture, (float) Math.sin(age * 0.1F), (float) Math.sin(age * 0.2F));
            }
            return RenderType.entityCutoutNoCull(texture);
        }

        @Override
        public void preRender(PoseStack poseStack, GeoSwordProjectile entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
                              boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            SwordProjectileAppearance.Geo appearance = (SwordProjectileAppearance.Geo) entity.getProjectileComponent().appearance();
            float scale = appearance.scale() * lifecycleScale(entity, appearance.lifecycle(), partialTick);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0.0F, appearance.offsetY(), 0.0F);
            orientAlongMotion(entity, partialTick, appearance.rollSpeed(), poseStack);
            super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
