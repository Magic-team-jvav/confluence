# 「移动」被当成「新建」的事故与处置（`check_duplicates.py` 的由来）

> 本文记录一次**真实事故**（用户当面指出：「你怎么把类的移动移植成了新建类，原类还在」），
> 以及为它补的**流程闸门**与**处置政策**。

## 一、事故是什么

`dep_subset.py` 的判定是**类型级**的：它拿 1.20 的 FQN 去 1.21 里找，找不到就判「缺失」。
**包移动**（1.20 把类从 A 包挪到 B 包）在它眼里就是「A 包的类缺失」—— 于是把一次**移动**报成了**新类**。

移植的人（本次会话的我）照着报单在 B 包**新建**了一份，A 包的**原类与其全部引用原封不动**。
结果：树里出现**两个同名类、两套实现**，新那份还是**死代码**（没有任何引用）。

### 本次实际发生（3 个文件，已在 1.21 侧累积了两批）

| 类 | 1.20 位置（事实来源） | 1.21 原有位置 | 谁造的重复 | 现状 |
|---|---|---|---|---|
| `BrushData` | `common/data/` | `common/data/saved/` | **本次会话 WP7B（`1ed5e1daa`）** | 已撤（本提交） |
| `DateStamp` | `common/data/` | `common/data/saved/` | **本次会话 WP7B（`1ed5e1daa`）** | 已撤（本提交） |
| `StarPhase` | `common/data/` | `common/data/saved/` | **本次会话 WP7B（`1ed5e1daa`）** | 已撤（本提交） |
| `MoonPhase` | `common/data/` | `common/data/saved/` | **WP2 批次 12（`f4d075f04`）** | ⚠️ **仍未处置**：`common/data/MoonPhase.java`（新，与 1.20 逐字相同）与 `common/data/saved/MoonPhase.java`（旧，内容不同）并存 |

三对的实际差异只有**包声明**（`BrushData`/`DateStamp` 另有形参名 `level`→`serverLevel`、
`instanceof` 写法差异），即**纯包移动**。也就是说我造的那 3 个是**百分百重复**。

### 另一类同名（不是事故，但必须写明）

| 类 | 1.20 位置 | 1.21 侧同名 | 性质 |
|---|---|---|---|
| `GunStats`/`AmmoStats`/`Ballistics`/`BallisticsResolver`/`ShotContext`/`FireMode`/`GunProjectilePattern`/`GunPropertyComponent` | `common/combat/gun/`、`common/item/gun/definition/`、`common/component/` | **TerraGuns 子模块** `org.confluence.terra_guns.{common.combat,common.definition,common.component}.*`（不同包、不同实现） | 属既定的「**TerraGuns/TerraEntity 退役、枪械与实体内联进主模组**」过渡态（先加后删）。**但我在 WP6G 的提交里写成「在 1.21 侧此前都不存在」，这句是错的** —— 应当写「TerraGuns 里有**旧实现**的同名类，本批是内联迁移的第一步，退役批次里删除旧类并把主模组引用改指过来」 |

## 二、补的闸门：`tools/port2native/check_duplicates.py`

**跑法（每个批次 staging 之前必须跑）**：

```powershell
python tools/port2native/check_duplicates.py `
    --src120 D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
    --ref    . `
    --file org/confluence/mod/common/data/BrushData.java `
    --file ...            # 或 --all（全量扫，慢）
```

- 扫描范围是**整个 1.21 仓库、含全部子模块**（`build/`、`.git`、`run*` 除外）——
  这正是我此前漏掉的一环：我只搜了 `ConfluenceOtherworld/src/main/java`，
  所以 TerraGuns 里的同名类和 `common/data/saved/` 里的同名类都没被看见。
- 判定：`SAME`（连包都一样，纯重复）/ `MOVE`（只差包声明，**是移动**）/
  `NEAR`（归一化相似度 ≥ 85%，疑似同一类的改写版）/ `DIFF`（不同实现）。
- 输出**建议的 alias 行**，可直接粘进工具调用参数或下面的别名表。

## 三、处置政策（两条路，二选一；不要「新建」）

1. **登记等价（推荐，团队既有做法）**：把这类关系写进别名表，让 `dep_subset`/`seed_closures`
   把它当「已满足」，从此不再报缺失、也不会有人再建副本。项目里已有先例：
   `org.confluence.mod.common.data.GamePhase=org.confluence.mod.common.data.saved.GamePhase`。
2. **整批迁移（真移动）**：`git mv` 全部同族文件 + 改所有引用 + 编译门。
   代价明确：本次同族共 **7 个类 / 51 处引用**（`BrushData` 13、`GamePhase` 16、
   `MoonPhase` 8、`Team` 8、`DateStamp` 3、`StarPhase` 2、`SpecificMoonVariant` 1），
   而且必须**整族一起做**（半迁移会留下更糟的不一致）；`GamePhase` 迁移后既有的 alias 行要一并删掉。

### 建议补进别名表（等用户裁决，见第五节）

```
org.confluence.mod.common.data.BrushData=org.confluence.mod.common.data.saved.BrushData
org.confluence.mod.common.data.DateStamp=org.confluence.mod.common.data.saved.DateStamp
org.confluence.mod.common.data.StarPhase=org.confluence.mod.common.data.saved.StarPhase
org.confluence.mod.common.data.MoonPhase=org.confluence.mod.common.data.saved.MoonPhase
org.confluence.mod.common.data.Team=org.confluence.mod.common.data.saved.Team
org.confluence.mod.common.data.SpecificMoonVariant=org.confluence.mod.common.data.saved.SpecificMoonVariant
```

## 四、本提交做了什么

1. **撤掉我造出的 3 个重复类**：`common/data/{BrushData,DateStamp,StarPhase}.java`
   （`git rm`；它们在撤掉前是死代码 —— 全仓搜索确认除自身外**零引用**，删掉不影响编译）。
2. 新增 `tools/port2native/check_duplicates.py`（本闸门）+ 本文。
3. WP7B 那一批因此**只剩 `ShimmerDecompositionInputs` 一个真新增文件**（其余 3 个改判为「移动」）。
4. 修正 WP6G 的定性（TerraGuns 内已有旧同名类，属内联迁移过渡态）。

## 五、用户裁决与执行结果（2026-09-27）

用户裁决：**同族包移动 =「整批真迁移」**（不采用只登记 alias 的省事方案）；
**TerraGuns 同名枪械类 = 立刻做枪械内联迁移**（清单见 `notes/GUNS-INLINE-MIGRATION.md`）。

### 5.1 包迁移已执行（提交 `ad89b41ba`）

| 动作 | 内容 |
|---|---|
| `git mv` ×6 | `BrushData` / `DateStamp` / `GamePhase` / `SpecificMoonVariant` / `StarPhase` / `Team`：`common/data/saved/` → `common/data/`，package 行同步改（git 按 rename 记录，相似度 98%） |
| 合并 ×1 | `MoonPhase`：目标 `common/data/MoonPhase.java` 已存在（WP2 批次 12 那份，与 1.20 逐字相同 = 事实来源）→ `git rm` 旧的 `saved/MoonPhase.java`，8 处引用改指新位置 |
| 引用改写 | 49 个文件（import 行 + 全限定名），`data.saved.<X>` → `data.<X>` |
| 补 import | `data/saved/` 内留存文件的同包裸引用：`ConfluenceData`(StarPhase)、`KillBoard`(GamePhase)、`NPCSpawner`(GamePhase) |
| 通配导入 | `ModEvents`/`TickEvents`/`ModCommands` 三处 `data.saved.*` 需同时可见新包 → 保留原行并补 `common.data.*` 一行（这三处**不能用 `Set-Content` 写**，PS 5.1 会写 BOM；已用 python 去 BOM 并核对 diff 只含 import 行） |

编译门：第一轮 14 处错误（全在 `ModCommands`，通配导入未覆盖新包）→ 修复后 **0 错误**。

### 5.2 ⚠️ `GamePhase` 的 `--alias` 自本批起**已失效**

```
org.confluence.mod.common.data.GamePhase=org.confluence.mod.common.data.saved.GamePhase   # 不再需要
```

两侧 FQN 现在**相同**（`org.confluence.mod.common.data.GamePhase`），该 alias 成了恒等映射。
后续跑 `dep_subset.py` / `seed_closures.py` 时**可以省掉这一条**；
仍在文本里引用它的地方（`notes/WP2M-SUBSET.md`、`tools/port2native/check_duplicates.py` 的示例注释）
保留为历史记录，但**不要再照抄进命令**。

### 5.3 枪械内联迁移

清单与批次切分见 `notes/GUNS-INLINE-MIGRATION.md`（99 文件 / 30 处引用 / G1~G6 六批）。

### 5.4 教训固化

1. **staging 前必须跑 `check_duplicates.py`**（README 过程纪律 5），它是本事故唯一的自动化防线；
2. **别名表就是「已知的移动」登记处**：一条 alias = 「这两处 FQN 是同一个类，别再搬」；
3. **提交信息里不许写「1.21 侧此前不存在」**，除非已用 `check_duplicates.py`（含子模块）证明过；
4. 包移动的落地形态只有两种：**登记 alias** 或 **整批 git mv + 改引用**；**绝不允许两边并存**。
