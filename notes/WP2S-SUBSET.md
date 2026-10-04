# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 2 个：`WindyBalloon`, `Gnome`
- 扩张后候选 **2** 个 `org.confluence.*` 类型
- 本批保留 **2** 个（其中 1.21 侧**新增** 2 个），因 `--defer` 剔除 **0** 个
- `--alias` 3 条（跨分支改过包路径的等价关系，不算新增）：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.monster.Gnome`
- `org.confluence.mod.common.entity.monster.WindyBalloon`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_
