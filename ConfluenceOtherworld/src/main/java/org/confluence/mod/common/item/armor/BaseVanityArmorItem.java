package org.confluence.mod.common.item.armor;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.NormalArmorItemRenderer;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collections;
import java.util.function.Consumer;

public class BaseVanityArmorItem extends BaseArmorItem implements GeoItem {
    protected static final String DEFAULT_NAME = "vanity_armor/dead_mans_seater";
    protected final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    protected final String name;

    public BaseVanityArmorItem(String name, ArmorItem.Type type, ModRarity rarity) {
        this(name, ModArmorMaterials.VANITY_ARMOR_MATERIALS, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, ModRarity rarity) {
        this(name, material, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, ModRarity rarity) {
        super(material, type, properties.stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.name = name;
        ItemAttributeModifiers modifiersFromProperties = this.components().get(DataComponents.ATTRIBUTE_MODIFIERS);
        this.defaultModifiers = Suppliers.memoize(() -> {
            if (modifiersFromProperties != null && !modifiersFromProperties.modifiers().isEmpty()) {
                return modifiersFromProperties;
            }
            return new ItemAttributeModifiers(Collections.emptyList(), false);
        });
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NormalArmorItemRenderer<BaseVanityArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (renderer == null) {
                    ResourceLocation textureResource = Confluence.asResource("textures/item/" + name + ".png");
                    if (Minecraft.getInstance().getResourceManager().getResource(textureResource).isEmpty()) {
                        this.renderer = new NormalArmorItemRenderer<>(DEFAULT_NAME);
                    } else {
                        this.renderer = new NormalArmorItemRenderer<>(name);
                    }
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}

