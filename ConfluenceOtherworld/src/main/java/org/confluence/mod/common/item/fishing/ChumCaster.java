package org.confluence.mod.common.item.fishing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.fishing.BloodyFishingHook;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

public class ChumCaster extends AbstractFishingPole {
    public static final ResourceLocation ID = Confluence.asResource("chum_caster");

    public ChumCaster() {
        super(new Properties().unbreakable(), ModRarity.GREEN);
        addAttributeModifiers(builder -> builder.add(Attributes.LUCK, new PortAttributeModifier(ID, 0.25, PortAttributeModifier.PortOperation.ADD_MULTIPLIED_TOTAL), PortEquipmentSlotGroup.MAINHAND));
    }

    @Override
    public FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new BloodyFishingHook(player, level, luckBonus, speedBonus);
    }
}
