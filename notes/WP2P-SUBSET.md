# WP2 批次 15（补件）+ 坐骑簇的推迟记录

> 本文件记两件事：批次 15 的两个补件，以及**坐骑簇为什么第 3 次被推迟**（附逐文件错误清单，
> 供下一批直接照单开工）。

## 一、批次 15 补件（2 个 / 93 非空行）

| 文件 | 行数 | 说明 |
|---|---:|---|
| `common/entity/projectile/mana/NPCShadowflameSkullProjectile` | 26 | 批次 13 推迟的最后一个「自包含枢纽」小件 |
| `common/entity/projectile/ProjectileHitRules` | 67 | 随坐骑簇闭包带出来、本身自包含，转换器直出 |

`NPCShadowflameSkullProjectile` 的唯一错误是**不变泛型**：
1.21 侧 `SkullProjectile(EntityType<SkullProjectile>, Level)`（`SkullProjectile.java:30`）
把 1.20 的 `EntityType<? extends SkullProjectile>`（1.20 `SkullProjectile.java:31`）收窄了，
子类传 `EntityType<? extends NPCShadowflameSkullProjectile>` 就编不过。
**修法是把 1.21 侧放宽回 1.20 的写法**（一行）：放宽后它仍是合法的
`EntityType.EntityFactory<SkullProjectile>` 实现，注册处不需要改。
这属于「1.21 侧比 1.20 旧/窄」的第 N 处 —— 遇到 override/泛型不匹配时，先怀疑 1.21 侧那个基类是不是被收窄过。

## 二、坐骑簇第 3 次推迟：这次量到了逐文件错误数

闭包（9 个文件 / 1065 非空行，`dep_subset` 显示 9/9 零扩张）：

```
common/entity/mount/AbstractMountEntity(307)   RideableBeeMountEntity   RideableLavaSharkMountEntity
RideableSlimeMountEntity   RideableUnicornMountEntity
common/item/mount/MountItem(52)   common/mount/MountManager(89)   common/init/item/MountItems(27)
common/entity/projectile/ProjectileHitRules(67)   ← 只这个能直接落，已随批次 15 落地
```

编译 24 处错误，分布如下（下一批按此开工即可）：

| 文件 | 错误 | 具体 |
|---|---:|---|
| `AbstractMountEntity` | 5 | 49/172 符号缺失；296/352 覆写签名不匹配；354 `AbstractMountEntity` → `RegistryFriendlyByteBuf` 类型不匹配（1.21 的实体序列化/网络 API 变了） |
| `RideableBeeMountEntity` | 5 | 61-64 符号缺失；71 `package ForgeMod does not exist`（应走 `NeoForgeMod`，1.20 的 Forge 常量在 1.21 侧换了归属） |
| `MountItems` | 5 | 24 `PortRegisterHandler.item(...)` 未转换（PortLib）；27-30 引用 `ModEntities.RIDEABLE_{SLIME,BEE,UNICORN,LAVA_SHARK}` —— 这 4 个成员在 1.21 **不存在** |
| `RideableSlimeMountEntity` | 3 | 59/126/127 符号缺失 |
| `RideableUnicornMountEntity` | 2 | 63/64 符号缺失 |
| `MountItem` | 2 | 54 覆写签名不匹配；57 符号缺失 |
| `MountManager` | 2 | 同上类 |

**要先做的 3 件事**（顺序有依赖）：
1. 给 `ModEntities` 补 4 条 `RIDEABLE_*` 注册（1.20 `ModEntities.java:622/624/626/635`；
   `RIDEABLE_SLIME`/`RIDEABLE_BEE` 是多行注册，尺寸见原文；四条都带
   `.clientTrackingRange(8).updateInterval(1).noSummon().noSave()`）。
   注意 `EntityType` 目标类是**坐骑包里的 4 个业已搬入的类**，所以注册与类必须同批。
2. `MountItems.ITEMS` 改成 `DeferredRegister.createItems(Confluence.MODID)`，
   并挂到既有集中注册点 `ModItems.register(eventBus)`（`ModItems.java:159-164` 的写法）。
3. `AbstractMountEntity` / `MountItem` 的覆写与序列化按 1.21 API 改写（先 diff 1.21 侧同类实现）。

第 1 条是**成员级盲区的第 6 次现身**：`dep_subset` 报「9/9 零扩张」，
但 `MountItems` 经 `ModEntities.RIDEABLE_*` 访问的 4 个成员一个都不存在 ——
**注册层的类建出来之后，成员必须单独核对**（与批次 10 记的是同一条教训）。

## 三、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

本批只落 3 个文件（2 新增 + `SkullProjectile` 放宽泛型），坐骑簇 8 个文件已从工作树移除，
没有留半成品。工作树与子模块都干净。

## 四、坐骑簇第 4 次尝试：把「开工前必须先补的东西」全部查实（第 12 轮）

第 12 轮实际动手做坐骑簇：先把 `ModEntities` 的 4 条 `RIDEABLE_*` 注册写进去 →
**`MountItems` 的 5 处错误立刻消失**（说明注册文本正确），但其余文件仍有 20 处错误，
根因是**四个共享类缺成员**，逐个查实如下（都是 1.21 侧确实没有）：

| 缺的成员 | 使用者 | 说明 |
|---|---|---|
| `PrefixUtils.attributeWithoutHeldItem(Player, Holder<Attribute>, ItemStack)` | `RideableUnicornMountEntity:63`、`RideableSlimeMountEntity:126` | 1.21 的 `util/PrefixUtils.java` 只有 `canInit/couldReforge/initPrefix/best/getPrefixType`，没有这个方法 |
| `ExtraInventory.getMount(boolean)` | `MountManager:39,84` | `common/attachment/ExtraInventory.java` 存在但无此方法（坐骑专用额外槽位） |
| `ModKeyBindings.MOUNT` | `MountItem:57` | `client/ModKeyBindings.java` 里没有坐骑键位 |
| `LibDamageTypes.SUMMONER` | `RideableUnicornMountEntity:64`、`RideableSlimeMountEntity:127` | 子模块 `Confluence-Magic-Lib` 的 `LibDamageTypes` 没有（做法同 `FROST_BURN`/`DUNGEON_GUARDIAN`，2 行 + 子模块提交） |

### 四处 API 差异（本轮实测，下次直接用）

1. **`Entity#getPassengersRidingOffset()` → `getPassengerAttachmentPoint(Entity, EntityDimensions, float)`，返回 `Vec3`**
   （`Entity.java:2085`；旧名全 jar 无命中）。1.20 的 `return getBbHeight() * 0.75 + 0.2;`
   应写成 `return new Vec3(0.0, getBbHeight() * 0.75 + 0.2, 0.0);`。
2. **`getAddEntityPacket()` → `getAddEntityPacket(ServerEntity)`**（`Entity.java:3428`），
   包体也变：`new ClientboundAddEntityPacket(this)` → `new ClientboundAddEntityPacket(this, serverEntity)`。
3. **`Holder<Attribute>#getDefaultValue()` 不存在**（1.20 是 Forge 扩展）。1.21 用
   `((RangedAttribute) Attributes.JUMP_STRENGTH.value()).getDefaultValue()`
   （`import net.minecraft.world.entity.ai.attributes.RangedAttribute;`）。
4. **`ForgeMod.EMPTY_TYPE.get()` → `NeoForgeMod.EMPTY_TYPE.value()`**
   （既有写法见 `BaseAquaticMonster.java:96`、`SandShark.java:123`）；
   同类的 `setMaxUpStep(1.0F)` → `Attributes.STEP_HEIGHT`（见批次 14 的 `DeerClops`）。

### ⚠️ 新发现的转换器**误报**：`vanilla-entitydata-define-builder` 会造出不存在的 `builder`

`RideableBeeMountEntity` 的 4 处 `builder.define(FLIGHT_ENERGY, …)` 全报「cannot find symbol」。
根因不是漏改而是**改错**：1.20 侧这里的方法是 `protected void defineMountSynchedData()`
（**没有形参**，`builder` 来自父类字段），而规则是按 1.21 的
`defineSynchedData(SynchedEntityData.Builder builder)`（有同名形参）写的，
于是把 `entityData.define(` 无脑换成 `builder.define(`，在该作用域里 `builder` 并不存在。
**以后遇到这类报错先看方法签名里有没有 `builder` 形参**；坐骑簇落地时要按 1.20 原文还原这一段。

### 本轮动作与结果

4 条 `RIDEABLE_*` 注册与 `AbstractMountEntity` 的 4 处改写都已在暂存区试过、方向已验证，
但 20 处错误里剩下一半要靠上表那四个共享类新增成员才能收口，**故本轮没有落地坐骑簇**：
为避免留半成品，暂存的 8 个文件已从工作树移除、`ModEntities` 的改动已回滚，
编译回到 0 错误、工作树干净。下一批按「先补上表 4 个成员 → 再落坐骑簇 8 个文件」的顺序做即可。

