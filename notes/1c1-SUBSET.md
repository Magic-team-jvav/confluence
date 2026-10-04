# 1c-1 · 最大可编译子集与推迟清单

由 `tools/port2native/dep_subset.py` 计算（原始输出见提交信息里的命令；本文是**策展版**，
只留与这批边界有关的结论，不抄 1500 行候选集）。

## 一、为什么 1c-1 不能只搬「Boss 部件系统 4 个文件」

`WP1C-RECON.md` 原先把 1c 拆成「1c-1 = 部件 4 文件 + 配置项」。实测这条切法**过不了编译门**：

| 依赖 | 说明 |
|---|---|
| `BaseBossPart<T extends BaseBoss>` | 泛型上界直接要求 `BaseBoss` 存在 |
| `BaseBoss extends BaseMonster implements Boss` | 要求 `BaseMonster` 存在 |
| `EnemyTargeting.applies(Mob)` 里的 `!(mob instanceof BaseBoss)` | 要求 `BaseBoss` 存在 |
| `BaseBoss` 的 `bossEvent` 同步 | 要求 `BossBarSyncPacketS2C` 存在 |

也就是说 **`BaseMonster` → `EnemyTargeting` → `BaseBoss` → `BaseBossPart`/`BaseLivingBossPart`/
`BossChildDeathLedger` 是一个不可分割的编译单元**。工具实测：
从 `BaseBossPart` 出发做 `org.confluence.*` 依赖不动点，候选 **1054** 个类型 ——
因为 `BaseBoss` 会牵到 `BaseMonster`，再牵到 1.20 的 `ModEntities` / `BossEntities` 注册层，
而注册层又指向全部物种（WP2/WP3）。

所以 1c-1 的实际边界由**编译门**决定，而不是由目录决定。

## 二、本批落地（1c-1）

10 个新文件 + 3 处既有文件改动，全部 `Unresolved` 为 0：

| 文件 | 行数(非空) | 说明 |
|---|---|---|
| `common/entity/monster/BaseMonster.java` | 427 | 怪物基类 |
| `common/entity/boss/BaseBoss.java` | 837 | Boss 基类 |
| `common/entity/boss/BaseBossPart.java` | 285 | 非生物部件的共享生命周期 |
| `common/entity/boss/BaseLivingBossPart.java` | 214 | 活体部件 |
| `common/entity/boss/BossChildDeathLedger.java` | 71 | 从属死亡世界邮箱 |
| `common/entity/boss/BossOwnedEntity.java` | 9 | 「属于某个 Boss」标记接口 |
| `common/entity/boss/BossChunkTicket.java` | 60 | 遭遇区块票据 |
| `common/entity/EnemyTargeting.java` | 76 | 敌怪通用目标仲裁 |
| `network/s2c/BossBarSyncPacketS2C.java` | 41 | 血条元数据同步（按 1.21 `IPacketS2C` 重写） |
| `client/handler/ClientBossBarTracker.java` | 23 | 客户端血条数据表 |

既有文件改动：`CommonConfigs`（+`BOSS_CLEAR_WHEN_NO_TARGET`）、
`NetworkEvents`（注册 `BossBarSyncPacketS2C`）。
`BossBarSyncPacketS2C` 虽然原计划属 1c-c，但 `BaseBoss` 的 tick 要发它，**被编译门提前拉进本批**。

## 三、推迟清单（连同依赖链一起还给后续批次）

| 推迟项 | 直接原因 | 归到哪一批 |
|---|---|---|
| `common/entity/boss/MechanicalMayhemTracker.java` | 依赖 `BossEntities.THE_TWINS / THE_DESTROYER / SKELETRON_PRIME`，1.21 侧没有 `common/init/entity/BossEntities` | 1c-c（注册层） |
| `common/entity/boss/AbstractTwinEye.java` | 依赖 `BaseFlyingMonster` / `BossOwnerTracker` / `TheTwins` / `TwinEyeProjectile` | WP3（物种） |
| `common/init/entity/BossEntities.java`（187 行） | 一次注册 20+ 个 Boss `EntityType`，指向全部 Boss 物种 | WP3 + 1c-c |
| 19 个引用 `BaseMonster` 的 BT 节点 | 只被物种的行为树引用，`BaseMonster` 本身不引用它们 | WP2/WP3（随物种） |
| `client/gui/hud/CustomBossBarRenderer.java` | 依赖 `BossEntities` 把 Boss 实体映射到血条样式；且需按 1.21 渲染管线重写 | 1c-c（须在 `BossEntities` 之后） |

### 被推迟项在 `BaseBoss` 里留下的两处显式 TODO

两处都是**可证的行为无变化**（1.21 当前根本没有对应实体，分支恒为 false），
恢复时机与内容都写在代码注释里：

1. `tick()` 里的 `MechanicalMayhemTracker.observe(this);`
   —— 该类自身的 `isMechanicalBoss(this)` 在没有任何机械 Boss 时恒为 false。
2. `isCombatAnchor(Entity)` 里的 `|| entity instanceof AbstractTwinEye`
   —— 1.21 当前没有双子魔眼，该分支恒为 false。

**没有**为了绕开这两处而发明占位类或桩实现。

## 四、复现命令

```powershell
# 1) 最大可编译子集（判断边界）
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java `
  --root121 Confluence-Magic-Lib/src/main/java `
  --seed    org/confluence/mod/common/entity/boss/BaseBossPart.java `
  --seed    org/confluence/mod/common/entity/boss/BaseLivingBossPart.java `
  --seed    org/confluence/mod/common/entity/boss/BossChildDeathLedger.java `
  --seed    org/confluence/mod/common/entity/boss/MechanicalMayhemTracker.java `
  --defer   org.confluence.mod.common.init.entity.BossEntities `
  --out     notes/1c1-SUBSET.md

# 2) 暂存 -> 转换 -> 合并（转换器只写有改动的文件，所以必须两层一起拷）
python tools/port2native/port2native.py --source $env:TEMP\port2native\1c1\src `
  --out $env:TEMP\port2native\1c1\out --rules tools\port2native\rules
python tools/port2native/apply_batch.py --stage $env:TEMP\port2native\1c1\src `
  --out $env:TEMP\port2native\1c1\out --dest ConfluenceOtherworld\src\main\java

# 3) 编译 + 解析错误（javac 会把长路径折行，必须拼回来再解析）
python tools/port2native/build_errors.py
```

## 五、本批踩到的工具坑（已修，写在这里免得再犯）

1. **转换器只写出有改动的文件**：本批 10 个里只有 6 个有改动，
   只拷 `out/` 会丢掉 4 个未改动的文件 → `apply_batch.py` 做成「暂存打底 + 转换器覆盖」两层。
2. **两个目录的相对路径必须各自相对自己的根算**：混用会把结果写到
   `src/main/java/../out/...`（即 `src/main/out/...`）这种错位目录里，**而且不报错** ——
   第一次合并就是这么把未转换的版本当成成品编了一轮。`apply_batch.py` 现在逐文件打印
   `add` / `OVERWRITE`。
3. **javac 的长路径折行**：直接 grep 只能拿到 `nster.java:428: error:`。
   `build_errors.py` 按「新起一行且以盘符开头」判断真正的文件行，其余非缩进行当续行拼回去。
4. **`Entity#onAddedToWorld()` 是改名，不是删除**（**本条一开始判错了，已更正**）：
   1.21.1 叫 `onAddedToLevel()`（`Entity.java:3733`，`public void onAddedToLevel() { this.isAddedToLevel = true; }`；
   调用点 `ServerLevel.java:933/:945`、`ClientLevel.java:355`、
   `PersistentEntitySectionManager.java:115/:122/:248`）。
   当初只 grep 了 `Entity.java` / `Level.java` 两个文件里的 `onAddedToWorld`，得到 0 次就下了
   「整条删除」的结论，于是把逻辑挪进 `tick()` 的「只跑一次」守卫 —— **这丢掉了 `super` 里
   `isAddedToLevel = true` 这个副作用**。现在四处（`BaseMonster` / `BaseMimic` / `Crawdad` /
   `BaseWormMonster`）都改回覆写 `onAddedToLevel()` 并保留 `super` 调用，
   并固化成规则 `vanilla-onaddedtoworld-rename`（1.20 侧还有 36 处使用）。
