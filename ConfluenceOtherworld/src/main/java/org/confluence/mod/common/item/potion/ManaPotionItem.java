package org.confluence.mod.common.item.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.PlayerUtils;

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
}
