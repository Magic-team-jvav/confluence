package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.GeoNormalModel;
import org.confluence.mod.common.entity.boss.DukeFishron;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class FishronGeoRenderer<T extends Entity & GeoEntity> extends BossGeoRenderer<T> {
    private final ResourceLocation modelFile;
    private final ResourceLocation animationFile;
    private final MissingModelRenderer<T> missingModel;

    public FishronGeoRenderer(EntityRendererProvider.Context context, String name) {
        this(context, name, new GeoNormalModel<>(Confluence.asResource("boss/" + name)));
    }

    public FishronGeoRenderer(EntityRendererProvider.Context context, String name, GeoModel<T> model) {
        super(context, model, true, 1.0F, 0.0F);
        modelFile = Confluence.asResource("geo/entity/boss/" + name + ".geo.json");
        animationFile = Confluence.asResource("animations/entity/boss/" + name + ".animation.json");
        missingModel = new MissingModelRenderer<>(context);
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack stack, MultiBufferSource buffers, int light) {
        if (entity instanceof DukeFishron duke && duke.isSummonSubmerged()) return;
        var resources = Minecraft.getInstance().getResourceManager();
        if (resources.getResource(modelFile).isEmpty() || resources.getResource(animationFile).isEmpty()) {
            missingModel.render(entity, yaw, partialTick, stack, buffers, light);
            return;
        }
        super.render(entity, yaw, partialTick, stack, buffers, light);
    }

    @Override
    protected void adjustPose(PoseStack stack, T entity, BakedGeoModel model, float partialTick) {
        if (!(entity instanceof DukeFishron duke)) return;
        float flip = duke.flipDegrees(partialTick);
        if (flip == 0) return;
        float pivot = duke.getBbHeight() * 0.5F / duke.getScale();
        double yaw = Math.toRadians(getRenderYaw(entity, partialTick));
        stack.translate(0, pivot, 0);
        stack.mulPose(Axis.of(new Vector3f((float) Math.cos(yaw), 0, (float) Math.sin(yaw))).rotationDegrees(flip));
        stack.translate(0, -pivot, 0);
    }
}
