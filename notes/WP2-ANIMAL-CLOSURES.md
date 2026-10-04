# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

> **本文件在 WP2 批次 20 之后重算过**：1.20 `common/entity/animal` 的 41 个 java 里，
> 1.21 侧现在只缺 `MysticFrog`（+ 无语义的 `package-info.java`），所以表里只剩它一个种子。
> 动物半边的**历史测量**（开工时的 36 个种子、公共底座排行、以及 `CritterEntities` 落地前后
> 171 → 3 的收敛）保留在逐批次笔记里：`notes/WP2Q-SUBSET.md`（批次 19）、
> `notes/WP2R-SUBSET.md`（批次 20）、`notes/WP2Q-CLOSURES.md`、`notes/WP2R-CLOSURES.md`。

- 种子 **1** 个；1.21 侧可扩张类型 704 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤8 个文件：**0** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |

## 重量种子（闭包 >8 个文件：**1** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `MysticFrog` | 157 | 156 | 20919 | 52 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**0** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
