package org.confluence.mod.common.summoner.register;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.*;
import org.confluence.mod.common.summoner.projectile.*;

import java.util.function.Supplier;


public final class SummonerAttachmentEntityTypes {

    public static final DeferredRegister<AttachmentEntityType<? extends AttachmentEntity>> TYPES = DeferredRegister.create(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY, Confluence.MODID);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<HornetMinion>> HORNET = register("hornet", HornetMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<HornetStinger>> HORNET_STINGER = register("hornet_stinger", HornetStinger::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<FinchMinion>> FINCH = register("finch", FinchMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<IronGolemMinion>> IRON_GOLEM = register("iron_golem", IronGolemMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<SculkWispMinion>> SCULK_WISP = register("sculk_wisp", SculkWispMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<ImpMinion>> IMP = register("imp", ImpMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<ImpFireball>> IMP_FIREBALL = register("imp_fireball", ImpFireball::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<DeadlySphereMinion>> DEADLY_SPHERE = register("deadly_sphere", DeadlySphereMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<SanguineBatMinion>> SANGUINE_BAT = register("sanguine_bat", SanguineBatMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<TerraprismaMinion>> TERRAPRISMA = register("terraprisma", TerraprismaMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<SlimeMinion>> SLIME = register("slime", SlimeMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<SnowFlinxMinion>> SNOW_FLINX = register("snow_flinx", SnowFlinxMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<VampireFrogMinion>> VAMPIRE_FROG = register("vampire_frog", VampireFrogMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<DesertTigerMinion>> DESERT_TIGER = register("desert_tiger", DesertTigerMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<SpiderMinion>> SPIDER = register("spider", SpiderMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<RuinRelicMinion>> RUIN_RELIC = register("ruin_relic", RuinRelicMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<EyeLaserTurretMinion>> EYE_LASER_TURRET = register("eye_laser_turret", EyeLaserTurretMinion::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<ForbiddenOrb>> FORBIDDEN_ORB = register("forbidden_orb", ForbiddenOrb::new);

    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<EyeFireball>> EYE_FIREBALL = register("eye_fireball", EyeFireball::new);

    /// 天顶剑飞剑。
    public static final DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<Zenith>> ZENITH = register("zenith", Zenith::new);

    private static <T extends AttachmentEntity> DeferredHolder<AttachmentEntityType<? extends AttachmentEntity>, AttachmentEntityType<T>> register(String name, Supplier<T> supplier) {
        return TYPES.register(name, id -> new AttachmentEntityType<T>(id, supplier));
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
