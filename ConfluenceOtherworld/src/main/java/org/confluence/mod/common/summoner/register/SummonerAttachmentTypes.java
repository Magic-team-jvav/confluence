package org.confluence.mod.common.summoner.register;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachment.AttachmentEntityData;
import org.confluence.mod.common.summoner.attachment.InfoData;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.common.summoner.attachment.WhipMarkTracker;
import org.confluence.mod.common.summoner.particle.SummonerParticleData;
import org.mesdag.portlib.attachment.PortAttachmentType;
import org.mesdag.portlib.registries.PortAttachmentRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class SummonerAttachmentTypes {

    private static final PortAttachmentRegistration TYPES = PortRegisterHandler.attachment(Confluence.MODID);

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<AttachmentEntityData>> ENTITY_DATA = TYPES.registerSimple("summoner_attachment_entity_data", () -> PortAttachmentType.builder(AttachmentEntityData::new).sync(new AttachmentEntityData()));

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<TargetCache>> TARGET_CACHE = TYPES.registerSimple("summoner_target_cache", () -> PortAttachmentType.builder(TargetCache::new));

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<WhipMarkTracker>> SUMMON_MARK_DATA = TYPES.registerSimple("summon_mark_data", () -> PortAttachmentType.builder(WhipMarkTracker::new).sync(new WhipMarkTracker()));

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<SummonerParticleData>> BATCHED_PARTICLES =
            TYPES.registerSimple("summoner_batched_particles", () -> PortAttachmentType.builder(SummonerParticleData::new));

    /// 信息数据（Level 级）：服务端累积、tick 末尾发包，客户端分流为数字/文本信息。
    ///
    /// 当前未启用 —— 没有任何调用点，原版伤害指示粒子保持原样；要接入见 {@link InfoData}。
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<InfoData>> INFO =
            TYPES.registerSimple("summoner_info", () -> PortAttachmentType.builder(InfoData::new));

    public static void init() {
    }
}
