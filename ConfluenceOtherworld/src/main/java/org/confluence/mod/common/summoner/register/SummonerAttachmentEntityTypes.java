package org.confluence.mod.common.summoner.register;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.HornetMinion;
import org.confluence.mod.common.summoner.projectile.HornetStinger;

public final class SummonerAttachmentEntityTypes {

    public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> TYPES =
            DeferredRegister.create(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);

    public static final RegistryObject<AttachmentEntityType<HornetMinion>> HORNET =
            PortDeferredRegisterExtension.register(TYPES, "hornet", id -> new AttachmentEntityType<>(id, HornetMinion::new));

    public static final RegistryObject<AttachmentEntityType<HornetStinger>> HORNET_STINGER =
            PortDeferredRegisterExtension.register(TYPES, "hornet_stinger", id -> new AttachmentEntityType<>(id, HornetStinger::new));

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
