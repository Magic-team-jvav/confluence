package org.confluence.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.util.Lazy;
import org.confluence.mod.Confluence;
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

    public static final Lazy<KeyMapping> GUN_SHOOT = register(() -> new KeyMapping("key.confluence.shoot", KeyConflictContext.IN_GAME, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, KEY_BINDINGS_CATEGORY));
    public static final Lazy<KeyMapping> GUN_AIM = register(() -> new KeyMapping("key.confluence.aim", KeyConflictContext.IN_GAME, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_BINDINGS_CATEGORY));
    public static final Lazy<KeyMapping> GUN_INSPECT = register(() -> new KeyMapping("key.confluence.inspect", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_BINDINGS_CATEGORY));

    //region 魔法系列

    /// 灵魂总览（魂师大界面）：默认 Shift + C
    public static final Lazy<KeyMapping> SOUL_OVERVIEW = Confluence.SOUL_SKILLS ? register(() -> new KeyMapping(
            "key.confluence.soulcerer.overview",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_BINDINGS_CATEGORY
    )) : null;

    /// 切换灵魂技能：默认**左 Alt**，按住时弹出快捷技能面板（小轮盘 / 大轮盘 / 横向卡片），松开确认。
    ///
    /// 原来这里有两份绑定（「灵魂快捷技能切换」与「释放灵魂技能」），但只有前者被真正读取，
    /// 改后者完全没效果；现已合并成这一条，界面里显示为「切换灵魂技能」。
    public static final Lazy<KeyMapping> MAGIC_SKILL_SWITCHING = Confluence.SOUL_SKILLS ? register(() -> new KeyMapping(
            "key.confluence.soulcerer.skill_switching",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            KEY_BINDINGS_CATEGORY
    )) : null;

    //endregion

    /// 快捷钩爪默认键：与 1.21.1 对齐为 R。
    public static final Lazy<KeyMapping> HOOK = register(() -> new KeyMapping("key.confluence.hook", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_BINDINGS_CATEGORY));

    /// 快捷坐骑只发送切换请求，实际槽位物品和实体类型由服务端重新读取。
    public static final Lazy<KeyMapping> MOUNT = register(() -> new KeyMapping("key.confluence.mount", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_BINDINGS_CATEGORY));
    public static final Lazy<KeyMapping> MOUNT_DESCEND = register(() -> new KeyMapping("key.confluence.mount_descend", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, KEY_BINDINGS_CATEGORY));

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

    /// 快捷使用增益药水与食物，默认不绑定按键。
    public static final Lazy<KeyMapping> QUICK_BUFF = register(() -> new KeyMapping(
            "key.confluence.quick_buff",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
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

    private static Lazy<KeyMapping> register(Supplier<KeyMapping> supplier) {
        Lazy<KeyMapping> lazy = Lazy.of(supplier);
        keyMappings.add(lazy);
        return lazy;
    }
}
