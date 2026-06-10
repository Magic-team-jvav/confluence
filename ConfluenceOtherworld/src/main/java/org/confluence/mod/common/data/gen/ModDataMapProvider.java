package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.confluence.mod.common.data.gen.data_map.*;
import org.confluence.mod.common.init.ModDataMaps;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.datamap.PortDataMapType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModDataMapProvider extends PortDataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        ValueSubProvider.gather(builder(ModDataMaps.VALUE, ValueSubProvider.Builder::new));
        TreasureBagSubProvider.gather(() -> builder(ModDataMaps.TREASURE_BAG));
        ExtractinatorSubProvider.gather(() -> builder(ModDataMaps.EXTRACTINATOR));
        ChlorophyteExtractinatorSubProvider.gather(() -> builder(ModDataMaps.CHLOROPHYTE_EXTRACTINATOR));
        ImmunitySubProvider.gather(() -> builder(ModDataMaps.IMMUNITY));
        DiggingPowerProvider.gather(() -> builder(ModDataMaps.DIGGING_POWER));
        BugNetEntityToItemSubProvider.gather(builder(ModDataMaps.BUG_NET_ENTITY_TO_ITEM, BugNetEntityToItemSubProvider.Builder::new));
        FurnaceFuelSubProvider.gather(() -> builder(NeoForgeDataMaps.FURNACE_FUELS));
        LivingInvulnerableEffectsSubProvider.gather(builder(ModDataMaps.LIVING_INVULNERABLE_EFFECTS, LivingInvulnerableEffectsSubProvider.Builder::new), provider);
        BlockBreakSpawnsSubProvider.gather(builder(ModDataMaps.BLOCK_BREAK_SPAWNS, BlockBreakSpawnsSubProvider.Builder::new), provider);
        GamePhase2AttributeModifiersSubProvider.gather(builder(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS, GamePhase2AttributeModifiersSubProvider.Builder::new));
    }

    @SuppressWarnings("unchecked")
    protected <T, R, B extends PortDataMapProvider.Builder<T, R>> Appender<B> builder(PortDataMapType<R, T> type, Supplier<B> supplier) {
        return () -> (B) builders.computeIfAbsent(type, dataMapType -> supplier.get());
    }

    @FunctionalInterface
    public interface Appender<T extends PortDataMapProvider.Builder<?, ?>> {
        T create();
    }
}
