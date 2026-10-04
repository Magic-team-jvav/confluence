# WP2 史莱姆族（服务端整族 + 普通渲染层）：落地记录

> **WP2 剩余**的第一批。编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**（`build_errors.py --maxerrs 2000`）。
> 规模：**14 新 java + 5 改 java + 8 新资源**（26 文件 / +1002 −29）。
>
> **本批解除了 `notes/WP2-REMAINDER-GATE.md` 记的那道闸**：那份测量写的是「WP2 怪物剩余 18 个的闭包都是
> 189~191 个文件，入口是 `TownSlimeNPC` → `BaseNPC` 框架」。**WP4（NPC 基座，批次 25 / `fc680ba14`）落地后，
> 史莱姆族的闭包从 189 塌缩到 1~13** —— 本批就是按这个新测量做的（§一）。

## 一、重新测量：闸门已解除

```powershell
# 7 个史莱姆类：闭包 = 1（只剩自己，因为 1.21 侧 BaseSlime 已存在）
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed org/confluence/mod/common/entity/monster/slime/CorruptSlime.java
# {"candidates": 1, "kept": 1, "new": 1, "removed": 0}

# SpikedSlime 首次测出 77，全是「未给 alias 导致注册层整表被当成新增」的假象：
python … --seed …/SpikedSlime.java
# {"candidates": 77, …}   ← 缺 ModEntities 的 alias

python … --alias org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities `
  --seed …/SpikedSlime.java
# {"candidates": 1, "kept": 1, "new": 1, …}   ← 真值
```

⚠️ **口径提醒（写进工具用法）**：`MonsterEntities`/`BossEntities`/`NpcEntities`/`CritterEntities` 在
**两侧同 FQN**（都在 `common/init/entity/`），只有 `ModEntities` 搬到了 `common/init/`。
把这四个也写成 alias 会让闭包看起来更大（我在本批第一次就踩了：把 `BossEntities` 写成 alias
反而使工具无法满足、闭包虚高到 55~61）。

## 二、本批内容（14 新增 / 5 改动 / 8 资源）

### 2.1 新增（14 java，全部 1.20 同名文件）

| 文件 | 来源（1.20） |
|---|---|
| `common/entity/monster/slime/{CorruptSlime,FleshSlime,GoldenSlime,LavaSlime,LuminousSlime,MotherSlime,SpikedSlime,TropicSlime}` | `common/entity/monster/slime/**` 8 个同名文件 |
| `common/entity/monster/Slimer` | 同名（地牢史莱姆的飞行变体） |
| `client/entity/model/BaseSlimeModel` | 同名 |
| `client/entity/renderer/{BaseSlimeRenderer,BaseSlimeOuterLayer}` | 同名 |
| `client/model/entity/projectile/SlimeSpikeProjectileModel` | 同名 |
| `client/renderer/entity/projectile/SlimeSpikeProjectileRenderer` | 同名 |

### 2.2 改动（5 java + 8 资源）

| 文件 | 要点 |
|---|---|
| `common/init/entity/MonsterEntities` | **补全史莱姆整族 30 个注册成员** + `registerSlime(...)` 两个重载（1.20 `:937/941`）。此前只有 `SWEET_SLIME` 一条（历史增量），本批把整族补齐，并按 1.20 的生物群系分组标题组织（1.21 该文件是按批次分节，故整族集中为一节并保留 1.20 的组标题便于逐条对照） |
| `common/init/ModEntities` | 新增 `SLIME_SPIKE` 注册（1.20 `common/init/entity/ModEntities:667`）。`SlimeSpikeEntity` 类本身随 1b 落地，但**注册条目一直缺** —— 它是 `SpikedSlime` 系列与 `SPIKED_*` 的 `.projectile(...)` 目标 |
| `common/entity/npc/TownSlimeNPC` | **判据回填**：`isStandardSlime(EntityType)`（WP4 的按注册 id 判等过渡写法）+ `STANDARD_SLIME_PATHS` 常量 **删除**，改回 1.20 的 9 个 `MonsterEntities.*_SLIME` 成员比对（1.20 `TownSlimeNPC:52` 原文）。同时清理随之失效的 `BuiltInRegistries`/`ResourceLocation`/`java.util.Set` import |
| `client/event/ModClientEvents` | 3 条层定义（`BaseSlimeModel.INNER_LAYER`/`OUTER_LAYER`、`SlimeSpikeProjectileModel.LAYER_LOCATION`，1.20 `:255/256/276`）+ **25 条普通史莱姆渲染器**（1.20 `:610-634` 逐条）+ `SLIME_SPIKE` 渲染器（1.20 `:473`） |
| 资源 8 张 | `textures/entity/slime/slime_{dungeon,flesh,gold,green_dumpling,swamp}.png` + `textures/entity/proj/{slime,jungle,ice}_spiked_projectile.png`（均 1.20 已存在、1.21 缺失） |

### 2.3 成员级对账（本批的验收硬指标）

```powershell
# 1.20 与 1.21 的 MonsterEntities 史莱姆 id 集合
1.20 slime ids: 30 | 1.21 slime ids: 30
1.20 has, 1.21 missing: []
1.21 has, 1.20 missing: []
```

即：**史莱姆族在注册层的成员名与注册 id 与 1.20 完全一致**（30/30，零差异）。
（`MonsterEntities` 其余仍是增量态：全量 id 1.20 有 144、1.21 现有 52，差的 114 属后续物种批次，含 WP3 boss。）

## 三、1.20 → 1.21 的 API 差异（本批实测，均已在文件内注明出处）

| 1.20 写法 | 1.21.1 实际 | 处理 |
|---|---|---|
| `Model#renderToBuffer(pose, buffer, light, overlay, float r, g, b, a)` | `…, int color)`（`Model.java:23`）；`ModelPart#render` 同理（`ModelPart.java:110`） | 3 处：`SlimeSpikeProjectileModel`、`BaseSlimeOuterLayer`（传 `-1`，等价于四个 1.0F）、`SlimeSpikeProjectileRenderer` |
| `LivingEntity#getDimensions(Pose)` 可覆写 | **final**（内部 `getDefaultDimensions(pose).scale(getScale())`） | `SpikedSlime` 改覆写 `protected getDefaultDimensions(Pose)`，并**去掉**原作里的 `getScale()`（父类会再乘一次）—— 同一改法见 `BaseSlime.java:158-168`、`Hoplite.java:49` |
| `EntityDimensions` 是普通类 | **record**（`EntityDimensions.java:6`） | 本批只用到 `.scale()`，无需改 |
| `LibAttributes.getArmorPenetration().get()` | `LibAttributes.getArmorPenetration()` | 30 条注册逐条机械改写 |

## 四、**故意留给下一批**（都是「不属于史莱姆族本身」的依赖）

| 项 | 为什么 |
|---|---|
| `SPIKED_SLIME` / `SPIKED_JUNGLE_SLIME` / `SPIKED_ICE_SLIME` / `SLIMER` 的**渲染器** | 1.20 走 `GeoSpecialSlimeRenderer`，它继承 1.20 自有的 `GeoNormalRenderer` —— 后者 import `common/entity/boss/BaseWormBoss`（属 **WP3**）并需要 `client/effect/RenderStateShardAccessor`（**整类未迁**，G5′ 已记）。四个**实体**已注册（成员级对齐），只是暂时不可见 |
| `CLUMSY_BALLOON_SLIME` 的渲染器 | 1.20 用 `MissingModelRenderer`（1.21 侧无该类） |
| `client/effect/DivaSlimeVertexConsumer` | 只被 `TownSlimeRenderer` 用；随城镇史莱姆渲染器一起做（`TownSlimeRenderer` 闭包 15，同样吃 `GeoNormalRenderer`/`RenderStateShardAccessor`） |
| `CrownOfKingSlimeModel` / `CrownOfKingSlimeModelEntity` / `CrownOfKingSlimeModelRenderer`（3 文件） | 本已 staged，**编译门实测**它们需要 `BossEntities.CROWN_OF_KING_SLIME_MODEL`（1.21 未落地）——该实体由 `KingSlime:230` 生成、注册在 `BossEntities`，属 **WP3（史莱姆王）**。已从本批移出（未提交） |
| 尖刺史莱姆的 geo/动画/贴图、`slimer.*`、`npc/*_slime.*` 资源 | 随各自渲染器落地 |
| 刷怪蛋（`SpawnEggItems` 的 5 个史莱姆蛋）、`ModTabs`、bestiary、掉落表、实体 lang | 物品/数据生成层，随 **WP7 datagen** 一起长 |

## 五、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（两轮：14 → 0；首轮全在 staged 文件内，我的手工接线零错误） |
| `check_duplicates.py`（14 个新增文件） | 0 处「疑似移动/重复」；3 处 DIFF 是对 **TerraEntity** 的同类物种（`terraentity.entity.monster.slime.{FleshSlime,GoldenSlime,SpikedSlime}`）—— 既定的「先加后删」，TE 退役另立批次 |
| 成员级对账 | 史莱姆族 30/30，注册 id 零差异（§2.3） |
| 子模块 | 未改动 |
| 待游戏内验收 | 25 种普通史莱姆的外观/外壳半透明/发光轮廓、尖刺史莱姆弹射物渲染与飞行姿态、城镇史莱姆「给史莱姆穿侍从装备」判据（`tryEquipSquire`）、`Slimer`/`WinglessSlimer` 的分裂行为 |
