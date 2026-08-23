package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModSoundEvents;

/// 把商贩背包作为当前世界的一次性永久升级提交到 {@link NPCSpawner}。
///
/// 该状态属于世界而非玩家，因此不使用 MagicLib 的玩家永久升级附件。客户端只预测使用动作；
/// 状态检查、广播、音效和物品消耗全部由服务端在同一次提交中完成。
public class PeddlersSatchelItem extends TooltipItem {
    public PeddlersSatchelItem() {
        super(new Properties(), ModRarity.LIGHT_PURPLE, Component.translatable("tooltip.item.confluence.peddlers_satchel.0").withStyle(ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        if (!(level instanceof ServerLevel serverLevel) || NPCSpawner.INSTANCE.isPeddlersSatchelUsed()) {
            return InteractionResultHolder.fail(itemStack);
        }

        NPCSpawner.INSTANCE.setPeddlersSatchelUsed(true);
        MutableComponent component = Component.translatable("message.confluence.peddlers_satchel").withColor(GlobalColors.MESSAGE.get());
        for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(component);
        }
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.TRANSMUTATION_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!player.hasInfiniteMaterials()) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.consume(itemStack);
    }
}
