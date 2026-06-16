package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> ACID_VENOM = register("acid_venom");
    public static final ResourceKey<DamageType> BOULDER = register("boulder");
    public static final ResourceKey<DamageType> CURSED_INFERNO = register("cursed_inferno");
    public static final ResourceKey<DamageType> DARKNESS = register("darkness");
    public static final ResourceKey<DamageType> FALLING_STAR = register("falling_star");
    public static final ResourceKey<DamageType> MAGICAL_PROJECTILE = register("magical_projectile");
    public static final ResourceKey<DamageType> SWORD_PROJECTILE = register("sword_projectile");
    public static final ResourceKey<DamageType> SPEAR_PROJECTILE = register("spear_projectile");

    private static ResourceKey<DamageType> register(String id) {
        return Confluence.asResourceKey(Registries.DAMAGE_TYPE, id);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key) {
        return of(level, key, null, null);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, @Nullable Entity causing) {
        return of(level, key, causing, causing);
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Entity causing) {
        return LibUtils.damageSource(level, key, direct, causing);
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        damageType(context, ACID_VENOM, DamageScaling.ALWAYS, 10);
        damageType(context, BOULDER, DamageScaling.ALWAYS, 5);
        damageType(context, CURSED_INFERNO, DamageScaling.ALWAYS, 10, DamageEffects.BURNING);
        damageType(context, DARKNESS, DamageScaling.ALWAYS, 20);
        damageType(context, FALLING_STAR, DamageScaling.ALWAYS, 10);
        damageType(context, MAGICAL_PROJECTILE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F);
        damageType(context, SWORD_PROJECTILE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F);
        damageType(context, SPEAR_PROJECTILE, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F);
    }

    private static void damageType(BootstapContext<DamageType> context, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {
        context.register(key, new DamageType(key.location().getPath(), scaling, exhaustion, effects, deathMessageType));
    }

    private static void damageType(BootstapContext<DamageType> context, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects) {
        damageType(context, key, scaling, exhaustion, effects, DeathMessageType.DEFAULT);
    }

    private static void damageType(BootstapContext<DamageType> context, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion) {
        damageType(context, key, scaling, exhaustion, DamageEffects.HURT, DeathMessageType.DEFAULT);
    }
}
