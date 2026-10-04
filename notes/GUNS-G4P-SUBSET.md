# 枪械内联 G4′（注册层 + 服务端管线 + 网络层）：落地记录

> **枪械内联迁移**（`notes/GUNS-INLINE-MIGRATION.md`）的第 4 批校正版 `G4′`。
> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**、`Confluence-Magic-Lib` **0 错误 / 0 文件**；
> PortLib 残留 **0 处**。**22 改 + 13 新增**（含子模块 1 文件）。

## 一、本批为什么比 G4 原本的设想大

`notes/GUNS-INLINE-MIGRATION.md` 第七节 7.4 已经把 G3′ 重定义成「枪械消费侧整批」，
并预告了三堵墙：`ItemEvents` 的 7 个处理器、`ModGunProperties` 绑 TE 的 `TGGunSounds`、
4 个枪械物品的改指。本次实测又量出**第四堵墙**：**服务端开火管线**。

```powershell
# 1.20 的服务端开火管线闭包 —— 5 个文件，自包含
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed org.confluence.mod.common.combat.gun.ShootingService `
  --seed org.confluence.mod.common.combat.gun.GunFiringService `
  --seed org.confluence.mod.common.combat.gun.GunProjectileFactory `
  --seed org.confluence.mod.util.ModGunUtils `
  --seed org.confluence.mod.network.s2c.ShotFeedbackPacketS2C
# {"candidates": 5, "kept": 5, "new": 5, "removed": 0, "unused_defer": 0}
```

这 5 个类里 `common/combat/gun/**` 在 **`common`**（不是客户端）：它们才是**真正扣扳机**的地方，
`ShootingService` 是「服务端权威入口」，`GunFiringService` 算弹道、`GunProjectileFactory` 造弹幕。
不搬它们，G4′ 之后主模组的枪「有物品、有事件、没有开火逻辑」，是一段**纯死代码 + 真回归**
（改指后 4 把枪已不再走 TE 的 `BaseGun` 管线）。所以本批把它们一起收了口。

随后 C2S 网络层（`ShootPacketC2S` + `InspectPacketC2S`，闭包 2 文件 / 55 行）也一并落地 ——
它们正是 `ShootingService.tryShoot` 的唯一入口。

**本批边界（G5′ 剩下的东西）**：客户端。`client/handler/GunHandler`（`GunSounds.getSound` 的消费点、
按键 `ModKeyBindings.GUN_SHOOT`、`BulletVfxManager`、`CameraAnimation`）、
`client/renderer/entity/bullet/BulletRenderer`（`GunTrailColors.getColor` 的消费点）、
以及 `terrain`/模型/配方/`TerraGuns` 主类（`ModelBakeryMixin`/`ModelManagerMixin`/`ModUtils` 里的
`TerraGuns.MODID`、`ModClientEvents` 的 `TGUtil`、`RainbowBoulderRenderer` 的 `TGRenderTypes`）。
**结论：G4′ 之后，枪的「服务端 + 数据 + 网络」全在，缺的是客户端触发器** ——
`ShootPacketC2S` 当前**没有发送方**（1.20 的发送方是 `GunHandler`），这是**有意留的接线口**，不是漏项。

## 二、本批内容（22 改 / 13 新增）

### 2.1 新增（13）

| 文件 | 行数 | 来源（1.20） |
|---|---:|---|
| `common/init/gun/GunSounds` | 47 | `common/init/gun/GunSounds.java` 逐字 |
| `common/init/gun/GunTrailColors` | 76 | `common/init/gun/GunTrailColors.java` 逐字 |
| `common/event/game/GunEvents` | 57 | `common/event/game/GunEvents.java` 逐字（2 个处理器） |
| `common/combat/gun/ShootingService` | 46 | 同名文件逐字 |
| `common/combat/gun/GunFiringService` | 47 | 同名文件逐字 |
| `common/combat/gun/GunProjectileFactory` | 76 | 同名文件逐字 |
| `util/ModGunUtils` | 69 | 同名文件逐字 |
| `network/s2c/ShotFeedbackPacketS2C` | 33 | 同名文件逐字 |
| `network/s2c/BulletImpactPacketS2C` | 46 | 同名文件逐字 |
| `network/c2s/ShootPacketC2S` | 34 | 同名文件逐字 |
| `network/c2s/InspectPacketC2S` | 33 | 同名文件逐字 |
| Magic-Lib `LibMathUtils.criticalDamageTotal`（改，见 2.3） | — | 1.20 子模块同名方法 |

### 2.2 重写/改指（22 改）

| 类别 | 文件 | 要点 |
|---|---|---|
| **注册层** | `init/item/GunItems` | 1.20 整层：**13 把枪 + 19 种子弹** + `GUN_ITEMS`/`BULLET_ITEMS` 两个列表（1.20 `:30/31`，消费点 `ModTabs`） |
| | `init/ModGunProperties` | 1.20 版：`GunSounds.putSound(BEE_GUN/SPACE_GUN/STAR_CANNON)` + `GunTrailColors.putColor("space_gun", …)` |
| | `init/ModTags` | 补 `Items.{MANUAL_GUN, SEED_AMMO, SNOW_AMMO}`（1.20 `:453/455/456`，**标签层补全**） |
| **事件** | `event/game/entity/ItemEvents` | 7 个 TE 形状处理器 → 1.20 的 **6 个**（`GunEvent.{Fire,Use,ShrinkBullet,AmmoData,AmmoSelection,InventoryExtra}`）；第 7 个（ProjectileCreation）**移出**到 `GunEvents` |
| **物品** | `item/gun/ManaGunItem`、`StarCannonItem` | 改指主模组 `GunDefinition`/`BaseBulletEntity`；删同包 `BaseGun` import；删 G2′ 的「暂缓改指」注释 |
| | `entity/projectile/StarCannonBulletEntity` | 删两条 TE import（`BaseBulletEntity`/`CustomBulletEntity` **与它同包**） |
| | `item/gun/{BeeGunItem,SpaceGunItem}` | **逐字核对后无需改动**（本来就没有 TE 引用） |
| **接线** | `event/ModEvents` | `GunSounds.init()` + `GunTrailColors.init()`（1.20 `ModEvents:100-101`，紧跟 `ModGunProperties.init()`） |
| | `event/NetworkEvents` | 注册 2 个 S2C + 2 个 C2S 包（1.20 靠 PortLib 自动登记，1.21 必须显式） |
| **消费侧改指** | `block/{common/BasePotBlock, natural/CrimsonHeartBlock, natural/ShadowOrbBlock}`、`data/gen/{NPCShopProvider, data_map/ValueSubProvider, loot/ChestSubProvider, loot/GiftSubProvider, recipe/HardmodeAnvilRecipeProvider, recipe/HeavyWorkBenchProvider, recipe/ModRecipeProvider, tag/ModItemTagsProvider}`、`init/ModTabs` | **93 处** `TGItems.` → `GunItems.`、`TGTags.` → `ModTags.Items.`（12 个 TE import 清零） |
| **成员级补全** | `util/PrefixUtils` | 补 `calculateUseTime(Player,int)`（1.20 `PrefixUtils:117`，`gun$Use` 要用） |

### 2.3 本批新发现的 3 个「成员级盲区」

| 成员 | 1.20 位置 | 消费点 | 宿主 |
|---|---|---|---|
| `LibMathUtils.criticalDamageTotal(float,float,RandomSource)` | Magic-Lib `LibMathUtils`（`checkChance` 之后） | `GunFiringService:39`（1.20 `:37`） | **Confluence-Magic-Lib**（子模块，提交 `??`） |
| `PrefixUtils.calculateUseTime(Player,int)` | `PrefixUtils.java:117` | `ItemEvents#gun$Use`（1.20 `:130`） | 主模组 |
| `ModTags.Items.{MANUAL_GUN,SEED_AMMO,SNOW_AMMO}` | `ModTags:453/455/456` | `ModItemTagsProvider` 的 4 个 tag 块 | 主模组 |

**又一次印证**：`dep_subset` 只看类型级边，「类存在」≠「成员存在」；这三个都是编译到才炸出来的。

### 2.4 标签层补全（Fork 报的缺口，本批补上）

1.21 侧此前**整段缺失** 1.20 的枪械 tag 块（1.20 `ModItemTagsProvider:1483-1507`）：
`AUTOMATIC_GUN` 少列 4 把枪、`MANUAL_GUN`/`BULLET`/`SNOW_AMMO`/`SEED_AMMO` 四个块 + `AMMO` 汇总全无。
本批补齐，`BULLET` 块用 `GunItems.BULLET_ITEMS.forEach(...)`（1.20 原文），
`SEED_AMMO` 的 `PortTags.Items.SEEDS`（PortLib 对 `c:seeds` 的转发）→ 原生 `Tags.Items.SEEDS`。

**两行按 1.20 有意删除**（不是漏改，注释已写在原地）：
- `.addOptionalTag(TGTags.AMMO)`（挂在 `tag(ModTags.Items.AMMO)` 上）：`TGTags.AMMO` 的直接替换物
  `ModTags.Items.AMMO` **就是被追加的那个 tag** → 会变成自引用；1.20 也没有这句。
- `tag(TGTags.AMMO).add(FALLING_STAR)`：陨星已在上方 AMMO 块里，而 `TagProvider.TagAppender`
  走 `TagBuilder.addElement` **不去重**，替换后生成的 JSON 会把 `confluence:falling_star` 列两遍。

## 三、平台/API 差异（1.20 → 1.21，都实测）

| 位置 | 1.20 | 1.21 |
|---|---|---|
| `PortEventHandler.addListener(...)` | PortLib 总线助手 | `NeoForge.EVENT_BUS.addListener(...)`；`GunEvents` 改成 `@EventBusSubscriber` + `@SubscribeEvent`（1.21 房内写法，故**没有** `init()`，也不需要 `Confluence` 调用点 —— 1.20 在 `Confluence:80` 调 `GunEvents.init()`） |
| `PortEventHandler.postEvent(x)` | PortLib | `NeoForge.EVENT_BUS.post(x)` —— **转换器只转了一部分**（8 处残留，与批次 25 同一个缺陷），5 个管线文件逐个手改 |
| `IPortPacket.{S2C,C2S}` + `ResourceLocation ID` + `identifier()` | PortLib | `IPacketS2C`/`IPacketC2S` + `Confluence.createType("…")` + `type()`（4 个新包） |
| `PortStreamCodec` / `PortByteBufCodecs` | PortLib | 原生 `StreamCodec` / `ByteBufCodecs`（缓冲区 `RegistryFriendlyByteBuf`） |
| `PortPacketDistributor.sendToPlayersNear(ResourceKey<Level>, …)` | PortLib | `PacketDistributor.sendToPlayersNear(ServerLevel, excluded, …)` |
| `PortDeferredItem` / `PortItemRegistration` | PortLib | `DeferredItem` / `DeferredRegister.Items`（`DeferredRegister.createItems`） |
| geckolib `software.bernie.geckolib.core.animation.Animation` | 1.20 包名 | `software.bernie.geckolib.animation.Animation`（4.8 无 `core.*`） |
| `player.getRandom1211()` | PortLib 别名 | `player.getRandom()`（转换器规则已覆盖 2 处） |

**证据来源（1.20 vs TE 的数值差异，以 1.20 为准）**：TE 的 `TGItems` 是**旧值** ——
`CURSED_BULLET` 伤害 4.0（TE 4.5）、`NANO_BULLET` 5.5（TE 5.0）、`PARTY_BULLET` 多一个 impact effect、
`ENDLESS_MUSKET_POUCH` 用 `registerBullet(name, 1, …)`；枪的 `HandAnimationProfile` 1.20 用
`HandAnimationProfile.builder()...` 逐通道写（TE 用 `HandAnimationProfile.handgun()`）。

## 四、闸门与校验

| 项 | 结果 |
|---|---|
| `check_duplicates.py`（13 个新文件） | **0 处「疑似移动/重复」**；8 处 DIFF 全是 TerraGuns 同名旧实现（既定**先加后删**：旧实现随 G6′ 删 gitlink 一起消失） |
| `dep_subset.py` 闭包 | 管线 5 文件自包含、网络 2 文件自包含（无 `--defer` 消耗） |
| `stage_batch --convert --apply` | `uncovered=[]`；`leftovers` 只有 8 处 `PortEventHandler.postEvent`（已手改） |
| `build_errors.py --maxerrs 2000`（主模组） | **0 错误 / 0 文件** |
| `build_errors.py`（Magic-Lib） | **0 错误 / 0 文件** |
| PortLib 残留全树扫描 | **0 处** |
| `org.confluence.terra_guns` 残留 | **5 处，全在 G5′ 边界**（`ModClientEvents`/`RainbowBoulderRenderer`/`ModelBakeryMixin`/`ModelManagerMixin`/`ModUtils`） |

## 五、推迟与偏差（逐条写明归属）

1. **客户端（G5′）**：`client/handler/GunHandler`、`client/renderer/entity/bullet/BulletRenderer`、
   `BulletVfxManager`、`ModKeyBindings.GUN_SHOOT`、`TerraGuns` 主类相关 5 处引用。
   → **G4′ 之后枪还不能在游戏里开火**：服务端管线与网络层已就位，但客户端触发器未落。
   这是**有意的接线口**（`ShootPacketC2S` 尚无发送方），不是漏项。
2. **TE 侧同名旧实现**：`terra_guns.common.combat.{GunFiringService,GunProjectileFactory}`、
   `terra_guns.network.{c2s,s2c}.*`、`terra_guns.common.init.*` 等仍在子模块里
   → **G6′**（删 gitlink）时一并删。**不许**说成「1.21 侧此前不存在」。
3. **`ModDamageTypes` vs `LibDamageTypes`**（`ManaGunItem#getDamageSource`）：1.20 用 Magic-Lib 的
   `LibDamageTypes`，1.21 用主模组 `ModDamageTypes` —— 属**伤害类型层**的另一条线，不在枪械内联范围。
4. **datagen 产物过期**：`ConfluenceOtherworld/src/generated/**` 里仍有 `#terra_guns:gun` /
   `#terra_guns:ammo` 的旧标签 JSON（以及 `build/**` 下的副本）→ 需 `runData` 重新生成（WP7），
   **不要手改生成物**。
5. **`GunItems` 与 TE `TGItems` 并存**：两套物品在不同命名空间（`confluence:*` vs `terra_guns:*`），
   不冲突；TE 那套随 G6′ 消失。
6. **行为面未验收**：开火/冷却/弹药消耗/暴击倍率（`criticalDamageTotal` 的 ×1.5）只在编译门层面成立，
   实机验收要等 G5′。
