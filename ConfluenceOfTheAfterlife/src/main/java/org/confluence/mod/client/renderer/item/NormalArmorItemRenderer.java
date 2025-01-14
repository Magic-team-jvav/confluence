package org.confluence.mod.client.renderer.item;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.armor.NormalArmorItem;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.confluence.mod.util.ClientUtils;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

import static org.confluence.mod.util.ModUtils.getSlotIndex;

public class NormalArmorItemRenderer extends DyeableGeoArmorRenderer<NormalArmorItem> {
    private boolean isDyeColor = false;

    public NormalArmorItemRenderer(String path) {
        super(new DefaultedItemGeoModel<>(Confluence.asResource(path)));
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return true;
    }

    @Override
    protected Color getColorForBone(GeoBone bone) {
        if (currentSlot != null && currentEntity instanceof AbstractClientPlayer) {
            int index = ModUtils.getSlotIndex(currentSlot);
            if (index != -1) {
                ItemStack vanityArmorDye = currentEntity.getData(ModAttachmentTypes.EXTRA_INVENTORY).getVanityArmorDye(index);
                if (!vanityArmorDye.isEmpty() && vanityArmorDye.getItem() instanceof BaseDyeItem dyeItem) {
                    this.isDyeColor = true;
                    return dyeItem.colour;
                }
            }
        }
        this.isDyeColor = false;
        return Color.WHITE;
    }

    @Override
    public ResourceLocation getTextureLocation(NormalArmorItem animatable) {
        ResourceLocation original = super.getTextureLocation(animatable);
        if (currentSlot != null && currentEntity instanceof AbstractClientPlayer) {
            if (isDyeColor || !currentEntity.getData(ModAttachmentTypes.EXTRA_INVENTORY).getVanityArmorDye(getSlotIndex(currentSlot)).isEmpty()) {
                return ClientUtils.getGrayTexture(original);
            }
        }
        return original;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
