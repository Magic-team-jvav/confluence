package org.confluence.mod.common.item.soul;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soul.skill.SoulSkill;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulSkillBookItem extends Item {
    private static final int CONSUME_COUNT = 1;
    private static final String BOUND_TOOLTIP = "tooltip.confluence.soul_skill_book.bound";
    private static final String UNBOUND_TOOLTIP = "tooltip.confluence.soul_skill_book.unbound";
    private static final String INVALID_TOOLTIP = "tooltip.confluence.soul_skill_book.invalid";
    private static final String LEARNED_MESSAGE = "message.confluence.soul_skill_book.learned";

    public SoulSkillBookItem() {
        super(new Properties());
    }

    public ItemStack createStack(ResourceLocation skillId) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModDataComponentTypes.SOUL_SKILL.get(), skillId);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ResourceLocation skillId = stack.get(ModDataComponentTypes.SOUL_SKILL.get());
        SoulSkill skill = skillId == null ? null : ModSoulSkills.getById(skillId);
        if (skill == null || skill == ModSoulSkills.EMPTY.get()) {
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.learnSoulSkill(serverPlayer, skillId)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.displayClientMessage(Component.translatable(LEARNED_MESSAGE, skill.getNameComponent(skill.getStack())), true);
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(CONSUME_COUNT);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ResourceLocation skillId = stack.get(ModDataComponentTypes.SOUL_SKILL.get());
        if (skillId == null) {
            tooltipComponents.add(Component.translatable(UNBOUND_TOOLTIP));
            return;
        }
        SoulSkill skill = ModSoulSkills.getById(skillId);
        if (skill == null || skill == ModSoulSkills.EMPTY.get()) {
            tooltipComponents.add(Component.translatable(INVALID_TOOLTIP));
            return;
        }
        tooltipComponents.add(Component.translatable(BOUND_TOOLTIP, skill.getNameComponent(skill.getStack())));
    }
}
