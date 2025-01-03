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

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyBindings {
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(HOOK.get());
        event.register(SHOW_DETAIL_SPECULAR.get());
    }

    public static final Lazy<KeyMapping> HOOK = Lazy.of(() -> new KeyMapping(
            "key.confluence.hook",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "key.categories.gameplay"
    ));

    public static final Lazy<KeyMapping> SHOW_DETAIL_SPECULAR = Lazy.of(() -> new KeyMapping(
            "key.confluence.specular_detail",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "key.categories.gameplay"
    ));
}
