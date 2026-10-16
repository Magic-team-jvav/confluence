package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FallenSoulCoreItem extends CustomRarityItem implements ILeftClickStateItem {
    private static final String SOUL_MESSAGE = "message.confluence.soul_mode.soul";
    private static final String MAGIC_MESSAGE = "message.confluence.soul_mode.magic";

    public FallenSoulCoreItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    /// 创造潜行增加成长计数，其余成功使用切换魂师模式。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        SoulStorage storage = SoulStorage.of(player);
        if (player.hasInfiniteMaterials() && player.isShiftKeyDown()) {
            storage.addSoul(1);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        if (!PlayerUtils.toggleSoulMode(serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, SoulSkillParameters.FALLEN_SOUL_CORE_COOLDOWN_TICKS);
        stack.consume(1, player);
        serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        player.displayClientMessage(Component.translatable(storage.isSoulMode() ? SOUL_MESSAGE : MAGIC_MESSAGE), true);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /// 创造潜行左键减少一次成长计数，按下去重由通用入口处理。
    @Override
    public void onLeftClick(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer && player.hasInfiniteMaterials() && player.isShiftKeyDown()) {
            SoulStorage.of(player).addSoul(-1);
        }
    }

    @Override
    public void onLeftRelease(Player player, ItemStack stack) {}
}
