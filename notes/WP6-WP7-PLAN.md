# WP6 / WP7 剩余批次实测与执行顺序（2026-09-29 第 22 轮）

> 承接 `notes/WORK-QUEUE.md` 第 22 轮的进度盘点：BOSS 批、客户端渲染接线、WP5 召唤体系
> 都已落地（`d5d6e8520` / `4fa5170bf` / `f64d44ce2`），注册层已无缺口
> （`MonsterEntities` 213、`BossEntities` 31、`CritterEntities` 41、`NpcEntities` 36）。
> 本文是**物品层与数据生成**这两块的实测账，度量方式同 `notes/WP2-REMAINDER-GATE.md`：
> 从切片种子出发按「模块内部 import」递归求闭包，再减去 1.21 已有的文件。

## 一、实测数字

| 切片 | 种子文件 | 内部闭包 | 1.21 缺 | 缺行数 | 缺口分布 |
|---|---:|---:|---:|---:|---|
| **WP6-A 鞭子 + 悠悠球** | 38 | 859 | **90** | 7540 | `common/item/{yoyo 12, whip 7, curve 6}`、`api/whip{,+curve} 12`、`common/entity/yoyo 7`、`common/init/*` 3、`common/component` 3、`util/generation{,/variant}` 4、`client/renderer/entity/yoyo` 3 等 40 个目录 |
| **WP6-B 弓 + 箭 + 剑** | 70 | 70 | **51** | 2724 | `common/entity/projectile/arrow 14`、`common/item/sword 12`、`common/item/bow 10`、`common/item/arrow 10`、`common/entity/projectile/sword 5` |
| **WP6-C 连枷 + 存储 + 召唤物** | 17 | 17 | **12** | 755 | `common/entity/flail 6`、`common/item/flail 3`、`common/entity/projectile/flail 2`、`common/item/storage 1` |
| **WP7 数据生成** | 75 | 75 | **15** | 2653 | 见第二节 |

WP6-B 与 WP6-C 的闭包**等于种子集**（70/70、17/17）——即这两块是自包含的，可以独立成批；
WP6-A 不是（859 的闭包里有 90 个缺失文件散在 40 个目录），所以它必须先做，且要按闭包清单整体搬。

## 二、WP7 缺失文件明细（15 个 / 2653 行）

| 文件 | 行数 | 备注 |
|---|---:|---|
| `NPCShopProvider.java` | 334 | **欠账主角**：1.21 原来的 TE 交易格式版本已随 `integration/terra_entity` 删除（`70686b138`），这份是 1.20 的原生 `NPCTradeOffer`/`TradeCondition` 版本，依赖 `WhipItems`/`YoyoItems`（WP6-A 落地后即可整份搬） |
| `NPCNameProvider.java` | 512 | NPC 名字数据 |
| `NPCDialogProvider.java` | 387 | 对话数据 |
| `data_map/AttackEffectsSubProvider.java` | 380 | datamap |
| `data_map/AccessoriesSubProvider.java` | 356 | datamap |
| `data_map/CompostableSubProvider.java` | 69 | datamap |
| `angler/AnglerQuestProvider.java` | 155 | 渔夫任务 |
| `NPCMoodProvider.java` | 115 | NPC 心情 |
| `language/LucyTheAxeLanguageSubProvider.java` | 94 | lang |
| `ModOpalDataProvider.java` | 65 | 猫眼石数据 |
| `NPCChatProvider.java` | 56 | 聊天数据 |
| `loot/LostPaperSubProvider.java` | 42 | 战利品 |
| `tag/ModFluidTagsProvider.java` | 37 | 标签 |
| `tag/ModWorldPresetTagsProvider.java` | 28 | 标签 |
| `loot/ArchaeologySubProvider.java` | 23 | 战利品 |

`SpawnEggItems`（`common/init/item`）也缺，它属于物品层：`MYSTIC_FROG` 等刷怪蛋目前没有注册入口
（`ModTabs`/lang 里对刷怪蛋的引用随之落空）。建议放进 WP6-C 或单独一小批。

## 三、执行顺序（含依赖理由）

> ⚖️ **2026-09-29 用户裁决（第 22 轮）**：WP6-B 撞上真正的**设计分叉**——1.21 分支自带一套
> **更旧但自洽**的武器层（数据组件式：`ModifyArrowBuilder`/`ModifierBuilder`/`SwordPrefabs`），
> 1.20 是后来重写的**类式**设计。实测证据：重叠度 `BaseTerraArrowItem` 15% / `BaseSwordItem` 23% /
> `BaseTerraBowItem` 32% / `SwordDefinition` 0%（MISSING）；箭支树 1.21 在
> `common/entity/projectile/range/arrow`（4 类，id 带 `_projectile`）、1.20 在 `projectile/arrow`（14 类）；
> 同名基类的构造函数与覆写**同签名不同体**，靠加成员救不回来，必须**替换**（一次实测按标准流程
> stage+convert 后是 145 错误 / 34 文件，连锁 ≈100 文件）。
>
> **裁决：做整层迁移（对齐 1.20）**，分 4 个子批。**实体 id 变更用别名兼容**：
> `BuiltInRegistries.ENTITY_TYPE.addAlias(from, to)`（用户指定）。已核实
> `net.neoforged.neoforge.registries.IRegistryExtension#addAlias(ResourceLocation, ResourceLocation)`
> 在 NeoForge 21.1.219 存在，且 `net.minecraft.core.Registry` 继承该接口——
> ⚠️ 用 `javap net.minecraft.core.Registry` 看不到继承来的方法，别据此误判「没有这个 API」。
>
> 子批：① 前置件（`util/generation` 4 / `util/track` 3 / `api/ITrackType` 28 /
> `SwordProjectileAppearance` 160 / `SwordProjectileParticleEffect` 59 / `BladeTrailEmitter` 85 /
> `DriveAwayController` 79 ≈ 600 行，全部缺失、纯增量）→ ② 箭支树替换（14 进 / `range/arrow` 4 删 /
> 17 处改指 / 14 个实体类型 + 别名 + 渲染器模型资源）→ ③ 剑层（`SwordDefinition` + 12 物品 + 14 弹幕，
> 覆写 5 个同名活文件，退役 `legacy/*` 6 文件）→ ④ 弓 + 弩 + 注册层对齐，最后删掉临时镬架
> `common/init/ModEffectStrategies`。

1. **WP6-A 鞭子 + 悠悠球**（90 文件）——✅ 已完成（`df135fc8b`，实到 57 文件 / 3629 行）。
   出口：`common/init/item/WhipItems`、`YoyoItems` 存在，`api/whip` + `common/entity/yoyo` + 渲染器齐全。
2. **WP6-B 弓 + 箭 + 剑**（整层迁移 ≈100 文件，见上方裁决）——分 4 子批串行推进。
   出口：`common/item/bow` 的 10 个原生弓类（`DemonBow`/`HuntingBow`/`TendonBow`/`MoltenFury` 等）与
   `common/item/sword` 的 12 个（`EffectSwordItem`/`BatBatItem`/`LightsBaneItem`/`Phasesaber` 等）落地，
   `common/item/arrow` 的 10 个箭类落地。**做完这块，`common/init/item/BowItems`、`SwordItems`
   就能按 1.20 整份改写**，`common/init/ModEffectStrategies`（WP8 里临时从 integration 挪出来的镬架）
   随之退役。
3. **WP6-C 连枷 + 存储 + 召唤物物品 + `SpawnEggItems`**（12 + 刷怪蛋层）。
4. **WP7 数据生成**（15 文件）——必须先有 1~3 的物品/实体的最终形态，否则 provider 写了还要再改；
   末尾跑 `runData` 验收（1.21 的 datagen 入口与 1.20 有差异，**不要把 1.20 生成出来的 json 直接拷过来**）。
5. 零散收尾：`checkMysticFrogSpawn`（`SpawnPlacementChecks` + `CreatureSpawnPlacements`）、
   `common/data/spawner` 3、`common/entity/storage` 3、`client/security` 8、`common/enchantment` 11、
   `util/generation` 4、`mixin/**` 残留 14。

## 四、每批的固定纪律（这几轮踩过的坑）

> ⚠️ **WP6-C 实测修正（2026-09-29）**：本文第一节的"12 文件／闭包 17"是**文件名差异**，
> 不等于"缺内容"。实测两种情形：
> - **连枷**：1.21 已有一套**活的、接好线的**连枷层（`FlailStrategy` 547 行 5 策略、
>   `FlailComponent` 18 个预设＝覆盖 1.20 全部 18 个连枷物品、`FLAIL_ENTITY`+3 弹幕、
>   `ModTabs:1915`、两个渲染器、上一批落的输入层与 `ModTags.Items.FLAIL`）。1.20 的是**后来的重构**
>   （record 化 `FlailComponent`、6 个实体子类、`BaseFlailEntity` 上 1.21 没有的钩子），
>   逐字搬入**不能编译**，硬搬会产生两套并行实现。所以本包**未移植**，功能上无缺口。
> - **`StorageCompanionItem`**：闭包并不自包含（还差 `common/entity/storage` 3、
>   `PetItems`、`ModEntities.CHESTER`/`FLYING_PIGGY_BANK`），且该功能 1.21 由 TerraEntity 子模块提供
>   （`TEPetItems.CHESTER_STAFF`/`WALLET`）。同样**未移植**。
>
> 结论：**"同名不同代"要在开工前判定**——与 WP6-B 同类（1.21 用不同机制实现同一特性）。
> ✅ **2026-09-29 用户授权（第 22 轮，追加）**：连枷**按 1.20 那一代整体替换**（"连枷需要换成
> 1.20 那代"），即显式批准"替换而非新增"。实测范围（1.20 侧）：
> `common/entity/flail` 7 个（`BaseFlailEntity` 582 行 + `AnchorFlailEntity` 41 /
> `DripplerCripplerFlailEntity` 36 / `FlaironFlailEntity` 74 / `FlowerPowerFlailEntity` 56 /
> `GuardianFlailEntity` 174 / `LaunchedFlailEntity` 32）、`common/entity/projectile/flail` 4 个
> （`FlailAuxiliaryProjectile` 117 / `FlaironBubbleProjectile` 107 / `DripplerCripplerProjectile` 62 /
> `FlowerPowerPetalProjectile` 28）、`common/item/flail` 4 个 + `package-info`
> （`BaseFlailItem` 206 / `FlaironItem` 56 / `DaoOfPowItem` 34 / `IgnitingFlailItem` 28）。
> 要替换/退役的 1.21 侧：`BaseFlailItem`（255 行、`useFlail`/`tryAutoSwing`/`getAttackStrategy`
> 状态机）、`FlailStrategy`（547 行 5 策略，退役）、字段式 `FlailComponent`（换成 1.20 的 record
> + `Codec`/`StreamCodec`/`Behavior`）、`BaseFlailEntity`（换 1.20 版并补 6 个子类）、
> `common/entity/projectile/Flail/**`、7 个实体类型（改名者走 `addAlias`）、
> `ModDataComponentTypes.FLAIL` 的编解码器、两个渲染器、`ClientWeaponInputManager`（1.20 是
> 静态 `press`/`release` 状态机）、`ModTags.Items.FLAIL`、`ModTabs:1915` 的连枷条目。
> **执行顺序**：与剑层/弓弩批**不可并行**（同改 `ModEntities`/`ClientWeaponInputManager`/`ModTags`）
> ——排在剑层（子批 ③）之后、弓弩（子批 ④）之前。
> `StorageCompanionItem` 仍维持"不移植"（功能由 TerraEntity 子模块提供；若也要换 1.20 那代需另行授权）。
>
> ✅ **同时落地**：`SpawnEggItems`（284 个刷怪蛋，1.20 逐字照搬，仅 PortLib/类型适配：
> `PortItemRegistration`→`DeferredRegister.Items`、`ForgeSpawnEggItem`→`DeferredSpawnEggItem`、
> `RegistryObject`→`DeferredHolder`）+ `ModItems` 一行接线。实测 313 个实体常量名两树一致、
> 0 命名冲突。**但刷怪蛋的"呈现层"仍缺**：1.21 没有 `entity` 创造标签页、没有 `IconItems.ENTITY_ICON`、
> 没有 `creativetab.confluence.entity` 与 284 条 lang，所以现在 `/give` 与 JEI 里显示的是原始翻译键
> ——这一层留给 WP7 datagen。另外 `confluence:*_spawn_egg` 与子模块的 `terra_entity:*_spawn_egg`
> 会并存（命名空间不同、不冲突，但要在某个时点收敛）。

1. 替换只作用于**本批新拷入的文件集合**，绝不按目录扫（上一轮按目录扫改坏了 94 个既有文件）。
2. 注册层插入点 = **第一个方法声明之前**（`ENTITIES`/`TYPES` 自己也是 `public static`，插错会
   `illegal forward reference`）。
3. 多行声明**整条**拷贝（按「以 `);` 结尾」判边界）。
4. import 块**不重排**，只补需要的几条（重排会吃掉 import 区里的既有注释）。
5. 源码注释照抄 1.20，**不写溯源/批次说明**（批次结论写 `notes/`）。
6. 出口三件事：`build_errors.py` 0 错误 → `check_duplicates.py` 对新文件 0 重复 →
   `git add -A` + `fix_eol.py --repo .` 后提交。

## 五、工具性坑（2026-09-29 这几轮实测，都会再次遇到）

1. **门禁把 `warning:` 也计入错误数**（`build_errors.py`）。因此 `[removal]` 弃用告警也必须清零，
   例如 `Item#initializeClient`、`AutoGlowingGeoLayer#getRenderType(T)`、
   `onEntitySwing(ItemStack, LivingEntity)`、`MobEffectInstance` 的 Holder 形参。
2. **Windows 大小写改名会污染 git 索引**：`projectile/Flail/` → `flail/` 时 `git add -A` 会按
   **旧大小写**暂存（`core.ignorecase=true`），提交后 Linux 上「目录名 ≠ 包名」直接编译失败。
   正确做法：`git rm -r --cached <旧大小写路径>` 再 `git -c core.ignorecase=false add <新路径>`，
   提交前用 `git ls-files | Select-String <包名>` 复核索引里只剩新大小写。
3. **Gradle 增量编译会因同一个坑留下残渣**：`build/tmp/compileJava/previous-compilation-data.bin`
   记着旧大小写的输出目录，下一次增量清理会把物理同名的 `flail` class 删掉而源文件不在重编译集里，
   于是报一堆 `package … does not exist`（与当时改动无关）。清掉该 bin + 陈旧 `build/classes/.../Flail`
   强制全量重编译即可自愈；新克隆的机器不会遇到。
4. **不要用 PowerShell 读中文来做内容核查**：控制台按 GBK 解码 UTF-8 源码会变乱码，
   拿乱码去 `Select-String -SimpleMatch` 必然假阴性（本轮把 1.20 原文注释误判成「移植者自加」）。
   注释口径核查一律用 Python（显式 `encoding="utf-8"`）逐行比对 1.20 原文。
5. **同名不同代要在开工前判定**：`file_lag.py` 的重叠度（<50% 即改过设计）比「文件名差异」可靠。
   本轮三处都属此类（武器层、连枷层、存储伙伴），前两处经用户授权做了「替换而非新增」，
   存储伙伴维持不移植。

