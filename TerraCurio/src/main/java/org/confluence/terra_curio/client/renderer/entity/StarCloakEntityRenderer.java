package org.confluence.terra_curio.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.common.entity.StarCloakEntity;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.NotNull;

public class StarCloakEntityRenderer extends EntityRenderer<StarCloakEntity> {
    private final ItemRenderer itemRenderer;
    private final ItemStack item;

    public StarCloakEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
        this.item = TCItems.STAR.get().getDefaultInstance();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StarCloakEntity pEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    protected int getBlockLightLevel(@NotNull StarCloakEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public void render(StarCloakEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        itemRenderer.renderStatic(item, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
}
