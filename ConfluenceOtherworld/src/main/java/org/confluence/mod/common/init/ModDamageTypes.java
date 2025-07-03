package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.terraentity.init.TETags;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> FALLING_STAR = register("falling_star");
    public static final ResourceKey<DamageType> ACID_VENOM = register("acid_venom");
    public static final ResourceKey<DamageType> CURSED_INFERNO = register("cursed_inferno");
    public static final ResourceKey<DamageType> BOULDER = register("boulder");
    public static final ResourceKey<DamageType> DARKNESS = register("darkness");
    public static final ResourceKey<DamageType> MAGICAL_PROJECTILE = register("magical_projectile");
    public static final ResourceKey<DamageType> SWORD_PROJECTILE = register("sword_projectile");


    private static ResourceKey<DamageType> register(String id) {
        return Confluence.asResourceKey(Registries.DAMAGE_TYPE, id);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return of(level, key, null, null);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity causing) {
        return of(level, key, causing, causing);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity direct, Entity causing) {
        return level.damageSources().source(key, direct, causing);
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        TETags.DamageTypes.createDamageTypes(context);
        context.register(SWORD_PROJECTILE, new DamageType("sword_projectile_damage_type", 0.1F));

    }
}
