package org.confluence.terra_curio.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.terra_curio.TerraCurio;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public final class TCDamageTypes {
    public static final ResourceKey<DamageType> STAR_CLOAK = register("star_cloak");

    private static ResourceKey<DamageType> register(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(TerraCurio.MODID, id));
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return of(level, key, null, null);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity causing) {
        return of(level, key, causing, causing);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity causing, Entity direct) {
        return level.damageSources().source(key, direct, causing);
    }
}
