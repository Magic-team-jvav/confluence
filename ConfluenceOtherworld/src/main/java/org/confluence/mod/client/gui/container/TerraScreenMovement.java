package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.container.npc_screen.TerraShopScreen;

import java.util.HashMap;
import java.util.Map;

/// 泰拉里背包、箱子、商店开着也能走路和跳。原版打开界面时按键不再传给移动键，
/// 这里每个客户端刻开始时按键盘的真实状态设置前后左右和跳跃。
/// NeoForge 的移动键属于 IN_GAME 上下文，有界面时 isDown() 一律为假，
/// 所以界面开着时临时把这几个键改成 UNIVERSAL，界面关上后改回原来的上下文。
/// Shift（潜行）和 Ctrl（疾跑）不接管：背包里它们是 Shift+点击 和 Ctrl+点击。输入框有焦点时不动。
public final class TerraScreenMovement {
    private static final Map<KeyMapping, IKeyConflictContext> SAVED = new HashMap<>();

    private TerraScreenMovement() {}

    public static boolean appliesTo(Screen screen) {
        return screen instanceof ExtraInventoryScreen || screen instanceof TerraChestScreen || screen instanceof TerraShopScreen;
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (minecraft.player == null || !ClientConfigs.terrariaInterface || !appliesTo(screen)) {
            restore();
            return;
        }
        Options options = minecraft.options;
        KeyMapping[] keys = {options.keyUp, options.keyDown, options.keyLeft, options.keyRight, options.keyJump};
        boolean typing = screen.getFocused() instanceof EditBox box && box.isFocused();
        long window = minecraft.getWindow().getWindow();
        for (KeyMapping key : keys) {
            if (!SAVED.containsKey(key)) {
                SAVED.put(key, key.getKeyConflictContext());
                key.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
            }
            InputConstants.Key bound = key.getKey();
            boolean down = !typing && bound.getType() == InputConstants.Type.KEYSYM && bound.getValue() != InputConstants.UNKNOWN.getValue()
                    && InputConstants.isKeyDown(window, bound.getValue());
            key.setDown(down);
        }
    }

    private static void restore() {
        if (SAVED.isEmpty()) return;
        SAVED.forEach(KeyMapping::setKeyConflictContext);
        SAVED.clear();
    }
}
