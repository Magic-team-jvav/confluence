# WP2 `MonsterAttributeScaling`：普通敌怪全局倍率落地

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（8 → 0）。
> 1.20 源：`common/entity/monster/MonsterAttributeScaling.java`（116 行）+ `CommonConfigs:35-43 / 134 / 152 / 201-231 / 377`。

## 一、为什么这个类值得单独一批

它的两个调用点分别落在**两条与玩法直接相关、但 1.21 侧一直缺失**的链路上，且**都是「缺一个类导致整条配置功能是关的」**：

| 1.20 调用点 | 作用 | 1.21 落地前状态 |
|---|---|---|
| `EntityEvents:55` `joinLevel` → `apply(living, !event.loadedFromDisk())` | **所有**实体入世界的路径（自然生成 / 刷怪蛋 / 脚本 / 区块读档）统一套敌怪倍率 | 1.21 的 `joinLevel` 只有 Boss 那半（BMPE 批刻意留的缺口），普通敌怪倍率**完全没有入口** |
| `CommonConfigs:152` `onLoad()` → `reload()` | 配置加载/重载后在服务器线程重算已加载实体 | 无 |

而且 1.21 的 `CommonConfigs` 里**整个 `MonsterAttributes` 配置段都不存在**（9 个成员 + `SPEC` 字段）——
即「敌怪全局倍率」这个功能在 1.21 侧此前**连开关都没有**。

## 二、本批内容（1 新 java + 3 改）

| 文件 | 改动 |
|---|---|
| `common/entity/monster/MonsterAttributeScaling`（新） | 1.20 原文 + 五处 API 适配（见下） |
| `common/CommonConfigs` | ① 9 个成员（`ENHANCE_ALL_MONSTER` + 8 个 `MONSTER_ATTRIBUTES_MULTIPLIER_*`）② `register` 内 `MonsterAttributes` 段（1.20 `:201-231` 逐字，含 8 条 comment）③ `public static ModConfigSpec SPEC` 字段并在 `register` 里赋值（1.20 `:134/377`；1.21 此前 build 后直接交给容器、没留引用）④ `onLoad()` 里 `MonsterAttributeScaling.reload()`（1.20 `:152`） |
| `common/event/game/entity/EntityEvents` | `joinLevel` 前半段接回（1.20 `:53-57`）：`if (!event.isCanceled() && entity instanceof LivingEntity living) MonsterAttributeScaling.apply(living, !event.loadedFromDisk());` |

## 三、1.20 → 1.21.1 的五处 API 差异（全部写进类注释）

| 1.20 | 1.21.1 | 处理 |
|---|---|---|
| `net.minecraftforge.common.ForgeConfigSpec` | `net.neoforged.neoforge.common.ModConfigSpec` | 字段类型与枚举里的 `Supplier<ForgeConfigSpec.DoubleValue>` 同步改名 |
| `net.minecraftforge.common.Tags.EntityTypes.BOSSES` | `net.neoforged.neoforge.common.Tags.EntityTypes.BOSSES` | 同名同义（已 `javap` 核对 `neoforge-21.1.170-universal.jar`） |
| `net.minecraftforge.server.ServerLifecycleHooks` | `net.neoforged.neoforge.server.ServerLifecycleHooks` | `getCurrentServer()` 同名 |
| 修饰符 id = **UUID**（`UUID.nameUUIDFromBytes("confluence.monster_config." + name())`） | `AttributeModifier` 是 **record `(ResourceLocation id, double amount, Operation)`**；record **没有 name** 参数 | id 改成固定 `ResourceLocation`（`confluence:monster_config.<名字小写>`）；`getModifier/removeModifier/hasModifier` 改吃 `ResourceLocation`；`previous.getAmount()/getOperation()` → `amount()/operation()`；`Operation.MULTIPLY_TOTAL` → `ADD_MULTIPLIED_TOTAL`（与 `BossMultiplayerEnhancement:28-31` 同一处理） |
| `Attributes.MAX_HEALTH` 等是 **`Attribute`**；`LibAttributes.getAttackDamage().value()` | 静态字段是 **`Holder<Attribute>`**，`LivingEntity#getAttribute` 只吃 `Holder<Attribute>` | 枚举字段类型 `Supplier<Attribute>` → `Supplier<Holder<Attribute>>`；`LibAttributes.getAttackDamage().value()` **回退**成 `LibAttributes.getAttackDamage()`（这一处只有编译门能发现，闭包工具看不见） |

> 前四处是「PortLib/Forge → 原生」的常规换名，**第五处是 1.21.1 的属性注册表形态**，
> 也是本批 8 个错误里唯一需要改**语义签名**的那一组（7 个 lambda + 1 个调用点）。

## 四、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（8 → 0） |
| `check_duplicates.py` | 0 处「疑似移动/重复」（同 FQN，识别为移植目标自身） |
| 接线核对 | `ModEvents:142/149` 两处 `CommonConfigs.onLoad()` 与 1.20 `ModEvents:107/113` 一一对应 → `reload()` 会在加载与重载两条路径上被调用 |
| **配置成员级对账**（新加的检查） | 用正则比对两侧 `public static <类型> <名字>;`：1.21 **不再缺本批以外的任何** 1.20 成员，只剩 4 个「消费者未移植」的（见下节）；且 1.21 端**零个**多出来的成员 |
| 待游戏内验收 | 改 `monsterAttributesMultiplier*` 后重载配置 → 已加载敌怪立即变化；新增敌怪按新倍率生成；`enhanceAllMonster` 打开后原版敌怪也吃倍率（Boss/NPC/小动物/召唤物除外） |

## 五、顺带查出的 4 个「配置成员缺、但消费者未移植」项（**本批不加**）

用上面的成员级正则比对发现 1.20 有 4 个 `public static` 成员在 1.21 不存在。逐个查消费者：
**它们各自都只有一个消费者，而那个消费者本身还没移植**，所以按「不落死配置项」原则暂不添加：

| 成员 | 1.20 定义 | 唯一消费者 | 为什么现在不加 |
|---|---|---|---|
| `AUTO_SWING_ALL_SWORDS` | `CommonConfigs:48/233`（`autoSwingAllSwords`） | `mixin/integration/terracurio/TCClientPacketHandlerMixin:20` | 消费者在 **integration** 层（按既定决策**不移植**）→ 该开关在 1.21 永远不会被读 |
| `AUTO_RELEASE_ALL_BOWS` | `:49/234` | `client/handler/BowHandler:17` | `BowHandler` 尚未移植 |
| `SHIMMER_DECOMPOSE_FIRST_TAG_ITEM` | `:54/250` | `mixin/world/entity/item/ItemEntityMixin:154` | 该 mixin 行尚未移植（微光分解第一条 tag 物品） |
| `ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD` | `:84/275` | `common/item/accessory/GuideVooDooDollItem:117` | 向导巫毒娃娃属 WP3/物品批 |

→ 建议把这一条**做成常规检查**（本节的正则脚本口径：`public static \w+ (\w+);`），
每批收尾跑一次，避免再出现「成员级盲区」靠编译门才发现的情况。
