package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.carver.*;

public final class ModCarvers {
    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(Registries.CARVER, Confluence.MODID);

    public static final RegistryObject<DemonicCaveCarver> DEMONIC_CAVE_CARVER = CARVERS.register("demonic_cave_carver", () -> new DemonicCaveCarver(DemonicCaveCarver.Config.CODEC));
    public static final RegistryObject<WavyCaveCarver> WAVY_CAVE_CARVER = CARVERS.register("wavy_cave_carver", () -> new WavyCaveCarver(CarverConfiguration.CODEC.codec()));
    public static final RegistryObject<DesertCaveCarver> DESERT_CAVE_CARVER = CARVERS.register("desert_cave_carver", () -> new DesertCaveCarver(CarverConfiguration.CODEC.codec()));
    public static final RegistryObject<GlowingMushroomCaveCarver> GLOWING_MUSHROOM_CAVE_CARVER = CARVERS.register("glowing_mushroom_cave_carver", () -> new GlowingMushroomCaveCarver(CarverConfiguration.CODEC.codec()));
    public static final RegistryObject<JungleCaveCarver> JUNGLE_CAVE_CARVER = CARVERS.register("jungle_cave_carver", () -> new JungleCaveCarver(CaveCarverConfiguration.CODEC));
    public static final RegistryObject<DrySeaCarver> DRY_SEA_CARVER = CARVERS.register("dry_sea_carver", () -> new DrySeaCarver(CarverConfiguration.CODEC.codec()));
}
