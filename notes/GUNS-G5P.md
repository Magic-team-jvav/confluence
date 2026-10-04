# 枪械内联 G5′（客户端层：触发链 + 渲染 + 特效）：落地记录

> **枪械内联迁移**（`notes/GUNS-INLINE-MIGRATION.md`）的第 5 批 `G5′`。
> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**（`build_errors.py --maxerrs 2000`）。
> 本批**未改动任何子模块**（Lib / TC 保持 WP6c 的 0/0，gitlink 不动）。
> 规模：**20 个新 java + 12 个既有 java 改动 + 78 个新资源 + 7 个资源覆盖**。
>
> **本批关闭了 G4′ 留下的回归窗口**：G4′ 之后枪「有物品、有事件、有服务端管线、有网络包，
> 但 `ShootPacketC2S` 没有发送方」—— `GunHandler` 就是那个发送方，本批落地并接进 `GameClientEvents`。
> 同时**把枪械家族全部资源从 TerraGuns 命名空间搬回主模组**（§1.3），否则新渲染器按
> `confluence:gun/<id>` 解析会直接导致 10 把枪无模型。

## 一、闭包与落点

```powershell
python tools/port2native/stage_batch.py --name g5p-client --convert --apply
# {"candidates": 19, "kept": 19, "new": 19, "removed": 0}
# uncovered.json = []（闭包内无 1.20 独有但未 staged 的类）
python tools/port2native/check_duplicates.py --src120 <1.20 src/main/java> --ref .
# 19 文件：7 DIFF + 1 NEAR(SilverImpactVfx)，对手全是 TerraGuns 的旧实现
#   → 既定「先加后删」（TerraGuns 在 G6′ 整模块退役），不是新重复
python tools/port2native/check_duplicates.py --file org/confluence/mod/client/particle/LuminiteImpactParticle.java
# [SELF]（1.21 侧此前不存在该类，纯新增）
```

### 1.1 新增（20 java，全部在 `org/confluence/mod/client`）

| 文件 | 行数 | 来源（1.20） |
|---|---:|---|
| `client/handler/GunHandler` | 65 | 同名文件逐字（唯一改动见 §3.1） |
| `client/renderer/entity/bullet/{BulletRenderer,BulletRenderTypes,BulletTrailStyle,BulletTrailStyles,BulletVfxManager,TrailPathSmoother}` | — | 同名文件逐字 |
| `client/renderer/entity/bullet/effect/{ActiveBulletVfx,BulletImpactVfx,BulletVfxRenderUtil,ChlorophyteImpactVfx,CrystalImpactVfx,LuminiteImpactVfx,PartyConfettiEffect,PartyConfettiVfx,SilverCrossEffect,SilverImpactVfx}` | — | 同名文件逐字 |
| `client/renderer/item/GunRenderer` | 389 | 同名文件（1.20 的 541+235 两文件版经转换器收敛；API 差异见 §3） |
| `client/renderer/item/TaczFirstPersonTransform` | 43 | 同名文件逐字 |
| `client/particle/LuminiteImpactParticle` | 89 | 同名文件（整类盲区，见 §4.1；顶点 API 改 1.21 形式） |

`StarCannonBulletRenderer` 是 `[SELF]` 冲突项：1.21 侧已存在（41 行 `ThrownItemRenderer<StarCannonBulletEntity>`），
**未覆盖**，`ModClientEvents:458` 的既有注册保持不动。

### 1.2 资源：`luminite_impact` 粒子四件套（6 文件）

| 文件 | 来源（1.20） |
|---|---|
| `resources/assets/confluence/particles/luminite_impact.json` | 同名资源逐字（5 帧序列） |
| `resources/assets/confluence/textures/particle/luminite_impact_ominous_{0..4}.png` | 5 图逐字 |

### 1.3 枪械家族资源：TerraGuns 命名空间 → 主模组（78 新 + 7 覆盖）

**为什么必须在 G5′ 做**：`registerGunModel` 用 `Confluence.asResource("gun/" + itemId)`，即渲染器只认
`confluence:geo/item/gun/<id>` 一套路径。G5′ 之前 1.21 主模组只有 3 把枪（`bee_gun`/`space_gun`/`star_cannon`）
的资源，另外 10 把的 geo/动画/贴图/物品模型全在 `assets/terra_guns/` 下 —— 新渲染器一落地，
这 10 把枪会**没有任何模型**（编译门完全看不见）。

| 路径 | 新增 | 覆盖 | 说明 |
|---|---:|---:|---|
| `geo/item/gun/<id>.geo.json` | 11 | 0 | 与 1.20 逐字节相同；与 TerraGuns 版本也逐字节相同（`hand_gun` 除外） |
| `animations/item/gun/<id>.animation.json` | 11 | 0 | 同上 |
| `textures/item/gun/*.png` | 21 | 0 | 枪体贴图 + `_glowmask` |
| `models/item/<id>.json` | 6 | **7** | 见下方说明 |
| `textures/item/*.png`（子弹 + `endless_musket_pouch`） | 16 + 1 mcmeta | 0 | 与 TerraGuns 版本逐字节相同 |
| `textures/vfx/{heads,particles,trails}/*.png` | 6 | 0 | 子弹命中 VFX 贴图 |

* `models/item/<id>.json` 的 **7 个覆盖**：`{blowgun,boomstick,minishark,musket,shotgun}` 是 1.21 侧
  遗留的**旧版**（32×32 + `gui_light: front`），`{bee_gun,space_gun}` 是 1.21 侧与 1.20 不同的调参版；
  全部按 1.20 覆盖（1.20 是事实来源，且新装上的 `GunRenderer` 正是 1.20 派生版，走 geo 定位骨骼
  `idle_view`/`thirdperson_hand`/`ground`/`fixed`，与 1.20 的 display 数据配套）。
  `star_cannon.json` 两侧逐字节相同，未动。
* **`hand_gun.geo.json` 的关键差异**：1.20 版有 `idle_view` 定位骨骼，TerraGuns 版没有 ——
  `TaczFirstPersonTransform.applyIdleViewInverse` 正是吃这个骨骼，所以**必须用 1.20 版**（已按 1.20 落盘）。
* 终检：13 把枪的 `models/geo/animations/贴图` 与 1.20 **逐字节全等（差异 0）**；
  TerraGuns 的 `assets/terra_guns/**` 中「1.20 有对应物」的文件已 **0 处残留在 TG 独有**。
* 顺带发现（**不改**，仅记录）：1.21 的音效 ogg 放在 `assets/confluence/sounds/*.ogg`（扁平），
  1.20 放在 `sounds/item/*.ogg`，`sounds.json` 的 16 条枪械事件键与顺序两侧完全一致 →
  行为等价，属 1.21 侧历史路径约定，不值得为对齐而挪文件。

### 1.4 既有 java 文件改动（12）

| 类别 | 文件 | 要点（1.20 依据） |
|---|---|---|
| **键位** | `client/ModKeyBindings` | 补 `GUN_SHOOT`/`GUN_AIM`/`GUN_INSPECT`（1.20 `:31/32/33`，键值/`KeyConflictContext.IN_GAME` 完全一致；G2′~G4′ 只搬了枪本体，键位表整块漏了） |
| **lang** | `ModChineseProvider` / `ModEnglishProvider` | +3 键（1.20 `:746/747/6371`、`:877/878/2569`） |
| | `lang/{es_es,lzh,pt_br}.json` | +`aim`/`shoot`（1.20 这三份也只有这两个；`inspect` 在 1.20 同样缺，故**故意不加**以保持 1.20 一致） |
| **事件** | `client/event/GameClientEvents` | `clientTick$Post` 加 `attackHeld`（1.20 `:170`）+ `GunHandler.handle`（1.20 `:192`）、`LoggingOut` 加 `GunHandler.reset`（1.20 `:211`）、`renderLevelStage` 加 `BulletVfxManager.render`（1.20 `:349`）、新增 `bullet$ImpactEffect` 订阅（1.20 `:543`）、`input$InteractionKeyMappingTriggered` 补**持枪取消原版攻击/挥手**（1.20 `:260-263`） |
| | `client/event/ModClientEvents` | `TGUtil.registerOtherGunModel`（TerraGuns）→ 本地 `registerGunModel`（1.20 `:997-1005`）改用 `GunItems.GUN_ITEMS`；补 `BASE_BULLET_ENTITY`/`GRAVITY_BULLET_ENTITY` 渲染器（1.20 `:559/560`，1.21 此前**未注册**）；补 `luminite_impact` 粒子注册（1.20 `:1014`） |
| | `client/event/ModClientSetups` | 新增 `TRAIL_RENDER_TYPE`（1.20 `client/effect/RenderStateShardAccessor:23`，整类缺失见 §4.2） |
| | `client/renderer/entity/projectile/RainbowBoulderRenderer` | `TGRenderTypes.coloredTrail()` → `ModClientSetups.TRAIL_RENDER_TYPE`（1.20 用的就是这条 lightning 底的渲染类型，不是 TerraGuns 的 `coloredTrail`，属**语义纠正**） |
| **渲染器** | `client/renderer/item/SimpleGeoItemRenderer` | 46 → 172 行：**合并为 `GeoRenderProvider` + `IClientItemExtensions` 双接口**（见 §5.1） |
| **注册** | `common/init/ModParticleTypes` | 补 `LUMINITE_IMPACT`（1.20 `:21`，整类成员盲区见 §4.1） |

## 二、接线关系（为什么这四点缺一不可）

| 1.20 调用点 | 1.21 落点 | 作用 |
|---|---|---|
| `GameClientEvents:192 GunHandler.handle(player, attackHeld)` | `clientTick$Post` 末尾 | 每客户端 tick 读 `GUN_SHOOT`/原版攻击键 → 发 `ShootPacketC2S`/`InspectPacketC2S`、播枪声、上冷却、发 `GunEvent.Use` |
| `GameClientEvents:260-263` 持枪分支 | `input$InteractionKeyMappingTriggered` | 持枪时 `setSwingHand(false)` + 取消 `isAttack`，否则开枪会**同时**触发一次原版近战（玩家侧表现为「挥臂 + 空挥」） |
| `GameClientEvents:349 BulletVfxManager.render(event)` | `renderLevelStage` 开头（在 `player == null` 早退**之前**） | 弹道拖尾/VFX 的世界渲染 |
| `GameClientEvents:543 bullet$ImpactEffect` | 新订阅 `BulletEvent.ImpactEffectEvent` | 服务端 `BulletImpactPacketS2C:43` 已经在 post 这个事件，但 1.21 **此前没有任何订阅者** → 命中特效永远不播（编译看不见） |

`BulletVfxManager.tick()` 由 `GunHandler.handle` 内部调用（1.20 同），无需另接。

## 三、转换器 / 平台差异（本批实测）

### 3.1 `PortEventHandler` 漏转（复发）

`GunHandler.java:49` 的 `PortEventHandler.postEvent(use)` 是 `stage_batch` 的 leftover：
→ `NeoForge.EVENT_BUS.post(use)` + `import net.neoforged.neoforge.common.NeoForge`。
这是**同一缺陷第 3 次出现**（G1′/G4′ 都报过一次），建议加入转换器规则表。

### 3.2 geckolib 1.20(Forge) → 1.21.1(4.8.4) 的包名/类名搬家

| 1.20 写法 | 4.8.4 实际 | 处理 |
|---|---|---|
| `software.bernie.geckolib.core.animatable.model.CoreGeoBone` | **类已删除**（`core.*` 整包不存在；`GeoBone` 已是具体类） | `GunRenderer.findModelBone` 直接收 `GeoBone`；`registeredGeoBones()` 简化为 `new ArrayList<>(…getRegisteredBones())`（不再需要 `instanceof` 过滤） |
| `software.bernie.geckolib.util.RenderUtils` | `…util.RenderUtil`（单数） | import 被转换器改了、**7 处调用点没改**（漏转）→ 全量替换 |
| `AbstractClientPlayer#getSkinTextureLocation()` | `getSkin().texture()`（`PlayerSkin`，1.20.2+） | `GunRenderer:397` 改指 |
| `renderRecursively(…, float red, green, blue, alpha)`（14 参） | `renderRecursively(…, int colour)`（11 参） | 签名 + `super` 透传改 `int colour`（本类不使用颜色参数） |
| `renderFinal(…, float red, green, blue, alpha)`（12 参） | `renderFinal(…, int colour)`（9 参） | 同上 |
| `VertexConsumer.vertex(…).color(…).uv(…).uv2(…).endVertex()` | `addVertex(…).setColor(…).setUv(…).setLight(LightTexture.FULL_BRIGHT)` | `BulletRenderer`/`BulletVfxRenderUtil` 共 3 处（同一文件里 `RainbowBoulderRenderer` 已是新 API，说明转换器**按文件命中率不稳定**） |

`BoneSnapshot`、`AnimationProcessor.QueuedAnimation.animation().name()`、`BoneSnapshot.getOffsetX()` 等在 4.8.4 均存在，无需改。

## 四、本批新发现的「编译几乎看不见」的盲区

### 4.1 `luminite_impact` 粒子：整类 + 成员双缺

工具的类型级闭包看不到「1.20 有、1.21 无」的**注册层成员**。本批实测：

```powershell
# 1.20 ModParticleTypes 有、1.21 无的粒子
Compare-Object (1.20 成员) (1.21 成员)
#   luminite_impact   ← G5′ 必需（LuminiteImpactVfx 引用）
#   no_trail          ← 仅 SpearProjectile/SwordProjectile 用，不在 G5′ 范围，已记队列
```

`luminite_impact` 连带缺失的不止一行注册，而是**四件套**：粒子类型成员、`client/particle/LuminiteImpactParticle`
（整类）、`ModClientEvents.registerParticles` 注册行、`particles/luminite_impact.json` + 5 张贴图。
四件缺任何一件，编译门都不会报错 —— 只在运行时表现为「命中无特效」。

### 4.2 `client/effect/RenderStateShardAccessor`：**整类未迁**

1.20 的 `client/effect/RenderStateShardAccessor.java`（115 行，9 个成员 + `create` 工厂）在 1.21 **完全不存在**：

| 成员 | 1.20 消费点（共 20 文件） | 1.21 现状 |
|---|---|---|
| `GLINT_FF0000` / `GLINT_RAINBOW` / `create` | `ModClientSetups:244/245`、`GameClientEvents:158` | 已在 `ModClientSetups`/`ColoredGlintContext` 里有等价物（**1.21 早期就内联了**） |
| `ENTITY_TRANSLUCENT_EMISSIVE` | `SwordProjectileRenderer:210` | 等价物是 `ModClientSetups.TERRA_SWORD_RENDER_TYPE` |
| `TRAIL_RENDER_TYPE` | `RainbowBoulderRenderer:43` | **本批补进 `ModClientSetups`** |
| `LASER` / `HILL_OF_FLESH_BOUNDARY` / `EYES` / `entityGlow` / `entityTranslucentCullOverlay` | `LaserProjectileRenderer` / `HillOfFleshRenderer` / `PhasebladeRenderer` / 4 个 geo 渲染器 / `TownSlimeRenderer` 等 | **1.21 无对应物**，这些渲染器在 1.21 侧是各自另写的实现 |

**裁决**：G5′ 只补枪械内联必需的 `TRAIL_RENDER_TYPE`，且按 1.21 既有归置惯例放进 `ModClientSetups`
（1.21 早先把同类渲染类型内联在这里）。**整类搬迁是独立 WP**，需与上述渲染器的 1.21 现状一起裁决，
已记入 `notes/WORK-QUEUE.md`。若日后做该 WP，应把 `ModClientSetups.TRAIL_RENDER_TYPE` 一并回迁到新类。

## 五、偏差与遗留

### 5.1 `SimpleGeoItemRenderer`：合并而非覆盖

1.20 版（189 行）`implements IClientItemExtensions`，枪械经 `RegisterClientExtensionsEvent` 注册；
1.21 既有版（46 行）`implements GeoRenderProvider`，5 个方块调用方走 `createGeoRenderer`。
**两者是不同接口、不同消费路径，但 1.20 侧本就是同一个类** → 本批按并集合并（保留 `getGeoItemRenderer()`，
新增 `getCustomRenderer()`/`applyForgeHandTransform()`/`DefaultedItemGeoModel` 构造 + 开火骨骼隐藏/静止姿势复位/相机骨骼捕获）。

* 既有 5 个方块调用方（`RelicBlock`/`LifeCrystalBlock`/`ExtractinatorBlock`/`SkyMillBlock`/`GeoBoulderBlock`）**零改动**、零回归；
* 枪械路径的 `applyForgeHandTransform` 返回 `true` 后，NeoForge 的 `IClientItemExtensions` 会走
  `ItemInHandRenderer` 的混合路径，而 `getCustomRenderer()` 仍由同一实例提供 `GunRenderer`；
* `GunItems.GUN_ITEMS` 与 `ManaWeaponItems.{BEE_GUN,SPACE_GUN}`（`DeferredItem<BeeGunItem/SpaceGunItem>`）
  共用同一个 helper（`DeferredHolder<Item, ? extends Item>` 形参 + 钻石推断），与 1.20 `:1003` 完全同形。

### 5.2 本批**未做**（留给 G6′）

| 项 | 位置 | 说明 |
|---|---|---|
| TerraGuns gitlink 退役 | `build.gradle` / `neoforge.mods.toml` / `settings.gradle` | 本批只把主模组**消费侧**改指完，模块本身仍在树上 |
| `TerraGuns.MODID` 3 处 | `mixin/client/resources/model/{ModelBakeryMixin,ModelManagerMixin}:8/20`、`util/ModUtils:80/95` | 仅用于命名空间跳过集合，删除需与 gitlink 退役同批做 |
| `TerraGuns` 旧实现删除 | `TerraGuns/src/**/client/renderer/{entity,item}/**`、`TGGunSounds`/`TGTrailColors`/`TGUtil`/`TGTags`/`TGItems` | 与 §1.1 的 8 处 DIFF 对应，先加后删的「删」 |
| 资源迁移 | `assets/terra_guns/{geo,animations,textures,models}/**` | ✅ **已随本批完成**（§1.3）：主模组已持有全部枪械家族资源，TG 侧只剩音效副本（主模组本就有）与 `data/terra_guns/enchantment/*`、`data/minecraft/tags/*`、`pack.mcmeta`、`terra_guns.mixins.json` —— 后者随模块退役一并删除 |

### 5.3 队列新增

* `NO_TRAIL` 粒子（1.20 `ModParticleTypes:20` + `ModClientEvents:1013` 注册 + `SpearProjectile:137`/`SwordProjectile:358` 消费）；
* `RenderStateShardAccessor` 整类搬迁（§4.2）；
* G6′ 全项（§5.2）。

## 六、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（两轮：15 → 0；首轮 15 处全在 staged 新文件内，接线改动零错误） |
| `check_duplicates.py`（19 staged + 4 增量） | 0 处「疑似移动/重复」；8 DIFF/NEAR 均为 TerraGuns 先加后删 |
| 子模块 | 未改动（Lib `659c8c8`、TC `fc709c5`、其余 gitlink 原样） |
| PortLib 残留 | 主模组 0（`PortEventHandler` 漏转已修） |
| 待游戏内验收 | 开火（单发/连发/冷却）、弹道拖尾与 5 类命中特效、第一人称持枪姿势与相机动画、`R` 检视、持枪左键不触发挥臂 |

已修但**首选验收仍是跑一次 `runClient`**：本批有 4 处只有运行时才暴露的行为（
`BulletEvent.ImpactEffectEvent` 订阅、持枪取消原版攻击、相机骨骼捕获、`applyForgeHandTransform` 抵消视角摆动）。
