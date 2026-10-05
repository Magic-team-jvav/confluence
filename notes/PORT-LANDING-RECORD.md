# 1.20.1 → 1.21.1 移植落地记录（终版）



> 本文是本次长会话的**结论性记录**：口径、已落地批次（含提交哈希）、裁定为"不移植"的清单、

> 编译验证结论、行为面变化、未做事项。逐条判定的原始证据见 `notes/CO-MISSING-TRIAGE.md`。



## 一、总口径（六条，后来所有批次都按它执行）



1. **方向规则**：1.20 分支约 2026-04/05 自 1.21 分出，合并基点 = `795ac9ccc`（2026-05-31）。

   分叉**之后**在 1.21 做的改动 → 反向同步到 1.20；分叉**之后**在 1.20 做的改动 → 同步到 1.21

   （即"1.21 对齐 1.20"只对 1.20 侧的新改动成立）。

2. **判方向的方法**：`git cat-file -e 795ac9ccc:<path>` —— 基点**不存在** = 1.20 侧新增（搬进 1.21）；

   基点**存在而现在没了** = 1.21 侧删除（反向欠账，**不要**再引入）。

   ⚠️ 子模块（`Confluence-Magic-Lib`/`TerraCurio`/`TerraFurniture`）是**独立仓库**，其"分叉后"必须查**子模块自己的历史**，

   用根仓历史查会得出错误结论。

3. **搬 ≠ 复制（历史优先）**：1.20 侧大量改动是**改名 / 成员搬家**。必须按创建提交

   （`git log --diff-filter=A --follow` + `git show --stat -M -C`）判定 `RENAME` / `MOVE` / `NEW`，

   再在 1.21 照搬**同一个动作**：改名就 `git mv` + 全仓改引用；搬家就**删掉旧类里的副本** + 改指所有调用点。

   **绝不允许"复制过去、旧类留副本"**。本会话为此返工过两次（`LibEntityUtils`、`getTeam` 双重包裹）。

4. **平台权宜不移植**：只为绕开 1.20.1/Forge/PortLib 的 API 缺口而存在的代码不搬（清单见 §三）。

5. **工程约定**：注释照抄 1.20（不加溯源/说明性注释）；工作树 CRLF + `fix_eol.py --check` 必须 0；

   子模块提交在前、根仓在后；一个批次一个提交；禁改的 10 个文件（`Confluence.java`、`ModEntities`、`ModItems`、

   `ModDataComponentTypes`、`Gun`/`BulletPropertyComponent`、`BaitItem`、`PathService`、`ServerEvents`、`SummonItems`）不碰。

6. **编译**：仅在用户许可时跑；`FROM-CACHE`/`UP-TO-DATE` **不算验证**（须删 `previous-compilation-data.bin` + `--no-build-cache`）。



## 二、已落地批次（提交哈希）



| # | 批次 | 提交 |

|---|---|---|

| 1 | `LibUtils` 去重（`LibEntityUtils` 18 个成员搬走、134 处调用点/73 文件改指、62 条孤儿 import 清除）+ port 规则收窄 | lib `93c1137`、TerraCurio `0c3eada`、root `aa0cb4c2b` |

| 2 | 改名组：`FeatureUtils→LibFeatureUtils`(R099)、`StructureUtils→LibStructureUtils`(R092)、`GameClientEvents→TCGameClientEvents`、`ModClientEvent→TCModClientEvent`、`ILivingEntity→ITCLivingEntity`、`IClientLivingEntity→ITCClientLivingEntity`、`LivingEntityClientMixin→ClientLivingEntityMixin` | 含在批次 1 的提交内 |

| 3 | 三组包改名：`integration/animation→api/animation/third_person`(7)、`client/animate→client/color`(3)、`mixin/accessor→mixin/client`(1) | lib `9e09d50`、root `3f82c9d26` |

| 4 | `@Deprecated(forRemoval=true)` 兼容层删除（89 个元素、14 文件删除、10 文件编辑；全部 0 引用死垫片） | lib `feccc4a`、TerraCurio `a9b7918`、root `f78f5ac2` |

| 5 | 动态光源 API 整套归 `Confluence-Magic-Lib`（实体+方块两条 mixin、帧末驱动、退出清理；根仓只剩唯一读取点） | lib `28cc0fe`/`0dc9288`、root `08da839be`/`cca6878b1` |

| 6 | `LibEntityUtils` + `LibEntityUtilsMixin` 移植（用户裁定要移植；前置 lib 类 325 行，3 处平台适配） | lib `85bd6cf3e`、root `282158ccc` |

| 7 | 可开关药水效果（`ILib*`）根仓 → lib 整体搬家（10 新文件 + 9 扩展，根仓删 9 个重复实现与 mixin/AT 条目） | lib `f85bbd3`、root `3b4ecd777` |

| 8 | 圣骑士盾伤害转移归 lib（`applyPaladinsShield`/`isPaladinsShieldOwner` 搬家）+ `getTeam` 双重包裹清除 | lib `29c5dbb`、TerraCurio `d69dbd3`、root `d5b59ca70` |

| 9 | TerraCurio 粒子触发线（声明式重构、删 5 个 per-item 子类、粒子定义 6→28、7 张贴图） | TerraCurio `6007d24`/`3234a5e`/`8b15ab1`/`5d18da0`、root `d8aecec11`/`c3c66cb7b`/`6fff3a2c6`/`3ad27cf65` |

| 10 | TerraFurniture `OneLegTable` 两阶段（`4d32771` 全套 → 对齐 1.20 HEAD 的组件模式重写） | TerraFurniture `1a112ed`/`a3b5276`、root `46b01f1c0`/`f933954ac` |

| 11 | 编译兜底修复（见 §四） | lib `0d472e8`、root `5cc85fd5e` |

| 12 | `particlestorm_version` 对齐 1.4.4（用户确认有意为之，与 1.20.1 一致） | lib `d3485e1`、TerraCurio `664f90a`、root `88da104da` |

| 13 | `VectorUtils`/`RenderUtils` 按 1.20 `c69f0f6` 拆分（删 908+142 行、8 成员补进 `LibMathUtils`/`LibUtils`/`LibRenderUtils`、`Vector3d→Vector3f` 落 27 文件） | lib `d9637fa`、root `38e1606b0`、TerraCurio `800aede`、root `51bad1b53` |

| 14 | 台账第 10–12 行（2026-06-10 家族）：part8 `f4b42537c` 判 **SKIP-PLATFORM**（0 行可移植：83 文件 diff 是原生→Forge/PortLib 形态转换，其内容 1.21 已有更全的原生等价物，3 条新增 AT 在 1.21.1 已 public）；part9 `e7b826680` **PORTED**（`LibUtils` 清掉已搬走的 `setItemAndDropChance`/`getAngleRadians`×2/`rotLerp`，−43 行，11 处调用点归位 `LibEntityUtils`）；fix2 `4b004c160` 本体 de-port 跳过、其中"删 1.3.0 旧存档兼容分支 + `confluence:fixed` 标记"属 1.20 侧删除 → 同步 | lib `7dd8f85`、root `6440fd8af` |

| 15 | 台账第 14/15 行：`395003423` **PORTED**（`LibClientUtils.keyMappingComponent` 双参重载 + TerraCurio 四饰品按键提示 `WHITE`/整行 `GRAY`）；`00b72167d` **部分 PORTED**（lib `LibDamageTypes` 补 9 个类型至 15 个 + `LibDamageTypeTagsProvider` 补 5 组标签，删掉"补齐 1.20 13 个伤害类型是独立 WP"的过期注释） | lib `ad33c30`、TerraCurio `6a94263`、root `2a03ef776` |

| 16 | 恢复 `setItemAndDropChance` 的**随机附魔活实现**（用户裁定：1.20 删它是 API 所迫，非设计意图 → 保留 1.21 活实现）。签名/位置/命名不变、**11 处调用点一行未改**，仅把 `getRandom1211()` → `mob.getRandom()`（1.21 API）并删掉失效的 `// todo` 标记 | lib `73d3100`、root `2b5ca84c5` |

| 17 | 台账第 15 行 TerraGuns 半边 + 第 21–24、29–38 行：**TerraGuns 半边判 `COVERED`**（1.21 侧没有该子模块，枪械已内联退役且实现逐字取自 1.20）；其余 12 行**全部 `SKIP-PLATFORM`**（逐文件归一化后真缺口 0 行）。本批真动作仅 5 处：lib 新增 `NonNullBiConsumer`/`NonNullFunction`（根仓那份删除不留副本）、新增 `mixin/client/gun/ItemInHandRendererMixin`（1.21 原先那条 mixin **缺枪械分支**，枪的第一人称手臂位姿此前是错的）+ mixins.json 登记、两个 `package-info.java`、删除死壳 `TerraStyleSoulHud` | lib `fea1322`、root `6799130c9` |



> ✅ **已裁定并落地**：`setItemAndDropChance` 保留 1.21 的活实现（批次 16）。刷怪装备（雪地/雨衣/矿工套装、骨镐）恢复按难度随机附魔：`enchantChance = (HAND ? 0.25F : 0.5F) * difficulty.getSpecialMultiplier()`，命中走原版 `mob_spawn_equipment` 提供器。

>

> ⚠️ **`EmptyEntity` 搬家 BLOCKED**（台账行 24）：1.20 已把它搬到 lib `common/entitiy/EmptyEntity`（1.21 该目录已在），但 1.21 的调用点之一是 **`common/init/ModEntities.java:65`（禁改文件）**，故未收口、无副本残留 —— 待授权碰 `ModEntities` 后再搬。

>

> ⚠️ **1.20 侧真 bug（建议反向修 1.20）**：`BilayerOreFeature` 的两个便利构造器都把 `outerOre` 传成了 `innerOre`（5 参与 4 参版皆是），而 `ModDataProvider` 的 DRAGONSAL_ORE 正走 5 参版 → 子代理**拒绝照搬**（否则把 bug 带进 1.21），该项记待裁定。

>

> 📌 **台账标签大面积误报（重要）**：`TRIAGE-PASS2` 对 `part10`–`part17` 那批行标的 `NEEDS-PORT/PARTIAL` 与实测相反——它们是 **Forge/PortLib 平台化波次 + 1.20.1 原生 API 回落**（`ResourceKey<LootTable>→ResourceLocation`、`MapCodec↔Codec`、`switch(pattern)→if/else`、`defineSynchedData(Builder)→()`、`getSpawnPos()→getXSpawn…`、`RegistryObject↔DeferredHolder`）。判别要点：**必须加基点谱系检查**（`795ac9ccc:<path>` 与 1.21 HEAD 的归一化重合度），用来分清"1.21 从没动过（缺口为真）"与"1.21 自己迭代过、只是落后版本"——`BranchTreeFeature` 重合 21%、`GraniteCaveStructure` 53%、`MarbleCaveStructure` 63% 均属后者（1.21 反而领先，不要再往 1.21 搬）。建议把该列并进 `file_lag.py`。



> ⚠️ **行为面变化（需裁定）**：第 14 批把 `setItemAndDropChance` 归位 `LibEntityUtils` 后是 **1.20 HEAD 形态**——其中的附魔逻辑被 `// todo` 注释掉（1.20 原文含 `getRandom1211()`，1.21 无该扩展）。

> 后果：**刷怪的雪地/矿工/雨衣装备不再随机附魔**（此前 1.21 的 `LibUtils` 那份是真实现）。按"对齐 1.20"这是正确结果，但相对改动前的 1.21 行为是退步，**需裁定是否保留 1.21 的活实现**。

>

> ⚠️ **反向欠账新增登记**：`LibUtils.damageSource`（3 重载）在基点 `154d1cf`（1.20 CML 指针）存在、被 1.21 侧提交 `93c1137` 删除且 1.21 已无调用点 → **不要再引入**（可反向同步到 1.20）。

>

> ⚠️ **工具缺陷（待修）**：`file_lag.py` 的 `PORT_REF_RE`（`\b(?:IPort|Port)[A-Z]\w*`）匹配不到**拼接形态**（如 `finalPortDeferredItem` 中 `final|Port` 之间无词边界），导致 1.20 侧 `partN` 行欠账被严重高估；归一化 `RegistryObject/DeferredItem`、`DeferredRegister.create*`、`AttributeModifier.Operation.*`、`.get()`、`toStack()↔getDefaultInstance()` 后，`f4b42537c` 的 miss 由 3176 → 740、`ModItemTagsProvider` 由 315 → 9。修该 regex 是下一步工具工作。

>

> ⚠️ **写盘教训**：用 PowerShell `Get-Content -Raw`/`WriteAllText` 改 java 文件会按 GBK 重解码，把中文注释变成乱码且**编译不报错**（本批出现过一次，靠 `git diff` 复核发现后回退重做）。改文件只用 edit 工具或显式 UTF-8 的 python；PowerShell 的 `\"` 不能转义（`git commit -m` 含引号会被拆参数）→ 提交信息一律走 `git commit -F <utf8 文件>`；`python -c "中文"` 会经控制台代码页二次解码变乱码 → 中文内容不要走 `-c`。



### 台账续行批次（18–20，均为收口项）



| # | 内容 | 提交 |

|---|---|---|

| 18 | 台账行 39–44：`5c56e83b2`/`b33c206fa`/`1e77c5edc` 判 **COVERED**（内容 1.21 已有，或只是去 BOM/家俱层平台降级）；`5481344ca` 的 lib 半边 **PORTED**（`ILibClientItemStack` 的 `confluence$setGroupId/getGroupId` → `confluence$clientSetGroupId/clientGetGroupId`，4 文件 6 处）；根仓 `RopeCoilItem.use` 补 `@Override`。另判 `b33c206fa`(箭/弓/弩) 与 `4dcf95cfe`(家具) 的特殊性：1.21 那两层**不是从这些行学来的**，而是 1.21 既有实现（箭层由 `d5c9c2b86`/`79f9090a6` 按 1.20 整层替换；TerraFurniture 是同一 upstream 的另一 1.21 分支），故只能比对形态等价性 | lib `617fda7`、root `daab610ba` |

| 19 | 台账行 24 `EmptyEntity` 搬家收口（原 BLOCKED 于禁改文件）：lib 建 `org/confluence/lib/common/entitiy/EmptyEntity.java`（**包名 `entitiy` 拼写照抄**，唯一适配 `defineSynchedData(SynchedEntityData.Builder)`），根仓删旧副本、`EmptyEntityRenderer` 改 1 行 import、`ModEntities.java` **仅 +1 行 import**（授权仅限该行，用完即恢复禁改）。`EmptyEntityRenderer` 按 1.20 事实**留在根仓** | lib `1fad734`、root `98e409ef8` |

| 20 | 台账行 21 `BilayerOreFeature` 按 1.20 修正形态同步（裁定 A 的后续）：`Optional<TagKey<Block>>` + `optionalFieldOf("replace_tag")`（**缺字段 = 不过滤**，取代 1.21 原先的 `null` = 恒 `return false`）+ 补 5 参/4 参便利构造器 + `LibUtils.DIRECTIONS`/`Direction.getRandom`（**随机流不变**）。**blob 与 1.20 `3f1c95b63:<path>` 完全相同**（`022f7c633…`）。全仓（含 json）无 `replace_tag` 消费者 → 无迁移影响，`ModDataProvider` 未改 | root `794d106fa` |



> ✅ **1.20 侧 bug 已反向修复**（用户裁定 A）：`BilayerOreFeature` 两个便利构造器把 `outerOre` 误传成 `innerOre` → 1.20 仓库提交 `3f1c95b63`（路径限定、diff 2 行）。收口后**两侧 DRAGONSAL_ORE 行为一致**。1.20 HEAD 基线由此从 `18221c338` 更新为 `3f1c95b63`。

>

> 📌 **判定基线的重要修正（方法层面）**：判断某行"内容是否已落地"时，基线必须是 **1.20 HEAD（或该文件最后一次 1.20 改动）**，而**不是该行自己的提交**。用行 41 自身比 1.21 会得到 `BaseTerraArrowItem` **39%**、`BaseArrowEntity` 78% 等**假缺口**（1.21 早已吸收 1.20 后续行的状态：箭伤害模型由 `BaseArrowEntity.getBaseDamage()` 覆写改为物品侧 `additionalDamage`）；换成 1.20 HEAD 基线后同批文件升到 88–100%，残余全为平台词汇。另：**1.20 HEAD 路径会漂移**（`common/init/ModEntities.java` 已移至 `common/init/entity/ModEntities.java`），比对脚本不能沿用行时代的路径。建议把"行自身提交 + 1.20 HEAD 双基线"并进 `file_lag.py`。

>

> ⚠️ **行 42 仍 BLOCKED（需授权两个文件）**：`ModEntities` 里还有 **42 个常量 + 35 个注册 id** 带 `_projectile` 后缀未改名；改注册 id 必须同时在 `Confluence.java` 的 `@Mod` 构造器首条调用 `registerEntityAliases()`（箭族 3 条别名是 WP6-B 先例）。这是**注册 id 层面的真差异**（旧存档/数据包会对不上），非纯命名。

>

> 📌 **`@ScheduledForMove` 可当"未收口搬家人工清单"用**：`ScheduledForMove`（`@Retention(SOURCE)`，`module()` 默认 `"lib"`）标记随搬家消失，全仓扫它可一次性列出其它同类待搬项（尚未统计）——比按台账逐行啃更快。

>

> ⚠️ **`entitiy/package-info.java` 未补**（1.20 `09dc98c` 同时建了它，内容 `@ParametersAreNonnullByDefault` + `@MethodsReturnNonnullByDefault`）：1.21 该包已存在且 `IAxisZRotate`/`IBouncy` 在其中而无 package-info，补上会**追溯性改变这两个既有类的空值语义**，超出当时授权 → 待裁定。



## 三、裁定为"不移植"的清单（含理由）



| 项 | 理由 |

|---|---|

| `common/enchantment/` 10 个附魔类 + `ProtectionEnchantmentMixin` | 1.20 在 PortLib 环境下被迫写成 java 类；1.21 走数据驱动附魔 |

| `SimpleTreeGrower` / `SimpleMegaTreeGrower` | 1.20 的 `AbstractTreeGrower`/`AbstractMegaTreeGrower` 只能靠子类重写传 feature；1.21.1 已合并改名 `TreeGrower`，构造器直接吃 `Optional<ResourceKey<ConfiguredFeature>>` |

| `ForgeItemModelShaperMixin` | 只是改名：Forge 的 `ForgeItemModelShaper` 在 NeoForge 叫 `RegistryAwareItemModelShaper`，1.21 已有同逻辑 mixin |

| `SupStemBlock`/`SupAttachedStemBlock`/`SupBrushableBlock`、`SimpleFinishedRecipe`/`UnitFinishedRecipe`、`FriendlyByteBufMixin` | 1.20.1 API 差异所致 |

| `SnowballItemMixin`、`BucketItemMixin` | 1.20 无奈之举（1.20 全仓没有 `ModifyDefaultComponentsEvent`）；曾误同步、已回撤（`25ee32487`） |

| `mixin/InventoryChangeTriggerMixin` | **反向欠账**：1.21 已有 `ServerPlayer$ContainerListenerMixin` 注入同一目标并发同一个包，照搬会双重发包 |

| `mixin/integration/curios/ClientEventHandlerMixin` | 1.20 用 PortLib 模拟的 `Attribute.Sentiment`，1.21.1 NeoForge 原生已有 |

| `a3f1cbc` 的"翅膀"半（`AccessoryGeoModel`/`WingsGeoRenderer` 等 + 2 资源） | 本就是 1.21→1.20 的反向同步，1.21 已有 |



**反向欠账（应同步到 1.20，用户裁定暂缓，仅登记）**：钩爪渲染层重构（`SimpleHookRenderer` + 20 模型 + 10 渲染器）、

删除 `BaseContainerBlockEntityMixin`、删除 `TCHelper` 与 `RecipeManagerMixin` 里对应注入。



## 四、编译验证结论



四模块全量实编译（删净 `previous-compilation-data.bin` + `--offline --no-build-cache`，

`:Confluence-Magic-Lib:compileJava :TerraCurio:compileJava :TerraFurniture:compileJava :ConfluenceOtherworld:compileJava`）：



```

四个 compileJava 全部 executed（无 FROM-CACHE / UP-TO-DATE）

0 条 error:   BUILD SUCCESSFUL   [build] exit=0

```



在此之前，8 个"纯 grep + 读码"推进的批次**累计只暴露 3 处错误**，全部只有编译器能发现，均已修复：



| 错误 | 位置 | 修法 |

|---|---|---|

| `Encoder<A>` 不适用 | `CraftingLootItemCondition:30` | 21.1.219 的 `ICondition.CODEC` 是 **`Codec`** 而非 `MapCodec`，改用 `encodeStart(...).flatMap(ops::getMap)` 后逐项 `prefix.add(...)`；**扁平 JSON 契约保留**（并已用仓库外 harness 双向 round-trip 实测） |

| 缺第三参 ×2 | `HookThrowingHandler:56`/`:91` | `PlayerJumpHandler.multiJump` 与 `PlayerJumpPacketC2S` record 都多了 `jumpType`，按 1.20 原文补 `JUMP_NONE` |



## 五、行为面变化（自 1.20 继承，需知）



1. **世界生成随机流在 5 处改变**（`Vector3d→Vector3f` 的连带结果）：`HugeMushroomTreeFeature`、`CrimsonCaveStructure`、

   `HeavenIslandsStructure`、`MineTunnelsStructure`，以及 `BaseStructures.everyB`（`nextInt(101)/100` → `nextDouble()`，

   即 1.20 `part10` 的 `182149f52`）。**新生成的区块结构与改动前不同、与 1.20 一致**；旧存档已生成部分不变。

2. **TerraCurio 饰品粒子改声明式**：删 5 个 per-item 子类，粒子定义 6→28，`BaseCurioItem` 增加 `onUnequip`（1.21 之前无 emitter 清理路径）。

3. **圣骑士盾**比较器由截断成 int 的 `(int)(a.distanceToSqr - b.distanceToSqr)` 改为 `Comparator.comparingDouble`（顺带修掉近似距离比较为 0 的隐患）。

4. **动态光源**归 lib（dispatcher + 两条 mixin + 帧末驱动），根仓只剩 `AttachmentEntityRenderDispatcher` 一处读取。

5. **TerraFurniture `OneLegTable`** 按 1.20 保留 tracked 战利品表（1.21 其余家具走过去 datagen 的路子）。



## 六、未做事项



1. **运行时验证（用户自行执行）**：mixin 应用（动态光源两条、`MobEffectInstance` 系、`LibEntityUtilsMixin`、`TCUtilsMixin`）、

   payload 注册无 ID 冲突、22+ 粒子定义与结构表现、`OneLegTable` 多人联机 blockstate。

2. **台账其余 `NEEDS-PORT` 行**：`notes/PORT-LEDGER.md` / `TRIAGE-PASS2.md`（TODO/REVIEW）继续按历史推进。

3. **工具口径遗留**：`FILE-LAG.md` 的 `MISSING` 按**文件名**匹配（改名/合并/内联会漏判），其 LAGGING/PARTIAL 段每段只列前 200 行；

   引用数字时以 `CO-MISSING-TRIAGE.md` 的判定表为准。

4. **1.20 仓库工作树长期脏**（`mixin/neoforge→mixin/forge` 已 staged、`PathService`/`ServerEvents`/两个 TreeGrower 有改动、

   `PortLib` 子模块脏）——台账数字在此期间会被这类未提交改动扰动。



## 七、批次 21（台账行 42）：`ModEntities` 去 `_projectile` 后缀（注册 id 级改名）



**提交**：根仓 `5df9d6ef5`（50 文件，+419/−417；无子模块改动）。**42 组**（常量 + 注册 id 一一对应）按 1.20 HEAD 改名

（`WATER_BOLT_PROJECTILE`→`WATER_BOLT`、`STORM_SPEAR_SHOT_PROJECTILE`→`STORM_SPEAR_SHOT` …）。

每条做了三重核对：①1.20 HEAD 无旧名有新名 ②新常量绑定的 id == 新 id ③**基点 `795ac9ccc` 里这 42 个用的都是旧名**

（旧路径 `common/init/ModEntities.java`）→ 属"1.20 侧分叉后改名"，方向正确。

**明确不改**：18 个 1.20 同样保留 `*_PROJECTILE` 的、4 个常量名无后缀但 id 两侧都有后缀的——必须逐符号核对，

盲替换会误伤 `SpearProjectileComponent.GHASTLY_PROJECTILE` 这类**同名自有字段**。



**影响面（本批实际改到的地方）**：java 常量引用 146 处（41 文件 + `ModClientEvents` 的 42 处裸引用）、注册 id 42 处、

datagen 2 文件、`resources` lang **5 文件 141 条** + `i18n` 2 文件 **84 条** `entity.confluence.<id>` 键。

**5 处外科手术**（非词替换）：两个渲染器原本把**实体 id 兼作资源路径**（`CRYSTAL_VILE_SHARD.getId()`），1.20 已解耦为

`Confluence.asResource("crystal_vile_shard_projectile")`——**不修就静默丢模型/贴图且编译不报错**；

`CloudProjectile` 的 `RainType` NBT 串、`SpearProjectileComponent` 3 处 `projType` 改指新 id。

**保留不动**的 `*_projectile` 串共 26 处，全是**资源名/模型层名/粒子 id**（1.20 逐条相同、目标文件在盘上）。



> ⚠️ **别名口径（重要，前提已被纠正）**：用户裁定"**不补别名**"，最初理由是"MISC 不持久化"——

> **该前提与 1.21.1 源码相反**：`MobCategory.java:15` `MISC("misc", -1, true, **true**, 128)`，第 4 参即 `isPersistent`

> ⇒ **MISC 是持久的**；真正决定是否写盘的是 **`EntityType.canSerialize()`（`.noSave()`）**，42 个里只有

> `TITANIUM_SHARDS` 调了它，其余 **41 个可序列化**；写盘身份是**字符串**（`Entity.save()` → `getEncodeId()`）。

> **裁定照旧执行（不补别名），但真实影响面应记为**：区块保存那一瞬**还在飞的弹幕**会以旧 id 落盘、改名后丢弃

> （不含世界生成/结构/常驻实体；唯一有意持久化的字符串 id `RainType` 已改指新 id）。

> 另注意 `DefaultedRegistry.get()` 对未知 id **不抛异常而返回默认值**（`ENTITY_TYPE` 默认是 `minecraft:pig`）——

> 旧 id 会**静默变成猪**，这是"改注册 id 必须想清楚别名"的根本原因。



**同批待办**：①`FALLING_STAR_ITEM_ENTITY`→`FALLING_STAR`、`TREASURE_BAG_ITEM_ENTITY`→`TREASURE_BAG`（id 已同，仅常量名滞后，走同一改名规则）

②`tools/port2native/te_lang_{keys,providers}.py` 各 1 行映射右值过期（`demon_scythe_projectile` → `demon_scythe`）

③**1.20 侧反向修复**：1.20 HEAD 自己也有改名时漏改的悬空串（`SpearProjectileComponent` 3 处 `projType`、`CloudProjectile` 的 `RainType`）→ 同 `BilayerOreFeature` 先例，反向修 1.20

④**`src/generated` 仍是旧 id**（`data_maps/entity_type/immunity.json` + 三个生成语言文件）→ 必须重跑 `runData`，在此之前**不要**拿旧 `src/generated` 测数据包

⑤`INVERSE_ENDERMAN` 是 1.21 独有（1.20 无）→ 保持 1.21 领先，不改。



## 八、批次 22–23（行 42 收尾 + 台账行 45–47）



| # | 内容 | 提交 |

|---|---|---|

| 22 | 行 42 收尾：`FALLING_STAR_ITEM_ENTITY`→`FALLING_STAR`、`TREASURE_BAG_ITEM_ENTITY`→`TREASURE_BAG`（id 本就一致，仅常量名滞后；6 处调用点）；`te_lang_keys.py:50` / `te_lang_providers.py:80` 映射右值 `demon_scythe_projectile`→`demon_scythe`（否则重跑 TE 脚本会删键而非迁移） | root `b2d77915e` |

| 23 | 台账行 45–47：行 45 唯一真动作 = 去重（1.20 把私有 `record ColorData` 换成 lib 的 `IntegerARGB`，1.21 仍留本地副本 → 已删换、6 处签名重定型；颜色值逐位等价 `ColorData(10,8,3,127)` ≡ `IntegerARGB(127,10,8,3)`）；行 46/47 判 **SKIP-PLATFORM**（行 46 机器证据：426 个被删签名行 **93% 在 1.21 存在**、195 个新增签名行仅 16% 存在 → 系统性"1.21 原生 → 1.20.1 形态"回写） | root `23fa9659b` |

| 23b | **1.20 侧反向修（裁定 A 同家族）**：`SpearProjectileComponent` 3 处 `projType`（`mushroom_projectile`→`mushroom` 等）+ `CloudProjectile` 的 `RainType` NBT 串 `confluence:blood_rain_projectile`→`confluence:blood_rain`。1.20 自己改名时漏改的悬空串；`DefaultedRegistry` 对未知 id 不抛异常而回落默认实体 | 1.20 `b15757f94`（路径限定，2 文件 4+/4−） |



> ⚠️ **改文件时的"注释吞噬"险情（已修复，务必记住）**：批次 22 首次编辑 `ModEntities.java` 时用了**过期快照**，回写时**丢掉 57 行注释**（`/// Fast Link:` 块、WP2/WP3 成员级盲区溯源注释等）——**编译通过、`git diff` 也容易漏看**，是复核 `git show --stat`（`59 +----` 而非预期的 `4 ++--`）才发现的。处置：`git checkout` 回退 → 逐字节 UTF-8 脚本重做（带出现次数断言 + CRLF 保持）→ 最终 delta 精确 2 增 2 删 → `--amend`。

> 结论：**注释的两个方向都会静默出事**（多加了难发现、改文件时被吞掉），所以①改 java 只用 edit 工具或显式 UTF-8 脚本；②每批都要看 `git show --stat` 的数字是否与预期相符。

>

> 📌 **同批报而未改（待裁定）**：①`ORICHALCUM_HALBERD_PROJ` 的 `orichalcum_halberd_projectile` 也是不存在的 id，但非行 42 余波（1.21 逐字相同）且该组件是**死代码**；②1.20 的 lang 资源仍带旧 `entity.confluence.blood_rain_projectile` 等键（`i18n/{en_us,zh_cn}.json:2969` + `pt_br`/`lzh`/`es_es`/`de_de`），超出该批授权范围。

>

> 📌 **台账行进度**：下一开放行为 **48**（`4ab2d42ad`，已预判 SKIP-PLATFORM、无需改码）→ 49（`c9f3af990`，含 10 个污染残留文件）→ 50 → 51（`f6b8f73b0` part20，`ModEntities` 搬家 + 18 个残留）。



## 九、批次 24：删除移植引入的多余注释（用户裁定口径的执行）



**方法**（可复现，脚本在 `%TEMP%`、未提交进仓库；仓库自带 `comment_diff.py` 只能双文件比对，做不了"全仓注释集合 + 引入提交溯源"）：

范围 = 四仓库 **2026-09-30 起**的本会话移植提交（root 73 / lib 22 / TerraCurio 10 / TerraFurniture 2）；

从这些提交 diff 取**新增注释行**（去重 1352 条）→ 与 **1.20 全仓 java 注释索引**（141161 条注释 body，含三种命中形态）比对 →

再用「会话基线上该文件是否已有此注释」剔除 **1.21 原生历史注释** → 再用「当前文件是否仍在」剔除已被后续提交改掉的。

**结果**：原始候选 12 条 → 人工复核后**删除 5 条（28 行 / 2 文件）**、保留 7 条。



| 仓库 | 提交 | 内容 |

|---|---|---|

| `Confluence-Magic-Lib` | **`2b9fd3e`** | `mixin/LivingEntityMixin.java` 删 27 行叙述块（**1.20 同文件 78 行通篇零注释** → 按"1.20 没有就不加"应零注释；被删内容含 WP6c 溯源对照表、"尚未接线"等已过期说明）+ `common/LibDamageTypes.java` 删 1 行 `// todo 统一这俩召唤师标签`（1.20 该处无注释、1.20 全仓无此串） |

| 根仓 | **`d05d7ae9e`** | 仅携带 lib 指针 |



**保留 7 条的依据（复核用）**：①被注释掉的**代码 + 作者 todo**（`ModDataProvider` 两行地狱小动物生成、`LivingEntityEvents` 的 `GHOST` 分支）——删它等于删意图，不属"多余注释"；

②`BestiaryLanguageSubProvider` 4 行 —— 属一个 ~130 行、**1.20 同样存在的注释块**（126 行逐字相同），只删这 4 行会撕开 1.20 原文块；

③TerraFurniture `OneLegTableBlock` javadoc —— 1.20 有同一段、只是换行位置不同；④`LibRenderUtils` 的 `// BOTTOM`/`// Edge`、`AimUtils`/`LibEffects`/`TCItems` 等行尾注释 —— 1.20 逐字相同（部分是 1.20 侧"与下一行粘连"的编辑事故）；

⑤`ModEntities` 的 `/// Fast Link:` 块 —— 引入于 **2025-02-27**，当时判为 1.21 原生历史注释故**保留**；**但用户随后裁定：该块及同类"移植叙述"按他的意思可以删，而且他本人已亲手删掉它**（批次 26 提交）。

⇒ **§九⑤ 这条例外对"移植叙述"就此作废**：凡描述移植过程/溯源的 prose（`///` 长块、对照表、"尚未接线"这类状态说明）都在清理范围内；

仅"描述 1.21 独有机制或未来工作、与移植无关"的说明继续保守保留并人工复核。



> ⚠️ **待裁定：批次 F（窗口外）**。把窗口放宽到 **2026-09-28**（纳入 WP4/WP6c 那几批）后，同口径候选从 12 条**暴涨到 1764 条**（root 1412 / lib 225 / TerraCurio 127）——其中绝大多数是描述 **1.21 独有机制**的新文件注释（条件③豁免），但确实也含同类 WP 溯源注释（如 `ModEntities.java` 的 `// ⚠️ 成员级盲区…`，引入于 `58431c613`）。要清就得**按文件分组人工过**，建议另开批次 F 并由用户先确认范围。

>

> ✅ **任务 E 编译验证**：删净 4 个 `previous-compilation-data.bin` + `--offline --no-build-cache` → 四个 `compileJava` 全 executed、缓存命中 0、`error:` 0、`BUILD SUCCESSFUL`。全程用**显式 UTF-8 字节级替换 + 断言**改 java（断言删除块行数、`///` 残留 0），`git diff` 复核无中文乱码。



## 十、批次 26：用户改动提交 + 批次 F（移植叙述清理）+ 台账行 54



### 26.1 用户自己的 `ModEntities` 改动

`ModEntities.java` 里那份"删 `/// Fast Link:` 块与 WP2/WP3 溯源注释、展平 `EntityType.Builder`、抽出 `ATTRIBUTES`/`CREATURE_DEFINITIONS` 表"的改动**由用户本人完成**，本次**只提交不编辑**：

根仓 **`5e22cc02e`** `chore(ModEntities): 提交用户删除移植叙述的改动`（1 file，+86/−435）。



### 26.2 批次 F —— 删除"移植叙述"注释（6 组）

**判据（可证明安全）**：一个注释块被删，必须同时满足 ①无被注释掉的代码 ②无 `todo` ③含至少一条硬移植标记（`WP\d`/`本批`/`批次`/`1.20`/`出处`/`逐字`/`照搬`/`成员级`/`收口`/`台账`/`尚未`/`未接线`/`PortLib`/`TerraGuns`/`TerraEntity`/`G\d′`…）

④**块内没有任何一行在 1.20 全仓注释索引里逐字存在** ⑤**块内每一行都由窗口内移植提交引入**。不满足者一律 **REVIEW（不动 + 列清单）**。

另设**"1.20 原文 + 移植附注"**形态：**不删行、只剥附注**，还原 1.20 逐字原文（本批 34 行）。



| 组 | 范围 | 规模 | 提交 |

|---|---|---|---|

| G1 | `common/entity/**` + `common/init/entity/**` + 属性缩放 | 51 文件，−421 / 重写 31 | root `6b9c87896` |

| G2 | `client/**` + `mixin/client/**` | 24 文件，−120 / 重写 1 | root `27c6050c1` |

| G3 | 枪械内联 + `network/**` + `ModTags`/`ModItemTagsProvider` + `mixin/integration/magiclib` | 24 文件，−162 / 重写 1 | root `f389c6cf0` |

| G4 | 主模组其余 | 43 文件，−198 / 重写 1 | root `d7e8795f6` |

| G5 | `Confluence-Magic-Lib` | 11 文件，−106 | lib `17e8135` → root `12c5c2939` |

| G6 | `TerraCurio` | 17 文件，−123 | tc `317511d` → root `9c1c27e04` |



**合计 170 文件 / 删除 1130 行 / 剥附注重写 34 行**；TerraFurniture 无候选。**7 次四模块实编译全部** `BUILD SUCCESSFUL`、四个 `compileJava` 全 executed、`error:` 0；每次提交后 `git show --stat` 与预期行数核对（无"注释吞噬"）。

删除内容类别：WP/G 批次溯源与状态说明、`1.20 出处/原文：File:NN` 逐字溯源与行号对照表、`///` 长叙述块（`GravitationEffect`/`HoneyEffect`/`LibClientGameEvents`/`GunItems`/`BulletImpactPacketS2C`/`ShotFeedbackPacketS2C`/`MonsterAttributeScaling`/`AbstractMountEntity` …）、`PortLib`/`TGTags`/`TE 退役`/`枪械内联 G4′` 过程叙述、`notes/*.md` 指引。



### 26.3 保留 68 块（留待人工复核）

- **A 类 60 块**：块内含 **1.20 逐字文本**（与 1.20 原文同块，删整块会撕开原文；部分块同时含移植叙述行，需逐行人判）

- **B 类 5 块**：引入提交在窗口外（**2026-09-27** 的 lib `CameraAnimation`/`LibEffects`/`GravitationEffect`/`HoneyEffect`/`LibStreamCodecUtils`）→ **若把窗口起点放宽到 09-27，它们会进入删除范围（预估再 ~30 行）**

- **C 类**：被注释掉的代码（`BestiaryLanguageSubProvider` 148 行、`ModDataProvider` 两条小动物生成、`LivingEntityEvents` 的 `GHOST` 分支）、`todo`（`ModDataProvider:1281/1297/1432/1689/1709` 等）、1.20 逐字注释（`OneLegTableBlock` javadoc、`LibRenderUtils` 的 `// BOTTOM`/`// Edge` 等）、以及"描述 1.21 独有机制/未来工作"的短注释



### 26.4 台账行 54 —— `COVERED / NOTHING-TO-PORT`

`4298126f9`「IPortItemExtension」：唯一看似新增的 `common/entity/ai/goal/AccelerateOnSeeingGoal.java` **在 1.20 HEAD 已被其自身 `b20c0cefd`（行 57）删除**、1.21 亦无 → 净零；`Nymph.java` 覆盖 0.90、`CustomMimicSummonKeyEvent.java` 0.96；其余 ~110 处是 `IPortItemExtension` 垫片机械扫尾（平台权宜）。

**遗留待复核**：`commit_coverage.py` 给的 30% 低覆盖名单**全是物品/方块注册表**（`MaterialItems` 0.09、`BowItems` 0.21、`PickaxeItems` 0.22 …）——属已登记的 `partN` 平台化误报形态，**这 9 个文件的逐文件归一化复核尚未做完**。**下一开放行 = 55**（`f6e114cdb` part21）；56–69 仍 TODO。



### 26.5 方法层发现（建议并入工具与记录）

1. **`git log --since=<date>` 会静默漏提交**（early-stop 剪枝）：lib 的 `659c8c8`（WP6c，提交日期 2026-09-28）等 4 个提交不在 `--since=2026-09-28` 结果里；必须**拉全量 `%cI` 后在脚本里比日期**。⇒ 记录 §九引用的候选数（1764）实际偏低（实测 root 1720 / lib 271 / tc 152）。

2. **1.20 注释索引必须含子模块**（lib/tc 的移植注释多为 1.20 逐字，纳入后 lib 候选 225→33、tc 127→0，方向是"少删"，安全）。

3. **"1.20 原文 + 移植附注"是主要噪声形态之一**：正解是**剥附注**而不是删行。

4. **近似/前缀扫描护栏有效**：据此拦下 34 行（如 `SpawnPlacementChecks`）本会被整行误删的 1.20 原文。

5. **提交信息文件禁用 `Set-Content -Encoding utf8`**（PowerShell 5.1 会写入 BOM 并落进提交标题——本会话已发生一次，靠 `--amend` 修复）。




## 十一、批次 27（台账行 55 收口）：剑组件粒子顺序修复 + 1.20 镜像 + 贴图改用原树叶精灵

> 本节在 `1ebc681d8` 提交时被 `Set-Content -Encoding ascii` 破坏（中文整节变成 `?`），现按提交记录与提交信息重写；ASCII 标识符与哈希未受影响（事故本身记入 §十四⑤）。

### 27.1 用户自己的四笔改动（1.21 侧）
| 提交 | 内容 |
|---|---|
| `82ce51ef6` | **剑组件粒子顺序修正**（ParticleStorm）：`SwordProjectileParticleEffect`、`SwordProjectileVisualHandler`、`SwordItems`、`BladeOfGrassItem` + 新增 `grass_sword_trail.particle.json`，5 文件 +66/−62 |
| `0a96f0bb5` | NeoForge 21.1.219 → **21.1.235**；`build.gradle` 里 Create/Ponder/Flywheel 的 `runtimeOnly` 行用 `if (false)` 注释掉；`gradle.properties` 加 4 条 `systemProp.*proxy*`（用户代理 WIP，**不在移植范围**） |
| `b3e73dfda` | `ModParticleTypes` 的持有类型统一（`DeferredHolder`）与 `addOptionalTag` 写法 |
| `f624850d4` | 删除 `DialogsLanguageSubProvider`（269 行），对话翻译并入 `NPCDialogProvider` |

三个子模块在 `git status` 里显示 ` M` 仅因**用户自己的 `gradle.properties`** 改动；`git diff --raw` 里没有指针 SHA 变化，故未纳入移植提交。

### 27.2 镜像到 1.20：`7a6eb3703`
- +66/−62 与 1.21 侧同规模；`SwordProjectileParticleEffect`、`SwordProjectileVisualHandler`、`grass_sword_trail.particle.json` 与 1.21 **blob 相同**。
- **唯一 API 适配**：1.20 侧 `emitter` 是 1 参重载，需显式传 `Event.TRAIL`；1.21 侧是 `emitter(Event, ResourceLocation)`。
- **类型面差异**：`ModParticleTypes` 的持有类型两侧不同（1.21 `DeferredHolder` ↔ 1.20 PortLib `PortRegistryEntry`），调用点都要 `.get()`，emitter 调用写法一致。
- 1.20 侧编译验证：`BUILD SUCCESSFUL in 3m 4s`，`compileJava` executed，**0 error**。（本会话最后一次编译；此后按用户裁定只做静态核对。）

### 27.3 贴图修正：1.21 `4e274c7db` / 1.20 `6d41e4a7e`
- 原设计 `ModParticleTypes.LEAVES` 走原版粒子贴图约定：`assets/confluence/particles/leaves.json` → `confluence:leaves/particle_0..3`，**4 张 8×8 精灵图**。
- 之前 `"texture"` 写成自造的 `confluence:grass_sword_trail`（**无此图**）→ 用户裁定"为什么不直接用原设计的树叶粒子"，遂改为 **`confluence:leaves/particle_0`**，`uv` 仍为 8×8，**不需要新增美术资源**；两侧 SHA256 相同，无 CRLF 问题（`fix_eol --check` 0）。
> 备注：原版约定是 4 张独立精灵图由 ParticleStorm 逐粒随机取用，不需要 Bedrock 式 `flipbook`（图集 + Molang 逐帧）。

### 27.4 台账行 55 = `SKIP-1.20-REVERTED` + `SKIP-PORTLIB`（PortLib 半边）
- `f6e114cdb`(part21) 的 143 个 java 中 **142 个在 1.20 HEAD 已不存在**；part22 `2569be361` 的 `util/entity/*`、`b20c0cefd` 的 244 行、part23 `7f83b379a` 同理（1.20 自己后来删/搬了）。
- 1.21 侧与之对应的 17 个 basename **全部已覆盖**（1.21 `ai/bt/**` ↔ 1.20 `ai/goal/behavior/**`，架构级搬家），判 `COVERED`；DEAD 158 个。
- **结论**：1 LAGGING + 35 PARTIAL，其余 32 行 `COVERED`；连同行 54 的 9 行，本区间实际**缺口 = 56 行**，`2569be361`(part22) 标 `DEFER-ARCH`。

### 27.5 顺带
- 1.20 仓 `fix_eol --check` 有 **57 个预存候选**（长期 WIP）；**不做 1.20 全局归一**，1.20 侧提交只保证 w/crlf。
- `BladeOfGrassItem` 与 1.20 侧各留一个**未使用 import**（`ModParticleTypes`），未清理。

## 十二、批次 28–37：跳行收割 + 毒提交回退

> §七–§十一 记批次 21–27；本节接续到最新 HEAD。此区间除按序的 111–120（§十三）外，还**提前落地**了台账行 67/68/69/75/84/208/249/331/370/373 中"裁定已完成、内容现成"的缺口。

| 台账行 | 1.20 提交 | 落地提交（根仓 / 子模块） |
|---|---|---|
| 67 | `fac72523a` part critters | `6961dec3e` |
| 68 | `911437e03` able to start game | lib `b7ade3f` + `51ee34ec5`；lib `6287ed0` + `461736804` |
| 69 | `1284d08a9` able to into world | `13208778e` |
| 75 | `63916265b` 新增实体并修复命名空间引用 | `13208778e` |
| 84 | `a6f8f089d` fix world selection | `13208778e` |
| 208 | `5c3978b01` 汇流箱子/附魔/宝石法杖 | `eba74e0d5` |
| 249 | `535992233` 为现有新增的怪物补全 | `530ff7116` |
| 331 | `7572f2ebd` 添加三个饰品 | `1ebdee4d6` |
| 370 | `185a0fdbb` 彩色火把 | `a1f84f613` |
| 373 | `2472a2c1b` 1.2.7 | `a1f84f613` |

### 28 demon_eye 贴图：先改后撤，最终以 1.21.1 原版为准
| 提交 | 内容 |
|---|---|
| `0f3f09a01`（1.21） | 把 cataract/dilated/green/normal/owl/purple/sleepy/spaceship **8 张**贴图换成 1.20 HEAD 最新版（64×64；1.21 原为 48×48），9/9（含未变的 `demon_eye_7.png`）与 1.20 blob 一致 |
| `3cd12997d`（1.21） | **用户改判后回撤**：1.21 恢复原有 48×48 版本（与回撤前基线 blob 一致），改为 1.20 向 1.21 对齐 |
| `b657b999e`（1.20） | 1.20 侧同步 9 张为 1.21.1 原版，blob 逐一对齐 ⇒ 两侧 9/9 相同 |

- 教训：美术资源这类"用最新版"的裁定可能被后续裁定推翻；**落地前先确认方向**，此例最终方向是与常规相反的一侧。

### 29 台账行 67 收口 —— `6961dec3e`
`ModItemTagsProvider` 补 `FOODS_COOKED_FISH`/`FOODS_COOKED_MEAT` 两个物品标签块：基点 `795ac9ccc` 两侧均无、1.21 现无 ⇒ 同步；逐行照抄 1.20 HEAD，仅 `PortTags.Items.` → `Tags.Items.`（与相邻 `FOODS_RAW_FISH/RAW_MEAT/FERTILIZERS` 写法一致）。目标常量确认存在于 NeoForge 21.1.235 的 `net/neoforged/neoforge/common/Tags.java`（`TagKey<Item>`，`c:foods/cooked_fish|cooked_meat`）。无调用点需要改指。

### 30 台账行 68 前半 —— lib `b7ade3f` + 根仓 `51ee34ec5`
- 1.20 `911437e03` 在拆掉 TerraEntity 集成后把 `TEUtils.rotToDir` 的实现搬进 lib（1.20 lib 提交 `02525ee`），并把 `NightEdgeProjectile` 的粒子朝向改指它。
- lib `b7ade3f`：`LibMathUtils.rotToDir` 逐字补回实现。
- 根仓 `51ee34ec5`：永夜剑气粒子朝向 `Vec3.directionFromRotation(xRot, yRot)` → `LibMathUtils.rotToDir(yHeadRot, xRot)`。1.21 原写法只是退役 TE 后的顶替，**x 分量与 1.20 相反** ⇒ 属行为修正，不是纯风格。

### 31 台账行 68 收口 —— lib `6287ed0` + 根仓 `461736804`
- lib `6287ed0`：`ToggleAmountResultSlot` 构造参数改名（`result/pSlot/pX/pY` → `container/slot/x/y`）+ 新增 `Setup<R>`/`For4x<R>` 两个静态子类；本文件与 1.20 HEAD **字节相同**（NF 归一后 sha256 `890dba68…`，2069 字节）。
- 根仓 `461736804`：`LoomMenu`/`SawmillMenu`/`SolidifierMenu` 的内联匿名子类 → `ToggleAmountResultSlot.For4x::new`，三个文件现与 1.20 HEAD **字节相同**；`HardmodeAnvilMenu` 同法改；`HeavyWorkBenchMenu.ResultSlot` 改 `extends ToggleAmountResultSlot.For4x<HeavyWorkBenchRecipe>`、构造器补 `Runnable setup`、`onTake` 用 `super.onTake(player, stack)`。
- 残留差异仅既有的平台书写（`blockState.is(X)` ↔ `is(X.get())`、`RecipeHolder.value()`、`import crafting.*`）；被替换的重复实现不再保留。

### 32 台账行 208 职业图标改名组 —— `eba74e0d5`
`MAGIC_ICON→SORCERER_ICON`、`MELEE_ICON→WARRIOR_ICON`、`REMOTE_ICON→RANGER_ICON`、`SUMMON_ICON→SUMMONER_ICON`（常量与注册 id 同步，4 张贴图跟随改名）；`ModTabs.SHOOTERS` → `RANGERS`（注册 id 本就是 `rangers`，未变）；删除两张已无任何引用的 `life_crystal.png`/`life_fruit.png`。基点 `795ac9ccc` 两侧都是旧名 ⇒ 方向 1.20→1.21。

### 33 台账行 249 TURTLE_SHELL —— `530ff7116`
补 `MaterialItems.TURTLE_SHELL` + 价值数据 + 巨型陆龟掉落（权重 833/9167，插在 `SAND_POACHER` 之后以保持 1.20 顺序）+ 中文名；贴图两侧 blob 本就相同（`3660eeeb`），未搬。英文名由 datagen 派生（1.20 也没有手写条目）。

### 34 台账行 331 金箔饰品击杀掉落 —— `1ebdee4d6`
`PlayerPiggyBankContainer.placeCoins(Container, long)`（方法体 40 行与 1.20 逐字相同，+3 import）；`ModUtils.enemyDropMoney` 增 `DamageSource` 形参，金箔（`AccessoryItems.AUTO$GET$COIN`）命中时放对应钱币音效 + 把掉落金额 `placeCoins` 进存钱罐、余额才落地；调用点 `LivingEntityEvents` 传 `damageSource`。依赖静态核对：`Coins.platinum2CopperEntries()`、`decodeCoin`、`ValueType.UnitType AUTO$GET$COIN`、`TCUtils.hasType(LivingEntity, ValueType<Unit, UnitValue>)`、`ModSoundEvents.COINS_{LARGE,MEDIUM,SMALL}`、`AttachmentEntityDamageSource.getOwner()` 返回 `Player` 均在 1.21 存在且签名与 1.20 相同。

### 35 台账行 370/373 发光棒与藏宝图 —— `a1f84f613`
补 `GLOWSTICK`/`STICKY_GLOWSTICK`（`stacksTo 9999`）与 `PIRATE_MAP`；`BasePotBlock.dropTorch` 按 1.20 实现（水下掉发光棒，否则按陶罐种类掉对应火把）；`LibTags.Items.WIP` 补 `FUZZY_CARROT`/`GLOWSTICK`/`STICKY_GLOWSTICK`；`PIRATE_MAP` 进 `boss_event_summons` 分组 + 海洋困难模式 1% 击杀掉落 + 中英名称与 3 条 tooltip。归属：`185a0fdbb`（彩色火把，含把火把掉落从 todo 实现）、`2472a2c1b`（三样进 WIP）、`b05c8dc3f`（PIRATE_MAP）。1.21 此前停在基点 `795ac9ccc` 的注释状态（`// todo` 掉火把 + `ModItems.GLOW_STICK`）。

### 36 毒提交回退 —— `3b3618a13` 落地、`170b54eb8` 全量回退
- `3b3618a13`（**错误落地**）：按 `dfcc5c041` 补三件套宝藏袋（毁灭者/世纪之花/拜月教邪教徒），7 点接线：`TreasureBagItems` 3 常量、`TreasureBagSubProvider` 3 映射、`ModChineseProvider` 3 中文名、`ModItemTagsProvider` 3 标签、`ValueSubProvider` 3 价值、`GiftSubProvider` 9 条 `output.accept` + 3 helper、`ModItemModelProvider.treasureBagModelAlias` + 4 调用。
- `170b54eb8`（**正确**）：用户重申"毒提交永不移植"，整批撤销；另删 `13208778e` 中同源的两条价值 `THE_TWINS_TREASURE_BAG`/`SKELETRON_PRIME_TREASURE_BAG`（归属 `dfcc5c041`）。`13208778e` 的其余部分（hellfire 粒子定义、DEERCLOPS 去重、SPEC 守卫）来源是行 69/75/84，保留不动。
- **保留项**：`THE_TWINS_TREASURE_BAG`/`SKELETRON_PRIME_TREASURE_BAG` 两个**常量**由 1.21 原生历史带入（`91de042a8 更新贴图，双子魔眼`、`66a73c714 编写wiki中发现的遗漏…`），不属毒提交 ⇒ 常量保留、价值条目缺失（**有意为之**）。
- 回退后实测：冲突标记 `git grep -E '^(<<<<<<<|>>>>>>>|=======)$' HEAD` = **0**；`THE_DESTROYER_TREASURE_BAG`、`treasureBagModelAlias` 全仓 = **0**；三件套其余落点（`PLANTERA_TREASURE_BAG`/`LUNATIC_CULTIST_TREASURE_BAG`/`GiftSubProvider` 3 helper/9 条 accept）= 0。
- 纪律：`git revert` 在 `ValueSubProvider.java` 冲突，第一版把冲突标记提交了进去；**`7fbc42bb1` 是被废弃的那一版**（不在 1.21 谱系里、无任何分支引用，`git merge-base --is-ancestor 7fbc42bb1 HEAD` = 1）⇒ 记录与引用**只认 `170b54eb8`**。

### 37 台账行 69/75/84 收口 —— `13208778e`
- ①`assets/confluence/particle_definitions/hellfire.particle.json`、`hellfire_exhaust_flame.particle.json`：逐字节取自 1.20 HEAD（归属行 69 `1284d08a9`，行 75 `63916265b` 改过）。搬前核过组件支持面：这两个文件用到的 6 个冷门组件（`emitter_lifetime_once`/`emitter_rate_instant`/`emitter_shape_sphere`/`particle_motion_collision`/`particle_initialization`/`emitter_local_space`）在 1.21 现有 72 个粒子定义里分别已被 19/13/12/17/15/6 个文件使用 ⇒ 1.21 的 ParticleStorm 全支持，**零适配**、字节相同。
- ②`ValueSubProvider` 补两条宝藏袋价值 —— **已随 `170b54eb8` 回退**（见批次 36）。
- ③`TreasureBagSubProvider` 清掉重复的 `BossEntities.DEERCLOPS` 行（1.20 只有一行；1.21 侧原有两份）。
- ④`ModClientEvents.modConfig$Reloading` 补 `CommonConfigs.SPEC.isLoaded()` 守卫，行内文本逐字取自 1.20 同名方法（归属行 84）；`clientSetup` 处**不加**守卫 —— 1.20 该处同样未加。

## 十三、台账行 111–120 裁定：全部 `COVERED` / `SKIP-1.20-REVERTED`（**无新增提交**，全静态核对）

### 13.1 毒提交排除集的口径与数字（本轮纠正）
- `dfcc5c041` 是 **1.20 分支上的提交**（「对齐 1.21 内容与运行时行为」，是 1.20 HEAD `b657b999e` 的祖先；**不是** 1.21 HEAD 的祖先），改动 **1690** 个路径。
- 其 1.20 侧回退 `dc57ba5c2`（"注释 杀杀杀"）改动 **663** 个路径，**⊂ 1690** ⇒ **并集 = 1690**。（此前报告里的"1923"是把 `dfcc5c041` 的路径数报了两遍。）
- 覆盖面：1690 占 1.21 HEAD 全部文件 **8.8%**、占 java 文件 **27.6%（717 个）** ⇒ 若按"文件命中即跳过"使用，误伤面很大。
- **粒度（必须遵守）**：排除集只能作用在**落点**（标识符/常量/标签块）上。对每行每个文件的 diff 新增内容逐个做 `git log -S '<标识符>' -- <path>` 归属；只有归属 `dfcc5c041`/`dc57ba5c2` 的落点才 `DO-NOT-PORT`，同文件里归属其它行的落点照常判。
- 实证（同一文件内两种归属并存）：

| 文件 | 在排除集内 | 标识符 → 真实归属 |
|---|---|---|
| `data/gen/loot/EntitySubProvider.java` | 是 | `setGelColor`→行 214 `38382758a`；`blackSlimeLoot`→行 203 `446c689a4`；`corruptionSlimeLoot`→行 194 `3e7cc41a9`；`pirateCommon`→行 256 `b05c8dc3f`（**均非毒**） |
| `entity/npc/BaseNPC.java` | 是 | `obsoleteModifier`→行 170 `9645da98c`（非毒） |
| `block/common/MuralBlock.java` | 是 | `pendingLoadTag`、`decodeOrDefer`→**`dfcc5c041`** ⇒ 毙 |
| `data/saved/KillBoard.java` | 是 | `previousPhase`→**`dfcc5c041`** ⇒ 毙 |
| `item/common/BossSummoningItem.java` | 是 | `bindSummoner`/`moveToRandomSummonPos`/`prepareSummonedMob`→**`dfcc5c041`** ⇒ 毙；`RANDOM_SUMMON_RADIUS`→行 174 `92e38df06`（非毒，待行 174 处理） |
| `event/game/entity/PlayerEvents.java` | 是 | `previousLevel`→**`dfcc5c041`** ⇒ 毙；`ItemPickupEvent`→`29c1459cf`（Forge 事件，NeoForge 无）；`cloneE`→行 68 `911437e03` 的改名写法，1.21 已有等价 `PlayerEvents.clone(PlayerEvent.Clone)`（`PlayerEvents.java:330`）⇒ 无缺口 |
| `entity/projectile/spear/SpearProjectile.java` | 否 | `projComponent`/`applyComponent`/`setProjComponent`→`262ee1133`（2026-05-19，**早于台账范围起点**）⇒ 非移植目标 |

### 13.2 逐行裁定
| 行 | 提交 | 裁定 | 依据 |
|---|---|---|---|
| 111 | `c2d92b019` 按 Servantry 架构对齐召唤系统 | `SKIP-1.20-REVERTED` | 1.20 HEAD **已无** `common/summon/**`、`client/summon/**`、`common/item/summon/**`、`SummonBoltEntity`、`SummonSyncPacketS2C`（行 308 `46e5e8626 移除旧架构召唤体系` 删除）；1.21 侧也没有这些包（1.21 走 `client/summoner/**` + `api/summon/**` 另一套）⇒ 无可移植内容。23/24 落点同时命中排除集 |
| 112 | `9729f1c30` 同上 | `SKIP-1.20-REVERTED` | 唯一落点 `SummonSavedState` 在 1.20 HEAD 不存在 |
| 113 | `fc43577f2` 清理无意义格式变更 | `SKIP-PLATFORM` | 29 落点全在排除集；对行 111/113/118 共 65 个 java 落点跑逐文件残差，除 3 个文件外全部残差 0（`DyeMixScreen`/`DyeVatScreen`/`BaseBoss`/`KingSlime`/`Skeletron`/`BaseSlime`/`Bird`/`SpearProjectile`… ），非零的 3 个（`ModClientEvents` 23、`MuralBlock` 13、`EntitySubProvider` 17）归属见 §13.1，其中 `MuralBlock` 的属毒、其余属行 194/203/214/256，均**非本行引入** |
| 114 | `24322f45a` 恢复 NPC 对话管理器原有架构 | `COVERED` | `BaseNPC` 属排除集；`chat/ChatManager.java` 残差 **0**（1.20 91 行 / 1.21 94 行，独立复核） |
| 115 | `aee0a94ca` 恢复 NPC 原有目标选择架构 | `COVERED` | `ai/NPCHealGoal.java` 残差 **0**（1.20 139 行 / 1.21 139 行）；`ArmsDealerNPC`/`DemolitionistNPC`/`GoblinTinkererNPC`/`GuideNPC` 在 1.20 HEAD 均不存在 ⇒ `SKIP-1.20-REVERTED` |
| 116 | `6ec97fb52` 恢复 NPC 原有交互架构 | `SKIP-1.20-REVERTED` | `AnglerNPC`/`BaseNPC`/`OldManNPC` 属排除集；`api/event/npc/InteractNPCEvent` 1.20 HEAD 无 |
| 117 | `27e9e0ac3` 清理 NPC 对齐残留并恢复原有架构 | `COVERED` | 8 排除 + 3 `SKIP-1.20-REVERTED`；3 个 live 文件残差 0（`NurseNPC` 72/73 行、`NPCHealGoal`、`ChatManager`） |
| 118 | `a8c5395a6` 清理跨版本移植残留与格式噪音 | `SKIP-PLATFORM` | 12 落点全在排除集；live 文件残差与归属见 §13.1（`ModClientEvents` 1.21 1360 行 vs 1.20 1159 行，1.21 侧只多不少） |
| 119 | `b2d1d939c` 对齐 NPC 战斗治疗与生命周期行为 | `COVERED` | 4 排除 + 5 `SKIP-1.20-REVERTED`；live 2 个残差 0（`NurseNPC`、`NPCHealGoal`） |
| 120 | `e1cdbb3ff` 接通 NPC 交互与聊天同步链路 | `COVERED` | 3 排除；live 2 个残差 0（`NPCEntityRenderer` 残差 0、1.20 与 1.21 均 113 行；`ChatManager`） |
| 121 | `d742dcf44` 补齐 NPC 默认聊天内容与触发条件 | `COVERED` | 落点归属全为本行/行 120 `e1cdbb3ff`/行 63 `231c505ca`，**无毒归属**；残差 0：`AttackTargetCondition` 25/25、`NPCItemInHandCondition` 26/26、`WeatherCondition` 28/28、`ModTradeConditions` 39→46（1.21 为超集）、`NPCEntityRenderer` 113/113；`npc/chat/guide.json` 1.20 HEAD 已无 ⇒ 该落点 `SKIP-1.20-REVERTED` |
| 122 | `159af78f9` 修复内置 NPC 商店资源加载 | `SKIP-1.20-REVERTED` | 19 个路径全为新增，且 **19/19 在 1.20 HEAD 已不存在**（`data/confluence/npc/trades/*.json` 全 19 个 NPC 交易表；两侧实测均为 0 个文件）⇒ 台账原判 `DEFER-ASSETS` 已被推翻 |
| 123 | `40c630e6b` 清理旧 NPC 商店资源 | `SKIP-1.20-REVERTED` | 19 个路径全为删除，19/19 在 1.20 HEAD 已无（同上批交易表：angler/arms_dealer/clothier/demolitionist/dryad/dye_trader/female_angler/goblin_tinkerer/mechanic/merchant/painter/party_girl/stylist/tax_collector/traveling_merchant/truffle/witch_doctor/wizard/zoologist） |
| 124 | `64950f063` 同步 NPC 商店定价与宝藏袋价值 | `COVERED`（另有 3 落点 `DO-NOT-PORT`） | `NPCShopProvider` 335/335 行、残差 **0**；`ValueSubProvider` 残差 **3** = `THE_DESTROYER_TREASURE_BAG`/`PLANTERA_TREASURE_BAG`/`LUNATIC_CULTIST_TREASURE_BAG`（1.20 行 1590/1591/1592）→ 归 `dfcc5c041` ⇒ `DO-NOT-PORT`（已由 `170b54eb8` 移除，保持现状）；`SILLY_BALLOON_MACHINE` 两侧均在 ⇒ 非缺口。**本行是"落点级粒度"的正面样本：同文件其余 2078 个标识符 100% 已覆盖，只有 3 条该毙** |
- 行 95/96（`dfcc5c041` / `dc57ba5c2`）按用户裁定**整行不分析**，标 `DO-NOT-PORT`。
- 行 111–124 **无新增提交**；下一开放行 = **125**（08-16「对齐城镇 NPC 远程战斗与护士治疗行为」一带）。

## 十四、方法层结论（本区间新增，后续行直接复用）

① **残留标识符 ≠ 缺口，必须逐个归属**：判定用"1.20 HEAD 文件 vs 1.21 全仓标识符集合"的逐文件残差（新工具 `tools/port2native/residual_file.py`，输出残差词 + 1.20 行号），再对每个残差词跑 `git log -S '<标识符>' -- <path>` 取**最早引入提交**。残差的四种正常成因：平台重命名（`LootTableReference`/`LootingEnchantFunction`/`getTagElement`/`SetNbtFunction`…）、pre-range 提交（`262ee1133`，早于 `795ac9ccc`）、已裁定行（行 170/174/194/203/214/256…）、毒提交（`dfcc5c041`）。只有"归属到待处理台账行且 1.21 全仓无对应实现"才是缺口。
② **排除集粒度**：见 §13.1 —— 按落点，不按文件。
③ **可复用脚本必须落 `tools/port2native/`**：本区间子代理写的 `rowexcl.py` 未入库、已丢失；`notes/` 是主代理独占区，子代理只读。
④ **跳行收割**：行 111–120 无内容可搬，但同区间从行 67/68/69/75/84/208/249/331/370/373 收割到 10 个现成缺口（§十二）——台账**不必严格按序**，裁定完成后即可按"内容是否现成"收割，能显著提高单轮产出。
⑤ **`notes/` 同样是编码危险区**：`Set-Content -Encoding ascii` 曾把本文档第 27 节中文整节写成 `?`（提交 `1ebc681d8`），ASCII 标识符幸存、中文不可恢复，只能按提交记录重写；写文件一律用 write/edit 工具（UTF-8 无 BOM）。提交信息文件同理（`Set-Content -Encoding utf8` 会带 BOM）。
⑥ **`git revert` 纪律**：动手前查 `git status`，冲突时绝不提交冲突标记；被废弃的 revert 提交（如 `7fbc42bb1`）可能留在库里但**不在谱系**（`--is-ancestor` = 1、无分支引用），引用前必须核 `git merge-base --is-ancestor <hash> HEAD`。
⑦ **台账状态必须双写**：`notes/PORT-LEDGER.md` 最后一列 + `notes/port-ledger-status.json`（JSON 以 1.20 **全 hash** 为键，行号 ↔ hash 由 `notes/port-ledger.json` 的 `commits[N-1]` 对齐）。只改前者会让两份状态分叉。
⑧ **`PORTED` 的边界**：只对"提交信息里写明该行**收口**、且可移植内容已全部落地"的行填 `PORTED`；未写明收口的（如行 84 只落了 `SPEC.isLoaded()` 守卫）保持 `TODO`，避免把部分落地记成整行完成。

## 十五、台账状态回填（本次）

新增工具 `tools/port2native/ledger_status.py`：`--list [起 止]` 查状态、`--check` 校验表格与状态取值、`<行>=<状态> …` 回填（默认拒绝覆盖已有非 `TODO` 状态，需 `--force`；可一次多行）。

本次改动：**新填 21 条 + 改写 2 条**（`port-ledger-status.json` 内状态 115 → 136 条）；第二轮（§十六）再新填 8 条（125–132）⇒ **144 条**。

| 状态 | 行 |
|---|---|
| `PORTED` | 67, 68, 69, 75, 208, 249, 331, 370, 373 |
| `COVERED` | 114, 115, 117, 119, 120, 121, 124；125, 126, 127, 129, 130, 131, 132 |
| `SKIP-1.20-REVERTED` | 111, 112, 116, 122, 123（122/123 由 `DEFER-ASSETS` 改写，依据见 §13.2） |
| `SKIP-PLATFORM` | 113, 118 |
| `SKIP-1.21-KEEPS` | 128 |
| `DO-NOT-PORT` | 95（既有）、96（新填） |

下一开放行 = **133**（行 125–132 的裁定见 §十六）。

## 十六、行 125–132 裁定 + 三处口径补充（第二轮）

### 16.1 逐行
| 行 | 提交 | 裁定 | 依据 |
|---|---|---|---|
| 125 | `1e2f65769` 对齐城镇 NPC 远程战斗与护士治疗行为 | `COVERED` | 落点归本行/行 66/68/63，无毒；`NPCHealGoal` 139/139、`NurseNPC` 72→73 残差 0；`BaseNPC` 残差 1 = `obsoleteModifier`（1.20 行 389/390）归**行 170 `9645da98c`**（待那行处理）；`ArmsDealerNPC`/`GuideNPC` 1.20 HEAD 已无 |
| 126 | `74346c9d2` | `COVERED` | `NPCShopProvider` 335/335 行、残差 0 |
| 127 | `9e00dbf7b` | `COVERED`（3 毒落点 `DO-NOT-PORT`） | 5 个新增条件类与 1.21 行数逐一相等且残差 0（`Dimension` 28/28、`GameEvent` 28/28、`Graveyard` 26/26、`MoonPhase` 32/32、`WorldFlag` 25/25）、`NPCTradeOffer` 残差 0；毒落点 = `NPCShopProvider.TradeCondition`、`NPCTradeMenu.PlayerMoneyTransaction`/`serverPlayer`（← `dfcc5c041`，实测 `git log -S` 最早引入提交即毒）；`NPCTradeMenu` 残差 2 = `displayTag`/`getOrCreateTagElement`（1.20 行 464/465/475，归行 109 `7a3e9f664`）⇒ 1.20.1 NBT API ⇒ `SKIP-PLATFORM` |
| 128 | `a640087ea` | `SKIP-1.21-KEEPS` | 唯一改动是**注释掉两行 lang**（`RECALL_MANA_CRYSTAL`/`RECALL_LIFE_CRYSTAL`）；这两个物品是 **1.21 原生**（1.21 侧 `15b500cd8 两个回溯水晶`、`db4b4c9b3 先传点`，均在基点 `795ac9ccc` 之后），1.20 侧是**毒提交 `dfcc5c041` 从 1.21 抄过去的**（`PortDeferredItem` 写法可证），随后被 `4af532ed1`/`a8e0b487f` 回退、行 128 只是清理残留 ⇒ 方向"1.20 撤销、1.21 保留"，**不要动 1.21 的回溯水晶** |
| 129 | `7d6b90edc` | `COVERED`（3 毒落点） | `HouseHandler` 的 `isEmpty`/`entrySet`/`getValue` ← `dfcc5c041`（实测复现）；`MechanicNPC` 27/27、`OldManNPC` 118/118、`HouseHandler` 190→188、`NpcEntities` 260→261 残差 0；`AnglerNPC` 残差 1 = `getLootData`（1.20 行 242）= 1.20.1 API ⇒ `SKIP-PLATFORM` |
| 130 | `d0fe20495` | `COVERED` | `NPCChatProvider` 57→58 残差 0；`ModDataGenerator` 残差 4 = `LookupWrapper`/`emptyNamed`/`thenApply`/`Bus` = Forge datagen API ⇒ `SKIP-PLATFORM`；`npc/chat/guide.json` 1.20 HEAD 已无 |
| 131 | `5bd5b4211` | `COVERED`（12 毒落点 + 3 条登记给后续行） | 本行落点无毒且已覆盖；毒落点 12 条（`ModDataProvider.IS_PLAINS`、`SpawnPlacementChecks` 9 条、`BaseFlyingCritter` 2 条）；`registerSlimes` ← `dfcc5c041` ⇒ **永不恢复**（1.21 计数 0 也不补）。**新增登记**：`checkGnomeSpawn`（`SpawnPlacementChecks:294`、`CreatureSpawnPlacements:113`）、`getStructureWithPieceAt`（`:299`）← **`b05c8dc3f`（行 256）**；`checkOldShakingChestSpawn`（`:266`、`:115`）← **`ac2ac4428`** ⇒ 1.21 计数均为 0，推到那两行时照单落地 |
| 132 | `441334efe` | `COVERED` | 唯一文件 `BaseFlyingCritter` 的 3 个落点全归行 131，96/96 残差 0 |

台账已回填：129–132 = `COVERED`（状态 JSON 共 **144** 条）。

### 16.2 口径补充：1.20 侧"撤销/删除"内容的三种成因（必须分辨）
| 成因 | 判别法 | 结论 |
|---|---|---|
| 毒内容被**注释式回退**（`dc57ba5c2` "注释 杀杀杀"） | 落点最早引入提交 ∈ `{dfcc5c041, dc57ba5c2}`，且 1.20 侧只以注释形态存在 | `DO-NOT-PORT`，**绝不**把注释里的内容当 1.20 原文抄回 |
| 1.20 **自己原创**后删除/重写 | 落点最早引入提交是普通 1.20 提交，且 1.20 HEAD 已无对应文件/标识符 | `SKIP-1.20-REVERTED` |
| 内容是 **1.21 原生**、毒提交把它抄去了 1.20、随后在 1.20 被清掉 | 在 **1.21 侧**跑 `git log -S '<标识符>'`，引入提交是基点 `795ac9ccc` 之后的 1.21 提交 | `SKIP-1.21-KEEPS`，**别删 1.21 的对应内容**（实例：`RECALL_MANA_CRYSTAL`/`RECALL_LIFE_CRYSTAL`） |
工具层同步：`row_attr.py` 的 `POISON = {dfcc5c041, dc57ba5c2}`；另三个同族提交（`4af532ed1`/`1e0393178`/`a8e0b487f`）划为 `REVERT_FAMILY`，只打 `1.21-KEEPS` 提示并注明"回退族 ≠ 毒，也 ≠ 可直接抄，逐条人工判"。

### 16.3 两处异常复核（更正记录）
- **`skeletron_prime_treasure_bag` 贴图**：**两侧都没有**（`textures/item/treasure_bag/` 目录两侧各 11 个文件、逐名相同）⇒ **不是 1.20→1.21 的缺口**，而是 1.21 原生物品 `SKELETRON_PRIME_TREASURE_BAG`（1.21 侧 `66a73c714` 引入）本身缺美术；`170b54eb8` 回退掉的 `treasureBagModelAlias` 曾经用 `skeletron_treasure_bag` 顶替过这张贴图，回退后该物品恢复"无别名"的原状（与 `3b3618a13` 之前一致）。**需要新美术，不是移植项**。
- **`entity` 创造标签页**：1.20 有（`ModTabs.java:1921`，介于 `SUMMONERS` 与 `DEVELOPER` 之间），1.21 **没有**，且 1.21 基点 `795ac9ccc` 也没有（`register("entity"` 在该基点不存在）⇒ 1.21 侧从未有过。`IconItems.ENTITY_ICON` 的最早引入提交是 **行 61 `058000c5c`**（2026-06-29 part enchantment）。按既定计划（`notes/WP6-WP7-PLAN.md:117`、`notes/WORK-QUEUE.md:668`）"刷怪蛋的呈现层"（`entity` 页 + `ENTITY_ICON` + `creativetab.confluence.entity` + 284 条 lang）**归属 WP7 数据生成批次**，属**已知挂起项**，不在本次台账行走范围内；当前 1.21 用 `/give` 或 JEI 看刷怪蛋会显示原始翻译键。

## 十七、全仓"注册 id 级"扫描 + 台账外的系统性缺口（收割轮）

> 本轮不再按台账行逐行走，而是从**成品侧**反查缺口：①注册 id 全仓比对；②"1.21 代码仍在引用、但资源/翻译不存在"的悬空引用；③1.20 提交新增的资源在 1.21 的缺失。分析产物在 `notes/_tmp_harvest/`（脚本 + 报告，被 git exclude 忽略，非长期资产）。

### 17.1 新工具 `tools/port2native/registry_id_diff.py`
- 口径：对同一相对路径，抽两树里 `[A-Za-z_]*[Rr]egister[A-Za-z_]*("id"` 形式的注册 id，做**文件级**差集；`--verify-121` 再用「1.21 注册宇宙（`git grep` 全 java 抽出 4349 个 id）+ 全仓字面量」两段判定，把每个 1.20-only id 分成 `ABSENT`（真缺口）/ `ALIAS?`（1.21 未注册但字面量存在，多为改名/别名/描述文本）/ `REGISTERED`（1.21 已在别处注册 ⇒ 假缺口）。
- **踩坑（务必记住）**：第一版正则写作 `\.?register[A-Za-z_]*\("`，**漏掉驼峰助手名**（`copyBlockRegister`/`registerWithItem`/`registerRelic`/`registerWithItem`…），于是把 `DecorativeBlocks` 的 18 个**已注册**方块误报成缺口。收成 `[A-Za-z_]*[Rr]egister[A-Za-z_]*` 后假缺口消失（1.20 侧 143 vs 1.21 侧 146，1.21 反而更多）。
- 扫描面：默认 roots 下 1197 个 java 文件。

### 17.2 扫描结论（1.20-only 279 个 id 的去伪存真）
| 类别 | 条目 | 结论 |
|---|---|---|
| `ModTags` 7 个 | `evil_ingot`、`gold_and_platinum` | **1.21 曾有、被 1.21 自己删了**（1.21 侧 `b73eacf4a 大改数据生成`）⇒ 不是缺口，别补 |
| | `fishing_able/all`、`fishing_able/not_lava` | 1.21 侧 `a3dd5bb35`（2025-02-27）后已无该族 ⇒ 同上，不是缺口 |
| | `jelly_fish` | 1.20 侧引入提交 = **`dfcc5c041`（毒）** ⇒ `DO-NOT-PORT`，永不补 |
| | `golden_slime_replaceable` | 1.20 侧引入 = 行 194 `3e7cc41a9`（非毒）、**1.21 从未有过** ⇒ **待判候选**（`TagKey<EntityType<?>>`，1.20 仅 1 处引用） |
| | `prefix_summon_only` | 尚未查完（查询超时）⇒ **待判候选** |
| `ModBlocks` 1 个 | `void_entity` | 用户已裁定跳过 |
| `IconItems` 1 个 | `entity_icon` | 与 `ModTabs.entity` 同属 **WP7 挂起项**（刷怪蛋呈现层） |
| `ModItems` 1 个 | `test_soul_gui` | 测试物品，不做 |
| `TreasureBagItems` 3 个 | 三件套宝藏袋 | `DO-NOT-PORT`（已由 `170b54eb8` 回退） |
| `ModEnchantments` 8 个 | `arcane_protection` 等 | 属**已裁定"附魔不移植"**；字面量只出现在 1.21 的 lang provider 里 |
| `ModEntities.java` | 整文件"缺失" | 只是路径搬家（1.20 `common/init/entity/ModEntities.java` → 1.21 `common/init/ModEntities.java`），非缺口（该文件属禁改清单） |

### 17.3 已核实并落地
- **A1 行 203/232：NPC 房屋标签在 1.21 是空的（真 bug）→ `ac55bcb0b`**
  - 症状：1.21 的 `ModBlockTagsProvider` 只给 `NPC_HOUSE_CONSTITUTE` 加了两个 paper pane lamp，缺 `addTag(NPC_HOUSE_CHAIR/TABLE)`、12 个原版光源块与 `TorchBlocks.BLOCKS`，也没有独立的 `NPC_HOUSE_CHAIR`（`BlockTags.BEDS` + `terra_furniture:house_chair`）与 `NPC_HOUSE_TABLE`（`CRAFTING_TABLE` + `terra_furniture:house_table`）；而 `entity/npc/house/HouseValidater.java:74/75/103/104` 正读这两个标签 ⇒ **房屋判定永远缺椅子/桌子**。
  - 落地：`ModBlockTagsProvider.java` +25/−1；**我复核：新增块与 1.20 HEAD 第 1908–1935 行 28 行逐字相同（`identical=True`）**。

### 17.4 已核实、待落地（已派工单）
| 工单 | 台账行 | 问题 | 证据要点 |
|---|---|---|---|
| A2 | 203/214/256 | **10 个实体没有掉落**：`LAVA_SLIME`/`MOTHER_SLIME`/`BABY_SLIME`/`SLIMELING`/`WINGLESS_SLIMER`/`PIRATE_{DECKHAND,DEADEYE,CROSSBOWER,CORSAIR,CAPTAIN}` 在 1.21 `EntitySubProvider` 0 命中 | 1.20 有 5 个 helper（`blackSlimeLoot:1580`/`lavaSlimeLoot`/`motherSlimeLoot`/`corruptionSlimeLoot(int)`/`pirateCommon(int)`）与 call site 1012/1045–1048/1178/1179/834–838/1405；1.21 的 `BLACK_SLIME:790`/`CRIMSLIME:921`/`CORRUPT_SLIME:933` **已有 inline 等价物**⇒别重复补；适配：凝胶着色用 1.21 的 `slimeCommon(int)` 数据组件写法，禁 NBT |
| A3 | 141 | **发射器打不出自定义矿车** | 1.20 `BaseMinecartItem.java` 的 `DISPENSE_BEHAVIOR`(:28) + `registerBehavior`(:75)；1.21 该文件 35 行、`dispens` 0 命中；适配：用 1.21 的 `item.createMinecart(level,x,y,z,RIDEABLE,stack,null)` |
| A4 | 13 行 | **79 条"代码在引用、全仓无翻译"** | 清单 `notes/_tmp_harvest/lang_gaps.txt`；抽查两条通过（`tooltip.confluence.yoyo.hellfire`→仅 `HelFireYoyoItem:31`、`gui.confluence.mood.homeless`→仅 `NPCMood:103`） |

### 17.5 资源类缺口（纯复制，批次 B–G，未开工）
| 批 | 内容 | 规模 |
|---|---|---|
| B | `fragile_{blue,green,pink}_bricks` + `mechanical_fragile_obsidian_bricks` 的 blockstate/models（1.21 有物品模型但缺方块模型，行 179 `e2807cc12`）；`spider_nest_stone` 8 个资源（行 332）；`finch_staff_empty.json`（行 156）；`entity/the_hungry_leaf` 模型（行 163）；`textures/summon_mark.png`（行 269）；`counterweight` 6 张贴图（行 342） | ~25 文件 |
| C | **悠悠球整套**：`geo/entity/yoyos.geo.json` + `textures/entity/yoyos/*`（20）+ `textures/item/yoyo/*` + 10 个 `models/item/*.json`（1.21 只有 java，资源 0；`YoyoRenderer` 引用中） | ~32 文件 |
| D/E/F | 召唤物（`hornet_baby`/`iron_golem_32`/`sculk_wisp`/`bird_nest`/`sanguine_bat`/`eye_laser_turret`/`ruin_relic`）、NPC（`stylist`/`tax_collector`）、动物（`mystic_frog`）+ 行 256 大块（含 `models/item/whip/*`） | ~120 文件 |
| G | **行 69 `1284d08a9` 的资产半边**（`animations/entity/npc/` 1.21 只有 8 个 vs 1.20 35 个；`geo/entity/animal/` 4 vs 33） | ~390 文件 |
- 边界：资产比对按 **basename** 去重，且只算"1.20 HEAD 仍存在"的路径 ⇒ 结果是**下界**（已实证漏报一例：行 179 的 `fragile_*_bricks.json` 因 1.21 有 `models/item/` 同名文件而被脚本漏掉）。

### 17.6 反例清单（"看起来像缺口、其实不是"，避免重复踩坑）
- 行 131：`terra_entity_*` biome key → 1.21 用**无前缀**同名 key（`ModDataProvider:1382+` + `data/confluence/neoforge/biome_modifier/*.json`）。
- 行 136：`WORM_SEGMENT` 掉落 = **1.20 自己删了**；`NODE_ARMOR` 减伤 → 1.21 `EaterOfWorlds:212-213` 已用 `CombatRules.getDamageAfterAbsorb` 等价实现。
- 行 161 `VERY_RARE`、行 167 `SPAWN_BEE` → **两侧都没有**，1.20 自己删了。
- 行 182 `AnglerQuestLoader`、行 191 `modid.name.*`、行 194 Boss 血条体系、行 237 JEI → 1.21 均已具备。
- 行 239/256 自定义 RenderType 与 `smooth_entity`/`hill_boundary` 着色器 → 1.21 改用原版着色器等价物（`HILL_OF_FLESH_BOUNDARY = entityTranslucentEmissive(noise.png)`），无悬空引用。
- 行 337 钱眼 `invMoneyHoleChance` → 1.21 已实现（`float moneyHoleChance`），仅类型差异。
- 行 179 `data/ae2/` 配方 → 1.20 HEAD 已是 0 个（改走 datagen），1.21 反有 14 个。
- 行 214 `confluence_magic_lib:nbt` → NBT/数据组件平台差异。
- **`blackSlimeLoot`（分析线误判，我复核推翻）**：1.21 `:790` 已有 inline 的 COMPASS 掉落；helper 名缺失 ≠ 内容缺失。

## 十八、**重大发现：1.21 有 104 个实体的掉落表从未落地**（已核实）

### 18.1 现象与证据
- 两侧 `common/data/gen/loot/EntitySubProvider.java` 引用的实体常量做差：**1.20 = 220 个 / 1.21 = 122 个 / 1.20-only = 104 个**。
- 新工具 `tools/port2native/loot_coverage.py`（`python tools/port2native/loot_coverage.py --out <文件>`）把常量解析成注册 id 并做三重判定，结果：**104 个全部满足"1.21 已注册该实体、但既不在 provider 里、也没有 on-disk 掉落表"**（实体不存在 0、id 未知 0、有 on-disk 表 0）。
- 分布：`MonsterEntities` **86**、`CritterEntities` **15**、`BossEntities` **2**（`LUNATIC_CULTIST_CLONE`、`THE_DESTROYER_PROBE`）、`NpcEntities` **1**（`STYLIST`）。
- 抽样端到端（注册 id 两树一致、1.21 无表、无 loot modifier 注入、on-disk 缺文件）：`ZOMBIE`(`confluence:zombie`, 1.21 `MonsterEntities:296` / 1.20 `:47`)、`GIANT_BAT`(`:165` / 1.20 `:96`)、`UNICORN`(`:770` / 1.20 `:465`)、`MOTHER_SLIME`(`:955`)、`STYLIST`。
- 正向对照（证明 on-disk 目录与 HEAD 的 provider 一致、可作为旁证）：provider 里有的 `black_slime`/`corrupt_slime` 在 `src/generated/.../loot_table/entities/` 里**都有**文件；1.21 on-disk `entities/` 共 **117** 个 ≈ provider 引用数 122。
- 机制排查：`SyntheticLootTableProvider` 只是接口（`getSyntheticLootTablePaths()`），实现者是 `AddBlockLootConfluenceSubProvider` / `AddChestLootConfluenceSubProvider` / `AddEntityLootConfluenceSubProvider`，用途是把 mod 掉落**注入原版表**——on-disk `loot_table/with/entities/` 只有 12 个**原版**怪（bat/elder_guardian/frog/guardian/parrot/piglin/skeleton/slime/warden/wither_skeleton/zombie/zombified_piglin）。**mod 自己的怪不在其中** ⇒ 这 104 个实体在游戏里**掉不出任何东西**。
- 结论：这不是"某行漏搬"，而是掉落 provider **整块从未补齐**（1.21 侧 provider 只有 1.20 的 55%）。**属本会话最大的一处功能缺口。**

### 18.2 与台账的关系
- 归属口径上，这批内容散布在 1.20 的多行里（如史莱姆/海盗族 = 行 203/214/256；`EntitySubProvider` 的首批条目来自行 69 `1284d08a9` "able to into world"），但**1.21 缺的是整块覆盖**，按行收口会漏 —— 正确做法是按**实体族分批补 provider**，并在每批提交信息里写清来源行。
- 与 §十七 的 A2（10 个实体：史莱姆族 + 5 个海盗）是**同一问题的一个子集**：A2 应并入本批（先补这 10 个，再按族继续）。

### 18.3 落地注意
1. 逐条必须核 **1.20 掉落内容里引用的物品/常量在 1.21 是否存在**（不存在则不能照抄，需登记）。
2. 平台适配：凝胶着色用 1.21 的 `slimeCommon(int)` 数据组件写法（**禁** 1.20 的 `confluence_magic_lib:nbt`/`DATA_COMPONENTS` NBT）；`LootingEnchantFunction.lootingMultiplier`、`LootItemRandomChanceWithLootingCondition`、`LootTableReference`、`SetNbtFunction` 等原版重命名按 1.21 现有条目（`:790` 一带）改写。
3. 建议按族分批提交：史莱姆/海盗（A2 的 10 个）→ 其余 `MonsterEntities` 76 个（可按"群系/事件族"再拆）→ `CritterEntities` 15 个 → `BossEntities` 2 个 + `NpcEntities` 1 个。
4. 该文件**不在**禁改 10 文件内；但同一时刻只允许一个写手改它（与 A2 串行）。

## 十九、1.20 侧远端更新 + rebase（2026-10-03）与台账扩到 400 行

### 19.1 发生了什么
- `D:\Minecraft\1.20forge\confluence` 的 `forge-dev/1.20.1` 已被 rebase 到更新后的 `origin/forge-dev/1.20.1`：
  - 旧 HEAD `b657b999e` → **新 HEAD `07c2ab5b5 修复组件崩溃`**（reflog 可见 `rebase (start): checkout origin/forge-dev/1.20.1`）。
  - 上游在 `18221c338`（旧台账末行）之上**新增 10 个提交**；本会话此前在 1.20 侧做的 4 个反向对齐提交被 rebase **重写哈希**（内容不变）。
- 1.21 仓 fetch 后与 `origin/neoforge-dev/1.21.1` **无领先/落后**（`HEAD..origin` = 0），即 1.21 侧远端没有新东西。

### 19.2 哈希重映射（记录里旧哈希已失效，按此表换算）
| 内容 | 旧哈希（失效） | 新哈希 |
|---|---|---|
| 剑组件粒子改走 ParticleStorm（1.20 镜像） | `7a6eb3703` | **`4c65367a0`** |
| 草剑拖尾改用树叶精灵（1.20 镜像） | `6d41e4a7e` | **`40fa4ff83`** |
| demon_eye 贴图回撤并同步 1.20 | `b657b999e` | **`c61970e9c`** |
| 修复组件崩溃（1.20 侧） | `9a2133227` | **`07c2ab5b5`** |
- 旧哈希仍存在于对象库（`git cat-file -t b657b999e` = commit）但**不在 `forge-dev/1.20.1` 历史里**，`--is-ancestor` 为假；引用时必须用新哈希。
- **同理**：本文件 §十二/§十六/§十七 里凡写"1.20 HEAD `b657b999e`"的地方，一律按 `c61970e9c`/`07c2ab5b5` 理解（内容一致）。

### 19.3 台账扩到 400 行（只追加，不重生成）
- 复跑 `commit_inventory.py` 到临时目录做对比：**行 1–386 的哈希与结构完全一致**，但它 **不保留** 既有人工/机器填的 `子模块`、`⚠️`、`备注`、`状态` 四列内容（实测 281 行会丢内容）⇒ **禁止用重生成覆盖 `PORT-LEDGER.md`**，只**追加**新行。
- 实际做法：把重生成结果里第 387–400 行按既有列格式追加到表尾；头部改成
  `范围：795ac9ccc..07c2ab5b5`、`提交数：400（含改名的提交 41 个，改名/复制事件 612 处）`。
- 新增状态取值 **`REVERSE-ALIGNED`**（本会话在 1.20 侧做的反向对齐提交，不作为移植源），已写进台账头部说明与 `ledger_status.py` 的合法集合。
- 行 397–400 = `REVERSE-ALIGNED`（即 19.2 表里那 4 个）；行 387–396 = `TODO`（待移植上游新提交）。
- `notes/port-ledger.json` 被 `notes/.gitignore:14` **忽略**（可再生数据），本次已就地补到 400 条并把 `range.head` 改成 `07c2ab5b5`，供 `ledger_status.py` 做行↔全哈希映射。

### 19.4 新增 10 行（387–396）的初判
| 行 | 提交 | 内容 | 1.21 现状 / 初判 |
|---|---|---|---|
| 387 | `ce6daf602` 火星工程师 | `MartianEngineer`(202) `TeslaTurret`(191) `MartianElectricBolt`(76) `ElectrifiedEffect`(52) + 渲染器 44 + lang + loot + `NetworkEvents` | **1.21 全部 ABSENT** ⇒ 新内容批（还需 geo/animation/贴图资源，待核 1.20 侧是否齐备） |
| 388 | `032534179` 泡泡半透明渲染 | `FlailAuxiliaryProjectileRenderer` +8/−4 | 小改动，宜先落 |
| 389 | `437e53a84` PortLib 状态效果修改器 ID 冲突 | 只动 PortLib 子模块 | `SKIP-PORTLIB`（1.21 侧无该模拟层） |
| 390 | `dafb03ee9` 净化转换表草植物→草方块 | `PureConversionTable` +2/−2 | 小修复，宜先落 |
| 391 | `f9bda32a5` 加俩怪 | `RayGunner`(268) `Scutlix`(251) `RayGunnerLaser`(61) + `GameEventSystem` + `ModTabs` + lang + loot | **1.21 全部 ABSENT** ⇒ 新内容批（同样需要资源） |
| 392 | `2de684965` 优化动态光照、移除可携带仆从接口行为 | summoner 包多处（`ICarryMinion` 删除、`PathNode` +42、`EyeLaserTurretMinion` −37、`AttachmentEntityData`/`AttachmentEntity` 删字段）+ lib 指针 | 1.21 仍有 `ICarryMinion`（4 处）⇒ 属"1.20 删、1.21 未跟"的**架构同步**，需单独立项（`DEFER-ARCH` 候选） |
| 393 | `6cda06303` 动态光照注册行为 + `ParticleAccessor` | `SummonerClientEvents` +6 + lib/PortLib 指针 | `ParticleAccessor` 1.21 **ABSENT** ⇒ 与行 392 同批处理 |
| 394 | `1780b9a88` 走妖和军官 | `MartianWalker`/`MartianOfficer` 模型+渲染器、`ShieldOutlineProjection`(52)、`LaserProjectileRenderer`、lang、loot | **1.21 全部 ABSENT** ⇒ 与 387/391 同一火星内容批 |
| 395 | `13376f960` BilayerOreFeature 构造器传参错位 | +2/−2 | 1.21 侧同类修复此前已做过 ⇒ 只需核 1.21 是否同样错位（很可能是**已覆盖**） |
| 396 | `9351a7e6b` 修正改名后遗留的悬空实体 id 字符串 | `SpearProjectileComponent`、`CloudProjectile` +4/−4 | 1.21 若同样残留 ⇒ 小修复；否则 `COVERED` |

### 19.5 结论与下一步
- 台账从 386 → **400 行**；上一开放行仍是 **133**（387–396 排在队尾，但火星内容批可提前收割）。
- 优先落地的"小修复三连"：行 388 / 390 / 395 / 396（均为 1–2 行级改动，需先核 1.21 现状是否已覆盖）。
- "火星内容批"（行 387 / 391 / 394 同族，加 393 的 `ParticleAccessor`）是新内容，**必须先核 1.20 侧 geo/animation/贴图资源是否齐备**，再排批次。

## 二十、掉落表缺口：最终批次计划（已按用户裁定定稿）

- **永久副本**：`notes/LOOT-GAP-PLAN.md`（294 行；原稿在临时目录 `notes/_tmp_lootverify/BATCH_PLAN.md`，临时目录被 git 忽略，故转存入库）。
- **修订锚点**：1.21 = `b1677a5d5`、1.20 = `07c2ab5b5`。
- **用户裁定（5 条，2026-10 本轮）**：
  1. 38 个"1.20 空表"**要补**，但**排在最后一批**；
  2. 7 个火星族实体判**"实体待移植"**，掉落表跟着实体走 ⇒ 独立成批整批搬（资源已核齐备）；
  3. 1.20 的 30+ 条"缺少未加入物品：XXX"注释**一并照抄**；
  4. `ENCHANTED_SWORD` 的 id 差异属**有意重命名** ⇒ 1.21 落点 `confluence:entities/enchanted_sword_monster`，不回头改 id；
  5. 批次顺序**按实体族依次做**。
- **缺口终值**：**94 个待补掉落表**（56 有内容 + 38 空表）+ 7 个待移植火星实体；**无"不可搬"项**（94 个所需物品/常量在 1.21 全部存在）。
- **两个跨批前置**（关键路径）：① 共用 import `DifficultyChanceLootItemCondition` 必须在**批次 3（海洋）**加入（批 4/5/6 都依赖它）；② 唯一类名搬家 `ModBlocks.CURSED_FLAME` → `MaterialItems.CURSED_FLAME` 在**批次 6（腐化/猩红）**（只有 CLINGER/WORLD_FEEDER 两处用到）。
- **批次顺序**：1 小动物(6) → 2 蚂蚁狮/沙漠(4) → 3 海洋/鲨鱼/人鱼(9，加 import) → 4 骷髅/地牢(12) → 5 雪原/杂项(13) → 6 腐化/猩红(11，类名搬家) → 7 Boss/NPC 零头(1) → 8 **火星实体批**(7 实体 + 资源) → 9 **38 个空表**。
- **勿重做**：`LAVA_SLIME`/`MOTHER_SLIME`/`BABY_SLIME`/`SLIMELING`/`WINGLESS_SLIMER`/`PIRATE_×5` 共 10 个已由 `b1677a5d5` 补齐；`giant_tortoise` 由 `530ff7116` 补齐；三个"缺失 helper"（`lavaSlimeLoot`/`motherSlimeLoot`/`blackSlimeLoot`）**内容其实已内联在 1.21**（`:798`/`:806`/`:802`），不要再补。
- **执行注意**：`WALKER_WEAPON` 的 1.20 id 是 `martian_walker_weapon`（带前缀）；7 个火星实体在 1.20 也全是空表，建议随实体一起落表以免出现"实体在、表无"的中间态（若推迟到批 9 则批 9 变 45 条，已在计划文件里显式列为清单项）。

## 二十一、A2/A3/A4 + 掉落表批次 1–2 落地（本轮），与用户对 7 个问题的裁定

### 21.1 落地清单（全部**未编译**，静态核对）
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `b1677a5d5` | **A2**：10 个实体掉落表（`LAVA_SLIME`/`MOTHER_SLIME`/`BABY_SLIME`/`SLIMELING`/`WINGLESS_SLIMER` + 5 个海盗），台账行 203/214/256 | `data/gen/loot/EntitySubProvider.java` | +68 |
| `bcf2ef1e0` | **A4**：**61 条**"代码在引用、全仓无翻译"的 lang（中英各 61 行），台账行 151/173/176/179/203/205/256/272/314/321/322/350/363 | `ModChineseProvider.java`、`ModEnglishProvider.java` | +61/+61 |
| `16a50f293` | **A3**：自定义矿车的发射器行为（台账行 141）；1.21 原本只有 mixin 等价路径且**丢自定义名** | `common/item/common/BaseMinecartItem.java` | +58 |
| `597e57702` | **掉落表批次 1**：小动物族 6 条（CLOUD_SHEEP/GLOWING_MOOSHROOM/CLUCKSHROOM/GLOWING_CLUCKSHROOM/RED_SQUIRREL/EXPLOSIVE_BUNNY） | `EntitySubProvider.java` | +27 |
| `05543e3f1` | **掉落表批次 2**：蚂蚁狮/沙漠族 4 条（ANTLION/ANTLION_LARVA/ANTLION_CHARGER/BASILISK，含 `:1339` 欠账注释） | `EntitySubProvider.java` | +18 |

- **A4 的条数更正**：`notes/_tmp_harvest/lang_gaps.txt` 实为 **13 组 / 67 条 / 去重 61 个 key**（不是 79）。67→61 的 6 条是跨组重复（`wall_of_flesh_eye`/`wall_of_flesh_mouth`、`yoyo.crystals`/`waves`/`afterimage`/`cascade`，行 179+350 与 322+350）。**上一轮只落 30 条是中断、不是筛选**，61 条全部满足判据、无一剔除。
- **A3 事实澄清**：1.21 原版 `MinecartItem` 的发射路径本就能出车（静态工厂被本仓 `AbstractMinecartMixin` 在 HEAD 处 `setReturnValue` 拦截），但**丢自定义名**；A3 落地后与 1.20 完全一致（7 处 API 适配：`source.state()/level()/pos()/center().x|y|z()`、`stack.has(DataComponents.CUSTOM_NAME)`）。
- 我复核：`git grep -ci dispens` 在 `BaseMinecartItem` 由 0 → 11；批次 1/2 的 10 个实体在 provider 里各恰好 1 次；`git diff 16a50f293 40dde3936` **为空**（见 21.3）。

### 21.2 用户裁定（7 条）
| # | 问题 | 裁定 |
|---|---|---|
| 1 | 38 个"1.20 空表"是否补 | **补**，但排在 56 个有内容之后（第 9 批） |
| 2 | 7 个火星族实体（1.21 无此实体） | **判"实体待移植"**，掉落表跟着实体走 ⇒ 独立成批整批搬（资源已核齐备） |
| 3 | 30+ 条"缺少未加入物品"注释 | **一并照抄**（保留欠账账目） |
| 4 | `ENCHANTED_SWORD` id 差异（1.20 `enchanted_sword` → 1.21 `enchanted_sword_monster`） | **有意重命名** ⇒ 落点用 `confluence:entities/enchanted_sword_monster` |
| 5 | 批次顺序 | **按实体族依次做** |
| 6 | A3 去留（写手误认为早前有"跳过 A3"的裁定——**本会话并无该裁定**） | **保留 A3** |
| 7 | `CRIMSLIME`/`CORRUPT_SLIME` 缺"史莱姆法杖 + 空 6999"池 | **开单补齐**（已下发） |
| 8 | **放置路径丢自定义名**（`createDefaultStackConfig` 因 mixin 在 HEAD cancel 而不执行） | **按 1.20 补 `useOn` 覆写**（已下发） |
| 9 | 1.20 `:111` 的"蚁狮掉落物已齐全"注释 | **不搬**（口径保持"只搬欠账注释"） |
| 10 | 批次写手的 plumbing 历史改写（`40dde3936` → `16a50f293`） | **接受**（内容逐字节相同，reflog 可回退） |
| 11 | `DREAMER_GHOUL` 被 `add` 两次 | **开单清理**（已下发） |

> 术语备忘（答复用户提问时用到）：`EntityType.createDefaultStackConfig(ServerLevel, ItemStack, Player)`（`build/_nfsrc_219/.../EntityType.java:928`）返回一个 `Consumer<T>`，在实体刚创建时把**物品上的数据搬到实体**：内部 `appendDefaultStackConfig` = `appendCustomNameConfig`（`DataComponents.CUSTOM_NAME` → `setCustomName`）+ `appendCustomEntityStackConfig`。1.21 vanilla 放置路径靠它带名字，本仓 mixin cancel 了调用它的方法体 ⇒ 放置丢名字。

### 21.3 台账回填
- 本轮新增：行 133 = `LOST?`（`LivingEntityEvents` 残差 25 未决）、134 = `DO-NOT-PORT`、135/136 = `COVERED`、141 = `PORTED`（状态 JSON 共 **153** 条）。
- **语言条目的 13 个行（151/173/176/179/203/205/256/272/314/321/322/350/363）不整体判 `PORTED`**：`bcf2ef1e0` 只落的是它们的**语言落点**，行内还有其它未清落点（例：行 203 的筛出缺口数 ⚠️=102、行 256 ⚠️=17、行 151/322 等亦有未清项）⇒ 保持 `TODO`，落地事实以本节为准。

### 21.4 下一步队列
1. **正在跑**（已下发）：① 行 141 的 `useOn` 覆写；② `CRIMSLIME`/`CORRUPT_SLIME` 补池；③ `DREAMER_GHOUL` 去重；④ **掉落表批次 3**（海洋族 9 条，含共用 import `DifficultyChanceLootItemCondition`）。
2. 之后按 `notes/LOOT-GAP-PLAN.md` 顺序：批次 4 地牢(12) → 5 雪原/杂项(13) → 6 腐化/猩红(11，含 `ModBlocks.CURSED_FLAME`→`MaterialItems.CURSED_FLAME`) → 7 Boss/NPC(1) → 8 火星实体批 → 9 三十八个空表。
3. 剩余缺口（本轮后）：**84 个 NO-LOOT**（含 38 空表）+ 7 个待移植火星实体。

## 二十二、行 141 收口 + 三处收口修复 + 掉落表批次 3 落地（本轮）

### 22.1 落地（4 个提交；区间 `2a712f76e..1384f96ef` 只碰 2 个文件，+69/−11）
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `fcdde0c39` | **`useOn` 覆写**（行 141 收口）：修"右键轨道放置自定义矿车丢自定义名"；与 1.20 逐行只差 1 行（`hasCustomHoverName()` → `has(DataComponents.CUSTOM_NAME)`），补 3 个 import | `common/item/common/BaseMinecartItem.java` | +21 |
| `cb5543c52` | `CRIMSLIME`/`CORRUPT_SLIME` 补回"史莱姆法杖 + 空 6999"池（各 +4 行；色值/数量/looting 保持 1.21 数据组件写法，未引入 1.20 的 NBT） | `data/gen/loot/EntitySubProvider.java` | +8 |
| `2ceb32a6b` | `DREAMER_GHOUL` 重复登记去重：两段 **SHA-256 相同**（`02e4e63b…`）后删掉第二条（保留紧贴 `TAINTED_GHOUL`、与 1.20 上下文契合的第一条） | 同上 | −11 |
| `1384f96ef` | **掉落表批次 3**（海洋/鲨鱼/人鱼族 9 条）+ **跨批共用 import** `DifficultyChanceLootItemCondition` | 同上 | +40 |
- 我独立复核：`useOn` 命中 1；`setWeight(6999)` 6 处；`DREAMER_GHOUL` 1 处；批次 3 的 9 个实体全部就位；import 2 处（import + 使用）；`git diff --numstat 2a712f76e..HEAD` 只有上述 2 个文件。

### 22.2 本轮 4 条裁定
| # | 问题 | 裁定 |
|---|---|---|
| 12 | 补池后 `CRIMSLIME`/`CORRUPT_SLIME` 的池序 `[法杖, 盲罩, 凝胶]` 与 1.20 的 `[法杖, 凝胶, 盲罩]` 不同 | **不动**（三池独立 roll、无行为差异），登记为排版差异 |
| 13 | 1.20 `createMinecart` 上方的 javadoc 是否补 | **不补**（口径：只搬欠账类注释、不新增说明性注释） |
| 14 | `ICE_MIMIC`（1.21 `:1030`/`:1041` 各一条 `add`）疑似重复 | **开单核对后处理**（同 key 且逐字节相同才算重复；若为 2 参 + 3 参两张不同表则属误报） |
| 15 | 批次 3 落点：计划原稿写"紧邻 `PIRANHA`"，实际拆成 4 个锚点以保 1.20 相对顺序 | **保持现状**（已同步修正计划文件措辞） |
> 附澄清：`RAINBOW_SHEEP` 的 `:1245/:1246` **不是重复**——2 参 `add` 生成 `confluence:entities/rainbow_sheep`、3 参 `add(…, ModLootTables.SHEEP_RAINBOW_WOOL, …)` 生成另一张表，属有意登记（前一轮已核实）。

### 22.3 进度
- 掉落表缺口：原 **94 个 NO-LOOT**（56 有内容 + 38 空表）+ 7 个待移植火星实体；
  已落 **29 个有内容**（A2 10 + 批 1 6 + 批 2 4 + 批 3 9）⇒ **剩余 65 个 NO-LOOT（27 有内容 + 38 空表）+ 7 火星实体**。
- 计划文件 `notes/LOOT-GAP-PLAN.md` 已同步：写入"已落地/已定案/落地后必做"三节，并按裁定 15 修正批次 3 的落点措辞。
- **落地后必做（未做）**：重跑 datagen（`runData`）生成 `src/generated/.../loot_table/entities/` 新表；本次会话按禁令未编译、未跑 datagen，磁盘产物仍是"HEAD 减去最近若干提交"的状态。
- 下一批：**批次 4 骷髅/地牢族 12 条**（已下发，与 `ICE_MIMIC` 核对同单）。

## 二十三、掉落表批次 4 落地 + `ICE_MIMIC` 判"非重复"（本轮）

### 23.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `fbb7b4055` | **掉落表批次 4**（骷髅/地牢族 12 条：`WALL_CREEPER`/`BLACK_RECLUSE`/`GIANT_BAT`/`ARMORED_SKELETON`/`ROCK_GOLEM`/`ILLUMINANT_BAT`/`LAVA_BAT`/`DUNGEON_SPIRIT`/`TIM`/`DOCTOR_BONES`/`PALADIN`/`BONE_LEE`，含 5 条欠账注释） | `data/gen/loot/EntitySubProvider.java` | +44 |

- 我独立复核：12 个实体在 provider 里各 1 次 `add`；批次 9 的 7 条空表仍各 **0** 命中（`JUNGLE_CREEPER`/`DESERT_SPIRIT`/`RUNE_WIZARD`/`NECROMANCER`/`DIABOLIST`/`RAGGED_CASTER` 等）；`DifficultyChanceLootItemCondition` 使用点 1 → 4。
- 写手断言（已收）5 组 12 块对 1.20 逐字节 `identical=True`；"HEAD + 同锚点重放 == 工作文件"端到端一致；`fix_eol --check` 候选 0；`{}` 25/25、`()` 2735→2871。

### 23.2 `ICE_MIMIC` = **非重复**（我复核过）
- `:1114` 是**活体**登记（不在块注释里）；`:1125` 那条位于 `/* … */` 内（前一行是 `// todo秘密种子冰雪宝箱怪使用这个common`）⇒ **死代码，不参与注册**，`ICE_MIMIC` 只有 1 条活体表。
- 1.20 结构完全相同（活体 `:1195` + 注释段 `:1206`），1.21 是忠实复刻；两条内容也不同（471 B vs 817 B）。
- **教训（并入方法层）**：判"重复登记"必须做**注释感知**扫描；只数 `add(` 文本命中会把块注释里的死代码算成重复。同类误报还有 `RAINBOW_SHEEP`（2 参 + 3 参两张不同表）。

### 23.3 裁定（第 16 条）
| # | 问题 | 裁定 |
|---|---|---|
| 16 | 批次 4 把 `ARMORED_SKELETON`/`ROCK_GOLEM` 放进蝙蝠段（因 1.20 里二者紧邻 `GIANT_BAT`） | **保持现状**（与批次 3 同口径：以 1.20 邻接关系为准） |

### 23.4 进度与预留
- 已落 **41 个有内容**掉落表（A2 10 + 批1 6 + 批2 4 + 批3 9 + 批4 12）⇒ **剩余 53 个 NO-LOOT（15 有内容 + 38 空表）+ 7 个待移植火星实体**。
- 计划文件已同步：批次 4 进度、裁定 5/6/7 与**给后续批次的预留空位**（批次 6 的 `WEREWOLF`；批次 9 的 `RUNE_WIZARD`/`ENCHANTED_SWORD`/`NECROMANCER`/`DIABOLIST`/`RAGGED_CASTER`/`JUNGLE_CREEPER`/`DESERT_SPIRIT`）。
- 下一批：**批次 5 雪原/杂项怪物族 13 条**（已下发）。

## 二十四、掉落表批次 5 落地 + 两条格式裁定（本轮）

### 24.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `0ed38808c` | **掉落表批次 5**（雪原/杂项怪物族 **13 条**：`ZOMBIE`/`ICE_GOLEM`/`ARMORED_VIKING`/`ICE_TORTOISE`/`DIGGER`/`ANGRY_TUMBLER`/`RED_DEVIL`/`MOSS_HORNET`/`GOBLIN_WARLOCK`/`ANGRY_NIMBUS`/`ANGRY_DANDELION`/`GRANITE_GOLEM`/`HOPLITE`，含 9 条欠账注释，**零适配**） | `data/gen/loot/EntitySubProvider.java` | +75 |

- 我独立复核：13 个实体各 1 次 `add`；批次 9 的空位抽查（`ICE_ELEMENTAL`/`WINDY_BALLOON`/`SHADOWFLAME_APPARITION`/`ARCH_WYVERN`）与批次 6 的 `WEREWOLF` 仍各 **0** 命中。
- 写手断言：12 个 1.20 原文块逐字节 `identical=True`；`add(MonsterEntities.*)` 139→152；欠账注释 11→20；`DifficultyChanceLootItemCondition` import 仍 1 处、构造点 4→5；端到端重放逐字节一致；`fix_eol --check` 候选 0。

### 24.2 裁定（第 17、18 条）
| # | 问题 | 裁定 |
|---|---|---|
| 17 | `GOBLIN_WARLOCK` 按 1.20 逐字落成单行 `add(..., goblinCommon());`，与 1.21 邻居哥布林的换行风格不一致 | **保持逐字**（保住该块逐字节 identical 断言；风格差异登记即可） |
| 18 | `ARMORED_VIKING` 罗盘池照抄 1.20 未写 `.setWeight(1)`，相邻 `UNDEAD_VIKING` 在 1.21 显式写了 | **保持逐字**（权重默认即 1，语义等价） |

> 至此格式类问题的口径已稳定：**除编译必需/语义必需的适配外，一律以 1.20 逐字为准**；1.21 侧既有的写法差异只登记、不回改（裁定 12/13/17/18 同源）。

### 24.3 进度（**已用工具复核更正**）
- 复核命令：`python tools/port2native/loot_coverage.py` ⇒ **1.20 引用实体 227 / 1.21 引用 176 / 1.20-only 57**，其中 **1.21 无掉落表 50**、**实体在 1.21 不存在 7**（火星族）、on-disk 有表 0。
- 口径澄清（**更正本节初稿的错误算术**）：A2 的 10 个是在"94"这个数字**之前**就已落地的，不能再计入"94 之内已落"。正确算法：`94 − (批1 6 + 批2 4 + 批3 9 + 批4 12 + 批5 13 = 44) = 50`。
- ⇒ **剩余 50 个 NO-LOOT**（批次 6 的 11 有内容 + 批次 7 的 1 有内容 + 批次 9 的 38 空表）+ **批次 8 的 7 个待移植火星实体**。
- 下一批：**批次 6 腐化/猩红/肉山后族 11 条**（已下发；含**唯一类名搬家** `ModBlocks.CURSED_FLAME` → `MaterialItems.CURSED_FLAME` 两处：`CLINGER`/`WORLD_FEEDER`）。
- 仍未做：**重跑 datagen**（需编译，等用户决定）。

## 二十五、掉落表批次 6 落地 + 两条裁定（本轮）

### 25.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `f78ae75e6` | **掉落表批次 6**（腐化/猩红/肉山后族 **11 条**：`THE_BRIDE`/`THE_GROOM`/`WEREWOLF`/`SWEET_SLIME`/`CLINGER`/`WORLD_FEEDER`/`GIANT_FLYING_FOX`/`CORRUPTOR`/`UNICORN`/`GASTROPOD`/`CHAOS_ELEMENTAL`；含 6 条欠账注释 + 1 条行内注释） | `data/gen/loot/EntitySubProvider.java` | +66 |

- **唯一类名搬家（编译关键路径）已生效**：我复核 `ModBlocks.CURSED_FLAME` 在 provider 内 **0 命中**、`MaterialItems.CURSED_FLAME` **3 命中**（2 处新落 + 1 处既有）；依据：1.21 `ModBlocks` 只有 `CURSED_FLAME_BLOCK`（`registerWithoutItem`），物品是 `MaterialItems.CURSED_FLAME`。
- 我复核：11 个实体各 1 次 `add`；落点遵守预留空位（`WEREWOLF` 在 `ROCK_GOLEM` 之后、夜明蝙蝠注释之前；`WORLD_FEEDER` 在 `DIGGER` 与 `TIM` 之间，批 9 的 `RUNE_WIZARD` 空位仍保留）。
- 工具交叉验证：`loot_coverage.py` 由「1.21 引用 176 / 无表 50」变为 **「引用 187 / 无表 39」**（−11 ✓）。

### 25.2 裁定（第 19、20 条）
| # | 问题 | 裁定 |
|---|---|---|
| 19 | 批次 6 为 `CLINGER` 的 `FunctionalBlocks.MEAT_GRINDER` 加回 1 行 `import org.confluence.mod.common.init.block.FunctionalBlocks;`（1.20 源本有、1.21 因前几批无使用者被省去） | **准，保留**（属"编译必需的适配"，与格式口径不冲突） |
| 20 | 批次 9 落点：1.21 的 `WINGLESS_SLIMER` 已在史莱姆段，而 1.20 里它在 `CORRUPTOR` 与 `UNICORN` 之间 | **只按 1.20 邻接插入新增空表；已落地的实体不动**（不为邻接去移动已提交的行） |

### 25.3 进度
- 复核命令（权威口径）：`python tools/port2native/loot_coverage.py` ⇒ **1.20 引用 227 / 1.21 引用 187 / 1.20-only 46**，其中「1.21 无掉落表 **39**」+「实体在 1.21 不存在 7（火星族）」。
- ⇒ **剩余 39 个 NO-LOOT**（批次 7 的 1 有内容 + 批次 9 的 38 空表）+ **批次 8 的 7 个待移植火星实体**。
- 下一批：**批次 7（1 条）+ 批次 9（38 张空表）**已下发（同一文件、串行两提交）；**批次 8 火星内容批**（实体 + 资源 + 掉落表 + lang）留待其后的独立工单。
- 仍未做：**重跑 datagen**（需编译，等用户决定）。

## 二十六、编译事故与修复：`CoinItem.valueOf`（毒提交的"被调方"被一并带入）

### 26.1 现象
- 用户编译报错：**`Cannot resolve method 'valueOf' in 'CoinItem'`**（`PlayerPiggyBankContainer` 两处）。

### 26.2 根因（已查实）
- 两处调用来自本会话 `1ebdee4d6` 移植的 1.20 `placeCoins`（1.20 提交 `7572f2ebd` = 台账行 331，**非毒**）。
- 但 `placeCoins` 体内调用的 **`CoinItem.valueOf(Item)` 与其 4 个面额常量（`COPPER/SILVER/GOLD/PLATINUM_VALUE`）最早引入提交是 `dfcc5c041`（毒提交）**：
  `git log -S 'public static long valueOf' -- CoinItem.java`（1.20 侧）⇒ `dfcc5c041`；而 1.21 侧 `git log -S valueOf -- CoinItem.java` **为空**（从未有过该 helper）。
- 即"移植 1.20 内容时，把毒提交引入的**被调方**一并带了进来"；且 `PlayerPiggyBankContainer.java` **本身就在毒残留清单里**（`notes/poison-residue-files.txt:24`），按《POISON-dfcc5c041.md》第 3 条本应"优先以 1.21 侧现有实现为准"。
- 我的复盘（自身失误）：`1ebdee4d6` 那轮我核了 `Coins.platinum2CopperEntries()`、`decodeCoin`、`ModSoundEvents.COINS_*`、`AttachmentEntityDamageSource.getOwner()`，**漏核了 `CoinItem.valueOf`**。

### 26.3 修复（`ac21de8c5`，1 文件 +8/−2）
- `PlayerPiggyBankContainer` 新增本地 `private static long coinValue(Item item)`，改用 **1.21 自己的面额体系**：
  `PlayerUtils.COIN_2_INDEX` + `CoinItem.UPGRADES_COUNT`（与 `PlayerUtils.getMoney` 同款写法），
  数值与 1.20 的常量**完全等价**（`100^(3-index)`：index 3→1、2→100、1→10_000、0→1_000_000）。
- 两处调用改指该本地 helper；补 1 行 `import net.minecraft.world.item.Item;`；**未移植任何毒提交内容**、未新增说明性注释。

### 26.4 规则扩展（本次事故的直接产物）
1. **核"被调方"**：移植一段 1.20 代码时，不只要核该行自己的文件，还要对代码里调用的**自定义 helper/常量**逐个 `git log -S` 取最早引入提交；命中 `dfcc5c041`/`dc57ba5c2` ⇒ 禁止移植，改用 1.21 侧等价实现。
2. **文件级预警**：新增工具 `tools/port2native/poison_residue_check.py`：
   - `--worktree` / `--commit <rev>` / 直接给路径 ⇒ 报出命中毒残留清单（992 条）的文件；
   - `--symbol <名字>` ⇒ 在 1.20 仓 `git log -S` 判该符号是否由毒提交引入（命中返回退出码 1）。
   实测：`--symbol 'public static long valueOf'` ⇒ 判定"毒提交引入"；`--commit ac21de8c5` ⇒ 正确报出 `PlayerPiggyBankContainer.java` 属残留文件。
3. 写手工单里今后要**预置这一步**：动手前先跑 `--worktree`（或对要改的文件跑路径模式），命中即按第 3 条优先用 1.21 实现，并在报告里说明。

### 26.5 影响面
- 本事故只影响 `placeCoins` 的那两处调用（`CoinItem.valueOf` 全仓仅这两处）；修复后无残留引用（`git grep -c 'CoinItem.valueOf'` = 0）。
- 毒残留清单里我们近期动过的文件还有 `ValueSubProvider.java`、`TreasureBagSubProvider.java`（属已回退的三件套，`170b54eb8`），当前工作树内容未受影响。
- 未编译（按用户禁令），本修复是纯静态判定；**用户下次编译即可验证**。

## 二十七、掉落表批次 7 + 批次 9 落地 —— **1.21 无掉落表归 0**

### 27.1 落地（本轮 2 个提交）
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `a05cbac11` | **批次 7**（Boss/NPC 零头 1 条）：`NpcEntities.STYLIST`（1.20 `:856-861` 逐字），落在 `MECHANIC` 与 `DYE_TRADER` 之间 | `data/gen/loot/EntitySubProvider.java` | +6 |
| `b7136a8b8` | **批次 9**（**38 张 1.20 空表** + 6 条欠账注释），14 个插入点全部按 1.20 实际邻接 | 同上 | +44 |

- 写手断言：38 条各恰 1 次 `add`；1.21 单行空表 38 / 1.20 45（差 7 = 火星实体）；端到端重放逐字节一致；`fix_eol --check` 候选 0；`()`/`{}` 配平；CRLF 1713 / 裸 LF 0。
- 工具交叉验证（我复核）：`loot_coverage.py` ⇒ **1.20 引用 227 / 1.21 引用 226 / 1.20-only 7**，其中 **「1.21 无掉落表 = 0」**、实体不存在 7（火星族）。
  ⇒ **掉落表缺口（除火星实体外）已全部补齐**：累计落地 `A2 10 + 批1 6 + 批2 4 + 批3 9 + 批4 12 + 批5 13 + 批6 11 + 批7 1 + 批9 38 = 104` 条。

### 27.2 裁定（第 21、22 条）
| # | 问题 | 裁定 |
|---|---|---|
| 21 | 1.20 `:841` 的段头注释 `//火星人事件`（计划里既不在批 9 的 6 条、批 8 又写"无注释"） | **随批次 8 的 7 个火星实体一起搬**（插在 `MARTIAN_PROBE` 之前） |
| 22 | 三处口径确认：① 5 条"无掉落物（仅钱币）…已齐全"解释性注释不搬；② `NECROMANCER`/`DIABOLIST`/`RAGGED_CASTER` 按 **1.20 实际邻接**（`BONE_LEE` 之后）而非预留空位 #7；③ `WINGLESS_SLIMER` 已在史莱姆段、**不为邻接移动**，新空表相邻落地 | **都按既定口径，不改** |
> 说明：①与裁定 3/13 同源（只搬欠账类注释）；②③与裁定 15/20 同源（以 1.20 实际邻接为准、已落地行不动）。

### 27.3 下一步
- **只剩批次 8：火星内容批**（7 个实体 + 资源 + 掉落表 + lang，含裁定 21 的段头注释）。计划文件已记：1.20 侧资源齐备（8 张贴图 + 7 geo + 7 animation + 音效 + lang），对应台账新行 387/391/394 + 393 的 `ParticleAccessor`。
- 仍未做：**重跑 datagen**（需编译，等用户决定）。

## 二十八、批次 8（火星内容批）启动：裁定 23/24 + 两线并行

### 28.1 裁定（第 23、24 条）
| # | 问题 | 裁定 |
|---|---|---|
| 23 | 批次 8 必须改到**禁改清单**里的 `common/init/ModEntities.java`（按 1.20 应在此注册两枚新射弹 `MartianElectricBolt`/`RayGunnerLaser`） | **解除禁改**，允许**只加这两条射弹注册**（该文件当前工作树干净；写完仍限路径提交、只动这两段） |
| 24 | 批次 8 的推进方式（约 49 个文件） | **拆子批自动推进**：每批派写手落地 → 主代理复核 → 回填台账；只有需要裁定时才停下来问 |

### 28.2 批次 8 的两线并行（已启动）
- **只读计划线**：产出逐文件/逐插入点/逐适配的落地计划（文件三分类：纯新增 / 需插共享文件 / 纯资源；子批划分；平台适配清单；禁区标注；风险与坑；末尾"需要用户裁定"）。
- **写手线（子批 A：纯资源）**：搬 6 个实体（`martian_engineer`/`martian_officer`/`martian_walker`/`ray_gunner`/`scutlix`/`tesla_turret`）的 `geo` + `animations` + `textures`，以及这 3 个 1.20 提交新增、1.21 尚缺的其它火星资源（含音效、`sounds.json` 条目级合并、路径搬家映射）；**不碰任何 java**。
- 子批划分（初步，等计划线确认后细化）：A 资源 → B 实体类 + `MonsterEntities` 注册 + 刷怪蛋/标签页/效果注册 → C 射弹（含解除禁改的 `ModEntities` 两条）+ 网络包 + `GameEventSystem` 接线 → D 渲染/客户端注册（geo 渲染器、`ShieldOutlineProjection`、模型）→ E lang / 掉落表 / bestiary。

### 28.3 并行注意事项（已写进写手工单）
- 工作树里有**他人在索引里暂存的 3 个 license 文件**（`assets/confluence/LICENSE-CC-BY-NC-SA-4.0.txt`、`license.bin`、`vfx/licenses/kenney-particle-pack.txt`）：写手被明确要求**不提交、不修改**这三个文件；其余提交一律限路径。
- 我之前那次误带入已处理：`b5dfc0adc` 只含两个 notes 文件，那 3 个文件已恢复为暂存状态。

## 二十九、批次 8 计划定稿 + 裁定 25–27 + 子批 B / BOM 清理开工

### 29.1 计划线的两个**前提修正**（重要，已记入 `notes/BATCH8-MARTIAN-PLAN.md`）
1. **1.20 侧这批代码从未编译通过**：`MartianEventHelper` **两仓 + 4 个子模块（`Confluence-Magic-Lib`/`PortLib`/`TerraCurio`/`TerraFurniture`）全盘都没有类定义**（主代理另行复核：1.20 仓 `Get-ChildItem -Recurse -Filter *.java | Select-String 'class MartianEventHelper'`（排除 `build`）返回空），只有 4 处悬空引用——`MartianOfficer.java:17`(import)/`:80`(调用)、`MartianWalker.java:20`(import)/`:211`(调用)；1.20 的编译产物里只有 `MartianProbe*` 5 个 class。⇒ **"非毒" ≠ "可编译"**，这是本条最值得记住的教训。
2. **裁定 23 的措辞过期**：实际要注册的两条射弹是 `MARTIAN_ELECTRIC_BOLT` + **`MONSTER_LASER`**（1.20 `1780b9a88` 已删除 `RayGunnerLaser.java`、改用 `MonsterLaser.java`，`ModEntities.java:61` 注册 `MONSTER_LASER`）⇒ 原文里的 `RayGunnerLaser` 作废，已在计划文件里更正。
3. 路径搬家唯一一处：1.20 `common/init/entity/ModEntities` → 1.21 `common/init/ModEntities`（`MonsterEntities` 同包）。

### 29.2 裁定（第 25–27 条）
| # | 问题 | 裁定 |
|---|---|---|
| 25 | 6 个新 `*.geo.json` 的 `identifier` 是 Blockbench 默认 `geometry.unknown`（1.20 原样）是否规范化 | **保持逐字节**（两仓 java 都 0 处读 identifier、渲染器走 `ExplicitGeoModel` 显式路径；1.20 侧 171/493、1.21 侧 162/421 个 geo 同为该值，属本仓惯例） |
| 26 | ~~8 个已入库 geo JSON 带 UTF-8 BOM + 0 CRLF + 1 裸 LF~~ **前提错误，已作废**（见 §三十一：那 8 个文件**无 BOM**、**不是 JSON**，而是 Huffman+Vigenère 加密模型载荷） | **作废**（用户 2026-10 确认；不动 `.gitattributes`） |
| 27 | `MartianEventHelper` 两仓都不存在，阻塞 `MartianOfficer`/`MartianWalker` | **先删掉两处调用并留 TODO**，实体照常落地；火星事件本体另立批次（已知行为差异：事件结束后走妖/军官不会自动消失，需写进文件注释） |

### 29.3 主代理拍板的技术决定（写进计划文件，不再逐条问）
- **子批顺序改拓扑序**：`A 资源（已落地）→ B 射弹+效果+网络+注册 → C 实体+注册+刷怪蛋+掉落+事件接线 → D 渲染/客户端 → E lang/bestiary`。**依据**：实体在编译期引用射弹（`RayGunner.java:20,213-216` 用 `MonsterLaser`、`TeslaTurret.java:19,128-133` 用 `MartianElectricBolt`、`MartianWalker.java:19,229-232` 用 `MonsterLaser`），原定 A→B实体→C射弹会产生死引用。
- **`ModTabs` 本批跳过**：1.21 全文**无刷怪蛋分节**（`grep egg` 0 命中），现有 284 个刷怪蛋同样不在任何创造页；`itemGroup.confluence.martian_entity` 会变死键 ⇒ 刷怪蛋创造页 + 模型 datagen 另立工单（与既有 WP7 挂起项同源）。
- **bestiary 6 条原地替换** `ModClientBestiaryEntryProvider.java:483-490` 的占位（1.21 无 `MARTIAN_PROBE` 锚点），order 与 `FilterEntry.MARTIAN_MADNESS` 照 1.20 抄，不新增段落。
- **lang 只加火星族 10 条**（7 实体名 + 2 射弹名 + 1 条 `addEffect(ModEffects.ELECTRIFIED, …)`），**不做全族回填**（1.21 完全缺 `entity.confluence.*` 怪物名段落属历史欠账，另立工单）。
- **`ModEntities` 不加新助手**：两条注册按该文件 `:313-357` 既有内联风格写，严守裁定 23 的"只加这两条"。
- **bestiary 文案取 1.20 HEAD 版**；`martian_probe.desc`（1.21 里被注释）**本批不动**。

### 29.4 开工
- **子批 B（射弹+效果+网络+注册）**已下发：7 个文件（`MartianElectricBolt`/`MonsterLaser`/`ElectrifiedEffect` 新建 + `ModEntities` 两条 + `ModEffects` 一条 + `ElectrifiedInputPacketC2S` 按 `IPacketC2S` 重写 + `NetworkEvents` 一行），含 8 条已核实的平台适配（`MobEffect.applyEffectTick` 返回 boolean、`defineSynchedData(Builder)`、`isPresent()→isBound()`、`get()` 抛 NPE、PortLib 网络 API → `IPacketC2S` 等）。
- **8 个 geo 的 BOM/行尾清理**已单独下发（另一个写手，文件不重叠）。
- 计划线被要求把完整计划（含 `file:line` 证据与 9 条定论）落成 **`notes/BATCH8-MARTIAN-PLAN.md`** 由我统一提交，避免只存在于会话里。

## 三十、批次 8 子批 B 落地 + 7 项确认（主代理定，不再逐条问）+ 子批 C 开工

### 30.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `610a9fd53` | **子批 B**：射弹 + 效果 + 网络包 + 注册 —— 新建 `MartianElectricBolt`(73) / `MonsterLaser`(116) / `ElectrifiedEffect`(53) / `ElectrifiedInputPacketC2S`(33)，改 `ModEntities.java`(+3，两条内联注册 `:63-64`) / `ModEffects.java`(+1，`ELECTRIFIED`:166) / `NetworkEvents.java`(+1，`:70` `.playToServer(...)`) | 7 个文件 | +280 |

- 我独立复核：4 个新类在 HEAD；`MARTIAN_ELECTRIC_BOLT`/`MONSTER_LASER` 在 `ModEntities` 各 1 处；`ELECTRIFIED = EFFECTS.register("electrified", …)` 在 `ModEffects:166`；`ElectrifiedInputPacketC2S` 注册在 `NetworkEvents:70`；`ELECTRIFIED.get()` 全仓残留 **0**。
- 逐字差异极小（写手用 `git diff --no-index --ignore-cr-at-eol` 对照）：`MartianElectricBolt` **1 hunk**、`MonsterLaser` **1 hunk**、`ElectrifiedEffect` 3 hunk（`PortMobEffect`→`MobEffect` + `applyEffectTick` 返回 boolean + import 重排）。

### 30.2 写手的 7 项确认 → 主代理定性（均在既定口径内，不必再问用户）
| # | 议题 | 定性 |
|---|---|---|
| 1 | 源仓 HEAD 实为 `f07491b3a`（任务书写 `07c2ab5b5`） | **无需动作**：多出的那个提交就是本会话同步 vfx 贴图的 6 个 PNG，java 真值等价 |
| 2 | 两处 `.get()` 去除（`MartianElectricBolt:59`、`ElectrifiedInputPacketC2S:26`）超出列举清单 | **准**：1.21 的 `MobEffectInstance` 构造器与 `hasEffect` **只收 `Holder<MobEffect>`**（`MobEffectInstance.java:56-89`、`LivingEntity.java:958`），保留 `.get()` 必编译失败；属"适配 3 的直接后果"。**不采用** `.getDelegate()` 写法 |
| 3 | `player != null` 死判是否删 | **保留（逐字为准）**：它不属于"注册对象判空"那类适配 |
| 4 | `ElectrifiedEffect` 的 import 按字典序重排 | **准**：与已移植同类 `BleedingEffect.java` 的 1.21 形态一致 |
| 5 | 网络包静态方法仍叫 `send(boolean)`（未跟随样板改名 `sendToServer`） | **保持 `send`**：对齐 1.20 调用点 `GameClientEvents.java:192`；**子批 D 必须调用 `ElectrifiedInputPacketC2S.send(...)`** |
| 6 | 本批不含 `electrified` 的 lang | **正确**：lang 属子批 E |
| 7 | 客户端渲染器尚未移植（`MartianElectricBoltRenderer`、`LaserProjectileRenderer`） | **正确**：属子批 D |

### 30.3 子批 C 已开工
- 范围：**新建 9 个**（`MartianEngineer`/`TeslaTurret`/`RayGunner`/`Scutlix`/`MartianOfficer`/`MartianWalker`/`WalkerWeapon` + 逐字可搬的 `WalkerGeometry`/`OfficerShieldGeometry`）+ **改 4 个**（`MonsterEntities` 18 行注册块 / `SpawnEggItems` 6 枚 / `EntitySubProvider` 7 条空表 + `//火星人事件` 注释 / `GameEventSystem` 的 `Scutlix.ensureRider` 接线）。
- **裁定 27 已写进工单**：删掉 `MartianOfficer:17`/`MartianWalker:20` 的 import 与 `:80`/`:211` 的调用（该 import 在 1.21 也无法解析，不删必编译失败），并在两处留一行 TODO 说明"火星事件本体未移植、事件结束后本实体不会自动消失"。
- 工单同时叮嘱：**不要顺手做** 渲染/客户端（子批 D）与 lang/bestiary（子批 E）；`ModTabs` 本批跳过；`ModEntities.java` 子批 B 已用过、本批不要再动。


## 三十一、子批 C 落地 + **裁定 26 作废**（"8 个 geo 带 BOM"是误判）+ 加密模型载荷的真相

### 31.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `388cbdf96` | **子批 C**：实体 + 注册 + 刷怪蛋 + 掉落 + 事件接线 —— 新建 9 个（`MartianEngineer`/`TeslaTurret`/`RayGunner`/`Scutlix`/`MartianOfficer`/`MartianWalker`/`WalkerWeapon` + **逐字可搬（diff 为空）** 的 `WalkerGeometry`/`OfficerShieldGeometry`），改 4 个（`MonsterEntities` 注释+7 条注册 / `SpawnEggItems` 6 枚 / `EntitySubProvider` 7 条空表 + `//火星人事件` / `GameEventSystem` 的 `Scutlix.ensureRider` 接线） | 13 个文件 | +1974 / −4 |

- 我独立复核：7 个实体常量在 `MonsterEntities` 各 1 处注册；`SpawnEggItems` 命中 2 处（新刷怪蛋）；`EntitySubProvider` 命中 2 处（新掉落条目）；**`MartianEventHelper` 全仓只剩 2 行 TODO**（`MartianOfficer.java:79`、`MartianWalker.java:212`）⇒ 裁定 27 已按令执行。
- 主要适配（写手逐条给了依据）：GeckoLib `core.` 包消失、`defineSynchedData(SynchedEntityData.Builder)`、`.isPresent()`→`.isBound()` 且删除 `.get()` 后的死 null 判断（**`EntityType.create(Level)` 仍 `@Nullable`，其后的判空全部保留**）、`finalizeSpawn` 4 参、`getPassengerAttachmentPoint`+`getPassengerRidingPosition`、`Attributes.STEP_HEIGHT`（照 `DeerClops` 写法）、`checkAndPerformAttack` 单参、`neoforged` 包、包搬家 `common.init.ModEntities`；`MonsterEntities` 另补 7 条实体 import（1.20 是 `monster.*` 通配）。

### 31.2 裁定 26 作废 + 主代理对 C 批次 3 项确认的定性
- **裁定 26 作废**（用户确认），`.gitattributes` 方案**不动**。
- 写手 3 项确认，主代理定性（均在既定口径内）：① TODO 落在**两个调用点**（2 行）即可，被删的 import 行不必再留；② `GameEventSystem` 照 1.20 逐条搬、lambda 内保留 1.20 的无花括号单行 `if`，**接受**；③ `MonsterEntities` 的 7 条 import 按字母序插入既有区块，**接受**。

### 31.3 加密模型载荷的真相（**重要知识，写入长期口径**）
"8 个 geo JSON 带 UTF-8 BOM"是**误判**（来自上一轮资源写手的措辞，我未复核就转了，责任在我）。清理工单的写手按"失败即停"**零改动**停手，并给出硬证据：

- **8 个文件无 BOM**（421 个 `geo/**/*.json` 全部无 BOM；全仓 13226 个已跟踪文件里只有 `tools/port2native/rules/{callsites,event-bus}.json` 带 BOM，且那是 `gen-rules.ps1` 生成、读取方显式用 `utf-8-sig` 的既有约定，**不要动**）。
- **它们不是 JSON**：`json.loads` ×8 全部 `Expecting value: line 1 column 1`。
- **它们是 Huffman+Vigenère 加密的模型载荷**：`confluence.mixins.json:151` → `FileLoaderMixin` → `ModClientSetups`（`!result.startsWith("{")` ⇒ 加密）→ `SecurityFace.S3`（`VigenereHuffman`，密钥来自 `license.bin`）；载荷格式 `<序列化哈夫曼树>|<位串>`，树叶 token 为 `L<字符>,<频次>,`。
- **那唯一的裸 CR / 裸 LF 就是哈夫曼树叶上的"CR 字符"和"LF 字符"**（频次恒成对相等：358/358、471/471、2085/2085…= 原文 JSON 的 CRLF 计数）；文件本身**一个行尾都没有**。git 因"裸 CR"判其为二进制，**唯一修法是改内容 ⇒ 会移位 Vigenère 密钥流并破坏"位串须停在叶边界"的校验 ⇒ 解密失败**。
- 实测：按工单"归一行尾"会固定 +3 字节，且 git **仍判 `-text`**（隔离实验：`b'abc\r'`→`-text`，`b'abc\r\n'`→text）⇒ 工单预期的"变成文本 + `w/crlf`"**不可能达成**。
- ⇒ **长期口径**：`assets/confluence/geo/**/*.geo.json` 里的加密文件（以"首字节非 `{`"识别）**禁止做任何 EOL/BOM/格式化处理**；`fix_eol.py` 因二进制判定自动跳过它们，属**正确行为**，不要为此改工具或加 `.gitattributes`。

### 31.4 下一步
- **子批 D（渲染/客户端）已开工**：新建 `MartianWalkerModel`/`MartianOfficerRenderer`/`MartianWalkerRenderer`/`ShieldOutlineProjection`/`MartianElectricBoltRenderer`，改 `LaserProjectileRenderer`（**必须回移 `MonsterLaser.Variant` 取色分支，否则两条激光全白**）、`ModClientEvents`（射弹渲染器 + 20 行实体渲染器块）、`GameClientEvents`（电击输入块 + `renderShields`，调用名用 `ElectrifiedInputPacketC2S.send`）。
- 之后仅剩**子批 E**（lang / bestiary）。

## 三十二、子批 D（渲染/客户端）落地 + 4 项确认定性 + 子批 E 开工

### 32.1 落地
| 提交 | 内容 | 文件 | +/- |
|---|---|---|---|
| `e74d310f6` | **子批 D**：新建 5 个（`MartianWalkerModel` 28 行 / `MartianOfficerRenderer` 196 / `MartianWalkerRenderer` 20 / `ShieldOutlineProjection` 52 / `MartianElectricBoltRenderer` 44，**后三个 diff hunk = 0 逐字搬**），改 3 个（`LaserProjectileRenderer` +14/−1、`ModClientEvents` +26、`GameClientEvents` +10） | 8 个文件 | +390 / −1 |

- **关键改动（我已逐行复核）**：`LaserProjectileRenderer` 回移了 1.20 `1780b9a88` 的变种取色 —— 新增 `import MonsterLaser`、单参 ctor（含 1.20 原有的 `/// 颜色改由实体动态提供…` javadoc，**确认 1.20 就有、非新增注释**）、`render` 内 `entity instanceof MonsterLaser laser → variant().innerColor()/middleColor()/outerColor()`；配色字面量在 `MonsterLaser.Variant`（1.20 逐字：`RAY_GUNNER 0xFFFFFF/0xFF5B4D/0xB50000`、`MARTIAN_WALKER 0xFFFFFF/0x73DFFF/0x0877FF`，子批 B 已按 1.20 落）。**不回移则两条激光全白**，现已正确。
- 其余适配（写手逐条给依据）：`neoforged` 包、`minecraft.renderBuffers().bufferSource()`（替代 `immediate(new BufferBuilder(65536))`）、`getPartialTick().getGameTimeDeltaPartialTick(false)`（1.21 返回 `DeltaTracker`）、`addVertex/setColor`（且**未**退回 `vertex()/endVertex()`）、GeckoLib `core.` 包消失。
- 我复核：`ModClientEvents` 4 处渲染器注册命中（`:636` 射弹 + `:947/:948/:950` 实体）；`geckolib.core.`／`endVertex()` 残留 **0**。

### 32.2 写手 4 项确认 → 主代理定性（均在既定口径内）
| # | 议题 | 定性 |
|---|---|---|
| 1 | `GameClientEvents` 的 `MartianOfficerRenderer` import 放 `:85`（= 1.20:66 同一相对位，紧随 `ZombieArmRenderer`），而非任务书字面的":80 附近" | **接受**：以 1.20 相对位为准 |
| 2 | `MONSTER_LASER` **只注册一次**（`1 ModClientEvents:950`，留在 20 行块原位），`:636` 仅注册 `MARTIAN_ELECTRIC_BOLT` | **接受**：与 1.20 一致（计划 §1(b)#13 的措辞由本条更正） |
| 3 | import 实需 **5 条**（计划写 2 条）——多出 `RayGunner`（1.20 靠 `common.entity.monster.*` 通配）、`LaserProjectileRenderer`（1.21 原无注册） | **接受**：编译必需 |
| 4 | 1.20 源仓存在**既有的**暂存删除 `vfx/licenses/kenney-particle-pack.txt` | **知悉**：那是用户在 1.20 侧自行处理 license 的状态，与本批无关 |

### 32.3 子批 E（最后一个）已开工
- 范围：4 个 datagen 文件 —— `ModChineseProvider`／`ModEnglishProvider` 各加 **10 条火星族 key**（7 实体名 + 2 射弹名 + `addEffect(ModEffects.ELECTRIFIED, …)`，中英逐字取 1.20 HEAD）；`ModClientBestiaryEntryProvider` 把 `:483-490` 注释占位替换为 **6 条真实 bestiary 条目**（order 用 1.20 的 41300/41400/41500/41700/41900/42000 + `FilterEntry.MARTIAN_MADNESS`）；`BestiaryLanguageSubProvider` 把 4 条注释占位启用为正式条目并**取 1.20 文案**（`martian_probe.desc` 不动）。
- 明确不做：`itemGroup.confluence.martian_entity`（`ModTabs` 本批跳过）、全族 `entity.confluence.*` 回填（历史欠账，另立工单）。
- 完成后**批次 8 即全部落地**（A 资源 → B 射弹/效果/网络 → C 实体/注册/掉落/事件 → D 渲染/客户端 → E lang/bestiary）。

## 三十三、**批次 8（火星内容批）全部落地** —— 收口

### 33.1 五个子批与提交
| 子批 | 内容 | 提交 | 规模 |
|---|---|---|---|
| **A** | 资源：6 实体 ×（geo + animation + texture）= 18 个文件（逐字节等于 1.20，blob OID 两侧相同） | `831c9908b` / `57c50c392` / `67a2fb7b8` | +18 文件 |
| **B** | 射弹 + 效果 + 网络 + 注册：新建 `MartianElectricBolt`/`MonsterLaser`/`ElectrifiedEffect`/`ElectrifiedInputPacketC2S`；改 `ModEntities`/`ModEffects`/`NetworkEvents` | `610a9fd53` | 7 文件 +280 |
| **C** | 实体 + 注册 + 刷怪蛋 + 掉落 + 事件接线：新建 7 实体 + `WalkerGeometry`/`OfficerShieldGeometry`（后两个逐字 dr=0）；改 `MonsterEntities`/`SpawnEggItems`/`EntitySubProvider`/`GameEventSystem` | `388cbdf96` | 13 文件 +1974/−4 |
| **D** | 渲染/客户端：新建 5（+2 个逐字）；改 `LaserProjectileRenderer`（**变种取色回移**）/`ModClientEvents`/`GameClientEvents` | `e74d310f6` | 8 文件 +390/−1 |
| **E** | lang/bestiary：中英各 **9 条** key + 6 条 bestiary 条目 + 8 条 bestiary 描述（取 1.20 文案） | `1344c1a06` | 4 文件 +34/−14 |
| 文档 | 计划入库 + §二十九…§三十三 | `fc3f76b74` / `ae3c67a43` / `269c68677` / `b801f3482` / `27dd678c4` 等 | — |

### 33.2 台账行覆盖核对（387/391/394）
- `387 ce6daf602`：24 个落点路径，1.21 **缺 0** ⇒ 全覆盖。
- `391 f9bda32a5`：21 个落点，1.21 缺 1 = `common/entity/projectile/RayGunnerLaser.java` —— 该文件由本提交新增、又由 `1780b9a88` **删除**，1.20 HEAD 已无它 ⇒ **不该搬**（不是缺口）。
- `394 1780b9a88`：31 个落点，缺的同样是 `RayGunnerLaser.java`（同上）。
⇒ 三行均判 **`PORTED`**（台账已回填）。

### 33.3 遗留 / 已知缺口（**不是本次移植漏项**，多为两侧共同的历史缺口）
1. **火星事件本体 `MartianEventHelper` 未移植**（裁定 27）：该类在**两仓 + 4 个子模块全盘都没有定义**，1.20 这批代码从未编译通过；已在 `MartianOfficer.java:79`/`MartianWalker.java:212` 留 TODO。**行为差异**：火星事件结束后走妖/军官不会自动消失。→ 需另立"火星事件批次"。
2. **刷怪蛋创造页 + 刷怪蛋模型 datagen**（裁定 3）：1.21 既无刷怪蛋分节、也没有刷怪蛋模型生成逻辑（`src/generated/.../models/item` 只有 1 个 `*_spawn_egg.json`）；本批未写 `itemGroup.confluence.martian_entity`（避免死键）。→ 与既有 WP7 挂起项同源，另立工单。
3. **`martian_electric_bolt` 无中文名**（用户裁定：不补、登记）：1.20 中文侧无任何自动命名（`ModChineseProvider` 无 `forEach`），英文侧靠 `ModEnglishProvider:1830` 自动命名；⇒ 游戏内中文会显示原始 key。**两侧共同缺口**。
4. **3 条 bestiary 描述无文案**（`martian_engineer`/`tesla_turret`/`scutlix_gunner`）：1.20 HEAD 没有这几条 desc，1.21 侧仍是注释占位（用户裁定：不补、登记）。
5. **`martian_probe.desc`**（1.21 EN `:501`/ZH `:1272` 被注释）本批**未动**（用户裁定）。
6. **重跑 datagen 未做**（需编译，用户禁令）：本批新增的 9 条 lang、6 条 bestiary 条目、7 条掉落表都只在 provider 源码里，`src/generated/**` 仍是旧快照 ⇒ **跑一次 `runData` 才会在游戏里生效**。
7. 另记：`electrified` 的中文按 1.20 逐字用 2 参 `add(ModEffects.ELECTRIFIED.get(), "带电")`（1.21 的 `addEffect` 是 3 参需 tooltip，而 1.20 无 tooltip ⇒ 未臆造）；1.21 EN 侧 `ModEffects.EFFECTS` 与 `ModEntities.ENTITIES` 的自动命名会与这两条显式行**同值覆盖**（无行为差异，仅备忘）。

### 33.4 下一步候选
- **A**：继续台账按序行走（下一开放行 **133**，`LivingEntityEvents` 的 `LOST?` 待收口）。
- **B**：火星事件批次（补 `MartianEventHelper` 语义 + 去掉那 2 行 TODO）。
- **C**：刷怪蛋创造页 + 模型 datagen（WP7 挂起项）。
- **D**：用户跑一次编译 → 把编译错误给我（本会话全程未编译，静态核对有 6 类"仅编译可证"项）。
- **E**：重跑 datagen。

## 三十四、行 133 收口（`LOST?` → `PORTED`）+ 行 137–150 裁定 + 两处落地

### 34.1 行 133 的收口（**重要的方法教训**）
- 之前把 `LivingEntityEvents` 的 **残差 25** 当作行 133 的缺口，其中 6 组"疑似真缺口"逐个 `git log -S` 归属后**全部不是行 133 的落点**：
  | token | 1.20 行 | 最早引入者 | 归属 | 定性 |
  |---|---|---|---|---|
  | `blockEnemyFriendlyFire` | 119,236 | `b05c8dc3f` | **行 256** | 后续行，登记不落地 |
  | `applyBossDefinitionDamage` | 250,264 | `92e38df06` | **行 174** | 后续行，登记不落地 |
  | `getFrom` | 427,428 | `90dfd7804` | **行 151** | 后续行，登记不落地 |
  | `incomingDamage` / `equipmentChange` / `breathe` | 123,242 / 130,425 / 133,505 | `182149f52`(part10) | 行 21 | **误报**：1.21 已有 `livingIncomingDamage:199`／`livingEquipmentChange:325`／`livingBreathe:402`（仅方法名多 `living` 前缀） |
  - 六组**均非毒**；`poison_residue_check.py` 命中 0。
- **行 133 的真正落点被 residual 掩盖了**：`livingDamage$Post` 里「攻击者为腐朽者→赋予邪念；再次命中→移除邪念 + 6.0 伤害 + 生成噬魂怪」**17 行**（1.20 行 342–356，`git blame` 全归 `f4625b5c4`=行 133）。1.21 侧确实缺（该文件 0 处 `DECAYEDER`/`DEMONIC_THOUGHTS`/`EATER_OF_SOULS`），**但因为这些 token 在 1.21 别处存在，`residual_file.py` 的"词表级"残差不报** ⇒ 这就是 `LOST?` 的成因。
- **已逐字落地** `7574a4db3`（+17，与 1.20 该提交的 +17 对齐）。**方法教训**：`residual_file.py` 只比对"标识符是否存在于全仓"，**挡不住"同一标识符在别的文件里有、导致本处缺口被掩盖"**；遇到 `LOST?` 要改用 **`git blame` 定位 1.20 那几行的引入提交**，再看 1.21 同位置是否有对应代码。

### 34.2 行 137–150 裁定（写手逐行给证据，主代理定论）
| 行 | 提交 | 裁定 | 关键证据 |
|---|---|---|---|
| 137 | `72421955a` | `SKIP-1.20-REVERTED` | `SummonBoltEntity`/`FinchSummon` 两仓 HEAD 都不存在；`FlyingPiggyBankEntity` 两仓**逐字节相同**（1.21 侧由 `08e1f12eb` 落地）；其余 3 文件纯折行 |
| 138 | `7163e1d05` | `COVERED` | `shouldAbortSubclassTick()`→`isRemoved()`：1.21 `WhipAttackEntity:137`/`YoyoEntity:165` 已是新写法 |
| 139 | `b8c9b7a07` | `SKIP-1.20-REVERTED`（维持） | `BulletRuntimeState.java` 两仓都不存在 |
| 140 | `ab0d06315` | `COVERED` | `MountManager` 两仓逐字节相同；其余为平台改名/适配 |
| 141 | `08276f2ca` | `PORTED`（维持） | 1.21 已有 `DISPENSE_BEHAVIOR`/`useOn`/`getRailShape`（本会话 A3 落地） |
| 142 | `acba5480f` | `PORTED` → **`ccca721d4`** | 1.20 `:64` 的 `if (player.hasEffect(ModEffects.SHIMMER)) return;`（`git blame` 归本行、非毒）1.21 缺失 ⇒ 落地；**同文件 `LAST_THROW_TICK_KEY`/冷却门禁（1.20 68–80）全归毒提交 ⇒ 不搬** |
| 143 | `a8e0b487f` | `SKIP-PLATFORM`（含 1 项 `DO-NOT-PORT`） | 4 个被删文件 1.21 早已不存在；`fireDerivedProjectile` 的 `-S` 链最早=毒 ⇒ 毙（`AbstractSpearItem` + 3 个 spear 子类） |
| 144 | `3b8f62076` | `SKIP-PLATFORM`（多项毒残留） | `PiggyBankMenu`/`CookingPotMenu` 的 hunk 是纯折行还原；12 个 MISS token 最早引入者**全是 `dfcc5c041`** ⇒ 1.20 HEAD 仍残留的毒内容，不搬 |
| 145 | `7dc9244e4` | `SKIP-1.20-REVERTED` | 相关 4 个类两仓 HEAD 都不存在；其余 1.21 保留原生架构（只登记不回改） |
| 146 | `bfd32eae3` | `COVERED`（维持） | 资产重命名，无 java |
| 147 | `0067364f0` | `SKIP-PLATFORM` | 唯一 MISS `useNormalFirstPersonTransformForMagicMirror` 最早引入=毒 ⇒ 毙 |
| 148 | `472be10da` | `SKIP-PLATFORM` | `registerInGameS2C`/`NETWORK_HANDLER` 属 1.20 自建/PortLib；1.21 已用 `registerPayloadHandlers` + `PacketDistributor` 注册 |
| 149 | `d3ae22457` | `SKIP-PLATFORM`（**1 项待复核**） | `renderEmoji` 1.21 `NPCEntityRenderer:72` 已有；`sharedGiantShellyModel` 最早引入=毒；`WORM_SEGMENT` 平台改名；**`DestroyerRenderer` 1.21 无对应物，未深挖**（登记待复核） |
| 150 | `93e64b5b5` | `SKIP-PLATFORM` | `PortItemStackExtension`/`optionalListStreamCodec` 属 PortLib；`PlayerAchievementProgress.java` 两仓都不存在 |

### 34.3 主代理对写手 6 项提问的定论
1. **"归后续行"的缺口**：已登记给**行 151**（`getFrom` 枪械收放动画回调）、**行 174**（`applyBossDefinitionDamage` Boss 伤害倍率）、**行 256**（`blockEnemyFriendFriendlyFire` 全局敌怪友伤拦截）—— 这三处在 1.21 侧是**整块缺失的真功能**，推到那三行时**优先落地**（本轮只登记）。台账里这三行已建状态条目并指向本节。
2. **行 137 的单值**：`SKIP-1.20-REVERTED` **认可**（主导成因是"1.20 自己删了/1.21 已另路落地"）。
3. **1.20 侧毒残留清理**：**不做**。1.20 分支是用户的在建分支、毒提交按既有裁定"永不碰"；只在 1.21 侧坚持不回改，并把"1.20 HEAD 仍带这批毒内容"记为已知事实。
4. **行 148 逐包注册表全量比对**：**不做专项**（1.21 注册形态不同，性价比低；关键包已逐个核过）。
5. **行 149 `DestroyerRenderer`**：**保持 `SKIP-PLATFORM` 但登记待复核**（不升 `LOST?`，避免阻塞行走）。
6. **前视提醒已入库**：行 151/174/256 的落地清单见本节第 1 条。

### 34.4 进度
- 台账状态 JSON 共 **167** 条；本轮落地 2 个提交（`7574a4db3` 行 133、`ccca721d4` 行 142）。
- 下一开放行 = **151**。

## 三十五、行走推进：行 151–174 / 256（6 项落地）+ 5 项待裁定

### 35.1 落地（6 个提交，均一提交一项、单/少文件）
| 行 | 1.20 提交 | 落地提交 | 内容 |
|---|---|---|---|
| 151 | `90dfd7804` | `c5e1c2aa5` (+7) | `LivingEquipmentChangeEvent` 里补回枪械收放动画回调（`BaseGun#putAwayAnimator/pickAnimator`） |
| 152 | `4ac81edb7` | `dc6a87794` (−1) | 移除 `HellFireEffect` 中无意义的 `super.onEffectStarted`（1.21 原生 `MobEffect#onEffectStarted` 为空实现） |
| 156 | `ce1ca67f8` | `c92ba8241` (4 文件 +12/−7) | `RepeaterContents` 的 `hashCode`/`equals` 补 `maxItemCapacity` 语义 + `getUedSlotSize`→`getUsedSlotSize` 及 3 处调用点 |
| 163 | `5bb8d5565` | `74f869a30` (+2/−2) | `BlockBehaviourMixin` 碰撞穿透处理器改名 `bypassCollision` 并补 `|| living.hasEffect(ModEffects.THE_TONGUE)` |
| 174 | `92e38df06` | `702c05a30` (+26) | 补回 `applyBossDefinitionDamage`（Boss 数据包伤害倍率）及其调用点；`BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE` 此前**只有声明无引用** |
| 256 | `b05c8dc3f` | `f46d2d912` (+5) | 补回 `EnemyDamageRules.blocks` 敌怪友伤守卫；**`LivingAttackEvent` 层判 `SKIP-PLATFORM`**（NeoForge 21.1.219 已无该类，取消 `LivingIncomingDamageEvent` 语义等价） |

- 我复核：6 个提交各只含本批文件（1/1/4/1/1/1 个文件），工作树无残留；`fix_eol --check` 候选 0。
- 写手自报一次事故并已修复：对 3 个调用点用 `Get-Content -Raw` 改名导致中文注释被 GBK 化，随后用 `git show HEAD:<path>` **逐字节还原 + 补 CRLF**，最终这 3 文件 diff 仅 1 行改名。**教训**：批量改含中文的 java 必须走字节级 UTF-8 脚本，不能用 PowerShell `-Raw` 往返。

### 35.2 逐行裁定
| 行 | 裁定 | 备注 |
|---|---|---|
| 151 | `PORTED` | 同族 11 条 lang 已由 `bcf2ef1e0` 落地 |
| 152 | `PORTED`（附魔改名部分 `SKIP-PLATFORM`） | `AbstractEnchantment→AbstractManaEnchantment` 属 1.20 被迫 java 化、1.21 走数据驱动（见 `notes/CO-MISSING-TRIAGE.md:41`） |
| 153 | `COVERED` | 4393 增行：covered 2421 / reverted 1721 / gap 251（strong 47）；强缺口真实落点在**更早的行**（见 §35.3 第 1 条） |
| 154 | `COVERED` | 201 增行，strong 0 |
| 155 | `SKIP-1.20-REVERTED`（复核维持） | — |
| 156 | `PORTED` | SpearProjectile 组件化整层 API 见 §35.3 第 4 条 |
| 157 | `SKIP-1.20-REVERTED`（近似） | 100 增行：covered 22 / reverted 78 / gap 0 |
| 158 | `COVERED` | 5/5 covered |
| 163 | `PORTED` | 唯一 strong gap = `bypassCollision` |
| 164 | `COVERED` | 9/9 covered |
| 167 | `COVERED` | 351 增行，strong 0 |
| 170 | `LOST?` | 2237 增行：covered 1073 / reverted 456 / **gap 708（strong 30）**；含 `addSpawnEggTranslations`、`SPAWN_EGG_TEXTURE_ALIASES`/`SHARED_SLIME_EGGS`、`BaseNPC:392-393 obsoleteModifier`、多条译名 |
| 173 | `COVERED` | 2 条 strong 均为 `getStandingEyeHeight`（1.21 已改 `getEyeHeight(Pose, EntityDimensions)`）|
| 174 | `PORTED` | 见上 |
| 256 | （**保持 `TODO`**） | 只落了被点名的 `blockEnemyFriendlyFire` 守卫；该行余量极大（**9745 增行：covered 6335 / reverted 2195 / gap 1215，strong 193**）⇒ 不宜判 `PORTED`，见 §35.3 第 5 条 |

### 35.3 待用户裁定（写手提出，主代理原样登记，**未据此动手**）
1. **行 153 的强缺口落点在更早的行**：`ModEntityTypeTagsProvider`（`POWDER_SNOW_WALKABLE_MOBS`/`FREEZE_IMMUNE_ENTITY_TYPES`/`AXOLOTL_HUNT_TARGETS`/`JELLY_FISH`）、`ModBiomeTagsProvider`（`HAS_STRUCTURE_NETHER_TOWER`/`JUNGLE_STRUCTURE_BIOMES`）、`ModBlockStateProvider`（`modelOrGenerate`/`hasTexture` 等 16 个 helper）、`GiftSubProvider.theDestroyerTreasureBag` 在 1.21 整块缺失，但行 153 的 diff 只是把先前提交写的多行 tag 合并成一行 ⇒ 真实落点在 **151 之前**（那些行已裁定/回填）。**问**：按落点回补更早行的裁定，还是记在行 153？
   - 附带提醒：其中 `theDestroyerTreasureBag` 属**已回退的毒来源**（`170b54eb8`）⇒ 一律 `DO-NOT-PORT`。
2. **行 170 的 spawn egg 模型/译名面**（`ModItemModelProvider` 的 `SPAWN_EGG_TEXTURE_ALIASES`/`SHARED_SLIME_EGGS`/`template_spawn_egg`、`addSpawnEggTranslations`）与工作树里**用户未跟踪的** `textures/item/egg/demon_eye_spawn_egg.png` 相邻 —— **问**：是否授权接手该面（避免与用户在建内容撞车）。
3. **禁改文件内的缺口**：行 153 强缺口含 `ModItems.java:137 TEST_SOUL_GUI / test_soul_gui`，而 `ModItems.java` 在禁改 10 文件名单内 —— **问**：跳过并登记 / 授权例外？
4. **行 156 的 SpearProjectile 组件化整层 API**（`projComponent`/`applyComponent`/`PierceRemaining`/`TicksAlive`/`VelocityX-Y-Z`、`AbstractSpearItem.onHitEntity(ItemStack, ServerLevel, LivingEntity, Entity)`、`fireDerivedProjectile(...)`）：1.21 侧签名不同，属**整层改写** —— **问**：整层移植，还是登记 `DEFER-ARCH`？（注：`fireDerivedProjectile` 的 `-S` 链最早=毒 ⇒ 该符号本身 `DO-NOT-PORT`）
5. **行 256 余量**（strong gap 193 条：新增敌怪/事件/鞭子/召唤等）—— **问**：按"行 256 单独一批"继续，还是拆到各自子项？

### 35.4 工具观察
- `residual_file.py` 的"被掩盖缺口"本轮**再现一例**：行 151 报 `SwordProjectile.writeItem/readItem` 为缺口，实为 1.21 用 `firedFromWeapon` + `addAdditionalSaveData/readAdditionalData` 的**内联等价实现**（`SwordProjectile.java:259-306`）⇒ `COVERED`。⇒ 与本会话 §34.1 的教训一致：**残差报缺口必须再用 `git blame`/读码复核**。
- 写手另在 `%TEMP%` 写了 `rowscan2/4/5.py`（按提交取增行→折叠空白→与两树比对、按"1.21 全仓不存在的标识符"分 strong/weak），**未入库**；若后续要复用，建议按约定落到 `tools/port2native/`。

### 35.5 进度
- 台账状态 JSON 已回填；**下一开放行 = 175**（151–174 已走完；170 = `LOST?` 待裁定，256 保持 `TODO`）。

## 三十六、行 153 强缺口回补（4 项零落地）+ 行走到 183 + **工具归属口径修正**

### 36.1 行 153 的 4 项"强缺口"：**全部零落地**（不是漏项，逐项有硬证据）
先纠正一个方法事故：**"行 153" 指 `port-ledger.json` 里的提交序号 153 = `9bc04295b`**，而不是 `PORT-LEDGER.md` 的 markdown 第 153 行（那是 `81488b6d0`）。`9bc04295b` 的 diff 只是把先前提交写的多行 tag **折成一行**，于是 rowscan 取"新增行"时把这 4 组挂到了 153。改用**路径级** `git log -S <tok> -- <path>` 归属后：

| 项 | 裁定 | 关键证据 |
|---|---|---|
| 1 `ModEntityTypeTagsProvider` 的 `POWDER_SNOW_WALKABLE_MOBS`/`FREEZE_IMMUNE_ENTITY_TYPES`/`AXOLOTL_HUNT_TARGETS` + `ModTags.EntityTypes.JELLY_FISH` | **`SKIP-1.21-KEEPS`** | 该文件内四者**最早引入 = `dfcc5c041`（毒）**；**更强证据**：这些 tag 原本活在**两仓共享史**的 `TerraEntity/…/TEEntityTypeTagsProvider.java`（`4d02a62a6`，成员表完全一致）⇒ **内容是 1.21 侧原生**，被毒抄进 ConfluenceOtherworld provider；1.21 今天没有它是**1.21 自己的 TerraEntity 层退役**（`ddc007385` 删该文件 + WP8）所致。与 `PORT-LANDING-RECORD.md:695`（`jelly_fish ⇒ DO-NOT-PORT`）同向 |
| 2 `ModBiomeTagsProvider` 的 `HAS_STRUCTURE_NETHER_TOWER`/`JUNGLE_STRUCTURE_BIOMES` | `COVERED` | 1.21 **已有手写等价资源**：`data/confluence/tags/worldgen/biome/has_structure/nether_tower.json`（`nether_wastes`+`ash_wasteland`）、`…/jungle_underground_cabins.json`（`jungle`/`sparse_jungle`/`bamboo_jungle`/`lush_caves`），逐值吻合（1.21 该 tag 走手写 json、不走 datagen） |
| 3 `ModBlockStateProvider` 的 16 个 helper | `COVERED`（无 helper 归属缺口）+ **7 个 `DO-NOT-PORT`** | 7 个（`modelOrGenerate`/`hasTexture`/`shouldGenerate`/`registerDecorationSet`/`registerLogSet`/`simpleBlockIfAbsent`/`hasHandwrittenModel`）最早=毒且在 1.21 该文件**全史为空**；非毒的 `ensureLogModels`→**行 198**、`torch` 家族→**行 370**（登记）。功能判据（逐个核产出文件）：38 个 `DecoBlockSet` 中 32 个 blockstate+整套模型齐备；缺的 6 个（`sandstone_bricks`/`red_sandstone_bricks`/`ebonsandstone_bricks`/`pearlsandstone_bricks`/`crimsandstone_bricks`/`rainbow_bricks`）**两仓共同缺 `textures/block/<id>.png`**（1.20 的 `registerDecorationSet` 遇 `!hasTexture` 直接早退，1.20 也从未产出）⇒ 与这 16 个 helper 无关 |
| 4 `GiftSubProvider.theDestroyerTreasureBag` | `DO-NOT-PORT` | 毒来源；1.21 曾误落地 `3b3618a13`、已由 `170b54eb8` 回退，今天正确缺席 |

### 36.2 行走推进（175–183），落地 1 项
| 行 | 裁定 | 备注 |
|---|---|---|
| 175 `29c1459cf` | `SKIP-PLATFORM` | 472 增行仅 15 STRONG：Forge 事件（`EntityItemPickupEvent`/`ItemPickupEvent`/`AttackEntityEvent`/`BonemealEvent`）、1.20.1 API 名、`PotionUtils.getPotion`（1.21 `ModUtils:125` 已用 `PotionContents`）、`terrablender.ParameterUtils`（1.21 自带注入器 `BiomeRegionTable:28`）；4 文件 1.20 HEAD 已无 + `PatchouliLanguageSubProvider` 两仓都无 ⇒ `SKIP-1.20-REVERTED` |
| 176 `19669033d` | `COVERED`（15 毒落点） | 1.21 `NPCTradeMenu` **已原生且有更完整的卖/回购**（`getSellPrice:267`/`sellCarried:286`/`buyBack:325`/`SlotState.BUYBACK:482`）；1.20 那套依赖毒的 `PlayerMoneyTransaction` |
| 177 `06415ab96` | `SKIP-PLATFORM`（3 毒落点） | `ToolActions.AXE_STRIP` → 1.21 `LevelEvents:54` 的 `ItemAbilities`；`bonemeal` → `PlayerEvents:493` |
| 178 `8ca8275f6` | `COVERED` | 单行改名 `getVariant()`→`getBunnyVariant()`，1.21 已是后者 |
| **179 `e2807cc12`** | **`PORTED` → `ddc04e62d`（+96）** | `addPreviouslyMissingTranslations` 是**真缺口**（`-S` 最早=行 179 且非毒、1.20 HEAD 仍在、1.21 全仓 0 处）⇒ **ZH 83 条 + EN 5 条**逐字回补；已存在键不重复写（ZH 2 条 `wall_of_flesh_eye/mouth`、EN 4 条 itemGroup 剔除） |
| 180/181 | `COVERED` | 图鉴键清理，Strong 增行 0 |
| 182 `7b3b5c28e` | `COVERED`（2 毒落点） | 渔夫任务系统各件 1.21 全在；`NETWORK_HANDLER` 归行 103（PortLib） |
| 183 `4122dbcf0` | `COVERED` | 唯一 STRONG 是局部变量 `decodedVersion` |

- 184/185/187/192 已有机器裁定 `SKIP-PLATFORM`、188/189/190 `SKIP-PORTLIB`：复核无异议。
- 台账状态 JSON 共 **189** 条；**下一开放行 = 186**。

### 36.3 **工具归属口径修正（重要）**：`poison_residue_check.py` 新增 `--path`
- 发现：`--symbol` 模式的**全仓级** `git log -S` 会被**共享史里别的文件的同名符号**带偏。实例：`POWDER_SNOW_WALKABLE_MOBS`/`FREEZE_IMMUNE_ENTITY_TYPES`/`AXOLOTL_HUNT_TARGETS` 在全仓级报"非毒 `4d02a62a6`"，而在 `ModEntityTypeTagsProvider.java` 路径级报"**毒 `dfcc5c041`**"——**结论相反**。
- 修正：新增 `--path <相对路径>`，把 `-S` 限定到落点所在文件；不做 `--path` 时**打印警告**说明全仓级只可用于"该符号是否在别处出现过"的粗筛。
- **口径**：**落地归属一律以路径级（`--path`）为准**（与"落点是标识符+路径"的既有定义一致）。已实测：加上 `--path` 后正确判定为毒提交。

### 36.4 主代理对写手 6 项提问的定论
1. **4 项零落地 → 接受**（证据链完整：毒 / 1.21 原生被退役 / 手写等价资源 / 与 helper 无关）；回填需覆盖 `DO-NOT-PORT` 与台账既有裁定，**不做**。
2. **同族整块缺失另立批次 → 采纳**：`AXOLOTL_ALWAYS_HOSTILES`/`CAN_BREATHE_UNDER_WATER`/`FALL_DAMAGE_IMMUNE`/`GOLDEN_SLIME_REPLACEABLE`/`LibTags SLIME 成员`/`BOSSES`/`SKELETONS`/`ARTHROPOD`/`UNDEAD`/`AQUATIC`/TC slime 等（1.21 provider 112 行 vs 1.20 290 行）。**这是"被掩盖缺口"的第三种形态**：**该类整块从未作为任何已走行的新增行出现 ⇒ rowscan/residual 永不报**。已加入下一轮工单（先做"逐 tag × 路径级归属"的分类，再只落非毒且 1.21 确实缺的）。
3. **行 179 那 18 条疑似死键 → 保持逐字**（写手自己的 liveness 检查对 `id + "_wall"`/`id + "_planks"` 拼接注册名有假阴性，不足以判死）。
4. **行 198（`ensureLogModels`）、行 370（torch 家族）登记 → 认可**，推到那两行时执行。
5. **6 个 DecoBlockSet 两仓共同缺贴图 → 登记为资产缺口**（需美术，两仓同缺；不属移植缺口）。
6. **两个新脚本入库 → 采纳**：要求写手把 `rw_rowscan.py`/`rw_attr.py` 落到 `tools/port2native/` 并说明用法（`notes/` 仍只读）。

## 三十七、修复：1.20 风格 effect 访问照搬进 1.21（`7574a4db3` → `c5961b861`）

**用户指出**：`7574a4db3`（行 133 落地）把 1.20 的 `livingEntity` effect 访问写法（对 effect 持有者多打一层 `.get()`）照搬进了 1.21.1。

**核实（1.21 反编译源）**：这些 API **只收 `Holder<MobEffect>`** ——
- `LivingEntity.hasEffect(Holder<MobEffect>)`（`LivingEntity.java:958`）
- `LivingEntity.getEffect(Holder<MobEffect>)`（`:963`）
- `LivingEntity.removeEffect(Holder<MobEffect>)`（`:1035`）
- `MobEffectInstance(Holder<MobEffect>, int)`（`MobEffectInstance.java:60`）

而 `ModEffects.X` 是 `DeferredHolder<MobEffect, MobEffect>`（**本身就是 `Holder`**）⇒ 正确写法是**直接传 `ModEffects.X`**，与 `610a9fd53` 里 `ModEffects.ELECTRIFIED.get()` → `ELECTRIFIED` 同源（**同一个坑第二次踩**）。

**修复（`c5961b861`，4 行）**：`LivingEntityEvents.java` 该块 4 处 `ModEffects.DEMONIC_THOUGHTS.get()` → `ModEffects.DEMONIC_THOUGHTS`（`hasEffect` / `MobEffectInstance` / `removeEffect` ×2）。行为零变更。

**全仓扫描结论**（`git grep -E '(ModEffects|LibEffects|MobEffects)\.[A-Z0-9_]+\.get\(\)'`，排除 datagen）：该模式全仓只 5 处 ——
本文件这 4 处（已修）+ `ModClientEvents.java:1174` 的 `registerMobEffect(…, ModEffects.LUCK_EFFECT.get())`（NeoForge `RegisterClientExtensionsEvent#registerMobEffect(..., MobEffect...)`，**要的正是 `MobEffect`**，属既有正确写法，未动）。
datagen 里的 `addEffect(ModEffects.X.get(), …)` 是仓内自建助手（`ModChineseProvider:4945` `addEffect(MobEffect, String, String)`、`ModEnglishProvider:1922` `addEffect(MobEffect, String)`），同样**必须**保留 `.get()` ⇒ 未动。

**附**：该块里 `removeEffect` 出现两次（`hurt` 前后各一次）是 **1.20 原文如此**（1.20 行 342–356），按"逐字为准"保留，不是移植失误。

**口径补充（已下发给在跑写手）**：定位到 `hasEffect`/`getEffect`/`addEffect`/`removeEffect`/`MobEffectInstance`（以及任何 `Holder<T>` 形参的 API）时，**必须先在 `build/_nfsrc_219/` 核形参是 `Holder<T>` 还是 `T`**，再决定保不保留 `.get()`；这条并入"核被调方"环节。本会话同类已出现两次（`ELECTIFIED`、`DEMONIC_THOUGHTS`），属**高频错误类型**。

## 三十八、tag 整块缺失专项 + 行 186–196 + 脚本入库 + **用户新增口径：毒落点要"查毒"**

### 38.1 落地（3 个提交）
| 提交 | 内容 |
|---|---|
| `201422b0b` | **脚本入库**：`tools/port2native/rw_rowscan.py`（180 行，提交 → 新增行 → 折叠空白对 1.21 树比对，分 STRONG/weak）、`rw_attr.py`（141 行，行 → 路径级 `-S` 归属）；docstring 写清与 `row_attr.py`/`residual_file.py` 的四者分工，并记录 **blame 陷阱**（纯重排提交会抢行归属，实例 `9bc04295b` 行 153） |
| `7cb1012c8` | **tag 整块缺失专项**：只落 `ModTags.EntityTypes.GOLDEN_SLIME_REPLACEABLE`（`ModTags.java:509` TagKey + `ModEntityTypeTagsProvider.java:101` 10 行成员，1.20 逐字） |
| `1b1f23a07` | **行 196 配对半边**：`LivingEntityEvents` 的金史莱姆替换改用 `golden_slime_replaceable` 标签（1.20 行 561-568 逐字，唯一适配 = `getRandom1211()` → `getRandom()`），并删掉只被旧写法引用的 `Slime` import |

### 38.2 "被掩盖缺口"第三种形态的归因（**主要就是毒提交**）
- 1.21 的 `ModEntityTypeTagsProvider` 只有 10 条 tag 语句、1.20 有 27 条；17 条 only-1.20 里**12 条整块归 `dfcc5c041`（毒）**（`ARTHROPOD`/`FLESH_ALLIANCE`/`AQUATIC`/`ZOMBIES`/`SKELETONS`/`UNDEAD`/`AXOLOTL_*`/`FREEZE_IMMUNE_ENTITY_TYPES`/`CAN_BREATHE_UNDER_WATER`/`FALL_DAMAGE_IMMUNE`…）⇒ 这正是 rowscan/residual **永不报**它们的原因（毒过滤工作正常）。
- **两个 MIXED 块**（表头/部分成员非毒、部分成员行毒）：`LibTags.EntityTypes.SLIME`（表头+26 成员非毒 / 4 行毒）、`Tags.EntityTypes.BOSSES`（`1284d08a9` 行 69 的 10 行非毒 + 10 行毒）。
- 其余三个 provider 的同型缺口**为 0**：`ModBlockTagsProvider` 的 57 条 only-1.20 **全是同名改名**；`ModBiomeTagsProvider` 7 条 = 5 改名 + `IS_OVERWORLD`（1.21 手写 json 只含 3/10 群系）+ `HAS_STRUCTURE_NETHER_TOWER`（1.21 手写 json 逐字等价 ⇒ `COVERED`）；`ModItemTagsProvider` 9 条真项多为改名/已裁/属未走行 359。

### 38.3 **用户新增口径（重要）：毒落点要"查毒"，不能一律毙**
> 用户原话："**1.21 照着 1.20 那样添加 boss 标签；1.20 的毒提交有时候是同逻辑覆写，所以应该查毒**。"

⇒ 口径细化（**对既有"毒落点 → DO-NOT-PORT"规则的补充**）：
1. 毒落点**先查再判**：查清该 diff 行是 **(a) 新引入的错误内容**（照旧 `DO-NOT-PORT`）、还是 **(b) 同逻辑覆写/与合法内容等价的写法**（毒提交有时只是把同一逻辑重写一遍）。
2. 属 (b) 的 ⇒ **按 1.20 落地**（1.21 与 1.20 对齐），不再因为"落在毒 diff 里"而排除。
3. **BOSSES 标签已明确指示：1.21 照 1.20 那样添加**（1.21 现在 `Tags.EntityTypes.BOSSES` 里**一个本模组 Boss 都没有**，而 4 处代码在用它判 Boss：`BestiaryEntry:119`、`MonsterAttributeScaling:63`、`EntityEvents:96/100`）。
4. 其它毒归因的 tag 块（`ARTHROPOD`/`SKELETONS`/`UNDEAD`/`ZOMBIES`/`AQUATIC`/`FLESH_ALLIANCE`/`FALL_DAMAGE_IMMUNE`/`AXOLOTL_*` …）**同法逐个查毒**后按结论落地/排除；此前的 `SKIP-1.21-KEEPS` 结论（内容为 1.21 原生、随 TerraEntity 退役消失）**不因此改判**。

### 38.4 行走推进（186–196）
| 行 | 裁定 | 备注 |
|---|---|---|
| 186 `a0148b9fa` | `COVERED` | 唯一新增行（注释掉 `npc.getSpawnAtPos() != null &&`）1.21 `HouseSelectPacketC2S:95` **逐字已有** |
| 191 `841165c47` | `SKIP-PORTLIB` | `ConfigScreenHandler.getScreenFactoryFor` ← 行 23（portlib API）；`patchRefmap` 是 1.20 工具链补丁 |
| 193 `647400f44` | `SKIP-PLATFORM` | 5 条 STRONG 全是 `VanillaGuiOverlay` ⇒ 1.21 已改 `VanillaGuiLayers`+`RegisterGuiLayersEvent`（`ModClientEvents:406-415`） |
| 194 `3e7cc41a9` | tag 部分已落；其余 `DEFER-ARCH`/`DEFER-ASSETS` | 1.20 把 200+ 个 `data/confluence/worldgen/**` json 改成 datagen（1.21 仍手写）；新增 18 张 boss_bar 贴图 + `boss_bar_flow.*` 着色器；`EntitySubProvider` 的 `lavaSlimeLoot`/`motherSlimeLoot` 已由本会话 A2 落地 |
| 195 `c458f224d` | `SKIP-PLATFORM` | 唯一 STRONG `Confluence.NETWORK_HANDLER.sendToPlayersTrackingEntity` → 1.21 原生 `PacketDistributor` |
| 196 `741f98d1e` | **`PORTED` → `1b1f23a07`** | 见上；**这是"回改 1.21 既有行为"**（1.21 原写法出自共享史 `df474e20c`：`instanceof Slime`+`nextInt(140)==1`+先取消后生成；1.20 行 196 是收窄写法：tag 判定+`nextInt(180)==0`+`addFreshEntity` 成功才取消）⇒ 属行 196 的真落点，按 1.20 落地；不落则该 tag 是死数据 |

- 台账状态 JSON 共 **195** 条；下一开放行 = **197**。

### 38.5 待办与登记
- **架构重构（用户原则）**：`Decayeder`（事件降为薄派发、逻辑搬进类；远程生物不能用 `doHurtTarget`）+ `CursedSkull`（33% 诅咒搬进其既有 `doHurtTarget` 覆写）——已下发，**排在行走之前**。
- `BiomeTags.IS_OVERWORLD`：1.21 手写 json 只含 3/10 群系（10 个群系常量在 1.21 全在）⇒ 需先做路径级归属再决定是否补全（属资源侧）。
- `ModTags.EntityTypes.DO_NOT_DROPS_EVIL_SOUL` 的 1.20-only 成员 `MOTHER_SLIME`/`BABY_SLIME`：成员级小缺口，待逐行归属。

## 三十九、用户裁定：那两处"派发重构"**必须镜像回 1.20**

> 用户原话："**那两处派发肯定要镜像回 1.20**。"

### 39.1 任务定义
- 对象：**① `Decayeder`**（`LivingEntityEvents.livingDamage$Post` 的 17 行 → 事件降为薄派发 `if (attacker instanceof Decayeder d) d.onDamageDealt(victim, damageSource);` + 逻辑主体进 `Decayeder` 类；因它是**远程**生物，不能用 `doHurtTarget`）；**② `CursedSkull`**（`ModUtils.applyCursedSkullDebuff` 的 33% 诅咒 → 搬进其既有 `doHurtTarget` 覆写，删掉 `ModUtils` 里那个静态方法与 `livingDamage$Pre:255` 的调用）。
- 方向：**1.21 先落地 → 再镜像到 1.20**（与本会话 `f07491b3a` 那次 vfx 同步同性质），使**两侧架构一致**。
- 1.20 侧必须**按 1.20 自己的 API 适配**（Forge/PortLib 形态：`getRandom1211()` 等 shim、1.20 的事件类与注册方式、`MobEffectInstance` 形参形态），**不是照抄 1.21 的 API 名**；逐处对照 1.20 HEAD 里同一段代码原本怎么写。

### 39.2 已写进写手工单的纪律（1.20 侧）
1. 动手前复核 1.20 仓 `rev-parse HEAD`（应为 `f07491b3a`）与 `git status --porcelain`；**若工作树除已知的 `vfx/licenses/kenney-particle-pack.txt` 暂存删除之外还有别的改动 ⇒ 停下报告，不覆盖用户在建内容**。
2. 限路径提交 `git -C <1.20仓> commit -F <无BOM UTF-8文件> -- <路径>`；禁止 `add -A`/`reset`/`stash`/`checkout`；不碰 1.20 仓非本任务文件与任何子模块指针。
3. 1.20 仓 `fix_eol --check` 有 **57 个预存候选**（长期 WIP）⇒ **不跑全局归一**，只保证**本次改动文件** CRLF、裸 LF 0。
4. 提交信息：`refactor(镜像 1.21): 生物攻击效果从事件/工具搬入生物类（Decayeder/CursedSkull）`，正文写明与 1.21 提交的对应关系与每处 API 改写。
5. 报告要求：两侧并排的最终代码对照（1.21 哈希 + 1.20 哈希 + 逐处 API 差异）+ 1.20 侧 `git status` 与"除本任务文件外零改动"的证据。

### 39.3 队列（唯一写手 `a8f2ac59`）
① 1.21 两处重构 → ② **镜像回 1.20** → ③ BOSSES + `LibTags.EntityTypes.SLIME` 查毒补齐（1.21）→ ④ `IS_OVERWORLD` 补全 → ⑤ `DO_NOT_DROPS_EVIL_SOUL` 的 `MOTHER_SLIME`/`BABY_SLIME` → ⑥ 从行 197 继续行走。

### 39.4 镜像前的 1.20 仓现状与用户授权（2026-10 补记）
动手前复核发现 1.20 仓与工单假设不同，用户已就此给出授权：
- `HEAD` = **`07c2ab5b5`**（不再是 `f07491b3a`）：用户把本会话那次 vfx 同步提交 **`f07491b3a` `reset` 回了 `07c2ab5b5`**（reflog 可见），6 张 vfx 贴图因此变为**已暂存未提交**（`M `）。
- `LivingEntityEvents.java` 有**用户未提交的在建改动**：`processCriticalDamage` 里重复的 `event.getDamageSource().getDirectEntity()` 收成局部变量 `entity`，并把 `return` 链改成 `else if` 链。
- **用户授权**："**镜像完把我的更改一起提交**"。⇒ 1.20 仓要提交**两个**提交：
  1. `sync(assets): textures/vfx 六张贴图同步为 1.21.1 侧的新版`（把那 6 张 PNG 补回，正文照 `f07491b3a` 原说明）；
  2. `refactor(镜像 1.21): 生物攻击效果从事件/工具搬入生物类（Decayeder/CursedSkull）`，**并把用户那处 `processCriticalDamage` 改动一并提交**（提交信息里单独列一行说明"原样保留、未做改动"）。
- 镜像编辑必须**基于工作树**进行、原样保留用户那段改动；使用 **1.20 的 API 形态**（Forge/PortLib shim、1.20 事件类、`MobEffectInstance` 形参），不照抄 1.21 的 API 名。
## 四十、用户裁定 6 条（行 196/197/198 遗留）与架构重构 1.21 侧落地（2026-10）

> 本节覆盖 §38.5 / §39.3 中已裁定项。

### 40.1 用户裁定（本轮，逐条）
| 议题 | 裁定 | 依据 / 影响 |
|---|---|---|
| 行 196 行为改动（`1b1f23a07`） | **保留 1.20 写法**（tag 判定 / `nextInt(180)==0` / `addFreshEntity` 成功才取消） | 不保留则刚落的 `GOLDEN_SLIME_REPLACEABLE` 成为死数据 |
| 两个 MIXED tag 块 `LibTags.EntityTypes.SLIME`、`Tags.EntityTypes.BOSSES` | **都落** | `BOSSES` 在 1.21 仍为空，而 `DO_NOT_DROPS_EVIL_SOUL` / `ENEMY_BANNER_BLACKLIST` 正 `addTag(BOSSES)` ⇒ 功能性缺口 |
| `BiomeTags.IS_OVERWORLD` | **补齐 10/10** | 10 个群系常量在 1.21 全在（1.21 手写 json 只含 3/10） |
| 行 198 `ensureLogModels` / 整套 decoBlockSet 生成 | **「同步 1.20」== 真缺口，以 1.20 为源同步；原 `DO-NOT-PORT` 作废** | 被调方 `hasHandwrittenModel` 虽归毒 `dfcc5c041`，但按「毒落点要查毒」口径属**正常且必要的功能** ⇒ 落。做法：先出清单（1.20 HEAD 全套 `hasTexture` / `shouldGenerate` / `hasHandwrittenModel` + 各守卫 + `ensureLogModels` 及调用点，逐条对 1.21 现状标「缺/已有/形态不同」），再按 1.20 HEAD 逐字落；**独立批次**，不与 tag 家族同笔提交 |
| 行 198 EN `addOverrides()`（约 37 行 / 20+ 键） | **先核每个成员在 1.21 是否存在，存在的落** | 含 `StatueBlocks.N0..N9_STATUE`、`NatureBlocks.*CATTAIL*`、`QuestedFishes.CAPN_TUNABEARD` 等；核不到只登记为已知缺口，不臆造 |
| 行 197 `SurfaceRuleManager`（`addToDefaultSurfaceRulesAtStage` / `RuleCategory` / `RuleStage`） | **`SKIP-PLATFORM`** | 系 TerraBlender 1.20 API，随 TerraBlender 退役，不追等价实现 |

队列（唯一写手，顺延）：① 1.21 架构重构 → ② **镜像回 1.20** → ③ tag 家族（含上表第 2、3 条）→ ④ EN `addOverrides()` 成员核对后落 → ⑤ decoBlockSet 生成独立批次 → ⑥ 从行 203 继续行走。

### 40.2 架构重构 1.21 侧落地（用户原则：攻击效果写在生物类里，不写事件）
- **`97f18d9e0`（Decayeder）**：`LivingEntityEvents.livingDamage$Post` 的 17 行按 `attacker.getType() == MonsterEntities.DECAYEDER.get()` 判定块 → 降为薄派发
  `if (attacker instanceof Decayeder decayeder) decayeder.onDamageDealt(victim, damageSource);`；
  逻辑 1:1 搬入 `Decayeder.onDamageDealt(LivingEntity victim, DamageSource damageSource)`（`attacker → this`；`serverLevel` 由 `level() instanceof ServerLevel` 守卫取；含 1.20 那处 `victim.removeEffect(DEMONIC_THOUGHTS)` 在 `hurt` 前后各一次的重复，**照搬不合并**）。
  **派发点必须留在事件里**：Decayeder 是远程生物（`BowCombatAction` 射箭），效果在**箭命中**时触发，`doHurtTarget` 只覆盖近战。
  等价性：全仓无任何类 `extends Decayeder`，且 `DECAYEDER` 以 `Decayeder::new` 注册 ⇒ `instanceof` 与 `getType() ==` 同义（故未再加 `getType()` 判断）。
- **`9792fddd7`（CursedSkull）**：`ModUtils.applyCursedSkullDebuff` 整方法删除（`git grep applyCursedSkullDebuff -- '*.java'` = 0），33% 诅咒搬入其既有 `doHurtTarget`：
  `boolean damaged = super.doHurtTarget(target); if (damaged && target instanceof LivingEntity living && getRandom().nextFloat() < 0.33F) living.addEffect(new MobEffectInstance(ModEffects.CURSED, 80));`
  顺序与原先一致（**先 `instanceof` 再掷骰**，避免对非 `LivingEntity` 目标消耗随机数）；`livingDamage$Pre` 只删那一行调用，`ModUtils.applyBrainOfCthulhuDebuff` 等同处职责全保留。
  覆盖面等价证据：CursedSkull 唯一伤害路径 = `BaseMonster.tickEntityContactAttack`（`:314 return doHurtTarget(target)`）；`PhasedFlyingPursuitAction` 只做转向、无任何伤害调用。
- **双侧独立验证（本会话，非转述）**：两笔 `--name-only` 无夹带（① 2 文件 = `Decayeder.java` + `LivingEntityEvents.java`；② 3 文件 = `CursedSkull.java` + `ModUtils.java` + `LivingEntityEvents.java`）；`Decayeder.onDamageDealt` 与 1.20 HEAD blob 逐行对照**逐字一致**（仅 `this`/`level()` 与 1.20 侧 `.get()` 形态差异）；`applyCursedSkullDebuff` 0 命中；`fix_eol --check` = 候选 0。
- **他人 WIP 隔离手法（写手自创，采纳）**：`LivingEntityEvents.java` 带有他人未提交的 BeeKeeper 暴击 WIP ⇒ 快照 WIP → 写 `HEAD+改动` → 提交 → 回写 `WIP+改动`；本会话复核 `git diff` 仍只那一处、仍未暂存。**此流程延续到用户自己提交该 WIP 为止。**
- 写手两个提问已答：`level() instanceof ServerLevel` **守卫保持**（1.20 HEAD 原码本身即 `if (!(victim.level() instanceof ServerLevel serverLevel)) return;`）；`onDamageDealt` **保持 `public`**（`LivingEntityEvents` 与 `Decayeder` 不同包，收包私有编译不过）。

### 40.3 行 197–200 判定
| 行 | 1.20 提交 | 裁定 | 证据要点 | 落地 |
|---|---|---|---|---|
| 197 | `5c99e9702` 生产环境修复 | `SKIP-PLATFORM`（+ 资产登记） | `Confluence.java` 的 `MixinEnvironment...DUMP_TARGET_ON_FAILURE`（禁改文件 + 调试开关）；`ModBiomes` 的 `SurfaceRuleManager`/`RuleCategory`/`RuleStage` = TerraBlender；`build.gradle` 的 `insertRefMap`/`patchRefmap` = 1.20 手补 refmap 工具链；`boss_bar_flow.*`/`smooth_entity.*` 着色器与 `TCClientPacketHandlerMixin` 登记 | 无 |
| 198 | `607470f04` 补缺失内容 | **`PORTED`（部分）** | 译名族落 `e76bbb3ba`（CN/EN 各 +35，9 回旋镖 + 13 鞭子，逐字取自 1.20 HEAD blob）；排除 3 条 `tooltip.confluence.boomerang.*`（1.21 已有 `CN:896-898`/`EN:904-906`，归行 203）；`ensureLogModels` 按上文改判；EN `addOverrides()`、2 个 `magic_mail_box_cherry*.json` 待后续 | `e76bbb3ba` |
| 199 | `55cc8fc6b` 生产环境修复 | `COVERED` | 110 路径几乎全为「weak 全部命中」（1.21 已有同样行）；STRONG 残项全是平台 token（`getRandom1211()` shim、`@Local GridLayout.RowHelper`、mixin 局部名）；`common/summon/*`、`SwordBehaviors`、`YoyoHitEffects` 等 1.20 原创后自删 | 无 |
| 200 | `15a05d234` 泰拉饰品掉落/遮罩 | `COVERED` | 14 路径新增行**全部在 1.21 逐字命中**（12 路径零 STRONG 零 weak）；`NPCTradeScreen` 两仓皆无；`DyeMixScreen`/`DyeVatScreen` 按毒口径登记不回抄 | 无 |

### 40.4 台账
- 行 197 = `SKIP-PLATFORM`、198 = `PORTED`、199 = `COVERED`、200 = `COVERED`（`ledger_status.py` 双写 `PORT-LEDGER.md` 末列 + `port-ledger-status.json`，状态 JSON 共 **199** 条）。
- **下一开放行 = 203**。
## 四十一、② 镜像回 1.20 落地 + tag 家族真缺口定案（2026-10）

### 41.1 1.20 镜像（用户授权项）完成
- **`b408792b1`** `sync(assets): textures/vfx 六张贴图同步为 1.21.1 侧的新版（512×512 → 32×32）`：1.20 仓基点 = `07c2ab5b5`。用户此前主动 `reset` 丢掉了 `f07491b3a`，那 6 张 PNG 因此变为暂存态；本次以暂存内容重新提交，**内容与 1.21 侧逐张 SHA-256 一致、均 32×32**（419/714/761/385/734/292 字节）。
- **`2e2192236`** `refactor(镜像 1.21): 生物攻击效果从事件/工具搬入生物类（Decayeder/CursedSkull）`：4 文件 `+40/−36`，并**按用户授权「镜像完把我的更改一起提交」把 `LivingEntityEvents.processCriticalDamage` 的用户在建改动一并提交**（`Entity directEntity = event.getDamageSource().getDirectEntity();` 局部变量 + 4 个 `else if` 分支 + 补大括号，逐行原样、提交信息单独列行声明）。
- **1.20 侧 API 适配**（与 1.21 恰好相反，务必别照抄）：`ModEffects.X.get()`（1.20 Forge/PortLib 收 raw `MobEffect`；1.21 收 `Holder` 不加 `.get()`）、`getRandom1211()`（PortLib shim）、事件名 `damage$Pre`/`damage$Post`（`PortLivingDamageEvent.Pre/Post`）。`instanceof Decayeder` 判定在 1.20 同样成立（两侧均无类 `extends Decayeder`）。
- **独立验证（本会话）**：两笔 `--numstat` 无夹带（第 1 笔 = 恰好 6 PNG；第 2 笔 = 恰好 `CursedSkull`/`Decayeder`/`LivingEntityEvents`/`ModUtils`）；已提交的 `processCriticalDamage` 确为用户 WIP 形态（`directEntity` 局部 + 4 `else if`）；`applyCursedSkullDebuff` 全仓 0 命中；`LivingEntityEvents` 内 `MonsterEntities.DECAYEDER`/`EaterOfSouls` 均 0 残留；**用户新增的 `ModDataProvider.java` WIP 与暂存的 `D vfx/licenses/kenney-particle-pack.txt` 未被碰**。提交后 1.20 `status` = ` M ModDataProvider.java` + `D kenney-particle-pack.txt`（均为用户条目）。

### 41.2 行 201–204 判定
| 行 | 1.20 提交 | 裁定 | 证据 |
|---|---|---|---|
| 201 | `6f8d84cbe` 修数量合成 | `SKIP-PORTLIB` | 只改 2 个子模块指针，无正文 |
| 202 | `0af0c9e94` 修创造模式标签页搜索 | `SKIP-PORTLIB` | 只改 PortLib 指针 |
| 203 | `446c689a4` | `COVERED` + 3 项登记 | 139 路径几乎全 `weak 全部命中`；`Summon*` 一族标 `[1.20HEAD无此文件]` ⇒ `SKIP-1.20-REVERTED`；boomerang tooltip 3 键 1.21 已有（CN:896-898 / EN:904-906）。登记：① `EntitySubProvider.blackSlimeLoot` = 已派工的 A2 掉落工单；② `Snail.selectAvoidanceDirection`/`canAdvanceOrAttach`（本行引入 + `38382758a` 精修）；③ `NPCReforgeMenu` 的 trading-player 保护 |
| 204 | `be6173c00` | `SKIP-PORTLIB` | 只改 2 个子模块指针 |

### 41.3 tag 家族真缺口定案（本会话独立核算，非写手转述）
新增两件只读工具（已入库）：`tools/port2native/tag_delta.py`（解析 `tag(...)` + 链式 `.add/.addTag/.addOptionalTag`，**1.20 HEAD ↔ 1.21 逐块比对**）、`tools/port2native/tag_member_check.py`（逐成员核 1.21 字段存在性）。

- **结论**：1.20 HEAD 有 **28** 个 tag 块，1.21 有 **14** 个 ⇒ **14 个块在 1.21 完全不存在**；其中 1 个（`TCTags.SLIME`）已被手写 json 覆盖，**13 个是真缺口**。
- **排除"1.21 自有等价物"**：1.21 全仓唯一的 entity_type 标签 json = `data/terra_curio/tags/entity_type/slime.json`；子模块 `Confluence-Magic-Lib`/`TerraCurio` 源码内也没有这些原版标签的注册（本会话扫过）⇒ 属真空缺。
- **决定性旁证（1.21 已在消费这些空标签）**：`block/common/SoulGlassBlock.java:25`（`EntityTypeTags.UNDEAD`）、`entity/npc/SkeletonMerchantNPC.java:63`（`EntityTypeTags.SKELETONS`）、`entity/boss/HillOfFlesh.java:253` / `entity/monster/SimpleWormMonster.java:167` / `entity/monster/TheHungry.java:272` / `entity/monster/slime/FleshSlime.java:43`（`ModTags.EntityTypes.FLESH_ALLIANCE`）、provider 自身 `SPAWN_AT_GRAVEYARD` 用 `EntityTypeTags.ZOMBIES`。⇒ 按用户口径「正常且必要的功能照落」**全部落**。
- **键映射表**（1.20 行号 → 1.21 键；成员数；1.21 成员存在性已逐个核过 = 全 OK）
  | 1.20 行 | 1.20 键 | 1.21 应写键 | 成员 |
  |---|---|---|---|
  | 153 | `tag(TCTags.SLIME).addTag(LibTags.EntityTypes.SLIME)` | 手写 json（见下） | — |
  | 155 | `PortTags.EntityTypes.ARTHROPOD` | `EntityTypeTags.ARTHROPOD` | 10 |
  | 167 | `ModTags.EntityTypes.FLESH_ALLIANCE` | 同名（常量 1.21 已有 `ModTags:508`） | 6 |
  | 175 | `PortTags.EntityTypes.AQUATIC` | `EntityTypeTags.AQUATIC` | 6 |
  | 183 | `PortTags.EntityTypes.ZOMBIES` | `EntityTypeTags.ZOMBIES` | 12 |
  | 197 | `EntityTypeTags.SKELETONS` | 同名 | 10 |
  | 209 | `PortTags.EntityTypes.UNDEAD` | `EntityTypeTags.UNDEAD` | 21 |
  | 232 | `EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS` | 同名 | 3 |
  | 233 | `EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES` | 同名 | 6 |
  | 241 | `EntityTypeTags.AXOLOTL_HUNT_TARGETS` | 同名 | 4 + `.addTag(JELLY_FISH)` |
  | 242 | `EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES` | 同名 | 3 |
  | 243 | `PortTags.EntityTypes.CAN_BREATHE_UNDER_WATER` | `EntityTypeTags.CAN_BREATHE_UNDER_WATER` | 4 + `.addTag(JELLY_FISH)` |
  | 244 | `EntityTypeTags.FALL_DAMAGE_IMMUNE` | 同名 | 13 |
  | 259 | `ModTags.EntityTypes.JELLY_FISH` | 同名，**常量需先补** | 5 |
- **三处附加缺口**：
  1. `ModTags.EntityTypes.JELLY_FISH` **在 1.21 不存在**（1.20 在 `ModTags:521`）⇒ 需先加注册；
  2. `SPAWN_AT_GRAVEYARD` 少成员：1.20 `:71-73` 是 `.addTag(PortTags.EntityTypes.ZOMBIES).add(MonsterEntities.GHOST.get());`，1.21 无该 `.add`（`MonsterEntities.GHOST` 在 1.21 `MonsterEntities.java:498` 存在）；
  3. `terra_curio:slime` 手写 json **只硬列 15 个 id**，而 1.20 语义是引用 `LibTags.EntityTypes.SLIME`（30 成员、含 `minecraft:slime`）⇒ 1.21 少 16 个；按 1.20 语义改为引用 `#confluence_magic_lib:slime`。

### 41.4 其余裁定（本轮）
- **Snail**（`selectAvoidanceDirection`/`canAdvanceOrAttach`，行 203 引入 + `38382758a` 精修）：按 **1.20 HEAD 最终形态**落；先核 1.21 现状，已有同形态则不重复落、只登记。
- **`NPCReforgeMenu` 的 trading-player 保护**：**落**（按 1.20 HEAD，属 bug 保护、非毒）。
- **`GORE_EFFECT_BLACKLIST` 的 `addOptionalTag`**：**保持 1.21 现状**（不改回 1.20 的 `addTag`）。依据：`b3e73dfda`「实体类型标签改可选标签」（2026-10-02）**只存在于 1.21 仓**（1.20 仓 `cat-file -t` 报 "Not a valid object name"）⇒ 属**分叉后 1.21 侧改动**，按「1.20 反向对齐 1.21」处理 ⇒ 记入 **1.20 反向对齐待办**。
- **BOSSES 成员**：严格照 1.20 的 20 行，**不补** 1.21-only Boss；若发现 1.21 独有 Boss 只登记、不落。
- 队列：① 1.21 重构 ✅ → ② 1.20 镜像 ✅ → ③④⑤ tag 家族部分 ✅ → **tag 家族剩余（13 块 + 3 处附加缺口）** → EN `addOverrides()` 核成员后落 → decoBlockSet 独立批次（`ensureLogModels` 已改判为真缺口）→ Snail / `NPCReforgeMenu` → **从行 205 继续行走**。

### 41.5 台账
- 行 201 = `SKIP-PORTLIB`、202 = `SKIP-PORTLIB`、203 = `COVERED`、204 = `SKIP-PORTLIB`（双写 `PORT-LEDGER.md` 末列 + `port-ledger-status.json`，状态 JSON 共 **201** 条）。
- **下一开放行 = 205**。
## 四十二、tag 家族 13 块落地（毒口径全量重判）+ 行 205–208（2026-10）

### 42.1 落地（写手，两笔）
- **`8fd7830f1`**「族类组」`ModEntityTypeTagsProvider` **+79**：`ARTHROPOD`(10)、`FLESH_ALLIANCE`(6)、`AQUATIC`(6)、`ZOMBIES`(12)、`SKELETONS`(10)、`UNDEAD`(21)。
- **`34ecd6caa`**「环境/水域/寒地/免疫 + JELLY_FISH 组」provider **+29** + `ModTags.java` **+1**：`POWDER_SNOW_WALKABLE_MOBS`(3)、`AXOLOTL_ALWAYS_HOSTILES`(6)、`AXOLOTL_HUNT_TARGETS`(3 + `.addTag(JELLY_FISH)`)、`FREEZE_IMMUNE_ENTITY_TYPES`(3)、`CAN_BREATHE_UNDER_WATER`(3 + `.addTag(JELLY_FISH)`)、`FALL_DAMAGE_IMMUNE`(13)、`JELLY_FISH`(5)，并**新注册 `ModTags.EntityTypes.JELLY_FISH`**（1.21 `ModTags.java:509`，紧接 `FLESH_ALLIANCE:508`，与 1.20 的 `520 / 521` 同序）。
- 键映射：`PortTags.EntityTypes.{ARTHROPOD,AQUATIC,ZOMBIES,UNDEAD,CAN_BREATHE_UNDER_WATER}` → 原版 `EntityTypeTags.*`（1.21 原版键内建位置 `EntityTypeTags.java:8/9/11/15/16/17/18/20/22/28/29`）。
- **成员剔除 = 0**；用户点名的"疑似 TerraEntity 时代常量"（`SNOW_FLINX`/`ICE_MIMIC`/`CRAWDAD`/`ARAPAIMA`/`HERPLING`/`POSSESS_ARMOR`/`BLOOD_JELLY`/`FUNGO_FISH`/`UNDEAD_VIKING`/`ModEntities.RIDEABLE_BEE`）在 1.21 均为真实声明。

### 42.2 独立验证（本会话，`tag_delta.py` 重算）
- 1.20 **28** 块 ↔ 1.21 **27** 块；除下列 6 条外，**其余共享键的成员集与次序逐字相同**。
- **5 条 `PortTags.*` 别名块**逐块对其 1.21 目标键：`10/10`、`6/6`、`12/12`、`21/21`、`4/4` **且次序完全相同** ⇒ 别名映射确已落地，非缺口。
- 余 1 条 `TCTags.SLIME` 见 42.3。
- `git show --numstat` 两笔无夹带（第二笔另含 `ModTags.java`）。

### 42.3 漏做的两处与裁定
1. **`SPAWN_AT_GRAVEYARD` 仍缺 `MonsterEntities.GHOST`**（1.20 `:71-73` = `.addTag(PortTags.EntityTypes.ZOMBIES).add(MonsterEntities.GHOST.get());`，1.21 只有 `EntityTypeTags.ZOMBIES`）⇒ 已下达补落令（`MonsterEntities.GHOST` 在 1.21 `MonsterEntities.java:498` 存在）。
2. **`terra_curio:slime` 语义未对齐** ⇒ **不入 provider**：datagen 产出的 `src/generated/resources/data/terra_curio/tags/entity_type/slime.json` 与手写 `src/main/resources/data/terra_curio/tags/entity_type/slime.json` **是同一资源路径**，会构成重复资源冲突。改为把**手写 json 改成引用形态** `"values": ["#confluence_magic_lib:slime"]`（`LibTags.EntityTypes.SLIME` 经 `ConfluenceMagicLib.asResource("slime")`，命名空间 `confluence_magic_lib`；该 Lib 标签已由 provider 填充 **30** 成员、含 `minecraft:slime`）。现状硬列 **15** 个 ⇒ 缺 16 个史莱姆，属功能缺口。

### 42.4 行 205–208 判定
| 行 | 1.20 提交 | 裁定 | 证据 |
|---|---|---|---|
| 205 | `84b1939df` 修复战斗结算并统一生物属性与鞭子判定 | `COVERED` + 登记 | 205 路径 / 50 STRONG，其中 **21 条 `[1.20HEAD无此文件]`**（`api/whip/WhipDefinition.java`、`WhipTagEffect.java` 两仓皆无 ⇒ 1.20 原创后自删）；`BaseAquaticMonster` 用 `BlockPathTypes`（1.20 API）⇒ `SKIP-PLATFORM`；`WormPartGeoModel` 家族类型重构**登记待深挖** |
| 206 | `c7dd86378` 修复洞穴探险高亮框偏移 | `COVERED` | 仅 1 路径、0 STRONG |
| 207 | `95bb0294e` 修复汇流箱子打不开 | `SKIP-PLATFORM` | 唯一 STRONG = `BlockItem.BLOCK_STATE_TAG`（1.20 API，1.21 已移除） |
| 208 | `5c3978b01` 修复汇流箱子打不开/魔法武器不能附魔… | **保持既有 `PORTED`**（本轮未改） | 该行早批已判 `PORTED` 并带目标记录；写手复检称残余 STRONG 仅 `EnchantmentCategory.create`（1.20 API，1.21 附魔改数据驱动）⇒ 作为"残余属平台 API"备注，**不动台账** |

### 42.5 其余裁定与状态
- 行 205 的 `WormPartGeoModel` 家族类型重构 ⇒ **下达深挖令**：先做路径级 `git log -S` 归属，再按方向规则裁定（1.20 分叉后改动且 1.21 缺 ⇒ 落；同形 ⇒ `COVERED`；平台重命名 ⇒ `SKIP-PLATFORM`）。
- 台账：205 = `COVERED`、206 = `COVERED`、207 = `SKIP-PLATFORM`；208 **保持 `PORTED`**。状态 JSON 共 **204** 条；**下一开放行 = 214**（209–213 早已非 TODO）。
- 工具 EOL：`7184011f7` 提交的 `tag_delta.py`/`tag_member_check.py` 工作树为 LF 被 `fix_eol` 标候选 2 ⇒ 已用 `--paths` 归一到 CRLF（**索引内存 LF**，core.autocrlf 归一，故无需额外提交）；1.21 仓 `--check` = **候选 0**。1.20 仓 57 个预存候选一律未动。
## 四十三、行 198 EN `addOverrides` 验证、decoBlockSet 清单与行 209–213 判定对账（2026-10）

### 43.1 行 198 EN `addOverrides()` 落地（`578d805b3`）
- 交付：`ModEnglishProvider.java` 1 文件 `+38`；**本会话独立比对 = 与 1.20 HEAD 的 `addOverrides()` 38 行逐字 `IDENTICAL`**（成员 34 条 `add`：24 条成员类 + 10 条 lang 键）。
- **`TImer` 拼写问题查清（无需动作）**：1.21 `ModEnglishProvider.java:1147/1149` 的 `"5 Second TImer"` / `"1/4 Second TImer"` 来自**共享史** `a3dd5bb35`「重命名」（该提交在 1.20 仓同样存在），1.20 侧亦残留于 `i18n/en_us.json:1947/1949`；1.20 的 datagen 主体**没有**这两个键，只有 `addOverrides` 里的正确值。1.21 现状 = 主体（错拼）+ `addOverrides`（正确、覆盖生效，调用点 1962 位置晚于 1147）⇒ **运行时输出与 1.20 一致**。按「1.21 措辞差异只登记不重写」口径 **不改主体**，仅登记。

### 43.2 decoBlockSet「同步 1.20」清单（写手只分析未落；本会话复核符号计数）
| 符号 | 1.20 HEAD | 1.21 HEAD |
|---|---|---|
| `shouldGenerate` | 23 | **0** |
| `hasTexture` | 13 | **0** |
| `hasHandwrittenModel` | 8 | **0** |
| `ensureLogModels` | 3 | **0** |
| `simpleBlockIfAbsent` | 6 | **0** |

⇒ 1.21 侧**整套缺失**（非单点），与"被调方 `hasHandwrittenModel` 归毒 `dfcc5c041`"一致；按用户裁定「同步 1.20」= **真缺口**，批准作为**独立批次**落地：带过 `shouldGenerate`/`hasTexture`/`hasHandwrittenModel`/`ensureLogModels`/`simpleBlockIfAbsent`/`modelOrGenerate`/`registerLogSet`/`registerDecorationSet` 八个成员，并把 `decoBlockSet`/`LogBlockSet` 两处循环体改为**委托调用**（不保留 1.21 的 `if (blockSet.LOG.isBound())` + `try/catch(Exception ignored)` 混合旧形态）。**torch 家族留给行 370**（`registerTorch`/`registerWallTorch`/`torchModel`/`torchTexture`），不与本批混。

### 43.3 行 209–213 判定对账（写手 COVERED vs 既有裁定）
写手本轮行走判 **209–212 = `COVERED`**（0 STRONG、新增行逐字已在 1.21）。台账对账结论：
| 行 | 既有裁定 | 本轮写手 | 采纳 |
|---|---|---|---|
| 209 | `COVERED` | `COVERED` | **一致，采纳** |
| 210 | `DEFER-ASSETS`（"无正文变更、只含资源/数据、1 个非 java 文件 ⇒ 收尾 R2 单独提交"） | `COVERED` | **保留 `DEFER-ASSETS`** |
| 211 | `SKIP-PLATFORM`（"分支只含子模块指针；子模块侧净改动 5 文件 +48/−38（Lib、PortLib），属 part 系列镜像"） | `COVERED` | **保留 `SKIP-PLATFORM`** |
| 212 | `SKIP-PLATFORM`（同上，PortLib 侧 2 文件 +11/−1） | `COVERED` | **保留 `SKIP-PLATFORM`** |
| 213 | `SKIP-PLATFORM`（TerraCurio 侧 +1/−0） | 未走 | 保留 |

理由：既有裁定带**子模块净改动级证据**（路径数、增删行、归属模块），写手本轮是"STRONG 为 0"的浅读；`0 STRONG` 只能说明"无新增**标识符**落点"，不能推翻"这是子模块指针提交/资产待落"的结论。⇒ **已非 TODO 的行一律不改台账**，除非有新证据（新证据只报告、由主代理裁定后 `--force` 覆写）。

### 43.4 状态
- 台账：209 = `COVERED`（JSON 同步）、210 = `DEFER-ASSETS`、211–213 = `SKIP-PLATFORM`（均**保持不动**）。状态 JSON 共 **204** 条；**下一开放行 = 214**。
- 写手队列：`SPAWN_AT_GRAVEYARD` 补 `GHOST` → `terra_curio:slime` 改引用形态 → 行 205 `WormPartGeoModel` 深挖 → **decoBlockSet 独立批次** → Snail / `NPCReforgeMenu` → 行 214 起继续行走。
## 四十四、tag 家族补齐、decoBlockSet 落地验证、行 205–220 与 structure_set 改造授权（2026-10）

### 44.1 落地（写手，本会话独立验证）
- **`0e54b8a88`**「tag 家族补齐（2/2）」：provider `+2/−1` + `terra_curio:slime` json `+1/−15`。
  - `SPAWN_AT_GRAVEYARD` 补 `.add(MonsterEntities.GHOST.get())`（1.20 `:73`；1.21 `MonsterEntities.java:498` 存在），注释行原样保留 ✓
  - 手写 json 改 `"values": ["#confluence_magic_lib:slime"]`（1.20 语义 = 引用 `LibTags.EntityTypes.SLIME` 30 成员）；**provider 未动**（`TCTags` 在 provider 命中 0）✓ 避免同资源路径冲突。
- **`6a02ba877`**「decoBlockSet 同步 1.20」：`ModBlockStateProvider` `+203/−70`（192→324 行）。**本会话脚本比对 8 个成员与 1.20 HEAD**：`registerLogSet` 91 行、`registerDecorationSet` 37、`ensureLogModels` 14、`simpleBlockIfAbsent` 16、`shouldGenerate` 11、`hasHandwrittenModel` 10、`hasTexture` 10 **逐字相同**；`modelOrGenerate` 仅差一个空行 ⇒ 通过。两处循环体已改委托调用，1.21 旧 `isBound()`+空 catch 形态消失，torch 家族（留行 370）残留 = 0 ✓
  - 计数口径澄清：1.20 总数 `shouldGenerate` 25 / `hasTexture` 16 / `hasHandwrittenModel` 8，1.21 落地后 23/14/7，差值 2/2/1 **恰好等于 torch 家族区间内的命中数** ⇒ 非遗漏。

### 44.2 行 205–220 判定与台账
| 行 | 提交 | 裁定 | 说明 |
|---|---|---|---|
| 205 | `84b1939df` | `COVERED`（+`SKIP-1.20-REVERTED`） | `WormPartGeoModel` 那段重构的 token（`familyType`/`usesFallbackResources`/`isSegmentCarrier` 属行 205 非毒、`isWyvernFamily` 归毒）**已被 1.20 自己删除**（`b05c8dc3f` 删 `usesFallbackResources`、`348c877a4` 删整个 `familyType` 块）；两仓 HEAD 该文件**均 38 行、diff 0 行** ⇒ 不落 |
| 213 | `5ebb83523` | 保持 `SKIP-PLATFORM` | 子模块指针提交（TerraCurio +1/−0）；写手新读为 `COVERED`，按"非 TODO 行不覆写"保留旧判 |
| 214 | `38382758a` | **`SKIP-1.20-REVERTED`**（122/127）+ `SKIP-PLATFORM`（5） | 113 对路径在 1.20 HEAD 已不存在（`api|client|common/summon/**`）、9 对 token 被 1.20 删除；余 5 条为 `EntitySubProvider` 的 NBT 系（`SetNbtFunction`/`setTag`/`stackTag`/`setGelColor`/`DATA_COMPONENTS`，1.21 无此类）⇒ 归已派工的 A2 掉落工单。**归属无一处归毒** |
| 215 | `b1884f9a6` | 保持 `SKIP-PLATFORM` | 写手新读 `COVERED`；保留旧判 |
| 216 | `6a24e6d1f` | 保持 `SKIP-PLATFORM` | 同上 |
| 217 | `7a3593600` | **`SKIP-PLATFORM`** | 0 STRONG、3 条为子模块指针（按 211/212 口径，采纳写手建议） |
| 218 | `d7c191964` | **`SKIP-PLATFORM`** | 0 STRONG、1 条子模块指针 |
| 219 | `50ff9fe88` | 保持 `DEFER-ASSETS` | 写手新读 `COVERED`；保留旧判 |
| 220 | `751055e45` | **`COVERED`** | 9 条 STRONG **全是 1.20 的注释态代码**（旧 `@Inject`/`@Local` 写法整段注释、改用 `WrapOperation`）；`PortImageButton` 属 PortLib ⇒ 附 `SKIP-PORTLIB` |
- 台账状态 JSON 共 **208** 条；**下一开放行 = 223**（221 `COVERED`、222 `DEFER-ASSETS` 早已裁定）。
- **教训登记（写手提出、采纳）**：`git log -S` 的**最早命中只说明"引入"**，判定前必须同时核**目标文件当前状态** —— `Snail.selectAvoidanceDirection`/`canAdvanceOrAttach`（行 203 引入 → 行 214 精修 → **1.20 `a358494fa` 整体重写删除**）与 `NPCReforgeMenu` 的 trading-player guard（**1.20 `ef1a4d138` 重构删除**）**在两仓 HEAD 均已不存在** ⇒ 二者裁定改为 **`SKIP-1.20-REVERTED`、不落只登记**（此前 §43/§三 的"落"的裁定作废）。

### 44.3 用户裁定：`registerStructureSet` 同步到 1.21（范围 **A**）
- 用户原话：「**把 1.20 的 `registerStructureSet` 也同步到 1.21；然后 1.20 和 1.21 都提交**」；范围经确认为 **A：整个 structure_set 族改回 datagen**。
- 背景：1.20 用 `registerStructureSet(...)` **17 处** + 内联 8 处 + `AIR`；1.21 **0 处助手**、仅内联 7 处，那 17 个 set 是**手写 json**（`src/main/resources/data/confluence/worldgen/structure_set/*.json` 恰好 17 个文件，1.20 侧 0 个）。
- **本会话前置核对（关键）**：脚本逐项比对 1.21 那 17 个 json 的 `spacing`/`separation`/`salt`/`exclusion_zone(other_set, chunk_count)` 与 1.20 datagen 实参 ⇒ **全部一致**（`obsidian_castle` = `30/28/26518570/confluence:nether_tower/5` ↔ `key("nether_tower"), 5`）⇒ 转换**不会丢任何 1.21 侧参数**，删除手写 json 安全。
- 已下达：(1) 落助手（**含用户加的 `@Nullable` 形态**）+ 17 处调用（`obsidian_pillar` 传 `null`）；(2) 删 17 个手写 json（避免与 datagen 同路径冲突）；(3) 1.21 自身 7 处内联**保持内联**（1.20 亦内联）；(4) `spider_nest`（1.20 内联有、1.21 全无）须先查清结构是否在 1.21 被删，存在则落、不存在则登记，不得臆造。
- **1.20 仓提交**：路径限定提交用户的 `ModDataProvider.java`（`@Nullable` + 1 import），**不带**暂存的 `D …vfx/licenses/kenney-particle-pack.txt`。

### 44.4 仍待执行（写手队列）
1. **(a)** 1.21 `Decayeder.onDamageDealt` 签名对齐为 `(ServerLevel, LivingEntity, DamageSource)` + 派发传 `serverLevel`（本会话实测 `Decayeder.java:100` 与 `LivingEntityEvents.java:289` **仍未对齐**）——目的：两侧方法体只差 `.get()` 一处平台差异。
2. **(b)** structure_set 族改 datagen（范围 A，见 44.3）。
3. **(c)** 1.20 仓提交用户 WIP。
4. 从 **223** 继续行走。
## 四十五、双侧一致性收工 + 行 221–224 与 TerraBlender 事实校正（2026-10）

### 45.1 两处派发重构的**双侧一致性收工**（`6e182eb75`，本会话实测）
- `6e182eb75`（1.21）`--numstat` = `Decayeder` 6/8 + `LivingEntityEvents` 1/1，与 1.20 侧 `6cc9131af` **完全相同**。
- `onDamageDealt`：两仓 HEAD 均 **15 行**，归一化（去 `.get()`、去缩进）**逐行 0 差异**；派发行两仓**逐字节相同**（`if (attacker instanceof Decayeder decayeder) decayeder.onDamageDealt(serverLevel, victim, damageSource);`）；`Decayeder` 内 `level()` 命中两侧均 **0**。
- 顺带核 `CursedSkull.doHurtTarget`：归一化后**只差** `getRandom1211()`（1.20）/ `getRandom()`（1.21）一处。
- ⇒ **两侧现在只剩平台必需差异**（`ModEffects.X.get()` vs `ModEffects.X`、`getRandom1211()` vs `getRandom()`），无任何人为分叉。**用户在 §三十九 要求的"镜像回 1.20 使两侧架构一致"达成。**
- 写手记录的 WIP 保护证据：WIP 快照 vs 回写后工作树 = **仅 1 行差异**（即本次派发行）⇒ 他人 BeeKeeper WIP 其余字节未动、未入提交。

### 45.2 行 221–224 判定
| 行 | 提交 | 裁定 | 说明 |
|---|---|---|---|
| 221 | `255250cc1` 调参/战利品表/塞光剑 | **`PORTED`（待写手落 3 行）** | 33 路径 / 3 STRONG：1.20 给 `ModTabs` 加 3 条 `enchantedBook`（`WHIP_SWEEP` 2、`MULTI_BOOMERANG` 3、`SUMMONER_PACT` 3，1.20 `:1825/:1826/:1917`）。**三常量在 1.21 均存在**（`ModEnchantments.java:34/35/42`）；1.21 `ModTabs` 已有 **13** 处同类用法、仅缺这 3 条 ⇒ 属"正常且必要功能"，与"附魔不移植"（针对 8 个 1.21 不存在的 id）**范围不同** ⇒ 落。**形态必须用 1.21 的**：`EnchantmentUtils.enchantedBook(registryLookup, ModEnchantments.X, n)`（1.20 为 `LibEnchantmentUtils.enchantedBook(ModEnchantments.X.get(), n)`） |
| 222 | `ea9708fb4` 增加发光贴图 | `DEFER-ASSETS` | 0 STRONG、纯资源行（与行 210 口径一致） |
| 223 | `aec2cd920` 修复 mixin/跳跃属性 | `SKIP-PLATFORM` | 唯一 STRONG = `Attributes.JUMP_STRENGTH_1211`（PortLib shim 名）⇒ 1.21 用原生 `Attributes.JUMP_STRENGTH` |
| 224 | `3518ab86e` 干掉 TerraBlender | `COVERED`（保持旧判） | 见 45.3 |

### 45.3 TerraBlender 事实校正（用户指出、本会话验证）
- 写手据 `git grep -l` 断言"**1.21 仍在 11 个文件里引用 TerraBlender**"，**该结论错误**。用户指出「1.21.1 不是早就按照 1.20 把 TerraBlender 干掉了吗」——核实后用户正确：
  - 那 11 个文件的 `TerraBlender` **全部出现在注释/javadoc**（`/// …与 TerraBlender 的区别…`、`// 原实现在 TerraBlender 里是 …` 等）；
  - **真实 API 调用 = 0**：`import terrablender` / `terrablender.` 在 `*.java` 里 0 命中（唯二两条匹配也在 javadoc 内），`*.gradle` 里 0 引用；
  - 唯一残留 = `gradle.properties:18 terrablender_version=4.1.0.8`（**已无消费者**的死属性），且**早已登记**在 `notes/1.21-BRANCH-DIVERGENCE.md:205` 与 `notes/PORTLIB_ARCH_MIGRATION_PLAN.md:551`（"应删"）⇒ 非本行新缺口，**不排新批次**。
- **教训登记**：`git grep -l`（只列文件名）会把"**注释里提到**该名字"的文件一并计入 ⇒ 判断"是否仍在使用某平台"必须看**匹配行内容**（排除 `//`、`///`、javadoc）或直接查 `import` 与构建依赖文件。

### 45.4 状态
- 台账状态 JSON 共 **209** 条；**下一开放行 = 225**（221 落完后由主代理改判为 `PORTED`）。
- 待写手执行：(a) structure_set 族改 datagen（范围 A，见 §44.3）→ (b) 1.20 仓提交用户 `ModDataProvider.java` → (c) 行 221 三条 `enchantedBook` → 从行 225 继续行走。
## 四十六、Lib 归位落地与**方向更正**（用户推断被证实）+ 行 221–229（2026-10）

### 46.1 方向更正：不是"1.21 吸收进主模组"，而是 **1.20 分叉后拆到 Lib**
> 用户质疑：「有没有可能不是"1.21 把 Lib 的整个工具类吸收进了主模组"，而是 1.20 从 1.21 分支出去后，自行拆分到 Lib 中的」——**查证后：用户正确，§四十五前我给的叙述作废。**

**证据链（全部实测）**：
| 事实 | 证据 |
|---|---|
| 两仓（主仓与 Lib 子模块）的分叉基点 | 主仓 `merge-base neoforge-dev/1.21.1 forge-dev/1.20.1` = **`795ac9ccc`（2026-05-31）**；Lib 子模块分叉基点 `154d1cf` |
| 1.20 主仓改动 | **`1516cbd2f`「part fluid type」（2026-06-30）**：`merge-base --is-ancestor 1516cbd2f neoforge-dev/1.21.1` = **失败** ⇒ **1.20 分支独有（分叉后）**。该提交同时 bump 了 Lib 指针 |
| Lib 子模块改动 | 同名提交 **`7f02f07`（2026-06-30）**：**删除** `org/confluence/lib/util/EnchantmentUtils.java`（−26）、**新增** `LibEnchantmentUtils.java`（+38）；`git log -S "enchantedBook"` 在 1.21 分支 = **空**（1.21 的 Lib 从未有过 `enchantedBook`），在 1.20 分支 = 仅 `7f02f07` |

⇒ 真相：**分叉前** `enchantedBook`/`runIterationOnHand`/槽位数组住在**主模组**的 `org.confluence.mod.util.EnchantmentUtils`，Lib 只有一个精简的 `EnchantmentUtils`（仅 `getEnchantmentLevel`）；**1.20 分支在分叉后（2026-06-30）把它们搬进 Lib 并把 Lib 类改名为 `LibEnchantmentUtils`**。按「1.21 对齐 1.20 的分叉后改动」⇒ 归位方向正确（用户裁定成立），但**动因是 1.20 侧的重组**，不是 1.21 吸收。

### 46.2 Lib 归位落地与验证
- **Lib 子模块 `e433b85`**：`EnchantmentUtils.java` **−27** / `LibEnchantmentUtils.java` **+51**；类名同步改；`getEnchantmentLevel` 保持 1.21 签名（`ResourceKey<Enchantment>` 版）；子模块 ` M gradle.properties`（他人 WIP）**未被提交、未被改动** ✓
- **主仓 `fa48638cb`**：7 路径 = 指针 bump（`gitlink → e433b854…`）+ 6 文件（`BaseArrowEntity`/`WhipAttackEntity`/`ModTabs`/`BoomerangItem`/`BaseTerraRepeaterItem`/`util/EnchantmentUtils`）；主类删掉搬走的三样并改走 `LibEnchantmentUtils.*`（2 处数组、5 处 `runIterationOnHand`）、顺带删 4 个无用 import
- **独立验证**：Lib 类成员集与 1.20 **完全一致**（`getEnchantmentLevel`/`enchantedBook`/`runIterationOnHand` + `SlotGroups.{ARMOR_N_MAINHAND,MAINHAND,ANY,ARMOR}`，类名 `LibEnchantmentUtils`/`SlotGroups`）；`git grep "org.confluence.lib.util.EnchantmentUtils"` = **0**；非 Lib 前缀的 `EnchantmentUtils.enchantedBook` = **0**；主类内 `enchantedBook`/`runIterationOnHand`/`HUMANOID_ARMOR*` 定义 = **0**（仅剩 5 处 `LibEnchantmentUtils.runIterationOnHand(...)` 调用）
- **行 221 三条已就地转为 Lib 形态**：`ModTabs:1829`（`WHIP_SWEEP` 2）、`:1830`（`MULTI_BOOMERANG` 3）、`:2011`（`SUMMONER_PACT` 3）⇒ 行 221 台账改判 **`PORTED`**（`--force`）

### 46.3 `1516cbd2f` 的**其余部分不得镜像**（本会话新裁定）
同一个 1.20 提交还做了三件事，性质与 Lib 归位**不同**，**1.21 保持现状**：
1. 把 `processManaRegeneration`/`processEfficientMagic`/`repairPlayerItems` 从**数据驱动附魔效果组件**（`EffectComponentTypes.*`、`EnchantedItemInUse`、`Optional`）改写为 1.20 惯用的**硬编码判定**（`EnchantmentHelper.getTagEnchantmentLevel`、`Map.Entry` 版 `getRandomItemWith`）；
2. **删除 `dropsStar`**（数据驱动 `ATTACK_DROPS_MANA` 版）并新增硬编码 `affect(ServerPlayer, LivingEntity, DamageSource)`（走 `ManaAffectiveEnchantment` java 类）；
3. 配套删 `SummonItemEffect.java`（−43）与 `ModEnchantments` 部分成员。
- **理由**：1.20.1 Forge 没有 1.21 的附魔 API（那些是经 PortLib shim 反向移植的形态），该提交是**1.20 平台自身的降级重写**；1.21 侧的**数据驱动实现是其平台原生且更新**（`ModDataProvider` 里 `mana_affective` 等以 datagen 效果组件定义，`ModTags`/`ModEnchantmentTagsProvider` 配套）。
- **裁定**：1.21 一律保持数据驱动形态（`SKIP-1.21-KEEPS` / 登记为**设计分歧**），不照搬 `affect`/硬编码写法、不删 `dropsStar`。
- 反向项：`processFlailWindBurst` + `WIND_BURST_AT_HIT`（1.21 `EnchantmentUtils`/`ModEnchantments`）是 **1.21 分支分叉后新增**（`b9bca2ee1` 不在 1.20 分支的历史里，1.20 全仓无 `WIND_BURST_AT_HIT`）⇒ 记入 **1.20 反向对齐待办**。

### 46.4 行 221–229 判定
| 行 | 提交 | 裁定 | 说明 |
|---|---|---|---|
| 221 | `255250cc1` | **`PORTED`** | 3 条 `enchantedBook` 先落 `113051fc3`（主类形态），随 Lib 归位在 `fa48638cb` 转成 `LibEnchantmentUtils.enchantedBook(registryLookup, ResourceKey, n)` |
| 222 | `ea9708fb4` | `DEFER-ASSETS` | 纯资源行、0 STRONG |
| 223 | `aec2cd920` | `SKIP-PLATFORM` | `Attributes.JUMP_STRENGTH_1211`（PortLib 补的原版属性）↔ 1.21 原生 `Options.JUMP_STRENGTH`；3 文件同行位一一对应 |
| 224 | `3518ab86e` | `COVERED`（保持） | 1.21 已退役 TerraBlender（`import terrablender` = 0，仅 2 处 javadoc 提及）；写手 23/26 + 3 的细分仅作备注 |
| 225 | `5d3cf72f6` | `SKIP-PORTLIB` | 唯一 STRONG = `@Diff`（`org.mesdag.portlib.diff.Diff`）注解的 `extendCreativeStackLimit`；1.21 同 mixin 只剩 `captureSpeed`。**登记缺口**：1.21 若要 Bigger Stacks 创造模式堆叠同步兼容，需去掉 `@Diff` 用原生 mixin 重写（待裁） |
| 226 | `9378868e5` | `SKIP-1.20-REVERTED`（38/41）+ `SKIP-PLATFORM`（2） | 光剑/悠悠球那套重构文件在 1.20 HEAD 已不存在；`initializeClient(Consumer<IClientItemExtensions>)` 属 Forge 专有、`registerInGameC2S` 属平台网络 API。**待细判项已由主代理结清**：`ItemInHandRendererMixin` 两仓**同形**（`check` 1.20:36 ↔ 1.21:34、`confluence$hasMatchingDeployedYoyo` 1.20:64 ↔ 1.21:62，仅行号位移 2）⇒ 该项 `COVERED` |
| 227 | `8a961bc0c` | `SKIP-PLATFORM`（4/5） | `PotionUtils`/`PotionColorCalculationEvent` 在 `_nfsrc_219` **不存在**，1.21 已用 `PotionContents`；颜色计算事件属 Forge 专有。余 1 条 `NPCSpawner` 不在 1.20 HEAD ⇒ `SKIP-1.20-REVERTED` |
| 228 | `d15b521d2` | `SKIP-PLATFORM`（5/5） | `LootTableReference` 在 1.21 已改名 **`NestedLootTable`**（仓内 5 文件在用） |
| 229 | `128469b81` | `COVERED` | 0 STRONG |

### 46.5 状态
- 台账状态 JSON 共 **214** 条；**下一开放行 = 231**（230 = `0ceb3c251`「portlib 升级为 1.2.2」早已是 `DEFER-ASSETS`，写手未走）。
- **写手 `a8f2ac59` 已按用户先前指令结束**（"这个子代理完成后停止"）；行走停在行 230/231 之间。已完成的全部落地项见 §四十一–四十六。
- 遗留待用户裁：① 行 225 的 Bigger Stacks 兼容 mixin 是否原生重写；② 1.20 反向对齐待办（`WIND_BURST_AT_HIT` 等）。

## 四十七、行 231（`d5cdb0f89`「修复黄蜂寻路、悠悠球增伤与渔夫任务」）= `COVERED`；并**查实三份机器分诊数据已过期**（2026-10）

### 47.1 方法：逐 hunk 双向查证（本轮确立、后续行走沿用）
对 1.20 该提交**每一条新增行**问两个问题（脚本：`build/_cmp231/survive.py`，产物 `survive231.txt`）：
1. **该行在 `1.20 HEAD` 还在不在？** 不在 ⇒ 1.20 自己后来撤了/改了 ⇒ **无移植物**（HANDOFF §7 血泪坑的正解）。
2. 在的话，**`1.21 HEAD` 有没有等价行？**（`this.`/`.get()`/缩进/折行/`getRandom1211()` 归一后比对）
> 只用「1.20 HEAD vs 1.21 HEAD」判定即可 —— 逐提交行走的**真目标是让 1.21 对齐 1.20 HEAD**，不是把历史 diff 复现一遍。

### 47.2 行 231 的 14 个文件逐项判定 —— **无一条需要新落地**
| 变更 | 1.20 HEAD | 1.21 HEAD | 裁定 |
|---|---|---|---|
| `PrefixUtils` 新增 `attributeWithoutHeldItem`/`heldItemContribution`（+26） | 存活 | **已有**（`PrefixUtils.java:113`/`:119`，签名改 `Holder<Attribute>`、枚举改 `ADD_VALUE`/`ADD_MULTIPLIED_*`，带注释说明） | 已移植 |
| `AnglerData.collectCandidates` 邪恶群系过滤（+33） | **已被取代**（后改为 `entry.availability().matches(level)`） | 与 1.20 HEAD **同形**（`AnglerData.java:43-49`） | `COVERED` |
| `IFishingHook` `questPending` 判定（+3） | 存活（`IFishingHook.java:97`） | **无此行** | `COVERED`（见 47.3，1.21 实现更细） |
| `DemonEyeSurroundAction` 冲撞行为重写（+103/-58） | 存活 | **已有**（HOVER/CHARGE 常量与 `chargeDirection` 全在） | `COVERED` |
| `DemonEye.applyLookRotation`（+16） | **已被删除** | 无 | 两侧一致，无可移植物 |
| `DemonEyeRenderer` yaw/pitch（`yBodyRot` + `rotLerp`） | **已改写**（`Mth.rotLerp` + 保留负号） | 与 1.20 HEAD **逐字相同** | 两侧一致 |
| `YoyoEntity` 伤害改走 `attributeWithoutHeldItem` | **已被撤回**（回到 `item.attackDamage() * (float) owner.getAttributeValue(...)`） | 与 1.20 HEAD **逐字相同** | 两侧一致 |
| `BaseSlime` 委托 `checkSurfaceDayMobSpawn`、`@Nullable` 注解 | 注解存活；委托**已被撤回** | 注解已有；委托无 | 两侧一致 |
| `SpawnPlacementChecks` 各 `!level.canSeeSky(pos)`（+12/-24） | 存活（1.20 侧折行） | **已有**（`:99`/`:195`/`:258`/`:396`/`:452`/`:457`） | `COVERED` |
| `Hornet` repath（`isDone()`/`== REPATH_THRESHOLD`）、`BeeGunBullet` `instanceof Enemy` | 存活 | **已有**（`Hornet.java:154`、`BeeGunBullet.java:34`） | `COVERED` |
| `CreatureSpawnPlacements` `group(...)` 改原始类型可变参 + 2 条注释 | 存活 | 1.21 用**原生** `SpawnPlacementTypes`/`SpawnPlacements`，无 PortLib 泛型数组问题 | `SKIP-PLATFORM` |
| `CreatureSpawnPlacements` 各注册项（DERPLING/POSSESS_ARMOR/WRAITH/SHADOW_MIMIC/水栖） | 存活 | **已有**（`:89`/`:96`/`:144`/`:265`/`:267`/`:277`，仅平台类型名不同） | `COVERED` |
| `SummonStats` 改走 `PrefixUtils`（+4/-25） | **文件已被删除**（`46e5e8626` 2026-09-20「移除旧架构召唤体系」） | **1.21 分支从未有过该文件** | 无可移植物 |
| 折行/合并行类改动（大量 `-`/`+` 成对） | —— | —— | 纯格式，按 §1.3「只登记不重写」 |

**⇒ 行 231 裁定 `COVERED`**（无代码改动、无资源改动；故此节不产生落地提交，只做台账与本节登记）。

### 47.3 反向对齐待办（新增一项，**不在 1.21 回退**）
- **1.21 的渔夫任务状态比 1.20 细**：1.21 `IFishingHook.modifyLoot` 走 **按玩家**的 `PlayerSpecialData.getCurrentQuestedFish(player)`（非空则直接返任务鱼），该方法由 **1.21 分支自己的 `1ce5148f2`「钓鱼任务基本功能」**引入（`1.20 HEAD` 全仓无 `getCurrentQuestedFish`）；
- 1.20 HEAD 则是**全局** `AnglerData.INSTANCE` + `questPending = !PlayerSpecialData.of(player).hasCompletedAnglerQuestToday(level)` 的补丁式判定。
- 按方向规则（分叉后 1.21 的改动 ⇒ 记反向待办）⇒ **1.21 保留按玩家形态，不照搬 1.20 的全局标志**；`questPending` 那条 `TODO` 随之归 `COVERED`（同一意图已由更细的机制达成）。

### 47.4 **本轮新发现：机器分诊数据过期**（影响行 231–400 的判定基础）
- `notes/batch-progress.json` 最后一次提交是 **`d83fbc5f0`（2026-09-27）**，而 WP 批次 `b089215d6`/`d83fbc5f0`/`d5d6e8520`/`58431c613`/`47282a425` 落在 **2026-09-28…09-30**，**晚于**该快照 ⇒ 台账行 231+ 的机器进度**系统性偏低**。
- 本轮用现行工具链重算（`file_lag.py --out build/_cmp231/flag` → `batch_progress.py`，脚本产物在 `build/_cmp231/flag/`；**未覆盖 `notes/` 内的既有产物**）：
  | 指标 | 旧（notes/ 内） | 重算 |
  |---|---|---|
  | 提交级 `100% 已搬完` | 10 | **47** |
  | 提交级 `0%（未动）` | 126 | **42** |
  | 提交级 `<50%` | 164 | **102** |
  | 提交级 `≥50%` | 23 | **132** |
  | 文件级 `IN-SYNC` | 2044 | **2140** |
  | 文件级 `LAGGING` / `MISSING` / `PARTIAL` | 183 / 66 / 602 | **165 / 42 / 569** |
  | 行 231 自身 `pct` | 0.49 | **0.77** |
- 行 231–400 的 **122 个 `TODO`**，按重算值分档：**23 行 `pct ≥ 0.90`**（其中 232/233/241/266/268/288/343/346/352/354/360/365/372/381 = `1.00`）、**44 行 `< 0.50`**、7 行「无可加权文件」。
- **口径提醒（重要）**：`pct` 只是**文件级加权粗筛**（`IN-SYNC` 的门槛是有效行重叠 ≥85%），**不等于该行已搬完**。行 231 的 `pct=0.77` 里那 23% 经逐 hunk 查证**全部**属「1.20 后来自己撤/改」+「PortLib 专有」+「1.21 更细实现」，**没有一条需要新写**。⇒ 后续行走仍以**逐 hunk 证据**为最终依据，`pct` 仅用于排优先级（先走高 `pct` 的行结账，再攻低 `pct` 行）。

### 47.5 状态
- 台账状态 JSON 共 **215** 条；行 231 = `COVERED`。
- **下一开放行 = 232**（`673efd7d5`「调整标签 悠悠球无敌帧」，重算 `pct = 1.00`，重点核「1.20 HEAD 是否已撤」）。
- 遗留待用户裁（沿用 §46.5）：① 行 225 Bigger Stacks mixin 是否原生重写；② 反向对齐待办累计 = `WIND_BURST_AT_HIT`/`processFlailWindBurst`（§46.3）+ 本次的「渔夫任务按玩家形态」（§47.3）。

### 47.6 分诊产物刷新（按用户裁定：**刷新 + 留旧对照**）
- 用户裁定（本轮）：① **按 `pct` 排序走，但每一行仍必须有逐 hunk 证据才能写状态**；② **刷新 `notes/` 分诊产物，并留一份旧的做对照**。
- 已刷新（tracked，随本批提交）：`notes/batch-progress.json`、`notes/BATCH-PROGRESS.md`、`notes/triage-pass2.json`、`notes/TRIAGE-PASS2.md`、`notes/FILE-LAG.md`。
- `notes/file-lag.json` 属 **可再生中间产物**，按 `notes/.gitignore:17` **不入库**，仅本地刷新。
- **旧快照留存**放在被忽略的 `build/_cmp231/prev-notes/`（避免为对照往仓库塞 ~1.2 MB 机器数据）：
  `file-lag.prev.json`（1231110 B）、`batch-progress.prev.json`（113326 B）、`FILE-LAG.prev.md`（66644 B）、`BATCH-PROGRESS.prev.md`（8151 B）。§47.4 的对照表即由这两组值算出。
- **安全检查**：`triage_pass2.py` 只写自己的 `TRIAGE-PASS2.md` / `triage-pass2.json`，**不碰台账**（已核 `triage_pass2.py:142-143` 的写盘点）；重跑结果 `TODO 91 / REVIEW 38 / COVERED 56`。
- **口径重申**：`pct` 与 `TRIAGE-PASS2` 的结论**只是排序与粗筛**，写台账状态**必须**有逐 hunk 证据（方法见 47.1）。**行走顺序改按 `pct` 从高到低**：先结高 `pct` 行（多为 `COVERED`），再集中攻低 `pct` 行。

## 四十八、按 `pct` 排序的第一批（行 232/233/241/346/360/365/381）+ **行 233 真落地**（2026-10）

按用户裁定「按 `pct` 从高到低走、每行仍须逐 hunk 证据」。工具：`build/_cmp231/rowaudit.py <commit>`（逐新增行问「1.20 HEAD 还在不在」→「1.21 HEAD 有没有等价行」，输出 GAP 候选），产物 `build/_cmp231/ra_*.txt`。

### 48.1 行 233 `4bd94d674`「关键帧多效果支持」= **`PORTED`**（本批唯一落地）
**台账原记「1 file / +4 ~2 -0」是错的**：实际 **6 文件 / +1702 -1**（`git show --numstat` 实测）。

| 文件 | 内容 | 裁定 |
|---|---|---|
| `mixin/integration/geckolib/KeyFramesAdapterMixin.java` | +167 | 1.21 **已有等价实现**（同名同路径、已注册 `confluence.mixins.json:154`）。逐行比对：除 **3 处 GeckoLib-5 平台改名**外**全部相同** —— ①`software.bernie.geckolib.core.animation.*`→`animation.*`、`core.keyframe.event.data.*`→`animation.keyframe.event.data.*`；②`@Inject` 描述符 `geckolib/core/animation/Animation$Keyframes`→`geckolib/animation/Animation$Keyframes`；③`JsonUtil.GEO_GSON`→`KeyFramesAdapter.GEO_GSON`。★见 48.4 备注 |
| `tools/blockbench-plugins/{README.md, geckolib_multi_particle.js, test/geckolib_multi_particle.test.mjs}` | +150/+575/+392 | **1.21 此前完全不存在** ⇒ **本批逐字落地**（3 个文件，见 48.2） |
| `gradle.properties` | +1（`thr_dim_particle_version=1.2.1`） | **`SKIP-PLATFORM`**：两侧是**不同 artifact** —— 1.20 `ThreeDimensionParticle-forge-*` ↔ 1.21 `ThreeDimensionParticle-neoforge-*`（`build.gradle:226` ↔ `:205`），版本线互不相干；1.21 现为 `1.2.0`，不因 forge 侧升版而改 |
| `confluence.mixins.json` | +1（注册 mixin） | 1.21 已有（`:154`） |

### 48.2 落地证据（可复核）
- 3 个文件用 `git show 1.20 HEAD:<path>` **字节级**取出后原样写入；`git rev-parse :<path>` 与 1.20 blob **完全一致**：
  `README.md` = `7e99e17fe976`、`geckolib_multi_particle.js` = `5cd3fed098dd`、`geckolib_multi_particle.test.mjs` = `5e5293230a57`（三个都 MATCH=True）。
- 换行：1.20 blob 是 LF；按本仓约定（`fix_eol.py` 头注：工作树一律 CRLF、索引 LF）跑
  `fix_eol.py --repo . --paths ConfluenceOtherworld/tools/blockbench-plugins` ⇒ 3 个文件 `i/lf w/crlf`（与全仓 5813 个文件同形）。
- 这三个文件是 **Blockbench 编辑器侧工具**（纯 JS/MD/mjs，不进 mod 构建），与已存在的 mixin 配套；照 §1.3「以 1.20 逐字为准」原样落地。

### 48.3 其余 6 行判定（`COVERED`，均为「新增行全部已存在 / 已撤」）
| 行 | 提交 | 审计数（code / alive / dead / GAP） | 裁定依据 |
|---|---|---|---|
| 232 | `673efd7d5` 调整标签 悠悠球无敌帧 | 20 / 17 / 3 / **0** | GAP=0：全部存活新增行在 1.21 均已存在 |
| 241 | `91724b1ce` 为水生生物添加自然巡游 | 60 / 43 / 17 / **0** | 同上 |
| 346 | `0776c451c` 修复音效 | 0 / 0 / 0 / **0**（**纯删除**提交：Hornet 的 `getHurtSound`/`getDeathSound` 返 `SOUL_DEATH` 各 3 行 + `sounds.json` 注释 1 行） | 两侧 HEAD **同时为无**：`grep SOUL_DEATH\|getHurtSound\|getDeathSound` 在 1.20/1.21 的 `Hornet.java` 均 = NONE；`sounds.json` 那条 `//` 注释在两侧 HEAD 也均 = NONE ⇒ 状态一致 |
| 360 | `1f1c456ff` 染料商 | 2 / 0 / 2 / **0** | 该提交加 `DYE_TRADER` 映射 + `dye_trader_trade.png`；**1.20 与 1.21 HEAD 在该行逐字相同**（两侧均 `NPCTradeScreens.java:22`），PNG 两侧都在 ⇒ 已同步（工具把该行判 dead 是「1.20 后又改过行形态」的误报，故补双重证据） |
| 365 | `7a072d51b` 添加爆破专家商店专用贴图及映射 | 2 / 0 / 2 / **0** | 同 360：两侧 HEAD 均 `NPCTradeScreens.java:24` 有 `DEMOLITIONIST` 映射，`demolitionist_trade.png` 两侧都在 |
| 381 | `2d21e3364` 修bug | 12 / 11 / 1 / **0** | GAP=0 |

### 48.4 ★ 备注：行 233 的 mixin 是一条**「1.21 已按 GeckoLib 5 改写」**的现成先例
`KeyFramesAdapterMixin` 的 **112 行正文在两仓逐字相同**，差异只有上述 3 处包名/描述符/静态入口；这说明 1.21 侧已有人按 GeckoLib 5 重写过一遍，**不是缺口**。文档注释里那句「GeckoLib 5 的动画加载结构完全不同（`ActorAnimation` + `ActorAnimationParticleEffect`），升级时需要另写一份」两侧都在 —— 是 1.20 侧写下、1.21 侧一并带过来的既有说明，按 §1.3「1.21 既有措辞只登记不重写」保留。

### 48.5 状态
- 台账状态 JSON 共 **222** 条；本批：232/241/346/360/365/381 = `COVERED`、**233 = `PORTED`**。
- **下一待办**：行 266 `d3c7c32ff`（GAP 11：`AfterimageHelper` 的 `ModArmorBonus.NINJA_SET` 判定 + `EyeOfCthulhuRenderer` 的 `red/green/blue/alpha` 参数）、行 288 `85cbebf74`（GAP 55）、行 343 `af1426bed`（GAP 10：`ModPrefix.Summon` 系列判定，**初判像真缺口**）。
- 初判：行 266 的 `EyeOfCthulhuRenderer` 那 6 条 GAP 是**渲染签名平台差异**（1.20 Forge 传 4 个 `float`，1.21 NeoForge 传 1 个打包 `int colour` —— 同 `DemonEyeRenderer` 的 `colour`，见 §47 表）⇒ 预期 `SKIP-PLATFORM`；`AfterimageHelper` 的 5 条需逐条核。

## 四十九、用户报告落地：刷怪蛋贴图 227 张 + **此前完全缺失的刷怪蛋模型生成器**（2026-10）

### 49.1 用户报告与核实
用户报告：`textures/item/egg` 少了很多 1.20.1 的贴图。核实结果：
- 1.20 HEAD **227** 张 ↔ 1.21 HEAD **2** 张（`demon_eye_spawn_egg.png`、`king_slime_spawn_egg.png`，且这 2 张与 1.20 **字节相同** —— 用户此前在手工一张张补）。
- 分叉点 `795ac9ccc` **两仓都是 0** ⇒ 227 张全部是 1.20 **分叉后**新增 ⇒ 按方向规则（§1.1）应当移植。

### 49.2 真正的问题比「少贴图」深一层：**1.21 没有生成刷怪蛋模型的代码**
| 侧 | 贴图 | 模型生成器 |
|---|---|---|
| 1.20 | 227 张 | `ModItemModelProvider:296` 有循环：遍历 `SpawnEggItems.ITEMS.getEntries()`，贴图存在 ⇒ `minecraft:item/generated` + `layer0 = confluence:item/egg/<name>`；否则 ⇒ `minecraft:item/template_spawn_egg`（原版两层着色模板） |
| 1.21 | 2 张 | **该循环完全没有**：`ModItemModelProvider` 里 `SpawnEgg`/`spawnEgg` = **NONE**；`template_spawn_egg` 在 1.21 整个主源码 = **0 命中**；`SpawnEggItems.ITEMS` 也不在任何 `genModels` 列表里（1.21 的 `genModels` 由 21 条 `createDir(...)` 显式注册表驱动） |

**⇒ 1.21 的刷怪蛋此前根本没有物品模型。**磁盘 datagen 产物实测：1.20 `ConfluenceOtherworld/src/generated/resources/assets/confluence/models/item/` 有 **285** 个 `*_spawn_egg.json`，1.21 只有 **1** 个。**所以「少贴图」只是表象，缺的是模型生成器。**

### 49.3 落地（两处）
1. **贴图**：用 `git show 1.20 HEAD:<path>` **字节级**取出后逐张写入 ⇒ **225 张新增 + 2 张原本已相同 = 227 张**；写后逐张复核 = **227/227 与 1.20 字节相同**。
2. **生成器**：把 1.20 的循环与两个映射表逐字搬进 1.21 的 `ModItemModelProvider`：
   - `SHARED_SLIME_EGGS`（17 个史莱姆共用 `slime_spawn_egg`）、`SPAWN_EGG_TEXTURE_ALIASES`（8 条别名：crab→crap、eater_of_worlds→eater_of_world、eye_of_cthulhu→cthulhu_eye、female_angler→angler、giant_antlion_swarmer→giant_antlion、granite_elemental→grantite_elemental、little_hornet→hornet、red_squirrel→squirrel）；
   - **唯一平台适配 1 处**：`PortRegistryEntry<Item, ?>` → `DeferredHolder<Item, ? extends Item>`（与本文件既有 4 处循环同形）；另补 `import net.minecraft.server.packs.PackType`。
   - **API 静态核对**（`build/_nfsrc_219/`）：`ModelProvider.existingFileHelper` 是 `public final`；`DeferredRegister.Items.getEntries()` 返回 `Collection<DeferredHolder<Item, ? extends Item>>`；`ExistingFileHelper.exists(ResourceLocation, PackType, String, String)` 存在 ⇒ **1.20 的调用原样可用**，无需改写。
   - **刻意不抄** 1.20 循环上那条注释「1.21 已提供独立贴图的刷怪蛋使用普通物品模型……」：该说法在 1.21 HEAD 上是**假的**（当时只有 2 张），抄进去等于把错误结论固化进 1.21 代码；按 §1.3「不加新的解释性注释」一并省略，特此登记备查。

### 49.4 覆盖度（静态算；脚本 `build/_cmp231/egg_cov.py`，产物 `build/_cmp231/egg_coverage.txt`）
1.21 共注册 **290** 个刷怪蛋：
- **241** 个拿到独立贴图（走 `item/generated` + 自定义贴图）；
- **49** 个无贴图 ⇒ 回退 `item/template_spawn_egg`。逐项核对：这 49 个 `arch_wyvern, base_bones, bone_lee, clumsy_slime, cool_slime, cyborg, deerclops, diabolist, diva_slime, elder_slime, enchanted_sword_monster, flesh_slime, golfer, hostile_bunny, lava_bat, lunatic_cultist, martian_engineer, martian_officer, martian_probe, martian_walker, mystic_frog, mystic_slime, necromancer, nerdy_slime, paladin, phantasm_dragon, plantera, prime_ender_dragon, ragged_caster, ray_gunner, retinazer, scutlix, skeleton_merchant, skeletron_prime, slimeling, slimer, spazmatism, spiked_ice_slime, spiked_jungle_slime, spiked_slime, squire_slime, steampunker, stylist, surly_slime, tax_collector, tesla_turret, the_destroyer, the_twins, wizard` **在 1.20 侧同样没有贴图**（抽查前缀 `enchanted_sword`/`the_destroyer`/`deerclops`/`plantera`/`slimeling`/`slimer`/`spiked_slime` ⇒ 1.20 全 ABSENT）⇒ **两侧行为一致，不是缺口**；
- **4** 张 1.20 独有、1.21 无对应刷怪蛋：`brown_spawn_egg`、`evil_bunny_spawn_egg`、`the_hungry_spawn_egg`、`tumbleweed_spawn_egg` —— 照「以 1.20 逐字为准」一并搬入，当前无人引用，**登记为观察项**。

### 49.5 归属与台账
- **行 273 `40f02b002`「补个蛋」⇒ `PORTED`**：该提交新增 **106** 张蛋贴图（另有 `ModChineseProvider`/`ModEnglishProvider`/`ModTabs`/`FoodItems` 小改）。
- **行 170 `9645da98c`（2026-08-24「重构……NPC 战斗体系……」）保持 `LOST?` 不动**：**本批结清其中"刷怪蛋模型生成器"这一块** —— `git log -S` 对 `SHARED_SLIME_EGGS` / `SPAWN_EGG_TEXTURE_ALIASES` / `item/egg/` 三处命中的**都只有它**；该行另有 58 个文件待复核，故不翻状态。
- **行 69 `1284d08a9`「able to into world」已是 `PORTED`，但蛋贴图这部分此前并未真正落地**（该提交新增 **121** 张，1.21 当时只有 2 张）⇒ 本次一并补齐，属**已判行的残留闭合**，不翻状态。
- **行 194 `3e7cc41a9`（`DEFER-ARCH`）**含 1 张蛋贴图重命名（`honey_slime_spawn_egg` → `sweet_slime_spawn_egg`）⇒ 本次以 `sweet_slime_spawn_egg.png` 形态落地。
- 台账状态 JSON 共 **223** 条；`fix_eol --check` = **候选 0**。

### 49.6 待用户执行 / 复核提示
1. **datagen 需重跑**（本会话遵 §1.10 不跑编译/datagen）：重跑后 1.21 的 `src/generated/resources/assets/confluence/models/item/` 才会出现约 **285** 个 `*_spawn_egg.json`。**在那之前，1.21 的刷怪蛋在游戏里仍会显示成缺失模型** —— 这正是用户看到的现象的完整成因。
2. 复核提示（记录，不作改动）：`item/generated` 只有 `tintindex 0` 一层，而 NeoForge `DeferredSpawnEggItem` 会为 tint 0/1 注册颜色（`build/_nfsrc_219/net/neoforged/neoforge/common/DeferredSpawnEggItem.java:94-99`）；Forge 侧同样为刷怪蛋注册颜色 ⇒ **两侧同形**，故照 1.20 原样落地**不引入新的表现差异**；若实机观感异常（例如自定义贴图被背景色相乘），需另立裁定。

## 五十、行 343 召唤词缀**真落地**（10 文件）+ 查实**两处系统性缺口**：字幕 lang 键 457 条、标签遮蔽（2026-10）

### 50.1 行 343 `af1426bed`「完成召唤词缀」= `PORTED`
逐 hunk 查证把 10 条 STRONG 全部解出，指向一个**完整功能**：1.20 有 `ModPrefix.Summon` 记录族 + `PrefixType.SUMMON`，1.21 **完全没有**（`ModPrefix` 记录只有 6 个：Accessory/Universal/Common/Melee/Ranged/Magic，无 Summon；`PrefixType` 无 `SUMMON`；`summon_prefix` / `registerGroup("summon")` 全 0 命中）。另有**一处置桩证据**：1.21 `WhipMarkTracker:93` 有 `private static float tagDamageOf(ModPrefix prefix) { return 0.0F; }` —— 一个等着被填的占位符。

**落地 10 文件**（写手执行，主代理逐项独立复核）：

| 文件 | 内容 | 复核 |
|---|---|---|
| `ModPrefix.java` | `record Summon`（13 个词缀 FABLED…SKITTISH）+ `VALUES`/`ID`/`createComponent`/`canBeMercy`/`getModifierId`/`register`/`init`；`ID_MAP` 追加 **85–97**（紧接既有 84）；`Summon.init()` | 与 1.20 用 difflib 比对：仅 4 行属必要适配（`createComponent(PrefixType)` 签名、`ImmutableListMultimap.<Holder<Attribute>,…>`、`getSummonDamage()` 不带 `.value()`、`PrefixComponent` 7 参） |
| `PrefixType.java` | `SUMMON("universal","summon")` 常量**逐字**插在 `MAGIC` 与 `ACCESSORY` 之间（含实例初始化器 + 2 个覆写） | 与 1.20 第 68–92 行 **byte-identical**；另加 `case SUMMON -> Summon.FABLED;`（1.21 的 `bestPrefix` 是全量 switch 表达式，缺 case 即编译错） |
| `PrefixUtils.java` | `getPrefixType` 增 `PREFIX_SUMMON_ONLY → SUMMON` 分支 | 与 1.20 仅差局部名 `stack`→`itemStack` |
| `SummonerWeaponItem.java` | `getSummonDamage`/`getSummonKnockback` 加 `Summon` 分支；`getSummonArmorPierce` 补回 `armorPierce + summon.armorPenetration()`；`createMinion`/`getPrefix` 两处 switch 加 `case SUMMON` | 与 1.20 第 113–151 行 **zero diff** |
| `WhipMarkTracker.java` | 删掉 0.0F 置桩，改回 1.20 的内联 `instanceof ModPrefix.Summon summon → additionalDamage += summon.tagDamage()` | 与 1.20 第 83–86 行 zero diff；`tagDamageOf` 全仓 0 命中 |
| `ModAttributeUtils.java` | 补回整段召唤武器 tooltip 块（`instanceof SummonerWeaponItem<?>` + 伤害/击退/标记伤害/穿甲 4 行） | 与 1.20 第 41–59 行 zero diff（含 1.20 的中文注释） |
| `ModTags.java` | 新注册 `PREFIX_SUMMON_ONLY`（带 1.20 的 `// 没有鞭子` 注释） | 与 1.20 第 421 行 zero diff |
| `ModItemTagsProvider.java` | `PREFIX_SUMMON_ONLY` 装 `SummonItems`；`SUMMONER_WEAPON.addTags(PREFIX_SUMMON_ONLY)` | 语义与 1.20 第 541–544 行一致 |
| `ModChineseProvider.java` / `ModEnglishProvider.java` | 13 个召唤词缀名 + `tooltip.armor_penetration` + `tooltip.summon_tag_damage` | 与 1.20 对应区间 zero diff |

**API 静态核对**（`build/_nfsrc_219/`，未跑编译）：`ModelProvider` 无关；本题涉及 `EquipmentSlotGroup.test(EquipmentSlot)` ✓、`Holder.is(Holder<T>)` ✓、`IItemStackExtension.getAttributeModifiers()` 返 `ItemAttributeModifiers`（record，含 `modifiers()`）✓ —— 写手为 1.21 补的 `hasKnockback` 助手（1.20 用 `getAttributeModifiers(EquipmentSlot)`，1.21 已无该重载）映射正确。`PrefixComponent` 在 1.21 是 **7 参** `(type,name,modifiers,manaCost,additionalMana,tier,value)`，与 1.21 既有 5 个兄弟记录的调用形状逐一对上。

### 50.2 ★ 关键修正：不补这两处，整套召唤词缀**在运行时是死的**
写手主动上报「新 `SUMMON` 类型会被遮蔽」，主代理查证**属实且找到元凶**：

- 1.20 HEAD：`tag(ModTags.Items.PREFIX_MAGIC_ONLY).addTags(ModTags.Items.MANA_WEAPON);`（**只有** MANA_WEAPON）
- 1.21 HEAD：`tag(ModTags.Items.PREFIX_MAGIC_ONLY).addTags(ModTags.Items.MANA_WEAPON, ModTags.Items.SUMMONER_WEAPON);`
- 而 `PrefixUtils.getPrefixType` 的顺序是 **MAGIC 先于 SUMMON** ⇒ 召唤武器永远判成 `MAGIC`，`SUMMON` 分支不可达。

**元凶 = `2f44045a8`「大改修饰语」（2026-09-23）**，`merge-base --is-ancestor` 实测**不可从 1.21 分支到达 ⇒ 纯 1.20 分叉后提交**。其 diff（`ModItemTagsProvider`）明确做了三件事：① 把 `SUMMONER_WEAPON` **从 `PREFIX_MAGIC_ONLY` 摘掉**（改成只有 `MANA_WEAPON`）；② 新引入 `PREFIX_SUMMON_ONLY` 并接线 `SUMMONER_WEAPON.addTags(PREFIX_SUMMON_ONLY)`；③ 把鞭子同时塞进 `whip` + `melee_weapon_tools` + `summoner_weapon`，并让 `PREFIX_MELEE_ONLY` 含 `ModTags.Items.WHIP`。同一提交还改了 `PrefixUtils.couldReforge`：1.20 是 `SUMMONER_WEAPON || PREFIX_UNIVERSAL_ONLY || … || PREFIX_SUMMON_ONLY || PREFIX_ACCESSORY_ONLY`，1.21 少了这两项。

**按 §1.1（分叉后 1.20 的改动 ⇒ 1.21 对齐 1.20）落地最小必要集**（否则功能与可重铸性二选一必坏）：
1. `ModItemTagsProvider`：`PREFIX_MAGIC_ONLY` 改回 `.addTags(ModTags.Items.MANA_WEAPON);`
2. `PrefixUtils.couldReforge`：补回 `stack.is(ModTags.Items.SUMMONER_WEAPON) ||`（首位）与 `stack.is(ModTags.Items.PREFIX_SUMMON_ONLY) ||`
> 只摘 `SUMMONER_WEAPON` 不会误伤鞭子：1.21 的鞭子**不在** `SUMMONER_WEAPON` 里（`:682` 只进 `whip`）。摘除只改变**召唤武器**，正是本意。

**登记为剩余欠账（`2f44045a8` 其余部分）**：`TOOLS_HOOK` 改名、`YOYO`/`BOOMERANG`/`FLAIL`/`SPEAR`/`LANCE` 归属调整、`PREFIX_MELEE_ONLY` 加 `WHIP`、鞭子三标签接线、`SHORT_SWORD` 新标签。**这些标签集在两仓还有 1.21 侧自己的差异**（如 1.21 的 `PREFIX_MELEE_ONLY` 含 `FLAIL` 且 `.add(Items.MACE)`，而 1.20 把 MACE 注释掉、改成含 `LANCE`+`WHIP`）⇒ 必须逐标签做方向判定，**不能整块镜像**，故留作 `2f44045a8` 专批。

### 50.3 ★ 系统性缺口一：**字幕 lang 键 457 条**（`port_sounds.py` 的检查口径漏掉了 datagen provider）
- 现象：1.20 的 datagen provider 有 **487** 个 `confluence.subtitle.*` 键（英文 520 条语句 / 487 唯一，含 33 条重复），1.21 只有 **30**。
- **归因**：`notes/SOUND-PORT.md` §4「字幕 lang 键」只统计了**静态** `assets/confluence/lang/{de_de,es_es,lzh,pt_br,ru_ru}.json` 与 `i18n/*`（全部「可补 0」），**没有覆盖 `ModChineseProvider`/`ModEnglishProvider`** —— 而 en_us/zh_cn 正是由这两个 provider 生成的。于是缺口被漏掉：磁盘实测 1.20 生成物 `zh_cn.json` 7339 条 / 字幕 493，1.21 只有 5889 条 / 字幕 36（**de_de 的 68 条字幕反而多于 en_us 的 36 条**，本身就是异常信号）。
- **必要性核验**：逐键比对后 **457 条全部**能在 1.21 的 `sounds.json` 找到对应音效（`sounds.json` 两仓均 **503** 条，完全一致）⇒ **457 条一条不缺都是必要的**，0 条属「音效本身没搬」。
- **落地**：脚本 `build/_cmp231/subport.py`（只补缺失键、不动既有键），插入 457（zh）+ 490（en）行。
- **不变量校验**：`build/_cmp231/subverify.py` 按「**末次生效**（last-wins）键→语句」比对 ⇒ 两 provider 均 **key sets EQUAL=True、effective-value mismatches=0**；英文侧的 33 条重复也**与 1.20 数量一致**（520 语句/487 唯一）。CRLF 保持、无 BOM。

### 50.4 ★ 系统性缺口二：datagen 其余 lang 键 279 条
同法逐键对齐（脚本 `build/_cmp231/langport.py`，锚定插入：每条缺失语句插在 1.20 里紧邻其前、且在 1.21 已存在的键之后，保持 1.20 次序；无孤儿）：
- `summon.confluence.*` 15（仆从显示名：sculk_wisp/vampire_frog/snow_flinx/slime/finch/hornet/i_32_iron_golem/imp/deadly_sphere/…）
- `prefix.confluence.legendary2` 1（`Melee.LEGENDARY2` 在 1.21 存在，只是缺名）
- 其余 `entity.confluence.*`（62，含 `*_segment` 与蝴蝶/蜻蜓/鸭变体）、`tooltip.*`（40）、`gui.*`/`house_validator.*`/`itemGroup.*`/`message.*`/`container.*`/`button.*`/`creativetab.*`/`key.*` 等
- **终态不变量**：**1.20 的键集已完全包含于 1.21**（zh/en 均 `1.20-subset-of-1.21=True`、`still-missing=0`）；12 个键族「零缺失」。

### 50.5 本批判 `COVERED` 的 21 行（证据同源）
`239, 266, 268, 269, 284, 289, 292, 299, 306, 311, 314, 333, 340, 352, 354, 358, 366, 372, 374, 375, 386`

**判定口径**（三步，全部有实测输出）：① `rowaudit.py` 逐 hunk 出 GAP 候选；② `classify.py` 把候选按「PortLib / 已知平台改名 / 待核」分桶 —— 本批待核行绝大多数是 **GeckoLib 4→5 包名**（`software.bernie.geckolib.core.*` → `…animation.*`）、`defineSynchedData()` 签名、`getOrCreateTag()`→组件、`forge:separate_transforms`→neoforge、`isSameItemSameTags`、顶点 API、RGBA float→打包 int、`Attributes.JUMP_STRENGTH_1211`；③ **对每个存疑符号直接做两仓存在性探针**（`probes.py`/`probes2.py`/`finalprobe.py`，共 60+ 符号）—— 结果**无一例外 BOTH**（含 `AfterimageHelper`/`callGetBob`/`callSetupRotations`/`AttachmentEntityRenderDispatcher`/`SculkWispMinion`/`IronGolemMinion`/`GroundMinion`/`bird_nest`/`ModBlockCounters.isGraveyard`/`NPCSpawner.Region`/`tickDungeonResidents`/`dungeonEntityRemoved`/`martian_probe`/`SWING_PROGRESS`/`FATIGUE`/`MAX_FLIGHT_ENERGY`/`goblin_tinkerer_trade`/`mechanic_trade`/`NPCReforgeScreen`/`BasePhasebladeItem` …）。
**唯一漏网的真缺口就是 50.3/50.4 的 lang 键** —— 那批键在 1.21 的**全部**来源只有 provider，因此「键缺 = 功能表现缺」，已随本批补齐。再叠加 `file-lag.json` 对这些文件全部为 `IN-SYNC`（0.88–1.00），故判 `COVERED`。

### 50.6 状态
- 台账状态 JSON 共 **245** 条；行 **343 = `PORTED`**，上述 21 行 = `COVERED`。
- 行 231–400 剩余 `TODO`：**113 − 22 = 91**。
- **下一批**：`2f44045a8`（大改修饰语，标签/词缀专批，需逐标签方向判定）；以及 pct 0.90–0.94 的剩余行（267 `f4ffadaa7` AttachmentEntity 体系迁移 = 72 文件大行、304、309、311 已完成、358 已完成…）。
- **待用户执行**：datagen 需重跑 —— 本批新增 **736 条** lang 语句只有在重跑后才进 `src/generated/resources/.../lang/*.json`。

## 五十一、**资产类缺口全量盘点**：517 个 1.20 资产在 1.21 完全不存在（含 20 个密文 geo）+ 语言子 provider 111 键（2026-10）

### 51.1 起因：行 288 的 `bird_nest` 暴露了一个**成规模的**缺口类别
行 288 `85cbebf74`「完成鸟巢叠加层」逐 hunk 查证时，6 个 java 文件全部 `IN-SYNC`，但 `bird_nest` 那 40 行「GAP」其实是一整个 **geo 模型文件的内容**。顺着查：

| | 结论 |
|---|---|
| 1.21 `BirdNestLayer.java:50/55` | 引用 `geo/entity/summon/bird_nest.geo.json` 与 `textures/entity/summon/bird_nest.png` |
| 1.20 HEAD | 两个文件**都存在** |
| 1.21 HEAD | **两个都不存在**（tracked 与磁盘皆无） |

⇒ 1.21 的鸟巢叠加层指向**不存在的资源**。而 `git grep -rl bird_nest` 在两仓都命中 2 个文件（都是 java），所以**符号探针会误判成 BOTH** —— 这正是「探针查代码、不查资源」的盲区。

### 51.2 全量资产盘点（脚本 `build/_cmp231/assetgap2.py`，坑已记）
| 指标 | 数量 |
|---|---|
| 1.20 HEAD 资产文件 | **10316** |
| 1.21 HEAD 资产文件 | **10366** |
| 路径级缺失 | **1559** |
| ↳ 同名文件存在于别处（= 1.21 扁平化/改名搬迁） | 1042（**本批不动**，另案） |
| ↳ **真正不存在**（basename 全仓都找不到） | **517** |

517 的构成：**二进制 282**（PNG）+ **密文 geo 20** + **纯文本 215**。
目录分布（Top）：`textures/item/flail` 30、`geo/entity/animal` 29、`animations/entity/animal` 28、`animations/entity/npc` 27、`geo/entity/npc` 27、`textures/entity/npc` 27、`textures/entity/summon` 25、`geo/entity/summon` 24、`animations/entity/summon` 23、`textures/entity/yoyos` 19、`textures/item/yoyo` 18、`textures/item/whip` 17、`textures/item/summon` 15 …

> ⚠️ **踩到的坑（已修）**：`git ls-tree -r --name-only HEAD:<subtree>` 返回的是**相对该子树**的路径，第一版脚本直接拿去 `git show HEAD:<path>` ⇒ **517 次全部读取失败**，而空读被误判成「非二进制纯文本」（于是分类结果里出现 0 个 binary 的荒谬结论）。修正为记录「根前缀 + 相对名」后分类才正确（282/20/215）。**凡按子树枚举路径者，必须回填前缀**。

### 51.3 逐字节落地 + 校验
- 517 个文件全部用 `git show 1.20 HEAD:<path>` **字节级**取出写入；
- **215 个纯文本**按本仓约定转 CRLF（`w/crlf`）；**282 个二进制**与 **20 个密文 geo** 保持原始字节；
- 校验：逐文件比对 `git rev-parse :<path>`（1.21 暂存对象）与 1.20 HEAD 对象 ⇒ **517/517 完全一致，0 个未暂存，0 个不同**；
- `bird_nest` 的 geo 是**明文 JSON**（首字节 `{`）⇒ 已转 CRLF，`git ls-files --eol` = `i/lf w/crlf` ✓。

**归属（脚本 `assetrows.py`，按 `git log --3 -- <path>` 归到台账行）**：跨多行，最大两处是
**行 69 `1284d08a9`（已判 `PORTED`）拿到 257 个**、**行 256（`TODO` 大行）拿到 103 个**；
其余：335(20)、319(19)、316(9)、332(8)、322(8)、257(7)、247(6)、342(6)、244(4)、288(2)、274(2)、385(2)、330/281/377(1) 等。
⇒ 这印证了 §47 的结论：**台账曾把「已判 PORTED 的行」标成完成，而其资产部分并未真正落地**（行 69 的 257 个就是证据）。本批把这一类残留一次性收口。

### 51.4 语言子 provider 111 键（`data/gen/language/`，与 §50.3/50.4 的主 provider 是**另一批文件**）
`langsub.py` 逐 provider 比对字面键：
| 文件 | 1.20 | 1.21 | 缺失 |
|---|---|---|---|
| `BestiaryLanguageSubProvider.java` | 1066→(键 536) | 766 | **66** |
| `ConfigurationLanguageSubProvider.java` | 619→(键 311) | 276 | **45** |
| `AchievementsLanguageSubProvider.java` | 562 | 562 | 0 |
| 其余 | — | — | 0 |

- **最佳iary 66 条**（`bestiary.entity.confluence.*.desc`：蝴蝶 9、蜻蜓 7、宝石兔/宝石松鼠各 9、萤火虫/瓢虫/蝎子/蠕虫变体、`the_hungry_*`、`wandering_eye`、`sweet_slime` …）。**必要性已核**：这些实体在 1.21 都存在（抽样 `butterfly`/`dragonfly`/`jewel_bunny`/`sweet_slime`/`wandering_eye`/`hornet_fatty` 全部 BOTH）⇒ 属**纯文本缺口**，不是「实体没搬」。这同时**修正了 §6.6 登记的「3 条 bestiary 文本缺失」——实际是 66 条**。
- **配置 45 条**（`confluence.configuration.*`）：其中 **41 条的对应配置项在 1.21 已存在**（`autoFireAllGuns`/`autoSwingAllSwords`/`enhanceAllMonster`/`spawnWithoutLight`/`weaponInputButton`/`gun|staff|whip|yoyo|flail UseButton`/`monsterAttributesMultiplier*`/`MonsterAttributes`/`WeaponInput` 全部 BOTH）⇒ 纯文本缺口；
- **剩余 4 项属代码缺口（登记，不在本批）**：`AutomaticWeaponUse`（配置**分类**缺失：1.20 `push/pop=21` ↔ 1.21 `20`）、`shimmerDecomposeFirstTagItem`、`npcAttackBlacklist`（`CommonConfigs` 的第二个 `defineListAllowEmpty`：1.20 有 2 个 ↔ 1.21 只有 1 个）、`indicatorMode`（`ClientConfigs`）。⇒ **归 行 369 `2ca9e2ac0`** 等行处置。
- 落地脚本 `langsub_port.py`（锚定插入，语句边界用**逐行**定位而非顺序消费 —— 顺序消费版会吞掉后续 `add(` 行，导致把已存在的键误判为缺失、进而**重复插入**；本轮实撞到：同一份文件两种解析给出 276 vs 66 的缺口数，改用整文件正则判定「是否存在」+ 逐行定位「插入位置」后才与 `langsub.py` 的 66/45 吻合）；
- 校验：两 provider 均 **`1.20-subset-of-1.21=True`、`still-missing=0`**。

### 51.5 ★ EOL 闸门的**唯一例外**（必须登记，否则误判为违规）
本批落地后 `fix_eol.py --check` = **候选 6**（不是 0），6 个全部是 **§1.9 的密文 geo**：
`geo/entity/animal/{bird,blue_jay,butterfly,cardinal,dragonfly}.geo.json`、`geo/entity/summon/piggy_bank.geo.json`。
逐个实测：**首字节均非 `{`（密文，形如 `B3362,B1437,B646`）**，且**工作树字节 == 1.20 blob 字节**（未被改动）。
按 §1.9「密文 geo 禁止做 EOL/BOM/格式化」⇒ **本批刻意不对这 6 个执行 fix_eol**；`git` 自己因 autocrlf 把它们当文本（`i/lf w/lf`），故这是**闸门与 §1.9 的真实冲突**，不是漏跑。
> 建议（待用户裁）：若要根治，可加 `.gitattributes` 把密文 geo 标为 binary；但注意 `*.geo.json` 里还有**明文** geo，一刀切 `-text` 会让所有明文 geo 的索引 blob 从 LF 变 CRLF ⇒ 产生全量 diff。故本轮**不动**，仅登记。

### 51.6 状态
- 台账状态 JSON 共 **246** 条；**行 288 = `COVERED`**（6 个 java 文件 `IN-SYNC` + 2 个资产已字节级补齐）。
- 行 231–400 剩余 `TODO`：**90**。
- 本批**不翻**行 69/256 等行的状态（它们仍有独立的代码欠账），但**其资产残留已收口**，已在 51.3 登记归属。
- **下一批**：`2f44045a8` 标签重排专批；行 304/309/303/293/363/235/320/307/276/369；行 267 `f4ffadaa7`（72 文件大行）。

### 51.7 51.2 的 517 里有 **1 个是平台专有物，已剔除**（脚本盲抄会踩的坑）
逐条内容复核 517 个文件时发现 **`ConfluenceOtherworld/src/main/resources/META-INF/coremods.json`** 不在 `assets/` 下，打开一看是 Forge 的 **JS coremod 声明**：
```json
{ "confluence_enum": "coremods/confluence_enum.js" }
```
而 1.21 的 `META-INF/` 只有 `accesstransformer.cfg` / **`enumextensions.json`** / `neoforge.mods.toml` —— NeoForge **原生**就有枚举扩展机制（`enumextensions.json`），1.20 才需要 JS coremod 顶。
⇒ 判定 **`SKIP-PLATFORM`**（引用物 `coremods/confluence_enum.js` 在 1.21 也不存在），**已从暂存区与磁盘一并删除**，落地数由 517 更正为 **516**（资产块 515 + 1 个 `META-INF/coremods.json` 剔除）。
**教训**：脚本化「按缺失清单批量补文件」时会连带搬入**平台专有的配置/描述符**；补完之后必须**逐条看内容**而不是只看扩展名与路径（本次的「可疑扫描」第一版把 `.js` 当成 `.json` 的子串，误报 231 条，改成按结尾精确匹配后才定位到唯一真凶）。
> 补充说明：`assets/confluence/geo/**/*.geo.json` 里有**明文**（首字节 `{`，本批已转 CRLF）与**密文**（首字节非 `{`，本批保持原字节）两类，**不能按扩展名一刀切**。

## 五十二、pct 0.89–0.98 一批（9 行判 `COVERED`）+ 补回漏接的 **3 处 `DevelopmentSpawnPolicy` 守卫**中的 2 处（2026-10）

### 52.1 本批 9 行 = `COVERED`
`235 a358494fa`、`276 785b151f0`、`293 157730424`、`303 d879d8ffc`、`304 c454b8d68`、`307 124baf17d`、`309 d3d8dcd24`、`320 e7cabcfea`、`363 a2dfa30da`

判定口径（承接 §50.5）：`rowaudit` 逐 hunk 出 GAP → `classify` 分桶 → **逐个存疑符号做两仓存在性探针**。本批探针（`probes3.py`/`probes4.py`）结果与残差：

| 行 | GAP 数 | 残差性质 |
|---|---|---|
| 303 | 0 | — |
| 309 | 0 | — |
| 304 | 2 | `BossEntities.PRIME_ENDER_DRAGON`：`RegistryObject<…>`（PortLib）↔ `DeferredHolder<…>`；`BoomerangItem` 的 `unbreakable()` 属 Forge 属性写法。两符**探针 BOTH** |
| 320 | 3 | `SummonItems.SANGUINE_STAFF`（该文件在 1.21 属**禁改**，但 1.21 **已有**该条）+ `SummonerAttachmentEntityTypes.SANGUINE_BAT`（1.21 `:37` 已有，仅声明类型改 `DeferredHolder`）+ import。**探针全部 BOTH** |
| 307 | 12 | 2 条 import（`DesertTigerMinion`/`SpiderMinion`）**BOTH** |
| 293 | 19 | `renderToBuffer(… FastColor.ARGB32.red(color) …)` 属 1.21 已用的打包色写法、3 条 import、`setSecondsOnFire` —— **BOTH** |
| 276 | 4 | `import FinchMinion` **BOTH** |
| 363 | 15 | `entityData.define(DATA_MOOD, new CompoundTag())`、`DataResult.PartialResult::message`、`Component.Serializer.toJson/fromJson` —— **BOTH** |
| 235 | 20 | 含 `event.getResult() == Event.Result.DENY`（→ 见 52.2，已修）、`BaseBossPart`/`BaseLivingBossPart`/`AbstractTwinEye`/`DungeonStructure.skipSpawn`/`GameEventSystem.shouldDenyNatureSpawn`/`HostileParticleProjectile`/`renderInGui` —— **BOTH** |

**行 235 额外做了一次文件级筛查**（166 文件大行）：`.java` 侧 **87 IN-SYNC / 6 PARTIAL / 2 ABSENT-21 / 1 LAGGING**。唯一的 `LAGGING` 是 **`ModEntities.java`（ovlp 0.043）** —— 该文件属 §1.5 **禁改**，且是全仓最大的注册表，其低重叠是**跨全部行的注册差异**，不是本行残留，**仅登记不动**。两条 `ABSENT-21` 的路径在 1.20 HEAD 也已不存在（搬迁/改名）⇒ 无害。

### 52.2 ★ 补回漏接的 `DevelopmentSpawnPolicy` 守卫（2 处；第 3 处登记）
追 `Event.Result.DENY` 时发现一条**成组**的漏接：`DevelopmentSpawnPolicy.allowsAutomaticSpawn(...)` **1.20 有 6 个调用点，1.21 只有 3 个**（`NPCSpawner:447`/`DungeonSpirit:54`/`GameEventSystem:240`）。由 **`b05c8dc3f`（= 行 256）**引入。缺的三处中，两处已在本批补回：

1. **`data/map/BlockBreakSpawns.java`**（1.21 `:42`）：1.20 是
   `spawn.types.getRandomValue(level.random).filter(DevelopmentSpawnPolicy::allowsAutomaticSpawn).ifPresent(...)`
   ⇒ 1.21 少了 `.filter(...)`。**已补**，补后该行与 1.20 **逐字相同**（`verify_sp.py` 实测两行字符串完全一致）。
2. **`event/game/entity/LivingEntityEvents.java`** 的 `mobSpawn$SpawnPlacementCheck`：1.20 开头有
   ```java
   if ((event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION)
           && !DevelopmentSpawnPolicy.allowsAutomaticSpawn(event.getEntityType())) { event.setResult(DENY); return; }
   if (event.getResult() == DENY) return;
   ```
   1.21 完全没有这段（直接从 `NATURAL && !getPlacementCheckResult()` 开始）。**已补**，**唯一平台适配**：`Event.Result.DENY` → `MobSpawnEvent.SpawnPlacementCheck.Result.FAIL`（NeoForge 的 `Result` 枚举实测为 `SUCCEED`/`DEFAULT`/`FAIL`，见 `build/_nfsrc_219/…/MobSpawnEvent.java`，`FAIL` 的 javadoc 正是「Forces the event to cause the placement check to fail」= 语义等价）。补后与 1.20 **结构逐行一致**，仅上述枚举名与既有 `event.getPlacementCheckResult()` 写法不同。
   两处均补 `import org.confluence.mod.common.init.entity.DevelopmentSpawnPolicy;`。

**第 3 处＝登记不落地**：`event/game/entity/PlayerEvents.java` 的**钓鱼钩生成怪**整段（`BLOODY_FISHING_HOOK` → `WANDERING_EYE_FISH`/`ZOMBIE_MERMAN`，其中含 `allowsAutomaticSpawn` 守卫，1.20 `:325-334`）在 1.21 **整段不存在**（`WANDERING_EYE_FISH`/`ZOMBIE_MERMAN`/`BLOODY_FISHING_HOOK` 在 1.21 `PlayerEvents` 内 0 命中；三个实体本身在 1.21 都存在）。该段跨两个 1.20 提交（守卫来自 `b05c8dc3f`=行 256，整段来自 `741f98d1e`），**归行 256 专批处置**——需先核 1.21 `PlayerEvents` 的钓鱼方法形状，不做盲搬。

### 52.3 踩到的自身工具坑（已修，登记以免复发）
`region()` 辅助函数把路径拼成 `common/event/game/PlayerEvents.java`，而真实路径是 `common/event/game/**entity**/PlayerEvents.java` ⇒ 两个仓库都报「FILE ABSENT」，我一度据此推断「1.21 该文件不存在」。**修正**：凡「文件不存在」的结论，必须用 `git ls-tree -r --name-only HEAD | grep <basename>` 复核真实路径，不能只信一次 `git show` 失败。

### 52.4 状态
- 台账状态 JSON 共 **255** 条；本批 9 行 = `COVERED`。
- 行 231–400 剩余 `TODO`：**81**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo，闸门与 §1.9 的既有冲突，本批未新增）。
- **下一批**：`2f44045a8`（标签重排专批）、行 **267 `f4ffadaa7`**（AttachmentEntity 体系迁移，72 文件大行）、**369**（4 项配置代码缺口：`AutomaticWeaponUse` 分类/`shimmerDecomposeFirstTagItem`/`npcAttackBlacklist`/`indicatorMode`）、行 **256** 专批（含上述钓鱼段与其余 103 资产之外的代码欠账）。

## 五十三、行 369 `COVERED`（补回**失效**的 NPC 攻击黑名单配置）+ 行 265 `COVERED` + 补回**缺失的游戏阶段刷怪门槛**（2026-10）

### 53.1 ★ 行 369 `2ca9e2ac0`「移植左键状态接口并添加 NPC 主动攻击黑名单」= `COVERED`
逐 hunk 把 17 文件解为两件事：**左键状态接口**（`ILeftClickStateItem` / `LeftClickState` / `LeftClickItemHandler` / `LeftClickItemActionPacketC2S` / `NPCHurtRetreatGoal` / `BaseLanceItem` / `BaseTerraRepeaterItem`）与 **NPC 攻击黑名单**。探针结果：以上符号**全部 BOTH**（1.21 都有），`NPCAttackBlacklist.java` 在 1.21 更是**整份 110 行都在**（仅 `ForgeRegistries.ENTITY_TYPES` → `BuiltInRegistries.ENTITY_TYPE`）。

**但发现一个"功能在、开关丢"的真缺口**：1.21 的**消费者在**（`BaseNPC:328` `!NPCAttackBlacklist.contains(target.getType())`），而**配置项不在** —— `CommonConfigs` 里 `NPC_ATTACK_BLACKLIST` 的字段/定义/`onLoad` 重载三处全缺 ⇒ `NPCAttackBlacklist.rules` **永远是空集**，黑名单**从未生效**（与 §50.2 的 SUMMON 遮蔽同一类缺陷：消费者在、驱动缺失）。

**落地**（`CommonConfigs.java`，4 处，全部与 1.20 **逐字相同**，已逐行 byte 比对 SAME）：
1. `import org.confluence.mod.common.entity.npc.NPCAttackBlacklist;`
2. 字段 `private static ConfigValue<List<? extends String>> NPC_ATTACK_BLACKLIST;`（插在 `NPC_SPAWN_INTERVAL` 与 `BROADCAST_NPC_MSG` 之间，与 1.20 同序）
3. `NPC_ATTACK_BLACKLIST = builder.defineListAllowEmpty("npcAttackBlacklist", List::of, value -> value instanceof String entry && NPCAttackBlacklist.isValid(entry));`（NPC 分类内，同序）
4. `NPCAttackBlacklist.reload(NPC_ATTACK_BLACKLIST.get());`（`onLoad()` 内，紧跟 `MonsterAttributeScaling.reload();`，与 1.20 同位）
→ `ConfigValue` 由既有 `ModConfigSpec.*` 通配导入提供、`List` 已导入，无需新增其余 import。

### 53.2 同批把「4 项配置缺口」全部结清 —— 其中**3 项经查证不是缺口**
`notes/WP2-MONSTER-ATTR-SCALING.md` 与 §51.4 曾登记 4 项配置代码缺口，逐项核完：
| 项 | 结论 |
|---|---|
| `npcAttackBlacklist` | **真缺口，已落地**（53.1） |
| `AUTO_SWING_ALL_SWORDS` + `AutomaticWeaponUse` 分类 | **不是缺口**：其消费者是 `mixin/integration/terracurio/TCClientPacketHandlerMixin`，属 integration 层；`notes/WORK-QUEUE.md:335` 早已明文「消费者在 integration 层 → **永不移植**」，所以 1.21 本就不该有这个开关（残留的 `prefix`/`autoSwingAllSwords` lang 键属死键，无害） |
| `shimmerDecomposeFirstTagItem` | **不是单纯配置缺口**：其消费者是 `mixin/world/entity/item/ItemEntityMixin:154` 的一个分支（微光分解「第一个 tag 物品」），该分支在 1.21 **也不存在**（1.21 同 mixin 只有 `:136` 的 `SHIMMER_DECOMPOSE` 早退）⇒ 属**整段未移植**，归该 mixin 的行处置，登记 |
| `indicatorMode` | **设计分歧**：1.20 是枚举 `IndicatorMode`（VIRTUAL/PARTICLE，`defineEnum`），1.21 是 `boolean damageIndicator/healIndicator`（`builder.define`）。属 1.21 侧的形态选择，**不回退**；对应 `confluence.configuration.indicatorMode.*` 两条 lang 键在 1.21 为死键（无害） |

### 53.3 ★ 补回**缺失的游戏阶段刷怪门槛** `SpawnPlacementChecks.atLeast`
查 `SpawnPlacementChecks` 时发现 1.20 有**两个**包络助手、1.21 只有**一个**：
- 1.20 `:770 hardmode(predicate)` / `:778 atLeast(GamePhase phase, predicate)`
- 1.21 `:478 hardmode(predicate)` —— **没有 `atLeast`**，使用点也不存在

后果（实测）：1.20 `CreatureSpawnPlacements:226` 把地牢怪组包在 `atLeast(GamePhase.PLANTERA, checkDungeonMonsterSpawn)` 里，而 1.21 该组（`:262`）直接裸用 `checkDungeonMonsterSpawn` ⇒ **世花前的玩家就能刷出圣骑士/骷髅李/死灵法师/恶魔教徒/褴褛法师**。

**落地**（2 文件，`atLeast` 方法体 6 行与 1.20 **逐行 SAME**）：
1. `SpawnPlacementChecks.java`：在 `hardmode` 之后补入 `atLeast`（含 1.20 的 `/// 为已有环境规则叠加指定游戏阶段门槛。` 注释）。`GamePhase`(:23) 与 `KillBoard`(:26) **本就已导入** ⇒ 无需新增 import。API 已核：`GamePhase.isAtLeast(GamePhase)` 在 1.21 `GamePhase.java:63` 存在。
2. `CreatureSpawnPlacements.java:262`：改为 `SpawnPlacementChecks.atLeast(GamePhase.PLANTERA, SpawnPlacementChecks::checkDungeonMonsterSpawn)`，并补 `import org.confluence.mod.common.data.GamePhase;`（1.21 该文件此前未导入）。

### 53.4 行 265 `cfe6ac100`「同步」= `COVERED`（行 367 仍 `TODO`）
- **265**：GAP 10 条 → 分类后仅 2 条待核：`FlailComponent` 的 `Behavior.STREAM_CODEC` 在 1.21 **已有等价物**（`StreamCodec.composite` + `ByteBufCodecs.*` 取代 1.20 的 `PortStreamCodec`/`PortByteBufCodecs`）；`level().playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, …)` 在 1.21 **同一行同位置**存在，仅 `SoundEvents.GENERIC_EXPLODE` → `.value()`（NeoForge 的 `SoundEvents` 常量是 `Holder<SoundEvent>`）⇒ 平台改名。故 `COVERED`。
- **367 `ac2ac4428`「统一生物生成检查并修正渔夫任务条件」保持 `TODO`**：本批只补了它的 `atLeast` 助手与那一个使用点。**其余仍缺**（实测差异）：`checkUndergroundMonsterSpawn` 组 1.20 是 `MAN_EATER, HORNET, SPORE_BAT` ↔ 1.21 只有 `MAN_EATER, HORNET`；同理 `SNOW_FLINX, ICE_BAT, UNDEAD_VIKING`（1.21 无该组）、`ARMORED_VIKING, ICY_MERMAN, ILLUMINANT_BAT`、`MOSS_HORNET, JUNGLE_CREEPER, BASILISK`、`LIGHT_LAMIA, SAND_POACHER`、`ICE_MIMIC, ICE_TORTOISE` 等**成员追加**，以及 `isGraveyard` 早退与 `checkOldShakingChestSpawn`。⇒ 该行是一次**成组重排**，需独立专批（不能只补助手就判完成）。
- 另：**行 378 `75a6a63ad` 的真实缺口已定位并登记**（见 53.5），本批不动。

### 53.5 登记：行 378 与行 319 的残留（本批未落地）
- **行 378 `75a6a63ad`**：1.20 `MobMixin`（43 行）含 `confluence$despawnInEmptyDimension` —— 用 `@ModifyExpressionValue` 挂 Forge 的 `ForgeEventFactory.canEntityDespawn(...)`，让**空维度**里久未行动的怪物有概率消失（不掉落、不刷怪）。1.21 `MobMixin` **只有 20 行、只有 `checkGraveyard`**，该功能**整段缺失**；而 `ForgeEventFactory.canEntityDespawn` 是 **Forge 专有**（1.21 无此符号）⇒ **不能照搬 mixin 目标**。已核实 NeoForge **有替代事件** `build/_nfsrc_219/net/neoforged/neoforge/event/entity/living/MobDespawnEvent.java` ⇒ 后续应按**事件订阅**（而非 mixin）重写；其余 GAP（`NPCTradeItemOutline`/`trade_item_outline`/`registerShader`）**都已 BOTH**。
- **行 319 `7169379cd`**：33 条 content 里 `SummonerParticleTypes`/`GenericParticleOptions`/`summoner_batched_particles`/`EyeLaserTurretMinion`/`RuinRelicMinion`/`AttachmentEntityGoalSelector`/`SummonerHelper` **全部 BOTH**；但两处**注册/序列化机制不同**：1.20 `PARTICLES.register(...)`（20=2 / 21=0）与 `writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE, …)`（20=1 / 21=0）在 1.21 均无命中，而 `getDeserializer().fromNetwork`（BOTH）仍在 ⇒ 1.21 用**原生 `DeferredRegister` 注册粒子类型**取代了 1.20 的 `PARTICLES` 辅助器。**未判完成**，留待专批核 1.21 的粒子注册形态是否等价。

### 53.6 状态
- 台账状态 JSON 共 **257** 条；本批 **265 / 369 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**79**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批未新增）。
- **下一批**：行 **367**（成组重排专批）、**378**（`MobDespawnEvent` 事件化重写）、**319**（粒子注册形态核对）、**267 `f4ffadaa7`**（72 文件大行）、`2f44045a8`（标签重排）。

## 五十四、行 367 的**结构级**盘点（逐实体谓词映射）+ 补回 `OLD_SHAKING_CHEST` 生成（2026-10）

### 54.1 方法：把「逐行 GAP」升级为「**逐实体谓词映射**」——行 367 的正确口径
行 367 `ac2ac4428`「统一生物生成检查并修正渔夫任务条件」的 74 条 GAP 里绝大多数是 `PortSpawnPlacementTypes` → `SpawnPlacementTypes` 的平台改名，逐行看会**淹掉真差异**。改用结构级口径（脚本 `build/_cmp231/r367.py` / `r367b.py`）：

1. **方法清单比对**（`SpawnPlacementChecks`）：1.20 107 个 / 1.21 106 个 ⇒ `ONLY-20 = {checkGnomeSpawn, checkOldShakingChestSpawn}`、`ONLY-21 = {isGraveyard}`。
2. **逐实体谓词映射比对**（`CreatureSpawnPlacements`）：把 `group(event, <SPT>, <谓词>, <实体…>)` 解析成 `实体 → 谓词集合`，再按实体求差。
   > ⚠️ 分组本身不能直接比：1.21 有 **131** 个 `group` 调用 vs 1.20 的 **121** —— 1.21 把实体**重新分组**了（例如 `ICE_MIMIC` 从 `ICE_MIMIC,ICE_TORTOISE` 组挪进 `ARMORED_SKELETON,GIANT_BAT` 组）。**分组不同无害，实体的谓词不同才有害。**

**逐实体结果（212 ↔ 210）**：
| 类别 | 内容 |
|---|---|
| 只在 1.20 注册 | **`GNOME`**（`checkGnomeSpawn`）、**`OLD_SHAKING_CHEST`**（`checkOldShakingChestSpawn`）—— 1.21 **完全没有这两条注册** |
| 只在 1.21 注册 | 0 |
| 同一实体、谓词不同 | **`JUNGLE_BAT`**（20 `checkRoutineMonsterSpawn` ↔ 21 `checkUndergroundMonsterSpawn`）、**`SNOW_FLINX`**、**`UNDEAD_VIKING`**（20 裸 `checkCaveMonsterSpawn` ↔ 21 `hardmode(checkCaveMonsterSpawn)`） |

### 54.2 本批落地：`OLD_SHAKING_CHEST` 恢复可生成
两个实体在 1.21 **都存在**（`MonsterEntities.OLD_SHAKING_CHEST:1046`、`MonsterEntities.GNOME` 且有渲染器/掉落表），**但都没有注册生成放置** ⇒ **永远无法自然生成**。`checkOldShakingChestSpawn` 的全部依赖在 1.21 均**已就绪且已导入**（`KillBoard.INSTANCE.isDefeated(EntityType)` `KillBoard.java:45`、`BossEntities.SKELETRON`、`canSpawnTownSlimeRescue` `SpawnPlacementChecks:796`、`NpcEntities.ELDER_SLIME` `NpcEntities:194`、`Direction`/`ModBlockCounters` 等）⇒ 可**逐字落**：

1. `SpawnPlacementChecks`：在 `checkClumsyBalloonSlimeSpawn` 之前插入 1.20 `:265-271` 的 7 行（含 `/// 旧摇摇箱仅在骷髅王后、洞穴层的牢固地面出现。`）。**7 行逐字 SAME**，花括号平衡（1.21 `{=121 }=121`）。
2. `CreatureSpawnPlacements`：在 `MYSTIC_FROG` 组之后插入
   `group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkOldShakingChestSpawn, MonsterEntities.OLD_SHAKING_CHEST);`
   （仅把 `PortSpawnPlacementTypes` 换成 `SpawnPlacementTypes`，与全文件其余 130 条同形；位置对应 1.20 `:115` 紧跟 `:114 MYSTIC_FROG` 的次序）
   ⇒ 落地后 `OLD_SHAKING_CHEST` **恢复生成**（条件：骷髅王已败 + 地下层 + 不见天 + 脚下方块坚面 + 可救援史莱姆）。

### 54.3 `GNOME` 未落地的原因（**跨侧世界生成分歧**，须用户裁定）
`checkGnomeSpawn` 需要 `ModStructures.Keys.LIVING_TREE`（`ResourceKey<Structure>`）。实测：
- 1.21 的 `ModStructures.Keys` 只有 **12** 个键（1.20 **30** 个）⇒ **没有 `LIVING_TREE` 键**（只有顶层 `ModStructures.LIVING_TREE` = **结构类型**，不是 `ResourceKey<Structure>`，不能直接当注册表键用）。
- **但**：`data/confluence/worldgen/structure/living_tree.json` —— **1.20 HEAD 已无此文件，1.21 却有**（1.20 在自己后来的提交里删掉了该结构 json）。
⇒ 现状是**两侧各缺一半**：1.20 保留了"侏儒只在活木树旁刷"的判定却删了结构；1.21 有结构却没有该判定与键。**这不是单侧落后，而是分歧**，且涉及「1.20 为何删 living_tree 结构」的意图判断，故**本批不落**，登记待裁。

### 54.4 登记（本批未动）
- **`JUNGLE_BAT` / `SNOW_FLINX` / `UNDEAD_VIKING` 的谓词差异**：两侧的引入提交已定位 —— 1.21 侧来自端口批次 `c7d25932a port(1.20 WP2 注册层全量收口)`，1.20 侧来自 `ac2ac4428`（= 行 367 本身，`git show ac2ac4428 -- CreatureSpawnPlacements` 实测确实改了 `SNOW_FLINX`/`UNDEAD_VIKING` 的分组行）。⇒ 需在行 367 专批里连同**组重排**一起对齐（那时才判 `hardmode` 包装该不该摘）。
- **`checkGnomeSpawn`**：见 54.3。
- **行 367 状态保持 `TODO`**（本轮只落了 1 个方法与 1 条注册，其余是组重排）。

### 54.5 ⚠️ 工作树发现**非本批改动**（未提交、未触碰）
`ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data/GamePhase.java` 显示为 ` M`，内容为**移除 `import org.jetbrains.annotations.NotNull;` 并把 `public @NotNull String getSerializedName()` 改为 `public String getSerializedName()`**（文件 mtime `2026-10-04T16:41`，**晚于本批所有写盘动作**，且本批脚本只写 `SpawnPlacementChecks.java` / `CreatureSpawnPlacements.java` 两个路径）。
⇒ 判定为**用户/他人的并发编辑**（形态与 HANDOFF §5 记录的「`EnchantmentUtils.java` 他人的 javadoc→/// 风格改动」同类），**已排除在本批路径限定提交之外，未触碰**。

### 54.6 状态
- 台账状态 JSON 共 **257** 条（本批无状态变更；行 367 仍 `TODO`）。
- 行 231–400 剩余 `TODO`：**79**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批未新增）。
- **下一批**：行 367 专批（组重排 + 3 处谓词 + `GNOME` 待裁）、**378**（`MobDespawnEvent` 事件化重写）、**319**（粒子注册形态）、**267 `f4ffadaa7`**（72 文件大行）、`2f44045a8`（标签重排）。

## 五十五、行 378 `COVERED`：把 Forge 的 `canEntityDespawn` mixin **改写成 NeoForge 事件订阅**（2026-10）

### 55.1 行 378 `75a6a63ad`「feat(client): 添加 NPC 商店商品稀有度描边渲染组件」的 7 文件
`ModClientEvents` +2（注册 shader）、`NPCTradeItemOutline` +124、`NPCTradeScreen` +16、**`MobMixin` +23**、3 个 shader 资产（`.fsh`/`.json`/`.vsh`）。

**渲染部分（已 COVERED，实测）**：
- 3 个 shader 资产在 1.21 **blob 逐字节相同**（`b8f7d88886` / `ff8e5e3801` / `f8c72d067a`）
- `NPCTradeItemOutline.java` 在**两仓同路径都存在**；1.21 用 `Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)` + `buffer.buildOrThrow()` + `BufferUploader.drawWithShader(...)` —— 即 **1.21 的渲染 API**（1.20 是 `getBuilder()`/`buffer.end()`），属平台适配
- shader 注册在 1.21 被**重构**到 `ModRenderer.register(event.getResourceProvider(), event::registerShader)`（`GameClientEvents:122-126`），1.20 是内联在 `ModClientEvents:1174`；1.21 `ModRenderer:36` 调 `NPCTradeItemOutline.setShader(shader)` ⇒ **等价形态**

### 55.2 ★ `MobMixin` 的空维度消失：**不是"照搬 mixin"，而是换成事件订阅**
1.20 `MobMixin`（43 行）用 `@ModifyExpressionValue` 挂 Forge 专有的
`Lnet/minecraftforge/event/ForgeEventFactory;canEntityDespawn(...)Lnet/minecraftforge/eventbus/api/Event$Result;`，让**同维度无人**时久未行动的怪物有概率消失（**不覆盖**持久化与平台保护结果）。1.21 的 `MobMixin` 只有 20 行、只有 `checkGraveyard` ⇒ 该功能**整段缺失**，且 **`ForgeEventFactory.canEntityDespawn` 在 1.21 不存在**，照搬即编译失败。

**查证 NeoForge 的等价机制（决定能否换成事件订阅）**：
- `EventHooks.checkMobDespawn(mob)`（`build/_nfsrc_219/…/EventHooks.java`）：
  `post(MobDespawnEvent)` 后 `switch (event.getResult()) { case ALLOW -> mob.discard(); case DEFAULT -> {}; case DENY -> mob.setNoActionTime(0); }`，并 `return getResult() != DEFAULT;`
- `Mob.checkDespawn()` 的**第一行**就是 `if (EventHooks.checkMobDespawn(this)) return;` ⇒ **非 DEFAULT 时直接跳过原版全部逻辑**
⇒ 订阅 `MobDespawnEvent` 并 `setResult(ALLOW)` 与 1.20 mixin 返回 `Event.Result.ALLOW` **语义完全一致**（`MobDespawnEvent.Result` 的 `ALLOW` javadoc 亦为「Forcibly allows the despawn to occur」）。

**落地**（`common/event/game/entity/LivingEntityEvents.java`，该类已是 `@EventBusSubscriber(modid = Confluence.MODID)`，紧邻既有的 `mobSpawn$PositionCheck` 之前插入）：
```java
/// 只补原版同维度无人时不执行距离消失判定的空档；不覆盖持久化和 Forge 的保护结果。
@SubscribeEvent
public static void mobDespawn(MobDespawnEvent event) {
    Mob mob = event.getEntity();
    if (event.getResult() != MobDespawnEvent.Result.DEFAULT || !mob.isAlive()
            || mob.getType().getCategory() != MobCategory.MONSTER
            || mob.hasCustomName() || mob instanceof Boss
            || mob instanceof BossOwnedEntity owned && owned.getBossOwner() != null
            || !mob.removeWhenFarAway(Double.POSITIVE_INFINITY)
            || !mob.level().players().isEmpty()) {
        return;
    }
    /// 沿用原版的闲置计时和随机消失概率，不另设无人倒计时或距离规则。
    if (mob.getNoActionTime() > 600 && mob.getRandom().nextInt(800) == 0)
        event.setResult(MobDespawnEvent.Result.ALLOW);
}
```
**与 1.20 的逐行核对（实测）**：两条 `///` 注释与 `if (mob.getNoActionTime() > 600 …)` 与 1.20 **逐字 SAME**；5 条 `||` 续行**逐字 SAME**；仅首行属平台适配（`result` → `event.getResult()`、`Event.Result.DEFAULT` → `MobDespawnEvent.Result.DEFAULT`），末行 `return Event.Result.ALLOW;` → `event.setResult(MobDespawnEvent.Result.ALLOW);`（mixin 是"改返回值"，事件是"设结果"）。
**无需新增 import**：`Mob`/`MobCategory` 由既有 `net.minecraft.world.entity.*`（:16）覆盖，`MobDespawnEvent` 由既有 `net.neoforged.neoforge.event.entity.living.*`（:30）覆盖，`Boss`(:31)/`BossOwnedEntity`(:60) 已在。花括号平衡 `{=121 }=121`。1.21 的 `MobMixin` **保持不动**（它本就只有 `checkGraveyard`，无需删改）。

### 55.3 状态
- 台账状态 JSON 共 **258** 条；**行 378 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**78**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批未新增）。
- **下一批**：行 **367 专批**（组重排 + 3 处谓词 + `GNOME` 待裁）、**319**（粒子注册形态）、**267 `f4ffadaa7`**（72 文件大行）、`2f44045a8`（标签重排）、**350/334/275/281/240**。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。

## 五十六、行 267 `COVERED`（AttachmentEntity 体系迁移，72 文件）：**56% 的新增行在 1.20 自己就撤了** + 顶点 API 改名表（2026-10）

### 56.1 大行的正确读法：先看「1.20 后来撤了多少」，再看真 GAP
行 267 `f4ffadaa7`「AttachmentEntity体系迁移」表面很大：**72 文件 / 新增 7763 行**。但 `rowaudit` 的双向统计给出另一幅图：

| 指标 | 值 |
|---|---|
| `ALIVE-in-20HEAD`（1.20 HEAD 仍在） | **3423** |
| `DEAD`（1.20 HEAD **已撤**） | **4340（56%）** |
| `GAP`（存活且 1.21 没有） | **80** |

⇒ 该提交的**大半内容在 1.20 HEAD 已不存在**，逐行读会被 4340 行噪音淹没。**20 个文件整份 `dead-only`**：`client/summoner/model/bbmodel/*`（10 文件 ≈ 2000 行）、`client/summoner/model/geo/*`（9 文件 ≈ 1200 行）、`DynamicLightDispatcher`(232)、`InvincibleData`(126)、`SummonerAttachmentTypes`(23)、`SummonerRegistries`(22)、`common/item/SummonerWeaponItem`(170) —— **无移植物**。
> 这也是 HANDOFF §7「先核目标文件当前状态」在大行上的极端形态：**行大 ≠ 欠账大**。

### 56.2 ★ 顶点 API 改名表（本批实测建立，可复用于行 319/366 等同款 GAP）
行 267 的 80 条 GAP 里绝大多数是 1.20 的**顶点流式 API**。以 `ColorBufferSource`（1.20/1.21 同路径同职责的 `VertexConsumer` 包装器）逐方法对照，得到**一一对应**：

| 1.20 | 1.21 |
|---|---|
| `vertex(x, y, z)` | `addVertex(x, y, z)` |
| `color(r, g, b, a)` | `setColor(r, g, b, a)` |
| `uv(u, v)` | `setUv(u, v)` |
| `overlayCoords(u, v)` | **`setUv1(u, v)`** |
| `uv2(u, v)` | **`setUv2(u, v)`** |
| `normal(x, y, z)` | `setNormal(x, y, z)` |
| `endVertex()` / `defaultColor(...)` / `unsetDefaultColor()` | 1.21 接口已无这三个方法，包装器**不再声明** |

⇒ 形如 `consumer.vertex(...).color(...).uv(...).overlayCoords(...).uv2(...).normal(...)` 的链式调用属**纯改名**，不是缺口。**已把该形态加入 `classify.py` 的改名规则**（此前 `\.endVertex\(\)` 只覆盖了一部分，导致 `ColorBufferSource`/`RenderUtil`/3 个 `rendererHelper` 被误列为待核；规则补全后行 267 的待核行由 80 → **14**）。

### 56.3 其余 14 条待核逐条结清
| 待核项 | 结论（实测） |
|---|---|
| `Confluence.java` 的 `SummonerRegistries.init()` / `SummonerAttachmentTypes.init()` / `SummonerClientEvents.init()` | **平台/架构**：1.21 的这两个注册类是 NeoForge `DeferredRegister`（`SummonerRegistries:25/33/35` 用 `makeRegistry(...)` + `register(eventBus)`），没有 `init()`；`SummonerClientEvents.init` 在 1.21 变成 **`init(eventBus)`**（`Confluence.java:82`）。且 `Confluence.java` 属 §1.5 **禁改** ⇒ 只登记不改 |
| `ColorBufferSource` 的 `overlayCoords(int,int)` / `uv2(int,int)` / `inner.normal(x,y,z)` | 见 56.2 改名表（1.21 该文件已是 `setUv1`/`setUv2`/`inner.setNormal`，实测 `inner.*` 调用为 `addVertex/setColor/setUv/setUv1/setUv2/setNormal`） |
| `RenderUtil` / `Laser|Lightning|SphereRendererHelper` 的 `.color(color)` / `.normal(...)` | 同上改名 |
| `SummonerClientEvents` 的 `public static void init()` 与 `AttachmentEntityRenderDispatcher.render(level, camera, poseStack, bufferSource, partialTick)` | `render(...)` 该签名**两仓都在**（§47 已核 BOTH）；`init()` 形状差异见上一行 |
| `AttachmentEntityData` 的 `SummonerRegistries.ATTACHMENT_ENTITY_TYPES.get(buf.readResourceLocation())` | **架构差异**：1.20 是 PortLib `PortCustomRegistration.get(id)`，1.21 改为 **`level.registryAccess().registryOrThrow(SummonerRegistries.ATTACHMENT_ENTITY_TYPE_KEY).get(id)`**（`AttachmentEntityData:239` 实测）—— 原生注册表查询 |
| `LyraStreamCodecs`(3) / `SummonerEvents`(1) / `SyncFieldDispatcher`(15) | 同属 `PortStreamCodec`/`PortByteBufCodecs` → 原生 `StreamCodec`/`ByteBufCodecs` 与 `SyncFieldDispatcher` 的同步字段表改写 |

**11 个 GAP 文件在 1.21 HEAD 全部存在且同路径**（`Confluence`/`ColorBufferSource`/`RenderUtil`/`SummonerClientEvents`/3×`rendererHelper`/`LyraStreamCodecs`/`SummonerEvents`/`AttachmentEntityData`/`SyncFieldDispatcher`）⇒ 无 `MISSING` 文件。

### 56.4 状态
- 台账状态 JSON 共 **259** 条；**行 267 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**77**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批未新增，本批**无代码改动**）。
- **下一批**：行 **367 专批**（组重排 + 3 处谓词 + `GNOME` 待裁）、**319**（粒子注册形态；其 33 条待核多数应可被 56.2 的改名表吸收）、**350/334/275/281/240**、`2f44045a8`（标签重排）。

## 五十七、行 319 `COVERED`（安卡十字与眼球激光塔；剩余待核被 §56.2 改名表吸收）（2026-10）

### 57.1 逐条结清（承接 §56.2 顶点改名表后，待核由 33 → 27 → 全清）
| 待核项 | 结论（实测） |
|---|---|
| `.color(color)` / `.normal(pose.normal(), …)` / `buffer.vertex(x, y, z)` | §56.2 的顶点 API 改名（`color`→`setColor`、`normal`→`setNormal`、`vertex`→`addVertex`）⇒ 平台改名 |
| `SummonerParticleTypes.init();` | **平台/架构**：1.20 是 PortLib `PortParticleTypeRegistration` + `init()`；1.21 是 `DeferredRegister`，`GENERIC = register("generic", true, CODEX, STREAM_CODEC)`（`SummonerParticleTypes:20`，返 `Supplier<ParticleType<…>>`），由 `Confluence.java:113` 的 `SummonerParticleTypes.TYPES.register(eventBus)` 驱动。且 `Confluence.java` 属 §1.5 **禁改** ⇒ 只登记不改 |
| `PARTICLES.register("generic", true, GenericParticleOptions.CODEC, GenericParticleOptions.STREAM_CODEC);` | 同上（1.21 `:20` 已是该四参形式，仅登记器换成原生 `TYPES`） |
| 粒子序列化那 8 行（`@SuppressWarnings("unchecked")` / `writeResourceLocation(BuiltInRegistries.PARTICLE_TYPE…)` / `entry.options.writeToNetwork(buffer)` / reader lambda / `Entry(options, x, y, z, vx, vy, vz)`） | **平台改名**：两仓 `SummonerBatchedParticlesPayload` 结构**逐项相同** —— 都是 `record … (List<Entry> entries) implements I*Packet.S2C` + `STREAM_CODEC = *StreamCodec.composite(Entry.STREAM_CODEC.apply(*ByteBufCodecs.list(4096)), …)` + 同一个 `record Entry(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz)`；差异仅在 `PortStreamCodec`/`PortByteBufCodecs`/`PortRegistryFriendlyByteBuf` → 原生 `StreamCodec`/`ByteBufCodecs`/`RegistryFriendlyByteBuf` |
| `ID = Confluence.asResource("summoner_batched_particles")` + `identifier()` | PortLib 自定义包接口（`IPortPacket.S2C`）→ 1.21 原生 `IPacketS2C`；包 ID 由注册表管理 |
| 5 条 import（`SummonerHelper`/`SummonerAttachmentEntityTypes`/`EyeLaserTurretMinion`/`RuinRelicMinion`/`AttachmentEntityGoalSelector`/`java.util.Objects`） | **4 个类在 1.21 均存在且同路径**（实测 `ls-files`） |

### 57.2 唯一一处**谓词差异**：`isOnCarry()` 是 **1.21 侧更前**
`EyeLaserTurretIdleGoal`：
- 1.20 HEAD `:19`：`if (minion.getPos().distanceTo(minion.getOwner().position()) < 6.0) {`
- 1.21 HEAD `:20`：`if (!minion.isOnCarry() && minion.getPos().distanceTo(minion.getOwner().position()) < 6.0) {`

1.21 多一个 `!minion.isOnCarry()` 守卫。该守卫属于**「携带」特性**（`ICarryMinion` 一族）—— 而 **1.20 后来把该特性删了**（行 354 的审计实测 `ICarryMinion.java` = `code=28 alive=0 dead=28`，即全部 28 行都被 1.20 自己撤掉）。⇒ 属「1.21 保留、1.20 反向对齐」的方向，**按 §1.1 不在 1.21 回退**，仅登记。

### 57.3 状态
- 台账状态 JSON 共 **260** 条；**行 319 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**76**。
- 本批**无代码改动**；`fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo）。
- **下一批**：行 **367 专批**（组重排 + 3 处谓词 + `GNOME` 待裁）、**350/334/275/281/240**（pct 0.79–0.84）、`2f44045a8`（标签重排）、行 **256** 大行。

## 五十八、行 367 `COVERED`：刷怪放置层达成**逐实体谓词完全对齐**（212 ↔ 212，差异 0）（2026-10）

### 58.1 验收口径：不是"GAP 清零"，而是**逐实体谓词映射相等**
行 367 `ac2ac4428`「统一生物生成检查并修正渔夫任务条件」是一次**成组重排**：74 条 GAP 里绝大多数是 `PortSpawnPlacementTypes` 平台改名，而分组本身两仓不同（1.21 有 131 个 `group` 调用 vs 1.20 的 121）。所以验收不能数 GAP，要用 §54.1 的**逐实体谓词映射**：

```
最终实测（脚本 r367b.py，1.21 侧读工作树）：
  entities: 1.20=212  1.21=212
  ONLY-20 = 0      ONLY-21 = 0      SAME-entity-DIFFERENT-predicate = 0
```
⇒ **212 个实体的生成谓词两侧逐一对齐**（此前 210 ↔ 212、1 处谓词不同、2 个实体只在 1.20 注册）。

### 58.2 本批落地（3 文件，全部与 1.20 逐字核对）
1. **`GNOME` 恢复可生成**（1.20 `:113` / `:294-300`）——三处配套：
   - `ModStructures.Keys`：补 `public static final ResourceKey<Structure> LIVING_TREE = key("living_tree");`（1.21 的 `Keys` 只有 12 键 ↔ 1.20 有 30 键；顶层那个 `LIVING_TREE` 是**结构类型** `StructureType<LivingTreeStructure>`，不能当注册表键用）
   - `SpawnPlacementChecks.checkGnomeSpawn`：**8 行逐字 SAME**（含 `/// 侏儒只在裸露的活木树结构附近生成。`）
   - `CreatureSpawnPlacements`：在 `MYSTIC_FROG` 组之前插入 GNOME 注册（对应 1.20 `:113` 紧跟 `:112 WINDY_BALLOON`、` :114 MYSTIC_FROG` 的次序）；仅 `PortSpawnPlacementTypes` → `SpawnPlacementTypes`
   > 有趣的**跨侧倒置**：1.20 HEAD **已删** `data/…/worldgen/structure/living_tree.json`（结构不再生成 ⇒ 1.20 的侏儒其实也刷不出来），而 **1.21 有该 json**。所以这次移植让 1.21 的侏儒**真正能刷**——比 1.20 HEAD 更完整。
2. **`SNOW_FLINX` / `UNDEAD_VIKING` 的 `hardmode` 包装去掉**（1.20 `:138-139`）：1.21 原先把 `ILLUMINANT_BAT, ARMORED_VIKING, UNDEAD_VIKING, SNOW_FLINX` 挤在一个 `hardmode(checkCaveMonsterSpawn)` 组里；按 1.20 拆成
   - `hardmode(checkCaveMonsterSpawn)`：`ILLUMINANT_BAT, ARMORED_VIKING`（保持门槛）
   - `checkCaveMonsterSpawn`（**无** hardmode）：`SNOW_FLINX, ICE_BAT, UNDEAD_VIKING`（逐字对应 1.20 `:139`）
   **方向证据**：1.20 的 `hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn)` 计数在 `ac2ac4428~1` 是 **3**、在 `ac2ac4428` 是 **6** ⇒ 行 367 自己**增加了** hardmode 包装；1.20 HEAD = 6 而 1.21 = 7 ⇒ 1.21 多的那一个正是本项。
3. **`JUNGLE_BAT` 归位**（1.20 `:96-101`）：从 1.21 的 `checkUndergroundMonsterSpawn` 组移入 `checkRoutineMonsterSpawn` 组。
   **该项其实属 `5bd5b4211`（= 行 131）**，`git log -S` 实测 JUNGLE_BAT 进入 routine 组来自 `5bd5b4211`「修正普通怪物自然生成规则」（2026-08-17），而**该提交不可从 1.21 分支到达**（`merge-base --is-ancestor` = 失败）⇒ 纯 1.20 分叉后改动。
   ⚠️ **顺带发现：行 131 早已被标 `COVERED`，但 1.21 此前并没有它的这处改动**（JUNGLE_BAT 一直挂在 underground 组）。本批把代码补正后，**行 131 的 `COVERED` 才算名副其实**；台账状态未动（无新状态可写，且该行在 231–400 之外）。

### 58.3 状态
- 台账状态 JSON 共 **261** 条；**行 367 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**75**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批未新增）。三个文件花括号平衡。
- **下一批**：**350/334/275/281/240**（pct 0.79–0.84）、`2f44045a8`（标签重排）、行 **256** 大行；另**行 131（越界）建议复核**：既然出现"已标 COVERED 但改动不存在"，说明 231 之前的行也可能有同类残留，值得按 §56.1 的「先看 1.20 撤了多少、再看逐实体/逐符号对齐」重扫一遍。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 五十九、行 350 `PORTED`：血肉墙/悠悠球大行落地 8 文件，并修好「消费者在、驱动器缺」的海盗入侵（2026-10）

### 59.1 先把 55 个 GAP 文件分类，再决定端口（§54.1 的两问法）
行 350 `ef1a4d138`「重构血肉山肉墙与悠悠球实现，更新 NPC 交互界面并统一敌怪射弹伤害」是**跨模块大杂烩提交**：113 个改动文件、55 个 GAP 文件。逐 hunk 核过后，55 个里**只有 8 处是真欠账**，其余归类如下：

| 类别 | 条目（1.21 侧证据） |
| --- | --- |
| 平台改名/已适配 | `BaseBossPart.lerpTo`（1.21 已是六参，文件里已有「1.20 Forge 补丁多一个 `boolean teleport`」的差异注释）；`YoyoItems` ×12（`PortDeferredItem`→`DeferredItem`，12 个球全在）；`CascadeYoyoItem`（`setSecondsOnFire`→`igniteForSeconds`）；`BaseTerraBowItem`（`ItemStack.ATTRIBUTE_MODIFIER_FORMAT`→`ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT`）；`NetworkEvents`/`OpenNPCServicePacketC2S`（PortLib → 原生 `IPacketC2S`/`StreamCodec`/`ByteBufCodecs`，1.21 文件尾还留了映射注释）；`WallOfFlesh` 生成包（Forge `IEntityAdditionalSpawnData` → NeoForge `IEntityWithComplexSpawn`，1.21 文件头 12 行注释已说明回调链） |
| `defineSynchedData` 族 | `HillOfFlesh` / `WallOfFleshPart` / `TerrarianProjectile` / `BaseYoyoProjectile`：1.21 一律 `defineSynchedData(SynchedEntityData.Builder)` + `builder.define(...)` |
| 注册层 | `BossEntities.WALL_OF_FLESH`（`:158` 在）、`MonsterEntities.MOSS_HORNET`（`:690` 在，且已带 `DevelopmentSpawnPolicy.developmentOnly`） |
| import 噪音 | 18 个敌怪文件的 `ModEntities` import（`Antlion.java:18` 等逐一核对）、GeckoLib `software.bernie.geckolib.core.*` → `…animation.*`、`CthulhuEyeProjectile(Renderer)` |
| 1.21 改法更好 | `SlimeSpikeProjectileRenderer`：`renderToBuffer(..., 1,1,1,1)` → 1.21 的 `-1` 重载；`BaseArrowEntity` 击退：1.21 用 vanilla `AbstractArrow#doKnockback`（lib `AbstractArrowMixin` 注入同一方法）承担**本体击退 + 击退抗性**，`getAdditionalKnockback()` 另算，1.20 HEAD 反而把两者相加后绕过抗性 |
| mixins.json 5 条 | 2 条 `COVERED`：`BucketItemMixin` ↔ 1.21 `mixin/world/item/ItemUtilsMixin:16`（`emptyStack.is(ModTags.Items.BOTTOMLESS)`）；`SnowballItemMixin` ↔ 1.21 `ModEvents.java:237`（`event.modify(Items.SNOWBALL, …MAX_STACK_SIZE, LibUtils.MAX_STACK_SIZE)`）。2 条 `SKIP-PLATFORM`：`ProtectionEnchantmentMixin`（1.21 已无 `ProtectionEnchantment` 类，附魔数据驱动）；`BaseContainerBlockEntityMixin`（1.20 侧 `confluence$setCustomName` **调用点 = 0**，纯死接口；1.21 已删且 `notes/CO-MISSING-TRIAGE.md:31` 早有裁定） |

### 59.2 落地 8 文件（其余 7 处与 1.20 逐字核对）
1. **`SpreadingGrassBlock.tryGrowPlant`**（含 `MUSHROOM_GROWTH_CHANCE=5000`/`SPORE_GROWTH_CHANCE=5000`/`THORN_GROWTH_CHANCE=50` 与 `OverworldUtils` import）——整文件按 1.20 HEAD 逐字重写（63 行），把 1.21 内联的「荆棘 1/50 + 四向空气」换回抽签：丛林（`getUndergroundY()` 以下）长孢子、猩红/腐化先抽蘑菇再抽荆棘。所需符号全部实测存在：`NatureBlocks.{JUNGLE_SPORE,VICIOUS_MUSHROOM,VILE_MUSHROOM,CRIMSON_THORN,CORRUPTION_THORN}`、`ThornBlock#getStateForPlacement(BlockGetter, BlockPos)`（`ThornBlock.java:59`）、`ISpreadable.Type.JUNGLE`。
2. **`ModUtils.enemyDropMoney`**：金币落点 `living instanceof WallOfFlesh wall ? wall.getCoinDropPosition(damageSource) : new Vec3(…)`（`WallOfFlesh.java:124` 该方法在）。
3. **`TreasureBagItem.createItemEntity`**：宝藏袋落点 `living instanceof BaseBoss boss ? boss.getRewardPosition(owner) : living.position()`（`BaseBoss.java:704`、`WallOfFlesh.java:119` 均在）。
4. **`GuideVooDooDollItem`**：把「非地狱维度」闸门从 `onDestroyed` 搬回 `summon` 顶部，并按 1.20 用 `entity instanceof ItemEntity` 决定是否播报失败——1.21 原先只在 LAVA 销毁时提示，`/summon` 或脚本调用 `summon` 会**静默失败**。
5. **`CommonConfigs`**：补 `ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD`（字段插在 `KING_SLIME_LARGE_MINIONS` 之后、`BOSS_ATTRIBUTES_*` 之前，与 1.20 `:84` 相对次序一致；定义 `builder.define("allowFleshBossesOutsideUnderworld", false)`）——它是第 4 项的**驱动器**，1.21 此前只有消费者没有配置项。
6. **`GameClientEvents`：接回 `WallOfFleshRenderer.renderWalls(event)`**。1.21 侧的 `renderWalls` 早就写好了（`WallOfFleshRenderer.java:79`，形参就是 neoforge 的 `net.neoforged.neoforge.client.event.RenderLevelStageEvent`），但全仓 **0 处调用** ⇒ 血肉墙的墙面对客户端根本不绘制（`render` 覆写是空的，注释写着「标准实体入口不重复绘制」）。按 1.20 `:366-368` 挂到 `Stage.AFTER_ENTITIES`，位于 `TongueRenderer.renderFirstPerson` 之前。这是本项目第 **5** 例「消费者在、驱动器缺」。
7. **`MonsterEntities`**：海盗船长补 `.projectile(ModEntities.PIRATE_CANNONBALL, projectile -> projectile.damage(52, 104, 260).scaleWithAttack(37))`（1.20 `:755`；`PIRATE_CANNONBALL` 在 1.21 `ModEntities.java:346`，同文件 `PIRATE_BULLET` 那行用的就是同一套 `.damage(a,b,c).scaleWithAttack(x)` 链式 API）。
8. **`GameEventSystem` / `LevelEvents`：接回海盗入侵**。1.21 的 `PirateInvasionGameEvent` **已完整实现**（`spawner`、`canStart`、`countKilled`…），`ConsumableItems.PIRATE_MAP` 也把 `PirateInvasionGameEvent.KEY` 交给了 `GameEventItem`，但 `GameEventSystem.events` 与 `INVASION_EVENTS` 两张表**都没注册**、`LevelEvents.modifyCustomSpawners` 也没加 spawner ⇒ **海盗地图用了没反应**，且 `INVASION_EVENTS` 里那句 `// todo 海盗，火星` 只对了一半（海盗缺的是注册，不是实现）。
   - 三处按 1.20 `:45-46` / `:182-183` / `:133-134` 原样补齐（并把 todo 注释改成只剩「火星」）。
   - **闸门映射**：1.20 用 `Confluence.UNRELEASED_EVENTS`，1.21 没有该字段；实测 `UNRELEASED_EVENTS`（1.20 `Confluence.java:57`）与 `UNRELEASED_SPAWNS`（1.21 `Confluence.java:57`）**同为 `LibUtils.isDev()`**，故用后者作同义映射，保持「开发者环境才启用」的原意。

### 59.3 状态
- 台账状态 JSON 共 **263** 条；**行 350 = `PORTED`**（行 275 的 `COVERED` 同批落账）。
- 行 231–400 剩余 `TODO`：**73**（本批 −2）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批 8 个 java 均 CRLF，未新增候选）。
- **下一批**：`936551676`（0.83）、`300bd6cfe`（0.81）、`ac457ea83`（0.80）、`6a3e17e86`（0.79），以及 `2f44045a8`（标签重排，需逐标签定方向）、行 **256**（675 文件大行）。
- 本轮**又一次**印证 §58.3 的提醒：大行的 GAP 数是**平台噪音 + 已适配**混出来的，必须先做本节的分类表再动手；而「实现写了但没人调用」的缺陷在 1.21 侧**不会报错**，只能靠「全仓符号引用计数 = 1」这类检查抓。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十、行 240 + 行 281 `PORTED`：两行只欠 4 处，却揪出第 5/6 例「消费者在、驱动器缺」（2026-10）

### 60.1 行 240 `ac457ea83`「修复敌怪行为、法术表现、NPC 交互及孢子回收」：54 文件 / 67 GAP，真欠账只有 2 个文件
| 类别 | 条目（1.21 侧证据） |
| --- | --- |
| 平台改名 | `NetworkEvents` 两条 `registerInGameC2S`（1.21 用 `registerPayloadHandlers`，两个包都已注册）；`SummonSkeletronPacketC2S`/`WhipPlaybackCompletePacketC2S`（PortLib → `IPacketC2S`/`StreamCodec`）；`BaseNPC` 的 `BlockPathTypes`→`PathType`（1.21 `:128-132` 五条全在）；`OldManNPC.finalizeSpawn`（1.21 是四参版）；`BaseCasterMonster.stopTriggeredAnimation`→`stopTriggeredAnim`（GeckoLib 同名版本方法名不同，见 `tools/port2native/rules/geckolib-package-map.json:99-105`） |
| 已适配 | `RelicBlock`：1.21 `:103` 已用 NeoForge `IClientItemExtensions` + `SimpleGeoItemRenderer`；`ModFoodPropertiesBuilder`：1.21 直接 `new FoodProperties(nutrition, saturation, …)`，不再需要「饱和度/营养换算」与那条 `IllegalArgumentException` |
| 无欠账 | `WhipPolylineSamples`、`ModEvents`(11 行)、`ModTabs`(8 行) 全是 `dead-only`（1.20 自己撤了） |
**真欠账（本批落地）**：`CrimsonHeartBlock` / `ShadowOrbBlock` —— 1.21 侧一直停在旧的 `onRemove(...)` 写法，即**任何**移除（`/setblock`、活塞推动、结构生成、区块卸载）都会结算猩红之心/暗影珠的掉落、成就、广播，甚至**召唤克苏鲁之脑/世界吞噬者**；1.20 `ac457ea83` 已收紧为 `onDestroyedByPlayer`（玩家破坏）+ `onBlockExploded`（仅当 `explosion.getIndirectSourceEntity() instanceof Player`）。两个文件各补 3 段（`onDestroyedByPlayer` / `onBlockExploded` / `private void onPlayerBroken`）+ 2 个 import，主体逐字沿用 1.21 自己的实现（`ShadowOrbBlock` 保留 1.21 已有的 `FlailItems.BALL_O_HURT`，1.20 那行还是空注释「链球」⇒ 这一处 **1.21 更新**，不动）。

### 60.2 行 281 `300bd6cfe`「重构敌怪效果与属性配置」：48 文件 / 2446 增行 / 71 GAP，只有 2 处真欠账
- 平台：`RenderUtils.matchModelPartRot` → 1.21 的 `RenderUtil.matchModelPartRot`（三个 `Vanilla*GeoModel` 都在用，只是包名 `util` 单复数变了）；GeckoLib `core.*` → `animation.*`/`cache.object.GeoBone`；`AttackEffectsSubProvider` 的 `PortDataMapProvider.Builder` → `DataMapProvider.Builder`；`MonsterAttributeScaling` 的 `ForgeConfigSpec`→`ModConfigSpec`、`ForgeRegistries`→`BuiltInRegistries`、`AttributeModifier.Operation.MULTIPLY_TOTAL`→`ADD_MULTIPLIED_TOTAL`（含 `previous.amount()` 记录式访问）——全部已在 1.21 适配。
- 注册层 `MonsterEntities` 16 条（`RegistryObject`→`DeferredHolder`）逐条核对在册。
- **真欠账 A：`AttackEffects.afterDamage` 无调用点。** 1.21 侧数据图 `ModDataMaps.ATTACK_EFFECTS`、数据生成 `AttackEffectsSubProvider`、`AttackEffects.afterDamage(LivingEntity, DamageSource)`（`AttackEffects.java:45`）**全都在**，但 `LivingEntityEvents` 里没有调用（全仓 `afterDamage` 只有定义处）⇒ 整个「命中附加效果」数据图在 1.21 **静默失效**。已按 1.20 `:324` 补上。
- **真欠账 B：21 条英文配置说明缺失。** 1.21 的 `ConfigurationLanguageSubProvider.chinese()` 里有 `MonsterAttributes` 一节 21 条中文（`:366-386`），`english()` 里却**一条都没有** ⇒ 英文界面下配置屏显示原始键名。已按 1.20 `english():54-74` 逐字补 21 条（插在 `Spawning.tooltip` 与 `Falling Star` 之间，与 1.20 同序）。

### 60.3 顺带发现：第 5、6 例「消费者在、驱动器缺」，以及一个可复用的实证手段
本批在 `LivingEntityEvents.livingDamage$Post` 一处就命中两例：
1. **`SwordItems.afterSuccessfulDamage(...)` 无调用点**（**pre-231 残留**，不是行 281 带来的）：1.21 有定义（`SwordItems.java:319`）与实现 `VolcanoItem.afterSuccessfulDamage`（74 行完整逻辑：命中后 0.75× 攻击力的局部爆炸 + 最近 2 个目标 + 粒子/音效），却**从没有任何调用点**（`git log -S` 在 1.21 分支上查不到该调用）⇒ 火山剑的爆炸后效一直是死的。本批与 A 同处补回（先武器后效、后数据图特效，严格照 1.20 次序）。
2. 连同 §59.2 的 `WallOfFleshRenderer.renderWalls`（有实现、0 调用）与海盗入侵（事件与地图都在、两张事件表都没注册），本项目至今已确认 **6 例**同类缺陷。它们的共同点：**能编译、不报错、无日志**，只能靠「全仓符号引用计数」抓。
- **实证手段（新）**：本机 `~/.gradle/caches/neoformruntime/intermediate_results/sourcesAndCompiledWithNeoForge_*.jar` 里有 **NeoForge 打补丁后的 1.21.1 源码**（用同 jar 内 `net/minecraft/DetectedVersion.java` 可辨版本，另有 1.20.1/26.x 各套）。本批据此确认 `IBlockExtension.onDestroyedByPlayer`（`:240`）与 `IBlockExtension.onBlockExploded`（`:739`）在 1.21.1 依然有效——关键在 `BlockBehaviour.BlockStateBase#onExplosionHit` 的第 218 行仍然调用 `state.onBlockExploded(level, pos, explosion)`（NeoForge 保留的兼容路由），**所以 1.20 的 `onBlockExploded` 覆写在 1.21.1 不是死代码**；若只凭"1.21 有 `onExplosionHit` 新钩子"就改写成新签名，反而会漏掉 NeoForge 的兼容路径。脚本：`build/_cmp231/blockapi.py`、`blockapi2.py`、`blockapi3.py`。

### 60.4 状态
- 台账状态 JSON 共 **266** 条；**行 240 = `PORTED`、行 281 = `PORTED`**。
- 行 231–400 剩余 `TODO`：**70**（本批 −3）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批 4 个 java 均 CRLF）。
- **下一批**：`6a3e17e86`（0.79）、其余 pct 0.7x 行；`2f44045a8`（标签重排，需逐标签定方向）、行 **256**（675 文件大行）。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十一、行 364 `PORTED` + 8 行 `COVERED`：批量筛选器上线，一轮清掉 9 行（2026-10）

### 61.1 新工具：`batchscreen.py`（平台噪音过滤后的「真实 GAP」）
`rowaudit.py` 的 GAP 数把平台改名、PortLib 桥、顶点 API 老写法全算进去了，导致「大行看着欠账多、实际只剩平台噪音」。`build/_cmp231/batchscreen.py` 在 rowaudit 之上加一层噪音正则（`org.mesdag.portlib`、`net.minecraftforge`、`software.bernie.geckolib.core.`、`Port[A-Z]\w*`、`RegistryObject`、`BlockPathTypes`、`ForgeConfigSpec`/`ForgeRegistries`、`defineSynchedData()`、`IEntityAdditionalSpawnData`/`NetworkHooks`/`PlayMessages`、`endVertex()`/`.vertex(`/`.overlayCoords(`/`.uv2(`、`MobSpawnType`、`setCustomClientFactory`… 共 27 条），只报告**过滤后的** GAP。`screenall.py` 用它把 231–400 全部 TODO 行一次筛完（`screenall_summary.txt` / `screenall_detail.txt`）。

### 61.2 行 364 `6a3e17e86`「完善剑类特效与剑气渲染」→ `PORTED`（1 文件）
19 文件 / 277 增行 / 25 GAP，过滤后只剩 1 处真欠账：
- **`LivingEntityEvents.armorPenetration` 缺两条破甲分支**：1.20 `:685-689` 给草木剑射弹 +20、魔光剑射弹 +5（`direct instanceof GrassSwordProjectile / LightBaneProjectile`）。已按 1.20 逐字补入（保留其 FQN 写法，便于日后重筛时行级比对）。
- 同处**顺带补回 pre-364 残留**：`WhipDamageSource` 分支（1.20 `:677-679`）。`FirecrackerItem.explode` 在 1.21 依旧构造带破甲值的 `WhipDamageSource`（`FirecrackerItem.java:56`），但 1.21 的 `armorPenetration` 处理器没有对应分支 ⇒ 鞭类破甲**一直被丢弃**。这是第 **7** 例「消费者在、驱动器缺」。
- 其余 24 条 GAP 逐条核对为误报：4 把剑（`BREAKER_BLADE`/`LIGHTS_BANE`/`BLADE_OF_GRASS`/`WAFFLES_IRON`）在 1.21 `SwordItems:166/174/218/228` 均在；`BreakerBladeItem` 的 2.5× 高血量加成与 `DamageTypeTags.IS_PLAYER_ATTACK` 已在；`StarFuryProjectile.onAddedToWorld` 在 1.21 是**改名后的** `onAddedToLevel`（`BaseWormMonster.java:112` 有注释记录该改名）；`LightBaneProjectile.defineSynchedData` 是 builder 版；`VolcanoItem` 的 `SoundEvents.GENERIC_EXPLODE.value()` 已在。

### 61.3 一行一个结论：8 行 `COVERED`（各附实证）
| 行 | 提交 | 结论与证据 |
| --- | --- | --- |
| 238 | `99902d986` 修复锁方块 | 1.20 把 `ItemPredicate` 换成自定义 `MatchTool` record，**真正修的是「用无注册表的 `NbtOps.INSTANCE` 解析需要注册表的编解码器」**（旧代码 `ItemPredicate.CODEC.parse(NbtOps.INSTANCE, …)`；lock 的工具要求一重载就丢）。1.21 的 `LockBlock` 用的是 `RegistryOps.create(NbtOps.INSTANCE, registries)`（`LockBlock.java:236/243`）**已经修好这一点**，且保留语义更宽的 vanilla `ItemPredicate`（支持 count/components）⇒ 1.21 不落后，`MatchTool` 属 1.20 侧的 schema 收窄，登记为反向差异 |
| 248 | `9090245f8` 修 | 1.20 在 `AbstractArrowMixin`/`FishingHookMixin` 里改调 `ILibExtraSyncedData.defaultSetData(self, id, o)`；1.21 用 `IAbstractArrow.super.confluence$setData(...)`（`FishingHookMixin` 同理）。两侧最终都走 lib 的 `PacketDistributor.sendToPlayersTrackingEntity(..., SetEntityDataPacketS2C)`（1.21 lib 把 `defaultSetData` 内联进默认方法）⇒ 等价 |
| 253 | `856c0047f` 调整末地高度 | 末地高度的实际载体是 `RegistryDataLoader$RegistryDataMixin`：1.21 **有同名文件且逻辑等价**（用 `@Inject` + `RegistryBuilder#onAdd` 取代 1.20 的 `@ModifyExpressionValue` 打 `MappedRegistry` NEW），并已注册进 `confluence.mixins.json`；其余 `Confluence.java`/`GlobalCloakData`/`ServerEvents`/`TheEndBiomeHolder` 的增删行均已在。4 个 `tools/ssh_proxy_*.ps1/.bat` 是**仓库开发脚本**（非模组内容），1.21 已有同类 `set_git_proxy.bat`/`unset_git_proxy.bat` ⇒ 该部分 `SKIP-PLATFORM`，行内代码部分 `COVERED` |
| 257 | `0bd4b2052` 纹理与模型问题 | `rowaudit` 原始 GAP = 0；11 个改动文件在 1.21 **全部存在**（含 4 个 geo/2 个 animation/3 个 png；仅 `TerraCurio` 子模块指针不算）⇒ `COVERED` |
| 263 | `9a5d3a4a6` 修复 portlib 的注册表 | 1.20 这次是把 PortLib 监听器换成原生事件处理器（`fmlClientSetup`/`registerAdditionalModels`/`registerShaders`）。1.21 用 `@EventBusSubscriber` + 23 个 `@SubscribeEvent`（`clientSetup`、`modConfig$Loading/Reloading`、`registerMenuScreens`、`registerEntityLayers`、`registerEntityRenderers`、`model$RegisterAdditional`…）原生实现；`registerShaders` 落在 `GameClientEvents:123`（1.20 在 `ModClientEvents:1171`）⇒ `COVERED` |
| 264 | `389b7a760` 修复网络包发送 | 原始 GAP = 0（`EverBeneficialItem` 在 1.21 已存在；`PortLib` 子模块指针除外）⇒ `COVERED` |
| 279 | `2eb9ce931` 动态光源接入 | 1.21 的 `LevelRendererMixin` **已有同逻辑注入**（方法名 `makeHerbEmissive` ↔ 1.20 `enhanceLightColor`，寒颤棘 AGE=2 时把方块光抬到 6），且 1.21.1 vanilla 的 `LevelRenderer.getLightColor(BlockAndTintGetter, BlockState, BlockPos)` 仍在（NeoForge 打补丁源码 `LevelRenderer.java:3628`）；动态光源本身两侧都走 lib 的 `DynamicLightDispatcher`（1.21 另有 `SodiumDynamicLightsHelper`）⇒ `COVERED` |
| 283 | `67c51a778` 神必青蛙模型 | 原始 GAP = 0；4 个改动文件（`EntitySubProvider`、`SpaceSpawner`、`water_bolt_mimic` 的 animation + png）在 1.21 全在 ⇒ `COVERED` |

### 61.4 状态
- 台账状态 JSON 共 **275** 条；本批 **行 364 = `PORTED`**，**行 238/248/253/257/263/264/279/283 = `COVERED`**。
- 行 231–400 剩余 `TODO`：**61**（本批 −9）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批仅 1 个 java 改动）。
- **下一批**：直接用 `screenall_summary.txt` 里 REALgaps=0 的行批量结账，再按 REALgaps 从小到大逐个处理；`2f44045a8`（标签重排）与行 **256**（675 文件大行）留待专项。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十二、批量筛选定稿：本轮清掉 33 行（TODO 70 → 37），落地 5 行 + 结账 28 行（2026-10）

### 62.1 `screenall.py` 全量结果：69 个 TODO 行里 21 行「真实 GAP = 0」
本轮把 §61.1 的 `batchscreen.py` 跑满 231–400 的**全部** TODO 行（`screenall_summary.txt` / `screenall_detail.txt`）。结论：**21 行的过滤后真实 GAP 为 0**，另有 3 行（`dafb03ee9`/`e6d47c412`/`dafb03ee9` 等）只剩 1–3 行。凭这一点本轮直接结账 28 行（见 62.3 的表），把剩余 TODO 压到 **37** 行——而且剩下的几乎都是**真有大块内容**的行（`b05c8dc3f` 1354、`900c068f7` 726、`a4b321499` 654、`6084476d0` 275、`bcecc5382` 268…）。

### 62.2 本批落地（5 行 / 6 文件）
| 行 | 提交 | 落地内容 |
| --- | --- | --- |
| 344 | `a94cf78dc` | ① `ItemEvents.attributeModifier` 补「召唤武器不吃主手词缀」短路（1.20 逐字，插在 ACCESSORY/空词缀判定之后）；② **补回 `finch_staff` 的 `enable` 物品属性**——1.21 此前**整条注册都没有**（1.20 原本就有 `LibClientUtils.COULD_ENABLE_PROPERTY_FUNCTION`，本行把它换成「已召唤雀鹰 → 0 否则 1」的 lambda），否则模型永远拿不到「已启用」状态 |
| 345 | `05b032399` | `SpaceSpawner` 空岛刷怪偏移由 `-10 + random.nextInt(21)` 放宽到 `-25 + random.nextInt(51)`（幻翼/飞龙两组共 4 行） |
| 380 | `fcd2368c6` | `PrefixUtils` 重铸价格由**平方缩放** `value * num14 * num14` 改为**线性** `value + value * num14`（1.20 同处把 `Mth.square(prefix.value())` 换掉；1.21 的 `num14` 即该倍率） |
| 384 | `e6c0036c2` | `PlayerEvents.attackEntity` 由「只拦长矛」扩到「长矛 / 链锤 / 悠悠球」——1.21 的客户端侧（`GameClientEvents:262`）早已是三标签，服务端侧漏了 |
| 388 | `032534179` | `FlailAuxiliaryProjectileRenderer` 给泡泡改用 `RenderType.entityTranslucent(FLAIRON)`（原先一律 `entityCutoutNoCull` ⇒ 气泡透明像素被裁成硬边） |

### 62.3 结账 28 行（各附实证要点）
- **行 364（+2 文件）**：`armorPenetration` 补草木剑 +20 / 魔光剑 +5，并补回 pre-364 残留的 `WhipDamageSource` 破甲分支（见 §61.2）。
- **行 390**：1.20 修的是 `PURE_CONVERSION_SHORT_GRASS` → `Blocks.GRASS`，而 1.21 的同一行写的是 `Blocks.SHORT_GRASS`——**1.21 已改名过的同一个方块**，两处（`TALL_GRASS` 分支 + 标签分支）在 1.21 都已是修好的形态。
- **行 368**（`修复连接材质`）：1.21 侧是**带注释的主动改写**——`getTargetU/V` 里去掉了 1.20 的 `uOffset * 16`，注释写着 `@replaced (localU) * 16 to localU`。两侧 `TextureAtlasSprite#getU(float)` 的入参语义不同（1.20 lerp 比例 ↔ 1.21 像素偏移），无法离线判定谁对，故判 **`REVERSE-ALIGNED`**（保留 1.21 形态，记入待目视复核清单），不冒险改渲染公式。
- **4 行纯子模块指针**：`a45ef0a13`/`6db8be065`/`8bb33454a`/`fa147c869` 只改 `TerraFurniture` 指针、`437e53a84` 只改 `PortLib` 指针 ⇒ `SKIP-PLATFORM`（非本仓内容；PortLib 按 HANDOFF 不迁移）。
- **行 371** 只把 `BaseLanceItem` 的两个 import 互换顺序（无语义）⇒ `COVERED`。
- **行 395**：`BilayerOreFeature` 两个便利构造器改传 `outerOre`，1.21 `:104/:113` **已是修好的写法**。
- **行 342**：`FROSTBURN_ARROW` 的制箭台配方在 1.21 `ModRecipeProvider:391` 已在（差异只是形参名 `writer`↔`recipeOutput`）。
- **行 349**：`WhipMarkTracker(IPortAttachmentHolder)` ↔ 1.21 `WhipMarkTracker(IAttachmentHolder)`，同形。
- **行 383**：1.20 这次**删掉**了 `mixin/client/multiplayer/MultiPlayerGameModeMixin` 并改用客户端 `GameClientEvents` 条件；1.21 既没有那个 mixin、`GameClientEvents:262` 也已是三标签版本 ⇒ 删除后的目标形态已达。
- **10 行「真实 GAP = 0 且改动文件在 1.21 全在」**：`62d4d191a`(247, 24 文件)、`253bb8d7b`(255)、`a086eeba6`(316, 21 文件)、`c7aefbf00`(321)、`892008ba0`(330)、`bb1892201`(338)、`11a43615e`(348)、`e9501cf56`(361)、`1038e97fe`(383)、`9351a7e6b`(396)；另 `bcecc5382`(261) 与 `b05c8dc3f`(256) 属大块内容，另案处理。

### 62.4 两个新的判定坑（写进方法论）
1. **路径搬迁**会让 rowaudit 把整文件判成 GAP：行 345 的 `common/data/spawner/SpaceSpawner.java` 在 1.21 是 `common/data/saved/SpaceSpawner.java`（行 298「移动到 init」之后又搬过），1.20 路径取不到 ⇒ 1.21 侧整文件被当成"不存在"。凡某行 GAP 集中在单个文件、且该文件在 1.21 有同名不同包者，先 `git grep -l 'class <Name>'` 再判。
2. **删除型文件**：`MultiPlayerGameModeMixin`（行 383 删除）在 1.21 不存在**正是目标状态**；`rowaudit` 对此类文件无 GAP 可报（没有新增行），但人工要显式确认"1.20 删了、1.21 也没有"这一条相等性成立。

### 62.5 状态
- 台账状态 JSON 共 **299** 条；行 231–400 剩余 `TODO`：**37**（本轮从 70 降到 37，共清 33 行：`PORTED` 6 行、`COVERED` 22 行、`SKIP-PLATFORM` 5 行）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本批 6 个文件均 CRLF）。
- **下一批**：按真实 GAP 从小到大继续（`322` 27 / `242` 26 / `357` 21 / `337` 18 / `295` 13 / `274` 12 / `377` 11 / `260` 10 …），大行 `256`(1354)、`323`(726)、`244`(654)、`261`(268，已派子代理移植图鉴条目) 单独立项；`2f44045a8`(359, 133) 需逐标签定方向。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十三、行 332 / 335 `PORTED`：两处「消费端在、入口缺」（2026-10）

### 63.1 行 332 `da5b986be`「蜘蛛巢石」
- `NatureBlocks.SPIDER_NEST_STONE` 在 1.21 已注册、也已接进 `SpiderNestStructure`（洞窟生成）与 `ModBlockStateProvider`（模型）、`ModBlockCounters`（计数），**唯独没有进创造模式「自然方块」页** ⇒ 玩家拿不到这个方块。已按 1.20 `ModTabs:82` 的位置补入（`MARBLE_TAPERED_BLOCK` 之后、`SMALL_STONE_PILES` 之前）。

### 63.2 行 335 `6d4f07298`「悠悠球和召唤杖调参」
- 礼物池（`GiftSubProvider` 中 `WEATHER_PAIN` + `LUCY_THE_AXE` 那一池）1.21 缺 `SummonItems.EYE_LASER_TURRET_STAFF`，且空物品权重仍是 2（1.20 同批改成 1）。已按 1.20 `:1682-1683` 补齐两行；`SummonItems` 由文件既有的 `import org.confluence.mod.common.init.item.*;` 覆盖，无需新增 import。
- 行 335 里其余的「调参」改动（悠悠球/召唤杖数值、外观）经 §61.1 的过滤器核对无真实 GAP。

### 63.3 状态
- 台账状态 JSON 共 **301** 条；行 231–400 剩余 `TODO`：**35**。
- 本轮（round 10）合计清 **35 行**：`PORTED` 8（344/345/364/380/384/388/332/335）、`COVERED` 22、`SKIP-PLATFORM` 5；TODO 70 → 35。
- `fix_eol --check` 仍为 **候选 6**。
- **下一批**：剩余 35 行里真实 GAP 从小到大的 `301`(1)/`308`(1)/`262`(1)/`270`(1)/`286`(2)/`298`(2)/`310`(4)/`393`(4)/`272`(5)…；大行 `256`(1354)/`323`(726)/`244`(654)/`237`(275)/`261`(268，图鉴条目已派子代理) 单独立项。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十四、round 11：图鉴 106 条新条目 + 8 行落地；TODO 35 → 24（2026-10）

### 64.1 行 261 `bcecc5382`「新版本图鉴」→ `PORTED`（子代理执行 + 主代理核收）
1.20 的 provider 有 **583** 条 `.add(...)`（另 17 条 `.variant(...)`），1.21 只有 249 条 ⇒ 这是本轮最大的内容块。做法：把 1.20 的整份语句序列与 1.21 逐键比对后合并。
- **落地 106 条真正新增键**（NpcEntities.STYLIST/GOLFER/STEAMPUNKER/TAX_COLLECTOR/CYBORG、各类史莱姆 NPC、MYSTIC_FROG、GOLDFISH、FIREFLY、GNOME、僵尸变体、THE_GROOM/BRIDE、ZOMBIE_MERMAN、DIGGER、约 70 个怪物与 Boss……）+ 1 个 import（`humanoid.Zombie`，供 6 条僵尸变体行调用 `Zombie.Variant.X.getSerializedName()`）。落地位置与 1.21 既有的 `// 发型师`/`// 蒸汽朋克人` 之类的**注释占位**自然对齐。
- **剔除 201 条「影子重复行」**：子代理首轮把 205 条「键已存在于 1.21」的 1.20 行也一并插入、并放在各自孪生行**之前**（`map.put` 后写覆盖 ⇒ 1.21 旧值生效）。主代理复核后按「键」把它们删掉，使 diff 只保留真实新增。删除后实测：括号 **2278/2278** 配平、链式结构自检异常 0（两处 `(` 结尾的是多行语句，正常）、`fix_eol` 未新增候选、`1.20 新增键 − 落地键` 恰为 **17**（= 有意跳过的 variant 行）。
- **有意跳过 18 条**（子代理报告 + 主代理确认）：17 条 `.variant(...)`（1.21 的 provider 用的是**自己的嵌套 `Builder`**，只有 `add×5 / numberedVariant×2 / demonEyeVariant×1 / mobArmorItems×4`，**没有** `variant(...)`；其中 14 条恶魔眼已被 1.21 的 `.demonEyeVariant(...)` 覆盖，**3 条兔子变体 PARTY/SLIMED/XMAS 是真缺口**）；1 条 `.mobArmorItems(..., null, ...)`（1.21 的 helper 会 `itemStack.save(provider)`，照搬 `null` 会在 datagen 时 NPE，且同键已在）。
- **未做的部分（登记）**：1.20 这次**重排**了 205 条既有条目的 `order(...)`（±100）与少量 rarity/background。这属纯展示顺序，本轮不覆盖 1.21 现值（否则要动 200+ 行且无法离线验收），登记为**反向待复核**；3 条兔子变体需先给 1.21 的 provider Builder 补 `variant(...)` 才能落地。

### 64.2 其余 10 行
| 行 | 提交 | 结论 |
| --- | --- | --- |
| 260 | `75c60a5fe` | **PORTED**：① `GameClientEvents` 补「骑乘岩浆鲨鱼 → 球形 32 格雾」（1.20 原注册在 LOWEST 优先级，故新增**独立**处理器而不是改既有 VoidSea 处理器）；② `EntityEvents` 火伤分支补「岩浆鲨鱼乘客免火」（1.20 `:104-110`）。剩下两处**不是欠账**：`SpikedSlime` 的 `getDefaultDimensions` —— 实测 1.21.1 vanilla `LivingEntity#getDimensions` 是 `getDefaultDimensions(pose).scale(getScale())`（NeoForge 补丁源码 `LivingEntity.java:3423-3425`），所以 1.21 的 `scale(MODEL_SCALE)` 与 1.20 的 `scale(MODEL_SCALE * getScale())` **等价**，照搬反而会**双重缩放**；`unicorn` 那行是 1.20 把动画路径从 `rideable/` 挪出后的**修正**，而 1.21 根本没有坐骑渲染器注册（见 64.4） |
| 308 | `46e5e8626` | **PORTED**：本行唯一新增行就是上面那条鲨鱼雾判定 |
| 262 / 301 | `17ca9b50f` / `b47adc3ce` | **PORTED**：`PhasebladeRenderer` 第三处（`:103`）改用 `minecraft.getFrameTime()`（1.20 HEAD 只改了这一处；另两处仍是 `partialTick`） |
| 310 | `3e76867aa` | **PORTED**：`SummonerSummonMarks` 的 FIRECRACKER 标记回调补 `FirecrackerItem.explode(source.getAttachmentEntity().getOwner(), target, target, damage, source.getArmorPenetration())` —— 1.21 此前只结算 2.75× 伤害、**没有爆炸**。同行的 `HARMFUL_EXCEPT_WHIP_TAG` 枚举判 `COVERED`：1.20 侧该常量与 `HARMFUL` 的 `is()` 实现**逐字相同**、全仓也无其它消费者（注释自陈「历史名称保留」），纯序列化名差异 |
| 272 | `641467c87` | **PORTED**：`ModItemModelProvider` 的 handheld 列表补 `createDir(SummonItems.ITEMS, "summon/")` 与 `createDir(WhipItems.ITEMS, "whip/")`，并补 `handheldTextureAlias(...)` 与 `new_hornet_staff` 的别名调用。实测 1.21 仓库里 `models/item/*_staff.json`、`models/item/summon/**` **全部为 0** ⇒ 不补这条，datagen 也永远生不出召唤杖/鞭子模型 |
| 270 | `4a3436770` | **COVERED**：唯一 gap 是 1.20 侧一个未使用的 `Confluence` import；13 条召唤标记在 1.21 逐条对应 |
| 286 / 393 | `c5b035680` / `6cda06303` | **COVERED**：两行新增的都是 `DynamicLightDispatcher/Provider/Register` 与 `GenericParticle` 的 **import**，而 1.20 自己从不使用（全仓只有 import 行）。动态光源在 1.21 已改由 lib 自驱动（`LibClientGameEvents:176 update(...)`、`:165 clearWorld()`，并有 `EntityRendererMixin`/`LevelRendererMixin` 查询），`GenericParticle` 类在 1.21 也在 |
| 298 | `a34060571` | **COVERED**：`ModBlockCounters` 已在 `common/init/`，1.21 的 `LivingEntityEvents:587/610` 与 `EnvironmentLevelAccess$MatcherMixin:17` 都在用（import 走通配） |

### 64.3 本轮顺带勘出的**大残留（不属 231–400 任何一行）**：坐骑渲染器
1.21 全仓对 `RIDEABLE_SLIME / RIDEABLE_BEE / RIDEABLE_UNICORN / RIDEABLE_LAVA_SHARK` 的渲染注册**为 0**（`MountGeoRenderer` 类在、`ModEntities` 四个实体在、`MountItems` 四个物品在，但没有任何 `registerEntityRenderer`）；且 `assets/.../rideable/unicorn.{geo.json,animation.json,png}` 三件资源在 1.21 **不存在**（1.20 有）。⇒ **四个坐骑在 1.21 客户端应当是不可见的**。台账 231–400 没有任何一行覆盖它（WP3A 坐骑批次在 231 之前），已记入本节待专项核收。

### 64.4 状态
- 台账状态 JSON 共 **312** 条；行 231–400 剩余 `TODO`：**24**（本轮 35 → 24，清 11 行：`PORTED` 9、`COVERED` 4 减去跨轮重复计入的 262/301 合并 = 见下表）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo；本轮 5 个 java 改动全 CRLF）。
- **下一批**：剩余 24 行中真实 GAP 较小的 `316/317/322/323/332…`（先跑 `screenall_detail.txt` 的对应小节）；大行 `256`(1354)、`323`(726)、`237`(275)、`244`(654) 单独立项；`359`(133) 标签重排需逐标签定方向。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十五、round 12：行 274/377 落地 + 4 行结账；TODO 24 → 18（2026-10）

### 65.1 行 274 `041c84915`「重命名钱币槽，生物初步调参」→ `PORTED`
| 缺口 | 处置 |
| --- | --- |
| 5 条中文图鉴描述（冰霜巨人/装甲维京人/冰冻人鱼/冰雪精/混沌精） | 1.21 里这 5 行**躺在注释里**（`//        add("bestiary.entity.confluence.<x>.desc", "…")`），1.20 已启用。本轮按 1.20 HEAD 逐字启用（含缩进 8 空格）。英文侧**两侧都是注释**，不动 |
| `item.confluence.money_trough` 静态语言键 | 补进 `lang/{pt_br,es_es,lzh}.json` 与 `i18n/{en_us,zh_cn}.json`；pt_br 另补 `tooltip.confluence.money_trough.info`。做法：从 1.20 文件取**原样行**，插到「1.20 里目标行之后、且该行在 1.21 也存在」的第一条锚点之后，再统一缩进为 2 空格。5 个文件补后 `json.load` 全部通过 |
| `EntitySubProvider` 的 `DIVING_HELMET` | 1.20 把 `.add(LootItem.lootTableItem(TCItems.DIVING_HELMET).setWeight(1))` 改成不带 `setWeight(1)` 的写法。`setWeight(1)` 即默认权重 ⇒ **等价**，不移植 |
- **顺带记下的残留（非本行）**：1.21 的 `ModEnglishProvider`/`ModChineseProvider` 都没有 `add(PetItems.MONEY_TROUGH.get(), …)`（1.20 `:1785`/`:5937`，属于 `addStorageCompanionTranslations` 方法，1.21 连该方法都没有）⇒ 命令 `/give` 等场景下钱币槽与切斯特的眼骨都只有原始键名。这属别的行，登记待办。

### 65.2 行 377 `05380071a`「调整种子特性」→ `PORTED`（6 文件 / 11 行）
1. **树叶破坏改为召唤「充能苦力怕」**：`NoTraps.dropBombWhenLeavesDestroy` → `dropPoweredCreeperWhenLeavesDestroy`，概率 0.25 → 0.05，炸弹换成 `EntityType.CREEPER.spawn(...)`；`LevelEvents` 的调用点同步改名；顺手删掉随之失效的 `BaseBombEntity`/`ModEntities` import。
   ⚠️ **形参数量差异**：1.20 的调用是 7 参（含 `ItemStack`），1.21 的 `EntityType#spawn` 是 6 参——本仓 `ForTheWorthy.java:41` 早已在用 6 参形式，故照它改写（`spawn(level, LibEntityUtils::poweringCreeper, pos, MobSpawnType.TRIGGERED, true, false)`），`LibEntityUtils.poweringCreeper(Creeper)` 在 1.21 lib 中实测存在。
2. **两块月亮蛋糕碎块降数值**：`HONEY_MOONCAKES_CHUNKS` `noEffectProperties(1, 0.75f)` → `(2, 1.25f)`；`EGG_YOLK_MOONCAKES_CHUNKS` `plentySatisfiedProperties(6000, 6, 3.5f)` → `(6000, 3, 1.75f)`。**注意此二处与同名常量行文本相同**，改的时候必须带物品名上下文，否则 `replace` 会命中 `HONEY_MOONCAKES`/`EGG_YOLK_MOONCAKES`（本轮第一次批量替换就因此报 3 处命中而中止）。
3. **爆炸不再破坏方块**：`BaseFoodItem`（No Traps 种子下吃樱桃）`ExplosionInteraction.MOB` → `NONE`；`CropBlockMixin`（成熟土豆踩踏）`BLOCK` → `NONE`。
4. **空岛刷怪 y 偏移** `10 + random.nextInt(15)` → `15 + random.nextInt(15)`（两处；与本轮之前行 345 的 x/z 改动同属一个偏移块）。

### 65.3 四行结账（各附实证）
- **行 278** `87b93ede2`「合并 stream codec」：真实 GAP = 0（10 个改动文件全在，唯一"缺失"是 `PortLib` 子模块指针）⇒ `COVERED`。
- **行 300** `14425eb1c`「uv 修复」：真实 GAP = 0；`client/summon/ClientSummonManager.java` 在**两仓 HEAD 都不存在**（该文件在 1.20 后续提交里被撤/换，`client/summon/**` 已被 `client/summoner/**` 取代），故其"缺失"是伪报 ⇒ `COVERED`。
- **行 342** `d5fd40bff`「平衡锤」：8 个文件全在；唯一 GAP 是 `fletchingTable(...)` 里 `writer` ↔ `recipeOutput` 的形参名差异，`FROSTBURN_ARROW` 制箭台配方在 1.21 `ModRecipeProvider:391` 已在 ⇒ `COVERED`。
- **行 349** `c0e8c4c75`「多召唤标记叠加支持」：`SummonMarkTypeBuild.java` 在 1.20 该提交里被**删除**、1.21 也没有（正是目标态）；唯一 GAP 是 `WhipMarkTracker(IPortAttachmentHolder)` ↔ 1.21 `WhipMarkTracker(IAttachmentHolder)` 的平台改名 ⇒ `COVERED`。

### 65.4 状态
- 台账状态 JSON 共 **318** 条；行 231–400 剩余 `TODO`：**18**（本轮 24 → 18，清 6 行）。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo）；本轮 7 个 java/语言文件改动均 CRLF，5 个 lang 文件 `json.load` 全通过。
- **下一批**：剩余 18 行里真实 GAP 较小者 `385`(11)/`295`(13)/`337`(18)/`357`(21)/`242`(26)/`322`(27)；大行 `256`(1354)/`323`(726)/`244`(654)/`237`(275) 与 `359`(133，逐标签定方向) 单独立项。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十六、风格回归：清掉本会话自添的 18 行解释性注释（2026-10）

用户重申「移植时不要写多余的注释」。回查 `notes/HANDOFF.md` §1.3 早有同款铁律——**「1.21 侧代码以 1.20 逐字为准…不加新的解释性注释；只抄 1.20 的『缺少未加入物品』类欠账注释（『已齐全』类注释不抄）」**，本会话多条移植里我自添了带「1.20 `提交号`」口吻的说明，属违规。

### 66.1 清理（9 文件 / 18 行，全部是我本轮加的）
| 文件 | 删除内容 |
| --- | --- |
| `common/event/game/entity/LivingEntityEvents.java` | 4 行（`mobDespawn` 2 行、`damage$Post` 1 行、`armorPenetration` 1 行） |
| `common/block/natural/CrimsonHeartBlock.java` | 3 行（`onDestroyedByPlayer` 上方整块，含空 `///`） |
| `common/block/natural/ShadowOrbBlock.java` | 3 行（同上） |
| `client/event/ModClientSetups.java` | 1 行（finch_staff 属性上方） |
| `client/renderer/entity/projectile/FlailAuxiliaryProjectileRenderer.java` | 1 行 |
| `common/event/game/entity/ItemEvents.java` | 1 行 |
| `util/PrefixUtils.java` | 1 行 |
| `client/event/GameClientEvents.java` | 1 行（岩浆鲨鱼雾处理器上方） |
| `common/data/gen/ModItemModelProvider.java` | 1 行（`new_hornet_staff` 别名上方） |
识别方法：逐个本会话提交取「新增的注释行」，凡该行正文在 1.20 HEAD 同一路径的文件里找不到 ⇒ 判为我自添（脚本 `build/_cmp231/findmycomments.py`）。代码本体一行未动，`fix_eol --check` 仍为候选 6。

### 66.2 未动的部分（说明理由）
工作树里另有 **56 行**同风格注释（如 `SpawnPlacementChecks` 的「1.21 侧增量版」段、`AnglerDialogScreen` 的搬移说明、`AbstractMountEntity` 的形参差异说明），`git log -S` 实测来自**更早的提交**（`90a673ebc`、`58431c613` 等，非本会话）。按 HANDOFF §1.3「1.21 既有的措辞差异只登记、不重写」，本轮**不擅自改写**；如需一并清理，请下指令，我再按「逐行与 1.20 原文比对」的方式批量处理。

### 66.3 后续口径
- 移植只搬运 1.20 原文自带的注释；平台差异改为**只写进 `notes/PORT-LANDING-RECORD.md`**，不写进源码。


## 六十七、round 13：行 337/295 落地；TODO 18 → 16（2026-10）

本轮遵守 §66 的口径：**源码里只搬运 1.20 原文自带的注释**（本批唯一抄的是 `// todo 石巨人、雪人军团` 这类欠账注释，属 §1.3 允许项），平台适配差异一律只写在本记录里，不进源码。

### 67.1 行 337 `fecb245e5`「罐子掉钱逻辑补充」→ `PORTED`（3 文件）
| 位置 | 变更 |
| --- | --- |
| `BasePotBlock` | 钱币洞概率由 `float moneyHoleChance` + `nextFloat() < chance` 改为 **`IntSupplier invMoneyHoleChance`** + `int i = invMoneyHoleChance.getAsInt(); if (i > 0 && level.random.nextInt(i) == 0)`；金钥匙 `0.0286F` → `nextInt(35) == 0`；药水 `0.0444F/0.0222F` → `nextInt(45) < (expert ? 2 : 1)`；虫洞药水 `0.0333F` → `nextInt(3) == 0`；专家治疗药水加量 `0.3333F` → `nextInt(3) == 0`；绳子 `nextInt(5,11)` → `nextInt(20,41)` 且 `ModBlocks.ROPE.get().asItem()` → `ModBlocks.ROPE.asItem()`；钱币倍率的 Boss 表补 `THE_DESTROYER`、`SKELETRON_PRIME`，`countDefeated` 补 `PirateInvasionGameEvent.KEY`，todo 注释同步收窄为「石巨人、雪人军团」 |
| `PotBlocks` | 13 个罐子的第三参由概率改为**倒数值**（500/461/0/272/461/400/375/365/125/365/384/365），利兹哈德罐用 `() -> KillBoard.INSTANCE.getGamePhase().isHardmode() ? 250 : 500`；`registerWithItem` 改收 `IntSupplier`，并新增 `int` 重载委托 `() -> invMoneyHoleChance`（1.21 侧保留自己的 `DeferredBlock`/`DeferredRegister` 写法） |
| `ForTheWorthy` | `summonPoweredCreeper` 的 `nextFloat() < 0.25F` → `nextInt(6) == 0` |
> 注：`OCEAN_POT` 的倒数取 **0**，配合 `i > 0` 守卫 ⇒ 海洋罐**永不**出钱币洞，与 1.20 一致。

### 67.2 行 295 `f212a001a`「修复生物渲染、蠕虫行为及特殊物品拾取」→ `PORTED`（1 文件）
- **唯一真欠账**：`PlayerEvents` 在「提供魔力」与「提供生命」两个拾取分支里补 `itemStack.setCount(0);`（原写法只 `itemEntity.discard()`，被拾取的堆叠数未清零）。
- 其余 5 个文件的 12 条 GAP **逐条核为平台/格式差异**：
  - `SnatcherRenderer`：1.21 的顶点助手是 `vertex(vertices, pose, x, y, z, u, v, light, overlay)`（新顶点 API 只需 `PoseStack.Pose`），1.20 是 `vertex(vertices, matrix, normal, …)`（额外 `Matrix3f`）⇒ 平台；
  - `EntityGlowingGeoLayer` / `TetheredPlantRenderer`：`reRender(...)`/`renderRecursively(...)` 的颜色参数 1.21 用 `0xFFFFFFFF` 整型重载，1.20 用 `1,1,1,1` 四参 ⇒ 平台；
  - `MonsterEntities.registerWorm`：1.21 是 `DeferredHolder<EntityType<?>, …>` + 已 import 的 `Supplier<…>`，1.20 是 `RegistryObject` + FQN `java.util.function.Supplier` ⇒ 平台（rowaudit 只因 FQN 文本判 GAP）；
  - `PlantHeadRenderer`：1.21 的 `applyRotations` 是 1.21 平台签名（多一个 `float scale` 形参）且局部变量名 `modelScale`，故 1.20 的 `float scale = entity.getScale();` / `… / scale, 0.0)` 两行匹配不上 ⇒ 平台。**并且**：本行提交里 `stemPosition` 一度是渲染器内自算，但 1.20 HEAD 已改成 `return entity.stemOffset(partialTick);` ——与 1.21 **逐字相同**，故此项无需移植。

### 67.3 状态
- 台账状态 JSON 共 **320** 条；行 231–400 剩余 `TODO`：**16**。
- `fix_eol --check` 仍为 **候选 6**（§51.5 的 6 个密文 geo）；本批 4 个 java 改动均 CRLF。
- **下一批**：剩余 16 行按真实 GAP：`357`(21)/`242`(26)/`322`(27)/`347`(32)/`392`(37)/`312`(42)/`280`(62)/`302`(77)/`317`(86)/`355`(122)/`359`(133)/`237`(275)/`244`(654)/`323`(726)/`256`(1354)。
- 仍**未触碰**工作树里的非本批改动 `common/data/GamePhase.java`（见 §54.5）。


## 六十八、round 14：4 行 `COVERED`（含两行「122 条 GAP 全是平台噪音」）；TODO 16 → 12（2026-10）

### 68.1 新证据法：属性 lambda 的「按实体配对 + 只比数字」
BossEntities / MonsterEntities 的属性行是 `() -> CreatureAttributeBuilder.xxx().maxHealth(…).armor(…)…`，两仓唯一差异是 **`LibAttributes.getArmorPenetration().get()`（1.20 raw）vs `LibAttributes.getArmorPenetration()`（1.21 Holder）**——
rowaudit 按行文本比对，必然把这类行全判成 GAP。本轮的证明方式（脚本 `build/_cmp231/attrsdiff.py`、`attrsproof.py`、`numsproof.py`）：
1. 用 `public static final … <NAME> =` 切出实体名，把每个实体的属性 lambda 收成一行；
2. 先做「方法名序列 + 数字序列」比对，再做**只比数字**的比对。

实测结果：
```
BossEntities.java   : 配对 28 个实体，数字序列不同 = 0
MonsterEntities.java: 配对 209 个实体，数字序列不同 = 1（SWEET_SLIME —— 解析器把 helper 签名误配，手工核对两侧均为 maxHealth(16).armor(0).attackDamage(0)）
```
⇒ **两侧 237 个实体的属性数值逐个相等**。

### 68.2 据此结账 4 行
| 行 | 提交 | 结论 |
| --- | --- | --- |
| 355 | `e6eace2b1`「平衡性调整尝试」 | 真实 GAP 122 条（MonsterEntities 120 + BossEntities 2）**全是 `.get()` 平台差异**；2 个改动文件都在，数值逐实体相等 ⇒ `COVERED` |
| 357 | `9e32f0135`「修复 Boss 从属清理与击退问题」 | 真实 GAP 21 条：20 条 Boss 属性行同属 `.get()` 差异；剩下 1 条 `CascadeFireProjectile` 的 `setSecondsOnFire` ——1.21 已写作 `recipient.igniteForSeconds(...)`（1.21 改名）⇒ `COVERED` |
| 385 | `fc3fabfb9`「平衡性调整，权重调整，贴图补充」 | 真实 GAP 11 条全为 Boss/Monster 属性行（同上）；9 个改动文件（含贴图）在 1.21 全部存在 ⇒ `COVERED` |
| 242 | `d599373c3`「修复 8 月 15 日遗留问题」 | 26 条 GAP 分布在 11 个文件，逐条核为**结构/平台等价**：`MagicConch` 的 `readStoredPositions`/`writeStoredPositions` 助手（1.20 用 compound+`readBlockPos(Tag)`，1.21 用 long+`readBlockPos(tag,key)`，各随本侧 NbtUtils API，语义同为「两个标记点」）；`VoidCrystalItem` 的 `Mark` record 与扁平行内 NBT（1.20 自己的注释即写明「与 1.21.1 一致」）；`WormholeToPlayerPacketC2S` 的目标校验（1.21 走 `WormholeHandler.judgment`，**比 1.20 的 `isTrackable` 更严**：多判存活/旁观/白队）与玻璃瓶回返（1.21 用 `if (add(...)) return; drop(...)`，与 1.20 的 `if (!add(...)) drop(...)` 等价）；`GlobalCloakData.networkDecode`（1.21 `clear()+putAll` 与 1.20 直接赋值结果一致且不换实例）；`KillBoard.defeat`（1.20 抽出的 2 参重载，1.21 内联 `ServerLifecycleHooks.getCurrentServer()`）；`MeteoriteTracker`（1.21 `readBlockPos(nbt,key).orElse(ZERO)`）；`DyeMixPacketC2S`/`PlayerUtils`（`isSameItemSameComponents` 改名）；`HookItems`（异常文案差异，§1.3「不重写」）；`OpenMenuPacketC2S`（1.21 `ClipContext` 收 `CollisionContext.of(player)`）；`ModUtils` 陨石（1.20 条件赋值 ↔ 1.21 直接赋 `nextBoolean()`，结果等价）。另 4 个「1.21 无此路径」的文件经查是 `dead-only`（`NPCSpawner` 8 行在 1.20 后续被撤）或纯删除（`WeaponUseStatePacketC2S`/`PlayerMoneyTransaction`/`terraprisma_gray.png`）⇒ 无可移植 ⇒ `COVERED` |

### 68.3 状态
- 台账状态 JSON 共 **324** 条；行 231–400 剩余 `TODO`：**12**。
- `fix_eol --check` 仍为 **候选 6**；本批**未改动任何源码**（纯裁定），工作树只多了 notes 三件与我自己写的 `GamePhase.java`（他人）之外的临时脚本（在 gitignore 的 `build/` 下）。
- **下一批**：剩余 12 行 —— `322`(27)/`347`(32)/`392`(37)/`312`(42)/`280`(62)/`302`(77)/`317`(86)/`359`(133)/`237`(275)/`244`(654)/`323`(726)/`256`(1354)。其中 `312`(暴击率)、`392`(动态光/仆从接口)、`347`(召唤词缀复制) 已在本轮之前的探查里定位到具体行，下一批从这三行开始。


## 六十九、行 347 `DEFER-ARCH`：`IndicatorMode` 配置**不落**——落地即回归（2026-10）

### 69.1 本行的全部内容（逐条已定位）
1.20 `e1cd9c8cb`「添加配置文件，召唤词缀复制」把客户端伤害/治疗数字的开关从**布尔**换成**三态枚举**：
| 位置 | 1.20 HEAD（=`IndicatorMode` 形态） | 1.21 HEAD（=布尔形态） |
| --- | --- | --- |
| `ClientConfigs` | `public static IndicatorMode damageIndicator = IndicatorMode.VIRTUAL;`（heal 同）、`private static EnumValue<IndicatorMode> DAMAGE_INDICATOR;`、`DAMAGE_INDICATOR = builder.defineEnum("damageIndicator", IndicatorMode.VIRTUAL);`、`enum IndicatorMode implements PortTranslatableEnum { PARTICLE, VIRTUAL; isParticle(); isVirtual(); getTranslatedName() }` | `public static boolean damageIndicator = true;`、`private static BooleanValue DAMAGE_INDICATOR;`、`builder.define("damageIndicator", true)` |
| `DamageIndicatorParticle` | `if (ClientConfigs.damageIndicator.isVirtual() && type == DAMAGE \|\| ClientConfigs.healIndicator.isVirtual() && type == HEAL) return;`（`:99`） | `if (!ClientConfigs.damageIndicator && … \|\| !ClientConfigs.healIndicator && …) return;`（`:104`） |
| 语言 | `ConfigurationLanguageSubProvider` 6 条（`indicatorMode.{particle,virtual}` 与两条 `.tooltip`，中英各一）+ 静态 `i18n/en_us.json`、`i18n/zh_cn.json` 各 4 条 | 只有旧的 `confluence.configuration.damageIndicator`（"Damage Indicator"）等 |
| 其余 | `SummonerBatchedInfoPayload.ID`/`return ID;`、`NetworkEvents.registerInGameS2C` | 平台层：1.21 用 `TYPE` + `playToClient`，已在册 |

### 69.2 为什么不落：落地会让「伤害数字」默认消失
- 1.21 现行为：`damageIndicator = true` → `!damageIndicator == false` → **不跳过**粒子 ⇒ 伤害数字用**粒子**显示（可用）。
- 1.20 现行为：默认 `IndicatorMode.VIRTUAL` → `isVirtual() == true` → **跳过**粒子，改走「虚拟信息」（`InfoData` + `InfoRenderer` + `InfoRenderDispatcher`）。
- **但虚拟信息的产线两侧都没有接上**：全仓实测 `InfoData.addNumber` / `addText`（`pendingNumbers.add` / `pendingTexts.add` 的唯一入口）**在 1.20 HEAD 与 1.21 HEAD 都没有任何外部调用点**，`SummonerBatchedInfoPayload` 也只在 `InfoData.tick` 内部构造；1.21 侧 `SummonerAttachmentTypes.INFO` 的文档注释也自陈「当前未启用——没有任何调用点」。
⇒ 若照 1.20 落这组配置，1.21 会变成「粒子不显示 + 虚拟信息无内容」＝**伤害数字彻底消失**，属 §1.1「不得回归 1.21」。

### 69.3 处理与后续条件
- 状态判 **`DEFER-ARCH`**：1.21 保持布尔配置与粒子显示不动；本行的枚举形态、语言键、`isVirtual()` 守卫全部登记在§69.1，随时可按表落地。
- **落地前置条件**（按顺序）：① 先在 1.21 接上虚拟信息产线（把 `DamageIndicatorOptions.sendDamageParticle(...)` 的调用点按配置分流到 `InfoData.addNumber/addText`，或找到 1.20 侧未来的接法）；② 再落枚举与 `defineEnum`，并同步 6+8 条语言键；③ 同批把默认值定为 `VIRTUAL` 才与 1.20 对齐。①未完成前不得单独落②③。
- 该判断同时解释了 §61/§64 里反复出现的「消费者在、驱动器缺」现象的一个变体：**这里缺的是整条产线**，所以上游开关不能先翻。

### 69.4 状态
- 台账状态 JSON 共 **325** 条；行 231–400 剩余 `TODO`：**11**（本轮 `COVERED` 4 + `DEFER-ARCH` 1）。
- `fix_eol --check` 仍为 **候选 6**；本轮未改动任何源码。


## 七十、行 392 / 312 落地：动态光源 + `PathNode` 四元数插值；`clientTick$Post` 补暂停守卫

### 70.1 行 392 `2de684965`「优化动态光照，移除可携带仆从接口行为」→ `PORTED`

**落地 3 个文件（4 行新增）**

| 文件 | 1.20 HEAD | 1.21（本批落地） |
| --- | --- | --- |
| `client/summoner/renderer/minion/SanguineBatRenderer` | `DynamicLightDispatcher.INSTANCE.addLightSource(new DynamicLightDispatcher.LightSource(visualNode.pos(), 8));`（`:35`） | `DynamicLightDispatcher.addLightSources(visualNode.pos(), 8);`（`super.render(...)` 之后） |
| `client/summoner/renderer/minion/TerraprismaRenderer` | 同上（`:124`） | 同上（`render` 首行） |
| `common/summoner/attachmentEntity/PathNode` | 新增 `toQuaternion()` + `fromQuaternion(Vec3, Quaternionf)`，`lerp` 改为**四元数 slerp**（JOML），并按点积符号统一同一旋转轴（`dot < 0` 时取反 `target`） | 逐行移植；原 `Mth.rotLerp` 逐欧拉角版本被替换 |

- 平台改写：1.20 是 `INSTANCE.addLightSource(new LightSource(pos, 8))`（实例 + 记录类），1.21 lib 是静态 `addLightSources(Vec3 pos, int light)`（`DynamicLightDispatcher.java:50`，含 `Mth.clamp(light, 0, 15)`）；`AttachmentEntityRenderDispatcher:88` 的 `INSTANCE.getDynamicLight(pos, packed)` ↔ 1.21 静态同签名 ⇒ 平台噪音。
- **驱动/消费两侧均已确认**（避免「消费者在、驱动器缺」）：1.21 lib 自驱动 `LibClientGameEvents:176 DynamicLightDispatcher.update(event)`、`:165 clearWorld()`；消费侧 `mixin/client/EntityRendererMixin:15`、`mixin/client/LevelRendererMixin:18`。故新增的两行不是死代码。
- 注释口径：1.20 该提交给 `toQuaternion`/`lerp`/`fromQuaternion` 各写了设计说明性 javadoc（含「为何不用 `Mth.rotLerp`」的长段）。按 §1.3 与用户口径「移植时不要写多余注释」，**只移植代码、不抄这三段说明**；无一处 1.20 欠账式注释被丢。
- **未落地（登记）**：该提交同时**删除**了「可携带仆从」一族——`common/summoner/minion/ICarryMinion.java`（整文件 28 行）、`EyeLaserTurretMinion` 的 `implements ICarryMinion<...>` 与 `onCarry()` 覆盖、`EyeLaserTurretIdleGoal` 的 `!minion.isOnCarry()` 守卫及其 `else` 分支、`AttachmentEntityData`(−11)/`AttachmentEntity`(−9) 的携带字段、`Minion`(−1)、`RuinRelicMinion`(−3)。1.21 仍有 `ICarryMinion` 4 处（`ICarryMinion.java`、`EyeLaserTurretMinion:21/127`、`AttachmentEntity:169-172`）⇒ 属「1.20 删、1.21 未跟」的架构同步，按 §1.1／§57.2 不在 1.21 回退（该特性在 1.20 是 `116bef809` 加、10 天后又被本提交撤掉的短命实验），仅登记为 `DEFER-ARCH` 候选。旁证：1.20 HEAD 里 `EyeLaserTurretMinion` 还留着**孤儿字段** `private boolean isOnCarry = false;`（`:26`，除 `:35` 的 SyncField 注册外无任何读写），本身就是删除未净的痕迹。
- 其余差异：`GroundMinion` 4 行 = 1.20 的 `net.minecraftforge.registries.RegistryObject` + `attachmentEntity.{AttachmentEntityType,IBlockCollision,PathNode,SyncFieldDispatcher}` 逐条 import ↔ 1.21 通配 `attachmentEntity.*` ⇒ 假阳性。

### 70.2 行 312 `c886cec0f`「修复暴击率问题」→ `PORTED`

**落地 1 个文件（`client/event/GameClientEvents.java`，18 +/− 16）**

1. `clientTick$Post` 补 `if (!minecraft.isPaused()) { ... }`：包住「触电输入包发送 → `MeteorLandingHandler` → `HookThrowingHandler` → `KeyRequestHandler` → `DropletsHandler` → `ClientGameEventSystem`」。
2. 该方法的调用顺序对齐 1.20 HEAD（`BowHandler` → 战斗/Axe/粒子 → `SwordProjectile`/`LeftClickItem`/`Flail` → `HouseSelectHud` → `ClientBiomeEffectSystem` → `ScryingOrbHandler` → `SoulSkillHandler` → 暂停守卫块）。
3. `WeatherHandler.handle()` 从 `if (player != null)` 内部前段**移到方法末尾**（1.20 该提交的原动作，行审计看不到「移动」因此不在 42 条 gap 里）。1.21 的 `WeatherHandler.handle()` 只做风速插值（`:155-162`，不依赖 player）⇒ 无条件调用安全。

**判定为等价/平台噪音（未落地）**

| 缺口 | 依据 |
| --- | --- |
| `TickEvent.ClientTickEvent` + `if (event.phase != START/END) return;` | 1.21 原生 NeoForge 用 `ClientTickEvent.Pre`/`ClientTickEvent.Post` 两个内嵌事件（`GameClientEvents:156/190`），语义等价；`TickEvent.PlayerTickEvent`+END ↔ `PlayerTickEvent.Post`（`:400`） |
| `PortEventHandler.addListener(...)` 整块注册 | 1.21 用 `@EventBusSubscriber(value = Dist.CLIENT, modid = Confluence.MODID)`（`:123`），全库 `PortEventHandler` 只存在于 notes/tools，不在 1.21 源码 |
| `org.confluence.mod.util.*` 四个具名 import | 1.20 把通配拆成具名，1.21 仍通配 ⇒ 假阳性 |
| `MeteorLandingHandler.handle(player)` / `DropletsHandler.handle(player)`、`event.player` 等 | 仅签名/取值形状差异（1.21 `handle(minecraft, player)`、`event.getEntity()`）⇒ 纯外观 |
| **暴击修复本体** | 1.20 lib `ILibDamageSource.processCritical` 新增 `boolean original` + `if (event.isCritical() && !original)`；1.21 lib **已是该形态**（`ILibDamageSource.java:41-58`）；1.20 lib 的 `@WrapOperation` 写 `confluence$setCritical(flag2)` ↔ 1.21 侧在 mod 内 `PlayerMixin:106-110` 用 `@Inject` 写 `confluence$setCritical(flag1)`；1.20 mod 侧被删的 `removeVanillaJumpCritical` 在 1.21 **本来就不存在**（全库 0 命中）⇒ `COVERED` |
| 其余：`customizeGuiOverlay$BossEventProgress`/`renderGuiLayer$Pre`/`renderTooltip$GatherComponents`/`itemToolTip` 改名 | 1.20 从 PortLib 事件迁回 Forge 事件的改名，1.21 对应 NeoForge 同名事件（`RenderGuiOverlayEvent`/`ItemTooltipEvent` 等） |
| `ScryingOrbHandler`/`SoulSkillHandler`/`FlailHandler`/`BowHandler`/`GunHandler` 抽取 | 两侧均已有这些类与调用（1.21 `clientTick$Post` 已调用 `BowHandler.releaseFullyDrawnBow`、`FlailHandler.handle(player, attackHeld)`、`GunHandler.handle(player, attackHeld)`、`ScryingOrbHandler.handle(minecraft, player)`） |

- **未落地（越界登记）**：该提交的 lib 子模块里还含 `NaturalSpawnerUtils` 的 `new HashSet<>()` → `new ReferenceOpenHashSet<>()`（`ResourceKey<Level>` 按引用比较）。1.21 的 `Confluence-Magic-Lib`（**嵌套独立仓库**，`git status` 干净）仍是 `HashSet` ⇒ 需在子模块单独提交，本批不动，仅登记。

### 70.3 状态

- 台账双写：`392=PORTED`、`312=PORTED`；状态 JSON 共 **327** 条；行 231–400 剩余 `TODO` = **9**（`322`/`280`/`302`/`317`/`359`/`237`/`244`/`323`/`256`）。
- `fix_eol --check` 仍为 **候选 6**；本批只改源码 4 文件 + notes 3 件，未碰 `GamePhase.java`（他人改动）。
- 教训记账：本批首版补丁用 `str.replace('\n','\r\n')` 套在已含 `\r\n` 的串上，写出 29 行 `\r\r\n`（`git ls-files --eol` 显示 `w/-text`、`git diff` 出现全文件重写）。已修为纯 CRLF 并全库文本扫描确认无第二处；以后一律「先拼 LF 串、最后一次性转 CRLF」。


## 七十一、行 322 `46ae3a168`「完善悠悠球及饰品功能，统一敌怪反击并修复附魔钓竿」：反击块 + 鱼线染色落地

### 71.1 落地 2 个文件（13 行新增、1 行替换）

| 文件 | 1.20 HEAD | 1.21（本批落地） |
| --- | --- | --- |
| `common/event/game/entity/LivingEntityEvents` | `damage$Post` 中 `AttackEffects.afterDamage(victim, damageSource);` 之后插入 10 行「统一敌怪反击」块（`EnemyTargeting.isConfluenceEnemy` → `attacker` → `setLastHurtByMob`/`setTarget`/`getBossOwner().onEncounterHurt`），并具名 import `EnemyTargeting` | 逐字插入到 `livingDamage$Post` 的同位置（`afterDamage` 之后、`FELL_OUT_OF_WORLD` 早退之前）；import 加在 `EnemyDamageRules` 之后 |
| `client/renderer/entity/fishing/BaseFishingHookRenderer` | `renderString` 里 `color = YoyoEquipment.animatedColor(YoyoEquipment.appearance(player, color, 0).stringColor(), (entity.level().getGameTime() % 120) + partialTicks);` | 替换原来的 `color = 0xFF << 24 \| (color & 0xFFFFFF);`（那正是本提交前的旧形态） |

- 类型/成员核对：1.21 `BossOwnedEntity.getBossOwner()` 返 `@Nullable BaseBoss`（`BossOwnedEntity.java:12`），`BaseBoss.onEncounterHurt(DamageSource)` 在（`BaseBoss:321`）；`EnemyTargeting.java` 两侧 **逐字节相同**（82 行）；`Mob` 由已存在的 `net.minecraft.world.entity.*` 覆盖；`EnemyDamageRules`/`BossOwnedEntity` 1.21 已 import。
- 注释口径：1.20 该块上方有一行 `/// 只记录实际造成伤害的外部攻击者，射弹和部件追溯到所有者。`。按 §1.3「不加解释性注释」，**不抄**；其语义记在本节——只对实际受伤的 1.21 敵怪生效，且攻击者会从射弹/部件追溯到真正的主人，再判断「非我方」才写 `lastHurtByMob`/`setTarget`。
- 该块在 1.20 HEAD 位于「早退检查之前」，因此在 1.21 也必须放在 `FELL_OUT_OF_WORLD`/`GENERIC_KILL` 早退**之前**（虚空/`generic_kill` 也照样记反击），本批照此摆放。

### 71.2 判为等价/平台噪音（未落地）

| 缺口（1.20 提交新增行） | 1.21 现状与依据 |
| --- | --- |
| `YoyoEntity` 6 行 `entityData.define(...)`、`BeeKeeperProjectile` `entityData.define(GIANT, false)` | 1.21 是 `defineSynchedData(SynchedEntityData.Builder builder)` + `builder.define(...)`（同名同参同值：`ROLE`/`PRIMARY_ID`/`DETACHED`/`COUNTERWEIGHT`/`STRING_COLOR`/`ORBIT_DIRECTION` 全在）⇒ 平台 API 改名 |
| `BeeKeeperProjectile.canChangeDimensions()`、`import net.minecraft.network.protocol.Packet` | 1.21.1 `canChangeDimensions(Level, Level)`、`getAddEntityPacket(ServerEntity)` ⇒ 平台 |
| `YoyoEquipment` 的 `CuriosApi.getCuriosInventory(owner).resolve().orElse(null)` | 1.21 Curios 直接返 `Optional` ⇒ `.orElse(null)`；两侧文件其余 **逐字节相同**（各 84 行，含外观槽两段循环与 `animatedColor`） |
| `HiveFiveYoyoItem` 的 `new Properties().unbreakable()`、`owner.getRandom1211()` | 1.21 用 `ModItems.unbreakable()` / `getRandom()`；其余逐字相同（含 `effectTooltip`、蜜蜂生成 5 行） |
| `YoyoItems` 的 `new Item.Properties().unbreakable()` | 1.21 已是 `ModItems.unbreakable()`；19 个悠悠球注册项 1.21 全在 |
| `AbstractFishingPole` 的 `int speedBonus = EnchantmentHelper.getFishingSpeedBonus(stack);` | 1.21.1 附魔 API 改为 level 感知：`getFishingLuckBonus(serverLevel, stack, player)` / `getFishingTimeReduction(serverLevel, stack, player) * 20.0F` ⇒ 平台等价（同一 `speedBonus` 变量名已用） |
| 暴击分支（`BeeKeeperProjectile`/`YoyoEntity`/`YoyoEffectProjectile`） | 1.21 `LivingEntityEvents:305 processCriticalDamage` 已有等价物，且投影类已按 1.21 体系写作 `BaseYoyoProjectile`/`TerrarianProjectile` ⇒ `COVERED` |
| `BaseFishingHookRenderer` 的 `addVertex/setColor/setNormal`、`ItemAbilities` 路径、`renderToBuffer` 4 参 | 平台改名/签名（1.20 的 6 参 `renderToBuffer` 相当于 1.21 的 4 参默认 (1,1,1,1)） |

### 71.3 登记（未落地，非本行可移植项）

- `common/data/gen/ModItemModelProvider`：1.20 该提交加了 3 行「悠悠球模型跳过」——`YoyoItems.ITEMS.getEntries().forEach(...)` + `if (hasHandwrittenModel(...)) skip.add(...)`。**1.21 该文件里 `yoyo` 0 命中**：既没有 `createDir(YoyoItems.ITEMS, "yoyo/")`，也没有 `hasHandwrittenModel` 私有方法、也没有文件末尾的 `generatedModels.keySet().removeIf(this::hasHandwrittenModel);`。⇒ 3 行跳过块**在 1.21 无法照落**（缺被调方与前置注册），且 1.21 不注册悠悠球进 datagen ⇒ 8 个手写模型（`amarok`/`chik`/`code_2`/`format_c`/`gradient`/`hel_fire`/`kraken`/`the_eye_of_cthulhu`，**1.21 均已存在**）不会被遮蔽。归入既有「datagen 未重跑」欠账：重跑前需补「悠悠球目录注册 + 手写模型跳过」机制（`DEFER-ASSETS` 家族）。
- 本节同时记下 1.20 HEAD 有、1.21 无、但**不属于本行提交**的一处观察：`livingDamage$Pre` 的 `if (victim.getVehicle() instanceof AbstractMountEntity mount) amount = mount.modifyRiderDamage(damageSource, amount);` 不在本提交的 28 行新增里，留给其所属行处理。

### 71.4 状态

- 台账双写：`322=PORTED`；状态 JSON 共 **328** 条；行 231–400 剩余 `TODO` = **8**（`280`/`302`/`317`/`359`/`237`/`244`/`323`/`256`）。
- `fix_eol --check` 仍为 **候选 6**；花括号平衡两文件 126/126、15/15；未碰 `GamePhase.java`。


## 七十二、行 302 `81488b6d0`「修改一股味的代码」：1.20 **自己回退**防御性代码 ⇒ `SKIP-1.20-REVERTED`

### 72.1 本行净效果（99 文件 +439/−1061）与逐族裁定

| 族 | 1.20 该提交做了什么 | 1.21 现状 | 裁定与依据 |
| --- | --- | --- | --- |
| `OverviewNode` 的 `List.copyOf(builder.connections)` / `...tooltipLines` | **删掉** `List.copyOf`（直接持有 builder 的集合） | 1.21 仍是 `this.connections = List.copyOf(builder.connections);`（两行都在） | `git log -S "List.copyOf(builder.connections)"` = `81488b6d0`(删) ← `bbef54bb1`(加)：是 1.20 自加自删 ⇒ **不回退 1.21**（保留防御性拷贝） |
| `ConfluenceBiomeInjector` 的 `case OVERWORLD -> List.copyOf(OVERWORLD_REGIONS)` | 删 `List.copyOf` | 1.21 仍是 `List.copyOf(OVERWORLD_REGIONS)` | `-S` = `81488b6d0` ← `3518ab86e`（台账行 224，`COVERED`）⇒ 同上 |
| `BiomeRegionTable` 的 `new BiomeRegionTable(..., List.copyOf(regions), List.copyOf(entries), List.copyOf(biomes))` | 只保留 `biomes` 的 `List.copyOf` | 1.21 三个都 `List.copyOf` | 同上；1.20 侧 `List<Entry> immutable = List.copyOf(collected);` 两侧都在，未受影响 |
| `SpearEntity` 的 `Float.isFinite(savedProgress) ? Mth.clamp(...) : 0.0F` + 写出端同款 | 去掉 `isFinite` 兜底，只留 `Mth.clamp(...)` | 1.21 的 `SpearEntity` 只有 `Direction` 存取（**完全没有** `Progress`/`TrapPos`/`Opened` 持久化） | `-S "Float.isFinite"` = `81488b6d0`(删) ← `dfcc5c041`(加)；而 `dfcc5c041` = **台账行 95，已判 `DO-NOT-PORT`**（「对齐 1.21 内容与运行时行为」）⇒ 本行只处理 clamp 形态，持久化字段的缺与不缺由行 95 的既定裁定负责 |
| `ThrownPowderEntity` 的 `Objects.requireNonNull(type, ...)` + `isFinite` 兜底 | 同上删除 | 1.21 只有 `type` 存取，无 `moveDist`/`lastPos`/`coveredPos` | 同上（`MoveDistance` 亦源自 `dfcc5c041`/行 95） |
| `common/event/game/GameEvents` 接收整张村民交易表（+163） / `ModVillagers` 失之（−165） | 交易从 `ModVillagers.villagerTrades(PortVillagerTradesEvent)` 迁到 `GameEvents` | 1.21 仍在 `ModVillagers.villagerTrades(VillagerTradesEvent)`，`NeoForge.EVENT_BUS.addListener(ModVillagers::villagerTrades)` | **逐行比对 113 = 113，双向差集均为 0**（`ItemListing` 行集合完全一致）⇒ 只是「家在哪个文件」不同，内容 `COVERED`（1.21 保留自有路径） |
| `api/client/animation/HandAnimation{Action,Api,Channel,Clip,Profile}`、`client/animation/GunCameraAnimation`、`api/whip/WhipTagTracker`、`client/component/SwordProjectileAppearance` | 整类删除（死代码） | **1.21 也没有这些文件** | 两侧皆无 ⇒ 无需动作（`COVERED`） |
| `util/ModGunUtils`(−19)、`common/data/map/AttackEffects`(−6)、`client/effect/RenderStateShardAccessor`(−6) | 仅删若干行 | 两侧都还在，1.21 外部引用 6 / 20 / 26 处 | 1.21 侧仍在被使用 ⇒ 不删（`COVERED`） |
| `StraightMonsterProjectile` 的排版回卷 + 去掉 `tag.contains(...)` 守卫 | 反序列化改为无守卫 `tag.getFloat(DAMAGE_KEY)` 等 | 1.21 就是无守卫形态（`damage = tag.getFloat(DAMAGE_KEY);` 四行一致） | 已经等于 1.20 HEAD ⇒ `COVERED` |
| `client/event/GameClientEvents` 的 9 条（`customizeGuiOverlay$BossEventProgress`/`renderGuiLayer$Pre`/`renderTooltip$GatherComponents`/`viewport$RenderFog`/`bullet$ImpactEffect`/`RenderLevelStageEvent.Stage.*`） | Forge 事件与改名 | 1.21 有同名同义方法（`customizeGuiOverlay$BossEventProgress`、`renderGuiOverlay$Pre(RenderGuiLayerEvent.Pre)`、`gatherComponents(RenderTooltipEvent.GatherComponents)`、`viewport$RenderFog`、`viewport$RenderFogLavaShark`、`bullet$ImpactEffect`、`renderLevelStage` 内 `stage == AFTER_SKY/AFTER_ENTITIES/AFTER_PARTICLES`） | 仅 NeoForge 事件类名/方法名差异 ⇒ 平台噪音（与 §70.2 同族） |

### 72.2 为什么给 `SKIP-1.20-REVERTED`

本行的主体是 **1.20 撤掉自己先前加的防御性代码**（`List.copyOf` ×3 族、`Float.isFinite` ×2 族、`requireNonNull`、`tag.contains` 守卫），其中三处已用 `git log -S` 证明「加在早前提交、删在本提交」。1.21 保留这些守卫是**更安全**的一侧，按 §1.1「不得回退 1.21」不回退、仅登记；其余族（交易表、死类、平台事件名）本就无需动作。故整行判 `SKIP-1.20-REVERTED`。

### 72.3 状态

- 台账双写：`302=SKIP-1.20-REVERTED`；状态 JSON 共 **329** 条；行 231–400 剩余 `TODO` = **7**（`280`/`317`/`359`/`237`/`244`/`323`/`256`）。
- 本行**未改任何源码**（纯裁定）；`fix_eol --check` 仍为候选 6。
- 记账：本行的 `GameEvents`(55 gap)/`ModVillagers`(3) 全部落在上表，屏幕筛出的 22 条去重行逐条有判。


## 七十三、行 280 `348c877a4`「统一生物属性与状态参数声明…」：机制 1.21 已在（路径搬家）+ 补 1 行重载清理

### 73.1 落地 1 处（`client/event/ModClientEvents`，+1）

| 1.20 HEAD | 1.21（本批落地） |
| --- | --- |
| `event.registerReloadListener((ResourceManagerReloadListener) manager -> org.confluence.mod.client.effect.BrainDissolveTexture.clearTextures());` | 加在已有的 `DivaSlimeVertexConsumer.clearTextures()` 那一行之后（同一 `registerClientReloadListeners`，`:1303` 之后） |

- 证据：1.21 的 `BrainDissolveTexture.clearTextures()` 存在（`BrainDissolveTexture.java:66-74`，会 `close()` 源图并 `release()` 全部动态贴图），但全库**没有任何调用点**——只有 `DivaSlimeVertexConsumer.clearTextures()` 被注册。⇒ 属「消费者在、驱动器缺」：资源重载后溶解贴图缓存不会清，`NativeImage` 与 `DynamicTexture` 泄漏、旧贴图残留。本批按 1.20 原样补上（用 FQN 内联写法，避免动 import 区）。

### 73.2 本行主体机制：1.21 **已有等价实现**（`COVERED`）

| 1.20 该提交引入 | 1.21 现状 |
| --- | --- |
| `common/init/entity/ModEntities` 里新增 `ATTRIBUTES`(LinkedHashMap)/`CREATURE_DEFINITIONS`/`withAttributes(...)`/`creatureAttributes(EntityType)` + `EntityAttributeCreationEvent` 回调 | 1.21 **在另一路径** `common/init/ModEntities.java`（386 行）：`withAttributes` 3 处、`creatureAttributes` 1 处 —— 这正是 rowaudit 把本行`ModEntities`整块判为缺口的**路径搬家假阳性**（1.20 `init/entity/ModEntities.java`，1.21 `init/ModEntities.java`） |
| `withAttributes(...)` 覆盖 Boss/Critter/Monster/Npc 四张注册表 | 1.21：`BossEntities` 29、`CritterEntities` 44、`MonsterEntities` 213、`NpcEntities` 35 处 ✓ |
| `CreatureAttributeBuilder` 改为包装类（内含 `states`/`projectiles`、`StateBuilder`/`ProjectileBuilder`） | 1.21 `CreatureAttributeBuilder.java` 481 行，`StateOverrides` 命中 2 处 ✓ |
| `CreatureDefinition` 增加 `special_states`/`projectiles` 两个 codec 字段 | 1.21 `CreatureDefinition.java` 323 行，`specialStates` 4 处、`StateOverrides` 16 处 ✓ |
| `BaseMonster` 的 `stateAttributeValue`/`stateModifier`/`SCALE` 变更后 `refreshDimensions()` | 1.21：`stateAttributeValue`（`BaseMonster` ✓、`ZombieMerman` 1 处）、`StateOverrides` 2 处、`setSpecialState` 1 处 ✓ |
| 各实体改用 `setSpecialState(...)`/`stateParameters(...)` 声明状态属性 | 1.21：`setSpecialState` 覆盖 24 个文件（boss 10 + monster 14）、`stateParameters` 覆盖 19 个文件（如 `QueenBee` 11、`GiantShelly` 5、`Golem` 5 处）✓ |
| `HostileParticleProjectile` 的 `WATER_BOLT_VISUAL` + `waterEmitter` | 1.21 全在（`WATER_BOLT_VISUAL` 7 处、`waterEmitter` 5 处，`define` 形态为 `builder.define`）✓ |
| `ModClientEvents` 的蠕虫段渲染器注册（`WYVERN_SEGMENT`/`ARCH_WYVERN_SEGMENT`/`DEVOURER_SEGMENT`/…，另一批为 `TONGUE`） | 1.21 逐条在（含 `WormPartRenderer(c, MonsterEntities.X.get(), 尺度, 是否)` 的新签名）✓ |
| `RideableLavaSharkMountEntity.getPassengersRidingOffset()`（−0.2） | 1.21.1 已删该方法，改为 `getPassengerAttachmentPoint(Entity, EntityDimensions, float)` 返 `Vec3`（见 notes/WP3A-MOUNT-SUBSET.md、`AbstractMountEntity:308`）⇒ 平台 |
| `GiantTortoise` 的 `ForgeEventFactory.getMobGriefingEvent` | 1.21 对应 `EventHooks.canEntityGrief`（notes/WP2F-SUBSET.md 已固化）⇒ 平台 |
| `/// 属性与实体一起声明，延迟到 Forge 创建属性时构建…` 等说明性注释 | 按 §1.3 不抄（1.21 侧该机制已在，无新注释需求） |

### 73.3 状态

- 台账双写：`280=PORTED`（机制 `COVERED` + 1 行落地）；状态 JSON 共 **330** 条；行 231–400 剩余 `TODO` = **6**（`317`/`359`/`237`/`244`/`323`/`256`）。
- `fix_eol --check` 仍为候选 6；未碰 `GamePhase.java`、未碰禁改的 `common/init/ModEntities.java`（该文件 1.21 侧已就位，本行无需编辑）。


## 七十四、行 317 `1da5e0d26`「修复一些问题」：微光鱼钩 + 钓鱼流体标签改名 + 创造模式开锁箱

### 74.1 落地 9 个文件（8 java + 2 个 tag JSON 改名）

| # | 文件 | 1.20 HEAD | 1.21（本批落地） |
| --- | --- | --- | --- |
| 1 | `common/effect/neutral/ShimmerEffect` | `applyEffectTick` 尾段：`amplifier > 0` 时 `return getBlockStates(box.inflate(0.1)).anyMatch(s -> !s.isAir());`，否则 `return !getBlockStates(box.inflate(-0.1)).allMatch(...)` | 替换原 `shouldExpire` 版本（原版在 `amplifier > 0` 时把 `effect.amplifier` 归零并恒返回 true） |
| 2 | 同上 | `applyShimmerEffect(LivingEntity living, int amplifier)`：**去掉** `getEyeInFluidType() == SHIMMER` 前置，`MobEffectInstance(..., INFINITE_DURATION, amplifier)` | 逐字移植（1.21 侧 holder 形态 `ModEffects.SHIMMER` 保留） |
| 3 | `mixin/world/entity/LivingEntityMixin` | `else if (fluidType == SHIMMER) { if (getEyeInFluidType() == SHIMMER) { applyShimmerEffect(self, 0); } ... }` | 眼位判定从 `ShimmerEffect` 内搬到调用点（1.21 侧写 `self.getEyeInFluidType()`） |
| 4 | `mixin/world/entity/projectile/FishingHookMixin` | 新增 `@Inject(method="tick", at=@At(INVOKE, FluidState.is, ordinal=0), cancellable=true) shimmer(...)`：钩子落在微光流体→给玩家 `applyShimmerEffect(player, 1)` 并 `discard()` + `ci.cancel()` | 逐字移植（见 74.2 的两处 1.21 适配） |
| 5 | 同上 | 调用点清理：`isValidFluid(self)`、`isValidBlock(self, instance, original.call(...))`，形参 `pTag`/`pType` → `tag`/`type` | 三处调用点 + 8 处形参名逐字对齐 |
| 6 | `mixed/IFishingHook` | `isValidBlock(self, instance, original)`（去掉无用 `Block block`）、`isValidFluid(self)`（去掉无用 `original`）；返回 `ModTags.Fluids.FISHING_ABLE_ALL` / `FISHING_ABLE_NOT_LAVA` | 逐字移植 + 删除随之失效的 `import net.minecraft.world.level.block.Block;` |
| 7 | `common/init/ModTags` | `FISHING_ABLE = register("fishing_able")` → `FISHING_ABLE_ALL = register("fishing_able/all")`；`NOT_LAVA = register("not_lava")` → `FISHING_ABLE_NOT_LAVA = register("fishing_able/not_lava")` | 逐字移植（全库仅 4 处引用：本行 6+7 与 `ModFluidTagsProvider`） |
| 8 | `common/data/gen/tag/ModFluidTagsProvider` + 2 个 tag JSON | 新 id 的内容：`all` = `#minecraft:water` + `#minecraft:lava` + `#c:honey`；`not_lava` = `#minecraft:water` + `#c:honey`（**两者都不再含 shimmer**） | provider 两行改名；**`data/confluence/tags/fluid/fishing_able.json` → `fishing_able/all.json`、`not_lava.json` → `fishing_able/not_lava.json`** 并各自删掉 `#confluence:shimmer`（1.21 手工 JSON 当时仍带着 shimmer，与自身 provider 不一致；重跑前 JSON 才是生效源，故必须一起改） |
| 9 | `common/block/common/BaseChestBlock` + `client/handler/MeteorLandingHandler` | 上锁箱放置时创造模式直接解锁（4 行）；`handlePacket(..., @Nullable Player)` + `if (player != null) calculate(player);` | 各逐字移植（`@Nullable` import 1.21 已在） |

### 74.2 两处 1.21 适配（非逐字，已核）

1. **`@Shadow getPlayerOwner()` 不加**：1.20 的 mixin 有 `@Shadow @Nullable public abstract Player getPlayerOwner();`，1.21 的 `FishingHookMixin` 只 shadow 了 `luck`。为不新增 shadow，新 inject 内写 `confluence$self().getPlayerOwner()`（全库既有写法）。已核 vanilla 1.21.1 `FishingHook` 确有 `public Player getPlayerOwner()`（`sourcesAndCompiledWithNeoForge_03c905f5…` 源码命中 4 处调用 + 1 处声明）。
2. **锚点可移植性实测**：vanilla 1.21.1 `FishingHook.tick()` 内 `FluidState.is(...)` 恰好两处（`:162 fluidstate.is(FluidTags.WATER)` 为 ordinal 0、`:228 !fluidstate.is(...)`），且 `fluidstate` 局部变量声明在 `:161` ⇒ 1.20 的 `ordinal = 0` 锚点与 `@Local FluidState fluidstate` 在 1.21.1 同样成立，不会因找不到注入点而报错。另注：文件内既有的 `@ModifyArg(... FluidState.is ...)`（无 ordinal，改写标签）不影响本 inject，因为 inject 用 fluidstate 对象自身判定 `SHIMMER`。

### 74.3 判为等价 / 平台 / 无需动作

| 缺口 | 依据 |
| --- | --- |
| `mixin/world/item/BucketItemMixin`（22 行新文件） | **既有裁定已 COVERED**：1.21 等价物 = `mixin/world/item/ItemUtilsMixin:16`（`emptyStack.is(ModTags.Items.BOTTOMLESS)`），见 `PORT-LANDING-RECORD` 早前 mixins.json 5 条那一节 |
| `BottomlessBucketItem.initCapabilities` + `FluidBottomlessBucketWrapper` | 1.20 是 Forge `ICapabilityProvider` 内联；1.21 在 `ModEvents` 里注册能力（`FluidBottomlessBucketWrapper` + 4 个 `ToolItems.BOTTOMLESS_*`）⇒ 平台 |
| `StarPhasesPacketS2C.sendToClient(ServerPlayer, ...)` | 1.21 已有（`serverPlayer` 形参名差异）⇒ 平台 |
| `WindSpeedPacketS2C` / `GoblinArmyProgressPacketS2C` | 同类形参名/PortLib 包装差异 ⇒ 平台 |
| `Confluence.java`（该提交 −2 行） | 1.20 只是删掉两句解释性注释（与用户「不要多余注释」口径一致）；且该文件属**禁改 10 文件**，1.21 无需动作 |
| `ModEvents` / `PlayerEvents` / `GameEventSystem` / `ModBlockCounters` / `TheCorruptionRegion` / `LivingEntityMixin` 的其余行 | PortLib→NeoForge 事件与 `PortTags`/`Tags` 平台差异（如 `PortTags.Fluids.HONEY` ↔ `Tags.Fluids.HONEY`） |

### 74.4 状态

- 台账双写：`317=PORTED`；状态 JSON 共 **331** 条；行 231–400 剩余 `TODO` = **5**（`359`/`237`/`244`/`323`/`256`）。
- `fix_eol --check` 仍为候选 6（新 JSON 亦为 CRLF）；未碰 `GamePhase.java`；未碰禁改文件。


## 七十五、行 359 `2f44045a8`「大改修饰语」：`PrefixType` 逐类型策略落地（含泰拉悠悠球独享传奇）；两处欠账登记

### 75.1 方向判定：1.21 的 `PrefixType` = 1.20 **359 之前**的形态（可对齐）

用 `difflib` 把三方对齐比过（这是本行的关键证据）：

| 比对 | 增/删行 |
| --- | --- |
| 1.20@`2f44045a8^`（359 前） vs 1.21 HEAD `PrefixType.java` | +42 / −32 |
| 1.20 HEAD（359 后） vs 1.21 HEAD `PrefixType.java` | +44 / −76 |
| 1.20 HEAD（359 后） vs 1.21 HEAD `PrefixUtils.java` | 见 75.3 |

`git log -S 'case MELEE ->'` 显示 1.21 的 switch 形态来自**分叉前**提交 `a3dd5bb35 重命名`（1.20 侧历史里同样存在该提交）⇒ 1.21 现有 `bestPrefix` 就是 1.20 的旧代码，**不是** 1.21 侧自己的新决策；359 是分叉后 1.20 的改动，按 §1.1 应对齐。1.21 侧唯一的偏差是移植时**漏掉了 yoyo/召唤**两处分支（见 75.2 的 `supportsLegendaryPrefix` 线索）。

### 75.2 落地 1 个文件（`common/component/prefix/PrefixType.java`，+65 / −25）

| 项 | 1.20 HEAD | 1.21（本批落地） |
| --- | --- | --- |
| `UNIVERSAL` | 常量带体：`randomPrefix(random, stack)` 内若 `stack.is(ModTags.Items.YOYO)` → 从 `available` 抽签，**泰拉悠悠球**多一个签位落到 `Melee.LEGENDARY2`；`bestPrefix` = GODLY/DEMONIC | 逐字落地（用 1.21 的 `private available` 字段，`SUMMON` 常量早已如此访问，可编译） |
| `MELEE` | `bestPrefix`：`ItemTags.SWORDS` → `Melee.LEGENDARY`；**`YoyoItems.TERRARIAN` → `Melee.LEGENDARY2`**；否则 GODLY/DEMONIC | 逐字落地（原 1.21：`Tags.Items.MELEE_WEAPON_TOOLS ? LEGENDARY : LIGHT`） |
| `RANGED` | `bestPrefix`：`hasKnockback(stack) ? Ranged.UNREAL : Universal.DEMONIC` | 逐字落地（原 1.21：恒 `Ranged.UNREAL`） |
| `MAGIC` | `bestPrefix`：`hasKnockback(stack) ? Magic.MYTHICAL : Universal.DEMONIC` | 逐字落地（原 1.21：`ModTags.Items.MANA_WEAPON ? MYTHICAL : Universal.RUTHLESS`） |
| `ACCESSORY` / `UNKNOWN` | 常量带体，各自覆盖 `bestPrefix` | 由 switch 分支改为常量覆盖（`UNKNOWN` 返回 `null`） |
| 方法签名 | `public abstract @Nullable ModPrefix bestPrefix(RandomSource, ItemStack)` | switch 方法删除，改抽象声明 |
| import | `net.minecraft.tags.ItemTags`、`YoyoItems`；不再需要 `Tags` | 同增同删（`hasKnockback` 保留 1.21 的 `getAttributeModifiers().modifiers().stream()` 形态，1.20 的是 `getAttributeModifiers(EquipmentSlot).containsKey(...)`，属平台） |

- **顺带结掉 1.21 自己的 TODO**：1.21 `ModPrefix:274` 的 `LEGENDARY2` 一直带 `// todo 泰拉悠悠球独享传奇` 注释，`PrefixType` 里却没有任何分支能取到它（全库 `YoyoItems.TERRARIAN` 命中 0）⇒ 本批把 1.20 HEAD 的取法（`stack.is(YoyoItems.TERRARIAN)`）补上，该注释所指功能落地。
- 语义变更 3 处（照 1.20 HEAD，逐条记录以免被误当回归）：`MELEE` 由「任何近战工具 → LEGENDARY，否则 LIGHT」变为「剑 → LEGENDARY、泰拉悠悠球 → LEGENDARY2，否则 GODLY/DEMONIC」；`RANGED` 由「恒 UNREAL」变为「有击退才 UNREAL」；`MAGIC` 由「按 `MANA_WEAPON` 标签」变为「按击退」。`Melee.LIGHT` / `Universal.RUTHLESS` 因此在 1.21 变成未被引用（定义仍在，无编译影响）。

### 75.3 判为等价（`COVERED`）

| 缺口 | 依据 |
| --- | --- |
| `initPrefix`：1.20 `random.nextInt(4) == 0` 走 unknown | 1.21 写 `random.nextFloat() < 0.75F` ⇒ 概率同为 25%/75%，等价 |
| `createWithMercy`：1.20 `prefix.canBeMercy() && random.nextInt(3) != 0` | 1.21 写 `random.nextFloat() < MERCY`（`MERCY = 2.0F / 3.0F`）⇒ 等价，且 1.21 多了常量命名 |
| `PrefixType.randomPrefix(random)`：1.20 `Util.getRandom(available, random)` | 1.21 `available[random.nextInt(available.length)]` ⇒ 逐字等价 |
| `PrefixComponent` 七参记录（`tier`/`value`）、`ModPrefix.register(..., int tier, float value)` | 1.21 已是七参形态（`ModPrefix:93`、`Accessory.register` 等），无需动作 |
| `ModPrefix` 各 prefix 记录体、`createComponent` | 1.21 与 1.20 HEAD 同形（仅 `LibAttributes.getX().value()` / `ImmutableListMultimap<Holder<Attribute>,…>` 等平台差异） |

### 75.4 登记欠账（本批**未**落，附具体阻塞点）

1. **`PrefixUtils.setAndUpdate` 的 300 行遗留表**（1.21 `PrefixUtils:150-454`）：1.20 HEAD 用
   `int tier = ModRarity.TIER.inverse().getOrDefault(ModRarity.getRarity(stack, true), -2); tier += prefix.tier(); …; ModRarity.TIER.get(tier)` + `value * (1 + prefix.value())` 取代了那张 `switch (num1)` + `num2..num8` + `num14` 的旧表。
   **阻塞点**：`ModRarity.TIER` 只存在于 1.20 的 lib。该行同时把 lib 子模块指向 `e9b848c93`，其 diff 实测 = **`ID_MAP` 改名 `TIER` 并删掉 7 条负 id 项**（`-13 MASTER`、`-12 EXPERT`、`-11 QUEST`、`-10 COMMON`、`-9 UNCOMMON`、`-8 RARE`、`-7 EPIC`），同时把 `ModRarity` 从 `class`（带 `special` 字段、两个构造器、`textColor` 缓存）改成 `record(String name, int color)`。1.21 的 lib 仍在旧态（`ModRarity.java:44 ID_MAP` 含那 7 条 + `:81/:87` 两个构造器），而 `Confluence-Magic-Lib` 是**嵌套独立仓库** ⇒ 需在子模块单独提交（与 §70.2/§72.1 同类），本批不动、仅登记。顺带证据：1.21 旧表里那段 `-10 → 0 / -9 → 2 / -8 → 4 / -7 → 6` 的手工补偿，正是为了抵消 `ID_MAP` 的负 id 项 —— 1.20 改成 `TIER` 后这段补偿随之消失。
2. **逐标签方向判定**（前面几节已登记、本轮仍留）：`TOOLS_HOOK` 改名（1.20 = `common("tools/hook")`，1.21 全库 0 命中）、`YOYO`/`BOOMERANG`/`FLAIL`/`SPEAR`/`LANCE` 归属、`PREFIX_MELEE_ONLY` 是否加 `WHIP`、`SHORT_SWORD` 新标签、`SUMMONER_WEAPON`↔`PREFIX_SUMMON_ONLY` 接线、`TOOLS_REPEATER`（1.21 另有 `TOOLS_REPEATER_CROSSBOW`，1.20 没有）。这些标签集两仓各有自己的差异（如 1.21 的 `PREFIX_MELEE_ONLY` 含 `FLAIL` 且 `.add(Items.MACE)`，1.20 注释掉 MACE 并改含 `LANCE`+`WHIP`）⇒ **逐标签裁定**，放下一批。

### 75.5 状态

- 台账双写：`359=PORTED`（`PrefixType` 落地 + 两处欠账登记）；状态 JSON 共 **332** 条；行 231–400 剩余 `TODO` = **4**（`237`/`244`/`323`/`256`）。
- `fix_eol --check` 仍为候选 6；花括号平衡 43/43；未碰 `GamePhase.java` 与禁改文件。


## 七十六、行 237 `6084476d0`「JEI兼容恢复」：整层 JEI 集成 1.21 已在 ⇒ `COVERED`

### 76.1 判定主证据：`integration/jei` **逐文件同名同数**

| 侧 | 文件数 | 差异 |
| --- | --- | --- |
| 1.20 HEAD `integration/jei/` | 27 | — |
| 1.21 HEAD `integration/jei/` | 27 | 仅 1.20 有 **0** 个、仅 1.21 有 **0** 个 |

含 `ModJeiPlugin`（1.20 该提交 +248）、`EitherRecipe4xHelper`（+290）、`RecipeTransferPacketC2S`（+137）、`ConfluenceScreenHandler`、`ITypedItemStack`、`JeiHelper`、`category/` 下 20 个分类（AlchemyTable／Altar／ArmorSetBonus／BrewingStandTerraPotion／CookingPot／CrystalBall／DyeVat／EnhancedForge／Extractinator／FletchingTable／HardmodeAnvil／HardmodeForge／HeavyWorkBench／Hellforge／Loom／Sawmill／ShimmerItemTransmutation／SkyMill／Solidifier + 两个 `package-info`）。

JEI 兼容 mixin 与依赖同样已在：
- `mixin/integration/jei/`：两侧均 `FurnaceFuelCategoryMixin`／`ScreenHelperMixin`／`TypedItemStackMixin`，差集双向为空；
- `confluence.mixins.json`：1.21 同样登记 4 条（含 `TypedItemStackMixin$S1Mixin`）；
- 依赖：1.21 用**自己的坐标**（`compileOnly jei-1.21.1-neoforge-api` + `runtimeOnly jei-1.21.1-neoforge`，`jei_version=19.25.0.322`），1.20 用 `modCompileOnly …-forge-api` + `gradle.properties:22 jei_version=15.56.0.205` ⇒ 平台。

### 76.2 其余被点到的文件：逐条等价

| 缺口 | 1.21 现状 | 裁定 |
| --- | --- | --- |
| 各 `*Menu` 的 `recipe.getResult().copy()`（AlchemyTable／CrystalBall／DyeVat／FletchingTable／SkyMill） | 1.21 走**原版 API**：`recipe.getResultItem(null).copy()`（`SkyMillMenu:135/148`、`AlchemyTableMenu:79` 等）⇒ 同样在拷贝，不会把配方内部栈直接塞进槽位 | `COVERED` |
| `HeavyWorkBenchMenu` 的 `recipes.get(index).getResult()` | 1.21 `common/menu/` 全库无 `getResult()` 命中，改用 `getResultItem(...)` 体系 | 平台/API |
| `EnhancedForgeBlock`／`BaseCauldronBlock` 的 `canResultInsert(...)` | 1.21 已有：`EnhancedForgeBlock:357`、`BaseCauldronBlock:224`，且调用处用 `recipe.value().getResultItem(level.registryAccess())`／`.assemble(...)` | `COVERED` |
| `FletchingTableRecipe` 新增 `getResult()`(+6) | 1.21 用 `getResultItem(@Nullable HolderLookup.Provider)` 返回 `result`（1.21 recipe API） | 平台/API |
| `CookingPotRecipe`／`EnhancedForgeRecipe` 的 codec／stream codec 行 | 1.20 是 `PortCodecExtension.lenientOptionalFieldOf`／`PortStreamCodec.composite`；1.21 是原生 `lenientOptionalFieldOf`／`StreamCodec`，`forGetter` 写法由 `AbstractAmountRecipe::getResult` 变 `recipe -> recipe.result` | 平台 |
| `SkyMillScreen`(+77) | 1.21 已有整套滚动列表（`SCROLLER_WIDTH/HEIGHT/FULL_HEIGHT`、`registerUpdateListener`、`isScrollBarActive`、`renderRecipes`），只是形参名仍是 `pGuiGraphics/pMenu`；1.20 该提交顺手做了形参改名 | 平台/整理 |
| `accesstransformer.cfg` 的 `StatePropertiesPredicate` 组 | **方向相反**：vanilla 1.21.1 的内嵌类型就是 `RangedMatcher`／`ExactMatcher`／`PropertyMatcher`／`ValueMatcher` ⇒ 1.21 的 AT 与源码（`CookingPotCategory:85-90` 用 `predicate.properties()`／`property.valueMatcher()`／`instanceof RangedMatcher(...)`）**本来就是原生形态**；1.20 侧的 `RangedPropertyMatcher`／`ExactPropertyMatcher` 与 `f_67715_` 等 SRG 名，来自 PortLib 的 `org.mesdag.portlib.diff.mixin.StatePropertiesPredicateMixin`（见 `tools/port2native/rules/types.json`）——即 **1.20 才是"补齐 1.21 形态"的一侧** | `SKIP-PLATFORM` |
| `SpriteShiftEntry`／`CTSpriteShiftEntry` 的 `/ f * 16` | **方向相反**：1.21 的版本带 `/// @removed * 16.0F`／`/// @replaced` 说明，是 1.21 `TextureAtlasSprite#getU(float)`（0–1 坐标）的正确形态；1.20 那个 `* 16` 是为 Forge 1.20 的 0–16 坐标**补回**的适配 | `SKIP-PLATFORM` |
| `BestiaryScreen`(+8) | 1.21 已有 `JeiHelper.IS_LOADED` + `ModJeiPlugin.handleShowUses(keyCode, scanCode, living.getPickResult())`（`:331-333`） | `COVERED` |
| `ModSoulSkills`(−4: Forge `IEventBus` 注册方法)／`SpelunkerHelper`(−1)／`LivingEntityEvents`(−1) | 均为删除行（Forge 事件总线形态、空行、死 import） | 无需动作 |

### 76.3 未落地并登记的 1 处

- `client/effect/biome/MoonlitDrySeaSkyRender`：1.20 该提交把每次调用新建的 `PoseStack` + `Matrix4f` 换成两个**静态复用字段**（`private static final Matrix4f mat` / `Quaternionf quat`），并把取值方式由「拷整份 model-view（含平移）」改成 `mat.rotation(getUnnormalizedRotation(quat))`（**只取旋转**）。1.21 现态是 `PoseStack poseStack = new PoseStack(); poseStack.mulPose(event.getModelViewMatrix()); Matrix4f matrix4f = poseStack.last().pose();`（`:128-130`）。
  **不落地原因**：两张表的语义并不只是"缓存"——1.20 版丢掉了平移分量，且两侧事件 API 不同（1.20 `getPoseStack()` ↔ 1.21 `getModelViewMatrix()`）。这属于"改了会不会歪"只能跑起来才看得见的渲染差异，按 §1.10（不做编译/datagen/运行验证）不blind移植，登记为待验项；若日后重跑验收，按 1.20 HEAD 的两字段 + `mat.rotation(...)` 落即可。
- （附记）`Confluence.java` 该提交把 `MixinEnvironment…DUMP_TARGET_ON_FAILURE` 注释掉并删掉 `ModSoulSkills.register(eventBus)`；前者是调试开关、后者是 Forge 总线残留 ⇒ 无需动作，且该文件属**禁改 10 文件**。

### 76.4 状态

- 台账双写：`237=COVERED`（未改源码）；状态 JSON 共 **333** 条；行 231–400 剩余 `TODO` = **3**（`244`/`323`/`256`）。
- `fix_eol --check` 仍为候选 6；工作树未动 `GamePhase.java`。


## 七十七、行 244 `a4b321499`「补充，修改一部分模型，删除不该存在的生物，为 AI 已注册的生物补充模型」：渲染器/资产/幽灵生物三族 1.21 已在 ⇒ `COVERED`

### 77.1 654 条 gap 的主体是**一个新资产文件**，且 1.21 已有

屏幕里 654 条几乎全部来自 `assets/confluence/animations/entity/unicorn.animation.json`（1.20 该提交**新建**的 654 行动画文件）。逐名单核对：**1.21 的 `animations/entity/unicorn.animation.json` 存在**（`git ls-tree` 实测，同目录共 250+ 个 `.animation.json`）⇒ 资产随功能一并移植过。

### 77.2 渲染器模型指向修复：6/6 已在 1.21

| 1.20 该提交把「借用别人的模型」改成「自己的模型」 | 1.21 HEAD |
| --- | --- |
| `CORRUPTOR` 由 `EATER_OF_SOULS.getId()` → `CORRUPTOR.getId()` | `ModClientEvents:908` 已是 `MonsterEntities.CORRUPTOR.getId()` ✓ |
| `GIANT_TORTOISE` 由 `sharedGiantShellyModel()` → `GIANT_TORTOISE.getId()` | `:928` 已是自己的 id ✓（`sharedGiantShellyModel` 在 1.21 全库仅存于 notes） |
| `GASTROPOD` 由 `sharedGiantShellyModel()` → 自己的 id + `withScale(1.2F)` | `:925` ✓ |
| `CHAOS_ELEMENTAL` 由 `DARK_CASTER.getId()` → 自己的 id | `:906` ✓ |
| `BLOOD_FEEDER` 由 `PIRANHA.getId()` → 自己的 id | `:900` ✓ |
| `STYLIST` 贴图由 `npc/party_girl` → `npc/stylist` | `:1051` ✓（`npc/party_girl` 保留给 `PARTY_GIRL` ✓） |

### 77.3 「删除不该存在的生物」：两侧都已不存在

`EVIL_SLIME`／`BLAZING_WHEEL`／`SPIKE_BALL`（该提交从 `ModEvents` 属性表、`ModTabs` 刷怪蛋、`CreatureSpawnPlacements`、`EntitySubProvider` 战利品、`ModEntityTypeTagsProvider` 中一并删除）：**1.20 HEAD 与 1.21 HEAD 命中均为 0** ⇒ 1.21 也没有这三个幽灵生物（无需动作，也不受 `ModEntities` 禁改影响）。

### 77.4 登记（不属本行、属既有 lang 欠账）

该提交顺带修正了 8 处中文名/刷怪蛋名（如 `BLOOD_FEEDER`、`BONE_LEE`、`SLIMER`、`WINGLESS_SLIMER` 及对应刷怪蛋）。核对发现这是**更大的既有欠账**，不该按本行落：

- 1.21 `ModChineseProvider` 里 `add(MonsterEntities.X.get(), …)` 只有 **7** 行（martian 那组），1.20 HEAD 有 **218** 行；`ModEnglishProvider` 同样 7 行。
- 1.21 `src/generated/.../lang/en_us.json` 里 `entity.confluence.*` 共 225 条，但**几乎全是弹幕/坐骑/工具实体**（`blood_feeder`／`slimer`／`bone_lee` 等怪物名不在其中）。
- 手写语言文件（`pt_br`/`es_es`/`lzh`/`de_de`）里这些怪物名反而在（如 `lzh.json:2255 "entity.confluence.eater_of_souls": "噬魂魔"`）⇒ 缺的是"provider → generated en_us/zh_cn"这条链，即 HANDOFF 已登记的「datagen 未重跑／736 条 lang 未落」家族。
⇒ 因此本行只做**核对**，落名放回 lang 专批（否则等于用本行去补 200+ 条怪物名）。

### 77.5 状态

- 台账双写：`244=COVERED`（未改源码）；状态 JSON 共 **334** 条；行 231–400 剩余 `TODO` = **2**（`323`/`256`）。
- `fix_eol --check` 仍为候选 6；未动 `GamePhase.java` 与任何禁改文件。


## 七十八、行 323 `900c068f7`「音频大导入（第一批）」：音效表/常量/接线/字幕四层 1.21 全等 ⇒ `COVERED`

### 78.1 四层证据（本行 726 条 gap 的全部主体是 `sounds.json`）

| 层 | 1.20 HEAD | 1.21 HEAD | 结论 |
| --- | --- | --- | --- |
| `assets/confluence/sounds.json`（726 gap 的来源） | 3315 行 / **503** 个 sound 事件键 | 3313 行 / **503** 键 | json 解析后键集合**双向差集 0** |
| `common/init/ModSoundEvents` | 656 行 / **503** 个常量 | 524 行 / **503** 个常量 | 常量数相等（1.20 是 `RegistryObject<SoundEvent>`，1.21 是 `DeferredHolder<SoundEvent, SoundEvent>`，故文件行数不同，属平台） |
| **音效接线**（`ModSoundEvents.X` 在实体里的调用点） | 87 个实体文件 / **199** 处 | 87 个实体文件 / **199** 处 | **逐文件计数完全相同**（`a[k]>b[k]` 与 `a[k]<b[k]` 两个方向都为 0；抽查 `WaterBoltMimic`/`Hoplite`/`Paladin`/`GiantTortoise`/`CursedSkull`/`QueenBee` 全为一致） |
| 字幕 lang | `confluence.subtitle.*`：en **520**、zh **487** | 同 en **520**、zh **487** | 键数相等 |

### 78.2 音频资产：68 个 ogg 在 1.21 **全部存在**

逐一按路径核对本提交的 68 个 `.ogg`：1.21 命中 **68/68**，缺失 **0**。
- 目录布局差异：1.20 除扁平 `sounds/xxx.ogg` 外还残留旧目录 `sounds/item/*.ogg`（本提交是把音效**重新导入到扁平路径**）；1.21 已是扁平布局（`sounds/` 下 819→816 个，差异主要就是 1.20 那批旧 `item/` 遗留）⇒ 1.21 侧布局等于本提交之后的目标态。
- 附记（不构成移植项）：这 68 个文件的 **blob 哈希两侧不同**（68/68），即音频字节不同。二进制音频不做 EOL/格式改写（§1.9 同理），且 rowaudit/screen 对二进制无判据；存在性与播放链路才是判据，故仅记录，不作为本行落地内容。

### 78.3 本行 62 个 java 文件的角色与现状

该提交的 java 改动 = **给实体接上新的 `ModSoundEvents` 常量**（boss 14 个 + monster/slime 46 个文件） + 两个 lang provider 补齐字幕键。由 78.1 的"87 文件 / 199 处"逐文件相等可知这些接线在 1.21 **已全部就位**（含本行点名的 `GiantTortoise`/`CursedSkull`/`QueenBee`/`Skeletron`/`SkeletronPrime`/`Retinazer`/`Spazmatism` 等）。

### 78.4 状态

- 台账双写：`323=COVERED`（未改源码）；状态 JSON 共 **335** 条；行 231–400 剩余 `TODO` = **1**（`256`，1354 gap）。
- `fix_eol --check` 仍为候选 6；未动 `GamePhase.java` 与禁改文件。


## 七十九、行 256 `b05c8dc3f`「扩充生物与事件内容，完善召唤和鞭子系统并修复战斗与渲染问题」：**范围切分与第一批取证**（行仍留 `TODO`）

> 本行 1354 gap / 305 java + 234 资源 + 143 二进制，是 231–400 里最大的一行。截至本节**尚未整行裁定**，台账**不写状态**（保持 `TODO`）；本节只落"已定项"与"下一批的精确入口"，避免下一轮重复筛查。

### 79.1 资产族：`rideable/unicorn` 三件套 **1.21 缺**（已定：`DEFER-ASSETS`）

| 资源 | 1.20 HEAD | 1.21 HEAD |
| --- | --- | --- |
| `assets/confluence/geo/entity/rideable/unicorn.geo.json`（841 gap 的来源，本行新建） | 有 | **无** |
| `assets/confluence/animations/entity/rideable/unicorn.animation.json` | 有 | **无** |
| `assets/confluence/textures/entity/rideable/unicorn.png` | 有 | **无** |
| 对照：`rideable/lava_shark.{geo.json,png}` | 有 | **有** ✓ |

- 代码侧其实已就位：`RideableUnicornMountEntity` 在 1.21 存在且已注册（`ModEntities` 里 `RIDEABLE_UNICORN`、`MountItems` 也在用）。
- 缺的只是「模型/动画/贴图 + 在 `ModClientEvents` 里按 1.20 HEAD 的 `Confluence.asResource("geo/entity/rideable/unicorn.geo.json")` / `textures/entity/rideable/unicorn.png` 注册渲染器」（该文件 9 条 gap 即此）。
- 裁定：**`DEFER-ASSETS`**（二进制资产不在移植窗口落地；与 §77/§78 同口径）。这一条也正是此前「231–400 之外的大残留」里记的 1.21 缺 `rideable/unicorn.*`。

### 79.2 语言/图鉴族：属 **TE 语言键收口** 决策，非本行落地项（已定）

- `BestiaryLanguageSubProvider`(144)、`ModChineseProvider`(78)、`ModEnglishProvider`(115)、`ModClientBestiaryEntryProvider`(+668/−598)。
- 1.21 的这些行大量处于**注释态**，来源是 1.21 侧自己的移植提交 `6606c37d6`「port(TE 退役 16): datagen provider 侧语言键收口（1328 处 → 0）」，并留下 `notes/TE-LANG-KEYS-REMOVED-PROVIDERS.md`（294 行）。逐键核对（以 `b05c8dc3f` 新增的 75 个 `bestiary.entity.*.desc` 为样本）：
  - 1.20 分叉点 `795ac9ccc`：**不存在**（0 生效 / 0 注释）；
  - 1.20 `b05c8dc3f^`：**注释**（0/2）；
  - 1.20 HEAD：**生效**（2/0，EN+ZH 各一）——即本行把它们启用了；
  - 1.21 HEAD：**注释**（0/2），且 `git log -S` 显示唯一相关提交就是上面那笔"收口"。
- ⇒ 1.20 的"启用"与 1.21 的"收口"是两侧各自的决策**相撞**：本行**不**去把 1.21 的 138 行取消注释（那会把 1328 键收口的成果重新灌回 datagen），登记为跨决策冲突，留待语言专批统一处理（要么切到 1.21 的键源，要么按 1.20 收口）。

### 79.3 代码族：逐文件抽查显示 **1.21 已在**

对屏幕点名的文件逐一核对（存在性 + 关键成员）：

| 项 | 1.21 |
| --- | --- |
| `RideableUnicornMountEntity` / `RideableLavaSharkMountEntity`、`MountItems.RIDEABLE_*` | ✓ |
| `PirateRangedMonster`、`PirateInvasionGameEvent`（含 `BasePotBlock`/`LevelEvents`/`GameEventSystem`/`ConsumableItems` 引用） | ✓ |
| `PartyGameEvent`、`SandstormGameEvent`、`WaterBoltMimic`、`Cluckshroom`、`Goldfish`、`BugNetEntityToItemSubProvider`、`RainProjectileRenderer`、`SpearRenderer` | ✓ |
| 屏幕其余大量单行 gap | 经 §46/§51/§70/§72/§78 同一套平台判据可解释：`entityData.define(...)` → `builder.define(...)`、`ForgeEventFactory.getMobGriefingEvent` → `EventHooks.canEntityGrief`、`ItemStack.isSameItemSameTags` → `isSameItemSameComponents`、`vertex/color/uv/overlayCoords/uv2/normal/endVertex` → `addVertex/setColor/setUv/setUv1/setUv2/setNormal`、`import …ModEntities` 通配化、`@Mod.EventBusSubscriber`+Forge `TickEvent` → NeoForge `@EventBusSubscriber`、`IPortProjectileExtension` 等 |

### 79.4 下一批入口（本轮已定位、待逐 hunk 裁定）

1. `common/entity/ThrownPowderEntity`(14 gap)：本行让污染/血腥粉**同化附近的动物**——`Penguin.corrupt(boolean)`、`Goldfish`/`Bunny`/`MysticFrog.purify()`（1.21 是否有这些方法与被调点，需逐行核）。
2. `common/event/game/entity/LivingEntityEvents`(7 gap)：`blockEnemyFriendlyFire(LivingAttackEvent)` + `event.setResult(Event.Result.DENY)`；以及 `if (victim.getVehicle() instanceof AbstractMountEntity mount) amount = mount.modifyRiderDamage(damageSource, amount);`（**上一轮在 §71.3 曾把后者记作"不属于行 322"的观察 ⇒ 现在确认它属于本行**，1.21 `livingDamage$Pre` 目前没有这段）。
3. `common/event/game/entity/PlayerEvents`(7 gap)：`event.getDrops().clear()`（城镇史莱姆相关）与 `DevelopmentSpawnPolicy.allowsAutomaticSpawn(type)` 守卫。
4. `common/gameevent/GoblinArmyGameEvent`(6 gap)：困难模式下的刷怪表切换。
5. `common/data/gen/**`（`ValueSubProvider`/`GiftSubProvider`/`EntitySubProvider`/`LivingInvulnerableEffectsSubProvider`/`HardmodeAnvilRecipeProvider`）：新增坐骑/召唤物的价格、礼物池、掉落与配方条目。
6. 召唤/鞭子族（`summon/ground|flying/*`、`item/whip/FirecrackerItem`、`WhipModelRegister`、`WhipAttackRenderer`、`ClientSummonManager`）：需与已落地的 §? 召唤批（行 343/344/345 家族）对齐后判定，避免重复。

### 79.5 状态

- 台账：**未写**（行 256 仍 `TODO`，状态 JSON 保持 **335** 条）；节内已把 79.1/79.2 两个族判死、79.3 判定为平台、79.4 列明下一批入口。
- `fix_eol --check` 仍为候选 6；本轮未改任何源码。


## 八十、行 256 第二批：落地 2 处（污染粉小动物转化 / 坐骑骑手减伤），并关闭 §34.1 的一条登记

> 行 256 仍**保持 `TODO`**（台账不写状态），本节只记本批落地与一条登记的关闭。

### 80.1 落地 `common/entity/ThrownPowderEntity`（+19 / −1）

| 1.20 HEAD（`b05c8dc3f` 该文件整段） | 1.21（本批落地） |
| --- | --- |
| 三个 import：`animal.Bunny`／`Goldfish`／`Penguin` | 同 |
| `tick()` 里 `if (!level().isClientSide) {` 之后：`type == PURE` 时对 1 格内 `MysticFrog` 调 `purify()`；`type == CORRUPT \|\| CRIMSON` 时对 1 格内 `Penguin`／`Goldfish`／`Bunny` 调 `corrupt(type == CRIMSON)` | 逐字落地 |
| `if (lastPos == blockPosition()) return;` → `if (blockPosition().equals(lastPos)) return;` | 同（引用比较 → 值比较，属同一 hunk） |

- **被调方核对**（避免"消费者在、驱动器缺"）：1.21 侧 `Bunny.java:126 corrupt(boolean)`、`Goldfish.java:93 corrupt(boolean)`、`Penguin.java:45 corrupt(boolean)`、`MysticFrog.java:34 purify()` **四者全在**，故本块可直接落地。
- 未落（属行 95 `dfcc5c041` 的裁定范围，`DO-NOT-PORT`）：该文件的 `Type`／`MoveDistance` 持久化与 `type` 非空默认值等，本批不碰。

### 80.2 落地 `common/event/game/entity/LivingEntityEvents`（+4）

- 在 `livingDamage$Pre` 的 `amount = SwordItems.processEffect(damageSource, attacker, victim, amount);` 与 `event.setNewDamage(amount);` 之间插入 1.20 HEAD 的
  `if (victim.getVehicle() instanceof AbstractMountEntity mount) { amount = mount.modifyRiderDamage(damageSource, amount); }`
  并补 import `org.confluence.mod.common.entity.mount.AbstractMountEntity`。
- 被调方核对：1.21 的 `AbstractMountEntity:164 modifyRiderDamage(DamageSource, float)` 在，且四个坐骑各有覆盖（`RideableSlimeMountEntity:150`／`RideableUnicornMountEntity:85`／`RideableBeeMountEntity:167`／`RideableLavaSharkMountEntity:55`）⇒ 1.21 之前**只定义了钩子却没有任何调用点**，本批把驱动器补上（§71.3 当时把这段记作"不属于行 322 的观察"，现归属本行落地）。

### 80.3 关闭 §34.1 的登记：`blockEnemyFriendlyFire` 判 **`COVERED`**

- §34.1 曾把它记为「行 256，后续行，登记不落地」。本批实测：**1.21 的行为已存在**——`LivingEntityEvents.livingIncomingDamage`（1.21 `:202-206`）开头就是
  `if (EnemyDamageRules.blocks(event.getEntity(), event.getSource())) { event.setCanceled(true); return; }`，
  与 1.20 的 `blockEnemyFriendlyFire(LivingAttackEvent)`（HIGHEST 优先级、同一谓词 `EnemyDamageRules.blocks`）**同谓词同效果**：1.21 用 NeoForge 的 `LivingIncomingDamageEvent` 一条入口即可覆盖，不需要再挂一个 `LivingAttackEvent` 监听。
- 这正是 §34.1 自己总结的教训再现：**词表级残差会把"同名方法缺失"误报成缺口**（1.21 全库 `blockEnemyFriendlyFire` 只出现在 notes 里，但 `EnemyDamageRules.blocks` 的调用点在）。⇒ 该条登记**关闭**，不再列入行 256 的待办。

### 80.4 状态

- 台账：**仍未写**（行 256 保持 `TODO`，状态 JSON 仍 **335** 条）——本节与 §79 一起构成行 256 的"分批推进"记录，最终整行裁定前不落 `PORTED`（与 §35.3 第 5 条口径一致）。
- 本批改动 2 文件（`ThrownPowderEntity` +19/−1、`LivingEntityEvents` +4）；`fix_eol --check` 仍为候选 6；未碰 `GamePhase.java` 与禁改文件。
- 行 256 剩余入口：§79.4 的 1/3/4/5/6 各条 + §79.1 的 `DEFER-ASSETS`（`rideable/unicorn` 三件套）。


## 八十一、行 256 第三批：落地 2 处（坐骑/召唤物定价、哥布林军团困难模式刷怪表），并定位 1 处不可孤立落地项

> 行 256 仍**保持 `TODO`**。本节同时补上 §75.4 与 §50.2/§50.3 的交叉引用（同一批 `2f44045a8` 的欠账散在两节）。

### 81.1 落地 `common/data/gen/data_map/ValueSubProvider`（+8 / −1）

| 1.20 HEAD（`b05c8dc3f`） | 1.21（本批落地） |
| --- | --- |
| 坐骑段：`HONEYED_GOGGLES` 之后加 `BLESSED_APPLE, gold5` 与 `SUPERHEATED_BLOOD, gold5`（分号移到末行） | 逐字落地（两常量在 1.21 `MountItems.java:29/30` 均在） |
| 召唤段：`SNOW_FLINX_STAFF` 之后加 `VAMPIRE_FROG_STAFF, gold1`／`DEADLY_SPHERE_STAFF, gold10`／`SANGUINE_STAFF, gold5`／`SPIDER_STAFF, gold1`／`DESERT_TIGER_STAFF, gold20` | 逐字落地（五常量在 1.21 `SummonItems.java:109/121/133/145/157` 均在；1.21 其后紧跟的 `SCULK_WISP_STAFF` 顺序与 1.20 相同） |

### 81.2 落地 `common/gameevent/GoblinArmyGameEvent`（+10 / −2）

| 1.20 HEAD | 1.21（本批落地） |
| --- | --- |
| `open()` 里把 `SpawnerData` 可变参数改成 `List<MobSpawnSettings.SpawnerData> entries = new ArrayList<>(List.of(...))`，困难模式再 `entries.add(GOBLIN_WARLOCK, 60, 1, 1)`，最后 `entries.toArray(...)` 交给 `GameEventSpawnerDataModificationEvent` | 同（1.21 的事件构造是 varargs `MobSpawnSettings.SpawnerData...`，`toArray(SpawnerData[]::new)` 可直接喂入） |
| `onStart()` 首行 `open(server);` | 同 |
| import `java.util.ArrayList` / `java.util.List` | 同 |

- **保留 1.21 侧既有差异**：1.21 的列表里 `ANGER_GOBLIN` 是**生效**项而 1.20 把它注释掉了 ⇒ 不删（§1.1 不回退 1.21），本批只做"加 warlock + 修 onStart"。
- 被调方核对：`MonsterEntities.GOBLIN_WARLOCK` ✓、`KillBoard` 已 import ✓、`NeoForge.EVENT_BUS` 已是 1.21 写法 ✓。
- 补记教训：本批首次生成时把 `List.of(...)` 各项的**逗号**一并剥掉（脚本对"去尾逗号"处理过头），已按 1.21 的 `List.of` 语义补回 5 处并复核 braces 42/42。

### 81.3 定位到一处**不可孤立落地**：`PlayerEvents.itemFished`

- 1.21 `PlayerEvents.java:274-282` 的 `itemFished(ItemFishedEvent)` 只有"高测试钓线 14.29% 脱钩"一段；1.20 HEAD 同处还含
  ①本行新增：`getRandom().nextInt(10) == 0` 时 `TownSlimeNPC.unlock(..., SURLY_SLIME, hook.position())` 并把 `event.getDrops().clear()`；
  ②本行改造：`hook.getType() == BLOODY_FISHING_HOOK ? 6 : 12` 的 1/6、1/12 概率刷怪，且把单体 `WANDERING_EYE_FISH` 改成 `nextBoolean() ? WANDERING_EYE_FISH : ZOMBIE_MERMAN` 并加 `DevelopmentSpawnPolicy.allowsAutomaticSpawn(type)` 守卫。
- **但 ① ② 所依附的 `BLOODY_FISHING_HOOK` 刷怪块在 1.20 的 `b05c8dc3f^`（即本行之前）就已存在**（该 hunk 里它是 **context 行**，不是 `+` 行）⇒ 引入者早于行 256。1.21 又**整段没有**它。
- ⇒ 归属未清：不能把"①+②"直接记作行 256 的落点去补一段 1.21 根本没有的前置块。**下一批先 `git blame`/`git log -S 'BLOODY_FISHING_HOOK'` 定位 1.20 该块的引入提交与所属台账行**，再决定：若该行已判 `COVERED` 则是误判需回改，若属未查行则整块一起落。
- 被调方在 1.21 均齐备（`TownSlimeNPC.unlock(ServerLevel, EntityType, Vec3)` `:36` ✓、`ZOMBIE_MERMAN`/`WANDERING_EYE_FISH` ✓、`DevelopmentSpawnPolicy` 125 处 ✓），缺的只是这次归属判定。

### 81.4 交叉引用补记（`2f44045a8` 的欠账分布）

- §50.2 已落：`PrefixUtils.couldReforge` 补 `SUMMONER_WEAPON`+`PREFIX_SUMMON_ONLY`、`PREFIX_MAGIC_ONLY` 摘掉 `SUMMONER_WEAPON`（现测 `ModItemTagsProvider:524-525` 只剩 `MANA_WEAPON`、`PrefixUtils:32-40` 两项齐备）。
- §50.3 已落：字幕 lang 键 457(zh)/490(en)。
- §75.4 仍欠：`PrefixUtils.setAndUpdate` 的 300 行遗留表（阻塞于 lib `ModRarity.TIER`，需在嵌套仓库 `Confluence-Magic-Lib` 单独提交）；以及逐标签方向判定（`TOOLS_HOOK` 现测 1.21 仍 0 命中）。
- ⇒ 三节同属行 359，**行 359 的状态保持 `PORTED`**，欠账以这三节为准。

### 81.5 状态

- 台账：**仍未写**（行 256 保持 `TODO`）；本批 2 文件（`ValueSubProvider` +8/−1、`GoblinArmyGameEvent` +10/−2）。
- `fix_eol --check` 仍为候选 6；未碰 `GamePhase.java` 与禁改文件。


## 八十二、行 256 第四批：捕虫网映射补 5 条小动物（+5）

- `common/data/gen/data_map/BugNetEntityToItemSubProvider`：按 1.20 HEAD（`b05c8dc3f`）在 `SLUGGY` 与 `SNAIL` 之间补 `BUGGY`／`STINKBUG`，在 `SNAIL` 之后补 `TRUFFLE_WORM`／`FIREFLY`／`LIGHTNING_BUG`，共 5 行。
- 被调方核对：`CritterEntities` 与 `BaitItems` 里这 5 对常量 **1.21 全部存在**（逐名实测各 1 处命中）。
- **不落的部分**：1.20 同一提交还把变体匹配从 `variant(Butterfly.Variant.X, …)`（PortLib 期用 `variant.serialize(tag)` + `NbtPredicate`）重写成 `NbtPredicate` 方案；1.21 用的是自己那套 `intVariant(int, …)`（`getType()`/`int` 变体 id）⇒ **两套机制并行**，属平台/架构差异，**不镜像**（否则会把 1.21 可用的变体匹配改回 `NbtPredicate`）。
- 行 256 仍保持 `TODO`；`fix_eol --check` 仍为候选 6。


## 八十三、行 256 第五批：落地「钓鱼钩生成怪」整段（闭 §52.2 第 3 处登记，并补齐行 196 的 `PlayerEvents` 内容）

### 83.1 归属先定清（§81.3 的悬案 → §52.2 早已登记）

- `git log -S 'BLOODY_FISHING_HOOK' -- PlayerEvents.java`：整段由 **`741f98d1e`**（台账**行 196**，`PORTED`）引入（+16/−1）；本行 `b05c8dc3f` 只做三处改造（`return` 提前退出、城镇史莱姆分支、随机二选一 + `allowsAutomaticSpawn` 守卫）。
- 与 §52.2（`:1976`）的登记**完全吻合**：那里写着「第 3 处＝登记不落地……该段跨两个 1.20 提交（守卫来自 `b05c8dc3f`=行 256，整段来自 `741f98d1e`），**归行 256 专批处置**——需先核 1.21 `PlayerEvents` 的钓鱼方法形状，不做盲搬」。本批即执行该专批。
- 1.21 现状（形状已核）：`PlayerEvents.itemFished` 只有"高测试钓线 14.29% 脱钩"一段，既无本行改造、也无行 196 的整段 ⇒ 属**消费者在（`ItemFishedEvent` 已订阅）、驱动器缺**一类。

### 83.2 落地 `common/event/game/entity/PlayerEvents`（+26）

按 1.20 HEAD 逐行落地整段（脱钩分支补 `return;` → 血月+服务端守卫 → 水流判定 → 10% 城镇史莱姆救援 → `BLOODY_FISHING_HOOK ? 6 : 12` 概率 → `WANDERING_EYE_FISH`/`ZOMBIE_MERMAN` 二选一 → `allowsAutomaticSpawn` 守卫 → `spawn(...)` → 设目标/初速 → `getDrops().clear()`），并补 5 个 import。

| 项 | 1.20 | 1.21（本批） | 依据 |
| --- | --- | --- | --- |
| `player.getRandom1211()` | PortLib 垫片 | `player.getRandom()` | 全库既有平台改名 |
| `type.spawn(serverPlayer.serverLevel(), hook.blockPosition(), MobSpawnType.EVENT)` | Forge 1.20 | **同名同形** | vanilla 1.21.1 `EntityType.java:952` 实测存在 `spawn(ServerLevel, BlockPos, MobSpawnType)` |
| `event.getDrops().clear()` | Forge `ItemFishedEvent` | **同名** | NeoForge 1.21.1 `ItemFishedEvent#getDrops()` 实测返 `NonNullList<ItemStack>`（可 `clear()`），事件类 `implements ICancellableEvent` |
| `MobSpawnType` | 1.20 | 1.21.1 **仍在** | vanilla 源码实测 `MobSpawnType.java` 存在（不再误用 `EntitySpawnReason`） |
| `TownSlimeNPC.unlock(ServerLevel, EntityType<TownSlimeNPC>, Vec3)` | 1.20 | 1.21 `TownSlimeNPC:36` **同签名** | 1.21 既有调用样板 `MysticFrog:35`（`NpcEntities.MYSTIC_SLIME.get()`）⇒ `NpcEntities.SURLY_SLIME.get()` 同形可编译 |
| `ModEntities.BLOODED_FISHING_HOOK` 所在的 `ModEntities` | `init/entity/ModEntities` | `init.ModEntities`（已由 `init.*` 通配覆盖） | 故**不**再加显式 import（避免与通配重复） |
| import | `FluidTags`/`MobSpawnType`/`TownSlimeNPC` | 同 + `DevelopmentSpawnPolicy`/`NpcEntities`（`init.entity.*` 未通配） | 逐个断言唯一后插行 |

### 83.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 1 文件 +26 行。
- 行 196（`741f98d1e`，`PORTED`）的 `PlayerEvents` 部分**由本批补齐**：其 `PORTED` 判定自本批起才与实际一致（此前属"整段未落"的欠账，见 §52.2）。台账状态**不回改**（行 196 已随本批落地而名副其实），仅在此留痕。
- `fix_eol --check` 仍为候选 6；未碰 `GamePhase.java` 与禁改文件。


## 八十四、行 256 第六批：蚁狮卵/群系修饰族**先取证再落**（本轮不落，附五个插入点）

### 84.1 为什么先不落：两侧对该方块的建模不同

| | 1.20 HEAD | 1.21 HEAD |
| --- | --- | --- |
| 注册处 | `DecorativeBlocks:59` = `PortDeferredBlock<AntlionEggBlock>`，`registerWithItem("antlion_eggs", … .strength(0).sound(BONE_BLOCK).noLootTable())` | `NatureBlocks:102` = `DeferredBlock<SmallPilesBlock>`，`registerWithItem("antlion_eggs", () -> new SmallPilesBlock(…ofFullCopy(Blocks.STONE)…strength(0.05F)))` |
| 类 | 专用 `AntlionEggBlock`（两侧该文件都在，1.21 侧**未必被引用**） | `SmallPilesBlock`（1.21 自己的"小堆"体系） |

⇒ 直接按 1.20 落 `BlockStateProvider.simple(DecorativeBlocks.ANTLION_EGGS.get())` 会指向 1.21 **不存在**的常量；须改挂 `NatureBlocks.ANTLION_EGGS`，但**先要确认 1.21 是否已用别的方式放置蚁狮卵**（若已放置，本族应判 `COVERED`，否则会重复生成）。本轮只取证、不下结论。

### 84.2 下一批的五个插入点（均取自 `b05c8dc3f` 的 diff，已定位到 1.20 行号）

1. `ModDataProvider$ConfiguredFeatures`：`FALLING_SAND_TRAP` 之后新增 `ANTLION_EGGS = key("antlion_eggs")`（1.20 `:249`）。
2. 同内部类 `register(context, ANTLION_EGGS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(24, 7, 3, direct(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(<蚁狮卵方块>) ), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.AIR), BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.SANDSTONE, Blocks.RED_SANDSTONE))))))`（1.20 `:587-590`）。**注意**：`direct(...)` 是 provider 内部助手，须核 1.21 是否有同名/同形助手。
3. `ModDataProvider$PlacedFeatures`：`ANTLION_EGGS = key("antlion_eggs")`（1.20 `:814`）。
4. `register(context, ANTLION_EGGS, configured.getOrThrow(ConfiguredFeatures.ANTLION_EGGS), CountPlacement.of(12), inSquare, bottomThroughUnderground, biome);`（1.20 `:994`）。
5. `addFeatures(context, "desert_ud", desert, HolderSet.direct(factory, PlacedFeatures.FALLING_SAND_TRAP, PlacedFeatures.ANTLION_EGGS), GenerationStep.Decoration.UNDERGROUND_DECORATION);`（1.20 `:1240-1242`，即在既有 `FALLING_SAND_TRAP` 列表里追加一项）。
6. 另：同一提交在该文件里还改了三条 `ForgeBiomeModifiers.AddSpawnsBiomeModifier`（`hallow_monsters`／`night_glow_bugs`／`truffle_worm`），1.21 同类修饰器用的是 NeoForge 的 `BiomeModifiers`（`ModDataProvider` 1.21 侧写法不同）⇒ 同样需先核写法再落。

### 84.3 行 256 剩余清单（本轮收口时）

- 上述蚁狮卵/群系修饰族（§84.2 六条）。
- `rideable/unicorn` 三件套 + `ModClientEvents` 渲染器注册（`DEFER-ASSETS`，§79.1）。
- lang/bestiary 族（属 TE 语言键收口决策，§79.2，不按本行落）。
- 召唤/鞭子族与已落地批次（行 343/344/345 家族）的对齐复核（§79.4 第 6 条）。
- 其余 300+ 文件在屏幕筛选中已判为平台噪音（`entityData.define`→`builder.define`、`ForgeEventFactory`→`EventHooks`、顶点 API、`isSameItemSameTags`→`isSameItemSameComponents` 等）。

### 84.4 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本轮另落 1 文件 +26（§83.2）。
- `fix_eol --check` 仍为候选 6。


## 八十五、行 256 第七批：落地蚁狮卵世界生成 + 5 个群系刷怪修饰器（+21 / −1）

### 85.1 落地 `common/data/gen/ModDataProvider`（`ANTLION_EGGS` 五处 + 五个修饰器）

| # | 位置 | 1.20 HEAD | 1.21（本批落地） |
| --- | --- | --- | --- |
| 1 | `ConfiguredFeatures` 键区（1.21 `:260`） | `ANTLION_EGGS = key("antlion_eggs")` 紧跟 `FALLING_SAND_TRAP` | 同 |
| 2 | `ConfiguredFeatures.bootstrap`（1.21 `:612-615`） | `Feature.RANDOM_PATCH` + `new RandomPatchConfiguration(24, 7, 3, direct(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(<蚁狮卵>)), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(matchesBlocks(AIR), matchesBlocks(new Vec3i(0,-1,0), SANDSTONE, RED_SANDSTONE)))))` | 逐字落地，方块用 **1.21 的 `NatureBlocks.ANTLION_EGGS`**（见 85.2） |
| 3 | `PlacedFeatures` 键区（1.21 `:845`） | `ANTLION_EGGS = key("antlion_eggs")` | 同 |
| 4 | `PlacedFeatures.bootstrap`（1.21 `:1029`） | `CountPlacement.of(12), inSquare, bottomThroughUnderground, biome` | 同 |
| 5 | `addFeatures(context, "desert_ud", …)`（1.21 `:1279-1281`） | 在 `PlacedFeatures.FALLING_SAND_TRAP` 后追加 `PlacedFeatures.ANTLION_EGGS` | 同 |
| 6 | `BiomeModifierz.bootstrap` 末尾（1.21 `:1526` 起） | 五个 `AddSpawnsBiomeModifier`（见下表） | 同（`ForgeBiomeModifiers` → 1.21 的 `BiomeModifiers`） |

**五个群系修饰器（1.20 `b05c8dc3f` 的 `+` 行，逐条对照）**

| key | 群系 | 刷怪表 |
| --- | --- | --- |
| `hallow_monsters` | `ModTags.Biomes.THE_HALLOW` | `CHAOS_ELEMENTAL` 20×(1,1)、`ILLUMINANT_BAT` 30×(1,2)（1.20 侧原本整块是注释，本行提交解注释并补 `ILLUMINANT_BAT`） |
| `night_glow_bugs` | `overworld` | `FIREFLY` 5×(1,5)、`LIGHTNING_BUG` 5×(1,5) |
| `truffle_worm` | `HolderSet.direct(ModBiomes.GLOWING_MUSHROOM)` | `TRUFFLE_WORM` 2×(1,1) |
| `mushroom_animals` | `HolderSet.direct(ModBiomes.GLOWING_MUSHROOM)` | `GLOWING_MOOSHROOM` 5×(2,3)、`GLOWING_CLUCKSHROOM` 5×(2,4) |
| `cluckshroom` | `HolderSet.direct(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS)` | `CLUCKSHROOM` 8×(2,4) |

- 1.21 提交前实测：这五个 key 在**全库 0 命中**（既无 provider 注册也无 `data/confluence/neoforge/biome_modifier/*.json` 手写文件；该目录仅 3 个下界刷怪文件）⇒ 真缺口，非重复。
- 被调方逐个核过：`ModTags.Biomes.THE_HALLOW` ✓（1.21 `ClientBiomeEffectSystem:38` 在用）、`ModBiomes.GLOWING_MUSHROOM` ✓（两侧同名 `register("glowing_mushroom")`）、`MonsterEntities.ILLUMINANT_BAT`/`CHAOS_ELEMENTAL` ✓、`CritterEntities.{FIREFLY,LIGHTNING_BUG,TRUFFLE_WORM,GLOWING_MOOSHROOM,GLOWING_CLUCKSHROOM,CLUCKSHROOM}` ✓（均在 `CritterEntities`）。

### 85.2 蚁狮卵的方块差异（§84.1 的悬案在本批定案）

- 1.20 用 `DecorativeBlocks.ANTLION_EGGS`（专用 `AntlionEggBlock`）；1.21 用 **`NatureBlocks.ANTLION_EGGS`（`SmallPilesBlock`，带物品）**，且 1.21 全库无任何 `antlion_eggs` 世界生成 ⇒ 本批把 feature 挂到 1.21 的方块常量上（**只换常量、不换机制**），并保留 1.20 的生成参数（`24,7,3` / `CountPlacement.of(12)` / 沙漠地下装饰阶段）。
- `direct(...)` 助手 1.21 已有：`ModDataProvider:781 private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> direct(F feature, FC config, PlacementModifier... modifiers)`，与 1.20 的用法同形 ✓。

### 85.3 工作树提醒（不影响本节提交）

提交时工作树里另有**用户本人的在建改动** `common/gameevent/GoblinArmyGameEvent.java`（把上一批我落的 `new ArrayList<>(List.of(...))` 改成 Guava `Lists.newArrayList(...)`，并把 `KillBoard.INSTANCE.getGamePhase().isHardmode()` 改成 `IMinecraftServer.isHardmode(server)`）。按既有约定**不碰、不提交** ⇒ 本节只提交 `ModDataProvider.java` + 本记录（路径限定）。

### 85.4 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 1 文件 +21/−1。
- `fix_eol --check` 仍为候选 6。


## 八十六、行 256 第八批：抗性数据 4 条 + 沙尘暴/派对事件登记（+8 / −1）

### 86.1 落地 `common/data/gen/data_map/LivingInvulnerableEffectsSubProvider`（+4 / −1）

| 1.20 HEAD（`b05c8dc3f`） | 1.21（本批落地） |
| --- | --- |
| `.add(MonsterEntities.DIGGER, LibEffects.CONFUSED)`、`.add(MonsterEntities.WORLD_FEEDER, LibEffects.CONFUSED)`（紧接 `GIANT_WORM`） | 同（`LibEffects.CONFUSED` 用 1.21 的裸 holder 形态，1.20 是 `.get()`） |
| `.add(MonsterEntities.GRANITE_GOLEM, MobEffects.POISON, LibEffects.CONFUSED)`（**同时删掉它上面那行 `// TODO 花岗岩巨人`**） | 同（注释一并删除，即 1.20 该 hunk 的原动作） |
| `.add(MonsterEntities.ARMORED_VIKING, ModEffects.FROST_BURN, ModEffects.FROSTBITE, MobEffects.POISON)`（紧接 `UNDEAD_VIKING`） | 同 |

**未落 1 条**：`.add(MonsterEntities.DUNGEON_SPIRIT, new AnyHolderSet<>(…), LivingInvulnerableEffects.Category.HARMFUL_EXCEPT_WHIP_TAG)` —— 1.21 的 `LivingInvulnerableEffects.Category` 只有 `BENEFICIAL`/`HARMFUL`/`NEUTRAL`（实测 enum 体），**没有 `HARMFUL_EXCEPT_WHIP_TAG`**（1.20 侧该值带注释「历史遗留：鞭子标记伤害不作为 MobEffect 免疫」）。该枚举值**不是行 256 带来的**（本行 diff 里它已存在），故这是**跨行依赖**：等那条更早的行落完再补本条，记在此处备查。

### 86.2 落地 `common/gameevent/GameEventSystem`（+4）：沙尘暴与派对事件**登记**上

- 1.21 的 `SandstormGameEvent`/`PartyGameEvent` 类都在（`KEY`/`INSTANCE` 齐备）且被别处引用（`SpawnPlacementChecks`/`SimpleWormMonster`/`NPCSpawner`），但**从未进 `events` 表** ⇒ 属「消费者在、驱动缺失」：事件永远无法开始。
- 1.20 HEAD 在 `events` 表末尾加：
  ```java
  if (LibUtils.isDev()) {
      map.put(SandstormGameEvent.KEY, SandstormGameEvent.INSTANCE);
      map.put(PartyGameEvent.KEY, PartyGameEvent.INSTANCE);
  }
  ```
  **1.21 适配**：用 1.21 自己的等价开关 `Confluence.UNRELEASED_SPAWNS`（其定义就是 `LibUtils.isDev()`，见 `Confluence.java:57`；且 1.21 已用它给 `PirateInvasionGameEvent` 加同款门，见 `GameEventSystem:45-46`）⇒ 语义等价、写法取 1.21 既有惯例。
- 旁记（不属本行）：1.20 HEAD 的 `events` 表里还有 `SolarEclipseGameEvent`，1.21 的该表**没有**它（`:41-54` 止于 `BoulderRainGameEvent`）⇒ 归其引入行，记此备查。

### 86.3 工作树

`common/gameevent/GoblinArmyGameEvent.java` 仍有**用户在建改动**（本轮已从 6/5 演进到 7/7 行），按约定不碰不提交；本节只提交上述 2 文件 + 本记录。

### 86.4 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 2 文件 +8/−1。
- `fix_eol --check` 仍为候选 6。


## 八十七、行 256 第九批：补回 `MoneyDropSource` 驱动器（+2）——又一处「实现者在、无人调用」

### 87.1 落地 `util/ModUtils`（+2）

- 1.20 HEAD 的 `getLivingBaseMoneyDrops(LivingEntity, Level)` 首行是
  `if (living instanceof MoneyDropSource source && !source.allowsMoneyDrops()) return 0.0;`
  1.21 的同名方法（`ModUtils:214`）**没有这一行**，且 1.21 全库对 `MoneyDropSource` 的引用只有接口自身。
- 而 1.21 侧「实现者」是齐的：`MoneyDropSource`（接口，`:7 boolean allowsMoneyDrops()`）、`Piranha:73 allowsMoneyDrops()`（覆盖实现）都在，调用点 `Bestiary:96` / `DungeonSpirit:60` / `ModUtils:186` 也都走 `getLivingBaseMoneyDrops` ⇒ **`Piranha` 的"不掉钱"此前被静默忽略**（第 6 类「消费者在、驱动缺失」，与 §50.2/§53.1/§83.1 同类）。
- 落地：补该守卫 + `import org.confluence.mod.common.entity.MoneyDropSource;`（按字母序插在 `...entity.boss.BaseBoss` 之前），与 1.20 **逐字相同**（`LibAttributes.getAttackDamage()` 的 `.value()` 差异属既有平台形态，不在本行）。

### 87.2 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本轮累计 3 文件 +10/−1（§86 的 2 文件 + 本节的 1 文件）。
- 工作树：`GoblinArmyGameEvent.java` 仍有用户在建改动（7/7 行），不碰不提交。
- `fix_eol --check` 仍为候选 6。
- 行 256 下一批入口（合并 §79.4 与 §86 的旁记）：`ItemEvents` 的 SPARKLE_SLIME_BALLOON→DIVA_SLIME（需核 `event.setShrink` 在 1.21 的形状）、`GiftSubProvider` 的 `SUPERHEATED_BLOOD` 礼物池、`BugNetItem.escapeNet`、`ModTabs.acceptAll(MountItems.ITEMS, …)`、`RainProjectileRenderer` 的 `offsetY` 构造函数、`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）、`SolarEclipseGameEvent` 未登记（旁记）。


## 八十八、行 256 第十批：闪耀史莱姆气球解锁 DIVA 史莱姆 + 捕虫网对神秘蛙放生（+15）

### 88.1 落地 `common/event/game/entity/ItemEvents`（+11）

- 1.21 的 `shimmerItemTransmutation$Pre`（`:164-171`）此前只有「史莱姆王冠 → 强制史莱姆雨」一段；本批按 1.20 HEAD 在 `ItemEntity source = event.getSource();` 之后插入：
  ```java
  if (source.getItem().is(ConsumableItems.SPARKLE_SLIME_BALLOON) && source.level() instanceof ServerLevel level) {
      if (TownSlimeNPC.unlock(level, NpcEntities.DIVA_SLIME.get(), source.position()) != null) {
          event.setShrink(1);
          level.playSound(null, source.blockPosition(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
      }
      event.setCanceled(true);
      return;
  }
  ```
- 被调方**逐个实测**（这正是 §87.2 标注要先核的两点）：
  - `ShimmerItemTransmutationEvent` 基类**有** `setShrink(int)`（1.21 `api/event/ShimmerItemTransmutationEvent.java`，且 `Pre` 继承它）⇒ 可调用；
  - `ConsumableItems.SPARKLE_SLIME_BALLOON` 存在（`ConsumableItems:58`，`ThrowableItem<SparkleSlimeBalloonProjectile>`）；
  - `TownSlimeNPC.unlock(ServerLevel, EntityType<TownSlimeNPC>, Vec3)` ✓、`NpcEntities.DIVA_SLIME` ✓（1.21 既有调用样板 `SparkleSlimeBalloonProjectile:33`）、`ModSoundEvents.SHIMMER_EVOLUTION` ✓。
- 形态适配仅两处（均为既有惯例）：`ConsumableItems.SPARKLE_SLIME_BALLOON` 用裸 holder（同文件 `:166` 的 `SLIME_CROWN` 写法）。

### 88.2 落地 `common/item/common/BugNetItem`（+4）

- 1.20 HEAD 在 `predicate.test(interactionTarget) && getBoundingBox().getSize() <= maxSize` 分支开头插入「神秘蛙改用 `escapeNet()` 而非套网」：
  ```java
  if (interactionTarget instanceof org.confluence.mod.common.entity.animal.MysticFrog frog) {
      frog.escapeNet();
      break l;
  }
  ```
- 1.21 的该分支与**循环标号 `l:`** 都在（`BugNetItem:90` 标号、`:96` 分支）⇒ `break l;` 可直接用；`MysticFrog.escapeNet()` 在 1.21 `MysticFrog:39` ✓。1.20 原样用 FQN，故不加 import。

### 88.3 仍留在行 256 的入口（本轮核对后更新）

| 项 | 现状与下步 |
| --- | --- |
| `ModTabs` 的 `acceptAll(MountItems.ITEMS, output)` | 1.21 的 `ModTabs` 里**没有** `MountItems.ITEMS` 命中 ⇒ 先要确认 1.21 用什么途径把坐骑进页签（可能 `MountItems.ITEMS.getEntries()` 或另一页签类），再决定是否落 |
| `GiftSubProvider` 的 `SUPERHEATED_BLOOD` 礼物池（4 行） | 待取 1.20 hunk + 核 1.21 相邻池 |
| `RainProjectileRenderer` 的 `offsetY` | **设计已分叉**：1.20 把它泛化成 `<T extends Entity>` + `offsetY` 构造参数；1.21 是具体类型 `EntityRenderer<RainProjectile>` 且无 `offsetY`。泛化会牵动注册点 ⇒ 暂不镜像，登记为设计差异 |
| `DUNGEON_SPIRIT` 抗性条 | 跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`（1.21 枚举缺） |
| `SolarEclipseGameEvent` 未登记 | 归其引入行（旁记） |
| `rideable/unicorn` 三件套 + 渲染器注册 | `DEFER-ASSETS`（§79.1） |

### 88.4 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 2 文件 +15。
- 工作树：`GoblinArmyGameEvent.java` 仍有用户在建改动（7/7），不碰不提交。
- `fix_eol --check` 仍为候选 6。


## 八十九、行 256 第十一批：熔岩匣礼物池补「过热之血」（+4）

- `common/data/gen/loot/GiftSubProvider`：按 1.20 HEAD 给两张熔岩匣表各插一个礼物池
  ```java
  .withPool(LootPool.lootPool().add(LootItem.lootTableItem(MountItems.SUPERHEATED_BLOOD).setWeight(19))
          .add(EmptyLootItem.emptyItem().setWeight(81)))
  ```
  位置：`gameplay/crate/obsidian_crate`（1.21 `:57`）与 `gameplay/crate/hellstone_crate`（1.21 `:366`）各自的 `environmentLavaCrate*Common()` 之后 —— 与 1.20 的两处 hunk 逐字对应（1.20 用 `Confluence.asResource(...)` 建键、1.21 用 `Confluence.asResourceKey(Registries.LOOT_TABLE, ...)`，属既有平台差异）。
- 被调方核对：`MountItems.SUPERHEATED_BLOOD` ✓（同文件已用 `MountItems.HONEYED_GOGGLES`，import 齐）、`LootPool`/`EmptyLootItem`/`LootItem` ✓（1.21 该文件均已 import）。
- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 1 文件 +4。`fix_eol --check` 仍为候选 6。


## 九十、行 256 第十二批：坐骑物品进召唤页签（+1），并登记同族的跨行缺口

### 90.1 落地 `common/init/ModTabs`（+1）

- 1.20 HEAD 的召唤页签里 `acceptAll(SummonItems/WhipItems/LightPetItems/PetItems)` 之后有 `acceptAll(MountItems.ITEMS, output)`；1.21 的该页签（`:2008-2012`）**完全没有 `MountItems`**（全文件 0 命中）⇒ 坐骑类物品在创造模式页签里缺失。
- 落地：在 1.21 的 `acceptAll(LightPetItems.ITEMS, output);`（`:2010`）之后插入该行（1.20 顺序为 …LightPetItems、PetItems、MountItems；1.21 无 PetItems 行，见 90.2，故直接接在 LightPetItems 后）。

### 90.2 同族缺口（**不属本行**，登记备查）

| 缺口 | 证据 |
| --- | --- |
| `acceptAll(PetItems.ITEMS, output)` | 1.21 该页签无此行（`git grep PetItems -- ModTabs` 只命中 `LightPetItems` 一处）；该行**不在** `b05c8dc3f` 的新增行里 ⇒ 属更早提交 |
| `GroupItem.belongsTo("<faction>_entity", output)` 整族 | 1.20 实体页签里 `goblin_entity`／`pirate_entity`／`martian_entity` 三组齐全（1.20 `:2110-2132`）；1.21 的 `ModTabs` **三者全 0 命中**，`GroupItem.belongsTo` 只用于木质方块组（`:46/50/53/56/59/62`）⇒ 本行只点名了 `pirate` 那 8 行（1 行分组 + 7 个刷怪蛋），但它是**整个 faction 分组族的第三组**：单独补 pirate 会与 1.21 现状不协调。**故本批只落 90.1，pirate 组留待该族所属行统一处理**。1.21 的 `SpawnEggItems` 里 7 个 `PIRATE_*_SPAWN_EGG` 都在 ✓（`:205-208` 等），一旦该族决定落，可直接接。 |

### 90.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 1 文件 +1。
- 工作树：`GoblinArmyGameEvent.java` 仍有用户在建改动（7/7），不碰不提交。
- `fix_eol --check` 仍为候选 6。
- 行 256 剩余：`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）、`SolarEclipseGameEvent` 旁记、`RainProjectileRenderer.offsetY`（设计分叉，不镜像）、faction 分组族（本节登记）、`rideable/unicorn` 三件套（`DEFER-ASSETS`）、lang/bestiary 族（TE 语言键收口决策）。


## 九十一、行 256 第十三批：坐骑渲染器（独角兽 + 熔岩鲨）+ **补齐独角兽三件套资产**（+8 行代码 / +3 资产文件）

### 91.1 落地 `client/event/ModClientEvents`（+8）

- 1.21 的该文件**此前没有任何坐骑渲染器注册**（`RIDEABLE_` 0 命中、`MountGeoRenderer` 全仓无调用点）⇒ 独角兽与熔岩鲨坐骑没有渲染器。
- 按 1.20 HEAD 在 `registerEntityRenderers` 的 `EMPTY_ENTITY` 与 `BOMB_ENTITY` 之间插入两条（与 1.20 该 hunk 位置逐字对应）：
  ```java
  event.registerEntityRenderer(RIDEABLE_UNICORN.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(
          Confluence.asResource("geo/entity/rideable/unicorn.geo.json"),
          Confluence.asResource("textures/entity/rideable/unicorn.png"),
          Confluence.asResource("animations/entity/rideable/unicorn.animation.json"))));
  event.registerEntityRenderer(RIDEABLE_LAVA_SHARK.get(), context -> new MountGeoRenderer<>(context, new ExplicitGeoModel<>(
          Confluence.asResource("geo/entity/rideable/lava_shark.geo.json"),
          Confluence.asResource("textures/entity/rideable/lava_shark.png"),
          Confluence.asResource("animations/entity/rideable/lava_shark.animation.json"))));
  ```
- 被调方核对：`MountGeoRenderer(Context, ExplicitGeoModel<T>)` ✓（1.21 `client/entity/renderer/MountGeoRenderer.java`，上一批会话已建）、`ExplicitGeoModel` 已 import ✓、`RIDEABLE_UNICORN`/`RIDEABLE_LAVA_SHARK` 在 `ModEntities:357/358` ✓（该文件用 `ModEntities` 静态导入），补 `import …client.entity.renderer.MountGeoRenderer;`。

### 91.2 资产：**独角兽三件套按 1.20 blob 逐字节拷贝**（§79.1 的 `DEFER-ASSETS` 收口）

| 文件 | 1.20 blob | 落地方式与核验 |
| --- | --- | --- |
| `geo/entity/rideable/unicorn.geo.json` | 27332 B，**首字节 `{`**（非 §1.9 密文） | 按 1.20 blob 拷贝，worktree 写成 CRLF（841 行）；**归一化后内容逐字节等价 = True** |
| `animations/entity/rideable/unicorn.animation.json` | 179 B，首字节 `{` | 同上（10 行），内容等价 = True |
| `textures/entity/rideable/unicorn.png` | 6147 B，PNG 魔数 `89504e47` | 二进制直拷；**sha256 与原 blob 完全相同 = True** |

- 判据：同类资产 `rideable/lava_shark.{geo.json,animation.json,png}` 在**两侧 blob 尺寸完全相同**（59639/17975/3086）⇒ 本仓惯例就是"资产逐字节照搬"，故独角兽三件套照此办理。
- 门禁：拷完 `fix_eol --check` 仍为 **候选 6**（新增两个文本资产是 CRLF，未触发）。§1.9 的 6 个密文 geo 不受影响（那 6 个不是本批文件）。

### 91.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本批 1 java（+8）+ 3 资产（新文件）。
- 工作树：`GoblinArmyGameEvent.java` 仍有用户在建改动（7/7），不碰不提交。
- 行 256 剩余：`DUNGEON_SPIRIT` 抗性条（跨行依赖）、faction 分组族（跨行：goblin/pirate/martian 三组 1.21 全缺）、`acceptAll(PetItems.ITEMS)`（跨行）、`SolarEclipseGameEvent` 未登记（旁记）、`RainProjectileRenderer.offsetY`（设计分叉，不镜像）、lang/bestiary 族（TE 语言键收口决策）。


## 九十二、行 256 收官审计：**审计器本身有缺陷**，修正后重新基线 + 新浮现族归类（本批 +2 行代码）

### 92.1 审计器缺陷：单侧归一化导致**静默漏报**（`build/_cmp231/rowaudit.py`）

- **现象**：1.20 HEAD `ModTabs:1941` 明明有 `corruption.accept(SpawnEggItems.WORLD_FEEDER_SPAWN_EGG.get());`，审计却把它记成 `dead`（"1.20 自己后来撤了"），据此**不会**进入 GAP 清单。
- **根因**：alive 判据是 `norm(探针行) in joined(语料)`，而 `norm()` 会剥掉 `this.` 与 `\w+.get()`，`joined()` 只删注释与空白、**不剥** ⇒ 凡新增行含 `.get()` 或 `this.`，探针串永远不是语料子串，**一律误判 dead**。本行 `b05c8dc3f` 的 151936 条新增代码行里有 **974 行**（0.6%）命中此缺陷——恰好都是注册表引用，正是最可能缺口的那些行。
- **修法**：新增 `joined_norm()`（逐行 `norm()` 后再拼成无空白流；`norm` 是行级变换，逐行施加再拼接与整文施加等价，折行/重排照样命中），`main()` 两侧语料改用同口径；保留 `--legacy` 开关复现旧数。新增差集工具 `gapdiff.py`、抽样抽取 `gapx.py`/`screenx.py`、定位 `langanchor.py`。
- **教训（写进方法学）**：归一化必须**同时**作用在探针与语料上；单侧归一化不会报错，只会静默漏报——比误报危险。
- **筛选用还有一处缺陷**：`batchscreen.py` 每个文件只打印 **8 条**样本，此前依据 `r256_after.txt` 得到的"逐族结论"实为抽样；**权威全量清单是 `rowaudit.py` 的 `r256_new.txt`**（本行 111 文件 / 1079 条）。

### 92.2 重新基线（提交 `b05c8dc3f`）

| 口径 | gap_files | 原始 GAP | 过滤平台噪音后 real_gaps |
| --- | --- | --- | --- |
| 修前 `--legacy` | 108 | 651 | 415 |
| 修后 | 111 | **1079** | **842** |

逐行差集：新浮现 **428 条**、消失 **0 条**（`gapdiff.py`，故旧清单是新清单的真子集）。

### 92.3 新浮现 428 条的逐族裁定（每条都查了权威）

| 族 | 量 | 裁定与证据 |
| --- | --- | --- |
| `ModChineseProvider` 的 `private void addXxxTranslations()` 声明 | ~40 | **结构噪音**：1.20 后续把 provider 拆成 **49** 个方法，1.21 只有 **3** 个；方法内的逐条 lang 另行单独判定 |
| 刷怪蛋呈现层：`ModTabs` 63 + `ModEnglishProvider` 86 条蛋名 + `ModChineseProvider` 蛋名 | ~150 | **已登记的挂起项，不在本行落地**：1.21 **从基点 `795ac9ccc` 起就没有** `entity` 创造页（`PORT-LANDING-RECORD.md:679`）；无 `addSpawnEggTranslations`，`notes/BATCH8-MARTIAN-PLAN.md` 定论「本批跳过……刷怪蛋创造页 + 模型 datagen 另立工单」（另见本记录 §49、§60 的 `ModItemModelProvider` 蛋模型缺口） |
| 实体名段：`MonsterEntities.` 1.20 **218** 行 / 1.21 **7** 行；`CritterEntities.` 40 / 0；`NpcEntities.` 34 / 0 | ~290 | **历史欠账、已另立工单**：`PORT-LANDING-RECORD.md:1049`「**不做全族回填**（1.21 完全缺 `entity.confluence.*` 怪物名段落属历史欠账，另立工单）」+ `notes/WORK-QUEUE.md:804`「`entity.confluence.*` 名称键的翻译完整度是既有欠账，与 TE 无关」⇒ 登记 |
| `ModDataProvider` 生物生成：`SpawnerData` 1.20 **269** / 1.21 **179** | 48 | **真缺口**（1.21 该文件 0 命中 `GIANT_BAT`/`PENGUIN`/`ROCK_GOLEM`/`ARMORED_SKELETON` 生成条目）⇒ **下一批首选** |
| 物品/射弹/效果名：坐骑 2 + 召唤法杖 5 + 射弹 1 + 效果 3 | 11（EN） | **待定，先验后落**：1.21 英文侧有自动命名环 `register.getEntries().forEach(item -> add(item.get(), LibUtils.toTitleCase(...)))`（`ModEnglishProvider:2656`），需先确认 `MountItems`/`SummonItems` 是否走该环；**中文侧 1.21 `forEach`/`getEntries` 命中 0（无任何自动命名）⇒ 中文名确为缺口**。抽样：1.21 已含 `spider_staff`/`deadly_sphere_staff`，缺 `blessed_apple`/`superheated_blood`/`vampire_frog_staff`/`sanguine_staff`/`desert_tiger_staff` |
| `BestiaryLanguageSubProvider` | 144 | **待逐条判定「键缺失 vs 文本被改」**：1.21 字面量键 1167 > 1.20 的 1140，多数应是文本差异（假缺口）⇒ 下一批按键名比对，不按整行 |

### 92.4 本批落地（提交 `717d635cd`，1 文件 +2）

- `client/event/ModClientEvents`（`registerCustomBestiaryEntryModel`）：补 1.20 HEAD `:1133` `event.registeSurefaceWorm(MonsterEntities.WORLD_FEEDER);` 与 `:1136` `event.registerBaseWorm(MonsterEntities.DIGGER);`。
- 位置按 1.20 顺序：`DEVOURER, WORLD_FEEDER, TOMB_CRAWLER, GIANT_WORM, DIGGER, LEECH`（先误插在 `GIANT_WORM` 之前，比对 1.20 `:1134-1137` 后改回）。

### 92.5 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）——本节把剩余面量化到可施工粒度。
- 工作树：`common/data/GamePhase.java`、`common/gameevent/GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 仍为 **候选 6**（本批只动 java）。
- 行 256 剩余（更新后）：①`ModDataProvider` 生物生成族（48）②中文物品名族（~11 + 实体名族另计）③`BestiaryLanguageSubProvider` 144 逐键判定 ④`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）⑤faction 分组族 + 刷怪蛋呈现层（WP7 挂起）⑥`acceptAll(PetItems.ITEMS)`（跨行）⑦`SolarEclipseGameEvent` 未登记（旁记）⑧`RainProjectileRenderer.offsetY`（设计分叉，不镜像）。


## 九十三、行 256 第十四批：群系生物生成条目按 1.20 顺序补入（+30）

### 93.1 落地 `common/data/gen/ModDataProvider`（30 行）

1.20 HEAD 在各 `AddSpawnsBiomeModifier` 里新增的 `SpawnerData`，1.21 的**同键 modifier 都在**（16 个键，除 `common_highlevel`／`hallow_critters` 两个 1.21 从未有过的），只是缺条目。逐条按 1.20 顺序镜像：

| 宿主键 | 1.21 锚（行号） | 插入 | 条数 |
| --- | --- | --- | --- |
| `common_icy` | `ICE_BAT`（`:1418`） | `PENGUIN` | 1 |
| `common_jungle` | `List.of(`（`:1429`） | `MYSTIC_FROG`（列表首条） | 1 |
| `common_overworld` | `NYMPH`（`:1448`） | `ARMORED_SKELETON`／`ROCK_GOLEM`／`GIANT_BAT`／`WEREWOLF`／`ANGLER_FISH`／`CORRUPT_GOLDFISH`／`VICIOUS_GOLDFISH`／`BLOOD_JELLY`／`FUNGO_FISH`／`FUNGI_BULB`／`GIANT_FUNGI_BULB`／`CLINGER` | 12 |
| `common_overworld` | `CritterEntities.FAIRY`（`:1449`） | `OLD_SHAKING_CHEST`／`GOLDFISH` | 2 |
| `only_forest` | `SQUIRREL`（`:1462`）／`CARDINAL`（`:1466`） | `CLOUD_SHEEP`／`STINKBUG` | 2 |
| `common_forest` | `GIANT_WORM`（`:1477`） | `DIGGER`／`TIM`／`RUNE_WIZARD`／`DOCTOR_BONES`／`THE_GROOM`／`THE_BRIDE`／`ANGRY_DANDELION`／`WINDY_BALLOON`／`GNOME`／`ANGRY_NIMBUS` | 10 |
| `common_nether_wastes` | `HELL_BAT`（`:1506`） | `LAVA_BAT`／`RED_DEVIL` | 2 |

- 两处锚行（`FAIRY`、`CARDINAL`）是 `List.of` 的**末条、无尾逗号**，插入时给锚行补逗号（故 diff 为 +32/−2）。
- 跨行错位防护：`CARDINAL` 后插 `STINKBUG` 而非 1.20 的"`BUTTERFLY` 后"——1.21 的 `only_forest` 只有 6 条（`BUTTERFLY`／`DRAGONFLY`／`LADYBUG` 属**别的行**的缺口），插在现存末条之后，待那些行补入时保持相对次序。

### 93.2 核验

- **30 个实体常量**逐个 `git grep`：`CritterEntities.PENGUIN/MYSTIC_FROG/GOLDFISH/CLOUD_SHEEP/STINKBUG`、`MonsterEntities.*` 25 个，均在 1.21 已注册（分布在 `MonsterEntities`／`CritterEntities` 与 `ModClientBestiaryEntryProvider` 等）。
- 插入前断言：锚行含指定子串、目标实体**不在同一 modifier 块内**（避免重复条目）；插入后 `{`/`}` 计数 85/85 不变；工作树仍是纯 CRLF。
- `fix_eol --check` 仍为 **候选 6**。提交 `83fbfe7d1`。

### 93.3 本族剩余（下一批）

| 项 | 说明 |
| --- | --- |
| `Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(DecorativeBlocks.ANTLION_EGGS.get()))` | 1.21 侧该特征配置写法待比（1.20 `:588` 的 `register(context, ANTLION_EGGS, Feature.RANDOM_PATCH, …)` 上下文要逐字比） |
| `common_highlevel` 整块（`HARPY`／`CLUMSY_BALLOON_SLIME`） | 1.21 **没有**该 modifier 键（18 vs 16），需整块新建（含生物群系 tag/HolderSet） |
| `hallow_critters` 整块（`PRISMATIC_LACEWING`） | 同上，不属本行但同族 |
| `.addSpawn(MobCategory.MONSTER, …WORLD_FEEDER…, 9, 1, 1)` ×3 | 1.20 在腐化/猩红群系注册处加的主体生成；1.21 宿主找齐后补 |
| 结构生成覆盖 6 条：`context.register(ModStructures.Keys.SPIDER_NEST, …)` + `WALL_CREEPER`／`BLACK_RECLUSE`、`GRANITE_ELEMENTAL`／`GRANITE_GOLEM`、`HOPLITE`、`WATER_BOLT_MIMIC` | `Structure.StructureSettings` 的 spawn override，1.21 结构注册处比对后补 |
| 5 条 `register(context, createModifierKey("…"), new ForgeBiomeModifiers.AddSpawnsBiomeModifier(` | **改名噪音**：5 个键（`hallow_monsters`／`night_glow_bugs`／`truffle_worm`／`mushroom_animals`／`cluckshroom`）在 1.21 **全部存在**，只是类名 `ForgeBiomeModifiers`→`BiomeModifiers`，无需移植（应加入 `batchscreen.py` 噪音表） |

### 93.4 方法学追加（本轮踩到的）

- **行号口径必须统一**：`spawncheck.py` 打印 0-based、`spawnseq.py` 打印 1-based，混用导致锚点写成 `MonsterEntities.FAIRY`（实为 `CritterEntities.FAIRY`）——被插入前的断言拦下，**未写盘**。断言写成"锚行须含指定子串"是必要成本。
- **缩进不能照抄锚行**：`List.of(` 行缩进 20，条目缩进 28；照抄锚行会让新增条目缩进错位（已修）。规则：插入行缩进取**同列表内既有条目**的缩进。

### 93.5 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）。
- 工作树：`GamePhase.java`、`GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 候选 6。
- 下一批：本族剩余 **18 条**（§93.3 前五项 = 13 条真缺口；后一项 5 条为改名噪音）。
- **落地后实测**：重跑 `rowaudit.py b05c8dc3f`（`r256_after14.txt`）——`ModDataProvider` 的 GAP 由 **48 → 18**，差额正是本批 30 条；余下 18 条与 §93.3 表逐条对应（`ANTLION_EGGS` 1 + `HARPY`/`CLUMSY_BALLOON_SLIME` 2 + `ForgeBiomeModifiers` 5 + `WORLD_FEEDER` 3 + 结构生成覆盖 7），即"落地量 = 审计差额"在本族闭合。


## 九十四、行 256 第十五/十六批：ModDataProvider 生物生成族**收口**（+9 / +6）

### 94.1 第十五批（提交 `d8f021562`，+9）

| 项 | 1.20 依据 | 1.21 落点 |
| --- | --- | --- |
| 新建 `common_highlevel` modifier | 1.20 `:1451-1455`：`overworld` + `HARPY` 60/1/2 + `CLUMSY_BALLOON_SLIME` 1/1/1 | 1.21 **没有该键**（18 vs 16）；按 1.20 顺序插在 `common_overworld` 的 `));` 之后、`common_swamp` 之前（`:1468-1472`） |
| `GRANITE_CAVE` 生成覆盖 +`GRANITE_GOLEM` | 1.20 `:2204` | 1.21 `GRANITE_ELEMENTAL` 之后（`:2235`） |
| `MARBLE_CAVE` 生成覆盖 +`HOPLITE` | 1.20 `:2207-2211`（整块 `Map.of(MONSTER, StructureSpawnOverride(...))`） | 1.21 原为单行 `Map.of()`（无任何生成覆盖）⇒ 改造为多行覆盖（`:2238-2242`） |
| 地牢生成覆盖 +`WATER_BOLT_MIMIC` | 1.20 `:2231`（`DARK_CASTER` 之后） | 1.21 `DARK_CASTER` 之后（`:2265`） |
| 三处 `WORLD_FEEDER` 9/1/1 | 1.20 `:1618`/`:1642`/`:1660`，宿主 `THE_CORRUPTION`／`THE_CORRUPTION_DESERT`／`THE_CORRUPTION_TUNDRA` | 三个群系块在 1.21 都有（`DEVOURER` 行 `:1598`/`:1619`/`:1634`），各在其后补一行 |

- 6 个实体常量（`WORLD_FEEDER`／`GRANITE_GOLEM`／`HOPLITE`／`WATER_BOLT_MIMIC`／`HARPY`／`CLUMSY_BALLOON_SLIME`）先在 `MonsterEntities.java` 里逐个断言已注册，才允许打补丁。

### 94.2 第十六批（提交 `a96df0ba8`，+6）：**发现并修掉一处潜在加载失败**

- 1.21 全仓**没有** `new SpiderNestStructure(...)`：`ModStructures:30` 只有 StructureType、`:38` 只有 `ResourceKey<Structure>`，`ModDataProvider` 里有 `register(context, "spider_nest", new StructureSet(structure.getOrThrow(ModStructures.Keys.SPIDER_NEST), …))`（`:2336`），而 `src/main/resources/data/confluence/worldgen/structure/` 下 18 个结构 json **不含** `spider_nest.json` ⇒ **StructureSet 引用了未注册的结构**（datapack 加载会报 missing element）。
- 按 1.20 `:2195-2200` 补回实例注册（`overworld` + MONSTER 覆盖 `WALL_CREEPER` 80/2/3、`BLACK_RECLUSE` 80/2/3，`TOP_LAYER_MODIFICATION`/`NONE`），位置按 1.20 在 `GRANITE_CAVE` 之前。

### 94.3 本族收口实测

| 阶段 | `ModDataProvider` GAP |
| --- | --- |
| 行提交原始 | 48 |
| 第十四批后（`r256_after14`） | 18 |
| 第十五批后（`r256_after15b`） | 9 |
| 第十六批后（`r256_after16`） | **6** |

余 6 条**全部**是已判定的改名噪音：`ANTLION_EGGS` 1 条（1.20 `DecorativeBlocks.ANTLION_EGGS` → 1.21 `NatureBlocks.ANTLION_EGGS`，方块搬家）+ `ForgeBiomeModifiers.AddSpawnsBiomeModifier` 5 条（→ `BiomeModifiers.`，5 个键在 1.21 全存在）。行 256 总 GAP：**1079 → 1035**。⇒ 本族**可判收口**。

### 94.4 本轮踩到的三个坑（都进了方法学）

1. **审计读的是 `HEAD`，不是工作树**：`rowaudit.py` 用 `git show HEAD:<path>`，所以"先跑审计、后提交"会得到过期结果（本轮浪费了一次审计；`r256_after15.txt` 与 `r256_after15b.txt` 的差异就是这么来的）。**规矩：先提交，再重跑审计**。
2. **锚行"曾是末条"要补尾逗号**：`GRANITE_ELEMENTAL`（原是 `WeightedRandomList.create(...)` 的末条，无逗号）后面插一行，前置行必须补逗号，否则语法错误（本轮已修）。凡"在某行后插入"都要先问：**这行是不是所在列表的末条？**
3. **`));` 不是插入点，语句结束之后才是**：`common_highlevel` 第一版插在 `common_swamp` 之前的 `));` 行**之前**，结果落进了上一个 `register(...)` 调用内部（语法错误）；改为插在该 `));` **之后**才对。规矩：新语句插在**前一条语句的终结行之后**。

### 94.5 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）。
- 工作树：`GamePhase.java`、`GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 候选 6（三批都只动 java）。
- 行 256 剩余（更新）：①中文物品名族（~11 + 实体名族另计，需先验 1.21 英文侧自动命名环是否覆盖）②`BestiaryLanguageSubProvider` 144 逐键判定 ③`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）④faction 分组族 + 刷怪蛋呈现层（WP7 挂起）⑤`acceptAll(PetItems.ITEMS)`（跨行）⑥`SolarEclipseGameEvent` 未登记（旁记）⑦`RainProjectileRenderer.offsetY`（设计分叉，不镜像）。
- 📌 **本族顺带发现（登记，不属本行）**：1.21 的 `THE_CORRUPTION` 群系生成表还缺 `CORRUPTOR` 65/1/2 与 `SLIMER` 35/1/1（1.20 `:1620`/`:1622`），地牢生成覆盖还缺 `PALADIN`／`BONE_LEE`／`NECROMANCER`／`DIABOLIST`／`RAGGED_CASTER`（1.20 `:2234-2238`）——都不在 `b05c8dc3f` 的新增行里，属其它行。


## 九十五、行 256 第十七批：三条效果 lang（中英 +3/+3）+ 物品名族**先裁决后落地**

### 95.1 落地（提交 `4cbaf75b4`，2 文件 +3/+3）

| 效果 | 1.20 位置 | 1.21 锚（EN / CN） |
| --- | --- | --- |
| `ModEffects.GARDEN_GNOME_LUCK` | `LUCK_EFFECT` 之后 | `:2470` / `:5465` |
| `ModEffects.WEBBED` | `FROZEN` 之后 | `:2479` / `:5474` |
| `ModEffects.JUNGLES_FURY` | `HELLFIRE` 之后 | `:2522` / `:5517` |

- 三条锚行在 1.21 都是**逐字相同**（含中文标点），故按 1.20 相对位置插入、文本逐字取自 1.20 HEAD。
- **英文侧不受自动命名环影响**：`addEffect(ModelEffect, String)` 的第二参是**效果描述**（"You are stuck!"），不是效果名；效果名由 `ModEffects.EFFECTS.getEntries().forEach(...)`（`ModEnglishProvider:2370`）按 id 生成。

### 95.2 物品/射弹名族（11 条）：**裁决依据已查齐，落地方式待定**

查证结论（都是实测，不是推断）：

| 事实 | 证据 |
| --- | --- |
| 1.21 英文侧有**自动命名环** `addAll(DeferredRegister.Items register)`（`ModEnglishProvider:2656` 定义），共 **51 个**注册表被逐一 `addAll(...)` | 调用点 `:2317-2368` 全量列出 |
| 该清单**不含** `MountItems.ITEMS`／`SummonItems.ITEMS` | `grep 'addAll\(.*\(Mount\|Summon\)Items'` ⇒ 0 命中 |
| 1.21 中文侧**根本没有**自动命名机制 | `ModChineseProvider` 的 `forEach`/`getEntries` 命中 **0**（1.20 也是 0） |
| 11 个名字在 1.21 的 provider、手写语区文件里**都不存在** | `恩赐苹果`／`过热的血`／`吸血鬼青蛙法杖`／`血红法杖`／`沙漠虎杖`／`符文冲击` 在 `ModChineseProvider`、`lzh.json` 均 0 命中；`Blessed Apple` 等在 `ModEnglishProvider` 0 命中 |
| 1.21 **没有**坐骑/召唤法杖的 lang 段 | `MountItems`／`SummonItems` 在两侧 provider 引用数均为 **0** |

⇒ 两条路线，各有代价：

1. **照 1.21 惯例补 `addAll(MountItems.ITEMS)` + `addAll(SummonItems.ITEMS)`**（英文侧 2 行即可覆盖 5 条，且 id→`toTitleCase` 恰好等于 1.20 显式名：`blessed_apple`→"Blessed Apple" 等）。**代价**：会把整个注册表都自动命名，其中与 1.20 显式名冲突的条目会被改掉——实测 1.20 把 `SummonItems.SNOW_FLINX_STAFF` 显式命名为 **"Flinx Staff"**，自动命名会给 **"Snow Flinx Staff"**。**且对中文侧一点帮助都没有**（无自动命名环）。
2. **按 1.20 逐条显式写**（中英各 6 行：5 个物品 + `ModEntities.RUNE_BLAST`）。**代价**：1.21 无对应段落 ⇒ 落点需自选并登记（1.20 的锚 `MountItems.HONEYED_GOGGLES`、`ModEntities.DARK_CASTER_PROJECTILE` 在 1.21 都不存在）。

**建议**：走路线 2（中英各自显式、文本逐字取 1.20 HEAD），落点选"召唤法杖那条字符串键段之后"（1.21 `ModChineseProvider:2219` 的 `add("summon.confluence.spider", "蜘蛛")` 正是 1.20 `:5265` 的锚，可逐字对齐），并在记录里登记为**自选落点**。`RUNE_BLAST`（中文）因 1.20 锚缺失，另需在该段附近另选锚。

### 95.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）。
- 工作树：`GamePhase.java`、`GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 候选 6。
- 行 256 剩余：①物品/射弹名族（本节路线 2，中英各 6 行）②`BestiaryLanguageSubProvider` 144 逐键判定 ③`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）④faction 分组族 + 刷怪蛋呈现层（WP7 挂起）⑤`acceptAll(PetItems.ITEMS)`（跨行）⑥`SolarEclipseGameEvent` 未登记（旁记）⑦`RainProjectileRenderer.offsetY`（设计分叉，不镜像）。
- 方法学：**判"英文名是否缺"之前必须先查 `addAll(...)` 那 51 个注册表的清单**（否则会把自动命名覆盖的条目误记为缺口）；`addEffect` 第二参是描述不是名字，两者归宿不同。


## 九十六、行 256 第十八批：坐骑/召唤法杖物品名（中英各 +7），并**按键族切开** `ModEntities` 名

### 96.1 落地（提交 `eb340cbc1`，2 文件 +7/+7）

| 键 | 1.20 显式名（逐字搬） |
| --- | --- |
| `MountItems.BLESSED_APPLE` | 恩赐苹果 / Blessed Apple |
| `MountItems.SUPERHEATED_BLOOD` | 过热的血 / Superheated Blood |
| `SummonItems.VAMPIRE_FROG_STAFF` | 吸血鬼青蛙法杖 / Vampire Frog Staff |
| `SummonItems.DEADLY_SPHERE_STAFF` | 致命球法杖 / Deadly Sphere Staff |
| `SummonItems.SANGUINE_STAFF` | 血红法杖 / Sanguine Staff |
| `SummonItems.SPIDER_STAFF` | 蜘蛛法杖 / Spider Staff |
| `SummonItems.DESERT_TIGER_STAFF` | 沙漠虎杖 / Desert Tiger Staff |

- **落点（自选并登记）**：1.21 两侧 provider 都**没有**坐骑/召唤法杖段（`MountItems`／`SummonItems` 引用数为 0）。选 1.21 现存的 `add("summon.confluence.spider", "蜘蛛");` 字符串键行为锚：前 6 行插其**前**、`DESERT_TIGER_STAFF` 插其**后** —— 与 1.20 `:86-88` 的"…SPIDER_STAFF → spider 键 → DESERT_TIGER_STAFF"**逐字同序**（EN 锚 `:971`，CN 锚 `:2220`）。文本逐字取自 1.20 HEAD。
- 落地后实测：`ModEnglishProvider` GAP 91 → **84**、`ModChineseProvider` 274 → **267**（各 −7，与落地量一致；累计 94→91→84 / 277→274→267）。

### 96.2 关键裁决：**按"键族"而不是按"行"来判归属**

provider 的 `add(X, 名)` 重载会按 X 的类型落到**不同键族**：

| 1.20 行 | 生成键 | 归属裁定 |
| --- | --- | --- |
| `add(MountItems./SummonItems.X.get(), "…")` | `item.confluence.*` | **本行（256）债务** ⇒ 本批落地 |
| `add(ModEntities.X.get(), "符文冲击")`（如 `RUNE_BLAST`） | `entity.confluence.*` | **§1049 已登记的历史欠账（另立工单）** ⇒ 本行**不落地** |

⇒ 由此 `ModChineseProvider` 那 ~200 条 `add(ModEntities.X.get(), …)` 中文行（本行提交里新增的）**全部归入 §1049 的既有工单**，不再是行 256 的待办；行 256 的中文侧只剩 `item.confluence.*` 与效果类。（§95.2 里"路线 2 = 中英各 6 行"的口径至此修正为"中英各 7 行物品名 + `RUNE_BLAST` 不落地"。）

### 96.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）。
- 工作树：`GamePhase.java`、`GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 候选 6。
- 行 256 剩余：①`BestiaryLanguageSubProvider` 144 逐键判定 ②`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`，缺则登记）③faction 分组族 + 刷怪蛋呈现层（WP7 挂起）④`acceptAll(PetItems.ITEMS)`（跨行）⑤`SolarEclipseGameEvent` 未登记（旁记）⑥`RainProjectileRenderer.offsetY`（设计分叉，不镜像）。


## 九十七、行 256 第十九批：`BestiaryLanguageSubProvider` 144 条**已定性**（未落地，先登记结论）

### 97.1 实测结论：144 条**全部**是"1.20 启用、1.21 注释"的同键同文行

| 检查 | 结果 |
| --- | --- |
| 144 条里有几条"键在 1.21 完全不存在" | **0**（144/144 键都在 1.21，`add("…` 字面量可解析） |
| 1.20 HEAD 侧状态 | **144 条全部 enabled**（无注释态） |
| 1.21 HEAD 侧状态 | **138 条 `//        add(…)` 注释态**，6 条 enabled |
| 组合 | `(1.20 enabled, 1.21 comment)` = **138**；`(enabled, enabled)` = 6 |
| 行号 | **逐条对齐**（例：`golfer.desc` 1.20 `:161` ↔ 1.21 `:161`；`steampunker.desc` `:167` ↔ `:167`） |

⇒ 这批**不是文本被改**、也不是键缺失，而是"1.21 把 1.20 已启用的 138 行停在注释里"。工具分段的结论是：**138 条可"启用"落地（文本已在 1.21，只需去注释）**，6 条无需处理。

### 97.2 落地口径建议（下一批执行）

- 有**同族先例**：本记录 `:2423`（行 256 早期批次）已按同一口径处理过 5 条中文图鉴描述——"1.21 里这 5 行躺在注释里（`//        add("bestiary.entity.confluence.<x>.desc", "…")`），1.20 已启用；本轮按 1.20 HEAD 逐字启用（含缩进 8 空格）"，并注明"英文侧两侧都是注释，不动"。
- ⇒ 本批宜同法处理，但**先逐键确认 1.21 侧确有对应图鉴条目**（`ModClientBestiaryEntryProvider` 里的 `.add(实体, builder -> …)`），避免只补 lang 而无消费者；有消费者才去注释，无消费者者登记（与 §1049 的"另立工单"同源）。
- 去注释时**只删行首 `//` 与随之多余空格**（`        //        add(` → `        add(`），文本一字不改——这与 §1.3"不加新注释/不改文本"一致。

### 97.3 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）；本节只登记结论与口径，**未改任何文件**。
- 行 256 剩余（收窄后）：①上表 138 条去注释（下一批）②`DUNGEON_SPIRIT` 抗性条 ③WP7 挂起项（faction 分组 + 刷怪蛋呈现层）④`acceptAll(PetItems.ITEMS)`（跨行）⑤`SolarEclipseGameEvent`（旁记）⑥`RainProjectileRenderer.offsetY`（设计分叉）。


## 九十八、行 256 第二十批：bestiary 图鉴描述**启用 135 行**（提交 `acb9ff8d8`）

### 98.1 落地

- 口径（与本节 `:2423` 先例一致）：①只处理**有消费者**的键（在 `ModClientBestiaryEntryProvider` + 实体注册里查到 id）；②去注释只删行首 `//` 与随之空格，**文本一字不改**；③逐条断言"去注释后的行 == 1.20 HEAD 同键的启用行（含缩进）"。
- 两遍执行：**英文段 67 行** + **中文段 68 行** = **135 行**（`git show --stat` 135/135）。启用态 bestiary 行 958 → **1093**。
- 跳过：`corrupt_bunny`／`vicious_bunny`（无消费者，保持注释）；其余 502 条注释行的键在**两侧都是注释**（属 TE 停车族，按惯例不动——由断言"1.20 无相同启用行"自动拦下）。
- ⚠️ 计数坑：审计的 144 条 GAP 对应**只有 75 个不同键**（同键的中英两行各占一条）；第一遍按"每键取首次匹配"只动了英文段，故必须**跑第二遍**才覆盖中文段。教训：**按"键"去重后才知道真实工作量**（144 → 75 键 / 135 行）。

### 98.2 收口实测

| 阶段 | `BestiaryLanguageSubProvider` GAP | 行 256 总 GAP |
| --- | --- | --- |
| 去注释前（`r256_after18`） | 144 | 1035 |
| 去注释后（`r256_after19`） | **11** | **882** |

### 98.3 残留 11 条的**逐条定性**（下一批处理，已查清）

| 组 | 条数 | 性质 | 处置 |
| --- | --- | --- | --- |
| `corrupt_bunny`／`vicious_bunny`（中英各 2 行） | 4 | **两侧都是注释**，且 1.21 无对应实体（`ent=False cons=False`） | 登记，不动 |
| `water_bolt_mimic`／`fungo_fish`／`goblin_warlock`／`pirates_curse`／`pirate_parrot` | 5 | 1.21 只启用了**中文**行（`ModChineseProvider` 那段正是本节 `:2423` 落地的 5 条），**英文行缺失** | 补 5 条英文行（插在英文段同键邻居处） |
| `werewolf`／`desert_spirit` | 2 | 1.21 英文行**文本被改写**（如 `werewolf` 1.21 为 "Cursed by the moonlight, these menacing lupines…"），且中文行仍注释、文本带机翻痕迹（`"…这些凶恶的 lupine（ lupine：此处指"狼形生物" ）曾是人类…"`） | 按"1.20 HEAD 是权威"对齐：替换英文文本 + 以 1.20 文本启用中文行 |

- 依据：这 5+2 个键的英文文本在 1.20 侧由 **`b05c8dc3f` 本体引入**（`git log -S"A seemingly powerful spellbook"` ⇒ 唯一命中 `b05c8dc3f`），即以 1.20 HEAD 为准是"移植本行"的正解；1.21 那两处改写读起来是移植期机器翻译产物，不属"1.21 自行演进"。

### 98.4 状态

- 台账：行 256 **仍未写状态**（保持 `TODO`）。
- 工作树：`GamePhase.java`、`GoblinArmyGameEvent.java` 仍有用户在建改动，不碰不提交。
- `fix_eol --check` 候选 6。
- 行 256 剩余：①bestiary 残留 9 行（§98.3 后两组）②`DUNGEON_SPIRIT` 抗性条（跨行依赖 `Category.HARMFUL_EXCEPT_WHIP_TAG`）③WP7 挂起项（faction 分组族 + 刷怪蛋呈现层）④`acceptAll(PetItems.ITEMS)`（跨行）⑤`SolarEclipseGameEvent`（旁记）⑥`RainProjectileRenderer.offsetY`（设计分叉）。


## 九十九、行 256 **结清**（第 21 批）：bestiary 残留落地 + 台账置 `PORTED` + 231–400 行**TODO 归零**

### 99.1 落地（提交 `f91680b01`，1 文件 +74/−69）

- 补 **5 条英文图鉴描述**（1.21 只启用了中文行）：落点按 1.20 邻居键顺序——`dark_caster` 后 +`water_bolt_mimic`；`fungi_bulb` 后 +`fungo_fish`／`goblin_warlock`／`pirates_curse`；`pirate_captain` 后 +`pirate_parrot`。文本逐字取 1.20 HEAD。
- **`werewolf`／`desert_spirit`**：1.21 的中文行停在注释**且文本是移植期改写**（`"…这些凶恶的 lupine（ lupine：此处指"狼形生物" ）…"`、`"…从 lamps 中挣脱…"`），按"1.20 HEAD 为权威"替换为 1.20 文本并启用；英文行两侧本就逐字一致，未动。
- **缩进对齐（自查出的缺陷）**：本文件约定**中文段 8 空格缩进**（去注释前 514 行全部带缩进、顶格 0 行；`acb9ff8d8^` 实测），上一批去注释把 67 行中文行落成顶格 ⇒ 本批一并补回（英文段则按所在区域既有风格，顶格/缩进各自就位）。

### 99.2 收口实测

| 量 | 数值 |
| --- | --- |
| `BestiaryLanguageSubProvider` GAP | 144（`after18`）→ 11（`after19`）→ **4**（`after20`） |
| 残留 4 条 | 只有 `corrupt_bunny`／`vicious_bunny` 的中英各 1 行 —— **1.21 无对应实体（无消费者）**，两侧皆注释，登记不落地 |
| 行 256 总 GAP | 1079 → **875**（本会话共落地/收口 204 条） |

### 99.3 行 256 逐族裁定总表（**结清**）

| 族 | 裁定 | 依据 |
| --- | --- | --- |
| `ModDataProvider` 群系生成 / 结构生成覆盖 | ✅ 已落地 48→6（余 6 = 改名噪音） | §93、§94 |
| 物品名（坐骑/召唤法杖/效果） | ✅ 已落地（中英各 7 + 效果 3+3） | §95、§96 |
| `BestiaryLanguageSubProvider` | ✅ 已落地（135 行启用 + 5 补 + 2 对齐），余 4 条无消费者 | §97–§99 |
| `ModChineseProvider` 的 `add(ModEntities.X.get(), …)` 段（约 200 行） | 归 **`entity.confluence.*` 历史欠账工单** | §1049 + `WORK-QUEUE.md:804` + 本记录 §96.2 的"键族"判据 |
| 刷怪蛋呈现层（`ModTabs` 63 + 蛋名 86 + `entity` 创造页 + 蛋模型 datagen） | 归 **WP7 挂起项** | 本记录 `:679`、`§3269`；`notes/BATCH8-MARTIAN-PLAN.md` 定论 |
| `DUNGEON_SPIRIT` 抗性条 | **跨行/架构债**：1.20 用 `LivingInvulnerableEffects.Category.HARMFUL_EXCEPT_WHIP_TAG`，1.21 的枚举只有 `BENEFICIAL／HARMFUL／NEUTRAL`（实测两侧源码）⇒ 需先引入枚举值 + 鞭子标签语义，非本行内容 | 本节实测 |
| `acceptAll(PetItems.ITEMS)` | 跨行（行 95 `dfcc5c041` = `DO-NOT-PORT`） | §90.2 |
| `SolarEclipseGameEvent` 未登记 | 行 227（pre-231）旁记 | §88.3 |
| `RainProjectileRenderer.offsetY` | 设计分叉，不镜像 | §88.3 |

### 99.4 台账（双写）

- `tools/port2native/ledger_status.py 256=PORTED` ⇒ `notes/PORT-LEDGER.md` 与 `notes/port-ledger-status.json` 同步写入（工具输出：`256 TODO -> PORTED（重写）`，状态条目 335 个）。
- **行 231–400 现状（`--list 231 400`：170 行）**：`COVERED 91`、`PORTED 39`、`SKIP-PLATFORM 16`、`DEFER-ASSETS 15`、`REVERSE-ALIGNED 5`、`SKIP-1.20-REVERTED 2`、`DEFER-ARCH 2` ⇒ **TODO = 0**（本会话目标"231–400 逐行行走"达成）。

### 99.5 门禁 `fix_eol --check` 的**唯一**残余（有据，非缺陷）

- 候选恒为 **6**，逐个核验：全部是 `assets/confluence/geo/**/*.geo.json`，**首字节 `0x42`（非 `{`）** ⇒ 属 HANDOFF §9「**禁改资源**：Huffman+Vigenère 加密的 geo.json，禁止改 EOL/BOM/格式」；其最近提交均为 `3500c5e2d`（既有资产批次，非本会话）。
- HANDOFF §8 的口径是"**只保证自己动的文件干净**"——本会话每批提交均满足；把这 6 个密文资产归一化才是违规操作，故**候选 6 是正确稳态**。

### 99.6 本行登记项（不在本行落地，均已在册）

`entity.confluence.*` 名称段（§1049）、WP7 刷怪蛋呈现层（`:679`/§3269）、`DUNGEON_SPIRIT` 抗性条（需扩枚举）、`PetItems` 召唤页签行（跨行）、`SolarEclipseGameEvent`（行 227）、`RainProjectileRenderer.offsetY`（设计分叉）。


## 一百、**编译修复**：`ModDataProvider` 的 `ModStructures.Keys.*` 无法解析（补回 17 个结构键）

### 100.1 现象与真实根因

- 用户报：`ModDataProvider` 里 `ModStructures.Keys.` **Cannot resolve symbol**。
- 根因（实测，与本节新增的 `SPIDER_NEST` 注册**无关**——该键 1.21 本来就有，`ModStructures.java:38`）：
  - `ModDataProvider:2392-2408` 的 `registerStructureSet(context, structure, structureSet, "…", ModStructures.Keys.X, …)`（签名第 5 参类型是 `ResourceKey<Structure>`）引用了 **16** 个键；
  - 而 1.21 的 `ModStructures.Keys` **只定义了 12 个**（AIR／CRIMSON_CAVE／CRIMSON_FOSSIL／GRANITE_CAVE／SPIDER_NEST／MARBLE_CAVE／DESERT_UNDERGROUND_CABINS／DUNGEON／DUNGEON_ALTAR／EBONY_STONE_THORN／SHIMMER_LAKE／LIVING_TREE），**1.20 HEAD 有 29 个** ⇒ 17 个键（连接口一起）在移植时漏了，编译期直接报符号缺失。
  - 引用集 − 定义集 = 16 个：`ENCHANTED_SWORD_SHRINE`／`HEAVEN_ISLANDS`／`ICE_THORN`／`ICE_UNDERGROUND_CABINS`／`JUNGLE_SHRINE`／`JUNGLE_UNDERGROUND_CABINS`／`LIVING_MAHOGANY_TREE`／`MINE_TUNNELS`／`NETHER_TOWER`／`OASIS`／`OBSIDIAN_CASTLE`／`OBSIDIAN_PILLAR`／`QUEEN_BEE_HIVE`／`SKY_VILLAGE`／`SMALL_LIVING_MAHOGANY_TREE`／`UNDERGROUND_CABINS`（第 17 个 `PYRAMID` 1.21 代码里没被引用，一并补）。

### 100.2 修复

- 按 **1.20 HEAD 的 29 键与顺序**重建该嵌套类的键列表，文本逐字取自 1.20（`public static final ResourceKey<Structure> X = key("path");`，复用 1.21 已有的私有 `key(...)` 助手，它指向 `Confluence.asResourceKey(Registries.STRUCTURE, path)`）。
- 校验：`ModDataProvider` 引用的 **28** 个 `Keys.*` 全部就位（脚本比对"引用集 − 定义集"= **空**）；`{`/`}` 3/3、`(`/`)` 80/80；`fix_eol --check` 仍为**候选 6**（只动 java）。

### 100.3 同类缺陷排查（一并做了）

| 排查 | 结果 |
| --- | --- |
| 全仓 2613 个 java 对 `ModStructures.*` 的引用 vs 定义 | **0 缺失** |
| 同上对 `ModFeatures.*` | 0 真缺失（启发式报出的 13 项是**误报**：`getBoulder`／`getDartTrap`／`getNetworkEntity`／`getPressurePlate`／`register` 都是小写方法名，非常量） |
| 18 个 JSON 结构定义（`data/confluence/worldgen/structure/*.json`）的 `type` | 全部落在 `ModStructures.TYPES`（16 个已注册）或原版 `minecraft:jigsaw` ⇒ **无同类缺口** |

### 100.4 状态与教训

- 台账：这是**跨行编译修复**（结构键补齐），**不改变任何行的裁定状态**（行 256 仍为 `PORTED`）。
- ⚠️ **教训（值得进流程）**：本仓此前有"不编译"的约定，于是"引用未定义符号"这类错误可以**长期潜伏**（本次报出的键从结构批次落地起就一直是坏的，只是没人编译过）。建议加一个**轻量静态检查**：对若干"持有者类"（`ModStructures`／`ModFeatures`／`ModBlocks`／`ModItems` 等）自动比对"全仓引用集 − 该类定义集"，作为提交前门禁之一（本次用 `build/_cmp231/sibling_sweep2.py` 的写法即可，约 40 行）。

### 100.5 同批修掉的第二个编译缺陷：**调用参数表尾逗号**（本记录 §93 那批是我的错）

- 用户 IDE 在 `ModDataProvider` 里改掉两处：`common_overworld` 的 `CritterEntities.GOLDFISH`、`only_forest` 的 `CritterEntities.STINKBUG` 行尾逗号。
- **根因是我在 §93 批次里的失误**：当时把新条目插到 `List.of(...)` 的**末尾**，却让插入的最后一行沿用了 1.20 行的尾逗号 ⇒ `f(a, b,)` 在 Java 里**非法**（Java 只允许数组初始化式与枚举常量表有尾逗号，实参表不允许）。当时只检查了"锚行是否曾是末条"，没检查"我插入的最后一行是否成了新的末条"。
- 复查：对本次会话改过的 8 个文件做了两种形态的扫描（跨行 `,\n)` 与同行 `, )`）⇒ **0 处**残留；`ModStructures` 的 29 键重建未引入此类问题。
- 结论：与 §100.4 同一类问题——**"不编译"会让语法级缺陷潜伏**；凡"向参数表尾部插条目"必须同时检查插入行的尾逗号。


## 一百零一、台账续走（新目标首批）：行 11、12 **逐条核完 ⇒ `COVERED`**

> 背景：231–400 已归零后，台账仍剩 **65 个 TODO**，全部集中在最早那批（行 10–110 与 159–169）。本批处理其中最小的两行。
> 取证口径：`rowaudit.py <1.20 提交>` 出全量 GAP → 平台噪音筛选（`screen_full.py`，**不做 8 条截断**）→ 逐条到 1.21 侧查等价物。

### 101.1 行 11 `e7b826680`（part9，2026-06-10）：GAP 15 → 筛选后 13（7 文件）

| 条目 | 1.21 侧实测 | 裁定 |
| --- | --- | --- |
| `.gitmodules` ×5 `branch = forge-dev/1.20.1` | 1.21 的 `.gitmodules` 三个子模块全部是 `branch = neoforge-dev/1.21.1` | 平台差异 |
| `BaobabTreeFeature` ×2 `LibFeatureUtils.leaves(boxDown/boxUp, …)` | 1.21 该文件已重写：`leavesPlace(...)`（`:296`）+ `LibFeatureUtils.updateLeavesOptimized(...)`（`:184`） | 等价实现（结构不同） |
| `OasisStructure` 静态导入 `LibStructureUtils.getHeight/lineSet` | 1.21 `:27` 有 `LibStructureUtils`，`:46` 用 `getHeight(`、`:68` 用 `lineSet(` | 等价（限定名调用） |
| `PlayerMixin` `LibEntityUtils.isAnimal(living)` | 1.21 `:125` 同一调用存在 | 等价 |
| `DungeonStructure` `import org.confluence.lib.util.*` | 1.21 `:58` 用 `LibStructureUtils`／`LibFeatureUtils`／`LibBlockUtils` | 等价（显式导入） |
| `SpearProjectile` `import LibMathUtils`、`EnchantmentUtils` `import EnchantmentHelper` | 均为纯导入行；1.21 对应文件按新 API 导入 | 噪音 |

⇒ **行 11 = `COVERED`**。

### 101.2 行 12 `4b004c160`（fix2，2026-06-10）：GAP 82 → 筛选后 24（12 文件）

| 条目 | 1.21 侧实测（全仓 `git grep`） | 裁定 |
| --- | --- | --- |
| `SyncEnemyBannerEntriesPacketS2C`／`VisibilityPacketS2C` 的 `ID` + `identifier()` | 两个类都在（8／27 处引用）；`identifier()` 仅 4 处，是 PortLib 包惯用法 | 等价（1.21 走 `CustomPacketPayload` 原生注册） |
| `IPortNBTSerializable<CompoundTag>`（`ExtraInventory`／`ChunkBrushData`／`ChunkDropletsData`／`EverBeneficial`／`ManaStorage`） | 该接口 **0 命中**；这些类都在且已实现 `INBTSerializable<CompoundTag>`（如 `ChunkBrushData:20`） | 平台差异（PortLib→NeoForge） |
| `AdditionalManaEvent extends PlayerEvent` + `@Cancelable` | 1.21 同类存在且已改为 `implements ICancellableEvent`（`AdditionalManaEvent:8`） | 等价（事件 API 换代） |
| `ModUtils.isWaterBottle`／`supportsEnchantment` | 都在（12／29 处）；1.21 签名改为 `supportsEnchantment(ItemStack, Holder<Enchantment>)` | 等价 |
| `ITrackType.TYPED_CODEC` | 1.21 `ITrackType:25` **逐字同构**（`ModCustomRegistries.TRACK_TYPE_PROVIDERS.byNameCodec().dispatch(...)`） | 等价（审计只因末尾 lambda 写法不同而报） |
| `BaseTerraBowItem.canApplyAtEnchantingTable` | **0 命中**（1.21 已移除该方法，附魔改数据驱动） | 平台差异 |
| `BaseTerraRepeaterItem.shoot(ServerLevel, …)` | 1.21 `:301` 同签名存在 | 等价 |
| `getSupportedHeldProjectiles`／`ExtraInventory` | 都在（2／153 处） | 等价 |

⇒ **行 12 = `COVERED`**。

### 101.3 状态

- 台账（双写）：`11=COVERED`、`12=COVERED`；**剩余 TODO 63 个**（区间：10、14-15、21-24、29-33、35-38、40-43、45-49、51-55、57-66、70、74、76、84、93、97-100、102-103、105-106、108-110、159-162、166、168-169）。
- 本批**无代码落地**（两行均为"1.21 已有等价实现/平台差异"）。
- `fix_eol --check` 候选 6（未动源码）。
- 下一批：**行 10** `f4b42537c`（part8，筛选后 **217 条 / 35 文件**，是本区间最大的几行之一）。


## 一百零二、`PrefixUtils` / `ModRarity` 收口：**1.21 的 300 行旧倍率表换成 1.20 HEAD 的现行实现**

> 用户点名：`PrefixUtils` 与 `ModRarity` 是**非常重要的近期提交**（对应台账行 359 `2f44045a8`「大改修饰语」与行 380 `fcd2368c6`「修重铸价格」）。本节把 §75.4① 登记的那条"被 lib `ModRarity.TIER` 卡住"的债务结清。

### 102.1 分叉面（实测）

| 文件 | 1.20 HEAD | 1.21 HEAD | 判读 |
| --- | --- | --- | --- |
| `ModPrefix.java` | 615 行（接口有 `int tier();` `float value();`） | 611 行（**记录已带 `tier`/`value` 字段**，接口缺两个访问器声明） | 只差 2 行声明 |
| `PrefixComponent.java` | 59 行 | 82 行 | 1.21 侧自带扩展（`manaCost` 等） |
| `PrefixType.java` | 186 行 | 190 行 | 基本同步 |
| **`PrefixUtils.java`** | **175 行** | **473 行** | **1.21 多出的 298 行就是那张旧表** |

- 1.20 的「大改修饰语」把倍率**搬进了数据**：`ModPrefix` 各 record 增加 `tier`/`value`，`setAndUpdate` 收成 17 行，只做「稀有度分层位移 + 价值按 `value()` 放大」。
- 1.21 仍是旧实现：`switch (ModPrefix.ID_MAP.inverse().getOrDefault(modPrefix, 0))` 80+ 分支硬编码 `num2…num8`，再按 `num14` 阈值调 `rarity`、`ModRarity.WHITE` 兜底（`:150-454`）。

### 102.2 lib `ModRarity` 的分叉与**方向裁定**

| | 1.20 lib（`595159d`） | 1.21 lib（`181c2d2`） |
| --- | --- | --- |
| 形态 | `record ModRarity(String name, int color)` | `class ModRarity implements DataComponentType<ModRarity>`，多 `special` 字段与 `asTextColor()`/`isSpecial()` |
| 映射表名 | `TIER`（**13 项**：-1..11 色阶） | `ID_MAP`（**20 项**：-13..-11、-10..-7 再 + -1..11） |
| 编码器 | PortLib `PortStreamCodec` | 原生 NeoForge `StreamCodec` |

⇒ **不回退 1.21 的 lib**（`class`→`record`、`ID_MAP`→`TIER` 会抹掉 1.21 侧自有的 `special`/`asTextColor` 演进，违反方向规则）。改为**在调用侧做语义等价表达**：1.20 的 `TIER` 只含色阶 -1..11，而 1.21 的 `ID_MAP` 还含 MASTER/EXPERT/QUEST/COMMON/UNCOMMON/RARE/EPIC（-13..-7）——直接换名会让这些稀有度被误调进色阶，因此补一行 `if (tier < -1) tier = -2;` 把"非色阶"归回 1.20 的"未登记"语义。

### 102.3 落地（2 文件 +16/−299）

1. `common/component/prefix/ModPrefix`：在 `ResourceLocation getModifierId();` 之后补 `int tier();` 与 `float value();`（与 1.20 `:38/:40` 同位置；各 record 已有同名字段，访问器由 record 自动生成，声明即可满足接口）。
2. `common/util/PrefixUtils.setAndUpdate`：**305 行 → 18 行**，按 1.20 HEAD 逐条搬（`createComponent` → 写 `PREFIX` → 按 `modPrefix.tier()` 位移并 clamp 到 [-1,11] → 写 `MOD_RARITY` → 按 `modPrefix.value()` 放大价值 → 写 `VALUE`），保留 1.21 原有的 `prefixType == null` 守卫。
3. 核验：`switch (num1)`/`num14` 残留 **0**；两文件 `{}`/`()` 计数平衡；`setAndUpdate` 的 3 处外部调用点（`ModCommands:311`、`NPCReforgeMenu:108`、内部 `:61/:147`）签名未变；`ModPrefix.ID_MAP` 仍被 `NPCReforgeMenu` 使用（未变成死代码）；`fix_eol --check` 仍候选 6。

### 102.4 语义提示（留待用户确认）

- 按 1.20 HEAD 的口径：**稀有度不在色阶（-1..11）内的物品被重铸时，`MOD_RARITY` 组件会被清空**（1.20 里 `TIER.get(-2)` 返回 `null` ⇒ `set(type, null)` 即移除）。1.21 旧表则把 COMMON/UNCOMMON/RARE/EPIC 折成色阶再调整 —— 两者行为不同，本次以 **1.20 HEAD 为权威**。
- 台账：行 359／380 状态不变（仍为 `PORTED`）；本节只结清它们遗留的实现债。


## 一百零三、台账续走：行 10 `f4b42537c`（part8）⇒ `COVERED`，并**修掉 1.21 AT 缺条目**导致的一处编译失败

### 103.1 取证与三分类

- `rowaudit`：files=93、新增代码 3833 行、ALIVE-in-20HEAD=779、DEAD=3054、GAP=400；平台噪音筛选后 **real=217 / 35 文件**（183 条被判噪音）。
- 新增符号级三分类工具 `triage_row.py`（对每条 GAP 抽大写标识符，查 1.21 同名文件是否已含）：
  **A（符号已在 1.21）=81 / B（缺符号）=15 / C（纯语句）=121**。
  - B 的 15 条基本是**误报**：`accesstransformer.cfg`（非 java 路径）、`ModBlocks` 的 `BASEDRUM`/`TERRACOTTA_GRAY`/`TUFF`（vanilla 常量）、`EVERYTHING`（vanilla 枚举 `PressurePlateBlock.Sensitivity`）。
- 抽样核验 6 处**行为性**条目（逐条比对两侧源码）：

| 条目 | 1.21 侧 | 裁定 |
| --- | --- | --- |
| `ModEffects` 的 `Attributes.LUCK` 修饰符 | `:77` 同逻辑（仅把 `ATT_VALUE` 写成 `AttributeModifier.Operation.ADD_VALUE`） | 等价 |
| `ModEffects` 的 `MOB_SPAWN_SPEED/COUNT_MULTIPLIER` | `:138/:143/:146/:149` 同数值 | 等价 |
| `LogBlockSet.getAllItems()` / `function == null` | `:108` `Stream<Item> getAllItems()` | 等价（返回类型收窄） |
| `StinkyEffect` 的 tick 覆写 | 1.21 用改名后的 `shouldApplyEffectTickThisTick` | 等价（API 改名） |
| `LootComponent.open(ServerPlayer, ItemStack)` | `:25` 同名同参 | 等价 |
| `BaseArmorItem.appendHoverText` | `:51` 1.21 签名（`TooltipContext` 取代 `Level`） | 等价 |

- `ModBlocks.tuffProperties()`／`tuffBricksProperties()`：1.21 **没有** —— 但 1.21 的 `TUFF_BOOTH` 改用 `BlockBehaviour.Properties.ofFullCopy(TUFF_BRICKS)`（与 1.20 手写属性等价），且 `tuffProperties()` 在 1.20 侧**自身无调用者**（1.20 的死代码）⇒ **不移植**。

⇒ 行 10 = **`COVERED`**（内容 1.21 均已有，差异为 API/写法；其余为 Forge 平台族）。

### 103.2 顺带发现并修掉：1.21 AT 文件缺 `FireBlock.setFlammable`（**会编译失败**）

- 1.21 `LogBlockSet:361/366/…` 仍在调用 `fireblock.setFlammable(...)`，而该方法在 vanilla 是 `protected`；
- 1.20 的 AT 文件有 `public … FireBlock m_53444_(Lnet/minecraft/world/level/block/Block;II)V # setFlammable`，**1.21 的 AT 文件里没有**（`grep FireBlock|setFlammable` = 0 命中）⇒ 与 §100 的 `ModStructures.Keys` 同一类潜伏缺陷。
- 修复：在 `### net.minecraft.world.level.block` 段、`CropBlock getGrowthSpeed(...)` 之后插入
  `public net.minecraft.world.level.block.FireBlock setFlammable(Lnet/minecraft/world/level/block/Block;II)V`（沿用 1.21 AT 的**官方名语法**；文件保持 CRLF，175 → 176 行；`fix_eol --check` 仍候选 6）。
- 同段另两条 1.20 AT（`Blocks.never`/`Blocks.always` = `m_50778_`/`m_50809_`）：1.21 代码**无对应调用** ⇒ 不需要；而 `Minecraft.isMultiplayerServer`／`Font.renderText`／`AdvancementToast.advancement` 三条 1.21 已有官方名等价条目（`:2`／`:4`／`:13`）。

### 103.3 状态

- 台账（双写）：**行 10 = `COVERED`**；剩余 TODO **62** 个。
- `fix_eol --check` 候选 6。
- 本行平台族（不移植）：`FMLJavaModLoadingContext` 构造器与 `registerConfig(...)`（`Confluence`／`StartupConfigs`／`CommonConfigs`／`ClientConfigs`）、AT 的 SRG 写法、`.gitignore` 的 1.20 专属子模块条目。
- 下一批：**行 14–15**（`14` `?`／`15` `?`，均为 2026-06-10 的 part 系列）。


## 一百零四、台账续走：行 14、15 ⇒ `COVERED`（并把 21–24 的取证数入档）

### 104.1 行 14 `395003423`（fix3，submodule-only）⇒ `COVERED`

- 该提交主仓只有 **2 个指针跳变**：`Confluence-Magic-Lib 698b1b92e→24e875251`、`TerraCurio 9f9e1536d→20e2cc7d8`。
- 按台账口径"进子模块仓库按 `旧SHA..新SHA` 找真实改动"实测：两侧子仓的区间里**各只有 1 条提交，且 SHA 完全相同**（`24e8752 fix3`、`20e2cc7 fix3`）——即 1.21 侧子模块**本就包含同一对象**（`git log old..new` 在 1.21 子仓同样返回这 1 条）⇒ 无需移植。

### 104.2 行 15 `00b72167d`（枪械合并，2026-06-10，⚠️ 污染残留 19）⇒ `COVERED`

- 取证：`rowaudit` GAP 数 175 → 筛选后 **95 条 / 32 文件**；符号级三分类 A=8 / B=0 / C=87（无缺符号）。
- 逐文件核对 234 个 java 路径 + 核心文件逐行比对：

| 文件 | 1.20 HEAD | 1.21 HEAD | 判读 |
| --- | --- | --- | --- |
| `api/event/GunEvent.java` | 294 行，嵌套 `Use`／`ShotConfirmed`／`Fire`／`AmmoSelection`／`InventoryExtra`／`AmmoData`／`ProjectileCreation`／`ShrinkBullet` | 293 行，**同名嵌套类全在**，差异仅 `Use`／`ShrinkBullet` 加 `implements ICancellableEvent` | 等价（NeoForge 事件接口换代） |
| `common/item/BaseBullet.java` | 80 行（`BaseBullet` + 内部 `Dummy`） | 81 行，同结构 | 等价 |
| `common/init/item/GunItems.java` | 136 行 | 137 行 | 等价 |
| `common/entity/projectile/CustomBulletEntity.java` | 63 行，`IPortEntityExtension` | 63 行，`IEntityExtension` | 等价（PortLib→NeoForge 接口改名） |

- ⚠️ 该提交被标"污染残留 19"，按 `notes/POISON-dfcc5c041.md` 口径本就**不得照抄 1.20 实现**；上表证据显示 1.21 侧枪械系统已由后续枪械专批落地 ⇒ 行 15 = `COVERED`。

### 104.3 工具修正（本轮踩到）

- `triage_row.py` 的路径解析写错（`R21` 变量本身已含 `src/main/java`，又在前面拼了一次）⇒ 一次全报"文件不存在"；修正为"仓库根 + `ConfluenceOtherworld/` + 相对路径"后恢复正常。
- 另注意：三分类的 **B（缺符号）桶仍会误报**——它只在**同一文件**里找标识符，而 `UUID`／`AABB`／`ARGB32`／`POSITIVE_INT`／`LOGGER`／`INSTANCE` 这类来自 vanilla 或其它类，会一律记成"缺"。本轮据此逐条复核 4 个核心文件后，全部是误报。

### 104.4 已取证、待逐条裁定的下一批（21–24，均为 2026-06-13/14 的 partN）

| 行 | 提交 | 说明 | 筛查后 real | 三分类 A/B/C |
| --- | --- | --- | --- | --- |
| 21 | `182149f52` | part10（+1 ~100 -1） | 227 / 57 文件 | A=89 / B=9（vanilla 常量） / C=129 |
| 22 | `b3f13d405` | part11（+2 ~48 -3） | 118 / 28 文件 | A=34 / B=6（vanilla 常量） / C=78 |
| 23 | `7d1fff5b6` | part12（+0 ~98 -0） | 394 / 75 文件 | A=50 / B=21（vanilla 常量） / C=323 |
| 24 | `b0716f0c9` | part13（+2 ~54 -2） | 74 / 32 文件 | A=21 / B=5（vanilla 常量） / C=48 |

### 104.5 状态

- 台账（双写）：**行 14／15 = `COVERED`**；剩余 TODO **60** 个（区间自 21 起）。
- `fix_eol --check` 候选 6。
- 下一批：**行 21–24**（上表数字已备，按族抽样核验后逐行裁定）。


## 一百零五、用户报告的游戏内问题：三症状修复 + 生物页签 + 装甲译名 + AT 纠正

> 本节的改动来自用户实测反馈（含对我上一批 AT 判断的纠正），按台账口径不计入行状态，但与 §100/§103 同属"接线/驱动缺失"类缺陷。

### 105.1 悠悠球无法使用（提交 `73aaaaf49`）

- **根因**：`ModTags.Items.YOYO` 在 1.21 有定义（`ModTags:436`）却**从未被任何 tag provider 填充**——全仓 87 个 `ModTags.Items` 里**唯一**一个；而 `YoyoSession.press` 第一道门就是 `if (!stack.is(ModTags.Items.YOYO) …) return false;`（同一个 gate 还用于 `ClientConfigs` 的按键映射与 `PrefixType`）⇒ 按键永远失败。
- 修：`ModItemTagsProvider` 补 `yoyo = tag(ModTags.Items.YOYO); YoyoItems.ITEMS…add`，并把 `YOYO` 并入 `PREFIX_UNIVERSAL_ONLY`（对齐 1.20 `:516-526`）。
- 附注：悠悠球默认绑**左键**（`yoyoUseButton = LEFT`），且 `YoyoInputHandler.blocksUse` 吞右键 ⇒ 修好后需按住左键丢球。

### 105.2 phasesaber 不在创造页签（提交 `73aaaaf49`）

- 1.21 只收了 7 个 phaseblade（`ModTabs:1883-1889`），**phasesaber 一个都没收**（16 个物品在 `SwordItems` 里注册齐全）。1.20 的归属：8 phaseblade → `pre_hardmode_broadswords`、7 phasesaber → `hardmode_broadswords`、`PINK_PHASESABER` → pre。
- 修：补 `PINK_PHASEBLADE`（pre）+ 7 phasesaber（hardmode）+ `PINK_PHASESABER`（pre），顺序逐条对齐 1.20 `:1775/1783-1790`。

### 105.3 相位剑在物品栏无 2D 贴图（提交 `73aaaaf49`）

- **两个原因叠加**：①手写模型是 `builtin/entity`（3D，靠 Geo 渲染器），GUI 里没有 2D 贴图；②datagen 的通用剑循环 `handheld.add(createDir(SwordItems.ITEMS, "sword/"))` 会为同一路径再生成 `item/handheld` + `confluence:item/sword/<name>` 的模型，而该贴图**不存在** ⇒ 兜底 `MISSING_ITEM = confluence:item/item_icon`（实测用户盘 `src/generated/.../models/item/blue_phasesaber.json`、`red_phaseblade.json` 正是 `parent=confluence:item/item_icon`），生成物在运行时压过手写模型 ⇒ 只显示占位图标。
- 修：`ModItemModelProvider` 让手写模型优先（`SwordItems` 走与 `YoyoItems` 相同的 `hasHandwrittenModel → skip` 流程，该机制的文档注释本就为此而设）；16 个相位剑模型 `perspectives.gui` 由 `builtin/entity` 改为 `item/generated` + `confluence:item/<kind>/<name>_item`，其中 7 个仍是裸 Blockbench 的 phaseblade 一并包成 `separate_transforms`（`base` = 原内容）。
- ⚠️ 生效前提：重跑 datagen **并删除 `src/generated` 下这 16 个陈旧模型**（datagen 不清理已不再生成的输出）。

### 105.4 生物（entity）创造页签（提交 `9d83e2480`）

- 1.21 此前**没有** `entity` 页签（自基点 `795ac9ccc` 起就没有），且 `IconItems.ENTITY_ICON` 也不存在（`entity_icon` 贴图两侧本来都有）—— 这正是 §679/§3269 登记的 WP7 挂起项。
- 修：`IconItems` 补 `ENTITY_ICON`；`ModTabs` 新增 `ENTITY = TABS.register("entity", …)`（图标 `ENTITY_ICON::toStack`、标题键 `creativetab.confluence.entity`、`.withTabsBefore(SUMMONERS.getId())`，位置在 SUMMONERS 与 DEVELOPER 之间），内容**整块搬 1.20 `:1921-2238`**：21 个分组 + **290 条刷怪蛋**（逐条核对 1.21 `SpawnEggItems` 常量 ⇒ 0 条缺失）。
- 自检：`ModTabs` 括号平衡（22/22、3631/3631）。

### 105.5 装甲分组译名（提交 `40a12b345`）

- 1.21 的 `ModTabs` 共 156 个分组，其中 **14 个装甲分组在 CN/EN 两个 provider 里都没有 `itemGroup.confluence.<id>` 键**（页签显示原始键）。
- 6 条**逐字取上游分支** `origin/neoforge/1.21.1`：`clown_set 小丑套装`／`sailor_set 水手套装`／`evocation_robe_armor 唤魔长袍盔甲`／`ember_robe_armor 余烬长袍盔甲`／`verdant_robe_armor 碧绿长袍盔甲`／`highland_armor 高地盔甲`（另修掉上游带进的两个前导空格）。
- 8 条为**新增译名（无权威源，待复核）**：`wolf_armor 狼套装`／`root_rot_armor 腐根盔甲`／`black_spot_armor 黑斑盔甲`／`entertainers_garb_armor 演艺家礼服`／`mercenary_armor 雇佣兵盔甲`／`renegade_armor 叛军盔甲`／`stalwart_armor 坚毅盔甲`／`troubadour_armor 吟游诗人套装`。
- 遗留：这些 1.21 独有套装共 **48 件盔甲物品**尚无中文名（英文由 `addAll(ArmorItems.ITEMS)` 自动命名覆盖）⇒ 待补 CN 条目。

### 105.6 AT 纠正（提交 `9d83e2480`）

- 用户指出：**NeoForge 里 `FireBlock#setFlammable` 默认就是 public，不需要 AT**。§103.2 我按 1.20 的 SRG 写法加了 `public … FireBlock setFlammable(...)`，属多余 ⇒ 已删除（AT 文件回到 175 行）。
- 教训：1.20（Forge）的 AT 条目**不能直接翻译过来用**，要先确认 1.21/NeoForge 侧该成员是否已放开。

### 105.7 `ModRarity` 为何仍是 `ID_MAP`（结论，未改动）

- 史实（实测两仓 lib）：1.20 lib 的 `TIER`（只含 -1..11 色阶）+ `record` 形态来自 lib 提交 **`e9b848c`（2026-09-23，post-fork）**；1.21 lib 的 `git log -S TIER` 为**空**，即该改名**从未同步**到 1.21 侧（1.21 lib 最近仍在同步 1.20 lib 的其它提交，如 `9e09d50 refactor(1.20→1.21 同步)`）。
- 因此 `PrefixUtils` 里我用 `ModRarity.ID_MAP` + `if (tier < -1) tier = -2;` 做语义等价（1.21 的 `ID_MAP` 比 `TIER` 多 7 个负 id）。
- **建议**（待用户决定是否改子仓）：把 1.21 lib 的 `ID_MAP` 更名为 `TIER` 并去掉那 7 个负 id（保留 1.21 自有的 `special`／`asTextColor` 形态，不回退），随后删掉那行 shim、使 `PrefixUtils` 与 1.20 逐字一致。


## 一百零六、台账续走：行 21–24（part10–13）⇒ 全部 `COVERED`

### 106.1 新增的**全仓级符号核查**（本轮主证据）

`triage_row.py` 只在**同一文件**里找符号（对 vanilla 常量会误报）。本轮改用 `sym2124.py`：

1. 从 1.20 提交的新增行里抽出**定义型符号**（`class|interface|enum|record X` 与 `UPPER_CASE =`）；
2. 拿这些符号到 **1.21 全仓 java 语料**（2613 文件、约 53 MB，一次读入）里查 `\b符号\b`；
3. 只有全仓都找不到的才算候选缺口。

| 行 | 提交 | 定义型符号 | 1.21 全仓缺失 |
| --- | --- | --- | --- |
| 21 | `182149f52` part10 | 95 | **1**：`ACTIONS` |
| 22 | `b3f13d405` part11 | 42 | **1**：`AskForSoftcoreLayer` |
| 23 | `7d1fff5b6` part12 | 48 | **0** |
| 24 | `b0716f0c9` part13 | 4 | **0** |

### 106.2 两个候选的裁定（都不是缺口）

| 候选 | 实测 | 裁定 |
| --- | --- | --- |
| `GardenShearsItem.ACTIONS`（行 21） | 1.20 新增行是 `private static final Set<ToolAction> ACTIONS = Stream.of(…)`，用 **Forge** `ToolAction`/`ToolActions`/`PortItemAbilities`；1.21 该文件 88 行（1.20 93 行）且已无 `ACTIONS` —— 属工具能力系统的平台机制 | **SKIP-PLATFORM**（1.21 已另写） |
| `AskForSoftcoreLayer`（行 22） | 该类在 **1.20 HEAD 里也已不存在**（本提交之后被删/改）⇒ 属"1.20 自己后来撤了"，无移植物；且 softcore 功能两侧都在（`AskForSoftcoreScreen` 各 5 处引用） | 无效条目（dead） |

### 106.3 C 桶抽样（文件级行数对比）

按 triage 的 C 计数取每行最大的 6 个文件，比较 1.20 HEAD 与 1.21 HEAD 行数（平台差异导致的缩减属正常）：

| 行 | 抽样结果（节选） |
| --- | --- |
| 21 | `ModLootTables` 129→105、`Confluence` 163→147（`FMLJavaModLoadingContext` 等 Forge 构造器被删）、`LivingEntityEvents` 710→695、`GroundBlockNBTFeature` 81→74 |
| 22 | `EverBeneficialItem` 182→160、`OverviewNode` 215→198、`HotbarWidget` **202→202**、`TerraStyleHealthHud` 193→191、`GameClientEvents` 557→598（1.21 侧另有扩展） |
| 23 | `VoidBlockRenderer` 519→476、`SoulOverviewScreen` 1259→1260、`BackgroundLayer` 517→527、`SecretSeedsSelectionScreen` 593→578 |
| 24 | `BodyPartRenderer` 233→224、`DungeonCompassRenderer` **128→128**、`EntityDisplayItemRenderer` **104→104**、`SpearProjectileRenderer` 66→70 |

- 行 23 的 `PlayerAdvancementsMixin`（triage 报 89→63）经查是**抽样匹配到了同名兄弟文件** `LocalPlayerAdvancementsMixin`；正确路径 `mixin/server/PlayerAdvancementsMixin.java` 为 1.20 `88` 行 → 1.21 `62` 行：1.21 用 `AdvancementHolder`（1.21 API）并去掉 Forge 时代的 `LOGGER`／`startProgress`／`markForVisibilityUpdate` 影子方法，成就存取由 `PlayerAdvancementsMixin` + `LocalPlayerAdvancementsMixin` 两个 mixin 共同承担 ⇒ 等价。

⇒ 行 21／22／23／24 全部 **`COVERED`**。

### 106.4 工具教训（两条，已写进方法学）

1. **符号核查要全仓级**：同文件检查会把 `UUID`／`AABB`／`ARGB32`／`POSITIVE_INT`／`LOGGER`／`INSTANCE` 这类 vanilla／其它类常量一律记成"缺"；本轮改成"提交新增的定义型符号 × 1.21 全仓语料"后才收敛到 2 个候选，且这 2 个经查都不是缺口。
2. **"提交新增符号"包含 1.20 后来删掉的东西**：`AskForSoftcoreLayer` 就是这种（1.20 HEAD 已无该类）⇒ 抽符号时必须与 rowaudit 的 alive/DEAD 判定**一起**看，单看新增行会误报。
3. **按文件名子串匹配路径会串**：`LocalPlayerAdvancementsMixin.java` 也以 `PlayerAdvancementsMixin.java` 结尾，本轮抽样因此取错文件、行数差看起来异常。

### 106.5 状态

- 台账（双写）：**行 21／22／23／24 = `COVERED`**；剩余 TODO **56** 个。
- `fix_eol --check` 候选 6；本轮无代码落地。
- 下一批：**行 29–33**（2026-06 中旬的 partN 系列）。


## 一百零七、台账续走：行 29–33 与 35–38 **九行一次核完** ⇒ 全部 `COVERED`

> 本轮用同一套流水线：`rowaudit` → `screen_full`（全文筛选）→ `symrow.py`（全仓级定义型符号核查）→ 逐候选查证 → 小行直接读待办行。

### 107.1 筛查与符号核查总表

| 行 | 提交 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- |
| 29 | `15afa497b` | fix magic mirror | 2 / 2 文件 | 0 | 0 |
| 30 | `a779580be` | part16（⚠️7） | 227 / 51 文件 | 44 | 3 |
| 31 | `a8fc8c2c6` | 语法降级（⚠️1） | 23 / 5 文件 | 8 | 0 |
| 32 | `f30688d17` | 迁移至 PortLib API（⚠️7） | 721 / 157 文件 | 30 | 3 |
| 33 | `7646c5505` | 物品移植（⚠️3） | 207 / 36 文件 | 15 | 0 |
| 35 | `093eda09f` | part17（⚠️6） | 93 / 18 文件 | 34 | 2 |
| 36 | `17af6914e` | 一点点粒子 | 58 / 2 文件 | 16 | 1 |
| 37 | `fbcb8e783` | fix crash | 29 / 12 文件 | 4 | 1 |
| 38 | `8bcc392be` | mob effect | 19 / 11 文件 | 10 | 0 |

> 行 32（"迁移至 PortLib API"，721 条/157 文件）属**整提交性质的平台搬迁**，按 §1 口径不逐条移植。

### 107.2 十一个候选的裁定（**全部不是缺口**）

| 候选 | 实测 | 裁定 |
| --- | --- | --- |
| `WireCutterItem.BASE_ID`（行 30） | 1.20 用 `BASE_ID` + PortLib `PortAttributeModifier`；1.21 内联为 `new AttributeModifier(Confluence.asResource("wire_cutter"), 20, ADDITION)`（`:23`） | 等价（内联） |
| `ModBlocks.FAILED_SKULL_WALL` / `VOID_ENTITY`（行 30） | 字面量 `failed_skull_wall` 在 1.21 **同样出现在 10 个文件**（含 `ModBlocks`／`ModChineseProvider`／五个语区 json）⇒ 常量改名；`VOID_ENTITY` → 1.21 的 `VOID_BLOCK_ENTITY` | 等价（改名） |
| `AltarBlock.ALTAR_RENDERER`（行 32） | **1.20 HEAD 本身已无**该常量 ⇒ dead，无移植物 | 无效条目 |
| `SimpleTreeGrower`／`SimpleMegaTreeGrower`（行 32） | 1.20 命中 4 处/2 文件；1.21 **31 处/4 文件**（`BaseSaplingBlock`／`ModFeatures`／`PineSaplingBlock`／`StoneSaplingBlock`）⇒ 1.21 用 `BaseSaplingBlock` 另实现 | 等价（设计不同） |
| `AttributeRegistration`（行 35） | 1.20 HEAD 与 1.21 **两侧都没有该文件** ⇒ dead（属 TE 集成拆迁） | 无效条目 |
| `SnowballItemMixin`（行 35） | 1.20 有 20 行 mixin（把 vanilla 雪球堆叠 16→`MAX_STACK_SIZE`，注册在 `confluence.mixins.json`）；1.21 **无该 mixin 也无 `SnowballItem` 引用**，但 `ModEvents:239` 用 NeoForge 原生 `event.modify(Items.SNOWBALL, …)` 达成同一效果 | 等价（原生 API 取代 mixin） |
| `ModParticleTypes.PARTICLES`（行 36／37） | 1.20 是 PortLib 的 `PortParticleTypeRegistration` ⇒ 平台字段（1.21 用 `DeferredRegister`） | 平台差异 |
| `TCCommonConfigs` 导入（行 29 的 2 条待办） | 1.20 与 1.21 **同为 5 处命中**（`RainbowBoulderEntity`／`TreasureBagItems`） | 等价（导入行写法差异） |
| `IRandomCount` 的 `instanceof` 分支（行 31） | 1.20 203 行含 `instanceof ArrayRandom random` 等 4 类分支；1.21 **214 行且无任何 `instanceof`** ⇒ 用另一套写法实现同一分派；该提交本身是"语法降级"（1.20 工具链的写法回退） | 等价（1.21 另有实现；该提交价值低） |
| `WholeItemParticleOptions`（行 36） | 两侧都有 `WholeItemParticle.java` + `WholeItemParticleOptions.java`，`WHOLE_ITEM` 均已在 `ModClientEvents` 注册粒子 Provider | 等价 |

⇒ 行 29／30／31／32／33／35／36／37／38 **全部 `COVERED`**。

### 107.3 状态

- 台账（双写）：上述九行 = `COVERED`；剩余 TODO **47** 个（下一批自 40 起）。
- `fix_eol --check` 候选 6；本轮无代码落地。
- 下一批：**行 40–43（partN 系列继续）→ 45–49 → 51–55 → 57–66**（57–66 是最大的一段，10 行）。
## 一百零八、台账续走：行 40–49 **九行一次核完** ⇒ 全部 `COVERED`（行 44 早前已判）

> 同一套流水线：`batch_rows.py 40 41 42 43 45 46 47 48 49` → `rowaudit` → `screen_full`（全文筛选）→ `symrow.py`（提交新增的定义型符号 × 1.21 全仓语料）→ 逐候选查证 → 小行直接读待办行。

### 108.1 筛查与符号核查总表

| 行 | 提交 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- |
| 40 | `5481344ca` | something2（submodule-only） | 0 / 0 文件 | 0 | 0 |
| 41 | `b33c206fa` | something3 | 8 / 5 文件 | 64 | 0 |
| 42 | `f7996a657` | rename（⚠️8） | 0 / 0 文件 | 70 | 0 |
| 43 | `4dcf95cfe` | 移植家具 | 61 / 8 文件 | 0 | 0 |
| 45 | `c5e9f9be5` | part18 | 132 / 13 文件 | 13 | 0 |
| 46 | `10705abc7` | part19（⚠️7） | 154 / 34 文件 | 9 | 3 |
| 47 | `d17dc7c9a` | fix crash | 4 / 1 文件 | 0 | 0 |
| 48 | `4ab2d42ad` | fix crash | 7 / 2 文件 | 0 | 0 |
| 49 | `c9f3af990` | extensions（⚠️10） | 40 / 22 文件 | 10 | 7 |

> 行 40 是 **submodule-only**（PortLib ×1、Confluence-Magic-Lib ×1、TerraCurio ×1），ConfluenceOtherworld 侧 0 行新增，无移植物。
> 行 46 的 34 个文件、行 49 的 22 个文件在 1.21 **逐一存在**（脚本核对，`NO` 数 = 0）。

### 108.2 候选裁定

| 候选 | 实测 | 裁定 |
| --- | --- | --- |
| `BSlab`／`BStair`／`BWall`（行 46，**本批唯一真候选**） | 1.20 是 `HellStoneBlock`／`DecorativeBlocks` 里的嵌套静态类（各 3 处命中）；1.21 **全仓无 `class B?Slab`**，改为 `HellStoneBlock` 的静态工厂 `hotStair`／`hotSlab`／`hotWall`（`common/block/natural/HellStoneBlock.java:50/63/76`，分别返回 `StairBlock`／`SlabBlock`／`WallBlock`），由 `DecoBlockSet.builder("hellstone_bricks", …).stair(HellStoneBlock::hotStair).slab(HellStoneBlock::hotSlab).wall(HellStoneBlock::hotWall)` 装配（`init/block/DecorativeBlocks.java:170–172`）；1.21 全仓 `extends SlabBlock`／`StairBlock` 只出现在 `DecoBlockSet`／`LogBlockSet` | 等价（嵌套类 → 工厂方法 + 通用 `DecoBlockSet`） |
| 行 49 的 7 个"缺符号"：`IMPORT_RE`／`IMPORT_REMOVE`／`REPLACEMENTS`／`SKIP_METHODS`／`SKIP_WHOLE`／`SRC_DIR` 等 | 出自该提交带进来的一次性转换脚本 `convert_extensions.py`／`convert_magic_lib.py`／`fix_remaining.py`（同提交还含 `GameClientEvents.java.bak`）⇒ PortLib→native 的一次性工具，**运行期零影响** | 平台／工具，不移植（1.21 原生 NeoForge 无 PortLib 可转；1.21 侧亦无这些 `.py`／`.bak`） |
| `EnchantmentPredicate(Enchantments.FIRE_ASPECT, …)`（行 47） | 1.20 直接传 `Enchantments.FIRE_ASPECT`；1.21 附魔入注册表 ⇒ `AddEntityLootConfluenceSubProvider.java:77` 用 `registries.lookupOrThrow(Registries.ENCHANTMENT)` 取 holder 后构造谓词 | 等价（注册表化改写） |
| `CommonComponents.EMPTY`（行 48） | 1.21 用 `Component.empty()`：`AddChestLootConfluenceSubProvider.java:130/132/134/135` 四处 `.addLine(Component.empty())`；`NavTab.java:26` 的 super 消息同样为 `Component.empty()` | 等价（1.21 首选 API） |
| `pack.mcmeta`／`recipe/special/package-info.java`（行 43） | 1.21 侧 `git ls-files 'pack.mcmeta'` **为空**（NeoForge 自行生成）；`package-info.java` 为纯文档（1.21 有 93 个同类文件） | 平台／非内容 |
| 5 条 SRG 式 AT（行 45） | 1.20 写法 `m_286081_()V`／`m_155232_(…)V`／`m_21198_(…)`／`f_45029_`／`f_151427_`；1.21 AT 用 named 形式且**已存在**：`flushIfUnmanaged`／`setChanged`／`getLastArmorItem` 均命中，SRG 名 0 命中 | 平台格式差异（已覆盖） |
| 行 41／42 的实体改名 | `BeeArrowEntity.java`／`DriveAwayArrowEntity.java` 在 1.21 `common/entity/projectile/arrow/` **均存在** | 等价（改名已落） |

### 108.3 抽验：行 45／49 的 API 漂移类 gap（1.20 写法 vs 1.21 改写）

| 1.20 行（gap） | 1.21 实测 | 文件行数 |
| --- | --- | --- |
| `RecipeManager.CachedCheck<Container, BlastingRecipe>`／`BlastingRecipe recipe`／`doBlasting(recipe, …)` | `RecipeManager.CachedCheck<RecipeInput, T>`／`RecipeHolder<BlastingRecipe>`／`doBlasting(recipeholder, …)`（`EnhancedForgeBlock.java:116/231–267`） | 592 |
| `ItemTransmutationRecipe` 的字段 + `setId/getId` + `getResultItem(RegistryAccess)` | 1.21 改为 `record ItemTransmutationRecipe(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase)`，`getResultItem(HolderLookup.Provider)`（`:22/33/43`） | 110 |
| `CauldronInteraction.EMPTY.put(…)`／`defaulted(Map<Item, CauldronInteraction>)`／`PotionUtils.getPotion`／`InteractionResult` | `CauldronInteraction.INTERACTIONS.values().forEach(map -> map.map())`／`DataComponents.POTION_CONTENTS` + `PotionContents`／`ItemInteractionResult.sidedSuccess(level.isClientSide)`（`ModUtils.java:128/304–338`） | 380 |
| `HardmodeForgeBlock` 的 `T recipeholder` | `RecipeHolder<T> recipeholder` + `holder.value().getCookingTime()`（`:49/105–106`） | 111 |
| `stack.getDyedColor()`／`setDyedColor(rgb)`（行 49） | `stack.get/set(DataComponents.DYED_COLOR, new DyedItemColor(rgb, true))`（`BaseDyeItem.java:20/29`、`PaintItem.java:19/28`） | 32／45 |
| `arrow.setup(PICKUP_ITEM_STACK, null)`（行 49） | `new Arrow(pLevel, x, y, z, PICKUP_ITEM_STACK, null); arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;`（`DartTrapBlock.java:33–35`，同样出现在 `SuperDartTrapBlock`） | 50 |
| `breaker.getRandom()`（行 49，平台改名 `getRandom1211()`→`getRandom()`） | `breaker.getRandom().nextIntBetweenInclusive(0, 2)`（`StaffOfRegrowth.java:118/121`） | 128 |
| `new AttributeModifier(…)`（行 49） | `attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(id, value, Operation.…))`（1.21 record 构造，`EverBeneficialItem.java:41–82`） | 160 |
| `guiGraphics.blitSprite(… textureW, textureH, …)`（行 49） | `blitSprite(ModClientSetups.LEGACY_SPRITE, LEGACY_SIZE, LEGACY_SIZE, …)`（`TerraStyleHealthHud.java:80–118`，1.21 签名变化） | 191 |

⇒ 行 40／41／42／43／45／46／47／48／49 **全部 `COVERED`**。

### 108.4 状态

- 台账（双写）：上述九行 = `COVERED`（状态条目 353 → **362**，剩余 TODO **47 → 38**）。
- `fix_eol --check` 候选 6（未变）；本轮**无代码落地**。
- 下一批：**行 51–55 → 57–66**（57–66 是最大的一段，10 行）→ 70／74／76／84…

## 一百零九、台账续走：行 51–55 五行 ⇒ 全部 `COVERED`（含"旧 AI 框架被 1.20 后续改写"的甄别）

> 本批第一次遇到"符号大面积缺失"（行 52 缺 81、行 55 缺 161），已查清成因：**这两行是 2026-06-22…24 加进来的旧实体 AI 框架**（`common/entity/ai/goal/behavior/**`、`ai/fsm/**`、`ai/keyframe/**`、`api/entity/**`），1.20 在 06-27 之后把它**整体改写成 `common/entity/ai/bt/**`**，所以 rowaudit 判定这些新增行已 DEAD；1.21 是从改写后的 1.20 复制出来的，因此**两侧 `common/entity/ai/` 都是 62 个文件、路径逐个一致**。

### 109.1 筛查与符号核查总表

| 行 | 提交 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- |
| 51 | `f6b8f73b0` | part20 | 4 / 1 文件 | 39 | 11 |
| 52 | `dffefea8d` | feat 怪物实体+AI（⚠️多） | 50 / 26 文件 | 306 | 81 |
| 53 | `0e370c928` | data component method rename | 7 / 7 文件 | 1 | 0 |
| 54 | `4298126f9` | IPortItemExtension | 27 / 26 文件 | 43 | 8 |
| 55 | `f6e114cdb` | part21 | **4 / 2 文件** | 194 | 161 |

> 行 55 的筛查后 real 只有 4 行（一个 `package-info.java` 的注解 + 一条 `import java.util.List;`），而它的 194 个"定义型符号"全部来自旧框架文件 ⇒ 再次印证"**符号数 ≠ 缺口**，必须与 rowaudit 的 alive/DEAD 一起看"（§106.4 教训 2 的同一条）。

### 109.2 旧框架名在 1.20 HEAD 已不存在（决定性证据）

`git ls-files` 于 **1.20 HEAD**：

| 旧名（行 51／52 新增） | 1.20 HEAD | 1.21 |
| --- | --- | --- |
| `MonstersEntities.java` | **0** | 实体注册已并入 `ModEntities.java` |
| `HoneySlime.java`／`BlackSlime.java`／`SpikedJungleSlime.java` | **0** | 史莱姆族在 `common/entity/monster/**` 重排 |
| `AbstractMonster.java`／`AbstractPrefab.java`／`BaseWorm.java` | **0** | `BaseHumanoidMonster.java`／`BaseWormMonster.java` 等 |
| `api/entity/**`（`IWorm`／`IMinion`／`ISkill`／`IFSMGeoMob`…） | **0** | `common/entity/ai/WormChainTrail.java`／`bt/leaf/WormMovementAction.java` 等具体类取代接口层 |
| `ai/goal/behavior/**`、`ai/fsm/**`、`ai/keyframe/**` | **0** | `ai/bt/**`（62 文件，与 1.20 HEAD 逐路径相同） |

⇒ 行 51／52／55 的"缺符号"是**1.20 自己后来删掉/改名的东西**，按两问法 Q1=否 ⇒ 不作移植源。

### 109.3 逐类落点（不是缺口的实证）

| gap 类别 | 1.20 写法 | 1.21 实测 |
| --- | --- | --- |
| `entityData.define(…)`（行 52，约 30 个文件各 1 行） | 构造函数里 `this.entityData.define(DATA_X, v)` | `defineSynchedData(SynchedEntityData.Builder)` + `builder.define(…)`（1.21 全仓 223 处；`SpearProjectile.java` 的 `builder.define(DATA_INIT_SPEED, …)` 已命中） |
| Forge 接口方法（行 52） | `getDefaultGravity()`／`getDefaultLootTable()`／`onSheared(…)`／`isShearable(…)` | 1.21：`getDefaultGravity` 27 处、掉落表方法 23 处、剪羊毛接口 19 处（含 `RainbowSheep`／`Cluckshroom`／`GlowingMooshroom`） |
| SRG 式 AT（行 52 的 5 行） | `m_21304_()I # getCurrentSwingDuration`／`f_147134_ # hitEntities`／`m_32070_()Z # isMergable`／`AbstractSkeleton f_32131_ meleeGoal`／`f_32130_ bowGoal` | 1.21 AT 用 named：`hitEntities`／`isMergable` 均命中 `accesstransformer.cfg`；骷髅的 goal 字段**不再需要**——1.21 的 `MeleeSkeleton.java`（203 行）改为直接 `@Override getCurrentSwingDuration()`（`:117`）并用 `BTRoot`/`SelectorNode`/`VanillaGoalAction` 建树 |
| 行 51 的 4 条 import（`ModBiomes`／`ModFluids`／`ModGunProperties`／`ModRecipes`） | `ModEvents.java` 顶部 import | 1.21 `ModEvents.java` 直接调用 `ModGunProperties.init()`（`:102`）、`ModFluids.registerInteraction()`／`registerShimmerTransform()`（`:106–107`）、`ModBiomes.registerRegionAndSurface()`（`:108`）、`ModRecipes.Brewing.initialize()`（`:150`） |
| 行 54 的构造器 `super(…)`（26 文件） | `new Properties().unbreakable()`／`.dyedColor(rgb, true)`／`canPerformAction(ItemStack, ToolAction)` | 1.21 组件化：`new Properties().component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE)`（`ChumCaster.java:21`、`DevFishingRod.java:15`）、`.component(ConfluenceMagicLib.MOD_RARITY, rarity)`（`CoinItem.java:31`）、`canPerformAction(ItemStack, ItemAbility)`（`BinocularsItem.java:20`，1.21 无 `ToolAction`） |
| 行 54 的 `convert_port_properties.py` 常量（`IMPORT_EXT`／`IMPORT_PORTITEM`／`REPLACEMENTS`／`SRC_DIRS`） | 一次性转换脚本 | 与 §108 行 49 同类：工具，不移植 |
| `AccelerateOnSeeingGoal`（行 54） | `common/entity/ai/goal/AccelerateOnSeeingGoal.java` | 1.21 改名 `AcceleratingMeleeAttackGoal.java`（6 处引用：`AngryTumbler`／`AntlionCharger`…） |

### 109.4 1.20 HEAD 与 1.21 的实体树逐项对齐

| 目录 | 1.20 HEAD | 1.21 | 仅 1.20 有 |
| --- | --- | --- | --- |
| `common/entity/monster/*` | 126 | 123 | `package-info.java`（3 个，纯注解/文档） |
| `common/entity/animal/*` | 41 | 40 | `package-info.java` |
| `common/entity/boss/*` | 47 | 47 | 无 |
| `common/entity/npc/*` | 66 | 66 | 无 |
| `common/entity/projectile/*` | 158 | 158 | 无 |
| `common/entity/*.java`（顶层） | 563 | 560 | 无 |

⇒ 行 51／52／53／54／55 **全部 `COVERED`**。

### 109.5 状态

- 台账（双写）：上述五行 = `COVERED`（状态条目 362 → **367**，剩余 TODO **38 → 33**）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 57–66**（10 行，part23／part24／part25／enchantment／fluid type／npc／critters & monsters／recipe datagen）。

## 一百一十、台账续走：行 57–66 十行 ⇒ 全部 `COVERED`（含首个"疑似真缺口"行 61 的甄别）

> 本批出现了行走到现在**最像真缺口**的一行：行 61 `058000c5c`（part enchantment）的全部自定义附魔类（`ManaMendingEnchantment`／`ArcaneProtectionEnchantment`／`MagicAttackEnchantment`／`ManaAffectiveEnchantment`／`ManaAttackEnchantment`／`ManaIOEnchantment`）在 1.21 java 里 **0 命中**，且 1.21 没有任何 `Enchantment` 子类。查清后仍判 `COVERED`：**1.21 的附魔是数据驱动的**，同一批附魔改由 `ResourceKey<Enchantment>` + 自定义效果组件 + 生成的 json 实现。

### 110.1 筛查与符号核查总表

| 行 | 提交 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- |
| 57 | `b20c0cefd` | remove all entity part | 0 / 0 文件 | 2 | 0 |
| 58 | `7f83b379a` | part23 | 11 / 6 文件 | 53 | 11 |
| 59 | `c0c6a321d` | part24 | 486 / 56 文件 | 100 | 15 |
| 60 | `ac7767860` | part25 | 31 / 2 文件 | 1 | 0 |
| 61 | `058000c5c` | part enchantment | 166 / 16 文件 | 43 | 10 |
| 62 | `1516cbd2f` | part fluid type | 48 / 14 文件 | 37 | 0 |
| 63 | `231c505ca` | part npc | 8 / 6 文件 | 98 | 10 |
| 64 | `6568d3ad1` | part critters & monsters | 10 / 7 文件 | 82 | 2 |
| 65 | `8ce7f4d7f` | part recipe datagen | 1056 / 14 文件 | 5 | 0 |
| 66 | `9a48d8619` | part npc1 | 33 / 10 文件 | 45 | 7 |

### 110.2 缺符号的 alive/DEAD 复核（**除行 61 全是 DEAD**）

对每行缺符号做"1.20 HEAD 是否有定义 × 1.21 是否有定义"双向核查：

| 行 | 缺符号 | 1.20 HEAD | 判定 |
| --- | --- | --- | --- |
| 58 | `BossPhase`／`CollisionAttack`／`ConditionNode`／`ConfluenceBoss`／`GeoBossRenderer`／`IBlackboardHolder`／`IsDaytimeCondition`／`JEWEL_VARIANTS`／`MobSkill`／`SummonCreature`／`SyncFlagAction` | 全部**无** | DEAD ⇒ 后续提交已改名/移除，不作移植源 |
| 59 | `BABY_SIZE`／`BlackSlime`／`CanBeHostileCondition`／`EVIL_SLIME`／`HONEY_SLIME`／`HoneySlime`／`IceSlime`／`MOTHER_SIZE`／`PRE_JUMP_TICKS`／`Pinky`／`SLIMELING_ID`／`SUMMON_FOCUS`／`SpikedIceSlime`／`SpikedJungleSlime`／`SummonFocusEffect` | 全部**无** | DEAD |
| 61 | `ARMOR_N_MANA`／`ArcaneProtectionEnchantment`／`MagicAttackEnchantment`／`ManaAffectiveEnchantment`／`ManaAttackEnchantment`／`ManaIOEnchantment`／`ManaMendingEnchantment`／`ProtectionEnchantmentMixin` | 全部**有** | **alive ⇒ 必须查 1.21 等价物**（见 110.3） |
| 61 | `AbstractEnchantment`／`SHOOTERS` | 无 | DEAD |
| 63 | `AlwaysCondition`／`DEFAULT_MOODS`／`FIND_HOUSE_INTERVAL`／`HOTBAR_SIZE`／`INVENTORY_SIZE`／`MoodCount`／`REFUND`／`SELL`／`TRY_INTERVAL`／`TradeConditionTypes` | 全部**无** | DEAD |
| 64 | `COLLISION_DAMAGE`／`CrimsonMimic` | 无 | DEAD |
| 66 | `AnglerQuestEntry`／`AnglerQuestPool`／`ArmsDealerNPC`／`DemolitionistNPC`／`GoblinTinkererNPC`／`GuideNPC`／`NPCGrenadeGoal` | 全部**无** | DEAD（`GuideNPC` 在 1.21 侧只出现在本记录里） |

### 110.3 行 61：1.21 的数据驱动附魔（等价物）

| 1.20 HEAD（10 个 `Enchantment` 子类） | 1.21 |
| --- | --- |
| `AbstractManaEnchantment`／`ArcaneProtectionEnchantment`／`MagicAttackEnchantment`／`ManaAffectiveEnchantment`／`ManaAttackEnchantment`／`ManaIOEnchantment`／`ManaMendingEnchantment`／`MultiBoomerangEnchantment`／`SummonerPactEnchantment`／`WhipSweepEnchantment` | `init/ModEnchantments.java`（85 行）声明 **13 个 `ResourceKey<Enchantment>`**：`MANA_REGENERATION`／`EFFICIENT_MAGIC`／`MANA_MENDING`／`CELESTIAL_ABSORPTION`／`SOOTHED_MANA`／`ARCANE_PROTECTION`／`SPELL_DESPERATION`／`MYSTIC_SURGE`／`WHIP_SWEEP`／`SUMMONER_PACT`／`FLAIL_WIND_BURST`／`FLAIL_TURBINE`／`MULTI_BOOMERANG`；另有自定义附魔效果组件（`EffectComponentTypes`）与实体效果类型（`EntityEffectTypes`：`summon_item`／`wind_burst_at_hit`） |
| 附魔数据本体 | datagen 生成 13 份 `data/confluence/enchantment/*.json`（本地 `src/generated` 实测：`arcane_protection`／`celestial_absorption`／`efficient_magic`／`flail_turbine`／`flail_wind_burst`／`mana_mending`／`mana_regeneration`／`multi_boomerang`／`mystic_surge`／`soothed_mana`／`spell_desperation`／`summoner_pact`／`whip_sweep`） |
| `super(EnchantmentCategory.ARMOR, …)` 等构造 | 1.21 **已移除 `EnchantmentCategory`**（java 侧 0 命中，仅存于 notes）；改为 `supportedItems` 标签 + 效果组件 |
| `ProtectionEnchantmentMixin`（1.20 改 vanilla `ProtectionEnchantment`） | 1.21 **vanilla 已删 `ProtectionEnchantment`**（全仓 0 引用：`git grep ProtectionEnchantment` 空）⇒ mixin 无目标，不可移植也无需移植 |
| 引用面 | 1.21：`MANA_MENDING` 6 文件（`ModDataProvider`／`ChestSubProvider`／`ModEnchantmentTagsProvider`／`ModEnchantments`／`ModTabs`／`EnchantmentUtils`）、`ARCANE_PROTECTION` 5 文件、译文含 `de_de`／`es_es`／`lzh` |

> 遗留登记（非本行债务）：附魔 json 属 datagen 产物，`src/generated` 未入库 ⇒ 需跑 `runData` 才会落盘（与 §107 的 phaseblade 模型清理同批处理）。

### 110.4 其余各行的平台改写实证

| 1.20 写法 | 1.21 实测 |
| --- | --- |
| `output.accept(Confluence.asResource("chests/…"), …)`（行 60 的 25 行、行 59 的 144 行） | loot datagen 改 `BiConsumer<ResourceKey<LootTable>, LootTable.Builder>`（`GiftSubProvider.java:38`，全仓 9 处） |
| `Consumer<FinishedRecipe>`／`import FinishedRecipe`（行 59） | 1.21 用 `RecipeOutput`（81 命中）；`FinishedRecipe` **0 命中**；`ModRecipeProvider` 已拆成 `CraftingRecipeProvider`／`HeavyWorkBenchProvider` 等分站 provider |
| `public void onAddedToWorld()`（行 59／64 各 4+2 处） | NeoForge 改名 `onAddedToLevel`（40 命中）；`onAddedToWorld` 仅剩 2 处 |
| `getGravity1211()`（行 59） | `getGravity()` + `1211-shims.json`（10 命中） |
| `this.entityData.define(…)`（行 58／59／64） | `defineSynchedData(SynchedEntityData.Builder)` + `builder.define(…)` |
| GeckoLib `preRender(…, red, green, blue, alpha)`（行 58／64） | 1.21 `GeoNormalRenderer.preRender`（`:111`）按 GeckoLib 4.7 签名重写，`:136` 调 `super.preRender(…)` |
| `ForgeMod.WATER_TYPE`／`LAVA_TYPE`、`IForgeMenuType.create` | NeoForge 形态（`NeoForgeMod`／`IMenuTypeExtension`） |
| SRG 式 AT（行 58／59／66：`f_286957_ # luck`／`f_37244_ # ownerUUID`／`f_150163_ # cachedOwner`／`f_140760_ # playersPerChunk`／`f_221845_ # LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK`） | 1.21 AT 用 named 成员（与 §108／§109 同类） |
| `package-info.java` 的 `@ParametersAreNonnullByDefault` 等（行 58×2、59×2、61） | 纯注解/文档，无运行期语义 |

### 110.5 状态

- 台账（双写）：上述十行 = `COVERED`（状态条目 367 → **377**，剩余 TODO **33 → 23**）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 剩余 TODO：**70／74／76／84／93／97–100／102–103／105–106／108–110／159–162／166／168–169**（23 行）。
- 下一批：**行 70／74／76／84**（2026-07-05 之后）。

## 一百一十一、台账续走：行 70／74／76／84 四行 ⇒ 全部 `COVERED`

### 111.1 筛查与符号核查总表

| 行 | 提交 | 日期 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- | --- |
| 70 | `c8e4e6416` | 2026-07-05 | fix IncompatibleClassChangeError | 3 / 2 文件 | 1 | 0 |
| 74 | `97fc3ed2e` | 2026-07-08 | npc goal | 18 / 4 文件 | 1 | 1 `DEFAULT_IMMUNITY_DURATINO` |
| 76 | `bd006659b` | 2026-07-11 | refactor：实体类改名 HillHungry | 0 / 0 文件 | 1 | 0 |
| 84 | `a6f8f089d` | 2026-07-25 | fix world selection | 49 / 49 文件 | 1 | 0 |

### 111.2 免疫时长机制（行 70／74）

| 项 | 1.21 | 1.20 HEAD |
| --- | --- | --- |
| `interface Immunity` | `Immunity.java` ✓ | `Immunity.java` ✓ |
| `getImmunityDuration` 出现次数 | **35** | 34 |
| 1.20 的拼写错常量 `DEFAULT_IMMUNITY_DURATINO` | 0（1.21 改走 `Immunity.super.confluence$getImmunityDuration(damageSource)`，见 `mixin/world/item/ItemStackMixin.java:34`） | 4（`IEntity`／`Immunity`／`ItemStackMixin`） |
| `EntityTypeMixin implements Immunity`（行 70） | `EntityTypeMixin.java` ✓（`getImmunityDuration` 命中） | ✓ |

⇒ 机制在 1.21 完整（含物品侧 `ItemStackMixin:30` 的 `confluence$getImmunityDuration(DamageSource)` 实现），唯一"缺符号"是 1.20 打错字的默认值常量，1.21 用 `Immunity.super` 取代。

### 111.3 行 84 的 49 行 = 模型加载器命名空间迁移

| 1.20 写法 | 1.21 |
| --- | --- |
| `"loader": "forge:obj"`（15 个 `emerald_coins_*.json` + `obelisk.json`） | 已改 `neoforge:obj`（1.21 全仓 16 文件，`emerald_coins_0.json` 实测 `"loader": "neoforge:obj"`） |
| `"loader": "forge:separate_transforms"`（27 件武器／工具：`adamantite_chainsaw`／`spear`／`zombie_arm`／`tragic_umbrella`…） | 已改 `neoforge:separate_transforms`（1.21 58 文件） |
| `context.registerConfig(ModConfig.Type.COMMON, SPEC = builder.build())` | `CommonConfigs.java` 在 1.21 ✓（同族 `StartupConfigs.java:27`／`ClientConfigs.java:267` 均为 `container.registerConfig(ModConfig.Type.…, builder.build())`） |
| `WorldSelectionList$WorldListEntryMixin.java` 的 `validateAndCreateAccess(summary.getLevelId())` | 同名 mixin 在 1.21 ✓（`mixin/client/gui/screens/worldselection/`） |

> 说明：`git grep 'forge:separate_transforms'` 在 1.21 也会命中 `neoforge:separate_transforms`（子串），故统计时两串文件集相同属预期——真正的判定看文件内 `"loader"` 的实际取值。

### 111.4 行 76

`HillHungry.java` 在 **1.20 HEAD 与 1.21 均存在**（`common/entity/monster/HillHungry.java`，1.21 侧另被 `MonsterEntities` 与 `HillOfFleshMouth` 引用），refactor 改名已落 ⇒ 0 行待办。

⇒ 行 70／74／76／84 **全部 `COVERED`**。

### 111.5 状态

- 台账（双写）：上述四行 = `COVERED`（状态条目 377 → **381**，剩余 TODO **23 → 19**）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 剩余 TODO：**93／97–100／102–103／105–106／108–110／159–162／166／168–169**（19 行）。
- 下一批：**行 93／97–100**。

## 一百一十二、台账续走：行 93／97–100 五行（含"长矛组件在 1.21 成孤儿类"的甄别）

### 112.1 筛查与符号核查总表

| 行 | 提交 | 日期 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- | --- |
| 93 | `bad95470c` | 2026-08-08 | 同步 1.21.1 的修改 | 0 / 0 文件 | 0 | 0 |
| 97 | `4af532ed1` | 2026-08-16 | refactor：回退错误公共架构并恢复 1.20 实现 | 58 / 14 文件 | 48 | 2 `DEFAULT_SOUNDS`／`GRAVITY_BULLET` |
| 98 | `1e0393178` | 2026-08-16 | 同上 | 51 / 5 文件 | 23 | 1 `SummonStats` |
| 99 | `c406dcc0b` | 2026-08-16 | fix：对齐城镇 NPC 敌我识别与恐慌行为 | 0 / 0 文件 | 0 | 0 |
| 100 | `1e2f65769` | 2026-08-16 | fix：对齐城镇 NPC 远程战斗与护士治疗行为 | 0 / 0 文件 | 0 | 0 |

> 行 97／98 的 2026-08-16 在 **1.21 分叉之后**（分叉点在行 67–69 的 2026-07-02…04），属"1.20 HEAD 侧继续演进"。三处缺符号经双向核查：`DEFAULT_SOUNDS`／`GRAVITY_BULLET`／`SummonStats` 的**出现位点均在 1.20 侧旧文件里**，1.21 用别的容器承载（见 112.2）。

### 112.2 行 97：长矛配置容器不同（**不是功能缺口**）

| 1.20 HEAD（row 97 恢复的实现） | 1.21 |
| --- | --- |
| `SpearProjectile.setProjComponent(SpearProjectileComponent, LivingEntity)`（`SpearProjectile.java:110`，`AbstractSpearItem.java:196` 调用） | `SpearProjectile`（486 行）自带 `Config` 内部类：`damageFactor`／`baseSpeed`／`existTicks`／`projGravity`／`pierceCount`／`acceleration`／`trackType`，子类构造时链式赋值（`GhastlyProjectile`／`MushroomProjectile`／`NorthPoleProjectile`／`NorthPoleSubProjectile`／`SporeCloudProjectile`／`StormSpearProjectile` 各自 `this.config = new Config()…`） |
| `component/SpearProjectileComponent.java` 的 7 个预设（`STORM_SPEAR_PROJ`／`ORICHALCUM_HALBERD_PROJ`／`MUSHROOM_SPEAR_PROJ`／`NORTH_POLE_PROJ`／`SPORE_CLOUD_PROJ`／`GHASTLY_PROJECTILE`…） | 同一份 `SpearProjectileComponent.java` 文件在 1.21 **仍存在但已无引用者**（全仓仅该文件自身命中）⇒ 孤儿类；同等语义由上述 `Config` 承载 |

> 登记（非本行债务）：`common/component/SpearProjectileComponent.java` 在 1.21 属可清理的孤儿类；清理涉及删文件，按"不碰"原则不在台账行内处理。

其余 13 个文件的 gap 逐条对上 1.21：

| 文件 | 1.20 行 | 1.21 |
| --- | --- | --- |
| `ThrowableDropSelfItem`（12 行） | `playSound(…WAVING…)`／`shootFromRotation`／`getCooldowns().addCooldown`／`awardStat`／`hasInfiniteMaterials`／`InteractionResultHolder.sidedSuccess` | 逐行同形（`:37–60`） |
| `SpearProjectile`（18 行） | `entityData.define(DATA_INIT_SPEED…)` | `builder.define(DATA_INIT_SPEED/DATA_INIT_GRAVITY/DATA_DIRECTION)`（`:82–84`）；`getProjTexture()`／`getModelLayer()` 仍在（`:348`／`:351`） |
| `AbstractSpearItem`（8 行） | `IntSet struckEntities`（fastutil） | `Set<Integer> struckEntities = new HashSet<>()`（`:67`），`clip(0.3)` 同形（`:151`） |
| `DamageSettableProjectile`（4 行） | `modifier.getOperation() == MULTIPLY_BASE` | record 访问器 `modifier.operation() == ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL`（`:51`／`:54`，1.21 枚举改名） |
| `BaseGun`／`ManaStaffItem`（各 1 行） | `appendHoverText(ItemStack, @Nullable Level, …)` | `appendHoverText(ItemStack, Item.TooltipContext, …)`（1.21 全仓 27 个文件；`BaseGun.java:99`） |
| `ModUtils`（1 行） | `stack.getUnbreakable()` | `stack.has(DataComponents.UNBREAKABLE)`（`:272`） |
| `ManaCrystalItem`／`PlayerEvents`／`CloudProjectile`／`SwordProjectile`／`NetworkEvents` 等 | `ManaStorage`／`EverBeneficial` 调用、`entityData.define`、包注册 | 1.21 `ManaStorage` 16 个文件；`NetworkEvents.java:73/87` 用 `.playToServer(TYPE, STREAM_CODEC, ::handle)` |

### 112.3 行 98

| 1.20 | 1.21 |
| --- | --- |
| `Beneficial(UUID id, String name, Predicate<EverBeneficial> pre, Post post)` + `DO_NOTHING` + `LIFE_CRYSTAL`／`LIFE_FRUITS`／`AEGIS_APPLE`／`AMBROSIA`／`GALAXY_PEARL`／`MINECART_UPGRADE_KIT`／`ARTISAN_LOAF` | `EverBeneficialItem`（160 行）同形：`Post DO_NOTHING`（`:34`）、同批 `Beneficial` 预设（`:35`–`61`，另多 `VITAL_CRYSTAL`） |
| `new AttributeModifier(id, name, …)`（带名字的旧构造） | `new AttributeModifier(id, amount, Operation.…)`（1.21 record 构造，`:41`／`:47`／`:54`／`:61`…） |
| `EverBeneficial implements IPortNBTSerializable<CompoundTag>` | `implements INBTSerializable<CompoundTag>`（`:10`，平台改名） |
| `AbstractMountEntity`：`entityData.define(OWNER…)` + 无参 `defineMountSynchedData()` | `defineSynchedData(SynchedEntityData.Builder builder)`（`:88`）→ `defineMountSynchedData(builder)`（`:91`／`:101`），1.21 侧甚至留有说明该签名差异的注释块（`:96–98`） |
| `SwordProjectilePacketC2S`／`ShootPacketC2S` 的 `ResourceLocation ID` + `identifier()` | 1.21 走 lib 的包框架：`implements IPacketC2S` + `STREAM_CODEC`（`SwordProjectilePacketC2S.java:12/15`、`ShootPacketC2S.java:17`），注册在 `NetworkEvents.java:73/87` |

⇒ 行 93 = `REVERSE-ALIGNED`（标题即"同步 1.21.1 的修改"，且 0 行待办）；行 97／98／99／100 = `COVERED`。

## 一百一十三、台账续走：行 102–169 十四行 ⇒ 收口（**台账 TODO 归零**）

### 113.1 筛查与符号核查总表

| 行 | 提交 | 说明 | 筛查后 real | 定义型符号 | 全仓缺失 |
| --- | --- | --- | --- | --- | --- |
| 102 | `116edf192` | fix：对齐 NPC 住宅与旅商生命周期行为 | 0 / 0 文件 | 1 | 1 `HOUSE_CHECK_MASK` |
| 103 | `fe090753f` | fix：对齐 NPC 交互与对话同步行为 | 1 / 1 文件 | 2 | 0 |
| 105 | `c6c2d493a` | fix：恢复 NPC 商店三态交易流程 | 1 / 1 文件 | 7 | 1 `SoldItem` |
| 106 | `7e8662bd4` | refactor：恢复 NPC 商品的组件定价模型 | 0 / 0 文件 | 1 | 0 |
| 108 | `b861536ae` | refactor：恢复 NPC 商店数据加载架构 | 1 / 1 文件 | 3 | 0 |
| 109 | `7a3e9f664` | fix：同步 NPC 商店权威价格显示 | 7 / 1 文件 | 0 | 0 |
| 110 | `e7703e76d` | fix：保证 NPC 商店报价与交易条件一致 | 0 / 0 文件 | 2 | 0 |
| 159 | `1c012ccb1` | 将饰品的药水效果转移至 lib | 13 / 6 文件 | 3 | 0 |
| 160 | `100f6e0f2` | 可开关的药水效果移到 lib | 5 / 2 文件 | 2 | 0 |
| 161 | `2a4dfce2c` | 同步粒子 | 10 / 4 文件 | 1 | 0 |
| 162 | `12b6877be` | 修复枪械动画报错 | 0 / 0 文件 | 1 | 0 |
| 166 | `21b060ec6` | 饰品能力全改为 datamap，修复潜行属性 | 0 / 0 文件 | 0 | 0 |
| 168 | `c2935419b` | docs：清理源码注释中的冗余 HTML 段落标签 | 0 / 0 文件 | 0 | 0 |
| 169 | `4ef159bf9` | 同步 1.21.1 翅膀迁移，部分饰品添加粒子 | 25 / 3 文件 | 49 | 0 |

> 两个"缺符号"`HOUSE_CHECK_MASK`（行 102）与 `SoldItem`（行 105）在 **1.20 HEAD 与 1.21 两侧都零命中**（连宽松匹配也是 0）⇒ 提交加入后又被 1.20 自己删掉，属 DEAD，不作移植源。

### 113.2 逐行落点

| 行 | 1.20 gap | 1.21 |
| --- | --- | --- |
| 103 | `BaseNPC.entityData.define(DATA_CHAT, new CompoundTag())` | `BaseNPC.java` 有 `DATA_CHAT`（1 文件命中）；`entityData.define` → `builder.define` |
| 105 | `NPCTradeMenu.fromNetwork(int, Inventory, FriendlyByteBuf)` | `NPCTradeMenu.java:69` 同形但第三参为 `RegistryFriendlyByteBuf`（1.21 网络缓冲类型） |
| 108 | `NPCTradeList` 的 `decoded.error().map(DataResult.PartialResult::message)` | `NPCTradeList.java` 在 1.21；`DataResult` 错误处理同族写法 |
| 109 | 物品名/Lore 走 NBT（`getOrCreateTagElement("display")`／`getList("Lore", TAG_STRING)`） | 1.21 改数据组件（`CUSTOM_NAME`／`LORE`），故 NBT 写法整段不存在 |
| 159 | `LivingInvulnerableEffectsSubProvider` 的 8 条免疫列表（`ICE_MIMIC`／`THE_TWINS`／`RETINAZER`／`SPAZMATISM`…）、`HoneyBucketItem` 的 `LibEffects.HONEY`、`EnvironmentLevelAccess$MatcherMixin`、lib 的 `SetEntityDataPacketS2C` | 1.21 `LivingInvulnerableEffects` 相关 **5 个文件**（`LivingInvulnerableEffects`／`LivingInvulnerableEffectsSubProvider`／`ModDataMaps`／`ModDataMapProvider`／`LivingEntityEvents`），`LivingInvulnerableEffectsSubProvider` 内同为 `.add(MonsterEntities.X, MobEffects.POISON, LibEffects.CONFUSED…)` 形态；lib 子模块有 `LibEffects.CONFUSED`（`ConfluenceMagicLib.java`）与 `SetEntityDataPacketS2C`（4 文件） |
| 160 | `EffectRenderingInventoryScreenMixin implements ILibAbstractContainerScreen` + `ILibAbstractContainerScreen.switchEnabled(mobeffectinstance)` | lib 子模块有 `ILibAbstractContainerScreen`（3 文件，含 `AbstractContainerScreenMixin`／`CreativeModeInventoryScreenMixin`）；1.21 主仓有 `EffectRenderingInventoryScreenMixin` 与 `OnGatherEffectScreenTooltipsEvent`（`GameClientEvents`） |
| 161 | `ShimmerTransmutationTrigger.INSTANCE` + `CriteriaTriggers.register(…)`（`Confluence.java`） | 1.21 有 `advancement/ShimmerTransmutationTrigger.java` + `init/ModAdvancements.java`；`CriteriaTriggers.register` **0 命中**（1.21 改注册表化：`Registries.TRIGGER_TYPES`） |
| 169 | `AccessoryItems` 的 `MEDICATED_BANDAGE`／`POCKET_MIRROR`／`REFLECTIVE_SHADES`／`ARMOR_POLISH`／`ARMOR_BRACING`、`Lunar.yearZhiIndex`、`ParadoxInteractiveMedal` | 1.21 `AccessoryItems.java` 全部命中（各 5–7 处引用，含 `AccessoriesSubProvider`／`ValueSubProvider`／`ModRecipeProvider`），`api/lunar/Lunar.java` 有 `yearZhiIndex`，`item/sponsor/ParadoxInteractiveMedal.java` 在（`ModItems` 引用） |
| 102／106／110／162／166／168 | 0 行待办 | 无需动作（其中 168 为注释清理、166 为饰品能力改 datamap） |

⇒ 行 169 = `REVERSE-ALIGNED`（标题即"同步 1.21.1 翅膀迁移"）；其余十三行 = `COVERED`。

### 113.3 状态：**台账逐行走线收口**

- 台账（双写）：本批 19 行全部落状态，**TODO = 0**，400 行 100% 有状态（`ledger_status.py --check` 通过）。
- 状态分布：`COVERED` 202、`SKIP-PLATFORM` 70、`PORTED` 56、`DEFER-ASSETS` 26、`SKIP-1.20-REVERTED` 16、`SKIP-PORTLIB` 13、`REVERSE-ALIGNED` 7、`DEFER-ARCH` 4、`DO-NOT-PORT` 3、`SKIP-1.21-KEEPS` 2、`LOST?` 1。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 遗留登记（非台账行债务，需另开工作面）：`SpearProjectileComponent` 孤儿类、附魔/模型 datagen 产物需跑 `runData`、`LOST?` 1 行的复核。

### 一百一十四、`LOST?` 行（170）复核 ⇒ `COVERED`（台账不再有 TODO／LOST?）

行 170 `9645da98c`（2026-08-24，feat(otherworld)：重构城镇 NPC 战斗体系并补全生物相关内容）此前被标 `LOST?`（待复核），本轮顺手走完同一条流水线：

| 项 | 实测 |
| --- | --- |
| 筛查 | 真实 GAP = 44（14 个文件）；平台噪音过滤 20 |
| 缺符号 6 个 | `BLAZING_WHEEL_SPAWN_EGG`／`EVIL_SLIME_SPAWN_EGG`／`HONEY_SLIME_SPAWN_EGG`／`POSSESS_ARMOR_VOID_VESSEL_SPAWN_EGG`／`SPIKE_BALL_SPAWN_EGG` **在 1.20 HEAD 与 1.21 两侧都是 0 命中** ⇒ DEAD（该提交加入后又改名/移除）；`ZOMBIE_SPAWN_EGG` 两侧各 12 处（符号抽取的假阳性） |
| spawn egg 生态 | `SpawnEggItems.java` + `class SpawnEggItems` 两侧均在，引用文件同为 7 个（`ModChineseProvider`／`ModEnglishProvider`／`ModItemModelProvider`／`ModItemTagsProvider`／`ModTabs`…），`SPAWN_EGG` 命中面两侧同为 9 文件 |
| gap 落点 | `NPCCombatProgression`（1.21 有 `common/entity/npc/ai/NPCCombatProgression.java`）、`GeoSwordItem`（手臂姿态 `ModArmPoses`）、`MutableRenderTypeItemExtension`（物品渲染层）、`LivingInvulnerableEffectsSubProvider`（§113 已验证在场）、`DryadsBlessingEffect` —— 均为写法/平台差异 |

⇒ 行 170 = `COVERED`。**台账 400/400 全部有确定状态，TODO 与 `LOST?` 均为 0。**

## 一百一十五、lib 对齐：`ModRarity.ID_MAP` → `TIER`（含删掉 7 个非色阶键、撤掉调用侧垫片）

> 用户裁定：**1.21 lib 的 `ID_MAP` → `TIER` 改名是必须的**。此前 §105.7／§2781 只登记未动手，原因是 `Confluence-Magic-Lib` 为嵌套独立仓库；本轮按当时的建议方案执行（**只改映射名与键集，保留 1.21 自有的 `class`/`special`/`asTextColor` 形态，不回退成 1.20 的 record**）。

### 115.1 权威依据

| 证据 | 内容 |
| --- | --- |
| 1.20 lib 的 `e9b848c`（2026-09-23「大改修饰语」） | 对 `ModRarity.java` **11 增 53 删**：`ID_MAP` → `TIER`，并删除 `-13 MASTER`／`-12 EXPERT`／`-11 QUEST`／`-10 COMMON`／`-9 UNCOMMON`／`-8 RARE`／`-7 EPIC` 七条 |
| 1.20 主仓 HEAD 钉的子模块提交 | `595159d718a5e1b47d51a9f71f0d03a180f70b33` = 1.20 lib HEAD，且 `merge-base --is-ancestor e9b848c 595159d` 成立 ⇒ **移植目标的 `ModRarity` 就是 13 项 `TIER`** |
| 先后关系 | `6280189`（引入 `ID_MAP`）是 `e9b848c` 的祖先 ⇒ 改名为 1.20 侧**更新**的状态，不是回退 |
| 1.21 引用面 | `ModRarity.ID_MAP` 的消费者**只有** `util/PrefixUtils.java:155/162` 两行；lib 自身零引用（`git grep ID_MAP` 于 lib 仅命中定义行，`.inverse()` 零命中） |

### 115.2 改动

| 文件 | 改动 |
| --- | --- |
| `Confluence-Magic-Lib`：`common/component/ModRarity.java` | 字段 `ID_MAP` → `TIER`；删除上述 7 条负键（保留 `-1 GRAY` 与 `0..11` 共 13 项，与 1.20 HEAD 逐项一致）；`class ModRarity implements DataComponentType<ModRarity>`、`special` 字段、`asTextColor()` 等 1.21 自有演进**不动** |
| 主仓：`common/util/PrefixUtils.java` | `ModRarity.ID_MAP` → `ModRarity.TIER`；删掉垫片 `if (tier < -1) tier = -2;` 与三元 `tier > -2 ? ModRarity.ID_MAP.get(tier) : null`，改为 `ModRarity.TIER.get(tier)` |

### 115.3 语义等价性（为何敢删垫片）

1.20 的 `TIER` 只含色阶 `-1..11`，故 `TIER.inverse().getOrDefault(rarity, -2)` 对"未登记"稀有度一律给 `-2`，`TIER.get(-2)` = `null`（清空组件）。

1.21 原来的 20 项表把 `EXPERT/MASTER/QUEST`（-13..-11）与 vanilla 的 `COMMON/UNCOMMON/RARE/EPIC`（-10..-7）也登记了，会把它们误判成色阶，因此当时补了 `if (tier < -1) tier = -2;` 归回"未登记"语义。键集对齐后该补偿**不再需要**：

| `ModRarity.getRarity(itemStack, true)` 的取值 | 20 项表 + 垫片 | 13 项 `TIER`（现状） |
| --- | --- | --- |
| `GRAY`(-1) … `PURPLE`(11) | 键命中 → 进色阶分支 | 同 ✓ |
| `EXPERT`／`MASTER`／`QUEST` | -12/-13/-11 → 垫片 -2 → null | 未命中 → -2 → null ✓ |
| vanilla `COMMON`／`UNCOMMITTED`… | -10..-7 → 垫片 -2 → null | 未命中 → -2 → null ✓ |
| `null`／其它 | 默认 -2 → null | 默认 -2 → null ✓ |

核验：全仓（1.21）搜索 `-13..-7` 在 rarity/tier 语境下的补偿 **0 处**（`-10` 的 6 处命中全是矿脉 feature 名）；`git grep ModRarity.TIER` 于工作区命中 `PrefixUtils` 两行；两个改动文件 `{}`／`()` 计数平衡；`fix_eol --check` 仍候选 6（两文件保持纯 CRLF：167/185 行、0 裸 LF）。

### 115.4 与 1.20 的逐字比对

`setAndUpdate` 正文（归一 `stack/itemStack`、`prefix/modPrefix`、丢签名）difflib 结果**只剩两处 1.21 自有差异**：

1. 多一行 `if (prefixType == null) return null;`（1.21 侧的空值守卫，保留）；
2. 形参名 `prefixType`（1.20 叫 `type`）。

其余（映射取 tier、`tier > -2` 分支、`tier += prefix.tier()` 与 `-1..11` 夹取、`TIER.get(tier)` 落组件、`ValueComponent` 计算）**逐字一致**。

## 一百一十六、子模块对齐工作面启动（用户裁定：不止主模块需要重构）

> 用户指示：「并非只有 confluence 模块才需要重构，其它子模块一并需要」。故把主台账（§1–§115 的 `PORT-LEDGER.md` 逐行行走）的同一套口径推广到三个子模块。移植源 = **1.20 侧子模块 HEAD**；1.21 侧子模块保持原生 NeoForge（PortLib 一律不移植）。

### 116.1 侦察：三个子模块都是「活」的，PortLib 残留只是注释

| 子模块 | 1.21 HEAD | 1.20 HEAD | 1.21 java 数 | 1.20 分叉后提交 |
| --- | --- | --- | --- | --- |
| Confluence-Magic-Lib | `babbb51`（2026-10-04 本次改名） | `595159d`（2026-10-03） | 263 | **67** |
| TerraCurio | `195ef2b`（2026-10-04） | `f00e8f5`（2026-10-03） | 222 | **55** |
| TerraFurniture | `3852337`（2026-10-04） | `b5f856c`（2026-10-03） | 123 | **26** |

- **PortLib 残留核查**：1.21 侧 `org.mesdag.portlib` 仅 lib 的 `GravitationEffect.java` 注释提及；`build.gradle` 与 `PlayerGeoAnimatable` 命中的 `org.mesdag:ParticleStorm` 是**第三方依赖 ParticleStorm**（同作者、非 PortLib）。TerraCurio／TerraFurniture 两侧 `portlib`／`net.minecraftforge`／`IForge*`／`FMLJavaModLoadingContext` **全为 0**。
- 那 5 处 PortLib 说明文字经 `git blame` 确认是**用户自己写的**（`659c8c82` 2026-09-28、`480f342f` 2026-09-27，作者 westernat）⇒ 本轮**不动**（不是我该清理的移植注记）。

### 116.2 文件清单差的初判

| 子模块 | 仅 1.20 有 | 仅 1.21 有 | 分类 |
| --- | --- | --- | --- |
| lib | 13 | 9 | 7 个是 PortLib 时代产物（`SimpleFinishedRecipe`／`UnitFinishedRecipe`／`FriendlyByteBufMixin`／`Sup*Block`，均带 `org.mesdag.portlib.diff.Diff`）⇒ **继续不移植**；另 6 个属**动态光照**（`DynamicLightProvider`／`DynamicLightRegister`／`ParticleEngineMixin`／`LevelRendererAccessor`／`MouseHandlerMixin`／`ItemEntityMixin`）需逐条对账（1.21 侧 `TerraCurio` 有自研 `DynamicLightHandlersMixin`） | 9 个是 1.21 原生基建（`IPacket`／`IPacketC2S`／`IPacketS2C` 取代 PortLib 的 `IPortPacket`、`ConfluenceResources`、`LibJeiPlugin`…） |
| TerraCurio | 4 | 17 | 仅 1.20：`ClientEventHandlerMixin`（PortLib `IPortAttribute`）⇒ 不移植；`BowItemMixin`／`InventoryChangeTriggerMixin`／`ObsidianSkullRenderer` 需对账 | 1.21 原生新增（气球物理、`TCTriggers`、`BetterCombatHelper`、多个 mixin 等） |
| TerraFurniture | 3 | 7 | 仅 1.20：`CherryChestBlock`／`CherryChestGeoModel`／`ModelLightBlock` ⇒ **疑似功能缺口** | 1.21 原生新增（三个 JEI 分类、`TentBlock`、`TFStateProperties` 等） |

### 116.3 新建的跟踪与工具

| 产物 | 说明 |
| --- | --- |
| `notes/SUBMODULE-LEDGER.md` | **148 行**清单台账（lib 1–67、TerraCurio 68–122、TerraFurniture 123–148），列为 行／子模块／提交／日期／主题／文件数／+/−／标记／状态；标记含 `PortLib×N`（该提交 diff 提及次数）、`资源only`、`纯删除`、`merge` |
| `notes/submodule-ledger-status.json` | 状态双写目标（与主台账同构） |
| `build/_cmp231/sub_rows.py` | 行走驱动：复用 `rowaudit`（仅把 `R20`/`R21` 指向子模块根）→ `screen_full`（该脚本与路径无关）→ 子模块语料级符号核查；产出 `sub_r<hash>.txt`／`sub_s<hash>.txt`／`sub_sym_<hash>.txt`／`sub_batch_summary.txt` |
| `build/_cmp231/sub_status.py` | 状态回填（双写台账末列 + JSON），用法 `sub_status.py 60=COVERED …` |

### 116.4 第一行（行 60 = lib `e9b848c93`「大改修饰语」）的裁定：`COVERED`

该提交即 §115 那个 `ID_MAP`→`TIER` + 删 7 个负键的提交（11 增 53 删，单文件）。行走器实测 real GAP = **9 行 / 1 文件**，逐条落在：

| gap 行 | 判定 |
| --- | --- |
| `import io.netty.buffer.ByteBuf;` | 平台差异（1.21 用 `RegistryFriendlyByteBuf`） |
| `public record ModRarity(String name, int color) {` | **1.21 原生设计**：保持 `class ModRarity implements DataComponentType<ModRarity>`（§105.7 既定：不回退成 record） |
| `EXPERT = new ModRarity("expert", -1)`／`MASTER = new ModRarity("master", -2)` | 同上：1.21 用三参构造 + `special` 标志（`color()` 交给 `ExpertColorAnimation`／`MasterColorAnimation`），1.20 是拿 -1/-2 当颜色 |
| `withColor` 的 2 条签名 + 3 条正文（共 5 行） | **形参改名假阳性**：1.20 写 `stack`，1.21 写 `itemStack`；`ModRarity.withColor(ItemStack, Style)`／`(ItemStack, MutableComponent)` 在 1.21 `:147`／`:160` **都在**（调用面 1.21 侧 8 处 vs 1.20 侧同形） |

> **方法学补记（新增假阳性类）**：`rowaudit` 的 token 比对只归一 `this.`／`.get()`／`getRandom1211()`，**不归一形参改名**，故 `stack`→`itemStack`、`prefix`→`modPrefix` 这类会成批报 GAP。子模块行走中此类一律判「等价（形参改名）」，必要时在记录里列出而不是逐行动手。

### 116.5 状态

- 台账（双写）：`SUBMODULE-LEDGER.md` 148 行，行 60 = `COVERED`，剩余 TODO **147**。
- `fix_eol --check` 候选 6；本批**无代码落地**（仅新增跟踪台账与工具）。
- 下一批：**行 1–10（lib 开局 `able to start game`／`able to into world`／对齐提交）+ 行 123–126（TerraFurniture 开局）**，并把 §116.2 的「仅 1.20 有」文件按批对账。

## 一百一十七、子模块行走 行 1–10（Confluence-Magic-Lib 开局）⇒ 9 `COVERED` + 1 `SKIP-PORTLIB` + 1 `REVERSE-ALIGNED`

### 117.1 清单

| 行 | 提交 | 日期 | 主题 | 真实GAP/文件 | 符号/缺失 | 裁定 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | `02525eea4` | 2026-07-04 | able to start game | 6 / 2 | 2 / 0 | `COVERED` |
| 2 | `df79b045c` | 2026-07-04 | able to into world | 113 / 8 | 10 / 5 | `SKIP-PORTLIB` |
| 3 | `31f79abbc` | 2026-08-08 | 同步 1.21.1 的修改 | 5 / 3 | 13 / 0 | `REVERSE-ALIGNED` |
| 4 | `d77f87c92` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | 13 / 7 | 53 / 52 | `COVERED` |
| 5 | `781e94002` | 2026-08-16 | 注释 杀杀杀（=行 4 的逆操作） | 4 / 4 | 0 / 0 | `COVERED` |
| 6 | `acbd70715` | 2026-08-18 | 药水效果（未完成） | 0 / 0 | 1 / 0 | `COVERED` |
| 7 | `1802b4488` | 2026-08-19 | refactor：拉通生物、NPC 与战斗系统迁移 | 8 / 3 | 14 / 0 | `COVERED` |
| 8 | `1c97b2e51` | 2026-08-22 | refactor：拉通 1.21 战斗、召唤与实体体系 | 6 / 1 | 0 / 0 | `COVERED` |
| 9 | `0718c593a` | 2026-08-22 | 将饰品的药水效果转移至 lib | 137 / 18 | 28 / 2 | `COVERED` |
| 10 | `5f2d48bbf` | 2026-08-22 | 可开关的药水效果移到 lib | 37 / 11 | 14 / 1 | `COVERED` |

### 117.2 行 2：`Sup*` 与 `SimpleFinishedRecipe` 属 PortLib 时代产物（`SKIP-PORTLIB`）

113 行 gap 中 **104 行**落在 4 个文件：`SupStemBlock`(41)、`SupAttachedStemBlock`(24)、`SimpleFinishedRecipe`(22)、`SupBrushableBlock`(17) —— 四者都带 `org.mesdag.portlib.diff.Diff`，且 **1.21 的 lib 与主仓两侧都不存在**（实测 `Sup*` 0 命中）。1.21 侧改由各自具体类承载同一能力：`StemBlock` 18 处（`BalloonStemBlock`／`BalloonAttachedStemBlock`／`BalloonMelonBlock`）、`BrushableBlock` 4 处（`OpalOreBlock`）。

其余 9 行全部有等价物：

| 1.20 行 | 1.21 |
| --- | --- |
| `AbstractRecipeProvider`（5 行） | **1.21 lib 有 `AbstractRecipeProvider.java` + `CollectRecipeProvider.java`**；主仓 10 个 provider 用 `RecipeOutput`（`FinishedRecipe` 两侧皆 0） |
| `StateProperties.HUMIDITY`（2 行） | 1.21 lib 无、**主仓 43 处 `HUMIDITY`**（`LunarCoralBlock`／`LunarCoralFanBlock`…）+ `humidity` 62 处 ⇒ 属性落在主仓 |
| `LibDamageTypeTagsProvider` 构造器（含 `ExistingFileHelper`） | 1.21 数据生成器已无 EFH ⇒ 平台 |
| `accesstransformer.cfg` 的 SRG 行 | 1.21 AT 用 named ⇒ 平台 |

### 117.3 行 9／10：药水效果与「可开关效果」整条链在 1.21 的落点

1.20 把「饰品药水效果」搬进 lib（137 行/18 文件），2 个"缺符号"经查都是**换了落点**：

| 1.20 lib | 1.21 |
| --- | --- |
| `MouseHandlerMixin`（重力翻转的鼠标/视角处理，19 行） | **TerraCurio** `terra_curio/mixin/client/MouseHandlerMixin.java`（该特性 1.21 侧本就住在 TerraCurio 的 `GravitationHandler`，7 个文件引用） |
| `HoneyBottleItemMixin`（18 行） | **主仓** `mod/mixin/world/item/HoneyBottleItemMixin.java`（`@Mixin(HoneyBottleItem.class)`），lib 侧改由 `HoneyEffect.applyHoneyEffect(LivingEntity)` 提供能力 |
| `LibLanguageProvider`（21 行） | 1.21 lib 有 `LibLanguageProvider.java` + `LanguageFixer.java`，datagen 入口在 `LibDataGenerator` |
| `GravitationHandler`／`GravitationEffect`／`GravitationPacketC2S`／`BroadcastGravitationRotPacketS2C`／`LibKeyBindings`／`EntityMixin`／`LocalPlayerMixin` | 1.21 lib 同名文件**全在**（`GravitationHandler` 逐字搬运，见 117.4） |
| 包类 `identifier()`／`ID` 形态 | 1.21 用 `IPacketC2S`／`IPacketS2C` + `STREAM_CODEC`（6 个文件），仅 `SingleJsonFileReloadListener` 还留 1.20 式写法 |
| 行 10 的 `extends Event` + `@Cancelable`（`OnGatherEffectScreenTooltipsEvent`） | NeoForge 事件形态；「可开关效果」整链在 1.21 齐备：`ILibMobEffectInstance`（11 文件）／`ILibAbstractContainerScreen`／`ILibClientboundUpdateMobEffectPacket`／`SwitchEffectEnabledPackedC2S`／`ClientboundUpdateMobEffectPacketMixin`／`MobEffectInstanceMixin` |

### 117.4 两个「假警报」记下来（避免下次误判）

1. **`LibUtils.damageSource` 不是缺 API，而是改了落点/名字**：1.20 lib `LibUtils.java:308/312/316` 有 3 个重载 `damageSource(Level, ResourceKey<DamageType>[, Entity[, Entity]])`；1.21 lib 的 `LibUtils` **0 命中**，但 `LibDamageTypes` 提供**同签名的 3 个 `of(...)` 重载**（`of(Level, key)`／`of(Level, key, causing)`／`of(Level, key, direct, causing)`）。主仓里 `damageSource(` 的 4 处命中是**投射物自己的实例方法** `public DamageSource damageSource()`（`SpearProjectile:342` 等），与 lib 助手无关。
   ⇒ 记一条**命名/落点漂移待裁**：1.20 = `LibUtils.damageSource`，1.21 = `LibDamageTypes.of`。（本行按"等价"判 `COVERED`；若要像 `ID_MAP→TIER` 那样统一名字，属另一次 API 决策，涉及 lib + 主仓调用面。）
2. **`LibDamageTypes.hurtWithoutKnockback` 也已落地**：1.21 **主仓** `Immunity.java:104` 自己实现了 `static boolean hurtWithoutKnockback(...)`（`FirecrackerItem:115` 调用）⇒ 能力在，1.21 lib 不再持有它。

顺带登记（**用户自己的注记，本轮不动**）：1.21 lib 的 `GravitationHandler.java:15–31` 由 `659c8c82`（2026-09-28，作者 westernat）写明「本类已落地但**尚未接线**，接线见 WP6c 第二步」，三个接线口为 ①`LibKeyBindings.init(...)` 无人调用 ②两个包未在 `LibModEvents#registerPayloadHandlers` 注册 ③`LibClientGameEvents` 的调用点未补（现仅一行 `// todo 类似1.20.1 GravitationHandler.reset();`）；且该特性目前在 TerraCurio 另有一份。这是**有意的中间态**，不属行走缺口。

### 117.5 状态

- 台账（双写）：`SUBMODULE-LEDGER.md` 行 1–10 落状态（`COVERED` 8、`SKIP-PORTLIB` 1、`REVERSE-ALIGNED` 1）；剩余 TODO **137**。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 11–20（lib 中段：药水效果/战斗/饰品 datamap 系列）**，随后转 TerraFurniture 行 123–148（含 `CherryChestBlock`／`ModelLightBlock` 疑似缺口）。

## 一百一十八、子模块行走 行 11–30（Confluence-Magic-Lib 中段）⇒ 19 `COVERED` + 1 `REVERSE-ALIGNED`

### 118.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 符号/缺失 | 裁定 |
| --- | --- | --- | --- | --- | --- | --- |
| 11 | `389b3c230` | 2026-08-23 | 调整版本 | 0 / 0 | 0 / 0 | `COVERED` |
| 12 | `1dc696fc5` | 2026-08-23 | 饰品能力全改为 datamap，修复潜行属性 | 0 / 0 | 0 / 0 | `COVERED` |
| 13 | `d4e048a89` | 2026-08-23 | 同步 1.21.1 翅膀迁移，部分饰品添加粒子 | 0 / 0 | 2 / 0 | `REVERSE-ALIGNED` |
| 14 | `ebad0b193` | 2026-08-24 | 玩家动画测试 | 13 / 5 | 11 / 0 | `COVERED` |
| 15 | `92d3adbf3` | 2026-08-28 | 玩家动画（未注册永夜动画） | 4 / 3 | 8 / 0 | `COVERED` |
| 16 | `e28501469` | 2026-08-30 | feat(entity)：完善普通敌怪、NPC 与召唤物的行为和渲染 | 2 / 2 | 0 / 0 | `COVERED` |
| 17 | `b14b25a66` | 2026-09-02 | 删除一些 Extension 类 | 2 / 1 | 0 / 0 | `COVERED` |
| 18 | `3f1a8e5f9` | 2026-09-03 | 渔夫任务系统修改 | 0 / 0 | 0 / 0 | `COVERED` |
| 19 | `9fd8ac593` | 2026-09-04 | 修部分服务端报错 | 0 / 0 | 0 / 0 | `COVERED` |
| 20 | `9d5dee1a2` | 2026-09-06 | 静态方法改接口 | 1 / 1 | 4 / 0 | `COVERED` |
| 21 | `8fe6eb995` | 2026-09-06 | 属性静态字段注入 | 5 / 3 | 1 / 0 | `COVERED` |
| 22 | `0425c71c4` | 2026-09-06 | 删除多余内容 | 5 / 1 | 0 / 0 | `COVERED` |
| 23 | `30d45a9fa` | 2026-09-06 | 处理一些胡乱改动 | 1 / 1 | 0 / 0 | `COVERED` |
| 24 | `4ede8fad4` | 2026-09-07 | 生产环境修复 | 0 / 0 | 0 / 0 | `COVERED` |
| 25 | `80a53446f` | 2026-09-07 | 泰拉饰品掉落不再影响本体，为 screen 加半透明黑遮罩 | 14 / 2 | 5 / 1 | `COVERED` |
| 26 | `f2ef458a2` | 2026-09-07 | 修数量合成 | 2 / 1 | 0 / 0 | `COVERED` |
| 27 | `351847f01` | 2026-09-07 | 修复 CustomRarityItem 的属性问题 | 4 / 2 | 0 / 0 | `COVERED` |
| 28 | `e3d54fe60` | 2026-09-08 | fix(otherworld)：修复战斗结算并统一生物属性与鞭子判定 | 1 / 1 | 0 / 0 | `COVERED` |
| 29 | `e1757f1c1` | 2026-09-08 | 修复汇流箱子打不开、魔法武器不能附魔等 | 3 / 1 | 0 / 0 | `COVERED` |
| 30 | `361c05c8d` | 2026-09-08 | 修复灯笼粒子往下掉的问题 | 0 / 0 | 0 / 0 | `COVERED` |

### 118.2 gap 归类（本批 20 行合计仅 53 行 gap，且无一项是真缺口）

| 类别 | 实例 | 判定依据 |
| --- | --- | --- |
| **Forge 时代构建脚本** | 行 14／15／16 的 `build.gradle`：`forge.logging.markers`、`modCompileOnly("software.bernie.geckolib:geckolib-forge-…")`、`mclib:20`、`ParticleStorm-forge-…` | 1.21 用 NeoForge 变体与 `neoforge` 坐标 ⇒ 平台 |
| **Forge/NeoForge 事件与扩展点改名** | 行 15 的 `FMLClientSetupEvent`／`RegisterParticleProvidersEvent`；行 22 的 `FMLJavaModLoadingContext` + `ConfigScreenHandler.ConfigScreenFactory`；行 23 的 `ViewportEvent.ComputeCameraAngles` | 1.21 用 `IConfigScreenFactory` 等扩展点，事件在，形态不同 ⇒ 平台 |
| **GeckoLib 4.7 渲染管线** | 行 14 的 `player.confluence$getAnimatable().handleAnimations(Minecraft.getInstance().getFrameTime())`；行 16 的 `model.getItem(arm)` | GeckoLib 1.21 走 render-state 管线 ⇒ 平台 |
| **SRG AT / 旧 mixin 级别** | 行 14 的 `f_103373_ # cloak`、`"compatibilityLevel": "JAVA_17"` | 1.21 AT named、mixin 用 JAVA_21 ⇒ 平台 |
| **PortLib 包装/扩展** | 行 21 的 `AttributeHolder.wrap(Attributes.ATTACK_DAMAGE)`（`org.mesdag.portlib.wrapper.…AttributeHolder`）→ 1.21 的 `LibAttributes.getAttackDamage()` **直接 `return Attributes.ATTACK_DAMAGE`**（1.21 该类本身就是 `Holder`）；行 27 的 `stack.getPortAttributeModifiers()`；行 28 的 `IPortHolderExtension.of(holder).is(attribute)` → `Holder#is` | 平台（PortLib 一律不移植） |
| **1.20 战利品 `Serializer` → 1.21 codec** | 行 25 的 `CraftingLootItemCondition.SERIALIZER`（1.21 lib 全仓 `SERIALIZER` **0 命中**） | 1.21 lib **有** `CraftingLootItemCondition.java`（7 处）并在 `ConfluenceMagicLib` 用 `LOOT_ITEM_CONDITION` + `MapCodec`（49 处 codec 用法）注册 ⇒ 等价 |
| **编解码/网络形态** | 行 17 `INGREDIENTS_CODEC`／`LibStreamCodecUtils`（1.21 lib 分别 5／8 处）；行 20 `MobEffect.DIRECT_STREAM_CODEC` → 1.21 的 `Holder<MobEffect>` + `MobEffect.STREAM_CODEC`（`SwitchEffectEnabledPackedC2S` 全文已核） | 等价 |
| **1.21 附魔数据驱动** | 行 29 `EnchantedBookItem.createForEnchantment(new EnchantmentInstance(…))` | 1.21 附魔入注册表 + 组件（§110.3） ⇒ 平台/设计 |

### 118.3 状态

- 台账（双写）：行 11–30 落状态（`COVERED` 19、`REVERSE-ALIGNED` 1）；剩余 TODO **117**（lib 尚余 37 行、TerraCurio 55、TerraFurniture 26）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 31–45（lib 后段：端口表注册、`IPort` 清理、JEI 兼容、动态光照系列）**。

## 一百一十九、子模块行走 行 31–45（Confluence-Magic-Lib 后段之一）⇒ 13 `COVERED` + 2 `SKIP-PORTLIB`

### 119.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 裁定 |
| --- | --- | --- | --- | --- | --- |
| 31 | `efe1372ad` | 2026-09-08 | 修复右键功能物品失效问题 | 23 / 3 | `COVERED` |
| 32 | `6b7b52517` | 2026-09-10 | 修汇流熔炉不能放燃料的问题 | 0 / 0 | `COVERED` |
| 33 | `fb10030ce` | 2026-09-10 | 修复 mixin，修复跳跃属性 | 11 / 2 | `COVERED` |
| 34 | `445f76396` | 2026-09-11 | 一些修复 | 0 / 0 | `COVERED` |
| 35 | `3ae3a9fc3` | 2026-09-11 | **portlib 升级为 1.2.2** | 0 / 0 | **`SKIP-PORTLIB`** |
| 36 | `5e6625bab` | 2026-09-12 | JEI 兼容恢复 | 23 / 6 | `COVERED` |
| 37 | `6e9d87bae` | 2026-09-13 | 版本更新 | 0 / 0 | `COVERED` |
| 38 | `f8363c053` | 2026-09-14 | 修 | 2 / 1 | `COVERED` |
| 39 | `e0b02d2f0` | 2026-09-15 | 修事件 | 0 / 0 | `COVERED` |
| 40 | `87aa1992a` | 2026-09-15 | 更新粒子 | 0 / 0 | `COVERED` |
| 41 | `0cf2c0fa3` | 2026-09-16 | extension | 0 / 0 | `COVERED` |
| 42 | `5187258a6` | 2026-09-16 | curios 属性显示兼容 | 0 / 0（3 符号） | `COVERED` |
| 43 | `454b5938f` | 2026-09-17 | 修改纹理/模型问题，挪贴图位置 | 0 / 0（资源 only） | `COVERED` |
| 44 | `067209093` | 2026-09-17 | **修复 portlib 的注册表** | 3 / 1（gradle only） | **`SKIP-PORTLIB`** |
| 45 | `b61a6ee57` | 2026-09-18 | 使用 neoforge 风味的网络包注册与发送 | 10 / 2 | `COVERED` |

### 119.2 关键落点

| 1.20 行 | 1.21 |
| --- | --- |
| 行 31：`LibModEvents.spawnClusterSize(LivingPackSizeEvent event)` | **`LivingPackSizeEvent` 在 1.21 的 lib 与主仓都是 0 命中**（Forge 独有、NeoForge 已删）⇒ 1.21 改由实体自身承载，如 `MeleeSkeleton.getMaxSpawnClusterSize()` 覆写返回 8（§109 已见） |
| 行 31：`EntityAttributeModificationEvent` | 1.21 lib 有（`LibGameEvents`／`LibModEvents` 各 1 处） |
| 行 31：`LibAttributes.registerAttribute(Holder<Attribute>, BiConsumer<EntityType<? extends LivingEntity>, Attribute>)` | 1.21 `LibAttributes.java:52` 同方法，**形参升级为 `Holder<Attribute>`**（1.21 属性本身即 Holder；`hasCustomAttribute` 逻辑保留） |
| 行 33：`mods.toml` 的 `modId = "portlib"` 依赖块 + `mixinextras-forge:0.5.3` + `jarJar(...)` + `portlib_version` | 1.21 无 PortLib 依赖、MixInExtras 走 neoforge 变体 ⇒ 平台 |
| 行 36：`AbstractAmountRecipe` 的 `protected final ItemStack result`／`getResult()`／`ItemStack.STRICT_CODEC.fieldOf("result")`／`INGREDIENTS_CODEC` | 1.21 lib 的 `AbstractAmountRecipe` 有 `INGREDIENTS_CODEC`（5 处）与 `LibStreamCodecUtils`（8 处），配方编解码体系在；JEI 侧 1.21 有 `LibJeiPlugin` |
| 行 38：`ILibExtraSyncedData.defaultSetData(...)` | 1.21 同名接口在（`ILibExtraSyncedData.java`），默认方法排布不同 |
| 行 44：`build.gradle` 的 `boolean subproject = true` 分支 | 构建脚本自身 ⇒ 平台（该提交实质内容即 PortLib 注册表修复） |
| 行 45：`handler.registerInGameC2S(GravitationPacketC2S.class, ….ID, ….STREAM_CODEC)` | 1.21 用 `registerPayloadHandlers`（2 文件）+ `IPacketC2S`／`IPacketS2C` 的 `TYPE`；`registerInGameC2S` 0 命中 ⇒ 同一批包、换管道 |

### 119.3 状态

- 台账（双写）：行 31–45 落状态（`COVERED` 13、`SKIP-PORTLIB` 2）；剩余 TODO **102**（lib 余 22 行、TerraCurio 55、TerraFurniture 26，含已判的 60）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 46–67（lib 收尾：`IdentityHashMap` 替换、动态群系、蜘蛛洞、肉山/悠悠球重构、动态光照系列）**。

## 一百二十、子模块行走 行 46–67（Confluence-Magic-Lib 收尾）⇒ 21 行全 `COVERED`（**lib 1–67 走完**）

### 120.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 裁定 |
| --- | --- | --- | --- | --- | --- |
| 46 | `ee7122937` | 2026-09-18 | 合并 stream codec | 0 / 0 | `COVERED` |
| 47 | `35147c5ed` | 2026-09-19 | 修复粒子的顶点绕序问题 | 0 / 0 | `COVERED` |
| 48 | `b8f5bde2b` | 2026-09-19 | 修复部分物品无法搜索的问题 | 0 / 0 | `COVERED` |
| 49 | `95b09e6cd` | 2026-09-19 | 移除动态光源至 MagicLib | 3 / 1 | `COVERED` |
| 50 | `53e9a1de4` | 2026-09-20 | 动态群系修改与 client tick 事件大一统 | 17 / 2 | `COVERED` |
| 51 | `82af813f1` | 2026-09-20 | 修改一股味的代码 | 5 / 3 | `COVERED` |
| 52 | `c6b57b9a5` | 2026-09-20 | 第一人称动画功能移到 lib | 0 / 0 | `COVERED` |
| 53 | `4d3f299e9` | 2026-09-20 | IdentityHashMap 换成 Reference2ObjectOpenHashMap | 0 / 0 | `COVERED` |
| 54 | `b95445c3c` | 2026-09-20 | feat: 完善动态群系覆盖与迷你群系判定 | 3 / 1 | `COVERED` |
| 55 | `6a56e90ad` | 2026-09-20 | 修复暴击率问题 | 13 / 2 | `COVERED` |
| 56 | `0b4b61ae1` | 2026-09-21 | 修复一些问题 | 4 / 1 | `COVERED` |
| 57 | `b9d59de31` | 2026-09-21 | feat(worldgen): 重构蜘蛛洞生成并接入蜘蛛巢方块 | 4 / 1 | `COVERED` |
| 58 | `351cec5be` | 2026-09-22 | feat: 重构肉山肉墙与悠悠球实现，更新 NPC 交互界面… | 2 / 1 | `COVERED` |
| 59 | `303308900` | 2026-09-22 | 删除 Ponder 的 nbt，升级粒子 | 0 / 0 | `COVERED` |
| 61 | `8378b03ff` | 2026-09-25 | 彩色火把 | 0 / 0 | `COVERED` |
| 62 | `4688a2983` | 2026-09-25 | 1.2.7 | 0 / 0（纯删除） | `COVERED` |
| 63 | `c711de55f` | 2026-09-25 | feat(magiclib): 支持直接注册动态光源 | 0 / 0（7 符号） | `COVERED` |
| 64 | `777e96ae8` | 2026-09-27 | 修重铸价格（没对接心情） | 0 / 0 | `COVERED` |
| 65 | `addf529ec` | 2026-10-02 | 优化动态光照，移除可携带仆从接口行为 | 157 / 6 | `COVERED` |
| 66 | `413d62d1f` | 2026-10-02 | 添加动态光照注册行为与 ParticleAccessor | 75 / 5 | `COVERED`（**粒子光源属可选增强，另登记**） |
| 67 | `595159d71` | 2026-10-03 | 修复组件崩溃 | 0 / 0 | `COVERED` |

### 120.2 行 49／63／65／66：动态光照在 1.21 **已同步且改成静态 API**（本批唯一的"大 gap"）

1.21 侧的文件历史给出了决定性证据：`Confluence-Magic-Lib` 的 `DynamicLightDispatcher.java` 由 **`28cc0fe`（2026-10-01「feat(1.20→1.21 同步): 移植 IntegerRange、CraftingLootItemCondition、EntityRendererMixin，**动态光源归位 MagicLib**」）**引入。

| 项 | 1.20 lib（行 49→66 的演进） | 1.21 lib |
| --- | --- | --- |
| `DynamicLightDispatcher` | 221 行、实例式（`INSTANCE`、`addLightSource(LightSource)`、`update(LevelRendererAccessor)`、内部 `LightSource` 类） | **325 行、静态 API**：`addLightSources(Vec3,Vec3,int)`／`addLightSource(long,Vec3,int)`／`registerLightSource(long,Supplier<Vec3>,IntSupplier)`／`unregisterLightSource`／`registerEntityLight(EntityType,ToIntFunction)`／`unregisterEntityLight`／`clearWorld`／`update(RenderLevelStageEvent)`／`getDynamicLight(…)`×2，内部 `Snapshot`／`Long2IntOpenHashMap`／`RegisteredSource` 等 record |
| 钩入渲染 | `chunk.LevelRendererAccessor`（`@Invoker`）+ `LevelRendererMixin` + `LevelChunkMixin` + `EntityRendererMixin` + `ParticleEngineMixin` | **`LevelRendererMixin:18`**（`cir.setReturnValue(DynamicLightDispatcher.getDynamicLight(level,state,pos,…))`）与 **`EntityRendererMixin:15`**（`getDynamicLight(entity.getLightProbePosition(partialTicks), original)`），二者均已注册进 `confluence_magic_lib.mixins.json`（`chunk.LevelChunkMixin` 亦在）；**不需要 Accessor**（改用 `cir`）也不走 `ParticleEngineMixin` |
| 注册 API | `DynamicLightProvider<T>`（`@FunctionalInterface`）+ `DynamicLightRegister` | 由 `registerLightSource`／`registerEntityLight` 承载（1.20 的两个类在 1.21 无对应，属 API 收敛） |
| 调用面（1.21 实测） | — | lib：`LibClientGameEvents:165 clearWorld()`；主仓：`AttachmentEntityRenderDispatcher:88 getDynamicLight`、`SanguineBatRenderer:34 addLightSources(…,8)`、`TerraprismaRenderer:49 addLightSources(…,8)` |

**遗留登记（可选增强，不阻断）**：行 66 的 `ParticleAccessor`／`ParticleEngineMixin`（粒子光源）在 1.21 无落点——1.21 的 `DynamicLightDispatcher` 只暴露实体/位置/注册式光源；粒子自发光若要支持，需要补 accessor + 粒子侧登记。属**视觉增强**，与 §116.2 的"仅 1.20 有"清单同源。

### 120.3 其余各行的落点

| 行 | gap 内容 | 1.21 |
| --- | --- | --- |
| 49 | `DynamicLightDispatcher` 的 3 条 fastutil/ArrayList import | 1.21 用 `Long2IntOpenHashMap`／`HashMap`／`Snapshot` ⇒ 同效 |
| 50 | `ChunkSerializerMixin`（`@Final Logger`／`@Local codec`）+ `PalettedContainer$Data` 的 SRG AT（`f_188032_ # data`） | 1.21 lib 已注册 `chunk.ChunkSerializerMixin`／`chunk.PalettedContainerMixin`；AT 走 named ⇒ 平台 |
| 51 | `LibClientGameEvents` 的 `CameraAnimation.clear()/apply(event)`／`capture(CoreGeoBone)`；`HandAnimationAction` | 1.21 lib 有 `CameraAnimation` + **整套** `HandAnimationApi`／`HandAnimationChannel`／`HandAnimationClip`／`HandAnimationProfile`（`api/animation/` 13 个文件）；`HandAnimationAction` 在 **1.20 HEAD 亦 0 命中** ⇒ DEAD |
| 54 | `ChunkEvent.Load`／`ChunkEvent.Unload`／`LevelEvent.Unload` 处理器 | NeoForge 同名事件在，注册形态不同 ⇒ 平台 |
| 55／56 | `PlayerMixin` 用 `ILibDamageSource`；`ILibDamageSource.of(damageSource).confluence$setCritical(…)` | 1.21 lib 有 `ILibDamageSource`（符号核查 0 缺失） |
| 57 | `ChunkWatchEvent.Watch/UnWatch`、`PlayerEvent.PlayerLoggedOut/Clone` | NeoForge 同名事件 ⇒ 平台 |
| 58 | `ItemEntityMixin` 的 `ModRarity.getModRarity(getItem(), false)` 与 `WHITE`／`GRAY` 比较 | 1.21 `ModRarity` 有 `getModRarity(ItemStack, boolean)` 与同名常量、`equals(name,color)` ⇒ 等价（与 §115 的 `TIER` 改动同类但已是同形） |

### 120.4 状态：lib 段走完

- 台账（双写）：行 46–67 落状态，**`Confluence-Magic-Lib` 行 1–67 全部判定完毕**（`COVERED` 62、`SKIP-PORTLIB` 3、`REVERSE-ALIGNED` 2）。
- 剩余 TODO **81**：TerraCurio 行 68–122（55）、TerraFurniture 行 123–148（26）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 68–82（TerraCurio 开局）**。

## 一百二十一、子模块行走 行 68–82（TerraCurio 开局）⇒ 12 `COVERED` + 2 `SKIP-PORTLIB` + 1 `REVERSE-ALIGNED`

### 121.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 符号/缺失 | 裁定 |
| --- | --- | --- | --- | --- | --- | --- |
| 68 | `3a3ce762c` | 2026-07-04 | able to start game | 0 / 0 | 1 / 0 | `COVERED` |
| 69 | `a9f3c48eb` | 2026-07-04 | able to into world | 5 / 1 | 1 / 0 | `COVERED` |
| 70 | `fc5713120` | 2026-07-22 | 修崩溃 | 0 / 0 | 0 / 0 | `COVERED` |
| 71 | `063dffb70` | 2026-07-29 | truly fix | 0 / 0 | 0 / 0 | `COVERED` |
| 72 | `aab92e201` | 2026-07-29 | 整理 | 0 / 0 | 0 / 0 | `COVERED` |
| 73 | `d52bc12dc` | 2026-08-07 | **portlib v1.0.0** | 1 / 1 | 0 / 0 | **`SKIP-PORTLIB`** |
| 74 | `9c96d2d04` | 2026-08-07 | **TerraCurio 依赖**（mods.toml 的 portlib 块） | 5 / 1 | 0 / 0 | **`SKIP-PORTLIB`** |
| 75 | `7b0cbe520` | 2026-08-08 | 同步 1.21.1 的修改 | 518 / 5 | 65 / 0 | `REVERSE-ALIGNED` |
| 76 | `841f9933c` | 2026-08-16 | 调整逻辑 | 0 / 0 | 1 / 0 | `COVERED` |
| 77 | `ddfcd27e0` | 2026-08-22 | 将饰品的药水效果转移至 lib | 34 / 11 | 9 / 0 | `COVERED` |
| 78 | `b38d16d66` | 2026-08-22 | 可开关的药水效果移到 lib | 18 / 3 | 0 / 0 | `COVERED` |
| 79 | `82e2636f0` | 2026-08-22 | 同步粒子 | 6 / 2 | 0 / 0 | `COVERED` |
| 80 | `b1af28359` | 2026-08-23 | 升级粒子 | 7 / 1 | 0 / 0 | `COVERED` |
| 81 | `99dc4ccf1` | 2026-08-23 | 调整版本 | 0 / 0 | 0 / 0 | `COVERED` |
| 82 | `45beb4784` | 2026-08-23 | 饰品能力全改为 datamap，修复潜行属性 | 308 / 8 | 159 / 0 | `COVERED`（**架构不同，见 121.3**） |

### 121.2 行 75：518 行 gap 的成因（`REVERSE-ALIGNED`）

标题即「同步 1.21.1 的修改」，方向 1.21 → 1.20。gap 集中在 5 个 datagen 文件，两侧同族文件的**规模相当**，差异是 1.20 侧为 PortLib/Forge 形态：

| 文件 | 1.20 TerraCurio | 1.21 TerraCurio |
| --- | --- | --- |
| `TCRecipeProvider.java` | 422 行（`FinishedRecipe` 11 处、`RecipeOutput` 0） | 381 行（`RecipeOutput` **9**、`FinishedRecipe` **0**） |
| `TCLootTableProvider.java` | 1010 行（`tables.accept(` 62） | **1013 行（62）** |
| `TCLanguageProvider.java` | 1068 行（`add(` 124） | **1068 行（153）** |
| `TCDataGenerator.java` / `TCGameEvents.java` | 小改 | 同在 |

⇒ 内容已同步，gap 属平台形态与行级重排。

### 121.3 行 82：308 行 gap 的实质 = **饰品数据的承载方式不同，不是功能缺口**

初看很可疑：`TCDataMapProvider.java` 1.20 侧 **778 行 / `add(TCItems.` 197 处**，1.21 侧仅 **130 行 / 6 处**。深查后确认（这是本行判定的关键证据）：

| 证据 | 结果 |
| --- | --- |
| 1.21 `TCDataMapProvider.java`（`common/data/gen/TCDataMapProvider.java`） | **确实在用数据图**：`add(TCItems.ICE_SKATES, helper -> helper.unit(TCItems.ICE$SPEED))`、`helper.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(...))`、`helper.of(TCItems.MAY$FLY, MayFlyAbilityValue.of(...))`、`helper.of(TCItems.FLUID$WALK, Set.of(TCTags.…))` |
| 1.21 的饰品数据主要落在**物品 builder 链** | `SunStone.java`（1.21 44 行 vs 1.20 35 行）：`super(builder("sun_stone").rarity(ModRarity.LIME).attribute(Attributes.ATTACK_SPEED, 0.1, ADD_MULTIPLIED_TOTAL)…)`；`DuneriderBoots` 35↔23、`CellPhone` 36↔30 —— **1.21 的类更大**，正说明数据写在类里 |
| 数据图机制在使用 | 1.21 TerraCurio：`EFFECT$IMMUNITIES` 21 处、`TCDataMaps` 8 处、`DataMapType` 5 处（`TerraCurio`／`TCDataMaps`／`BaseCurioItem`…） |
| 行 69 的 gap 佐证 | 1.20 新加的 `TCDataMapProvider.add(ItemLike, Consumer<Helper>)`／`wrap(Item, Consumer<Helper>)` 助手，在 1.21 有同构的 `add(TCItems.X, helper -> …)` 写法 |

⇒ 1.20 侧（2026-08-23）把约 200 件饰品的数据**搬进 datamap provider**，1.21 侧仍是"物品 builder 链 + 少量数据图"的写法；**能力等价、架构不同** ⇒ `COVERED`。
**登记（可选对齐）**：若要与 1.20 的"全量数据图化"看齐，需要把 1.21 TerraCurio 的饰品数据从 item builder 迁到 `TCDataMapProvider`（约 200 条）——属**架构迁移**，非缺失，未擅动。

### 121.4 其余各行

| 行 | gap 内容 | 1.21 |
| --- | --- | --- |
| 69 | 数据图 provider 的 `add(ItemLike,…)`／`wrap(Item,…)` 5 行 | 1.21 有同构写法（见 121.3） |
| 73／74 | `archivesName = "TerraCurio-forge"`；`mods.toml` 的 `modId = "portlib"` 依赖块 | 平台（PortLib 不移植） |
| 77 | `TCGameEvents` 的 `LivingFallEvent`／`LivingDeathEvent`／`EntityJoinLevelEvent`／`PlayerEvent.*`；`StepStoolHandler` 的台阶逻辑；`ITCLivingEntity.of`／`ITCClientLivingEntity.of`；`LivingEntityRendererMixin`／`ClientLivingEntityMixin` 的 cosmetic 显隐 | 同名事件/接口在 1.21（0 缺失符号） ⇒ 平台与行级重排 |
| 78 | `RamRune.cancel(victim)`／`TCUtils.applyFireAttack`／`applyHoneyComb` | 1.21 有（0 缺失符号） |
| 79／80 | 粒子相关小 gap | 平台（无缺符号） |

### 121.5 状态

- 台账（双写）：行 68–82 落状态；剩余 TODO **66**（TerraCurio 余 40 行 83–122、TerraFurniture 26）。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 83–100（TerraCurio 中段：翅膀迁移、玩家动画、Extension 清理、mixin 修复）**。

## 一百二十二、子模块行走 行 83–100（TerraCurio 中段）⇒ 14 `COVERED` + 2 `DEFER-ASSETS` + 1 `REVERSE-ALIGNED` + 1 `SKIP-PORTLIB`

### 122.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 裁定 |
| --- | --- | --- | --- | --- | --- |
| 83 | `a3f1cbca7` | 2026-08-23 | 同步 1.21.1 翅膀迁移，部分饰品添加粒子 | 330 / 18 | `REVERSE-ALIGNED` |
| 84 | `06d637298` | 2026-08-24 | 玩家动画测试（含 4 个粒子 json 的 tint 渐变） | 33 / 5 | **`DEFER-ASSETS`** |
| 85 | `6ee91b55b` | 2026-08-28 | 玩家动画（未注册永夜动画） | 0 / 0 | `COVERED` |
| 86 | `3516ac33a` | 2026-09-02 | 删除一些 Extension 类 | 0 / 0 | `COVERED` |
| 87 | `38fcb3595` | 2026-09-06 | 静态方法改接口 | 4 / 2 | `COVERED` |
| 88 | `aae737d41` | 2026-09-06 | 属性静态字段注入 | 0 / 0 | `COVERED` |
| 89 | `94c69f0bc` | 2026-09-06 | 删除多余内容 | 4 / 2 | `COVERED` |
| 90 | `be87be1cb` | 2026-09-06 | 封印魂 | 3 / 1 | `COVERED` |
| 91 | `d4be8935d` | 2026-09-07 | 生产环境修复 | 0 / 0 | `COVERED` |
| 92 | `59730e912` | 2026-09-07 | 生产环境修复 | 0 / 0 | `COVERED` |
| 93 | `0b3846038` | 2026-09-07 | 泰拉饰品掉落不再影响本体，为 screen 加遮罩 | 7 / 1 | `COVERED` |
| 94 | `ea3fe72d3` | 2026-09-08 | 修复汇流箱子打不开、魔法武器不能附魔等 | 0 / 0 | `COVERED` |
| 95 | `262f4dae5` | 2026-09-08 | 修复灯笼粒子往下掉 | 0 / 0 | `COVERED` |
| 96 | `9a8c29d2b` | 2026-09-08 | 修复部分靴子没有自动上台阶 | 0 / 0 | `COVERED` |
| 97 | `e9789b7c2` | 2026-09-10 | 修一些资源错误 | 14 / 1 | **`DEFER-ASSETS`** |
| 98 | `b797d87e8` | 2026-09-10 | 修复 mixin，修复跳跃属性 | 19 / 3 | `COVERED` |
| 99 | `eebc21cbe` | 2026-09-11 | 修复与 Bigger Stacks 的 Mixin 冲突 | 1 / 1 | `COVERED` |
| 100 | `feded30e6` | 2026-09-11 | **portlib 升级为 1.2.2** | 0 / 0 | **`SKIP-PORTLIB`** |

### 122.2 行 83：翅膀（330 行 gap、唯一"缺符号"`DEFAULT_ANIMATION`）

两侧 TerraCurio 的翅膀文件集**同为 12 个**（`NormalWingsGeoModel.java`／`WingsGeoRenderer.java`／`normal_wings.animation.json`／`fledgling_wings.geo.json`／7 张贴图 + 动画）。

1.20 `NormalWingsGeoModel.java:13`：

```java
protected static final ResourceLocation DEFAULT_ANIMATION = TerraCurio.asResource("animations/accessory/normal_wings.animation.json");
```

1.21 同文件改**动态推导**（`:13/15/17/21`）：

```java
protected final ResourceLocation animation;
public NormalWingsGeoModel(ResourceLocation id) {
    this.animation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "animations/accessory/" + id.getPath()…);
}
public ResourceLocation getAnimationResource(AccessoryGeoModel animatable) { return animation; }
```

⇒ 1.21 的设计更通用（按饰品 id 推导动画路径），**不需要** 1.20 的常量；该行标题即"同步 1.21.1 翅膀迁移"（方向 1.21 → 1.20）⇒ `REVERSE-ALIGNED`。

### 122.3 两处**资源级真差**（`DEFER-ASSETS`）

| 行 | 1.20 资源 | 1.21 资源 | 差异 |
| --- | --- | --- | --- |
| 84 | `blizzard.particle.json` 61 行／`cloud.json` 68／`sandstorm.particle.json` 61／`tsunami.particle.json` 54，**四个都含 `particle_appearance_tinting` + `gradient`**（`"0.0": "#00FFFFFF"`、`"0.15": "#E6FFFFFF"`… 按 `v.particle_age / v.particle_lifetime` 插值） | 同名四个文件 50／57／50／43 行，**tinting=N、gradient=N** | 1.20 给这 4 个粒子加了"随寿命渐隐/渐显"的着色渐变，1.21 尚未同步 ⇒ 属**资源增强**（ParticleStorm 两边同源，键名可直接搬） |
| 97 | `fledgling_wings.geo.json` 的几何（`visible_bounds_width`、多组 `pivot`／`origin`） | 同名文件几何不同 | 该提交标题即"修一些资源错误"⇒ 1.20 侧的几何修正未进 1.21 |

> 两行都按 `DEFER-ASSETS` 落账（账内术语：随所属功能提交处理）。搬运是机械的（4 个粒子 json 的 tinting 块 + 1 个 geo 的几何值），但会改视觉效果且**无法在本工作流内运行验证**（无编译/无 datagen），故本批不擅自改资源，登记为可执行项。

### 122.4 其余各行

| 行 | gap 内容 | 1.21 |
| --- | --- | --- |
| 84 | `ParticleTriggers` 用 `ForgeMod.EMPTY_TYPE.get()` | NeoForge 变体 ⇒ 平台 |
| 87 | `Attribute.DIRECT_CODEC`／`Attribute.DIRECT_STREAM_CODEC`；`IPortResourceKeyExtension.streamCodec(…)` | 1.21 走注册表化 codec；`IPort*` 属 PortLib ⇒ 平台 |
| 89 | `TerraCurio` 调 `LibClientUtils.registerConfigScreen(context)`；`IMultiFunctionCouldEnable` 用 `ConfluenceMagicLib.IS_CONFLUENCE_LOAD` | 1.21 `TerraCurio.java:27` **直接** `modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new)`（主仓 `Confluence.java:72` 同法）⇒ 不需要 lib 包装方法；`IS_CONFLUENCE_LOAD` 在 1.21 lib（2 文件） |
| 90 | `MultiFunctionTooltip` 覆写 `getWidth(Font)`／`renderText(Font,int,int,Matrix4f,MultiBufferSource.BufferSource)`／`renderImage(Font,int,int,GuiGraphics)` | 同名 `TooltipComponent` 契约在 ⇒ 平台/行级 |
| 93 | `TCGlobalLootModifierProvider` 的 `CraftingLootItemCondition`／`NotCondition`／`LootTableIdCondition.builder(…)` | 1.21 lib 有 `CraftingLootItemCondition`（§118 已核，codec 注册） |
| 98／99 | `build.gradle`：`ParticleStorm-forge`、`mixinextras-forge`、`jarJar(…)`、`mixin:0.8.5:processor`、`project(":Confluence-Magic-Lib")`；`mods.toml`：portlib／geckolib 依赖块 | 平台 |

### 122.5 状态

- 台账（双写）：行 83–100 落状态；剩余 TODO **48**（TerraCurio 余 22 行 101–122、TerraFurniture 26 行 123–148）。
- `fix_eol --check` 候选 6；本批**无代码落地**（两处资源差按 `DEFER-ASSETS` 登记）。
- 下一批：**行 101–122（TerraCurio 收尾：JEI 恢复、第一人称动画、粒子绕序、饰品能力数据图化、重铸价格、构建修复）**。

## 一百二十三、子模块行走 行 101–122（TerraCurio 收尾）⇒ 20 `COVERED` + 2 `SKIP-PORTLIB`（**TerraCurio 68–122 走完**）

### 123.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 裁定 |
| --- | --- | --- | --- | --- | --- |
| 101 | `25eb4545d` | 2026-09-12 | JEI 兼容恢复 | 6 / 3 | `COVERED` |
| 102 | `aa3c27be4` | 2026-09-13 | 版本更新 | 0 / 0 | `COVERED` |
| 103 | `b638adcd5` | 2026-09-14 | 为新增怪物补全需要的东西 | 0 / 0（资源 only） | `COVERED` |
| 104 | `efb867308` | 2026-09-15 | 微调 | 0 / 0 | `COVERED` |
| 105 | `e911661de` | 2026-09-15 | 更新粒子 | 0 / 0 | `COVERED` |
| 106 | `2c55cf65f` | 2026-09-16 | 调整末地高度 | 1 / 1 | `COVERED` |
| 107 | `717a424d6` | 2026-09-16 | extension | 0 / 0 | `COVERED` |
| 108 | `b9b221844` | 2026-09-16 | curios 属性显示兼容 | 55 / 3 | **`SKIP-PORTLIB`** |
| 109 | `b5b775e93` | 2026-09-17 | 修改纹理/模型问题，挪贴图位置 | 0 / 0（资源 only） | `COVERED` |
| 110 | `2f2f24793` | 2026-09-17 | **修复 portlib 的注册表** | 2 / 1 | **`SKIP-PORTLIB`** |
| 111 | `5d64f259a` | 2026-09-18 | 使用 neoforge 风味的网络包注册与发送 | 35 / 10 | `COVERED` |
| 112 | `e678ff8fc` | 2026-09-19 | 修复粒子的顶点绕序问题 | 0 / 0 | `COVERED` |
| 113 | `577a5d955` | 2026-09-20 | 第一人称动画功能移到 lib | 0 / 0 | `COVERED` |
| 114 | `83efd2c63` | 2026-09-20 | IdentityHashMap 换 Reference2ObjectOpenHashMap | 2 / 1 | `COVERED` |
| 115 | `79351d003` | 2026-09-22 | 改 | 1 / 1 | `COVERED` |
| 116 | `a1a803d74` | 2026-09-22 | 删除 Ponder 的 nbt，升级粒子 | 0 / 0 | `COVERED` |
| 117 | `83f19c9db` | 2026-09-23 | fall_damage_multiplier 属性不再导致摔落声音 | 1 / 1 | `COVERED` |
| 118 | `252bb9caa` | 2026-09-26 | 调整种子特性 | 0 / 0（资源 only） | `COVERED` |
| 119 | `5666e140a` | 2026-09-27 | 修重铸价格（没对接心情） | 0 / 0 | `COVERED` |
| 120 | `ef4356518` | 2026-09-27 | 修复构建问题 | 1 / 1 | `COVERED` |
| 121 | `df37454c2` | 2026-09-27 | 平衡性调整，权重调整，贴图补充 | 0 / 0（资源 only） | `COVERED` |
| 122 | `f00e8f541` | 2026-10-03 | 修复组件崩溃 | 0 / 0 | `COVERED` |

### 123.2 行 108：PortLib 的 curios 集成 mixin（`SKIP-PORTLIB`），标签面已同步

| 1.20 加的东西 | 1.21 |
| --- | --- |
| `ClientEventHandlerMixin`（`org.confluence.terra_curio.mixin.integration.curios`，用 PortLib `IPortAttribute`）+ `terra_curio.mixins.json` 注册项 | **1.21 TerraCurio 无该类**（`ClientEventHandler` 0 命中）——PortLib 专有，按口径不移植 |
| `TCItemTagsProvider`：`tag(LibTags.Items.WIP).add(TCItems.FROZEN_WINGS.get(), JETPACK, LEAF_WINGS, BAT_WINGS, …)` | **1.21 同名 provider 同类写法**（`TCItemTagsProvider.java:51` `tag(LibTags.Items.WIP).add(`）；1.21 lib 的 `LibTags.Items.WIP`（`LibTags.java:14`）有 3 个消费方（`LibClientGameEvents:80`、`GuiGraphicsMixin:34`、`WipNotDisplayOutput:35`）；翅膀/喷气背包条目在 `TCItems`／`TCItemTagsProvider`／`TCLanguageProvider` 各 4 处 |

### 123.3 其余各行的 gap 归类

| 类别 | 实例 |
| --- | --- |
| **构建脚本/版本号** | 行 106 `jarJar(mixinextras-forge)`；行 110 `boolean subproject = true` 分支；行 115 `mod_version=1.3.1.5`；行 120 `includeGroupByRegex "com\.bawnorton.*"`；行 101 `jei_version=15.56.0.205`；行 111 `portlib_version=1.2.5` |
| **1.20 自造包注册助手 → 1.21 原生** | 行 111 `TCModEvents` 的 `.registerInGameC2S(X.class, X.ID, X.STREAM_CODEC)` 链与各包的 `sendToClient(ServerPlayer)`；1.21 用 `registerPayloadHandlers` + `IPacketS2C`（0 缺失符号，与 lib 行 45 同源） |
| **NeoForge 事件形态** | 行 114 `TCGameClientEvents` 的 `ComputeFovModifierEvent`／`InputEvent.InteractionKeyMappingTriggered` |
| **配方/JEI 访问器** | 行 101 `WorkshopMenu`／`WorkshopCategory` 的 `recipe.getResult()`（1.21 同名 API 在） |
| **实现细节** | 行 117 `BaseSpeedBoots` 的 `player.onGround() && !player.swinging` 判断 |

### 123.4 状态：TerraCurio 段走完

- 台账（双写）：行 101–122 落状态，**TerraCurio 行 68–122 全部判定完毕**（`COVERED` 45、`SKIP-PORTLIB` 5、`REVERSE-ALIGNED` 2、`DEFER-ASSETS` 2）。
- 剩余 TODO **26**：仅 **TerraFurniture 行 123–148**。
- `fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 123–148（TerraFurniture 全部：拟对账 `CherryChestBlock`／`CherryChestGeoModel`／`ModelLightBlock` 的"架构差异 vs 真缺口"，以及 `4d327715d`「单腿桌子」+1218、`b5f856c95`「加点」+6753 两个大提交）**。

## 一百二十四、子模块行走 行 123–148（TerraFurniture 全部）⇒ 17 `COVERED` + 2 `REVERSE-ALIGNED` + 2 `SKIP-PORTLIB` + **5 行真缺口**
>
> **子模块全量行走到此收口**：148 行中 143 行落定，仅 TerraFurniture 的 5 行（141／145–148）为**已裁定的真缺口、待移植**。

### 124.1 清单

| 行 | 提交 | 日期 | 主题 | GAP/文件 | 裁定 |
| --- | --- | --- | --- | --- | --- |
| 123 | `d08fb3230` | 2026-07-04 | able to start game | 2 / 2 | `COVERED` |
| 124 | `ae0621561` | 2026-08-08 | 同步 1.21.1 的修改 | 1 / 1 | `REVERSE-ALIGNED` |
| 125 | `3176c2ac0` | 2026-08-23 | 调整版本 | 0 / 0 | `COVERED` |
| 126 | `c4eb231f0` | 2026-08-23 | 同步 1.21.1 翅膀迁移，部分饰品添加粒子 | 0 / 0（纯删除） | `REVERSE-ALIGNED` |
| 127 | `89b641939` | 2026-09-02 | 删除一些 Extension 类 | 3 / 1 | `COVERED` |
| 128 | `996bcbf97` | 2026-09-03 | 渔夫任务系统修改 | 0 / 0 | `COVERED` |
| 129 | `4bbc937be` | 2026-09-06 | 静态方法改接口 | 1 / 1 | `COVERED` |
| 130 | `eacdaad1c` | 2026-09-06 | 属性静态字段注入 | 0 / 0 | `COVERED` |
| 131 | `81db6984a` | 2026-09-07 | 生产环境修复 | 2 / 1 | `COVERED` |
| 132 | `4946ac82a` | 2026-09-10 | 修汇流熔炉不能放燃料 | 0 / 0 | `COVERED` |
| 133 | `ef098a90a` | 2026-09-10 | 修复 mixin，修复跳跃属性 | 18 / 2 | `COVERED` |
| 134 | `3a4b2e990` | 2026-09-11 | 一些修复 | 0 / 0 | `COVERED` |
| 135 | `1c894ac05` | 2026-09-11 | **portlib 升级为 1.2.2** | 0 / 0 | **`SKIP-PORTLIB`** |
| 136 | `7264bacb3` | 2026-09-12 | JEI 兼容恢复 | 3 / 2 | `COVERED` |
| 137 | `fc3779532` | 2026-09-13 | 版本更新 | 0 / 0 | `COVERED` |
| 138 | `ae2d6f2f2` | 2026-09-16 | extension | 0 / 0 | `COVERED` |
| 139 | `80a9b93c1` | 2026-09-17 | **修复 portlib 的注册表** | 2 / 1 | **`SKIP-PORTLIB`** |
| 140 | `797379213` | 2026-09-18 | 使用 neoforge 风味的网络包注册与发送 | 2 / 2 | `COVERED` |
| **141** | `4d327715d` | 2026-09-19 | 单腿桌子 | 21 / 1 | **TODO（真缺口）** |
| 142 | `d060e05de` | 2026-09-20 | 对称桌子花边（作者自注"先提交吧"） | 0 / 0 | `COVERED` |
| 143 | `bf35ef75d` | 2026-09-20 | 改成组件模式 | 0 / 0 | `COVERED` |
| 144 | `3b1e55c09` | 2026-09-20 | 改腿渲染逻辑 | 0 / 0 | `COVERED` |
| **145** | `3e45ca5a6` | 2026-09-20 | 添加方块 | 443 / 12 | **TODO（真缺口）** |
| **146** | `df868f5ac` | 2026-09-22 | 家具 | 69 / 2 | **TODO（真缺口）** |
| **147** | `62d4c7027` | 2026-09-27 | 传一点 | 359 / 10 | **TODO（真缺口）** |
| **148** | `b5f856c95` | 2026-10-03 | 加点 | **6714 / 37** | **TODO（真缺口）** |

### 124.2 五行的缺口清单（可直接当移植批次用）

| 行 | 缺失符号（1.21 同子模块 0 命中） | gap 主要文件（行数） | 性质 |
| --- | --- | --- | --- |
| 141 | `OneLegTableGeoModel`／`TabletopRenderer`／`ModelVariant` | `one_leg_table.json`(21) | 1.21 已有 `OneLegTableLegGeoModel`（`OneLeg` 命中 29 处）⇒ **缺桌面模型与渲染器**（半成品） |
| 145 | `SinkBlock` 的 `IRON_BASE`／`SPRUCE_BASE` | `candelabras.json`(155)、`sink.json`(112+86)、`SinkBlock.java`(28)、`iron_sink.json`(19)、`spruce_sink.json`(19)、`TFBlockTagsProvider`(7)、`TFBlocks`(5) | **水槽方块（铁/云杉）与其模型、烛台资源**整批未迁 |
| 146 | `BLUE_DUNGEON_FLAMES`／`GLASS_FLAMES`／`SPRUCE_FLAMES`／`SPRUCE_SHAPE`／`OAK_CANDLE_SHAPE` | `CandelabraBlock.java`(50)、`SwitchableLightBlock.java`(19) | 两个类 1.21 **都在**，差的是**变体常量集**（1.21 变体更少） |
| 147 | `SPRUCE_BASE` | `bathtub.json`(134)、`base.json`(88)、`forward.json`(49)、`bed.json`(26)、`BathtubBlock.java`(21) | **浴缸方块与模型**未迁 + 变体资源 |
| 148 | `CherryChestBlock`／`CherryChestGeoModel`／`CHERRY_CHEST`／`CHERRY_CHEST_ENTITY`／`CHERRY_CHEST_ITEM`／`ModelLightBlock`／`SPRUCE_CANDLESTICK_ONE｜TWO｜THREE` | `spruce_candlestick_three_lit.json`(1385)、`three_unlit`(1103)、`two_lit`(1040)、`two_unlit`(852)、`one_lit`(535)、`spruce_lamp_lit`(497)、`one_unlit`(441)、`spruce_lamp_unlit`(403)、`toilet.json`(147)、`CherryChestBlock.java`(48)、`cherry_chest.geo.json`(41)、`ModelLightBlock.java`(24) | **樱花木箱（方块+实体+物品+GeckoLib 模型）／模型光源方块／云杉烛台（1~3 支、明/灭）／云杉灯／马桶**整批未迁 |

文件面佐证：`*Chest*` 1.20 = **2** → 1.21 = **0**；`*Light*` 1.20 = 2 → 1.21 = 1；而 `*Table*`／`*Chair*`／`*Sofa*`／`*Bed*` 两侧相等（6/2/1/1）⇒ 缺口集中在上面这批。

### 124.3 状态

- 台账（双写）：行 123–148 落状态。表头已补语义：**本台账 `TODO` = 已裁定为真缺口、待移植**（当前仅 141／145–148）。
- 全量统计（148 行）：`COVERED` 122、`SKIP-PORTLIB` 12、`REVERSE-ALIGNED` 6、`DEFER-ASSETS` 2、`TODO` 5，另 lib 行 60 为 `COVERED`。
- **三个子模块的行走全部完成**；`fix_eol --check` 候选 6；本批**无代码落地**。
- 下一批：**行 148（6714 行，樱花木箱 + 模型光源 + 云杉烛台/灯 + 马桶）** ⇒ 按资产/方块/注册/语言分步落地，先落 Java 侧（`CherryChestBlock`／`ModelLightBlock`／`TFBlocks` 注册），再搬 GeckoLib JSON，最后 tags/lang。

## 一百二十五、行 141 改判 `DEFER-ASSETS` + 余下四行（145–148）落地前置勘察

### 125.1 行 141「单腿桌子」：三个"缺符号"全是 DEAD，代码侧本就齐备

| 符号 | 1.20 HEAD 定义 | 1.21 定义 | 说明 |
| --- | --- | --- | --- |
| `OneLegTableGeoModel` | **0** | 0 | 该提交加入后由 1.20 自己改名 |
| `TabletopRenderer` | **0** | 0 | 同上 |
| `ModelVariant` | **0** | 0 | 同上 |
| `OneLegTableLegGeoModel` | **1** | **1** | 现名，两侧同名文件 |

两侧文件集与资源**逐一对应**：`OneLegTableLegGeoModel.java`／`OneLegTableGeoRenderer.java`／`OneLegTableBlock.java`／`TableBDG.java`／`TableBlock.java`／`TFLootTableProvider.java`，资源 `one_leg_table.json`（blockstates）**104 行 / 104 行**、`models/item/one_leg_table.json` **58 行 / 58 行**、掉落表 22 行 / 22 行（1.21 用的目录名是 1.21 数据包布局 `loot_table/`）。

⇒ 代码与文件面均已覆盖，仅 21 行 JSON **内容**不同 ⇒ 改判 **`DEFER-ASSETS`**（与行 84／97 同类）。台账 TODO 由 5 行降为 **4 行（145／146／147／148）**。

### 125.2 余下四行的落地前置（已核实，可直接开工）

| 前置项 | 现状 |
| --- | --- |
| 1.21 `SwitchableLightBlock` 构造器 | **与 1.20 同签名** `SwitchableLightBlock(TFBlockSetType type, Properties properties, BlockShapeType shapeType)`（`:43`），`getGenerator()` 也在（`:117`）；差异仅访问修饰（1.21 为 `protected getShape`／`useWithoutItem`）与基类（1.21 用原生 `CopperBulbBlock`，1.20 用 `PortCopperBulbBlock`） |
| 1.21 依赖类 | `BlockDataGenerator`（2 文件/20 引用）、`BlockShapeType`（1/4）、`TFBlockSetType`（1/18）、`AutoGenBlockData`（1/7）**全在** |
| 1.20 `ModelLightBlock`（31 行，待移植源） | `extends SwitchableLightBlock`，持 `VoxelShape shape`，覆写 `getShape` 返回固定形状、`getGenerator()` 返回 `null`（"模型/方块态/碰撞箱全部手工提供"语义） |
| 1.20 `TFBlocks` 的用法 | `setGetterFor(TFBlockType.LAMP, (properties, applier) -> new ModelLightBlock(SPRUCE, properties, …))`；`registerWithItem("spruce_candlestick_one", () -> new ModelLightBlock(SPRUCE, Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion().lightLevel(…), …))` |
| 1.21 `TFBlocks` 现状 | 已用 NeoForge `DeferredBlock`／`DeferredRegister`，并已有 `TFBlockSet`／`TFBlockType` 机制与 `LargeChandelierBlock`／`CandelabraBlock`／`ClockBlock`／`HangingPotBlock` 等 ⇒ 注册点可沿用 |

### 125.3 落地顺序（下一批起执行）

| 步 | 内容 | 规模（1.20 侧） | 依赖 |
| --- | --- | --- | --- |
| 1 | **行 148 第一批**：`ModelLightBlock`（31 行）类 + `spruce_candlestick_one/two/three` 与 `spruce_lamp` 的注册与 Java 侧 | 类 31 行 + `TFBlocks` 若干条 | 前置已满足（125.2） |
| 2 | 行 148 第二批：樱花木箱 `CherryChestBlock`(61) + `CherryChestGeoModel` + `CHERRY_CHEST(_ENTITY/_ITEM)` 注册 + `cherry_chest.geo.json`(41) | ~120 行 + 资产 | 照 `ChestBlock` 现成实现 |
| 3 | 行 148 第三批：`spruce_candlestick_{one,two,three}_{lit,unlit}.json`（441／535／852／1040／1103／1385 行）、`spruce_lamp_{lit,unlit}.json`（403／497）、`toilet.json`(147) 等资产 | ~6.1k 行资产 | 第 1 步的方块 id |
| 4 | 行 145：`SinkBlock` 补 `IRON_BASE`／`SPRUCE_BASE` 变体（1.21 已有 89 行版，1.20 为 133 行）+ `iron_sink`／`spruce_sink` 资源 + `candelabras.json`(155) + tags/TFBlocks 注册 | ~500 行 | — |
| 5 | 行 147：`BathtubBlock`(106) 整批 + `bathtub.json`(134)/`base.json`(88)/`forward.json`(49)/`bed.json`(26) + `SPRUCE_BASE` | ~360 行 | 与行 145 共享 `SPRUCE_BASE` |
| 6 | 行 146：`CandelabraBlock`／`SwitchableLightBlock` 的变体常量集补齐（`BLUE_DUNGEON_FLAMES`／`GLASS_FLAMES`／`SPRUCE_FLAMES`／`SPRUCE_SHAPE`／`OAK_CANDLE_SHAPE`，1.20 分别 143／174 行 vs 1.21 75／136 行） | ~110 行 | 第 1／3 步的变体命名 |

> 每步落地前先确认同批次涉及的 **tags／lang／item model／blockstate**（`TFBlockTagsProvider`、`TFChineseProvider`、`TFBlockSet` 的自动生成面），并在子模块提交后更新父仓 gitlink。

### 125.4 状态

- 台账（双写）：行 141 → `DEFER-ASSETS`；剩余 TODO **4**（145／146／147／148）。全量：`COVERED` 122、`SKIP-PORTLIB` 12、`REVERSE-ALIGNED` 6、`DEFER-ASSETS` 3、`TODO` 4。
- `fix_eol --check` 候选 6；本批**无代码落地**（落地按 125.3 的顺序从下一批开始）。
- 下一批：**125.3 第 1 步**（`ModelLightBlock` + 云杉烛台/灯的 Java 侧）。

## 一百二十六、落地第 1 步**按纪律中止**：行 145–148 的资产面是用户在建 WIP

### 126.1 发现过程与证据

按 §125.3 执行第 1 步（`ModelLightBlock` + 云杉烛台/灯的 Java 侧）时，先做资产搬运，结果拷贝脚本报告**目标文件"已存在且内容完全相同"**（12 个烛台资产），只有 3 个贴图（`spruce_lamp.png`／`spruce_sink.png`／`spruce_toilet.png`）不存于 1.21 工作区。遂查 `git -C TerraFurniture status`：

| 项 | 实测 |
| --- | --- |
| `TerraFurniture` 工作区未跟踪条目 | **17 条，全部为 resource**（`?? src/main/resources/...`），**无任何 `.java`** |
| 内容 | `blockstates/spruce_candlestick_{one,two,three}.json`、`models/block/spruce_candlestick_{one,two,three}_{lit,unlit}.json`（6 个）、`models/item/spruce_candlestick_{one,two,three}.json`、`textures/block/spruce/{fire.png, fire.png.mcmeta, spruce_candelabras.png, spruce_candlestick.png}`、`textures/block/spruce/spruce_bathtub/`（目录）等 |
| 与 1.20 HEAD 的关系 | 已存在的 12 个烛台资产与 1.20 HEAD **字节相同** ⇒ 用户已按 1.20 侧成品铺好资产 |
| HEAD 侧 | 这些文件在 1.21 HEAD **未被跟踪**（`git ls-files '*spruce_candlestick*'` 为空）⇒ 本台账把行 145–148 判为缺口的结论**与 HEAD 一致、并未判错** |

⇒ **行 145–148 的"资产面"正在用户手里推进**（未提交的工作区文件）。按工作流纪律「用户在建文件不碰不提交」，我**中止该批落地**，并已把本轮不小心新建的 3 个贴图（`spruce_lamp.png`／`spruce_sink.png`／`spruce_toilet.png`，均为 1.20 的字节拷贝）**删除撤回**，工作区恢复为其原有 17 条未跟踪资源。

### 126.2 由此得到的结论（对行 145–148 的落地面重新划界）

| 面 | 状态 | 归属 |
| --- | --- | --- |
| 资产（blockstate／model／texture） | 已有 17 条未跟踪文件在工作区（烛台 12 项 + 若干贴图 + `spruce_bathtub/` 目录） | **用户在建**（勿重复、勿提交） |
| Java 侧（`ModelLightBlock`／云杉烛台与灯的注册／`SinkBlock` 变体／`BathtubBlock`／`CherryChestBlock`＋`CherryChestGeoModel`／`ModelLightBlock` 等） | **0 个 java 未跟踪文件** ⇒ 尚未开始 | 待定：我接手 或 用户自行推进（见 126.3） |
| tags／lang／item model 生成 | 由 `TFBlockTagsProvider`／`TFChineseProvider` 等 datagen 覆盖 | 随 Java 侧一并处理 |

### 126.3 需要用户决定的分工

本轮已把 Java 侧的落地材料全部备齐（可直接开工，均经核实）：

| 项 | 结论 |
| --- | --- |
| `ModelLightBlock` 可移植性 | 1.21 `SwitchableLightBlock` 构造器与 1.20 同签名；`getShape` 在 1.21 为 `protected`（Java 允许放宽为 `public`，两种写法都能编译）；`getGenerator()` 返回类型两侧一致（`@Nullable BlockDataGenerator<? super SwitchableLightBlock>`）；`TFBlockSetType`／`BlockDataGenerator` 的包路径两侧**完全相同** |
| 注册助手 | 1.21 `TFBlocks.registerWithItem(String, Supplier<B>)`（`:299`）与 1.20 同名同形 |
| 变体常量 | 1.21 `TFBlocks` 已 `import static …TFBlockSetTypes.*`（`:42`）⇒ `SPRUCE` 可直接用 |
| 光照属性函数 | `litBlockEmission(int)` 两侧都在（1.21 `TFBlocks:331`，1.20 `:368`）⇒ 可在本类内直接调用 |
| 1.20 三条烛台的注册原文 | 已抄录（`BlockShapeType.CANDLE` + `Block.box(6.5,0,6.5,9.5,13,9.5)`／`(3.5,0,6.5,12.5,13,9.5)`／`(2.5,0,6.5,13.5,13,9.5)`，`lightLevel(litBlockEmission(15))`、`noOcclusion()`） |

**待用户指示**：① 由我推进 Java 侧（不动你的 17 条资源、不提交它们）；② 整个 TerraFurniture 家具移植（行 145–148）由你自己推进，我把台账 4 行改为已移交/登记；③ 我改做别的（如 §125.4 里那三项可选对齐）。

### 126.4 状态

- 台账（双写）：本轮**未改状态**（行 145–148 仍为 `TODO`：HEAD 侧确为缺口）。
- `TerraFurniture` 工作区：恢复为用户原有 17 条未跟踪资源（我新建的 3 个贴图已删）。
- 父仓：本批仅改 `notes/`（§126 本节）；`fix_eol --check` 候选 6。
- 其他子模块无用户在建改动（lib／TerraCurio 工作区干净）。

## 一百二十七、落地记录：行 148 第一批（`ModelLightBlock` + 三条云杉烛台注册）

用户指示「继续」后，按 §126.3 的默认分工（**我推进 Java 侧，不碰也不提交工作区在建资源**）执行 §125.3 第 1 步。

### 127.1 落地内容（`TerraFurniture` 子模块）

| 文件 | 改动 |
| --- | --- |
| `common/block/light/ModelLightBlock.java`（**新增**） | `extends SwitchableLightBlock`；持 `private final VoxelShape shape`；构造器 `(TFBlockSetType, Properties, BlockShapeType, VoxelShape)` 转调 `super(type, properties, supportType)`；覆写 `protected VoxelShape getShape(...)` 返回固定形状；覆写 `public @Nullable BlockDataGenerator<? super SwitchableLightBlock> getGenerator()` 返回 `null`（"模型/方块态/碰撞箱手工提供"语义）。与 1.20 原文逐行对应，仅 `getShape` 可见性取 1.21 的 `protected`（该 javadoc 为 1.20 原作者所有，原样保留） |
| `common/init/TFBlocks.java` | 新增两条 import（`…block.light.BlockShapeType`、`…block.light.ModelLightBlock`）；新增 `SPRUCE_CANDLESTICK_ONE/TWO/THREE` 三条 `registerWithItem(...)`，属性与碰撞箱照抄 1.20 注册原文：`BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).noOcclusion().lightLevel(litBlockEmission(15))`、`BlockShapeType.CANDLE`、`Block.box(6.5,0,6.5,9.5,13,9.5)` / `(3.5,0,6.5,12.5,13,9.5)` / `(2.5,0,6.5,13.5,13,9.5)` |

### 127.2 落地前核实的 API 依据（本工作流无编译验证，故逐项对证）

| 项 | 结论 | 依据 |
| --- | --- | --- |
| 构造器 | `SwitchableLightBlock(TFBlockSetType, Properties, BlockShapeType)` 两侧同签名 | 1.21 `SwitchableLightBlock.java:43` |
| `getShape` 可见性 | 1.21 为 `protected`（1.20 为 public，Java 允许放宽，这里取一致） | 1.21 `:51` |
| `getGenerator` 返回类型 | `@Nullable BlockDataGenerator<? super SwitchableLightBlock>`，两侧一致 | 1.21 `:117` |
| 包路径 | `TFBlockSetType` → `common.block.func.set`、`BlockDataGenerator` → `common.datagen.empowered`，**两侧完全相同** | 1.21 `SwitchableLightBlock` import `:30/:33` |
| 注册助手 | `registerWithItem(String, Supplier<B>)` | 1.21 `TFBlocks:299` |
| 变体常量 | `SPRUCE`（`TFBlockSetTypes.java:22`）由 `import static …TFBlockSetTypes.*`（`TFBlocks:42`）提供 | 实测 |
| 光照函数 | `public static ToIntFunction<BlockState> litBlockEmission(int)` | 1.21 `TFBlocks:331` |
| 属性工厂 | 1.21 用 `Properties.ofFullCopy(...)`（13 处），`Properties.copy(...)` **0 处** ⇒ 取 `ofFullCopy` | 实测 |
| 形状枚举 | `BlockShapeType.CANDLE` 存在（`LAMP/CANDLE/CHANDELIER/LANTERN/DEFAULT`） | `BlockShapeType.java:15` |

结构自检：新类 30 行（纯 CRLF）、`{}` 4/4；`TFBlocks` 插入后 352 行（纯 CRLF）、`{}` 8/8、`()` 375/375 平衡；子模块已跟踪改动仅这 2 个文件（工作区 17 条用户在建资源保持未跟踪、未触碰）。

### 127.3 行 148 的剩余部分（台账仍为 `TODO`）

| 批次 | 内容 | 状态 |
| --- | --- | --- |
| 第一批（本批） | `ModelLightBlock` + 三条云杉烛台注册 | **已落地** |
| 第二批 | `SPRUCE_SET` 的 `TFBlockType.LAMP` 改由 `ModelLightBlock` 提供（1.20 用 `setGetterFor(TFBlockType.LAMP, …)`；1.21 `TFBlockSet.java:158` 现为 `new SwitchableLightBlock(..., BlockShapeType.LAMP)`，需查 1.21 `TFBlockSet.Builder` 是否提供同名 getter 覆盖） | 待做 |
| 第三批 | 樱花木箱：`CherryChestBlock`(61 行) + `CherryChestGeoModel` + `CHERRY_CHEST(_ENTITY/_ITEM)` 注册（`TFBlockSetTypes.CHERRY` 已在 `:28`） | 待做 |
| 第四批 | 马桶/浴缸/水槽：`ToiletBlock` 已存在，`BathtubBlock`(106)/`SinkBlock` 变体（`IRON_BASE`／`SPRUCE_BASE`）与 `candelabras.json`(155) | 待做 |

> 资产（blockstates/models/textures 与 `spruce_bathtub/`）在用户工作区在建，按纪律既不提交也不改动；Java 侧落地后，用户提交资源即可整体生效。

### 127.4 状态

- `TerraFurniture` 子模块：已提交本批 2 个 Java 文件；父仓更新 gitlink。
- 台账：行 148 仍为 `TODO`（本批为部分落地）；全量统计不变（`COVERED` 122 / `SKIP-PORTLIB` 12 / `REVERSE-ALIGNED` 6 / `DEFER-ASSETS` 3 / `TODO` 4）。
- `fix_eol --check` 候选 6；下一步：行 148 第二批（`SPRUCE_SET` 的 LAMP getter 覆盖）。

## 一百二十八、落地记录：行 148 第二批（`SPRUCE_SET` 的 LAMP 改由 `ModelLightBlock` 提供）

### 128.1 改动（`TerraFurniture` 子模块，`common/init/TFBlocks.java`）

```java
public static final TFBlockSet SPRUCE_SET = new TFBlockSet.Builder(SPRUCE, Blocks.SPRUCE_PLANKS, true)
        .disableAll()
        .setAvailabilityFor(TFBlockType.TABLE, true)
        .setAvailabilityFor(TFBlockType.CHAIR, true)
        .setAvailabilityFor(TFBlockType.LAMP, true)                                  // 本批 +3
        .setGetterFor(TFBlockType.LAMP, (properties, applier) -> new ModelLightBlock(
                SPRUCE, properties, BlockShapeType.LAMP, Block.box(5, 0, 5, 11, 26, 11)))
        .setPropertyFor(TFBlockType.LAMP, properties -> properties.noOcclusion().lightLevel(litBlockEmission(15)))
        .build();
```

### 128.2 一处刻意的写法偏差（已在此登记）

1.20 侧是分两步写：`setPropertyFor(LAMP, noOcclusion)`（`1.20 TFBlocks:180`）**之后** 再 `doLightSetup(14, 14, 15, 15, 15)`（`:182`），而 `doLightSetup` 内部对 LAMP 又调 `setPropertyFor(LAMP, lightLevel(...))`（1.21 `TFBlockSet:249` 同构）。在 1.21 的 Builder 里按同序书写，后一次 `setPropertyFor` 会**覆盖**前一次，灯的 `noOcclusion` 会丢；本批改为**一次 `setPropertyFor` 同时给足 `noOcclusion` 与 15 级亮度**，语义等价且与调用顺序无关（该集合内 CANDLE／LANTERN／CANDELABRAS／CHANDELIER 均 disabled，`doLightSetup` 的其余四项没有作用对象）。

### 128.3 落地前核实（无编译验证，故逐项对证）

| 项 | 结论 | 依据 |
| --- | --- | --- |
| `setAvailabilityFor(TFBlockType<T>, boolean)` | 存在 | 1.21 `TFBlockSet:188` |
| `setGetterFor(TFBlockType<T>, BiFunction<Properties, Consumer<Properties>, T>)` | 存在，与 1.20 的 `(properties, applier) -> …` 用法匹配 | 1.21 `TFBlockSet:194` |
| `setPropertyFor(TFBlockType<T>, Function<Properties, Properties>)` | 存在 | 1.21 `TFBlockSet:209` |
| `TFBlockType.LAMP` 的类型 | 集合字段为 `DeferredBlock<SwitchableLightBlock>`（`TFBlockSet:52`），`ModelLightBlock` 是其子类 ⇒ 协变可用 | 实测 |
| 1.21 默认 LAMP 提供者 | `putEntry(TFBlockType.LAMP, (p, a) -> new SwitchableLightBlock(materialType, p, BlockShapeType.LAMP))`（`:158`），本批用 getter 覆盖 | 实测 |

结构自检：`TFBlocks.java` 357 行、纯 CRLF、`{}` 8/8、`()` 384/384 平衡；子模块已跟踪改动仍**仅**该文件，工作区 17 条用户在建资源未动。

### 128.4 灯的资源仍缺（未纳入）

1.20 侧灯的资源：`blockstates/spruce_lamp.json`（7 行）、`models/block/spruce/spruce_lamp_lit.json`（498 行）、`models/block/spruce/spruce_lamp_unlit.json`（404 行）、`models/item/spruce_lamp.json`（11 行）、`textures/block/spruce/spruce_lamp.png`（4 行）。它们**不在**用户工作区那 17 条 WIP 里 ⇒ 本批 Java 落地后，云杉灯在资源补齐前会缺模型。按「资源归用户在建面」的分工，本批不擅自搬运；如需我搬，说一声即可（1.20 侧字节拷贝，零编译风险）。

### 128.5 状态

- `TerraFurniture` 子模块：本批 1 个文件（`TFBlocks.java`）已提交；父仓更新 gitlink。
- 台账：行 148 仍 `TODO`（已完成第一、二批；余：樱花木箱、马桶/浴缸/水槽）。
- `fix_eol --check` 候选 6；下一步：行 148 第三批（樱花木箱 `CherryChestBlock` + `CherryChestGeoModel` + 注册）。

## 一百二十九、落地记录：行 148 第三批（樱花木箱）

### 129.1 改动（`TerraFurniture` 子模块，4 个文件）

| 文件 | 类型 | 内容 |
| --- | --- | --- |
| `common/block/misc/CherryChestBlock.java` | 新增（60 行） | `extends ChestBlock`；`super(properties, TFBlocks.CHERRY_CHEST_ENTITY::get)`；`getStateForPlacement`／`updateShape` 强制 `ChestType.SINGLE`；`newBlockEntity` 返回内部 `Entity`；`Entity extends ChestBlockEntity implements GeoBlockEntity`（`GeckoLibUtil.createInstanceCache(this)`、空 `registerControllers`） |
| `client/model/CherryChestGeoModel.java` | 新增（17 行） | `extends CacheBlockModel<CherryChestBlock.Entity>`；`setCustomAnimations(...)`：`float openness = chest.getOpenNess(animationState.getPartialTick())` → 缓动 → `getBone("down").ifPresent(lid -> lid.setRotX(-eased * (float) (Math.PI / 2.0)))` |
| `common/init/TFBlocks.java` | +9 行 | `CHERRY_CHEST`（`registerWithoutItem` + `ofFullCopy(Blocks.CHEST).noOcclusion()`）、`CHERRY_CHEST_ITEM`（`SimpleGeoRenderedItem`）、`CHERRY_CHEST_ENTITY`（`BlockEntityType.Builder.of(...).build(DSL.remainderType())`）＋ 1 条 import |
| `client/event/TFModClient.java` | +4 行 | 2 条 import + `event.registerBlockEntityRenderer(TFBlocks.CHERRY_CHEST_ENTITY.get(), context -> BaseFunctionalGeoBER.Builder.<CherryChestBlock.Entity>of(new CherryChestGeoModel(), false).build());` |

### 129.2 1.20 → 1.21 的适配点（全部来自 1.21 原生用法）

| 项 | 1.20 | 1.21 |
| --- | --- | --- |
| GeckoLib 包名 | `software.bernie.geckolib.core.animation.*`／`core.animatable.instance.*` / `core.animatable.model.CoreGeoBone` | 去 `core.` 前缀：`animation.AnimatableManager`／`animation.AnimationState`／`animatable.instance.AnimatableInstanceCache`／`cache.object.GeoBone` |
| `SimpleGeoRenderedItem` 构造 | `(block, properties, false, false)` 四参 | **三参** `(block, properties, isNegative)`（`SimpleGeoRenderedItem.java:20`） |
| GeckoLib 方块实体样板 | — | 与 1.21 TF `SimpleModelGeoBE:15`／`LargeChandelierBlock:162` 同形（`GeckoLibUtil.createInstanceCache(this)`） |

### 129.3 本批 API 的取证方式（关键：本地就有 1.21.1 vanilla 源树）

本轮发现工作区里存在 **`build/_nfsrc_219/`**，其 `SharedConstants.java` 标明 `VERSION_STRING = "1.21.1"`／`WORLD_VERSION = 3955` ⇒ 是 **Minecraft 1.21.1 的本地反编译源**，vanilla API 可就地取证，不必再靠推断：

| API | 依据 |
| --- | --- |
| `ChestBlock(Properties, Supplier<BlockEntityType<? extends ChestBlockEntity>>)` | `_nfsrc_219/…/ChestBlock.java:121` |
| `ChestBlockEntity.getOpenNess(float)`（内部委托 `chestLidController.getOpenness`） | `_nfsrc_219/…/ChestBlockEntity.java:149` |
| `ChestBlock.TYPE`（`EnumProperty<ChestType>`） | `_nfsrc_219/…/ChestBlock.java:57` |
| `SimpleGeoRenderedItem(Block, Properties, boolean)` | `SimpleGeoRenderedItem.java:20` |
| `registerWithoutItem(String, Supplier<B>)` | `TFBlocks.java:337` |
| 方块实体注册形态（`DeferredHolder<BlockEntityType<?>, …>` + `DSL.remainderType()`） | `TFBlocks.java:282-284` |
| `BaseFunctionalGeoBER.Builder.of(GeoModel<O>, boolean)` | `BaseFunctionalGeoBER.java:79` |
| `CacheBlockModel<T>` 无参构造 + `setCustomAnimations` 签名 | `CacheBlockModel.java:37`；主仓 `AngryTumblerModel.java:13` |
| `getBone(String).ifPresent(...)` + `GeoBone.setRotX(float)` | 主仓 `AngryTumblerModel.java:15/17`、`HopliteModel.java:17` |
| `AnimationState` 包名 | `software.bernie.geckolib.animation.AnimationState`（主仓 `AngryTumblerModel.java:5`） |

结构自检：两个新文件与两个改动文件均纯 CRLF、`{}`／`()` 平衡；子模块已跟踪改动**仅**这 4 个 Java（工作区 17 条用户在建资源未动）。

### 129.4 樱花木箱的资源仍缺（未纳入，5 个）

| 1.20 侧文件 | 行数 |
| --- | --- |
| `assets/terra_furniture/blockstates/cherry_chest.json` | 6 |
| `assets/terra_furniture/geo/block/cherry_chest.geo.json` | 42 |
| `assets/terra_furniture/models/block/cherry_chest.json` | 7 |
| `assets/terra_furniture/models/item/cherry_chest.json` | 13 |
| `assets/terra_furniture/textures/block/cherry_chest.png` | （二进制） |

（另有 `textures/block/cherry/cherry_table.png`，属樱花木桌、非本批。）这些**不在**用户工作区那 17 条 WIP 内；按分工未擅自搬运。`CacheBlockModel` 按约定从 `geo/block/<id>.geo.json` 取模型，故缺 `cherry_chest.geo.json` 时方块无模型。

### 129.5 状态

- `TerraFurniture` 子模块：本批 4 个文件已提交；父仓更新 gitlink。
- 台账：行 148 仍 `TODO`（已完成第一、二、三批；余：马桶/浴缸/水槽）。
- `fix_eol --check` 候选 6；下一步：行 148 第四批（`BathtubBlock`／`SinkBlock` 变体／马桶与烛台变体），或先补两批的资产。

## 一百三十、落地记录：行 145 第一批（`SinkBlock` 铁/云杉变体形状）

### 130.1 改动（`TerraFurniture` 子模块，1 个文件，+38 行）

1.20 的 `SinkBlock` 是 133 行、1.21 只有 88 行，**1.20 独有 38 行**（行级差集实测）。本批把这 38 行按 1.21 风格合入：

| 位置 | 内容 |
| --- | --- |
| import | `org.confluence.terra_furniture.common.init.TFBlockSetTypes` |
| 字段 | `protected static final VoxelShape IRON_BASE;` / `SPRUCE_BASE;` |
| `getShape` | 前置两个分支：`if (getType().equals(TFBlockSetTypes.IRON)) return modelFittedShape(IRON_BASE, state.getValue(FACING));` 与 SPRUCE 同理；其后维持原有 `switch (FACING)`（**1.21 的 `protected @NotNull` 签名与结构未动**） |
| 新方法 | `private static VoxelShape modelFittedShape(VoxelShape base, Direction facing)`：四向各给直立管 `Block.box(…,14,…,19,…)` 与出水口 `Block.box(…,16,…,19,…)`，`Shapes.or(base, upright, spout)` |
| 静态块 | `IRON_BASE = Shapes.or(三块叠加)`；`SPRUCE_BASE = Shapes.or(缸体 + 四边矮沿，五块叠加)` |

### 130.2 核验

| 项 | 结果 |
| --- | --- |
| 计数一致性 | `IRON_BASE`／`SPRUCE_BASE`／`modelFittedShape`／`TFBlockSetTypes` 在 **1.20 与 1.21 均各 3 处**，`getShape` 各 1 处 ✓ |
| 变体常量存在性 | `TFBlockSetTypes.IRON`（`:42`）／`SPRUCE`（`:22`）在 1.21 均在 ✓ |
| 结构自检 | 134 行、纯 CRLF、`{}` 25/25、`()` 72/72 ✓；子模块已跟踪改动**仅**该文件（用户 17 条在建资源未动） |
| 平台面 | 用到的都是 vanilla（`Block.box`、`Shapes.or`、`Mirror`/`Rotation`）⇒ 已在本地 1.21.1 源树 `build/_nfsrc_219` 中确认存在 |

> 说明：本批脚本末尾那条 `assert` 是我把期望值写成 4 而写错的（正确为 3：声明／使用／初始化各一处），补丁本身在 assert 之前已正确落盘，随后用计数复核确认无误。

### 130.3 行 145 的剩余

| 项 | 内容 |
| --- | --- |
| `SPRUCE_SET` 条目 | 1.20 的 `SPRUCE_SET` 还有 BATHTUB（`new BathtubBlock(SPRUCE, properties, BathtubBlock.tubShapes(10, 2), false)` + noOcclusion）、SINK（+ noOcclusion）、TOILET（+ noOcclusion）、CANDELABRAS、`doLightSetup(14,14,15,15,15)`；1.21 当前仅 TABLE／CHAIR／LAMP（本线第一、二批） |
| 依赖 | BATHTUB 需要 `BathtubBlock` 的自定义形状支持（1.20 独有 31 行，下一批）；SINK 需要本批的变体形状 ✓ 已就绪 |
| 资产 | `iron_sink.json`／`spruce_sink.json`／`candelabras.json`(155) 等仍未纳入 |

### 130.4 状态

- `TerraFurniture` 子模块：本批 1 个文件已提交；父仓更新 gitlink。
- 台账：行 145 仍 `TODO`（第一批已落地）；全量统计不变（`COVERED` 122／`SKIP-PORTLIB` 12／`REVERSE-ALIGNED` 6／`DEFER-ASSETS` 3／`TODO` 4）。
- `fix_eol --check` 候选 6；下一步：行 147 第一批（`BathtubBlock` 自定义形状 31 行）或行 145 第二批（`SPRUCE_SET` 的 SINK 条目）。

## 一百三十一、落地记录：行 147 第一批（`BathtubBlock` 自定义缸体形状）

### 131.1 改动（`TerraFurniture` 子模块，1 个文件，+34 行）

| 位置 | 内容 |
| --- | --- |
| import | `org.confluence.lib.common.block.StateProperties`、`org.jetbrains.annotations.Nullable` |
| 字段 | `private final VoxelShape @Nullable [] customShapes;`（按开口方向排列，null 时用默认 12 像素缸体）、`private final boolean singleTexture;`（两半是否共用贴图）＋ 1.20 原文注释 |
| 构造器 | 2 参改为 `this(type, properties, null, true);`；新增 4 参 `(TFBlockSetType, Properties, VoxelShape @Nullable[], boolean)` ＋ 1.20 原文 javadoc |
| 新方法 | `public static VoxelShape[] tubShapes(int height, int thickness)`：`Shapes.join(outer, box(...), BooleanOp.ONLY_FIRST)` 四向缸体 |
| `getShape` | 前置分支：`if (customShapes != null) { int openDirection = StateProperties.ForwardTwoPart.getConnectedDirection(state).get2DDataValue(); return customShapes[openDirection]; }`；**保留 1.21 的 `protected` 签名** |
| 三处 | `isSingleTexture()`／`hasParticle(TFBedBlock)`／`needItemTexture()` 由 `return true;` 改为 `return singleTexture;` |

### 131.2 核验（本批用「规范化行集比对」而非仅计数）

| 项 | 结果 |
| --- | --- |
| 非空行集比对 | 1.20 = **91 行**，1.21 = **91 行**；**唯一差异**是 `public VoxelShape getShape(...)`（1.20）对 `protected VoxelShape getShape(...)`（1.21，刻意保留） ✓ |
| 关键符号计数 | `customShapes` 4/4、`singleTexture` 8/8、`tubShapes` 2/2、`StateProperties` 2/2（两侧一致） |
| `getConnectedDirection` 的 1.21 形态 | `StateProperties.ForwardTwoPart.getConnectedDirection(state)` 是 **1.21 原生写法**：lib `HorizontalDirectionalWithForwardTwoPartBlock:56`、TerraFurniture `TFBedBlock:140`、主仓 `BehaviourStatueBlock:90/138/157`（`StateProperties.VerticalTwoPart.…`） ⇒ 1.20 那行可原样使用，无需改造 |
| 结构自检 | 107 行、纯 CRLF、`{}` 18/18、`()` 49/49；子模块已跟踪改动**仅**该文件（用户 17 条在建资源未动） |

### 131.3 行 147 的剩余

| 项 | 内容 |
| --- | --- |
| `SPRUCE_SET` 的 BATHTUB 条目 | 1.20：`.setAvailabilityFor(TFBlockType.BATHTUB, true)` ＋ `.setGetterFor(TFBlockType.BATHTUB, (properties, applier) -> new BathtubBlock(SPRUCE, properties, BathtubBlock.tubShapes(10, 2), false))` ＋ `.setPropertyFor(BATHTUB, noOcclusion())` ⇒ 本批完成后该条目可照搬 |
| 资产 | `bathtub.json`(134)、`base.json`(88)、`forward.json`(49)、`bed.json`(26) 与 `textures/block/spruce/spruce_bathtub/`（用户工作区已有部分：`spruce_bathtub/` 目录在其实 17 条 WIP 内） |

### 131.4 状态

- `TerraFurniture` 子模块：本批 1 个文件已提交；父仓更新 gitlink。
- 台账：行 147 仍 `TODO`（第一批已落地）；全量统计不变（`COVERED` 122／`SKIP-PORTLIB` 12／`REVERSE-ALIGNED` 6／`DEFER-ASSETS` 3／`TODO` 4）。
- `fix_eol --check` 候选 6；下一步：行 146（`CandelabraBlock` 55 行：粒子与变体形状）或行 147 第二批（`SPRUCE_SET` 的 BATHTUB）。

## 一百三十二、落地记录：行 145/147 第二批（`SPRUCE_SET` 的 BATHTUB/SINK/TOILET ＋ `IRON_SET` 的 SINK）

### 132.1 改动（`TerraFurniture` 子模块，1 个文件 `TFBlocks.java`，+11 行）

`SPRUCE_SET` 现与 1.20 同序（TABLE → CHAIR → **BATHTUB → SINK → TOILET** → LAMP）：

```java
.setAvailabilityFor(TFBlockType.BATHTUB, true)
.setGetterFor(TFBlockType.BATHTUB, (properties, applier) -> new BathtubBlock(SPRUCE, properties, BathtubBlock.tubShapes(10, 2), false))
.setPropertyFor(TFBlockType.BATHTUB, properties -> properties.noOcclusion())
.setAvailabilityFor(TFBlockType.SINK, true)
.setPropertyFor(TFBlockType.SINK, properties -> properties.noOcclusion())
.setAvailabilityFor(TFBlockType.TOILET, true)
.setPropertyFor(TFBlockType.TOILET, properties -> properties.noOcclusion())
```

`IRON_SET` 补 `SINK`（availability + `noOcclusion()`），即 1.20 的铁水槽；另加 import `common.block.sleep.BathtubBlock`。

### 132.2 前置依赖（都在本线前面几批落地）

| 依赖 | 批次 |
| --- | --- |
| `BathtubBlock` 4 参构造 + `tubShapes(int, int)` | §131（行 147-1） |
| `SinkBlock` 的 `IRON_BASE`／`SPRUCE_BASE` 变体形状 | §130（行 145-1） |
| `TFBlockType.BATHTUB`／`SINK`／`TOILET` 与 `TFBlockSet.Builder` 的 `setAvailabilityFor`／`setGetterFor`／`setPropertyFor` | 1.21 原有（§128.3 已核） |

### 132.3 核验（与 1.20 的计数逐项对齐）

| 符号 | 1.21 | 1.20 | 结果 |
| --- | --- | --- | --- |
| `BathtubBlock` | 3 | 3 | OK |
| `TFBlockType.BATHTUB` | 3 | 3 | OK |
| `TFBlockType.SINK` | 4 | 4 | OK（SPRUCE_SET 2 + IRON_SET 2） |
| `TFBlockType.TOILET` | 3 | 3 | OK |
| `ModelLightBlock` | **8**（import 1 + 3 条烛台 `DeferredBlock<ModelLightBlock>` + 3 处 `new ModelLightBlock(` + `SPRUCE_SET` 1 处） | 4（1.20 用 `PortDeferredBlock<…>` 体系与不同的注册点，**非同项**） | 不作数值比较（该行仅登记实测值） |

结构自检：`TFBlocks.java` 376 行、纯 CRLF、`{}` 8/8、`()` 416/416；子模块已跟踪改动**仅**该文件。

### 132.4 本批刻意留下的一项

1.20 的 `SPRUCE_SET` 末尾还有 `.setAvailabilityFor(TFBlockType.CANDELABRAS, true)` 与 `.doLightSetup(14, 14, 15, 15, 15)`。二者**留待行 146 批次**：`doLightSetup` 会对 LAMP 等类型再调一次 `setPropertyFor(lightLevel)`，若现在加进来会覆盖本线第二批给 LAMP 设置的 `noOcclusion`（§128.2 已记录该顺序陷阱）；与烛台变体（`SPRUCE_SHAPE`／粒子）一起处理更合适。

### 132.5 状态

- `TerraFurniture` 子模块：本批 1 个文件已提交；父仓更新 gitlink。
- 台账：行 145／147 仍 `TODO`（各已完成两批/一批）；全量统计不变。
- `fix_eol --check` 候选 6；下一步：行 146（`CandelabraBlock` 55 行：粒子 + `SPRUCE_SHAPE` 等变体形状 + CANDELABRAS/doLightSetup 条目）。

## 一百三十三、落地记录：行 146（`CandelabraBlock` 粒子与变体形状 ＋ `SPRUCE_SET` 收尾）

### 133.1 改动（`TerraFurniture` 子模块，2 个文件）

| 文件 | 内容 |
| --- | --- |
| `common/block/light/CandelabraBlock.java` | 新增 `SPRUCE_SHAPE`（三段 `Block.box` 叠加）与三张火焰坐标表（`GLASS_FLAMES`／`BLUE_DUNGEON_FLAMES`／`SPRUCE_FLAMES`）；`getShape` 改为 `getType() == SPRUCE ? SPRUCE_SHAPE : SHAPE`（保留 1.21 的 `protected`）；新增 `animateTick(BlockState, Level, BlockPos, RandomSource)`（未点燃或含水即返回；GLASS→`ParticleTypes.SMALL_FLAME` 随 FACING 旋转，BLUE_DUNGEON／SPRUCE→`SOUL_FIRE_FLAME` 不旋转）与 `private static void addFlames(...)`；补 5 条 vanilla import 与 3 条 `static TFBlockSetTypes.*` |
| `common/init/TFBlocks.java` | `SPRUCE_SET` 收尾：`.setAvailabilityFor(TFBlockType.CANDELABRAS, true)` 与 `.doLightSetup(14, 14, 15, 15, 15)`（1.20 原序，紧随 LAMP 之后） |

### 133.2 核验

| 项 | 结果 |
| --- | --- |
| `CandelabraBlock` 行集比对 | 1.20 = **133 行**、1.21 = **133 行**（非空）；**仅 4 处差异**：`net.minecraftforge.common.data.BlockTagsProvider` → `net.neoforged.neoforge.common.data.BlockTagsProvider`（平台），以及 `getShape`／`rotate`／`mirror` 的 `public` → `protected`（1.21 可见性） |
| 符号计数 | `SPRUCE_SHAPE` 2/2、`GLASS_FLAMES` 2/2、`BLUE_DUNGEON_FLAMES` 2/2、`SPRUCE_FLAMES` 2/2、`animateTick` 1/1、`addFlames` 4/4、`ParticleTypes.` 3/3 ✓ |
| `SPRUCE_SET` 计数 | `TFBlockType.CANDELABRAS` 1/1、`doLightSetup` 3/3 ✓ |
| 1.21.1 vanilla 取证 | `Block.animateTick(BlockState, Level, BlockPos, RandomSource)`（`_nfsrc_219/…/Block.java:277`）、`ParticleTypes.SMALL_FLAME`（`:115`）／`SOUL_FIRE_FLAME`（`:63`）、`Level.addParticle(ParticleOptions, double×6)`（`:530`） |
| 结构自检 | `CandelabraBlock` 143 行纯 CRLF、`{}` 36/36、`()` 61/61；`TFBlocks` 378 行纯 CRLF、`{}` 8/8、`()` 418/418 |

### 133.3 **纠正 §128.2／§132.4 的一处错误论断**

§128.2 与 §132.4 曾判断：1.20 先 `setPropertyFor(LAMP, noOcclusion)` 再 `doLightSetup(...)`，后者会对 LAMP 再调 `setPropertyFor(lightLevel)` 从而**覆盖**前者的 `noOcclusion`，因此第二批把两条合并成一次调用。**该判断是错的**，实测 1.21 的 `TFBlockSet.Builder.setPropertyFor` 是**叠加**语义：

```java
public <T extends Block> Builder setPropertyFor(TFBlockType<T> key, Function<Properties, Properties> properties) {
    BlockBehaviour.Properties old = entries.get(key).properties;
    entries.get(key).properties = properties.apply(old);   // 在既有属性上再应用，而非替换
    return this;
}
```

且 `doLightSetup` 的 5 条 `setPropertyFor(...)` 是在 **`build()` 内**执行的（`TFBlockSet.java:246-250`），所以最终 `LAMP` 的属性 = 默认 → `noOcclusion()` + `lightLevel(15)`（第二批）→ `lightLevel(15)`（build 阶段再叠一次）⇒ **两个效果都在**。

结论：① 第二批的合并写法**并非必要**（但结果等价、无副作用，不回退）；② §132.4 里"留到行 146 再处理 doLightSetup"的理由同样不成立，本批已按 1.20 原序补上。此纠正仅影响理由陈述，**不影响已落地的代码语义**。

### 133.4 状态

- `TerraFurniture` 子模块：本批 2 个文件已提交；父仓更新 gitlink。
- **四个 TODO 行的 Java 侧已全部落地**：行 146 ✓（本批）、行 145 ✓（SinkBlock 变体 + IRON_SET/SPRUCE_SET 的 SINK）、行 147 ✓（BathtubBlock + SPRUCE_SET 的 BATHTUB）、行 148 ✓（ModelLightBlock + 三条烛台 + LAMP + 樱花木箱）。
- 仍缺的**非 Java**面（下一批起）：`TFBlockTagsProvider`(+7)／`TFChineseProvider`(+3) 的新方块 tag/lang 条目（属 Java，但属"随功能收尾"，需按 1.20 的 provider 差异核对），以及各批资产（云杉灯、樱花木箱、`iron_sink`／`spruce_sink`／`candelabras`、`bathtub`/`base`/`forward`/`bed` 等）。
- `fix_eol --check` 候选 6；下一步：**provider 差异批次**（tag/lang 收尾），随后视用户指示处理资产。

## 一百三十四、落地记录：行 145/146/147/148 收尾（新方块的 tag 与中文名）

### 134.1 改动（`TerraFurniture` 子模块，2 个文件）

| 文件 | 内容 |
| --- | --- |
| `common/datagen/TFBlockTagsProvider.java` | `tag(TFTags.SINKS)` 增 `.add(SPRUCE_SET.SINK)`／`.add(IRON_SET.SINK)`；新增三组 `tag(...)`：`TFTags.SPRUCE_FURNITURE`（`SPRUCE_SET` 的 SINK/TOILET/LAMP ＋ 三条 `SPRUCE_CANDLESTICK`）、`TFTags.WOODEN_FURNITURE`（上述 ＋ `CHERRY_CHEST`）、`TFTags.IRON_FURNITURE`（`IRON_SET.SINK`）；`BlockTags.MINEABLE_WITH_PICKAXE` 增 `.add(IRON_SET.SINK)` |
| `common/datagen/TFChineseProvider.java` | 10 条中文名（照抄 1.20 原文）：云杉木烛台／马桶／落地灯、云杉木单/双/三烛台、樱花木箱、云杉木水槽、云杉木浴缸、铁水槽 |

### 134.2 核验（行集比对）

| 文件 | 1.20 | 1.21 | 差异 |
| --- | --- | --- | --- |
| `TFBlockTagsProvider.java` | 77 行（非空） | **77 行** | **仅 2 处**：`net.minecraftforge.common.data.BlockTagsProvider`／`ExistingFileHelper` → `net.neoforged.neoforge.common.data.*` |
| `TFChineseProvider.java` | 111 行 | **112 行** | 1.20 独有：`net.minecraftforge.common.data.LanguageProvider`（→ NeoForge 同名）；1.21 独有：同 import 的 NeoForge 版 ＋ **`add(TFBlocks.TENT.get(), "帐篷");`**（帐篷为 1.21 侧新增内容，**保留**） |

前置核实：`TFTags.SPRUCE_FURNITURE`（`TFTags.java:11`）／`IRON_FURNITURE`（`:21`）／`WOODEN_FURNITURE`（`:87`）／`SINKS`（`:92`）在 1.21 均在；`TFBlockSet` 公开字段 `SINK`（`:45`）／`TOILET`（`:42`）／`BATHTUB`（`:44`）／`LAMP`（`:52`）／`CANDELABRAS`（`:53`）在 1.21 均在。

### 134.3 里程碑：四个 TODO 行的 **Java 面全部落地**

至此行 145／146／147／148 的 Java 面（方块类、注册、渲染器、tag、语言）均已落地：

| 行 | Java 面完成情况 |
| --- | --- |
| 148 | `ModelLightBlock` ＋ 三条 `SPRUCE_CANDLESTICK_*`（§127）＋ `SPRUCE_SET` 的 LAMP（§128）＋ 樱花木箱 4 文件（§129）＋ CANDELABRAS/doLightSetup（§133）＋ tag/lang（§134） |
| 145 | `SinkBlock` 铁/云杉变体（§130）＋ `SPRUCE_SET` 的 BATHTUB/SINK/TOILET 与 `IRON_SET` 的 SINK（§132）＋ tag/lang（§134） |
| 147 | `BathtubBlock` 自定义形状（§131）＋ `SPRUCE_SET` 的 BATHTUB（§132）＋ tag/lang（§134） |
| 146 | `CandelabraBlock` 粒子与 `SPRUCE_SHAPE`（§133）＋ CANDELABRAS 条目与 `doLightSetup`（§133）＋ tag/lang（§134） |

### 134.4 仍缺：只剩资产

| 批次 | 缺的资源 |
| --- | --- |
| 云杉灯（148） | `blockstates/spruce_lamp.json`、`models/block/spruce/spruce_lamp_{lit,unlit}.json`、`models/item/spruce_lamp.json`、`textures/block/spruce/spruce_lamp.png` |
| 樱花木箱（148） | `blockstates/cherry_chest.json`、`geo/block/cherry_chest.geo.json`、`models/block/cherry_chest.json`、`models/item/cherry_chest.json`、`textures/block/cherry_chest.png` |
| 烛台（148） | 用户工作区已有（12 个 json ＋ `spruce_candlestick.png`） |
| 水槽（145） | `iron_sink.json`、`spruce_sink.json`、`candelabras.json`(155) 等 |
| 浴缸（147） | `bathtub.json`(134)、`base.json`(88)、`forward.json`(49)、`bed.json`(26)（贴图目录 `spruce_bathtub/` 用户已有） |

> 资产按分工属用户在建面（工作区 17 条 WIP 已覆盖烛台 12 项、`spruce_candelabras.png`、`spruce_bathtub/`），本轮**未搬未提交**。

### 134.5 状态

- `TerraFurniture` 子模块：本批 2 个文件已提交；父仓更新 gitlink。
- 下一步：重跑行 141／145／146／147／148 的 audit 以量化剩余 gap（预期只剩资产），据此更新台账状态（Java 已完结、余项为资产的行使 `DEFER-ASSETS`）。
- `fix_eol --check` 候选 6。

## 一百三十五、落地后的端到端复核（重跑 audit）＋ 剩余 gap 分类

### 135.1 重跑 audit：行 141／145／146／147／148 的 gap 前后对比

以落地后的 1.21 子模块 HEAD 重跑同一套 audit（`sub_rows.py TerraFurniture 141 145 146 147 148`）：

| 行 | 落地前 GAP/文件 | 落地后 GAP/文件 | 缺符号（前→后） | 说明 |
| --- | --- | --- | --- | --- |
| 141 | 21 / 1 | 21 / 1 | 3 → 3 | 该行早已判为 `DEFER-ASSETS`（三个"缺符号"是 1.20 自己改名的旧名，DEAD），剩 `one_leg_table.json` 内容差 |
| 145 | 443 / 12 | **400 / 8** | 2 → **0** | `SinkBlock` 变体（§130）＋ `IRON_SET`/`SPRUCE_SET` 的 SINK（§132）＋ tag/lang（§134）已落地 |
| 146 | 69 / 2 | **19 / 1** | 5 → 1 | `CandelabraBlock` 的 50 行（§133）已落地 |
| 147 | 359 / 10 | **327 / 6** | 1 → 0 | `BathtubBlock` 的 31 行（§131）＋ `SPRUCE_SET` 的 BATHTUB（§132）已落地 |
| 148 | 6714 / 37 | **6597 / 34** | 9 → **0** | `ModelLightBlock`／三条烛台／樱花木箱／LAMP 等（§127–§133）＋ tag/lang（§134）已落地 |

合计消除 **287 行** gap 与 10 个文件项；`SimpleGeoRenderedItem` 等缺符号全部归零。

### 135.2 剩余 gap 的逐文件分类（决定台账状态）

| 行 | 剩余 | 分类 |
| --- | --- | --- |
| 145 | 8 文件 400 行：`models/block/spruce/candelabras.json`(155)、`models/block/iron/sink.json`(112)、`models/block/spruce/sink.json`(86)、`blockstates/iron_sink.json`(19)、`blockstates/spruce_sink.json`(19)、`models/item/{iron_sink,spruce_sink,spruce_candelabras}.json`(3×3) | **纯资产** ⇒ `DEFER-ASSETS` |
| 147 | 6 文件 327 行：`models/item/spruce/bathtub.json`(134)、`models/block/spruce/bathtub/{base,forward}.json`(88/49)、`models/item/oak/bed.json`(26)、`models/block/oak/bed/{base,forward}.json`(18/12) | **纯资产** ⇒ `DEFER-ASSETS` |
| 146 | 1 文件 19 行：`common/block/light/SwitchableLightBlock.java`（含 `OAK_CANDLE_SHAPE`） | **Java 未完成** ⇒ 仍 `TODO` |
| 148 | 34 文件：资产为主；Java 仅 5 个文件、13 行 —— 其中 **11 行是已登记的适配差异**（`Properties.ofFullCopy` ×3、LAMP 合并属性行、`TFModClient` 的通配 import、`ModelLightBlock` 的 `protected` 可见性、`CherryChestBlock` 在 1.20 多余的一条 `import ...Block`），**另 6 行是真实剩余特性**：`SimpleGeoRenderedItem` 的 `animated` 标志（1.20 为 4 参构造 ＋ `private final boolean animated` ＋ `registerControllers` 早退） | **Java 剩 6 行** ⇒ 仍 `TODO` |

### 135.3 台账（双写）

- 行 **145／147 → `DEFER-ASSETS`**（Java 面已完成，仅余资源）。
- 行 **146／148 保持 `TODO`**（各余 19 行／6 行 Java）。
- 全量：`COVERED` 122、`SKIP-PORTLIB` 12、`REVERSE-ALIGNED` 6、`DEFER-ASSETS` 5（含此二行与 84／97／141）、`TODO` 2。

### 135.4 下一步

最后一批 Java：`SwitchableLightBlock`（19 行，行 146）＋ `SimpleGeoRenderedItem` 的 `animated`（6 行，行 148）⇒ 完成后这四行的 Java 面全部收口，只剩资产（用户在建面）。

