package org.confluence.mod.common.init;

import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.particle.SummonerParticleData;
import org.mesdag.portlib.attachment.PortAttachmentType;
import org.mesdag.portlib.registries.PortAttachmentRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class ModAttachmentTypes {
    public static void init() {}

    public static final PortAttachmentRegistration TYPES = PortRegisterHandler.attachment(Confluence.MODID);

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ManaStorage>> MANA_STORAGE = TYPES.register("mana", () -> PortAttachmentType.serializable(ManaStorage::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<SoulStorage>> SOUL_STORAGE = TYPES.register("soul", () -> PortAttachmentType.serializable(SoulStorage::new).copyOnDeath().sync(SoulStorage.SYNC_HANDLER).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<SoulSkillStorage>> SOUL_SKILL_STORAGE = TYPES.register("soulcerer_skill", () -> PortAttachmentType.serializable(SoulSkillStorage::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<EverBeneficial>> EVER_BENEFICIAL = TYPES.register("ever_beneficial", () -> PortAttachmentType.serializable(EverBeneficial::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ExtraInventory>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> PortAttachmentType.serializable(() -> new ExtraInventory(true)).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerPiggyBankContainer>> PIGGY_BANK = TYPES.register("piggy_bank", () -> PortAttachmentType.serializable(PlayerPiggyBankContainer::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerVoidVaultContainer>> VOID_VAULT = TYPES.register("void_vault", () -> PortAttachmentType.serializable(PlayerVoidVaultContainer::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerSafeContainer>> SAFE = TYPES.register("safe", () -> PortAttachmentType.serializable(PlayerSafeContainer::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerSpecialData>> SPECIAL_DATA = TYPES.register("special_data", () -> PortAttachmentType.serializable(PlayerSpecialData::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<YoyoSession>> YOYO_SESSION = TYPES.register("yoyo_session", () -> PortAttachmentType.builder(YoyoSession::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<LeftClickState>> LEFT_CLICK_STATE = TYPES.register("left_click_state", () -> PortAttachmentType.builder(LeftClickState::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<LeftClickCooldowns>> LEFT_CLICK_COOLDOWNS = TYPES.register("left_click_cooldowns", () -> PortAttachmentType.builder(holder -> new LeftClickCooldowns((Player) holder)).build());

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ChunkBrushData>> CHUNK_BRUSH_DATA = TYPES.register("chunk_brush_data", () -> PortAttachmentType.serializable(ChunkBrushData::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ChunkDropletsData>> CHUNK_DROPLETS_DATA = TYPES.register("chunk_droplets_data", () -> PortAttachmentType.serializable(ChunkDropletsData::new).build());

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<AttachmentEntityData>> SUMMONER_ATTACHMENT_ENTITY_DATA = TYPES.register("summoner_attachment_entity_data", () -> PortAttachmentType.builder(AttachmentEntityData::new).sync(new AttachmentEntityData.SyncHandler()).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<TargetCache>> SUMMONER_TARGET_CACHE = TYPES.register("summoner_target_cache", () -> PortAttachmentType.builder(TargetCache::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<WhipMarkTracker>> SUMMON_MARK_DATA = TYPES.register("summon_mark_data", () -> PortAttachmentType.builder(WhipMarkTracker::new).sync(new WhipMarkTracker.SyncHandler()).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<SummonerParticleData>> SUMMONER_BATCHED_PARTICLES = TYPES.register("summoner_batched_particles", () -> PortAttachmentType.builder(SummonerParticleData::new).build());
    /// 信息数据（Level 级）：服务端累积、tick 末尾发包，客户端分流为数字/文本信息。
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<InfoData>> SUMMONER_INFO = TYPES.register("summoner_info", () -> PortAttachmentType.builder(InfoData::new).build());
    /// 玩家持有天顶剑时的蓄力与挥砍状态。
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ZenithData>> ZENITH_DATA = TYPES.register("zenith_data", () -> PortAttachmentType.builder(ZenithData::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ArcSurgeData>> ARC_SURGE_DATA = TYPES.register("arc_surge_data", () -> PortAttachmentType.builder(ArcSurgeData::new).build());
}
