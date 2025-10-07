package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.GoldenShowerProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;

public class GoldenShowerItem extends ManaStaffItem<GoldenShowerProjectile> {
    public GoldenShowerItem() {
        super(ModRarity.LIGHT_RED, GoldenShowerProjectile::new, 20, 7, 30, 0, 0.04);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    protected boolean couldShoot(ServerPlayer player, ItemStack itemStack) {
        return PlayerUtils.extractMana(player, itemStack, () -> PrefixUtils.calculateManaCost(itemStack, manaCost / 3F));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if ((remainingUseDuration & 2) == 0 && livingEntity instanceof ServerPlayer player) { // 每2tick发射一次
            if (couldShoot(player, stack)) {
                GoldenShowerProjectile projectile = factory.create(player);
                beforeShoot(player, stack, projectile);
                level.addFreshEntity(projectile);
                if (remainingUseDuration % 6 == 0) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.REGULAR_STAFF_SHOOT_3.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }
}
