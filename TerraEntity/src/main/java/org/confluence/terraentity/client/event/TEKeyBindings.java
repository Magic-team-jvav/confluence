package org.confluence.terraentity.client.event;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.confluence.terraentity.TerraEntity;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

@EventBusSubscriber(modid = TerraEntity.MODID, value = Dist.CLIENT)
public final class TEKeyBindings {
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(RIDE.get());

    }

    public static final Supplier<KeyMapping> RIDE = Suppliers.memoize(() -> new KeyMapping(
            "key.terra_entity.ride",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            gameplay()
    ));


    private static String gameplay() {
        return "key.terra_entity.gameplay"; // confluence mixin here
    }
}
