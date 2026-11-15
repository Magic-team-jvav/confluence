package org.confluence.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.confluence.mod.Confluence;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Confluence.MODID, value = Dist.CLIENT)
public final class ModKeyBindings {
    public static final String KEY_BINDINGS_CATEGORY = "key.confluence.gameplay";
    private static List<Lazy<KeyMapping>> keyMappings = new LinkedList<>();

    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        for (Lazy<KeyMapping> lazy : keyMappings) {
            event.register(lazy.get());
        }
        keyMappings = null;
    }

    public static final Lazy<KeyMapping> GUN_SHOOT = register(() -> new KeyMapping(
            "key.confluence.shoot",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> GUN_AIM = register(() -> new KeyMapping(
            "key.confluence.aim",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> GUN_INSPECT = register(() -> new KeyMapping(
            "key.confluence.inspect",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_BINDINGS_CATEGORY
    ));

    //region 魔法系列

    /// 灵魂总览（魂师大界面）：默认 Shift + C
    public static final Lazy<KeyMapping> SOUL_OVERVIEW = register(() -> new KeyMapping(
            "key.confluence.soulcerer.overview",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_BINDINGS_CATEGORY
    ));

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

    public static final Lazy<KeyMapping> HOOK = register(() -> new KeyMapping(
            "key.confluence.hook",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> MOUNT = register(() -> new KeyMapping(
            "key.confluence.mount",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_BINDINGS_CATEGORY
    ));

    public static final Lazy<KeyMapping> MOUNT_DESCEND = register(() -> new KeyMapping(
            "key.confluence.mount_descend",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
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
