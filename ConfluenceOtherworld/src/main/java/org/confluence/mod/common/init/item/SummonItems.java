package org.confluence.mod.common.init.item;

import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.minion.FinchMinion;
import org.confluence.mod.common.summoner.minion.HornetMinion;
import org.confluence.mod.common.summoner.minion.IronGolemMinion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
import org.confluence.mod.common.summoner.minion.SculkWispMinion;
import org.confluence.mod.common.summoner.minion.SanguineBatMinion;
import org.confluence.mod.common.summoner.minion.DeadlySphereMinion;
import org.confluence.mod.common.summoner.minion.DesertTigerMinion;
import org.confluence.mod.common.summoner.minion.EyeLaserTurretMinion;
import org.confluence.mod.common.summoner.minion.ImpMinion;
import org.confluence.mod.common.summoner.minion.RuinRelicMinion;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;
import org.confluence.mod.common.summoner.minion.SlimeMinion;
import org.confluence.mod.common.summoner.minion.SnowFlinxMinion;
import org.confluence.mod.common.summoner.minion.SpiderMinion;
import org.confluence.mod.common.summoner.minion.VampireFrogMinion;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SummonItems {
    public static void init() {}
    // 取wiki 75%的数值为基础再调
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<SummonerWeaponItem<FinchMinion>> FINCH_STAFF = ITEMS.register("finch_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
                    SummonerAttachmentEntityTypes.FINCH,
                    MinionSlotType.Minion,
                    3.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<HornetMinion>> NEW_HORNET_STAFF = ITEMS.register("new_hornet_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.HORNET,
                    MinionSlotType.Minion,
                    8.0F,
                    0.2F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<IronGolemMinion>> IRON_GOLEM_STAFF = ITEMS.register("iron_golem_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
                    SummonerAttachmentEntityTypes.IRON_GOLEM,
                    MinionSlotType.Minion,
                    12.0F,
                    1.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<SlimeMinion>> SLIME_STAFF = ITEMS.register("slime_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED),
                    SummonerAttachmentEntityTypes.SLIME,
                    MinionSlotType.Minion,
                    5.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<SculkWispMinion>> SCULK_WISP_STAFF = ITEMS.register("sculk_wisp_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.SCULK_WISP,
                    MinionSlotType.Minion,
                    7.0F,
                    1.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<ImpMinion>> IMP_STAFF = ITEMS.register("imp_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.IMP,
                    MinionSlotType.Minion,
                    14.0F,
                    1.0F,
                    0.0F,
                    ModSoundEvents.SUMMON_IMP,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<SnowFlinxMinion>> SNOW_FLINX_STAFF = ITEMS.register("snow_flinx_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.SNOW_FLINX,
                    MinionSlotType.Minion,
                    7.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<VampireFrogMinion>> VAMPIRE_FROG_STAFF = ITEMS.register("vampire_frog_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.VAMPIRE_FROG,
                    MinionSlotType.Minion,
                    11.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<DeadlySphereMinion>> DEADLY_SPHERE_STAFF = ITEMS.register("deadly_sphere_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.YELLOW),
                    SummonerAttachmentEntityTypes.DEADLY_SPHERE,
                    MinionSlotType.Minion,
                    41.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<SanguineBatMinion>> SANGUINE_STAFF = ITEMS.register("sanguine_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED),
                    SummonerAttachmentEntityTypes.SANGUINE_BAT,
                    MinionSlotType.Minion,
                    27.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<SpiderMinion>> SPIDER_STAFF = ITEMS.register("spider_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED),
                    SummonerAttachmentEntityTypes.SPIDER,
                    MinionSlotType.Minion,
                    19.5F,
                    1.0F,
                    0.0F,
                    ModSoundEvents.ROUTINE_SUMMON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<DesertTigerMinion>> DESERT_TIGER_STAFF = ITEMS.register("desert_tiger_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.YELLOW),
                    SummonerAttachmentEntityTypes.DESERT_TIGER,
                    MinionSlotType.Minion,
                    31.0F,
                    0.0F,
                    0.0F,
                    ModSoundEvents.ROUTINE_SUMMON,
                    (weapon, player, itemStack) -> {
                        DesertTigerMinion minion = weapon.createMinion(player, itemStack);
                        SummonerHelper summonerHelper = SummonerHelper.get(player);
                        MinionSlotType slotType = weapon.getSlotType(itemStack);
                        List<DesertTigerMinion> tigers = summonerHelper.getEntityData().getGroups().getOrDefault(weapon.getEntityType(), List.of()).stream()
                                .filter(DesertTigerMinion.class::isInstance)
                                .map(DesertTigerMinion.class::cast)
                                .toList();
                        if (tigers.isEmpty()) {
                            if (summonerHelper.canSummon(slotType, minion.getSlotCost())) {
                                AABB box = player.getBoundingBox();
                                Vec3 pos = box.getCenter();
                                minion.init(new PathNode(pos.offsetRandom(player.getRandom(), 2), 0, 0, 0));
                                summonerHelper.add(minion);
                            }
                        } else if (summonerHelper.canSummon(slotType, minion.getSlotCost())) {
                            DesertTigerMinion tiger = tigers.get(0);
                            tiger.setSlotCost(tiger.getSlotCost() + minion.getSlotCost());
                            tiger.setDamage(minion.getDamage());
                            tiger.setKnockback(minion.getKnockback());
                            tiger.setArmorPierce(minion.getArmorPierce());
                        }
                    },
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<TerraprismaMinion>> TERRAPRISMA = ITEMS.register("terraprisma",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PINK),
                    SummonerAttachmentEntityTypes.TERRAPRISMA,
                    MinionSlotType.Minion,
                    67.0F,
                    0.0F,
                    0.0F,
                    SummonerSoundEvents.USE_TERRAPRISM,
                    (weapon, player, itemStack) -> {
                        TerraprismaMinion minion = weapon.createMinion(player, itemStack);
                        SummonerHelper summonerHelper = SummonerHelper.get(player);
                        MinionSlotType slotType = weapon.getSlotType(itemStack);
                        if (summonerHelper.canSummon(slotType, minion.getSlotCost())) {
                            minion.init(minion.getInterpolatedIdleState(1));
                            minion.setOwner(player);
                            summonerHelper.add(minion);
                        }
                    },
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<RuinRelicMinion>> RUIN_STAFF = ITEMS.register("ruin_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE),
                    SummonerAttachmentEntityTypes.RUIN_RELIC,
                    MinionSlotType.Minion,
                    30.0F,
                    0.4F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final PortDeferredItem<SummonerWeaponItem<EyeLaserTurretMinion>> EYE_LASER_TURRET_STAFF = ITEMS.register("eye_laser_turret_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.EYE_LASER_TURRET,
                    MinionSlotType.Sentry,
                    18F,
                    0.25F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    (weapon, player, itemStack) -> {
                        EyeLaserTurretMinion minion = weapon.createMinion(player, itemStack);
                        SummonerHelper summonerHelper = SummonerHelper.get(player);
                        MinionSlotType slotType = weapon.getSlotType(itemStack);
                        if (summonerHelper.canSummon(slotType, minion.getSlotCost())) {
                            minion.init(new PathNode(player.position().offsetRandom(minion.getRandom(), 0.1f).add(0.0, 2, 0.0), 0, 0, 0));
                            summonerHelper.add(minion);
                        }
                    },
                    null
            ));
}
