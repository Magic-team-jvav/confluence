# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）

- 种子 **11** 个；1.21 侧可扩张类型 714 个
- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，已满足的边算 0 成本、不继续走
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 按单独闭包从小到大（轻量 ≤12 个文件：**10** 个种子）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `Snail` | 1 | 0 | 394 | 394 |  |
| `Fairy` | 1 | 0 | 379 | 379 |  |
| `Goldfish` | 1 | 0 | 96 | 96 |  |
| `Penguin` | 1 | 0 | 48 | 48 |  |
| `RedSquirrel` | 1 | 0 | 22 | 22 |  |
| `JewelSquirrel` | 1 | 0 | 21 | 21 |  |
| `Bunny` | 2 | 1 | 258 | 210 |  |
| `HostileBunny` | 2 | 1 | 258 | 48 |  |
| `ExplosiveBunny` | 3 | 2 | 306 | 48 |  |
| `JewelBunny` | 3 | 2 | 292 | 34 |  |

## 重量种子（闭包 >12 个文件：**1** 个）

| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |
| --- | ---: | ---: | ---: | ---: | :--: |
| `MysticFrog` | 157 | 156 | 20919 | 52 |  |

## 公共底座（出现在 ≥2 个种子的闭包里：**2** 个）

**先搬这些，后面每个种子都会变轻。**

| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |
| --- | ---: | ---: | ---: |
| `common.entity.animal.HostileBunny` | 3 | 48 | 2 |
| `common.entity.animal.Bunny` | 3 | 210 | 2 |
