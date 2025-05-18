package org.confluence.mod.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.terra_curio.common.init.TCEffects;

import java.util.List;

public class RedPotionItem extends AbstractPotionItem {
    private final List<Holder<MobEffect>> beneficial = List.of(
            ModEffects.OBSIDIAN_SKIN,
            MobEffects.REGENERATION,
            MobEffects.MOVEMENT_SPEED,
            ModEffects.IRON_SKIN,
            ModEffects.MANA_REGENERATION,
            ModEffects.MAGIC_POWER,
            MobEffects.SLOW_FALLING,
            ModEffects.SPELUNKER,
            ModEffects.ARCHERY,
            ModEffects.HEART_REACH,
            ModEffects.HUNTER,
            ModEffects.ENDURANCE,
            ModEffects.LIFE_FORCE,
            ModEffects.INFERNO,
            MobEffects.DIG_SPEED,
            ModEffects.RAGE,
            ModEffects.WRATH,
            ModEffects.DANGER_SENSE
    );
    private final List<Holder<MobEffect>> harmful = List.of(
            MobEffects.POISON,
            MobEffects.DARKNESS,
            ModEffects.CURSED,
            ModEffects.BLEEDING,
            TCEffects.CONFUSED,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.WEAKNESS,
            ModEffects.SILENCED,
            ModEffects.BROKEN_ARMOR,
            ModEffects.CHOKING
    );

    public RedPotionItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED));
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (ModSecretSeeds.FOR_THE_WORTHY.match(serverLevel)) {
            for (int i = 0; i < 3; i++) {
                living.addEffect(new MobEffectInstance(Util.getRandom(beneficial, living.getRandom()), 30 * 60 * 20));
            }
        } else {
            int duration = LibUtils.switchByDifficulty(level, living.blockPosition(), 60, 120, 180) * 60 * 20;
            if (living.getRandom().nextFloat() < 1.0F / 11.0F) {
                living.setRemainingFireTicks(duration);
            } else {
                living.addEffect(new MobEffectInstance(Util.getRandom(harmful, living.getRandom()), duration));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.red_potion.0").withStyle(ChatFormatting.GRAY));
    }
}
