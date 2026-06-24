package org.confluence.mod.common.item.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.NormalArmorItemRenderer;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class NormalGeoArmorItem extends BaseArmorItem implements GeoItem {
    protected final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    protected final String name;

    public NormalGeoArmorItem(String name, ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
        this.name = name;
    }

    public NormalGeoArmorItem(String name, ModRarity rarity, ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.name = name;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private NormalArmorItemRenderer<NormalGeoArmorItem> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null) {
                    ResourceLocation textureResource = Confluence.asResource("textures/item/" + name + ".png");
                    if (Minecraft.getInstance().getResourceManager().getResource(textureResource).isEmpty()) {
                        this.renderer = new NormalArmorItemRenderer<>("armor/cactus_armor"); // as a fallback
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
        return CACHE;
    }
}
