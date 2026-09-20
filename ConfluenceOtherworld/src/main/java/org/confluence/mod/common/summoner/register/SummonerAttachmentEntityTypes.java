package org.confluence.mod.common.summoner.register;

import PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.*;
import org.confluence.mod.common.summoner.projectile.HornetStinger;
import org.confluence.mod.common.summoner.projectile.ImpFireball;

import java.util.function.Supplier;

public final class SummonerAttachmentEntityTypes {

    public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> TYPES = DeferredRegister.create(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);

    public static final RegistryObject<AttachmentEntityType<HornetMinion>> HORNET = register("hornet", HornetMinion::new);

    public static final RegistryObject<AttachmentEntityType<HornetStinger>> HORNET_STINGER = register("hornet_stinger", HornetStinger::new);

    public static final RegistryObject<AttachmentEntityType<FinchMinion>> FINCH = register("finch", FinchMinion::new);

    public static final RegistryObject<AttachmentEntityType<IronGolemMinion>> IRON_GOLEM = register("iron_golem", IronGolemMinion::new);

    public static final RegistryObject<AttachmentEntityType<SculkWispMinion>> SCULK_WISP = register("sculk_wisp", SculkWispMinion::new);

    public static final RegistryObject<AttachmentEntityType<ImpMinion>> IMP = register("imp", ImpMinion::new);

    public static final RegistryObject<AttachmentEntityType<ImpFireball>> IMP_FIREBALL = register("imp_fireball", ImpFireball::new);

    public static final RegistryObject<AttachmentEntityType<DeadlySphereMinion>> DEADLY_SPHERE = register("deadly_sphere", DeadlySphereMinion::new);

    public static final RegistryObject<AttachmentEntityType<BloodBatMinion>> VAMPIRE_BAT = register("vampire_bat", BloodBatMinion::new);

    public static final RegistryObject<AttachmentEntityType<TerraprismaMinion>> TERRAPRISMA = register("terraprisma", TerraprismaMinion::new);

    public static final RegistryObject<AttachmentEntityType<SlimeMinion>> SLIME = register("slime", SlimeMinion::new);

    public static final RegistryObject<AttachmentEntityType<SnowFlinxMinion>> SNOW_FLINX = register("snow_flinx", SnowFlinxMinion::new);

    public static final RegistryObject<AttachmentEntityType<VampireFrogMinion>> VAMPIRE_FROG = register("vampire_frog", VampireFrogMinion::new);

    public static final RegistryObject<AttachmentEntityType<DesertTigerMinion>> DESERT_TIGER = register("desert_tiger", DesertTigerMinion::new);

    public static final RegistryObject<AttachmentEntityType<SpiderMinion>> SPIDER = register("spider", SpiderMinion::new);

    private static <T extends AttachmentEntity> RegistryObject<AttachmentEntityType<T>> register(String name, Supplier<T> supplier) {
        return PortDeferredRegisterExtension.register(TYPES, name, id -> new AttachmentEntityType<>(id, supplier));
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
