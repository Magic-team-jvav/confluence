package org.confluence.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.client.PortRegisterKeyMappingsEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public final class ModKeyBindings {
    public static final String KEY_BINDINGS_CATEGORY = "key.confluence.gameplay";
    private static List<Lazy<KeyMapping>> keyMappings = new LinkedList<>();

    public static void init() {
        PortEventHandler.addListener(ModKeyBindings::keyBinding);
    }

    private static void keyBinding(PortRegisterKeyMappingsEvent event) {
        for (Lazy<KeyMapping> lazy : keyMappings) {
            event.register(lazy.get());
        }
        keyMappings = null;
    }

    // region 魔法系列

    /// 灵魂总览
    public static final Lazy<KeyMapping> SOUL_OVERVIEW = register(() -> new KeyMapping(
            "key.confluence.soul.overview",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_BINDINGS_CATEGORY
    ));

    /// 灵魂快捷技能切换
    public static final Lazy<KeyMapping> MAGIC_QUICK_SKILL_SWITCHING = register(() -> new KeyMapping(
            "key.confluence.soul.quick_skill_switching",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            KEY_BINDINGS_CATEGORY
    ));

    /// 魔法技能释放
    public static final Lazy<KeyMapping> MAGIC_SKILL_RELEASE = register(() -> new KeyMapping(
            "key.confluence.magic.skill_release",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            KEY_BINDINGS_CATEGORY
    ));

    // endregion
    public static final Lazy<KeyMapping> HOOK = register(() -> new KeyMapping(
            "key.confluence.hook",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> SHOW_DETAIL_SPECULAR = register(() -> new KeyMapping(
            "key.confluence.specular_detail",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> HEALING = register(() -> new KeyMapping(
            "key.confluence.healing",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> MANA = register(() -> new KeyMapping(
            "key.confluence.mana",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_4,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> EXTRA_INVENTORY = register(() -> new KeyMapping(
            "key.confluence.extra_inventory",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KEY_BINDINGS_CATEGORY
    ));

    // 枪械按键
    public static final Lazy<KeyMapping> GUN_SHOOT = register(() -> new KeyMapping(
            "key.confluence.gun.shoot",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> GUN_AIM = register(() -> new KeyMapping(
            "key.confluence.gun.aim",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            KEY_BINDINGS_CATEGORY
    ));

    private static Lazy<KeyMapping> register(Supplier<KeyMapping> supplier) {
        Lazy<KeyMapping> lazy = Lazy.of(supplier);
        keyMappings.add(lazy);
        return lazy;
    }
}
