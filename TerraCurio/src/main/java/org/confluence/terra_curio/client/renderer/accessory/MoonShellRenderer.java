package org.confluence.terra_curio.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.terra_curio.client.model.accessory.MermanModel;
import org.confluence.terra_curio.client.model.accessory.WerewolfModel;
import org.confluence.terra_curio.mixed.IClientLivingEntity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MoonShellRenderer implements ICurioRenderer {
    private final MermanModel mermanModel;
    private final WerewolfModel werewolfModel;

    public MoonShellRenderer(EntityModelSet entityModels) {
        this.mermanModel = new MermanModel(entityModels.bakeLayer(MermanModel.LAYER_LOCATION));
        this.werewolfModel = new WerewolfModel(entityModels.bakeLayer(WerewolfModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity living = slotContext.entity();
        if (living.isInWaterOrBubble()) {
            ICurioRenderer.followBodyRotations(living, mermanModel);
            ICurioRenderer.followHeadRotations(living, mermanModel.head);
            mermanModel.renderToBuffer(poseStack, multiBufferSource.getBuffer(MermanRenderer.CUTOUT), light, OverlayTexture.NO_OVERLAY);
            ((IClientLivingEntity) living).terra_curio$setShowingCosmetic(true);
        } else if (LibDateUtils.isNight(living.level())) {
            ICurioRenderer.followBodyRotations(living, werewolfModel);
            ICurioRenderer.followHeadRotations(living, werewolfModel.head);
            werewolfModel.renderToBuffer(poseStack, multiBufferSource.getBuffer(WerewolfRenderer.CUTOUT), light, OverlayTexture.NO_OVERLAY);
            ((IClientLivingEntity) living).terra_curio$setShowingCosmetic(true);
        }
    }
}
