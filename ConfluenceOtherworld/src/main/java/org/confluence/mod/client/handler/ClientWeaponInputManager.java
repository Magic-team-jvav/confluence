package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/// 将客户端输入事件分发给已注册的武器输入处理器。
public final class ClientWeaponInputManager {
    private static final List<ClientWeaponInputHandler> HANDLERS = new ArrayList<>();

    private ClientWeaponInputManager() {}

    public static void init() {
        if (HANDLERS.isEmpty()) register(YoyoInputHandler.INSTANCE);
    }

    public static void register(ClientWeaponInputHandler handler) {
        HANDLERS.add(handler);
    }

    public static void tick(LocalPlayer player) {
        ItemStack stack = player.getMainHandItem();
        boolean attackHeld = Minecraft.getInstance().options.keyAttack.isDown();
        for (ClientWeaponInputHandler handler : HANDLERS) handler.tick(player, stack, attackHeld);
    }

    public static boolean scroll(LocalPlayer player, double amount) {
        ItemStack stack = player.getMainHandItem();
        for (ClientWeaponInputHandler handler : HANDLERS) {
            if (handler.scroll(player, stack, amount)) return true;
        }
        return false;
    }

    public static boolean blocksAttack(ItemStack stack) {
        for (ClientWeaponInputHandler handler : HANDLERS) {
            if (handler.blocksAttack(stack)) return true;
        }
        return false;
    }

    public static boolean blocksUse(ItemStack stack) {
        for (ClientWeaponInputHandler handler : HANDLERS) {
            if (handler.blocksUse(stack)) return true;
        }
        return false;
    }

    public static void reset() {
        for (ClientWeaponInputHandler handler : HANDLERS) handler.reset();
    }
}
