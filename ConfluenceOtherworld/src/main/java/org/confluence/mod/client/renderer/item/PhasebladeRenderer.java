package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.model.item.PhasebladeModel;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.item.sword.Phasesaber;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PhasebladeRenderer extends GeoItemRenderer<BasePhasebladeItem> {
    private static final ResourceLocation LIGHTNING = Confluence.asResource("textures/particle/phaseblade/phaseblade_lightling.png");
    private final boolean forceBladeOn;
    private boolean bladeOn;

    public PhasebladeRenderer() {
        this(false);
    }

    public PhasebladeRenderer(boolean forceBladeOn) {
        super(new PhasebladeModel(forceBladeOn));
        this.forceBladeOn = forceBladeOn;
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected RenderType getRenderType(BasePhasebladeItem item) {
                return RenderStateShardAccessor.EYES.apply(item.emissiveResource(), RenderType.TRANSLUCENT_TRANSPARENCY);
            }

            @Override
            public void render(PoseStack poseStack, BasePhasebladeItem item, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, float partialTick,
                               int packedLight, int packedOverlay) {
                if (bladeOn)
                    super.render(poseStack, item, model, renderType, buffers, buffer, partialTick, packedLight, packedOverlay);
            }
        });
        addRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void render(PoseStack poses, BasePhasebladeItem item, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
                renderLightning(poses, item, buffers, partialTick, overlay);
            }
        });
    }

    private void renderLightning(PoseStack poses, BasePhasebladeItem item, MultiBufferSource buffers, float partialTick, int overlay) {
        if (forceBladeOn || !bladeOn || Minecraft.getInstance().level == null) return;
        if (renderPerspective != ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                && renderPerspective != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && renderPerspective != ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                && renderPerspective != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) return;
        double time = BladeItemVisualState.time(partialTick);
        int frame = BladeItemVisualState.get(getCurrentItemStack(), time).lightningFrame(time);
        if (frame < 0) return;
        var geometry = item.projectileGeometry();
        float centerY = geometry.maxY() - geometry.bladeLength() * 0.5F;
        float radius = geometry.bladeLength() * 0.6F;
        float u = geometry.centerX();
        float z = geometry.maxZ() + 0.02F;
        float v0 = frame / 7.0F;
        float v1 = (frame + 1) / 7.0F;
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityTranslucentEmissive(LIGHTNING));
        var pose = poses.last();
        vertices.vertex(pose.pose(), u - radius, centerY - radius, z).color(255, 255, 255, 255).uv(0, v1).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), u + radius, centerY - radius, z).color(255, 255, 255, 255).uv(1, v1).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), u + radius, centerY + radius, z).color(255, 255, 255, 255).uv(1, v0).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), u - radius, centerY + radius, z).color(255, 255, 255, 255).uv(0, v0).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BasePhasebladeItem item, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource buffers, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        bladeOn = forceBladeOn || BasePhasebladeItem.isTurnOn(getCurrentItemStack());
        if (!forceBladeOn)
            BladeItemVisualState.get(getCurrentItemStack(), BladeItemVisualState.time(partialTick));
        super.actuallyRender(poseStack, item, model, renderType, buffers, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @Nullable RenderType getRenderType(BasePhasebladeItem item, ResourceLocation texture, @Nullable MultiBufferSource buffers, float partialTick) {
        return RenderType.text(texture);
    }

    @Override
    protected void renderInGui(ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (forceBladeOn) {
            super.renderInGui(context, poses, buffers, light, overlay);
            return;
        }
        double time = BladeItemVisualState.time(Minecraft.getInstance().getFrameTime());
        double extension = BladeItemVisualState.get(getCurrentItemStack(), time).extension(time);
        String suffix = extension <= 0 ? "inactive" : extension >= 1 ? "item" : "activation";
        String family = animatable instanceof Phasesaber ? "phasesaber" : "phaseblade";
        ResourceLocation texture = Confluence.asResource("textures/item/" + family + "/" + animatable.color().resourceName() + "_" + family + "_" + suffix + ".png");
        int frame = Math.min(6, (int) (extension * 7));
        float v0 = suffix.equals("activation") ? frame / 7.0F : 0;
        float v1 = suffix.equals("activation") ? (frame + 1) / 7.0F : 1;
        var pose = poses.last();
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityTranslucentEmissive(texture));
        vertices.vertex(pose.pose(), 0, 0, 0.5F).color(255, 255, 255, 255).uv(0, v1).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), 1, 0, 0.5F).color(255, 255, 255, 255).uv(1, v1).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), 1, 1, 0.5F).color(255, 255, 255, 255).uv(1, v0).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
        vertices.vertex(pose.pose(), 0, 1, 0.5F).color(255, 255, 255, 255).uv(0, v0).overlayCoords(overlay).uv2(15728880).normal(pose.normal(), 0, 0, 1).endVertex();
    }
}
