package org.confluence.mod.common.item.fishing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.fishing.BaseFishingHook;
import org.confluence.terra_curio.common.component.ModRarity;

public class ReinforcedFishingPole extends AbstractFishingPole {
    public static final ResourceLocation ID = Confluence.asResource("reinforced_fishing_pole");

    public ReinforcedFishingPole() {
        super(new Properties().durability(128), ModRarity.WHITE);
        addAttributeModifiers(builder -> builder.add(Attributes.LUCK, new AttributeModifier(ID, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    protected FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus) {
        return new BaseFishingHook(player, level, luckBonus, speedBonus, BaseFishingHook.Variant.REINFORCED);
    }
}
