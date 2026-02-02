package org.confluence.terra_curio.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.confluence.terra_curio.TerraCurio;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = TerraCurio.MODID, value = Dist.CLIENT)
public final class TCKeyBindings {
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(METAL_DETECTOR.get());
        event.register(STEP_STOOL.get());
        event.register(FLIP_GRAVITATION.get());
        event.register(CTHULHU_SPRINTING.get());
    }

    public static final Lazy<KeyMapping> METAL_DETECTOR = Lazy.of(() -> new KeyMapping(
            "key.terra_curio.metal_detector",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            category()
    ));

    public static final Lazy<KeyMapping> STEP_STOOL = Lazy.of(() -> new KeyMapping(
            "key.terra_curio.step_stool",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            category()
    ));

    public static final Lazy<KeyMapping> FLIP_GRAVITATION = Lazy.of(() -> new KeyMapping(
            "key.terra_curio.flip_gravitation",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            category()
    ));

    public static final Lazy<KeyMapping> CTHULHU_SPRINTING = Lazy.of(() -> new KeyMapping(
            "key.terra_curio.cthulhu_sprinting",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            category()
    ));

    private static String category() {
        return "key.terra_curio.gameplay"; // confluence mixin here
    }
}
