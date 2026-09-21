package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Confluence.MODID);

    public static final RegistryObject<SoundEvent> TRANSMISSION = register("transmission"); // 泰拉传送
    public static final RegistryObject<SoundEvent> LIGHTSABER_OPEN = register("lightsaber_open"); // 光剑
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT = register("regular_staff_shoot"); // 法杖1
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT_2 = register("regular_staff_shoot_2"); // 法杖2
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT_3 = register("regular_staff_shoot_3"); // 法杖3 滋水专属
    public static final RegistryObject<SoundEvent> LIFE_CRYSTAL_USE = register("life_crystal_use");// 生命水晶
    public static final RegistryObject<SoundEvent> MANA_STAR_USE = register("mana_star_use"); // 魔力水晶
    public static final RegistryObject<SoundEvent> COINS = register("coins"); // 币
    public static final RegistryObject<SoundEvent> COINS_LARGE = register("coins_large"); // 捡起一堆钱
    public static final RegistryObject<SoundEvent> COINS_MEDIUM = register("coins_medium"); // 捡起适量钱
    public static final RegistryObject<SoundEvent> COINS_SMALL = register("coins_small"); // 捡起一点钱
    public static final RegistryObject<SoundEvent> COOLDOWN_RECOVERY = register("cooldown_recovery"); // CD冷却
    public static final RegistryObject<SoundEvent> BOW_COOLDOWN_RECOVERY = register("bow_cooldown_recovery"); // CD冷却-弓箭
    public static final RegistryObject<SoundEvent> FROZEN_ARROW = register("frozen_arrow"); // 冰雪射弹
    public static final RegistryObject<SoundEvent> FROZEN_BROKEN = register("frozen_broken"); // 冰雪射弹消失
    public static final RegistryObject<SoundEvent> SHIMMER_DETACHMENT = register("shimmer_detachment"); // 脱离微光
    public static final RegistryObject<SoundEvent> SHIMMER_EVOLUTION = register("shimmer_evolution"); // 嬗变
    public static final RegistryObject<SoundEvent> SHIMMER_IMMERSION = register("shimmer_immersion"); // 生物入微光
    public static final RegistryObject<SoundEvent> SHIMMER_ITEM_INTERACTIONS = register("shimmer_item_interactions"); // 物品入微光
    public static final RegistryObject<SoundEvent> STAR = register("star"); // 坠星
    public static final RegistryObject<SoundEvent> STAR_LANDS = register("star_lands"); // 星星落地
    public static final RegistryObject<SoundEvent> TERRA_OPERATION = register("terra_operation"); // 操作音效
    public static final RegistryObject<SoundEvent> DECOUPLING = register("decoupling"); // 脱钩
    public static final RegistryObject<SoundEvent> ACHIEVEMENTS = register("achievements"); // 成就音效
    public static final RegistryObject<SoundEvent> TRANSMUTATION_USE = register("transmutation_use"); // 高级增益使用
    public static final RegistryObject<SoundEvent> HOOK_SHOOT = register("hook_shoot");
    public static final RegistryObject<SoundEvent> HOOK_ATTACH = register("hook_attach");
    public static final RegistryObject<SoundEvent> LUCYAXE_TALK = register("lucyaxe_talk");
    public static final RegistryObject<SoundEvent> REPEATER_ITEM_AERIAL_SHOOTING = register("repeater_item_aerial_shooting");
    public static final RegistryObject<SoundEvent> CRYSTAL_VILE_SHARD_SHOOT = register("crystal_vile_shard_shoot"); // 魔晶碎块

    // 枪械音效
    public static final RegistryObject<SoundEvent> GUN_AUTO = register("gun_auto");
    public static final RegistryObject<SoundEvent> GUN_FISH = register("gun_fish");
    public static final RegistryObject<SoundEvent> GUN_SPACE = register("gun_space");
    public static final RegistryObject<SoundEvent> GUN_FLAMETHROWER = register("gun_flamethrower");
    public static final RegistryObject<SoundEvent> GUN_GENERIC = register("gun_generic");
    public static final RegistryObject<SoundEvent> GUN_HIGHPOWER = register("gun_highpower");
    public static final RegistryObject<SoundEvent> GUN_NAIL = register("gun_nail");
    public static final RegistryObject<SoundEvent> GUN_TOXIC = register("gun_toxic");
    public static final RegistryObject<SoundEvent> PISTOL_DART = register("pistol_dart");
    public static final RegistryObject<SoundEvent> RIFLE_BURST = register("rifle_burst");
    public static final RegistryObject<SoundEvent> RIFLE_DART = register("rifle_dart");
    public static final RegistryObject<SoundEvent> SHOTGUN_ALIEN = register("shotgun_alien");
    public static final RegistryObject<SoundEvent> SHOTGUN_ALIEN_PROJ = register("shotgun_alien_proj");
    public static final RegistryObject<SoundEvent> SHOTGUN_MULTI = register("shotgun_multi");
    public static final RegistryObject<SoundEvent> SHOTGUN_TACTICAL = register("shotgun_tactical");
    public static final RegistryObject<SoundEvent> BLOWPIPE_SHOT = register("blowgun_shot");

    // 生物音效
    public static final RegistryObject<SoundEvent> ROUTINE_HURT = register("routine_hurt"); // 常规受伤音效
    public static final RegistryObject<SoundEvent> ROUTINE_DEATH = register("routine_death"); // 常规死亡音效
    public static final RegistryObject<SoundEvent> DRIPPLER_HURT = register("drippler_hurt"); // 滴滴怪受伤音效
    public static final RegistryObject<SoundEvent> DRIPPLER_DEATH = register("drippler_death"); // 滴滴怪死亡音效
    public static final RegistryObject<SoundEvent> METAL_HURT = register("metal_hurt"); // 金属受伤音效
    public static final RegistryObject<SoundEvent> METAL_DEATH = register("metal_death"); // 金属死亡（爆炸）音效
    public static final RegistryObject<SoundEvent> ROAR = register("roar"); // boss吼叫
    public static final RegistryObject<SoundEvent> HURRIED_ROARING = register("hurried_roaring"); //疯狗冲刺
    public static final RegistryObject<SoundEvent> DIG_SOUND = register("dig_sound"); //蠕虫挖掘
    public static final RegistryObject<SoundEvent> USE_MOUNTS = register("use_mounts"); // 召唤坐骑
    public static final RegistryObject<SoundEvent> WHIP_ATTACK = register("whip_attack"); // 鞭打
    // 饿鬼
    public static final RegistryObject<SoundEvent> THE_HUNGRY_DEATH = register("the_hungry_death");
    public static final RegistryObject<SoundEvent> THE_HUNGRY_HURT = register("the_hungry_hurt");
    // 血肉墙
    public static final RegistryObject<SoundEvent> WALL_OF_FLESH_HURT = register("wall_of_flesh_hurt");
    public static final RegistryObject<SoundEvent> WALL_OF_FLESH_ROAR = register("wall_of_flesh_roar");
    public static final RegistryObject<SoundEvent> WALL_OF_FLESH_SUMMON = register("wall_of_flesh_summon");
    // 血爬虫
    public static final RegistryObject<SoundEvent> BLOOD_CRAWLER_DEATH = register("blood_crawler_death");
    public static final RegistryObject<SoundEvent> BLOOD_CRAWLER_FREE = register("blood_crawler_free");
    public static final RegistryObject<SoundEvent> BLOOD_CRAWLER_HURT = register("blood_crawler_hurt");
    // 巨型卷壳怪
    public static final RegistryObject<SoundEvent> GIANT_SHELLY_DEATH = register("giant_shelly_death");
    public static final RegistryObject<SoundEvent> GIANT_SHELLY_FREE_0 = register("giant_shelly_free_0");
    public static final RegistryObject<SoundEvent> GIANT_SHELLY_FREE_1 = register("giant_shelly_free_1");
    public static final RegistryObject<SoundEvent> GIANT_SHELLY_HURT = register("giant_shelly_hurt");
    // 飞眼怪
    public static final RegistryObject<SoundEvent> VISUAL_NEURON_DEATH = register("visual_neuron_death");
    public static final RegistryObject<SoundEvent> VISUAL_NEURON_HURT = register("visual_neuron_hurt");
    // 脸怪
    public static final RegistryObject<SoundEvent> FACE_HOOT = register("face_hoot");
    // 僵尸或骷髅
    public static final RegistryObject<SoundEvent> TR_ZOMBIE_DEATH = register("tr_zombie_death");
    public static final RegistryObject<SoundEvent> TR_ZOMBIE_FREE = register("tr_zombie_free");
    public static final RegistryObject<SoundEvent> TR_SKELETON_HURT = register("tr_skeleton_hurt");
    // 蚁狮
    public static final RegistryObject<SoundEvent> ANTLION_DEATH = register("antlion_death");
    public static final RegistryObject<SoundEvent> ANTLION_HURT = register("antlion_hurt");
    public static final RegistryObject<SoundEvent> ANTLION_FREE = register("antlion_free");
    // 蚁狮蜂
    public static final RegistryObject<SoundEvent> ANTLION_SWARMER_DEATH = register("antlion_swarmer_death");
    public static final RegistryObject<SoundEvent> ANTLION_SWARMER_HURT = register("antlion_swarmer_hurt"); // 蚁狮蜂：受伤（补登记：音频早已存在但未注册）
    public static final RegistryObject<SoundEvent> ANTLION_SWARMER_FREE = register("antlion_swarmer_free");
    // 蚁狮射沙音效
    public static final RegistryObject<SoundEvent> SAND_SHOOT = register("sand_shoot");
    // 蝙蝠
    public static final RegistryObject<SoundEvent> BAT_DEATH = register("bat_death");
    // 胭脂虫
    public static final RegistryObject<SoundEvent> BEETLE_DEATH = register("beetle_death");
    // 血水母
    public static final RegistryObject<SoundEvent> BLOOD_JELLY_DEATH = register("blood_jelly_death");
    public static final RegistryObject<SoundEvent> BLOOD_JELLY_FREE = register("blood_jelly_free");
    // 骨蛇
    public static final RegistryObject<SoundEvent> BONE_SERPENT_DEATH = register("bone_serpent_death");
    // 恶魔
    public static final RegistryObject<SoundEvent> DEMON_DEATH = register("demon_death");
    public static final RegistryObject<SoundEvent> DEMON_FREE = register("demon_free");
    public static final RegistryObject<SoundEvent> DEMON_HURT = register("demon_hurt");
    // 地牢幽灵
    public static final RegistryObject<SoundEvent> DUNGEON_SPIRIT_DEATH = register("dungeon_spirit_death");
    public static final RegistryObject<SoundEvent> DUNGEON_SPIRIT_FREE = register("dungeon_spirit_free");
    public static final RegistryObject<SoundEvent> DUNGEON_SPIRIT_HURT = register("dungeon_spirit_hurt");
    // 花岗岩巨人
    public static final RegistryObject<SoundEvent> GRANITE_GOLEM_DEATH = register("granite_golem_death");
    public static final RegistryObject<SoundEvent> GRANITE_GOLEM_HURT = register("granite_golem_hurt");
    public static final RegistryObject<SoundEvent> GRANITE_GOLEM_FREE = register("granite_golem_free");
    // 水母
    public static final RegistryObject<SoundEvent> JELLYFISH_DEATH = register("jellyfish_death");
    public static final RegistryObject<SoundEvent> JELLYFISH_FREE = register("jellyfish_free");
    public static final RegistryObject<SoundEvent> JELLYFISH_HURT = register("jellyfish_hurt");
    // 妖精
    public static final RegistryObject<SoundEvent> PIXIE_DEATH = register("pixie_death");
    public static final RegistryObject<SoundEvent> PIXIE_FREE = register("pixie_free");
    public static final RegistryObject<SoundEvent> PIXIE_HURT = register("pixie_hurt");
    // 灵体相关音效
    public static final RegistryObject<SoundEvent> SOUL_DEATH = register("soul_death");
    // 独角兽相关音效
    public static final RegistryObject<SoundEvent> UNICORN_DEATH = register("unicorn_death");
    public static final RegistryObject<SoundEvent> UNICORN_HURT = register("unicorn_hurt");
    // 飞龙相关音效
    public static final RegistryObject<SoundEvent> WYVERN_DEATH = register("wyvern_death");
    public static final RegistryObject<SoundEvent> WYVERN_HURT = register("wyvern_hurt");
    // 血腥芽孢
    public static final RegistryObject<SoundEvent> BLOODY_SPORE_DEATH = register("bloody_spore_death");
    public static final RegistryObject<SoundEvent> BLOODY_SPORE_FUSE = register("bloody_spore_fuse");
    public static final RegistryObject<SoundEvent> BLOODY_SPORE_HIT = register("bloody_spore_hit");
    // 腐骴
    public static final RegistryObject<SoundEvent> DECAYEDER_AMBIENT = register("decayeder_ambient");
    public static final RegistryObject<SoundEvent> DECAYEDER_DEATH = register("decayeder_death");
    public static final RegistryObject<SoundEvent> DECAYEDER_HURT = register("decayeder_hurt");
    public static final RegistryObject<SoundEvent> DECAYEDER_STEP = register("decayeder_step");
    // 泰拉挥动
    public static final RegistryObject<SoundEvent> WAVING = register("waving");
    // 召唤
    public static final RegistryObject<SoundEvent> ROUTINE_SUMMON = register("routine_summon"); // 大多数召唤杖
    public static final RegistryObject<SoundEvent> SUMMON_EYE = register("summon_eye"); // 魔眼
    public static final RegistryObject<SoundEvent> SUMMON_IMP = register("summon_imp"); // 小鬼
    public static final RegistryObject<SoundEvent> SUMMON_MONEY_TROUGH = register("summon_money_trough"); // 存钱罐
    public static final RegistryObject<SoundEvent> SUMMON_HORNET = register("summon_hornet"); // 黄蜂召唤（补登记：音频早已存在但未注册）

    // ==================== 新增：TerrariaSounds(ogg) 导入音效 ====================
    // 以下常量由 TerrariaSounds(ogg) 按作用重命名后注册；名称遵循“作用名称+编号”规范。
    // 每组多文件家族由单个音效条目随机播放其中一个变体。


    public static final RegistryObject<SoundEvent> ABIGAIL_ATTACK = register("abigail_attack"); // 阿比盖尔：攻击
    public static final RegistryObject<SoundEvent> ABIGAIL_CRY = register("abigail_cry"); // 阿比盖尔：哭泣（3 个变体）
    public static final RegistryObject<SoundEvent> ABIGAIL_SUMMON = register("abigail_summon"); // 阿比盖尔：召唤
    public static final RegistryObject<SoundEvent> ABIGAIL_UPGRADE = register("abigail_upgrade"); // 阿比盖尔：强化（3 个变体）

    public static final RegistryObject<SoundEvent> ANGRY_NIMBUS_DEATH = register("angry_nimbus_death"); // 愤怒雨云怪：死亡
    public static final RegistryObject<SoundEvent> ANGRY_NIMBUS_FREE = register("angry_nimbus_free"); // 愤怒雨云怪：鸣叫（3 个变体）
    public static final RegistryObject<SoundEvent> ANGRY_NIMBUS_HURT = register("angry_nimbus_hurt"); // 愤怒雨云怪：受伤

    public static final RegistryObject<SoundEvent> BIRD_FREE = register("bird_free"); // 小鸟：鸣叫（5 个变体）

    public static final RegistryObject<SoundEvent> BLACK_RECLUSE_DEATH = register("black_recluse_death"); // 黑隐士：死亡
    public static final RegistryObject<SoundEvent> BLACK_RECLUSE_HURT = register("black_recluse_hurt"); // 黑隐士：受伤

    public static final RegistryObject<SoundEvent> BLIZZARD_INSIDE_BUILDING_LOOP = register("blizzard_inside_building_loop"); // 暴风雪：室内
    public static final RegistryObject<SoundEvent> BLIZZARD_STRONG_LOOP = register("blizzard_strong_loop"); // 暴风雪：强风暴循环

    public static final RegistryObject<SoundEvent> BLOOD_CRAWLER_DEATH_0 = register("blood_crawler_death_0"); // 血爬虫：死亡（与已有 mob/ 版本不同的录音）
    public static final RegistryObject<SoundEvent> BLOOD_JELLY_HURT = register("blood_jelly_hurt"); // 血水母：受伤
    public static final RegistryObject<SoundEvent> BLOOD_ZOMBIE_DEATH = register("blood_zombie_death"); // 血腥僵尸：死亡
    public static final RegistryObject<SoundEvent> BLOOD_ZOMBIE_FREE = register("blood_zombie_free"); // 血腥僵尸：鸣叫（3 个变体）
    public static final RegistryObject<SoundEvent> BLOOD_ZOMBIE_HURT = register("blood_zombie_hurt"); // 血腥僵尸：受伤

    public static final RegistryObject<SoundEvent> BRAIN_SCRAMBLER_DEATH = register("brain_scrambler_death"); // 大脑干扰器：死亡
    public static final RegistryObject<SoundEvent> BRAIN_SCRAMBLER_HURT = register("brain_scrambler_hurt"); // 大脑干扰器：受伤

    public static final RegistryObject<SoundEvent> BUBBLE_SHIELD_DEATH = register("bubble_shield_death"); // 泡泡护盾：死亡
    public static final RegistryObject<SoundEvent> BUBBLE_SHIELD_HURT = register("bubble_shield_hurt"); // 泡泡护盾：受伤

    public static final RegistryObject<SoundEvent> BURNING_SPHERE_HURT = register("burning_sphere_hurt"); // 燃烧球：受伤

    public static final RegistryObject<SoundEvent> BUTCHER_DEATH = register("butcher_death"); // 屠夫：死亡
    public static final RegistryObject<SoundEvent> BUTCHER_HURT = register("butcher_hurt"); // 屠夫：受伤

    public static final RegistryObject<SoundEvent> CAMERA = register("camera"); // 相机：模式截图

    public static final RegistryObject<SoundEvent> CELESTIAL_PILLAR_DEATH = register("celestial_pillar_death"); // 天界柱护盾：死亡

    public static final RegistryObject<SoundEvent> CHAT = register("chat"); // 聊天：窗口切换

    public static final RegistryObject<SoundEvent> CHATTERING_TEETH_FREE = register("chattering_teeth_free"); // 喋喋不休的牙齿：鸣叫

    public static final RegistryObject<SoundEvent> CHESTER_CLOSE = register("chester_close"); // 切斯特：关闭（2 个变体）
    public static final RegistryObject<SoundEvent> CHESTER_OPEN = register("chester_open"); // 切斯特：开启（2 个变体）

    public static final RegistryObject<SoundEvent> CLOWN_FREE = register("clown_free"); // 小丑：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> COCKATIEL_FREE = register("cockatiel_free"); // 玄凤鹦鹉：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> COIN = register("coin"); // 钱币：掉落/拾取（5 个变体）

    public static final RegistryObject<SoundEvent> CRAWDAD_DEATH = register("crawdad_death"); // 小龙虾：死亡
    public static final RegistryObject<SoundEvent> CRAWDAD_FREE = register("crawdad_free"); // 小龙虾：鸣叫
    public static final RegistryObject<SoundEvent> CRAWDAD_HURT = register("crawdad_hurt"); // 小龙虾：受伤

    public static final RegistryObject<SoundEvent> CRITTER_FREE = register("critter_free"); // 小动物：鸣叫

    public static final RegistryObject<SoundEvent> DD2_BALLISTA_TOWER_SHOT = register("dd2_ballista_tower_shot"); // 弩车哨塔：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_DEATH = register("dd2_betsy_death"); // 贝琪：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_FIREBALL_IMPACT = register("dd2_betsy_fireball_impact"); // 贝琪：命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_FIREBALL_SHOT = register("dd2_betsy_fireball_shot"); // 贝琪：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_FLAME_BREATH = register("dd2_betsy_flame_breath"); // 贝琪：喷火
    public static final RegistryObject<SoundEvent> DD2_BETSY_FLYING_CIRCLE_ATTACK = register("dd2_betsy_flying_circle_attack"); // 贝琪：攻击
    public static final RegistryObject<SoundEvent> DD2_BETSY_HURT = register("dd2_betsy_hurt"); // 贝琪：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_SCREAM = register("dd2_betsy_scream"); // 贝琪：尖啸
    public static final RegistryObject<SoundEvent> DD2_BETSY_SUMMON = register("dd2_betsy_summon"); // 贝琪：召唤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BETSY_WIND_ATTACK = register("dd2_betsy_wind_attack"); // 贝琪：攻击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BOOK_STAFF_CAST = register("dd2_book_staff_cast"); // 无限智慧之书：施法（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_BOOK_STAFF_TWISTER_LOOP = register("dd2_book_staff_twister_loop"); // 无限智慧之书：龙卷风循环
    public static final RegistryObject<SoundEvent> DD2_CRYSTAL_CART_IMPACT = register("dd2_crystal_cart_impact"); // 永恒水晶矿车：命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_ATTACK = register("dd2_dark_mage_attack"); // 黑暗法师：攻击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_CAST_HEAL = register("dd2_dark_mage_cast_heal"); // 黑暗法师：治疗施法（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_DEATH = register("dd2_dark_mage_death"); // 黑暗法师：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_HEAL_IMPACT = register("dd2_dark_mage_heal_impact"); // 黑暗法师：命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_HURT = register("dd2_dark_mage_hurt"); // 黑暗法师：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DARK_MAGE_SUMMON_SKELETON = register("dd2_dark_mage_summon_skeleton"); // 黑暗法师：召唤骷髅（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DEFEAT_SCENE = register("dd2_defeat_scene"); // 撒旦军队：失败
    public static final RegistryObject<SoundEvent> DD2_DEFENSE_TOWER_SPAWN = register("dd2_defense_tower_spawn"); // 撒旦军队哨塔：出现
    public static final RegistryObject<SoundEvent> DD2_DRAKIN_BREATH_IN = register("dd2_drakin_breath_in"); // 龙人：吸气（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DRAKIN_DEATH = register("dd2_drakin_death"); // 龙人：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DRAKIN_HURT = register("dd2_drakin_hurt"); // 龙人：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_DRAKIN_SHOT = register("dd2_drakin_shot"); // 龙人：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_ETHERIAN_PORTAL_DRYAD_TOUCH = register("dd2_etherian_portal_dryad_touch"); // 神秘传送门：树妖触碰
    public static final RegistryObject<SoundEvent> DD2_ETHERIAN_PORTAL_IDLE_LOOP = register("dd2_etherian_portal_idle_loop"); // 神秘传送门：待机循环
    public static final RegistryObject<SoundEvent> DD2_ETHERIAN_PORTAL_OPEN = register("dd2_etherian_portal_open"); // 神秘传送门：开启
    public static final RegistryObject<SoundEvent> DD2_ETHERIAN_PORTAL_SPAWN_ENEMY = register("dd2_etherian_portal_spawn_enemy"); // 神秘传送门：生成敌人（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_EXPLOSIVE_TRAP_EXPLODE = register("dd2_explosive_trap_explode"); // 爆炸陷阱：引爆（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_FLAMEBURST_TOWER_SHOT = register("dd2_flameburst_tower_shot"); // 烈焰爆裂哨塔：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GHASTLY_GLAIVE_IMPACT_GHOST = register("dd2_ghastly_glaive_impact_ghost"); // 恐怖长戟：幽灵命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GHASTLY_GLAIVE_PIERCE = register("dd2_ghastly_glaive_pierce"); // 恐怖长戟：贯穿（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_BOMBER_DEATH = register("dd2_goblin_bomber_death"); // 埃特尼亚哥布林投弹手：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_BOMBER_HURT = register("dd2_goblin_bomber_hurt"); // 埃特尼亚哥布林投弹手：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_BOMBER_SCREAM = register("dd2_goblin_bomber_scream"); // 埃特尼亚哥布林投弹手：尖啸（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_BOMBER_THROW = register("dd2_goblin_bomber_throw"); // 埃特尼亚哥布林投弹手：投掷（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_DEATH = register("dd2_goblin_death"); // 埃特尼亚哥布林：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_HURT = register("dd2_goblin_hurt"); // 埃特尼亚哥布林：受伤（6 个变体）
    public static final RegistryObject<SoundEvent> DD2_GOBLIN_SCREAM = register("dd2_goblin_scream"); // 埃特尼亚哥布林：尖啸（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_JAVELIN_THROWERS_ATTACK = register("dd2_javelin_throwers_attack"); // 埃特尼亚标枪投掷怪：攻击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_JAVELIN_THROWERS_DEATH = register("dd2_javelin_throwers_death"); // 埃特尼亚标枪投掷怪：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_JAVELIN_THROWERS_HURT = register("dd2_javelin_throwers_hurt"); // 埃特尼亚标枪投掷怪：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_JAVELIN_THROWERS_TAUNT = register("dd2_javelin_throwers_taunt"); // 埃特尼亚标枪投掷怪：嘲讽（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_DEATH = register("dd2_kobold_death"); // 狗头人：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_EXPLOSION = register("dd2_kobold_explosion"); // 狗头人：爆炸（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_FLYER_CHARGE_SCREAM = register("dd2_kobold_flyer_charge_scream"); // 狗头人滑翔者：尖啸（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_FLYER_DEATH = register("dd2_kobold_flyer_death"); // 狗头人滑翔者：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_FLYER_HURT = register("dd2_kobold_flyer_hurt"); // 狗头人滑翔者：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_HURT = register("dd2_kobold_hurt"); // 狗头人：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_IGNITE = register("dd2_kobold_ignite"); // 狗头人：点燃
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_IGNITE_LOOP = register("dd2_kobold_ignite_loop"); // 狗头人：点燃循环
    public static final RegistryObject<SoundEvent> DD2_KOBOLD_SCREAM_CHARGE_LOOP = register("dd2_kobold_scream_charge_loop"); // 狗头人：蓄力循环
    public static final RegistryObject<SoundEvent> DD2_LIGHTNING_AURA_ZAP = register("dd2_lightning_aura_zap"); // 闪电光环：电击（4 个变体）
    public static final RegistryObject<SoundEvent> DD2_LIGHTNING_BUG_DEATH = register("dd2_lightning_bug_death"); // 埃特尼亚闪电虫：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_LIGHTNING_BUG_HURT = register("dd2_lightning_bug_hurt"); // 埃特尼亚闪电虫：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_LIGHTNING_BUG_ZAP = register("dd2_lightning_bug_zap"); // 埃特尼亚闪电虫：电击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_MONK_STAFF_GROUND_IMPACT = register("dd2_monk_staff_ground_impact"); // 瞌睡章鱼：砸地命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_MONK_STAFF_GROUND_MISS = register("dd2_monk_staff_ground_miss"); // 瞌睡章鱼：砸地落空（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_MONK_STAFF_SWING = register("dd2_monk_staff_swing"); // 瞌睡章鱼：挥击（4 个变体）
    public static final RegistryObject<SoundEvent> DD2_OGRE_ATTACK = register("dd2_ogre_attack"); // 食人魔：攻击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_OGRE_DEATH = register("dd2_ogre_death"); // 食人魔：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_OGRE_GROUND_POUND = register("dd2_ogre_ground_pound"); // 食人魔：重砸
    public static final RegistryObject<SoundEvent> DD2_OGRE_HURT = register("dd2_ogre_hurt"); // 食人魔：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_OGRE_ROAR = register("dd2_ogre_roar"); // 食人魔：咆哮（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_OGRE_SPIT = register("dd2_ogre_spit"); // 食人魔：喷吐
    public static final RegistryObject<SoundEvent> DD2_PHANTOM_PHOENIX_SHOT = register("dd2_phantom_phoenix_shot"); // 幻影凤凰：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_SKELETON_DEATH = register("dd2_skeleton_death"); // 旧日军团骷髅：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_SKELETON_HURT = register("dd2_skeleton_hurt"); // 旧日军团骷髅：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_SKELETON_SUMMONED = register("dd2_skeleton_summoned"); // 旧日军团骷髅：出现
    public static final RegistryObject<SoundEvent> DD2_SKY_DRAGONS_FURY_CIRCLE = register("dd2_sky_dragons_fury_circle"); // 天空之龙之怒：环绕（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_SKY_DRAGONS_FURY_SHOT = register("dd2_sky_dragons_fury_shot"); // 天空之龙之怒：射击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_SKY_DRAGONS_FURY_SWING = register("dd2_sky_dragons_fury_swing"); // 天空之龙之怒：挥击（4 个变体）
    public static final RegistryObject<SoundEvent> DD2_SONIC_BOOM_BLADE_SLASH = register("dd2_sonic_boom_blade_slash"); // 飞龙：斩击（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WIN_SCENE = register("dd2_win_scene"); // 撒旦军队：胜利
    public static final RegistryObject<SoundEvent> DD2_WITHER_BEAST_AURA_PULSE = register("dd2_wither_beast_aura_pulse"); // 枯萎兽：光环脉冲（2 个变体）
    public static final RegistryObject<SoundEvent> DD2_WITHER_BEAST_CRYSTAL_IMPACT = register("dd2_wither_beast_crystal_impact"); // 枯萎兽：命中（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WITHER_BEAST_DEATH = register("dd2_wither_beast_death"); // 枯萎兽：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WITHER_BEAST_HURT = register("dd2_wither_beast_hurt"); // 枯萎兽：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WYVERN_DEATH = register("dd2_wyvern_death"); // 埃特尼亚飞龙：死亡（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WYVERN_DIVE_DOWN = register("dd2_wyvern_dive_down"); // 埃特尼亚飞龙：俯冲（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WYVERN_HURT = register("dd2_wyvern_hurt"); // 埃特尼亚飞龙：受伤（3 个变体）
    public static final RegistryObject<SoundEvent> DD2_WYVERN_SCREAM = register("dd2_wyvern_scream"); // 埃特尼亚飞龙：尖啸（3 个变体）

    public static final RegistryObject<SoundEvent> DEADLY_SPHERE_DEATH = register("deadly_sphere_death"); // 致命球：死亡
    public static final RegistryObject<SoundEvent> DEADLY_SPHERE_FREE = register("deadly_sphere_free"); // 致命球：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> DEADLY_SPHERE_HURT = register("deadly_sphere_hurt"); // 致命球：受伤

    public static final RegistryObject<SoundEvent> DEERCLOPS_DEATH = register("deerclops_death"); // 独眼巨鹿：死亡
    public static final RegistryObject<SoundEvent> DEERCLOPS_HIT = register("deerclops_hit"); // 独眼巨鹿：受击（3 个变体）
    public static final RegistryObject<SoundEvent> DEERCLOPS_ICE_ATTACK = register("deerclops_ice_attack"); // 独眼巨鹿：攻击（3 个变体）
    public static final RegistryObject<SoundEvent> DEERCLOPS_RUBBLE_ATTACK = register("deerclops_rubble_attack"); // 独眼巨鹿：攻击
    public static final RegistryObject<SoundEvent> DEERCLOPS_SCREAM = register("deerclops_scream"); // 独眼巨鹿：尖啸（3 个变体）
    public static final RegistryObject<SoundEvent> DEERCLOPS_STEP = register("deerclops_step"); // 独眼巨鹿：脚步

    public static final RegistryObject<SoundEvent> DERPLING_DEATH = register("derpling_death"); // 跳跳怪：死亡
    public static final RegistryObject<SoundEvent> DERPLING_FREE = register("derpling_free"); // 跳跳怪：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> DERPLING_HURT = register("derpling_hurt"); // 跳跳怪：受伤

    public static final RegistryObject<SoundEvent> DIG = register("dig"); // 方块：多数方块与墙壁被击中（3 个变体）

    public static final RegistryObject<SoundEvent> DOLPHIN_FREE = register("dolphin_free"); // 海豚：鸣叫

    public static final RegistryObject<SoundEvent> DOOR_CLOSE = register("door_close"); // 门：关闭
    public static final RegistryObject<SoundEvent> DOOR_OPEN = register("door_open"); // 门：开启

    public static final RegistryObject<SoundEvent> DOUBLE_JUMP = register("double_jump"); // 二段跳：二段跳

    public static final RegistryObject<SoundEvent> DR_MAN_FLY_DEATH = register("dr_man_fly_death"); // 苍蝇博士：死亡
    public static final RegistryObject<SoundEvent> DR_MAN_FLY_FREE = register("dr_man_fly_free"); // 苍蝇博士：鸣叫（3 个变体）
    public static final RegistryObject<SoundEvent> DR_MAN_FLY_HURT = register("dr_man_fly_hurt"); // 苍蝇博士：受伤

    public static final RegistryObject<SoundEvent> DRIP = register("drip"); // 水滴：滴落（3 个变体）

    public static final RegistryObject<SoundEvent> DRONE = register("drone"); // 无人机：Kwad Racer 无人机飞行

    public static final RegistryObject<SoundEvent> DROWN = register("drown"); // 溺水：玩家开始溺水

    public static final RegistryObject<SoundEvent> DST_FEMALE_HIT = register("dst_female_hit"); // 饥荒世界女玩家：受击（3 个变体）
    public static final RegistryObject<SoundEvent> DST_MALE_HIT = register("dst_male_hit"); // 饥荒世界男玩家：受击（3 个变体）

    public static final RegistryObject<SoundEvent> DUCK_FREE = register("duck_free"); // 鸭子：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> DUKE_FISHRON_DEATH = register("duke_fishron_death"); // 猪龙鱼公爵：死亡
    public static final RegistryObject<SoundEvent> DUKE_FISHRON_FREE = register("duke_fishron_free"); // 猪龙鱼公爵：鸣叫
    public static final RegistryObject<SoundEvent> DUKE_FISHRON_HURT = register("duke_fishron_hurt"); // 猪龙鱼公爵：受伤

    public static final RegistryObject<SoundEvent> EMPRESS_OF_LIGHT_DEATH = register("empress_of_light_death"); // 光之女皇：死亡

    public static final RegistryObject<SoundEvent> FEMALE_HIT = register("female_hit"); // 女玩家：受伤（3 个变体）

    public static final RegistryObject<SoundEvent> FLYING_SNAKE_DEATH = register("flying_snake_death"); // 飞蛇：死亡
    public static final RegistryObject<SoundEvent> FLYING_SNAKE_FREE = register("flying_snake_free"); // 飞蛇：鸣叫

    public static final RegistryObject<SoundEvent> FRANKENSTEIN_FREE = register("frankenstein_free"); // 科学怪人：鸣叫

    public static final RegistryObject<SoundEvent> FROG_FREE = register("frog_free"); // 青蛙：鸣叫

    public static final RegistryObject<SoundEvent> GHOUL_DEATH = register("ghoul_death"); // 食尸鬼：死亡
    public static final RegistryObject<SoundEvent> GHOUL_FREE = register("ghoul_free"); // 食尸鬼：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> GHOUL_HURT = register("ghoul_hurt"); // 食尸鬼：受伤

    public static final RegistryObject<SoundEvent> GIANT_TORTOISE_DEATH = register("giant_tortoise_death"); // 巨型陆龟：死亡
    public static final RegistryObject<SoundEvent> GIANT_TORTOISE_FREE = register("giant_tortoise_free"); // 巨型陆龟：鸣叫
    public static final RegistryObject<SoundEvent> GIANT_TORTOISE_HURT = register("giant_tortoise_hurt"); // 巨型陆龟：受伤

    public static final RegistryObject<SoundEvent> GIGAZAPPER_FREE = register("gigazapper_free"); // 千兆电击者：鸣叫（2 个变体）

    public static final RegistryObject<SoundEvent> GLOMMER_BOUNCE = register("glommer_bounce"); // 格罗姆：弹跳（2 个变体）

    public static final RegistryObject<SoundEvent> GOBLIN_WARLOCK_DEATH = register("goblin_warlock_death"); // 哥布林术士：死亡
    public static final RegistryObject<SoundEvent> GOBLIN_WARLOCK_FREE = register("goblin_warlock_free"); // 哥布林术士：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> GOBLIN_WARLOCK_HURT = register("goblin_warlock_hurt"); // 哥布林术士：受伤

    public static final RegistryObject<SoundEvent> GRAB = register("grab"); // 拾取：拾取物品

    public static final RegistryObject<SoundEvent> GRASS = register("grass"); // 植物：植物方块被击中 / 叶绿箭命中

    public static final RegistryObject<SoundEvent> ITEM_AMMO_BOX = register("item_ammo_box"); // 弹药箱：使用
    public static final RegistryObject<SoundEvent> ITEM_AXE_GUITAR = register("item_axe_guitar"); // 吉他斧：使用
    public static final RegistryObject<SoundEvent> ITEM_BEES_KNEES = register("item_bees_knees"); // 蜜蜂之膝：使用
    public static final RegistryObject<SoundEvent> ITEM_BELL = register("item_bell"); // 铃铛：使用
    public static final RegistryObject<SoundEvent> ITEM_BLOWGUN = register("item_blowgun"); // 吹箭筒：使用
    public static final RegistryObject<SoundEvent> ITEM_BOOMERANG_THROW = register("item_boomerang_throw"); // 回旋镖：投掷
    public static final RegistryObject<SoundEvent> ITEM_BOOT_HOVER = register("item_boot_hover"); // 靴子悬停：使用
    public static final RegistryObject<SoundEvent> ITEM_BOULDER_IMPACT = register("item_boulder_impact"); // 巨石：命中
    public static final RegistryObject<SoundEvent> ITEM_BRAIN_SCRAMBLER = register("item_brain_scrambler"); // 大脑干扰器：使用
    public static final RegistryObject<SoundEvent> ITEM_BUBBLE_GUN = register("item_bubble_gun"); // 泡泡枪：使用
    public static final RegistryObject<SoundEvent> ITEM_BUBBLE_POP = register("item_bubble_pop"); // 泡泡破裂：使用
    public static final RegistryObject<SoundEvent> ITEM_CELEBRATION = register("item_celebration"); // 庆典：使用
    public static final RegistryObject<SoundEvent> ITEM_CHAINSAW = register("item_chainsaw"); // 电锯：使用
    public static final RegistryObject<SoundEvent> ITEM_CLINGER_STAFF = register("item_clinger_staff"); // 爬藤怪法杖：使用
    public static final RegistryObject<SoundEvent> ITEM_CRACKED_DUNGEON_BRICK = register("item_cracked_dungeon_brick"); // 破裂地牢砖：使用
    public static final RegistryObject<SoundEvent> ITEM_CRYSTAL_CHARGE_IMPACT = register("item_crystal_charge_impact"); // 水晶弹：命中（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_CRYSTAL_SERPENT = register("item_crystal_serpent"); // 水晶蛇：使用
    public static final RegistryObject<SoundEvent> ITEM_DEADLY_SPHERE_STAFF = register("item_deadly_sphere_staff"); // 致命球法杖：使用
    public static final RegistryObject<SoundEvent> ITEM_DEATH_LASER = register("item_death_laser"); // 死亡激光：使用
    public static final RegistryObject<SoundEvent> ITEM_DEATH_SICKLE = register("item_death_sickle"); // 死神镰刀：使用
    public static final RegistryObject<SoundEvent> ITEM_DREADNAUTILUS = register("item_dreadnautilus"); // 恐惧鹦鹉螺：使用（3 个变体）
    public static final RegistryObject<SoundEvent> ITEM_DRILL = register("item_drill"); // 钻头：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_CLOSED_HIHAT = register("item_drum_closed_hihat"); // 鼓组 闭合踩镲：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_CYMBAL = register("item_drum_cymbal"); // 鼓组 镲片：使用（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_DRUM_FLOOR_TOM = register("item_drum_floor_tom"); // 鼓组 落地嗵鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_HIHAT = register("item_drum_hihat"); // 鼓组 踩镲：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_KICK = register("item_drum_kick"); // 鼓组 底鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_SNARE = register("item_drum_snare"); // 鼓组 军鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_TOM_HIGH = register("item_drum_tom_high"); // 鼓组 高音嗵鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_TOM_LOW = register("item_drum_tom_low"); // 鼓组 低音嗵鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_DRUM_TOM_MID = register("item_drum_tom_mid"); // 鼓组 中音嗵鼓：使用
    public static final RegistryObject<SoundEvent> ITEM_ELECTROSPHERE_IMPACT = register("item_electrosphere_impact"); // 电球导弹：命中
    public static final RegistryObject<SoundEvent> ITEM_EMPRESS_OF_LIGHT = register("item_empress_of_light"); // 光之女皇：使用（7 个变体）
    public static final RegistryObject<SoundEvent> ITEM_FART = register("item_fart"); // 放屁：使用
    public static final RegistryObject<SoundEvent> ITEM_FIREBALL_IMPACT = register("item_fireball_impact"); // 火球：命中
    public static final RegistryObject<SoundEvent> ITEM_FLAME_CAST = register("item_flame_cast"); // 火焰：施法
    public static final RegistryObject<SoundEvent> ITEM_FROST_IMPACT = register("item_frost_impact"); // 霜冻：命中
    public static final RegistryObject<SoundEvent> ITEM_FUZZY_CARROT = register("item_fuzzy_carrot"); // 毛绒胡萝卜：使用
    public static final RegistryObject<SoundEvent> ITEM_GOLF_BALL_SUNK = register("item_golf_ball_sunk"); // 高尔夫球入洞：使用
    public static final RegistryObject<SoundEvent> ITEM_GOLF_CLUB_SWING = register("item_golf_club_swing"); // 高尔夫球杆：挥击
    public static final RegistryObject<SoundEvent> ITEM_GOLF_WHISTLE = register("item_golf_whistle"); // 高尔夫哨：使用
    public static final RegistryObject<SoundEvent> ITEM_GRENADE_EXPLODE = register("item_grenade_explode"); // 榴弹爆炸：使用
    public static final RegistryObject<SoundEvent> ITEM_GRENADE_LAUNCH = register("item_grenade_launch"); // 榴弹发射：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_AM = register("item_guitar_am"); // 吉他 Am 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_BM = register("item_guitar_bm"); // 吉他 Bm 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_C = register("item_guitar_c"); // 吉他 C 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_D = register("item_guitar_d"); // 吉他 D 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_EM = register("item_guitar_em"); // 吉他 Em 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_GUITAR_G = register("item_guitar_g"); // 吉他 G 和弦：使用
    public static final RegistryObject<SoundEvent> ITEM_HARP = register("item_harp"); // 竖琴：使用
    public static final RegistryObject<SoundEvent> ITEM_HORNET_SHOOT = register("item_hornet_shoot"); // 黄蜂：射击
    public static final RegistryObject<SoundEvent> ITEM_ICE_BLOCK_PLACE = register("item_ice_block_place"); // 冰块放置：使用
    public static final RegistryObject<SoundEvent> ITEM_ICE_BREAK = register("item_ice_break"); // 冰块：破碎
    public static final RegistryObject<SoundEvent> ITEM_ICE_MIST_SUMMON = register("item_ice_mist_summon"); // 冰雾：召唤
    public static final RegistryObject<SoundEvent> ITEM_INFERNO_BOLT_IMPACT = register("item_inferno_bolt_impact"); // 狱火弹：命中
    public static final RegistryObject<SoundEvent> ITEM_INFERNO_FORK = register("item_inferno_fork"); // 狱火叉：使用
    public static final RegistryObject<SoundEvent> ITEM_KO_CANNON = register("item_ko_cannon"); // 击倒炮：使用
    public static final RegistryObject<SoundEvent> ITEM_LARVA_BREAK = register("item_larva_break"); // 幼虫：被击中 / 憎恶之蜂
    public static final RegistryObject<SoundEvent> ITEM_LASER_DRILL = register("item_laser_drill"); // 激光钻头：使用
    public static final RegistryObject<SoundEvent> ITEM_LASER_MACHINEGUN = register("item_laser_machinegun"); // 激光机枪：使用
    public static final RegistryObject<SoundEvent> ITEM_LASER_SHOOT = register("item_laser_shoot"); // 激光：射击
    public static final RegistryObject<SoundEvent> ITEM_LIFE_CRYSTAL = register("item_life_crystal"); // 生命水晶：使用
    public static final RegistryObject<SoundEvent> ITEM_LIGHTNING_ORB_SUMMON = register("item_lightning_orb_summon"); // 闪电球：召唤
    public static final RegistryObject<SoundEvent> ITEM_LIGHTNING_RITUAL_SUMMON = register("item_lightning_ritual_summon"); // 闪电仪式：召唤
    public static final RegistryObject<SoundEvent> ITEM_MAGIC_MISSILE = register("item_magic_missile"); // 魔法导弹：使用
    public static final RegistryObject<SoundEvent> ITEM_MANA_CRYSTAL = register("item_mana_crystal"); // 魔力水晶：使用
    public static final RegistryObject<SoundEvent> ITEM_MEOWMERE_IMPACT = register("item_meowmere_impact"); // 喵刀：命中（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_METEOR_IMPACT = register("item_meteor_impact"); // 陨石：命中
    public static final RegistryObject<SoundEvent> ITEM_METEOR_STAFF = register("item_meteor_staff"); // 陨石法杖：使用
    public static final RegistryObject<SoundEvent> ITEM_MINECART_BOUNCE = register("item_minecart_bounce"); // 矿车：弹跳
    public static final RegistryObject<SoundEvent> ITEM_MINECART_RIDE = register("item_minecart_ride"); // 矿车行驶：使用
    public static final RegistryObject<SoundEvent> ITEM_MINECART_SLOW = register("item_minecart_slow"); // 矿车减速：使用
    public static final RegistryObject<SoundEvent> ITEM_MINECART_TRACK = register("item_minecart_track"); // 矿车轨道：使用
    public static final RegistryObject<SoundEvent> ITEM_MISSILE_LAUNCH = register("item_missile_launch"); // 导弹发射：使用
    public static final RegistryObject<SoundEvent> ITEM_MUSIC_BOX = register("item_music_box"); // 八音盒：使用
    public static final RegistryObject<SoundEvent> ITEM_NEBULA_ARCANUM = register("item_nebula_arcanum"); // 星云奥秘：使用
    public static final RegistryObject<SoundEvent> ITEM_NIMBUS_RAIN = register("item_nimbus_rain"); // 雨云降雨：使用
    public static final RegistryObject<SoundEvent> ITEM_PET_SUMMON = register("item_pet_summon"); // 宠物：召唤
    public static final RegistryObject<SoundEvent> ITEM_PHANTASM_DRAGON_SUMMON = register("item_phantasm_dragon_summon"); // 幻影龙：召唤
    public static final RegistryObject<SoundEvent> ITEM_PHANTASMAL_BOLT = register("item_phantasmal_bolt"); // 幻影矢：使用（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_PHASEBLADE = register("item_phaseblade"); // 晶光刃：使用
    public static final RegistryObject<SoundEvent> ITEM_POGO_STICK = register("item_pogo_stick"); // 弹簧单高跷：使用
    public static final RegistryObject<SoundEvent> ITEM_POO = register("item_poo"); // 便便：使用
    public static final RegistryObject<SoundEvent> ITEM_PORTAL_GUN = register("item_portal_gun"); // 传送枪：使用（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_POTION_DRINK = register("item_potion_drink"); // 药水饮用：使用
    public static final RegistryObject<SoundEvent> ITEM_PROJECTILE_REFLECT = register("item_projectile_reflect"); // 弹幕反弹：使用
    public static final RegistryObject<SoundEvent> ITEM_PULSE_BOW = register("item_pulse_bow"); // 脉冲弓：使用
    public static final RegistryObject<SoundEvent> ITEM_QUEEN_SLIME = register("item_queen_slime"); // 史莱姆皇后：使用（3 个变体）
    public static final RegistryObject<SoundEvent> ITEM_RAINBOW_GUN = register("item_rainbow_gun"); // 彩虹枪：使用
    public static final RegistryObject<SoundEvent> ITEM_RAZORBLADE_TYPHOON = register("item_razorblade_typhoon"); // 剃刀台风：使用
    public static final RegistryObject<SoundEvent> ITEM_REFORGE = register("item_reforge"); // 重铸：使用
    public static final RegistryObject<SoundEvent> ITEM_SCALY_TRUFFLE = register("item_scaly_truffle"); // 鳞片松露：使用
    public static final RegistryObject<SoundEvent> ITEM_SENTRY_BEAM = register("item_sentry_beam"); // 哨戒光束：使用
    public static final RegistryObject<SoundEvent> ITEM_SENTRY_SUMMON = register("item_sentry_summon"); // 哨戒：召唤
    public static final RegistryObject<SoundEvent> ITEM_SHADOWBEAM = register("item_shadowbeam"); // 暗影光束：使用
    public static final RegistryObject<SoundEvent> ITEM_SHADOWFLAME_BOW = register("item_shadowflame_bow"); // 暗影焰弓：使用
    public static final RegistryObject<SoundEvent> ITEM_SHADOWFLAME_HEX = register("item_shadowflame_hex"); // 暗影焰妖术：使用
    public static final RegistryObject<SoundEvent> ITEM_SHIMMER_USE_2 = register("item_shimmer_use_2"); // 微光使用：使用
    public static final RegistryObject<SoundEvent> ITEM_SLAP_HAND = register("item_slap_hand"); // 拍击手：使用
    public static final RegistryObject<SoundEvent> ITEM_SLIMY_SADDLE = register("item_slimy_saddle"); // 史莱姆鞍：使用
    public static final RegistryObject<SoundEvent> ITEM_SNAKE_CHARMER_FLUTE = register("item_snake_charmer_flute"); // 耍蛇人之笛：使用
    public static final RegistryObject<SoundEvent> ITEM_SNOW_BREAK = register("item_snow_break"); // 雪块：破碎（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_SNOWBALL_IMPACT = register("item_snowball_impact"); // 雪球：命中
    public static final RegistryObject<SoundEvent> ITEM_SOLAR_ERUPTION = register("item_solar_eruption"); // 日曜喷发：使用
    public static final RegistryObject<SoundEvent> ITEM_SPIDER_STAFF = register("item_spider_staff"); // 蜘蛛法杖：使用
    public static final RegistryObject<SoundEvent> ITEM_STAFF_OF_EARTH = register("item_staff_of_earth"); // 大地法杖：使用
    public static final RegistryObject<SoundEvent> ITEM_STAR_WRATH = register("item_star_wrath"); // 狂星之怒：使用
    public static final RegistryObject<SoundEvent> ITEM_SWING = register("item_swing"); // 挥击：多数挥击类武器/工具（2 个变体）
    public static final RegistryObject<SoundEvent> ITEM_TERRA_BLADE = register("item_terra_blade"); // 泰拉刃：使用
    public static final RegistryObject<SoundEvent> ITEM_TOXIC_FLASK = register("item_toxic_flask"); // 毒气瓶：使用
    public static final RegistryObject<SoundEvent> ITEM_TOXIC_FLASK_IMPACT = register("item_toxic_flask_impact"); // 毒气瓶：命中
    public static final RegistryObject<SoundEvent> ITEM_VAMPIRE_KNIVES = register("item_vampire_knives"); // 吸血鬼刀：使用
    public static final RegistryObject<SoundEvent> ITEM_VOID_BAG = register("item_void_bag"); // 虚空袋：使用
    public static final RegistryObject<SoundEvent> ITEM_WAFFLE_IRON = register("item_waffle_iron"); // 华夫饼铁：使用
    public static final RegistryObject<SoundEvent> ITEM_WATER_BOLT = register("item_water_bolt"); // 水矢：使用
    public static final RegistryObject<SoundEvent> ITEM_WHIP_ATTACK_0 = register("item_whip_attack_0"); // 鞭子：攻击
    public static final RegistryObject<SoundEvent> ITEM_WINGS = register("item_wings"); // 翅膀：使用
    public static final RegistryObject<SoundEvent> ITEM_ZAPINATOR = register("item_zapinator"); // 电击枪：使用

    public static final RegistryObject<SoundEvent> LAVAFALL = register("lavafall"); // 岩浆瀑布：环境音

    public static final RegistryObject<SoundEvent> LIHZAHRD_DEATH = register("lihzahrd_death"); // 蜥蜴人：死亡
    public static final RegistryObject<SoundEvent> LIHZAHRD_FREE = register("lihzahrd_free"); // 蜥蜴人：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> LIHZAHRD_HURT = register("lihzahrd_hurt"); // 蜥蜴人：受伤

    public static final RegistryObject<SoundEvent> LIQUIDS_HONEY_LAVA = register("liquids_honey_lava"); // 液体：蜂蜜与岩浆（3 个变体）
    public static final RegistryObject<SoundEvent> LIQUIDS_HONEY_WATER = register("liquids_honey_water"); // 液体：蜂蜜与水（3 个变体）
    public static final RegistryObject<SoundEvent> LIQUIDS_WATER_LAVA = register("liquids_water_lava"); // 液体：水与岩浆（3 个变体）

    public static final RegistryObject<SoundEvent> LUNATIC_CULTIST_DEATH = register("lunatic_cultist_death"); // 拜月教邪教徒：死亡
    public static final RegistryObject<SoundEvent> LUNATIC_CULTIST_FREE = register("lunatic_cultist_free"); // 拜月教邪教徒：鸣叫（4 个变体）
    public static final RegistryObject<SoundEvent> LUNATIC_CULTIST_HURT = register("lunatic_cultist_hurt"); // 拜月教邪教徒：受伤
    public static final RegistryObject<SoundEvent> LUNATIC_CULTIST_SUMMONED_FREE = register("lunatic_cultist_summoned_free"); // 拜月教邪教徒：鸣叫

    public static final RegistryObject<SoundEvent> MACAW_FREE = register("macaw_free"); // 金刚鹦鹉：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> MARTIAN_DRONE_DEATH = register("martian_drone_death"); // 火星无人机：死亡
    public static final RegistryObject<SoundEvent> MARTIAN_DRONE_FREE = register("martian_drone_free"); // 火星无人机：鸣叫（3 个变体）
    public static final RegistryObject<SoundEvent> MARTIAN_DRONE_HURT = register("martian_drone_hurt"); // 火星无人机：受伤
    public static final RegistryObject<SoundEvent> MARTIAN_WALKER_FREE = register("martian_walker_free"); // 火星行者：鸣叫（4 个变体）

    public static final RegistryObject<SoundEvent> MAX_MANA = register("max_mana"); // 魔力：回满

    public static final RegistryObject<SoundEvent> MECH = register("mech"); // 机关：触发

    public static final RegistryObject<SoundEvent> MENU_CLOSE = register("menu_close"); // 菜单：关闭界面
    public static final RegistryObject<SoundEvent> MENU_OPEN = register("menu_open"); // 菜单：打开界面
    public static final RegistryObject<SoundEvent> MENU_TICK = register("menu_tick"); // 菜单：界面悬停

    public static final RegistryObject<SoundEvent> MISTER_STABBY_DEATH = register("mister_stabby_death"); // 刀刺先生：死亡
    public static final RegistryObject<SoundEvent> MISTER_STABBY_HURT = register("mister_stabby_hurt"); // 刀刺先生：受伤

    public static final RegistryObject<SoundEvent> MOON_LORD_DEATH = register("moon_lord_death"); // 月亮领主：死亡（2 个变体）
    public static final RegistryObject<SoundEvent> MOON_LORD_FREE = register("moon_lord_free"); // 月亮领主：鸣叫（7 个变体）
    public static final RegistryObject<SoundEvent> MOON_LORD_HURT = register("moon_lord_hurt"); // 月亮领主：受伤
    public static final RegistryObject<SoundEvent> MOON_LORD_SUMMONED_FREE = register("moon_lord_summoned_free"); // 月亮领主：鸣叫

    public static final RegistryObject<SoundEvent> MOTHRON_DEATH = register("mothron_death"); // 蛾怪：死亡
    public static final RegistryObject<SoundEvent> MOTHRON_FREE = register("mothron_free"); // 蛾怪：鸣叫
    public static final RegistryObject<SoundEvent> MOTHRON_HURT = register("mothron_hurt"); // 蛾怪：受伤

    public static final RegistryObject<SoundEvent> MUMMY_FREE = register("mummy_free"); // 木乃伊：鸣叫（2 个变体）

    public static final RegistryObject<SoundEvent> MUSHI_LADYBUG_DEATH = register("mushi_ladybug_death"); // 虫瓢虫：死亡
    public static final RegistryObject<SoundEvent> MUSHI_LADYBUG_FREE = register("mushi_ladybug_free"); // 虫瓢虫：鸣叫（4 个变体）
    public static final RegistryObject<SoundEvent> MUSHI_LADYBUG_HURT = register("mushi_ladybug_hurt"); // 虫瓢虫：受伤

    public static final RegistryObject<SoundEvent> NPC_HURT_UNUSED = register("npc_hurt_unused"); // 怪物：音效

    public static final RegistryObject<SoundEvent> OWL_FREE = register("owl_free"); // 猫头鹰：鸣叫（5 个变体）

    public static final RegistryObject<SoundEvent> PARROT_DEATH = register("parrot_death"); // 鹦鹉：死亡
    public static final RegistryObject<SoundEvent> PARROT_FREE = register("parrot_free"); // 鹦鹉：鸣叫
    public static final RegistryObject<SoundEvent> PARROT_HURT = register("parrot_hurt"); // 鹦鹉：受伤

    public static final RegistryObject<SoundEvent> PHANTASM_DRAGON_DEATH = register("phantasm_dragon_death"); // 幻影龙：死亡
    public static final RegistryObject<SoundEvent> PHANTASM_DRAGON_HURT = register("phantasm_dragon_hurt"); // 幻影龙：受伤

    public static final RegistryObject<SoundEvent> PHANTASMAL_DEATHRAY_FREE = register("phantasmal_deathray_free"); // 幻影死亡射线：鸣叫
    public static final RegistryObject<SoundEvent> PHANTASMAL_EYE_FREE = register("phantasmal_eye_free"); // 幻影眼：鸣叫
    public static final RegistryObject<SoundEvent> PHANTASMAL_SPHERE_FREE = register("phantasmal_sphere_free"); // 幻影球：鸣叫

    public static final RegistryObject<SoundEvent> PIGRON_DEATH = register("pigron_death"); // 猪龙：死亡
    public static final RegistryObject<SoundEvent> PIGRON_FREE = register("pigron_free"); // 猪龙：鸣叫（4 个变体）
    public static final RegistryObject<SoundEvent> PIGRON_HURT = register("pigron_hurt"); // 猪龙：受伤

    public static final RegistryObject<SoundEvent> PLAYER_DEATH = register("player_death"); // 玩家：死亡
    public static final RegistryObject<SoundEvent> PLAYER_HIT = register("player_hit"); // 男玩家：受伤（3 个变体）

    public static final RegistryObject<SoundEvent> PSYCHO_DEATH = register("psycho_death"); // 精神病患者：死亡
    public static final RegistryObject<SoundEvent> PSYCHO_HURT = register("psycho_hurt"); // 精神病患者：受伤

    public static final RegistryObject<SoundEvent> QUEEN_BEE_DEATH = register("queen_bee_death"); // 蜂王：死亡
    public static final RegistryObject<SoundEvent> QUEEN_BEE_FREE = register("queen_bee_free"); // 蜂王：鸣叫
    public static final RegistryObject<SoundEvent> QUEEN_SLIME_DEATH = register("queen_slime_death"); // 史莱姆皇后：死亡
    public static final RegistryObject<SoundEvent> QUEEN_SLIME_FREE = register("queen_slime_free"); // 史莱姆皇后：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> REAPER_DEATH = register("reaper_death"); // 死神：死亡
    public static final RegistryObject<SoundEvent> REAPER_HURT = register("reaper_hurt"); // 死神：受伤

    public static final RegistryObject<SoundEvent> RESEARCH = register("research"); // 研究：音效（3 个变体）
    public static final RegistryObject<SoundEvent> RESEARCH_COMPLETE = register("research_complete"); // 研究：完成

    public static final RegistryObject<SoundEvent> ROAR_2 = register("roar_2"); // BOSS：备用吼叫

    public static final RegistryObject<SoundEvent> RUN = register("run"); // 奔跑：奔跑鞋

    public static final RegistryObject<SoundEvent> SALAMANDER_DEATH = register("salamander_death"); // 蝾螈：死亡
    public static final RegistryObject<SoundEvent> SALAMANDER_FREE = register("salamander_free"); // 蝾螈：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> SALAMANDER_HURT = register("salamander_hurt"); // 蝾螈：受伤

    public static final RegistryObject<SoundEvent> SCUTLIX_DEATH = register("scutlix_death"); // 鳞甲怪：死亡
    public static final RegistryObject<SoundEvent> SCUTLIX_FREE = register("scutlix_free"); // 鳞甲怪：鸣叫
    public static final RegistryObject<SoundEvent> SCUTLIX_HURT = register("scutlix_hurt"); // 鳞甲怪：受伤

    public static final RegistryObject<SoundEvent> SEAGULL_FREE = register("seagull_free"); // 海鸥：鸣叫（3 个变体）

    public static final RegistryObject<SoundEvent> SHADOWFLAME_APPARITION_DEATH = register("shadowflame_apparition_death"); // 暗影焰幻影：死亡
    public static final RegistryObject<SoundEvent> SHADOWFLAME_APPARITION_HURT = register("shadowflame_apparition_hurt"); // 暗影焰幻影：受伤

    public static final RegistryObject<SoundEvent> SHATTER = register("shatter"); // 玻璃：玻璃方块被击中

    public static final RegistryObject<SoundEvent> SPLASH = register("splash"); // 水花：接触/离开液体（6 个变体）

    public static final RegistryObject<SoundEvent> TARGET_DUMMY_HURT = register("target_dummy_hurt"); // 训练假人：受伤（3 个变体）

    public static final RegistryObject<SoundEvent> TESLA_TURRET_DEATH = register("tesla_turret_death"); // 特斯拉炮塔：死亡
    public static final RegistryObject<SoundEvent> TESLA_TURRET_HURT = register("tesla_turret_hurt"); // 特斯拉炮塔：受伤

    public static final RegistryObject<SoundEvent> THE_POSSESSED_DEATH = register("the_possessed_death"); // 着魔之人：死亡
    public static final RegistryObject<SoundEvent> THE_POSSESSED_FREE = register("the_possessed_free"); // 着魔之人：鸣叫（2 个变体）
    public static final RegistryObject<SoundEvent> THE_POSSESSED_HURT = register("the_possessed_hurt"); // 着魔之人：受伤

    public static final RegistryObject<SoundEvent> THUNDER = register("thunder"); // 雷雨：雷鸣（7 个变体）

    public static final RegistryObject<SoundEvent> TINK = register("tink"); // 石质方块：石质方块被击中（3 个变体）

    public static final RegistryObject<SoundEvent> TOUCAN_FREE = register("toucan_free"); // 巨嘴鸟：鸣叫（2 个变体）

    public static final RegistryObject<SoundEvent> TRUE_EYE_OF_CTHULHU_FREE = register("true_eye_of_cthulhu_free"); // 克苏鲁真眼：鸣叫（2 个变体）

    public static final RegistryObject<SoundEvent> UNLOCK = register("unlock"); // 宝箱：开锁

    public static final RegistryObject<SoundEvent> VAMPIRE_FREE = register("vampire_free"); // 吸血鬼：鸣叫

    public static final RegistryObject<SoundEvent> VILE_SPIT_DEATH = register("vile_spit_death"); // 邪恶唾液：死亡

    public static final RegistryObject<SoundEvent> VULTURE_DEATH = register("vulture_death"); // 秃鹫：死亡
    public static final RegistryObject<SoundEvent> VULTURE_HURT = register("vulture_hurt"); // 秃鹫：受伤

    public static final RegistryObject<SoundEvent> WATERFALL = register("waterfall"); // 瀑布：环境音

    public static final RegistryObject<SoundEvent> WEREWOLF_HURT = register("werewolf_hurt"); // 狼人：受伤

    public static final RegistryObject<SoundEvent> WINDY_BALLOON_DEATH = register("windy_balloon_death"); // 风气球：死亡

    public static final RegistryObject<SoundEvent> WRAITH_DEATH = register("wraith_death"); // 幽灵：死亡
    public static final RegistryObject<SoundEvent> WRAITH_FREE = register("wraith_free"); // 幽灵：鸣叫（3 个变体）
    public static final RegistryObject<SoundEvent> WRAITH_HURT = register("wraith_hurt"); // 幽灵：受伤

    public static final RegistryObject<SoundEvent> ZOMBIE5_FREE = register("zombie5_free"); // 僵尸：鸣叫

    public static final RegistryObject<SoundEvent> ZOMBIE87_FREE = register("zombie87_free"); // 僵尸：鸣叫

    private static RegistryObject<SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new ForgeSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
