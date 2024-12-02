package org.confluence.mod.client.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.client.handler.HookThrowingHandler;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PerformJumpingEvent;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        ProjectileStrategy.handle(minecraft, player);
        HookThrowingHandler.handle(player);
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

    @SubscribeEvent
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (tooltipElements.isEmpty()) return;
        Optional<FormattedText> displayName = tooltipElements.getFirst().left();
        if (displayName.isPresent() && displayName.get() instanceof Component component) {
            PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                ChatFormatting format = prefix.tier() >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                tooltipElements.set(0, Either.left(
                        Component.translatable("prefix.confluence." + prefix.name()).withStyle(format).append(" ").append(component)
                ));
            }
        }
    }

    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null) return;

        if (prefix.type() == PrefixType.MAGIC) {
            if (prefix.manaCost() != 0.0) {
                boolean b = prefix.manaCost() > 0.0;
                event.getToolTip().add(Component.translatable(
                        "prefix.confluence.tooltip." + (b ? "plus" : "take"),
                        ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (b ? 100.0 : -100.0)),
                        Component.translatable("prefix.confluence.tooltip.mana_cost")
                ).withStyle(b ? ChatFormatting.RED : ChatFormatting.BLUE));
            }
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            if (prefix.additionalMana() > 0) {
                event.getToolTip().add(Component.translatable(
                        "prefix.confluence.tooltip.add",
                        prefix.additionalMana(),
                        Component.translatable("prefix.confluence.tooltip.additional_mana")
                ).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}