package org.confluence.mod.common.event.game;

import com.xiaohunao.equipment_benediction.common.event.AfterEquipmentBenedictionUpdatedEvent;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.heaven_destiny_moment.common.event.MomentEvent;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstance;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.data.AchievementOffsetLoader;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModCommands;
import org.confluence.mod.common.init.ModHookTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.AchievementOffsetSyncPacketS2C;
import org.confluence.mod.network.s2c.ExtraInventorySyncPacketS2C;
import org.confluence.mod.network.s2c.FishingPowerInfoPacketS2C;
import org.confluence.mod.network.s2c.VisibilityPacketS2C;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.AfterAccessoryAbilitiesFlushedEvent;
import org.confluence.terra_guns.api.event.GunEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Confluence.MODID)
public final class GameEvents {
    @SubscribeEvent
    public static void afterAccessoryAbilitiesFlushed(AfterAccessoryAbilitiesFlushedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaStorage.of(player).flushAbility(player);
            FishingPowerInfoPacketS2C.sendAndGet(player);
            VisibilityPacketS2C.sendEcho(player);
        }
    }

    @SubscribeEvent
    public static void afterEquipmentBenedictionUpdated(AfterEquipmentBenedictionUpdatedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaStorage.of(player).flushAbility(player);
        }
    }

    @SubscribeEvent
    public static void command(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void curioAttributeModifier(CurioAttributeModifierEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
        if (prefix == null) return;
        String suffix = "_" + event.getSlotContext().index();
        for (Map.Entry<Holder<Attribute>, Collection<AttributeModifier>> entry : prefix.modifiers().get().asMap().entrySet()) {
            Holder<Attribute> attribute = entry.getKey();
            for (AttributeModifier modifier : entry.getValue()) {
                event.addModifier(attribute, new AttributeModifier(modifier.id().withSuffix(suffix), modifier.amount(), modifier.operation()));
            }
        }
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        if (CommonConfigs.BREWING_STAND_RECIPE.get()) {
            ModRecipes.Brewing.registerRecipes(event.getBuilder()::addRecipe);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer from = event.getPlayer();
        if (from == null) {
            for (ServerPlayer to : event.getPlayerList().getPlayers()) {
                ExtraInventorySyncPacketS2C.sendToPlayersTrackingEntityAndSelf(to, to, ExtraInventory.of(to));
            }
        } else {
            ExtraInventorySyncPacketS2C.sendToClient(from, from, ExtraInventory.of(from)); // 需要晚于Curios Api
            AchievementOffsetSyncPacketS2C.sendToClient(from);
        }
    }

    @SubscribeEvent
    public static void moment$End(MomentEvent.End event) {
        MomentInstance momentInstance = event.getMomentInstance();
        if (momentInstance.getLevel() instanceof ServerLevel) {
            if (momentInstance.getMoment() == TMMoments.BLOOD_MOON.get()) {
                for (Player player : momentInstance.getPlayers()) {
                    AchievementUtils.awardAchievement((ServerPlayer) player, "bloodbath");
                }
                KillBoard.INSTANCE.defeat(momentInstance.getMoment());
            } else if (momentInstance.getMoment() == TMMoments.GOBLIN_ARMY.get()) {
                for (Player player : momentInstance.getPlayers()) {
                    AchievementUtils.awardAchievement((ServerPlayer) player, "goblin_punter");
                }
                KillBoard.INSTANCE.defeat(momentInstance.getMoment());
            }
        }
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && PrefixUtils.canInit(event.getTo())) {
            PrefixUtils.initPrefix(player.getRandom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void shimmerItemTransmutation$Post(ShimmerItemTransmutationEvent.Post event) {
        MinecraftServer currentServer;
        if (event.getTargets() != null && (currentServer = ServerLifecycleHooks.getCurrentServer()) != null) {
            boolean corruption = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CORRUPTION);
            boolean crimson = IMinecraftServer.matchesSecretFlag(currentServer, IWorldOptions.THE_CRIMSON);
            if (corruption != crimson) {
                List<ItemStack> targets = new ArrayList<>();
                for (ItemStack target : event.getTargets()) {
                    if (corruption && target.is(MaterialItems.CRIMTANE_INGOT)) {
                        targets.add(MaterialItems.DEMONITE_INGOT.toStack(target.getCount()));
                    } else if (crimson && target.is(MaterialItems.DEMONITE_INGOT)) {
                        targets.add(MaterialItems.CRIMTANE_INGOT.toStack(target.getCount()));
                    } else {
                        targets.add(target);
                    }
                }
                event.setTargets(targets);
            }
        }
    }

    @SubscribeEvent
    public static void gun$ShrinkBullet(GunEvent.ShrinkBulletEvent event) {
        if (event.isInfinity()) return;
        HookMapManager.postHooks(ModHookTypes.SKIP_AMMO_CONSUME.get(), (owner, hook, original) -> {
            if (hook.shouldSkipConsume(owner, original.getPlayer(), original.getBulletStack())) {
                original.setCanceled(true);
            }
            return original;
        }, event.getPlayer(), event);
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(AchievementOffsetLoader.getInstance());
    }

    @SubscribeEvent
    public static void additionalMana(AdditionalManaEvent event) {
        ArsNouveauHelper.additionalMana(event);
        IronSpellHelper.additionalMana(event);
    }
}
