package org.confluence.terra_curio.client.event;


import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.TCClientConfigs;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.client.handler.*;
import org.confluence.terra_curio.client.renderer.tooltip.MultiFunctionTooltip;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.mixin.client.accessor.MinecraftAccessor;
import org.confluence.terra_curio.network.c2s.ShootXBonePacketC2S;
import org.confluence.terra_curio.util.TCUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;

@EventBusSubscriber(modid = TerraCurio.MODID, value = Dist.CLIENT)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            GravitationHandler.reset();
            StepStoolHandler.reset();
            TCClientPacketHandler.reset();
            InformationHandler.reset();
            PlayerJumpHandler.reset(true);
            PlayerClimbHandler.reset();
            PlayerSprintingHandler.reset();
            ScopeFovHandler.reset();
        } else {
            GravitationHandler.tryExpire(player);
            StepStoolHandler.handle(player);
            TCClientPacketHandler.handle(minecraft, player);
            InformationHandler.handle(player);
            ScopeFovHandler.handle(player);
            TCUtils.applyCthulhuSprinting(TCKeyBindings.CTHULHU_SPRINTING.get().isDown(), player);
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = (LocalPlayer) event.getEntity();
        Input input = event.getInput();
        boolean jumping = input.jumping;

        MobEffectInstance effect = player.getEffect(TCEffects.GRAVITATION);
        if (effect != null) {
            if (effect.getAmplifier() > 0) {
                GravitationHandler.force(player);
            } else {
                GravitationHandler.handle(player);
            }
        } else if (GravitationHandler.isHasGlobe()) {
            GravitationHandler.handle(player);
        } else {
            GravitationHandler.expire();
        }

        PlayerJumpHandler.handle(player, jumping);
        PlayerClimbHandler.handle(player, input.getMoveVector(), jumping);

        if (TCClientPacketHandler.isHasTabi() /* confluence mixin here */) {
            PlayerSprintingHandler.handle(player, input);
        }
    }

    @SubscribeEvent
    public static void cameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (GravitationHandler.isShouldRot()) {
            event.setRoll(180.0F);
        }
    }

    @SubscribeEvent
    public static void fov(ComputeFovModifierEvent event) {
        if (ScopeFovHandler.isScoping()) {
            event.setNewFovModifier(ScopeFovHandler.getFovModifier());
        }
    }

    @SubscribeEvent
    public static void interactionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (TCClientConfigs.rightClickDelay && event.isUseItem() && player.getItemInHand(event.getHand()).getItem() instanceof BlockItem) {
            MinecraftAccessor instance = (MinecraftAccessor) Minecraft.getInstance();
            int delay = instance.getRightClickDelay() - TCClientPacketHandler.getRightClickSubtractor();
            instance.setRightClickDelay(Math.max(0, delay));
        }
        if (TCClientPacketHandler.isBoneGlove() && player.getMainHandItem().is(Tags.Items.TOOLS)) {
            PacketDistributor.sendToServer(ShootXBonePacketC2S.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void input$MouseScrolling(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && ScopeFovHandler.isScoping()) {
            ScopeFovHandler.handleScroll(player, event.getScrollDeltaY());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void screen$MouseScrolled$Pre(ScreenEvent.MouseScrolled.Pre event) {
        if (MultiFunctionTooltip.isShowing) {
            MultiFunctionTooltip.mouseScrollY -= (int) event.getScrollDeltaY();
        } else {
            MultiFunctionTooltip.mouseScrollY = 0;
        }
    }

    @SubscribeEvent
    public static void renderTooltip$GatherComponents(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().is(TCItems.DEMON_HEART)) {
            List<Either<FormattedText, TooltipComponent>> list = event.getTooltipElements();
            list.add(1, Either.left(Component.translatable("tooltip.item.terra_curio.demon_heart.0").withStyle(ChatFormatting.GREEN)));
            CuriosApi.getCuriosInventory(Minecraft.getInstance().player).ifPresent(iCuriosItemHandler -> {
                ICurioStacksHandler iCurioStacksHandler = iCuriosItemHandler.getCurios().get(TerraCurio.CURIO_SLOT);
                list.add(2, Either.left(Component.translatable(
                        "tooltip.item.terra_curio.demon_heart.1",
                        TCCommonConfigs.MAX_ACCESSORIES.get() - iCurioStacksHandler.getSlots()
                ).withStyle(ChatFormatting.GRAY)));
            });
        }
    }
}
