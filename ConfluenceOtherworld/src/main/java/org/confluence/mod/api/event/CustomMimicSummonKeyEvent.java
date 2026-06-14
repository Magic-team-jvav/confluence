package org.confluence.mod.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CustomMimicSummonKeyEvent extends PlayerEvent {
    private final ItemStack key;
    private final ChestMenu menu;
    private final ChestBlockEntity blockEntity;

    public CustomMimicSummonKeyEvent(Player player, ItemStack key, ChestMenu menu, ChestBlockEntity blockEntity) {
        super(player);
        this.key = key;
        this.menu = menu;
        this.blockEntity = blockEntity;
    }

    public ItemStack getKey() {
        return key;
    }

    public ChestMenu getMenu() {
        return menu;
    }

    public ChestBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public static void summon(WoodenMimic mimic, ChestBlockEntity blockEntity) {
        mimic.setPos(blockEntity.getBlockPos().getBottomCenter());
        blockEntity.clearContent();
        mimic.level().removeBlock(blockEntity.getBlockPos(), false);
        mimic.level().addFreshEntity(mimic);
    }
}
