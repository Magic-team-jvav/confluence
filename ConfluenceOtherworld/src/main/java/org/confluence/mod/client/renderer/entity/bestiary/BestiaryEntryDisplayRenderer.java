package org.confluence.mod.client.renderer.entity.bestiary;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.api.event.bestiary.RegisterCustomBestiaryEntryRendererEvent;
import org.confluence.mod.common.entity.BestiaryEntryDisplay;

public class BestiaryEntryDisplayRenderer extends LivingEntityRenderer<BestiaryEntryDisplay, EntityModel<BestiaryEntryDisplay>> {
    public BestiaryEntryDisplayRenderer(EntityRendererProvider.Context context) {
        super(context, new EntityModel<>() {
            @Override
            public void setupAnim(BestiaryEntryDisplay entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

            @Override
            public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}
        }, 0);
        RegisterCustomBestiaryEntryRendererEvent.postEvent(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BestiaryEntryDisplay entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(BestiaryEntryDisplay entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        LivingEntity delegate = entity.getDelegate();
        delegate.setXRot(entity.getXRot());
        delegate.setYRot(entity.getYRot());
        delegate.yBodyRot = entity.yBodyRot;
        delegate.yHeadRot = entity.yHeadRot;
        RegisterCustomBestiaryEntryRendererEvent.getRenderer(
                entity.getKey()).render(delegate, entityYaw, partialTick, poseStack, bufferSource, packedLight
        );
        delegate.yBodyRotO = entity.yBodyRotO;
        delegate.yHeadRotO = entity.yHeadRotO;
    }
}
