package org.confluence.mod.common.item.fishing;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.FishingPoleItems;
import org.confluence.mod.common.item.accessory.FishingBobber;
import org.confluence.mod.mixed.IFishingHook;
import org.confluence.mod.common.entity.fishing.FishingSession;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.mesdag.portlib.wrapper.common.extensions.IPortLivingEntityExtension;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.function.Consumer;

public abstract class AbstractFishingPole extends FishingRodItem {
    public static final String BAIT_KEY = "Bait";
    public static final String HAS_BAIT_KEY = "HasBait";
    protected PortItemAttributeModifiers modifiers;

    public AbstractFishingPole(Properties properties) {
        super(properties.stacksTo(1));
    }

    public AbstractFishingPole(ModRarity rarity) {
        this(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    public AbstractFishingPole(Properties properties, ModRarity rarity) {
        this(properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.fishing != null) {
            if (!level.isClientSide) {
                int i = player.fishing.retrieve(stack);
                ItemStack original = stack.copy();
                stack.hurtAndBreak(i, player, IPortLivingEntityExtension.getSlotForHand(hand));
                if (stack.isEmpty()) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, original, hand);
                }
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), getRetrieveSound(), SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), getThrowSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (level instanceof ServerLevel) {
                int luckBonus = EnchantmentHelper.getFishingLuckBonus(stack);
                int speedBonus = EnchantmentHelper.getFishingSpeedBonus(stack);
                FishingHook hook;
                FishingBobber curio = CuriosUtils.findCurio(player, FishingBobber.class);
                // 开发竿保留自己的特殊鱼钩，避免被饰品浮标替换。
                if (this instanceof DevFishingRod || curio == null) {
                    hook = getHook(stack, player, level, luckBonus, speedBonus);
                } else {
                    hook = new CurioFishingHook(player, level, luckBonus, speedBonus, curio.variant);
                }

                FishingSession session = new FishingSession(player, stack, hand);
                IFishingHook.of(hook).confluence$setFishingSession(session);
                ItemStack bait = session.bait();

                if (this == FishingPoleItems.HOTLINE_FISHING_HOOK.get() ||
                        (!bait.isEmpty() && bait.is(ModTags.Items.LAVA_PROOF_BAIT)) ||
                        TCUtils.hasType(player, AccessoryItems.LAVAPROOF$FISHING$HOOK)
                ) IFishingHook.of(hook).confluence$setIsLavaHook();

                FishingPowerInfoPacketS2C.sendToClient((ServerPlayer) player);
                level.addFreshEntity(hook);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /// 迁移旧存档中预扣在钓竿里的鱼饵；清空旧数据后再返还，保证只返还一次。
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            ItemStack legacyBait = getBait(stack);
            if (!legacyBait.isEmpty()) {
                setBait(stack, ItemStack.EMPTY);
                if (!player.addItem(legacyBait)) player.drop(legacyBait, false);
            }
        }
    }

    protected SoundEvent getRetrieveSound() {
        return SoundEvents.FISHING_BOBBER_RETRIEVE;
    }

    protected SoundEvent getThrowSound() {
        return SoundEvents.FISHING_BOBBER_THROW;
    }

    public abstract FishingHook getHook(ItemStack itemStack, Player player, Level level, int luckBonus, int speedBonus);

    /// 读取钓竿独立渔力属性的固定加成，数值单位为渔力点。
    public float getFishingPower() {
        if (modifiers == null) return 0;
        return (float) modifiers.getAttributeModifiers(EquipmentSlot.MAINHAND).get(ConfluenceMagicLib.FISHING_POWER.value()).stream()
                .filter(modifier -> modifier.getOperation() == AttributeModifier.Operation.ADDITION)
                .mapToDouble(AttributeModifier::getAmount).sum();
    }

    protected void addAttributeModifiers(Consumer<PortItemAttributeModifiers.Builder> consumer) {
        PortItemAttributeModifiers.Builder builder = PortItemAttributeModifiers.builder();
        consumer.accept(builder);
        this.modifiers = builder.build();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return modifiers == null ? super.getAttributeModifiers(slot, stack) : modifiers.getAttributeModifiers(slot);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }

    public static ItemStack getBait(ItemStack fishingPole) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(fishingPole);
        if (tag == null || !tag.getBoolean(HAS_BAIT_KEY) || !tag.contains(BAIT_KEY)) return ItemStack.EMPTY;
        return ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag.get(BAIT_KEY)).result().orElse(ItemStack.EMPTY);
    }

    public static void setBait(ItemStack fishingPole, ItemStack bait) {
        LibUtils.updateItemStackNbt(fishingPole, tag -> {
            tag.put(BAIT_KEY, ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, bait)
                    .result().orElseGet(CompoundTag::new));
            tag.putBoolean(HAS_BAIT_KEY, !bait.isEmpty());
        });
    }

}
