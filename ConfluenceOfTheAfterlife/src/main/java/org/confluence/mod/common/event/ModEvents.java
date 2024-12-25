package org.confluence.mod.common.event;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.advancement.ModAchievements;
import org.confluence.mod.common.block.common.AetheriumCauldronBlock;
import org.confluence.mod.common.block.common.HoneyCauldronBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.fluid.FluidBuilder;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.accessory.MusicBoxItem;
import org.confluence.mod.network.c2s.ApplySelectionPacketC2S;
import org.confluence.mod.network.c2s.HookThrowingPacketC2S;
import org.confluence.mod.network.c2s.ReplaceMusicBoxItemPacketC2S;
import org.confluence.mod.network.c2s.SwordShootingPacketC2S;
import org.confluence.mod.network.s2c.*;
import org.confluence.phase_journey.api.PhaseJourneyEvent;
import org.confluence.terra_curio.api.event.RegisterAccessoriesComponentUpdateEvent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCTabs;

import java.util.Map;

import static org.confluence.mod.Confluence.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonConfigs.onLoad();
            Confluence.registerGameRules();
            ModFluids.registerInteraction();
            ModFluids.registerShimmerTransform();
            ModAchievements.initialize();
            ModBiomes.registerRegionAndSurface();
        });
    }

    @SubscribeEvent
    public static void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            LogBlockSet.wrapStrip();
            ISpreadable.Type.buildMap();
            MusicBoxItem.initialize();
            ModRecipes.Brewing.initialize();
            CauldronInteraction.INTERACTIONS.values().forEach(map -> {
                Map<Item, CauldronInteraction> interactionMap = map.map();
                interactionMap.put(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), CauldronInteraction.FILL_WATER);
                interactionMap.put(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), CauldronInteraction.FILL_LAVA);
                interactionMap.put(ToolItems.BOTTOMLESS_HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
                interactionMap.put(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), AetheriumCauldronBlock.FILL_AETHERIUM);
                interactionMap.put(ToolItems.HONEY_BUCKET.get(), HoneyCauldronBlock.FILL_HONEY);
                interactionMap.put(NatureBlocks.AETHERIUM_BLOCK.asItem(), AetheriumCauldronBlock.FILL_AETHERIUM);
            });
        });
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {

    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        FluidBuilder.register(event);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ManaPacketS2C.TYPE, ManaPacketS2C.STREAM_CODEC, ManaPacketS2C::handle);
        registrar.playToClient(GamePhasePacketS2C.TYPE, GamePhasePacketS2C.STREAM_CODEC, GamePhasePacketS2C::handle);
        registrar.playToClient(FishingPowerInfoPacketS2C.TYPE, FishingPowerInfoPacketS2C.STREAM_CODEC, FishingPowerInfoPacketS2C::handle);
        registrar.playToClient(StarPhasesPacketS2C.TYPE, StarPhasesPacketS2C.STREAM_CODEC, StarPhasesPacketS2C::handle);
        registrar.playToClient(EchoVisibilityPacketS2C.TYPE, EchoVisibilityPacketS2C.STREAM_CODEC, EchoVisibilityPacketS2C::handle);
        registrar.playToClient(OpenSelectionsScreenPacketS2C.TYPE, OpenSelectionsScreenPacketS2C.STREAM_CODEC, OpenSelectionsScreenPacketS2C::handle);
        registrar.playToClient(MeteoriteLocationPacketS2C.TYPE, MeteoriteLocationPacketS2C.STREAM_CODEC, MeteoriteLocationPacketS2C::handle);
        registrar.playToClient(WindSpeedPacketS2C.TYPE, WindSpeedPacketS2C.STREAM_CODEC, WindSpeedPacketS2C::handle);
        registrar.playToClient(SecretFlagSyncPacketS2C.TYPE, SecretFlagSyncPacketS2C.STREAM_CODEC, SecretFlagSyncPacketS2C::handle);

        registrar.playToServer(SwordShootingPacketC2S.TYPE, SwordShootingPacketC2S.STREAM_CODEC, SwordShootingPacketC2S::receive);
        registrar.playToServer(HookThrowingPacketC2S.TYPE, HookThrowingPacketC2S.STREAM_CODEC, HookThrowingPacketC2S::handle);
        registrar.playToServer(ApplySelectionPacketC2S.TYPE, ApplySelectionPacketC2S.STREAM_CODEC, ApplySelectionPacketC2S::handle);
        registrar.playToServer(ReplaceMusicBoxItemPacketC2S.TYPE, ReplaceMusicBoxItemPacketC2S.STREAM_CODEC, ReplaceMusicBoxItemPacketC2S::handle);
    }

    @SubscribeEvent
    public static void registerUnitType(RegisterAccessoriesComponentUpdateEvent.UnitType event) {
        event.register(AccessoryItems.LUCKY$COIN);
        event.register(AccessoryItems.SHEARS$DIG);
        event.register(AccessoryItems.ICE$SAFE);
        event.register(AccessoryItems.AUTO$GET$MANA);
        event.register(AccessoryItems.HURT$GET$MANA);
        event.register(AccessoryItems.FAST$MANA$GENERATION);
        event.register(AccessoryItems.HIGH$TEST$FISHING$LINE);
        event.register(AccessoryItems.TACKLE$BOX);
        event.register(AccessoryItems.LAVAPROOF$FISHING$HOOK);
        event.register(AccessoryItems.SPECTRE$GOGGLES);
    }

    @SubscribeEvent
    public static void registerOtherType(RegisterAccessoriesComponentUpdateEvent.OtherType event) {
        event.register(AccessoryItems.ADDITIONAL$MANA);
        event.register(AccessoryItems.MANA$USE$REDUCE);
        event.register(AccessoryItems.MANA$PICKUP$RANGE);
        event.register(AccessoryItems.COIN$PICKUP$RANGE);
        event.register(AccessoryItems.REDUCE$HEALING$COOLDOWN);
        event.register(AccessoryItems.FISHING$POWER);
        event.register(AccessoryItems.SPECIAL$PRICE);
    }

    @SubscribeEvent
    public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TCTabs.ACCESSORIES.get()) {
            event.insertFirst(TCItems.BASE_POINT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertFirst(TCItems.EVERLASTING.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            Object[] entries = AccessoryItems.ITEMS.getEntries().toArray();
            for (int i = entries.length - 1; i > -1; i--) {
                DeferredItem<? extends Item> entry = (DeferredItem<? extends Item>) entries[i];
                event.insertFirst(entry.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    /**
     * @see org.confluence.mod.common.data.saved.ConfluenceData#increaseRevealStep
     */
    @SubscribeEvent
    public static void phaseJourney$Register(PhaseJourneyEvent.Register event) {
        BlockState target = Blocks.DEEPSLATE.defaultBlockState();
        int step = 0;
        for (int state = 0; state < 3; state++) {
            int finalState = state;
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_MITHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
            event.phaseRegister(Confluence.asResource("reveal_step_" + (step++)), context -> {
                context.blockReplacement(OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
                context.blockReplacement(OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, finalState), target);
            });
        }
    }
}
