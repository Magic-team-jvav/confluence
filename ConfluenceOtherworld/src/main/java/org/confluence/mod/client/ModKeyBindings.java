package org.confluence.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.confluence.mod.Confluence;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

    private static Lazy<KeyMapping> register(Supplier<KeyMapping> supplier) {
        Lazy<KeyMapping> lazy = Lazy.of(supplier);
        keyMappings.add(lazy);
        return lazy;
    }
}
