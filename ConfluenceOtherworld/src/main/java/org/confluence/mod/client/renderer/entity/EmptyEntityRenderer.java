package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.EmptyEntity;

public class EmptyEntityRenderer extends EntityRenderer<EmptyEntity> {
	public EmptyEntityRenderer(final EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(final EmptyEntity p_entity, final float entityYaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
		super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(final EmptyEntity entity) {
		return null;
	}
}
