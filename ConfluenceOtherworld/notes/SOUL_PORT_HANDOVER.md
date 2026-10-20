# 魂师迁移交接单（1.21.1 → 1.20.1 Forge）

给下一个会话用。先读本文，再读 `notes/SOUL_PORT_PLAN_1_20_1.md`（原始缺口清单，已全部完成）。
1.21.1 侧的改动语义见 `D:\汇流来世\confluence\ConfluenceOtherworld\notes\SOUL_CHANGES_MASTER.md`。

## 一、当前状态

两项验证都是绿的：

- `gradlew :ConfluenceOtherworld:compileJava` → `BUILD SUCCESSFUL`
- `gradlew :ConfluenceOtherworld:runData` → `BUILD SUCCESSFUL`

1.21.1 侧同样 `compileJava` 通过。

## 二、本轮补齐的内容（原交接单的 1~6 项）

| 层 | 内容 |
| --- | --- |
| 任务 1 祈灵之刃 | `ModItems.createAttributes(...)` 按 PortLib 重写（原为注释）；`SoulItems.SPIRITWARD_BLADE` + `FIRST_OBTAIN_SOUL_SKILLS`；`PlayerUtils.learnSoulSkillsOnFirstObtain` 接进 `TickEvents`；`AttackEntityEvent` 贯穿、伤害前 +3、击杀 +1 魂三条监听；短剑标签、模型、配方、创造栏、姿态渲染 |
| 任务 2 套装 | `ModArmorBonus.SOUL$DROP` + 6 个套装键（hermit/seeker/souldancer/phantom/blue_hermit/white_pumpkin）；`ArmorItems` 7 套逐件数值；`SoulStorage.consumedSoul`/`souldancerBuffEnd`；`PlayerUtils.onSoulSpent`/isWearing*Set/onSoulCollected/tickSoulArmorSets（临时修饰符用 `PortAttributeModifier.rl2uuid`）；`LivingEntityEvents` 的逐灵增伤、收魂加成、魂舞减伤、白南瓜暴击回魂 |
| 任务 3 指令 | `/confluence soulSkill reset`、`slots <3..6>`、`ritual <0..6>` |
| 任务 4 创造栏与交易 | 魂师页签主/副武器与技能书组（含 8 魂灯、6 本书）；饰品页签战斗组 5 件；天师 3 级卖天穹倒转书 |
| 任务 5 数据生成 | `soul_attach`/`passive`/`soul_lure` 三个技能标签；魂灯 handheld、书皮 override、祈灵之刃模型；31 技能名 + 说明 + 仪式文案 + 状态文案 + 5 条饰品 tooltip；战利品（鬼魂 1% 引渡者、骷髅王 66% 记忆、克脑/世吞/血肉墙技能书、空岛村 33% 天穹倒转、发光蘑菇箱松露灯）；配方（工作台/重型工作台/地狱熔炉/祭坛/灵雾工作台） |
| 任务 6 客户端 | 书皮 item property；灵魂条 #abfffe + `OVERLAY_SOUL_UV_Y`；总览 7 个新节点；`BandOfSoulRegeneration` 再生手环（`registerDirectly` + `LibEffects.healPerSecond`）；引渡者被动 +1（`SOUL_LURE` 标签）；刻骨的记忆免手持施法 |

顺带修掉的两处双版本缺陷（两棵树同步改）：

1. `SOUL_STING`（自然之惩戒毒刺）从未注册渲染器 → 两版本都补 `SoulStingProjectileRenderer`。
2. 1.20.1 的通用书池把**技能 ID**写进 `SOUL_SKILL` 组件，掉出来是查不到定义的坏书 → 改为按 1.21.1 的 `soulBookLoot(bookId)` 写法，注入书籍 ID。

## 三、1.20.1 必守的写法（本轮实测）

| 项 | 1.20.1 写法 |
| --- | --- |
| 事件派发 | `PortEventHandler.postEventWithReturn(new XxxEvent(...))` |
| 伤害事件 | 用 PortLib 的 `PortLivingIncomingDamageEvent`（等价于 NeoForge 的单入口），不必拆成 Hurt/Damage |
| 属性读取 | `player.getAttributeValue(attr.value())`（`Holder` 必须 `.value()`） |
| 属性操作枚举 | `PortAttributeModifier.Operation`（`ADD_VALUE` / `ADD_MULTIPLIED_TOTAL`） |
| 临时修饰符 | `new AttributeModifier(PortAttributeModifier.rl2uuid(id), id.getPath(), value, operation.unwrap())` + `addOrUpdateTransientModifier` |
| 物品属性组件 | `PortItemAttributeModifiers.builder()`；攻击力/攻速的固定 ID 用 `ModItems.BASE_ATTACK_DAMAGE_ID`（1.20.1 没有 `Item.BASE_ATTACK_DAMAGE_ID`） |
| 效果类 | 继承 `PortMobEffect`；`hasEffect/removeEffect` 用 `ModEffects.X.get()` |
| 随机数 | 魂师代码统一用 `entity.getRandom()`；`getRandom1211()` 是 PortLib 兼容用，两者都可 |
| GUI 图标 | `guiGraphics.blitSprite(loc, x, y, w, h)`；裁剪时显式传入源尺寸，图集外 PNG 使用 `blit` |
| 战利品组件 | 1.20.1 没有 `SetComponentsFunction`，用 `BindSoulSkillFunction.bind(id)`（只写 `SOUL_SKILL`，不写稀有度） |
| 1.21 独有物品 | `Items.OMINOUS_BOTTLE` 不存在，劫掠之火配方改用 `Items.DRAGON_BREATH` |

## 四、编辑纪律（本轮又踩到一个坑）

1. **严禁用 PowerShell 的 `Get-Content -Raw` + `Set-Content` 做整文件替换**：本机是 PowerShell 5.1，无 BOM 的 UTF-8 文件会被按 GBK 读入再写成 UTF-8+BOM，中文全变乱码、行尾变 LF，javac 也会报错。整段插入一律用 `edit` 工具。
2. 含中文、注解、静态 import、`$` 变量名（如 `BAND$OF$SOUL`）的改动，同样走 `edit`。
3. 泛型集合字面量要带显式类型参数：`List.<ResourceLocation>of(...)`。
4. `javac 17.0.12` 偶发 `OutOfMemoryError: Java heap space`（同一份代码重跑即成功），遇到时**先原样重跑一次**再判断是不是真错误。
5. 一次只做一项，做完立即 `compileJava`。

## 五、仍未做

1. 全部内容**没有进游戏实测**（施法、书皮切换、复习刷新、贯穿手感、6 套套装触发、手环回血、毒刺可见性）。
2. 1.21.1 §14.2 里的历史遗留仍在：`go_over` 图标是占位图、`firmament_reversed` 只是原型、`soul_surge`/`spirit_surge` 中文同名、松露灯只有创造栏与发光蘑菇箱两个途径。
3. 1.21.1 侧 `notes/` 里多个旧文档仍是旧数值，未回头同步。
