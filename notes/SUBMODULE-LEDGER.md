# 子模块对齐台账（SUBMODULE-LEDGER）

> 目的：把主台账 `notes/PORT-LEDGER.md` 的「逐行行走」口径推广到三个子模块。**移植源 = 1.20 侧子模块 HEAD**；
> 1.21 侧子模块保持**原生 NeoForge**（PortLib 一律不移植）。两问法与主台账一致：
> ① 该新增行在 **1.20 子模块 HEAD** 还在不在（不在 ⇒ 1.20 自己撤/改，无移植物）；
> ② 在的话，**1.21 同子模块**有没有等价实现（比对前做 `this./.get()/缩进/折行/getRandom1211()` 归一）。
> 范围：1.20 侧各子模块在**分叉点 2026-07-04** 之后的提交（lib 65 / TerraCurio 53 / TerraFurniture 25）。
>
> 状态取值：TODO / PORTED / COVERED / SKIP-PLATFORM / SKIP-PORTLIB / SKIP-1.20-REVERTED / REVERSE-ALIGNED /
> DEFER-ASSETS / DEFER-ARCH / DO-NOT-PORT / LOST?。回填见 `notes/submodule-ledger-status.json`。

| 行 | 子模块 | 提交 | 日期 | 主题 | 文件 | +/− | 标记 | 状态 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Confluence-Magic-Lib | `02525eea4` | 2026-07-04 | able to start game | 4 | +53 −10 | PortLib×1 | COVERED |
| 2 | Confluence-Magic-Lib | `df79b045c` | 2026-07-04 | able to into world | 12 | +231 −17 | PortLib×6 | SKIP-PORTLIB |
| 3 | Confluence-Magic-Lib | `31f79abbc` | 2026-08-08 | 同步1.21.1的修改 | 13 | +163 −134 | PortLib×6 | REVERSE-ALIGNED |
| 4 | Confluence-Magic-Lib | `d77f87c92` | 2026-08-15 | feat: 对齐 1.21 内容与运行时行为 | 59 | +3549 −180 | PortLib×25 | COVERED |
| 5 | Confluence-Magic-Lib | `781e94002` | 2026-08-16 | 注释 杀杀杀 | 59 | +180 −3549 | PortLib×25 | COVERED |
| 6 | Confluence-Magic-Lib | `acbd70715` | 2026-08-18 | 药水效果（未完成） | 1 | +4 −0 |  | COVERED |
| 7 | Confluence-Magic-Lib | `1802b4488` | 2026-08-19 | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 | 6 | +69 −23 | PortLib×3 | COVERED |
| 8 | Confluence-Magic-Lib | `1c97b2e51` | 2026-08-22 | refactor(confluence): 拉通 1.21 战斗、召唤与实体体系 | 2 | +17 −2 |  | COVERED |
| 9 | Confluence-Magic-Lib | `0718c593a` | 2026-08-22 | 将饰品的药水效果转移至lib | 30 | +803 −122 | PortLib×23 | COVERED |
| 10 | Confluence-Magic-Lib | `5f2d48bbf` | 2026-08-22 | 可开关的药水效果移到lib | 23 | +520 −11 | PortLib×19 | COVERED |
| 11 | Confluence-Magic-Lib | `389b3c230` | 2026-08-23 | 调整版本 | 2 | +3 −3 | PortLib×8 | COVERED |
| 12 | Confluence-Magic-Lib | `1dc696fc5` | 2026-08-23 | 饰品能力全改为datamap，修复潜行属性 | 1 | +1 −5 |  | COVERED |
| 13 | Confluence-Magic-Lib | `d4e048a89` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | 5 | +15 −22 |  | REVERSE-ALIGNED |
| 14 | Confluence-Magic-Lib | `ebad0b193` | 2026-08-24 | 玩家动画测试 | 15 | +1249 −4 | PortLib×3 | COVERED |
| 15 | Confluence-Magic-Lib | `92d3adbf3` | 2026-08-28 | 玩家动画（未注册永夜动画） | 16 | +405 −921 | PortLib×10 | COVERED |
| 16 | Confluence-Magic-Lib | `e28501469` | 2026-08-30 | feat(entity): 完善普通敌怪、NPC与召唤物的行为和渲染 | 3 | +4 −2 |  | COVERED |
| 17 | Confluence-Magic-Lib | `b14b25a66` | 2026-09-02 | 删除一些Extension类 | 5 | +19 −19 | PortLib×25 | COVERED |
| 18 | Confluence-Magic-Lib | `3f1a8e5f9` | 2026-09-03 | 渔夫任务系统修改 | 4 | +12 −16 | PortLib×19 | COVERED |
| 19 | Confluence-Magic-Lib | `9fd8ac593` | 2026-09-04 | 修部分服务端报错 | 2 | +7 −10 | PortLib×5 | COVERED |
| 20 | Confluence-Magic-Lib | `9d5dee1a2` | 2026-09-06 | 静态方法改接口 | 11 | +19 −27 | PortLib×24 | COVERED |
| 21 | Confluence-Magic-Lib | `8fe6eb995` | 2026-09-06 | 属性静态字段注入 | 5 | +10 −13 | PortLib×11 | COVERED |
| 22 | Confluence-Magic-Lib | `0425c71c4` | 2026-09-06 | 删除多余内容 | 1 | +15 −0 | PortLib×1 | COVERED |
| 23 | Confluence-Magic-Lib | `30d45a9fa` | 2026-09-06 | 处理一些胡乱改动 | 3 | +15 −11 | PortLib×1 | COVERED |
| 24 | Confluence-Magic-Lib | `4ede8fad4` | 2026-09-07 | 生产环境修复 | 3 | +5 −5 |  | COVERED |
| 25 | Confluence-Magic-Lib | `80a53446f` | 2026-09-07 | 泰拉饰品掉落不再能影响本体，为screen添加半透明黑色遮罩 | 3 | +50 −4 |  | COVERED |
| 26 | Confluence-Magic-Lib | `f2ef458a2` | 2026-09-07 | 修数量合成 | 1 | +7 −0 | PortLib×4 | COVERED |
| 27 | Confluence-Magic-Lib | `351847f01` | 2026-09-07 | 修复CustomRarityItem的属性问题 | 2 | +13 −18 |  | COVERED |
| 28 | Confluence-Magic-Lib | `e3d54fe60` | 2026-09-08 | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 | 1 | +2 −1 | PortLib×4 | COVERED |
| 29 | Confluence-Magic-Lib | `e1757f1c1` | 2026-09-08 | 修复汇流箱子打不开、魔法武器不能附魔、附魔文本重复的、宝石法杖没粒子的问题 | 2 | +4 −5 | PortLib×1 | COVERED |
| 30 | Confluence-Magic-Lib | `361c05c8d` | 2026-09-08 | 修复灯笼粒子往下掉的问题 | 1 | +1 −1 | PortLib×1 | COVERED |
| 31 | Confluence-Magic-Lib | `efe1372ad` | 2026-09-08 | 修复右键功能物品失效问题 | 4 | +45 −35 | PortLib×23 | COVERED |
| 32 | Confluence-Magic-Lib | `6b7b52517` | 2026-09-10 | 修汇流熔炉不能放燃料的问题 | 1 | +2 −1 |  | COVERED |
| 33 | Confluence-Magic-Lib | `fb10030ce` | 2026-09-10 | 修复mixin，修复跳跃属性 | 3 | +15 −7 | PortLib×8 | COVERED |
| 34 | Confluence-Magic-Lib | `445f76396` | 2026-09-11 | 一些修复 | 2 | +6 −5 |  | COVERED |
| 35 | Confluence-Magic-Lib | `3ae3a9fc3` | 2026-09-11 | portlib升级为1.2.2 | 1 | +1 −1 | PortLib×2 | SKIP-PORTLIB |
| 36 | Confluence-Magic-Lib | `5e6625bab` | 2026-09-12 | JEI兼容恢复 | 7 | +41 −20 |  | COVERED |
| 37 | Confluence-Magic-Lib | `6e9d87bae` | 2026-09-13 | 版本更新 | 1 | +2 −1 | PortLib×2 | COVERED |
| 38 | Confluence-Magic-Lib | `f8363c053` | 2026-09-14 | 修 | 1 | +5 −1 |  | COVERED |
| 39 | Confluence-Magic-Lib | `e0b02d2f0` | 2026-09-15 | 修事件 | 1 | +1 −1 |  | COVERED |
| 40 | Confluence-Magic-Lib | `87aa1992a` | 2026-09-15 | 更新粒子 | 1 | +1 −1 | PortLib×1 | COVERED |
| 41 | Confluence-Magic-Lib | `0cf2c0fa3` | 2026-09-16 | extension | 1 | +1 −1 | PortLib×2 | COVERED |
| 42 | Confluence-Magic-Lib | `5187258a6` | 2026-09-16 | curios属性显示兼容 | 1 | +3 −4 | PortLib×5 | COVERED |
| 43 | Confluence-Magic-Lib | `454b5938f` | 2026-09-17 | 修改一些纹理和模型上的问题，挪贴图位置 | 5 | +0 −0 | 资源only | COVERED |
| 44 | Confluence-Magic-Lib | `067209093` | 2026-09-17 | 修复portlib的注册表 | 1 | +6 −1 | PortLib×7 | SKIP-PORTLIB |
| 45 | Confluence-Magic-Lib | `b61a6ee57` | 2026-09-18 | 使用neoforge风味的网络包注册与发送 | 9 | +40 −32 | PortLib×36 | COVERED |
| 46 | Confluence-Magic-Lib | `ee7122937` | 2026-09-18 | 合并stream codec | 1 | +7 −0 |  | COVERED |
| 47 | Confluence-Magic-Lib | `35147c5ed` | 2026-09-19 | 修复粒子的顶点绕序问题 | 1 | +1 −1 | PortLib×1 | COVERED |
| 48 | Confluence-Magic-Lib | `b8f5bde2b` | 2026-09-19 | 修复部分物品无法搜索的问题 | 1 | +3 −0 |  | COVERED |
| 49 | Confluence-Magic-Lib | `95b09e6cd` | 2026-09-19 | 移除动态光源至MagicLib | 5 | +309 −1 |  | COVERED |
| 50 | Confluence-Magic-Lib | `53e9a1de4` | 2026-09-20 | 动态群系修改与client tick事件大一统 | 16 | +1121 −0 |  | COVERED |
| 51 | Confluence-Magic-Lib | `82af813f1` | 2026-09-20 | 修改一股味的代码 | 26 | +325 −29 | PortLib×5 | COVERED |
| 52 | Confluence-Magic-Lib | `c6b57b9a5` | 2026-09-20 | 第一人称动画功能移到lib | 5 | +19 −37 | PortLib×1 | COVERED |
| 53 | Confluence-Magic-Lib | `4d3f299e9` | 2026-09-20 | IdentityHashMap换成Reference2ObjectOpenHashMap | 2 | +7 −4 |  | COVERED |
| 54 | Confluence-Magic-Lib | `b95445c3c` | 2026-09-20 | feat: 完善动态群系覆盖与迷你群系判定 | 10 | +526 −195 |  | COVERED |
| 55 | Confluence-Magic-Lib | `6a56e90ad` | 2026-09-20 | 修复暴击率问题 | 3 | +23 −5 |  | COVERED |
| 56 | Confluence-Magic-Lib | `0b4b61ae1` | 2026-09-21 | 修复一些问题 | 1 | +6 −26 |  | COVERED |
| 57 | Confluence-Magic-Lib | `b9d59de31` | 2026-09-21 | feat(worldgen): 重构蜘蛛洞生成并接入蜘蛛巢方块 | 2 | +68 −2 |  | COVERED |
| 58 | Confluence-Magic-Lib | `351cec5be` | 2026-09-22 | feat: 重构肉山肉墙与悠悠球实现，更新 NPC 交互界面并统一敌怪射弹伤害 | 1 | +2 −2 |  | COVERED |
| 59 | Confluence-Magic-Lib | `303308900` | 2026-09-22 | 删除Ponder的nbt，升级粒子 | 1 | +1 −1 | PortLib×1 | COVERED |
| 60 | Confluence-Magic-Lib | `e9b848c93` | 2026-09-23 | 大改修饰语 | 1 | +11 −53 | PortLib×3 | COVERED |
| 61 | Confluence-Magic-Lib | `8378b03ff` | 2026-09-25 | 彩色火把 | 1 | +2 −0 | PortLib×2 | COVERED |
| 62 | Confluence-Magic-Lib | `4688a2983` | 2026-09-25 | 1.2.7 | 1 | +0 −2 | PortLib×2 纯删除 | COVERED |
| 63 | Confluence-Magic-Lib | `c711de55f` | 2026-09-25 | feat(magiclib): 支持直接注册动态光源 | 2 | +151 −93 |  | COVERED |
| 64 | Confluence-Magic-Lib | `777e96ae8` | 2026-09-27 | 修重铸价格（没对接心情） | 1 | +1 −1 | PortLib×1 | COVERED |
| 65 | Confluence-Magic-Lib | `addf529ec` | 2026-10-02 | 优化动态光照，移除可携带仆从接口行为 | 7 | +191 −268 |  | COVERED |
| 66 | Confluence-Magic-Lib | `413d62d1f` | 2026-10-02 | 添加动态光照注册行为与ParticleAccessor | 5 | +148 −57 | PortLib×5 | COVERED |
| 67 | Confluence-Magic-Lib | `595159d71` | 2026-10-03 | 修复组件崩溃 | 1 | +2 −0 | PortLib×1 | COVERED |
| 68 | TerraCurio | `3a3ce762c` | 2026-07-04 | able to start game | 7 | +27 −27 | PortLib×4 | TODO |
| 69 | TerraCurio | `a9f3c48eb` | 2026-07-04 | able to into world | 4 | +8 −31 |  | TODO |
| 70 | TerraCurio | `fc5713120` | 2026-07-22 | 修崩溃 | 1 | +1 −1 |  | TODO |
| 71 | TerraCurio | `063dffb70` | 2026-07-29 | truly fix | 1 | +1 −1 |  | TODO |
| 72 | TerraCurio | `aab92e201` | 2026-07-29 | 整理 | 2 | +2 −4 | PortLib×5 | TODO |
| 73 | TerraCurio | `d52bc12dc` | 2026-08-07 | portlib v1.0.0 | 2 | +5 −7 | PortLib×4 | TODO |
| 74 | TerraCurio | `9c96d2d04` | 2026-08-07 | TerraCurio依赖 | 1 | +7 −0 | PortLib×1 资源only | TODO |
| 75 | TerraCurio | `7b0cbe520` | 2026-08-08 | 同步1.21.1的修改 | 14 | +643 −868 | PortLib×2 | TODO |
| 76 | TerraCurio | `841f9933c` | 2026-08-16 | 调整逻辑 | 3 | +6 −34 |  | TODO |
| 77 | TerraCurio | `ddfcd27e0` | 2026-08-22 | 将饰品的药水效果转移至lib | 50 | +167 −716 | PortLib×30 | TODO |
| 78 | TerraCurio | `b38d16d66` | 2026-08-22 | 可开关的药水效果移到lib | 12 | +39 −99 |  | TODO |
| 79 | TerraCurio | `82e2636f0` | 2026-08-22 | 同步粒子 | 3 | +17 −7 |  | TODO |
| 80 | TerraCurio | `b1af28359` | 2026-08-23 | 升级粒子 | 7 | +35 −31 | PortLib×1 | TODO |
| 81 | TerraCurio | `99dc4ccf1` | 2026-08-23 | 调整版本 | 2 | +5 −4 | PortLib×8 | TODO |
| 82 | TerraCurio | `45beb4784` | 2026-08-23 | 饰品能力全改为datamap，修复潜行属性 | 11 | +786 −497 | PortLib×14 | TODO |
| 83 | TerraCurio | `a3f1cbca7` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | 285 | +2500 −324 | PortLib×14 | TODO |
| 84 | TerraCurio | `06d637298` | 2026-08-24 | 玩家动画测试 | 27 | +259 −30 |  | TODO |
| 85 | TerraCurio | `6ee91b55b` | 2026-08-28 | 玩家动画（未注册永夜动画） | 1 | +1 −1 | PortLib×1 | TODO |
| 86 | TerraCurio | `3516ac33a` | 2026-09-02 | 删除一些Extension类 | 1 | +1 −1 |  | TODO |
| 87 | TerraCurio | `38fcb3595` | 2026-09-06 | 静态方法改接口 | 10 | +65 −67 | PortLib×20 | TODO |
| 88 | TerraCurio | `aae737d41` | 2026-09-06 | 属性静态字段注入 | 9 | +55 −61 | PortLib×16 | TODO |
| 89 | TerraCurio | `94c69f0bc` | 2026-09-06 | 删除多余内容 | 2 | +4 −1 |  | TODO |
| 90 | TerraCurio | `be87be1cb` | 2026-09-06 | 封印魂 | 1 | +5 −6 |  | TODO |
| 91 | TerraCurio | `d4be8935d` | 2026-09-07 | 生产环境修复 | 1 | +1 −1 |  | TODO |
| 92 | TerraCurio | `59730e912` | 2026-09-07 | 生产环境修复 | 2 | +2 −2 |  | TODO |
| 93 | TerraCurio | `0b3846038` | 2026-09-07 | 泰拉饰品掉落不再能影响本体，为screen添加半透明黑色遮罩 | 1 | +10 −1 | PortLib×1 | TODO |
| 94 | TerraCurio | `ea3fe72d3` | 2026-09-08 | 修复汇流箱子打不开、魔法武器不能附魔、附魔文本重复的、宝石法杖没粒子的问题 | 1 | +1 −1 | PortLib×1 | TODO |
| 95 | TerraCurio | `262f4dae5` | 2026-09-08 | 修复灯笼粒子往下掉的问题 | 1 | +1 −1 | PortLib×1 | TODO |
| 96 | TerraCurio | `9a8c29d2b` | 2026-09-08 | 修复部分靴子没有自动上台阶功能的问题 | 1 | +1 −0 |  | TODO |
| 97 | TerraCurio | `e9789b7c2` | 2026-09-10 | 修一些资源错误 | 1 | +14 −14 | 资源only | TODO |
| 98 | TerraCurio | `b797d87e8` | 2026-09-10 | 修复mixin，修复跳跃属性 | 6 | +32 −24 | PortLib×9 | TODO |
| 99 | TerraCurio | `eebc21cbe` | 2026-09-11 | 修复与Bigger Stacks的Mixin冲突 | 1 | +4 −1 |  | TODO |
| 100 | TerraCurio | `feded30e6` | 2026-09-11 | portlib升级为1.2.2 | 1 | +1 −1 | PortLib×2 | TODO |
| 101 | TerraCurio | `25eb4545d` | 2026-09-12 | JEI兼容恢复 | 3 | +7 −8 | PortLib×2 | TODO |
| 102 | TerraCurio | `aa3c27be4` | 2026-09-13 | 版本更新 | 1 | +2 −1 | PortLib×2 | TODO |
| 103 | TerraCurio | `b638adcd5` | 2026-09-14 | 为现有新增的怪物补全点需要的东西 | 1 | +0 −0 | 资源only | TODO |
| 104 | TerraCurio | `efb867308` | 2026-09-15 | 微调 | 1 | +1 −1 |  | TODO |
| 105 | TerraCurio | `e911661de` | 2026-09-15 | 更新粒子 | 1 | +1 −1 | PortLib×1 | TODO |
| 106 | TerraCurio | `2c55cf65f` | 2026-09-16 | 调整末地高度 | 1 | +1 −4 |  | TODO |
| 107 | TerraCurio | `717a424d6` | 2026-09-16 | extension | 1 | +1 −1 | PortLib×2 | TODO |
| 108 | TerraCurio | `b9b221844` | 2026-09-16 | curios属性显示兼容 | 3 | +60 −0 | PortLib×3 | TODO |
| 109 | TerraCurio | `b5b775e93` | 2026-09-17 | 修改一些纹理和模型上的问题，挪贴图位置 | 5 | +0 −0 | 资源only | TODO |
| 110 | TerraCurio | `2f2f24793` | 2026-09-17 | 修复portlib的注册表 | 1 | +6 −1 | PortLib×7 | TODO |
| 111 | TerraCurio | `5d64f259a` | 2026-09-18 | 使用neoforge风味的网络包注册与发送 | 24 | +90 −72 | PortLib×89 | TODO |
| 112 | TerraCurio | `e678ff8fc` | 2026-09-19 | 修复粒子的顶点绕序问题 | 1 | +1 −1 | PortLib×1 | TODO |
| 113 | TerraCurio | `577a5d955` | 2026-09-20 | 第一人称动画功能移到lib | 1 | +1 −1 | PortLib×1 | TODO |
| 114 | TerraCurio | `83efd2c63` | 2026-09-20 | IdentityHashMap换成Reference2ObjectOpenHashMap | 2 | +7 −7 | PortLib×3 | TODO |
| 115 | TerraCurio | `79351d003` | 2026-09-22 | 改 | 1 | +1 −1 |  | TODO |
| 116 | TerraCurio | `a1a803d74` | 2026-09-22 | 删除Ponder的nbt，升级粒子 | 1 | +1 −1 | PortLib×1 | TODO |
| 117 | TerraCurio | `83f19c9db` | 2026-09-23 | fall_damage_multiplier属性不再导致摔落声音 | 3 | +40 −42 |  | TODO |
| 118 | TerraCurio | `252bb9caa` | 2026-09-26 | 调整种子特性 | 1 | +3 −3 | 资源only | TODO |
| 119 | TerraCurio | `5666e140a` | 2026-09-27 | 修重铸价格（没对接心情） | 1 | +1 −1 | PortLib×1 | TODO |
| 120 | TerraCurio | `ef4356518` | 2026-09-27 | 修复构建问题 | 1 | +6 −0 |  | TODO |
| 121 | TerraCurio | `df37454c2` | 2026-09-27 | 平衡性调整，权重调整，贴图补充 | 6 | +0 −0 | 资源only | TODO |
| 122 | TerraCurio | `f00e8f541` | 2026-10-03 | 修复组件崩溃 | 1 | +1 −1 |  | TODO |
| 123 | TerraFurniture | `d08fb3230` | 2026-07-04 | able to start game | 2 | +2 −60 |  | TODO |
| 124 | TerraFurniture | `ae0621561` | 2026-08-08 | 同步1.21.1的修改 | 2 | +3 −3 | PortLib×2 | TODO |
| 125 | TerraFurniture | `3176c2ac0` | 2026-08-23 | 调整版本 | 2 | +4 −4 | PortLib×7 | TODO |
| 126 | TerraFurniture | `c4eb231f0` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | 1 | +0 −4 | 纯删除 | TODO |
| 127 | TerraFurniture | `89b641939` | 2026-09-02 | 删除一些Extension类 | 2 | +8 −8 | PortLib×13 | TODO |
| 128 | TerraFurniture | `996bcbf97` | 2026-09-03 | 渔夫任务系统修改 | 1 | +2 −3 | PortLib×6 | TODO |
| 129 | TerraFurniture | `4bbc937be` | 2026-09-06 | 静态方法改接口 | 1 | +2 −2 | PortLib×2 | TODO |
| 130 | TerraFurniture | `eacdaad1c` | 2026-09-06 | 属性静态字段注入 | 1 | +1 −1 | PortLib×2 | TODO |
| 131 | TerraFurniture | `81db6984a` | 2026-09-07 | 生产环境修复 | 1 | +2 −1 | PortLib×4 | TODO |
| 132 | TerraFurniture | `4946ac82a` | 2026-09-10 | 修汇流熔炉不能放燃料的问题 | 2 | +5 −3 |  | TODO |
| 133 | TerraFurniture | `ef098a90a` | 2026-09-10 | 修复mixin，修复跳跃属性 | 3 | +23 −11 | PortLib×14 | TODO |
| 134 | TerraFurniture | `3a4b2e990` | 2026-09-11 | 一些修复 | 1 | +1 −1 |  | TODO |
| 135 | TerraFurniture | `1c894ac05` | 2026-09-11 | portlib升级为1.2.2 | 1 | +1 −1 | PortLib×2 | TODO |
| 136 | TerraFurniture | `7264bacb3` | 2026-09-12 | JEI兼容恢复 | 2 | +7 −7 |  | TODO |
| 137 | TerraFurniture | `fc3779532` | 2026-09-13 | 版本更新 | 1 | +2 −2 | PortLib×2 | TODO |
| 138 | TerraFurniture | `ae2d6f2f2` | 2026-09-16 | extension | 1 | +1 −1 | PortLib×2 | TODO |
| 139 | TerraFurniture | `80a9b93c1` | 2026-09-17 | 修复portlib的注册表 | 1 | +6 −1 | PortLib×8 | TODO |
| 140 | TerraFurniture | `797379213` | 2026-09-18 | 使用neoforge风味的网络包注册与发送 | 6 | +14 −20 | PortLib×11 | TODO |
| 141 | TerraFurniture | `4d327715d` | 2026-09-19 | 单腿桌子 | 22 | +1218 −0 |  | TODO |
| 142 | TerraFurniture | `d060e05de` | 2026-09-20 | 仔细研究后我发现实际上这种对称的桌子可以把花边单独分开，但是已经这样写了，先提交吧，我后面再改，绷不住了 | 1 | +14 −12 |  | TODO |
| 143 | TerraFurniture | `bf35ef75d` | 2026-09-20 | 改成组件模式 | 18 | +277 −754 |  | TODO |
| 144 | TerraFurniture | `3b1e55c09` | 2026-09-20 | 改腿渲染逻辑 | 1 | +50 −11 |  | TODO |
| 145 | TerraFurniture | `3e45ca5a6` | 2026-09-20 | 添加方块 | 15 | +513 −1 |  | TODO |
| 146 | TerraFurniture | `df868f5ac` | 2026-09-22 | 家具 | 2 | +94 −1 |  | TODO |
| 147 | TerraFurniture | `62d4c7027` | 2026-09-27 | 传一点 | 14 | +427 −69 | PortLib×1 | TODO |
| 148 | TerraFurniture | `b5f856c95` | 2026-10-03 | 加点 | 43 | +6753 −73 |  | TODO |
