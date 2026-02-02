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
import org.confluence.lib.util.LibDateUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.client.model.accessory.WerewolfModel;
import org.confluence.terra_curio.mixed.IClientLivingEntity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class WerewolfRenderer implements ICurioRenderer {
    static final RenderType CUTOUT = RenderType.entityCutout(TerraCurio.asResource("textures/curio/werewolf.png"));

    private final WerewolfModel model;

    public WerewolfRenderer(EntityModelSet entityModels) {
        this.model = new WerewolfModel(entityModels.bakeLayer(WerewolfModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity living = slotContext.entity();
        if (LibDateUtils.isNight(living.level()) && !TCClientPacketHandler.canShowNeptunesShell(living)) {
            ICurioRenderer.followBodyRotations(living, model);
            ICurioRenderer.followHeadRotations(living, model.head);
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(CUTOUT), light, OverlayTexture.NO_OVERLAY);
            ((IClientLivingEntity) living).terra_curio$setShowingCosmetic(true);
        }
    }
}
