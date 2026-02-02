package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.entity.monster.BaseWormPart;
import org.confluence.terraentity.entity.monster.Wyvern;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class WyvernRenderer<T extends Wyvern<S>, S extends BaseWormPart> extends GeoWormRenderer<T, S>  {

    boolean initialized = false;
    GeoBone bone;
    GeoBone bone2;
    GeoBone bone3;
    GeoBone bone4;
    public WyvernRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        super(renderManager, path);
    }

    protected WyvernBodyRenderer createPartRenderer(EntityRendererProvider.Context renderManager, ResourceLocation path) {
        return new WyvernBodyRenderer<>(renderManager,this, path, scale, offsetY);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if(!initialized){
            this.bone = this.model.getBone("Bone").get();
            this.bone2 = this.model.getBone("Bone2").get();
            this.bone3 = this.model.getBone("Bone3").get();
            this.bone4 = this.model.getBone("Bone4").get();
            initialized = true;
        }
        this.bone.setHidden(false);
        this.bone2.setHidden(true);
        this.bone3.setHidden(true);
        this.bone4.setHidden(true);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
