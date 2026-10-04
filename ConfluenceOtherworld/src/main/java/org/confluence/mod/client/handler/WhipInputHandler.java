package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.confluence.mod.network.c2s.WhipControlPacketC2S;

public final class WhipInputHandler implements ClientWeaponInputHandler {
    public static final WhipInputHandler INSTANCE = new WhipInputHandler();
    private ItemStack activeStack = ItemStack.EMPTY;
    private int selectedSlot = -1;
    private ClientConfigs.WeaponUseButton activeButton;

    private WhipInputHandler() {}

    @Override
    public void tick(LocalPlayer player, ItemStack stack, boolean attackHeld) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean held = ClientConfigs.usesLeftWeaponButton(stack) ? attackHeld : minecraft.options.keyUse.isDown();
        boolean active = stack.getItem() instanceof BaseWhipItem && held && minecraft.screen == null && minecraft.isWindowActive() && player.isAlive() && !player.isSpectator();
        ClientConfigs.WeaponUseButton button = ClientConfigs.weaponUseButton(stack);
        boolean changed = activeStack.getItem() != stack.getItem() || selectedSlot != player.getInventory().selected || activeButton != button;
        if (!active || changed) {
            if (!activeStack.isEmpty()) WhipControlPacketC2S.send(false);
            reset();
        }
        if (active && activeStack.isEmpty()) {
            activeStack = stack;
            selectedSlot = player.getInventory().selected;
            activeButton = button;
            WhipControlPacketC2S.send(true);
        }
    }

    @Override
    public boolean blocksAttack(ItemStack stack) {
        return stack.is(ModTags.Items.WHIP);
    }

    @Override
    public boolean blocksUse(ItemStack stack) {
        return stack.is(ModTags.Items.WHIP);
    }

    @Override
    public void reset() {
        activeStack = ItemStack.EMPTY;
        selectedSlot = -1;
        activeButton = null;
    }
}
