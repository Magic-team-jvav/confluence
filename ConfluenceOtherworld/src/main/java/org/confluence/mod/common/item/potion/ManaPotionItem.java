package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.PlayerUtils;

public class ManaPotionItem extends AbstractPotionItem {
    private final int amount;

    public ManaPotionItem(int amount, ModRarity rarity) {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
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
            MobEffectInstance instance = serverPlayer.getEffect(ModEffects.MANA_SICKNESS.get());
            if (instance == null) {
                instance = new MobEffectInstance(ModEffects.MANA_SICKNESS.get(), EnchantmentUtils.processManaSicknessDuration(serverPlayer, 100));
            } else {
                int duration = Math.min(EnchantmentUtils.processManaSicknessDuration(serverPlayer, instance.duration + 100), 200);
                instance = new MobEffectInstance(instance); // 复制一份，保证能正常更新
                instance.duration = duration;
            }
            serverPlayer.addEffect(instance);
        }
    }

    public static void use(Player player) {
        ManaStorage manaStorage = ManaStorage.of(player);
        float required = manaStorage.getMaxMana() - manaStorage.getCurrentMana();
        AbstractPotionItem.use(player, required, ManaPotionItem.class, manaPotionItem -> manaPotionItem.amount);
    }
}
