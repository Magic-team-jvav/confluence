package org.confluence.mod.common.summoner.register;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachment.*;
import org.confluence.mod.common.summoner.particle.SummonerParticleData;

public final class SummonerAttachmentTypes {

    private static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Confluence.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AttachmentEntityData>> ENTITY_DATA = TYPES.register("summoner_attachment_entity_data", () -> AttachmentType.builder(AttachmentEntityData::new).sync(new AttachmentEntityData.SyncHandler()).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TargetCache>> TARGET_CACHE = TYPES.register("summoner_target_cache", () -> AttachmentType.builder(TargetCache::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WhipMarkTracker>> SUMMON_MARK_DATA = TYPES.register("summon_mark_data", () -> AttachmentType.builder(WhipMarkTracker::new).sync(new WhipMarkTracker.SyncHandler()).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SummonerParticleData>> BATCHED_PARTICLES =
            TYPES.register("summoner_batched_particles", () -> AttachmentType.builder(SummonerParticleData::new).build());

    /// 信息数据（Level 级）：服务端累积、tick 末尾发包，客户端分流为数字/文本信息。
    ///
    /// 当前未启用 —— 没有任何调用点，原版伤害指示粒子保持原样；要接入见 {@link InfoData}。
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InfoData>> INFO =
            TYPES.register("summoner_info", () -> AttachmentType.builder(InfoData::new).build());

    /// 玩家持有天顶剑时的蓄力与挥砍状态。
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ZenithData>> ZENITH_DATA =
            TYPES.register("zenith_data", () -> AttachmentType.builder(ZenithData::new).build());

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
