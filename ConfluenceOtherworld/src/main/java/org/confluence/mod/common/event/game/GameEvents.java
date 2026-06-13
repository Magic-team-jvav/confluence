package org.confluence.mod.common.event.game;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.init.ModCommands;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.network.s2c.AchievementOffsetSyncPacketS2C;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PrefixUtils;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.brewing.PortRegisterBrewingRecipesEvent;
import org.mesdag.portlib.event.other.PortAddReloadListenerEvent;
import org.mesdag.portlib.event.other.PortOnDatapackSyncEvent;
import org.mesdag.portlib.event.other.PortRegisterCommandsEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.Collection;
import java.util.Map;

public final class GameEvents {
    public static void init() {
        PortEventHandler.addListener(GameEvents::command);
        PortEventHandler.addListener(GameEvents::curioAttributeModifier);
        PortEventHandler.addListener(GameEvents::registerBrewingRecipes);
        PortEventHandler.addListener(PortEventPriority.LOW, GameEvents::onDatapackSync); // 需晚于Curios API
        PortEventHandler.addListener(GameEvents::addReloadListener);
    }

    private static void command(PortRegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    private static void curioAttributeModifier(CurioAttributeModifierEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
        if (prefix == null) return;
        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Attribute attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, modifier);
            }
        }
    }

    private static void registerBrewingRecipes(PortRegisterBrewingRecipesEvent event) {
        if (StartupConfigs.brewingStandRecipe()) {
            ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
        }
    }

    private static void onDatapackSync(PortOnDatapackSyncEvent event) {
        ServerPlayer sendTo = event.getPlayer();
        if (sendTo == null) {
            for (ServerPlayer target : event.getPlayerList().getPlayers()) {
                ExtraInventorySyncPacketS2C.sendToPlayersTrackingEntityAndSelf(target, target);
            }
        } else {
            ExtraInventorySyncPacketS2C.sendToClient(sendTo, sendTo);
            AchievementOffsetSyncPacketS2C.sendToClient(sendTo);
            AchievementUtils.setData(sendTo);
        }
    }

    private static void addReloadListener(PortAddReloadListenerEvent event) {
        event.addListener(AchievementOffsetLoader.getInstance());
    }
}
