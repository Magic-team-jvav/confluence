# TerraEntity / TerraGuns 刻意丢弃清单（Q4 决策记录）

- **决策（Q4）**：**TerraEntity / TerraGuns 的东西一律不收割**；1.21 侧一切按 1.20.1 的新 API / 新实现来。
- **本文用途**：把"曾经存在、现在被刻意放弃"的东西逐项记下来。日后若被玩家或作者要求"找回某个效果"，先查本文，再决定是否在 1.20 上重做。
- **完整枚举**：`notes/ENTITY-MODULE-COVERAGE.md` §3（逐文件、逐行号引用）。本文只做分组索引与决策记录，不复制全部 200 条。
- **恢复政策**：恢复 ≠ 搬 TE 代码。恢复必须在 **1.20 的新 API / 新框架（行为树、原生实体、ModDataMaps、本地交易条件）上重做**，然后走正常回移流程。任何"把 TE 代码搬回来"的做法都与 Q3/Q4 冲突。

---

## 1. 玩家可见效果（放弃）

| 子系统 | 规模 | 一句话说明 | 恢复时的落点建议 |
|---|---|---|---|
| **动态模糊** | 9 文件 + 7 个 shader program | `IMotionBlurContext/Holder/Manager/Renderer`、`MotionBlurManager`、`PosRotMotionBlur*`、`GeoMotionBlurRenderer`；shader：`color_blit`/`dissolve_blit`/`dissolve_blit_lager`/`float_bar`/`float_fire`/`mix_add`/`pixel_style_dissolve`。**1.20 所有历史 ref 都没有**，`AfterimageHelper` 不是替代品 | 1.20 的残影模块（`AfterimageHelper`/`AfterimageStyle`）上扩展，或新写 shader 效果层 |
| **客户端动画器 / 骨骼层** | 10 文件 | `AbstractAnimator`/`BoneAnimator`/`AnimatorContext`/`BoneState`/`GeoBone*`/`Left|RightHandGeoBoneAnimator`/`MultiBoneAnimator`/`SkeletronAnimator`。注意：关键帧引擎本体（keyframe/baker/interpolator/motion curve/IK/状态机）在 1.20 历史里**可恢复**（`2569be361` = `b20c0cefd^`），缺的是**这一层动画器 API** | 1.20 现有 geckolib 动画路径上补一层 animator（若真需要） |
| **模型/渲染/工具** | 4 文件 | `AnimatorModel`、`AnimatorRenderer`、`DefaultBoneBoundIdents`、`ShaderUtil` | 随动画器一起 |
| **后处理** | 3 文件 | `BossSpawnCameraManager`、`BrainTranslucent`、`PlayerSwordTrailRenderer` | 镜头/后处理统一走 1.20 的 client 层 |
| **拖尾框架** | 10 文件 | `ITrail`、`SwordTrail`、`BoomerangTrail`、`SummonSwordTrail`、`PositionPose{,Properties,Trail}`、`TrailProperties`、`ColorfulItemInHandTrail`、`ItemInHandTail` + `ItemInHandTrailAttachment`；唯一消费者 `integration\terra_entity\trail\TerraSwordTrail`(189 行) | 若要拖尾效果，按 1.20 的渲染体系重做（1.20 已有剑气渲染等自研效果可复用） |
| **镜头震动** | 3 文件 | `CameraShakeManager`、`CameraShakeData`、`SyncCameraShakePacket` | 1.20 client 层新写 + 自研网络包（1.20 已有统一网络层） |
| **聊天合成与气泡** | 25 文件（+5 个 `api\npc\chat\**` seam 接口） | `ChatArranger`、`ToTypeChat`(NPC↔NPC 对话)、`ChatElementProvider`/`ChatConditionProvider` 及变体、`client\gui\renderer\chat\**`（气泡 3 + 元素渲染 3）、`NPCChatBubbleBuffer` | 1.20 已有自己的对话/气泡体系（NPC 对话框、`OpenNPCDialogPacketS2C` 等），按其体系扩展 |
| **Chester 注册表** | 4 文件 | `ChesterType(s)`、`ChesterConditionalType(s)` | 1.20 的容器实现上重做 |
| **EffectStrategy 注册表** | 8 文件 | `EffectStrategy(Provider)`、`IEffectStrategy`、`PrefabEffect`、`RandomWeightEffect`、`TimePossibilityAmplifierEffect` | 1.20 的命中效果自研体系 |
| **DriveAwaySystem** | 5 文件 | `DriveAwayArrowIntegration`/`DriveAwayAttachment`/`DriveAwayDataAttachment`/`DriveAwayExecutor`/`DriveAwayMath` | 1.20 的 attachment 体系 |
| **FigureBlock（手办方块）** | 3 文件 | `FigureBlock` + `FigureBlockRenderer` + `TEFigureBlocks` | 内容项，见 §4 |
| **行为树周边** | 2 项 | `BTServer` 的 **web viewer 工具 + 配置端口**（`behavior_tree_web_viewer_server_port`）、`CrossBowAttackOnCooldownBrain`（弩类 NPC 攻击冷却） | 1.20 行为树上补节点/查看器 |
| **工具类** | 7+ 文件 | `AdapterUtils`、`CircularArrayBuffer`、`EfficientCylinderDestruction`、`WorldChunksManager`、`TEItemUtil`/`TEUtils`（≈ 1.20 的 `ModUtils`）、`RecipeDrawerUtils`；`DifficultSelector`、`AttBuilder` | 按需在 1.20 重写 |
| **配置键** | ≈20 个 | `enable_entity_motion_blur`、`npc_chat_bubble_style`、`spawn_without_light`、`enemy_spawn_chance(_apply_all)`、`chance_to_spawn_slime_on_zombie_head`、`boss_bar_style`/`boss_bar_number_offset_x|y`、`enableNonSpiderModel`、`display_summon_items`、`boss_clear_when_no_target`、`boss_no_physics`、`boss_leave_on_day`、`boss_keep_wandering`、`generate_projectile_particle`、`boss_attributes_multiplier_*`、`TEAttributeModifierConfig` | 1.20 的 `StartupConfigs`/`PortConfigSpec` 体系；⚠️ 注意 1.20 的 `boss_attributes_multiplier_*` 上界是 100（TE 是 10） |
| **无对应的 mixin/accessor** | ≈20 项 | `BossEventMixin`、`ServerBossEventMixin`、`ShaderInstanceMixin`、`LootDataManagerMixin`、`ReloadableServerResourcesMixin`、`NaturalSpawnerMixin`、`MobSpawnSettings(Builder)Mixin`、`ModifiableBiomeInfoMixin`、`ServerEntityMixin`、`client\BossHealthOverlayMixin`、`client\EntityRenderDispatcherMixin` + 8 个 accessor（`CameraAccessor`/`GameRendererAccessor`/`Geo(Entity)RendererAccessor`/`LevelRendererAccessor`/`MobAccessor`/`ProjectileWeaponAccessor`/`SlimeAccessor`） | 逐项判定是否需要（1.20 已有自己的 mixin 集 135 个） |

## 2. 架构扩展点（放弃）

| 项 | 规模 | 与 1.20 现状的关系 |
|---|---|---|
| **12 个自定义数据包注册表** | 14 个中缺 12 个：`effect_strategy*`、`trade_provider/task/lock/generator/modifier`、`chester_type`、`chat_element`、`chat_condition`、`mapped_data_type` | 1.20 只有 3 个（`track_type_provider`、`generation_provider`、`trade_conditions`）+ 2 个召唤相关 |
| **mapped data 体系** | 19 文件（`DeferredMappedType`、`MappedData(Type|Loader|Types)`、`MappedKey`、`BossSkillMapDatas`、`MonsterMappedDatas`、`NPCMappedDatas`、`WeaponMappedDatas`、`WhipData`…）+ 4 个生成 json | 1.20 用 vanilla 风格的 `ModDataMaps` 取代 |
| **交易 SPI** | 46 文件（`ITrade*` 13、`TradeProvider/Task/Lock/Modifier/Generator` 注册表与变体、8 个 `*LockRecipeDrawer`、`SimpleTradeMenu`/`TETradesMenu`、`BitMask`/`TradeParams`/`TradeModifiers`）+ 3 个网络包 + datagen provider | **功能在、SPI 不在**：1.20 的 27 个交易条件是 1.21 侧 9 个 lock 的超集；1.20 用 `NPCTradeOffer`/`NPCTradeMenu` + 自研 schema |
| **datagen schema 冲突** | 1.21 生成 `data\confluence\npc\shop\*.json`（18 个，含 `maid_shop.json`）；1.20 生成 `data\confluence\npc\trades\*.json`（19 个） | **以 1.20 的 schema 为准**，1.21 侧 datagen 重写（这条与 Q2/Q3 一致） |
| **JEI NPC 分类 + 11 个 recipe drawer** | 1.21 有、1.20 完全没有（`RecipeDrawer` 只作为语言字符串出现） | 玩家可见的展示层，**建议按 1.20 的展示体系重做**（见 §4 待确认） |

## 3. TerraGuns（放弃）

- **4 个类**：`TaczAnimationConstraint`（197 行 ICA/TACZ 约束骨骼数学，**1.20 五个模块全局 grep 无命中**）、`GunEnchantmentService`(155 行)、`TGEnchantments`、`EmergencyMeleePacketC2S`。
- **数据**：`data\terra_guns\enchantment\{compressed_tactics, emergency_melee, temporary_reserve}.json`、`tags\item\enchantable\gun.json`、`TGTags.GUN_ENCHANTABLE`、`TGDataComponents.EMERGENCY_MELEE_COOLDOWN_END`。
- **消费端 delta**：`fireRadial`、`spawnRadial`、`getAmmoForShot`、`hasEnoughAmmo`、`consumeAmmo`、`setTemporaryReserveLevel`、temporaryReserve handler、`HandAnimationAction.EJECT_SHELL` + `shell_action` 通道。
- **mixin 行为**：`mixin\client\ItemInHandRendererMixin` 的 PUT_AWAY 举起保持（`mainHandHeight >= 0.6F`）；1.20 有同名 mixin 但用途不同（对 GUN tag 取消 `applyItemArmTransform`、弓/鞭/悠悠球/镜子），**都不碰 `mainHandHeight`/`PUT_AWAY`**。
- **单元测试**：`TrailPathSmootherTest`、`BallisticsResolverTest`、`HomingControllerTest` —— **1.20 根本没有 `src\test` 目录**。
- ⚠️ 记录一个现状：1.20 有 `isPutAwayAnimationPlaying` 辅助方法但**没有任何调用者**，说明这块在 1.20 侧本来就是未完成状态。

## 4. 内容小缺口（**Q9 已定：全部丢弃**）

**决策（Q9）**：**全部丢弃，1.20 的内容即最终内容**——本节各项不做重做，直接随 TerraEntity/TerraGuns 一起消失。

| 项 | 内容 | 处置 |
|---|---|---|
| `StardustDragon` + `StardustDragonSegment`（星尘龙）+ `SummonFocusEffect` | 实体/效果 | **丢弃**（1.20 的 20 个 BOSS 即最终 BOSS 集，且比 1.21 多 4 个） |
| 物品 id：`stardust_dragon_staff`、`summon_{wooden,stone,iron,golden,diamond,netherite}_sword_staff`、`wallet` | 7+1 个物品 | **丢弃** |
| `HouseDetectItem` | 物品 | **丢弃**（1.20 有该功能的 HUD 流程，无对应物品） |
| `FigureBlock` + `FigureBlockRenderer` + `TEFigureBlocks` | 手办方块 | **丢弃** |
| 11 个交易 lock recipe drawer + JEI NPC 分类 | 展示层 | **丢弃**（1.20 没有 recipe drawer 概念） |

⚠️ **副作用记录**：这些 id 在 1.21 侧**已经在线上存过**。整树替换后，1.21 玩家旧存档里已获得的这些物品/实体将变成"未知物品"或被清理。建议在 1.21 侧的 CHANGELOG / 发布说明里明确写出"以下内容已移除"清单（本节即该清单），避免玩家把它当成 bug 上报。

## 5. 集成文件三分类（替换前必须先成文，R12）

### 5.1 `integration\terra_entity`（43 个）

| 分类 | 数量 | 明细 |
|---|---|---|
| **纯接线 → 消失** | 6 | `TEEvents`、`TEGameEvents`、`TEHelper`、`AttributeRegistration`、`IAbstractTerraNPC`，以及 `brain\{ConfluenceArmDealerNPCAi, ConfluenceDemolitionistNPCAi}`（**边界情形**：内容是武器商/爆破专家的战斗 AI，1.20 已用 `NPCCombatActions.{ARMS_DEALER, DEMOLITIONIST}` + `NpcEntities.*` 表达 → 判为已覆盖） |
| **1.20 已有等价实现 → 零工作（需逐项核对）** | 12 + 7 + 1 | 12 个交易 lock（1.20 的 `trade\conditions\*` 基本一一对应；**仅 `FishingHookInFluidLock`(46 行) 与 `QuestedFishPrecheckLock`(37 行) 需要人工确认**）；7 个金钱交易类（≈ `NPCTradeOffer`/`NPCTradeMenu`）；`TEItemComponentModify`（≈ `ModDataComponentTypes`） |
| **1.20 缺失 → 判定"重做还是丢"** | 11 + 4 + 1 | 11 个 recipe drawer（1.20 无 drawer 概念）；4 个注册表注册（`AdditionalChesterTypes`/`ModEffectStrategies`/`ModTradeLockProviderTypes`/`ModTradeProviders`）；`TerraSwordTrail`(189 行，依赖已丢弃的拖尾栈) |
| **纯接线 + 需迁移注入成员** | 1 | `AbstractTerraNPCMixin`(69 行) 消失，但其注入成员需搬进 1.20 的 `BaseNPC` |

### 5.2 `mixin\integration`（58 个，其中 44 个与 TE 无关 → 不受影响）

| 分类 | 数量 | 明细 |
|---|---|---|
| **与 TE/TG 无关**（sodium/curios/jei/jade/terracurio/create/ftbchunks…） | 44 | 不受本次决策影响；1.20 大多有对应物（`mixin\integration\{curios, geckolib, jei, magiclib, terracurio, terrafurniture, sodium}\*`）。仅 `geckolib\{GeoCubeMixin, InternalUtilMixin}` 与 `jade\HarvestToolProviderMixin` 需在内联后复查 |
| **TE 接线 → 消失** | 4（+2 边界） | `AbstractTerraNPCMixin`（成员搬 `BaseNPC`）、`ServerBoundEventPacketMixin`、`TEKeyBindingsMixin`（≈ `client\ModKeyBindings`）、`TEUtilsMixin`（≈ `util\ModUtils`） |
| **内容需保留** | 10 | Angler / Mechanic / TravelingMerchant / DemonEye / DungeonGuardian / GoldenSlime / Skeletron / TEAnimals / SpawnPlacementChecks / `DynamicAnglerTradeTaskMixin`（最后这个**必然消失**，因为目标 `DynamicAnglerTradeTask` 属 TE 的交易任务 SPI → 改为扩展 1.20 的渔夫任务） |
