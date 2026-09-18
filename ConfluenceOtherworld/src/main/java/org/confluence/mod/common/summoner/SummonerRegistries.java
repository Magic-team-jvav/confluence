package org.confluence.mod.common.summoner;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.mesdag.portlib.registries.PortCustomRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public final class SummonerRegistries {
    private static final ResourceKey<Registry<AttachmentEntityType<? extends AttachmentEntity>>> ATTACHMENT_ENTITY_TYPE_KEY =
            ResourceKey.createRegistryKey(Confluence.asResource("summoner_attachment_entity_types"));

    public static final PortCustomRegistration<AttachmentEntityType<? extends AttachmentEntity>> ATTACHMENT_ENTITY_TYPES =
            PortRegisterHandler.custom(Confluence.MODID, ATTACHMENT_ENTITY_TYPE_KEY, maker -> maker.sync(true));

    private SummonerRegistries() {
    }

    public static void init() {
    }

    public static ResourceLocation getKey(AttachmentEntityType<?> type) {
        return type.identifier();
    }
}
