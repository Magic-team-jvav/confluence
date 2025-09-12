package org.confluence.mod.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

public record SizedTextureComponent(int width, int height, ResourceLocation location) implements TooltipComponent {
    public static SizedTextureComponent of(int width, int height, Item item, String prefix, String suffix) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        String path = prefix + '/' + key.getPath() + suffix;
        return new SizedTextureComponent(width, height, ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path));
    }

    public static SizedTextureComponent of(int width, int height, Item item, String prefix) {
        return of(width, height, item, prefix, "");
    }
}
