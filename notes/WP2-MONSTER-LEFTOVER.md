# WP2 剩余物种（狼人 / 大风气球怪）：落地记录

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（`build_errors.py --maxerrs 2000`）。
> 本批在「史莱姆族」与「史莱姆尾巴」之后，继续清 WP2 菜单里**不依赖 WP3 未落地成员**的剩余物种。

## 一、本批内容（3 新 java + 6 改 + 6 资源）

| 文件 | 来源（1.20） | 说明 |
|---|---|---|
| `common/entity/monster/Werewolf` | 同名，**逐字一致** | 唯一差异是 `finalizeSpawn` 4 参（见下） |
| `common/entity/monster/WindyBalloon` | 同名 | 同上；`BTNode` 里读 `ConfluenceData` 风向 |
| `client/entity/model/VanillaHumanoidGeoModel` | 同名 | 原版人形骨架 → Geo 骨骼旋转复制，狼人渲染器依赖 |
| `common/init/entity/MonsterEntities` | `:661` `WEREWOLF`、`:683` `WINDY_BALLOON` | 两条都是 `developmentOnly`，属性逐字一致 |
| `client/event/ModClientEvents` | `:833`、`:856` | 两条 `GeoNormalRenderer` 注册，实参逐字一致 |
| `common/entity/SpawnPlacementChecks` | `:789` / `:314` / `:463` / `:677` | 新增「地表敌怪基础规则」四支谓词 |
| `common/init/entity/CreatureSpawnPlacements` | `:112-113` / `:179` | 新增「前困难模式怪物」「困难模式怪物」两组的**增量**登记 |
| `common/CommonConfigs` | `:63` | 补 `SPAWN_WITHOUT_LIGHT`（`spawnWithoutLight`，**成员级盲区**） |
| `common/event/ModEvents` | — | 仅注释：放置规则注册中心已从「小动物半」扩到含怪物增量 |

资源 6：`geo/entity/{werewolf,windy_balloon}.geo.json`、`animations/entity/{werewolf,windy_balloon}.animation.json`、
`textures/entity/{werewolf,windy_balloon}.png` —— 与 1.20 逐字节相同（`servant_of_cthulhu` 等已有资源已核对同哈希）。

### 为什么必须同批带上放置规则

1.20 里 `MonsterEntities.WEREWOLF` / `WINDY_BALLOON` 与 `CreatureSpawnPlacements` 的两条 `group(...)`
是**一次提交里的两半**。1.21 的 `CreatureSpawnPlacements` 此前只落了 `registerCritters`（详见
`notes/WP5C-SUBSET.md` 第三节：整篇搬被 159 文件闭包挡住），所以这两个物种光注册 `EntityType`
只能靠 `/summon` 或刷怪蛋出现 —— 本批按「物种批次补那一半」的原计划补上：

| 物种 | 放置类型 | 谓词 | 1.20 出处 |
|---|---|---|---|
| `WINDY_BALLOON` | `ON_GROUND` | `checkAngryDandelionSpawn`（晴朗有风白天 + 草地 + 顺风朝玩家） | `CreatureSpawnPlacements:112-113` |
| `WEREWOLF` | `ON_GROUND` + `hardmode(...)` | `checkWerewolfSpawn`（困难模式 + 满月地表夜晚） | `CreatureSpawnPlacements:179` |

`checkAngryDandelionSpawn` 需要 `checkGroundSpawn` → `checkMonsterSpawnRules` → `CommonConfigs.SPAWN_WITHOUT_LIGHT`，
四支谓词与那个配置成员因此一并落地（方法体与 1.20 逐字一致，仅行内注明适配点）。

## 二、1.20 → 1.21.1 的 API 差异（均已写进代码注释）

| 1.20 | 1.21.1 | 处理 |
|---|---|---|
| `Mob#finalizeSpawn(level, difficulty, reason, group, tag)` 5 参 | 4 参（去掉 `@Nullable CompoundTag`） | `WindyBalloon` 覆写与两处调用点同删该实参 |
| geckolib `core.animatable.model.CoreGeoBone` | `cache.object.GeoBone` | `VanillaHumanoidGeoModel` 导入与循环变量 |
| geckolib `core.animation.AnimationState` | `animation.AnimationState` | 同上 |
| geckolib `util.RenderUtils` | `util.RenderUtil` | 同上；`matchModelPartRot(ModelPart, GeoBone)` 语义不变（对照 `TerraCurio/.../LayeredGeoRenderer:239`） |

## 三、验证与结果

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（4 → 0；前 4 处为 `GeoBone`/`RenderUtil` 导入与 `GeoNormalRenderer` 导入） |
| `check_duplicates.py`（3 个新文件） | 0 处「疑似移动/重复」 |
| `Werewolf` / `Werewolf` 属性、尺寸 | 与 1.20 逐字一致（182/18/37/12/32/1/0.64/0.3/7、0.9×2.2） |
| 渲染器可见性 | 两个物种都拿到 `GeoNormalRenderer`（**不留「注册了但没渲染器」的隐形实体**） |
| 待游戏内验收 | 狼人满月夜晚生成 + 原版人形骨架动作（走路/挥臂/看向玩家）；大风气球怪顺风漂移、贴地或玩家接近时爆掉并抛下史莱姆乘骑 |
| 渲染器实例为 1.20 实参 | `WINDY_BALLOON` `(c, id, false, 1.0F, 0.0F)`；`WEREWOLF` `(c, armor(...), false, 1.0F, -0.015625F)` |

## 四、同批排查后**延后**的项（附实测诊断，供后续批次直接接手）

| 项 | 1.20 出处 | 延后原因（实测） |
|---|---|---|
| `DungeonSpirit` | `MonsterEntities:563` | 类与渲染器都依赖 `BossEntities.PLANTERA`（WP3 未落地）→ 编译期缺符号；本批先把渲染器依赖清干净后撤出（不要留半截注册） |
| `MonsterAttributeScaling` | `common/entity/attribute/MonsterAttributeScaling` | 28 处错误：PortLib 的 `AttributeModifier` UUID 构造 + **`ForgeConfigSpec` → `ModConfigSpec`** 整层；顺带能补齐 `EntityEvents.joinLevel` 的前半段 → 值得单独一批 |
| `TheHungry` / `HillHungry` / `HungryMovementAction` / `VisualNeuron` | `MonsterEntities` + `entity/ai` | 与 `WallOfFlesh*` / `BrainOfCthulhu` / `BrainFake` / `HorrifiedEffect` 构成 **15 文件并集**，应按「克苏鲁之脑 + 血肉墙」两个 Boss 批次走，而不是塞进 WP2 小批 |
| `ANGRY_DANDELION` | `MonsterEntities:685` | ✅ **已在紧随其后的一批落地**（见第六节）：类与弹幕注册本来就在，只差注册 + 渲染器 + 6 个资源 |
| `CLUMSY_BALLOON_SLIME` 专用模型 | `MonsterEntities`（1.20 亦 todo） | 仍用 `MissingModelRenderer` 占位（前批已记） |

## 五、顺带发现：**引用得到、文件不存在**的资源清单

写本批资源时顺手做了一次全仓审计（`Confluence.asResource("…")` 的 **910 处**引用 × 磁盘存在性），
**37 处引用的资源在 1.21 磁盘上不存在**。狼人那 2 处已随本批修掉；剩下 35 处按「能不能从 1.20 直接拷」
分成三类（判据都跑了双检：**1.20 磁盘存在性** + **1.20 源码是否引用同一路径** + `git log --all` 路径史）：

**A. 可直接从 1.20 拷入（17 处，1.20 HEAD 磁盘上都有）** —— 下一批「纯资源补漏」的主体：

| 类别 | 文件 | 1.21 引用点 |
|---|---|---|
| 恶魔眼 | `geo/entity/demon_eye.geo.json`、`animations/entity/demon_eye.animation.json` | `DemonEye:252/271` → **恶魔眼当前没有模型/动画** |
| 连枷 | `geo/item/flail/handle.geo.json`、`textures/entity/flail/{anchor,chain_knife,drippler_crippler,flairon,flower_power}.png` | `BaseFlailItemRenderer:27`、`FlailComponent:256-392` |
| 小动物 | `textures/entity/animal/{bird,blue_jay,cardinal}.png`、`animal/butterfly/{hell_butterfly,prismatic_lacewing}.png`、`animal/fairy/faeling.png` | `Bird:112`、`BlueJay:18`、`Cardinal:18`、`HellButterfly:39`、`PrismaticLacewing:32`、`Fealing:43` |
| 界面/特效 | `textures/gui/noise.png`、`textures/trail.png`、`textures/damage_font.png` | `RenderStateShardAccessor:22`、`LyraRenderTypes:42`、`Info:18` |

**B. 1.20 自己也是坏的（6 处，上游既有缺陷）** —— 1.20 源码引用同一路径，但 1.20 检出的磁盘上同样没有文件，
且 `git log --all` 显示该路径**从未存在过**。1.20→1.21 逐字照搬即「同样缺」，不是本次移植引入；
按「向 1.20 对齐」的原则**保持与 1.20 一致并在本表留档**，不擅自造资源：
`textures/atlas/entity_blood.png`、`textures/environment/sunglasses_boulder.png`、
`textures/gui/information/mechanical_lens.png`、`textures/slot/accessory.png`、
`textures/entity/ghastly_projectile.png`、`textures/entity/mushroom_projectile.png`。

**C. 1.21 独有路径（12 处，1.20 任何提交都没有）** —— 引用它们的是 1.21 侧的**新实现**
（`FlailComponent` 自述「与 1.21 DataComponent 解耦，纯 POJO，兼容 1.20.1 移植」，而 1.20 HEAD 的
`FlailComponent` 只用 `textures/entity/flail/*`、不用 `geo/entity/flail/*`）：8 个模型
（`geo/entity/flail/{anchor,chain_guillotines,chain_knife,drippler_crippler,flairon,flower_power,golem_fist,ko_cannon}.geo.json`）
+ 3 张贴图（`textures/entity/flail/{chain_guillotines,golem_fist,ko_cannon}.png`）
+ `geo/item/phaseblade_bar.geo.json`（该路径在 1.20 历史里出现过，HEAD 已无，且 1.20 代码不引用）。
→ **不在「向 1.20 对齐」的范围内**，需单独确认这批 1.21 新连枷/光剑内容的资源出处，不要当成移植欠账。

→ 结论：A 类 17 处已随**纯资源批**收口（`notes/ASSET-GAP-A.md`，审计缺口 37 → 18），B 类留档、
C 类单独立项。

## 六、紧接的下一批：愤怒蒲公英（`ANGRY_DANDELION`）落地

第五节审计做完后立刻把「大风天」段收尾。1.20 出处：`MonsterEntities:685`（注册）、
`ModClientEvents:826`（生物渲染器）、`ModClientEvents:465`（**弹幕**渲染器）、
`CreatureSpawnPlacements:112-113`（与 `WINDY_BALLOON` 同组，共用 `checkAngryDandelionSpawn`）。

| 改动 | 内容 |
|---|---|
| `MonsterEntities` | +`ANGRY_DANDELION` 注册（属性逐字：26/0/8/穿甲 3/击退抗性 1.0/移速 0.0/跟随 32；`.projectile(ModEntities.DANDELION_SEED, damage(4,8,11).scaleWithAttack(8))`）+ `AngryDandelion` import |
| `ModClientEvents` | +`MonsterEntities.ANGRY_DANDELION` 的 `GeoNormalRenderer`；+**`DANDELION_SEED` 弹幕渲染器**（`ExplicitGeoModel` 三件套）—— 1.21 此前这个弹幕**完全没有渲染器** |
| `CreatureSpawnPlacements` | `checkAngryDandelionSpawn` 那一组补上 `ANGRY_DANDELION`（与 1.20 `:112-113` 的两个成员一致） |
| 资源 6 | `entity/angry_dandelion.{geo.json,animation.json,png}` + `entity/proj/angry_dandelion.{geo.json,animation.json,png}`，与 1.20 同哈希 |
| 顺带 | 修掉上一批留下的两行过期注释（`ModClientEvents` 里描述已撤出的 `DungeonSpirit`、`MonsterEntities` 的「留待下一批」） |

**1.20 行号勘误**：第五节与 `notes/ASSET-GAP-A.md` 里曾把弹幕渲染器写成 `ModClientEvents:464`、
生物渲染器写成 `:825`；核对原文后实际是 **`:465`** 与 **`:826`**（464 是 `SPIDER_WEB_SPIT`）。

验证：编译门 **0 错误 / 0 文件**；`angry_dandelion` / `dandelion_seed` 两个 id 全仓唯一；
6 个资源与 1.20 同哈希。

