package org.confluence.mod.client.renderer.item;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.item.armor.NormalArmorItem;
import org.confluence.mod.common.item.vanity_armor.BaseDyeItem;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class NormalArmorItemRenderer extends DyeableGeoArmorRenderer<NormalArmorItem> {
    public NormalArmorItemRenderer(String path) {
        super(new DefaultedItemGeoModel<>(Confluence.asResource(path)));
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return true;
    }

    @Override
    protected Color getColorForBone(GeoBone bone) {
        if (currentSlot != null && currentEntity != null && AbstractClientPlayer.class.isAssignableFrom(currentEntity.getClass())) {
            ItemStack vanityArmorDye = currentEntity.getData(ModAttachmentTypes.EXTRA_INVENTORY).getVanityArmorDye(ModUtils.getSlotIndex(currentSlot));
            return !vanityArmorDye.isEmpty() && vanityArmorDye.getItem() instanceof BaseDyeItem dyeItem ? dyeItem.colour : Color.WHITE;
        }
        return Color.WHITE;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
