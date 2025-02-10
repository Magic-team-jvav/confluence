package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.carver.DemonicCaveCarver;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;

import java.util.function.Supplier;

public final class ModCarvers {
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(BuiltInRegistries.CARVER, Confluence.MODID);

    public static final Supplier<DemonicCaveCarver> DEMONIC_CAVE_CARVER = CARVERS.register("demonic_cave_carver", () -> new DemonicCaveCarver(DemonicCaveCarver.Config.CODEC));
    public static final Supplier<TheConstant.WavyCaveCarver> WAVY_CAVE_CARVER = CARVERS.register("wavy_cave_carver", () -> new TheConstant.WavyCaveCarver(TheConstant.WavyCaveCarver.Config.CODEC));
}
