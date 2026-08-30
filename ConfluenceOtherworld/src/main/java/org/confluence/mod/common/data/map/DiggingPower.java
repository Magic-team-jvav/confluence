package org.confluence.mod.common.data.map;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.GetCustomDiggingPowerEvent;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;

import java.util.List;

/// 一般用于镐子，也可用于锤子
public record DiggingPower(int power) {
    public static final Codec<DiggingPower> CODEC = ExtraCodecs.POSITIVE_INT.xmap(DiggingPower::new, DiggingPower::power);

    public static int getPower(ItemStack stack) {
        return getPower(stack, stack.getItemHolder());
    }

    public static int getPower(ItemStack stack, Holder<Item> holder) {
        int power = -1;
        DiggingPower diggingPower = holder.getData(ModDataMaps.DIGGING_POWER);
        if (diggingPower == null) {
            if (stack.getItem() instanceof TieredItem tieredItem) {
                Tier tier = tieredItem.getTier();
                if (tier instanceof ModTiers.PoweredTier poweredTier) {
                    power = poweredTier.getPower();
                } else if (tier instanceof Tiers tiers) {
                    power = ModTiers.getPowerForVanillaTiers(tiers);
                }
            }
        } else {
            power = diggingPower.power;
        }
        return NeoForge.EVENT_BUS.post(new GetCustomDiggingPowerEvent(stack, power)).getPower();
    }

    public static void addTooltip(ItemStack stack, Holder<Item> holder, List<Component> toolTip) {
        int power = getPower(stack, holder);
        if (power <= 0) return;
        if (stack.is(ItemTags.PICKAXES) || stack.is(ModTags.Items.TOOLS_DRILL)) {
            toolTip.add(Component.translatable("tooltip.confluence.pickaxe_power", power).withStyle(ChatFormatting.GRAY));
        } else if (stack.is(ModTags.Items.TOOLS_HAMMER)) {
            toolTip.add(Component.translatable("tooltip.confluence.hammer_power", power).withStyle(ChatFormatting.GRAY));
        }
    }
}
