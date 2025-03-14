package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.PlayerUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ManaPotionItem extends AbstractPotionItem {
    private final int amount;

    public ManaPotionItem(int amount, Rarity rarity) {
        super(new Properties().rarity(rarity));
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        if (living instanceof ServerPlayer serverPlayer) {
            PlayerUtils.receiveMana(serverPlayer, () -> amount);
            MobEffectInstance instance = serverPlayer.getEffect(ModEffects.MANA_SICKNESS);
            if (instance == null) {
                serverPlayer.addEffect(new MobEffectInstance(ModEffects.MANA_SICKNESS, 100));
            } else {
                instance.mapDuration(raw -> Math.min(raw + 100, 200));
                serverPlayer.addEffect(instance);
            }
        }
    }

    public static void use(Player player) {
        ManaStorage manaStorage = player.getData(ModAttachmentTypes.MANA_STORAGE);
        float required = manaStorage.getMaxMana() - manaStorage.getCurrentMana();
        if (required <= 0.0F) return;
        List<Tuple<ItemStack, Integer>> potions = new ArrayList<>();
        if (player.getOffhandItem().getItem() instanceof ManaPotionItem potionItem) {
            potions.add(new Tuple<>(player.getOffhandItem(), potionItem.amount));
        }
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() instanceof ManaPotionItem potionItem) {
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
                return;
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
