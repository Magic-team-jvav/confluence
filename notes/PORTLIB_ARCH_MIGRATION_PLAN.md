# 把 1.20.1 的 PortLib 新架构引入 neoforge-dev/1.21.1（分析与实施计划）

- 文档位置：`D:\Minecraft\1.21neoforge\confluence\notes\PORTLIB_ARCH_MIGRATION_PLAN.md`（1.21.1 侧）
- 考察对象：`D:\Minecraft\1.20forge\confluence`（`forge-dev/1.20.1` @ `a75bda140`，2026-09-25）与 `D:\Minecraft\1.21neoforge\confluence`（`neoforge-dev/1.21.1` @ `c387b9010`，2026-09-22）
- 两者同源：`origin = git@github.com:Magic-team-jvav/confluence.git`
- 状态：**v1，已锁定总路线（Q1 = 方案 C，Q2 = 方案 B）**；第 7 章剩余问题仍在逐一讨论
- 所有数字均来自实际命令（见附录 A），可作为后续校验的基线

---

## 0. 决策记录（Decision Log）

| # | 议题 | 决定 | 日期 |
|---|---|---|---|
| Q1 | 总路线 | **方案 C：冻结 1.21，单线开发 1.20，定期整体回移** | 本次讨论 |
| Q2 | 1.21 侧形态 | **方案 B：1.21 去 Port 化，写 NeoForge 原生 API**（1.21 分支不引入 PortLib） | 本次讨论 |
| Q3 | 实体与子模块边界 | **与 1.20 一致：实体/枪械内联进 ConfluenceOtherworld；TerraEntity / TerraGuns 在 Confluence 构建中退役**（仓库保留给其他使用方） | 本次讨论 |
| Q4 | 收割范围 | **TerraEntity/TerraGuns 的东西一律不收割**；1.21 侧一切按 1.20.1 的新 API/新实现来（丢弃项必须成文，见附录 B3 的"刻意丢弃清单"） | 本次讨论 |
| Q5 | 分支与历史 | **直接在 `neoforge-dev/1.21.1` 上逐提交移植**（分支名、CI ref、发布渠道不变；不做整树替换，见 Q10） | 本次讨论 |
| Q6 | 回移节奏 | **每次 1.20 minor 版本发布后回移一次（+ 紧急 hotfix 例外）**；验证门槛 = 编译 + runData 对拍 + runClient 冒烟 | 本次讨论 |
| Q7 | 版本口径 | **以 1.20 版本线为准，1.21 产物只加平台/日期后缀**（沿用现有 `mod_version + "+" + minecraft_version + "-yyMMdd"` 格式） | 本次讨论 |
| Q8 | 世界生成对齐 | **群系注入器以 1.20 为唯一实现**：先收尾 1.21 的未提交 WIP 并提交，再逐处判定（平台适配 vs 逻辑改进）后把改进收割回 1.20 | 本次讨论 |
| Q9 | 内容小缺口 | **全部丢弃，1.20 的内容即最终内容**：星尘龙（`StardustDragon`/`StardustDragonSegment`/`SummonFocusEffect`）、7+1 个物品（`stardust_dragon_staff`、6 个 `summon_*_sword_staff`、`wallet`）、`HouseDetectItem`、`FigureBlock` 手办方块、11 个 JEI recipe drawer 与 NPC 分类，均不重做 | 本次讨论 |
| Q10 | **移植方式** | **按 1.20.1 的分叉点逐提交比对移植，不做整树替换**（因为存在类的位置变动：609 处模块内移动 + 37 个"搬进子模块"的提交） | 本次讨论 |
| Q11 | Mixin 层策略 | **Mixin 不参与机械转换**：以 1.21 现有 mixin 为基准 + `check_mixin_targets.py` 审计驱动三分类；审计未清空不许进入编译阶段 | 本次讨论 |
| Q12 | **integration 层** | **1.20.1 是刻意删掉全部第三方集成的，1.21.1 要保留自己的集成**：移植时凡涉及 `integration/**` 的删除/改动一律不碰（`SKIP-1.21-KEEPS`）。台账工具已把 `/integration/` 设为默认忽略路径 | 本次讨论 |
| Q13 | **新提交的顺序** | **1.20.1 后续新增的提交一律排在 backlog 之后**（台账时间正序即已如此）：它们**依赖 1.20.1 的新架构**（PortLib / 模块搬迁 / 事件体系等），backlog 未落地前抢先移植会引入对 1.21 不存在的依赖 | 本次讨论 |
| Q14 | **污染提交规避** | **`dfcc5c041`（2026-08-15，cooobird，「feat: 对齐 1.21 内容与运行时行为」）永不移植**（台账标 `DO-NOT-PORT`）。作者提醒：其改动范围极大（1690 文件 / +105554 −20918）、**大部分内容是错误的**，部分已被 16 个回滚提交撤回（最大 `dc57ba5c2` 覆盖 663 文件），**部分残留仍在影响正确逻辑**（残留 **992** 文件，其中 java **172**）。凡触及残留文件的提交（**142 个**）都不得照抄 1.20 当前实现，优先以 **1.21 侧现有实现**为准；详见 `notes/POISON-dfcc5c041.md`、`notes/poison-residue-files.txt`，台账「污染残留」列已逐行标注 | 本次讨论 |

**Q5/Q10 的落地约束（必须在流程里写明）**：
- 移植**直接提交在 `neoforge-dev/1.21.1`**（Q5：分支名、CI ref、发布渠道不动），但改成**逐提交推进**（Q10），每步都必须让分支保持可编译——这样 nightly 不会被一次推上去的坏树打挂。
- 开工前给当前头打 tag（如 `pre-commit-march-2026-09`）留回滚点，并在 CHANGELOG/NOTES 里写明"自 `<分叉点>` 起按 `PORT-LEDGER.md` 逐提交移植"。
- 每个移植提交的信息统一写 `port of <1.20 hash>`，并在台账里回填 1.21 侧 hash；这样"某个 1.21 行为对应哪个 1.20 提交"始终可查（R4 hotfix 追溯依赖它）。
- 禁止"先合上后修"：编译不过就停下修完再往下走。

**C+B 的直接后果（本计划据此重写）**：
1. **1.20.1 = 唯一开发线**。所有新功能只在 1.20 侧开发（继续用 Port 词汇与 PortLib）。
2. **1.21 分支 = 派生产物**：由 1.20 树"去 Port 化 + 平台适配"生成，不再独立演进。
3. **PortLib 不进入 1.21**：`settings.gradle` 不加 `:PortLib`，不发布 `MesdagPortLib-neoforge`，1.21 不依赖 `portlib` mod。附录 B1 的 444 条 FACADE→原生映射表**改为当作"Port→原生"翻译词典**使用。
4. **每轮回移 = 复制 1.20 源码 + 跑转换器 + 编译错误收敛**，而不是逐文件重写（转换器是本计划的核心工程产物，见 Phase 2）。
5. **冻结前必须先"收割"**：1.21 独有内容（虚空海/虫洞/晶塔代码/连枷长矛重构/Night's Edge/hook 模型/壁画预览/残影模块/PR #232）必须先并入 1.20 主线，否则冻结即删除（见 Phase 1、§7 Q4）。
6. **实体/子模块（Q3）**：1.21 与 1.20 一致——实体与枪械内联进 `ConfluenceOtherworld`，`TerraEntity`/`TerraGuns` 模块在 Confluence 构建中退役；1.21 侧的 87 个 TerraEntity 集成文件需逐类判定（作废 / 改写为对内联实体的集成）。

---

## 0.1 结论摘要（TL;DR）

1. **两条分支已经长期分叉**：分叉点 `795ac9ccc`（2026-05-31）。此后 `forge-dev/1.20.1` 独自前进 383 个提交，`neoforge-dev/1.21.1` 独自前进 196 个提交。手工同步成本正在随内容量线性增长（1.21 侧近期提交里已有大量 "同步更改 / 同步光剑更改 / 同步修复物品搜索问题" 这类人工搬运）。

2. **1.20.1 侧的"新架构"核心是一个平台抽象层 `PortLib`**：它把 **1.21.1 NeoForge 的 API 面貌**用 `org.mesdag.portlib.*` 命名在 **1.20.1 Forge 上重新实现**（`archivesName = "MesdagPortLib+neoforge1.21.1 to forge1.20.1"`），并配套：
   - 接口注入（`portlib-interfaceinjection.json`，82 条，把 `IPort*Extension` 注入原版类）；
   - 两个 coremod JS 插入脚本（ASM）；
   - `@Diff` 注解标记"加载器差异代码"；
   - 统一的事件总线（`PortEventHandler`）、网络（`IPortPacket`/`PortStreamCodec`/`PortPacketDistributor`）、注册（`PortRegisterHandler`/`PortRegistryEntry`/`PortDeferredBlock|Item`）、数据映射（datamap）、数据组件（component）、附件（attachment）、配置（`PortConfigSpec`）、客户端 GUI 组件（`PortSprite`/`PortGuiLayer`/`PortConfigurationScreen`）；
   - PortLib 作为**独立可发布的 mod 依赖**（1.20 主模组 `mods.toml` 里 `modId="portlib", mandatory=true, versionRange=[${portlib_version},)`），不 jarJar。

3. **按 C+B，1.21 侧的"架构迁移"= 把 1.20 的树重写成 1.21 原生代码**，而不是引入 PortLib。1.20 侧已用 `Port*` 词汇改写（主模组 474/2496 个文件、Magic-Lib 59/266、TerraCurio 53/206、TerraFurniture 20/114 直接 import PortLib），因此"去 Port 化"可以**系统性映射**（B1 已给出 444 条 FACADE → 原生类对应关系），这是一次可工具化、可分批验证的大规模重构，而不是逐个文件的自由发挥。

4. **规模已量化**：1.20 主模组 2496 个 java 文件、1.21 侧 1760 个；两者同路径 **1495 个**，其中 317 个完全一致、**1000 个差异 ≤50 行**、233 个深度分叉。1.21 侧会被触及的加载器 API 命中面：注册 227 文件、事件 181、网络 129（+Magic-Lib 自有 89）、数据组件 59、attachment 20、datamap 8。1.20 独有 1001 个文件（实体 425/召唤师 92/…），1.21 独有 265 个（多为 TerraEntity 集成 87）。

5. **两条"红线"已量化**（详见附录 B1/B2）：
   - 1.21 侧独有内容**必须在冻结前收割进 1.20**：虚空海、虫洞药水与界面、晶塔代码（1.20 侧只有资源没有代码，**最危险**）、连枷/长矛重构、Night's Edge、20 个 hook 模型、壁画放置预览、残影模块，以及 4 个 merge 提交里的贡献者 PR #232。
   - 1.20 侧的移植工作**不要重复投入**：TerraBlender 集成管道、legendarytooltips/itemborders 兼容、datatip 整条线、waystone 集成等 1.21 侧已删除的加载器兼容代码，不要反向搬回。

6. **主要风险**：去 Port 化是"大范围机械 + 少量语义"的重构，静默行为改变是头号风险（B1 的 R5/R6/R7/R8 已列出具体陷阱）；其次是 1.20 主线在冻结期间继续前进导致的基线漂移。

---

## 1. 现状考察

### 1.1 分支拓扑与体量

| 项 | forge-dev/1.20.1 | neoforge-dev/1.21.1 |
|---|---|---|
| 头部提交 | `a75bda140` 2026-09-25 | `c387b9010` 2026-09-22 |
| 分叉点 | `795ac9ccc` 2026-05-31（共同祖先） | 同 |
| 分叉后提交数 | 383 | 196 |
| MC / 加载器 | 1.20.1 / Forge 47.4.20（`legacyForge` + moddev 2.0.141，Java 17） | 1.21.1 / NeoForge 21.1.219（`net.neoforged.moddev` 2.0.140，Java 21） |
| 子模块（settings.gradle） | Confluence-Magic-Lib, TerraCurio, TerraFurniture, **PortLib** | Confluence-Magic-Lib, **TerraEntity**, TerraCurio, **TerraGuns**, TerraFurniture |
| ConfluenceOtherworld java 文件 | **2496** | **1760** |

注：1.20forge 仓库里本地 `neoforge-dev/1.21.1` 指针停在 2026-05-31 的 `795ac9ccc`（陈旧）；真实 1.21 头部在另一个 clone（即本目录）里。

### 1.2 1.20.1 侧的"新架构"由五件事组成

**(1) PortLib 平台抽象层**（627 个 java 文件在 `org/mesdag/portlib` 下，另有约 18 个在 `PortLib/extensions/**`；按 git 计 645 个）。包体量：

| 包 | 文件数 | 性质 |
|---|---|---|
| `diff` | 168 | 1.20 侧差异实现：`Diff` 注解、`IPort*` 差异接口、**168 个 mixin/ES 模拟**、registry-manager hooks |
| `event` | 218 | 把 NeoForge 1.21 事件体系在 1.20 上重建（`PortEventHandler`、`PortEventHooks`、`PortBus`，以及 brewing/client/entity/living/... 全套事件类型） |
| `wrapper` | 164 | API 形状包装：`PortItem`/`PortFluidType`/`PortMobEffect`/`PortFoodProperties`/`PortAbstractArrow`/`PortRegistryEntry` 等 |
| `network` | 22 | `IPortPacket`(C2S/S2C)、`PortPacketDistributor`、`PortStreamCodec`/`PortByteBufCodecs`、config 阶段包 |
| `registries` | 20 | `PortRegisterHandler`、`PortRegistryEntry`、`PortDeferredBlock/Item`、各 `PortXxxRegistration` |
| `util` / `datamap` / `client` / `attachment` / `component` / `config` / `loot` | 11/7/6/5/3/1/1 | 共享工具、数据映射（含同步）、GUI 组件、附件同步、数据组件、配置、loot modifier |

关键机制（决定 1.21 侧怎么做）：
- **接口注入**：`portlib-interfaceinjection.json` 把 `IPortItemExtension`、`IPortEntityExtension`、`IPortItemStackExtension` … 注入原版类，使 Mod 代码可以用 `((IPortItemStackExtension) stack).portXxx()` 的形式访问"1.21 才有"的行为。
- **`PortLib/extensions/**`（18 个）**：借用 Manifold 的目录/命名风格（`package PortLib.extensions.<目标类全名>`、静态方法首参 `thiz`），但**已放弃 Manifold 插件**，实际是**普通静态工具类**，调用形式为显式静态调用，例如 `PortListExtension.getFirst(list)`、`PortCodecExtension.lenientOptionalFieldOf(...)`。主模组有 97 处引用（用到其中 6 个类）。→ **这 18 个类与加载器无关，可原样复制到 1.21**。
- **`@Diff`**：源码级注解，明确标出"这是加载器差异"，**1.21 侧不需要任何对应实现** —— 是天然的裁剪边界。
- **coremod JS**（`portlib_insert.js` / `portlib_extension_insert.js`）+ `META-INF/accesstransformer.cfg`：启动期字节码插入。
- **refmap patch**：`build.gradle` 里手工补 `EnchantmentMenuMixin`/`RenderBuffersMixin` 的 lambda 映射（1.20 特有痛点）。
- **可独立发布**：走 `maven.confluence.ink`，`mod_version=1.2.6`，主模组要求 `[1.2.5,)`。

**(2) 模块边界重组**：`part8`(2026-06-10) 起移除 TerraEntity / TerraGuns 子模块，实体体系**内联进 ConfluenceOtherworld**（`org.confluence.mod.common.entity` 从分叉时的 133 个文件涨到 **552** 个），并在 `part22`/`part23`(2026-06-27) 把原 TerraEntity 风格的接口（`ICollisionAttackEntity`/`IMinion`/`IWorm`/`IFSMGeoMob`/`SharedFlagController` …）**删除**，改为自研**行为树 AI**（`common/entity/ai/bt/**`：`BTStatus`、`ConditionalSwitchNode`、`RoundRobinSelectorNode` …）＋召唤师体系（`common/summoner` 92 个文件、`client/summoner` 49 个）。

**(3) 全量 Port 化改写**：`part` ~ `part25`（2026-06-04 ~ 06-28）以及 `part npc`/`part critters`/`part recipe datagen`/`part enchantment`/`part fluid type`（至 2026-07-02）是把 **1.21 侧代码移植到 1.20** 的过程：源码改用 Port 词汇，类型/注册/网络/事件/数据组件统一下沉到 PortLib。此后 1.20 侧继续独立演进（2026-07 ~ 09：NPC 商店、渔夫、染料商、火星探测器、剑气渲染、修饰语大改等）。

**(4) 事件与机制"大一统"**：如 `动态群系修改与client tick事件大一统`、`统一生物生成检查`、`统一 NPC 心情配置与环境计算`、`IdentityHashMap 改成 Reference2ObjectOpenHashMap` —— 这些是把逻辑收敛到 PortLib 事件总线/统一入口的产物。

**(5) 存档/属性/数据的兼容层**：PortLib 在 1.20 上补了 1.21 才有的属性（`generic.scale`、`player.block_break_speed`、`generic.burning_time`、`player.sneaking_speed` …）、方块（tuff 系列）、datamap（compostables/furnace_fuels）、attachment（含同步协议）、数据组件等。

### 1.3 1.21.1 侧现状

- 没有任何 PortLib 痕迹（工作区全文检索 `portlib` 零命中）；`gradle.properties` 无 `portlib_version`；`settings.gradle` 无 `:PortLib`。
- 使用 NeoForge 原生 API + `Confluence-Magic-Lib` 自有抽象（例如网络包用 `org.confluence.lib.network.IPacketS2C` + `Type<T>`，而不是 PortLib 的 `IPortPacket.S2C` + `ResourceLocation`）。
- 实体/动画/AI 在 **TerraEntity 独立模块**（860 个 java 文件，`org.confluence.terraentity.*`），主模组有 185 处 import 依赖它；主模组内只剩 137 个实体相关文件（抛射物、鱼钩、矿车等）。
- 1.21 特有资产：TerraEntity/TerraGuns 模块本身、`integration/terra_entity`(43 文件)、`mixin/integration`(44 文件)、Ageratum 指南书资源、Create/Ponder/Registrate/KubeJS/Irons Spells/Ars Nouveau/Player Animator 等集成。
- **当前工作区未提交**（重要！）：世界生成/群系注入相关改动（`BiomeSourceHandler`、`RegionBiomeHandler`、`BiomeRegionTable`、`TheEndBiomeHolder`、若干 mixin、新增 `BiomeSourceMixin.java`、删除 `InjectionProbe.java`），以及 3 个子模块内容脏（Confluence-Magic-Lib / TerraCurio / TerraEntity）。

### 1.4 量化差异（基线数据）

主模组 `ConfluenceOtherworld/src/main/java`：1.20 = 2496 文件，1.21 = 1760 文件，**同名同路径（可参与 merge）1495 个**。

对这 1495 个共同文件，剥掉 `import org.mesdag.portlib.*` 行后按行对比：

| 差异行数 | 文件数 |
|---|---|
| 完全一致 | 317 |
| 1–5 行（多为 import / 空行） | 248 |
| 6–20 行 | 433 |
| 21–50 行 | 264 |
| 51–200 行 | 167 |
| >200 行（深度分叉） | 66 |

→ **约 1000 个共同文件差异 ≤50 行，属于"词汇级/API 拼写级"差异**；233 个文件是真实逻辑分叉。差异的典型成因（实测样例）：
- Port 词汇 vs 原生：`PortStreamCodec` ↔ `StreamCodec`；`IPortPacket.S2C` ↔ `IPacketS2C`；`PortLib.CHISELED_TUFF.get()` ↔ `Blocks.CHISELED_TUFF`（1.20 没有这些方块，所以走 PortLib 常量）。
- **原版方法签名差异（PortLib 不覆盖）**：`void applyEffectTick(...)` + `isDurationEffectTick(...)`（1.20）↔ `boolean applyEffectTick(...)` + `shouldApplyEffectTickThisTick(...)`（1.21）；`appendHoverText(ItemStack, @Nullable Level, ...)` ↔ `appendHoverText(ItemStack, TooltipContext, ...)`；1.21 的 `Block#codec()` / `MapCodec` 在 1.20 缺失。
- 自有库分叉：`LibUtils.createItemEntity` ↔ `LibEntityUtils.createItemEntity`（Confluence-Magic-Lib 也分叉了）。

其余模块：Confluence-Magic-Lib 共同 183（一致 68 / 差异 115，仅 1.20 有 83，仅 1.21 有 36）；TerraCurio 共同 178（一致 35 / 差异 143，仅 1.20 有 28，仅 1.21 有 57）；TerraFurniture 共同 111（一致 45 / 差异 66，仅 1.20 有 3，仅 1.21 有 9）。

**独有文件分布**（主模组）：

| 只在 1.20 有（1001 个） | 数量 | | 只在 1.21 有（265 个） | 数量 |
|---|---|---|---|---|
| common/entity | 425 | | mixin/integration | 44 |
| common/summoner | 92 | | integration/terra_entity | 43 |
| common/item | 86 | | client/model | 22 |
| client/entity | 79 | | common/data | 17 |
| client/renderer | 53 | | client/renderer | 12 |
| client/summoner | 49 | | common/item | 10 |
| 其余 | 217 | | 其余 | 117 |

→ 1.20 的独有部分**主要就是"内联实体框架 + 召唤师体系 + 新内容"**；1.21 的独有部分**主要就是"TerraEntity 集成层"**。两边独有集合的冲突面其实不大。

### 1.5 已有资产：PortLib 自己的 1.21.1 分支

`PortLib` 子模块仓库（`git@github.com:westernat/MesdagPortLib.git`）分支：

| 分支 | 版本 | 最后提交 | java 类 | 机制 | 关系 |
|---|---|---|---|---|---|
| `forge-dev/1.20.1`（当前使用） | 1.2.6 | 2026-09-25（177 个提交） | 645 | 接口注入 + 静态扩展工具类（**无 Manifold**）+ coremod + mixin | 与下面**无共同祖先** |
| `origin/neoforge/1.21.1` | 0.0.27 | 2026-04-21（58 个提交） | 382 | **真 Manifold**（`jarJar manifold-rt` + `-Xplugin:Manifold`，`@Extension` 扩展方法 `PortLib/extensions/net/minecraft/...`）+ mixin + `neoforge.mods.toml`，Java 21 / NeoForge 21.1.219 | 已放弃 |

两分支类集合对比：共同 FQN **328**，仅 1.21 有 54，仅 1.20 有 317。
1.20 主模组源码 import 的 PortLib 类型去重 **159** 个，其中 **87 个在 1.21 分支已有、72 个缺失**（缺失集中在 `client.gui.components.*`、`config.PortConfigSpec`、`datamap.builtin.*`、`network.*`、`registries.PortDeferred*`、大量 `wrapper.common.extensions.IPort*`、`event.network.PortRegisterPayloadHandlersEvent` 等）。

**按 Q2=B 的口径，这个分支不再是"要复活的实现"，而是**：可当作改写参考（382 个类的 API 形状、Manifold 之外的部分），但 1.21 侧最终不引入 PortLib；真正要用的是这份资产反面的**映射关系**——即附录 B1 从 forge 线最新代码整理出的 444 条 FACADE→原生对应表。

### 1.6 1.21 侧 Port 化工作量抽样（Phase 3a 的靶子）

1.21 六个模块共 **3296** 个 java 文件，按"会被 Port 词汇替换的加载器 API"统计命中文件数：

| 目标 API（1.21 原生） | 命中文件数 | 对应 Port 词汇 |
|---|---|---|
| `net.neoforged.neoforge.registries.*`（DeferredRegister/DeferredItem/Holder） | 227 | `PortRegisterHandler` / `PortRegistryEntry` / `PortDeferredBlock|Item` |
| NeoForge 事件总线（`net.neoforged.bus/fml/common.event`、`@SubscribeEvent`、`@EventBusSubscriber`） | 181 | `PortEventHandler` / `PortEventHooks` |
| `net.neoforged.neoforge.network.*`（`CustomPacketPayload`/`PayloadRegistrar`） | 129 | `IPortPacket` / `PortPacketDistributor` |
| `org.confluence.lib.network`（Magic-Lib 自有 `IPacketS2C`） | 89 | `IPortPacket.S2C`（1.20 已改用 PortLib） |
| `net.minecraft.core.component.DataComponents` | 59 | PortLib 组件包装/`PortDataComponentType` |
| `AttachmentType` | 20 | `PortAttachmentType` |
| `DataMapType` | 8 | `PortDataMapType` |

（`org.confluence.terraentity` 在主模组侧另有 185 处引用，属 Q3 的边界问题。）

---

## 2. 目标定义与方案比较

已定口径（Q1 = C，Q2 = B）：**1.20.1 是唯一开发线；1.21.1 分支是派生产物，由 1.20 树"去 Port 化 + 平台适配"定期整体生成；1.21 侧不引入 PortLib。**

§2 的其余候选方案保留如下（供回看，已不采用）：

| 方案 | 做法 | 工作量 | 风险 | 收益 | 状态 |
|---|---|---|---|---|---|
| A. PortLib 双端化 | 1.21 实现 PortLib 门面，1.21 源码 Port 化，双向收敛 | 高（≈464–472 类门面 + 全量 Port 化） | 中 | 高：跨分支 merge 成日常 | **未采用**（Q1 选了 C） |
| B. 1.21 去 Port 化写原生 | 1.21 侧用 NeoForge 原生 API，不含 PortLib | 中高（一次性整树重构，但可工具化） | 中 | 两边代码"各自地道" | **已采用**（Q2） |
| C. 冻结 1.21 + 单线开发 1.20 + 定期整体回移 | 1.21 只做派生发布 | 低（每周期一次整树移植） | 高（派生分支无独立维护） | 消除双线同步 | **已采用**（Q1） |
| D. 单仓库单分支 + 共享 sourceSet + 预处理器 | 一个分支内按 loader 叠 overlay | 很高 | 高（MC 版本差异不是加载器差异，仍需 shim） | 理论最优 | 不采用 |
| E. 反向：1.20 退化成 1.21 原生 | 拆掉 PortLib | 极高 | 极高 | 无 | 不采用 |

**C+B 组合的代价与对冲**（本计划的核心论证）：
- 代价：每轮回移都要把 1.20 的改动变成 1.21 的原生代码；若靠人手逐文件重写，等于把现在的 196 个"同步更改"提交放大成几千个文件的工作量，且会重复发生。
- 对冲一（工具）：把去 Port 化**工具化**。附录 B1 已把 444 个 FACADE 类逐一对上 NeoForge 原生类型/API，155 个 EMULATION-ONLY 类给出"1.21 侧应当做什么"，20 个 SHARED-PLAIN 可直接复制——这本质上是一张**翻译词典**，据此实现 `Port→原生` 转换器（Phase 2），机械部分交给脚本。
- 对冲二（流程，Q10）：**逐提交移植**而不是整树转换。1.20 分叉后有 609 处模块内移动 + 37 个"搬进子模块"的提交，路径级对比会丢掉对应关系；逐提交走 git 的 `-M -C` 能得到行级对应，单提交的可编译门槛也让"中断即安全"。
- 前提：消费端代码要"词汇一致"。1.20 侧现已统一用 Port 词汇（part1–25 的成果），这正是转换器能可靠工作的原因——否则同一语义在 1.20 侧有十种写法，规则无法穷举。

---

## 3. 推荐路线：分阶段实施

### Phase 0 —— 准备（0.5~1 天）
1. 先把 1.21 侧**当前未提交的群系注入改动**提交/暂存到独立分支（例如 `wip/worldgen-injector`），保证后续大迁移有干净基线（现在工作区有 12 处修改 + 1 删除 + 1 新增 + 3 个子模块脏）。
2. 建立**可复现的度量脚本**（附录 A 的命令固化为 `notes/tools/measure-divergence.ps1`），每次阶段结束重跑，作为"收敛度/漂移度"指标。
3. 记录基线：1.20 侧 `a75bda140` + PortLib 指针 `fc518eb`；1.21 侧 `c387b9010`；并记录当前 `:ConfluenceOtherworld:compileJava` 是否绿。
4. 冻结 1.21 侧的**新功能开发**（在收割完成前，1.21 侧只允许修 bug，避免收割目标移动）。

### Phase 1 —— 收割：把 1.21 独有内容并入 1.20 主线（**冻结前必须完成**）

> 这是 C 方案的前置条件：1.20 必须成为 1.21 的**超集**，否则"冻结 + 逐提交回移"就等于删除 1.21 侧独有成果。

1. 范围（按附录 B2 的簇与红线清单）：
   - **红线**：虚空海 Void Sea（约 20 类 + 4 shader）、虫洞药水与界面、**晶塔代码**（1.20 只有资源无代码）、连枷/锁链家族、长矛 Builder 重构、Night's Edge、20 个 hook 模型、壁画放置预览、残影模块、贡献者 PR #232（`6e9102d84`）。
   - **内容簇**：C3 datagen 内容与 117 个 json、C4 世界生成数据（只取数据、不取 TerraBlender 机械）、C5 战斗、C7 方块与 328 资源、C8 盔甲/时装、C9 客户端渲染/HUD/hook、C10 粒子、C11 晶塔、C13 i18n、C14 美术。
   - **必须有意保留的行为修复 9 组**：`f481e777b`、`6f7093e23`、`f5b5863aa`、`22f7b05cf`、`43459d956`、`04004d5d8`/`f8b6f87f0`、`c25a6308c`、`d3e20bbba`/`a272c601c`。
   - **不收割**（已确认是平台性工作、1.20 侧不应有）：TerraBlender/citadel/lithium/mixinsquared 管道、legendarytooltips/itemborders 兼容、datatip 整条线、waystone 集成、纯版本号提升、旧 HUD 层删除。
   - **TerraEntity/TerraGuns：一律不收割（Q4 已定）**——附录 B3 里那 ≈200 个"1.20 无对应物"的类不进入收割清单；1.21 侧一切按 1.20.1 的新 API/新实现来。但必须产出一份**「刻意丢弃清单」**（`notes/DROPPED-FROM-TERRA-FAMILY.md`），逐项记录：客户端动画器层、动态模糊（9 文件 + 7 shader）、拖尾（10）、镜头震动（3 + `BossSpawnCameraManager`）、聊天/气泡（25）、`EffectStrategy`(8)、`Chester` 注册表(4)、`DriveAwaySystem`(5)、`FigureBlock`(3)、BT decoration 节点 + `BTServer`、`CrossBowAttackOnCooldownBrain`、4 个 AI 事件类、12 个数据包注册表、交易 SPI 46 文件 + 8 个 recipe drawer + JEI NPC 分类、mapped data 19 文件、TerraGuns 的 `TaczAnimationConstraint`/3 个附魔/`EmergencyMeleePacketC2S`/`PUT_AWAY`/`EJECT_SHELL`/3 个单元测试。**记录目的**：日后被玩家或作者要求"找回某个效果"时，能立刻知道它曾在哪、以及"要在 1.20 新 API 上重做而不是搬 TE 代码"。
   - `integration\terra_entity`(43) 与 `mixin\integration` 里的 14 个 TE mixin：6 个纯接线 + 12 个随模块消失；**承载内容的部分不搬 TE 代码，而是判定"1.20 新 API 下是否需要该内容"**（例如 12 个交易 lock 若在 1.20 的 27 个交易条件里已有对应实现，就是零工作；11 个 recipe drawer 若玩家可见则按 1.20 的展示系统重做）。
2. 方向与手段：**1.21 原生代码 → 1.20 Port 词汇**（与 1.20 侧 `part1`–`part25` 同向，可复用其经验与手法；必要时先写一个"原生→Port"方向的脚本，它是 Phase 2 转换器的逆用）。
3. 每项功能在 1.20 侧落地后，**在 1.20 端做一次运行验证**（runClient/runData），并在收割清单里记录"对应 1.21 提交哈希"以便回溯。
4. 交付物：`notes/HARVEST-CHECKLIST.md`（逐项：1.21 提交/文件 → 1.20 目标路径 → 状态 → 验证方式），以及 1.20 侧对应提交。

### Phase 2 —— 建"Port → 原生"转换器（**本计划的核心工程产物**）

目的：把"每轮整体回移都要人工重写"降级为"脚本 + 编译错误收敛"。规则来源就是附录 B1（444 条 FACADE→原生映射、155 条 EMULATION-ONLY 处理规则、20 条直接复制）。

三层规则，输出必须区分"已自动处理"与"需人工裁决"：
1. **类型/import 层（机械，覆盖绝大多数文件）**：`PortStreamCodec→StreamCodec`、`PortByteBufCodecs→ByteBufCodecs`、`PortRegistryEntry→DeferredHolder`、`PortDeferredItem/Block→DeferredItem/Block`、`PortRegisterHandler→DeferredRegister`、`PortDataMapType→DataMapType`、`PortAttachmentType→AttachmentType`、`PortConfigSpec→ModConfigSpec`、`PortWidgetSprites→WidgetSprites`、`PortRegistries→NeoForgeRegistries`、`IPortAttachmentHolder→IAttachmentHolder`、`IPortPacket(.Context)→CustomPacketPayload + IPayloadContext`、218 个 `Port*Event`→同名 `net.neoforged.neoforge.event.*`。注意 B1-R2/R3：**名字与包路径不只是加前缀**（`PortSpawnClusterSizeEvent→SpawnClusterSizeEvent`、`event.other.*`→`event.*`），必须用显式映射表而不是字符串替换。
2. **调用点层（API 形状差异）**：`PortRegisterHandler.create(...)`→`DeferredRegister.create(...)`（1.21 是 `ResourceKey` 优先，B1-R5）、`registrar("1")`→`PayloadRegistrar.versioned("1")`、`registerInGameS2C→playToClient`、`registerLoginS2C→configurationToClient`、附件 `getAttach/setAttach/hasAttach/syncAttach`→`getData/setData/hasData/syncData`（B1-R6）、`PortEventHandler.addListener`→`IEventBus#addListener`（同名重载，B1-R4 说明这是最顺的一层）。
3. **人工裁决清单（禁止自动改写，必须逐条处理）**：`PortCalculatePlayerTurnEvent` 的 `PortTriState`→`boolean`、`PortRenderLivingEvent` 3 类型参数→2、`PortDeltaTicker→DeltaTracker`、`PortTriState→neoforged…common.util.TriState`、`PortArmorItem.PortType`（1.21.1 的 `ArmorMaterial` 是 record）、`IPortAttributeExtension.Sentiment`（1.21.2 特性）、数据组件/附件的"模拟语义 vs 原生语义"差异。**转换器必须把这些点打成 TODO 报告，绝不静默通过。**

实现形态（**已落地**）：数据驱动的规则表（`tools/port2native/rules/*.json`）+ Python 引擎 `tools/port2native/port2native.py`，每次运行产出：
- 转换后的树（只写有改动的文件）；
- `_report/uncovered.json`（无法映射的条目，**必须为空**才能进入编译阶段）；
- `_report/manual-todo.md`（人工裁决点，逐条处理）；
- `_report/leftovers.txt`（转换后仍残留 Port 引用的位置）；
- `_report/stats.json`（重写计数，用于估算与回归）。

**规则面已实测为两个规则集**（比原估计多一个）：
1. **PortLib 词汇**：主模组用到 159 个去重类型（PortLib 共 645 类）；
2. **不经 PortLib 的直接 Forge import**：四模块合计 **665 处 / 199 个去重类型**——1.20 源码并未 100% Port 化（例：`TFRegistries.java` 里 `import net.minecraftforge.eventbus.api.IEventBus;`）。高频项：`eventbus.api` 62、`RegistryObject` 53、`common.data` 52、`event.entity` 39、`registries.DeferredRegister` 38、`client.*` 104、`ForgeRegistries` 26、`fml.event` 24。其中 `RegistryObject` 无同名对应物，须逐种用法映射到 `DeferredHolder`/`DeferredItem`/`DeferredBlock`/`Holder`。

**试点（TerraFurniture，2026-09-27）**：114 文件扫描、20 个含 PortLib、20 个已转换；`uncovered` 仅 3 条（`PortLib.extensions.*` 缺 `shared` 规则）；`manual-todo` 22 处；residual 10 个文件全部对得上 manual/drop 清单。过程中修掉三个引擎缺陷（重复 import、`PortLib.extensions.*` 命名空间未识别、中低置信度映射未进人工裁决清单）。详见 `tools/port2native/README.md`。

**已硬化（2026-09-27）**：全量规则到位后跑试点的过程中发现**不能无条件机械应用**——实跑踩到 `pattern=\.get → replace=.get()`（把 `.getKey()` 改成 `.get()Key()`）与 `replace="SAME"` 占位符（把 `.getName()` 改成 `SAME)Name()`），故加了三道安全闸（`review` 不自动应用 / 占位符拒绝 / 正则最长字面量 <8 视为过宽）+ 两道自检（括号配平、`BUS` 占位符残留，违反则拒写该文件并保留原文）+ 规则执行顺序修正（调用点 → 全限定名 → 简单名）+ 人工治理文件 `rules/overrides.json`（disable 10 / promote 11 / busAware 4 / extraRules 2）。同时让 **Forge→NeoForge 规则真正生效**（引擎原先只识别 PortLib 命名空间的 import，现已改为"有规则即可转换"）。TerraFurniture 复跑：含 PortLib/Forge 引用的文件 **20 → 44**、改动文件 **19 → 44**、`uncovered` 0、损伤与占位符残留 0；**总线解析**能把"游戏总线事件"自动写成 `NeoForge.EVENT_BUS.addListener(...)`，把"模组总线事件"拦下并说明必须用 `@Mod` 构造器的 `IEventBus`。

验收：对 1.20 全树（2497 + 266 + 206 + 114 文件）跑一遍，`uncovered.json` 为空、`manual-todo.md` 逐条裁决完，并人工抽查若干文件的转换正确性。

### Phase 3 的前置：**分层策略（L1–L4）——不能整树都塞进转换器**

原方案只说"跑转换器重建 1.21 树"，漏掉了一层关键事实：**版本与加载器差异不只在类型名，还在 Mixin 的注入目标上**。机械转换 Mixin 层的后果是启动期 `MixinApplyError` / 注入点找不到，而这类错误在编译期完全看不出来。因此按层区别对待：

| 层 | 内容 | 处理方式 |
|---|---|---|
| **L1 内容与游戏逻辑** | `common/entity`、`item`、`block`、`worldgen` 数据、client 渲染、recipe、datagen 内容 | **转换器批量转换**（主战场） |
| **L2 Mixin** | 三个模块共 200 个 mixin 文件（CO 135 / Magic-Lib 46 / TerraCurio 19） | **不参与机械转换**。以 **1.21 侧现有 mixin 为基准**（CO 184 / Magic-Lib 25 / TerraCurio 30，已经带 1.21 正确的目标），再用审计报告驱动的三分类逐文件处理 |
| **L3 平台胶水** | 网络/事件注册、datagen provider、`neoforge.mods.toml`、`build.gradle`、群系注入 mixin | **不机械转换**；以 1.21 侧现有实现为基准，人工合并 1.20 的功能性改动 |
| **L4 1.21 独有** | 184−135 的 mixin、虚空海/虫洞/晶塔/连枷长矛/Night's Edge/hook 模型/壁画预览/残影、PR #232 | **保留**（Phase 1 收割跟踪） |

**L2 的审计结论（工具：`tools/port2native/check_mixin_targets.py`，以 1.21 反编译源 + 本工程 1.21 侧源码为参照）**：

| 模块 | 文件 | 目标类存在 | 目标类缺失 | 方法点存在 | 方法点缺失 | λ风险 | 多目标 | UNKNOWN |
|---|---|---|---|---|---|---|---|---|
| ConfluenceOtherworld | 135 | 119 | 10 | 124 | 0 | 7 | 13 | 11 |
| Confluence-Magic-Lib | 46 | 42 | 1 | 51 | 3 | 0 | 2 | 1 |
| TerraCurio | 19 | 17 | 2 | 19 | 1 | 0 | 0 | 2 |
| **合计** | **200** | **178** | **13** | **194** | **4** | 7 | 15 | 14 |

→ **绝大多数 mixin 的目标在 1.21 仍然成立**（178/200 目标类、194/200 方法点），真正需要重设计的只有 13 个，且分五类：
1. **第三方模组内部类（5）**：Curios 3（`CuriosUtilMixinHooks`/`ClientEventHandler`/`SPacketSyncCurios`）+ geckolib 3（`loading.FileLoader`/`cache.object.GeoCube`/`KeyFramesLoadingAdapter`）——第三方 1.20/1.21 版包结构不同，需按 1.21 版重写。
2. **本工程自混入、1.21 改名/换位（4）**：`LibEntityUtils`（1.21 叫 `LibUtils`）、`LibKeyBindings`、`lib.mixin.chunk.LevelChunk`、`mixin.world.entity.LivingEntity`——人工点一下即可。
3. **加载器专有（2）**：`net.minecraftforge.client.model.ForgeItemModelShaper`、`net.minecraftforge.items.ItemStackHandler`——1.21 上删除或改用 NeoForge 机制（`net.neoforged.neoforge.items.ItemStackHandler`）。
4. **原版真删除（1）**：`net.minecraft.world.item.enchantment.ProtectionEnchantment`（1.21 附魔数据驱动化后不存在）——必须重新设计；1.21 侧很可能已有对应实现可参照。
5. **λ 风险（7）+ 多目标（15）+ UNKNOWN（14）**：lambda 编号在版本间不稳定（1.20 的 `build.gradle` 里那个 `patchRefmap` 任务就是为它存在的），必须逐个核对；UNKNOWN 属工具静态判不出来（继承来的方法、`@Slice`/`@At` 复杂组合）的部分。

**门槛（每次回移都必须过）**：`check_mixin_targets.py` 的 `TARGET-MISSING` / `LOADER-CLASS` / `METHOD-MISSING` 必须全部有处置结论，**否则不许进入编译阶段**——因为这些问题编译期发现不了，只在启动期炸。

### Phase 3 —— 逐提交移植（**不做整树替换**）

> 决策依据（本次新增）：**按 1.20.1 的分叉点逐一比对每次提交来移植**，而不是"整树跑一遍转换器"。
> 原因：分叉后 1.20 侧做了大量**类的位置变动**——实测 **609 处模块内改名/移动**（39 个提交），
> 以及 **37 个"删 java 文件 + 子模块指针跳变"的提交**（真正的另一半改动在子模块仓库里，git 认不出 rename）。
> 路径级整树对比会把这些当成"删除 + 新增"，直接丢掉对应关系；逐提交走 git，`-M -C` 能给出改名对应。

**输入：移植台账** `notes/PORT-LEDGER.md` + `notes/port-ledger.json`
（由 `python tools/port2native/commit_inventory.py --log <gitlog> --out notes` 生成，可重跑）

> ⚠️ **2026-09-27 修正（第二轮分诊的实测结论，执行前必读）**：
> 覆盖率工具（`commit_coverage.py`）对 Port 词汇免疫不了，导致 386 行里有 124 行「没有条目」——
> 改成 Port 免疫的**文件级欠账**度量（`file_lag.py`）后，欠账被量化：**1.21 完全没有对应文件的 java 有 781 个、
> 明显落后的 367 个，TODO 欠账权重合计 111929 行**。也就是说这不是「搬 PortLib 架构」的量级，
> 而是「1.20 分叉后新写了大约一半的内容层」（新生物架构 552 文件 vs 137、AttachmentEntity 召唤体系 141 文件全新增）。
> 因此**判定仍逐提交做（台账），执行改按依赖排序的工作包做**——见 `notes/WORK-QUEUE.md`（WP1 生物基座 → WP2 物种 →
> WP3 Boss → WP4 NPC → WP5 召唤/Attachment → WP6 物品武器 → WP7 datagen → 子模块旁支）。
> 偏离台账顺序的唯一理由是**跨提交的编译依赖**：新物种必须在基座之后才编得过；工作包内部仍严格按台账顺序、
> 仍以「一个 1.20 源提交 = 一个 1.21 提交」的粒度落账。

| 项 | 实测 |
|---|---|
| 范围 | `795ac9ccc..a75bda140`，**380 个非 merge 提交**，时间正序 = 移植顺序 |
| 含改名的提交 | 39 个 / **609 处**改名或复制 |
| 类别分布 | `content` 186、`content+submodule` 115、**`submodule-only` 52**、`assets/other` 17、`assets+submodule` 8、`platform` 2 |
| 子模块指针跳变 | PortLib **115**（按 Q2 不移植，属噪音）、**Magic-Lib 98**、**TerraCurio 85**、**TerraFurniture 35**、TerraEntity 5、TerraGuns 6（后两者按 Q3 退役） |
| 疑似"搬进子模块" | **37 个提交**单列（如 `dbac6afed`「part」删 98 个 java + 跳 5 个子模块） |

**单提交移植循环（7 步，逐条记账）**：

1. **看**：`git show -M -C <hash>`（在 `D:\Minecraft\1.20forge\confluence`），含改名对与子模块指针跳变。
2. **判**：
   - `platform` / 纯构建文件 / PortLib 指针跳变 → `SKIP-PLATFORM`
   - 1.21 侧已有等价实现 → `COVERED`
   - 有真实功能或内容改动 → 进入第 3 步
3. **定位 1.21 侧落点**：查台账"移动"列与移动族表；若属"搬进子模块"，去该子模块仓库按 `旧SHA..新SHA` 找对应子模块提交，再映射到 1.21 侧同一模块。**模块映射注意 Q3**：`TerraEntity`/`TerraGuns` 已退役，其 1.20 侧内容在 1.21 应落到主模组内联路径。
4. **应用**：同路径优先 `git cherry-pick -n <hash>`（冲突即人工裁决）；路径/API 不同则按 diff 手工落。Port 词汇用转换器**只跑这次提交碰到的文件**（`--only <路径>`），不做整树。
5. **Mixin 层按 L2 规则**：不机械转换；按 `check_mixin_targets.py` 的结论决定「搬 / 重写 / 删」，跨版本 λ 与多目标点人工核对。
6. **编译门槛**：`:模块:compileJava` 必须过；过不了就停下修，**不留"先合上后修"**。
7. **记账**：更新 `PORT-LEDGER.md` 的状态列（`PORTED`/`SKIP-PLATFORM`/`COVERED`/`MOVED`/`LOST?`）并填 1.21 侧提交 hash；1.21 侧提交信息统一写 `port of <1.20 hash>`，保持可追溯。

**台账使用要点（实测得出）**：
- **`part N` 是同步波**：同一个逻辑步骤在主仓库与各子模块里各有一个同名提交（如 row2 = 主仓库 `part2` + Magic-Lib `part2` + TerraCurio `part2` + TerraFurniture `part2`）。判断这类波时要把两边一起看，**波里常夹带真内容**（例：row1 的 TerraFurniture 侧夹了「木床」「添加蜡烛」两个内容提交）。
- **看区间净改动，不要只看提交列表**：指针区间里可能有"加了又删/改名"的提交（例：row1 的 TerraFurniture 区间里加过 `models/block/oak/{bed,candle}`，两侧现在都没有了）。`SUBMODULE-LINKS.md` 已带「区间净改动」列（文件数 +增/−删）。
- **状态记在 `notes/port-ledger-status.json`**（hash → status/target/note），台账重生成不会丢；状态取值 `TODO`/`PORTED`/`SKIP-PLATFORM`/`SKIP-1.21-KEEPS`/`COVERED`/`MOVED`/`LOST?`。
- **已完成的判定示例**：第 1 行 `dbac6afed`「part」= 删 98 个 integration 类 + 28 处拔集成调用 + `mods.toml`/`build.gradle` 平台改动 + 5 个子模块指针 → **`SKIP-1.21-KEEPS`**（integration 按 Q12 不碰；平台文件 1.21 有自己的形态）。

**批次与节奏**：严格按台账时间正序推进；以 `part` 阶段分批，每批收尾做一次 `runData` 对拍 + 冒烟；批与批之间 `neoforge-dev/1.21.1` 必须始终处于可编译状态（这样随时可发版、可中断）。

**模块对齐前提（Q3 已定）**：
- `settings.gradle` 只保留 `:ConfluenceOtherworld`、`:Confluence-Magic-Lib`、`:TerraCurio`、`:TerraFurniture`，**移除 `:TerraEntity`/`:TerraGuns`**；实体/枪械代码来自主模组内联版。
- 主模组里 1.21 独有的 `integration\terra_entity`(43) 与 `mixin\integration`(44) 中，TerraEntity 专有接线作废，承载真实内容的按 `notes/ENTITY-MODULE-COVERAGE.md` 逐类判定。
- 子模块仓库本身保留给其他使用方，但其 `neoforge-dev/1.21.1` 分支对 Confluence 不再有输入/输出关系。

**平台适配（转换器覆盖不到，逐提交遇到就处理）**：Java 21 + `net.neoforged.moddev` 2.0.140、依赖版本（geckolib/curios/JEI/ParticleStorm/ThreeDimensionParticle/TheTrackers）、datagen provider API、`neoforge.mods.toml`、`runData` 的 `--existing-mod` 参数、CI 的 Java 版本与 `runData` 步骤。

**跨仓库对应（已工具化）**：`tools/port2native/submodule_link_table.py` → `notes/SUBMODULE-LINKS.md`
把每个主仓库提交映射到「子模块 + 旧SHA..新SHA + 子模块侧提交列表」；台账工具的 `--submodule-links`
会把结果标注到每行（实测 **166 行**带子模块侧提交）。实测：

| 子模块 | 指针跳变 | 子模块侧提交 |
|---|---|---|
| PortLib | 116 | 115（按 Q2 不移植） |
| Confluence-Magic-Lib | 99 | 98 |
| TerraCurio | 83 | 83 |
| TerraFurniture | 34 | 34 |
| TerraEntity / TerraGuns | 5 / 6 | 0 / 0（按 Q3 退役） |

**范例（为什么必须逐提交 + 跨仓库）**：「饰品的药水效果转移至 lib」在 1.20 侧被拆成 **3 个主仓库提交
（`1c012ccb1`、`100f6e0f2`、`2a4dfce2c`）+ 4 个子模块提交**（Magic-Lib `9de76c9df`/`0718c593a`/
`2c66dc065`、TerraCurio `ddfcd27e0`/`b38d16d66`、TerraFurniture `f6a10ca45`）。真正的搬迁内容在
Magic-Lib `0718c59`（新增 `GravitationEffect`/`HoneyEffect`/`LibEffects`/`GravitationHandler`/
`ILibEntity` + 若干 mixin，并把 `LibEffects`、`LibGameEvents` 拆位改名）；**主仓库那侧只有调用点更新
+ 一行指针跳变，只看主仓库 diff 会完全错过这次搬迁**。

**已知阻塞**：9 个子模块指针区间（Magic-Lib 4 + TerraFurniture 5）因本地子模块仓库缺少较新对象读不出
子模块提交，需先 `git -C <子模块> fetch --all`（或 `git submodule update --init --recursive`，后者会
改动 1.20 工作副本的子模块检出点，需本人确认）。

### Phase 4 —— 验证
1. **编译门槛**：每个模块 `compileJava` 必须过（CI 强制）。
2. **红线验收**：按附录 B2 的清单逐项确认 1.21 侧内容存在（虚空海/虫洞/晶塔/连枷长矛/Night's Edge/hook 模型/壁画预览/残影/PR #232/9 组行为修复）。
3. **数据生成对拍**：1.20 与 1.21 的 `runData` 产物 diff 分类（原版版本差异纳入白名单）。
4. **运行时**：`runClient` 三端（client/client1/client2）与 `runServer`；重点回归：属性同步、数据组件、attachment、网络包、datamap、配置界面、群系注入。
5. **存档兼容**：1.20 侧用 PortLib 以 NBT/capability 模拟数据组件，1.21 是原生实现——**同一份内容在两端的数据落盘形态不同**，需确认各自存档各自能加载（不要试图跨端通用存档）。
6. **边界检查**：确认 1.21 侧**没有任何 PortLib 残留**（`org.mesdag.portlib` / `IPortPacket` / `PortDeferred*` 零命中），也确认 1.20 侧没有被反向写入原生 API 之外的东西。
7. **移除内容公告（Q9）**：1.21 的 CHANGELOG / 发布说明必须带上《移除内容清单》（引用 `notes/DROPPED-FROM-TERRA-FAMILY.md` §1–§4），避免玩家把"星尘龙/法杖/手办方块消失、JEI 条目消失"当成 bug 上报；若旧存档出现"未知物品"再决定是否加数据修复。

### Phase 5 —— 稳态：定期整体回移流程（Q6/Q7 已定）
1. **触发与频率（Q6）**：**每次 1.20 minor 版本发布后回移一次**；紧急 hotfix 例外（1.20 修好 → 立刻跑一轮回移）。回移点用 1.20 侧 tag 固定。
2. **标准流程（Q10 已定：逐提交，不做整树）**：定回移起点 `1.20 tag` → 生成/更新 `notes/PORT-LEDGER.md` → 按台账正序逐提交走 Phase 3 的 7 步循环（`git show -M -C` → 判类别 → 定位 1.21 落点 → cherry-pick/手工落 + 转换器只跑本次涉及的文件 → Mixin 按 L2 规则 → 编译门槛 → 记账）→ 每批 `runData` 对拍 + `runClient` 冒烟 → 发版。Mixin 审计（`check_mixin_targets.py`）在编译门槛之前跑。
3. **禁止反向开发**：1.21 分支不再接受功能开发提交（只接受回移产物与 hotfix）；hotfix 必须先落在 1.20，再随下一轮回移过去（否则重新产生分叉）。
4. **版本口径（Q7）**：以 1.20 的 `mod_version` 为准，1.21 产物保持现有格式 `mod_version + "+" + minecraft_version + "-yyMMdd"`；CHANGELOG 维护"1.21 版本 ↔ 1.20 版本"对应表。PortLib 版本只在 1.20 侧有意义（`portlib_version`），1.21 侧不再出现该依赖。
5. **漂移看板**：CI 定期跑度量脚本，输出"距上次回移的 1.20 提交数 / 文件数 / 差异行数"。
6. **责任人**：回移与 1.21 发版固定一人负责（待指定）；nightly 只在"分支暂时不可编译"的窗口暂停（逐提交推进的正常状态下分支始终可编译，不需要停）。

---

## 4. 工作分解与估算（粗估，需讨论后细化）

| 阶段 | 任务 | 规模 | 依赖 | 风险 |
|---|---|---|---|---|
| P0 | WIP 隔离/基线/度量脚本 | 小 | — | 低（注意现存未提交改动） |
| P1 | **收割：1.21 独有内容并入 1.20** | **大**：196 提交里的内容簇（336 java + 920 资源中的内容部分）+ 红线 20+ 类，逐项移植到 Port 词汇 | P0 | **高：漏项即永久丢失** |
| P2 | **Port→原生 转换器** | 中：444 条 FACADE 映射 + 155 条排除规则 + 人工裁决清单；规则表 + 脚本 | B1 清单（已完成） | 中：规则不全 → 编译错误可见 |
| P3 | **1.21 分支重建 + 编译收敛** | 大：主模组 2496 + 库模块 586 文件过转换器 + 平台适配 | P2、Q3/Q5 决策 | 中高：静默语义改变 |
| P4 | 验证（红线/对拍/冒烟/存档/无残留） | 中 | P3 | 中 |
| P5 | 稳态流程、漂移看板、回移演练 | 小 | P4 | 低 |

---

## 5. 风险登记

| # | 风险 | 概率 | 影响 | 对策 |
|---|---|---|---|---|
| R1 | **收割漏项**：1.21 独有内容（虚空海/晶塔代码/连枷长矛/PR #232…）未并入 1.20 就被冻结 → 永久丢失 | 高 | 高 | 以附录 B2 的逐簇哈希清单为验收依据，产出 `notes/HARVEST-CHECKLIST.md` 逐项勾选，禁止只按文件树 diff 判断 |
| R2 | **转换器规则覆盖不全**导致静默语义改变（"能编译但行为不同"） | 高 | 高 | 未覆盖清单必须为空；人工裁决点强制成 TODO 报告；每轮 runData 对拍 + runClient 冒烟 |
| R3 | Port 词汇里的 **1.20 模拟语义**被误译成 1.21 原生（数据组件/属性/附件） | 中 | 高 | 按 B1-R5/R6 逐项处理；两端存档分别验证（**不追求跨端存档通用**） |
| R4 | 1.21 分支不再独立开发后，**1.21 用户 bug 的修复路径变长**（hotfix 必须回 1.20 再回移） | 中 | 中 | 明确 hotfix 流程（1.20 修 → 触发生成一次回移）；缩短回移周期 |
| R5 | 回移期间 1.20 继续前进 → 基线漂移，转换结果与测试对不上 | 高 | 中 | 回移点用 1.20 侧 tag 固定；漂移看板监控；冻结窗口内不改 1.20 |
| R6 | **平台适配层**（依赖 1.21 版本、Java 21、datagen provider API、群系注入、`neoforge.mods.toml`）无法被转换器覆盖 | 中 | 中 | 单列手工清单，按模块逐条过 |
| R7 | **子模块**（Magic-Lib/TerraCurio/TerraFurniture/TerraEntity/TerraGuns）的 `neoforge-dev/1.21.1` 分支若继续独立演进，会在子模块层面重新长出一条双线 | 中 | 高 | Q3/Q4 决策明确处置（并入 1.20 / 退役 / 冻结）；子模块按同一套回移流程走 |
| R8 | 漏掉 1.21 侧的 **merge 提交**（含贡献者 PR #232 `6e9102d84`）与子模块 gitlink 推进 | 中 | 高 | 按附录 B2 的逐簇哈希清单验收 |
| R9 | 转换器把平台专有写法翻错（`PortTriState` vs `boolean`、`PortRenderLivingEvent` 类型参数个数、`PortDeltaTicker`、`PortArmorItem.PortType`） | 中 | 中 | 按 B1-R7/R8/R9 列为人工裁决点，不自动改写 |
| R10 | 基线漂移：本计划分叉后提交数取自已更新的 forge clone（383），部分重合度统计基于较旧 ref（346） | 高 | 中 | **执行前用最新 forge 头重跑附录 A 的度量脚本** |
| R11 | **刻意丢弃的功能日后被要求找回**（动态模糊/拖尾/镜头震动/聊天气泡/12 个数据包注册表/交易 SPI/TerraGuns 附魔与 ICA 动画） | 中 | 中 | Q4 已决定全部不收割；必须产出并维护 `notes/DROPPED-FROM-TERRA-FAMILY.md`，明确"若要恢复，按 1.20 新 API 重做" |
| R12 | 1.21 侧 TerraEntity 集成文件（37 + 10）在替换时被机械删除，连"该内容是否需要"都没判定 | 中 | 中 | Q4+Q9 已给出判定口径（1.20 已有→零工作；1.20 缺失→丢弃）；替换前把三分类表落盘备查 |
| R13 | **移除内容引发的玩家侧问题**：1.21 玩家旧存档里已存在的星尘龙/法杖/手办方块变成"未知物品"或消失，可能被视为 bug | 中 | 中 | Phase 4 第 7 条：发布说明附《移除内容清单》并说明原因（架构统一）；必要时再做数据修复 |
| R14 | **把 Mixin 层塞进机械转换** → 启动期 `MixinApplyError` / 注入点找不到（编译期看不出来，只在启动期炸） | 高 | 高 | 分层策略 L2：Mixin 不走转换器；以 1.21 现有 mixin 为基准 + `check_mixin_targets.py` 审计驱动三分类；审计未清空不许进入编译阶段 |
| R15 | Mixin 层**双向不对称**被忽略：CO 1.20=135 / 1.21=184，Magic-Lib 1.20=46 / **1.21=25**（1.20 反而多 21），TerraCurio 19/30 | 中 | 中 | 逐模块分别对账；1.20 多出来的（如 Magic-Lib 的 21 个）要逐个判定是"1.20 专有的兼容/模拟用途（删）"还是"1.21 缺的功能（收割）"；1.21 多出来的（CO 49 个）进 Phase 1 保留清单 |
| R16 | **污染残留传播**：`dfcc5c041`（1690 文件、大部分内容错误、部分被回滚）的残留改动仍留在当前 1.20 树里（992 文件 / 172 java），142 个后续提交触碰它们——照抄就会把错误逻辑搬进 1.21 | 高 | 高 | Q14：该提交永不移植；台账「污染残留」列逐行标注；涉残留行优先以 1.21 侧现有实现为准；必要时对照回滚提交（`dc57ba5c2`/`4af532ed1` 等）之后的状态 |

---

## 6. 里程碑与验收

| 里程碑 | 验收标准 |
|---|---|
| M1 收割完成 | `notes/HARVEST-CHECKLIST.md` 全部项在 1.20 侧落地并各自验证过；1.20 树成为 1.21 的超集（附录 B2 红线清单零遗漏） |
| M2 转换器可用 | 引擎与台账工具已落地（`tools/port2native/`：`port2native.py`、`check_mixin_targets.py`、`commit_inventory.py`）；全量规则到位后对单个提交/单模块跑通；`uncovered.json` 为空；`manual-todo.md` 裁决完 |
| M2b 台账可用 | `notes/PORT-LEDGER.md` 生成（380 提交、609 处移动、37 个搬进子模块的候选）+ `submodule_link_table.py` 能把子模块侧提交对上号 |
| M3 1.21 编译通过 | 库模块 + 主模组 `compileJava` 全绿（M3a 库模块 / M3b 主模组） |
| M4 1.21 运行验证 | `runData` 对拍完成（差异已分类）；`runClient` 三端进游戏；红线清单逐项验收；PortLib 零残留 |
| M5 回移演练 | 第二轮回移（1.20 新增若干提交后）的耗时与人工量被记录，流程可重复 |
| M6 稳态 | 漂移看板上线；hotfix 流程成文；1.21 分支只接受回移产物 |

---

## 7. 讨论结论

**已决（本轮）**：

| # | 议题 | 结论 |
|---|---|---|
| Q1 | 总路线 | **方案 C**：冻结 1.21，单线开发 1.20，定期整体回移 |
| Q2 | 1.21 侧形态 | **方案 B**：1.21 去 Port 化，写 NeoForge 原生 API；1.21 侧不引入 PortLib |
| Q3 | 实体与子模块 | **与 1.20 一致**：实体/枪械内联进 `ConfluenceOtherworld`；`TerraEntity`/`TerraGuns` 在 Confluence 构建中退役（仓库保留给其他使用方） |
| Q5 | 分支与历史 | **直接在 `neoforge-dev/1.21.1` 上做整树替换提交**（先临时分支验证，替换前打 tag） |
| Q6 | 回移节奏 | **每次 1.20 minor 版本发布后回移一次 + 紧急 hotfix 例外**；门槛 = 编译 + runData 对拍 + runClient 冒烟 |
| Q7 | 版本口径 | **以 1.20 版本线为准**，1.21 产物只加平台/日期后缀；CHANGELOG 维护对应关系 |
| Q8 | 世界生成 | **注入器以 1.20 为唯一实现**：先收尾并提交 1.21 的 WIP，再逐处判定（平台适配 / 逻辑改进）后把改进收割回 1.20 |
| Q4 | 收割范围 | **TerraEntity/TerraGuns 的东西一律不收割**；1.21 侧一切按 1.20.1 的新 API/新实现来；丢弃项写入 `notes/DROPPED-FROM-TERRA-FAMILY.md`；`integration\terra_entity`(43) 与 14 个 TE mixin 在替换前先做三分类列表 |
| Q9 | 内容小缺口 | **全部丢弃，1.20 的内容即最终内容**（星尘龙 + 7+1 物品 + `HouseDetectItem` + `FigureBlock` + 11 个 JEI drawer/NPC 分类），并在 1.21 发布说明里列出移除清单 |

**Q4+Q9 合起来把 R12 的判定简化成一句话**：47 个集成文件里，**1.20 已有等价实现 → 零工作**（12 个交易 lock ≈ `trade\conditions\*`、7 个金钱交易 ≈ `NPCTradeOffer`/`NPCTradeMenu`、10 个内容 mixin 对应的 NPC/怪物在 1.20 都是原生实现、物品组件钩子 ≈ `ModDataComponentTypes`）；**1.20 缺失 → 丢弃**（11 个 recipe drawer、4 个注册表注册、`TerraSwordTrail`、`DynamicAnglerTradeTaskMixin`）。替换前只需把这份三分类表落盘备查，不需要再逐项做技术判定。

**待讨论（仅剩操作性事项）**：

- **责任人**：回移与 1.21 发版由谁负责（Q6 提到"责任人固定一人"，需要具体人名/角色）。
- **1.21 是否继续发 nightly**：`nightly-ci.yml` 的 ref 指向 `neoforge-dev/1.21.1`；整树替换期间是暂停 nightly，还是等替换完成后再恢复。

---

## 8. 附录 A：证据与命令（可复现）

```powershell
# 1) 分支与分叉点
git -C D:\Minecraft\1.20forge\confluence rev-list --left-right --count c387b9010...forge-dev/1.20.1   # 196  383
git -C D:\Minecraft\1.20forge\confluence log -1 --date=iso --pretty="%ad %h %s" (git -C D:\Minecraft\1.20forge\confluence merge-base c387b9010 forge-dev/1.20.1)  # 795ac9ccc 2026-05-31

# 2) PortLib 使用面（1.20 侧）
Get-ChildItem D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src -Recurse -Filter *.java |
  Select-String -Pattern "org\.mesdag\.portlib" -List | Measure-Object      # 474 / 2496

# 3) 共同文件差异直方图（剥离 portlib import 后逐行比较）
#    见 Phase 0 固化的 measure-divergence.ps1；结果：317/248/433/264/167/66（共 1495）

# 4) PortLib 两个分支的类集合差异
git -C D:\Minecraft\1.20forge\confluence\PortLib merge-base --all origin/neoforge/1.21.1 forge-dev/1.20.1   # 空 => 无共同祖先
git -C D:\Minecraft\1.20forge\confluence\PortLib rev-list --left-right --count origin/neoforge/1.21.1...forge-dev/1.20.1  # 58  177

# 5) 主模组实际用到的 PortLib 类型
#    159 个去重类型；其中 87 在 1.21 PortLib 分支已有，72 缺失
```

---

## 9. 附录 B：补充分析

### B1 PortLib API 分类清单（已完成）

完整清单：`notes/PORTLIB_API_INVENTORY.md`（89 KB，逐类表 + 原生映射 + 行号引用；核对基准是本机解包的 NeoForge 21.1.219 / MC 1.21.1 源码）。结论摘要：

| 分类 | 文件数 | 占比 | 处理方式 |
|---|---|---|---|
| `FACADE`（门面委托） | **444** | 70.8% | 在 1.21 上重写为对 NeoForge 原生的薄委托（多数 10–40 行） |
| `EMULATION-ONLY`（纯 1.20 模拟） | **155** | 24.7% | **1.21 侧不实现**：112 个 `diff\mixin\*`、`diff\action\*`、`diff\datagen\*`、coremod 脚本、`PortLib.java` 的注册段、`PortTags`/`PortSoundEvents` 等 |
| `SHARED-PLAIN`（可直接复制） | **20** | 3.2% | `util\*`（11）+ 少量目录无关常量 |
| `UNKNOWN`（需人工裁决） | **8** | 1.3% | 见清单内的逐条说明 |

→ **1.21 原生 PortLib 预计需要 ≈464–472 个文件**（今天的 74–75%），新增代码量约 **6–9k 行 + 约 3k 行机械别名**；同时可删除 112 个 mixin、`coremods.json`、两个 coremod JS、`portlib.mixins.json`、`accesstransformer.cfg`（1.20 SRG 之名，随 mixin 一起消失）以及 `mixinextras` 依赖。`portlib-interfaceinjection.json` **保留**（82 条注入映射），只是重定向到 1.21 目标类。

主要结构性风险（实现前必须读的 11 条，摘录）：
- **R1 事件不能"薄委托"**：NeoForge 只 fire 它自己的事件类，而 Mod 代码按 `Port*Event` 名字监听 → 每个 `Port*Event` 都要由 PortLib 重新 post（`event\PortEventHooks.java`）。`PortEvent`/`PortEventHooks`/`PortEventHandler`/`PortBus` 必须保留。
- **R2/R3 名字与包路径并不只是加前缀**：`PortFinalizeSpawnEvent`↔`FinalizeSpawnEvent`、`PortSpawnClusterSizeEvent`↔`SpawnClusterSizeEvent`、`PortBlockGrowFeatureEvent`↔`BlockGrowFeatureEvent`…；14 个嵌套枚举名被 Mod 源码逐字引用（如 `PortMobEffectEvent.Applicable.PortResult.DO_NOT_APPLY`）；21 个事件在 1.20 位于 `event.other` 而原生在 `event.*`；通配符 import 使包路径成为"承重墙"。
- **R4 好消息**：Mod 全仓（2496 文件）**没有** `@SubscribeEvent`/`@EventBusSubscriber`，170+ 监听器都走 `PortEventHandler.addListener`，与 `IEventBus#addListener` 1:1 对应。隐患：`postEvent` 用 `instanceof IPortModBusEvent` + 调用方 modid 选总线，**62 个客户端事件里有 16 个缺 marker**，会 post 到错误总线而静默不触发（现存缺陷，建议顺手修）。
- **R5/R6 语义差异**：`ResourceLocation` 优先 vs `ResourceKey` 优先；数据组件在 1.20 是 NBT/capability 模拟（`portlib:data_components`），附件方法名也不同（`getAttach`↔`getData`、`syncAttach`↔`syncData`）。→ 印证 Q7：1.21 实现必须一律直通原生，模拟语义只留在 forge 线。
- **R7 两处硬编译冲突**：`PortCalculatePlayerTurnEvent` 的 `PortTriState` vs 原生 `boolean`；`PortRenderLivingEvent` 3 个类型参数 vs 原生 2 个。
- **R8 易猜错的重命名**：`PortConfigSpec`→`ModConfigSpec`、`PortWidgetSprites`→`WidgetSprites`、`PortRegistries`→`NeoForgeRegistries`、`PortDeltaTicker`→`DeltaTracker`（**不是** `RenderTickCounter`）、`PortTriState`→`neoforged…common.util.TriState`（**不是**原版 `net.minecraft.util.TriState`，后者是 1.21.2+）。
- **R9 版本漂移**：`PortArmorItem.PortType`（`ArmorType` 是 1.21.2+，1.21.1 的 `ArmorMaterial` 是 record）、`IPortAttributeExtension.Sentiment`（1.21.2 特性）等，需要按 1.21.1 单独裁决。
- **附带缺陷发现**：`coremods\portlib_extension_insert.js` 里 `'block_entity_renderer'` 键重复声明，第 5 行引用的 `IPortBlockRendererExtension` 并不存在 → 死代码被重复键静默覆盖。

### B2 1.21 侧 196 提交分类清单（已完成）

完整清单：`notes/1.21-BRANCH-DIVERGENCE.md`（43 KB，逐簇提交哈希与路径清单）。量级：**196 个提交（192 普通 + 4 merge，含贡献者 PR #232）**，涉及 **1275 个文件（+31656 / −8806）**，其中 java 只有 **336 个**、920 个是 `ConfluenceOtherworld/src/main/resources` 下的资源。

| 簇 | 提交数 | java 文件 | 平台性/内容性 | 难度 |
|---|---|---|---|---|
| C1 加载器/依赖/构建管线 | 19 | 33 | 平台 | 低（多数被 PortLib 取代） |
| C2 移除 TerraBlender | 1 | 35 | 平台 + 世界生成架构 | 低（本侧）/高（若重复实现） |
| C3 datagen 管线 | 11 | 32（+117 json） | 混合 | **高** |
| C4 世界生成特征/结构 | 14 | 32 | 内容 | 中（91% 已在 forge 侧） |
| C5 **战斗：连枷/长矛/剑/枪** | **28** | **76** | 内容 | **高（最大簇）** |
| C6 实体/NPC | 15 | 8 | 内容（多为子模块 gitlink） | 中 |
| C7 方块：苔藓/植物/矿 | 14 | 22（+328 资源） | 内容 | 中 |
| C8 盔甲/时装 | 10 | 12 | 内容 | 低–中（java 100% 已在 forge 侧） |
| C9 **客户端渲染/HUD/hook** | 18 | **92** | 内容 + 客户端架构 | **高（与 forge 侧仅 51% 重合）** |
| C10 粒子/VFX | 7 | 4 | 混合（实体在 Magic-Lib） | 中 |
| C11 晶塔(Pylon) | 3 | 13 | 内容 + 平台 | 中–高（forge 侧只有资源、**没有代码**） |
| C12 重构/清理 | 11 | 89 | 平台邻近 | 低价值/中噪音 |
| C13 i18n | 3 | 3 | 内容 | 低（按键合并） |
| C14 纯美术 | 12 | 6 | 内容 | 低 |
| C15 TerraCurio 模型 | 12 | 12 | 子模块 | 低/中 |
| C16 文档/TODO | 4 | 0 | — | 可忽略 |
| C17 杂项修复 | 10 | 21 | 混合 | 低–中（含真实崩溃修复，**不要盲抄**） |
| C18 merge | 4 | — | PR #232 补丁 | 低但**极易漏掉** |

**Port 化后自然消失的平台性工作**（不要重复移植）：TerraBlender 集成的全部管道（`9d93c8bfc`/`2f04cc8f5`：依赖、`offline`/mixinsquared 分支、citadel/lithium、6 个 mixin/plugin、`[[mixins]]` 块）——forge 侧已有自己的 `BiomeRegion*` 注入器；legendarytooltips/itemborders 兼容（`4b4078583`/`3cb065b25`）；datatip 整条线（6 个提交，forge 侧无 datatip，PortLib 接管该层）；waystone 集成（`14e1a6b4c`）；纯版本号提升（10 个提交）；fastutil map 替换（`8da5101c8`）；命名空间重命名（`d851e0ae1`）；旧 HUD 层删除（`96a03e0df`）。

**必须保留的 1.21 独有内容**（forge 侧**完全没有**，属"零丢失"红线）：
1. **虚空海 Void Sea**：`common/util/{VoidSeaHelper,VoidSeaConstants}`、`client/renderer/{VoidSeaRenderer,VoidSeaRenderSettings,VoidSeaIrisCompat,ModRenderTypes,ModRenderStateShards}`、`client/gui/VoidSeaFilterRenderer`、`client/effect/VoidSeaSwimEffects`、`client/particle/VoidSeaSuspendedParticle`、`DimensionalOverlapEffect`、`mixed/IPlayer`、3 个 mixin、4 个 shader。
2. **虫洞药水 + 界面**：`WormholePotionItem`、`WormholeScreen`、`WormholeHandler(Client)`、2 个网络包。
3. **晶塔代码**：`BasePylonBlock`、`PylonBlocks`、`BasePylonModel`（forge 侧有资源无代码 → **重复实现风险**）。
4. **连枷/锁链families**（~7 物品 + 2 实体 + 4 抛射物 + 组件 + 2 包 + 4 渲染器 + ~110 资源）。
5. **长矛 Builder 重构**（`3fa4715e4`/`f1bf62836`；`SpearProjectileComponent` 已废弃，**不要复活**）。
6. **Night's Edge**（抛射物 + 790 行动画 + 3 粒子定义）。
7. **20 个 hook 模型** + `SimpleHookRenderer` + `HookThrowingHandler`。
8. **壁画放置预览**（`MuralBlock` 310 行重写 + `MuralBlockModel` + `MuralPlacementPreviewRenderer`）。
9. **残影模块**（`AfterimageHelper`/`AfterimageStyle`/`ModArmorBonus`，生物侧在 TerraEntity）。
10. 苔藓/自然方块 + 4 张转化表 + 328 资源；盔甲/时装；世界生成内容数据（6 个 region 类、`SurfaceRuleData`、`TheEndBiomeHolder`、`ModBiomes`、`BaobabTreeFeature` 等，**只移植数据、不移植机械**）；datagen 内容数据；i18n。
11. 需要**有意**移植的行为修复：`f481e777b`（不同步抖动）、`6f7093e23`（移除客户端守卫，真正修复）、`f5b5863aa`、`22f7b05cf`、`43459d956`、`04004d5d8`/`f8b6f87f0`（交易 GUI）、`c25a6308c`、`d3e20bbba`/`a272c601c`。

**构建/依赖差异**（分叉点至今）：`gradle.properties` 键名 `particle_storm_version`→`particlestorm_version`，`particlestorm 1.3.0→1.4.3.1`、`thr_dim_particle 1.1.1→1.2.0`、`the_trackers 0.2.5→0.2.6`；⚠️ `terrablender_version=4.1.0.8` 仍在但已无消费者（应删）；`ConfluenceOtherworld/gradle.properties` 的 `mod_version` 被**从 1.3.0 降到 1.2.5** 再到 1.2.6（刻意的版本线重置，Q6 需对齐）；`ConfluenceOtherworld/build.gradle` 改动 70 行（去 TerraBlender/citadel/lithium/waystones/balm/legendary/itemborders，geckolib 不再 jarJar，新增 datatip，Create 运行时开关由 `if(false)`→`if(true)`）。**构建文件不可 merge，应以重构后的树为准重写，只取上述差异。**

⚠️ **两点必须注意**：
1. 本附录中"forge 侧已有"的比例基于 `origin/forge-dev/1.20.1 @ 55aebf2f9`（346 个分叉后提交），而 §1 的 383 个提交基线取自已更新的 1.20forge clone（`a75bda140`）。**执行前需用最新的 forge 头重跑重合度测量**（相差 37 个提交，重合百分比会变）。
2. 5 个子模块 gitlink 在区间内推进（Confluence-Magic-Lib、TerraCurio、TerraEntity、TerraFurniture、TerraGuns），C6/C10/C15 的真实代码在这些仓库里，评估时不可只算主仓库。

### B3 TerraEntity / TerraGuns 退役的功能覆盖度（已完成）

完整清单：`notes/ENTITY-MODULE-COVERAGE.md`（84 KB，783 行，逐类判定 + 行号引用）。**这是 Q4 的主要输入，也修正了 §1 的一个前提**。

1. **重要发现：1.20 侧曾经内联过 TerraEntity，然后刻意删除并从零重写**：
   - `f6e114cdb`(part21, 2026-06-24) 把 TerraEntity 框架内联进 `ConfluenceOtherworld`（189 文件 +8115 行）→ `2569be361`(part22) 迁移到 `util/entity/…` → **`b20c0cefd`「remove all entity part」一次性删除 250 文件 / −28744 行** → `7f83b379a`(part23) 起用行为树**重新实现**。
   - 因此"1.20 里没有同名类"**不等于**"功能缺失"，但也不是全都有：现在的 1.20 树是一次**独立重写**。
   - **便宜的收割源**：`2569be361`（= `b20c0cefd^`）保留了 244 个被删文件，其中 162 个 basename 与 TerraEntity 相同，且包名已改成 `org.confluence.mod.*`——比从 TerraEntity(1.21) 移植更省事。

2. **覆盖度数字**：

   | 对象 | 同 basename 命中 | 历史可恢复 | 仅改名/换位 | **真缺失** | 纯库基础设施 |
   |---|---|---|---|---|---|
   | TerraEntity（860 java） | 182 | 162 | ≈300 | **≈200** | ≈16 |
   | TerraGuns（99 java） | 62 | — | 48 | **4** | 1 |

3. **内容不是问题**：语言键对比显示实体只缺 1 个（`stardust_dragon`）、物品缺几组（`stardust_dragon_staff`、6 个 `summon_*_sword_staff`、`wallet`、`house_detector`）；**20 个 BOSS 全部覆盖**（1.20 还多 4 个）；怪物/动物/坐骑/抛射物/效果/附魔 1.20 ⊇ 1.21。

4. **真正的缺口（玩家可见）**：
   - **客户端动画器层**：`client\animation\**`(11)、`AnimatorModel`/`AnimatorRenderer`、`DefaultBoneBoundIdents`、`ShaderUtil`、`Easing`、`SmoothFloat`、`KeyframeAnimationCounter`（关键帧/烘焙器/插值/运动曲线/IK/状态机本体在 1.20 历史里可恢复）。
   - **动态模糊**：9 文件 + 7 个 shader program，**1.20 所有 ref 都没有**（`AfterimageHelper` 不是替代品）。
   - **拖尾**：10 文件（`ITrail`、`SwordTrail`、`PlayerSwordTrailRenderer`、`ItemInHandTrailAttachment`）。
   - **镜头震动**：`CameraShakeManager`/`CameraShakeData`/`SyncCameraShakePacket` + `BossSpawnCameraManager`。
   - **聊天/气泡**：25 文件（`ChatArranger`、`ToTypeChat`(NPC↔NPC)、元素/条件 provider、`client\gui\renderer\chat\**`、`NPCChatBubbleBuffer`）。
   - **TerraGuns**：`TaczAnimationConstraint`（197 行 ICA 数学，**五个模块全局 grep 无命中**）、`GunEnchantmentService`/`TGEnchantments` + 3 个附魔 json/tag/component、`EmergencyMeleePacketC2S`、`PUT_AWAY` 举起 mixin、`EJECT_SHELL`，以及 **3 个单元测试**（1.20 侧根本没有 `src\test`）。
   - 其它：`EffectStrategy`(8)、`Chester` 注册表(4)、`DriveAwaySystem`(5)、`FigureBlock`(3)、BT decoration 节点 + `BTServer` 查看器、`CrossBowAttackOnCooldownBrain`、4 个 AI 事件类。

5. **架构扩展点缺口（最容易争论的部分）**：TerraEntity 的 **14 个自定义数据包注册表中有 12 个在 1.20 无对应物**（`effect_strategy*`、`trade_provider/task/lock/generator/modifier`、`chester_type`、`chat_element`、`chat_condition`、`mapped_data_type`），1.20 只有 3 个（`track_type_provider`、`generation_provider`、`trade_conditions`）+ 2 个召唤相关；交易 **SPI 46 文件**、8 个 lock recipe drawer、JEI NPC 分类、`BitMask`/`TradeParams`/`TradeModifiers` 缺失，但 1.20 的 27 个交易条件**是 1.21 侧 9 个 lock 的超集**（即**功能在、扩展点与"任务/修饰符/生成器"机制不在**）；mapped data 19 文件在 1.20 是 `ModDataMaps` 的 vanilla 风格实现。

6. **集成文件判定**：43 个 `integration\terra_entity` 中 6 个是纯模块接线（随模块消失），**37 个承载内容必须存活**（12 个交易 lock、11 个 recipe drawer【缺】、7 个金钱交易类、4 个注册表注册【缺】、`TerraSwordTrail`、1 个物品组件钩子）；58 个 `mixin\integration` 中 **44 个与 TerraEntity 无关**（sodium/curios/jei/jade/terracurio/create/ftbchunks…），14 个在 `terraentity\` 下：12 个消失，**10 个承载真实内容**（Angler/Mechanic/TravelingMerchant、DemonEye、DungeonGuardian、GoldenSlime、Skeletron、TEAnimals、SpawnPlacementChecks、`DynamicAnglerTradeTaskMixin`）。

7. **子模块在分叉后的提交**：TerraEntity 20 个提交基本是资源 churn，真实代码约 35 行（无新实体/BOSS/NPC/AI/动画特性）；**TerraGuns 15 个提交则很实**——`ba9feda`(2026-08-04, 82 java / 174 文件，**枪械整体重写**)、`76219e7`(手枪手部动画 28 java)、`3251d02`(17 java, 3 个附魔)、`d845e2a`(`PUT_AWAY`)、`31050e0`(datagen 重做)、`58f140f`(1.4.5.7 平衡)。1.20 侧独立重建了**结构**（`dfcc5c041`/`90dfd7804`/`9bc04295b`/`ce1ca67f8`），但没做附魔/ICA/动画。

8. **报告自陈的不确定项**：未做编译/运行验证；`(hist)` 分类只按行数比对（`InverseKinematics3D` 82 行差异、`Vec3KeyframeAnimation` 69 行需人工复核）；未 diff 约 1103 个 TerraEntity 资源文件；`UpdateBlackboardPacket`、`DeathAnimOptions`、以及"1.20 的行为树对具体某个 BOSS 是否功能完备"仍为 UNVERIFIED。
