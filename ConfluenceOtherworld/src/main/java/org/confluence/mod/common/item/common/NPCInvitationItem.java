package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.data.saved.NPCSpawner;

public class NPCInvitationItem extends TooltipItem {
    public NPCInvitationItem() {
        super(new Properties().stacksTo(1), ModRarity.WHITE, "tooltip.item.confluence.npc_invitation.0");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            NPCSpawner.Region region = new NPCSpawner.Region(player.chunkPosition());
            for (EntityType<?> entityType : NPCSpawner.INSTANCE.getNpcSpawned()) {
                NPCSpawner.INSTANCE.setNPCAlive(region, entityType, false);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
