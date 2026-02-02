package org.confluence.terraentity.item;

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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.entity.renderer.GeoHumanoidArmorRenderer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BaseArmorItem extends ArmorItem implements GeoItem {
    protected final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    protected final String name;

    public BaseArmorItem(String name, Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        this.name = name;
    }

    public BaseArmorItem(String name, ModRarity rarity, Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.name = name;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        boolean supportedItem = stack.is(enchantment.value().definition().supportedItems());
        if (stack.has(DataComponents.UNBREAKABLE)) {
            return supportedItem && !enchantment.is(Enchantments.UNBREAKING) && !enchantment.is(Enchantments.MENDING);
        }
        return supportedItem;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoHumanoidArmorRenderer<BaseArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (renderer == null) {
                    ResourceLocation textureResource = TerraEntity.space("textures/item/" + name + ".png");
                    if (Minecraft.getInstance().getResourceManager().getResource(textureResource).isEmpty()) {
                        this.renderer = new GeoHumanoidArmorRenderer<>(TerraEntity.space("armor/possessed_armor"));
                    } else {
                        this.renderer = new GeoHumanoidArmorRenderer<>(TerraEntity.space(name));
                    }
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}
