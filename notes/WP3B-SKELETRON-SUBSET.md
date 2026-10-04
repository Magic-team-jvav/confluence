# 批次 24 · 骷髅王组（`Skeletron` + `SkeletronHand` + 增量 `BossEntities`）

> 目的：把 WP4（NPC 基座）剩下的**唯一 Boss 侧硬依赖**解掉 ——
> `NPCSpawner.oldManSummoned(OldManNPC, Skeletron)` 与 `OldManNPC.summonSkeletron()` 都要求
> `Skeletron` 真实存在（见 `notes/WP4-NPC-GATE.md` 第七节）。
> 这一批落地后，批次 25（`NPCSpawner` 包移动 + 53 个 npc 文件 + 增量 `NpcEntities`）就没有 Boss 侧阻塞了。

## 一、开工测量（三条证据，都不是估计）

```powershell
# 1) 闭包：把 BossEntities 摘掉后，Skeletron 只剩自己 → 其它依赖 1.21 全有
python tools/port2native/dep_subset.py … `
  --seed …entity.boss.Skeletron --seed …entity.boss.SkeletronHand `
  --defer org.confluence.mod.common.init.entity.BossEntities
# {"candidates": 152, "kept": 1, "new": 1, "removed": 151}

# 2) 移动 vs 新增审计
python tools/port2native/check_duplicates.py … --file …/Skeletron.java --file …/SkeletronHand.java
# 共 2 个待查文件，发现 0 处「疑似移动/重复」；SkeletronHand 判 DIFF（TerraEntity 旧形态，见第三节）

# 3) 逐项核对 Skeletron 的前置
#    BaseBoss ✅ / 整棵行为树 ai/bt（含 WaitAction 等 37 个 leaf）✅ / SkeletronSkullProjectile ✅
#    （且该类与 1.20 逐字相同）/ ModSoundEvents ✅ / ModEntities ✅
```

## 二、落地清单

| 文件 | 动作 | 说明 |
|---|---|---|
| `common/entity/boss/Skeletron` | 新增 420 行 | 与 1.20 仅差 4 处（见第四节） |
| `common/entity/boss/SkeletronHand` | 新增 385 行 | 与 1.20 仅差 3 处 |
| `common/init/entity/BossEntities` | **新增（增量版）** | 只有 `SKELETRON` / `SKELETRON_HAND` 两个条目；属性用 `ModEntities.withAttributes(...)` 附带，与 1.20 写法一致 |
| `common/init/ModEntities` | 改（+6 行） | ① 新增成员 `SKELETRON_SKULL`（1.20 `:224-230` 逐字）；② **钩爪 id 改名**（见第三节） |
| `Confluence` | 改（+3 行） | `BossEntities.register(eventBus)`（紧邻 `CritterEntities`） |

编译门：**第 1 次 10 处 → 2 处 → 0**（两轮）。

## 三、⚠️ 两个必须先查出来、否则会「崩服 / 死代码」的坑

### 3.1 `skeletron_hand` 的 **id 冲突**（1.20 已经给了答案）

| 侧 | `skeletron_hand` 这个 id | 另一个 id |
|---|---|---|
| **1.20** | **Boss 的手**（`BossEntities.SKELETRON_HAND`，`MobCategory.MONSTER`） | 钩爪改叫 `skeletron_hand_hook`（`ModEntities.java:485`） |
| **1.21（旧）** | **钩爪**（`ModEntities.java:169` 的 `registerHook("skeletron_hand", …)`） | Boss 的手不存在 |

也就是说：直接照 1.20 注册 `BossEntities.SKELETRON_HAND = "skeletron_hand"`，会和 1.21 现有的钩爪
**在同一张 ENTITY_TYPE 表里撞 id**（重复注册 → 启动期直接崩）。
本批按 1.20 的做法把钩爪改成 `skeletron_hand_hook`（改动只有一行 + 注释），
**物品 `skeletron_hand`（`HookItems`）不受影响**（物品与实体类型是两张表，1.20 也是这么保留的）。

### 3.2 成员级盲区：`ModEntities.SKELETRON_SKULL` 不存在

`SkeletronSkullProjectile` **类**在 1.21 侧早就有（而且与 1.20 逐字相同），但实体类型**成员**没有 ——
1.21 只有另一个 `SKULL_PROJECTILE`（`skull_projectile`，通用骷髅弹幕）。
而 1.20 的 `Skeletron.java:355` 用 `ModEntities.SKELETRON_SKULL.get().create(level())`，
`BossEntities` 的属性块也配了 `.projectile(ModEntities.SKELETRON_SKULL, …)` → 必须补这个成员
（任务书第七节第 2 条列的那类盲区，这是第 N 次）。

## 四、挖到的 API 差异（都实测）

| 位置 | 1.20 | 1.21 实测 |
|---|---|---|
| `SkeletronHand:329` | `definePartSynchedData()` 无参钩子 | 1.21 的 `BaseBossPart` 已改成 `definePartSynchedData(SynchedEntityData.Builder builder)`（`BaseBossPart.java:235`）→ 子类签名要跟着补形参。⚠️ **与坐骑批次同一个转换器缺陷**：1.20 靠 `entityData` 字段写同步字段，转换器把 `entityData.define(...)` 改写成 `builder.define(...)` 却传不进 `builder` |
| `SkeletronHand:356` | `lerpTo(…, int steps, boolean teleport)`（Forge 补丁的多一个形参） | NeoForge 21.1 是**六参**（`Entity.java:2202`）→ 去掉 `teleport`，判据改用 `steps <= 0`（先例 `BaseBossPart.java:148-157`、`BaseWormPart.java:286`） |
| `BossEntities` 属性块 | `LibAttributes.getArmorPenetration().get()` | 1.21 返回的 `Holder<Attribute>` **没有 `.get()`**，而 `CreatureAttributeBuilder#add` 本来就吃 `Holder` → 直接传（与任务书里 `ModEffects.X` 那条同类） |
| `BossEntities` 的 import | `org.confluence.mod.common.init.entity.ModEntities` | 1.21 是 `org.confluence.mod.common.init.entity.ModEntities`（正是 `--alias init.entity.ModEntities=init.ModEntities` 处理的那条路径搬迁）；静态导入 `withAttributes` 也要用新包 |

## 五、与 TerraEntity 旧形态的关系（政策要求写明）

`SkeletronHand` 的闸门判定是 **DIFF**：TerraEntity 里有 `terraentity.entity.boss.{Skeletron,SkeletronHand}`、
客户端 `SkeletronRenderer`/`SkeletronHandRenderer`/`SkeletronAnimator`，以及集成层的
`mixin/integration/terraentity/SkeletronMixin`、`TEBossEntities.SKELETRON_HAND`
（`LivingEntityEvents:580`、`TEEvents:36` 在用）。属既定的「**TerraEntity 退役、内容内联进主模组**」过渡态：
**旧实现在 TerraEntity 子模块，随其退役批次删除**；本批落的是 1.20 架构对应物，不是它的副本。

## 六、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| 骷髅王**客户端渲染器/模型/动画** | 1.21 主模组没有对应渲染器（TE 那套是另一个实体类型）；属客户端批次，与坐骑、动物渲染器同一条既有约定。**服务端语义完整**（实体注册、属性、AI、召唤流程都在） |
| 生成蛋 / lang / 图鉴 / 战利品 / 标签 | WP7 数据生成批次 |
| `BossDelaySpawner`、其它 Boss | 仍在 WP3 内容批次里（本批只解除 NPC 层的硬依赖） |
