# 1.20.1 → 1.21.1 逐提交移植台账

- 范围：`795ac9ccc..07c2ab5b5`（1.20 侧 `forge-dev/1.20.1`；`07c2ab5b5` = 2026-10-03 rebase 后的远端 HEAD）
- 提交数：**400**（已排除 merge；含改名的提交 41 个，改名/复制事件 612 处）；行 387–400 为 2026-10-03 远端 rebase 后的新增段（387–396 为上游新提交，397–400 为本会话在 1.20 侧做的反向对齐提交）
- 顺序即移植顺序（**从分叉点向新，时间正序**）；请从上往下推进，填最后一列状态。

状态取值：`TODO` / `PORTED`（已按 1.21 原生改写并提交）/ `COVERED`（1.21 侧已有等价实现，含机器判定）/  `SKIP-PLATFORM`（1.21 已有的平台性改动）/ `SKIP-1.21-KEEPS`（integration：1.20 刻意删、1.21 保留）/  `SKIP-PORTLIB`（整提交只动 PortLib——1.20 专有的 1.21.1 API 模拟层，1.21 侧不需要）/  `SKIP-1.20-REVERTED`（改动文件在 1.20 HEAD 已不存在，1.20 自己后来删了）/  `MOVED`（对应到 1.21 侧的另一路径）/ `DEFER-ASSETS`（资源类，随所属功能提交一起处理）/  `DEFER-ARCH`（架构级搬迁，归 Phase 1 收割 / Phase 3 模块对齐）/ `DO-NOT-PORT`（污染提交，永不移植）/  `LOST?`（判定为无需移植但不确定，需复核）/ `REVERSE-ALIGNED`（1.20 侧反向对齐 1.21 的提交，不作为移植源）

## 概况

| 类别 | 提交数 |
|---|---|
| content | 180 |
| content+submodule | 93 |
| submodule-only | 52 |
| content+submodule+integration | 23 |
| assets/other | 17 |
| content+integration | 9 |
| assets+submodule | 9 |
| platform | 2 |
| assets/other+integration | 1 |

### 可直接跳过的行（Q12：integration 不移植）

- `integration-only`：**0** 个提交（整提交只动集成，1.21 侧一律不动）
- 含集成改动的提交：**33** 个（其余改动仍需按循环判定）
- 当前状态文件：`port-ledger-status.json`（已填 115 条；状态取值见本文件顶部说明）

| 已填状态 | 条数 |
|---|---|
| SKIP-PLATFORM | 48 |
| DEFER-ASSETS | 28 |
| COVERED | 21 |
| SKIP-PORTLIB | 8 |
| SKIP-1.20-REVERTED | 6 |
| DEFER-ARCH | 2 |
| SKIP-1.21-KEEPS | 1 |
| DO-NOT-PORT | 1 |


### 污染残留警告（来源提交 `dfcc5c041`）

- 残留清单：992 个文件（`notes/poison-residue-files.txt`）
- **142 个提交触碰了残留文件**（表内「污染残留」列给出数量）
- 这些提交的内容**不得照抄 1.20 的当前实现**：那批改动大部分是错的、部分被回退、部分残留仍在影响逻辑；
  遇到可疑处优先以 **1.21 侧现有实现**为准（1.21 分支没经历过这次事故）。详见 `notes/POISON-dfcc5c041.md`。

| 提交 | 日期 | 说明 | 残留文件数 |
|---|---|---|---|
| `dfcc5c041` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | 758 |
| `446c689a4` | 2026-09-07 | 修复成就json 修复剑的属性定义问题 修复雀杖的雀在常规状态下抽风 修复召唤物小雪怪移 | 102 |
| `41c27595c` | 2026-06-12 | feat: 合并 TerraGuns 模块并迁移至 PortLib API | 52 |
| `911437e03` | 2026-07-04 | able to start game | 49 |
| `74a1fc885` | 2026-06-14 | 移除BOM | 43 |
| `9bc04295b` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | 28 |
| `1284d08a9` | 2026-07-04 | able to into world | 27 |
| `84b1939df` | 2026-09-08 | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 | 24 |
| `4ef159bf9` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | 22 |
| `3b8f62076` | 2026-08-17 | refactor: 清理既有逻辑中的迁移噪音 | 21 |
| `417d561ec` | 2026-09-18 | 使用neoforge风味的网络包注册与发送 | 20 |
| `00b72167d` | 2026-06-10 | 枪械合并 | 19 |
| `c0c6a321d` | 2026-06-28 | part24 | 19 |
| `f6b8f73b0` | 2026-06-22 | part20 | 18 |
| `3e7cc41a9` | 2026-09-06 | Boss 血条 - 新增 CustomBossBarRenderer，使用边框和填充纹理 | 18 |


## 子模块指针跳变统计

> 主仓库这侧只看到指针跳变时，**真正的代码改动在子模块仓库里**。
> 移植这类提交必须去子模块仓库按 `旧SHA..新SHA` 找出对应的子模块提交。
> PortLib 的跳变按 Q2（1.21 不引入 PortLib）**不移植**，属噪音。

| 子模块 | 被跳变的提交数 |
|---|---|
| `PortLib` | 115 |
| `Confluence-Magic-Lib` | 98 |
| `TerraCurio` | 87 |
| `TerraFurniture` | 35 |
| `TerraGuns` | 6 |
| `TerraEntity` | 5 |

## 类的位置变动（模块内）

| 移动族 | 次数 |
|---|---|
| `ConfluenceOtherworld/src/main/resources/data/confluence/advancement/achievements  →  ConfluenceOtherworld/src/main/resources/data/confluence/advancements/achievements` | 136 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal/behavior/leaf  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal/behavior/leaf` | 24 |
| `ConfluenceOtherworld/src/main/resources/data/confluence/structure/sky_village  →  ConfluenceOtherworld/src/main/resources/data/confluence/structures/sky_village` | 24 |
| `ConfluenceOtherworld/src/main/resources/data/create/recipe/crushing  →  ConfluenceOtherworld/src/main/resources/data/confluence_create/recipes/crushing` | 24 |
| `ConfluenceOtherworld/src/main/resources/data/create/recipe/milling  →  ConfluenceOtherworld/src/main/resources/data/confluence_create/recipes/milling` | 22 |
| `ConfluenceOtherworld/src/main/resources/data/confluence/structure/shimmer_lake/trees  →  ConfluenceOtherworld/src/main/resources/data/confluence/structures/shimmer_lake/trees` | 21 |
| `ConfluenceOtherworld/src/main/resources/data/confluence_magic_lib/structure/simple_template/dungeon  →  ConfluenceOtherworld/src/main/resources/data/confluence_magic_lib/structures/simple_template/dungeon` | 19 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal` | 17 |
| `ConfluenceOtherworld/src/main/resources/data/create/recipe/mechanical_crafting  →  ConfluenceOtherworld/src/main/resources/data/confluence_create/recipes/mechanical_crafting` | 17 |
| `ConfluenceOtherworld/src/main/resources/assets/confluence/textures/item/phaseblade  →  ConfluenceOtherworld/src/main/resources/assets/confluence/textures/item/phasesaber` | 16 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal/behavior/condition  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal/behavior/condition` | 14 |
| `ConfluenceOtherworld/src/main/resources/data/ae2/recipe/item_transmutation  →  ConfluenceOtherworld/src/main/resources/data/ae2/recipes/item_transmutation` | 14 |
| `ConfluenceOtherworld/src/main/resources/data/create/recipe/mixing  →  ConfluenceOtherworld/src/main/resources/data/confluence_create/recipes/mixing` | 11 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal/behavior/decoration  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal/behavior/decoration` | 8 |
| `ConfluenceOtherworld/src/main/resources/data/confluence/loot_table/blocks  →  ConfluenceOtherworld/src/main/resources/data/confluence/loot_tables/blocks` | 8 |
| `ConfluenceOtherworld/src/main/resources/assets/confluence/models/item/whip_segments  →  ConfluenceOtherworld/src/main/resources/assets/confluence/models/item/whip` | 8 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal/summon  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal/summon` | 7 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/animation  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/animation` | 7 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data/saved  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data` | 7 |
| `ConfluenceOtherworld/src/main/resources/data/confluence/structure/mine_tunnels  →  ConfluenceOtherworld/src/main/resources/data/confluence/structures/mine_tunnels` | 6 |
| `ConfluenceOtherworld/src/main/resources/data/confluence/structure/natural  →  ConfluenceOtherworld/src/main/resources/data/confluence/structures/natural` | 6 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/brain/behavior/range  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/brain/behavior/range` | 5 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/brain/sensor  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/brain/sensor` | 5 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/fsm  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/fsm` | 5 |
| `ConfluenceOtherworld/src/main/java/org/confluence/mod/common/entity/ai/goal/behavior  →  ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/goal/behavior` | 5 |

## 疑似「搬进子模块/lib」的提交（删 java + 子模块指针跳变）

> 这类在主仓库里看不出 rename：真正的另一半提交在子模块仓库（Confluence-Magic-Lib 等）里，
> 移植时要到子模块仓库按同期提交去找对应的新增文件。

| 提交 | 日期 | 说明 | 删掉的 java | 子模块 |
|---|---|---|---|---|
| `dbac6afed` | 2026-06-04 | part | 98 | Confluence-Magic-Lib, TerraCurio, TerraEntity, TerraFurniture, TerraGuns |
| `4b004c160` | 2026-06-10 | fix2 | 1 | Confluence-Magic-Lib, PortLib |
| `45fefd336` | 2026-06-10 | datamap datagen | 1 | PortLib, TerraCurio |
| `00b72167d` | 2026-06-10 | 枪械合并 | 2 | Confluence-Magic-Lib |
| `41c27595c` | 2026-06-12 | feat: 合并 TerraGuns 模块并迁移至 PortLib API | 2 | PortLib |
| `182149f52` | 2026-06-13 | part10 | 1 | Confluence-Magic-Lib, PortLib |
| `b3f13d405` | 2026-06-13 | part11 | 3 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `b0716f0c9` | 2026-06-14 | part13 | 2 | Confluence-Magic-Lib, PortLib |
| `a779580be` | 2026-06-14 | part16 | 1 | PortLib |
| `f30688d17` | 2026-06-15 | update: 将多个方块迁移至 PortLib API 并调整方法签名 | 3 | PortLib |
| `093eda09f` | 2026-06-16 | part17 | 3 | Confluence-Magic-Lib, PortLib |
| `10705abc7` | 2026-06-21 | part19 | 3 | Confluence-Magic-Lib, PortLib, TerraCurio, TerraFurniture |
| `2569be361` | 2026-06-27 | part22 | 1 | Confluence-Magic-Lib |
| `c0c6a321d` | 2026-06-28 | part24 | 1 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `1516cbd2f` | 2026-06-30 | part fluid type | 1 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `8ce7f4d7f` | 2026-07-01 | part recipe datagen | 2 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `9a48d8619` | 2026-07-01 | part npc1 | 16 | PortLib |
| `911437e03` | 2026-07-04 | able to start game | 71 | Confluence-Magic-Lib, PortLib, TerraCurio, TerraFurniture |
| `1284d08a9` | 2026-07-04 | able to into world | 1 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `dfcc5c041` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | 15 | Confluence-Magic-Lib, PortLib |
| `9bc04295b` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | 4 | Confluence-Magic-Lib |
| `1c012ccb1` | 2026-08-22 | 将饰品的药水效果转移至lib | 1 | Confluence-Magic-Lib, PortLib, TerraCurio |
| `100f6e0f2` | 2026-08-22 | 可开关的药水效果移到lib | 10 | Confluence-Magic-Lib, TerraCurio |
| `2a4dfce2c` | 2026-08-22 | 同步粒子 | 2 | Confluence-Magic-Lib, TerraCurio |
| `29c1459cf` | 2026-09-02 | 删除一些Extension类 | 1 | Confluence-Magic-Lib, PortLib, TerraCurio, TerraFurniture |
| `7b3b5c28e` | 2026-09-03 | 渔夫任务系统修改 | 4 | Confluence-Magic-Lib, PortLib, TerraFurniture |
| `647400f44` | 2026-09-06 | 封印魂 | 1 | TerraCurio |
| `741f98d1e` | 2026-09-06 | feat(otherworld): 完善生物行为、NPC交易与配置界面 | 1 | PortLib |
| `84b1939df` | 2026-09-08 | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 | 1 | Confluence-Magic-Lib |
| `38382758a` | 2026-09-09 | fix(otherworld): 修复召唤物行为、蜗牛爬行与武器逻辑 | 3 | PortLib |
| `d599373c3` | 2026-09-13 | 修复8月15日遗留问题 | 2 | PortLib |
| `b05c8dc3f` | 2026-09-16 | feat: 扩充生物与事件内容，完善召唤和鞭子系统并修复战斗与渲染问题 | 9 | PortLib |
| `c5b035680` | 2026-09-19 | 移除动态光源至MagicLib | 2 | Confluence-Magic-Lib |
| `e145cafb5` | 2026-09-20 | 动态群系修改与client tick事件大一统 | 12 | Confluence-Magic-Lib, PortLib, TerraFurniture |
| `81488b6d0` | 2026-09-20 | 修改一股味的代码 | 6 | Confluence-Magic-Lib, PortLib |
| `ef1a4d138` | 2026-09-22 | feat: 重构肉山肉墙与悠悠球实现，更新 NPC 交互界面并统一敌怪射弹伤害 | 1 | Confluence-Magic-Lib |
| `e9501cf56` | 2026-09-23 | fall_damage_multiplier属性不再导致摔落声音 | 1 | PortLib, TerraCurio |

## 提交清单

| # | 提交 | 日期 | 说明 | 模块 | 改动 | 移动 | 子模块侧提交 | 类别 | 1.21 侧对应 | 状态 |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | `dbac6afed` | 2026-06-04 | part | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio, TerraEntity, TerraFurniture, TerraGuns | +1 ~36 -99 |  | Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×3 | ⚠️ 3 | content+submodule+integration | （不动 1.21） — 删了 98 个 integration 类 + 28 处拔集成调用 + mods.toml 改名 + build.gradle 重写；1.20 刻意删集成、1.21 保留（Q12），平台文件亦不需改 | SKIP-1.21-KEEPS |
| 2 | `1d216237b` | 2026-06-06 | part2 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio, TerraEntity, TerraFurniture | +0 ~12 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 1 | content+submodule+integration | （不动 1.21） — part2 整提交是「原生写法→Port 写法」：Entity#getInBlockState() → PortEntityExtension.getInBlockState(...)（4 个文件）、ByteBufCodecs.idMapper → PortByteBufCodecs.idMapper（GamePhase）；1.21 保留原生写法即正确。另含 settings.gradle（平台）、3 个子模块指针、1 个 integration 文件（Q12 跳过） | SKIP-PLATFORM |
| 3 | `6c1a7a656` | 2026-06-07 | part3 | Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~5 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | content+submodule | （不动 1.21） — part3：① accessories.json 的 neoforge:conditions→forge:conditions 是 1.20 平台适配（1.21 保留 neoforge: ✓）；② 去掉 .get() 是 1.20 的类型差异（1.21 的 ModKeyBindings 仍是 Holder，需 .get() ✓）；③ RadioThing 加 super.onUnequip 是 1.20 lib 改动的连带（1.21 的 BaseCurioItem 没有 onUnequip 实现，且 1.21 的 onUnequip 本来就在发包）；④ 子模块侧：TerraCurio part3 = datagen 重构（删 DataGenerator/WorkshopProvider + 242 个手写数据文件，改由生成；1.21 对应物在 src/generated/resources ✓）、Magic-Lib part3 = 清理 | SKIP-PLATFORM |
| 4 | `00c759b63` | 2026-06-07 | part4 | TerraCurio | +0 ~1 -0 |  | TerraCurio×1 |  | submodule-only | （不动 1.21） — part4：TerraCurio 侧仅 1 文件 +4/-4，属 part 系列滚动（Port 化/构建接线） | SKIP-PLATFORM |
| 5 | `28e34a500` | 2026-06-07 | part5 | Confluence-Magic-Lib, TerraCurio, TerraEntity, TerraFurniture, TerraGuns | +0 ~5 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | submodule-only | （不动 1.21） — part5：子模块侧全是 build.gradle 依赖接线（slim=true→false 分支、jarJar(project(:PortLib)) 等），1.20 构建配置，1.21 有自己的一套 | SKIP-PLATFORM |
| 6 | `bf1491b2c` | 2026-06-07 | fix | TerraCurio | +0 ~1 -0 |  | TerraCurio×1 |  | submodule-only | （不动 1.21） — fix：TerraCurio 两个 mixin 细节（BowItemMixin 删空行、SlimeBlockMixin 的 @Local 调整），属 1.20 侧 mixin 层微调 | SKIP-PLATFORM |
| 7 | `48289ba27` | 2026-06-08 | part6 | Confluence-Magic-Lib, TerraCurio, TerraGuns | +0 ~3 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — part6：TerraCurio 把 common/data 包整体移到 common/datagen（15 文件）+ Magic-Lib 3 文件，属 datagen 包重组；1.21 的 datagen 布局自行成立 | SKIP-PLATFORM |
| 8 | `72adcfeae` | 2026-06-08 | part7 | Confluence-Magic-Lib, ConfluenceOtherworld, TerraEntity, TerraFurniture, TerraGuns | +0 ~10 -0 |  | Confluence-Magic-Lib×1, TerraFurniture×1 |  | content+submodule | 1.21 已是正确原生形态 — part7：改动方向是 1.20 对齐 1.21 API —— getComponents().getOrDefault()→PortItemStackExtension.getDataOrDefault()（Port 化）；actuallyRender 由 int color 改成 float r,g,b,a（对齐 1.21 geckolib 签名）；defineSynchedData(Builder)→defineSynchedData()（改回 1.20 签名）。1.21 侧本来就是 float RGBA + Builder 形态，无需动作 | COVERED |
| 9 | `85475d011` | 2026-06-09 | 泰拉生物移植前 | TerraCurio | +0 ~1 -0 |  | TerraCurio×1 |  | submodule-only | （不动 1.21） — 泰拉生物移植前：TerraCurio BowItemMixin 的 @At(target=...) 加 remap=false，属 mixin 平台细节（1.20/Forge 的 remap 要求在 1.21 侧有自己的写法） | SKIP-PLATFORM |
| 10 | `f4b42537c` | 2026-06-10 | part8 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio, TerraEntity | +6 ~84 -3 |  | Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 4 | content+submodule |  | TODO |
| 11 | `e7b826680` | 2026-06-10 | part9 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraGuns | +7 ~122 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 11 | content+submodule+integration |  | COVERED |
| 12 | `4b004c160` | 2026-06-10 | fix2 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +2 ~29 -1 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 4 | content+submodule |  | COVERED |
| 13 | `45fefd336` | 2026-06-10 | datamap datagen | ConfluenceOtherworld, PortLib, TerraCurio | +0 ~6 -1 |  | PortLib×1, TerraCurio×1 |  | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port41/-Port0），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 14 | `395003423` | 2026-06-10 | fix3 | Confluence-Magic-Lib, TerraCurio | +0 ~2 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | submodule-only |  | TODO |
| 15 | `00b72167d` | 2026-06-10 | 枪械合并 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld | +80 ~216 -2 |  | Confluence-Magic-Lib×1 | ⚠️ 19 | content+submodule |  | TODO |
| 16 | `d194600b9` | 2026-06-12 | remove: 删除 TerraGuns 子模块 | TerraGuns | +0 ~0 -1 |  |  |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，且子模块侧无可枚举改动（对象不可得/无净改动） | SKIP-PLATFORM |
| 17 | `57d824a9d` | 2026-06-12 | remove: 删除 TerraGuns 子模块配置 | (repo-root) | +0 ~1 -0 |  |  |  | platform | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 18 | `41c27595c` | 2026-06-12 | feat: 合并 TerraGuns 模块并迁移至 PortLib API | ConfluenceOtherworld, PortLib | +0 ~513 -2 | `GunRenderTypes.java`→`ModRenderTypes.java` | PortLib×1 | ⚠️ 52 | content+submodule+integration | 1.21 已存在 — 机器判定：新增 12854 行中 10885 行（85%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 19 | `391e750d9` | 2026-06-12 | update: 将 GameEvent、LucyTheAxeDialogCategory 和 Team 中的 StreamCodec 替换为 PortLib 版本 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port10/-Port0），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 20 | `cb558bcf6` | 2026-06-13 | 调整PortEnchantmentHelper | ConfluenceOtherworld, PortLib, TerraCurio | +0 ~8 -0 |  | PortLib×1, TerraCurio×1 | ⚠️ 2 | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port19/-Port19），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 21 | `182149f52` | 2026-06-13 | part10 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +1 ~100 -1 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 2 | content+submodule+integration |  | TODO |
| 22 | `b3f13d405` | 2026-06-13 | part11 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +2 ~48 -3 | `AmmoDataContext.java`→`AmmoDataContext.java` 等2处 | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 4 | content+submodule |  | TODO |
| 23 | `7d1fff5b6` | 2026-06-13 | part12 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~98 -0 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 6 | content+submodule |  | TODO |
| 24 | `b0716f0c9` | 2026-06-14 | part13 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +2 ~54 -2 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 2 | content+submodule+integration |  | TODO |
| 25 | `0b38e2e69` | 2026-06-14 | part14 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +1 ~58 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 5 | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port2149/-Port30），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 26 | `c85213f17` | 2026-06-14 | part15 | ConfluenceOtherworld | +0 ~24 -0 |  |  | ⚠️ 3 | content | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port518/-Port0），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 27 | `13a467d59` | 2026-06-14 | enum extend | ConfluenceOtherworld | +2 ~10 -1 |  |  |  | content | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port114/-Port28），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 28 | `74a1fc885` | 2026-06-14 | 移除BOM | (repo-root), ConfluenceOtherworld | +0 ~275 -1 |  |  | ⚠️ 43 | content+integration | 1.21 已存在 — 机器判定：新增 260 行中 259 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 29 | `15afa497b` | 2026-06-14 | fix magic mirror | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~6 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 1 | content+submodule |  | TODO |
| 30 | `a779580be` | 2026-06-14 | part16 | ConfluenceOtherworld, PortLib | +0 ~82 -1 |  | PortLib×1 | ⚠️ 7 | content+submodule |  | TODO |
| 31 | `a8fc8c2c6` | 2026-06-15 | 语法降级 | ConfluenceOtherworld | +0 ~28 -0 |  |  | ⚠️ 1 | content+integration |  | TODO |
| 32 | `f30688d17` | 2026-06-15 | update: 将多个方块迁移至 PortLib API 并调整方法签名 | ConfluenceOtherworld, PortLib | +2 ~173 -3 | `SimpleGeoItemRenderer.java`→`SimpleGeoItemRenderer.java` | PortLib×1 | ⚠️ 7 | content+submodule |  | TODO |
| 33 | `7646c5505` | 2026-06-15 | 物品移植 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +4 ~76 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 3 | content+submodule |  | TODO |
| 34 | `2efa17a52` | 2026-06-16 | rollback1 | ConfluenceOtherworld | +2 ~32 -0 |  |  |  | content | 1.21 已存在 — 机器判定：新增 10289 行中 10198 行（99%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 35 | `093eda09f` | 2026-06-16 | part17 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +2 ~45 -3 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 6 | content+submodule+integration |  | TODO |
| 36 | `17af6914e` | 2026-06-16 | 一点点粒子 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | TODO |
| 37 | `fbcb8e783` | 2026-06-16 | fix crash | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~23 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | TODO |
| 38 | `8bcc392be` | 2026-06-16 | mob effect | ConfluenceOtherworld, PortLib | +0 ~17 -0 |  | PortLib×1 |  | content+submodule |  | TODO |
| 39 | `5c56e83b2` | 2026-06-17 | something | ConfluenceOtherworld, TerraCurio | +0 ~3 -0 |  | TerraCurio×1 |  | content+submodule | 1.21 已存在 — 机器判定：新增 3 行中 3 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 40 | `5481344ca` | 2026-06-18 | something2 | Confluence-Magic-Lib, PortLib, TerraCurio | +0 ~3 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | submodule-only |  | TODO |
| 41 | `b33c206fa` | 2026-06-18 | something3 | (repo-root), ConfluenceOtherworld | +29 ~19 -2 | `BeeArrow.java`→`BeeArrowEntity.java` 等3处 |  |  | content+integration |  | TODO |
| 42 | `f7996a657` | 2026-06-18 | rename | ConfluenceOtherworld | +1 ~55 -0 | `DriveAwayArrow.java`→`DriveAwayArrowEntity.java` |  | ⚠️ 8 | content+integration |  | TODO |
| 43 | `4dcf95cfe` | 2026-06-18 | 移植家具 | (repo-root), ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +2 ~11 -0 |  | PortLib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule |  | TODO |
| 44 | `1e77c5edc` | 2026-06-18 | remove BOM | ConfluenceOtherworld, TerraCurio | +0 ~15 -0 |  | TerraCurio×1 |  | content+submodule | 1.21 已存在 — 机器判定：新增 14 行中 14 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 45 | `c5e9f9be5` | 2026-06-19 | part18 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~37 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | TODO |
| 46 | `10705abc7` | 2026-06-21 | part19 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~68 -3 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 7 | content+submodule |  | TODO |
| 47 | `d17dc7c9a` | 2026-06-21 | fix crash | (repo-root), ConfluenceOtherworld, PortLib, TerraCurio | +0 ~4 -0 |  | PortLib×1, TerraCurio×1 |  | content+submodule |  | TODO |
| 48 | `4ab2d42ad` | 2026-06-21 | fix crash | ConfluenceOtherworld, PortLib, TerraCurio | +0 ~5 -0 |  | PortLib×1, TerraCurio×1 |  | content+submodule |  | TODO |
| 49 | `c9f3af990` | 2026-06-21 | extensions | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +3 ~121 -0 | `GameClientEvents.java`→`GameClientEvents.java.bak` | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 10 | content+submodule |  | TODO |
| 50 | `c7e4f8fab` | 2026-06-21 | extensions2 | (repo-root), PortLib, TerraCurio | +1 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 51 | `f6b8f73b0` | 2026-06-22 | part20 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +8 ~133 -0 | `ModEntities.java`→`ModEntities.java` | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 18 | content+submodule+integration |  | TODO |
| 52 | `dffefea8d` | 2026-06-23 | feat: 添加多个怪物实体类、AI系统和相关工具类 | ConfluenceOtherworld | +156 ~45 -0 |  |  | ⚠️ 8 | content |  | TODO |
| 53 | `0e370c928` | 2026-06-24 | data component method rename | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~29 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule |  | TODO |
| 54 | `4298126f9` | 2026-06-24 | IPortItemExtension | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +2 ~110 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 6 | content+submodule |  | TODO |
| 55 | `f6e114cdb` | 2026-06-24 | part21 | ConfluenceOtherworld, PortLib | +143 ~46 -0 |  | PortLib×1 | ⚠️ 2 | content+submodule |  | TODO |
| 56 | `2569be361` | 2026-06-27 | part22 | Confluence-Magic-Lib, ConfluenceOtherworld | +38 ~41 -1 | `TrapDamageHelper.java`→`TrapDamageHelper.java` 等137处 | Confluence-Magic-Lib×1 |  | content+submodule | （归 Phase 1/3，不逐提交移植） — part22：137 处改名（实体整体迁移到 util/entity），架构级搬迁 -> 归 Phase 1/3 | DEFER-ARCH |
| 57 | `b20c0cefd` | 2026-06-27 | remove all entity part | ConfluenceOtherworld | +0 ~4 -244 | `AmmoDataContext.java`→`AmmoDataContext.java` 等2处 |  | ⚠️ 2 | content |  | TODO |
| 58 | `7f83b379a` | 2026-06-27 | part23 | (repo-root), ConfluenceOtherworld | +56 ~13 -0 |  |  | ⚠️ 10 | content |  | TODO |
| 59 | `c0c6a321d` | 2026-06-28 | part24 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +42 ~92 -1 | `MonstersEntities.java`→`MonsterEntities.java` | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 19 | content+submodule+integration |  | TODO |
| 60 | `ac7767860` | 2026-06-28 | part25 | ConfluenceOtherworld | +0 ~2 -0 |  |  | ⚠️ 1 | content |  | TODO |
| 61 | `058000c5c` | 2026-06-29 | part enchantment | ConfluenceOtherworld | +9 ~10 -0 |  |  | ⚠️ 2 | content |  | TODO |
| 62 | `1516cbd2f` | 2026-06-30 | part fluid type | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~22 -1 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 3 | content+submodule |  | TODO |
| 63 | `231c505ca` | 2026-06-30 | part npc | (repo-root), ConfluenceOtherworld | +30 ~13 -1 |  |  |  | content+integration |  | TODO |
| 64 | `6568d3ad1` | 2026-06-30 | part critters & monsters | ConfluenceOtherworld | +37 ~9 -1 |  |  | ⚠️ 7 | content |  | TODO |
| 65 | `8ce7f4d7f` | 2026-07-01 | part recipe datagen | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~24 -2 | `CrimsonMimic.java`→`BaseMimic.java` | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | content+submodule |  | TODO |
| 66 | `9a48d8619` | 2026-07-01 | part npc1 | (repo-root), ConfluenceOtherworld, PortLib | +22 ~50 -16 |  | PortLib×1 | ⚠️ 2 | content+submodule+integration |  | TODO |
| 67 | `fac72523a` | 2026-07-02 | part critters | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +51 ~39 -0 | `Fairy.java`→`Dragonfly.java` 等5处 | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 14 | content+submodule+integration |  | PORTED |
| 68 | `911437e03` | 2026-07-04 | able to start game | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +3 ~351 -71 | `RegistryAwareItemModelShaperMixin.java`→`ForgeItemModelShaperMixin.java` 等2处 | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 49 | content+submodule+integration |  | PORTED |
| 69 | `1284d08a9` | 2026-07-04 | able to into world | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +816 ~76 -2 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 27 | content+submodule+integration |  | PORTED |
| 70 | `c8e4e6416` | 2026-07-05 | fix IncompatibleClassChangeError | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | content |  | TODO |
| 71 | `cf4b3d201` | 2026-07-05 | fix IncompatibleClassChangeError | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （无代码改动） — 该提交在两侧都没有 java 改动（改动落在 PortLib 子模块指针与构建文件上），无源码可移植。依据：notes/COMMIT-LAG.md（NO-JAVA） | DEFER-ASSETS |
| 72 | `0a575062b` | 2026-07-06 | docs: 修正PointedDripstoneBlockMixin中@ModifyVariable注释 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +2/-1（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 73 | `150b32bb4` | 2026-07-06 | docs: 修正PointedDripstoneBlockMixin中@ModifyVariable注释 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +2/-1（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 74 | `97fc3ed2e` | 2026-07-08 | npc goal | ConfluenceOtherworld | +0 ~11 -0 |  |  |  | content |  | TODO |
| 75 | `63916265b` | 2026-07-08 | feat: 新增实体并修复命名空间引用 | ConfluenceOtherworld | +7 ~31 -0 |  |  | ⚠️ 9 | content |  | PORTED |
| 76 | `bd006659b` | 2026-07-11 | refactor: 重命名饿鬼实体类为HillHungry | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | TODO |
| 77 | `5dac72b22` | 2026-07-11 | 改coremod | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +8/-3（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 78 | `6ffa7c374` | 2026-07-11 | refactor: 重命名PortPlayerEvent内部类引用 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +2/-2（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 79 | `f9332ca42` | 2026-07-12 | refactor: 修正PortLib插入脚本中的方法调用指令 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +2/-2（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 80 | `d4d5d0fe1` | 2026-07-22 | 修崩溃 | PortLib, TerraCurio | +0 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 8 文件 +21/-16（PortLib,TerraCurio），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 81 | `c378879f4` | 2026-07-22 | chore: 将所有子模块远程切换为 SSH | (repo-root) | +0 ~1 -0 |  |  |  | platform | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 82 | `4b9200a0f` | 2026-07-22 | ssh doc | (repo-root) | +1 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 83 | `09601301f` | 2026-07-25 | fix | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +6/-1（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 84 | `a6f8f089d` | 2026-07-25 | fix world selection | ConfluenceOtherworld, PortLib | +0 ~51 -0 |  | PortLib×1 | ⚠️ 13 | content+submodule |  | TODO |
| 85 | `0fa73dd79` | 2026-07-29 | truly fix | PortLib, TerraCurio | +0 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 2 文件 +5/-10（PortLib,TerraCurio），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 86 | `77f3b047e` | 2026-07-29 | feat: 新增步高度属性并完善跨版本桥接功能 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 36 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 87 | `b375ad22b` | 2026-07-29 | refactor: 优化网络包系统并完善跨版本桥接 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 8 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 88 | `f02d012ad` | 2026-07-29 | 整理 | PortLib, TerraCurio | +0 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — 该提交改动的 28 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 89 | `bf3b4f26e` | 2026-08-04 | 修吃东西崩溃 无法正常返还物品 堆叠数 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 9 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 90 | `6c62927b5` | 2026-08-04 | 不懂 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +4/-1（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 91 | `83674802b` | 2026-08-07 | portlib v1.0.0 | PortLib, TerraCurio | +0 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — 该提交改动的 7 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 92 | `80378e047` | 2026-08-07 | TerraCurio依赖 | PortLib, TerraCurio | +0 ~2 -0 |  | PortLib×1, TerraCurio×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 2 文件 +8/-1（PortLib,TerraCurio），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 93 | `bad95470c` | 2026-08-08 | 同步1.21.1的修改 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~6 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 1 | content+submodule |  | TODO |
| 94 | `4fa865322` | 2026-08-11 | 修复重生事件逻辑 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 5 文件 +45/-34（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 95 | `dfcc5c041` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +649 ~712 -95 | `a_rare_realm.json`→`a_rare_realm.json` 等234处 | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 758 | content+submodule+integration | （永不移植） — 作者当面提醒：改动范围极大（1690 文件 / +105554 −20918）、大部分内容错误，部分已被 16 个回滚提交撤回（最大一次 dc57ba5c2 覆盖 663 文件），部分残留仍在影响正确逻辑（残留 992 文件，其中 java 172）。详见 notes/POISON-dfcc5c041.md 与 notes/poison-residue-files.txt | DO-NOT-PORT |
| 96 | `dc57ba5c2` | 2026-08-16 | 注释 杀杀杀 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~663 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule+integration |  | DO-NOT-PORT |
| 97 | `4af532ed1` | 2026-08-16 | `refactor: 回退错误公共架构并恢复 1.20 实现` | ConfluenceOtherworld | +0 ~89 -9 |  |  |  | content |  | TODO |
| 98 | `1e0393178` | 2026-08-16 | `refactor: 回退错误公共架构并恢复 1.20 实现` | ConfluenceOtherworld | +6 ~0 -0 |  |  |  | content |  | TODO |
| 99 | `c406dcc0b` | 2026-08-16 | fix: 对齐城镇 NPC 敌我识别与恐慌行为 | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | content |  | TODO |
| 100 | `1e2f65769` | 2026-08-16 | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | content |  | TODO |
| 101 | `188ade36f` | 2026-08-16 | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 | ConfluenceOtherworld | +1 ~0 -0 |  |  |  | content | （不动 1.21） — 该提交改动的 1 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md | SKIP-1.20-REVERTED |
| 102 | `116edf192` | 2026-08-16 | fix: 对齐 NPC 住宅与旅商生命周期行为 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | TODO |
| 103 | `fe090753f` | 2026-08-16 | fix: 对齐 NPC 交互与对话同步行为 | ConfluenceOtherworld | +0 ~7 -0 |  |  |  | content |  | TODO |
| 104 | `1c40b0ecc` | 2026-08-16 | fix: 补充 NPC 交互事件 | ConfluenceOtherworld | +1 ~0 -0 |  |  |  | content | （不动 1.21） — 该提交改动的 1 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md | SKIP-1.20-REVERTED |
| 105 | `c6c2d493a` | 2026-08-16 | fix: 恢复 NPC 商店三态交易流程 | ConfluenceOtherworld | +0 ~2 -1 |  |  |  | content |  | TODO |
| 106 | `7e8662bd4` | 2026-08-16 | refactor: 恢复 NPC 商品的组件定价模型 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | TODO |
| 107 | `fd6f0910b` | 2026-08-16 | 调整逻辑 | TerraCurio | +0 ~1 -0 |  | TerraCurio×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 3 文件 +6/-34（TerraCurio），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 108 | `b861536ae` | 2026-08-16 | refactor: 恢复 NPC 商店数据加载架构 | ConfluenceOtherworld | +0 ~4 -2 |  |  |  | content |  | TODO |
| 109 | `7a3e9f664` | 2026-08-16 | fix: 同步 NPC 商店权威价格显示 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | TODO |
| 110 | `e7703e76d` | 2026-08-16 | fix: 保证 NPC 商店报价与交易条件一致 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | TODO |
| 111 | `c2d92b019` | 2026-08-16 | refactor: 按 Servantry 架构对齐召唤系统 | ConfluenceOtherworld | +0 ~24 -0 |  |  |  | content |  | SKIP-1.20-REVERTED |
| 112 | `9729f1c30` | 2026-08-16 | refactor: 按 Servantry 架构对齐召唤系统 | ConfluenceOtherworld | +1 ~0 -0 |  |  |  | content | （不动 1.21） — 该提交改动的 1 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md | SKIP-1.20-REVERTED |
| 113 | `fc43577f2` | 2026-08-16 | style: 清理移植改动中的无意义格式变更 | ConfluenceOtherworld | +0 ~29 -0 |  |  | ⚠️ 4 | content |  | SKIP-PLATFORM |
| 114 | `24322f45a` | 2026-08-16 | refactor: 恢复 NPC 对话管理器原有架构 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | COVERED |
| 115 | `aee0a94ca` | 2026-08-16 | refactor: 恢复 NPC 原有目标选择架构 | ConfluenceOtherworld | +0 ~6 -0 |  |  |  | content |  | COVERED |
| 116 | `6ec97fb52` | 2026-08-16 | refactor: 恢复 NPC 原有交互架构 | ConfluenceOtherworld | +0 ~3 -1 |  |  |  | content |  | SKIP-1.20-REVERTED |
| 117 | `27e9e0ac3` | 2026-08-16 | refactor: 清理 NPC 对齐残留并恢复原有架构 | ConfluenceOtherworld | +0 ~13 -1 |  |  |  | content |  | COVERED |
| 118 | `a8c5395a6` | 2026-08-16 | refactor: 清理跨版本移植残留与格式噪音 | ConfluenceOtherworld | +0 ~8 -4 |  |  |  | content |  | SKIP-PLATFORM |
| 119 | `b2d1d939c` | 2026-08-16 | fix: 对齐 NPC 战斗治疗与生命周期行为 | ConfluenceOtherworld | +0 ~11 -0 |  |  |  | content |  | COVERED |
| 120 | `e1cdbb3ff` | 2026-08-16 | feat: 接通 NPC 交互与聊天同步链路 | ConfluenceOtherworld | +1 ~4 -0 |  |  |  | content |  | COVERED |
| 121 | `d742dcf44` | 2026-08-16 | feat: 补齐 NPC 默认聊天内容与触发条件 | ConfluenceOtherworld | +4 ~2 -0 |  |  |  | content |  | COVERED |
| 122 | `159af78f9` | 2026-08-16 | fix: 修复内置 NPC 商店资源加载 | ConfluenceOtherworld | +19 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（19 个文件，无 java），随所属功能提交一起处理 | SKIP-1.20-REVERTED |
| 123 | `40c630e6b` | 2026-08-16 | 石 | ConfluenceOtherworld | +0 ~0 -19 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（19 个文件，无 java），随所属功能提交一起处理 | SKIP-1.20-REVERTED |
| 124 | `64950f063` | 2026-08-16 | fix: 修正 NPC 商店生成内容与商品价值 | ConfluenceOtherworld | +0 ~2 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 125 | `2a9014345` | 2026-08-16 | fix: 对齐 NPC 固定商店商品与出售条件 | ConfluenceOtherworld | +0 ~2 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 126 | `74346c9d2` | 2026-08-16 | fix: 清理错误与无效的 NPC 固定商店入口 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 127 | `9e00dbf7b` | 2026-08-16 | fix: 拉通 NPC 商店条件与交易结算 | ConfluenceOtherworld | +5 ~8 -0 |  |  |  | content |  | COVERED |
| 128 | `a640087ea` | 2026-08-16 | fix: 拉通 NPC 商店条件与交易结算 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | SKIP-1.21-KEEPS |
| 129 | `7d6b90edc` | 2026-08-16 | fix: 拉通 NPC 住宅交互与生命周期行为 | ConfluenceOtherworld | +1 ~10 -0 |  |  |  | content |  | COVERED |
| 130 | `d0fe20495` | 2026-08-16 | refactor: 将 NPC 默认聊天数据迁入 datagen | ConfluenceOtherworld | +1 ~1 -1 |  |  |  | content |  | COVERED |
| 131 | `5bd5b4211` | 2026-08-17 | fix: 对齐普通生物自然生成规则 | ConfluenceOtherworld | +0 ~11 -0 |  |  |  | content |  | COVERED |
| 132 | `441334efe` | 2026-08-17 | fix: 对齐普通生物自然生成规则 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 133 | `f4625b5c4` | 2026-08-17 | fix: 拉通生物行为并统一属性访问方式 | ConfluenceOtherworld | +0 ~59 -0 |  |  | ⚠️ 7 | content |  | PORTED |
| 134 | `50bd06aab` | 2026-08-17 | fix: 拉通生物行为并统一属性访问方式 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | DO-NOT-PORT |
| 135 | `a86216caf` | 2026-08-17 | fix: 拉通普通生物注册生成与属性语义 | ConfluenceOtherworld | +0 ~8 -0 |  |  |  | content |  | COVERED |
| 136 | `d6c99c740` | 2026-08-17 | fix: 拉通Boss属性部件伤害与掉落行为 | ConfluenceOtherworld | +0 ~15 -0 |  |  | ⚠️ 3 | content |  | COVERED |
| 137 | `72421955a` | 2026-08-17 | fix: 拉通召唤战斗与储物伙伴行为 | ConfluenceOtherworld | +0 ~6 -0 |  |  | ⚠️ 2 | content |  | SKIP-1.20-REVERTED |
| 138 | `7163e1d05` | 2026-08-17 | fix: 修复弹幕生命周期中断判断 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | COVERED |
| 139 | `b8c9b7a07` | 2026-08-17 | refactor: 移除废弃的子弹运行时状态架构 | ConfluenceOtherworld | +0 ~0 -1 |  |  |  | content | （不动 1.21） — 该提交改动的 1 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md 该文件 BulletRuntimeState.java 在 1.21 树里从来不存在（1.20 自己加了又删，不是 1.21 的欠账）。 | SKIP-1.20-REVERTED |
| 140 | `ab0d06315` | 2026-08-17 | fix: 拉通坐骑移动与交互行为 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | COVERED |
| 141 | `08276f2ca` | 2026-08-17 | fix: 恢复自定义矿车创建与放置行为 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | PORTED |
| 142 | `acba5480f` | 2026-08-17 | fix: 恢复钩爪使用限制与服务端校验 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | PORTED |
| 143 | `a8e0b487f` | 2026-08-17 | refactor: 清理跨版本迁移残留与测试辅助代码 | ConfluenceOtherworld | +0 ~37 -4 |  |  | ⚠️ 14 | content |  | SKIP-PLATFORM |
| 144 | `3b8f62076` | 2026-08-17 | refactor: 清理既有逻辑中的迁移噪音 | ConfluenceOtherworld | +0 ~23 -0 |  |  | ⚠️ 21 | content+integration |  | SKIP-PLATFORM |
| 145 | `7dc9244e4` | 2026-08-17 | style: 清理新增架构类中的无意义拆行 | ConfluenceOtherworld | +0 ~12 -0 |  |  |  | content |  | SKIP-1.20-REVERTED |
| 146 | `bfd32eae3` | 2026-08-17 | refactor: 清理资源迁移噪音并恢复历史命名 | ConfluenceOtherworld | +0 ~11 -0 |  |  |  | assets/other | 1.21 已存在 — 机器判定：新增 2 行中 2 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 147 | `0067364f0` | 2026-08-17 | refactor: 清理混入迁移噪音与编译警告 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | SKIP-PLATFORM |
| 148 | `472be10da` | 2026-08-17 | fix: 补全网络注册与服务端线程边界 | ConfluenceOtherworld | +0 ~6 -0 |  |  | ⚠️ 1 | content |  | SKIP-PLATFORM |
| 149 | `d3ae22457` | 2026-08-17 | fix: 完善实体渲染资源校验并清理注册噪音 | ConfluenceOtherworld | +0 ~3 -0 |  |  | ⚠️ 1 | content |  | SKIP-PLATFORM |
| 150 | `93e64b5b5` | 2026-08-17 | refactor: 清理玩家附件与组件迁移冗余 | ConfluenceOtherworld | +0 ~6 -0 |  |  |  | content |  | SKIP-PLATFORM |
| 151 | `90dfd7804` | 2026-08-18 | refactor(combat): 重构剑类、剑气与枪械系统 | ConfluenceOtherworld | +28 ~51 -12 |  |  | ⚠️ 2 | content |  | PORTED |
| 152 | `4ac81edb7` | 2026-08-18 | 药水效果（未完成） | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~11 -9 | `AbstractEnchantment.java`→`AbstractManaEnchantment.java` | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | PORTED |
| 153 | `9bc04295b` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | Confluence-Magic-Lib, ConfluenceOtherworld | +53 ~471 -4 |  | Confluence-Magic-Lib×1 | ⚠️ 28 | content+submodule |  | COVERED |
| 154 | `35424398b` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | ConfluenceOtherworld | +0 ~7 -0 |  |  |  | content |  | COVERED |
| 155 | `48c509684` | 2026-08-20 | fix(summon): 严格对齐 1.21.1 召唤体系行为与渲染 | ConfluenceOtherworld | +0 ~28 -0 |  |  |  | content | （不动 1.21） — 该提交改动的 28 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md | SKIP-1.20-REVERTED |
| 156 | `ce1ca67f8` | 2026-08-22 | refactor(confluence): 拉通 1.21 战斗、召唤与实体体系 | Confluence-Magic-Lib, ConfluenceOtherworld | +4 ~228 -0 |  | Confluence-Magic-Lib×1 | ⚠️ 8 | content+submodule |  | PORTED |
| 157 | `024a28ac7` | 2026-08-22 | fix(confluence): 补齐 NPC 交互与实体属性对齐 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | SKIP-1.20-REVERTED |
| 158 | `7cadcf9a8` | 2026-08-22 | fix(confluence): 补齐 NPC 交互与实体属性对齐 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 159 | `1c012ccb1` | 2026-08-22 | 将饰品的药水效果转移至lib | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +1 ~22 -1 | `EnvironmentLevelAccess$MatcherMixin.java`→`EnvironmentLevelAccess$MatcherMixin.java` | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule+integration |  | TODO |
| 160 | `100f6e0f2` | 2026-08-22 | 可开关的药水效果移到lib | Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +1 ~13 -10 |  | Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule+integration |  | TODO |
| 161 | `2a4dfce2c` | 2026-08-22 | 同步粒子 | Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +1 ~50 -2 |  | Confluence-Magic-Lib×4, TerraCurio×1 | ⚠️ 2 | content+submodule |  | TODO |
| 162 | `12b6877be` | 2026-08-23 | 修复枪械动画报错 | ConfluenceOtherworld | +0 ~3 -0 |  |  | ⚠️ 1 | content |  | TODO |
| 163 | `5bb8d5565` | 2026-08-23 | refactor(entity): 拉通敌怪行为、肉墙机制与客户端渲染 | ConfluenceOtherworld | +7 ~46 -0 |  |  | ⚠️ 2 | content |  | PORTED |
| 164 | `48be70c79` | 2026-08-23 | 升级粒子 | (repo-root), ConfluenceOtherworld, TerraCurio | +0 ~8 -0 |  | TerraCurio×1 | ⚠️ 1 | content+submodule | 1.21 已存在 — 机器判定：新增 9 行中 9 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 165 | `863e8bef8` | 2026-08-23 | 调整版本 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~6 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（2 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 166 | `21b060ec6` | 2026-08-23 | 饰品能力全改为datamap，修复潜行属性 | Confluence-Magic-Lib, PortLib, TerraCurio | +0 ~3 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | submodule-only |  | TODO |
| 167 | `3f2eb3be2` | 2026-08-23 | fix: 重构悠悠球系统与客户端武器输入架构 | ConfluenceOtherworld | +7 ~8 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 168 | `c2935419b` | 2026-08-23 | docs: 清理源码注释中的冗余 HTML 段落标签 | ConfluenceOtherworld | +0 ~298 -0 |  |  |  | content |  | TODO |
| 169 | `4ef159bf9` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~79 -9 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 22 | content+submodule |  | TODO |
| 170 | `9645da98c` | 2026-08-24 | feat(otherworld): 重构城镇 NPC 战斗体系并补全生物相关内容 | ConfluenceOtherworld | +12 ~42 -5 |  |  | ⚠️ 1 | content |  | LOST? |
| 171 | `4557b85fc` | 2026-08-24 | 玩家动画测试 | Confluence-Magic-Lib, TerraCurio | +0 ~2 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | submodule-only | （1.20 已撤销） — 玩家动画测试：新增行**全部**引用 Port 类型（行级可计数新增为 0）；主体是 Magic-Lib 的 integration/animation 实验代码（PlayerGeoModel/PlayerGeoAnimatable 等 6 个文件），这批文件在 1.20 HEAD 已删除（ghost 6），残留的 mixin 侧在 1.21 已 IN-SYNC（89~100%）。 | COVERED |
| 172 | `9057f178c` | 2026-08-28 | 玩家动画（未注册永夜动画） | (repo-root), Confluence-Magic-Lib, PortLib, TerraCurio | +0 ~4 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 173 | `a6fd78681` | 2026-08-30 | feat(entity): 完善普通敌怪、NPC与召唤物的行为和渲染 | Confluence-Magic-Lib, ConfluenceOtherworld | +5 ~87 -0 |  | Confluence-Magic-Lib×1 | ⚠️ 3 | content+submodule |  | COVERED |
| 174 | `92e38df06` | 2026-09-01 | feat(otherworld): 重构 Boss 战斗、蠕虫体节与属性覆盖架构 | ConfluenceOtherworld | +9 ~87 -0 |  |  | ⚠️ 3 | content |  | PORTED |
| 175 | `29c1459cf` | 2026-09-02 | 删除一些Extension类 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~91 -1 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 11 | content+submodule |  | SKIP-PLATFORM |
| 176 | `19669033d` | 2026-09-02 | feat(otherworld): 重构战斗 AI、Boss 行为与 NPC 交互系统 | ConfluenceOtherworld | +2 ~92 -1 |  |  | ⚠️ 9 | content |  | COVERED |
| 177 | `06415ab96` | 2026-09-02 | 删除AI乱改的进度系统，部分事件改用原生类 | ConfluenceOtherworld | +0 ~20 -5 |  |  | ⚠️ 3 | content |  | SKIP-PLATFORM |
| 178 | `8ca8275f6` | 2026-09-02 | 删除AI乱改的进度系统，部分事件改用原生类 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 179 | `e2807cc12` | 2026-09-03 | feat(otherworld): 完善资源生成、渔夫任务与图鉴变种体系 | ConfluenceOtherworld | +10 ~16 -0 | `boulder_bread_block.json`→`boulder_bread.json` 等136处 |  | ⚠️ 1 | content |  | PORTED |
| 180 | `7003e5824` | 2026-09-03 | 删除多余的图鉴键注册 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 181 | `f68cf4b17` | 2026-09-03 | 删除多余的图鉴键注册 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 182 | `7b3b5c28e` | 2026-09-03 | 渔夫任务系统修改 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraFurniture | +1 ~29 -4 | `DateStamp.java`→`DateStamp.java` 等2处 | PortLib×1, Confluence-Magic-Lib×1, TerraFurniture×1 | ⚠️ 2 | content+submodule |  | COVERED |
| 183 | `4122dbcf0` | 2026-09-04 | 修部分服务端报错 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~6 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | COVERED |
| 184 | `c83192cf6` | 2026-09-05 | 暂存 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 11 文件 +43/-162（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 185 | `31896204b` | 2026-09-06 | 静态方法改接口 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~86 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 6 | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port188/-Port241），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 186 | `a0148b9fa` | 2026-09-06 | 修部分接口 | ConfluenceOtherworld, PortLib | +0 ~2 -0 |  | PortLib×1 |  | content+submodule |  | COVERED |
| 187 | `0cf629c19` | 2026-09-06 | 属性静态字段注入 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~40 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 1 | content+submodule | （不动 1.21） — 机器判定：方向为 de-port（新增/删除行以 Port 引用为主，+Port33/-Port112），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 188 | `609fba732` | 2026-09-06 | 新增 IPortConfigValueExtension，提供配置值读取方法和接口转换入口 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 5 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 189 | `2e7d30d15` | 2026-09-06 | configuration task | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 11 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 190 | `ea0bb9c0f` | 2026-09-06 | 优化 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 该提交改动的 5 个 java 文件全部位于 PortLib 子模块内（这些路径在 1.20 HEAD 已随 PortLib 重构消失）。PortLib 是 1.20 专有的「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生实现（决策 Q2），无对应物可移植。依据：notes/COMMIT-LAG.md（PORTLIB-ONLY）、tools/portnative/file_lag.py | SKIP-PORTLIB |
| 191 | `841165c47` | 2026-09-06 | 删除多余内容 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~10 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule |  | SKIP-PORTLIB |
| 192 | `4ff55e760` | 2026-09-06 | 修扳手、剪线钳属性 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port4/-Port4），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 193 | `647400f44` | 2026-09-06 | 封印魂 | ConfluenceOtherworld, TerraCurio | +0 ~22 -1 | `SoulSkillClientHolder.java`→`SoulSkillClientHandler.java` | TerraCurio×1 | ⚠️ 3 | content+submodule |  | SKIP-PLATFORM |
| 194 | `3e7cc41a9` | 2026-09-06 | Boss 血条 - 新增 CustomBossBarRenderer，使用边框和填充纹理绘制自定义 Boss 血条 - 使用 Boss 注册对象建立实体类型与血条资源的映射 - 定义原版、静态和动态三种血条显示模式 - 支持显示当前生命值与最大生命值，并调整多条血条的布局间距 - 新增 ClientBossBarTracker，按 Boss 事件 UUID 保存实体类型、生命值及最大生命值 - 新增 BossBarSyncPacketS2C，提供血条数据同步和可见状态移除处理 - 添加 boss_bar_flow 顶点着色器、片元着色器及配置，实现动态填充亮度效果 - 添加史莱姆王、克苏鲁之眼、世界吞噬者、克苏鲁之脑、蜂王、骷髅王、鹿角怪、血肉墙和血肉山共十八张分层血条纹理 | ConfluenceOtherworld | +29 ~30 -247 | `CreatureDefinition.java`→`CreatureDefinition.java` 等3处 |  | ⚠️ 18 | content |  | DEFER-ARCH |
| 195 | `c458f224d` | 2026-09-06 | 处理一些胡乱改动 | Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~6 -118 |  | Confluence-Magic-Lib×1 | ⚠️ 2 | content+submodule |  | SKIP-PLATFORM |
| 196 | `741f98d1e` | 2026-09-06 | feat(otherworld): 完善生物行为、NPC交易与配置界面 | ConfluenceOtherworld, PortLib | +23 ~188 -1 |  | PortLib×1 | ⚠️ 18 | content+submodule |  | PORTED |
| 197 | `5c99e9702` | 2026-09-07 | 生产环境修复 | ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~24 -0 |  | PortLib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 1 | content+submodule+integration |  | SKIP-PLATFORM |
| 198 | `607470f04` | 2026-09-07 | 补缺失内容 | ConfluenceOtherworld | +2 ~28 -0 |  |  | ⚠️ 4 | content |  | PORTED |
| 199 | `55cc8fc6b` | 2026-09-07 | 生产环境修复 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~110 -0 | `MultiNoiseBiomeSourceMixin.java`→`MultiNoiseBiomeSourceMixin.java` 等2处 | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 7 | content+submodule |  | COVERED |
| 200 | `15a05d234` | 2026-09-07 | 泰拉饰品掉落不再能影响本体，为screen添加半透明黑色遮罩 | Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~14 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 2 | content+submodule |  | COVERED |
| 201 | `6f8d84cbe` | 2026-09-07 | 修数量合成 | Confluence-Magic-Lib, PortLib | +0 ~2 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 3 文件 +12/-4（Confluence-Magic-Lib,PortLib），属 part 系列接线/Port 化滚动 | SKIP-PORTLIB |
| 202 | `0af0c9e94` | 2026-09-07 | 修创造模式标签页搜索 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +20/-2（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PORTLIB |
| 203 | `446c689a4` | 2026-09-07 | 修复成就json 修复剑的属性定义问题 修复雀杖的雀在常规状态下抽风 修复召唤物小雪怪移速过快导致抽风 修复召唤物在释放弹幕时导致崩溃 修复恶魔眼路径抽风，有时候完全不攻击的问题 补全桌椅tag | ConfluenceOtherworld | +1 ~138 -0 |  |  | ⚠️ 102 | content |  | COVERED |
| 204 | `be6173c00` | 2026-09-07 | 修复CustomRarityItem的属性问题 | Confluence-Magic-Lib, PortLib | +0 ~2 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | submodule-only |  | SKIP-PORTLIB |
| 205 | `84b1939df` | 2026-09-08 | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 | Confluence-Magic-Lib, ConfluenceOtherworld | +11 ~193 -1 |  | Confluence-Magic-Lib×1 | ⚠️ 24 | content+submodule |  | COVERED |
| 206 | `c7dd86378` | 2026-09-08 | 修复洞穴探险高亮框偏移 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 207 | `95bb0294e` | 2026-09-08 | 修复汇流箱子打不开的问题 | ConfluenceOtherworld | +0 ~11 -0 |  |  | ⚠️ 1 | content |  | SKIP-PLATFORM |
| 208 | `5c3978b01` | 2026-09-08 | 修复汇流箱子打不开、魔法武器不能附魔、附魔文本重复的、宝石法杖没粒子的问题 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~9 -2 | `remote_icon.png`→`ranger_icon.png` 等4处 | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule |  | PORTED |
| 209 | `d24f247af` | 2026-09-08 | 修复钻石套装失效的问题 | ConfluenceOtherworld | +1 ~2 -0 |  |  |  | content+integration | 1.21 已存在 — 机器判定：新增 2 行中 2 行（100%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 210 | `b184c0a5f` | 2026-09-08 | 修复灯笼粒子往下掉的问题 | (repo-root), Confluence-Magic-Lib, TerraCurio | +0 ~3 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 211 | `046a6ad1c` | 2026-09-08 | 修复右键功能物品失效问题 | Confluence-Magic-Lib, PortLib | +0 ~2 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 5 文件 +48/-38（Confluence-Magic-Lib,PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 212 | `bedb2af6e` | 2026-09-08 | 修复微光无法自然清除的问题 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 2 文件 +11/-1（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 213 | `5ebb83523` | 2026-09-08 | 修复部分靴子没有自动上台阶功能的问题 | TerraCurio | +0 ~1 -0 |  | TerraCurio×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +1/-0（TerraCurio），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 214 | `38382758a` | 2026-09-09 | fix(otherworld): 修复召唤物行为、蜗牛爬行与武器逻辑 | ConfluenceOtherworld, PortLib | +6 ~36 -3 |  | PortLib×1 | ⚠️ 1 | content+submodule |  | SKIP-1.20-REVERTED |
| 215 | `b1884f9a6` | 2026-09-10 | 1.21.1的凝灰岩系列，以及id修复 | ConfluenceOtherworld, PortLib | +0 ~6 -0 |  | PortLib×1 |  | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port9/-Port0），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 216 | `6a24e6d1f` | 2026-09-10 | 恢复贴图 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 6 文件 +30/-0（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 217 | `7a3593600` | 2026-09-10 | 修汇流熔炉不能放燃料的问题 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraFurniture | +0 ~5 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraFurniture×1 | ⚠️ 1 | content+submodule |  | SKIP-PLATFORM |
| 218 | `d7c191964` | 2026-09-10 | 修一些资源错误 | ConfluenceOtherworld, TerraCurio | +2 ~8 -0 |  | TerraCurio×1 | ⚠️ 2 | content+submodule |  | SKIP-PLATFORM |
| 219 | `50ff9fe88` | 2026-09-10 | 修一些资源错误 | ConfluenceOtherworld | +0 ~0 -1 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 220 | `751055e45` | 2026-09-10 | 修复种子按钮位置，修复世界类型图标 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | COVERED |
| 221 | `255250cc1` | 2026-09-10 | 调参，修复一些战利品表错误，塞光剑 | ConfluenceOtherworld | +10 ~14 -8 | `phaseblade.animation.json`→`phasesaber.animation.json` |  |  | content | 1.21 已存在 — 机器判定：新增 330 行中 304 行（92%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | PORTED |
| 222 | `ea9708fb4` | 2026-09-10 | 增加发光贴图 | ConfluenceOtherworld | +16 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（16 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 223 | `aec2cd920` | 2026-09-10 | 修复mixin，修复跳跃属性 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~12 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule |  | SKIP-PLATFORM |
| 224 | `3518ab86e` | 2026-09-11 | 干掉TerraBlender | (repo-root), ConfluenceOtherworld | +14 ~16 -2 | `MultiNoiseBiomeSourceMixin.java`→`MultiNoiseBiomeSourceMixin.java` |  |  | content+integration | 1.21 已存在 — 机器判定：新增 1532 行中 1300 行（85%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 225 | `5d3cf72f6` | 2026-09-11 | 修复与Bigger Stacks的Mixin冲突 | ConfluenceOtherworld, TerraCurio | +0 ~4 -0 |  | TerraCurio×1 |  | content+submodule |  | SKIP-PORTLIB |
| 226 | `9378868e5` | 2026-09-11 | feat: 重构光剑、悠悠球与生物行为并修复多项战斗和生成问题 | ConfluenceOtherworld | +42 ~58 -2 | `Phaseblade.java`→`BasePhasebladeItem.java` 等10处 |  |  | content |  | SKIP-1.20-REVERTED |
| 227 | `8a961bc0c` | 2026-09-11 | 一些修复 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraFurniture | +1 ~18 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraFurniture×1 | ⚠️ 1 | content+submodule |  | SKIP-PLATFORM |
| 228 | `d15b521d2` | 2026-09-11 | 修复战利品表的问题 | (repo-root), ConfluenceOtherworld, PortLib | +0 ~6 -2 |  | PortLib×1 |  | content+submodule |  | SKIP-PLATFORM |
| 229 | `128469b81` | 2026-09-11 | 修复药水效果的问题 | ConfluenceOtherworld, PortLib | +0 ~3 -0 |  | PortLib×1 |  | content+submodule |  | COVERED |
| 230 | `0ceb3c251` | 2026-09-11 | portlib升级为1.2.2 | (repo-root), Confluence-Magic-Lib, PortLib, TerraCurio, TerraFurniture | +0 ~5 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 231 | `d5cdb0f89` | 2026-09-12 | fix(otherworld): 修复黄蜂寻路、悠悠球增伤与渔夫任务 | ConfluenceOtherworld | +0 ~14 -0 |  |  | ⚠️ 2 | content |  | COVERED |
| 232 | `673efd7d5` | 2026-09-12 | 调整标签 悠悠球无敌帧 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | COVERED |
| 233 | `4bd94d674` | 2026-09-12 | 关键帧多效果支持 | (repo-root), ConfluenceOtherworld | +4 ~2 -0 |  |  |  | assets/other+integration |  | PORTED |
| 234 | `7f159793d` | 2026-09-12 | 移除亚刻方法 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（3 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 235 | `a358494fa` | 2026-09-12 | 修复敌怪生成与战斗行为，完善武器渲染、NPC 广播及翻译 | ConfluenceOtherworld | +33 ~117 -0 | `blue_phasesaber.png`→`blue_phasesaber.png` 等16处 |  | ⚠️ 7 | content |  | COVERED |
| 236 | `313ecb135` | 2026-09-12 | 光剑贴图动画 | ConfluenceOtherworld | +24 ~9 -0 |  |  |  | content | 1.21 已存在 — 机器判定：新增 980 行中 799 行（82%）已在 1.21 侧存在，属「1.20 从 1.21 抄回去」的内容，无需移植；待抽查 | COVERED |
| 237 | `6084476d0` | 2026-09-12 | JEI兼容恢复 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +30 ~31 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule+integration |  | COVERED |
| 238 | `99902d986` | 2026-09-13 | 修复锁方块 | ConfluenceOtherworld, PortLib | +0 ~3 -0 |  | PortLib×1 |  | content+submodule |  | COVERED |
| 239 | `5a2553293` | 2026-09-13 | refactor(otherworld): 重构鞭子挥动流程并修复水生生物行为 | ConfluenceOtherworld | +7 ~25 -3 |  |  |  | content |  | COVERED |
| 240 | `ac457ea83` | 2026-09-13 | fix: 修复敌怪行为、施法表现、NPC交互及鞭子回收 | ConfluenceOtherworld | +4 ~50 -0 |  |  | ⚠️ 4 | content |  | PORTED |
| 241 | `91724b1ce` | 2026-09-13 | 为水生生物添加自然巡游 | ConfluenceOtherworld | +1 ~2 -0 |  |  |  | content |  | COVERED |
| 242 | `d599373c3` | 2026-09-13 | 修复8月15日遗留问题 | ConfluenceOtherworld, PortLib | +0 ~48 -3 |  | PortLib×1 | ⚠️ 9 | content+submodule |  | COVERED |
| 243 | `810fb6b14` | 2026-09-13 | 版本更新 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~7 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule | （1.21 已同步，有 1 处残留） — 版本更新：仅动 ExtraInventory.java。残留 `public ItemStack getMount(boolean dye)`（3 行新增中 2 行未命中）——1.21 有 getPet/getLightPet/getMinecart/getHook，**没有** getMount；记入 notes/PORT-RESIDUALS.md。 | COVERED |
| 244 | `a4b321499` | 2026-09-14 | 补充，修改一部分模型，删除不该存在的生物，为ai已经注册了的生物补充模型 | ConfluenceOtherworld | +19 ~19 -1 |  |  |  | content |  | COVERED |
| 245 | `7525f7829` | 2026-09-14 | 激光模型 | ConfluenceOtherworld | +5 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（5 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 246 | `aa1e02f44` | 2026-09-14 | 新铁傀儡模型 | ConfluenceOtherworld | +3 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（3 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 247 | `62d4d191a` | 2026-09-14 | 翻新已经有的模型 | ConfluenceOtherworld | +3 ~21 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 248 | `9090245f8` | 2026-09-14 | 修 | Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~3 -0 |  | Confluence-Magic-Lib×1 |  | content+submodule |  | COVERED |
| 249 | `535992233` | 2026-09-14 | 为现有新增的怪物补全点需要的东西 | ConfluenceOtherworld, TerraCurio | +4 ~10 -0 |  | TerraCurio×1 | ⚠️ 2 | content+submodule |  | PORTED |
| 250 | `55262a7f2` | 2026-09-15 | 微调 | ConfluenceOtherworld, TerraCurio | +0 ~11 -0 |  | TerraCurio×1 |  | content+submodule | （1.21 已同步） — 微调：34 行新增中 14 行未命中，全部是 ClientBiomeEffectSystem 里 `vertex(...).color(255,255,...)`（1.20 的 int RGBA）对 1.21 的 float RGBA 差异，属平台 API 差异。 | COVERED |
| 251 | `6dcb832a0` | 2026-09-15 | 修事件 | Confluence-Magic-Lib | +0 ~1 -0 |  | Confluence-Magic-Lib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +1/-1（Confluence-Magic-Lib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 252 | `870e6966a` | 2026-09-15 | 更新粒子 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~4 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（2 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 253 | `856c0047f` | 2026-09-16 | 调整末地高度 | ConfluenceOtherworld, PortLib, TerraCurio | +5 ~7 -0 |  | PortLib×1, TerraCurio×1 |  | content+submodule |  | COVERED |
| 254 | `a78e2b42d` | 2026-09-16 | extension | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~6 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule | （不动 1.21） — 机器判定：方向为 de-port（新增/删除行以 Port 引用为主，+Port0/-Port2），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 255 | `253bb8d7b` | 2026-09-16 | curios属性显示兼容 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~4 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | content+submodule |  | COVERED |
| 256 | `b05c8dc3f` | 2026-09-16 | feat: 扩充生物与事件内容，完善召唤和鞭子系统并修复战斗与渲染问题 | ConfluenceOtherworld, PortLib | +435 ~214 -26 | `amber_whip.json`→`amber_whip.json` 等8处 | PortLib×1 | ⚠️ 17 | content+submodule |  | PORTED |
| 257 | `0bd4b2052` | 2026-09-17 | 修改一些纹理和模型上的问题，挪贴图位置 | ConfluenceOtherworld, TerraCurio | +3 ~8 -0 |  | TerraCurio×1 |  | content+submodule |  | COVERED |
| 258 | `ab2707f51` | 2026-09-17 | 修改一些纹理和模型上的问题，挪贴图位置 | Confluence-Magic-Lib | +0 ~1 -0 |  | Confluence-Magic-Lib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 5 文件 +0/-0（Confluence-Magic-Lib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 259 | `d22a12fdc` | 2026-09-17 | fix(summon): 修正白虎移动、扑击命中与升级跳位 | ConfluenceOtherworld | +10 ~7 -0 |  |  |  | content | （不动 1.21） — 该提交改动的 8 个 java 文件在 1.20 HEAD 已经全部不存在——1.20 自己后来删掉/替换了这批代码（旧召唤体系被 AttachmentEntity 体系取代、子弹运行时状态架构被移除等），因此没有可移植物。依据：notes/COMMIT-LAG.md（DEAD-ONLY）、notes/FILE-LAG.md | SKIP-1.20-REVERTED |
| 260 | `75c60a5fe` | 2026-09-17 | fix(render): 修正生物薄片渲染、动画与坐骑表现 | ConfluenceOtherworld | +0 ~20 -0 | `FairyRenderer.java`→`FullbrightGeoRenderer.java` |  | ⚠️ 1 | content |  | PORTED |
| 261 | `bcecc5382` | 2026-09-17 | 新版本图鉴 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | PORTED |
| 262 | `17ca9b50f` | 2026-09-17 | fix(weapon): 调整光剑与晶光刃投掷交互及手持渲染 | ConfluenceOtherworld | +0 ~9 -2 |  |  |  | content |  | PORTED |
| 263 | `9a5d3a4a6` | 2026-09-17 | 修复portlib的注册表 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~8 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |  | content+submodule |  | COVERED |
| 264 | `389b7a760` | 2026-09-17 | 修复网络包发送 | ConfluenceOtherworld, PortLib | +0 ~2 -0 |  | PortLib×1 |  | content+submodule |  | COVERED |
| 265 | `cfe6ac100` | 2026-09-18 | 同步 | ConfluenceOtherworld | +0 ~11 -0 |  |  |  | content |  | COVERED |
| 266 | `d3c7c32ff` | 2026-09-18 | 同步 | ConfluenceOtherworld | +25 ~0 -0 |  |  |  | content |  | COVERED |
| 267 | `f4ffadaa7` | 2026-09-18 | AttachmentEntity体系迁移 | ConfluenceOtherworld | +71 ~1 -0 |  |  |  | content |  | COVERED |
| 268 | `27ee0313c` | 2026-09-18 | 无敌帧体系接入 | ConfluenceOtherworld | +0 ~11 -1 |  |  |  | content |  | COVERED |
| 269 | `01f0936ac` | 2026-09-18 | 鞭子标记体系接入 | ConfluenceOtherworld | +6 ~13 -0 |  |  |  | content |  | COVERED |
| 270 | `4a3436770` | 2026-09-18 | 修复错误 | ConfluenceOtherworld | +1 ~13 -2 | `WhipTracker.java`→`WhipMarkTracker.java` 等3处 |  |  | content |  | COVERED |
| 271 | `ac0d4a88d` | 2026-09-18 | Better Difficulty Asking Screen (from 1.21.1) | ConfluenceOtherworld | +2 ~5 -1 |  |  | ⚠️ 1 | content | （1.21 内容回流） — 标题即 Better Difficulty Asking Screen (**from 1.21.1**)：是 1.21 的内容被移植到 1.20，方向本来就相反。108 行新增中 4 行未命中，全是 1.20 侧 GuiGraphics#render/renderBackground/发包调用 的签名差异。 | COVERED |
| 272 | `641467c87` | 2026-09-18 | 迁移黄蜂召唤物至 AttachmentEntity | (repo-root), ConfluenceOtherworld | +13 ~24 -2 |  |  | ⚠️ 3 | content |  | PORTED |
| 273 | `40f02b002` | 2026-09-18 | 补个蛋 | ConfluenceOtherworld | +106 ~4 -0 |  |  |  | content |  | PORTED |
| 274 | `041c84915` | 2026-09-18 | 重命名钱币槽，生物初步调参 | (repo-root), ConfluenceOtherworld | +2 ~19 -0 | `wallet.png`→`money_trough.png` |  | ⚠️ 7 | content |  | PORTED |
| 275 | `33d89dd63` | 2026-09-18 | 黄蜂完成 | ConfluenceOtherworld | +0 ~10 -0 | `SummonerWeaponItem.java`→`SummonerWeaponItem.java` |  | ⚠️ 1 | content |  | COVERED |
| 276 | `785b151f0` | 2026-09-18 | 小鸟完成 | ConfluenceOtherworld | +7 ~10 -1 |  |  | ⚠️ 1 | content |  | COVERED |
| 277 | `417d561ec` | 2026-09-18 | 使用neoforge风味的网络包注册与发送 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio, TerraFurniture | +0 ~68 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 | ⚠️ 20 | content+submodule+integration | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port165/-Port4），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 278 | `87b93ede2` | 2026-09-18 | 合并stream codec | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~10 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | COVERED |
| 279 | `2eb9ce931` | 2026-09-19 | 动态光源接入 | ConfluenceOtherworld | +1 ~2 -0 |  |  |  | content |  | COVERED |
| 280 | `348c877a4` | 2026-09-19 | 统一生物属性与状态参数声明，修复敌怪行为、渲染及商店分页 | ConfluenceOtherworld | +1 ~69 -0 |  |  | ⚠️ 1 | content |  | PORTED |
| 281 | `300bd6cfe` | 2026-09-19 | 重构敌怪效果与属性配置，修复生物行为和动画 | ConfluenceOtherworld | +6 ~32 -10 |  |  | ⚠️ 6 | content |  | PORTED |
| 282 | `b9ccf1061` | 2026-09-19 | Worldgen fix | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content | （1.21 已同步，有 1 处语义待确认） — Worldgen fix：MineTunnelsStructure/BlockPostFeature overlap 96~100%。残留 1 行：1.20 把 `context.heightAccessor().getMinBuildHeight()` 换成 `context.chunkGenerator().getGenDepth()`；1.21 的 ChunkGenerator 仍有 getGenDepth()，但恒为正数 → `worldMinY < 0` 分支永不触发，与 1.21 取 MinBuildHeight 的行为**不同**。疑似 1.20 侧写错，不按「1.20 为准」硬搬，记入 notes/PORT-RESIDUALS.md 待确认。 | COVERED |
| 283 | `67c51a778` | 2026-09-19 | 小传一手神必青蛙模型 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | COVERED |
| 284 | `0fe39ec92` | 2026-09-19 | 移除Geo静态渲染器 | ConfluenceOtherworld | +1 ~10 -25 | `hornet_baby.animation.json`→`hornet_baby.animation.json` |  |  | content |  | COVERED |
| 285 | `6f638b9b4` | 2026-09-19 | 修复粒子的顶点绕序问题 | (repo-root), Confluence-Magic-Lib, PortLib, TerraCurio | +0 ~4 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 286 | `c5b035680` | 2026-09-19 | 移除动态光源至MagicLib | Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~8 -2 |  |  |  | content+submodule |  | COVERED |
| 287 | `65c6d2985` | 2026-09-19 | 修复部分物品无法搜索的问题 | Confluence-Magic-Lib | +0 ~1 -0 |  | Confluence-Magic-Lib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +3/-0（Confluence-Magic-Lib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 288 | `85cbebf74` | 2026-09-19 | 完成鸟巢叠加层 | ConfluenceOtherworld | +4 ~5 -0 |  |  |  | content |  | COVERED |
| 289 | `9a3c5798b` | 2026-09-19 | 完成铁傀儡 | ConfluenceOtherworld | +8 ~15 -1 |  |  | ⚠️ 1 | content |  | COVERED |
| 290 | `b12020ec9` | 2026-09-19 | 更新家具 | TerraFurniture | +0 ~1 -0 |  |  |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，且子模块侧无可枚举改动（对象不可得/无净改动） | SKIP-PLATFORM |
| 291 | `c2075d90f` | 2026-09-19 | 修复 | TerraFurniture | +0 ~1 -0 |  |  |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，且子模块侧无可枚举改动（对象不可得/无净改动） | SKIP-PLATFORM |
| 292 | `1d8a00610` | 2026-09-19 | 完成幽匿游灵 | ConfluenceOtherworld | +4 ~10 -1 |  |  | ⚠️ 1 | content |  | COVERED |
| 293 | `157730424` | 2026-09-20 | 完成小鬼，致命球，血蝙蝠，棱镜 | ConfluenceOtherworld | +21 ~14 -9 |  |  | ⚠️ 2 | content |  | COVERED |
| 294 | `ed36d3b0d` | 2026-09-20 | 上传模块 | Confluence-Magic-Lib, TerraFurniture | +0 ~2 -0 |  |  |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，且子模块侧无可枚举改动（对象不可得/无净改动） | SKIP-PLATFORM |
| 295 | `f212a001a` | 2026-09-20 | fix: 修复生物渲染、蠕虫行为及特殊物品拾取 | ConfluenceOtherworld, PortLib | +2 ~33 -0 |  | PortLib×1 | ⚠️ 1 | content+submodule |  | PORTED |
| 296 | `a988279e7` | 2026-09-20 | 改动 | TerraFurniture | +0 ~1 -0 |  |  |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，且子模块侧无可枚举改动（对象不可得/无净改动） | SKIP-PLATFORM |
| 297 | `e145cafb5` | 2026-09-20 | 动态群系修改与client tick事件大一统 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraFurniture | +9 ~120 -12 | `ScryingOrb.java`→`ScryingOrbHandler.java` 等11处 | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 11 | content+submodule+integration | （归 Phase 1/3，不逐提交移植） — 动态群系修改与 client tick 事件大一统：11 处改名 + 无新增行，属架构统一重构 -> 归 Phase 1/3 | DEFER-ARCH |
| 298 | `a34060571` | 2026-09-20 | 移动到init | ConfluenceOtherworld | +0 ~12 -0 | `ModBlockCounters.java`→`ModBlockCounters.java` 等3处 |  |  | content+integration |  | COVERED |
| 299 | `2818e719b` | 2026-09-20 | 蜘蛛爬墙 | ConfluenceOtherworld | +1 ~2 -0 |  |  |  | content |  | COVERED |
| 300 | `14425eb1c` | 2026-09-20 | uv修复 | ConfluenceOtherworld | +0 ~8 -0 |  |  |  | content |  | COVERED |
| 301 | `b47adc3ce` | 2026-09-20 | 光剑修改 | ConfluenceOtherworld | +0 ~18 -0 |  |  |  | content |  | PORTED |
| 302 | `81488b6d0` | 2026-09-20 | 修改一股味的代码 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~93 -6 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 4 | content+submodule |  | SKIP-1.20-REVERTED |
| 303 | `d879d8ffc` | 2026-09-20 | 第一人称动画功能移到lib | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~5 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 1 | content+submodule |  | COVERED |
| 304 | `c454b8d68` | 2026-09-20 | fix(otherworld): 修复本源末影龙属性注册并调整蠕虫移动逻辑 | ConfluenceOtherworld | +0 ~6 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 305 | `ff7a62eea` | 2026-09-20 | IdentityHashMap换成Reference2ObjectOpenHashMap | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~13 -0 |  | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 | ⚠️ 2 | content+submodule+integration | （1.21 已是目标形态） — IdentityHashMap→Reference2ObjectOpenHashMap：已核对 1.21 侧 GlobalCloakData.java 第 50/54/58/59/60 行**本来就是** Reference2ObjectOpenHashMap；未命中的 6 行只是 1.20 顺手加的 `final` 修饰符。 | COVERED |
| 306 | `64b7bbe9e` | 2026-09-20 | 完成吸血鬼青蛙，史莱姆，小雪怪，铁傀儡 | ConfluenceOtherworld | +12 ~18 -6 |  |  | ⚠️ 1 | content |  | COVERED |
| 307 | `124baf17d` | 2026-09-20 | 完成蜘蛛，沙漠虎 | ConfluenceOtherworld | +6 ~13 -2 | `VampireFrogIdleGoal.java`→`DesertTigerIdleGoal.java` 等2处 |  | ⚠️ 1 | content |  | COVERED |
| 308 | `46e5e8626` | 2026-09-20 | 移除旧架构召唤体系 | ConfluenceOtherworld | +0 ~19 -40 | `SummonDamageSource.java`→`WhipDamageSource.java` |  | ⚠️ 8 | content |  | PORTED |
| 309 | `d3d8dcd24` | 2026-09-20 | 清理导入 | ConfluenceOtherworld | +0 ~14 -0 |  |  |  | content |  | COVERED |
| 310 | `3e76867aa` | 2026-09-20 | 迁移标记体系 | ConfluenceOtherworld | +0 ~16 -3 |  |  |  | content |  | PORTED |
| 311 | `701d48a08` | 2026-09-20 | feat: 完善动态群系覆盖与迷你群系判定 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~14 -1 |  | Confluence-Magic-Lib×1 |  | content+submodule+integration |  | COVERED |
| 312 | `c886cec0f` | 2026-09-20 | 修复暴击率问题 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~4 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | PORTED |
| 313 | `a45ef0a13` | 2026-09-20 | 修复暴击率问题 | TerraFurniture | +0 ~1 -0 |  | TerraFurniture×4 |  | submodule-only |  | SKIP-PLATFORM |
| 314 | `286102066` | 2026-09-20 | fix: 完善植物茎部受击判定与坐骑行为 | ConfluenceOtherworld | +1 ~16 -0 |  |  |  | content |  | COVERED |
| 315 | `6db8be065` | 2026-09-20 | 加点 | TerraFurniture | +0 ~1 -0 |  | TerraFurniture×1 |  | submodule-only |  | SKIP-PLATFORM |
| 316 | `a086eeba6` | 2026-09-20 | 调整一部分生物的动画，碰撞箱，贴图 | ConfluenceOtherworld | +7 ~14 -0 |  |  |  | content |  | COVERED |
| 317 | `1da5e0d26` | 2026-09-21 | 修复一些问题 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +1 ~22 -0 |  | PortLib×1, Confluence-Magic-Lib×1 | ⚠️ 4 | content+submodule |  | PORTED |
| 318 | `fc4498b8b` | 2026-09-21 | 改群系生成 | ConfluenceOtherworld | +0 ~6 -0 |  |  |  | content | 已由 adb78dbae 一并带入 — 改群系生成：BiomeRegionAllocator/BiomeRegionType/BiomeRegionTable/ConfluenceBiomeInjector 等 overlap 98~100%，行级复核 101 行新增**全部命中**。 | COVERED |
| 319 | `7169379cd` | 2026-09-21 | 精修碰撞箱，添加安卡十字与眼球激光塔 | ConfluenceOtherworld | +39 ~27 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 320 | `e7cabcfea` | 2026-09-21 | 精修模型 | ConfluenceOtherworld | +1 ~4 -1 | `BloodBatMinion.java`→`SanguineBatMinion.java` 等3处 |  | ⚠️ 1 | content |  | COVERED |
| 321 | `c7aefbf00` | 2026-09-21 | 添加翻译键 | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | COVERED |
| 322 | `46ae3a168` | 2026-09-21 | feat: 完善悠悠球及饰品功能，统一敌怪反击并修复附魔钓竿 | ConfluenceOtherworld | +27 ~41 -1 |  |  | ⚠️ 2 | content |  | PORTED |
| 323 | `900c068f7` | 2026-09-21 | 音频大导入（第一批） | ConfluenceOtherworld | +0 ~63 -68 |  |  | ⚠️ 5 | content |  | COVERED |
| 324 | `ddb08626b` | 2026-09-21 | 音频大导入（第二批） | ConfluenceOtherworld | +120 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（120 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 325 | `9f57aa9cd` | 2026-09-21 | 音频大导入（第三批） | ConfluenceOtherworld | +120 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（120 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 326 | `1e71b3c12` | 2026-09-21 | 音频大导入（第四批） | ConfluenceOtherworld | +120 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（120 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 327 | `5f3b80aea` | 2026-09-21 | 音频大导入（第五批） | ConfluenceOtherworld | +100 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（100 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 328 | `6756199b8` | 2026-09-21 | 音频大导入（第六批） | ConfluenceOtherworld | +134 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（134 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 329 | `654e45653` | 2026-09-21 | 音频大导入（最后一批） | ConfluenceOtherworld | +123 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（123 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 330 | `892008ba0` | 2026-09-21 | feat(worldgen): 重构蜘蛛洞生成并接入蜘蛛巢方块 | Confluence-Magic-Lib, ConfluenceOtherworld | +1 ~9 -0 |  | Confluence-Magic-Lib×1 |  | content+submodule |  | COVERED |
| 331 | `7572f2ebd` | 2026-09-21 | 添加三个饰品 | (repo-root), ConfluenceOtherworld | +1 ~14 -0 |  |  | ⚠️ 5 | content |  | PORTED |
| 332 | `da5b986be` | 2026-09-21 | 蜘蛛巢石 | ConfluenceOtherworld | +8 ~7 -0 |  |  |  | content |  | PORTED |
| 333 | `9074bb9e5` | 2026-09-21 | fix(npc): 按地牢入口独立管理老人存续与补刷 | ConfluenceOtherworld | +0 ~4 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 334 | `936551676` | 2026-09-21 | 完成新的伤害信息 | ConfluenceOtherworld | +7 ~7 -0 |  |  |  | content |  | COVERED |
| 335 | `6d4f07298` | 2026-09-21 | 悠悠球和召唤杖调参，外观调整 | ConfluenceOtherworld | +15 ~23 -0 |  |  | ⚠️ 1 | content |  | PORTED |
| 336 | `f38c08ae6` | 2026-09-21 | 悠悠球和召唤杖调参，外观调整 | ConfluenceOtherworld | +1 ~0 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 337 | `fecb245e5` | 2026-09-21 | 罐子掉钱逻辑补充 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | PORTED |
| 338 | `bb1892201` | 2026-09-21 | 伤害粒子的更改 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | COVERED |
| 339 | `55aebf2f9` | 2026-09-22 | 改 | (repo-root), ConfluenceOtherworld, TerraCurio | +0 ~13 -0 |  | TerraCurio×1 | ⚠️ 1 | content+submodule | （不动 1.21） — 机器判定：方向为 port-ing（新增/删除行以 Port 引用为主，+Port292/-Port289），属把原生写法换成 Port 写法，1.21 保留原生即可 | SKIP-PLATFORM |
| 340 | `9d66b34d0` | 2026-09-22 | 更新连枷 | ConfluenceOtherworld | +25 ~10 -0 |  |  |  | content |  | COVERED |
| 341 | `8bb33454a` | 2026-09-22 | 家具 | TerraFurniture | +0 ~1 -0 |  | TerraFurniture×1 |  | submodule-only |  | SKIP-PLATFORM |
| 342 | `d5fd40bff` | 2026-09-22 | 平衡锤 | ConfluenceOtherworld | +7 ~1 -0 |  |  |  | content |  | COVERED |
| 343 | `af1426bed` | 2026-09-22 | 完成召唤词缀 | ConfluenceOtherworld | +0 ~6 -0 |  |  |  | content |  | PORTED |
| 344 | `a94cf78dc` | 2026-09-22 | 完成词缀提示与词缀属性 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | PORTED |
| 345 | `05b032399` | 2026-09-22 | 现在邪恶蘑菇，丛林孢子可以生成了 | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | content |  | PORTED |
| 346 | `0776c451c` | 2026-09-22 | 修复音效 | ConfluenceOtherworld | +0 ~2 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 347 | `e1cd9c8cb` | 2026-09-22 | 添加配置文件，召唤词缀复制 | (repo-root), ConfluenceOtherworld | +0 ~12 -0 |  |  | ⚠️ 3 | content |  | DEFER-ARCH |
| 348 | `11a43615e` | 2026-09-22 | 更改远程数值 | ConfluenceOtherworld | +0 ~10 -0 |  |  |  | content |  | COVERED |
| 349 | `c0e8c4c75` | 2026-09-22 | 添加多召唤标记叠加支持 | ConfluenceOtherworld | +1 ~8 -1 |  |  |  | content |  | COVERED |
| 350 | `ef1a4d138` | 2026-09-22 | feat: 重构肉山肉墙与悠悠球实现，更新 NPC 交互界面并统一敌怪射弹伤害 | Confluence-Magic-Lib, ConfluenceOtherworld | +13 ~103 -1 | `YoyoEffectProjectile.java`→`BaseYoyoProjectile.java` | Confluence-Magic-Lib×1 | ⚠️ 3 | content+submodule |  | PORTED |
| 351 | `e2a096341` | 2026-09-22 | 删除Ponder的nbt，升级粒子 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~3 -4 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（5 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 352 | `5fe2c370b` | 2026-09-22 | fix: 完善 NPC 区域生成、地表落点与存活记录管理 | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | content |  | COVERED |
| 353 | `1ab2b43ac` | 2026-09-22 | 修复与TerraBlender的兼容 | ConfluenceOtherworld | +1 ~11 -1 |  |  |  | content | 已由 adb78dbae 前向移植 — 群系注入修复（BiomeSourceMixin +109、其余 injector 文件全部 overlap 98~100%）。行级复核 128 行新增中 18 行未逐字命中，均为中文注释改写与 `for (Entry entry : list)` 一类格式差异。**本条是自检锚点**：工具独立复现了已手工移植的结论。 | COVERED |
| 354 | `116bef809` | 2026-09-22 | 添加哨兵携带接口 | ConfluenceOtherworld | +1 ~7 -0 |  |  |  | content |  | COVERED |
| 355 | `e6eace2b1` | 2026-09-23 | 平衡性调整尝试 | ConfluenceOtherworld | +0 ~2 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 356 | `c22ea51d4` | 2026-09-23 | 软核询问屏幕十秒后暂停游戏 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content | （1.21 已同步） — 软核询问屏幕十秒后暂停游戏：8 行新增**全部命中** AskForSoftcoreScreen.java（overlap 96%）。 | COVERED |
| 357 | `9e32f0135` | 2026-09-23 | fix: 修复 Boss 从属清理与击退问题，集中悠悠球射弹参数 | ConfluenceOtherworld | +0 ~18 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 358 | `a037263e1` | 2026-09-23 | feat: 整理 NPC 商店界面并修复渔夫任务筛选 | ConfluenceOtherworld, PortLib | +5 ~4 -1 | `NPCReforgeScreen.java`→`NPCReforgeScreen.java` 等3处 | PortLib×1 |  | content+submodule |  | COVERED |
| 359 | `2f44045a8` | 2026-09-23 | 大改修饰语 | Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~13 -0 |  | Confluence-Magic-Lib×1 | ⚠️ 2 | content+submodule |  | PORTED |
| 360 | `1f1c456ff` | 2026-09-23 | 染料商 | ConfluenceOtherworld | +1 ~1 -0 |  |  |  | content |  | COVERED |
| 361 | `e9501cf56` | 2026-09-23 | fall_damage_multiplier属性不再导致摔落声音 | ConfluenceOtherworld, PortLib, TerraCurio | +0 ~5 -1 |  | PortLib×1, TerraCurio×1 | ⚠️ 1 | content+submodule |  | COVERED |
| 362 | `b2ff1bdf0` | 2026-09-23 | 修复地牢盔甲架和水矢 | ConfluenceOtherworld | +0 ~5 -0 |  |  |  | assets/other | （本轮不处理） — 机器快筛 R2：只改资源/数据（5 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 363 | `a2dfa30da` | 2026-09-24 | refactor: 统一 NPC 心情配置与环境计算 | ConfluenceOtherworld | +1 ~7 -0 |  |  |  | content |  | COVERED |
| 364 | `6a3e17e86` | 2026-09-25 | feat: 完善剑类特效与剑气渲染，调整植物敌怪追击 | ConfluenceOtherworld | +5 ~13 -1 |  |  |  | content |  | PORTED |
| 365 | `7a072d51b` | 2026-09-25 | 添加爆破专家商店专用贴图及映射 | ConfluenceOtherworld | +1 ~1 -0 |  |  |  | content |  | COVERED |
| 366 | `3bc53d43d` | 2026-09-25 | 火星探测器 | ConfluenceOtherworld | +8 ~8 -0 |  |  |  | content |  | COVERED |
| 367 | `ac2ac4428` | 2026-09-25 | fix: 统一生物生成检查并修正渔夫任务条件 | ConfluenceOtherworld | +0 ~9 -0 |  |  |  | content |  | COVERED |
| 368 | `e6d47c412` | 2026-09-25 | 修复连接材质 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | REVERSE-ALIGNED |
| 369 | `2ca9e2ac0` | 2026-09-25 | feat: 移植左键状态接口并添加 NPC 主动攻击黑名单 | ConfluenceOtherworld | +4 ~11 -0 |  |  | ⚠️ 2 | content |  | COVERED |
| 370 | `185a0fdbb` | 2026-09-25 | 彩色火把 | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld | +2 ~22 -0 |  | Confluence-Magic-Lib×1 | ⚠️ 2 | content+submodule |  | PORTED |
| 371 | `980615a95` | 2026-09-25 | 彩色火把 | ConfluenceOtherworld | +0 ~1 -0 |  |  | ⚠️ 1 | content |  | COVERED |
| 372 | `801f95ff3` | 2026-09-25 | 塞贴图 | ConfluenceOtherworld | +1 ~4 -0 |  |  |  | content |  | COVERED |
| 373 | `2472a2c1b` | 2026-09-25 | 1.2.7 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~7 -0 |  | PortLib×1, Confluence-Magic-Lib×1 |  | content+submodule |  | PORTED |
| 374 | `a6819f175` | 2026-09-25 | feat(magiclib): 支持直接注册动态光源 | Confluence-Magic-Lib | +0 ~1 -0 |  | Confluence-Magic-Lib×1 |  | submodule-only |  | COVERED |
| 375 | `566fde2d4` | 2026-09-25 | fix(confluence): 修复血肉墙墙后玩家处理并调整渔夫按钮布局 | ConfluenceOtherworld | +0 ~4 -0 |  |  |  | content |  | COVERED |
| 376 | `a75bda140` | 2026-09-25 | PortLib: PortSelectMusicEvent 置空时停止音乐并跳过本刻 | PortLib | +0 ~1 -0 |  | PortLib×1 |  | submodule-only | （不动 1.21） — 机器快筛 R4：主仓库只有子模块指针，子模块侧净改动仅 1 文件 +35/-4（PortLib），属 part 系列接线/Port 化滚动 | SKIP-PLATFORM |
| 377 | `05380071a` | 2026-09-26 | 调整种子特性 | ConfluenceOtherworld, TerraCurio | +2 ~10 -0 |  | TerraCurio×1 |  | content+submodule |  | PORTED |
| 378 | `75a6a63ad` | 2026-09-26 | feat(client): 添加 NPC 商店商品稀有度描边渲染组件 | ConfluenceOtherworld | +4 ~3 -0 |  |  |  | content |  | COVERED |
| 379 | `fa147c869` | 2026-09-27 | 改一点 | TerraFurniture | +0 ~1 -0 |  | TerraFurniture×1 |  | submodule-only |  | SKIP-PLATFORM |
| 380 | `fcd2368c6` | 2026-09-27 | 修重铸价格（没对接心情） | (repo-root), Confluence-Magic-Lib, ConfluenceOtherworld, TerraCurio | +0 ~5 -0 |  | Confluence-Magic-Lib×1, TerraCurio×1 |  | content+submodule |  | PORTED |
| 381 | `2d21e3364` | 2026-09-27 | 修bug | ConfluenceOtherworld | +0 ~3 -0 |  |  |  | content |  | COVERED |
| 382 | `69a48adae` | 2026-09-27 | 修复构建问题 | ConfluenceOtherworld, TerraCurio | +0 ~2 -0 |  | TerraCurio×1 |  | assets+submodule | （本轮不处理） — 机器快筛 R2：只改资源/数据（1 个文件，无 java），随所属功能提交一起处理 | DEFER-ASSETS |
| 383 | `1038e97fe` | 2026-09-27 | 链球防止攻击时肘击的正确修复方式 | ConfluenceOtherworld | +0 ~3 -1 |  |  | ⚠️ 1 | content |  | COVERED |
| 384 | `e6c0036c2` | 2026-09-27 | 防止悠悠球肘击地板 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | PORTED |
| 385 | `fc3fabfb9` | 2026-09-27 | 平衡性调整，权重调整，贴图补充 | ConfluenceOtherworld, TerraCurio | +2 ~7 -0 |  | TerraCurio×1 | ⚠️ 1 | content+submodule |  | COVERED |
| 386 | `18221c338` | 2026-09-27 | 调整为标签 | ConfluenceOtherworld | +0 ~16 -0 | `YoyoSession.java`→`YoyoSession.java` |  | ⚠️ 1 | content |  | COVERED |
| 387 | `ce6daf602` | 2026-09-27 | 火星工程师（数值上可能还有问题） | ConfluenceOtherworld | +12 ~12 -0 |  |  |  | content |  | PORTED |
| 388 | `032534179` | 2026-09-27 | 泡泡半透明渲染 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | PORTED |
| 389 | `437e53a84` | 2026-09-27 | fix(portlib): 修复状态效果共用修改器 ID 时的属性曲线冲突 | PortLib | +0 ~1 -0 |  |  |  | submodule-only |  | SKIP-PLATFORM |
| 390 | `dafb03ee9` | 2026-09-27 | fix(block): 修复净化转换表将草植物错误转换为草方块 | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 391 | `f9bda32a5` | 2026-09-30 | 加俩怪，修ai写的sb判定 | ConfluenceOtherworld | +9 ~12 -0 |  |  |  | content |  | PORTED |
| 392 | `2de684965` | 2026-10-02 | 优化动态光照，移除可携带仆从接口行为 | Confluence-Magic-Lib, ConfluenceOtherworld | +0 ~12 -1 |  |  |  | content+submodule |  | PORTED |
| 393 | `6cda06303` | 2026-10-02 | 添加动态光照注册行为与ParticleAccessor | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib | +0 ~3 -0 |  |  |  | content+submodule |  | COVERED |
| 394 | `1780b9a88` | 2026-10-03 | 走妖和军官 | ConfluenceOtherworld | +16 ~14 -1 |  |  |  | content |  | PORTED |
| 395 | `13376f960` | 2026-10-01 | fix(worldgen): BilayerOreFeature 两个便利构造器把 outerOre 误传成 innerOre | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | content |  | COVERED |
| 396 | `9351a7e6b` | 2026-10-02 | fix(entity): 修正改名后遗留的悬空实体 id 字符串 | ConfluenceOtherworld | +0 ~2 -0 |  |  |  | content |  | COVERED |
| 397 | `4c65367a0` | 2026-10-02 | fix(sword): 剑组件粒子改走 ParticleStorm 发射器，修正物品注册早于粒子注册 | ConfluenceOtherworld | +1 ~4 -0 |  |  |  | content |  | REVERSE-ALIGNED |
| 398 | `40fa4ff83` | 2026-10-02 | fix(sword): 草剑拖尾改用原设计的树叶粒子精灵（confluence:leaves/particle_0） | ConfluenceOtherworld | +0 ~1 -0 |  |  |  | assets/other |  | REVERSE-ALIGNED |
| 399 | `c61970e9c` | 2026-10-02 | revert(assets): demon_eye 贴图回撤到 1.21.1 原版，并把该版本同步到 1.20.1 | ConfluenceOtherworld | +0 ~8 -0 |  |  |  | assets/other |  | REVERSE-ALIGNED |
| 400 | `07c2ab5b5` | 2026-10-03 | 修复组件崩溃 | Confluence-Magic-Lib, ConfluenceOtherworld, PortLib, TerraCurio | +0 ~8 -0 | `ForgeItemModelShaperMixin.java`→`ForgeItemModelShaperMixin.java` 等2处 |  |  | content+submodule |  | REVERSE-ALIGNED |