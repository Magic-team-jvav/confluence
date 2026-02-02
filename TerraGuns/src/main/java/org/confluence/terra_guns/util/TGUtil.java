package org.confluence.terra_guns.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.client.renderer.item.SimpleGeoItemRenderer;
import org.confluence.terra_guns.common.item.gun.BaseGun;
import software.bernie.geckolib.model.DefaultedItemGeoModel;


public class TGUtil {
    public static void registerOtherGunModel(RegisterClientExtensionsEvent event, String modid, DeferredHolder<Item, ? extends Item> gunSupplier) {
        registerGunModel(event, ResourceLocation.fromNamespaceAndPath(modid, "gun/" + gunSupplier.getId().getPath()), gunSupplier);
    }

    public static void registerGunModel(RegisterClientExtensionsEvent event, DeferredHolder<Item, ? extends Item> gunSupplier) {
        registerGunModel(event, ResourceLocation.fromNamespaceAndPath(TerraGuns.MODID, "gun/" + gunSupplier.getId().getPath()), gunSupplier);
    }

    public static void registerGunModel(RegisterClientExtensionsEvent event, ResourceLocation resourceLocation, DeferredHolder<Item, ? extends Item> gunSupplier) {
        event.registerItem(new SimpleGeoItemRenderer<BaseGun>(new DefaultedItemGeoModel<>(resourceLocation)), gunSupplier.get());
    }

    public static float criticalDamageTotal(float critical, float damage, RandomSource random) {
        return LibUtils.checkChance(critical, random) ? damage * 1.5F : damage;
    }
}
