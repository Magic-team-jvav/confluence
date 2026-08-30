package org.confluence.mod.common.item.fishing;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.fishing.HotlineFishingHook;
import org.confluence.mod.common.init.item.ModItems;

public class HotlineFishingHookItem extends AbstractFishingPole {
    public static final ResourceLocation ID = Confluence.asResource("hotline_fishing_hook");

    public HotlineFishingHookItem() {
        super(new Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE).fireResistant(), ModRarity.ORANGE);
        addAttributeModifiers(builder -> builder.add(Attributes.LUCK, new AttributeModifier(ID, 0.45, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 4;
    }

    @Override
    public FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new HotlineFishingHook(player, level, luckBonus, speedBonus);
    }
}
