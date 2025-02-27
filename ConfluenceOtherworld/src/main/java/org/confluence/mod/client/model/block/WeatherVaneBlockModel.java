package org.confluence.mod.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

public class WeatherVaneBlockModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("weather_vane"), "main");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/weather_vane_top.png");
    private final ModelPart bone;

    public WeatherVaneBlockModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(42, 0).addBox(6.5F, -2.0F, 0.5F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(8.0F, -10.0F, -8.5F, 0.0F, 21.0F, 21.0F, CubeDeformation.NONE)
                .texOffs(0, 42).addBox(5.5F, 6.5F, -8.5F, 5.0F, 0.0F, 21.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 10.0F, 10.0F, 1.5708F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        bone.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), packedLight, packedOverlay);
    }
}