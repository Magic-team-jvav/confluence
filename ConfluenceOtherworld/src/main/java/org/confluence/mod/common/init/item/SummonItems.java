package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.*;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;

import java.util.List;

public class SummonItems {
    // 取wiki 75%的数值为基础再调
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<SummonerWeaponItem<FinchMinion>> FINCH_STAFF = ITEMS.register("finch_staff",
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
    public static final DeferredItem<SummonerWeaponItem<HornetMinion>> NEW_HORNET_STAFF = ITEMS.register("new_hornet_staff",
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
    public static final DeferredItem<SummonerWeaponItem<IronGolemMinion>> IRON_GOLEM_STAFF = ITEMS.register("iron_golem_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
                    SummonerAttachmentEntityTypes.IRON_GOLEM,
                    MinionSlotType.Minion,
                    16.0F,
                    1.0F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<SlimeMinion>> SLIME_STAFF = ITEMS.register("slime_staff",
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
    public static final DeferredItem<SummonerWeaponItem<SculkWispMinion>> SCULK_WISP_STAFF = ITEMS.register("sculk_wisp_staff",
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
    public static final DeferredItem<SummonerWeaponItem<ImpMinion>> IMP_STAFF = ITEMS.register("imp_staff",
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
    public static final DeferredItem<SummonerWeaponItem<SnowFlinxMinion>> SNOW_FLINX_STAFF = ITEMS.register("snow_flinx_staff",
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
    public static final DeferredItem<SummonerWeaponItem<VampireFrogMinion>> VAMPIRE_FROG_STAFF = ITEMS.register("vampire_frog_staff",
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
    public static final DeferredItem<SummonerWeaponItem<DeadlySphereMinion>> DEADLY_SPHERE_STAFF = ITEMS.register("deadly_sphere_staff",
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
    public static final DeferredItem<SummonerWeaponItem<SanguineBatMinion>> SANGUINE_STAFF = ITEMS.register("sanguine_staff",
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
    public static final DeferredItem<SummonerWeaponItem<SpiderMinion>> SPIDER_STAFF = ITEMS.register("spider_staff",
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
    public static final DeferredItem<SummonerWeaponItem<DesertTigerMinion>> DESERT_TIGER_STAFF = ITEMS.register("desert_tiger_staff",
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
    public static final DeferredItem<SummonerWeaponItem<TerraprismaMinion>> TERRAPRISMA = ITEMS.register("terraprisma",
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
    public static final DeferredItem<SummonerWeaponItem<RuinRelicMinion>> RUIN_STAFF = ITEMS.register("ruin_staff",
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
    public static final DeferredItem<SummonerWeaponItem<EyeLaserTurretMinion>> EYE_LASER_TURRET_STAFF = ITEMS.register("eye_laser_turret_staff",
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
