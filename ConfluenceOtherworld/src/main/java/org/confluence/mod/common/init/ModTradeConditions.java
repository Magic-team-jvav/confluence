package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.*;

/// 贸易条件的 `MapCodec` 注册层（1.20 `common/init/ModTradeConditions.java` 逐条对齐）。
///
/// 1.20 用 `DeferredRegister.create(ModCustomRegistries.Keys.TRADE_CONDITIONS, MODID)` +
/// `RegistryObject<...>`；1.21 侧 `RegistryObject` 没有同名对应物，按本仓库既有规矩改成
/// `DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<X>>`（与 `NpcEntities` 同一写法）。
/// 注册表对象本身由 {@link ModCustomRegistries} 建好（`DeferredRegister.create` 的 `Registry` 重载，
/// 先例 `TerraEntity/.../track/TrackTypeProviderTypes.java:17`）。
public final class ModTradeConditions {
    public static final DeferredRegister<MapCodec<? extends TradeCondition>> TYPES = DeferredRegister.create(ModCustomRegistries.TRADE_CONDITIONS, Confluence.MODID);

    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = TYPES.register("always", () -> AlwaysTrueCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<HardmodeCondition>> HARDMODE = TYPES.register("hardmode", () -> HardmodeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<ArtisanLoafUnusedCondition>> ARTISAN_LOAF_UNUSED = TYPES.register("artisan_loaf_unused", () -> ArtisanLoafUnusedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<AnyBossDefeatedCondition>> ANY_BOSS_DEFEATED = TYPES.register("any_boss_defeated", () -> AnyBossDefeatedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<BossDefeatedCondition>> BOSS_DEFEATED = TYPES.register("boss_defeated", () -> BossDefeatedCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<BiomeCondition>> BIOME = TYPES.register("biome", () -> BiomeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<TimeCondition>> TIME = TYPES.register("time", () -> TimeCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<KillEntityCondition>> KILL_ENTITY = TYPES.register("kill_entity", () -> KillEntityCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<MoodCondition>> MOOD = TYPES.register("mood", () -> MoodCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<NPCNearbyCondition>> NPC_NEARBY = TYPES.register("npc_nearby", () -> NPCNearbyCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<BestiaryCondition>> BESTIARY = TYPES.register("bestiary", () -> BestiaryCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<DateCondition>> DATE = TYPES.register("date", () -> DateCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<PositionHeightCondition>> POSITION_HEIGHT = TYPES.register("position_height", () -> PositionHeightCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<FluidCondition>> FLUID = TYPES.register("fluid", () -> FluidCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<AttackTargetCondition>> ATTACK_TARGET = TYPES.register("attack_target", () -> AttackTargetCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<NPCItemInHandCondition>> NPC_ITEM_IN_HAND = TYPES.register("npc_item_in_hand", () -> NPCItemInHandCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<WeatherCondition>> WEATHER = TYPES.register("weather", () -> WeatherCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<DimensionCondition>> DIMENSION = TYPES.register("dimension", () -> DimensionCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<GameEventCondition>> GAME_EVENT = TYPES.register("game_event", () -> GameEventCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<GraveyardCondition>> GRAVEYARD = TYPES.register("graveyard", () -> GraveyardCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<MoonPhaseCondition>> MOON_PHASE = TYPES.register("moon_phase", () -> MoonPhaseCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<WorldFlagCondition>> WORLD_FLAG = TYPES.register("world_flag", () -> WorldFlagCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<AndCondition>> AND = TYPES.register("and", () -> AndCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<OrCondition>> OR = TYPES.register("or", () -> OrCondition.CODEC);
    public static final DeferredHolder<MapCodec<? extends TradeCondition>, MapCodec<NotCondition>> NOT = TYPES.register("not", () -> NotCondition.CODEC);
}
