package org.confluence.mod.integration.terra_entity.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.ConditionsLock;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.MoonPhaseLock;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.SecretFlagLock;
import org.confluence.mod.integration.terra_entity.npc_trade_lock.TrueLock;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.function.Supplier;

public class ModTradeLockProviderTypes {
    public static final DeferredRegister<TradeLockProvider> TYPES = DeferredRegister.create(TERegistries.TradeLockProviders.REGISTRY, Confluence.MODID);

    public static final Supplier<TradeLockProvider> MOON_PHASE_LOCK = TYPES.register("moon_phase_lock", () -> new TradeLockProvider(MoonPhaseLock.CODEC));
    public static final Supplier<TradeLockProvider> SECRET_FLAG_LOCK = TYPES.register("secret_flag_lock", () -> new TradeLockProvider(SecretFlagLock.CODEC));
    public static final Supplier<TradeLockProvider> CONDITIONS_LOCK = TYPES.register("conditions_lock", () -> new TradeLockProvider(ConditionsLock.CODEC));
    public static final Supplier<TradeLockProvider> TRUE_LOCK = TYPES.register("true_lock", () -> new TradeLockProvider(TrueLock.CODEC));
}
