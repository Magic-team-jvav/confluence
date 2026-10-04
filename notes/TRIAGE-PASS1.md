# 快筛第一轮报告（2026-09-27）

目标：把 386 行里「无需逐提交判断」的部分按**可审计规则**销账，宁可留 TODO 也不误销。

## 规则与命中

| 规则 | 命中 | 说明 |
|---|---|---|
| R1 方向 = port-ing/de-port | 13 | 新增行以 Port 引用为主 → 1.21 保留原生即可（上一轮） |
| R2 只改资源/数据、无 java | 27 | 记 `DEFER-ASSETS`：随所属功能提交一起处理，不单独推进 |
| R4 submodule-only 且子模块净改动小（≤20 文件、新增 ≤50 行） | 28 | `SKIP-PLATFORM`：part 系列接线/Port 化滚动 |
| R3 架构级搬迁（改名≥25，或改名≥10 且新增行≥4000） | 2 | `DEFER-ARCH`：归 Phase 1 收割 / Phase 3 模块对齐（`2569be361` part22 137 处改名、`e145cafb5` 大一统重构） |
| 覆盖率 COVERED（≥80% 新增行已在 1.21） | 11 | `COVERED`：1.20 从 1.21 抄回去的内容（机器判定待抽查） |

R3 首版阈值（改名≥10）误伤 3 行（`a358494fa` 内容短名单行、`e2807cc12`、`9378868e5`），已退回 TODO 人工看。

## 台账现状

| 状态 | 行数 |
|---|---|
| TODO | 295 |
| SKIP-PLATFORM | 48 |
| DEFER-ASSETS | 27 |
| COVERED | 12 |
| DEFER-ARCH | 2 |
| SKIP-1.21-KEEPS | 1 |
| DO-NOT-PORT | 1 |

## 剩余 TODO 的构成

| 类别 | 行数 |
|---|---|
| 机器判 NEEDS-PORT（含内容短名单 87 + 待归类 41） | 147 |
| 其它（无计入新增行/微改） | 102 |
| 机器判 UNCLEAR（人工过一眼） | 24 |
| submodule-only 但子模块侧改动较大（需查链接表） | 19 |
| 含 integration（Q12 多半跳过） | 3 |

## 下一步

1. 走 `notes/SHORTLIST.md` 的 A 表（87 行内容性质），按台账顺序推进；
2. 剩余「含 integration」25 行按 Q12 快速过一遍（多半 `SKIP-1.21-KEEPS`）；
3. 剩余「submodule-only 且改动较大」行，用 `notes/SUBMODULE-LINKS.md` 的区间净改动逐个判。