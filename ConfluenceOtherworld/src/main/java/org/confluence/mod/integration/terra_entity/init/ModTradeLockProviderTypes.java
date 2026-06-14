package org.confluence.mod.integration.terra_entity.init;

import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.*;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.drawer.*;
import org.confluence.terraentity.api.npc.trade.TradeLockRecipeDrawer;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.function.Supplier;

public class ModTradeLockProviderTypes {
    public static final DeferredRegister<TradeLockProvider> TYPES = DeferredRegister.create(TERegistries.TRADE_LOCK_PROVIDERS, Confluence.MODID);

    public static final Supplier<TradeLockProvider> MOON_PHASE_LOCK = TYPES.register("moon_phase_lock", () -> new TradeLockProvider(MoonPhaseLock.CODEC, new MoonPhaseLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> SECRET_FLAG_LOCK = TYPES.register("secret_flag_lock", () -> new TradeLockProvider(SecretFlagLock.CODEC, new SecretFlagLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> CONDITIONS_LOCK = TYPES.register("conditions_lock", () -> new TradeLockProvider(ConditionsLock.CODEC, new ConditionsLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> POSITION_LOCK = TYPES.register("position_lock", () -> new TradeLockProvider(PositionLock.CODEC, new PositionLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> DATE_LOCK = TYPES.register("date_lock", () -> new TradeLockProvider(DateLock.CODEC, new DateLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> DIMENSION_LOCK = TYPES.register("dimension_lock", () -> new TradeLockProvider(DimensionLock.CODEC, new DimensionLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> ENVIRONMENT_LOCK = TYPES.register("environment_lock", () -> new TradeLockProvider(EnvironmentLock.CODEC, new EnvironmentLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> FISHING_HOOK_IN_FLUID_LOCK = TYPES.register("fishing_hook_in_fluid_lock", () -> new TradeLockProvider(FishingHookInFluidLock.CODEC, new FishingHookInFluidLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> ANY_BOSS_DEFEATED_LOCK = TYPES.register("any_boss_defeated_lock", () -> new TradeLockProvider(AnyBossDefeatedLock.CODEC, new AnyBossDefeatedLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> BESTIARY_UNLOCKED_COUNT_LOCK = TYPES.register("bestiary_unlocked_count_lock", () -> new TradeLockProvider(BestiaryUnlockedCountLock.CODEC, new BestiaryUnlockedCountLockRecipeDrawer()));
    public static final Supplier<TradeLockProvider> QUESTED_FISH_PRECHECK_LOCK = TYPES.register("quested_fish_precheck_lock", () -> new TradeLockProvider(QuestedFishPrecheckLock.CODEC, TradeLockRecipeDrawer.EMPTY));
    public static final Supplier<TradeLockProvider> GAME_EVENT_LOCK = TYPES.register("game_event_lock", () -> new TradeLockProvider(GameEventLock.CODEC, new GameEventLockRecipeDrawer()));
}
