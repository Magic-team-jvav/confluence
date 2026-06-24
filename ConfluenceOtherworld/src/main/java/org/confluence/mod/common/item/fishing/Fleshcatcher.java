package org.confluence.mod.common.item.fishing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class Fleshcatcher extends AbstractFishingPole {
    public static final ResourceLocation ID = Confluence.asResource("fleshcatcher");

    public Fleshcatcher() {
        super(new Properties().unbreakable(), ModRarity.BLUE);
        addAttributeModifiers(builder -> builder.add(Attributes.LUCK, new PortAttributeModifier(ID, 0.22, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL), PortEquipmentSlotGroup.MAINHAND));
    }

    @Override
    public FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new BaseFishingHook(player, level, luckBonus, speedBonus, BaseFishingHook.Variant.FLESHCATCHER);
    }
}
