package org.confluence.mod.common.item.common;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.SizedTextureComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Optional;
import java.util.function.Consumer;

public class BasePickaxeItem extends PickaxeItem {
    private @Nullable Vector2i imageSize;
    private @Nullable TooltipComponent component;

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, ModRarity rarity) {
        this(tier, rawDamage, rawSpeed, new Properties(), rarity);
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).attributes(createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1.0F, rawSpeed - 4.0F)));
    }

    public BasePickaxeItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity).attributes(ModItems.createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1.0F, rawSpeed - 4.0F, consumer)));
    }

    @ApiStatus.Internal
    public BasePickaxeItem image(int width, int height) {
        this.imageSize = new Vector2i(width, height);
        return this;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && imageSize != null) {
            this.component = SizedTextureComponent.of(imageSize.x, imageSize.y, stack.getItem(), "pickaxe");
        }
        return Optional.ofNullable(component);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }
}
