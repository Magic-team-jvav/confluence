package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.attachment.SoulSkillStorage;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulSkillBookItem extends CustomRarityItem {
    private static final String LEARNED_MESSAGE = "message.confluence.soulcerer_skill.learned";
    private static final String REVIEW_MESSAGE = "message.confluence.soulcerer_skill.review";
    private static final String UNBOUND_TOOLTIP = "tooltip.confluence.soulcerer_skill_book.unbound";
    private static final String REQUIRED_TOOLTIP = "tooltip.confluence.soulcerer_skill_book.required";

    public SoulSkillBookItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    /// 首次使用永久解锁技能或被动，之后每次使用改为给予复习；书籍不消耗，可反复阅读。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        SoulBooks.Definition book = SoulBooks.of(stack);
        if (book == null) {
            return InteractionResultHolder.fail(stack);
        }
        if (book.required() != null && !PlayerUtils.isSoulSkillLearned(serverPlayer, book.required())) {
            return InteractionResultHolder.fail(stack);
        }
        if (PlayerUtils.isSoulBookGranted(serverPlayer, book)) {
            PlayerUtils.applySoulBookReview(serverPlayer);
            player.displayClientMessage(Component.translatable(REVIEW_MESSAGE), true);
        } else if (PlayerUtils.grantSoulBook(serverPlayer, book)) {
            String customMessage = SoulBooks.useMessageKey(book.id());
            player.displayClientMessage(customMessage == null
                    ? Component.translatable(LEARNED_MESSAGE, Component.translatable(SoulBooks.nameKey(book.id())))
                    : Component.translatable(customMessage), true);
        } else {
            return InteractionResultHolder.fail(stack);
        }
        serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /// 书籍名称由绑定组件决定，未绑定时回落到物品自身名称。
    @Override
    public Component getName(ItemStack stack) {
        SoulBooks.Definition book = SoulBooks.of(stack);
        return book == null ? super.getName(stack) : Component.translatable(SoulBooks.nameKey(book.id()));
    }

    /// 展示书籍授予的技能、被动效果与使用前置。
    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SoulBooks.Definition book = SoulBooks.of(stack);
        if (book == null) {
            tooltipComponents.add(Component.translatable(UNBOUND_TOOLTIP));
            return;
        }
        if (book.skill() == null) {
            String passiveKey = SoulBooks.passiveTooltipKey(book.id());
            if (passiveKey != null) {
                tooltipComponents.add(Component.translatable(passiveKey));
            }
        } else {
            SoulSkill skill = SoulSkillStorage.findSoulSkill(book.skill());
            if (skill != null) {
                tooltipComponents.add(skill.getNameComponent(skill.getStack()));
            }
        }
        if (book.required() != null) {
            SoulSkill required = SoulSkillStorage.findSoulSkill(book.required());
            if (required != null) {
                tooltipComponents.add(Component.translatable(REQUIRED_TOOLTIP, required.getNameComponent(required.getStack())));
            }
        }
    }
}
