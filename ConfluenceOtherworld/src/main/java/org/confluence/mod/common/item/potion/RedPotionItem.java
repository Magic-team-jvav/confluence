package org.confluence.mod.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class RedPotionItem extends AbstractPotionItem {
    private final List<Supplier<? extends MobEffect>> beneficial = List.of(
            ModEffects.OBSIDIAN_SKIN,
            supplier(MobEffects.REGENERATION),
            supplier(MobEffects.MOVEMENT_SPEED),
            ModEffects.IRON_SKIN,
            ModEffects.MANA_REGENERATION,
            ModEffects.MAGIC_POWER,
            supplier(MobEffects.SLOW_FALLING),
            ModEffects.SPELUNKER,
            ModEffects.ARCHERY,
            ModEffects.HEART_REACH,
            ModEffects.HUNTER,
            ModEffects.ENDURANCE,
            ModEffects.LIFE_FORCE,
            ModEffects.INFERNO,
            supplier(MobEffects.DIG_SPEED),
            ModEffects.RAGE,
            ModEffects.WRATH,
            ModEffects.DANGER_SENSE
    );
    private final List<Supplier<? extends MobEffect>> harmful = List.of(
            supplier(MobEffects.POISON),
            supplier(MobEffects.DARKNESS),
            ModEffects.CURSED,
            ModEffects.BLEEDING,
            TCEffects.CONFUSED,
            supplier(MobEffects.MOVEMENT_SLOWDOWN),
            supplier(MobEffects.WEAKNESS),
            ModEffects.SILENCED,
            ModEffects.BROKEN_ARMOR,
            ModEffects.CHOKING
    );

    private static Supplier<MobEffect> supplier(MobEffect effect) {
        return () -> effect;
    }

    public RedPotionItem() {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED));
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (ModSecretSeeds.FOR_THE_WORTHY.match(serverLevel)) {
            for (int i = 0; i < 3; i++) {
                living.addEffect(new MobEffectInstance(Util.getRandom(beneficial, living.getRandom()).get(), 30 * 60 * 20));
            }
        } else {
            int duration = LibUtils.switchByDifficulty(level, living.blockPosition(), 60, 120, 180) * 60 * 20;
            if (living.getRandom().nextFloat() < 1.0F / 11.0F) {
                living.setRemainingFireTicks(duration);
            } else {
                living.addEffect(new MobEffectInstance(Util.getRandom(harmful, living.getRandom()).get(), duration));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.red_potion.0").withStyle(ChatFormatting.GRAY));
    }
}
