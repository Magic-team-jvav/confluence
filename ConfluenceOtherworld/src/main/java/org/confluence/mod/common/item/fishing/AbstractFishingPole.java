package org.confluence.mod.common.item.fishing;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.FishingPoleItems;
import org.confluence.mod.common.item.accessory.FishingBobber;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;

import java.util.function.Consumer;

public abstract class AbstractFishingPole extends FishingRodItem {
    protected static final ImmutableMultimap<Attribute, AttributeModifier> EMPTY = ImmutableMultimap.of();
    protected ItemAttributeModifiers modifiers;

    public AbstractFishingPole(Properties properties) {
        super(properties);
    }

    public AbstractFishingPole(ModRarity rarity) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY.get(), rarity));
    }

    public AbstractFishingPole(Properties properties, ModRarity rarity) {
        super(properties.component(ConfluenceMagicLib.MOD_RARITY.get(), rarity));
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (pPlayer.fishing != null) {
            if (!pLevel.isClientSide) {
                int i = pPlayer.fishing.retrieve(itemstack);
                ItemStack original = itemstack.copy();
                itemstack.hurtAndBreak(i, pPlayer, LivingEntity.getSlotForHand(pHand));
                if (itemstack.isEmpty()) {
                    net.neoforged.neoforge.event.EventHooks.onPlayerDestroyItem(pPlayer, original, pHand);
                }
            }
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), getRetrieveSound(), SoundSource.NEUTRAL, 1.0F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            pPlayer.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), getThrowSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (pLevel instanceof ServerLevel serverLevel) {
                int luckBonus = EnchantmentHelper.getFishingLuckBonus(serverLevel, itemstack, pPlayer) + (int) FishingPowerInfoPacketS2C.sendAndGet((ServerPlayer) pPlayer);
                int speedBonus = (int) (EnchantmentHelper.getFishingTimeReduction(serverLevel, itemstack, pPlayer) * 20.0F);
                FishingHook fishingHook;
                FishingBobber curio = CuriosUtils.findCurio(pPlayer, FishingBobber.class);
                if (curio == null) {
                    fishingHook = getHook(itemstack, pPlayer, pLevel, luckBonus, speedBonus);
                } else {
                    fishingHook = new CurioFishingHook(pPlayer, pLevel, luckBonus, speedBonus, curio.variant);
                }
                if (TCUtils.hasAccessoriesType(pPlayer, AccessoryItems.LAVAPROOF$FISHING$HOOK)) {
                    ((IFishingHook) fishingHook).confluence$setIsLavaHook();
                    pLevel.addFreshEntity(fishingHook);
                } else {
                    if (this == FishingPoleItems.HOTLINE_FISHING_HOOK.get()) {
                        ((IFishingHook) fishingHook).confluence$setIsLavaHook();
                    }
                    pLevel.addFreshEntity(fishingHook);
                }
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pPlayer.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
    }

    protected SoundEvent getRetrieveSound() {
        return SoundEvents.FISHING_BOBBER_RETRIEVE;
    }

    protected SoundEvent getThrowSound() {
        return SoundEvents.FISHING_BOBBER_THROW;
    }

    protected abstract FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus);

    protected void addAttributeModifiers(Consumer<ItemAttributeModifiers.Builder> consumer) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        consumer.accept(builder);
        this.modifiers = builder.build();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return modifiers == null ? super.getDefaultAttributeModifiers(stack) : modifiers;
    }
}
