package org.confluence.mod.common.init;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.feature.ModFeatures;

import java.util.Optional;

public final class ModTreeGrowers {
    public static final TreeGrower SHADOW_GROWER = register("shadow", ModFeatures.SHADOW);
    public static final TreeGrower EBONY_GROWER = register("ebony", ModFeatures.EBONY);
    public static final TreeGrower PALM_GROWER = register("palm", ModFeatures.PALM);
    public static final TreeGrower PEARL_GROWER = register("pearl", ModFeatures.PEARL);
    public static final TreeGrower RUBY_GROWER = register("ruby", ModFeatures.RUBY);
    public static final TreeGrower AMBER_GROWER = register("amber", ModFeatures.AMBER);
    public static final TreeGrower TOPAZ_GROWER = register("topaz", ModFeatures.TOPAZ);
    public static final TreeGrower EMERALD_GROWER = register("emerald", ModFeatures.EMERALD);
    public static final TreeGrower DIAMOND_GROWER = register("diamond", ModFeatures.DIAMOND);
    public static final TreeGrower SAPPHIRE_GROWER = register("sapphire", ModFeatures.SAPPHIRE);
    public static final TreeGrower TR_AMETHYST_GROWER = register("tr_amethyst", ModFeatures.TR_AMETHYST);
    public static final TreeGrower ASH_GROWER = register("ash", ModFeatures.ASH);
    public static final TreeGrower LIVING_GROWER = register("living", ModFeatures.LIVING);

    private static TreeGrower register(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
        return new TreeGrower(Confluence.MODID + ":" + name, Optional.empty(), Optional.of(tree), Optional.empty());
    }
}
