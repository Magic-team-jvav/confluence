package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.lib.util.GlobalColors;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;

public class AdvancedCombatTechniquesItem extends TooltipItem {
    public AdvancedCombatTechniquesItem() {
        super(new Properties(), ModRarity.GREEN, getTooltipsFromString("advanced_combat_techniques", 2, ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel) {
            if (!NPCSpawner.INSTANCE.isAdvancedCombatTechniquesUsed()) {
                NPCSpawner.INSTANCE.setAdvancedCombatTechniquesUsed(true);
                serverLevel.getEntities().get(EntityTypeTest.forClass(AbstractTerraNPC.class), npc -> {
                    NPCSpawner.applyAdvancedCombatTechniques(npc, Confluence.asResource("advanced_combat_techniques"));
                    return AbortableIterationConsumer.Continuation.CONTINUE;
                });
                for (ServerPlayer serverPlayer : serverLevel.players()) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.confluence.advancement_combat_techniques").withColor(GlobalColors.MESSAGE.getRGB()));
                }
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        } else {
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
