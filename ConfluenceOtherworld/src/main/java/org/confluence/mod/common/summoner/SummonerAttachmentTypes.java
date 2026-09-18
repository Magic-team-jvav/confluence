package org.confluence.mod.common.summoner;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachment.AttachmentEntityData;
import org.confluence.mod.common.summoner.attachment.InvincibleData;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.mesdag.portlib.attachment.PortAttachmentType;
import org.mesdag.portlib.registries.PortAttachmentRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class SummonerAttachmentTypes {
    private static final PortAttachmentRegistration TYPES = PortRegisterHandler.attachment(Confluence.MODID);

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<AttachmentEntityData>> ENTITY_DATA =
            TYPES.registerSimple("summoner_attachment_entity_data",
                    () -> PortAttachmentType.builder(AttachmentEntityData::new).sync(new AttachmentEntityData()));

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<TargetCache>> TARGET_CACHE =
            TYPES.registerSimple("summoner_target_cache", () -> PortAttachmentType.builder(TargetCache::new));

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<InvincibleData>> INVINCIBLE_DATA =
            TYPES.registerSimple("summoner_invincible_data", () -> PortAttachmentType.builder(InvincibleData::new));

    private SummonerAttachmentTypes() {
    }

    public static void init() {
    }
}
