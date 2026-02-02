package org.confluence.terra_curio.common.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;

import java.util.function.Supplier;

public final class TCAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TerraCurio.MODID);

    public static final Supplier<AttachmentType<AccessoriesAttachment>> ACCESSORIES = TCAttachments.TYPES.register("accessories", () -> AttachmentType.serializable(AccessoriesAttachment::new).copyOnDeath().build());
}
