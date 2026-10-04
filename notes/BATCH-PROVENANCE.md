# 批次 → 来源提交 映射（逐提交溯源的补表）

- 区间 `795ac9ccc..18221c338`；1.21 侧从 `14fee792a` 之后逐提交列
- 读法：左列是 1.21 提交，右列是它动过的文件在 1.20 侧对应文件上**改过的 1.20 提交**
  （一个工作包覆盖多个 1.20 提交是常态，这正是「判定逐提交、执行按工作包」的产物）
- ⚠️ 粒度是**文件级**：某 1.20 提交出现在这里，只说明它改过这些文件，
  **不等于**本批次移植了那个提交的改动（例如 `dfcc5c041` 是既定的 DO-NOT-PORT 污染提交，
  它碰过的文件很多，本仓库从不移植它）。逐提交的判定仍以 `notes/COMMIT-LAG.md` 为准。
- 台账头部是 `C|<hash>`（不含主题），故右列只列 hash；主题查 `notes/PORT-LEDGER.md`

## `3a9048386` port(1.20 枪械内联 G2′): 落地 BaseGun + GunEvent（含 5 处 1.21 API 修正）；改指经实测暂缓并写明原因

- 改动 java 文件 3 个（映射到 1.20 3，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `90dfd7804` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `81488b6d0` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `95bb0294e` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `d879d8ffc` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `446c689a4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4122dbcf0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `12b6877be` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4af532ed1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b20c0cefd` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4298126f9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `0e370c928` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c9f3af990` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b3f13d405` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `20cbc0204` port(1.20 枪械内联 G1′): 主模组补枪械前置（GunDefinition + GUN/BULLET_PROPERTY + 自动开火配置 + 自动枪标签）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dc57ba5c2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f4b42537c` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `2ca9e2ac0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `300bd6cfe` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a6f8f089d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `182149f52` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2f44045a8` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5c3978b01` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `3e7cc41a9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4ef159bf9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1284d08a9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `8ce7f4d7f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 5 个

## `cde95394d` port(1.20 枪械内联前置 G0): 子模块补 lib/api/animation/first_person + 修正 G1~G6 批次顺序

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `55d1eecb8` port(1.20 WP2 批次 22): 动物刷怪放置层 —— CreatureSpawnPlacements 小动物半边 + SpawnPlacementChecks 子集

- 改动 java 文件 3 个（映射到 1.20 3，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `e145cafb5` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `ac2ac4428` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `701d48a08` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a34060571` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f212a001a` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `d5cdb0f89` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `5bd5b4211` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a4b321499` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a86216caf` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `041c84915` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `535992233` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 29 个

## `b4939c013` docs(port): WP5 核心 28 文件已落地（fe98072a3）—— 更新工作队列与 WP5B 清单状态

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `fe98072a3` port(1.20 WP5 批次 D): AttachmentEntity 核心 28 文件落地（5 个根因全部解决，编译门 0 错误）

- 改动 java 文件 31 个（映射到 1.20 31，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `f4ffadaa7` ×16 —— （主题见 notes/PORT-LEDGER.md）
  - `936551676` ×10 —— （主题见 notes/PORT-LEDGER.md）
  - `4a3436770` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `01f0936ac` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `e1cd9c8cb` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `c0e8c4c75` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `7169379cd` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `116bef809` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `27ee0313c` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `641467c87` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `9a3c5798b` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `af1426bed` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `64b7bbe9e` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `87b93ede2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `d3d8dcd24` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `417d561ec` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `6084476d0` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - …另 55 个

## `ea82af762` docs(port): 记录用户裁决与执行结果 + 枪械内联迁移（TerraGuns → 主模组）实测清单

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `ad89b41ba` fix(port): 把 1.20 的包移动真正做出来（common/data/saved -> common/data，7 个类 + 51 处引用）

- 改动 java 文件 62 个（映射到 1.20 55，无对应 7）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `e145cafb5` ×44 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×26 —— （主题见 notes/PORT-LEDGER.md）
  - `74a1fc885` ×21 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×21 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×19 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×15 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×13 —— （主题见 notes/PORT-LEDGER.md）
  - `c9f3af990` ×10 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×9 —— （主题见 notes/PORT-LEDGER.md）
  - `c0c6a321d` ×9 —— （主题见 notes/PORT-LEDGER.md）
  - `7b3b5c28e` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `29c1459cf` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `9a48d8619` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `10705abc7` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `417d561ec` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `093eda09f` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `182149f52` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `55cc8fc6b` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `31896204b` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - …另 124 个

## `c03dc6205` fix(tools): check_duplicates 跳过同 FQN 的移植目标（避免误报）+ 实测结论

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `1fd7f44cc` fix(port): 撤掉我误建的 3 个重复类（实为「包移动」）+ 新增「移动 vs 新增」审计闸门

- 改动 java 文件 3 个（映射到 1.20 3，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `1ed5e1daa` port(1.20 WP7 批次 B): 4 个自包含数据/工具叶子（BrushData/DateStamp/StarPhase/ShimmerDecompositionInputs）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `c2935419b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `59342cdda` port(1.20 WP7 批次 A): 两个战利品条件 entity_variant / difficulty_chance（+ 注册）

- 改动 java 文件 3 个（映射到 1.20 3，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `1284d08a9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `74a1fc885` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `182149f52` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `d4c9d56df` port(1.20 WP5 批次 C): 召唤体系粒子层（GenericParticleOptions/Builder + SummonerParticleTypes 原生化）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `7169379cd` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c0e8c4c75` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a34060571` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e145cafb5` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `417d561ec` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `641467c87` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4a3436770` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `01f0936ac` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f4ffadaa7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `856c0047f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `6084476d0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5c99e9702` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `647400f44` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2a4dfce2c` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4af532ed1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `231c505ca` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 11 个

## `9cc27098e` port(1.20 WP6 批次 G): 枪械数值/定义词汇 8 个（自包含，闭包 8/8 零扩张）

- 改动 java 文件 8 个（映射到 1.20 8，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `ce1ca67f8` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1516cbd2f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `e3820ac55` port(1.20 WP5 前置 2/2): mixed/Immunity 补 isActive/apply（1.20 对齐）+ ModPrefix.Summon 改判为 WP6 设计任务

- 改动 java 文件 1 个（映射到 1.20 1，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `97fc3ed2e` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c8e4e6416` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `dd890f61c` port(1.20 WP5 前置): 子模块 LibStreamCodecUtils 补 VEC_3（gitlink → 95133c3）+ WP5 核心首次尝试的逐文件错误图

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `1ded8d99c` port(1.20 WP5 批次 A): 召唤体系几何/数据基座 4 个纯类型（AttachmentEntity 开工）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `f4ffadaa7` ×4 —— （主题见 notes/PORT-LEDGER.md）

## `407b18d75` port(1.20 WP2 批次 21): 侏儒 Gnome + 花园侏儒方块 GardenGnomeBlock（自包含轻量叶子）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `10705abc7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `0b38e2e69` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f4b42537c` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fc3fabfb9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `05380071a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `3bc53d43d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e6eace2b1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `11a43615e` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `05b032399` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a086eeba6` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `286102066` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `81488b6d0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f212a001a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `300bd6cfe` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `041c84915` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `75c60a5fe` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 16 个

## `ebf33e08f` docs(port): 附魔簇判定「不移植」（1.21 已原生数据驱动实现）+ 怪物/全 common 剩余闭包度量

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `374f31592` docs(port): 批次 19/20 溯源补表 + 重算台账（NEEDS-PORT 176->172、COVERED 25->27）+ 同步 WP2 动物半边收口

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `bee3361ce` port(1.20 WP2 批次 20): 动物半边收口 —— 10 个动物 + 4 条邪恶转化注册（只剩 MysticFrog）

- 改动 java 文件 12 个（映射到 1.20 12，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dfcc5c041` ×9 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `7f83b379a` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `63916265b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `31896204b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c0c6a321d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `38382758a` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `75c60a5fe` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `300bd6cfe` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a086eeba6` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - …另 29 个

## `07e4a927b` port(1.20 WP2 批次 19): 动物注册层 CritterEntities 增量落地 + Cluckshroom/CloudSheep/GlowingMooshroom

- 改动 java 文件 5 个（映射到 1.20 5，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c0e8c4c75` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `7169379cd` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a34060571` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e145cafb5` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `417d561ec` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `641467c87` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4a3436770` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `01f0936ac` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f4ffadaa7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `856c0047f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `6084476d0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5c99e9702` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `647400f44` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2a4dfce2c` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4af532ed1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 25 个

## `a6305cd1e` docs(port): 重算逐提交台账（NEEDS-PORT 190->176、COVERED 18->25）+ 新增溯源补表工具 batch_provenance.py

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `7a4ee70bd` port(1.20 WP2 批次 18): 动物再落 11 个（WP2 动物半边已完成 26/41）

- 改动 java 文件 11 个（映射到 1.20 11，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dfcc5c041` ×11 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×10 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×9 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `0cf629c19` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `31896204b` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `5bd5b4211` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `55cc8fc6b` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `83c8eeb75` port(1.20 WP2 批次 17): 动物 11 个（自动物基类层之后，WP2 动物半边过半）

- 改动 java 文件 11 个（映射到 1.20 11，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `741f98d1e` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `0cf629c19` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `31896204b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `5bd5b4211` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `55cc8fc6b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fc43577f2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `63916265b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `81488b6d0` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `923fd7c8f` port(1.20 WP2 批次 16): 动物基类层 4 个文件（WP2 的动物半边开工）

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `741f98d1e` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `0cf629c19` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `31896204b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `97fc3ed2e` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `7f83b379a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `441334efe` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5bd5b4211` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `65fb9b375` port(1.20 坐骑簇前置 1/4): 子模块 gitlink 更新（LibDamageTypes +SUMMONER）

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `376a95df7` docs(port): 坐骑簇开工前的 4 个共享成员缺口 + 4 处 API 差异 + 转换器一处误报（第 12 轮实测）

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `35730c352` docs(port): 坐骑簇第 3 次推迟的逐文件错误清单 + 批次 15 补件记录

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `4805193fe` port(1.20 WP2 批次 15): 补上批次 13 推迟的 NPCShadowflameSkullProjectile + ProjectileHitRules

- 改动 java 文件 3 个（映射到 1.20 3，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dc57ba5c2` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9645da98c` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9378868e5` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `92e38df06` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a8e0b487f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dffefea8d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f6b8f73b0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f7996a657` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e7b826680` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `23b326a45` fix(port): ModTags 被误合并的一行拆回两行（批次 14 的收尾）

- 改动 java 文件 1 个（映射到 1.20 1，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `2f44045a8` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5c3978b01` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `3e7cc41a9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4ef159bf9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1284d08a9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `8ce7f4d7f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `231c505ca` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2569be361` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `c5e9f9be5` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `05172ec0a` port(1.20 WP2 批次 14): 枢纽里的 boss/NPC 叶子 4 个（坐骑簇整体推迟）

- 改动 java 文件 6 个（映射到 1.20 6，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dc57ba5c2` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `231c505ca` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9378868e5` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `3e7cc41a9` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `7d6b90edc` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `1284d08a9` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `92e38df06` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `841165c47` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 22 个

## `c64945a73` port(1.20 WP2 批次 13): 26 个自包含枢纽落地 23 个（18 个重量物种闭包 186 -> 163）

- 改动 java 文件 23 个（映射到 1.20 23，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dc57ba5c2` ×16 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×16 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×14 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×12 —— （主题见 notes/PORT-LEDGER.md）
  - `ce1ca67f8` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `92e38df06` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `231c505ca` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `2ca9e2ac0` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `1516cbd2f` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fc43577f2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b20c0cefd` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2569be361` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `841165c47` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 5 个

## `f4d075f04` port(1.20 WP2 批次 12): 被 18 个重量物种共享的 5 个数据/事件文件

- 改动 java 文件 4 个（映射到 1.20 4，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `ac2ac4428` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `7b3b5c28e` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a037263e1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `d5cdb0f89` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `38382758a` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4122dbcf0` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e2807cc12` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `9a48d8619` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `8a961bc0c` ×1 —— （主题见 notes/PORT-LEDGER.md）

## `c1312ca25` docs(port): 实测结论 —— WP2 剩余部分的前置是 WP4（NPC 层），并修正工作包顺序

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `009c13e7e` port(1.20 WP2 批次 11): 蜂群/沙虫/海盗三簇 8 个文件 + 环境事件生成否决钩子

- 改动 java 文件 13 个（映射到 1.20 13，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×10 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `92e38df06` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `46ae3a168` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `b20c0cefd` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `2569be361` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `11a43615e` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f212a001a` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `75c60a5fe` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - …另 55 个

## `9888dbb78` port(1.20 WP2 批次 10): 施法者群 6 个 + HostileParticleProjectile 枢纽 + 注册层第二片

- 改动 java 文件 12 个（映射到 1.20 12，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×10 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `11a43615e` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `ac457ea83` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `231c505ca` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f6b8f73b0` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `fbcb8e783` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `5bb8d5565` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - …另 59 个

## `7ab0bafd0` port(1.20 WP2 批次 9): 注册层首片（MonsterEntities）+ EaterOfSouls/BloodTumor/BloodySpore

- 改动 java 文件 5 个（映射到 1.20 5，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `dfcc5c041` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `5bb8d5565` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `19669033d` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `c0e8c4c75` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `7169379cd` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `1da5e0d26` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a34060571` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `e145cafb5` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `417d561ec` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 49 个

## `ffa375c24` docs(tools): README 补第 4 条过程纪律（提交信息文件只用写盘工具生成）

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `83e234149` docs(port): WORK-QUEUE 记 WP6a 完成情况与 WP6c 开工清单

- 改动 java 文件 0 个（映射到 1.20 0，无对应 0）
- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）

## `04dbc7a20` port(1.20 WP2 批次 8): 效果层放行的 4 个物种 + PlantSpit + 新规则 libeffects-holder-unwrap

- 改动 java 文件 6 个（映射到 1.20 6，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `46ae3a168` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `f6e114cdb` ×2 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2818e719b` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `300bd6cfe` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `5bb8d5565` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `b20c0cefd` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `2569be361` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - `4298126f9` ×1 —— （主题见 notes/PORT-LEDGER.md）
  - …另 17 个

## `0cd33b090` port(1.20 WP6a 批次 3): 主模组改用 Magic Lib 的 LibEffects + lang 键改名 + 迁移笔记

- 改动 java 文件 16 个（映射到 1.20 13，无对应 3）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `1c012ccb1` ×12 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `b05c8dc3f` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `29c1459cf` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `911437e03` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `90dfd7804` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `c9f3af990` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `41c27595c` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `dbac6afed` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `7572f2ebd` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `00b72167d` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `46e5e8626` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `e145cafb5` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `cfe6ac100` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `446c689a4` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `55cc8fc6b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `100f6e0f2` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×3 —— （主题见 notes/PORT-LEDGER.md）
  - …另 94 个

## `51dcbadde` port(1.20 WP2 批次 7): 轻量物种 18 个 + ModEntities 增量注册 4 条 + 新工具 seed_closures.py

- 改动 java 文件 19 个（映射到 1.20 19，无对应 0）
- 覆盖的 1.20 提交（按命中文件数排序）：
  - `b05c8dc3f` ×13 —— （主题见 notes/PORT-LEDGER.md）
  - `741f98d1e` ×11 —— （主题见 notes/PORT-LEDGER.md）
  - `9bc04295b` ×11 —— （主题见 notes/PORT-LEDGER.md）
  - `dfcc5c041` ×11 —— （主题见 notes/PORT-LEDGER.md）
  - `dc57ba5c2` ×9 —— （主题见 notes/PORT-LEDGER.md）
  - `900c068f7` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `84b1939df` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `c2935419b` ×8 —— （主题见 notes/PORT-LEDGER.md）
  - `a6fd78681` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `348c877a4` ×7 —— （主题见 notes/PORT-LEDGER.md）
  - `b20c0cefd` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `f6e114cdb` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `dffefea8d` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `a358494fa` ×6 —— （主题见 notes/PORT-LEDGER.md）
  - `fac72523a` ×5 —— （主题见 notes/PORT-LEDGER.md）
  - `ef1a4d138` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `5bb8d5565` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `ac457ea83` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `6568d3ad1` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - `f4625b5c4` ×4 —— （主题见 notes/PORT-LEDGER.md）
  - …另 27 个
