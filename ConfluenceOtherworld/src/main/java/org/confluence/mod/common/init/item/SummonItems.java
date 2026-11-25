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
import org.confluence.mod.common.item.summon.EyeLaserTurretStaffItem;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.common.summoner.SummonerHelper;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.*;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;

import java.util.List;
import java.util.function.Supplier;


public class SummonItems {
    // 取wiki 75%的数值为基础再调
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<SummonerWeaponItem<FinchMinion>> FINCH_STAFF = registerItem("finch_staff", "雀杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
                    SummonerAttachmentEntityTypes.FINCH,
                    MinionSlotType.Minion,
                    3.0F,
            0.4F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<HornetMinion>> NEW_HORNET_STAFF = registerItemWithEnglishName("new_hornet_staff", "黄蜂法杖", "Hornet Staff", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<IronGolemMinion>> IRON_GOLEM_STAFF = registerItem("iron_golem_staff", "铁傀儡杖", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<SlimeMinion>> SLIME_STAFF = registerItem("slime_staff", "史莱姆法杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED),
                    SummonerAttachmentEntityTypes.SLIME,
                    MinionSlotType.Minion,
                    5.0F,
            0.2F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<SculkWispMinion>> SCULK_WISP_STAFF = registerItem("sculk_wisp_staff", "幽匿游灵法杖", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<ImpMinion>> IMP_STAFF = registerItem("imp_staff", "小鬼法杖", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<SnowFlinxMinion>> SNOW_FLINX_STAFF = registerItemWithEnglishName("snow_flinx_staff", "小雪怪法杖", "Flinx Staff", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.SNOW_FLINX,
                    MinionSlotType.Minion,
                    7.0F,
            0.2F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<VampireFrogMinion>> VAMPIRE_FROG_STAFF = registerItem("vampire_frog_staff", "吸血鬼青蛙法杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.VAMPIRE_FROG,
                    MinionSlotType.Minion,
                    11.0F,
            0.5F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<DeadlySphereMinion>> DEADLY_SPHERE_STAFF = registerItem("deadly_sphere_staff", "致命球法杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.YELLOW),
                    SummonerAttachmentEntityTypes.DEADLY_SPHERE,
                    MinionSlotType.Minion,
                    41.0F,
            0.2F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<SanguineBatMinion>> SANGUINE_STAFF = registerItem("sanguine_staff", "血红法杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED),
                    SummonerAttachmentEntityTypes.SANGUINE_BAT,
                    MinionSlotType.Minion,
                    27.0F,
            0.3F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));
    public static final DeferredItem<SummonerWeaponItem<SpiderMinion>> SPIDER_STAFF = registerItem("spider_staff", "蜘蛛法杖", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<DesertTigerMinion>> DESERT_TIGER_STAFF = registerItem("desert_tiger_staff", "沙漠虎杖", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.YELLOW),
                    SummonerAttachmentEntityTypes.DESERT_TIGER,
                    MinionSlotType.Minion,
                    31.0F,
            0.25F,
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
    public static final DeferredItem<SummonerWeaponItem<TerraprismaMinion>> TERRAPRISMA = registerItem("terraprisma", "泰拉棱镜", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PINK),
                    SummonerAttachmentEntityTypes.TERRAPRISMA,
                    MinionSlotType.Minion,
                    67.0F,
            0.4F,
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
    public static final DeferredItem<SummonerWeaponItem<RuinRelicMinion>> RUINOUS_STAFF = registerItemWithEnglishName("ruinous_staff", "毁灭法杖", "Ruinous Staff", () -> new SummonerWeaponItem<>(
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
    public static final DeferredItem<SummonerWeaponItem<EyeLaserTurretMinion>> EYE_LASER_TURRET_STAFF = registerItem("eye_laser_turret_staff", "眼球激光塔法杖", () -> new EyeLaserTurretStaffItem(
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
    public static final DeferredItem<SummonerWeaponItem<VexTotemMinion>> VEX_TOTEM = registerItemWithEnglishName("vex_totem", "恼鬼图腾", "Vex Totem", () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.VEX_TOTEM,
                    MinionSlotType.Minion,
                    23.5F,
                    0.5F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));

    public static final DeferredItem<SummonerWeaponItem<MiniDestroyerMinion>> MINI_EARTH_SHAKER = registerItemWithEnglishName("mini_earth_shaker", "迷你撼地锤", "Mini Earth Shaker", () -> new SummonerWeaponItem<>(
            new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE),
            SummonerAttachmentEntityTypes.MINI_DESTROYER,
            MinionSlotType.Minion,
            2.0F,
            0.2F,
            0.0F,
            SummonerSoundEvents.USE_MINION_WEAPON,
            (weapon, player, itemStack) -> {
                SummonerHelper helper = SummonerHelper.get(player);
                if (helper.canSummon(weapon.getSlotType(itemStack), 1)) {
                    List<MiniDestroyerMinion> segments = helper.getEntityData().get(weapon.getEntityType());
                    MiniDestroyerMinion tail = weapon.createMinion(player, itemStack);
                    MiniDestroyerMinion body;
                    if (segments.isEmpty()) {
                        MiniDestroyerMinion head = weapon.createMinion(player, itemStack);
                        body = weapon.createMinion(player, itemStack);
                        head.setSlotCost(0);
                        body.setSlotCost(0);
                        body.setOrder(1);
                        head.setSameSize(3);
                        body.setSameSize(3);
                        head.init(new PathNode(player.position().add(0, 3, 0), 0, 0, 0));
                        body.init(head.getCurrentPathNode().modifyPos(head.getPos().add(0, 0, -body.getSegmentDistance())));
                        helper.add(head);
                        helper.add(body);
                    } else {
                        body = segments.getLast();
                        for (MiniDestroyerMinion segment : segments) {
                            segment.setSameSize(segments.size() + 1);
                        }
                    }
                    tail.setOrder(body.getOrder() + 1);
                    tail.setSameSize(body.getSameSize());
                    tail.init(body.getCurrentPathNode().modifyPos(body.getPos().subtract(body.getLookAngle().scale(tail.getSegmentDistance()))));
                    MiniProbeMinion probe = new MiniProbeMinion();
                    probe.copyAttributes(tail);
                    probe.attachTo(body);
                    helper.add(tail);
                    helper.add(probe);
                }
            },
            (weapon, player, itemStack) -> {
                SummonerHelper helper = SummonerHelper.get(player);
                helper.getEntityData().remove(SummonerAttachmentEntityTypes.MINI_DESTROYER.get());
                helper.getEntityData().remove(SummonerAttachmentEntityTypes.MINI_PROBE.get());
            }
    ));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        Confluence.addLanguageProvider(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        Confluence.addLanguageProvider(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
