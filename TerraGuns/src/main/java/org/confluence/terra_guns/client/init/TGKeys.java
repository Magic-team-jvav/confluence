package org.confluence.terra_guns.client.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class TGKeys {
    public static final Lazy<KeyMapping> SHOOT = Lazy.of(() -> new KeyMapping(
            "key.terra_guns.shoot",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            "key.categories.gameplay"
    ));

    public static final Lazy<KeyMapping> AIM = Lazy.of(() -> new KeyMapping(
            "key.terra_guns.aim",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            "key.categories.gameplay"
    ));
}
