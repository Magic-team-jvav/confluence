package org.confluence.mod.common.event.game;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.init.ModCommands;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.network.s2c.AchievementOffsetSyncPacketS2C;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PrefixUtils;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.Collection;
import java.util.Map;

@EventBusSubscriber(modid = Confluence.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void command(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void curioAttributeModifier(CurioAttributeModifierEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
        if (prefix == null) return;
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, new AttributeModifier(modifier.id(), modifier.amount(), modifier.operation()));
            }
        }
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        if (StartupConfigs.brewingStandRecipe()) {
            ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW) // 需要晚于Curios Api
    public static void onDatapackSync(OnDatapackSyncEvent event) {
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

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(AchievementOffsetLoader.getInstance());
    }
}
