package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.carver.*;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModCarvers {
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(BuiltInRegistries.CARVER, Confluence.MODID);

    public static final Supplier<DemonicCaveCarver> DEMONIC_CAVE_CARVER = CARVERS.register("demonic_cave_carver", () -> new DemonicCaveCarver(DemonicCaveCarver.Config.CODEC));
    public static final Supplier<WavyCaveCarver> WAVY_CAVE_CARVER = CARVERS.register("wavy_cave_carver", () -> new WavyCaveCarver(WavyCaveCarver.Config.CODEC));
    public static final Supplier<DesertCaveCarver> DESERT_CAVE_CARVER = CARVERS.register("desert_cave_carver", () -> new DesertCaveCarver(DesertCaveCarver.Config.CODEC));
    public static final Supplier<GlowingMushroomCaveCarver> GLOWING_MUSHROOM_CAVE_CARVER = CARVERS.register("glowing_mushroom_cave_carver", () -> new GlowingMushroomCaveCarver(GlowingMushroomCaveCarver.Config.CODEC));
    public static final Supplier<JungleCaveCarver> JUNGLE_CAVE_CARVER = CARVERS.register("jungle_cave_carver", () -> new JungleCaveCarver(CaveCarverConfiguration.CODEC));

    public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_DEMONIC_CAVE_CARVER = ResourceKey.create(Registries.CONFIGURED_CARVER, Confluence.asResource("demonic_cave_carver"));
    public static final ResourceKey<ConfiguredWorldCarver<?>> CONFIGURED_GLOWING_MUSHROOM_CAVE_CARVER = ResourceKey.create(Registries.CONFIGURED_CARVER, Confluence.asResource("glowing_mushroom_cave_carver"));
}
