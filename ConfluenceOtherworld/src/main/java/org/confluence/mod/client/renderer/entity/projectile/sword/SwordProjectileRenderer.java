package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
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

import java.util.HashMap;
import java.util.Map;

/// 按剑气组件选择 Geo、实体模型、物品或交叉面片表现。
public final class SwordProjectileRenderer<T extends SwordProjectile> extends EntityRenderer<T> {
    private final Map<ResourceLocation, EntityModel<SwordProjectile>> models = new HashMap<>();
    private final GeoRenderer geoRenderer;

    public SwordProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        models.put(Confluence.asResource("ice_blade_sword_projectile"), new IceBladeSwordProjectileModel(context.bakeLayer(IceBladeSwordProjectileModel.LAYER_LOCATION)));
        models.put(Confluence.asResource("enchanted_sword_projectile"), new EnchantedSwordProjectileModel(context.bakeLayer(EnchantedSwordProjectileModel.LAYER_LOCATION)));
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

    private void renderModel(T entity, SwordProjectileAppearance.Model appearance, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        EntityModel<SwordProjectile> model = models.get(appearance.model());
        if (model == null) return;
        poseStack.pushPose();
        float scale = appearance.scale() * lifecycleScale(entity, appearance.lifecycle(), partialTick);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, appearance.offsetY(), appearance.offsetZ());
        orientAlongMotion(entity, partialTick, appearance.rollSpeed(), poseStack);
        model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, entity.getYRot(), entity.getXRot());
        model.renderToBuffer(poseStack,
                bufferSource.getBuffer(renderType(appearance, entity.tickCount + partialTick)), packedLight,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private void renderItem(T entity, SwordProjectileAppearance.Item appearance, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        boolean nightEdgeEffect = appearance.effect().filter(Confluence.asResource("nights_edge")::equals).isPresent() && entity instanceof NightEdgeProjectile;
        if (nightEdgeEffect) {
            renderNightEdge((NightEdgeProjectile) entity, partialTick, poseStack, bufferSource, packedLight);
            return;
        }
        ItemStack weapon = entity.getWeaponItem();
        if (weapon == null || weapon.isEmpty()) return;
        poseStack.pushPose();
        poseStack.scale(appearance.scale(), appearance.scale(), appearance.scale());
        if (appearance.transform() == SwordProjectileAppearance.Transform.OWNER_SWING && entity instanceof NightEdgeProjectile nightEdge) {
            float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
            Vec3 point = nightEdgeRelativePosition(nightEdge, age);
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
        Minecraft.getInstance().getItemRenderer().renderStatic(weapon, ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), entity.getId());
        poseStack.popPose();
    }

    private static void renderNightEdge(NightEdgeProjectile entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Entity owner = entity.getOwner();
        float yaw = -(owner == null ? entity.getYRot() : Mth.lerp(partialTick, owner.yRotO, owner.getYRot())) + 70.0F;
        Vec3 entityPosition = owner == null ? Vec3.ZERO : new Vec3(
                Mth.lerp(partialTick, entity.xo - owner.xo, entity.getX() - owner.getX()),
                Mth.lerp(partialTick, entity.yo - owner.yo, entity.getY() - owner.getY()),
                Mth.lerp(partialTick, entity.zo - owner.zo, entity.getZ() - owner.getZ()));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        renderNightEdgeTrail(entity, entityPosition, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-entityPosition.x, -entityPosition.y, -entityPosition.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.scale(1.0F, 1.0F, -1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.translate(entityPosition.x, entityPosition.y, entityPosition.z);
        poseStack.translate(0.0F, 0.1F, 0.0F);
        poseStack.scale(0.8F, 0.8F, 0.8F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(25.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw - 180.0F));
        renderNightEdgeTrail(entity, entityPosition, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private void renderCross(T entity, SwordProjectileAppearance.Cross appearance, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float age = entity.tickCount + partialTick;
        float scale = Math.min(age * 0.2F, 1.0F) * appearance.scale();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * appearance.spinSpeed()));
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(appearance.texture()));
        int light = appearance.blockLight() < 0 ? packedLight : LightTexture.pack(appearance.blockLight(), LightTexture.sky(packedLight));
        renderPlane(consumer, poseStack.last(), light, appearance.color());
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderPlane(consumer, poseStack.last(), light, appearance.color());
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

    private static void renderNightEdgeTrail(NightEdgeProjectile entity, Vec3 entityPosition, float partialTick,
                                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!(entity.getOwner() instanceof net.minecraft.world.entity.LivingEntity owner)) return;
        float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
        int pointCount = Mth.floor(age * 2.0F) + 1;
        if (pointCount < 2) return;
        VertexConsumer consumer = bufferSource.getBuffer(RenderStateShardAccessor.ENTITY_TRANSLUCENT_EMISSIVE);
        Matrix4f matrix = poseStack.last().pose();
        Vec3 previousLeft = null;
        Vec3 previousRight = null;
        int previousColor = 0x00FFFFFF;
        float previousProgress = 0.0F;
        Vec3 previous = NightEdgeProjectile.worldPosition(owner, 0.0F).subtract(owner.position()).subtract(entityPosition);
        for (int index = 1; index < pointCount; index++) {
            float time = Math.min(index * 0.5F, age);
            Vec3 current = NightEdgeProjectile.worldPosition(owner, time).subtract(owner.position()).subtract(entityPosition);
            float progress = (index - 1) / (float) pointCount;
            org.joml.Vector3f normal = new org.joml.Vector3f(0.0F, 0.0F, 1.0F);
            float xRot = owner.getXRot() * 0.8F * Mth.DEG_TO_RAD;
            float yRot = (NightEdgeProjectile.sampleYaw(time) + owner.getYRot()) * Mth.DEG_TO_RAD;
            new org.joml.Quaternionf().rotateY(-yRot).rotateX(xRot).transform(normal);
            Vec3 side = new Vec3(normal.normalize()).scale(1.5F * progress);
            Vec3 left = current.add(side);
            Vec3 right = current.subtract(side);
            if (previousLeft == null) {
                previousLeft = previous.add(side);
                previousRight = previous.subtract(side);
            }
            int alpha = index == pointCount - 1 ? 20 : Math.round(200.0F * progress);
            int color = 0x00FFFFFF | alpha << 24;
            trailVertex(consumer, poseStack.last(), matrix, previousLeft, previousColor, 0.0F, previousProgress, packedLight);
            trailVertex(consumer, poseStack.last(), matrix, previousRight, previousColor, 1.0F, previousProgress, packedLight);
            trailVertex(consumer, poseStack.last(), matrix, right, color, 1.0F, progress, packedLight);
            trailVertex(consumer, poseStack.last(), matrix, left, color, 0.0F, progress, packedLight);
            previousLeft = left;
            previousRight = right;
            previousColor = color;
            previousProgress = progress;
        }
    }

    private static Vec3 nightEdgeRelativePosition(NightEdgeProjectile entity, float time) {
        Entity owner = entity.getOwner();
        if (!(owner instanceof net.minecraft.world.entity.LivingEntity living)) return Vec3.ZERO;
        return NightEdgeProjectile.worldPosition(living, time).subtract(entity.position());
    }

    private static void trailVertex(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f matrix, Vec3 point, int argb,
                                    float u, float v, int packedLight) {
        consumer.vertex(matrix, (float) point.x, (float) point.y, (float) point.z).color(argb).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
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
