package org.confluence.mod.common.init;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.*;
import org.mesdag.portlib.attachment.PortAttachmentType;
import org.mesdag.portlib.registries.PortAttachmentRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class ModAttachmentTypes {
    public static void init() {}

    public static final PortAttachmentRegistration TYPES = PortRegisterHandler.attachment(Confluence.MODID);

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ManaStorage>> MANA_STORAGE = TYPES.register("mana", () -> PortAttachmentType.serializable(ManaStorage::new).copyOnDeath().build());
    //    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<SoulStorage>> SOUL_STORAGE = TYPES.register("soul", () -> PortAttachmentType.serializable(SoulStorage::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<EverBeneficial>> EVER_BENEFICIAL = TYPES.register("ever_beneficial", () -> PortAttachmentType.serializable(EverBeneficial::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ExtraInventory>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> PortAttachmentType.serializable(() -> new ExtraInventory(true)).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerPiggyBankContainer>> PIGGY_BANK = TYPES.register("piggy_bank", () -> PortAttachmentType.serializable(PlayerPiggyBankContainer::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerSafeContainer>> SAFE = TYPES.register("safe", () -> PortAttachmentType.serializable(PlayerSafeContainer::new).copyOnDeath().build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<PlayerSpecialData>> SPECIAL_DATA = TYPES.register("special_data", () -> PortAttachmentType.serializable(PlayerSpecialData::new).copyOnDeath().build());

    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ChunkBrushData>> CHUNK_BRUSH_DATA = TYPES.register("chunk_brush_data", () -> PortAttachmentType.serializable(ChunkBrushData::new).build());
    public static final PortRegistryEntry<PortAttachmentType<?>, PortAttachmentType<ChunkDropletsData>> CHUNK_DROPLETS_DATA = TYPES.register("chunk_droplets_data", () -> PortAttachmentType.serializable(ChunkDropletsData::new).build());
}
