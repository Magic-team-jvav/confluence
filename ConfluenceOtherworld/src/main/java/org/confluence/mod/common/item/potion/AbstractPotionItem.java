package org.confluence.mod.common.item.potion;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.natural.spreadable.conversion_table.MoistenConversionTable;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.mixed.IMinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public abstract class AbstractPotionItem extends Item {
    public AbstractPotionItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (canUse(player.getItemInHand(hand), level, player))
            return ItemUtils.startUsingInstantly(level, player, hand);
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        apply(stack, level, living);
        if (living instanceof Player player && !player.hasInfiniteMaterials()) {
            stack.shrink(1); // 创造模式不消耗
            if (CommonConfigs.RETURN_POTION_GLASS_BOTTLE.get()) {
                if (stack.isEmpty()) {
                    return getReturnItem();
                }
                ItemStack itemstack = getReturnItem();
                if (!player.getInventory().add(itemstack)) {
                    player.drop(itemstack, false);
                }
                return stack;
            }
        }
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    protected ItemStack getReturnItem() {
        return PotionItems.BOTTLE.toStack();
    }

    protected boolean canUse(ItemStack itemStack, Level level, Player player) {
        return true;
    }

    protected abstract void apply(ItemStack itemStack, Level level, LivingEntity living);

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player instanceof ServerPlayer serverPlayer &&
                context.getClickedFace() != Direction.DOWN &&
                stack.is(PotionItems.BOTTLED_WATER.get())
        ) {
            BlockPos pos = context.getClickedPos();
            BlockState source = level.getBlockState(pos);
            BlockState target = MoistenConversionTable.INSTANCE.get(source, IMinecraftServer.isHardmode(serverPlayer.server));
            if (target != null) {
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, PotionItems.BOTTLE.toStack()));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.setBlockAndUpdate(pos, target);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String key = "tooltip." + stack.getDescriptionId() + ".0";
        if (I18n.exists(key)) {
            tooltipComponents.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }

    public static <T extends AbstractPotionItem> void use(Player player, float required, Class<T> type, ToIntFunction<T> function) {
        if (required <= 0.0F) return;
        List<Tuple<ItemStack, Integer>> potions = new ArrayList<>();
        Item item = player.getOffhandItem().getItem();
        if (type.isInstance(item)) {
            potions.add(new Tuple<>(player.getOffhandItem(), function.applyAsInt(type.cast(item))));
        }
        for (ItemStack itemStack : player.getInventory().items) {
            item = itemStack.getItem();
            if (type.isInstance(item)) {
                potions.add(new Tuple<>(itemStack, function.applyAsInt(type.cast(item))));
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
        PortListExtension.getLast(potions).getA().finishUsingItem(level, player);
    }
}
