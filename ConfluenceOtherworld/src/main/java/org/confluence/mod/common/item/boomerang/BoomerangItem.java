package org.confluence.mod.common.item.boomerang;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoomerangItem extends Item {
    private final Settings settings;

    public BoomerangItem(Settings settings) {
        super(settings.properties());
        this.settings = settings;
    }

    /// 左键包和右键入口共用同一套发射逻辑，避免两个按键路径以后出现伤害、冷却或数量上限差异。
    public void throwBoomerang(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int maximumActive = settings.maxCount()
                + EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.MULTI_BOOMERANG.get(), stack);
        if (player.getCooldowns().isOnCooldown(this)
                || activeCount(player, stack) >= maximumActive) {
            return;
        }
        BoomerangProjectile projectile = new BoomerangProjectile(
                ModEntities.BOOMERANG_PROJECTILE.get(),
                player.level());
        projectile.configure(player, stack, settings);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, settings.flySpeed(), 0.0F);
        player.level().addFreshEntity(projectile);
        player.playSound(SoundEvents.TRIDENT_THROW, 0.6F, 1.35F);
        player.swing(hand, true);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, settings.cooldown());
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(hand));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            throwBoomerang(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("attribute.name.generic.attack_damage")
                .append(": ")
                .append(String.format("%.1f", settings.damage()))
                .withStyle(style -> style.withColor(0x00FF00)));
        tooltip.add(Component.translatable("tooltip.confluence.boomerang.fly_speed")
                .append(": ")
                .append(String.format("%.2f", settings.flySpeed()))
                .withStyle(style -> style.withColor(0xCCCC00)));
        if (settings.maxCount() > 1) {
            tooltip.add(Component.translatable("tooltip.confluence.boomerang.max_count")
                    .append(": ")
                    .append(Integer.toString(settings.maxCount()))
                    .withStyle(style -> style.withColor(0xAA8800)));
        }
        if (settings.penetration() > 1) {
            tooltip.add(Component.translatable("tooltip.confluence.boomerang.penetration")
                    .append(": ")
                    .append(Integer.toString(settings.penetration()))
                    .withStyle(style -> style.withColor(0x00FFFF)));
        }
    }

    private int activeCount(Player player, ItemStack stack) {
        int count = 0;
        for (BoomerangProjectile projectile : player.level().getEntitiesOfClass(
                BoomerangProjectile.class,
                player.getBoundingBox().inflate(settings.activeSearchRange()))) {
            if (projectile.belongsTo(player) && ItemStack.isSameItem(projectile.getWeapon(), stack)) {
                count++;
            }
        }
        return Math.max(0, count);
    }

    public record Settings(
            float damage,
            float flySpeed,
            float backSpeed,
            int forwardTicks,
            int cooldown,
            int maxCount,
            int penetration,
            boolean fire,
            Item.Properties properties
    ) {
        private static final double DEFAULT_ACTIVE_SEARCH_RANGE = 96.0;

        public Settings {
            if (damage < 0.0F || flySpeed <= 0.0F || backSpeed <= 0.0F) {
                throw new IllegalArgumentException("Boomerang speed and damage settings must be positive");
            }
            if (forwardTicks <= 0 || cooldown < 0 || maxCount <= 0 || penetration <= 0) {
                throw new IllegalArgumentException("Boomerang tick and count settings must be positive");
            }
        }

        public double activeSearchRange() {
            return DEFAULT_ACTIVE_SEARCH_RANGE;
        }
    }
}
