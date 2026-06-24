package org.confluence.mod.common.item.armor;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.NormalArmorItemRenderer;
import org.confluence.mod.common.init.armor.ModArmorMaterials;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BaseVanityArmorItem extends BaseArmorItem implements GeoItem {
    protected static final String DEFAULT_NAME = "vanity_armor/dead_mans_seater";
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final String name;

    public BaseVanityArmorItem(String name, ArmorItem.Type type, ModRarity rarity) {
        this(name, ModArmorMaterials.VANITY_ARMOR_MATERIALS.get(), type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(String name, ArmorMaterial material, ArmorItem.Type type, ModRarity rarity) {
        this(name, material, type, new Properties(), rarity);
    }

    public BaseVanityArmorItem(String name, ArmorMaterial material, ArmorItem.Type type, Properties properties, ModRarity rarity) {
        super(material, type, properties.stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.name = name;
        PortItemAttributeModifiers attributes = properties.getAttributes();
        if (attributes != null) {
            this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                    .putAll(defaultModifiers)
                    .putAll(attributes.getAttributeModifiers(type.getSlot()))
                    .build();
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private NormalArmorItemRenderer<BaseVanityArmorItem> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
