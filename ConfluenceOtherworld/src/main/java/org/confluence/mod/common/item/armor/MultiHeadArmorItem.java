package org.confluence.mod.common.item.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.MultiHeadArmorItemRenderer;
import org.confluence.mod.client.renderer.item.NormalArmorItemRenderer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MultiHeadArmorItem extends NormalArmorItem {
    private static final Map<String, MultiHeadArmorItem[]> headMap = new HashMap<>();

    public MultiHeadArmorItem(String name, ModRarity rarity, Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(name, rarity, material, type, properties);
        if (type == Type.HELMET && LibUtils.isPhysicalClient()) {
            MultiHeadArmorItem[] original = headMap.computeIfAbsent(name, k -> new MultiHeadArmorItem[3]);
            int length = original.length;
            for (int i = 0; i < length; i++) {
                if (original[i] == null) {
                    original[i] = this;
                    break;
                } else if (i == length - 1) {
                    MultiHeadArmorItem[] copy = new MultiHeadArmorItem[length + 1];
                    System.arraycopy(original, 0, copy, 0, length);
                    copy[length] = this;
                    headMap.put(name, copy);
                    break;
                }
            }
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NormalArmorItemRenderer<NormalArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (renderer == null) {
                    MultiHeadArmorItem[] items = headMap.get(name);
                    if (items == null || Minecraft.getInstance().getResourceManager().getResource(Confluence.asResource("textures/item/" + name + ".png")).isEmpty()) {
                        this.renderer = new NormalArmorItemRenderer<>("armor/cactus_armor"); // as a fallback
                    } else {
                        this.renderer = new MultiHeadArmorItemRenderer<>(name, items);
                    }
                }
                return renderer;
            }
        });
    }
}
