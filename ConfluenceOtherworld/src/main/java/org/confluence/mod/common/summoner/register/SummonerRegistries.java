package org.confluence.mod.common.summoner.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

public final class SummonerRegistries {

    public static final ResourceKey<Registry<AttachmentEntityType<? extends AttachmentEntity>>> ATTACHMENT_ENTITY_TYPE_KEY = ResourceKey.createRegistryKey(Confluence.asResource("summoner_attachment_entity_types"));

    public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> ATTACHMENT_ENTITY_TYPES = DeferredRegister.create(ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);

    public static final ResourceKey<Registry<SummonMarkType>> SUMMON_MARK_TYPE_KEY = ResourceKey.createRegistryKey(Confluence.asResource("summon_mark"));

    public static final DeferredRegister<SummonMarkType> SUMMON_MARK_TYPES = DeferredRegister.create(SUMMON_MARK_TYPE_KEY, Confluence.MODID);

    /// 由 {@code Confluence} 构造器调用：先建注册表，再挂到模组事件总线。
    public static void register(IEventBus eventBus) {
        ATTACHMENT_ENTITY_TYPES.makeRegistry(builder -> builder.sync(true));
        SUMMON_MARK_TYPES.makeRegistry(builder -> builder.sync(true));
        ATTACHMENT_ENTITY_TYPES.register(eventBus);
        SUMMON_MARK_TYPES.register(eventBus);
    }
}
