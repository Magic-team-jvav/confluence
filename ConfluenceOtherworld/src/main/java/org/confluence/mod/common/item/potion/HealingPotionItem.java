package org.confluence.mod.common.item.potion;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.effect.harmful.PotionSicknessEffect;
import org.confluence.mod.common.init.ModEffects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HealingPotionItem extends AbstractPotionItem {
    private final int amount;

    public HealingPotionItem(int amount, Rarity rarity) {
        super(new Properties().rarity(rarity));
        this.amount = amount;
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
        PotionSicknessEffect.addTo(living, 1200);
    }

    public static void use(Player player) {
        float required = player.getMaxHealth() - player.getHealth();
        if (required <= 0.0F || player.hasEffect(ModEffects.POTION_SICKNESS)) return;
        List<Tuple<ItemStack, Integer>> potions = new ArrayList<>();
        if (player.getOffhandItem().getItem() instanceof HealingPotionItem potionItem) {
            potions.add(new Tuple<>(player.getOffhandItem(), potionItem.amount));
        }
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() instanceof HealingPotionItem potionItem) {
                potions.add(new Tuple<>(itemStack, potionItem.amount));
            }
        }
        if (potions.isEmpty()) return;
        potions.sort(Comparator.comparingInt(Tuple::getB));
        Level level = player.level();
        for (int i = 0; i < potions.size(); i++) {
            Tuple<ItemStack, Integer> left = potions.get(i);
            if (required <= left.getB()) {
                left.getA().finishUsingItem(level, player);
                return;
            }
            if (i == potions.size() - 1) {
                left.getA().finishUsingItem(level, player);
            } else {
                Tuple<ItemStack, Integer> right = potions.get(i + 1);
                if (right.getB() >= required) {
                    right.getA().finishUsingItem(level, player);
                    return;
                }
            }
        }
        potions.getLast().getA().finishUsingItem(level, player);
    }
}
