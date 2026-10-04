# 枪械内联迁移（TerraGuns → 主模组）：实测规模与批次切分

> 用户裁决（2026-09-27）：「**立刻做枪械内联迁移**」——把 `TerraGuns` 子模块的枪械内容内联进
> `ConfluenceOtherworld` 主模组，并删除子模块里的旧同名类（对应既定决策 Q3/Q4：
> TerraEntity/TerraGuns 退役、其实体/枪械内联进主模组）。
> **本文件是开工清单**；执行时**每个批次单独提交、单独过编译门**，且必须**先 diff 再动**（不得再把「内联」做成「新建副本」）。

## 一、实测规模（本文件写就时的仓库状态）

| 项 | 数值 |
|---|---:|
| `TerraGuns` java 文件 | **99** |
| 主模组对 `org.confluence.terra_guns.*` 的引用 | **30 处 / 23 个文件** |

TerraGuns 内部分布（前几大）：`common/definition/behavior` 15、`common/init` 12、`client/renderer/entity/effect` 10、
`common/combat` 9、`common/definition` 6、`api/client/animation` 5、`common/datagen/provider` 5、
`client/renderer/entity` 5、`client/renderer/item` 4、`network/c2s` 3、`common/event` 2、`api/event` 2 …

主模组引用最多的是：

| 引用目标 | 处数 | 说明 |
|---|---:|---|
| `common.init.TGItems` | 11 | 枪械物品注册层（配方/战利品/商店 provider 都在用） |
| `common.entity.bullet.BaseBulletEntity` | 3 | 弹幕基类 |
| `TerraGuns`（模组主类） | 3 | 模组入口/事件 |
| `common.definition.GunDefinition` | 2 | 枪械定义 |
| `common.item.gun.BaseGun` | 2 | 枪械物品基类 |
| `common.init.TGTags` / `TGTrailColors` / `TGSoundEvents` / `TGGunSounds` | 2/1/1/1 | 标签与音效 |
| `util.TGUtil`、`client.init.TGRenderTypes`、`common.entity.bullet.CustomBulletEntity`、`api.event.GunEvent` | 各 1 | 工具/客户端/事件 |

## 二、已完成的第一步（WP6G，`9cc27098a` 一带）

主模组侧**已经**落地的枪械词汇（8 个）：
`common/combat/gun/{GunStats,AmmoStats,Ballistics,BallisticsResolver,ShotContext}`、
`common/component/GunPropertyComponent`、`common/item/gun/definition/{FireMode,GunProjectilePattern}`。

**更正**：WP6G 的提交信息写成「在 1.21 侧此前都不存在」是错的 —— TerraGuns 子模块里有同名旧类；
`FireMode` 实测是 **MOVE 级**（只差包声明 = 同一个类），所以 WP6G 是**内联迁移的第一步**，
不是新增词汇。本批之后，主模组侧的内联目标包已经建好，后续批次往里搬、并把引用改过来。

## 三、批次切分（依赖顺序，每批一个提交 + 编译门）

| 批次 | 内容 | 为什么这个顺序 |
|---|---|---|
| **G1** | `common/definition/**`（6）+ `api/event/GunEvent`（2） | 纯定义/事件词汇，主模组已引用 `GunDefinition`（2 处）；先搬定义层，后面物品/弹幕才有类型可用 |
| **G2** | `common/combat/**` 剩余 4（已内联 5，TerraGuns 现 9）+ `api/client/animation`（5） | 弹道/掉落/动画接口，依赖 G1 |
| **G3** | `common/entity/bullet/**`（`BaseBulletEntity`/`CustomBulletEntity` 等）+ `common/item/gun/BaseGun` | 主模组已引用这三个（3+1+2 处）；搬完就能把引用改指主模组 |
| **G4** | `common/init/**`（`TGItems`/`TGTags`/`TGSoundEvents`/`TGTrailColors`/`TGGunSounds`…） | 注册层必须**最后**搬：主模组 11 处引用集中在 `TGItems`，且配方/战利品/商店 provider 都依赖它 |
| **G5** | 客户端（`client/renderer/**` 19、`network/c2s` 3、`datagen/provider` 5）与主类 `TerraGuns` | 客户端与数据生成依赖 G1~G4 |
| **G6** | 收尾：`TerraGuns` 子模块 gitlink 移除 + 主模组 `build.gradle` 依赖/`neoforge.mods.toml` 清理 + 全仓确认零 `org.confluence.terra_guns` 引用 | 只有到这一步才允许删子模块 |

### 每批的硬约束（沿用本项目纪律）

1. **先 diff 再动**：TerraGuns 的类与 1.20 主模组的同名类**内容不同**（TerraGuns 是 1.21 旧实现，
   1.20 主模组是分叉后的新架构）——搬到主模组时**以 1.20 版为事实来源**，
   但**必须逐类核对成员**（TerraGuns 侧的被引用成员在主模组新版里是否还在；
   反之亦然），不能整包覆盖。
2. **每批跑 `check_duplicates.py`**（README 过程纪律 5）：`SAME/MOVE/NEAR` 说明是同一个类，
   按「移动」处理（改引用 + 删旧），不许两边并存到批次结束。
3. **引用改指与搬运同批完成**：主模组引用的 `terra_guns.*` 必须在对应批次里改指主模组新类，
   否则又会出现「两份实现、旧的在用」。
4. 每批过 `build_errors.py --module ConfluenceOtherworld`（0 错误）；涉及子模块删除的 G6 还要
   确认子模块自身仍能编译（或按退役流程整体移除）。

## 四、风险与工作量提示（诚实说明）

- **99 个文件 + 30 处引用**是与 WP5（召唤体系 92+49）同量级的工作包，不可能一次做完；
  按上表 G1~G6 分 6 批推进，每批都要编译门 + 单独提交。
- 其中**注册层（G4）与物品内容**是最大的一块（`TGItems` 背后是整车枪械物品/配方/战利品数据），
  且 1.20 侧同样内容已内联进主模组（`ModItems`/`ModTabs` 等）——内联时要**以 1.20 的注册形态为准**，
  避免与 1.21 既有的 `common/item/gun/{BeeGunItem,ManaGunItem,SpaceGunItem,StarCannonItem}` 撞车
  （那 4 个是 1.21 旧形态，要按 1.20 决定是保留、改造还是删除，必须在批次笔记里写清）。
- G6 之前，**主模组允许存在「新类在主模组、旧类在子模块」的过渡态**（这是既定先加后删），
  但**每个批次都要在提交信息里写明旧类在哪、何时删**。

## 五、⚠️ 实测修正（2026-09-27 开工前的闭包测量）：G1~G6 的顺序**不能照做**

第三节那张表是按「引用处数」切的，**没有量闭包**。真去量了之后发现三件事：

```powershell
python tools/port2native/dep_subset.py --seed org.confluence.mod.common.item.gun.definition.GunDefinition
# {"candidates": 1, ...}                     <- 只有自己，自包含
python tools/port2native/dep_subset.py --seed org.confluence.mod.api.event.GunEvent
# {"candidates": 3, ...}                     <- GunEvent + BaseGun + GunDefinition
python tools/port2native/dep_subset.py --seed org.confluence.mod.common.item.gun.definition.BulletBehavior
# {"candidates": 156, ...}
python tools/port2native/dep_subset.py --seed org.confluence.mod.api.event.BulletEvent
# {"candidates": 156, ...}
```

1. **G1 的 `GunEvent` 依赖 G3 的 `BaseGun`** —— 事件批不可能先于枪械基类落地。
2. **`BaseGun` 自己还有一个谁都没记的前置**：1.20 的 `common/item/gun/BaseGun` 直接 import
   `org.confluence.lib.api.animation.first_person.{HandAnimationApi,HandAnimationChannel,HandAnimationProfile}`
   —— 这套 API 1.21 侧**既不在 Magic-Lib 也不在主模组**（1.21 把它放在 TerraGuns 的
   `api/client/animation/**`，随退役一起删）。另外 `ModDataComponentTypes.GUN_PROPERTY`、
   `CommonConfigs.AUTO_FIRE_ALL_GUNS`、`ModTags.Items.AUTOMATIC_GUN` 在 1.21 侧**都还不存在**。
3. **`BulletBehavior`/`BulletEvent` 的 156 文件闭包**：它们要 `BaseBulletEntity`，而 1.20 的
   `BaseBulletEntity` 引用了 `common/entity/npc/BaseNPC`（**WP4 的 NPC 基座**）、
   `common/init/item/GunItems`（G4 注册层）、`common/item/BaseBullet`。
   也就是说 **G3 的弹幕层被 WP4 与 G4 双重挡住**，而「定义层 ↔ 弹幕层」又是互相依赖的
   （`BulletBehavior` 的方法签名吃 `BaseBulletEntity`，`BaseBulletEntity` 又调用 `BulletBehavior`）。

### 修正后的顺序（每批仍是一个提交 + 编译门）

| 批次 | 内容 | 状态 |
|---|---|---|
| **G0** | Magic-Lib 补 `lib/api/animation/first_person/**`（5 文件 / 227 非空行） | ✅ **已完成**：子模块 `a19d894` + 根仓库 `cde95394d` |
| **G1′** | 主模组前置：`ModDataComponentTypes` 的 `GUN_PROPERTY`/`BULLET_PROPERTY`、`CommonConfigs.AUTO_FIRE_ALL_GUNS`、`ModTags.Items.AUTOMATIC_GUN`，外加自包含的 `GunDefinition` | ✅ **已完成**（`20cbc0204`；4 文件：1 新 + 3 改；`uncovered=[]`、编译门 0 错误） |
| **G2′** | `BaseGun` + `GunEvent` + 改指主模组（含 4 个旧形态枪械 `BeeGunItem`/`ManaGunItem`/`SpaceGunItem`/`StarCannonItem` 的取舍裁决） | ⚠️ **半完成**（`3a9048386`）：`BaseGun`+`GunEvent` 已落（无消费点）；**改指做不了**，实测原因见第七节 |
| **G3′** | 弹幕层（`BaseBulletEntity`/`CustomBulletEntity`/`BaseBullet`）+ 行为层 15 个 + `BulletDefinition`/`BulletBehavior` + `BulletEvent` | ✅ **已完成**（随 **WP4 批次 25** 一起落地：`BaseBulletEntity -> BaseNPC` 那条链正是它被挡住的原因，NPC 层落地后一次性清完；编译门 0 错误）。落地记录与 **1.21 API 差异**（`NeoForge.EVENT_BUS.post` 取代 `PortEventHandler.postEvent`、`@Cancelable` → `ICancellableEvent`、`ItemStack.of/save` 要 `HolderLookup.Provider`、`MobEffectInstance` 吃 `Holder<MobEffect>`、`appendHoverText` 第 2 参 `Item.TooltipContext`）见 `notes/WP4-BATCH25-WIP.md` 第十三节 |
| **G4′** | 注册层（`TGItems`/`TGTags`/`TGSoundEvents`/`TGTrailColors`/`TGGunSounds`…）+ **服务端开火管线** + **网络层** + `ItemEvents` 枪械处理器重写 | ✅ **已完成**。落地形态与「G4」的原始设想不同：**以 1.20 结构为准**（不是把 `TG*` 类整包搬过来）→ 新增 `common/init/gun/{GunSounds,GunTrailColors}` + `GunItems` 整层 + `ModGunProperties`（1.20 版）+ `common/combat/gun/{ShootingService,GunFiringService,GunProjectileFactory}` + `util/ModGunUtils` + 4 个网络包 + `event/game/GunEvents`；`ItemEvents` 的 7 个 TE 形状处理器 → 1.20 的 6 个；93 处 `TGItems.`→`GunItems.` 改指；标签层补齐 4 个块。**详见 `notes/GUNS-G4P-SUBSET.md`** |
| **G5′** | 客户端（renderer 19 / network 3 / datagen 5）+ 主类 `TerraGuns` | 待做。**范围已按 G4′ 实测收窄**：只剩**客户端触发器**—— `client/handler/GunHandler`（`GunSounds.getSound` 消费点 + `ModKeyBindings.GUN_SHOOT`）、`client/renderer/entity/bullet/BulletRenderer`（`GunTrailColors.getColor` 消费点）、`BulletVfxManager`、`CameraAnimation`，以及 5 处 `TerraGuns`/`TGUtil`/`TGRenderTypes` 引用（`ModClientEvents`/`RainbowBoulderRenderer`/`ModelBakeryMixin`/`ModelManagerMixin`/`ModUtils`）。⚠️ **G4′ 之后枪还不能在游戏里开火**：服务端管线与网络层已就位，`ShootPacketC2S` 尚无发送方（那正是 `GunHandler` 的活） |
| **G6′** | 删 `TerraGuns` gitlink + `build.gradle` / `neoforge.mods.toml` 清理 + 全仓零 `org.confluence.terra_guns` 引用 | 待做。⚠️ 同批还要删 **TE 侧的过渡桥** `integration/terra_entity/TENpcCompat` 与全部调用点（TE 退役那一批）；以及 `TerraGuns` 子模块里的**同名旧实现**（`common/combat/{GunFiringService,GunProjectileFactory}`、`common/init/TG*`、`network/{c2s,s2c}/*` 等，G4′ 已按「先加后删」在主模组落好新形态） |

> **口径不变**：搬运仍以 **1.20 版为事实来源**，`TerraGuns` 侧同名类是**旧实现**（见 G0 的
> `check_duplicates` 结果：5 个文件里 1 个 `NEAR`、4 个 `DIFF`），G6′ 之前两边共存但主模组只用新类。

## 六、G1′ 落地记录（主模组前置）

| 文件 | 动作 | 依据（1.20 位置） |
|---|---|---|
| `common/item/gun/definition/GunDefinition` | **新增**（51 非空行） | 1.20 `common/item/gun/definition/GunDefinition.java`；`stage_batch --convert` 直出（`written={}` = 无需改写，纯 Java） |
| `common/init/ModDataComponentTypes` | 改（+8 行） | 1.20 `ModDataComponentTypes:27/28` 的 `gun_property` / `bullet_property` |
| `common/CommonConfigs` | 改（+4 行） | 1.20 `CommonConfigs:50/235` 的 `autoFireAllGuns`（默认 false，同值） |
| `common/init/ModTags` | 改（+2 行） | 1.20 `ModTags:454` 的 `Items.AUTOMATIC_GUN` |

两处实现细节（都写进了代码注释）：

1. **数据组件用组件自己的 `fastBuilder`**：1.20 是 `TYPES.builder("gun_property", GunPropertyComponent::fastBuilder)`；
   1.21 的 `registerComponentType` 形参是 `UnaryOperator<Builder<D>>`（要返回值），所以写成
   `builder -> { GunPropertyComponent.fastBuilder(builder); return builder; }` ——
   codec 装配仍留在组件类里（`fastBuilder` 在 1.21 侧本来就已存在、此前无人调用），不复制一遍
   `persistent(CODEC).networkSynchronized(STREAM_CODEC)`。
2. **`GunDefinition` 与 `BulletPropertyComponent` 的关系**：`GunDefinition.component()` 产出
   `GunPropertyComponent`，`BulletDefinition.component()` 产出 `BulletPropertyComponent` ——
   两个组件类 1.21 侧已有（WP6G），所以 `GunDefinition` 自包含；而 `BulletDefinition` 仍被
   `BulletBehavior` 拖住（见第三节，属 G3′）。

**本批没有消费点**：`GunDefinition` 要等 G2′ 的 `BaseGun` 才被用到（与 WP6G 的 8 个词汇类同一状态）。
主模组此刻仍然用 TerraGuns 的 `common.definition.GunDefinition`（`ManaGunItem`/`StarCannonItem` 两处），
按约束 3 这批不改指 —— 因为主模组的新 `BaseGun` 还没搬进来，改指无处可指。

**G2′ 开工清单（下一批，已核实前置齐备）**：
- `common/item/gun/BaseGun`（194 行）：需要 G0 的 `HandAnimationApi/Channel/Profile` ✅、
  `ModDataComponentTypes.GUN_PROPERTY` ✅、`CommonConfigs.AUTO_FIRE_ALL_GUNS` ✅、
  `ModTags.Items.AUTOMATIC_GUN` ✅、`LibClientUtils`（Magic-Lib 已有）✅ ——
  剩下要处理的只有 geckolib 4.8 包名（`core.animation.*` → `animation.*`、`core.object.PlayState`
  → `animation.PlayState`）与 `ForgeRegistries.SOUND_EVENTS` → `BuiltInRegistries.SOUND_EVENT`
- `api/event/GunEvent`（294 行）：闭包 {GunEvent, BaseGun, GunDefinition}，随 BaseGun 一起落
- 4 个旧形态枪械的取舍：`BeeGunItem`/`ManaGunItem`/`SpaceGunItem`/`StarCannonItem`
  （1.21 侧现存、继承 TerraGuns 的 BaseGun）—— 要按 1.20 版逐类 diff 后决定改造还是删除。

## 七、G2′ 落地记录 +「改指为什么做不了」（实测）

### 7.1 落地

| 文件 | 动作 | 说明 |
|---|---|---|
| `common/item/gun/BaseGun` | **新增**（193 行） | 1.20 逐字；`stage_batch --convert` 后只需 5 处 API 修正（见 7.2） |
| `api/event/GunEvent` | **新增**（292 行） | 1.20 逐字；`@Cancelable` 两处改 `ICancellableEvent` |
| `common/item/gun/StarCannonItem` | 只加 2 行注释 | 标出「改指暂缓」的原因与本节位置（**没有**改任何代码） |

转换报告：`uncovered=[]`、`leftovers` 空、（与 G1′ 合计）手工裁决 17 条。
编译门：第 1 次 **5 处错误** → 修完全部后 **0 错误**。

### 7.2 五处 API 修正（1.20 → 1.21.1，都实测）

| 位置 | 1.20 | 1.21 |
|---|---|---|
| `GunEvent:32/250` | `@Cancelable`（Forge 注解） | **注解已删** → `implements ICancellableEvent`（`net.neoforged.bus.api`，本仓库既有写法 `MinecartAbilityEvent.RightClickRailBlock`） |
| `BaseGun:97` | `appendHoverText(ItemStack, @Nullable Level, …)` | 第 2 参是 **`Item.TooltipContext`** |
| `BaseGun:116` | `ForgeRegistries.SOUND_EVENTS.getValue(key)` | `BuiltInRegistries.SOUND_EVENT.get(key)` |
| `BaseGun:185` | `onEntitySwing(ItemStack, LivingEntity)` | 两参版本自 21.1 起 `@Deprecated(forRemoval = true)` → 用三参 `onEntitySwing(ItemStack, LivingEntity, InteractionHand)`（TE 旧实现也是这么写的） |
| 其余 geckolib | `core.animation.*` / `core.object.PlayState` | `animation.*`（既有规则自动改写，6 处） |

### 7.3 ⚠️「改指」做不到，而且**不是**因为子弹层（比原计划更早的一堵墙）

我按约束 3 试着把 `StarCannonItem` 改指到主模组的新类（它只用到 `GunDefinition`+`BaseGun`，
且它引用的 `StarCannonBulletEntity` **主模组里本来就有**，看起来是最干净的改指点）。**编译门当场报 2 处错**：

```
ItemEvents.java:125  incompatible types: BaseGun cannot be converted to StarCannonItem
    >>> if (event.getGun() instanceof StarCannonItem starCannonItem) {
ModGunProperties.java:13  no suitable method found for putSound(DeferredItem<StarCannonItem>, DeferredHolder<SoundEvent,SoundEvent>)
    >>> TGGunSounds.putSound(GunItems.STAR_CANNON, ModSoundEvents.STAR);
```

两个错误各自指向一堵墙：

1. **`ItemEvents` 绑的是 TerraGuns 的 `GunEvent`**，而两边的**嵌套事件名不同**：
   TE 侧是 `GunFireEvent` / `UseGunEvent` / `ProjectileCreationEvent` / `ShrinkBulletEvent` /
   `AmmoDataEvent` / `AmmoSelectionEvent` / `InventoryExtraEvent`；1.20（现已落地）是
   `Use` / `ShrinkBullet` / … —— 也就是说 `ItemEvents` 的 7 个枪械处理器**要按 1.20 的事件形状重写**，
   不是换个 import 就行（`event.getGun()` 的返回类型也跟着从 TE 的 `BaseGun` 变成主模组的 `BaseGun`）。
2. **`ModGunProperties` 绑的是 TerraGuns 的 `TGGunSounds`**：`putSound` 吃的是 TE 的枪械物品类型，
   主模组的 `StarCannonItem` 换基类后就不再匹配 → 这一处属于 **G4′ 注册层**（`TGGunSounds`/`TGTrailColors`
   要一起搬进来）。

再加上 `ManaGunItem`/`BeeGunItem`/`SpaceGunItem` 需要弹幕基类（TE 的 `BaseBulletEntity`
≠ 1.20 主模组的 `common/entity/projectile/BaseBulletEntity`，后者在 G3′ 的 156 文件闭包里）——
**结论：4 个枪械物品的改指必须与「`ItemEvents` 重写 + `TGGunSounds`/`TGTrailColors` 搬迁」同批做**，
单独改指任何一个都会留下「一半用新基类、一半用旧基类」的破碎中间态（那正是纪律禁止的）。

因此本批**主动回滚**了那次改指试探，只留一条指向本节的注释。

### 7.4 修正后的 G3′ 定义（顺序再往后挪一格）

G3′ 现在不是「弹幕层」而应是 **「枪械消费侧整批」**：
`ItemEvents` 的 7 个处理器按 1.20 事件形状重写 + `ModGunProperties` + `TGGunSounds`/`TGTrailColors`
（= G4′ 的一半）+ 4 个枪械物品的改指；其中 `ManaGunItem` 系仍需 G3′ 的弹幕基类，
而弹幕基类仍被 **WP4 NPC 基座**与 `GunItems` 注册层挡住。
**→ 枪械内联迁移在 G2′ 之后的前途，实际上取决于 WP4 是否先做。**
