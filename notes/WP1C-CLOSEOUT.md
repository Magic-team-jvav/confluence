# WP1 批次 1c 收尾报告：1c-c 无法独立落地（实测）

**结论先说**：1c 的 1c-a / 1c-1 / 1c-2 / 1c-3 已全部落地并通过编译门。
剩下的 **1c-c（血条渲染器 + BossEntities + MechanicalMayhemTracker）不能作为独立批次做** ——
它的编译闭包实测是 **320 个文件 / 37342 非空行**，横跨 WP2/WP3/WP4/WP5。这不是「还没做」，
而是「按当前切法做不了」，理由与量化过程如下。

## 一、1c-c 三件套的真实依赖链

| 项 | 依赖 | 实测证据 |
|---|---|---|
| `client/gui/hud/CustomBossBarRenderer`（117 行） | `BossEntities.KING_SLIME` 等 **9 个** Boss 实体类型 | 源码里 `STYLES` 直接写 `style(BossEntities.KING_SLIME)` … 共 9 条 |
| `common/init/entity/BossEntities`（166 行） | 20+ 个 Boss 物种类（`KingSlime` / `EaterOfWorlds` / `WallOfFlesh` …） | `import ...entity.boss.*;` 通配 |
| `common/entity/boss/MechanicalMayhemTracker`（59 行） | `BossEntities.THE_TWINS / THE_DESTROYER / SKELETRON_PRIME` | `boss.getType() == BossEntities.X.get()` |

而 `CustomBossBarRenderer` 的闭包并不止于 Boss：
`CustomBossBarRenderer -> BossEntities -> EaterOfWorlds -> BaseNPC`
（`EaterOfWorlds` 引用 NPC 体系），`--defer BaseNPC` 一下就能取下这条链，见下节命令。

## 二、量化（`tools/port2native/dep_subset.py`）

```powershell
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias   "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --seed    org/confluence/mod/client/gui/hud/CustomBossBarRenderer.java `
  --out     notes/WP1C-CLOSEOUT.md
```

结果：候选 **320** 个（都是 1.21 侧新增），按子系统拆分：

| 子系统 | 文件数 | 非空行 | 归属工作包 |
|---|---|---|---|
| `common.entity.monster` | 111 | 12002 | WP2 物种 |
| `common.entity.boss` | 39 | 11956 | WP3 Boss |
| `common.entity.npc` | 63 | 4255 | WP4 NPC |
| `common.entity.projectile` | 37 | 3283 | WP2/WP3 弹幕 |
| `common.data` | 5 | 1281 | 数据包 |
| `common.init.entity` | 4 | 1241 | 注册层 |
| 其余（item/gameevent/mount/storage/gui/…） | 61 | 3324 | WP6 等 |
| **合计** | **320** | **37342** | — |

也就是说：1c-c 等于把 WP2 + WP3 + WP4 + WP5 一起做掉。相比之下本仓库单批最大是 1c-1 的 2052 行。

`--alias` 是必须的：1.20 的 `common.init.entity.ModEntities` 在 1.21 是
`common.init.ModEntities`（纯路径搬迁）。不带 alias 时工具会把整个 1.20 注册层错算成新增
（141 个候选），带上后才排除掉那条假边。

## 三、所以 1c 的最终形态

| 子批 | 内容 | 状态 |
|---|---|---|
| 1c-a | 修正 `notes/WP1C-API-DIFF.md` 口径并重跑（86→83 覆写点，需处理 44→6） | ✅ `59750f81c` |
| 1c-1 | Boss/Monster 基座层 10 文件 + 配置项 + 封包注册（2052 行） | ✅ `43bb87ae4` |
| 1c-2 | 引用 `BaseMonster` 的 20 个 BT 节点（含 1b 推迟的 `SpawnArrowAction`，1623 行） | ✅ `635ae6029` |
| 1c-3 | `AttackEffects` data map + 3 个 BT 节点 + 4 个怪物/弹幕基类（1612 行） | ✅ 本批 |
| 1c-c | 血条渲染器 + `BossEntities` + `MechanicalMayhemTracker` | ⛔ **并入 WP2/WP3/WP4/WP5** |

`BaseBoss` 里留给 1c-c 的两处 TODO（`MechanicalMayhemTracker.observe(this)` 与
`isCombatAnchor` 里的 `instanceof AbstractTwinEye`）**要等各自物种批次的注册层落地后一起恢复**，
注释里已写清内容与时机。两者当前都可证为行为无变化（1.21 没有任何机械 Boss / 双子魔眼）。

## 四、WP1 的剩余欠账（清清楚楚只剩两块）

| 块 | 内容 | 被谁挡住 |
|---|---|---|
| 注册层 | `common/init/entity/` 下 7 个文件 / 2343 非空行（`ModEntities` 703、`MonsterEntities` 826、`BossEntities` 166、`CreatureSpawnPlacements` 253、`NpcEntities` 228、`CritterEntities` 146、`DevelopmentSpawnPolicy` 21） | 需物种先落地 |
| `SpawnPlacementChecks` | `common/entity/` 根唯一剩余文件 / 703 非空行，闭包 463 个类型 | 同上 |

除此之外 WP1 的目录（`common/entity/` 根、`common/entity/ai/**`、`common/data/map/**`）已无缺文件。
