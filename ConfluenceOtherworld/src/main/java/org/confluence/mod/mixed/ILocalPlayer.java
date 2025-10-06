package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.confluence.mod.client.gui.TombstoneEditScreen;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.init.block.ModBlocks;

public interface ILocalPlayer {
    void confluence$setCanMove(boolean canMove);

    boolean confluence$isCanMove();

    static ILocalPlayer of(LocalPlayer player) {
        return (ILocalPlayer) player;
    }

    static boolean redirectEditScreen(SignBlockEntity signEntity, boolean isFrontText, Minecraft minecraft) {
        if (signEntity instanceof TombstoneBlock.BEntity entity) {
            Block block = entity.getBlockState().getBlock();
            boolean isGold = ModBlocks.TOMBSTONES.object2BooleanEntrySet().stream()
                    .filter(entry -> entry.getKey().get() == block)
                    .findAny().map(Object2BooleanMap.Entry::getBooleanValue).orElse(false);
            minecraft.setScreen(new TombstoneEditScreen(entity, isFrontText, minecraft.isTextFilteringEnabled(), isGold));
            return true;
        }
        return false;
    }
}
