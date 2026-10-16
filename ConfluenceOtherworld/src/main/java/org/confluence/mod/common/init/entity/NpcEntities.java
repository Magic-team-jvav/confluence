package org.confluence.mod.common.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.CreatureAttributeBuilder;
import org.confluence.mod.common.entity.npc.*;
import org.confluence.mod.common.entity.npc.ai.NPCCombatActions;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.projectile.NPCProjectileEffects;
import org.confluence.mod.common.init.item.*;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.entity.ModEntities.withAttributes;

public class NpcEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            net.minecraft.core.registries.Registries.ENTITY_TYPE, Confluence.MODID);

    // 常驻城镇：向导
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> GUIDE = withAttributes(register("guide", "向导", () -> Items.BOW, NPCCombatActions.ARROW, builder -> builder.maxHealth(65).damage(3).defense(30).projectileSpeed(1.5).healthRegeneration(2)),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：商人与护士
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> MERCHANT = withAttributes(register("merchant", "商人", ConsumableItems.THROWING_KNIVE, NPCCombatActions.thrown(ConsumableItems.THROWING_KNIVE::toStack, NPCProjectileEffects.NONE), builder -> builder.maxHealth(65).damage(4).projectileSpeed(1.2)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<NurseNPC>> NURSE = withAttributes(register("nurse", "护士", NurseNPC::new, () -> Items.SPLASH_POTION, NPCCombatActions.thrown(() -> new ItemStack(Items.SPLASH_POTION), NPCProjectileEffects.POISON), builder -> builder.maxHealth(65).damage(3).attackRange(5).attackInterval(35).projectileSpeed(1.1), 0.6F, 1.85F),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：爆破专家与军火商
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> DEMOLITIONIST = withAttributes(register("demolitionist", "爆破专家", ConsumableItems.GRENADE, NPCCombatActions.thrown(ConsumableItems.GRENADE::toStack, NPCProjectileEffects.EXPLOSIVE), builder -> builder.maxHealth(65).damage(6).attackRange(8).retreatRange(5).attackInterval(45)
                    .projectileSpeed(0.8)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> ARMS_DEALER = withAttributes(register("arms_dealer", "军火商", npc -> NPCCombatActions.isHardmode() ? GunItems.MINISHARK.get() : GunItems.FLINTLOCK_PISTOL.get(), NPCCombatActions.ARMS_DEALER, builder -> builder.maxHealth(65).damage(7).attackRange(13).attackInterval(30).projectileSpeed(2)),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：染料商与油漆工
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> DYE_TRADER = withAttributes(register("dye_trader", "染料商", SwordItems.EXOTIC_SCIMITAR, NPCCombatActions.MELEE, builder -> builder.maxHealth(65).damage(3).attackRange(3).retreatRange(3).attackInterval(25)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> PAINTER = withAttributes(register("painter", "油漆工", PaintItems.PAINTBRUSH, NPCCombatActions.thrown(PaintItems.PAINT::toStack, NPCProjectileEffects.NONE), builder -> builder.maxHealth(65).damage(3).attackInterval(20).projectileSpeed(1.5)),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：动物学家与树妖
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> ZOOLOGIST = withAttributes(register("zoologist", "动物学家", () -> Items.AIR, NPCCombatActions.MELEE, builder -> builder.maxHealth(65).damage(4).attackRange(3).retreatRange(3).attackInterval(20)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<DryadNPC>> DRYAD = withAttributes(register("dryad", "树妖", DryadNPC::new, () -> Items.AIR, NPCCombatActions.DRYAD_WARD, builder -> builder.maxHealth(65).damage(2).attackRange(18.75).retreatRange(4).attackInterval(200), 0.6F, 1.85F),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：巫医与服装商
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> WITCH_DOCTOR = withAttributes(register("witch_doctor", "巫医", GunItems.BLOWGUN, NPCCombatActions.thrown(() -> new ItemStack(Items.ARROW), NPCProjectileEffects.POISON), builder -> builder.maxHealth(65).damage(6).attackRange(12).attackInterval(25).projectileSpeed(1.6)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> CLOTHIER = withAttributes(register("clothier", "服装商", ManaWeaponItems.BOOK_OF_SKULLS, NPCCombatActions.SHADOWFLAME_SKULL, builder -> builder.maxHealth(65).damage(5).attackRange(12).attackInterval(35).projectileSpeed(0.7)),
            () -> CreatureAttributeBuilder.npc().build());

    // 常驻城镇：派对女孩
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> PARTY_GIRL = withAttributes(register("party_girl", "派对女孩", ConsumableItems.GRENADE, NPCCombatActions.thrown(ConsumableItems.GRENADE::toStack, NPCProjectileEffects.EXPLOSIVE), builder -> builder.maxHealth(65).damage(8).attackRange(9).retreatRange(5).attackInterval(50)
                    .projectileSpeed(0.9)),
            () -> CreatureAttributeBuilder.npc().build());

    // 海洋救援：渔夫及女性变种
    public static final DeferredHolder<EntityType<?>, EntityType<AnglerNPC>> ANGLER = withAttributes(register("angler", "渔夫", AnglerNPC::new, ConsumableItems.FROST_DAGGERFISH, NPCCombatActions.thrown(ConsumableItems.FROST_DAGGERFISH::toStack, NPCProjectileEffects.NONE), builder -> builder.maxHealth(65).projectileSpeed(1.3), 0.6F, 1.4F),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<AnglerNPC>> FEMALE_ANGLER = withAttributes(register("female_angler", "渔女", AnglerNPC::new, ConsumableItems.FROST_DAGGERFISH, NPCCombatActions.thrown(ConsumableItems.FROST_DAGGERFISH::toStack, NPCProjectileEffects.NONE), builder -> builder.projectileSpeed(1.3), 0.45F, 1.45F),
            () -> CreatureAttributeBuilder.npc().build());

    // 蜘蛛洞救援：发型师
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> STYLIST = withAttributes(register("stylist", "发型师", SwordItems.STYLISH_SCISSORS, NPCCombatActions.MELEE, builder -> builder.maxHealth(65).damage(4).attackRange(3).retreatRange(3).retreatRange(3).attackInterval(20)),
            () -> CreatureAttributeBuilder.npc().build());

    // 地下救援：哥布林工匠
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> GOBLIN_TINKERER = withAttributes(register("goblin_tinkerer", "哥布林工匠", ConsumableItems.SPIKY_BALL, NPCCombatActions.SPIKY_BALL, builder -> builder.maxHealth(65).damage(4).attackRange(9).projectileSpeed(0.75)),
            () -> CreatureAttributeBuilder.npc().build());

    // 地牢救援：机械师
    public static final DeferredHolder<EntityType<?>, EntityType<MechanicNPC>> MECHANIC = withAttributes(register("mechanic", "机械师", MechanicNPC::new, BoomerangItems.COMBAT_WRENCH, NPCCombatActions.COMBAT_WRENCH, builder -> builder.maxHealth(65).damage(3).projectileSpeed(1.4), 0.6F, 1.85F),
            () -> CreatureAttributeBuilder.npc().build());

    // 地下沙漠救援：高尔夫球手
    public static final DeferredHolder<EntityType<?>, EntityType<GolferNPC>> GOLFER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("golfer", "高尔夫球手", GolferNPC::new, () -> Items.AIR, NPCCombatActions.thrown(() -> new ItemStack(Items.SNOWBALL), NPCProjectileEffects.NONE), builder -> builder.maxHealth(65).damage(10).attackRange(8).attackInterval(35).projectileSpeed(1.2), 0.6F, 1.85F)),
            () -> CreatureAttributeBuilder.npc().build());

    // 困难模式地下救援：巫师
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> WIZARD = withAttributes(register("wizard", "巫师", ManaWeaponItems.FLOWER_OF_FIRE, NPCCombatActions.FIREBALL, builder -> builder.maxHealth(65).damage(5).attackRange(12).attackInterval(35).projectileSpeed(0.75)),
            () -> CreatureAttributeBuilder.npc().build());

    // 地狱转化：税收官
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> TAX_COLLECTOR = withAttributes(register("tax_collector", "税收官", () -> Items.AIR, NPCCombatActions.MELEE, builder -> builder.maxHealth(65).damage(4).attackRange(3).retreatRange(3)),
            () -> CreatureAttributeBuilder.npc().build());

    // 发光蘑菇城镇：松露人
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> TRUFFLE = withAttributes(register("truffle", "松露人", () -> Items.AIR, NPCCombatActions.TRUFFLE_SPORES, builder -> builder.maxHealth(65).damage(4).attackRange(8).attackInterval(25).projectileSpeed(0.8)),
            () -> CreatureAttributeBuilder.npc().build());

    // 困难模式城镇：蒸汽朋克人与机器侠
    public static final DeferredHolder<EntityType<?>, EntityType<BurstGunNPC>> STEAMPUNKER = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("steampunker", "蒸汽朋克人", BurstGunNPC::new, () -> Items.AIR, (npc, target, values) -> ((BurstGunNPC) npc).startBurst(target, values), builder -> builder.maxHealth(65).damage(8).attackRange(14).attackInterval(30).projectileSpeed(2.0), 0.6F, 1.85F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> CYBORG = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("cyborg", "机器侠", () -> Items.AIR, NPCCombatActions.CYBORG, builder -> builder.maxHealth(65).damage(10).attackRange(14).retreatRange(4).attackInterval(40).projectileSpeed(1.2))),
            () -> CreatureAttributeBuilder.npc().build());

    // 临时访客：旅商
    public static final DeferredHolder<EntityType<?>, EntityType<TravelingMerchantNPC>> TRAVELING_MERCHANT = withAttributes(register("traveling_merchant", "旅商", TravelingMerchantNPC::new, npc -> NPCCombatActions.isHardmode() ? Items.BOW : GunItems.FLINTLOCK_PISTOL.get(), NPCCombatActions.TRAVELING_MERCHANT, builder -> builder.maxHealth(65).damage(4).attackRange(12).attackInterval(25).projectileSpeed(2), 0.6F, 1.85F),
            () -> CreatureAttributeBuilder.npc().build());

    // 地下游商：骷髅商人
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonMerchantNPC>> SKELETON_MERCHANT = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("skeleton_merchant", "骷髅商人", SkeletonMerchantNPC::new, () -> Items.BONE, (npc, target, values) -> ((SkeletonMerchantNPC) npc).throwBone(target, values), builder -> builder.maxHealth(65).defense(30).damage(10).attackRange(8).attackInterval(30).projectileSpeed(1.2), 0.6F, 1.85F)),
            () -> CreatureAttributeBuilder.npc().build());

    // 地牢入口：老人
    public static final DeferredHolder<EntityType<?>, EntityType<OldManNPC>> OLD_MAN = withAttributes(register("old_man", "老人", OldManNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0).attackRange(0).retreatRange(6).attackInterval(Integer.MAX_VALUE)
                    .healthRegeneration(0), 0.6F, 1.85F),
            () -> CreatureAttributeBuilder.npc().build());

    // 城镇史莱姆：书呆子、酷酷、长者与笨拙
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> NERDY_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("nerdy_slime", "书呆子史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> COOL_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("cool_slime", "酷酷史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> ELDER_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("elder_slime", "长者史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> CLUMSY_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("clumsy_slime", "笨拙史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());

    // 城镇史莱姆：天后、暴躁、神秘与侍卫
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> DIVA_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("diva_slime", "天后史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> SURLY_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("surly_slime", "暴躁史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> MYSTIC_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("mystic_slime", "神秘史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());
    public static final DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>> SQUIRE_SLIME = withAttributes(DevelopmentSpawnPolicy.developmentOnly(register("squire_slime", "侍卫史莱姆", TownSlimeNPC::new, () -> Items.AIR, (npc, target, values) -> {}, builder -> builder.maxHealth(65).damage(0), 0.8F, 0.7F)),
            () -> CreatureAttributeBuilder.npc().build());

    /// 城镇史莱姆是独立类型的无序集合，不参与性别变种的职业合并。
    public static final Set<DeferredHolder<EntityType<?>, EntityType<TownSlimeNPC>>> TOWN_SLIMES = Set.of(
            NERDY_SLIME, COOL_SLIME, ELDER_SLIME, CLUMSY_SLIME, DIVA_SLIME, SURLY_SLIME, MYSTIC_SLIME, SQUIRE_SLIME);

    /// 首次查询时才解析注册项，避免外部注册类初始化时访问尚未绑定的实体类型。
    private static final class ProfessionVariants {
        /// 只登记变种到基础职业的映射；其他类型默认代表自身。
        private static final Map<EntityType<?>, EntityType<?>> PROFESSIONS = Map.of(
                FEMALE_ANGLER.get(), ANGLER.get());
    }

    /// 返回变种所属职业，生成、救援与选房共用这一归属，不合并性别专属对话与偏好。
    public static EntityType<?> getProfession(EntityType<?> type) {
        return type == null ? null : ProfessionVariants.PROFESSIONS.getOrDefault(type, type);
    }

    /// 比较职业身份；普通 NPC 和未登记额外变种的类型默认只与自身同族。
    public static boolean isSameProfession(EntityType<?> first, EntityType<?> second) {
        return first != null && second != null && getProfession(first) == getProfession(second);
    }

    /// 常规 NPC（含老人和游商）支持微光外观，城镇史莱姆保留自身的宠物转换规则。
    public static boolean hasShimmerAppearance(EntityType<?> type) {
        return TOWN_SLIMES.stream().noneMatch(entry -> entry.get() == type)
                && ENTITIES.getEntries().stream().anyMatch(entry -> entry.get() == type);
    }

    /// 注册没有额外交互或生命周期逻辑、使用固定武器的普通城镇 NPC。

    /// 注册没有额外交互或生命周期逻辑、可按当前状态切换武器的普通城镇 NPC。

    /// 注册具有专用实体类、碰撞尺寸和固定武器的城镇 NPC。
    private static <T extends BaseNPC> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, NPCFactory<T> factory,
                                                                              Supplier<? extends Item> weapon,
                                                                              NPCCombatProfile.Attack attack,
                                                                              Consumer<NPCCombatProfile.Builder> settings,
                                                                              float width, float height) {
        return register(name, factory, ignored -> weapon.get(), attack, settings, width, height);
    }

    /// 构建不可变战斗定义，并把它交给实体工厂；注册类只负责声明与 Forge 注册。
    private static <T extends BaseNPC> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, NPCFactory<T> factory,
                                                                              Function<BaseNPC, Item> weapon,
                                                                              NPCCombatProfile.Attack attack,
                                                                              Consumer<NPCCombatProfile.Builder> settings,
                                                                              float width, float height) {
        NPCCombatProfile.Builder builder = NPCCombatProfile.builder(weapon, attack);
        settings.accept(builder);
        NPCCombatProfile profile = builder.build();
        return ENTITIES.register(name, id -> EntityType.Builder.<T>of(
                        (type, level) -> factory.create(type, level, profile), MobCategory.CREATURE)
                .sized(width, height).clientTrackingRange(10).build(id.toString()));
    }

    @FunctionalInterface
    private interface NPCFactory<T extends BaseNPC> {
        /// 使用注册类型、世界和已经校验的不可变战斗配置创建 NPC。
        T create(EntityType<T> type, Level level, NPCCombatProfile profile);
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> register(String registryId, String chineseName, Supplier<? extends Item> weapon, NPCCombatProfile.Attack attack, Consumer<NPCCombatProfile.Builder> settings) {
        DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> entry = register(registryId, SimpleNPC::new, ignored -> weapon.get(), attack, settings, 0.6F, 1.85F);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> register(String registryId, String chineseName, Function<BaseNPC, Item> weapon, NPCCombatProfile.Attack attack, Consumer<NPCCombatProfile.Builder> settings) {
        DeferredHolder<EntityType<?>, EntityType<SimpleNPC>> entry = register(registryId, SimpleNPC::new, weapon, attack, settings, 0.6F, 1.85F);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends BaseNPC> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryId, String chineseName, NPCFactory<T> factory, Supplier<? extends Item> weapon, NPCCombatProfile.Attack attack, Consumer<NPCCombatProfile.Builder> settings, float width, float height) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = register(registryId, factory, ignored -> weapon.get(), attack, settings, width, height);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends BaseNPC> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryId, String chineseName, NPCFactory<T> factory, Function<BaseNPC, Item> weapon, NPCCombatProfile.Attack attack, Consumer<NPCCombatProfile.Builder> settings, float width, float height) {
        DeferredHolder<EntityType<?>, EntityType<T>> entry = register(registryId, factory, weapon, attack, settings, width, height);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}
