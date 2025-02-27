package org.confluence.mod.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.*;

import java.util.function.Supplier;

public final class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Confluence.MODID);

    public static final Supplier<AttachmentType<ManaStorage>> MANA_STORAGE = TYPES.register("mana", () -> AttachmentType.serializable(ManaStorage::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<EverBeneficial>> EVER_BENEFICIAL = TYPES.register("ever_beneficial", () -> AttachmentType.serializable(EverBeneficial::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<WeaponStorage>> WEAPON_STORAGE = TYPES.register("weapon_storage", () -> AttachmentType.serializable(WeaponStorage::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<ExtraInventory>> EXTRA_INVENTORY = TYPES.register("extra_inventory", () -> AttachmentType.serializable(ExtraInventory::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<ChunkBrushData>> CHUNK_BRUSH_DATA = TYPES.register("chunk_brush_data", () -> AttachmentType.serializable(ChunkBrushData::new).build());
}
