# 泰拉瑞亚生物复刻计划表

> 状态: 草案 | 日期: 2026-06-27 | 目标平台: Minecraft Forge 1.20.1
>
> 架构决策: **完全抛弃 TerraEntity 模块，在 ConfluenceOtherworld 中全量重写。**

---

## 一、模块职责

```
Confluence-Magic-Lib/          # 共享库 —— 属性、伤害类型、Boss 标记接口
├── api/entity/
│   ├── Boss                  # Boss 标记接口（已存在）
│   └── IDiscardWhenRespawnEntity
├── common/
│   ├── LibAttributes          # 自定义属性: crit_chance, ranged_damage, dodge_chance, summon_damage 等
│   └── LibDamageTypes
└── (网络、粒子、颜色工具等)

PortLib/                       # Forge/NeoForge 抽象层
├── PortAttachmentType         # 数据附着
├── PortNetworkHandler         # 网络通道
├── PortCustomRegistration     # 自定义注册系统
├── IPortItemExtension         # 接口注入
└── (事件封装、Mixin 等)

ConfluenceOtherworld/          # 主模组 —— 一切内容
├── common/
│   ├── entity/                # 【新】生物实现（全部在此重写）
│   │   ├── boss/              #   Boss 实体
│   │   ├── monster/           #   怪物实体
│   │   ├── summon/            #   召唤物实体
│   │   ├── npc/               #   NPC 实体
│   │   ├── animal/            #   动物实体
│   │   └── projectile/        #   弹射物实体（合并原 TerraEntity 弹射物）
│   ├── entity/ai/             # 【新】AI 系统
│   │   ├── fsm/               #   FSM 状态机
│   │   ├── goal/              #   通用 AI Goal
│   │   └── skill/             #   Boss 技能组件
│   ├── entity/api/            # 【新】生物 API 接口（实体间契约）
│   ├── init/entity/           # 【扩】实体注册（集中管理）
│   ├── attachment/            # Player attachment（召唤栏位等）
│   ├── network/               # 网络包（已有 c2s/ s2c/）
│   └── ...
├── client/
│   └── entity/                # 【新】生物渲染
│       ├── renderer/          #   渲染器
│       ├── model/             #   模型
│       └── animation/         #   动画系统
└── data/
    └── entity_definition/     # 【新】数据驱动生物定义（JSON/datapack）
```

**TerraEntity、TerraCurio、TerraFurniture 均不参与新架构，仅作旧代码参考。**

---

## 二、API 接口设计

全部定义在 `ConfluenceOtherworld/common/entity/api/` 下。

### 2.1 核心接口

```java
// === Boss 生物 ===
// 继承自 Confluence-Magic-Lib 的 Boss 标记
public interface ConfluenceBoss extends Boss {
    BossPhase getCurrentPhase();
    SkillManager<?> getSkillManager();
    BossBarProperties getBossBar();     // 颜色、分段、动画配置
    default boolean isExpert() { return false; }
    default boolean isMaster() { return false; }
}

// === Boss 部件（多部件 Boss 的子实体） ===
public interface BossPart extends Boss.BossPart {
    ConfluenceBoss getMaster();
    void syncFromMaster();
}

// === 召唤物 ===
public interface SummonCreature extends OwnableEntity {
    SummonType getSummonType();         // MINION / SENTRY / PET
    int getSlotCost();
    default float getSummonDamage() { return (float) getAsMob().getAttributeValue(LibAttributes.getSummonDamage()); }
    private Mob getAsMob() { return (Mob) this; }
}

// === 蠕虫分段 ===
public interface WormSegment {
    int getSegmentIndex();
    WormSegment getPrev();
    WormSegment getNext();
    void updateSegmentPosition();
}

// === 碰撞伤害（触碰到即造成伤害） ===
public interface CollisionAttack {
    AABB getCollisionBounds();
    boolean canCollisionHurt(Entity target);
    float getCollisionDamage();
    int getCollisionCooldownTicks();
}
```

### 2.2 变体系统

不定义自己的变体接口。直接使用 Minecraft 内置的 `VariantHolder<T>`:

```java
// vanilla: net.minecraft.world.entity.VariantHolder<T>
// - getVariant() / setVariant(T)
// - 变体数据通过 EntityDataAccessor<int> (ordinal) 同步
// - 纹理路径由 Variant 枚举自身的 getTexture() 提供，Render 直接调用

// 示例：内部枚举 Variant 实现 StringRepresentable，携带纹理路径
public class Bunny extends Animal implements VariantHolder<Bunny.Variant> {

    public enum Variant implements StringRepresentable {
        NORMAL, JEWEL, EXPLOSIVE;

        @Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

        public ResourceLocation getTexture() {
            return switch (this) {
                case JEWEL -> tex("golden_bunny");
                case EXPLOSIVE -> tex("explosive_bunny");
                default -> tex("bunny");
            };
        }
    }
}
```

### 2.3 AI 技能接口

```java
// === 技能（Boss / FSM 怪物的单个行为单元） ===
public interface MobSkill<T extends Mob> {
    String id();
    int durationTicks();
    int cooldownTicks();
    boolean canExecute(T mob);
    void onStart(T mob);
    void onTick(T mob, int elapsedTicks);
    void onEnd(T mob);
    default int priority() { return 0; }
}

// === Boss 阶段 ===
public interface BossPhase {
    String name();
    float healthThreshold();            // 触发血量百分比 [0.0, 1.0]
    List<MobSkill<?>> skills();
    default AttributeModifier[] phaseModifiers() { return new AttributeModifier[0]; }
}
```

---

## 三、实体继承体系

全部写在 `ConfluenceOtherworld/common/entity/` 下。

```
Mob (vanilla)
├── Monster (vanilla)
│   ├── BaseBoss                     ← Boss 基类
│   │   ├── KingSlime
│   │   ├── EyeOfCthulhu
│   │   ├── EaterOfWorlds            implements WormSegment (root)
│   │   ├── BrainOfCthulhu
│   │   ├── Deerclops
│   │   ├── QueenBee
│   │   ├── Skeletron                (主控体，管理 Hands)
│   │   ├── WallOfFlesh              (主控体，管理 Mouth/Eyes)
│   │   ├── TheTwins
│   │   ├── SkeletronPrime           (主控体，管理四臂)
│   │   ├── TheDestroyer             implements WormSegment (root)
│   │   ├── Plantera                  (主控体，管理 Hooks/Tentacles)
│   │   ├── DungeonGuardian
│   │   └── LunaticCultist
│   │
│   ├── BaseMonster                  ← 普通怪物基类
│   │   ├── BaseSlime                ← 史莱姆族基类
│   │   │   ├── GreenSlime, BlueSlime, RedSlime
│   │   │   ├── PurpleSlime, YellowSlime, BlackSlime
│   │   │   ├── MotherSlime, BabySlime
│   │   │   ├── JungleSlime, IceSlime, SandSlime, LavaSlime
│   │   │   ├── SpikedSlime
│   │   │   └── QueenSlime (extends BaseBoss instead)
│   │   │
│   │   ├── BaseFlyingMonster        ← 飞行怪物基类
│   │   │   ├── DemonEye 变种 (CataractEye, DialatedEye, GreenEye, PurpleEye, WanderingEye)
│   │   │   ├── Harpy
│   │   │   ├── CaveBat / JungleBat / HellBat / IceBat / GiantBat
│   │   │   ├── Pixie, Wraith
│   │   │   ├── EaterOfSouls, Crimera
│   │   │   └── CursedSkull
│   │   │
│   │   ├── BaseWormMonster          ← 蠕虫怪物基类 implements WormSegment
│   │   │   ├── Wyvern, ArchWyvern
│   │   │   ├── Devourer, WorldFeeder
│   │   │   └── BoneSerpent
│   │   │
│   │   ├── BaseCasterMonster        ← 法术怪物基类
│   │   │   ├── DarkCaster
│   │   │   ├── FireImp, Demon, VoodooDemon
│   │   │   ├── Tim
│   │   │   ├── Necromancer, RaggedCaster, Diabolist
│   │   │   └── GoblinSorcerer
│   │   │
│   │   └── HumanoidMonster          ← 人形怪物
│   │       ├── Zombie 变种 (普通/Armed/Slimed/Pincushion/Twiggy/Swamp/Raincoat/Blood/Eskimo/Bald)
│   │       ├── Skeleton 变种 (普通/Short/Big/Hoplite/Armored/AngryBones/BoneThrower)
│   │       ├── FaceMonster
│   │       └── Paladin, BoneLee
│   │
│   └── PlantMonster                  ← 植物型怪物
│       ├── ManEater, Snatcher
│       └── PlanteraTentacle (BossPart)
│
├── TamableAnimal (vanilla)
│   └── BaseSummon                    ← 召唤物基类 implements SummonCreature
│       ├── MinionSummon              ← 随从（跟随+攻击）
│       │   ├── SlimeMinion, HornetMinion, ImpMinion
│       │   ├── SwordMinion (多阶)
│       │   ├── Terraprisma, StardustDragon
│       │   └── FinchMinion
│       ├── SentrySummon              ← 哨兵（定点驻守）
│       └── PetSummon                 ← 宠物（纯装饰/照明）
│           ├── Chester, PiggyBank
│           └── FairyPet, GlowingSnail
│
├── PathfinderMob (vanilla)
│   └── BaseNPC                       ← NPC 基类（Brain 驱动，非 BT）
│       ├── SimpleNPC                 ← 默认实现，15 个 NPC
│       ├── AnglerNPC                 ← 渔夫（任务周期、水中漂浮）
│       └── TravelingMerchantNPC      ← 旅商（定时消失、随机商品）
│
└── Animal (vanilla)
    └── BaseCritter                    ← 小动物基类
        ├── Bunny (普通/Jewel/Explosive)  implements VariantHolder<XXAnimal.Variant>
        ├── Bird (Blue/Cardinal/Gold)     implements VariantHolder<XXAnimal.Variant>
        ├── Squirrel / JewelSquirrel
        ├── Duck, Crab, Scorpion, Worm
        ├── Butterfly 变种               implements VariantHolder<XXAnimal.Variant>
        ├── Dragonfly, Grasshopper
        └── Fairy (ambient)
```

---

## 四、AI 系统设计

### 4.1 双 AI 架构：BehaviorTree（怪物） + Brain（NPC）

**怪物/Boss** 统一使用 **BehaviorTree** 作为 AI 框架：

- **简单生物** → 1-3 个节点的极简 BT
- **中等怪物** → 带条件分支的 BT
- **Boss** → 多阶段 Selector 驱动的大型 BT

**NPC** 保留 Minecraft 原版 **Brain 系统**（Schedule / Memory / Sensor）：

- Schedule 原生支持"白天工作→夜晚回家"的时间驱动切换
- Memory 处理状态共享（HOME、NEAREST_ENEMY），无需额外 Blackboard
- Sensor 被动感知环境，非每 tick 扫描

### 4.2 BehaviorTree 架构

#### 4.2.1 节点类型

```
BTNode (抽象基类)
├── CompositeNode (组合节点 —— 控制子节点执行顺序)
│   ├── SelectorNode      # OR：依次尝试，任一成功则成功（优先级选择）
│   ├── SequenceNode      # AND：依次执行，任一失败则失败
│   ├── ParallelNode      # 并行执行所有子节点
│   │   ├── Policy.REQUIRE_ONE   # 任一完成即结束
│   │   └── Policy.REQUIRE_ALL   # 全部完成才结束
│   └── WeightNode        # 加权随机选择子节点
│
├── DecorationNode (装饰节点 —— 修改单个子节点的行为)
│   ├── ConditionNode     # if(condition) → then, else
│   ├── InterruptNode     # 条件满足时中断当前子树
│   ├── InverterNode      # 取反
│   ├── RepeaterNode      # 循环 N 次
│   ├── RepeatUntilNode   # 循环直到条件成立
│   └── TimeControlNode   # 限制子节点执行时长
│
├── Condition (条件节点 —— 检查状态，节点本身无状态)
│   ├── TargetExistCondition      # 有攻击目标
│   ├── DistanceLowerThanCondition # 目标距离 < X
│   ├── HealthLowerThanCondition  # 血量 < X%
│   ├── AngleLowerThanCondition   # 夹角 < X 度
│   ├── TimeCondition             # 时间周期
│   ├── NavigationCondition       # 导航状态
│   ├── AndCondition / OrCondition / NotCondition  # 布尔组合
│   └── (自定义条件可按需扩展)
│
└── Leaf (叶子节点 —— 执行具体行为)
    ├── MoveToTargetAction     # 向目标移动
    ├── FlyTowardTargetAction  # 飞向目标
    ├── DashAction             # 冲刺
    ├── JumpForwardAction      # 向前跳跃
    ├── LandRandomStrollAction # 地面漫步
    ├── ShootAction            # 发射弹射物
    ├── JumpAttackAction       # 跳跃攻击
    ├── TrackTargetAction      # 跟踪瞄准目标
    ├── WaitAction             # 等待 N tick
    ├── AnimCtrlAction         # 控制动画播放
    ├── SyncFlagAction         # 同步标记位（驱动客户端动画/渲染）
    ├── SyncAction             # 同步自定义数据
    ├── SetAttributeAction     # 修改属性
    ├── SetNoPhysicsAction     # 无碰撞/无重力模式
    ├── TeleportAction         # 传送到指定位置
    └── BurrowAction           # 钻入/钻出地面（沙鲨等）
```

#### 4.2.2 BT 构建器 (BTFactory)

```java
// fluent DSL 构建行为树
BTRoot<MyBoss> root = BTFactory.selector()
    // Phase 1: 有目标时战斗
    .addWithCondition(new TargetExistCondition(mob), BTFactory.sequence()
        .addChild(new RoarAnim(mob))        // 咆哮动画
        .addChild(BTFactory.selector()
            // 近战分支：距离 < 4
            .addWithCondition(new DistanceLowerThanCondition(mob, 4),
                BTFactory.withTimer(15, new MeleeAttack()))
            // 远程分支：否则
            .addChild(BTFactory.withTimer(10, new RangedAttack())))
        .addChild(new MoveToTargetAction(mob, 3, 20)))  // 持续靠近目标
    // Phase 2: 无目标时漫游
    .addChild(BTFactory.infinite(
        new LandRandomStrollAction(mob, 1.0f, 50)))
    .build();
```

#### 4.2.3 Blackboard (共享内存)

行为树节点间通过 `Blackboard` 共享状态：

```java
public interface IBlackboardHolder {
    Blackboard getBlackboard();
}

// 用法示例
blackboard.put(KeyType.STAGE, 2);                 // 写入阶段号
blackboard.put(KeyType.LAST_TELEPORT_TIME, tick);  // 写入时间戳
int stage = blackboard.get(KeyType.STAGE);          // 读取
```

#### 4.2.4 BTNode 执行周期

每个 tick 调用 `BTNode.execute()`，返回三种状态：

| BTStatus | 含义 | 行为 |
|----------|------|------|
| `SUCCESS` | 节点成功完成 | 父节点决定下一步（Selector 停止，Sequence 继续） |
| `FAILURE` | 节点失败 | 父节点换下一个子节点或向上传递失败 |
| `RUNNING` | 仍在执行中 | 下个 tick 继续调用同一节点 |

---

### 4.3 BT 与 Forge 系统的衔接

`BTRoot` 实现为 Forge 的 `Goal`，注册到最高优先级（优先级 0），始终活跃：

```java
public abstract class BTRoot<T extends Mob> extends Goal {
    protected T mob;
    protected BTNode rootNode;

    @Override
    public boolean canUse() { return true; }  // BT 永远占用控制权

    @Override
    public void tick() { rootNode.execute(); }
}

// 基类构造函数中注册
this.goalSelector.addGoal(0, this.createBT());
```

目标选择（找谁打）从条件节点驱动，不依赖 vanilla `targetSelector`：

```java
// 条件: 有目标 → 战斗分支; 否则 → 漫游分支
.addWithCondition(new TargetExistCondition(mob), combatSubtree())
.addChild(idleSubtree())
```

这样所有决策都在一棵树内完成。

---

### 4.4 Boss 阶段管理

通过 BehaviorTree 的条件节点实现阶段切换，不需要单独的 PhaseManager：

```java
BTFactory.selector()
    // Expert 专属阶段
    .addWithCondition(AndCondition.of(
        new HealthLowerThanCondition(mob, 0.05f),
        new ExpertCondition(mob)), expertSkills())
    // Phase 4: 25% → 0%，狂暴
    .addWithCondition(new HealthLowerThanCondition(mob, 0.25f), ragePhaseSkills())
    // Phase 3: 50% → 25%
    .addWithCondition(new HealthLowerThanCondition(mob, 0.5f), phase3Skills())
    // Phase 2: 75% → 50%
    .addWithCondition(new HealthLowerThanCondition(mob, 0.75f), phase2Skills())
    // Phase 1: 100% → 75%，默认
    .addChild(phase1Skills());
```

条件节点从上到下评估，第一个满足条件的分支生效。血量低于阈值后自动"跌落"到对应阶段。

---

### 4.5 极简 BT：最简单的生物也走 BT

每个生物的 `createBT()` 返回其行为树。简单生物只需几行：

```java
// === 史莱姆（最简单） ===
BTFactory.selector()
    .addWithCondition(new HasTargetCondition(mob),   // 有目标/受伤→追
        BTFactory.sequence()
            .addChild(new SlimeJumpGoal(mob, targetDir)) // 跳跃攻击
            .addWithTimer(15, new SyncFlagAction(mob, ATTACK_FLAG)))
    .addChild(BTFactory.sequence()                   // 无目标→漫游
        .addWithTimer(40 + randomTicks(), new SlimeBounceGoal(mob, randomDir)));

// === 蝙蝠 ===
BTFactory.selector()
    .addWithCondition(new HasTargetCondition(mob),
        BTFactory.sequence()
            .addWithTimer(20, new FlutterTowardTarget(mob))
            .addChild(new WaitAction(10)))
    .addChild(new FlutterWanderGoal(mob));

// === 鸟（小动物） ===
BTFactory.selector()
    .addWithCondition(new PlayerCloseCondition(mob, 5),
        new FleeFlyGoal(mob))                       // 玩家靠近→飞走
    .addChild(new IdleStandGoal(mob));               // 否则静止
```

没有 FSM、没有专门为简单怪准备的子类系统——所有生物都用同一套 BT 节点库。

### 4.6 泰拉瑞亚 AI 类型 → BehaviorTree 映射

以下基于泰拉瑞亚 125 种内置 AI 类型，归类为可复用的 BT/FGoal 模式：

#### 4.6.1 可复用 BT 模式（一族怪物共享同一套 BT 模板）

| Terraria AI ID | 名称 | 复刻方式 | 对应 BT/Goal 模板 |
|---------------|------|----------|-------------------|
| 1 | 史莱姆 AI | BT | Selector(HasTarget→JumpAttack / SlimeBounceWander)， 条件: 受伤/夜间→切换追踪 |
| 2 | 恶魔眼 AI | BT | Selector(HasTarget→Sequence(CircleAroundTarget + ChargeAttack) / FlyWander) |
| 3 | 战士 AI | BT | Selector(HasTarget→Sequence(MoveToTarget + MeleeAttack) / RandomStroll) —— 最常见 |
| 5 | 飞行 AI | BT | Selector(HasTarget→Sequence(FlyTowardTarget + CircleAroundTarget) / FlyWander) |
| 6 | 蠕虫 AI | BT | Selector(HasTarget→WormDigChase / WireIdle)，头→体→尾联动，穿透方块 |
| 7 | 被动 AI | BT | Selector(PlayerClose→FleeRandom / RandomStroll) —— NPC/小动物 |
| 8 | 法师 AI | BT | Sequence(ShootAction×3 → TeleportAction → Repeat)，InterruptNode(受伤时中断) |
| 10 | 诅咒骷髅头 AI | BT | Parallel(CircleAroundTarget + ShootAction)，保持距离 |
| 13 | 植物 AI | BT | Selector(AttachedToBlock→PlantReachTowardPlayer / DieWithoutBlock) |
| 14 | 蝙蝠 AI | BT | Selector(HasTarget→Sequence(FlutterTowardTarget + Wait) / FlutterWander) |
| 16 | 游泳 AI | BT | Selector(PlayerInWater→BurstTowardPlayer / SwimWander) |
| 17 | 秃鹰 AI | BT | Selector(Player接近/Hurt→SwitchToFlyingBT / IdleStand) |
| 18 | 水母 AI | BT | Selector(PlayerInWater→BurstTowardPlayer / FloatWander) |
| 22 | 悬停 AI | BT | Selector(HasTarget→Sequence(HoverMoveToTarget + MeleeAttack) / HoverWander) |
| 24 | 鸟 AI | BT | Selector(PlayerClose→FleeFly / IdleStand) |
| 25 | 宝箱怪 AI | BT | Selector(PlayerClose→Sequence(JumpAttack + Slam) / MimicIdle) |
| 26 | 独角兽 AI | BT | Selector(HasTarget→Sequence(AccelerateMove + JumpLeap) / WalkWander) |
| 38 | 雪人 AI | BT | Selector(HasTarget→Sequence(JumpMove + MeleeAttack) / RandomStroll) |
| 39 | 陆龟 AI | BT | Sequence(CrawlApproach → LeapToTarget) |
| 40 | 蜘蛛 AI | BT | Selector(OnWall→WallCrawlTowardTarget / Selector(HasTarget→战士BT / RandomStroll)) |
| 41 | 蹦蹦兽 AI | BT | Selector(HasTarget→Sequence(BounceHigh → LandOnTarget) / BounceWander) |
| 44 | 飞鱼 AI | BT | LinearDashTowardTarget |
| 56 | 地牢幽魂 AI | BT | AccelerateChargeTowardTarget |
| 74 | 流星火怪 AI | BT | PhaseThroughBlocks + ChargeTowardTarget |
| 87 | 生物群落宝箱怪 AI | BT | Selector(PlayerClose→Sequence(Jump+Leap+Slam) / Idle，宝箱怪升级版) |
| 91 | 花岗精 AI | BT | Selector(DistanceFar→PhaseThroughBlocks + FloatTowardTarget / FloatTowardTarget) |
| 103 | 沙鲨 AI | BT | Selector(PlayerClose→BurrowOut + LeapAttack / StayBurrowed) |
| 122 | 海盗诅咒 AI | BT | FloatThroughBlocks + ChaseTarget |

#### 4.6.2 Boss 专属 BT（每个 Boss 独立实现）

| Terraria AI ID | Boss | 核心 BT 结构 |
|---------------|------|-------------|
| 4 | 克苏鲁之眼 | Sequence(HoverAbovePlayer → Charge → Repeat)，低血量 Parallel(Spin + Charge) |
| 11 + 12 | 骷髅王 | Head: Parallel(CirclePlayer + FloatAbove)，Hands: SyncWithHead + SwipeAttack |
| 15 | 史莱姆王 | Selector(JumpToPlayer → TeleportWhenStuck)，条件: 受伤→SpawnBlueSlime |
| 27 + 28 + 29 | 血肉墙 | 主体: LinearSweep(水平推进)，眼睛: Parallel(Float + ShootAtPlayer)，饿鬼: AttachToParent + ReachTowardPlayer |
| 30 + 31 | 双子魔眼 | 激光眼: Selector(HoverShoot / ChargeShoot)，魔焰眼: Selector(HoverShoot / FlamethrowerCharge) |
| 32-36 | 机械骷髅王 | 头部: Head AI，四臂各独立 BT (SawDash / ViceGrab / CannonBomb / LaserShoot)，暴怒模式切换 |
| 37 | 毁灭者 | WormDig + Parallel(ShootFromBody + DeployProbes) |
| 43 | 蜂王 | Selector(HoverAbovePlayer+Shoot / HorizontalDash / VerticalDash) |
| 45-48 | 石巨人 | 身体: Selector(Jump + LaserEye)，拳头: FlyToPlayer+ReturnToBody，游离头: FloatShoot |
| 51-53 | 世纪之花 | Phase1: ChaseThroughBlocks + ShootPetal，Phase2: AggressiveChase + SpawnTentacles |
| 54 + 55 | 克苏鲁之脑 | Phase1: Teleport+SpawnCreepers，Phase2: Reveal+Charge |
| 69 | 猪龙鱼公爵 | 多阶段冲撞+召唤龙卷+召唤鲨鱼龙 |
| 76 | 火星飞碟 | Parallel(CirclePlayer + DeathRay + TurretShoot) |
| 77-79 + 81 + 82 | 月亮领主 | 头/手/心脏各独立 BT，心脏无敌直到手被击败，真眼环绕射击 |
| 84 | 拜月教邪教徒 | Selector(Teleport+ShootMultiple / SummonPhantomDragon / SummonAncientLight) |
| 120 | 光之女皇 | Selector(HoverShootMultiAttack / Charge) 快速循环 |
| 121 | 史莱姆皇后 | Selector(Jump+Slam / Hover+ShootGel / SpawnMinions) |
| 123 | 独眼巨鹿 | BT(接近+近战/远程冰刺/冲撞选择，破坏箱子行为) |

#### 4.6.3 小动物/环境生物（全 BT）

| Terraria AI ID | 类型 | BT 结构 |
|---------------|------|---------|
| 64 | 萤火虫 | Sequence(RandomFloat + GlowToggle) |
| 65 | 蝴蝶 | RandomFlyWander |
| 66 | 被动蠕虫 | PassiveCrawlWander |
| 67 | 蜗牛 | Selector(OnWall→WallCrawl / GroundCrawl) |
| 68 | 鸭 | Selector(PlayerClose→FleeFly / InWater→SwimWander / LandWander) |
| 112 | 仙灵 | Selector(TreasureNearby→FloatTowardTreasure / FollowPlayer) |
| 114 | 蜻蜓 | Sequence(WaypointFly → Wait → WaypointFly) |
| 115 | 瓢虫 | Selector(Flying→FlyWander / LandCrawlWander) |
| 116 | 水黾 | Selector(OnWater→WaterSlide / LandJumpWander) |

---

## 五、渲染架构

### 5.1 技术栈

- **GeckoLib** — 模型加载 + 骨骼动画
- **Minecraft VariantHolder<XX.Variant>** — 生物变体（内部枚举 + getTexture()）
- **自定义 BossBar 渲染** — 纹理/颜色/分段动画
- **粒子系统** — 复用 ConfluenceOtherworld 已有的 ParticleStorm 集成

### 5.2 渲染器基类

```java
// 通用生物渲染器
public class GeoNormalRenderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
    float scale;
    float offsetY;
    float rotX;         // 飞行生物前倾
    boolean motionAnim; // 是否启用移动动画
}

// Boss 专用渲染器
public class GeoBossRenderer<T extends BaseBoss & GeoEntity> extends GeoNormalRenderer<T> {
    // Boss 特殊效果：发光、半透明、残影、阶段切换着色
}
```

### 5.3 变体纹理映射

```java
// 纹理路径由 Variant 枚举自提供，Renderer 直接委托
public class BunnyRenderer extends GeoNormalRenderer<Bunny> {

    @Override
    public ResourceLocation getTextureLocation(Bunny bunny) {
        return bunny.getVariant().getTexture();
    }
}
```

---

## 六、实体注册架构

### 6.1 集中注册

```java
// ConfluenceOtherworld/common/init/entity/ModEntities.java
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(Registries.ENTITY_TYPE, Confluence.MODID);

    // 按类别分文件注册，避免单文件过大
    public static void register(IEventBus bus) {
        BossEntities.register(ENTITIES);
        MonsterEntities.register(ENTITIES);
        SummonEntities.register(ENTITIES);
        NPCEntities.register(ENTITIES);
        CritterEntities.register(ENTITIES);
        ProjectileEntities.register(ENTITIES);
        ENTITIES.register(bus);
    }
}
```

### 6.2 每个类别独立文件

```
init/entity/
├── ModEntities.java          # 总入口
├── BossEntities.java         # Boss 注册 + 属性 + 渲染器
├── MonsterEntities.java      # 怪物注册
├── SummonEntities.java       # 召唤物注册
├── NPCEntities.java          # NPC 注册
├── CritterEntities.java      # 动物/小动物注册
└── ProjectileEntities.java   # 弹射物注册
```

---

## 七、网络同步

使用已有的 `PortNetworkHandler`，在 `ConfluenceOtherworld/common/network/` 下新增：

| 包名 | 方向 | 用途 |
|------|------|------|
| BossPhaseChangePacket | S→C | Boss 阶段切换 + 特效指令 |
| SkillSyncPacket | S→C | 技能开始/结束 + 参数（弹射物朝向/速度） |
| SummonSlotSyncPacket | S→C | 玩家召唤栏位状态 |
| BossBarCustomPacket | S→C | 自定义 BossBar 渲染数据 |
| NPCTradeSyncPacket | S→C | NPC 商品列表 |
| NPCChatPacket | S→C | NPC 对话气泡 |

---

## 八、玩家附着数据 (Attachments)

使用 PortLib 的 `PortAttachmentType`:

| Attachment | 存储内容 |
|------------|----------|
| SUMMONER_SLOTS | 当前召唤物 ID 列表 + 已用栏位 + 最大栏位 |
| SENTRY_SLOTS | 哨兵 ID 列表 |
| BOSS_COOLDOWNS | 玩家级 Boss 召唤冷却 |
| NPC_HAPPINESS | 对各 NPC 的好感度/价格系数 |
| BESTIARY | 已击杀生物记录 |

---

## 九、复刻优先级

### P0 — 架构与基类（2-3 周）

- [ ] BehaviorTree 引擎：BTNode / 组合节点 (Selector, Sequence, Parallel) / 装饰节点 / 条件节点 / 叶子节点 / Blackboard
- [ ] BTFactory DSL：流畅的树构建器
- [ ] BaseBoss：基于 BT 的 Boss 基类（碰撞攻击、多部件支持、BossBar、阶段条件）
- [ ] BaseMonster + 子类 BaseSlime / BaseFlyingMonster / BaseWormMonster / BaseCasterMonster（均基于 BT）
- [ ] BaseSummon：主人跟随、传送、目标选择、召唤伤害计算
- [ ] BaseNPC：房屋检测、对话、交易、心情
- [ ] BaseCritter：小动物基类
- [ ] 完整注册流程 + 第一只测试生物（GreenSlime）跑通全链路

### P1 — 核心怪物（3-4 周）

- [ ] 全部史莱姆变体 (15 种)
- [ ] 全部恶魔眼变体 (9 种，含 Wandering Eye 的昼夜机制)
- [ ] 基础僵尸变体 (10 种)
- [ ] 基础骷髅变体 (8 种)

### P2 — Boss 实现（4-6 周）

- [ ] KingSlime（传送 + 分裂）
- [ ] EyeOfCthulhu（冲撞 + 旋转 + Servant 召唤）
- [ ] EaterOfWorlds（多段蠕虫 + 分裂）
- [ ] BrainOfCthulhu（分身 + Creeper 环绕）
- [ ] QueenBee（冲撞 + 毒刺 + 小蜜蜂召唤）
- [ ] Skeletron（双手 + 头部旋转）
- [ ] WallOfFlesh（横向推进 + Hungry + 激光）
- [ ] TheTwins（双 Boss 独立 AI）
- [ ] SkeletronPrime（四臂）
- [ ] TheDestroyer（长蠕虫 + Probe 无人机）
- [ ] Plantera（双阶段 + 触手 + 钩爪）
- [ ] DungeonGuardian（一击必杀）
- [ ] LunaticCultist（分身 + 幻影龙）

### P3 — 生态群落怪物（3-4 周）

- [ ] 丛林: Hornet, ManEater, GiantTortoise, Derpling, GiantFlyingFox
- [ ] 地狱: FireImp, Demon/VoodooDemon, BoneSerpent
- [ ] 腐化/血腥: Corruptor, Devourer, BloodCrawler, FaceMonster, BloodFeeder, Slimer
- [ ] 神圣: Unicorn, Gastropod, LightMummy, ChaosElemental, EnchantedSword
- [ ] 地牢: BlazingWheel, SpikeBall, Paladin, BoneLee, Necromancer, Diabolist, RaggedCaster
- [ ] 飞行族: 全部蝙蝠变体, IceBat, GiantBat, ArchWyvern

### P4 — NPC 与深度系统（2-3 周）

- [ ] 全部 16 个 NPC + 房屋系统 + 心情/价格系数
- [ ] 召唤物体系：StardustDragon, Terraprisma, 多阶剑等
- [ ] 专家/大师模式适配（Boss 额外阶段 + 额外掉落）
- [ ] 数据驱动生物定义（JSON datapack 格式，简单怪物可配置化）

### P5 — 打磨（1-2 周）

- [ ] 粒子特效补全（Boss 登场/死亡/阶段切换特效）
- [ ] 音效注册与播放
- [ ] 性能优化（AI tick 频率控制、LOD 渲染距离）
- [ ] 战利品表 + 掉落物

---

## 十、从旧代码中可复用的部分

虽然完全重写，但以下旧代码的**设计思路和数值**可以直接参考：

| 旧位置 (TerraEntity) | 可复用内容 | 复用方式 |
|----------------------|-----------|----------|
| `entity/boss/` 下各 Boss | 技能顺序、阶段划分逻辑、碰撞箱大小 | 参考代码逻辑，重新实现 |
| `entity/ai/fsm/CircleMobSkills.java` | FSM 循环调度算法 | 参考设计，简化重写 |
| `TEBossEntities.java` | Boss 属性值（攻击/血量/护甲/击退抗性） | 直接复用数值 |
| `ISummonMob.java` | 召唤物跟随距离、传送逻辑、目标选择规则 | 参考逻辑，拆分为多个独立组件 |
| `GeoNormalRenderer.java` | 渲染器参数结构（scale/offsetY/rotX） | 复用参数设计 |
| `init/TEAttachments.java` | Attachment 数据结构 | 复用字段定义 |
| TEBossEntities 的注册数值 (`AttBuilder`) | Boss 的攻击力/生命值/护甲 | 直接复用数值常量 |

---

## 十一、技术选择

| 项目 | 选择 | 理由 |
|------|------|------|
| 骨骼动画 | GeckoLib 4.x | 项目已深度集成，动画资源丰富 |
| 实体渲染 | GeckoLib GeoEntityRenderer | 已验证的渲染管线 |
| 网络通信 | PortNetworkHandler → Forge SimpleChannel | 已有基础设施 |
| 数据附着 | PortAttachmentType | 已有基础设施 |
| 变体系统 | Vanilla VariantHolder\<T\> | Minecraft 原生 API，不需要自己造 |
| AI 框架 | 统一自研 BehaviorTree | 从史莱姆到月亮领主，全部走同一套 BT 系统 |
| 数据驱动 | JSON datapack | 简单怪物可配置化，Boss 保持 Java 实现 |
| 粒子 | ParticleStorm (已集成) | 项目已集成 |

---

## 十二、兔兔复刻经验总结

基于首个生物（兔兔族）的实际复刻过程，总结出以下可复用的模式与注意事项。

### 12.1 变体设计

- 使用 `VariantHolder<XX.Variant>`，Variant 是实体内部枚举，实现 `StringRepresentable`
- **Variant 只承载视觉信息**：`modelPath()` 和 `texturePath()` 两个方法，StringRepresentable 处理序列化
- 禁止在 Variant 里放 `isHostile()`、`isJewel()` 等行为判断——行为归实体子类
- 纹理/模型不同→改 Variant；AI 不同→建子类；生成规则不同→建新 EntityType

```java
public enum Variant implements StringRepresentable {
    NORMAL, ...;
    public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
    public ResourceLocation modelPath() { ... }
    public ResourceLocation texturePath() { ... }
}
```

### 12.2 实体类继承分层

```
BaseCritter (GeoEntity + BT 抽象方法)
├── Bunny      (flee BT)              — 被动变体：NORMAL, GOLD, PARTY, SLIMED, XMAS, EXPLOSIVE
├── JewelBunny (flee BT, 构造时随机宝石) — AMETHYST~DIAMOND
└── HostileBunny (attack BT)          — CORRUPT, VICIOUS
```

- 每个子类重写 `createBT()` 返回自己的行为树
- 属性通过 `static AttributeSupplier.Builder createAttributes()` 定义，在 ModEvents 中注册
- 爆炸等特殊死亡效果在 `die()` 中检查 Variant 处理

### 12.3 BehaviorTree 节点复用

最简小动物只需 4 种叶子节点：

| 节点 | 用途 |
|------|------|
| `PanicFleeAction(mob, speed)` | 远离玩家逃跑 |
| `RandomStrollAction(mob, speed, range)` | 随机漫步 |
| `WaitAction(ticks)` | 原地等待 |
| `MoveToTargetAction(mob, speed, closeEnough)` | 追击目标 |
| `PlayerCloseCondition(mob, range)` | 玩家在范围内？ |

组合模式：`Selector(Sequence(Condition, Action), Sequence(Wait, Stroll))` 覆盖绝大多数小动物需求。

### 12.4 多模型渲染

变体对应不同 geo 模型时，**不**在 Renderer 里直接改 `this.model`。写一个自定义 GeoModel 委托给变体对应的模型：

```java
public class BunnyGeoModel extends DefaultedEntityGeoModel<Bunny> {
    private final Map<ResourceLocation, GeoNormalModel<Bunny>> cache = new HashMap<>();

    @Override public ResourceLocation getModelResource(Bunny b) { return b.getVariant().modelPath(); }
    @Override public ResourceLocation getTextureResource(Bunny b) { return b.getVariant().texturePath(); }
    @Override public void setCustomAnimations(Bunny b, long id, AnimationState<Bunny> s) {
        cache.computeIfAbsent(b.getVariant().modelPath(), ...).setCustomAnimations(b, id, s);
    }
}
```

`GeoModel.getBakedModel(ResourceLocation)` 自动按路径缓存，不同变体自然隔离。Renderer 保持简洁。

### 12.5 注册模式

- EntityType 在 `CritterEntities.java` 中内联一行注册（`PortDeferredRegisterExtension.register`），不提取 register helper
- 属性/生成规则/渲染器直接在 `ModEvents` / `ModClientEvents` 中内联，**不在 CritterEntities 中封装事件注册方法**

```java
// CritterEntities — 只放 EntityType
public static final RegistryObject<EntityType<Bunny>> BUNNY = PortDeferredRegisterExtension.register(
    ENTITIES, "bunny", id -> EntityType.Builder.of(Bunny::new, CREATURE).sized(0.4F, 0.5F).clientTrackingRange(10).build(id.toString()));

// ModEvents — 内联属性
event.put(CritterEntities.BUNNY.get(), Bunny.createAttributes().build());

// ModClientEvents — 内联渲染器
event.registerEntityRenderer(CritterEntities.BUNNY.get(), BunnyRenderer::new);
```

### 12.6 1.20.1 API 陷阱

| 错误写法 | 正确写法 | 原因 |
|----------|---------|------|
| `defineSynchedData(Builder)` | `defineSynchedData()` 无参 | 1.20.1 无 Builder 参数 |
| `PathType.WATER` | `BlockPathTypes.WATER` | Parchment 映射名，jar 中真实类名 |
| `onAddedToLevel()` | 构造函数中初始化 | 1.20.1 方法名不同，构造时更简洁 |
| `import geckolib.animatable.instance.*` | `import geckolib.core.animatable.instance.*` | GeckoLib 4.8.3 core 子包 |

### 12.7 GeckoLib 4.8.3 包结构速查

```
# 有 core. 前缀
software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
software.bernie.geckolib.core.animation.AnimatableManager
software.bernie.geckolib.core.animation.AnimationController / AnimationState / RawAnimation

# 无 core. 前缀
software.bernie.geckolib.animatable.GeoEntity       ← 注意！不在 core 里
software.bernie.geckolib.cache.object.{BakedGeoModel, GeoBone}
software.bernie.geckolib.constant.{DataTickets, DefaultAnimations}
software.bernie.geckolib.model.{GeoModel, DefaultedEntityGeoModel}
software.bernie.geckolib.model.data.EntityModelData
software.bernie.geckolib.renderer.GeoEntityRenderer
software.bernie.geckolib.util.GeckoLibUtil
```

### 12.8 @NotNull 策略

每个包放 `package-info.java`：
```java
@javax.annotation.ParametersAreNonnullByDefault
@net.minecraft.MethodsReturnNonnullByDefault
package org.confluence.mod.common.entity.animal;
```

方法签名不加 `@NotNull`，仅在确实可返回 null 的地方加 `@javax.annotation.Nullable`。

---

## 十三、NPC 系统设计

> 状态: 草案 | 日期: 2026-06-30
>
> NPC 不沿用怪物组的 BehaviorTree，保留 Minecraft 原版 **Brain 系统**。
> 原因：Schedule（时间表驱动作息）、Memory（状态共享）、Sensor（被动感知）三重机制天然适配社交 NPC，
> BT 需要大量条件节点模拟，代码臃肿且无法表达"白天工作→夜晚回家"这类时间驱动切换。

### 13.1 继承体系

```
PathfinderMob (vanilla)
└── BaseNPC (新)                    ← Brain 驱动 + GeckoLib 人形动画
    ├── SimpleNPC (新)              ← 默认实现，覆盖 15 个 NPC
    ├── AnglerNPC (新)              ← 渔夫：任务周期、水中漂浮
    └── TravelingMerchantNPC (新)   ← 旅商：定时消失、随机商品
```

区别于 `BaseMonster` 分支——NPC 不主动攻击玩家、不燃烧、需要房屋、依赖条件生成。

### 13.2 Brain 架构

```java
public abstract class BaseNPC extends PathfinderMob implements GeoEntity {

    // === Brain ===
    protected Brain<BaseNPC> brain;

    // === Schedule（时间表） ===
    // 0:00~6:00  IDLE   → 在家待机
    // 6:00~18:00 WORK   → 出门散步/交互
    // 18:00~0:00 IDLE   → 回家

    // === Memory（记忆模块） ===
    // HOME           → GlobalPos  家位置
    // NEAREST_ENEMY  → LivingEntity  最近威胁
    // NEAREST_NPC    → LivingEntity  最近友方NPC
    // WALK_TARGET    → WalkTarget  寻路目标

    // === Sensor（传感器） ===
    // NearestLivingEntitySensor  感知敌人
    // NearestNPCSensor           感知其他 NPC
    // HomeBlockSensor            感知房屋位置

    // === 注入点（子类或外部注册时设置） ===
    protected Supplier<ItemStack> weaponSupplier;          // 防御武器（null=不攻击）
    protected BiConsumer<BaseNPC, LivingEntity> onAttack;  // 攻击回调

    @Override
    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(memories, sensors);
    }

    @Override
    protected void customServerAiStep() {
        brain.tick((ServerLevel) level(), this);
    }
}
```

### 13.3 Activity 与 Behavior

| Activity | 优先级 | Behavior | 触发条件 |
|----------|--------|----------|---------|
| PANIC | 1 (最高) | FleeToHome | 血量 < 30% 或附近有敌对生物 |
| IDLE | 2 | GoHome | 天黑 或 离家 > 50 格 |
| WORK | 3 | RandomStrollAroundHome | 白天、心情正常 |
| WORK | 4 | LookAtNearbyPlayer | 空闲时注视附近玩家 |
| WORK | 5 | RangedDefend | weaponSupplier != null 且有敌人在视野内 |

每种特殊 NPC 通过 **lambda 注入**而非新建 AI 子类：

```java
// Nurse：攻击回调用治疗代替
nurse.onAttack = (npc, target) -> {
    HealPotionProjectile.shoot(npc, target);
};

// Demolitionist：投弹回调用手榴弹代替
demo.weaponSupplier = () -> new ItemStack(ModItems.GRENADE);
demo.onAttack = (npc, target) -> {
    BaseGrenadeEntity.throwAt(npc, target);
};
```

### 13.4 房屋系统

#### House 数据结构

```java
public record House(@Nullable UUID uuid, BlockPos min, BlockPos max) {
    public static final House EMPTY = new House(null, BlockPos.ZERO, BlockPos.ZERO);

    public BlockPos center() {
        return new BlockPos(
            (min.getX() + max.getX()) / 2,
            min.getY() + 2,
            (min.getZ() + max.getZ()) / 2
        );
    }

    public boolean contains(BlockPos pos);
    public boolean isValid() { return uuid != null; }
}
```

- `center` 由 min/max 计算得出，不存储（减少序列化数据）
- `isValid()` 替代旧的 `isEmpty()`，语义更清晰

#### 存储（HouseHandler）

```
dimension (Level key)
  └── region (NPCSpawner.Region)
        └── npcUUID → House(min, max)
```

三层嵌套 Map，自带 Region 分桶（天然索引），不再使用旧的 `HouseManager` 全局单例。

#### 房屋验证（HouseValidater）

```
HouseValidater.scan(Level level, BlockPos start)
  → BFS flood fill，遇非空气/非 NPC_HOUSE_CONSTITUTE 方块停止
  → 限制扫描半径 20，体积 60~3000
  → 验证条件：XZ 跨度 ≥ 3，存在光源(light ≥ 10)，存在桌椅（标签）
  → 返回 HouseResult(min, max, 有效/错误原因)
  → 内部缓存（坐标→结果，5 秒 TTL，避免多 NPC 重复扫描）
```

#### 自动查找

NPC 从当前位置向外扩散采样候选点（而非仅从脚下扫描）：

```
每 ~600 tick（30秒）：
  1. 先扫描近处 6 方向（±8 格水平，±3 格垂直）
  2. 每 1200 tick（60秒）补充 8 个随机采样点（半径 32）
  3. 找到第一个合法房间 → 存入 HouseHandler + 设置 Brain HOME memory
  4. 已分配到房屋的 NPC 跳过扫描
```

#### 手动分配

复用 `HouseSelectPacketC2S` 流程，底层 API 替换：

| 操作 | 旧 API | 新 API |
|------|--------|--------|
| 检测 | `IHouseDetector.detect(pos, level)` | `HouseValidater.scan(level, pos)` |
| 查占用 | `HouseManager.getInstance().isInsideHouse(pos)` | `HouseHandler.INSTANCE` 遍历 Region |
| 分配 | `HouseManager.getInstance().tryAddHouse(house)` | `HouseHandler.INSTANCE.setHouse(...)` |
| 删除 | `HouseManager.getInstance().removeHouse(uuid)` | `HouseHandler.INSTANCE.removeHouse(...)` |

#### 走回家 Behavior

```
walkToHome:
  1. 没有 HOME memory → 跳过
  2. 夜晚且离家 > 50 格 → 传送回家
  3. 其他情况且离家 > 5 格 → 寻路回家
```

### 13.5 NPC 清单

| NPC | 类 | 武器 | 特殊行为 |
|-----|-----|------|---------|
| Guide | SimpleNPC | 无 | 基础 |
| Merchant | SimpleNPC | 投掷刀 | - |
| Nurse | SimpleNPC | 无 | onAttack: 向友方投掷治疗药水 |
| Demolitionist | SimpleNPC | 手榴弹 | onAttack: 投掷手榴弹 |
| DyeTrader | SimpleNPC | 无 | - |
| Painter | SimpleNPC | 无 | - |
| Dryad | SimpleNPC | 无 | - |
| ArmsDealer | SimpleNPC | 手枪/弩 | RangedDefend (Flintlock) |
| GoblinTinkerer | SimpleNPC | 无 | 重铸功能，不参与战斗 |
| WitchDoctor | SimpleNPC | 无 | - |
| Clothier | SimpleNPC | 无 | - |
| Mechanic | SimpleNPC | 无 | - |
| PartyGirl | SimpleNPC | 无 | - |
| Stylist | SimpleNPC | 无 | - |
| TaxCollector | SimpleNPC | 无 | - |
| Truffle | SimpleNPC | 无 | - |
| Wizard | SimpleNPC | 无 | - |
| Zoologist | SimpleNPC | 无 | - |
| Angler | AnglerNPC | 无 | 钓鱼任务周期、水中漂浮、睡眠状态 |
| TravelingMerchant | TravelingMerchantNPC | 无 | 随机到来、定时离开、商品随机 |

### 13.6 生成条件

由 `NPCSpawner`（ConfluenceOtherworld 已有）管理，每个 NPC 的解锁条件：

| NPC | 条件 |
|-----|------|
| Guide | 始终可生成（除世界创建时已有） |
| Merchant | 玩家背包 ≥ 50 银币 |
| Nurse | Merchant 已存在 + 玩家最大生命 > 5 心 |
| Demolitionist | 玩家背包有爆炸物 |
| ArmsDealer | 玩家背包有枪或子弹 |
| Dryad | 击败任意 Boss（克眼/世界吞噬者/克脑/骷髅王） |
| GoblinTinkerer | 击败哥布林入侵 |
| Mechanic | 在地牢找到 |
| Angler | 在海洋生物群系找到 |
| Stylist | 在蜘蛛巢找到 |
| Painter | 已有 3+ NPC |
| DyeTrader | 玩家背包有染料材料 |
| WitchDoctor | 击败蜂王 |
| TaxCollector | 用净化粉净化 Tortured Soul |
| Clothier | 击败骷髅王 |
| TravelingMerchant | 随机，2+ NPC 已存在 |
| Truffle | 地上发光蘑菇生物群系 |
| Wizard | 击败肉山后地下找到 |
| PartyGirl | 已有 14+ NPC |
| Zoologist | 图鉴进度 ≥ 10% |

### 13.7 移植优先级

| 阶段 | 内容 | 说明 |
|------|------|------|
| P0 | BaseNPC + SimpleNPC (Guide) | 基类, Brain/Schedule/Sensor 初始化, GeckoLib 动画, 注册 |
| P1 | 房屋系统 | House / HouseValidater / HouseHandler 集成 / 自动查找 / 手动分配 |
| P2 | 交易/对话/心情 | 数据驱动（JSON），从 TerraEntity 迁移数据，接入 BaseNPC |
| P3 | 全 NPC 注册 + 生成条件 | 逐个注册，NPCSpawner 条件对接，旅商特殊逻辑 |
| P4 | 客户端渲染 / GUI | NPC 模型、交易 GUI、对话气泡 HUD |

### 13.8 交易条件系统

#### 设计

`PortCustomRegistration` — 注册表 Key 为
`RegistryKey<Registry<Codec<? extends TradeCondition>>>`，
注册表存储 `MapCodec<? extends TradeCondition>` 作为值。
根 Codec 基于 registry id dispatch，支持外部扩展。

```java
public final class TradeConditionTypes {
    public static final ResourceKey<Registry<Codec<? extends TradeCondition>>> KEY =
        ResourceKey.createRegistryKey(Confluence.asResource("trade_condition"));

    public static final PortCustomRegistration<Codec<? extends TradeCondition>> REGISTRY =
        PortRegisterHandler.custom(Confluence.MODID, KEY, maker -> maker.sync(true));

    // 根 Codec：按 registry id dispatch
    public static final Codec<TradeCondition> CODEC =
        REGISTRY.registryCodec().dispatch(TradeCondition::codec, Function.identity());
}

// 开放式接口（非 sealed）
public interface TradeCondition {

    boolean test(@Nullable ServerLevel level, @Nullable BaseNPC npc);

    Codec<? extends TradeCondition> codec();

    // 默认组合方法
    default TradeCondition and(TradeCondition other) { return new AndTradeCondition(this, other); }
    default TradeCondition or(TradeCondition other)  { return new OrTradeCondition(this, other); }
    default TradeCondition not()                      { return new NotTradeCondition(this); }
}
```

#### 内置条件类型

| 类型 ID | 实现 | 参数 | 评估逻辑 |
|---------|------|------|---------|
| `always` | `AlwaysTradeCondition` | 无 | 始终通过 |
| `hardmode` | `HardmodeTradeCondition` | 无 | `HardmodeConvertor` 已启用 |
| `any_boss_defeated` | `AnyBossDefeatedTradeCondition` | 无 | `KillBoard.getDefeatedBosses()` 非空 |
| `boss_defeated` | `BossDefeatedTradeCondition` | `EntityType<?>` | `KillBoard` 记录包含该 Boss |
| `biome` | `BiomeTradeCondition` | `List<ResourceKey<Biome>>`, `List<TagKey<Biome>>` | NPC 所在 biome 匹配 key 或 tag |
| `time` | `TimeTradeCondition` | `int from, to`, `boolean exclude` | dayTime 在 `[from, to]` 区间 |
| `kill_entity` | `KillEntityTradeCondition` | `EntityType<?>` | 玩家击杀统计 > 0 |
| `mood` | `MoodTradeCondition` | `int value`, `boolean less` | 心情值 ≥ value（`less=true` 则 ≤） |
| `npc_nearby` | `NPCNearbyTradeCondition` | `EntityType<?>` | 附近存在该类型 NPC |
| `bestiary` | `BestiaryTradeCondition` | `int count` | 图鉴解锁数 ≥ count |
| `date` | `DateTradeCondition` | `String preset` 或 `Boolean isLunar, DateStamp from, to` | 农历/公历日期匹配 |
| `and` | `AndTradeCondition` | `TradeCondition left, right` | left && right |
| `or` | `OrTradeCondition` | `TradeCondition left, right` | left \|\| right |
| `not` | `NotTradeCondition` | `TradeCondition inner` | !inner |

#### JSON 示例

```json
{
  "offers": [
    {
      "item": {"id": "minecraft:torch", "count": 1}
    },
    {
      "item": {"id": "confluence:silver_bullet", "count": 50},
      "condition": {
        "type": "and",
        "left":  {"type": "hardmode"},
        "right": {"type": "time", "from": 0, "to": 6000}
      }
    },
    {
      "item": {"id": "confluence:minishark", "count": 1},
      "condition": {"type": "any_boss_defeated"}
    }
  ]
}
```

- `condition` 字段为可选，缺失等价于 `"always"`
- 价格**不写在 JSON 中**，统一从 `ValueComponent` 读取

### 13.9 交易菜单

#### 布局

```
┌──────────────────────────────────────────────┐
│ NPC Name                    资金: 12金 34银  │
├──────────────────────────────────────────────┤
│  Row0 [ 0][ 1][ 2][ 3][ 4][ 5][ 6][ 7][ 8] │  ↑
│  Row1 [ 9][10][11][12][13][14][15][16][17]  │  █ 滚动条
│  Row2 [18][19][20][21][22][23][24][25][26]  │  ↓
│  Row3 [27][28][29][30][31][32][33][34][35]  │
├──────────────────────────────────────────────┤
│  玩家背包  Row0 [36]...[44]                  │
│            Row1 [45]...[53]                  │
│            Row2 [54]...[62]                  │
│  快捷栏    Row3 [63]...[71]                  │
└──────────────────────────────────────────────┘
```

- 36 个交易槽位（4×9，对齐玩家背包）
- 支持滚动条
- 关闭界面时，`PLAYER_SOLD` 物品**清空**（不持久化）

#### 槽位三态

| 状态 | 含义 | 点击行为（cursor 为空） |
|------|------|-----------------------|
| `EMPTY` | 可卖入 | cursor 有物品 → 卖出 |
| `NPC_ITEM` | NPC 出售品 | 付钱 → 物品到 cursor |
| `PLAYER_SOLD` | 玩家卖入 | 付原价 → 退款 |

#### 价格公式

| 操作 | 金额 |
|------|------|
| 卖出 | `ValueComponent × moodCoef` → 玩家收入 |
| 买入 | `ValueComponent × 5 × moodCoef` → 玩家支出 |
| 退款 | 原始卖出价 → 玩家支出（不收 mood 影响） |

`moodCoef` 来自 `NPCMood.getValue()` (0.75 ~ 1.5);
`ValueComponent` 来自物品 DataComponent。

#### 网络同步

```
打开界面 → S2C: NPC 商品列表（已过滤条件） + 心情系数 + 余额
玩家点击 → C2S: (slotIndex, action: BUY / SELL / REFUND)
服务端   → 验证 → 执行 → S2C: 更新槽位状态 + 余额
```

#### 物品 Codec

- 使用 `PortItemStackExtension.itemNonAirCodec` 序列化 `ItemStack`（支持数量）
- 价格读取：`ValueComponent.getValue(itemStack, 0)`（`ValueComponent` 无值返回 0 则不可交易）

#### 交易数据存储

- NPC 的 offers 从 JSON 加载（文件：`data/confluence/npc_trades/<npc_id>.json`）
- 每个 NPC 类型独立文件
- 服务端加载后缓存在内存，同步到客户端仅发送通过条件的商品

---

## 附录: 模块依赖

```
PortLib (Forge/NeoForge 抽象层)
  ↑
Confluence-Magic-Lib (共享属性、Boss 标记、工具类)
  ↑
ConfluenceOtherworld (所有内容：物品、方块、世界生成、实体、渲染、NPC)
  ↑
  ├── GeckoLib (实体动画)
  └── ParticleStorm (粒子特效)
```
