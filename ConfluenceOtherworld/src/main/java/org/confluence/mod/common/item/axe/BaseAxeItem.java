package org.confluence.mod.common.item.axe;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Optional;
import java.util.function.Consumer;

public class BaseAxeItem extends AxeItem {
    private @Nullable TooltipComponent component;
    private boolean hasImage;

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public BaseAxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<PortItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.defaultModifiers = ModItems.mergeModifiers(defaultModifiers, consumer);
    }

    public BaseAxeItem hasImage() {
        this.hasImage = true;
        return this;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && hasImage) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }
}
