# WP4（NPC 基座）开工闸门：实测它**无法切成小批**，需要一次裁决

> 2026-09-27 第 15 轮测量。用户裁决「先做 WP4 NPC 基座」后，我按纪律先量闭包再动手，
> 结论是**这一包没有可编译的小切片**，先把它记清楚，避免下一轮重复踩。
> 所有数字都是 `tools/port2native/{dep_subset,seed_closures}.py` 的输出，可复现。

## 一、规模（实测，不是估计）

```powershell
python tools/port2native/dep_subset.py `
  --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed org.confluence.mod.common.entity.npc.BaseNPC `
  --alias "org.confluence.lib.util.LibEntityUtils=org.confluence.lib.util.LibUtils" `
  --alias "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities"
# {"candidates": 156, "kept": 156, "new": 156, "removed": 0, "unused_defer": 0}
```

| 项 | 数值 |
|---|---:|
| `BaseNPC` 单点闭包 | **156 文件 / 155 新增 / 20867 非空行** |
| 1.20 `common/entity/npc/**` | 66 文件（1.21 侧现有 12 个：chat/dialog/house/mood 的一部分 + `NPCAttackBlacklist`/`NPCNames`/`RandomItemListing`） |
| 闭包构成 | `common/entity/**` 105（npc 55 / boss 25 / 弹幕 8 / 其它）、`common/item/**` 20（枪械行为 15 + `BaseBullet` + boomerang/mount）、`common/init/**` 8、客户端 3、网络 5、生成/轨道 provider 7、其它 |

## 二、为什么切不出小批（三条实测）

### 1. 54 个 NPC 种子的闭包**完全相同**

```powershell
python tools/port2native/seed_closures.py … --seed-dir common/entity/npc --small 12 `
  --assume-present org.confluence.mod.common.init.entity.MonsterEntities
# {"seeds": 54, "light": 0, "heavy": 54, "hubs": 156, "min_new": 155, "max_new": 156}
```

每个 NPC 类（`BaseNPC`、`SimpleNPC`、`MechanicNPC`…）单独量都是 **156 文件 / 20867 行**——
它们通过 `BaseNPC ↔ NPCTradeList ↔ NPCMood ↔ …` 互相咬死，**不存在「先搬几个轻的」这条路**。

### 2. `TradeCondition.test(ServerPlayer, BaseNPC)` 把贸易层焊在基类上

`TradeCondition.java:17` 的方法签名直接吃 `BaseNPC`，而 25 个 `trade/conditions/**` 都实现它，
`ModTradeConditions` 又通过 `ModCustomRegistries` 注册 —— 种子换成 `TradeCondition` /
`NPCTradeList` / `ModTradeConditions` 量出来**还是 156**。

### 3. 真正的关键路径是 `NPCSpawner`，它吊着 Boss 注册层

`--defer org.confluence.mod.common.init.entity.BossEntities` 会让候选从 156 **塌到 2**，
工具给出的链是：

```
BaseNPC -> NPCSpawner -> BossEntities          （NPCServiceMenu / 两个 NPC 网络包 / GenerationProvider…
                                                几乎每条链都汇到 NPCSpawner）
```

`NPCSpawner`（1.20 侧 1052 行）对 `BossEntities` 的用法**只有 7 处**，全是「某 Boss 被打败过」的
生成门槛：

| 位置 | 用法 |
|---|---|
| `NPCSpawner:484` | `KillBoard.INSTANCE.isDefeated(BossEntities.PLANTERA.get())` → 机器侠 |
| `NPCSpawner:494` | `…isDefeated(BossEntities.KING_SLIME.get())` → 书呆子史莱姆 |
| `NPCSpawner:702-705` | `EYE_OF_CTHULHU` / `EATER_OF_WORLDS` / `BRAIN_OF_CTHULHU` / `SKELETRON` → 树妖 |
| `NPCSpawner:715` | `…isDefeated(BossEntities.QUEEN_BEE.get())` → 巫医 |
| `NPCSpawner:870` | `…isDefeated(BossEntities.SKELETRON.get())` → 机械师 |

而 `BossEntities` 是 **WP3 的注册层**（1.20 的 boss 一个都还没搬），所以：
**WP4 的 155 个文件要么连 `BossEntities`（＝ boss 内容，WP3）一起搬，要么解耦这 7 处。**

## 三、两条路线（需要裁决）

| 路线 | 内容 | 代价 / 风险 |
|---|---|---|
| **A. 整包搬（含最小 BossEntities）** | 155 文件 + `BossEntities` 里这 7 个 Boss 的注册条目（连带它们的实体类，否则注册不了） | 实际变成 WP3+WP4 合并，规模 ≥ 200 文件；但**语义零损失**，且一次解开 WP2 剩余 18 怪物 + 40 动物 + 枪械弹幕层 |
| **B. 解耦 `NPCSpawner` 的 7 处 Boss 门槛** | 把这 7 处换成不依赖 `EntityType` 的判据（1.21 的 `KillBoard`/`GamePhase` 已有 `isAnyMechBossDefeated()` 等）；其余 155 文件照搬 | 会**轻微改变语义**（「打败过这个具体 Boss」→「到达某阶段」）；好处是 WP4 能独立落地，且 7 处改动有明确边界，日后 boss 落地可回填 |
| **C. 先做 WP3 的 38 文件切片** | `notes/WP2-REMAINDER-GATE.md` 第四节量的 `EyeOfCthulhu`+`KingSlime`+`ServantOfCthulhu`+`BossMultiplayerEnhancement`（defer `BossEntities` 后 38 文件） | 能让 `BossEntities` 先长出这 7 个成员（路线 A 的前置），再回头做 WP4；顺序最稳但轮次多 |

## 四、给下一轮的执行清单（无论选哪条）

1. **先跑 `check_duplicates.py` 对待落文件全量审计**（155 个文件规模下尤其重要：
   `common/entity/npc/**` 的 12 个 1.21 现存文件必须逐类 diff，**不许覆盖**，
   同名类按 `SAME`/`MOVE`/`NEAR`/`DIFF` 分派 —— 这正是 `notes/MOVE-VS-NEW-AUDIT.md` 那类事故的高危区）。
2. 用 `stage_batch.py --convert --apply` 按子包分批暂存（`npc/`、`npc/trade/**`、`npc/ai/**`、
   `npc/chat|dialog|house|mood/**`、`data/spawner/NPCSpawner`），**同一批里一次性过编译门**
   （因为类型互相依赖，分批暂存≠分批提交）。
3. 注册层 `NpcEntities` 按 `CritterEntities`（批次 19，`07e4a927b`）的**增量落地**写法：
   只写「类已存在」的条目，避免一次拉进整棵依赖树。
4. 落地后立刻重跑 `file_lag.py` + `triage_pass2.py`（本会话第 15 轮的度量会因为这一包而大幅变化）。

---

## 五、⚠️ 修正：用户选的「路线 C（先做 WP3 的 38 文件切片）」**今天量不出来**

`notes/WP2-REMAINDER-GATE.md` 第四节记的「defer `BossEntities` 后 38 文件」是**旧工具口径的产物**，
按当前 HEAD 重测**复现不了**：

```powershell
python tools/port2native/dep_subset.py … `
  --seed …entity.boss.EyeOfCthulhu --seed …entity.boss.KingSlime `
  --seed …entity.boss.ServantOfCthulhu --seed …entity.boss.BossMultiplayerEnhancement `
  --defer org.confluence.mod.common.init.entity.BossEntities
# {"candidates": 156, "kept": 5, "new": 5, "removed": 151}
```

留下的 5 个是 `BossMultiplayerEnhancement` + 坐骑四件套（`AbstractMountEntity` / `MountItem` /
`MountManager` / `MountItems`）。**但这 5 个也不能落**：`MountItems` 写的是
`new MountItem<>(ModEntities.RIDEABLE_SLIME / RIDEABLE_BEE / RIDEABLE_UNICORN / RIDEABLE_LAVA_SHARK)`，
而 1.21 的 `ModEntities` **没有这四个成员**（实测 grep 为空），对应实体类在 `common/entity/mount/**`
—— 这正是任务书第七节第 2 条记的**成员级盲区**（工具只看类型级边，`ModEntities` 类型存在就认为成员存在）。
照单落地必然编译不过。

**不 defer 时的真实规模**：

```powershell
… --seed EyeOfCthulhu --seed KingSlime --seed ServantOfCthulhu --seed BossMultiplayerEnhancement `
  --seed org.confluence.mod.common.init.entity.BossEntities
# {"candidates": 156, "kept": 156, "new": 156, "removed": 0}
```

即 **Boss 切片与 WP4 是同一个 156 文件强连通团**（155 新增）。boss 侧确有真依赖、不是工具误报：
- `EyeOfCthulhu.java:390` → `BossEntities.SERVANT_OF_CTHULHU.get().create(level())`
- `CrownOfKingSlimeModelEntity.java:50` → `this(BossEntities.CROWN_OF_KING_SLIME_MODEL.get(), level)`
  （`KingSlime -> CrownOfKingSlimeModelEntity -> BossEntities` 就是这条链）

### 修正后的结论与建议

| | 事实 |
|---|---|
| 路线 A / C 的差别 | **不存在**：C 的切片 = A 的整包 = 155 文件（同一个 SCC） |
| 唯一能缩小批次的路线 | **B（解耦）**：把 `NPCSpawner` 那 7 处 `KillBoard.isDefeated(BossEntities.X)` 换成不依赖 `EntityType` 的判据后，`BossEntities` 不再被 NPC 侧引用，这个 SCC 会裂成「NPC+弹幕+枪械（≈130）」与「boss（≈25）」两块 |
| B 的附带收益 | 同时解开**枪械 G3′**（`BaseBulletEntity` 的链正是 `BaseBulletEntity -> BaseNPC -> NPCSpawner -> BossEntities`） |
| B 的代价 | 7 处判据从「打败过这个具体 Boss」变成「到达某阶段」，语义轻微变化；边界明确、日后 boss 落地可回填 |

**建议：选 B（解耦那 7 处）** —— 它把这一个 155 文件的死结变成三个可收口的批次
（NPC 框架 → 弹幕/枪械 → boss 内容），并且是唯一能让枪械内联继续往前的路线。

---

## 六、路线 B′（**语义无损**版）：按「注册 id」查击杀，而不是按阶段判据

用户选了路线 C（不可行）之后，我回头把路线 B 的**代价**再压了一遍：路线 B 原方案是
「把 7 处 `KillBoard.isDefeated(BossEntities.X.get())` 换成 GamePhase 阶段判据」，
实测那样**只有 2 处能对上**（`PLANTERA` ↔ `GamePhase.PLANTERA`、`SKELETRON` ↔ `AFTER_SKELETRON`），
其余 5 处（`KING_SLIME`/`QUEEN_BEE`/`DEERCLOPS`/克苏鲁三件套/机械三王）**没有阶段等价物** → 会真的丢语义。

于是改用一条不丢语义的做法：

1. **耦合清单（实测，全在这 4 个文件里）**：

| 文件 | 处数 | 形态 |
|---|---:|---|
| `common/data/spawner/NPCSpawner` | 7 | `KillBoard.INSTANCE.isDefeated / isAnyDefeated(BossEntities.X.get())` |
| `common/entity/npc/ai/NPCCombatProgression` | 11 | 同上（战斗强度按击杀进度加成） |
| `common/entity/npc/NurseNPC` | 3 | 同上（护士卖货档位） |
| `common/entity/npc/OldManNPC` | **1 处硬依赖** | `new Skeletron(BossEntities.SKELETRON.get(), level())` —— 这是**内容**依赖，不是判据 |

2. **B′ 的做法**：给 1.21 的 `KillBoard` 加**按 id 查**的重载
   （`isDefeated(ResourceLocation)` / `isAnyDefeated(ResourceLocation...)`，已落地，见下），
   NPC 层那 21 处判据改写成 `KillBoard.INSTANCE.isDefeated(Confluence.asResource("plantera"))`。
   - **语义完全不变**：数据本来就是 `Object2BooleanMap<EntityType<?>>`，id 只是先经
     `BuiltInRegistries.ENTITY_TYPE` 解析成 `EntityType`；boss 内容还没搬进来时 id 解析为 `null`
     → 按「未打败」处理（与「类不存在」等价），不会崩；boss 落地后 id 对得上
     （`confluence:plantera` 等，1.20 `BossEntities.registerEntity("plantera", …)`），判定自动生效。
   - 副作用是好的：**NPC 层不再编译期依赖 Boss 注册层**，`BaseNPC -> NPCSpawner -> BossEntities`
     那条把候选集从 156 塌到 2 的链断掉。
3. **剩下唯一的真依赖**：`OldManNPC.summonSkeletron()` 要 `new Skeletron(...)` —— 这是「老人夜里召骷髅王」
   这个**功能**本身，不是判据，所以 **`OldManNPC` 随 boss 内容批次走**（不做 stub）。
   连带影响一处：`mood/MoodEnvironment:37` 的 `other instanceof OldManNPC` 改成
   **按 id 判实体类型**（`confluence:old_man`）—— 今天两种写法都恒假（类与注册项都不存在），
   NPC/boss 内容落地后自动生效，**没有语义损失**。

### 本轮已落地的前置（提交见本批提交信息）

`common/data/saved/KillBoard` 新增两个重载（+ 两个 import），编译门 0 错误。这是 B′ 的**唯一前置**，
与既有的 `VEC_3`、`Immunity.isActive/apply`、G0 的动画 API 同一做法（先补被依赖方，消费点随下一批）。

### 下一批的文件边界（已量好）

`missing_files.py --sub common/entity/npc` → **54 个缺失文件**；本批计划：

| 归属 | 文件 |
|---|---|
| 进本批（53 个） | `npc/**` 全部（BaseNPC / SimpleNPC / 各族 NPC / `ai/**` / `chat/**` / `mood/**` / `trade/**` + 25 个 trade conditions） |
| 进本批（另加） | `common/data/spawner/NPCSpawner`（1052 行，7 处改为 id 判据） |
| 手写（增量） | `common/init/entity/NpcEntities` —— 按 `CritterEntities`（批次 19，`07e4a927b`）的写法，**只注册类已存在的条目**，跳过 `OLD_MAN` |
| 推迟 | `OldManNPC`（要 Skeletron）、`TownSlimeRescue`（看它的依赖）、`network/c2s/SummonSkeletronPacketC2S`（同样要 Skeletron）、以及 boss / 弹幕 / 枪械那些仍在同一团里的文件 |

---

## 七、第 2 轮（goal round 2）实测：边界已收窄到「只需 Skeletron」，且前置全部已就位

### 7.1 ⚠️ 抓到一处**包移动**（过程纪律 5 的又一次实战）

`check_duplicates.py` 对 55 个待查文件（54 个 npc + `NPCSpawner`）报出 **12 条 DIFF**，其中 11 条是
**TerraEntity 的同名旧形态**（`terraentity.entity.npc.{AnglerNPC,MechanicNPC,SimpleNPC,TravelingMerchantNPC,
ChatManager,NPCMood}`、`entity.ai.goal.NPCTradeGoal`、`entity.ai.goal.behavior.condition.{And,Not,Or,Time}Condition`
—— 后四个其实是行为树条件，与 1.20 的贸易条件只是重名）→ 属既定的 **TerraEntity 退役**过渡态，随退役删除。

**第 12 条是真问题**：

| 1.20 | 1.21 现状 | 性质 |
|---|---|---|
| `common/data/**spawner**/NPCSpawner`（1052 行） | `common/data/**saved**/NPCSpawner`（**743 行，旧版同架构**：`IGlobalData` + `Region` + `INSTANCE`） | **移动 + 重写**，不是新增 |

1.21 现有那份被 **17 个文件**引用（`HouseHandler`、`BossDelaySpawner`、`LivingEntityEvents`、`PlayerEvents`、
4 个物品、`AchievementUtils`、5 个 mixin/integration、2 个网络包等）。
按 MOVE-VS-NEW-AUDIT 的处置政策与用户既定裁决（包移动＝真迁移），做法只能是
**`git mv data/saved/NPCSpawner.java → data/spawner/NPCSpawner.java` + 换内容 + 改这 17 处引用**，
**不许**在新包新建副本。⚠️ 另注：1.21 那份旧版还引用了 **`TEBossEntities`**（`SKELETRON` 等），
也就是它本身就是 TerraEntity 退役的障碍之一 —— 换成主模组版后这个依赖一并消失。

### 7.2 唯一的 Boss 侧硬依赖 = Skeletron，而它的前置**全在 1.21 了**

`NPCSpawner` 里有若干「地牢住户」方法真正需要 OldManNPC 的字段，不是判据：

```java
public void oldManSummoned(OldManNPC oldMan, Skeletron boss) {   // :838
    GlobalPos entrance = oldMan.getDungeonEntrance();            // OldManNPC:57 的方法（BaseNPC 没有）
    resident.skeletron = boss.getUUID();
}
```

而 `OldManNPC:109` 又 `new Skeletron(BossEntities.SKELETRON.get(), level())`。
所以路线 B′ 的 Boss 侧需求收敛成**一个 Boss：Skeletron**。实测它的前置：

| Skeletron 的依赖 | 1.21 现状 |
|---|---|
| `extends BaseBoss` | ✅ 已有（`common/entity/boss/BaseBoss.java`） |
| `common/entity/ai/bt/{BTNode,BTRoot,leaf.WaitAction}` | ✅ **整套行为树框架已在**（`bt/` 下 composite/condition/decoration/leaf 齐全，含 37 个 leaf） |
| `common/entity/projectile/SkeletronSkullProjectile` | ✅ 已有 |
| `common/init/ModSoundEvents` | ✅ 已有 |
| `common/init/entity/BossEntities`（`SKELETRON` / `SKELETRON_HAND`） | ❌ **1.21 没有 BossEntities** → 需**增量新建**（按 `CritterEntities` 批次 19 的写法，只注册已存在的类） |
| `SkeletronHand` | 自身无任何 mod 依赖 → 直接可搬 |

### 7.3 由此定下的两批（都已量过边界，可直接执行）

| 批次 | 内容 | 规模 | 解开什么 |
|---|---|---|---|
| **24** | `Skeletron` + `SkeletronHand` + **增量 `BossEntities`**（只这两个成员）+ `OldManNPC` | ≈4 新文件 + 1 新注册类 | `NPCSpawner.oldManSummoned` 与 `OldManNPC.summonSkeletron` 不再是硬依赖；`MoodEnvironment` 的 `instanceof OldManNPC` 可原样保留（**不必**改成 id 判据） |
| **25** | `NPCSpawner`（`git mv` + 换 1.20 内容 + 17 处引用改指）+ **53 个 npc 文件**（54 减 `OldManNPC`）+ **增量 `NpcEntities`** + 把约 21 处击杀判据改成 id 版（前置 `5d83b5236` 已落） | ≈55 文件 | **WP4 整层**；并连带解开 WP2 剩余 18 怪物 + 40 动物 + 枪械 G3′（`BaseBulletEntity -> BaseNPC -> NPCSpawner` 那条链断掉） |

> 口径说明：批次 25 里 `NPCSpawner` 的 `oldManSummoned(OldManNPC, Skeletron)` **原样保留**（批次 24 已把两个类型带进来），
> 因此**没有任何语义改写**；唯一改动是 21 处 `BossEntities.X.get()` → `Confluence.asResource("x")`，
> 以及包移动带来的引用改指。
