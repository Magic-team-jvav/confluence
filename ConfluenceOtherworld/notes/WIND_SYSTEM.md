# 泰拉瑞亚式风系统设计（Wind System）

> 状态：**已实现（P1 ~ P6 落地，P7 部分收尾）**，`./gradlew :ConfluenceOtherworld:compileJava` 通过
> 关联类：`org.confluence.mod.client.handler.WeatherHandler`
> 参考：[Terraria Wiki - Wind](https://terraria.wiki.gg/wiki/Wind)、[风（中文）](https://terraria.wiki.gg/zh/wiki/%E9%A3%8E)

---

## 0.0.1 1.20.1 Forge 移植说明

> 本节只记录 **1.20.1 Forge 移植**相对 1.21 NeoForge 实现的实际差异。
> 自 `## 0.0` 起的各节描述的是 1.21 NeoForge 版本的实现与契约，条文本身不随移植改变。

| # | 项目 | 1.21 NeoForge | 1.20.1 Forge |
| --- | --- | --- | --- |
| 1 | 渲染 mixin 的顶点 API | `BufferBuilder.addVertex(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;`（8 处） | `BufferBuilder.vertex(DDD)Lcom/mojang/blaze3d/vertex/VertexConsumer;`（同样 8 处） |
| 2 | 雨倾斜的 `@WrapOperation` | 目标描述符与形参类型依上述顶点 API 而定 | 目标描述符与形参类型（`double`）随 `vertex(DDD)` 相应改变 |
| 3 | 雨倾斜的剪切公式 | `x - y*s` | `x - y*s`（**不变**；1.20.1 的顶点坐标同样是相机相对） |
| 4 | 云：`ticks` 字段读取 | `ordinal = 1` 命中漂移项 | `ordinal = 1` 命中漂移项（两个注入点的参数**完全一致**） |
| 5 | 云：`@ModifyVariable` | 对应 `camZ` | `@ModifyVariable(argsOnly=true, ordinal=2)` 对应 `camZ` |
| 6 | 云：`renderClouds` 签名 | 比 1.20.1 多一个参数 | `renderClouds(PoseStack, Matrix4f, float, double, double, double)`，比 1.21 **少一个参数**，但**按类型计数的 ordinal 不变** |
| 7 | 网络层 | NeoForge 的 `IPacketS2C` + `StreamCodec` / `PacketDistributor` | PortLib 的 `IPortPacket.S2C` + `PortStreamCodec` / `PortByteBufCodecs` / `PortPacketDistributor`，注册走 `PortRegisterPayloadHandlersEvent` |
| 8 | 配置规范 | `ModConfigSpec` | `net.minecraftforge.common.ForgeConfigSpec` |
| 9 | 实体 tick 事件 | NeoForge 的 `EntityTickEvent.Post` | PortLib 的 `PortEntityTickEvent` |
| 10 | Forge 侧事件注册 | — | `PortEventHandler.addListener(...)` + `TickEvent.LevelTickEvent` |
| 11 | `IGlobalData` 与持久化 | `IGlobalData` 接口 | 接口在两版**完全一致**，`WindState` 的持久化方式不变 |
| 12 | `WindField` 依赖的原版 API | — | `Climate.Sampler.temperature()` / `ServerChunkCache.randomState()` / `BiomeTags.IS_OVERWORLD` 均存在，`WindField` **原样可用** |
| 13 | mixin 重映射 | — | 走**旧式 refmap**（注解处理器生成 `confluence.refmap.json`），`@At` 目标按 mojmap 名字书写，并由处理器自动重映射 |
| 14 | 沙尘暴范围判定的沙漠标签 | NeoForge 的 `Tags.Biomes.IS_DESERT` | 改用 `OverworldUtils.isDesert(level.getBiome(pos))`（内部是 PortLib `PortTags.Biomes.IS_DESERT` = `c:is_desert`，PortLib 同时生成 `forge:` 兼容标签），与暴风雪用 `OverworldUtils.isSnowy` 的写法一致 |
| 15 | datagen 产物 | `src/generated` 由 `runData` 刷新 | 同样；`en_us.json`/`zh_cn.json` **只存在于** `src/generated/resources`（不进版本控制），所以新增的语言键必须跑 `:ConfluenceOtherworld:runData` 才会出现在游戏内 |

> **本分支不存在的改动（N/A，非遗漏）：** 1.21 提交同时改了 4 个游戏内文档——
> `assets/confluence/ageratum/zh_cn/{confluence_changelog/changelog.md, detailed_information/command/command_list.md, detailed_information/mechanic/index.md, detailed_information/mechanic/wind.md}`。
> 1.20.1 分支**没有** `assets/confluence/ageratum/**` 这套文档体系，因此这 4 个文件在目标仓库无对应物，未移植。
> 风系统的说明以**本文件**为准；命令用法见 `/confluence wind get|set <x> <z> [durationTicks]|stop|debug`。

---

## 0.0 实现状态与偏差

**已实现：** 全局风态（m/s、连续缓变、阵风）、温度噪声方向场、网络协议、`WeatherHandler` 重写、
风粒子改造（含负风向 bug 修复与群系分流）、大风天事件、沙尘暴恒速恒向 + 实体受力、
新增暴风雪事件（含既有音效接入）、雨雪倾斜与云速渲染、生成条件阈值统一、配置、命令、语言、存档迁移。

**与设计草案的偏差（实现时做的调整）：**

| # | 草案 | 实现 | 原因 |
| --- | --- | --- | --- |
| 1 | 风类分散在 `common.data.saved` / `common.worldgen` | 统一放 `org.confluence.mod.common.wind` | 内聚：`WindMath`/`WindTier`/`WindState`/`WindField` 是一套完整契约 |
| 2 | `WindState` 作为字段嵌在 `ConfluenceData`（`SavedData`，**维度级**）里持久化 | `WindState` 改为枚举单例并实现 **`IGlobalData`**，写进 `level.dat` | 风是整个存档级别的状态，不是维度数据。与 `KillBoard`/`GameEventSystem` 同一套机制；换存档时由 `clear()` 复位，避免新存档继承旧存档的风 |
| 3 | 阈值用 `8.94 m/s` 字面量 | 由 mph 精确推导：`WindMath.WINDY_DAY_MIN = 20 * 0.44704`（**mph = 英里每小时**） | 单一来源，避免换算漂移 |
| 4 | 每 tick 粒子预算计数器 + `maxWindParticlesPerTick` 配置 | 改为「按风速档位调整随机门限」（`WeatherHandler.particleThreshold()`） | 效果等价（调用次数同比例下降）但更省，且不需要新配置 |
| 5 | 新增独立的渲染 mixin 类 | 加进已有的 `LevelRendererMixin` | 该项目已有一个针对 `LevelRenderer` 的 mixin，避免同目标多 mixin 的优先级问题 |
| 6 | 云速累加器直接累加 | 累加值按 `819200` tick 取模 | 原版云坐标按 2048 取模，对应同样周期；同时保证累加值远小于 `2^24`，长时间运行不会因 float 精度丢失而让云停住 |
| 7 | 玩家受风走 `WeatherHandler` | 独立 `client.handler.WindPushHandler` | `WeatherHandler` 只负责风态与粒子，事件交互单独成类 |
| 8 | `WindyDayGameEvent` 放 `UNRELEASED_EVENTS` 门后（D4） | **未加门**，与 `BlizzardGameEvent` 一起直接注册 | 它的消费方（瓢虫/风气球/愤怒蒲公英生成、图鉴 `windy_day`）本来就已经生效；加门反而会让"图鉴有条目但事件不存在"的不一致继续存在。**待确认** |
| 9 | 暴风雪音效「留作后续」 | **已实现**：`BlizzardAmbientSound`（`client.sound`）+ `BlizzardAmbienceHandler`，露天/室内用两个既有音效，40 tick 淡入淡出，新增配置 `blizzardAmbience` | — |
| 10 | `/confluence wind debug` 用客户端信息 | 用服务端权威信息（含方向偏差、所属风晶格、曝露度） | 服务端数据更全，且不需要额外的调试包 |
| 11 | 风车/风气球/愤怒翻滚怪沿用全局风向量 | 全部改为取**自己位置**的风（`WindField.direction` / `windAt`） | 方向既然随位置变化，用全局向量就自相矛盾 |
| 12 | 五种非生成语言"手工补或接受英文回退" | **全部补齐**：`weather_radio` 加上第二个参数，并补上 8 个方位名与 5 个档位名 | 之前的方向会整段缺失；现在 7 种语言（en/zh/de/es/pt_br/ru/lzh）都能显示风向 |

**已知遗留（非本次引入）：**

* `WeatherHandler.FLUID_PARTICLES` / `handleFluid` 仍是**死路径**：`registerFluidParticle` 从来没有被调用过，
  所以流体风粒子永远不生成。本次保留了机制与实现，只是没有注册任何流体——需要时再补生物群系与粒子类型。
* `ru_ru` 的 `info.confluence.weather_radio.thunder_snow` 原本就缺失（本次顺手补上了），其余语言完整。
* `info.terra_curio.compass.*` 这套方位名只在 3 种语言里存在 4 个方位、且带坐标 `%s` 格式，
  无法复用于风向，所以风向另立 `info.confluence.wind.compass.*`（8 个方位，7 种语言齐全）。

---

## 0. 结论摘要（TL;DR）

| 维度 | 决定 |
| --- | --- |
| 单位 | **规范单位 = m/s**。内部一律用 m/s 存储/运算，HUD 显示 m/s（括号内可选 mph） |
| 风速（大小） | **全局一致**，服务端权威。`current` 以有限速率逼近 `target`，`target` 每 1~6 分钟重掷，叠加确定性的**阵风**函数 |
| 风向 | **随空间变化**：由世界生成的 **温度噪声场梯度** 产生方向偏差，按 **64 格晶格缓存 + 双线性插值** 得到任意位置的风向 |
| 阈值 | **只认泰拉瑞亚的那一条线：20 mph = 8.94 m/s**。所有 gameplay 判定（大风天、专属生成、静风生物、沙尘暴门槛）都挂在同一条线上，两侧分工 |
| 作用强度 | 再乘局部 **曝露度**（地表 100%，地表下 24 格衰减到 0）。曝露度只影响**风粒子**，**不影响 HUD 读数** |
| 实体影响 | ❗**风不影响任何实体**（玩家、掉落物、箭、下落方块、生物全部不受风推力）。实体受风只在 **沙尘暴 / 暴风雪** 事件内、由这**两个事件各自**结算（§9.1、§9.5） |
| 网络 | 单包 `WindSpeedPacketS2C(speed, angle, gustPhase)`，**按需下发到每个玩家**（跨越晶格 / 全局变化 / 100 tick 心跳） |
| 覆盖范围 | 风粒子、雨雪倾斜、云层速度、大风天事件、沙尘暴/暴风雪（实体效果）、气象台/风速计、生成条件、命令、配置、存档迁移 |

核心公式（一眼版）：

```text
风向量(p, t) = speed(t) × gust(t) × exposure(p) × dir(p, t)      // 仅用于粒子与渲染，不施加到实体
dir(p, t)    = rotate(angleGlobal(t), ±WIND_DIR_AMP × cross(grad(p), u(angleGlobal(t))) × directionBlend)
grad(p)      = normalize(∇T(p))        // T = 世界生成温度噪声（2D，只与 x/z 有关）
gust(t)      = 1 + 0.25 × (0.6·sin(ω₁t+φ) + 0.4·sin(ω₂t+φ′))
```

---

## 1. 现状与问题清单

现有实现（[`WeatherHandler.java`](../src/main/java/org/confluence/mod/client/handler/WeatherHandler.java)、[`ConfluenceData.java`](../src/main/java/org/confluence/mod/common/data/saved/ConfluenceData.java)）：

| # | 现状 | 问题 | 本设计处置 |
| --- | --- | --- | --- |
| 1 | 服务端每天 06:00 随机一次 `windSpeed = (rand(-1,1), rand(-1,1))` | 风速是**阶跃**的，一天内完全不变；没有渐变、没有阵风、没有持续时长概念 | 改为连续漂移 + 定期重掷 + 阵风 |
| 2 | 客户端 `WIND_SPEED` 以固定 `STEP = 0.02/tick` 线性追 `WIND_SPEED_N` | 到达速率与"距离"无关；且 `handleWindSpeed` 先把旧值赋给新值再追，收包瞬间会**跳变** | 改为固定速率 + 角量最短插值，收包不重置当前值 |
| 3 | 方向 = `Direction.getNearest(x, 0, z)` | 砍成 4 个正方向，粒子只能沿 ±X/±Z 飞；45° 风被吞掉 | 引入连续角度 `windAngle`；`Direction` 仅作兼容保留 |
| 4 | `handleBlock/handleFluid` 早退条件 `getWindSpeedX() < 0.1 && getWindSpeedZ() < 0.1` | **逻辑 bug**：飓风式纯 -X 风（x=-1, z=0）会被判定为无风，什么都不生成 | 改为按**风速模长**判断 |
| 5 | `windSpeedInfo` 直接取**目标值**模长 | HUD 显示与实际吹的风不一致（当前值是插值中的） | HUD 取含阵风的当前值 |
| 6 | 单位是裸 `[-1,1]`，无单位 | 无法对齐原作的"20 mph 大风天"；粒子/风车/着色器的系数是拍脑袋的 | 统一 m/s + 档位表，并提供**兼容归一化访问器**让美术系数零改动 |
| 7 | 生成条件里 3 处写死 `0.25F`（0.5 模长）、沙尘暴写死 `0.36F` | 阈值散落、与图鉴文本"20 mph"不一致 | 收敛到 `WindTier` 常量 |
| 8 | 图鉴已有 `windy_day` 过滤条目（瓢虫、风气球、愤怒蒲公英），但**没有对应事件** | 死数据 | 新增 `WindyDayGameEvent` |
| 9 | 沙尘暴期间风速随机、风向随机 | 原作沙尘暴是"恒向恒速风" | 事件期间锁定 `speed` 与 `directionBlend = 0` |
| 10 | 生物群系粒子表给**所有**主世界群系都注册了 落叶+沙+雪 | 雪原会飘沙粒、沙漠会飘雪 | 按群系标签分流注册 |
| 11 | 风只影响粒子 | 无雨、无云；且沙尘暴/暴风雪没有实体层面的表现（沙尘暴只有生成条件与怪物 AI 用到风） | 新增雨倾斜 + 云速两个渲染消费端；实体受风交给沙尘暴/暴风雪两个事件（§9.1、§9.5） |

---

## 2. 单位与档位

### 2.1 单位换算

**`mph` = miles per hour = 英里每小时。** 不是"米每小时"，也不是别的任何东西：

```text
1 mile = 1609.344 m（国际英里，精确值）
1 mph  = 1609.344 / 3600 = 0.44704 m/s
```

实际实现的常量（`WindMath`，**m/s 一律由 mph 推导，不写死字面量**）：

```java
float MPH_TO_MPS       = 0.44704F;                    // 英里每小时 → 米每秒
float BLOCKS_PER_TICK  = 0.05F;                       // 1 m/s = 0.05 方块/tick（1 tick = 50 ms，1 方块 = 1 m）
float REF_SPEED        = 30.0F * MPH_TO_MPS;          // 13.4112 m/s —— 旧单位 1.0 ↔ 30 mph
float WINDY_DAY_MIN    = 20.0F * MPH_TO_MPS;          // 8.9408  m/s —— 原作大风天阈值 20 mph
float SANDSTORM_FORCED = REF_SPEED;                   // 30 mph
```

| 原作 mph | m/s | 用途 |
| --- | --- | --- |
| 3 | `1.34112` | 静风/微风分界 |
| 10 | `4.47040` | 微风/有风分界 |
| **20** | **`8.94080`** | **大风天阈值（唯一 gameplay 阈值）** |
| 30 | `13.41120` | 旧单位满刻度；沙尘暴强制风速 |
| 45 | `20.11680` | 烈风档下界（仅事件/命令可达） |

> ⚠️ 反例提醒：如果把 mph 当成 m/s 直接用，20 会变成 20 m/s = **44.7 mph**，是正确值的 2.24 倍。
> 因此本实现里**没有任何一处**出现裸的 mph 数值参与 m/s 运算——全部经过 `MPH_TO_MPS` 或上述推导常量。

> `REF_SPEED` **只**用于两件事：把旧存档的 `[-1,1]` 风速换算成 m/s、给美术系数（风车转速、着色器）提供归一化基准。
> 它**不参与任何 gameplay 判定**，所以即使原作风速计满刻度不是 30 mph，也只需改这一个常量。

> HUD 的规范显示单位是 **m/s**（`"8.9 m/s"`）；`/confluence wind get` 作为调试命令会同时给出 mph。

### 2.2 阈值 —— 对齐泰拉瑞亚

**主阈值只有一条：20 mph = 8.94 m/s。** 原作的「大风天」就是以它为界（本仓库自己的图鉴文本也这么写：
"A Windy Day is an event that occurs when the wind speed exceeds 20 mph."，5 种语言一致）。
本设计把所有 gameplay 判定都挂到**同一条线**上，两侧分工，不再另造门槛：

| 判定 | 原作条件 | 本设计 |
| --- | --- | --- |
| 大风天事件 `WindyDayGameEvent` | 风速 > 20 mph 且白天 | `≥ 8.94 m/s` |
| 大风天专属生成（瓢虫 / 风气球 / 愤怒蒲公英） | 大风天 | `≥ 8.94 m/s` |
| 沙尘暴门槛 `SandstormGameEvent` | 沙漠 + 大风 | `≥ 8.94 m/s`（配置项 `windSandstormMinSpeed`） |
| 静风生物（蝴蝶 / 臭虫 / 萤火虫） | 非大风天 | `< 8.94 m/s` |
| 暴风雪 `BlizzardGameEvent` | **雪原 + 下雨**（与风速无关） | 不看风速 |
| 沙尘暴期间强制风速 | 恒速恒向 | `13.41 m/s`（30 mph，配置项） |

**视觉强度分档（`WindTier`，仅用于粒子密度 / 云速 / 雨倾角的线性插值）：**

| 档位 | m/s | mph | 风粒子 | 云速 | 雨倾角 |
| --- | --- | --- | --- | --- | --- |
| `CALM` 静风 | `< 1.34` | `< 3` | 关闭 | 静止 | ~0° |
| `LIGHT` 微风 | `1.34 ~ 4.47` | `3 ~ 10` | 稀疏 | 0.3× 原版 | 7° |
| `BREEZY` 有风 | `4.47 ~ 8.94` | `10 ~ 20` | 明显 | 0.6× 原版 | 15° |
| `WINDY` 大风天 | `≥ 8.94` | `≥ 20` | 密集 | 1.0× 原版 | 31° |
| `GALE` 烈风 | `≥ 20.12` | `≥ 45` | 最大 | 1.5×（封顶） | 封顶 42° |

> ⚠️ 除 **20 mph** 外，`3 / 10 / 45 mph` 这几条线是**本设计的视觉插值刻度，不是原作官方档位**。
> 原作并没有"微风/有风/烈风"这样的分类，只有 mph 数值 + 大风天阈值；这几条线只决定"粒子多密、云多快、雨多斜"，改它们不影响任何 gameplay。
>
> ⚠️ 沙尘暴的**确切风速门槛**未能从可用渠道核实（本环境的 `web_search` 不返回摘要，wiki 直连 403）。
> 因此它与大风天统一到 20 mph，并做成配置项 `windSandstormMinSpeed`，你可以随时按实测修正。
> `GALE` 不会被自然重掷抽到，只由事件/命令写入。

---

## 3. 分层架构与数据流

```text
┌─ 服务端（权威） ───────────────────────────────────────────────────────┐
│                                                                        │
│  WindState（全局标量，随存档持久化）                                    │
│    speedCurrent / speedTarget / angleCurrent / angleTarget / timer      │
│    + 事件覆盖：forcedSpeed / forcedAngle / directionBlend / forceTicks  │
│         │                                                              │
│         │ LevelTickEvent.Post（主世界，每 tick）                        │
│         ▼                                                              │
│  WindMath（纯函数，两端共用）                                           │
│    gust(gameTime, phase) / exposure(level, pos) / tier(speed) / 换算    │
│         │                                                              │
│         │                                                              │
│  WindField（空间方向场，仅服务端可算）                                   │
│    T = level.getChunkSource().randomState().router().temperature()     │
│    64 格晶格缓存 T → ∇T 单位向量 → 双线性插值 → 偏差角                  │
│         │                                                              │
│         ├──► 生成条件：SpawnPlacementChecks（§9.2，在该位置求风）        │
│         ├──► WindyDayGameEvent（§9.3）                                  │
│         ├──► SandstormGameEvent  ← 自身结算实体受力（§9.1、§9.4）        │
│         ├──► BlizzardGameEvent   ← 自身结算实体受力（§9.1、§9.5）        │
│         └──► WindSpeedPacketS2C → 各玩家（§7）                          │
│                                                                        │
│  ❗ 风系统本身不产生任何实体推力（无 WindDrift、无逐实体结算）           │
└────────────────────────────────────────────────────────────────────────┘
                                    │  按需下发
                                    ▼
┌─ 客户端 ──────────────────────────────────────────────────────────────┐
│  WeatherHandler（重写，仍是唯一入口）                                   │
│    收包 → targetSpeed/targetAngle/gustPhase                            │
│    每 tick → 同款 gust + 同款插值速率 → WIND_SPEED（局地风向量, m/s）    │
│         ├──► 风粒子（ClientLevelMixin，§8.4）                          │
│         ├──► 风速计 / 风车 / 灯笼夜 sprite（§11，走兼容访问器）          │
│         ├──► 气象台 HUD（§11）                                         │
│         ├──► 雨雪倾斜（LevelRendererMixin @WrapOperation，§10.1）      │
│         └──► 云层速度（LevelRendererMixin，§10.2）                      │
└────────────────────────────────────────────────────────────────────────┘
```

**关键设计取舍 —— 为什么方向场只在服务端算？**
`ClientLevel` 拿不到 `RandomState`（`ClientLevel.ClientLevelData` 里**没有** worldSeed 字段，`ClientChunkCache` 也不暴露 `randomState()`），
而 `RandomState.create(registries, settingsKey, levelSeed)` 需要 worldSeed。
所以客户端无法自行求温度噪声。而所有需要"位置精确风向"的东西中：

* **生成条件判定**（瓢虫/蒲公英在哪个坐标生成）→ 服务端，逐位置精确 ✓
* **沙尘暴 / 暴风雪的实体受力** → 服务端事件自己结算，逐实体位置精确 ✓
* **粒子 / 雨 / 云 / 风速计 / 气象台** → 都在玩家 48 格渲染半径内，**用玩家所在处的风向**足够精确 ✓
* **远处的风车方块实体** → 用玩家处风向近似（已知限制，见 §15）

因此**不需要**给客户端喂噪声场，只需要给每个玩家下发"你脚下的风向量"。这是本设计最大的简化，代价极小。

---

## 4. 全局风态 `WindState`

位置：`org.confluence.mod.common.wind.WindState`
持久化：`enum WindState implements IGlobalData { INSTANCE; ... }`——**写进 `level.dat`**，与 `KillBoard`/`GameEventSystem` 同一套机制
（注册见 `ModEvents`，读写见 `PrimaryLevelDataMixin`；`serializeKey() = "confluence:wind"`）。
风是整个存档级别的状态而非维度数据，所以**不**放进 `ConfluenceData`（`SavedData` 是维度级的）；换存档时 `clear()` 复位。

### 4.1 字段

```java
public final class WindState {
    private float speedCurrent;              // m/s，实际吹的风速（不含阵风）
    private float speedTarget;               // m/s，目标风速
    private float angleCurrent;              // rad，全局风向
    private float angleTarget;               // rad
    private int   timer;                     // 距离下次重掷还剩多少 tick
    private float gustPhase;                 // 阵风相位（0~1，随存档固定）

    // 事件/命令覆盖层（不持久化，重载后失效）
    private float forcedSpeed   = -1.0F;     // <0 表示无覆盖
    private float forcedAngle   = Float.NaN;
    private int   forceTicks    = 0;
    private float directionBlend = 1.0F;     // 0 = 关闭温度噪声偏差（沙尘暴）
    private float speedFloor    = 0.0F;      // 事件期间风速下限（大风天）
}
```

### 4.2 每 tick（`LevelTickEvent.Post`，仅主世界）

```java
public void tick(ServerLevel level) {
    if (forceTicks > 0 && --forceTicks == 0) clearOverride();

    float targetSpeed = forcedSpeed >= 0.0F ? forcedSpeed : Math.max(speedTarget, speedFloor);
    float targetAngle = Float.isNaN(forcedAngle) ? angleTarget : forcedAngle;

    speedCurrent = approach(speedCurrent, targetSpeed, SPEED_RATE);          // 0.025 m/s per tick
    angleCurrent = approachAngle(angleCurrent, targetAngle, ANGLE_RATE);     // 0.20 °/tick

    if (--timer <= 0 && forcedSpeed < 0.0F) reroll(level.random);
}
```

`approachAngle` 必须走**最短角差**（把差值规约到 `[-π, π]`），不能对 cos/sin 分量分别插值。

### 4.3 重掷分布（`reroll`）

```java
private void reroll(RandomSource random) {
    float roll = random.nextFloat();
    if (roll < windyChance && windyCooldown <= 0) {  // windyChance 取配置，默认 0.04
        speedTarget = Mth.nextFloat(random, WindMath.WINDY_DAY_MIN, NATURAL_MAX_SPEED); // 大风天档
        windyCooldown = WINDY_COOLDOWN;              // 2 个游戏日内不再抽到大风天档
    } else if (roll < 0.15F) {                       // 有风档 10 ~ 20 mph
        speedTarget = Mth.nextFloat(random, WindTier.BREEZY.min(), WindMath.WINDY_DAY_MIN);
    } else {                                         // 偏静风（乘积分布，严重偏向 0）
        speedTarget = Mth.nextFloat(random, 0.0F, WindTier.BREEZY.min()) * random.nextFloat();
    }

    float delta = Mth.nextFloat(random, 30.0F, 110.0F) * Mth.DEG_TO_RAD;
    if (random.nextFloat() < 0.20F) delta = Mth.PI - delta * 0.5F;   // 20% 概率近似反向（原作的风会转向）
    angleTarget = angleCurrent + (random.nextBoolean() ? delta : -delta);

    timer = REROLL_MIN + random.nextInt(REROLL_MAX - REROLL_MIN);    // 2 ~ 10 分钟
}
```

> ❗**频率必须按"每个游戏日"算，不能只看百分比。**
> 一个游戏日 = 24000 tick = 20 分钟；重掷平均每 6 分钟一次 ⇒ **约 3.3 次/游戏日**。
>
> | 参数 | 早期（错） | 现在 | 效果 |
> | --- | --- | --- | --- |
> | 重掷间隔 | 1 ~ 6 分钟（均 3.5） | **2 ~ 10 分钟（均 6）** | 5.7 次/日 → 3.3 次/日 |
> | 大风天档概率 | 10% | **4%** | **每 ~1.7 日一次 → 每 ~7.5 日一次** |
> | 有风档概率 | 25% | **15%** | 每天一次 → 每 ~2 日一次 |
> | 大风天冷却 | 无 | **2 个游戏日** | 硬上界：绝不出现接连两天大风天 |
>
> 早期那组数意味着**几乎每隔一天就有一次大风天**，而大风天事件又会把风速下限锁定到入夜
> （见 §9.3），叠起来体感就是"一直在刮大风"。这正是"哪有这么频繁的大风"的来源。
>
> 注意 `windWindyChance` 是**配置项**：已经生成的 `config/confluence-common.toml` 里会保留旧值 `0.1`，
> 改默认值不会自动生效，需要玩家自己改成 `0.04`。**冷却 `WINDY_COOLDOWN` 是代码常量**，
> 所以即使配置填得很高，频率也有硬上界。

### 4.4 阵风 `WindMath.gust`

```java
/// 周期性抖动，确定性函数：服务端（生成条件 / 事件受力）与客户端（渲染 / HUD）结果完全一致，因此无需额外同步。
public static float gust(long gameTime, float phase) {
    float s = (gameTime % 72000L) * 0.05F;                    // 秒
    return 1.0F + GUST_AMP * (0.6F * Mth.sin(s * 1.0F + phase * Mth.TWO_PI)
                            + 0.4F * Mth.sin(s * 2.5F + phase * 4.0F * Mth.TWO_PI));
}
// GUST_AMP = 0.25F  → 瞬时风速在 0.75× ~ 1.25× 目标之间摆动，周期约 6.3s / 2.5s
```

`phase` 随存档固定（`RandomSource.create(seed).nextFloat()`），**随包下发**（客户端不能自造，否则两端不一致）。

### 4.5 事件/命令覆盖 API

```java
public void override(float speed, float angle, int ticks);   // 强制恒风（沙尘暴、Boss 技）
public void setSpeedFloor(float floor);                      // 风速下限（大风天，防止事件中途掉档抖动）
public void setDirectionBlend(float blend);                  // 0 = 关闭噪声偏差（沙尘暴恒向）
public void setRerollHold(int ticks);                        // 暂停重掷（事件期间锁档）
public void clearOverride();
```

---

## 5. 空间方向场 `WindField`

位置：`org.confluence.mod.common.worldgen.WindField`（仅服务端调用）

### 5.1 温度噪声来源（已核实 API）

```java
Climate.Sampler sampler = level.getChunkSource().randomState().sampler();
DensityFunction temperature = sampler.temperature();          // RandomState#sampler() 存在（RandomState.java:141）
double T = temperature.compute(new DensityFunction.SinglePointContext(x, SAMPLING_Y, z));
```

* `NoiseRouterData` 里**没有** `TEMPERATURE` 这个 `ResourceKey<DensityFunction>`（只有 `overworld/continents`、`overworld/depth` 等），所以**不能**像 `DrunkWorld` 那样 `registryAccess().holderOrThrow(...)`。必须走 `RandomState.router()` 或 `sampler()`。
* 原版主世界的温度路由是 `DensityFunctions.shiftedNoise2d(..., Noises.TEMPERATURE)`（`NoiseRouterData.java:404`），**是 2D 的**：只与 `(x, z)` 有关，与 `y` 无关。因此 `SAMPLING_Y` 取常量（如海平面 `64`）即可，且梯度是一个良定义的 2D 向量场。
* `RandomState`/`Sampler` 引用应在世界加载时**取一次缓存**（`LevelTickEvent` 首帧或 `GameEvent.open`），不要每 tick 走 `getChunkSource().randomState()`。
* 非主世界（下界 legacy 温度噪声是 3D 的、末地无意义）：**禁用方向偏差**，`directionBlend` 视为 0，用全局角度。

### 5.2 晶格采样与梯度

```java
public static final int CELL = 64;      // 风晶格边长（方块）

/// 晶格点 (i, j) 的温度，LRU 缓存
private double temperatureAt(ServerLevel level, int i, int j) {
    return CACHE.computeIfAbsent(key(i, j), k ->
        sampler.temperature().compute(new SinglePointContext(i * CELL, SAMPLING_Y, j * CELL)));
}

/// 晶格点处的温度梯度方向（单位向量）。
/// 直接复用相邻晶格点的温度差，中心差分：∇T ≈ (T(i+1,j)-T(i-1,j), T(i,j+1)-T(i,j-1)) / (2·CELL)
private Vector2f gradientDir(ServerLevel level, int i, int j) { ... }
```

### 5.3 局部风向（无绕环公式）

```java
/// dir(p) = rotate(angleGlobal, WIND_DIR_AMP * cross(grad(p), u(angleGlobal)) * directionBlend)
public static Vector2f localDirection(ServerLevel level, WindState state, double x, double z) {
    Vector2f g = bilinearGradientDir(level, x, z);            // 单位向量，已归一化
    float gx = Mth.cos(state.angleCurrent()), gz = Mth.sin(state.angleCurrent());
    // cross(a, b) = a.x*b.z - a.z*b.x = sin(θ_b - θ_a)，天然无 ±π 绕环问题
    float dTheta = WIND_DIR_AMP * (g.x * gz - g.y * gx) * state.directionBlend();
    float theta  = state.angleCurrent() + dTheta;
    return new Vector2f(Mth.cos(theta), Mth.sin(theta));
}
```

**角度约定**：`dir = (cos θ, sin θ)` 映射到 `(x, z)`，即 θ = 0 指向 +X（东）。

**为什么用 `cross` 而不是 `θ_grad - θ_global`**：

* 角度差在 ±π 处不连续，会让风向在"温度梯度正好背对全局风"的地方出现撕裂线；
* `cross(a,b)` 是两单位向量的叉积 = `sin(Δθ)`，**处处连续、天然有界于 [-1,1]**；
* 偏差角因此被限制在 ±`WIND_DIR_AMP`（默认 90°），不会出现"局部风向完全脱离全局趋势"的怪现象；
* 物理含义清晰：**温度梯度把风向往自己这边"拽"**，拽的力度 ∝ 两者夹角的正弦（垂直时最大，同向/反向时为 0）。

**双线性插值**必须作用在**单位向量**上再归一化（不能插角度，也不能插未归一化的梯度）；四个角点中若有任一为零向量则退回 `u(angleGlobal)`。

### 5.4 缓存与性能

| 项 | 值 |
| --- | --- |
| 晶格边长 | 64 方块（温度场相关长度 ~数百方块，64 足够平滑） |
| 缓存实现 | `Long2ObjectLinkedOpenHashMap<Vector2f>`（LRU，上限 2048 ≈ 覆盖 90×90 晶格 ≈ 5800×5800 方块） |
| 单次求值 | 4 次晶格查表（角点）+ 一次归一化；**晶格点温度只在首次访问时计算** |
| 线程 | 只在服务端主线程访问（`LevelTickEvent` / `EntityTickEvent`），无并发问题 |

### 5.5 `WindField.windAt(level, pos)` —— 位置相关的风向量

> 供**生成条件判定**与**事件**使用（例如"愤怒蒲公英只在风把它吹向玩家时生成"、沙尘暴/暴风雪对实体施加推力）。
> **不用于任何"风自动影响实体"的逻辑**——风系统本身不碰实体（§0）。

```java
/// 返回该位置的风向量，单位：方块/tick
public static Vec3 windAt(ServerLevel level, Vec3 pos) {
    WindState s = WindState.get(level);
    float speed = s.instantSpeed(level.getGameTime());            // 含阵风
    if (speed < MIN_APPLY_SPEED) return Vec3.ZERO;                // 2.0 m/s 以下直接跳过
    Vector2f dir = localDirection(level, s, pos.x, pos.z);
    float mps = speed * WindMath.exposure(level, BlockPos.containing(pos));
    return new Vec3(dir.x, 0.0, dir.y).scale(mps * BLOCKS_PER_TICK);
}

/// 只要方向、不要强度（生成条件里判"风是否朝向玩家"用）
public static Vector2f windDirAt(ServerLevel level, double x, double z) { ... }
```

---

## 6. 曝露度 `WindMath.exposure`

```java
public static final float EXPOSURE_DEPTH = 24.0F;   // 地表下 24 格衰减到 0

public static float exposure(Level level, BlockPos pos) {
    int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
    int depth = surface - pos.getY();
    float vertical = Mth.clamp(1.0F - depth / EXPOSURE_DEPTH, 0.0F, 1.0F);
    float sky = level.canSeeSky(pos) ? 1.0F : 0.35F;   // 屋檐/树冠下减半
    return vertical * sky;
}
```

* 客户端**也调用同一个函数**（`Level` 通用 API，两端都可用）。
* 作用范围：**只用于风粒子**。粒子在地下/洞穴里应当完全消失，这条线给了平滑的深度衰减，比现有的单点遮挡判定更自然。
* **不影响 HUD 读数**：气象台/风速计显示的永远是全局 `speed`，与位置无关（原作风格：地下无风，但风速计照样读数）。
* 雨倾斜与云速**不乘**曝露度——雨和云在露天渲染，玩家站在屋檐下看雨时雨仍应被风吹斜。

---

## 7. 网络协议

改造现有 [`WindSpeedPacketS2C`](../src/main/java/org/confluence/mod/network/s2c/WindSpeedPacketS2C.java)，**保留类名与包名**（减少 diff 噪音），只扩字段：

```java
public record WindSpeedPacketS2C(float speed, float angle, float gustPhase) implements IPacketS2C {
    public static final StreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::speed,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::angle,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::gustPhase,
            WindSpeedPacketS2C::new
    );
    @Override public void work(Player player) { WeatherHandler.handleWindSpeed(this); }
}
```

| 字段 | 含义 | 说明 |
| --- | --- | --- |
| `speed` | 全局风速 m/s（**不含**阵风、**不含**曝露度） | 客户端用 `gust(gameTime, gustPhase)` 自行求出瞬时值 |
| `angle` | **接收者所在位置**的局地风向 rad（已含温度噪声偏差与 `directionBlend`） | 服务端用 `WindField.localDirection(level, state, player.x, player.z)` |
| `gustPhase` | 阵风相位 | 保证两端阵风完全一致 |

### 7.1 下发时机

实现选的是**固定频率逐玩家下发**，而不是"跨晶格 + 变化阈值 + 心跳"那套按需方案：

```java
// TickEvents.levelTick$Post（主世界，服务端）
WindState.INSTANCE.tick(serverLevel);
if (serverLevel.getGameTime() % WIND_SYNC_INTERVAL == 0) {   // WIND_SYNC_INTERVAL = 5
    WindSpeedPacketS2C.sendToAll(serverLevel);               // 逐玩家算各自的局地风向
}
```

* **为什么不做按需下发**：按需需要为每个玩家维护 `lastCell / lastSpeed / lastAngle`，要么往 `ServerPlayer`
  上挂字段、要么自己维护一张会泄漏的表；而载荷只有 3 个 float，固定频率的代价 ≈ **4 包/秒/玩家**
  （10 个玩家也就 40 包/秒，原版的粒子与实体同步比这高好几个数量级）。用简单换来的开销可以忽略。
* **每 5 tick 一包会不会跳**：不会。客户端对角度用的是"按剩余角差取比例、并夹在上下限之间"的跟随
  （`ANGLE_FOLLOW = 0.15`，下限与服务端的全局转向速率一致），所以即使跨越风晶格导致局地风向突变 90 度，
  也能在约 1 秒内平滑收敛，没有跳变。
* **登录 / 换维度 / 重生**：`PlayerUtils.syncSavedData` 里直接补一包，不必等下一个 5 tick。

---

## 8. 客户端 `WeatherHandler` 重写

保留类名与包路径（外部有 4 处引用），内部拆成"状态 + 平滑 + 查询"，**不再**把粒子表、风态、HUD 字符串糅在一个类里。

### 8.1 公开 API（与实现一致，含当前消费方）

```java
public final class WeatherHandler {
    // —— 对外 API ——
    public static float  windSpeed()         // m/s，含阵风，不含曝露度（HUD / 雨 / 云用）
    public static float  windExposedSpeed()  // m/s，含阵风与玩家处曝露度（粒子用）      → WeatherVaneBlock
    public static Vector2f windVector()      // 局地风向量，m/s，含阵风与曝露度
    public static Vec3   windVec3()          // 同上，方块/tick（玩家受风用）              → WindPushHandler
    public static float  windAngle()         // 局地风向 rad                              → WeatherVaneBlock
    public static boolean isWindyDay()       // 是否达到 20 mph（大风天）
    public static boolean isGusting()        // gust 系数 > 1.15，用于装饰性抖动
    public static float  exposure()          // 玩家处曝露度
    public static float  particleThreshold() // 由 showWindParticles 与风速合成的随机门限   → ClientLevelMixin
    public static Component windSpeedInfo()  // "8.9 m/s"                                → InformationHandlerMixin
    public static Component windCompassInfo()// "东南风"                                  → InformationHandlerMixin
    public static float  precipitationSlant()// 雨雪剪切系数                               → LevelRendererMixin
    public static float  cloudAccumulator()  // 云速虚拟 tick 累加器                       → LevelRendererMixin

    // —— 兼容层：只保留"归一化量"，让既有美术系数零改动 ——
    public static float getLegacyWindSpeedX()  // → TFPinWheelMixin, LanternNightSprite
    public static float getLegacyWindSpeedZ()

    // —— 公开字段 ——
    public static Direction windDirection;   // 水平 4 向，仅供方块级遮挡判定 → ClientLevelMixin
}
```

**实现细节（private，不进对外 API）**：`WIND_SPEED`（类内风向量）、`windBlocksPerTick`、`particleDensity`、
`instantSpeed`、`horizontalDirection`、`tickCloudAccumulator`。

> ❗ 已**删除**不再需要的旧客户端接口：`getWindSpeedX()` / `getWindSpeedZ()`（与已删的
> `ConfluenceData.getWindSpeedX/Z` 是同名残留）、`windTier()`、以及 `WIND_SPEED` 的公开暴露。
> `windSpeed()` / `isWindyDay()` / `isGusting()` / `windVector()` / `exposure()` 目前**没有调用方**，
> 但它们是本类对外契约的一部分（例如将来"在大风天放风筝"的成就需要 `isWindyDay()`），故保留。
}
```

### 8.2 每 tick 平滑（`handle()`）

```java
public static void handle() {
    ClientLevel level = Minecraft.getInstance().level;
    if (level == null) return;                       // ← 现在没有这个判断，切世界时会 NPE 风险

    speed += Mth.clamp(targetSpeed - speed, -SPEED_RATE, SPEED_RATE);     // 同服务端速率
    angle  = approachAngle(angle, targetAngle, ANGLE_RATE);               // 最短角差

    float inst = speed * WindMath.gust(level.getGameTime(), gustPhase);
    float exp  = WindMath.exposure(level, player.blockPosition());
    WIND_SPEED.set(Mth.cos(angle) * inst * exp, Mth.sin(angle) * inst * exp);
    windDirection = horizontalDirection();       // 水平 4 向，只给方块级遮挡判定用
    tickCloudAccumulator(inst);                  // 云速累加器必须在这里（每 tick）推进
}
```

**与旧实现的两个关键差异**：收包时**不**重置 `speed/angle`（消除跳变）；平滑用「按剩余量的比例 + 上下限夹取」
而非固定小步长（既不会永远追不上，又不会在跨越风晶格时突变）。

### 8.3 生物群系粒子表重做（`initialize`）

现状给**每个**主世界群系都注册了 `leaves + sand + snow`，导致雪原飘沙。改为按 tag 分流：

```java
public static void initialize(Player player) {
    for (Holder<Biome> biome : /* 主世界群系 */) {
        ResourceKey<Biome> key = biome.getKey();
        registerBlockParticle(key, map -> {
            if (biome.is(BiomeTags.IS_FOREST) || biome.is(BiomeTags.IS_JUNGLE) || biome.is(BiomeTags.IS_TAIGA))
                leavesParticles(map);
            if (biome.is(BiomeTags.IS_BEACH) || biome.is(BiomeTags.IS_BADLANDS) || isDesert(biome))
                sandParticles(map);
            if (biome.is(BiomeTags.IS_SNOWY) || biome.is(BiomeTags.IS_MOUNTAIN))
                snowParticles(map);
        });
    }
}
```

同时把 `leavesParticles` 的逐方块枚举改为 **按 `BlockTags.LEAVES` 打表**（`map` 的键类型是 `Block`，所以仍需展开；但可以遍历 `BuiltInRegistries.BLOCK` 用 `is(BlockTags.LEAVES)` 收集，自动覆盖模组树叶），枫叶/樱花等特殊材质单独覆写。

### 8.4 粒子生成改造

* **修 bug**：`handleBlock` / `handleFluid` 的早退改为 `windExposedSpeed() < WindTier.CALM.max`（顺带把 `< 0.1` 的负风漏判修掉）。
* **连续方向**：`isAvailable` 的方向判定与 `apply` 的偏移量从 `windDirection.getStepX()/getStepZ()` 换成 `windAngle()` 的 `(cos, sin)`，彻底摆脱 4 向量化。`Facing.POSITIVE_Y` / `Facing.HORIZONTAL` 的结构可以保留，只是把"阶梯方向"换成"单位向量 + 点积"。
* **粒子速度**：

```java
float mps = windExposedSpeed();
float v   = Mth.clamp(mps * BLOCKS_PER_TICK * PARTICLE_COUPLING * Mth.nextFloat(random, 0.4F, 1.0F), 0.0F, 0.2F);
particle.setParticleSpeed(dir.x * v, 0.0, dir.y * v);
// PARTICLE_COUPLING = 0.25 → 大风天(8.94 m/s) 时约 0.11 方块/tick
```

* **密度随风速**：`ClientLevelMixin` 里把 `ClientConfigs.showWindParticles`（原有 0~100 比例，语义保留）与风速档位相乘：

```java
float density = Mth.clamp((windExposedSpeed() - WindTier.CALM.max) / (WindTier.GALE.min - WindTier.CALM.max), 0.0F, 1.0F);
int threshold = 1 + Mth.floor((100 - ClientConfigs.showWindParticles) * (1.0F - density * 0.7F));
```

* **每 tick 粒子预算**：`doAnimateTick` 是对玩家周围每个方块位置调用一次，高密度下粒子数会爆炸。加一个 `WeatherHandler` 内的每 tick 计数器，超过 `MAX_WIND_PARTICLES_PER_TICK`（默认 12，可配）就静默丢弃；这比调低 `showWindParticles` 更能保证"近处有、远处省"。

---

## 9. 服务端接入

### 9.1 实体受风 —— 只由沙尘暴 / 暴风雪两个事件结算

**风系统本身不产生任何实体推力。** 没有通用的 `WindDrift`，没有"大风天把掉落物吹跑"，也没有平时状态下的任何逐实体结算。

| 来源 | 是否影响实体 | 说明 |
| --- | --- | --- |
| 平时（任何风速，含大风天） | ❌ **完全不影响** | 5 m/s 还是 13 m/s，掉落物/箭/生物/玩家的运动学表现完全一致 |
| `WindyDayGameEvent` | ❌ 否 | 只做生成条件与视觉（粒子/云/雨） |
| `SandstormGameEvent` | ✅ 是 | 事件激活期间，对作用范围内的实体施力（沙漠 + 地表） |
| `BlizzardGameEvent` | ✅ 是 | 事件激活期间，对作用范围内的实体施力（雪原 + 露天 + 雨中） |

实体受力的**唯一入口**仍是 [`TickEvents.entityTick$Post`](../src/main/java/org/confluence/mod/common/event/game/TickEvents.java)（`EntityTickEvent.Post`），但**第一道门是"有没有风推事件在跑"**——平时 100% 早退，开销等于一次 `instanceof`：

```java
@SubscribeEvent
public static void entityTick$Post(EntityTickEvent.Post event) {
    Immunity.tick(event.getEntity());
    GameEventSystem.INSTANCE.pushEventWind(event.getEntity());   // ← 唯一入口
}
```

```java
// GameEventSystem
public void pushEventWind(Entity entity) {
    for (GameEvent event : getAllEventInstances()) {
        if (event.started() && event instanceof WindPushedEvent pushed) {   // 平时循环体不会进入
            pushed.pushWindOn(entity);
            return;                                                        // 同一时刻只允许一个风推事件
        }
    }
}
```

```java
/// 由沙尘暴、暴风雪各自实现；风系统不认识这个接口
public interface WindPushedEvent extends GameEvent {
    /// 该实体的当前位置是否在本事件的受力范围内
    boolean inWindZone(ServerLevel level, BlockPos pos);
    /// 该实体的推力系数
    float couplingFor(Entity entity);

    default void pushWindOn(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (level.dimension() != OverworldUtils.dimension()) return;
        if (entity instanceof Player) return;                 // 玩家走客户端，见下
        if (entity.onGround()) return;                        // 落地不推，避免贴地滑步
        if (!inWindZone(level, entity.blockPosition())) return;

        Vec3 wind = WindField.windAt(level, entity.position());          // 方块/tick
        if (wind == Vec3.ZERO) return;
        entity.setDeltaMovement(entity.getDeltaMovement().add(wind.scale(couplingFor(entity))));
        entity.hurtMarked = true;                            // 让服务端速度能同步给客户端
    }
}
```

**系数表（只有这两个事件用）**：

| 事件 | 对象 | coupling | 事件风速下的实际受力 |
| --- | --- | --- | --- |
| 沙尘暴 | 非玩家实体（`!onGround()`） | `0.05` | 13.41 m/s → 0.447 方块/tick × 0.05 ≈ **0.022 方块/tick** |
| 沙尘暴 | 本地玩家 | 顺风 `0.01` / 逆风阻力 `0.03` | 原作沙尘暴中玩家移动明显变困难 |
| 暴风雪 | 非玩家实体（`!onGround()`） | `0.04` | ≈0.018 方块/tick，另加一个向下的轻微雪压 `-0.004` |
| 暴风雪 | 本地玩家 | 顺风 `0.015` | 轻微，几乎无感 |

**玩家为什么必须走客户端**：`ServerPlayer` 的移动由客户端权威，服务端 `setDeltaMovement` 不会传回客户端，等于无效。所以本地玩家的推力在**客户端 tick** 里结算（挂在 `GameClientEvents` 已有的 `minecraft.player != null` 分支内），数据取自 `WeatherHandler.windVec3()`：

```java
// 仅当事件激活时才会被执行
if (player.onGround()) return;
Vec3 wind = WeatherHandler.windVec3();                 // 方块/tick
player.push(wind.x * coupling, 0.0, wind.z * coupling);
```

> **区分两件事**：以上是"**风推实体**"。而**生物 AI 主动读取风向量做自己的移动**（风气球用风漂移、愤怒翻滚怪沿风向滚、风车随风转）是**既有行为，保留**——那是实体在读风，不是风在推实体，不受本节约束。它们的风向量改从 `WindField.windAt(level, 自己的位置)` 取（现在取的是全局值，位置无关）。

### 9.2 生成条件阈值统一

现有 5 处散落阈值 → 全部换成同一条线 `WindTier.WINDY`（20 mph = 8.94 m/s），并且**在生成坐标处求风**（而不是全局值），语义才正确：

| 位置 | 现在 | 改为 |
| --- | --- | --- |
| `SpawnPlacementChecks.checkLadybugSpawn` | `x²+z² ≥ 0.25`（≈15 mph） | `windSpeedAt(pos) ≥ 8.94`（大风天） |
| `SpawnPlacementChecks.checkCalmDayCritterSpawn`（蝴蝶/臭虫） | `< 0.25` | `< 8.94`（非大风天） |
| `SpawnPlacementChecks.checkGlowBugEnvironment`（萤火虫） | `< 0.25` | `< 8.94` |
| `SpawnPlacementChecks.checkAngryDandelionSpawn` | `≥ 0.25` + 风向朝向玩家 | `≥ 8.94` + 用**该位置的风向**做点积（现在用的是全局向量，位置无关） |
| `SandstormGameEvent.strongWind` | `x²+z² ≥ 0.36`（≈18 mph） | `≥ 8.94`（与大风天同线），或事件已激活时直接为真 |
| `WindyBalloon` / `AngryTumbler` | 直接用全局向量 | 用各自位置的风向量（`WindField.windAt`）；这是 AI 读风，不是风推实体（§9.1） |

> ⚠️ 这是**行为改动**：线从旧单位的 0.5（≈15 mph）抬到 20 mph。
> 瓢虫/愤怒蒲公英/风气球会更稀有；但蝴蝶/臭虫/萤火虫的窗口反而变宽（`< 8.94` 而不是 `< 6.7`），因为它们现在只需"不是大风天"即可。
> 这条改动本身就是"按泰拉阈值"的直接结果，见 §17 D1。

### 9.3 新增大风天事件 `WindyDayGameEvent`

`Windy Day` 是**派生事件**（条件 → 状态），不是独立抽签，所以实现为 `GameEvent` 的"无状态派生"形态：

```java
public enum WindyDayGameEvent implements GameEvent {
    INSTANCE;
    public static final ResourceKey<WindyDayGameEvent> KEY = GameEvent.createKey(Confluence.asResource("windy_day"));
    private ServerLevel level;
    private boolean active;
    private int calmTicks;      // 低于阈值已持续多久

    @Override public boolean canStart() {
        return level.isDay() && WindState.get(level).instantSpeed(level.getGameTime()) >= WindTier.WINDY.min;
    }
    @Override public void onStart() {
        active = true; calmTicks = 0;
        WindState.get(level).setSpeedFloor(WindTier.WINDY.min + 0.5F);   // 粘滞：防止事件刚开就掉档抖动
        WindState.get(level).setRerollHold(3600);                        // 3 分钟内不重掷
    }
    @Override public boolean canEnd() {
        return !level.isDay() || (calmTicks > 600);                      // 夜晚 或 连续 10 秒低于阈值
    }
    @Override public void onEnd() { active = false; WindState.get(level).setSpeedFloor(0.0F); }
    @Override public void tick() {
        if (active) calmTicks = (WindState.get(level).instantSpeed(level.getGameTime()) < WindTier.WINDY.min) ? calmTicks + 1 : 0;
    }
    @Override public boolean isNonEnvEvent() { return false; }           // 环境事件：不阻断自然刷怪
    @Override public void encode(CompoundTag tag) { tag.putBoolean("Started", active); }
    @Override public void decode(CompoundTag tag) { active = tag.getBoolean("Started"); }
    ...
}
```

注册在 [`GameEventSystem`](../src/main/java/org/confluence/mod/common/gameevent/GameEventSystem.java) 的 events map，与 `SandstormGameEvent` 并列（是否置于 `Confluence.UNRELEASED_EVENTS` 门后见 §17 D4）。

**消费端**：图鉴 `FilterEntry.WINDY_DAY` 终于有了对应状态；懒惰成就 `achievements.confluence.a_rather_blustery_day`（"在大风天放风筝"）可用 `WeatherHandler.isWindyDay()` / `GameEventSystem.isEventStarted(WindyDayGameEvent.KEY)` 判定；粒子密度、云速加成也在事件期间额外 ×1.3。

### 9.4 沙尘暴 —— 锁定风向 + 实体受力

`SandstormGameEvent` 原有逻辑（沙漠生成、`checkSandstormSpawn`、`AngryTumbler`）保留，新增两件事。

**① 锁定风向（原作沙尘暴是恒速恒向风）**：

```java
@Override public void onStart() {
    active = true;
    WindState state = WindState.get(level);
    state.override(WindMath.SANDSTORM_FORCED_SPEED,      // 13.41 m/s（30 mph），配置项可改
                   state.angleCurrent(), remainingTicks);
    state.setDirectionBlend(0.0F);                       // 关闭温度噪声偏差 → 全场同向
}
@Override public void onEnd() {
    active = false;
    WindState.get(level).setDirectionBlend(1.0F);
    WindState.get(level).clearOverride();
}
```

`onEnd()` 恢复 `directionBlend = 1`、`clearOverride()`。`strongWind()` 改为 `active || instantSpeed() >= 8.94F`，免得命令强制启动后被自己的结束条件打断。

**② 实体受力（实现 `WindPushedEvent`）**：

```java
@Override public boolean inWindZone(ServerLevel level, BlockPos pos) {
    return level.getBiome(pos).is(Tags.Biomes.IS_DESERT)
            && pos.getY() >= OverworldUtils.getSurfaceY()
            && (level.canSeeSky(pos) || pos.getY() >= OverworldUtils.getSurfaceY() + 2);
}
@Override public float couplingFor(Entity entity) { return 0.05F; }
```

玩家：沙尘暴期间在客户端给**逆风阻力**（`push(-wind * 0.03)`）而不是顺风推力，手感上表现为"顶着风走变慢"，与原作一致。

### 9.5 新增暴风雪事件 `BlizzardGameEvent`

暴风雪在**本仓库自己的图鉴文本里**已经定义了触发条件："在下雨期间位于雪原生物群系中时，它会以暴雪的形式呈现，导致降雪量增加。"
即 **雪原 + 下雨**，**与风速无关**——这与原作的暴风雪一致（暴风雪是"雪原中的雨"，不是"风大的雪"）。

仓库里已经备好了资源但**没有任何 Java 逻辑**使用它们：
`ModSoundEvents.BLIZZARD_STRONG_LOOP`（暴风雪：强风暴循环）、`BLIZZARD_INSIDE_BUILDING_LOOP`（暴风雪：室内），
以及图鉴 `FilterEntry.BLIZZARD`。本设计把它们接起来。

```java
public enum BlizzardGameEvent implements GameEvent, WindPushedEvent {
    INSTANCE;
    public static final ResourceKey<BlizzardGameEvent> KEY = GameEvent.createKey(Confluence.asResource("blizzard"));

    @Override public boolean canStart() { return level.isRaining() && anyPlayerInSnowyBiome(level); }
    @Override public boolean canEnd()   { return !level.isRaining(); }
    @Override public boolean isNonEnvEvent() { return false; }

    // —— WindPushedEvent ——
    @Override public boolean inWindZone(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).is(BiomeTags.IS_SNOWY) && level.canSeeSky(pos);
    }
    @Override public float couplingFor(Entity entity) { return 0.04F; }   // 另在 pushWindOn 内加 -0.004 的雪压
}
```

**音效接入（已实现）**：`org.confluence.mod.client.sound.BlizzardAmbientSound` + `client.handler.BlizzardAmbienceHandler`。

* 判定与事件本体一致：雪原 + 下雨 + 事件已激活，**并且要求该玩家自己站在雪原里**——事件是全局的
  （只要有人进雪原就会开），沙漠里的玩家不该听见暴风雪。
* 露天播 `BLIZZARD_STRONG_LOOP`、室内（`!level.canSeeSky(player.blockPosition())`）播
  `BLIZZARD_INSIDE_BUILDING_LOOP`——这两个音效名本身就是为这个区分准备的。
* `SoundSource.WEATHER`，跟随玩家，进入/退出/室内外切换一律 **40 tick 淡入淡出**
  （与 `BiomeAmbientSoundsHandler.LoopSoundInstance` 同款做法），最大音量 0.65 以免盖过其他声音。
* 由配置 `blizzardAmbience` 总开关；退出世界时 `BlizzardAmbienceHandler.reset()` 立即停声。

**与沙尘暴的关系**：两者互斥（沙漠 vs 雪原），因此 `pushEventWind` 里 `return` 掉第一个命中的事件是安全的。若将来新增第三个风推事件，需要改成"叠加"策略，届时把 `return` 去掉即可。

---

## 10. 客户端渲染

### 10.1 雨雪倾斜（mixin `LevelRenderer`）

雨的四边形在 [`LevelRenderer.renderSnowAndRain`](../../../build/_nfsrc_219/net/minecraft/client/renderer/LevelRenderer.java) 中由 4 个 `BufferBuilder.addVertex(FFF)` 组成，跨度 `[j2, k2]`。对顶点做**以相机高度为基准的剪切**，可以让所有雨条获得**统一倾角**（因为剪切后的线段方向 `((y1-y0)·s, y1-y0)` 与位置无关）：

> **用 MixinExtras 的 `@WrapOperation`，不用 `@Redirect`。**
> 本仓库其余 ~50 处同类注入全部是 `@WrapOperation`，而 `@Redirect` 在同一目标上只能存在一个 ——
> 别的模组若也改动同一处调用，两个 `@Redirect` 会直接冲突崩溃；包裹则可以层层叠加。
> `@WrapOperation` 还是 late injection，与其它注入器的共存性更好。
> 未指定 `ordinal` 时会包裹**所有**匹配到的调用点（`WrapOperationInjectionInfo` 对每个 `InjectionNode` 逐个注入），
> 所以这里 8 个顶点全部会被剪切。

```java
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @WrapOperation(method = "renderSnowAndRain",
            at = @At(value = "INVOKE",
                     target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;addVertex(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer confluence$slantPrecipitation(BufferBuilder instance, float x, float y, float z,
                                                        Operation<VertexConsumer> original) {
        float[] slant = WeatherHandler.precipitationSlant();     // 方块/tick 的剪切系数，已按方向守恒夹取上限
        if (slant[0] == 0.0F && slant[1] == 0.0F) return original.call(instance, x, y, z);
        return original.call(instance, x - y * slant[0], y, z - y * slant[1]);
    }
}
```

* `RAIN_SLANT_GAIN = 1.35` → 大风天(8.94 m/s)倾角 ≈ 31°，沙尘暴恒速(13.41 m/s) ≈ 42°，封顶 `atan(0.9) ≈ 42°`。
  **雪用同一套系数**（未按 `Biome.Precipitation` 分支降增益）——雪本来就有自己的水平飘摆，观感可以接受。
* 该注入只作用于 `renderSnowAndRain` 内的 `addVertex(FFF)`，不会误伤其他渲染。
* 雷暴时的雨量本身仍由原版/模组其他地方控制，风只负责倾角。
* 注意 `BufferBuilder.addVertex(FFF)` 的**返回类型是 `VertexConsumer`**（不是 `BufferBuilder`），描述符必须按此写。

> ❗**两个已经踩过的坑**（第二版才修对）：
> 1. **传进 `addVertex` 的 x/y/z 已经是相机相对坐标**（原版写的是 `k1 - camX`、`k2 - camY`、`j1 - camZ`）。
>    一开始误以为 y 是世界坐标又减了一次 `camY`，偏移量因此变成 `-(camY)·s`（几十到几百格），
>    **雨被整体甩出视野 = "雨不渲染了"**。剪切零点只决定"在哪一层保持竖直"，
>    雨场本来就按方块平铺，用相机高度（即 y 的零点）正合适。
> 2. **符号**：下落雨滴满足 `x = 常数 - (vx/vy)·y`（y 越大越早、x 越小），
>    所以是**下端**顺风偏移，必须 `x - y·s`。写成 `x + y·s` 的话雨会逆风倾斜。

### 10.2 云层漂移（mixin `LevelRenderer#renderClouds`）—— X 与 Z 都要驱动

原版云只沿 **X** 漂：`d1 = (ticks + partialTick) * 0.03F; d2 = (camX + d1) / 12.0`，
而 Z 是 `d4 = camZ / 12.0 + 0.33` ——**没有时间项**。所以只替换 `ticks` 的话，刮南北风时云完全不动、
刮斜风时只走 X 分量。**云的运动方向必须与风向一致**，因此两个轴都要驱动。

**推导（把"特征的世界坐标"算出来，才能确定符号与速率）：**

```text
特征的世界坐标 = 12·(256u - floor(d) - frac(d)) + cam                 // 由 pose.scale(12) + translate(-frac) + UV=(local+floor)/256
              = 3072u - 12·d + cam
d2 = (camX + d1) / 12   →  特征 X = 3072u - d1                        // camX 项被抵消，所以云是"世界锚定"的
d4 = (camZ + driftZ) / 12 + 0.33  →  特征 Z = 3072u - driftZ - 3.96
```

于是：

* **X**：`dX/dt = -0.03 · d(ticks)/dt` → 想让云以 `vx` 顺风漂 ⇒ `d(ticks)/dt = -vx / 0.03`
* **Z**：`dZ/dt = -d(driftZ)/dt` → 想让云以 `vz` 顺风漂 ⇒ `d(driftZ)/dt = -vz`

两者都取 `v = 0.03 · factor`，其中 `factor = speed / CLOUD_REFERENCE_SPEED`（封顶 6）：

```java
// —— 每 tick，WeatherHandler.handle() 内推进（renderClouds 是每帧调用，放渲染里云速会随帧率变化）——
float factor = Mth.clamp(windSpeed() / CLOUD_REFERENCE_SPEED, 0.0F, MAX_CLOUD_FACTOR);
cloudTicks  = wrapFloat(cloudTicks  - factor * cos(angle), CLOUD_TICK_WRAP);            // 819200
cloudDriftZ = wrapFloat(cloudDriftZ - 0.03F * factor * sin(angle), CLOUD_DRIFT_WRAP);   // 24576

// —— mixin ——
@ModifyExpressionValue(method = "renderClouds",
        at = @At(value = "FIELD", target = "...LevelRenderer;ticks:I", ordinal = 1, opcode = Opcodes.GETFIELD))
private int confluence$windDrivenCloudTicks(int original) { return Mth.floor(WeatherHandler.cloudTicks()); }

@ModifyVariable(method = "renderClouds", at = @At("HEAD"), argsOnly = true, ordinal = 2)   // camZ
private double confluence$windCloudDriftZ(double original) {
    if (!WeatherHandler.cloudDriftApplies()) return original;
    return original + WeatherHandler.cloudDriftZ();
}
```

> ❗**基准风速不能取大风天阈值（8.94 m/s）**——这是第二版才修对的。
> 重掷分布下风速的**中位数只有约 1.6 m/s、均值约 3.6 m/s**（65% 的样本落在 0 ~ 4.47 m/s 的"偏静风"分支），
> 按 8.94 归一化会让**常见情况只有原版云速的 0.4 倍**。原版云速本身就慢（`0.03` 方块/tick = 0.6 方块/秒），
> 再打四折观感就是"云几乎不动"。
>
> `CLOUD_REFERENCE_SPEED = 2.0` 后的倍数：静风上界(1.34) ≈ 0.7×、中位(1.6) ≈ 0.8×、均值(3.6) ≈ 1.8×、
> 微风上界(4.47) ≈ 2.2×、**大风天(8.94) ≈ 4.5×**、强风及以上封顶 6×（0.18 方块/tick ≈ 3.6 方块/秒）。
> 整体嫌慢/嫌快只改这一个常量。

**为什么 Z 用「改参数 camZ」而不是改 `d4`：**
`camZ` 在该方法里只被用到两处——传给 `level.effects().renderClouds(...)`，以及算 `d4`。
改参数意味着偏移发生在原版 floor / frac / 网格缓存键（`prevCloudX/Z`）这条管线**之前**，
UV 原点、位移动画、网格重建时机三者天然自洽，**不会出现贴图跳变**。
反之，若只在 `buildClouds` 与 `PoseStack.translate` 两处注入偏移，缓存键仍按未偏移的 `floor(d4)` 重建，
每约 12 方块漂移就会有一次约 12 方块的纹理跳变。

**代价与护栏**：`camZ` 同一次调用也会传给 `effects().renderClouds(...)`，所以
`WeatherHandler.cloudDriftApplies()` **限定只在主世界注入**（其它维度可能有模组自己的云渲染，不该拿到被偏移的相机坐标）。
主世界里原版 `DimensionSpecialEffects.OVERWORLD` 的那个回调直接返回 false，不受影响。

* 精度：`Mth.floor(cloudTicks)` 带来 ≤0.03 方块的位置量化，肉眼不可见；帧间平滑由 `partialTick` 提供。
* 两个取模周期都对应原版自己的 2048 取模（`2048·12/0.03` 与 `2048·12`），所以取模是**无缝**的；
  同时把累加值限制在远小于 `2^24`，避免长时间运行后 float 精度丢失导致云停住。
* 配置 `windSlantsRainClouds` 关闭时：虚拟 tick 与真实 tick 同步（云回到原版方向与速度），Z 漂移不再注入 —— 即完全回退原版。

---

## 11. HUD / 气象台 / 风速计 / 其他

| 位置 | 现在 | 改为 |
| --- | --- | --- |
| [`InformationHandlerMixin`](../src/main/java/org/confluence/mod/mixin/integration/terracurio/InformationHandlerMixin.java) | `Component.translatable("info.confluence.weather_radio." + weather, WeatherHandler.windSpeedInfo)` | 参数扩为 `(风速, 风向)`：`windSpeedInfo` = `"8.9 m/s"`，新增 `windCompassInfo` = `"东南风"` |
> ❗**方向的两套说法（已踩过一次）**：
> - **轴对齐**：`dir = (cos θ, sin θ)` 映射到 `(x, z)`，与原版 `Direction` 一致 ——
>   `EAST = +X`、`WEST = -X`、`SOUTH = +Z`、`NORTH = -Z`（见 `Direction` 源码与 `toYRot`：yaw 0=南、90=西、180=北、270=东）。
> - **`/confluence wind set <x> <z>` 的 x/z 是"吹向"的分量**。
> - **气象台/`wind get` 显示的方位名是"来向"**（气象学惯例：东风 = 从东边吹来）。
>   `WindMath.compassKey` 因此取 `θ + 180°`（`COMPASS[(towards + 4) & 7]`）。
>   早期版本按"吹向"命名，于是"吹向东"被标成"东风"，是错的 —— 而且真实风向标的箭头也指向风来的方向，
>   按"来向"命名才能让风标与读数一致。
> - `wind set` 的**回显刻意不带方位名**，只给 x/z 与速度，避免两个方向词并排出现造成误读。
| 语言键 `info.confluence.weather_radio.*` | `"天气: 晴天, 风速: %s"` | `"天气: 晴天, 风速: %s %s"`（改 `ModEnglishProvider`/`ModChineseProvider` + `i18n/*.json`；`assets/confluence/lang/` 由 datagen 生成） |
| [`WeatherVaneBlock.BEntity`](../src/main/java/org/confluence/mod/common/block/functional/WeatherVaneBlock.java) | 用 `getWindSpeedX/Z` 归一化后算角 | 直接用 `WeatherHandler.windAngle()`；追赶与抖动**每 tick 都推进**，角度一律走最短角差（见下方坑） |
| [`TFPinWheelMixin`](../src/main/java/org/confluence/mod/mixin/integration/terrafurniture/TFPinWheelMixin.java) | `WIND_SPEED.length() * 0.09f` | 换 `WeatherHandler.getLegacyWindSpeedX/Z()` 模长（**零系数改动**） |
| [`LanternNightSprite`](../src/main/java/org/confluence/mod/client/gameevent/LanternNightSprite.java) | `variable.wind_x/wind_z` = 原单位 | 换 `getLegacyWindSpeedX/Z()`（**着色器零改动**，仍是 [-1,1] 量级） |
| `ConfluenceData.updateWind` | 每天 06:00 一次 | **删除**；改由 `WindState.tick` 每 tick 连续驱动 |
| `/confluence windSpeed get|set` | x/z 裸值（旧单位向量） | **删除**，不保留兼容子命令；改为 `/confluence wind get|set|stop|debug`，其中 `set` 用 **x/z 分量（m/s）** |

### 11.1 风速计为什么会抖（已修的三个坑）

旧版风标是"收到新包才动"的设计，在"风向每 5 tick 变一次"的新风下必然抽动。修好的版本有三条铁律：

1. **每 tick 必须跑完整套动画，不能因为"风向没变"就提前 `return`。**
   旧实现的 `if (lastWindAngle == windAngle) return;` 把追赶与抖动动画一起跳过了，
   于是风标只在收到新包的那几 tick 才动 —— 每秒跳 4 下的抽动。现在提前返回只用于静风。
2. **角度一律用最短角差（`WindMath.wrapRadians`）。**
   `target = HALF_PI - windAngle` 不做归一，风向扫过正西时 `windAngle` 会在 ±π 之间跳 2π，
   直接相减会让风标整整倒转一圈 —— 这也是"剧烈抖动"的来源。
   现在 `rotation` 与 `targetRotation` 都放在**连续域**（不归一），二者的差永远是最近角差，
   同时仍然兼容渲染端的 `Mth.rotLerp(partialTick, rotationO, rotation)`（它内部会归一差值）。
3. **抖动只在"真正转向"时触发一次，且正在抖时不重新触发。**
   旧实现每 tick 重设 `shakeTime = |Δtarget| * 10`，把摆动无限续期 → 持续振动。
   现在只有 `转向 > SHAKE_TRIGGER(≈3°)` 且 `shakeTime <= 0` 时才触发，最多 `MAX_SHAKE_TICKS(10)` tick。

静风（`windExposedSpeed() < WindTier.CALM.max()`）时风标停住且不晃；从静风起风时 `lastWindAngle` 为 NaN，
所以第一次不会误触发抖动。

### 11.2 覆盖与持久化的不对称（"显示 40 然后掉到 10"）

这是被实测反馈揪出来的一个**设计不对称**：

| | 是否持久化 |
| --- | --- |
| `forcedSpeed` / `forcedAngle` / `forceTicks`（强制风，会话内有效） | ❌ 不写入存档 |
| `speedCurrent`（被强制风顶上去的当前值） | ✅ 写入存档 |

所以 `/confluence wind set 40 0` 之后退出再进（或覆盖到期）：

```text
set 40 之后      forcedSpeed=40, speedCurrent→40        显示 40
退出 / 进世界    forcedSpeed 丢失（本就不持久化）
                 speedCurrent 仍是 40（存下来了）        显示 40   ← "为什么不合理的大风还在"
之后每秒         current 以 SPEED_RATE = 0.5 m/s 每秒跌向自然目标
                                                        40 → 20 → 10 → …
```

**两处修复：**

1. `clearOverride()` 里把 `speedCurrent` 夹回 `NATURAL_MAX_SPEED`（30 mph）。
   强制风期间 current 允许超过自然上限（那是覆盖的意义），但**解除的那一刻必须收回来**，
   否则就是一条长达一分多钟的衰减尾巴。
2. `decode()` **先 `clear()` 再读**。`WindState` 是单例，而 `decode` 在每次世界加载时都会被调用；
   早期版本在本存档没有 wind 数据时直接 `return`，于是上个存档留下的值和 `initialized` 标记
   会原样带进新世界（新建世界一进去就刮着旧存档的风）。
   同时读档时的夹取范围改用 `NATURAL_MAX_SPEED` 而不是 `MAX_FORCED_SPEED`——
   读档时不存在覆盖，当前值就必须落在自然范围内。

> 通用教训：**单例（IGlobalData）的 `decode` 必须是"自包含"的** —— 无条件先把状态清干净，
> 再按存档内容写入。不能假设 `clear()` 一定会被调用（它挂在 `ServerStoppedEvent` 上，
> 依赖事件时序），也不能在"没有数据"时什么都不做。

---

## 12. 存档、配置、命令

### 12.1 存档与迁移

**新格式**（`IGlobalData`，写在 `level.dat` 的 `confluence:wind` 下）：

> ❗ **`IGlobalData` 不需要、也**不**应该用 `setDirty()` 去"提醒"落盘。**
> `level.dat` 的重写是**无条件**的：`MinecraftServer.saveAllChunks`（每次自动保存 `ticksUntilAutosave` 归零、
> `/save-all`、关服时都会走）→ `LevelStorageSource.saveDataTag` → `WorldData.createTag` →
> `PrimaryLevelData.setTagData`，而 `IGlobalData.encode` 就注入在 `setTagData` 的尾部。
> dirty 门控只存在于 `DataStorage`/`SavedData` 那一侧（`confluence.dat` 那种）；
> 拿 `ConfluenceData.setDirty()` 去标记风是完全无效的——它标记的是**另一份文件**。
> （实现时这里一度留了这样一行残留代码，已删除。）

```java
nbt.putFloat("windSpeed",       speedCurrent);   // m/s
nbt.putFloat("windSpeedTarget", speedTarget);    // m/s
nbt.putFloat("windAngle",       angleCurrent);   // rad
nbt.putFloat("windAngleTarget", angleTarget);    // rad
nbt.putInt  ("windTimer",       timer);          // 距离下次重掷
nbt.putFloat("windGustPhase",   gustPhase);      // 阵风相位
```

**旧存档迁移**：旧版把风存在 `ConfluenceData`（`confluence.dat`）的 `windSpeedX`/`windSpeedZ`（旧单位 [-1,1] 向量）。
`level.dat` 的解析发生在 `ServerLevel` 构造之前，那时拿不到 `ConfluenceData`，所以迁移放在 `ConfluenceData` 的构造里：

```java
// ConfluenceData 构造
WindState.INSTANCE.migrateLegacy(nbt.getFloat("windSpeedX"), nbt.getFloat("windSpeedZ"));
```

```java
// WindState.migrateLegacy：只在全局数据尚未初始化时生效
float legacy = Mth.sqrt(legacyX * legacyX + legacyZ * legacyZ);
speedCurrent = speedTarget = legacy * WindMath.REF_SPEED;   // 旧 1.0 → 13.41 m/s
angleCurrent = angleTarget = (float) Mth.atan2(legacyZ, legacyX);
gustPhase    = Mth.frac(Mth.abs(legacyX) * 7.0F + Mth.abs(legacyZ) * 13.0F); // 旧存档无相位，派生一个稳定的
```

旧键**继续写回** `ConfluenceData`（`windSpeedX/Z` = `dir * speed / REF_SPEED`），这样单方面回滚旧版本也不会读出一堆 0。

### 12.2 配置

**分类原则：影响玩法的（服务端权威）进 `CommonConfigs`，纯视听的进 `ClientConfigs`。**

`CommonConfigs` → `Weather` 分类：

| 键 | 默认 | 说明 |
| --- | --- | --- |
| `doWind` | `true` | 风系统总开关（关闭后退化为"无风"，所有消费端静默） |
| `windDirectionNoise` | `true` | 是否启用温度噪声方向场（关闭 = 全局恒向，便于对比调试） |
| `windDirectionAmplitude` | `90.0`（度） | 局地风向相对全局风向的最大偏转，0 = 全局恒向 |
| `windWindyChance` | `0.04` | 每次重掷抽到"大风天档"的概率（已生成的 toml 会保留旧值 0.1，需手动改） |
| `windSandstormForcedSpeed` | `13.41` | 沙尘暴期间强制风速（m/s） |
| `sandstormPushesEntities` | `true` | 沙尘暴的实体受力开关（§9.4） |
| `blizzardPushesEntities` | `true` | 暴风雪的实体受力开关（§9.5） |

`ClientConfigs` → `Weather` 分类：

| 键 | 默认 | 说明 |
| --- | --- | --- |
| `windSlantsRainClouds` | `true` | 雨雪倾斜 + 云层速度（**纯视觉**） |
| `blizzardAmbience` | `true` | 暴风雪环境风声（**纯音效**，露天/室内两种，§9.5） |

> `windDirectionNoise` / `windDirectionAmplitude` 虽然"看起来像视觉"，但它们改的是**服务端的风向量场本身**，
> 会同时影响生成条件、愤怒蒲公英的朝向判定、以及沙尘暴/暴风雪的实体受力方向，所以属于 `CommonConfigs`。
>
> `showWindParticles`（既有项，0~100 比例）留在 `ClientConfigs` 根下**没有动**：
> 把它挪进 `Weather` 分类会改变配置路径，玩家已保存的值会被丢弃。

> ⚠️ 这两个键**换过文件**：早期版本误放在 `confluence-common.toml` 里，现已移到 `confluence-client.toml`。
> 若你在 common 里改过它们，那份值会被忽略（客户端 toml 生效，默认 `true`）。

> ❗**没有** `windAffectsEntities` / `windAffectsProjectiles` 这类配置——按新设定，风系统不存在任何实体受力路径，不需要开关。
> `windRerollMinTicks` / `MaxTicks` / `windSandstormMinSpeed` 也只写在文档里，实际是代码常量
> （`WindState.REROLL_MIN/MAX`、`WindMath.WINDY_DAY_MIN`），没有做成配置项。

### 12.3 语言与文档

* `ModEnglishProvider` / `ModChineseProvider`：`info.confluence.weather_radio.*` 加第二个 `%s`（风向）；新增 `info.confluence.wind.tier.*`（五个视觉档位名）与 `info.confluence.wind.compass.*`（八方位名）。
* `i18n/en_us.json`、`i18n/zh_cn.json` 同步（**这两个是手写源**）；`assets/confluence/lang/*.json` 由 datagen 重新生成；`ru_ru/pt_br/lzh/es_es/de_de` 无生成器，需要手工补或接受英文回退。
* `ageratum` 玩家文档：`detailed_information/mechanic/` 下新增 `wind.md`，`command_list.md` 补 `wind` 命令。
* `ConfluenceOtherworld/notes/CHANGELOG.md` 追加条目。

---

## 13. 迁移与兼容

| 项 | 处置 |
| --- | --- |
| `ConfluenceData.getWindSpeedX/Z()`、`setWindSpeed(x, z)`、`updateWind(level)` | **全部删除**，不再保留任何旧接口薄壳；位置相关的调用点已改为 `WindField`，写入改为 `WindState.override` / `overrideSpeed` |
| `WeatherHandler.WIND_SPEED` 字段 | 保留（内容语义从"归一化"变为"m/s"），所有美术系数改走 `getLegacyWindSpeedX/Z()` |
| `WeatherHandler.windDirection` | 不再为 `null`（`ClientLevelMixin` 的 `== null` 判断可以留着，永假） |
| `WindSpeedPacketS2C` 2 字段 → 3 字段 | 破坏性协议变更，需要**客户端与服务端同版本**。模组自身版本内同步，无外部兼容负担 |
| 旧存档 | 见 §12.1 迁移（只读旧键，不写只读不回；旧键仍写回是为了单方面回滚版本时能读出合理值） |
| `SandstormGameEvent.strongWind()` | 保留但改为 `active || 阈值`，避免事件刚被命令强制启动时被自己的结束条件打断 |

---

## 14. 性能预算

| 环节 | 频率 | 成本 | 备注 |
| --- | --- | --- | --- |
| `WindState.tick` | 1/tick | ~20 次浮点运算 | 可忽略 |
| `WindField` 晶格温度 | 首次访问 1 次/晶格点 | `DensityFunction.compute`（数十~数百 ns） | 缓存后 0；新探索区域按 64 格分摊 |
| `WindField.windAt`（`>` 生成条件） | 仅生成尝试时 | 4 次查表 + 归一化 | 频次极低 |
| `EntityTickEvent.Post` 早退 | 每实体每 tick | 稀有事件循环遍历（未激活时零分支命中） | 平时**没有**任何实体受力开销，见 §15 风险 1 |
| `WindField.windAt`（事件内） | **仅沙尘暴/暴风雪激活时** × 受力范围内实体 | 4 次查表 + 归一化 | 事件外为 0；事件内也只在 `!onGround()` 且范围内才求值 |
| 客户端粒子 | 每 tick（`doAnimateTick` 逐方块位置） | 加了每 tick 预算上限 | §8.4 |
| 网络 | ≈0.2~1 包/s/玩家 | 3 float | §7.1 |
| 雨的 `@WrapOperation` | 每帧 × 雨条数（≤ (2·10+1)² × 8 顶点） | 1 次乘加/顶点 | 可忽略 |

---

## 15. 风险与备选

1. **`EntityTickEvent.Post` 的早退成本**：事件对**每个**已加载实体每 tick 触发。第一道门是"有没有风推事件在跑"，平时零命中，但循环仍会走 `getAllEventInstances()`。优化：在 `GameEventSystem` 里维护一个 `boolean anyWindPushedEvent`（事件 `onStart`/`onEnd` 时维护），让平时退化成一次布尔判断。这是**唯一**需要留意的性能点，因为新设定把风从"每实体结算"改成了"只有事件期间结算"。
2. **云只沿 ±X 漂移**（方案 A 的固有限制）：见 §10.2；若不可接受再上方案 B。
3. **远处风速计的风向不准**：客户端只知道玩家所在处的风向，>64 格外的风车会用玩家处的风。备选（按需再做）：在玩家附近区块加载时，用 `ClientboundBlockEntityDataPacket` 式的定向同步给风车方块实体推送它所在晶格的风向。
4. **温度噪声在超平坦/自定义维度下可能退化**：`custom` 维度或无 `overworld` 噪声路由的世界，`sampler.temperature()` 可能恒为常数 → `gradient = 0` → 公式自动退化为"全局恒向"（因为 `cross(0, u) = 0`）。这是**天然的安全退化**，不需要额外特判。
5. **mixin 目标脆弱性**：`renderClouds` 的 `ordinal = 1` 依赖 `this.ticks` 在该方法内恰好出现两次；`renderSnowAndRain` 的 `addVertex(FFF)` 描述符在 MC 版本升级时可能变（如改成 `addVertex(PoseStack, FFF)`）。两者都需要在**升级 MC 版本时重新核对**，建议在 mixin 里加 `require = 1` 让它早期失败而不是静默失效。
6. **事件受力的手感风险**：沙尘暴/暴风雪对实体的推力是**新增的可玩性影响**（且在事件内是持续性的）。系数已刻意取小（0.04~0.05），并给了 `sandstormPushesEntities` / `blizzardPushesEntities` 两个开关可一键关掉，视觉部分（粒子/雨/云）不受影响。
7. **暴风雪是新增事件，可能与已有的雨/雷暴逻辑重叠**：`canStart()` 依赖 `level.isRaining()`，如果模组其他地方也按"雨 + 雪原"做判定，需要确认不会双重表现。已有资源（两个 `BLIZZARD_*_LOOP` 音效 + `FilterEntry.BLIZZARD`）说明原计划就有这个事件，风险低。

---

## 16. 分阶段实施计划

| 阶段 | 内容 | 验收标准 |
| --- | --- | --- |
| **P1** 状态与协议 | `WindState` + `WindMath` + 包改造 + `WeatherHandler` 重写（**只换单位与平滑，消费端语义不变**） | 旧视觉表现基本不变；`/confluence wind debug` 能打出 m/s/角度；收包不再跳变 |
| **P2** 方向场 | `WindField`（温度噪声 + 晶格缓存 + 双线性 + `cross` 偏差） | 不同区块的风车指向不同；把玩家传送到相隔 1000 格的两点，风向明显不同且与温度分布相关 |
| **P3** 粒子 | 修负风向 bug、连续方向、密度随风速、每 tick 预算、群系分流注册 | 纯 -X 风能出粒子；雪原不再飘沙；高密度下帧率不掉 |
| **P4** 事件：大风天 | `WindyDayGameEvent` + 生成条件阈值统一到 20 mph + 图鉴/成就接线 | `/confluence event start windy_day` 可强制；瓢虫仅在 ≥20 mph 生成；**确认此时任何实体都不受风影响** |
| **P5** 事件：沙尘暴 / 暴风雪 | `WindPushedEvent` + 沙尘暴锁定风向与实体受力 + 新增 `BlizzardGameEvent`（含两个既有音效接入） | 平时实体完全不受风；沙尘暴内可见实体被推、玩家顶风变慢；暴风雪内雪原实体被推 + 环境音 |
| **P6** 渲染 | 雨倾斜 `@WrapOperation`、云速方案 A | 大风天雨明显斜、云加速；静风云近乎静止 |
| **P7** 收尾 | 配置项、命令、语言（含 5 种非生成语言）、ageratum 文档、CHANGELOG、旧存档迁移自测 | 旧存档读入后风速/风向合理；配置全生效 |

P1 是**唯一**的破坏性一步（协议 + 单位），其后每阶段都可独立回滚。

---

## 17. 需要你拍板的决策点

| # | 问题 | 我的建议 |
| --- | --- | --- |
| **D1** | 阈值统一到 **20 mph (8.94 m/s)** 后，瓢虫/愤怒蒲公英/风气球更稀有（现在 ≈15 mph），但蝴蝶/臭虫/萤火虫的窗口从 `< 6.7` 放宽到 `< 8.94`。接受吗？ | 接受，一条线两侧分工，对齐图鉴文本 |
| **D2** | ~~大风天频率~~ | **已定**：实测反馈"哪有这么频繁的大风"，已降到 4% + 2 游戏日冷却 ⇒ 约每 7~8 游戏日一次（§4.3） |
| **D3** | 气象台显示格式：`8.9 m/s · 东南风`（纯 m/s）还是 `8.9 m/s (20 mph) · 东南风`？ | 纯 m/s（按你的要求），mph 只做注释 |
| **D4** | `WindyDayGameEvent` 是否和 `SandstormGameEvent` 一样放在 `Confluence.UNRELEASED_EVENTS` 门后（未发布功能）？`BlizzardGameEvent` 是新增事件，是否需要公告/状态栏文本？ | 先放门后；暴风雪只做环境表现 + 音效，不加公告 |
| **D5** | 沙尘暴期间玩家是**只加逆风阻力**（顶风变慢，原作感），还是**同向推力 + 逆风阻力**都加？ | 只加逆风阻力（0.03），更贴近"沙尘暴里举步维艰" |
| **D6** | 暴风雪的实体受力范围：只推**露天**实体（`canSeeSky`），还是雪原内全部实体？ | 只推露天的，屋顶下/洞穴内不受影响 |
| **D7** | 沙尘暴/暴风雪的推力是否也作用于**掉落物与箭**（会有可见的"东西被吹跑"），还是排除这两类以免影响掉落物拾取与命中手感？ | 一视同仁（都用 0.05/0.04），因为只在事件内、且系数很小 |

---

## 附：参数总表（便于评审时一次性调参）

| 常量 | 默认 | 位置 |
| --- | --- | --- |
| `BLOCKS_PER_TICK` | `0.05` | `WindMath` |
| `REF_SPEED` | `13.41` m/s | `WindMath` |
| `SPEED_RATE` | `0.025` m/s/tick | `WindState` |
| `ANGLE_RATE` | `0.20` °/tick | `WindState` |
| `reroll` 间隔 | `2400 + rnd(9600)` tick（2~10 分钟） | `WindState` |
| `WINDY_CHANCE` | `0.04`（约每 7~8 游戏日一次大风天） | `WindState` |
| `WINDY_COOLDOWN` | `48000` tick（2 个游戏日） | `WindState` |
| `GUST_AMP` | `0.25` | `WindMath` |
| `WINDY_THRESHOLD`（唯一 gameplay 阈值） | `8.94` m/s（20 mph） | `WindTier` |
| `SANDSTORM_FORCED_SPEED` | `13.41` m/s（30 mph） | `WindMath` |
| `WIND_DIR_AMP` | `90°` | `WindField` |
| `CELL` | `64` 方块 | `WindField` |
| 晶格缓存上限 | `2048` | `WindField` |
| `SAMPLING_Y` | `64`（仅占位，温度与 y 无关） | `WindField` |
| `EXPOSURE_DEPTH` | `24` 方块（只作用于风粒子） | `WindMath` |
| `MIN_APPLY_SPEED` | `2.0` m/s | `WindField` |
| `PARTICLE_COUPLING` | `0.25` | `WeatherHandler` |
| `MAX_WIND_PARTICLES_PER_TICK` | `12` | `ClientConfigs` |
| `RAIN_SLANT_GAIN` | `1.35` | `LevelRendererMixin` |
| `SNOW_SLANT_GAIN` | `1.35 × 0.5` | `LevelRendererMixin` |
| `CLOUD_GAIN` | `1.5` | `LevelRendererMixin` |
| 沙尘暴 coupling：非玩家实体 / 玩家逆风阻力 | `0.05` / `0.03` | `SandstormGameEvent` |
| 暴风雪 coupling：非玩家实体 / 玩家 | `0.04` / `0.015` | `BlizzardGameEvent` |

> 风系统**没有**"平时作用于实体"的系数——上表最后两行只在这两个事件激活时生效。
