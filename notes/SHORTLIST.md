# 移植短名单（机器筛 + 人工口径）

筛选链：**386 个提交** → 去掉纯 Port 化/忽略路径（约 188 个）→ 机器覆盖率检查（NEEDS-PORT 162 / COVERED 11 / UNCLEAR 25）→ 去掉架构改造（arch-churn）→ **剩余待处理短名单**。

> 机器语义：`coverage` = 该提交新增行在 1.21 对应文件里已存在的比例。**高覆盖率（≥80%）可信**（说明 1.21 本来就有，例如 1.20 抄了 1.21 的 TerraGuns/剑类/TerraBlender）；**低覆盖率既可能是真内容、也可能是 1.20 的平台适配**（1.21 已正确），必须人工过一眼。

## A. 待处理短名单：内容性质（建议按此顺序推进）（87）

| 提交 | 日期 | 说明 | 新增行 | 覆盖 | 子模块侧 |
|---|---|---|---|---|---|
| `a358494fa` | 2026-09-12 | 修复敌怪生成与战斗行为，完善武器渲染、NPC 广播及翻译 | 2322 | 1% |  |
| `3e7cc41a9` | 2026-09-06 | Boss 血条 - 新增 CustomBossBarRenderer，使用边框和填充纹理绘制 | 1377 | 3% |  |
| `84b1939df` | 2026-09-08 | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 | 1197 | 5% | Confluence-Magic-Lib×1 |
| `a6fd78681` | 2026-08-30 | feat(entity): 完善普通敌怪、NPC与召唤物的行为和渲染 | 1178 | 1% | Confluence-Magic-Lib×1 |
| `159af78f9` | 2026-08-16 | fix: 修复内置 NPC 商店资源加载 | 1104 | 0% |  |
| `38382758a` | 2026-09-09 | fix(otherworld): 修复召唤物行为、蜗牛爬行与武器逻辑 | 890 | 1% | PortLib×1 |
| `ac457ea83` | 2026-09-13 | fix: 修复敌怪行为、施法表现、NPC交互及鞭子回收 | 593 | 3% |  |
| `f68cf4b17` | 2026-09-03 | 删除多余的图鉴键注册 | 556 | 0% |  |
| `446c689a4` | 2026-09-07 | 修复成就json 修复剑的属性定义问题 修复雀杖的雀在常规状态下抽风 修复召唤物小雪怪移速过 | 468 | 24% |  |
| `535992233` | 2026-09-14 | 为现有新增的怪物补全点需要的东西 | 414 | 6% | TerraCurio×1 |
| `3f2eb3be2` | 2026-08-23 | fix: 重构悠悠球系统与客户端武器输入架构 | 389 | 2% |  |
| `48c509684` | 2026-08-20 | fix(summon): 严格对齐 1.21.1 召唤体系行为与渲染 | 345 | 0% |  |
| `63916265b` | 2026-07-08 | feat: 新增实体并修复命名空间引用 | 335 | 7% |  |
| `9e00dbf7b` | 2026-08-16 | fix: 拉通 NPC 商店条件与交易结算 | 263 | 2% |  |
| `fe090753f` | 2026-08-16 | fix: 对齐 NPC 交互与对话同步行为 | 240 | 0% |  |
| `4ef159bf9` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | 237 | 17% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `7b3b5c28e` | 2026-09-03 | 渔夫任务系统修改 | 235 | 9% | PortLib×1, Confluence-Magic-Lib×1, TerraFurniture×1 |
| `d5cdb0f89` | 2026-09-12 | fix(otherworld): 修复黄蜂寻路、悠悠球增伤与渔夫任务 | 233 | 2% |  |
| `c2d92b019` | 2026-08-16 | refactor: 按 Servantry 架构对齐召唤系统 | 217 | 0% |  |
| `b861536ae` | 2026-08-16 | refactor: 恢复 NPC 商店数据加载架构 | 216 | 0% |  |
| `55cc8fc6b` | 2026-09-07 | 生产环境修复 | 213 | 5% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `c6c2d493a` | 2026-08-16 | fix: 恢复 NPC 商店三态交易流程 | 206 | 0% |  |
| `e1cdbb3ff` | 2026-08-16 | feat: 接通 NPC 交互与聊天同步链路 | 200 | 0% |  |
| `5bd5b4211` | 2026-08-17 | fix: 对齐普通生物自然生成规则 | 152 | 24% |  |
| `d742dcf44` | 2026-08-16 | feat: 补齐 NPC 默认聊天内容与触发条件 | 132 | 0% |  |
| `8a961bc0c` | 2026-09-11 | 一些修复 | 132 | 14% | PortLib×1, Confluence-Magic-Lib×1, TerraFurniture×1 |
| `7e8662bd4` | 2026-08-16 | refactor: 恢复 NPC 商品的组件定价模型 | 130 | 0% |  |
| `4b004c160` | 2026-06-10 | fix2 | 122 | 7% | PortLib×1, Confluence-Magic-Lib×1 |
| `d6c99c740` | 2026-08-17 | fix: 拉通Boss属性部件伤害与掉落行为 | 111 | 9% |  |
| `9729f1c30` | 2026-08-16 | refactor: 按 Servantry 架构对齐召唤系统 | 105 | 0% |  |
| `b2d1d939c` | 2026-08-16 | fix: 对齐 NPC 战斗治疗与生命周期行为 | 105 | 0% |  |
| `024a28ac7` | 2026-08-22 | fix(confluence): 补齐 NPC 交互与实体属性对齐 | 104 | 1% |  |
| `1e2f65769` | 2026-08-16 | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 | 93 | 0% |  |
| `7d6b90edc` | 2026-08-16 | fix: 拉通 NPC 住宅交互与生命周期行为 | 89 | 7% |  |
| `c406dcc0b` | 2026-08-16 | fix: 对齐城镇 NPC 敌我识别与恐慌行为 | 87 | 0% |  |
| `08276f2ca` | 2026-08-17 | fix: 恢复自定义矿车创建与放置行为 | 77 | 8% |  |
| `64950f063` | 2026-08-16 | fix: 修正 NPC 商店生成内容与商品价值 | 74 | 3% |  |
| `188ade36f` | 2026-08-16 | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 | 68 | 0% |  |
| `116edf192` | 2026-08-16 | fix: 对齐 NPC 住宅与旅商生命周期行为 | 67 | 6% |  |
| `27e9e0ac3` | 2026-08-16 | refactor: 清理 NPC 对齐残留并恢复原有架构 | 66 | 2% |  |
| `fbcb8e783` | 2026-06-16 | fix crash | 65 | 8% | PortLib×1, Confluence-Magic-Lib×1 |
| `0cf629c19` | 2026-09-06 | 属性静态字段注入 | 63 | 18% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `2a9014345` | 2026-08-16 | fix: 对齐 NPC 固定商店商品与出售条件 | 62 | 3% |  |
| `a6f8f089d` | 2026-07-25 | fix world selection | 61 | 0% | PortLib×1 |
| `91724b1ce` | 2026-09-13 | 为水生生物添加自然巡游 | 61 | 0% |  |
| `5c99e9702` | 2026-09-07 | 生产环境修复 | 57 | 26% | PortLib×1, TerraCurio×1, TerraFurniture×1 |
| `6ec97fb52` | 2026-08-16 | refactor: 恢复 NPC 原有交互架构 | 54 | 0% |  |
| `72421955a` | 2026-08-17 | fix: 拉通召唤战斗与储物伙伴行为 | 52 | 0% |  |
| `d0fe20495` | 2026-08-16 | refactor: 将 NPC 默认聊天数据迁入 datagen | 50 | 0% |  |
| `d3ae22457` | 2026-08-17 | fix: 完善实体渲染资源校验并清理注册噪音 | 44 | 0% |  |
| `95bb0294e` | 2026-09-08 | 修复汇流箱子打不开的问题 | 44 | 2% |  |
| `7dc9244e4` | 2026-08-17 | style: 清理新增架构类中的无意义拆行 | 43 | 0% |  |
| `12b6877be` | 2026-08-23 | 修复枪械动画报错 | 43 | 0% |  |
| `e7703e76d` | 2026-08-16 | fix: 保证 NPC 商店报价与交易条件一致 | 41 | 0% |  |
| `1c40b0ecc` | 2026-08-16 | fix: 补充 NPC 交互事件 | 39 | 0% |  |
| `a86216caf` | 2026-08-17 | fix: 拉通普通生物注册生成与属性语义 | 34 | 0% |  |
| `751055e45` | 2026-09-10 | 修复种子按钮位置，修复世界类型图标 | 23 | 13% |  |
| `7a3e9f664` | 2026-08-16 | fix: 同步 NPC 商店权威价格显示 | 21 | 0% |  |
| `ab0d06315` | 2026-08-17 | fix: 拉通坐骑移动与交互行为 | 20 | 0% |  |
| `673efd7d5` | 2026-09-12 | 调整标签 悠悠球无敌帧 | 20 | 10% |  |
| … | | 另有 27 个 | | | |

## B. 待归类（other）：需你确认是内容还是架构（41）

| 提交 | 日期 | 说明 | 新增行 | 覆盖 | 子模块侧 |
|---|---|---|---|---|---|
| `dbac6afed` | 2026-06-04 | part | 28 | 29% | Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×3 |
| `6c1a7a656` | 2026-06-07 | part3 | 4 | 0% | Confluence-Magic-Lib×1, TerraCurio×1 |
| `72adcfeae` | 2026-06-08 | part7 | 67 | 8% | Confluence-Magic-Lib×1, TerraFurniture×1 |
| `45fefd336` | 2026-06-10 | datamap datagen | 3 | 0% | PortLib×1, TerraCurio×1 |
| `b3f13d405` | 2026-06-13 | part11 | 372 | 5% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `b0716f0c9` | 2026-06-14 | part13 | 184 | 2% | PortLib×1, Confluence-Magic-Lib×1 |
| `0b38e2e69` | 2026-06-14 | part14 | 334 | 24% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `c85213f17` | 2026-06-14 | part15 | 26 | 0% |  |
| `13a467d59` | 2026-06-14 | enum extend | 59 | 7% |  |
| `a779580be` | 2026-06-14 | part16 | 389 | 6% | PortLib×1 |
| `7646c5505` | 2026-06-15 | 物品移植 | 431 | 17% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `093eda09f` | 2026-06-16 | part17 | 262 | 5% | PortLib×1, Confluence-Magic-Lib×1 |
| `8bcc392be` | 2026-06-16 | mob effect | 43 | 7% | PortLib×1 |
| `b33c206fa` | 2026-06-18 | something3 | 988 | 15% |  |
| `f7996a657` | 2026-06-18 | rename | 306 | 1% |  |
| `4dcf95cfe` | 2026-06-18 | 移植家具 | 57 | 5% | PortLib×1, TerraCurio×1, TerraFurniture×1 |
| `c5e9f9be5` | 2026-06-19 | part18 | 259 | 20% | PortLib×1, Confluence-Magic-Lib×1 |
| `10705abc7` | 2026-06-21 | part19 | 245 | 18% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `0e370c928` | 2026-06-24 | data component method rename | 40 | 22% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `4298126f9` | 2026-06-24 | IPortItemExtension | 211 | 29% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `b20c0cefd` | 2026-06-27 | remove all entity part | 29 | 0% |  |
| `ac7767860` | 2026-06-28 | part25 | 104 | 2% |  |
| `058000c5c` | 2026-06-29 | part enchantment | 341 | 5% |  |
| `1516cbd2f` | 2026-06-30 | part fluid type | 110 | 3% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `911437e03` | 2026-07-04 | able to start game | 3761 | 4% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `97fc3ed2e` | 2026-07-08 | npc goal | 43 | 2% |  |
| `bd006659b` | 2026-07-11 | refactor: 重命名饿鬼实体类为HillHungry | 1 | 0% |  |
| `a8c5395a6` | 2026-08-16 | refactor: 清理跨版本移植残留与格式噪音 | 12 | 8% |  |
| `3b8f62076` | 2026-08-17 | refactor: 清理既有逻辑中的迁移噪音 | 77 | 22% |  |
| `0067364f0` | 2026-08-17 | refactor: 清理混入迁移噪音与编译警告 | 8 | 0% |  |
| `4ac81edb7` | 2026-08-18 | 药水效果（未完成） | 32 | 0% | PortLib×1, Confluence-Magic-Lib×1 |
| `1c012ccb1` | 2026-08-22 | 将饰品的药水效果转移至lib | 89 | 2% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `c2935419b` | 2026-08-23 | docs: 清理源码注释中的冗余 HTML 段落标签 | 845 | 0% |  |
| `06415ab96` | 2026-09-02 | 删除AI乱改的进度系统，部分事件改用原生类 | 193 | 23% |  |
| `8ca8275f6` | 2026-09-02 | 删除AI乱改的进度系统，部分事件改用原生类 | 1 | 0% |  |
| `4bd94d674` | 2026-09-12 | 关键帧多效果支持 | 1 | 0% |  |
| `6084476d0` | 2026-09-12 | JEI兼容恢复 | 117 | 14% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1, TerraFurniture×1 |
| `7525f7829` | 2026-09-14 | 激光模型 | 60 | 0% |  |
| `aa1e02f44` | 2026-09-14 | 新铁傀儡模型 | 658 | 0% |  |
| `62d4d191a` | 2026-09-14 | 翻新已经有的模型 | 1065 | 0% |  |
| … | | 另有 1 个 | | | |

## C. 架构改造（arch-churn）：归 Phase 1 收割 / Phase 3 模块对齐，不做逐提交移植（34）

| 提交 | 日期 | 说明 | 新增行 | 覆盖 | 子模块侧 |
|---|---|---|---|---|---|
| `f4b42537c` | 2026-06-10 | part8 | 3726 | 1% | Confluence-Magic-Lib×1, TerraCurio×1 |
| `e7b826680` | 2026-06-10 | part9 | 645 | 0% | Confluence-Magic-Lib×1, TerraCurio×1 |
| `00b72167d` | 2026-06-10 | 枪械合并 | 7319 | 13% | Confluence-Magic-Lib×1 |
| `182149f52` | 2026-06-13 | part10 | 784 | 10% | PortLib×1, Confluence-Magic-Lib×1 |
| `7d1fff5b6` | 2026-06-13 | part12 | 741 | 4% | PortLib×1, Confluence-Magic-Lib×1 |
| `f30688d17` | 2026-06-15 | update: 将多个方块迁移至 PortLib API 并调整方法签名 | 1033 | 12% | PortLib×1 |
| `f6b8f73b0` | 2026-06-22 | part20 | 975 | 0% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `dffefea8d` | 2026-06-23 | feat: 添加多个怪物实体类、AI系统和相关工具类 | 6527 | 0% |  |
| `f6e114cdb` | 2026-06-24 | part21 | 6701 | 0% | PortLib×1 |
| `2569be361` | 2026-06-27 | part22 | 11409 | 0% | Confluence-Magic-Lib×1 |
| `7f83b379a` | 2026-06-27 | part23 | 3745 | 0% |  |
| `c0c6a321d` | 2026-06-28 | part24 | 3216 | 2% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `231c505ca` | 2026-06-30 | part npc | 1282 | 0% |  |
| `6568d3ad1` | 2026-06-30 | part critters & monsters | 1740 | 0% |  |
| `8ce7f4d7f` | 2026-07-01 | part recipe datagen | 1199 | 6% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `9a48d8619` | 2026-07-01 | part npc1 | 1414 | 0% | PortLib×1 |
| `fac72523a` | 2026-07-02 | part critters | 5711 | 5% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `1284d08a9` | 2026-07-04 | able to into world | 102119 | 0% | PortLib×1, Confluence-Magic-Lib×1, TerraCurio×1 |
| `dfcc5c041` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | 99506 | 10% | PortLib×1, Confluence-Magic-Lib×1 |
| `dc57ba5c2` | 2026-08-16 | 注释 杀杀杀 | 4964 | 15% | PortLib×1, Confluence-Magic-Lib×1 |
| `f4625b5c4` | 2026-08-17 | fix: 拉通生物行为并统一属性访问方式 | 423 | 1% |  |
| `90dfd7804` | 2026-08-18 | refactor(combat): 重构剑类、剑气与枪械系统 | 2569 | 5% |  |
| `9bc04295b` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | 6135 | 2% | Confluence-Magic-Lib×1 |
| `ce1ca67f8` | 2026-08-22 | refactor(confluence): 拉通 1.21 战斗、召唤与实体体系 | 3387 | 2% | Confluence-Magic-Lib×1 |
| `5bb8d5565` | 2026-08-23 | refactor(entity): 拉通敌怪行为、肉墙机制与客户端渲染 | 1734 | 0% |  |
| `9645da98c` | 2026-08-24 | feat(otherworld): 重构城镇 NPC 战斗体系并补全生物相关内容 | 2195 | 0% |  |
| `92e38df06` | 2026-09-01 | feat(otherworld): 重构 Boss 战斗、蠕虫体节与属性覆盖架构 | 4110 | 0% |  |
| `19669033d` | 2026-09-02 | feat(otherworld): 重构战斗 AI、Boss 行为与 NPC 交互系统 | 1223 | 0% |  |
| `e2807cc12` | 2026-09-03 | feat(otherworld): 完善资源生成、渔夫任务与图鉴变种体系 | 1049 | 24% |  |
| `741f98d1e` | 2026-09-06 | feat(otherworld): 完善生物行为、NPC交易与配置界面 | 5802 | 1% | PortLib×1 |
| … | | 另有 4 个 | | | |
