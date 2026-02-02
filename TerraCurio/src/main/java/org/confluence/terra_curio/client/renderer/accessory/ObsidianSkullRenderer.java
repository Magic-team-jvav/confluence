package org.confluence.terra_curio.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.model.accessory.ObsidianSkullModel;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ObsidianSkullRenderer implements ICurioRenderer {
    private static final RenderType CUTOUT = RenderType.entityCutout(TerraCurio.asResource("textures/curio/obsidian_skull.png"));

    private final ObsidianSkullModel model;

    public ObsidianSkullRenderer(EntityModelSet entityModels) {
        this.model = new ObsidianSkullModel(entityModels.bakeLayer(ObsidianSkullModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ICurioRenderer.followHeadRotations(slotContext.entity(), model.head);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(CUTOUT), light, OverlayTexture.NO_OVERLAY);
    }
}
