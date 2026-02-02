package org.confluence.terraentity.init;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.confluence.lib.common.PlayerContainer;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.attachment.ItemInHandTrailAttachment;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.attachment.UnSyncableAttachment;
import org.confluence.terraentity.attachment.WeaponStorage;

import java.util.function.Supplier;


public final class TEAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TerraEntity.MODID);

    public static final Supplier<AttachmentType<SummonerAttachment>> SUMMONER_STORAGE = TYPES.register("summoner_storage", () -> AttachmentType.serializable(ins->new SummonerAttachment(SummonerAttachment.SummonerType.MINION)).copyOnDeath().build());
    public static final Supplier<AttachmentType<SummonerAttachment>> SENTRY_STORAGE = TYPES.register("sentry_storage", () -> AttachmentType.serializable(ins->new SummonerAttachment(SummonerAttachment.SummonerType.SENTRY)).copyOnDeath().build());
    public static final Supplier<AttachmentType<WeaponStorage>> WEAPON_STORAGE = TYPES.register("weapon_storage", () -> AttachmentType.serializable(WeaponStorage::new).copyOnDeath().build());
    public static final Supplier<AttachmentType<ItemInHandTrailAttachment>> TRAIL_STORAGE = TYPES.register("trail_storage", () -> AttachmentType.serializable(ItemInHandTrailAttachment::new).build());
    public static final Supplier<AttachmentType<PlayerContainer>> CHESTER = TYPES.register("chester", () -> AttachmentType.serializable(()->new PlayerContainer(6)).copyOnDeath().build());
    public static final Supplier<AttachmentType<UnSyncableAttachment>> UNSYNC = TYPES.register("unsync", () -> AttachmentType.serializable(()->new UnSyncableAttachment()).build());

}
