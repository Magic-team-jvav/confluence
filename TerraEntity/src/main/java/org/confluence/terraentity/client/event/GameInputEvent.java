package org.confluence.terraentity.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.integration.ModChecker;
import org.confluence.terraentity.item.BaseWhipItem;
import org.confluence.terraentity.network.c2s.ServerBoundEventPacket;
import org.lwjgl.glfw.GLFW;

import static org.confluence.terraentity.TerraEntity.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class GameInputEvent {

    @SubscribeEvent
    public static void mouseScroll(InputEvent.MouseScrollingEvent event) {
        // 防止切鞭，非常超模
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();

        if (item instanceof BaseWhipItem whipItem && player.getCooldowns().isOnCooldown(whipItem)) {
            event.setCanceled(true);
            return;
        }

        if (!player.isSpectator() && item instanceof ILeftClickStateItem item1) {
            if (Minecraft.getInstance().mouseHandler.isLeftPressed()) { // 暂时可以这样写省性能，如果后面有新需求，需要注释掉
                if (event.getScrollDeltaY() > 0) {
                    ServerBoundEventPacket.wheelUp();
                } else {
                    ServerBoundEventPacket.wheelDown();
                }
            }

            if (!item1.canSwitchWithoutRelease(player, stack) && WeaponStorage.of(player).leftClicking) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void mouseClick(InputEvent.MouseButton.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && !Minecraft.getInstance().isPaused() && Minecraft.getInstance().screen == null && !player.isSpectator()) {
            WeaponStorage data = WeaponStorage.of(player);
            boolean clicking = data.leftClicking;
            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) { // 左键
                if (event.getAction() == InputConstants.RELEASE) { // 松开
                    data.leftClicking = false;
                    if (clicking) {
//                        player.sendSystemMessage(Component.literal("not clicking"));
                        ServerBoundEventPacket.mouseRelease();
                    }
                    return;
                }
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof ILeftClickStateItem) {
                    if (event.getAction() == InputConstants.PRESS) { // 按下
                        if (!clicking) {
                            data.leftClicking = true;
//                            player.sendSystemMessage(Component.literal("clicking"));
                            ServerBoundEventPacket.mouseLeftClick();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void KeyPressed(InputEvent.Key event) {
        if (event.getAction() == InputConstants.PRESS && ModChecker.curios.isLoaded()) {
            if (TEKeyBindings.RIDE.get().isDown()) { // 这个方法不会在其它地方触发导致崩溃
                ServerBoundEventPacket.rideOrLeave();
            }
        }
    }

    @SubscribeEvent
    public static void interactionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack() && event.getHand() == InteractionHand.MAIN_HAND) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof ILeftClickStateItem) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }
}
