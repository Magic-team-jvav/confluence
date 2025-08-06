package org.confluence.mod.client.renderer.item;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.util.ClientUtils;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

import java.util.OptionalInt;

import static org.confluence.lib.util.LibUtils.getSlotIndex;

public class NormalArmorItemRenderer<T extends Item & GeoItem> extends DyeableGeoArmorRenderer<T> {
    public NormalArmorItemRenderer(String path) {
        super(new DefaultedItemGeoModel<>(Confluence.asResource(path)));
    }

    @Override
    protected boolean isBoneDyeable(GeoBone bone) {
        return true;
    }

    @Override
    protected Color getColorForBone(GeoBone bone) {
        if (currentSlot != null && currentEntity instanceof AbstractClientPlayer player) {
            OptionalInt color = ClientUtils.getVanityDyeColor(ExtraInventory.of(player), getSlotIndex(currentSlot), player);
            if (color.isPresent()) return new Color(color.getAsInt());
        }
        return Color.WHITE;
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        ResourceLocation original = super.getTextureLocation(animatable);
        if (currentSlot != null && currentEntity instanceof AbstractClientPlayer player) {
            if (!ExtraInventory.of(player).getVanityArmorDye(getSlotIndex(currentSlot)).isEmpty()) {
                return ClientUtils.getGrayTexture(original);
            }
        }
        return original;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
