package org.confluence.mod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.client.handler.HookThrowingHandler;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.terra_curio.api.event.PerformJumpingEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        ProjectileStrategy.handle(minecraft, player);
        HookThrowingHandler.handle(player);
        WeatherHandler.handleWind(minecraft, player);
    }

    @SubscribeEvent
    public static void leftClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) return;
        if (event.isUseItem() || event.isAttack() || event.isPickBlock()) {
            if (localPlayer.hasEffect(ModEffects.STONED) || localPlayer.hasEffect(ModEffects.CURSED)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void renderGuiOverlay$Pre(RenderGuiLayerEvent.Pre event) {
        if (ClientConfigs.terraStyleHealth && VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void performJumping(PerformJumpingEvent event) {
        if (event.isCanPerform() && event.getEntity().hasEffect(ModEffects.SHIMMER)) {
            event.setCanPerform(false);
        }
    }

    @SubscribeEvent
    public static void renderHandEvent(RenderHandEvent event) {
        ArrowInBowHud.render(event);
    }
}