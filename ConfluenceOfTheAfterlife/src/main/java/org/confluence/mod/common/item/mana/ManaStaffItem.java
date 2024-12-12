package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.BaseManaStaffProjectileEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.component.ModRarity;
import org.jetbrains.annotations.NotNull;

public class ManaStaffItem extends CustomRarityItem {
    private final BulletFactory factory;
    private final int manaCost;
    private final float velocity;
    private final int cooldown;

    public ManaStaffItem(Properties properties, ModRarity rarity, BulletFactory factory, int manaCost, float velocity, int cooldown) {
        super(properties, rarity);
        this.factory = factory;
        this.manaCost = manaCost;
        this.velocity = velocity;
        this.cooldown = cooldown;
    }

    /**
     * @param rawVelocity 换算前的射弹速度
     * @param cooldown 换算前的冷却时间
     */
    public ManaStaffItem(ModRarity rarity, BulletFactory factory, int manaCost, float rawVelocity, int cooldown) {
        this(new Properties().stacksTo(1), rarity, factory, manaCost, rawVelocity / 8.0F, cooldown);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && PlayerUtils.extractMana(serverPlayer, () -> PrefixUtils.calculateManaCost(itemStack, manaCost))) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            BaseManaStaffProjectileEntity baseBulletEntity = factory.create(serverPlayer, level);
            baseBulletEntity.shootFromRotation(serverPlayer, serverPlayer.getXRot(), serverPlayer.getYRot(), 0.0F, velocity, 0.0F);
            level.addFreshEntity(baseBulletEntity);
            player.getCooldowns().addCooldown(this, cooldown);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.REGULAR_STAFF_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @FunctionalInterface
    public interface BulletFactory {
        BaseManaStaffProjectileEntity create(ServerPlayer serverPlayer, Level level);
    }
}
