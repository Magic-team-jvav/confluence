package org.confluence.mod.integration.terra_curio;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terra_curio.TerraCurio;

import java.util.ArrayList;
import java.util.List;

public final class TCRemoval {
    public static final List<ResourceLocation> RECIPES = Util.make(new ArrayList<>(), list -> {
        list.add(TerraCurio.asResource("blizzard_in_a_bottle"));
        list.add(TerraCurio.asResource("life_form_analyzer"));
        list.add(TerraCurio.asResource("obsidian_skull"));
        list.add(TerraCurio.asResource("workshop"));
        list.add(TerraCurio.asResource("flurry_boots"));
        list.add(TerraCurio.asResource("ice_skates"));
        list.add(TerraCurio.asResource("rocket_boots"));
        list.add(TerraCurio.asResource("stopwatch"));
        list.add(TerraCurio.asResource("avenger_emblem"));
        list.add(TerraCurio.asResource("architect_gizmo_pack"));
        list.add(TerraCurio.asResource("anklet_of_the_wind"));
        list.add(TerraCurio.asResource("ankh_charm"));
    });
}
