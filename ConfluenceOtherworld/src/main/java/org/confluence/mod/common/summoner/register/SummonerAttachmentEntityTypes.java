package org.confluence.mod.common.summoner.register;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.FinchMinion;
import org.confluence.mod.common.summoner.minion.HornetMinion;
import org.confluence.mod.common.summoner.projectile.HornetStinger;

import java.util.function.Supplier;

public final class SummonerAttachmentEntityTypes {

    public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> TYPES = DeferredRegister.create(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);

    public static final RegistryObject<AttachmentEntityType<HornetMinion>> HORNET = register("hornet", HornetMinion::new);

    public static final RegistryObject<AttachmentEntityType<HornetStinger>> HORNET_STINGER = register("hornet_stinger", HornetStinger::new);

    public static final RegistryObject<AttachmentEntityType<FinchMinion>> FINCH = register("finch", FinchMinion::new);

    private static <T extends AttachmentEntity> RegistryObject<AttachmentEntityType<T>> register(String name, Supplier<T> supplier) {
        return PortDeferredRegisterExtension.register(TYPES, name, id -> new AttachmentEntityType<>(id, supplier));
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
