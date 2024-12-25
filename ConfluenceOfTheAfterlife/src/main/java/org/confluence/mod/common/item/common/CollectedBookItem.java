package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CollectedBookItem extends Item {
    public CollectedBookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        WrittenBookContent writtenbookcontent = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (writtenbookcontent != null) {
            String s = writtenbookcontent.title().raw();
            if (!StringUtil.isBlank(s)) {
                return Component.literal(s);
            }
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        WrittenBookContent writtenbookcontent = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (writtenbookcontent != null) {
            if (!StringUtil.isBlank(writtenbookcontent.author())) {
                tooltipComponents.add(Component.translatable("book.byAuthor", writtenbookcontent.author()).withStyle(ChatFormatting.GRAY));
            }

            tooltipComponents.add(Component.translatable("book.generation." + writtenbookcontent.generation()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.openItemGui(itemstack, hand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public static boolean resolveBookComponents(ItemStack bookStack, CommandSourceStack resolvingSource, @Nullable Player resolvingPlayer) {
        WrittenBookContent writtenbookcontent = bookStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (writtenbookcontent != null && !writtenbookcontent.resolved()) {
            WrittenBookContent writtenbookcontent1 = writtenbookcontent.resolve(resolvingSource, resolvingPlayer);
            if (writtenbookcontent1 != null) {
                bookStack.set(DataComponents.WRITTEN_BOOK_CONTENT, writtenbookcontent1);
                return true;
            }

            bookStack.set(DataComponents.WRITTEN_BOOK_CONTENT, writtenbookcontent.markResolved());
        }

        return false;
    }
}
