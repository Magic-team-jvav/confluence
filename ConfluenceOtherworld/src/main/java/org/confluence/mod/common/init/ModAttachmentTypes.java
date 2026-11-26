package org.confluence.mod.common.init;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.*;
import org.confluence.mod.common.particle.SummonerParticleData;

import java.util.function.Supplier;

public final class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Confluence.MODID);

    public static final Supplier<AttachmentType<ManaStorage>> MANA_STORAGE = TYPES.register("mana", () -> AttachmentType.serializable(ManaStorage::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<SoulStorage>> SOUL_STORAGE = TYPES.register("soul", () -> AttachmentType.serializable(SoulStorage::new).copyOnDeath().sync(SoulStorage.SYNC_HANDLER).build());
    public static final Supplier<AttachmentType<SoulSkillStorage>> SOUL_SKILL_STORAGE = TYPES.register("soulcerer_skill", () -> AttachmentType.serializable(SoulSkillStorage::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<EverBeneficial>> EVER_BENEFICIAL = TYPES.register("ever_beneficial", () -> AttachmentType.serializable(EverBeneficial::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<ExtraInventory>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> AttachmentType.serializable(holder -> new ExtraInventory(true)).copyOnDeath().build());
    public static final Supplier<AttachmentType<PlayerPiggyBankContainer>> PIGGY_BANK = TYPES.register("piggy_bank", () -> AttachmentType.serializable(PlayerPiggyBankContainer::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<PlayerVoidVaultContainer>> VOID_VAULT = TYPES.register("void_vault", () -> AttachmentType.serializable(PlayerVoidVaultContainer::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<PlayerSafeContainer>> SAFE = TYPES.register("safe", () -> AttachmentType.serializable(PlayerSafeContainer::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<PlayerSpecialData>> SPECIAL_DATA = TYPES.register("special_data", () -> AttachmentType.serializable(PlayerSpecialData::new).copyOnDeath().build());

    public static final Supplier<AttachmentType<ChunkBrushData>> CHUNK_BRUSH_DATA = TYPES.register("chunk_brush_data", () -> AttachmentType.serializable(ChunkBrushData::new).build());
    public static final Supplier<AttachmentType<ChunkDropletsData>> CHUNK_DROPLETS_DATA = TYPES.register("chunk_droplets_data", () -> AttachmentType.serializable(ChunkDropletsData::new).build());

    public static final Supplier<AttachmentType<YoyoSession>> YOYO_SESSION = TYPES.register("yoyo_session", () -> AttachmentType.builder(YoyoSession::new).build());
    public static final Supplier<AttachmentType<LeftClickState>> LEFT_CLICK_STATE = TYPES.register("left_click_state", () -> AttachmentType.builder(LeftClickState::new).build());
    public static final Supplier<AttachmentType<LeftClickCooldowns>> LEFT_CLICK_COOLDOWNS = TYPES.register("left_click_cooldowns", () -> AttachmentType.builder(holder -> new LeftClickCooldowns((Player) holder)).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AttachmentEntityData>> SUMMONER_ATTACHMENT_ENTITY_DATA = TYPES.register("summoner_attachment_entity_data", () -> AttachmentType.builder(AttachmentEntityData::new).sync(new AttachmentEntityData.SyncHandler()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TargetCache>> SUMMONER_TARGET_CACHE = TYPES.register("summoner_target_cache", () -> AttachmentType.builder(TargetCache::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WhipMarkTracker>> SUMMON_MARK_DATA = TYPES.register("summon_mark_data", () -> AttachmentType.builder(WhipMarkTracker::new).sync(new WhipMarkTracker.SyncHandler()).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SummonerParticleData>> SUMMONER_BATCHED_PARTICLES = TYPES.register("summoner_batched_particles", () -> AttachmentType.builder(SummonerParticleData::new).build());
    /// 信息数据（Level 级）：服务端累积、tick 末尾发包，客户端分流为数字/文本信息。
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InfoData>> SUMMONER_INFO = TYPES.register("summoner_info", () -> AttachmentType.builder(InfoData::new).build());
    /// 玩家持有天顶剑时的蓄力与挥砍状态。
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ZenithData>> ZENITH_DATA = TYPES.register("zenith_data", () -> AttachmentType.builder(ZenithData::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArcSurgeData>> ARC_SURGE_DATA = TYPES.register("arc_surge_data", () -> AttachmentType.builder(ArcSurgeData::new).build());
}
