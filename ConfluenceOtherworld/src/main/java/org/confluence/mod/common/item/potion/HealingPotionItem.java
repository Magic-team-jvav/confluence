package org.confluence.mod.common.item.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.effect.harmful.PotionSicknessEffect;
import org.confluence.mod.common.init.ModEffects;


public class HealingPotionItem extends AbstractPotionItem {
    private final int amount;
    private final int sickNessDuration;

    public HealingPotionItem(int amount, ModRarity rarity, int sickNessDuration) {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.amount = amount;
        this.sickNessDuration = sickNessDuration;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 4;
    }

    @Override
    protected boolean canUse(ItemStack itemStack, Level level, Player player) {
        return !player.hasEffect(ModEffects.POTION_SICKNESS);
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        living.heal(amount);
        PotionSicknessEffect.addTo(living, sickNessDuration);
    }

    public static void use(Player player) {
        if (player.hasEffect(ModEffects.POTION_SICKNESS)) return;
        float required = player.getMaxHealth() - player.getHealth();
        AbstractPotionItem.use(player, required, HealingPotionItem.class, healingPotionItem -> healingPotionItem.amount);
    }
}
