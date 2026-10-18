package org.confluence.mod.common.item.soulcerer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulSkillBookItem extends CustomRarityItem {
    private static final String LEARNED_MESSAGE = "message.confluence.soulcerer_skill.learned";
    private static final String UNBOUND_TOOLTIP = "tooltip.confluence.soulcerer_skill_book.unbound";
    private static final String INVALID_TOOLTIP = "tooltip.confluence.soulcerer_skill_book.invalid";

    public SoulSkillBookItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    /// 仅首次永久学会合法绑定技能时消费技能书。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ResourceLocation id = stack.get(ModDataComponentTypes.SOUL_SKILL.get());
        SoulSkill skill = id == null ? null : SoulSkillStorage.findSoulSkill(id);
        if (skill == null || skill == ModSoulSkills.EMPTY.get()) {
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.learnSoulSkill(serverPlayer, skill)) {
                return InteractionResultHolder.fail(stack);
            }
            stack.consume(1, player);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            player.displayClientMessage(Component.translatable(LEARNED_MESSAGE, skill.getNameComponent(skill.getStack())), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /// 展示绑定技能，未绑定或未知技能时给出提示。
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ResourceLocation id = stack.get(ModDataComponentTypes.SOUL_SKILL.get());
        if (id == null) {
            tooltipComponents.add(Component.translatable(UNBOUND_TOOLTIP));
            return;
        }
        SoulSkill skill = SoulSkillStorage.findSoulSkill(id);
        tooltipComponents.add(skill == null || skill == ModSoulSkills.EMPTY.get()
                ? Component.translatable(INVALID_TOOLTIP) : skill.getNameComponent(skill.getStack()));
    }
}

