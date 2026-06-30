package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.*;

public final class ModTradeConditions {
    public static final DeferredRegister<MapCodec<? extends TradeCondition>> TYPES = DeferredRegister.create(ModCustomRegistries.Keys.TRADE_CONDITIONS, Confluence.MODID);

    public static final RegistryObject<MapCodec<AlwaysCondition>> ALWAYS = TYPES.register("always", () -> AlwaysCondition.CODEC);
    public static final RegistryObject<MapCodec<HardmodeCondition>> HARDMODE = TYPES.register("hardmode", () -> HardmodeCondition.CODEC);
    public static final RegistryObject<MapCodec<AnyBossDefeatedCondition>> ANY_BOSS_DEFEATED = TYPES.register("any_boss_defeated", () -> AnyBossDefeatedCondition.CODEC);
    public static final RegistryObject<MapCodec<BossDefeatedCondition>> BOSS_DEFEATED = TYPES.register("boss_defeated", () -> BossDefeatedCondition.CODEC);
    public static final RegistryObject<MapCodec<BiomeCondition>> BIOME = TYPES.register("biome", () -> BiomeCondition.CODEC);
    public static final RegistryObject<MapCodec<TimeCondition>> TIME = TYPES.register("time", () -> TimeCondition.CODEC);
    public static final RegistryObject<MapCodec<KillEntityCondition>> KILL_ENTITY = TYPES.register("kill_entity", () -> KillEntityCondition.CODEC);
    public static final RegistryObject<MapCodec<MoodCondition>> MOOD = TYPES.register("mood", () -> MoodCondition.CODEC);
    public static final RegistryObject<MapCodec<NPCNearbyCondition>> NPC_NEARBY = TYPES.register("npc_nearby", () -> NPCNearbyCondition.CODEC);
    public static final RegistryObject<MapCodec<BestiaryCondition>> BESTIARY = TYPES.register("bestiary", () -> BestiaryCondition.CODEC);
    public static final RegistryObject<MapCodec<DateCondition>> DATE = TYPES.register("date", () -> DateCondition.CODEC);
    public static final RegistryObject<MapCodec<AndCondition>> AND = TYPES.register("and", () -> AndCondition.CODEC);
    public static final RegistryObject<MapCodec<OrCondition>> OR = TYPES.register("or", () -> OrCondition.CODEC);
    public static final RegistryObject<MapCodec<NotCondition>> NOT = TYPES.register("not", () -> NotCondition.CODEC);
}
