package org.confluence.mod.common.summoner.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.mesdag.portlib.registries.PortCustomRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public final class SummonerRegistries {

    public static final ResourceKey<Registry<AttachmentEntityType<? extends AttachmentEntity>>> ATTACHMENT_ENTITY_TYPE_KEY = ResourceKey.createRegistryKey(Confluence.asResource("summoner_attachment_entity_types"));

    public static final PortCustomRegistration<AttachmentEntityType<? extends AttachmentEntity>> ATTACHMENT_ENTITY_TYPES = PortRegisterHandler.custom(Confluence.MODID, ATTACHMENT_ENTITY_TYPE_KEY, maker -> maker.sync(true));

    public static final ResourceKey<Registry<SummonMarkType>> SUMMON_MARK_TYPE_KEY = ResourceKey.createRegistryKey(Confluence.asResource("summon_mark"));

    public static final PortCustomRegistration<SummonMarkType> SUMMON_MARK_TYPES = PortRegisterHandler.custom(Confluence.MODID, SUMMON_MARK_TYPE_KEY, maker -> maker.sync(true));

    public static void init() {
    }
}
