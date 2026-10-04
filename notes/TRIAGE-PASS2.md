# 台账第二轮分诊（file-lag 驱动，Port 免疫）

- 参与分诊：**185** 行还没有状态的行（已完成判定的 215 行见文末「已落账」）
- 度量：`commit-lag` = 该提交改动文件的有效行欠账加权；有效行剔除 import/package、Port 引用、平台 API 引用（详见 `FILE-LAG.md`）
- 规则 A1~A5 见 `tools/port2native/triage_pass2.py` 文件头
- 结果：**TODO 91 / COVERED 56 / REVIEW 38**

## SKIP-PORTLIB（0）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|

## SKIP-1.20-REVERTED（0）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|

## DEFER-ASSETS（0）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|

## COVERED（56）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|
| `f4ffadaa7` | 2026-09-18 | AttachmentEntity体系迁移 | NO-COV | COVERED | 0 | 5315 | 25 | A3 |
| `ef1a4d138` | 2026-09-22 | feat: 重构肉山肉墙与悠悠球实现，更新 NPC 交互界面并统一敌怪射弹伤害 | NO-COV | COVERED | 192 | 1995 | 4 | A3 |
| `4af532ed1` | 2026-08-16 | `refactor: 回退错误公共架构并恢复 1.20 实现` | UNCLEAR | COVERED | 51 | 1842 | 28 | A3 |
| `7169379cd` | 2026-09-21 | 精修碰撞箱，添加安卡十字与眼球激光塔 | NO-COV | COVERED | 34 | 1506 | 0 | A3 |
| `46ae3a168` | 2026-09-21 | feat: 完善悠悠球及饰品功能，统一敌怪反击并修复附魔钓竿 | NO-COV | COVERED | 30 | 1375 | 3 | A3 |
| `157730424` | 2026-09-20 | 完成小鬼，致命球，血蝙蝠，棱镜 | NO-COV | COVERED | 15 | 1089 | 16 | A3 |
| `300bd6cfe` | 2026-09-19 | 重构敌怪效果与属性配置，修复生物行为和动画 | NO-COV | COVERED | 26 | 1016 | 10 | A3 |
| `64b7bbe9e` | 2026-09-20 | 完成吸血鬼青蛙，史莱姆，小雪怪，铁傀儡 | NO-COV | COVERED | 9 | 766 | 8 | A3 |
| `701d48a08` | 2026-09-20 | feat: 完善动态群系覆盖与迷你群系判定 | NO-COV | COVERED | 1 | 694 | 0 | A3 |
| `ac2ac4428` | 2026-09-25 | fix: 统一生物生成检查并修正渔夫任务条件 | NO-COV | COVERED | 0 | 654 | 0 | A3 |
| `124baf17d` | 2026-09-20 | 完成蜘蛛，沙漠虎 | NO-COV | COVERED | 6 | 549 | 4 | A3 |
| `9a3c5798b` | 2026-09-19 | 完成铁傀儡 | NO-COV | COVERED | 4 | 525 | 7 | A3 |
| `f212a001a` | 2026-09-20 | fix: 修复生物渲染、蠕虫行为及特殊物品拾取 | NO-COV | COVERED | 16 | 458 | 1 | A3 |
| `641467c87` | 2026-09-18 | 迁移黄蜂召唤物至 AttachmentEntity | NO-COV | COVERED | 28 | 449 | 8 | A3 |
| `936551676` | 2026-09-21 | 完成新的伤害信息 | NO-COV | COVERED | 3 | 457 | 0 | A3 |
| `3bc53d43d` | 2026-09-25 | 火星探测器 | NO-COV | COVERED | 5 | 451 | 0 | A3 |
| `286102066` | 2026-09-20 | fix: 完善植物茎部受击判定与坐骑行为 | NO-COV | COVERED | 1 | 376 | 0 | A3 |
| `2ca9e2ac0` | 2026-09-25 | feat: 移植左键状态接口并添加 NPC 主动攻击黑名单 | NO-COV | COVERED | 0 | 337 | 0 | A3 |
| `6a3e17e86` | 2026-09-25 | feat: 完善剑类特效与剑气渲染，调整植物敌怪追击 | NO-COV | COVERED | 0 | 312 | 1 | A3 |
| `a2dfa30da` | 2026-09-24 | refactor: 统一 NPC 心情配置与环境计算 | NO-COV | COVERED | 0 | 289 | 0 | A3 |
| `0fe39ec92` | 2026-09-19 | 移除Geo静态渲染器 | NO-COV | COVERED | 0 | 249 | 20 | A3 |
| `d3c7c32ff` | 2026-09-18 | 同步 | NO-COV | COVERED | 0 | 265 | 0 | A3 |
| `785b151f0` | 2026-09-18 | 小鸟完成 | NO-COV | COVERED | 3 | 251 | 4 | A3 |
| `1d8a00610` | 2026-09-19 | 完成幽匿游灵 | NO-COV | COVERED | 3 | 242 | 4 | A3 |
| `cfe6ac100` | 2026-09-18 | 同步 | NO-COV | COVERED | 9 | 245 | 0 | A3 |
| `01f0936ac` | 2026-09-18 | 鞭子标记体系接入 | NO-COV | COVERED | 0 | 212 | 7 | A3 |
| `9074bb9e5` | 2026-09-21 | fix(npc): 按地牢入口独立管理老人存续与补刷 | NO-COV | COVERED | 0 | 205 | 0 | A3 |
| `9d66b34d0` | 2026-09-22 | 更新连枷 | NO-COV | COVERED | 2 | 203 | 1 | A3 |
| `c0e8c4c75` | 2026-09-22 | 添加多召唤标记叠加支持 | NO-COV | COVERED | 41 | 195 | 1 | A3 |
| `5fe2c370b` | 2026-09-22 | fix: 完善 NPC 区域生成、地表落点与存活记录管理 | NO-COV | COVERED | 0 | 193 | 0 | A3 |
| `116bef809` | 2026-09-22 | 添加哨兵携带接口 | NO-COV | COVERED | 0 | 171 | 0 | A3 |
| `75a6a63ad` | 2026-09-26 | feat(client): 添加 NPC 商店商品稀有度描边渲染组件 | NO-COV | COVERED | 23 | 165 | 0 | A3 |
| `c454b8d68` | 2026-09-20 | fix(otherworld): 修复本源末影龙属性注册并调整蠕虫移动逻辑 | NO-COV | COVERED | 3 | 159 | 0 | A3 |
| `9e32f0135` | 2026-09-23 | fix: 修复 Boss 从属清理与击退问题，集中悠悠球射弹参数 | NO-COV | COVERED | 33 | 151 | 0 | A3 |
| `a6819f175` | 2026-09-25 | feat(magiclib): 支持直接注册动态光源 | NO-COV | COVERED | 1 | 151 | 0 | A3 |
| `892008ba0` | 2026-09-21 | feat(worldgen): 重构蜘蛛洞生成并接入蜘蛛巢方块 | NO-COV | COVERED | 5 | 138 | 0 | A3 |
| `2818e719b` | 2026-09-20 | 蜘蛛爬墙 | NO-COV | COVERED | 0 | 128 | 0 | A3 |
| `85cbebf74` | 2026-09-19 | 完成鸟巢叠加层 | NO-COV | COVERED | 0 | 114 | 1 | A3 |
| `27ee0313c` | 2026-09-18 | 无敌帧体系接入 | NO-COV | COVERED | 0 | 77 | 4 | A3 |
| `17ca9b50f` | 2026-09-17 | fix(weapon): 调整光剑与晶光刃投掷交互及手持渲染 | NO-COV | COVERED | 1 | 71 | 2 | A3 |
| `e1cd9c8cb` | 2026-09-22 | 添加配置文件，召唤词缀复制 | NO-COV | COVERED | 5 | 73 | 0 | A3 |
| `af1426bed` | 2026-09-22 | 完成召唤词缀 | NO-COV | COVERED | 0 | 71 | 0 | A3 |
| `33d89dd63` | 2026-09-18 | 黄蜂完成 | NO-COV | COVERED | 9 | 59 | 1 | A3 |
| `a037263e1` | 2026-09-23 | feat: 整理 NPC 商店界面并修复渔夫任务筛选 | NO-COV | COVERED | 0 | 48 | 4 | A3 |
| `18221c338` | 2026-09-27 | 调整为标签 | NO-COV | COVERED | 0 | 51 | 1 | A3 |
| `d879d8ffc` | 2026-09-20 | 第一人称动画功能移到lib | NO-COV | COVERED | 0 | 47 | 1 | A3 |
| `e7cabcfea` | 2026-09-21 | 精修模型 | NO-COV | COVERED | 1 | 46 | 1 | A3 |
| `11a43615e` | 2026-09-22 | 更改远程数值 | NO-COV | COVERED | 8 | 38 | 0 | A3 |
| `566fde2d4` | 2026-09-25 | fix(confluence): 修复血肉墙墙后玩家处理并调整渔夫按钮布局 | NO-COV | COVERED | 0 | 34 | 0 | A3 |
| `d3d8dcd24` | 2026-09-20 | 清理导入 | NO-COV | COVERED | 1 | 17 | 0 | A3 |
| `2d21e3364` | 2026-09-27 | 修bug | NO-COV | COVERED | 0 | 12 | 0 | A3 |
| `c5b035680` | 2026-09-19 | 移除动态光源至MagicLib | NO-COV | COVERED | 0 | 7 | 2 | A3 |
| `801f95ff3` | 2026-09-25 | 塞贴图 | NO-COV | COVERED | 0 | 4 | 0 | A3 |
| `1f1c456ff` | 2026-09-23 | 染料商 | NO-COV | COVERED | 0 | 2 | 0 | A3 |
| `7a072d51b` | 2026-09-25 | 添加爆破专家商店专用贴图及映射 | NO-COV | COVERED | 0 | 2 | 0 | A3 |
| `0776c451c` | 2026-09-22 | 修复音效 | NO-COV | COVERED | 0 | 1 | 0 | A3 |

## REVIEW（38）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|
| `dffefea8d` | 2026-06-23 | feat: 添加多个怪物实体类、AI系统和相关工具类 | NEEDS-PORT | COVERED | 105 | 4568 | 32 | A5 |
| `6568d3ad1` | 2026-06-30 | part critters & monsters | NEEDS-PORT | COVERED | 62 | 1977 | 1 | A5 |
| `231c505ca` | 2026-06-30 | part npc | NEEDS-PORT | COVERED | 33 | 1419 | 3 | A5 |
| `9a48d8619` | 2026-07-01 | part npc1 | NEEDS-PORT | COVERED | 24 | 1309 | 27 | A5 |
| `7f83b379a` | 2026-06-27 | part23 | NEEDS-PORT | COVERED | 24 | 1305 | 5 | A5 |
| `b33c206fa` | 2026-06-18 | something3 | NEEDS-PORT | COVERED | 26 | 1214 | 6 | A5 |
| `a358494fa` | 2026-09-12 | 修复敌怪生成与战斗行为，完善武器渲染、NPC 广播及翻译 | NEEDS-PORT | COVERED | 3 | 1145 | 3 | A5 |
| `c2935419b` | 2026-08-23 | docs: 清理源码注释中的冗余 HTML 段落标签 | NEEDS-PORT | COVERED | 46 | 802 | 17 | A5 |
| `f6b8f73b0` | 2026-06-22 | part20 | NEEDS-PORT | COVERED | 5 | 722 | 22 | A5 |
| `ac457ea83` | 2026-09-13 | fix: 修复敌怪行为、施法表现、NPC交互及鞭子回收 | NEEDS-PORT | COVERED | 2 | 667 | 0 | A5 |
| `5a2553293` | 2026-09-13 | refactor(otherworld): 重构鞭子挥动流程并修复水生生物行为 | NEEDS-PORT | COVERED | 20 | 660 | 3 | A5 |
| `f6e114cdb` | 2026-06-24 | part21 | NEEDS-PORT | COVERED | 1 | 206 | 158 | A5 |
| `4b004c160` | 2026-06-10 | fix2 | NEEDS-PORT | COVERED | 40 | 332 | 19 | A5 |
| `b0716f0c9` | 2026-06-14 | part13 | NEEDS-PORT | COVERED | 61 | 293 | 10 | A5 |
| `fe090753f` | 2026-08-16 | fix: 对齐 NPC 交互与对话同步行为 | NEEDS-PORT | COVERED | 2 | 258 | 0 | A5 |
| `b20c0cefd` | 2026-06-27 | remove all entity part | NEEDS-PORT | COVERED | 0 | 50 | 204 | A5 |
| `b861536ae` | 2026-08-16 | refactor: 恢复 NPC 商店数据加载架构 | NEEDS-PORT | COVERED | 0 | 217 | 2 | A5 |
| `c6c2d493a` | 2026-08-16 | fix: 恢复 NPC 商店三态交易流程 | NEEDS-PORT | COVERED | 0 | 171 | 2 | A5 |
| `4bd94d674` | 2026-09-12 | 关键帧多效果支持 | NEEDS-PORT | COVERED | 0 | 167 | 0 | A5 |
| `7e8662bd4` | 2026-08-16 | refactor: 恢复 NPC 商品的组件定价模型 | NEEDS-PORT | COVERED | 0 | 130 | 0 | A5 |
| `c406dcc0b` | 2026-08-16 | fix: 对齐城镇 NPC 敌我识别与恐慌行为 | NEEDS-PORT | COVERED | 0 | 79 | 4 | A5 |
| `116edf192` | 2026-08-16 | fix: 对齐 NPC 住宅与旅商生命周期行为 | NEEDS-PORT | COVERED | 0 | 71 | 1 | A5 |
| `91724b1ce` | 2026-09-13 | 为水生生物添加自然巡游 | NEEDS-PORT | COVERED | 0 | 69 | 0 | A5 |
| `1e2f65769` | 2026-08-16 | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 | NEEDS-PORT | COVERED | 0 | 56 | 2 | A5 |
| `e7703e76d` | 2026-08-16 | fix: 保证 NPC 商店报价与交易条件一致 | NEEDS-PORT | COVERED | 0 | 45 | 0 | A5 |
| `7a3e9f664` | 2026-08-16 | fix: 同步 NPC 商店权威价格显示 | NEEDS-PORT | COVERED | 0 | 21 | 1 | A5 |
| `673efd7d5` | 2026-09-12 | 调整标签 悠悠球无敌帧 | NEEDS-PORT | COVERED | 0 | 22 | 0 | A5 |
| `12b6877be` | 2026-08-23 | 修复枪械动画报错 | NEEDS-PORT | COVERED | 0 | 14 | 1 | A5 |
| `4ab2d42ad` | 2026-06-21 | fix crash | NEEDS-PORT | COVERED | 2 | 11 | 1 | A5 |
| `c8e4e6416` | 2026-07-05 | fix IncompatibleClassChangeError | NEEDS-PORT | COVERED | 1 | 12 | 0 | A5 |
| `bd006659b` | 2026-07-11 | refactor: 重命名饿鬼实体类为HillHungry | NEEDS-PORT | COVERED | 0 | 1 | 0 | A5 |
| `032534179` | 2026-09-27 | 泡泡半透明渲染 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `437e53a84` | 2026-09-27 | fix(portlib): 修复状态效果共用修改器 ID 时的属性曲线冲突 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `dafb03ee9` | 2026-09-27 | fix(block): 修复净化转换表将草植物错误转换为草方块 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `2de684965` | 2026-10-02 | 优化动态光照，移除可携带仆从接口行为 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `6cda06303` | 2026-10-02 | 添加动态光照注册行为与ParticleAccessor | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `13376f960` | 2026-10-01 | fix(worldgen): BilayerOreFeature 两个便利构造器 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |
| `9351a7e6b` | 2026-10-02 | fix(entity): 修正改名后遗留的悬空实体 id 字符串 | NO-COV | NO-ROW | 0 | 0 | 0 | A0 |

## TODO（工作队列，按欠账权重降序）（91）

| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |
|---|---|---|---|---|---|---|---|---|
| `f4b42537c` | 2026-06-10 | part8 | NEEDS-PORT | NEEDS-PORT | 2611 | 4269 | 4 | A4 |
| `8ce7f4d7f` | 2026-07-01 | part recipe datagen | NEEDS-PORT | NEEDS-PORT | 1329 | 1769 | 6 | A4 |
| `348c877a4` | 2026-09-19 | 统一生物属性与状态参数声明，修复敌怪行为、渲染及商店分页 | NO-COV | PARTIAL | 781 | 2203 | 0 | A4 |
| `21b060ec6` | 2026-08-23 | 饰品能力全改为datamap，修复潜行属性 | NO-COV | NEEDS-PORT | 776 | 787 | 3 | A4 |
| `c0c6a321d` | 2026-06-28 | part24 | NEEDS-PORT | PARTIAL | 717 | 3323 | 29 | A4 |
| `bad95470c` | 2026-08-08 | 同步1.21.1的修改 | NEEDS-PORT | NEEDS-PORT | 515 | 806 | 6 | A4 |
| `bcecc5382` | 2026-09-17 | 新版本图鉴 | NO-COV | NEEDS-PORT | 505 | 505 | 0 | A4 |
| `900c068f7` | 2026-09-21 | 音频大导入（第一批） | NO-COV | PARTIAL | 486 | 1961 | 0 | A4 |
| `4ef159bf9` | 2026-08-23 | 同步1.21.1翅膀迁移，部分饰品添加粒子 | NEEDS-PORT | PARTIAL | 480 | 1349 | 10 | A4 |
| `e7b826680` | 2026-06-10 | part9 | NEEDS-PORT | PARTIAL | 453 | 1595 | 13 | A4 |
| `a45ef0a13` | 2026-09-20 | 修复暴击率问题 | NO-COV | NEEDS-PORT | 350 | 360 | 0 | A4 |
| `1c012ccb1` | 2026-08-22 | 将饰品的药水效果转移至lib | NEEDS-PORT | PARTIAL | 308 | 1065 | 25 | A4 |
| `100f6e0f2` | 2026-08-22 | 可开关的药水效果移到lib | UNCLEAR | PARTIAL | 255 | 583 | 16 | A4 |
| `e6eace2b1` | 2026-09-23 | 平衡性调整尝试 | NO-COV | NEEDS-PORT | 227 | 227 | 0 | A4 |
| `00b72167d` | 2026-06-10 | 枪械合并 | NEEDS-PORT | PARTIAL | 203 | 3142 | 22 | A4 |
| `058000c5c` | 2026-06-29 | part enchantment | NEEDS-PORT | NEEDS-PORT | 192 | 368 | 1 | A4 |
| `81488b6d0` | 2026-09-20 | 修改一股味的代码 | NO-COV | PARTIAL | 183 | 711 | 30 | A4 |
| `182149f52` | 2026-06-13 | part10 | NEEDS-PORT | PARTIAL | 167 | 1027 | 25 | A4 |
| `c5e9f9be5` | 2026-06-19 | part18 | NEEDS-PORT | PARTIAL | 145 | 616 | 10 | A4 |
| `a779580be` | 2026-06-14 | part16 | NEEDS-PORT | PARTIAL | 135 | 652 | 14 | A4 |
| `7646c5505` | 2026-06-15 | 物品移植 | NEEDS-PORT | PARTIAL | 134 | 742 | 9 | A4 |
| `f30688d17` | 2026-06-15 | update: 将多个方块迁移至 PortLib API 并调整方法签名 | NEEDS-PORT | PARTIAL | 109 | 1145 | 8 | A4 |
| `1516cbd2f` | 2026-06-30 | part fluid type | NEEDS-PORT | NEEDS-PORT | 95 | 165 | 26 | A4 |
| `2a4dfce2c` | 2026-08-22 | 同步粒子 | UNCLEAR | NEEDS-PORT | 85 | 111 | 2 | A4 |
| `17af6914e` | 2026-06-16 | 一点点粒子 | UNCLEAR | NEEDS-PORT | 78 | 140 | 1 | A4 |
| `8bb33454a` | 2026-09-22 | 家具 | NO-COV | NEEDS-PORT | 69 | 94 | 0 | A4 |
| `093eda09f` | 2026-06-16 | part17 | NEEDS-PORT | PARTIAL | 66 | 392 | 23 | A4 |
| `2f44045a8` | 2026-09-23 | 大改修饰语 | NO-COV | PARTIAL | 64 | 317 | 0 | A4 |
| `10705abc7` | 2026-06-21 | part19 | NEEDS-PORT | PARTIAL | 59 | 540 | 79 | A4 |
| `4dcf95cfe` | 2026-06-18 | 移植家具 | NEEDS-PORT | PARTIAL | 54 | 1021 | 28 | A4 |
| `3e76867aa` | 2026-09-20 | 迁移标记体系 | NO-COV | NEEDS-PORT | 50 | 90 | 4 | A4 |
| `7d1fff5b6` | 2026-06-13 | part12 | NEEDS-PORT | PARTIAL | 48 | 850 | 16 | A4 |
| `4298126f9` | 2026-06-24 | IPortItemExtension | NEEDS-PORT | PARTIAL | 39 | 235 | 13 | A4 |
| `253bb8d7b` | 2026-09-16 | curios属性显示兼容 | NEEDS-PORT | NEEDS-PORT | 34 | 65 | 6 | A4 |
| `05b032399` | 2026-09-22 | 现在邪恶蘑菇，丛林孢子可以生成了 | NO-COV | NEEDS-PORT | 34 | 41 | 0 | A4 |
| `a8fc8c2c6` | 2026-06-15 | 语法降级 | UNCLEAR | PARTIAL | 30 | 93 | 15 | A4 |
| `395003423` | 2026-06-10 | fix3 | NO-COV | PARTIAL | 27 | 347 | 0 | A4 |
| `d599373c3` | 2026-09-13 | 修复8月15日遗留问题 | UNCLEAR | PARTIAL | 26 | 468 | 4 | A4 |
| `1da5e0d26` | 2026-09-21 | 修复一些问题 | NO-COV | PARTIAL | 22 | 142 | 2 | A4 |
| `fecb245e5` | 2026-09-21 | 罐子掉钱逻辑补充 | NO-COV | NEEDS-PORT | 22 | 42 | 0 | A4 |
| `15afa497b` | 2026-06-14 | fix magic mirror | NEEDS-PORT | NEEDS-PORT | 21 | 26 | 3 | A4 |
| `4a3436770` | 2026-09-18 | 修复错误 | NO-COV | PARTIAL | 19 | 57 | 4 | A4 |
| `8bcc392be` | 2026-06-16 | mob effect | NEEDS-PORT | PARTIAL | 18 | 83 | 3 | A4 |
| `97fc3ed2e` | 2026-07-08 | npc goal | NEEDS-PORT | PARTIAL | 18 | 37 | 4 | A4 |
| `a94cf78dc` | 2026-09-22 | 完成词缀提示与词缀属性 | NO-COV | NEEDS-PORT | 18 | 32 | 0 | A4 |
| `a086eeba6` | 2026-09-20 | 调整一部分生物的动画，碰撞箱，贴图 | NO-COV | NEEDS-PORT | 16 | 21 | 0 | A4 |
| `c886cec0f` | 2026-09-20 | 修复暴击率问题 | NO-COV | PARTIAL | 15 | 106 | 1 | A4 |
| `fc3fabfb9` | 2026-09-27 | 平衡性调整，权重调整，贴图补充 | NO-COV | PARTIAL | 15 | 81 | 0 | A4 |
| `c9f3af990` | 2026-06-21 | extensions | UNCLEAR | PARTIAL | 13 | 246 | 46 | A4 |
| `041c84915` | 2026-09-18 | 重命名钱币槽，生物初步调参 | NO-COV | PARTIAL | 11 | 159 | 0 | A4 |
| `6d4f07298` | 2026-09-21 | 悠悠球和召唤杖调参，外观调整 | NO-COV | PARTIAL | 10 | 34 | 0 | A4 |
| `62d4d191a` | 2026-09-14 | 翻新已经有的模型 | NEEDS-PORT | PARTIAL | 9 | 31 | 0 | A4 |
| `f7996a657` | 2026-06-18 | rename | NEEDS-PORT | PARTIAL | 8 | 255 | 2 | A4 |
| `fbcb8e783` | 2026-06-16 | fix crash | NEEDS-PORT | PARTIAL | 5 | 96 | 9 | A4 |
| `a4b321499` | 2026-09-14 | 补充，修改一部分模型，删除不该存在的生物，为ai已经注册了的生物补充模型 | NEEDS-PORT | PARTIAL | 5 | 31 | 0 | A4 |
| `6084476d0` | 2026-09-12 | JEI兼容恢复 | NEEDS-PORT | PARTIAL | 4 | 2679 | 5 | A4 |
| `05380071a` | 2026-09-26 | 调整种子特性 | NO-COV | PARTIAL | 4 | 18 | 0 | A4 |
| `fcd2368c6` | 2026-09-27 | 修重铸价格（没对接心情） | NO-COV | NEEDS-PORT | 4 | 4 | 0 | A4 |
| `da5b986be` | 2026-09-21 | 蜘蛛巢石 | NO-COV | PARTIAL | 2 | 7 | 0 | A4 |
| `0e370c928` | 2026-06-24 | data component method rename | NEEDS-PORT | PARTIAL | 1 | 78 | 4 | A4 |
| `75c60a5fe` | 2026-09-17 | fix(render): 修正生物薄片渲染、动画与坐骑表现 | NO-COV | PARTIAL | 1 | 130 | 0 | A4 |
| `40f02b002` | 2026-09-18 | 补个蛋 | NO-COV | PARTIAL | 1 | 57 | 0 | A4 |
| `87b93ede2` | 2026-09-18 | 合并stream codec | NO-COV | PARTIAL | 1 | 34 | 2 | A4 |
| `a34060571` | 2026-09-20 | 移动到init | NO-COV | PARTIAL | 1 | 12 | 3 | A4 |
| `46e5e8626` | 2026-09-20 | 移除旧架构召唤体系 | NO-COV | PARTIAL | 1 | 243 | 40 | A4 |
| `d5fd40bff` | 2026-09-22 | 平衡锤 | NO-COV | NEEDS-PORT | 1 | 1 | 0 | A4 |
| `e9501cf56` | 2026-09-23 | fall_damage_multiplier属性不再导致摔落声音 | NO-COV | PARTIAL | 1 | 46 | 2 | A4 |
| `b3f13d405` | 2026-06-13 | part11 | NEEDS-PORT | PARTIAL | 0 | 627 | 16 | A4 |
| `5481344ca` | 2026-06-18 | something2 | NO-COV | PARTIAL | 0 | 10 | 5 | A4 |
| `d17dc7c9a` | 2026-06-21 | fix crash | NEEDS-PORT | PARTIAL | 0 | 4 | 0 | A4 |
| `ac7767860` | 2026-06-28 | part25 | NEEDS-PORT | PARTIAL | 0 | 107 | 0 | A4 |
| `a6f8f089d` | 2026-07-25 | fix world selection | NEEDS-PORT | PARTIAL | 0 | 15 | 2 | A4 |
| `1e0393178` | 2026-08-16 | `refactor: 回退错误公共架构并恢复 1.20 实现` | UNCLEAR | PARTIAL | 0 | 732 | 1 | A4 |
| `99902d986` | 2026-09-13 | 修复锁方块 | UNCLEAR | PARTIAL | 0 | 31 | 3 | A4 |
| `9090245f8` | 2026-09-14 | 修 | NEEDS-PORT | PARTIAL | 0 | 9 | 0 | A4 |
| `856c0047f` | 2026-09-16 | 调整末地高度 | UNCLEAR | PARTIAL | 0 | 44 | 4 | A4 |
| `0bd4b2052` | 2026-09-17 | 修改一些纹理和模型上的问题，挪贴图位置 | NO-COV | PARTIAL | 0 | 1 | 0 | A4 |
| `9a5d3a4a6` | 2026-09-17 | 修复portlib的注册表 | NO-COV | PARTIAL | 0 | 83 | 13 | A4 |
| `389b7a760` | 2026-09-17 | 修复网络包发送 | NO-COV | PARTIAL | 0 | 2 | 1 | A4 |
| `2eb9ce931` | 2026-09-19 | 动态光源接入 | NO-COV | PARTIAL | 0 | 4 | 1 | A4 |
| `67c51a778` | 2026-09-19 | 小传一手神必青蛙模型 | NO-COV | PARTIAL | 0 | 1 | 1 | A4 |
| `14425eb1c` | 2026-09-20 | uv修复 | NO-COV | PARTIAL | 0 | 36 | 1 | A4 |
| `b47adc3ce` | 2026-09-20 | 光剑修改 | NO-COV | PARTIAL | 0 | 4 | 0 | A4 |
| `6db8be065` | 2026-09-20 | 加点 | NO-COV | PARTIAL | 0 | 61 | 0 | A4 |
| `c7aefbf00` | 2026-09-21 | 添加翻译键 | NO-COV | PARTIAL | 0 | 12 | 0 | A4 |
| `bb1892201` | 2026-09-21 | 伤害粒子的更改 | NO-COV | PARTIAL | 0 | 5 | 0 | A4 |
| `e6d47c412` | 2026-09-25 | 修复连接材质 | NO-COV | PARTIAL | 0 | 2 | 0 | A4 |
| `980615a95` | 2026-09-25 | 彩色火把 | NO-COV | PARTIAL | 0 | 1 | 0 | A4 |
| `fa147c869` | 2026-09-27 | 改一点 | NO-COV | PARTIAL | 0 | 48 | 0 | A4 |
| `1038e97fe` | 2026-09-27 | 链球防止攻击时肘击的正确修复方式 | NO-COV | PARTIAL | 0 | 2 | 1 | A4 |
| `e6c0036c2` | 2026-09-27 | 防止悠悠球肘击地板 | NO-COV | PARTIAL | 0 | 3 | 0 | A4 |

## 工作队列概览

- TODO 行数 **91**，欠账权重合计 **12186**（权重 = 该提交认领文件的**新增行数**和）
- 欠账权重 ≥1000 的行：**2**；≥100 的行：**20**；<100 的行：**69**

| 月份 | TODO 行数 |
|---|---|
| 2026-06 | 29 |
| 2026-07 | 3 |
| 2026-08 | 7 |
| 2026-09 | 52 |

## 已落账（`port-ledger-status.json` 里的全部判定）

- 共 **215** 条；本轮 A1a/A1b/A2/A3 写入了其中 24 条，其余 91 行保持「无状态 = TODO」
- `notes/PORT-RESIDUALS.md` 另记行级复核抠出的 2 条微量残留

| 提交 | 日期 | 状态 | 说明 |
|---|---|---|---|
| `0af0c9e94` | 2026-09-07 | SKIP-PORTLIB | 修创造模式标签页搜索 |
| `2e7d30d15` | 2026-09-06 | SKIP-PORTLIB | configuration task |
| `5d3cf72f6` | 2026-09-11 | SKIP-PORTLIB | 修复与Bigger Stacks的Mixin冲突 |
| `609fba732` | 2026-09-06 | SKIP-PORTLIB | 新增 IPortConfigValueExtension，提供配置值读取方法和接口转换入 |
| `6f8d84cbe` | 2026-09-07 | SKIP-PORTLIB | 修数量合成 |
| `77f3b047e` | 2026-07-29 | SKIP-PORTLIB | feat: 新增步高度属性并完善跨版本桥接功能 |
| `83674802b` | 2026-08-07 | SKIP-PORTLIB | portlib v1.0.0 |
| `841165c47` | 2026-09-06 | SKIP-PORTLIB | 删除多余内容 |
| `b375ad22b` | 2026-07-29 | SKIP-PORTLIB | refactor: 优化网络包系统并完善跨版本桥接 |
| `be6173c00` | 2026-09-07 | SKIP-PORTLIB | 修复CustomRarityItem的属性问题 |
| `bf3b4f26e` | 2026-08-04 | SKIP-PORTLIB | 修吃东西崩溃 无法正常返还物品 堆叠数 |
| `ea0bb9c0f` | 2026-09-06 | SKIP-PORTLIB | 优化 |
| `f02d012ad` | 2026-07-29 | SKIP-PORTLIB | 整理 |
| `024a28ac7` | 2026-08-22 | SKIP-1.20-REVERTED | fix(confluence): 补齐 NPC 交互与实体属性对齐 |
| `159af78f9` | 2026-08-16 | SKIP-1.20-REVERTED | fix: 修复内置 NPC 商店资源加载 |
| `188ade36f` | 2026-08-16 | SKIP-1.20-REVERTED | fix: 对齐城镇 NPC 远程战斗与护士治疗行为 |
| `1c40b0ecc` | 2026-08-16 | SKIP-1.20-REVERTED | fix: 补充 NPC 交互事件 |
| `38382758a` | 2026-09-09 | SKIP-1.20-REVERTED | fix(otherworld): 修复召唤物行为、蜗牛爬行与武器逻辑 |
| `40c630e6b` | 2026-08-16 | SKIP-1.20-REVERTED | 石 |
| `48c509684` | 2026-08-20 | SKIP-1.20-REVERTED | fix(summon): 严格对齐 1.21.1 召唤体系行为与渲染 |
| `6ec97fb52` | 2026-08-16 | SKIP-1.20-REVERTED | refactor: 恢复 NPC 原有交互架构 |
| `72421955a` | 2026-08-17 | SKIP-1.20-REVERTED | fix: 拉通召唤战斗与储物伙伴行为 |
| `7dc9244e4` | 2026-08-17 | SKIP-1.20-REVERTED | style: 清理新增架构类中的无意义拆行 |
| `9378868e5` | 2026-09-11 | SKIP-1.20-REVERTED | feat: 重构光剑、悠悠球与生物行为并修复多项战斗和生成问题 |
| `9729f1c30` | 2026-08-16 | SKIP-1.20-REVERTED | refactor: 按 Servantry 架构对齐召唤系统 |
| `b8c9b7a07` | 2026-08-17 | SKIP-1.20-REVERTED | refactor: 移除废弃的子弹运行时状态架构 |
| `c2d92b019` | 2026-08-16 | SKIP-1.20-REVERTED | refactor: 按 Servantry 架构对齐召唤系统 |
| `d22a12fdc` | 2026-09-17 | SKIP-1.20-REVERTED | fix(summon): 修正白虎移动、扑击命中与升级跳位 |
| `0ceb3c251` | 2026-09-11 | DEFER-ASSETS | portlib升级为1.2.2 |
| `1e71b3c12` | 2026-09-21 | DEFER-ASSETS | 音频大导入（第四批） |
| `4b9200a0f` | 2026-07-22 | DEFER-ASSETS | ssh doc |
| `50ff9fe88` | 2026-09-10 | DEFER-ASSETS | 修一些资源错误 |
| `57d824a9d` | 2026-06-12 | DEFER-ASSETS | remove: 删除 TerraGuns 子模块配置 |
| `5f3b80aea` | 2026-09-21 | DEFER-ASSETS | 音频大导入（第五批） |
| `654e45653` | 2026-09-21 | DEFER-ASSETS | 音频大导入（最后一批） |
| `6756199b8` | 2026-09-21 | DEFER-ASSETS | 音频大导入（第六批） |
| `69a48adae` | 2026-09-27 | DEFER-ASSETS | 修复构建问题 |
| `6f638b9b4` | 2026-09-19 | DEFER-ASSETS | 修复粒子的顶点绕序问题 |
| `7525f7829` | 2026-09-14 | DEFER-ASSETS | 激光模型 |
| `7f159793d` | 2026-09-12 | DEFER-ASSETS | 移除亚刻方法 |
| `863e8bef8` | 2026-08-23 | DEFER-ASSETS | 调整版本 |
| `870e6966a` | 2026-09-15 | DEFER-ASSETS | 更新粒子 |
| `9057f178c` | 2026-08-28 | DEFER-ASSETS | 玩家动画（未注册永夜动画） |
| `9f57aa9cd` | 2026-09-21 | DEFER-ASSETS | 音频大导入（第三批） |
| `aa1e02f44` | 2026-09-14 | DEFER-ASSETS | 新铁傀儡模型 |
| `b184c0a5f` | 2026-09-08 | DEFER-ASSETS | 修复灯笼粒子往下掉的问题 |
| `b2ff1bdf0` | 2026-09-23 | DEFER-ASSETS | 修复地牢盔甲架和水矢 |
| `c378879f4` | 2026-07-22 | DEFER-ASSETS | chore: 将所有子模块远程切换为 SSH |
| `c7e4f8fab` | 2026-06-21 | DEFER-ASSETS | extensions2 |
| `cf4b3d201` | 2026-07-05 | DEFER-ASSETS | fix IncompatibleClassChangeError |
| `ddb08626b` | 2026-09-21 | DEFER-ASSETS | 音频大导入（第二批） |
| `e2a096341` | 2026-09-22 | DEFER-ASSETS | 删除Ponder的nbt，升级粒子 |
| `ea9708fb4` | 2026-09-10 | DEFER-ASSETS | 增加发光贴图 |
| `f38c08ae6` | 2026-09-21 | DEFER-ASSETS | 悠悠球和召唤杖调参，外观调整 |
| `128469b81` | 2026-09-11 | COVERED | 修复药水效果的问题 |
| `15a05d234` | 2026-09-07 | COVERED | 泰拉饰品掉落不再能影响本体，为screen添加半透明黑色遮罩 |
| `19669033d` | 2026-09-02 | COVERED | feat(otherworld): 重构战斗 AI、Boss 行为与 NPC 交互系统 |
| `1ab2b43ac` | 2026-09-22 | COVERED | 修复与TerraBlender的兼容 |
| `1e77c5edc` | 2026-06-18 | COVERED | remove BOM |
| `24322f45a` | 2026-08-16 | COVERED | refactor: 恢复 NPC 对话管理器原有架构 |
| `27e9e0ac3` | 2026-08-16 | COVERED | refactor: 清理 NPC 对齐残留并恢复原有架构 |
| `2a9014345` | 2026-08-16 | COVERED | fix: 对齐 NPC 固定商店商品与出售条件 |
| `2efa17a52` | 2026-06-16 | COVERED | rollback1 |
| `313ecb135` | 2026-09-12 | COVERED | 光剑贴图动画 |
| `3518ab86e` | 2026-09-11 | COVERED | 干掉TerraBlender |
| `35424398b` | 2026-08-19 | COVERED | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 |
| `3f2eb3be2` | 2026-08-23 | COVERED | fix: 重构悠悠球系统与客户端武器输入架构 |
| `4122dbcf0` | 2026-09-04 | COVERED | 修部分服务端报错 |
| `41c27595c` | 2026-06-12 | COVERED | feat: 合并 TerraGuns 模块并迁移至 PortLib API |
| `441334efe` | 2026-08-17 | COVERED | fix: 对齐普通生物自然生成规则 |
| `446c689a4` | 2026-09-07 | COVERED | 修复成就json 修复剑的属性定义问题 修复雀杖的雀在常规状态下抽风 修复召唤物小雪怪移 |
| `4557b85fc` | 2026-08-24 | COVERED | 玩家动画测试 |
| `48be70c79` | 2026-08-23 | COVERED | 升级粒子 |
| `55262a7f2` | 2026-09-15 | COVERED | 微调 |
| `55cc8fc6b` | 2026-09-07 | COVERED | 生产环境修复 |
| `5bd5b4211` | 2026-08-17 | COVERED | fix: 对齐普通生物自然生成规则 |
| `5c56e83b2` | 2026-06-17 | COVERED | something |
| `64950f063` | 2026-08-16 | COVERED | fix: 修正 NPC 商店生成内容与商品价值 |
| `7003e5824` | 2026-09-03 | COVERED | 删除多余的图鉴键注册 |
| `7163e1d05` | 2026-08-17 | COVERED | fix: 修复弹幕生命周期中断判断 |
| `72adcfeae` | 2026-06-08 | COVERED | part7 |
| `74346c9d2` | 2026-08-16 | COVERED | fix: 清理错误与无效的 NPC 固定商店入口 |
| `74a1fc885` | 2026-06-14 | COVERED | 移除BOM |
| `751055e45` | 2026-09-10 | COVERED | 修复种子按钮位置，修复世界类型图标 |
| `7b3b5c28e` | 2026-09-03 | COVERED | 渔夫任务系统修改 |
| `7cadcf9a8` | 2026-08-22 | COVERED | fix(confluence): 补齐 NPC 交互与实体属性对齐 |
| `7d6b90edc` | 2026-08-16 | COVERED | fix: 拉通 NPC 住宅交互与生命周期行为 |
| `810fb6b14` | 2026-09-13 | COVERED | 版本更新 |
| `84b1939df` | 2026-09-08 | COVERED | fix(otherworld): 修复战斗结算并统一生物属性与鞭子判定 |
| `8ca8275f6` | 2026-09-02 | COVERED | 删除AI乱改的进度系统，部分事件改用原生类 |
| `9bc04295b` | 2026-08-19 | COVERED | refactor(otherworld): 拉通生物、NPC与战斗系统迁移 |
| `9e00dbf7b` | 2026-08-16 | COVERED | fix: 拉通 NPC 商店条件与交易结算 |
| `a0148b9fa` | 2026-09-06 | COVERED | 修部分接口 |
| `a6fd78681` | 2026-08-30 | COVERED | feat(entity): 完善普通敌怪、NPC与召唤物的行为和渲染 |
| `a86216caf` | 2026-08-17 | COVERED | fix: 拉通普通生物注册生成与属性语义 |
| `ab0d06315` | 2026-08-17 | COVERED | fix: 拉通坐骑移动与交互行为 |
| `ac0d4a88d` | 2026-09-18 | COVERED | Better Difficulty Asking Screen (from 1.21.1 |
| `aee0a94ca` | 2026-08-16 | COVERED | refactor: 恢复 NPC 原有目标选择架构 |
| `b2d1d939c` | 2026-08-16 | COVERED | fix: 对齐 NPC 战斗治疗与生命周期行为 |
| `b9ccf1061` | 2026-09-19 | COVERED | Worldgen fix |
| `bfd32eae3` | 2026-08-17 | COVERED | refactor: 清理资源迁移噪音并恢复历史命名 |
| `c22ea51d4` | 2026-09-23 | COVERED | 软核询问屏幕十秒后暂停游戏 |
| `c7dd86378` | 2026-09-08 | COVERED | 修复洞穴探险高亮框偏移 |
| `d0fe20495` | 2026-08-16 | COVERED | refactor: 将 NPC 默认聊天数据迁入 datagen |
| `d24f247af` | 2026-09-08 | COVERED | 修复钻石套装失效的问题 |
| `d5cdb0f89` | 2026-09-12 | COVERED | fix(otherworld): 修复黄蜂寻路、悠悠球增伤与渔夫任务 |
| `d6c99c740` | 2026-08-17 | COVERED | fix: 拉通Boss属性部件伤害与掉落行为 |
| `d742dcf44` | 2026-08-16 | COVERED | feat: 补齐 NPC 默认聊天内容与触发条件 |
| `e1cdbb3ff` | 2026-08-16 | COVERED | feat: 接通 NPC 交互与聊天同步链路 |
| `f68cf4b17` | 2026-09-03 | COVERED | 删除多余的图鉴键注册 |
| `fc4498b8b` | 2026-09-21 | COVERED | 改群系生成 |
| `ff7a62eea` | 2026-09-20 | COVERED | IdentityHashMap换成Reference2ObjectOpenHashMap |
| `0067364f0` | 2026-08-17 | SKIP-PLATFORM | refactor: 清理混入迁移噪音与编译警告 |
| `00c759b63` | 2026-06-07 | SKIP-PLATFORM | part4 |
| `046a6ad1c` | 2026-09-08 | SKIP-PLATFORM | 修复右键功能物品失效问题 |
| `06415ab96` | 2026-09-02 | SKIP-PLATFORM | 删除AI乱改的进度系统，部分事件改用原生类 |
| `07c2ab5b5` | 2026-10-03 | REVERSE-ALIGNED | 修复组件崩溃 |
| `08276f2ca` | 2026-08-17 | PORTED | fix: 恢复自定义矿车创建与放置行为 |
| `09601301f` | 2026-07-25 | SKIP-PLATFORM | fix |
| `0a575062b` | 2026-07-06 | SKIP-PLATFORM | docs: 修正PointedDripstoneBlockMixin中@ModifyVa |
| `0b38e2e69` | 2026-06-14 | SKIP-PLATFORM | part14 |
| `0cf629c19` | 2026-09-06 | SKIP-PLATFORM | 属性静态字段注入 |
| `0fa73dd79` | 2026-07-29 | SKIP-PLATFORM | truly fix |
| `1284d08a9` | 2026-07-04 | PORTED | able to into world |
| `13a467d59` | 2026-06-14 | SKIP-PLATFORM | enum extend |
| `150b32bb4` | 2026-07-06 | SKIP-PLATFORM | docs: 修正PointedDripstoneBlockMixin中@ModifyVa |
| `1780b9a88` | 2026-10-03 | PORTED | 走妖和军官 |
| `185a0fdbb` | 2026-09-25 | PORTED | 彩色火把 |
| `1d216237b` | 2026-06-06 | SKIP-PLATFORM | part2 |
| `2472a2c1b` | 2026-09-25 | PORTED | 1.2.7 |
| `255250cc1` | 2026-09-10 | PORTED | 调参，修复一些战利品表错误，塞光剑 |
| `2569be361` | 2026-06-27 | DEFER-ARCH | part22 |
| `28e34a500` | 2026-06-07 | SKIP-PLATFORM | part5 |
| `29c1459cf` | 2026-09-02 | SKIP-PLATFORM | 删除一些Extension类 |
| `31896204b` | 2026-09-06 | SKIP-PLATFORM | 静态方法改接口 |
| `391e750d9` | 2026-06-12 | SKIP-PLATFORM | update: 将 GameEvent、LucyTheAxeDialogCategory |
| `3b8f62076` | 2026-08-17 | SKIP-PLATFORM | refactor: 清理既有逻辑中的迁移噪音 |
| `3e7cc41a9` | 2026-09-06 | DEFER-ARCH | Boss 血条 - 新增 CustomBossBarRenderer，使用边框和填充纹理 |
| `40fa4ff83` | 2026-10-02 | REVERSE-ALIGNED | fix(sword): 草剑拖尾改用原设计的树叶粒子精灵（confluence:leav |
| `417d561ec` | 2026-09-18 | SKIP-PLATFORM | 使用neoforge风味的网络包注册与发送 |
| `45fefd336` | 2026-06-10 | SKIP-PLATFORM | datamap datagen |
| `472be10da` | 2026-08-17 | SKIP-PLATFORM | fix: 补全网络注册与服务端线程边界 |
| `48289ba27` | 2026-06-08 | SKIP-PLATFORM | part6 |
| `4ac81edb7` | 2026-08-18 | PORTED | 药水效果（未完成） |
| `4c65367a0` | 2026-10-02 | REVERSE-ALIGNED | fix(sword): 剑组件粒子改走 ParticleStorm 发射器，修正物品注册 |
| `4fa865322` | 2026-08-11 | SKIP-PLATFORM | 修复重生事件逻辑 |
| `4ff55e760` | 2026-09-06 | SKIP-PLATFORM | 修扳手、剪线钳属性 |
| `50bd06aab` | 2026-08-17 | DO-NOT-PORT | fix: 拉通生物行为并统一属性访问方式 |
| `535992233` | 2026-09-14 | PORTED | 为现有新增的怪物补全点需要的东西 |
| `55aebf2f9` | 2026-09-22 | SKIP-PLATFORM | 改 |
| `5bb8d5565` | 2026-08-23 | PORTED | refactor(entity): 拉通敌怪行为、肉墙机制与客户端渲染 |
| `5c3978b01` | 2026-09-08 | PORTED | 修复汇流箱子打不开、魔法武器不能附魔、附魔文本重复的、宝石法杖没粒子的问题 |
| `5c99e9702` | 2026-09-07 | SKIP-PLATFORM | 生产环境修复 |
| `5dac72b22` | 2026-07-11 | SKIP-PLATFORM | 改coremod |
| `5ebb83523` | 2026-09-08 | SKIP-PLATFORM | 修复部分靴子没有自动上台阶功能的问题 |
| `607470f04` | 2026-09-07 | PORTED | 补缺失内容 |
| `63916265b` | 2026-07-08 | PORTED | feat: 新增实体并修复命名空间引用 |
| `647400f44` | 2026-09-06 | SKIP-PLATFORM | 封印魂 |
| `65c6d2985` | 2026-09-19 | SKIP-PLATFORM | 修复部分物品无法搜索的问题 |
| `6a24e6d1f` | 2026-09-10 | SKIP-PLATFORM | 恢复贴图 |
| `6c1a7a656` | 2026-06-07 | SKIP-PLATFORM | part3 |
| `6c62927b5` | 2026-08-04 | SKIP-PLATFORM | 不懂 |
| `6dcb832a0` | 2026-09-15 | SKIP-PLATFORM | 修事件 |
| `6ffa7c374` | 2026-07-11 | SKIP-PLATFORM | refactor: 重命名PortPlayerEvent内部类引用 |
| `741f98d1e` | 2026-09-06 | PORTED | feat(otherworld): 完善生物行为、NPC交易与配置界面 |
| `7572f2ebd` | 2026-09-21 | PORTED | 添加三个饰品 |
| `7a3593600` | 2026-09-10 | SKIP-PLATFORM | 修汇流熔炉不能放燃料的问题 |
| `80378e047` | 2026-08-07 | SKIP-PLATFORM | TerraCurio依赖 |
| `85475d011` | 2026-06-09 | SKIP-PLATFORM | 泰拉生物移植前 |
| `8a961bc0c` | 2026-09-11 | SKIP-PLATFORM | 一些修复 |
| `90dfd7804` | 2026-08-18 | PORTED | refactor(combat): 重构剑类、剑气与枪械系统 |
| `911437e03` | 2026-07-04 | PORTED | able to start game |
| `92e38df06` | 2026-09-01 | PORTED | feat(otherworld): 重构 Boss 战斗、蠕虫体节与属性覆盖架构 |
| `93e64b5b5` | 2026-08-17 | SKIP-PLATFORM | refactor: 清理玩家附件与组件迁移冗余 |
| `95bb0294e` | 2026-09-08 | SKIP-PLATFORM | 修复汇流箱子打不开的问题 |
| `9645da98c` | 2026-08-24 | LOST? | feat(otherworld): 重构城镇 NPC 战斗体系并补全生物相关内容 |
| `a640087ea` | 2026-08-16 | SKIP-1.21-KEEPS | fix: 拉通 NPC 商店条件与交易结算 |
| `a75bda140` | 2026-09-25 | SKIP-PLATFORM | PortLib: PortSelectMusicEvent 置空时停止音乐并跳过本刻 |
| `a78e2b42d` | 2026-09-16 | SKIP-PLATFORM | extension |
| `a8c5395a6` | 2026-08-16 | SKIP-PLATFORM | refactor: 清理跨版本移植残留与格式噪音 |
| `a8e0b487f` | 2026-08-17 | SKIP-PLATFORM | refactor: 清理跨版本迁移残留与测试辅助代码 |
| `a988279e7` | 2026-09-20 | SKIP-PLATFORM | 改动 |
| `ab2707f51` | 2026-09-17 | SKIP-PLATFORM | 修改一些纹理和模型上的问题，挪贴图位置 |
| `acba5480f` | 2026-08-17 | PORTED | fix: 恢复钩爪使用限制与服务端校验 |
| `aec2cd920` | 2026-09-10 | SKIP-PLATFORM | 修复mixin，修复跳跃属性 |
| `b05c8dc3f` | 2026-09-16 | TODO | feat: 扩充生物与事件内容，完善召唤和鞭子系统并修复战斗与渲染问题 |
| `b12020ec9` | 2026-09-19 | SKIP-PLATFORM | 更新家具 |
| `b1884f9a6` | 2026-09-10 | SKIP-PLATFORM | 1.21.1的凝灰岩系列，以及id修复 |
| `bedb2af6e` | 2026-09-08 | SKIP-PLATFORM | 修复微光无法自然清除的问题 |
| `bf1491b2c` | 2026-06-07 | SKIP-PLATFORM | fix |
| `c2075d90f` | 2026-09-19 | SKIP-PLATFORM | 修复 |
| `c458f224d` | 2026-09-06 | SKIP-PLATFORM | 处理一些胡乱改动 |
| `c61970e9c` | 2026-10-02 | REVERSE-ALIGNED | revert(assets): demon_eye 贴图回撤到 1.21.1 原版，并把 |
| `c83192cf6` | 2026-09-05 | SKIP-PLATFORM | 暂存 |
| `c85213f17` | 2026-06-14 | SKIP-PLATFORM | part15 |
| `cb558bcf6` | 2026-06-13 | SKIP-PLATFORM | 调整PortEnchantmentHelper |
| `ce1ca67f8` | 2026-08-22 | PORTED | refactor(confluence): 拉通 1.21 战斗、召唤与实体体系 |
| `ce6daf602` | 2026-09-27 | PORTED | 火星工程师（数值上可能还有问题） |
| `d15b521d2` | 2026-09-11 | SKIP-PLATFORM | 修复战利品表的问题 |
| `d194600b9` | 2026-06-12 | SKIP-PLATFORM | remove: 删除 TerraGuns 子模块 |
| `d3ae22457` | 2026-08-17 | SKIP-PLATFORM | fix: 完善实体渲染资源校验并清理注册噪音 |
| `d4d5d0fe1` | 2026-07-22 | SKIP-PLATFORM | 修崩溃 |
| `d7c191964` | 2026-09-10 | SKIP-PLATFORM | 修一些资源错误 |
| `dbac6afed` | 2026-06-04 | SKIP-1.21-KEEPS | part |
| `dc57ba5c2` | 2026-08-16 | DO-NOT-PORT | 注释 杀杀杀 |
| `dfcc5c041` | 2026-08-15 | DO-NOT-PORT | feat: 对齐 1.21 内容与运行时行为 |
| `e145cafb5` | 2026-09-20 | DEFER-ARCH | 动态群系修改与client tick事件大一统 |
| `e2807cc12` | 2026-09-03 | PORTED | feat(otherworld): 完善资源生成、渔夫任务与图鉴变种体系 |
| `ed36d3b0d` | 2026-09-20 | SKIP-PLATFORM | 上传模块 |
| `f4625b5c4` | 2026-08-17 | PORTED | fix: 拉通生物行为并统一属性访问方式 |
| `f9332ca42` | 2026-07-12 | SKIP-PLATFORM | refactor: 修正PortLib插入脚本中的方法调用指令 |
| `f9bda32a5` | 2026-09-30 | PORTED | 加俩怪，修ai写的sb判定 |
| `fac72523a` | 2026-07-02 | PORTED | part critters |
| `fc43577f2` | 2026-08-16 | SKIP-PLATFORM | style: 清理移植改动中的无意义格式变更 |
| `fd6f0910b` | 2026-08-16 | SKIP-PLATFORM | 调整逻辑 |
