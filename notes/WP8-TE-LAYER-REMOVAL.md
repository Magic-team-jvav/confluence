# WP8 · 删除 `integration/terra_entity` 桥接层（2026-09-27 第 21 轮）

> 用户指令：「integration 的 terraentity 层可以删了」。
> 本批不是移植，而是**拆掉 1.21 分支特有的桥接层**，把消费点改回 1.20 的原生形态。
> 之所以现在能拆：WP4 批次 25 已把 1.20 的原生 NPC/交易层（`BaseNPC`、`NPCSpawner`、
> `HouseHandler`/`HouseValidater`、`NPCTradeMenu`/`NPCTradeOffer`/`TradeCondition` + 25 个条件、
> 原生对话屏）落到了 1.21，桥接层要补的那部分功能已经有原生对应物。

## 一、为什么 `integration/terra_entity` 是纯 1.21 分支产物

1.20 树里 **根本没有** `integration/terra_entity`：它的 `integration/` 只有
`geckolib` / `jei` / `terra_curio` / `terra_furniture` 四个包，而且 1.20 侧连
TerraEntity 子模块都没有（1.21 侧子模块 `62984cf3` 仍在，本批**不动**它）。
1.21 侧这层存在的理由是：1.21 分支当时没有主模组自己的 NPC 层，所以要在 TE 的
`AbstractTerraNPC` 上外挂区域、祝福、房屋搬迁、交易锁、TE 交易变体、物品效果组件等。
这些「外挂」现在全部有原生落点。

因此本批的口径是：**删除桥接，消费点按 1.20 改回原生**；1.20 没有对应物的部分按 1.20 的
处理方式（注释掉 / 删除），并在下文登记成后续批次的欠账。

## 二、删除清单（git 层面）

| 类别 | 内容 | 数量 |
|---|---|---|
| 包 | `org/confluence/mod/integration/terra_entity/**` | 44 文件 |
| mixin | `org/confluence/mod/mixin/integration/terraentity/**` | 14 文件 |
| mixin 配置 | `confluence.mixins.json` 里 `integration.terraentity.*` 条目 | 14 条 |
| 1.21 独有界面/包 | `common/menu/NPCTradesForgeMenu`、`client/gui/container/WithForgeTradeScreen`、`network/c2s/SellTradePacketC2S` | 3 文件 |
| 1.21 独有 datagen | `common/data/gen/NPCShopProvider`（写的是 TE 交易格式，原生端读不了） | 1 文件 |

被删的 mixin 里没有任何**外部调用点**依赖它注入的成员（全树搜 `.confluence$` 调用点为空），
所以删除是安全的。

## 三、迁移与改指

### 3.1 `ModEffectStrategies` 迁到 `common/init/`（用户选择的方案）

`integration/terra_entity/init/ModEffectStrategies`（188 行）**没有删**，而是原样搬到
`org/confluence/mod/common/init/ModEffectStrategies.java`：它注册的
`DeferredRegister<EffectStrategy>`（`TERegistries.EFFECT_STRATEGIES`）仍被物品层的
TE 效果组件体系使用（`BowItems` 2 处、`SwordItems` 5 处，如 `LIGHTS_BANE_EFFECT`、
`BLOOD_BUTCHERED_EFFECT`、`BAT_FANG_EFFECT`、`BEE_KEEPER_EFFECT`、`TENTACLE_SPIKES_EFFECT`、
`PURPLE_CLUBBERFISH_EFFECT`）。1.20 侧这些效果写在各自的物品类里（`DemonBow`、`BatBatItem`、
`EffectSwordItem` 等），属于**尚未移植的物品层（WP6）**，等那批落地后这个文件随之退役。
注册点从 `TEEvents.register` 移到 `Confluence#register`（`ModEffectStrategies.EFFECT_STRATEGY.register(eventBus)`）。

### 3.2 移植 1.20 的 NPC 交易界面（用户确认一起做）

删掉 `WithForgeTradeScreen` 后，原生 `ModMenuTypes.NPC_TRADE` 就没有屏幕了，所以同批移植
1.20 的 `client/gui/container/npc_screen/**`：

| 文件 | 与 1.20 的差异 |
|---|---|
| `NPCTradeScreens.java` | 与 1.20 **逐字节相同** |
| `NPCTradeScreen.java` | 仅 `renderBackground(graphics)` → `renderBackground(graphics, mouseX, mouseY, partialTick)` |
| `NPCTradeItemOutline.java` | 仅顶点缓冲：`Tesselator.getInstance().getBuilder()` + `begin/vertex/uv/color/endVertex` → `Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)` + `addVertex/setUv/setColor` + `BufferUploader.drawWithShader(buildOrThrow())` |
| `NPCTradePortrait.java` | 无差异（`RenderSystem.setShaderLights` 在 1.21.1 仍在，实测 `RenderSystem.java:401`，保留调用） |

配套资源（1.21 侧此前完全没有）：`assets/confluence/shaders/core/trade_item_outline.{json,vsh,fsh}` 3 个 +
`assets/confluence/textures/gui/trade/*.png` 10 张。着色器注册加在
`client/renderer/ModRenderer.register`（第 4 条，`DefaultVertexFormat.POSITION_TEX_COLOR`，
回调里 `NPCTradeItemOutline.setShader`），对应 1.20 的 `ModClientEvents:1150-1157`。
屏幕注册：`ModClientEvents` 的 `NPC_TRADES_MENU → WithForgeTradeScreen` 改成
`NPC_TRADE → NPCTradeScreens::create`（与 1.20 `ModClientEvents:226` 一致）。
1.21 原有的 `client/gui/container/NPCReforgeScreen`（139 行，注册在 `REFORGE_MENU`）与 1.20 的
`npc_screen/NPCReforgeScreen`（96 行）是两份不同实现，**保留 1.21 那份**，不重复移植。

### 3.3 消费点改回原生（对齐 1.20）

| 文件 | 原桥接 | 改成 |
|---|---|---|
| `Confluence` | `TEEvents.register` / `ModTradeLockProviderTypes.TYPES.register` | 删两条，换成 `ModEffectStrategies.EFFECT_STRATEGY.register` |
| `common/event/ModEvents` | `TEHelper.redirectLootTable()` / `TEEvents.modifyAttributes(event)` / `TEItemComponentModify.modifyDefaultComponents(event)` | 全删（1.20 无对应物；`modifyDefaultComponents` 只留雪球堆叠那条） |
| `common/event/game/entity/LivingEntityEvents` | `TENpcCompat.onNPCRemoved` + `TENpcEntities.CLOTHIER/GUIDE` + TE `Skeletron` | `NPCSpawner.INSTANCE.onNPCRemoved(npc)` + `NpcEntities.CLOTHIER/GUIDE` + 主模组 `Skeletron(BossEntities.SKELETRON.get(), level)`（即 1.20 `LivingEntityEvents:190-205`）；顺带删掉只有一句空实现的 TE 蠕虫分支（1.20 该段整块注释掉） |
| `common/event/game/entity/PlayerEvents` | `TENpcCompat.applyBenedictions` | `NPCSpawner.INSTANCE.applyBenedictions(npc)`（1.20 `PlayerEvents:428-434`） |
| `network/c2s/HouseSelectPacketC2S` | TE `HouseManager`/`IHouseDetector` + `TENpcCompat.moveNPCToAnotherRegion` + `IAbstractTerraNPC.confluence$getRegion` | 1.20 原生版本：`HouseHandler.INSTANCE.findHouseAt/removeHouse/setHouse` + `HouseValidater.scan(...).message()/isValid()/make(uuid)` + `BaseNPC.setHouse`；并补上 1.20 的两道防越权校验（区块加载 + 64 格距离） |
| `client/event/GameClientEvents` | 给 TE `DialogScreen` 加「重铸」按钮 | 整块删除（1.20 该块是注释态；1.21 的原生 `GoblinTinkererDialogScreen` 已有重铸按钮） |
| `mixin/world/entity/EntityMixin` | `AbstractTerraNPC` + `IAbstractTerraNPC` 的区域失效 | `BaseNPC` + `NPCSpawner.INSTANCE.forgetNPC(sourceNpc)`（1.20 `EntityMixin:186-194`） |
| `mixin/world/item/crafting/RecipeManagerMixin` | `TEHelper.processRecipes` | 删掉该 `@Inject`（1.20 只有 `TCHelper.processRecipes`） |
| `common/init/ModMenuTypes` | `NPC_TRADES_MENU` 注册 | 删（1.20 只有 `NPC_TRADE` + `REFORGE_MENU`） |
| `common/event/NetworkEvents` | `SellTradePacketC2S` 注册 | 删 |
| `network/c2s/OpenMenuPacketC2S` | `MAID_TRADE_MENU` map 条目 | 注释掉（1.20 `:40` 就是注释态），常量保留 |
| `common/init/item/BowItems`、`SwordItems` | `integration.terra_entity.init.ModEffectStrategies` | `common.init.ModEffectStrategies`（只是换包） |
| `common/data/gen/ModDataGenerator` | `NPCShopProvider::new` 挂在 `CollectRecipeProvider` 下 | 删（见下） |

## 四、本批留下的欠账（必须在后续批次补）

1. **`NPCShopProvider` 需要按 1.20 重写**（WP7）。1.20 的版本是 334 行的 `DataProvider`
   （构造 `NPCShopProvider(PackOutput)`，注册 `generator.addProvider(server, new NPCShopProvider(output))`），
   写的是原生 `NPCTradeOffer`/`TradeCondition` 格式；1.21 原有那份是 600+ 行的
   `AbstractRecipeProvider` 子类 + TE 交易格式（`MoneyTradeItem`/`SellTrade`/`TradeProperties`），
   与原生 `NPCTradeList` 不兼容，故本批直接删除。
   **已实测**：1.20 那份引用的 173 个符号里，1.21 只差 `WhipItems` / `YoyoItems`（鞭子与悠悠球的
   原生物品层，属 WP6）与 `TFBlocks.HANGING_POT_ITEM`（TerraFurniture 子模块命名）。也就是说
   **WP6 的 whip/yoyo 物品层一落地，这个 provider 就能整份搬过来**。在那之前 NPC 商店没有生成数据。
2. **TE 实体的属性补正没了**。被删的 `TEEvents.modifyAttributes` 给 TE 侧怪物/Boss 挂
   `LibAttributes.getArmorPenetration()` 与 `ARMOR_TOUGHNESS`（护甲穿透/韧性）。1.20 里这些数值写在
   原生注册层（`BossEntities`/`MonsterEntities` 的 `withAttributes(...)`），所以等 WP2/WP3 的原生
   实体注册补齐后自然回归；当前 1.21 仍会从 `CrimsonHeartBlock`/`LarvaBlock`/`ShadowOrbBlock` 等
   地方生成 TE 版 Boss，它们暂时没有这两项属性。
3. **鞭子数值修正没了**（`TEEvents.onRegisterWhips`：TE 子模块鞭子伤害除以
   `WhipRegisterModifyEvent.damageFactor`）。1.20 用原生鞭子（WP6 的 `api/whip`）。
4. **TE 物品默认组件修正没了**（`TEItemComponentModify`：鞭子/悠悠球等的默认组件）。
   同样等 WP6 原生物品层。
5. **`AvailableHouseSelectPacketS2C` 仍指向 `TENpcEntities`**（房屋选择 HUD 的槽位表），
   属 WP4/WP7 的收尾；本批只保证 `HouseSelectPacketC2S` 不再依赖 `integration`。

## 五、验证

- 编译门：`python tools/port2native/build_errors.py --module :ConfluenceOtherworld --repo . --maxerrs 2000`
  本批开始前 0 错误，改动后见提交信息与下表（批次出口必须 0）。
- 全树残留检索：`integration.terra_entity` / `TENpcCompat` / `IAbstractTerraNPC` / `TEEvents` /
  `TEGameEvents` / `TEHelper` / `TEItemComponentModify` / `ModTradeProviders` /
  `ModTradeLockProviderTypes` / `NPCTradesForgeMenu` / `WithForgeTradeScreen` / `SellTradePacketC2S` /
  `NPC_TRADES_MENU` / `TerraSwordTrail` 全部为 0（`OpenMenuPacketC2S` 里那行按 1.20 保留为注释）。
- 未做：`runClient` 游戏内验收（NPC 商店界面、头像渲染、商品轮廓描边、房屋工具）。
