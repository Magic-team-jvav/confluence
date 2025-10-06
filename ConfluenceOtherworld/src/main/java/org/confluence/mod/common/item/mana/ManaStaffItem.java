package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.init.TCAttributes;

import java.util.List;
import java.util.function.Consumer;

public class ManaStaffItem<E extends DamageSettableProjectile> extends CustomRarityItem {
    public static final ResourceLocation ID = Confluence.asResource("mana_staff");
    protected final ProjectileFactory<E> factory;
    protected final float damage;
    protected final int manaCost;
    protected final float velocity;
    protected final int cooldown;

    public ManaStaffItem(Properties properties, ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity);
        this.damage = damage;
        this.factory = factory;
        this.manaCost = manaCost;
        this.velocity = rawVelocity / 8.0F;
        this.cooldown = cooldown;
    }

    public ManaStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown, Consumer<ItemAttributeModifiers.Builder> consumer) {
        this(new Properties().stacksTo(1), rarity, factory, damage, manaCost, rawVelocity, cooldown);
        addAttributeModifiers(consumer);
    }

    /**
     * @param rawVelocity 换算前的射弹速度
     */
    public ManaStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown, double critChance) {
        this(new Properties().stacksTo(1), rarity, factory, damage, manaCost, rawVelocity, cooldown);
        if (critChance == 0.0) return;
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(ID, critChance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && couldShoot(serverPlayer, itemStack)) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            E projectile = factory.create(serverPlayer);
            beforeShoot(serverPlayer, itemStack, projectile);
            level.addFreshEntity(projectile);
            afterShoot(serverPlayer, itemStack, projectile);
        }
        return InteractionResultHolder.success(itemStack);
    }

    protected boolean couldShoot(ServerPlayer player, ItemStack itemStack) {
        return PlayerUtils.extractMana(player, itemStack, () -> PrefixUtils.calculateManaCost(itemStack, manaCost));
    }

    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, E projectile) {
        projectile.setDamage(damage);
        projectile.setDefaultVelocity(velocity);
        projectile.setOwner(player);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 0.0F);
    }

    protected void afterShoot(ServerPlayer player, ItemStack itemStack, E projectile) {
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(this, cooldown);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.REGULAR_STAFF_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.confluence.damage", damage).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.mana_cost", manaCost).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.velocity", velocity).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.confluence.cooldown", cooldown).withStyle(ChatFormatting.GRAY));
    }

    @FunctionalInterface
    public interface ProjectileFactory<E extends Projectile> {
        E create(ServerPlayer serverPlayer);
    }
}
