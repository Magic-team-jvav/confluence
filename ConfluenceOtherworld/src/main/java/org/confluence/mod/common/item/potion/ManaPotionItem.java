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
            applyAutomaticUseEffects(serverPlayer);
        }
    }

    /// 应用喝下魔力药水后必然产生的魔力病效果。
    ///
    /// <p>统一弹幕成本会在整批弹幕成功生成后调用本方法；魔力恢复和药水扣除已在可回滚的提交
    /// 阶段完成，因此这里不能再次恢复魔力或消耗物品。</p>
    public static void applyAutomaticUseEffects(ServerPlayer player) {
        MobEffectInstance instance = player.getEffect(ModEffects.MANA_SICKNESS.get());
        if (instance == null) {
            instance = new MobEffectInstance(
                    ModEffects.MANA_SICKNESS.get(),
                    EnchantmentUtils.processManaSicknessDuration(player, 100));
        } else {
            int duration = Math.min(
                    EnchantmentUtils.processManaSicknessDuration(player, instance.duration + 100), 200);
            instance = new MobEffectInstance(instance); // 复制一份，保证能正常更新
            instance.duration = duration;
        }
        player.addEffect(instance);
    }

    public static void use(Player player) {
        ManaStorage manaStorage = ManaStorage.of(player);
        float required = manaStorage.getMaxMana() - manaStorage.getCurrentMana();
        AbstractPotionItem.use(player, required, ManaPotionItem.class, manaPotionItem -> manaPotionItem.amount);
    }
}
