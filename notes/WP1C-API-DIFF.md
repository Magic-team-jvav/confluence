# 1c-a · 1.21 实体 API 差异对照表（v2：只比参数类型 + 继承闭包）

由 `tools/port2native/entity_api_diff.py` 生成，可重跑（命令见文末）。

## 口径：v2 相对首版修正的三处缺陷

| # | 首版口径 | 后果 | v2 修正 |
|---|---|---|---|
| 1 | 参数按「类型+形参名」整体文本比较 | 1.21 反编译源码的形参名常是 `p_xxxxx_`，`remove(RemovalReason reason)` / `readAdditionalSaveData(CompoundTag tag)` 等 26 行误报「参数变了」 | **只比参数类型**，丢掉形参名 |
| 2 | 用正则抓 `@Override` 后的方法，不区分嵌套层级 | 匿名内部类里的方法被算成目标类的覆写点（`BaseMonster` 的 `createTree()` / `execute()` 其实在 `new BTNode(){...}` 里） | **只认类体层级（大括号深度 1）成员**，嵌套块整体跳过 |
| 3 | 只在 `net.minecraft.*` 反编译源码里找方法 | `PartHitTarget` / `org.confluence.lib.api.entity.Boss` / geckolib `GeoEntity` 声明的接口方法被误判成「1.21 无此方法」 | **在继承闭包里找**，闭包并入 1.21 工程源码 + geckolib sources |

判定：`OK` 覆写点成立；`SIG_CHANGED` 同名但参数类型表对不上；`REMOVED` 闭包完整而整条链无此方法名；`UNRESOLVED` 闭包不完整、结论不可信。

> ⚠️ **`REMOVED` 只表示「这个名字在 1.21 不存在」，不等于「被删除」** —— 也可能是**改名**。本表里的 `onAddedToWorld` 就是改名成 `onAddedToLevel`（`Entity.java:3733`），当初按「删除」处理，把逻辑挪进 `tick()` 的「只跑一次」守卫，结果丢掉了 `super` 里的 `isAddedToLevel = true`。**每个 `REMOVED` 都要先按「会不会是改名」查一遍。**

`声明于` 列给出 1.21 侧命中的类，`来源` 列给出它所在层（原版/NeoForge、工程源码、第三方库）。

## 汇总

| 文件 | 覆写点 | OK | SIG_CHANGED | REMOVED | UNRESOLVED | 闭包类型数 |
|---|---|---|---|---|---|---|
| `BaseBossPart` | 21 | 18 | 3 | 0 | 0 | 15 |
| `BaseLivingBossPart` | 19 | 18 | 1 | 0 | 0 | 24 |
| `BossChildDeathLedger` | 1 | 0 | 1 | 0 | 0 | 1 |
| `MechanicalMayhemTracker` | 0 | 0 | 0 | 0 | 0 | 0 |
| `BaseBoss` | 31 | 31 | 0 | 0 | 0 | 25 |
| `BossOwnedEntity` | 0 | 0 | 0 | 0 | 0 | 0 |
| `BossChunkTicket` | 0 | 0 | 0 | 0 | 0 | 0 |
| `EnemyTargeting` | 0 | 0 | 0 | 0 | 0 | 0 |
| `BaseMonster` | 11 | 10 | 0 | 1 | 0 | 22 |
| **合计** | **83** | **77** | **5** | **1** | **0** | — |

## v1 → v2 收敛对照

首版（提交 `69cee922c` 的本文旧版）把 86 个覆写点里的 44 个判为「需处理」。按上表逐条复核后，**44 里有 38 个是口径缺陷造成的假阳性，真实需要动手的只有 6 个**。

> 附带记录：旧版正文写「需处理 44」时的分项是「`参数变了` 26、`1.21 无此方法` 18」，但用脚本数它自己的表，实际是 **23 + 21 = 44** —— 旧版的正文数字与旧版的表也对不上。

### 旧判「参数变了」的 23 行

| 归因 | 条数 | 明细 |
|---|---|---|
| 假阳性：只有形参名不同 | 15 | `readAdditionalSaveData`×3、`addAdditionalSaveData`×3、`startSeenByPlayer`、`stopSeenByPlayer`、`setDeltaMovement`、`handleEntityEvent`、`die`、`setTarget`、`getHurtSound`、`tickHeadTurn`×2 |
| 假阳性：嵌套类限定前缀 | 3 | `remove(RemovalReason)`×3 —— 1.21 反编译写成 `Entity.RemovalReason` |
| **真差异** | 5 | `defineSynchedData`×2、`getAddEntityPacket`、`lerpTo`、`save` |

### 旧判「1.21 无此方法」的 21 行

| 归因 | 条数 | 明细 |
|---|---|---|
| 工程接口声明，1.21 侧本来就有 | 11 | `PartHitTarget` 的 `damageRecipient`/`encounterOwner`/`dedupeIdentity`/`acceptsDirectHit`（两处各 4）+ `Boss` 的 `shouldShowMessage`/`isMainBody`/`shouldEnhanceMultiplayer`（`Confluence-Magic-Lib`） |
| 同批待移植的 `BaseMonster` 默认钩子 | 4 | `onCreatureDefinitionReload`、`hasEntityContactAttack`、`contactAttackInterval`、`contactAttackInflation` |
| 匿名内部类误收，本就不是该类的类体成员 | 3 | `createTree`、`execute`（在 `new BTNode(){...}` 里）、`createPathFinder`（在 `createNavigation` 返回的 `new GroundPathNavigation(...){...}` 里）|
| geckolib 接口 | 2 | `getAnimatableInstanceCache`、`registerControllers`（`GeoAnimatable`） |
| **改名**（不是删除） | 1 | `Entity#onAddedToWorld()` → `Entity#onAddedToLevel()` |

覆写点总数 86 → 83，差额 3 正是上表第三类（匿名内部类误收）。

### 被这一轮推翻的旧结论（含后来才查清的一条）

1. **`lerpTo` 的方向反了。** 旧结论写「1.21 多一个插值参」，实测正相反：**1.20.1 Forge 比 1.21.1 NeoForge 多一个布尔参**。已直接核对两侧 jar —— `forge-1.20.1-47.4.20-sources.jar` / `net/minecraft/world/entity/Entity.java:2115` 是 `lerpTo(double,double,double,float,float,int,boolean teleport)`（Forge 自己补的 `teleport`），而 `neoforge-21.1.219-sources.jar` 同文件 `:2202` 是 `lerpTo(double,double,double,float,float,int)`（NeoForge 没带这个补丁，它到 1.21.2 才回原版）。所以移植方向是**删**参数，不是加。
2. **`tickHeadTurn` 不是差异。** 旧结论写「参数表不同」，实测两侧都是 `(float, float)`，与 `Mob#tickHeadTurn` 一致，`BaseBoss` / `BaseLivingBossPart` 两处都无需改动。
3. **`onAddedToWorld` 不是「被删除」，是改名成 `onAddedToLevel`。** 本表的 `REMOVED` 判定只说明「这个名字在 1.21 不存在」。当初据「1.21 的 `Entity.java` / `Level.java` 里 `onAddedToWorld` 出现 0 次」就下了删除的结论，把 4 个类的逻辑挪进 `tick()` 的「只跑一次」守卫；后来查清 1.21.1 是 `public void onAddedToLevel() { this.isAddedToLevel = true; }`（`Entity.java:3733`，调用点 `ServerLevel.java:933/:945`、`ClientLevel.java:355`、`PersistentEntitySectionManager.java:115/:122/:248`），**误判导致丢掉了 `super` 里置 `isAddedToLevel` 的副作用**。1.21 分支原本已有 6 个文件（`SwordProjectile`/`BeeKeeperProjectile`/`SporeCloudProjectile`/`SpearProjectile`/`NorthPoleSubProjectile`/`BaseArrowEntity`）正确地覆写它。已固化成规则 `vanilla-onaddedtoworld-rename`（1.20 侧还有 36 处使用）。

## 1c 的可执行改动清单（SIG_CHANGED + REMOVED 的全部）

除下表之外，**其余覆写点在 1.21 侧签名一致，移植时不需要任何 API 手术**。

| 文件 | 方法 | 1.20 参数 | 1.21 参数 | 处理 |
|---|---|---|---|---|
| `BaseBossPart` | `lerpTo` | `double`, `double`, `double`, `float`, `float`, `int`, `boolean` | `double`, `double`, `double`, `float`, `float`, `int` | 1.21.1 是六参（无 `boolean teleport`）；删掉第七个形参，把「是否瞬移」的判断改由调用方/`steps <= 0` 决定 |
| `BaseBossPart` | `defineSynchedData` | — | `SynchedEntityData.Builder` | 改成 `protected void defineSynchedData(SynchedEntityData.Builder builder)`，把 `entityData.define(...)` 换成 `builder.define(...)`，并调用 `super.defineSynchedData(builder)` |
| `BaseBossPart` | `getAddEntityPacket` | — | `ServerEntity` | 改成 `public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity)`，`new ClientboundAddEntityPacket(this, entity)` |
| `BaseLivingBossPart` | `defineSynchedData` | — | `SynchedEntityData.Builder` | 改成 `protected void defineSynchedData(SynchedEntityData.Builder builder)`，把 `entityData.define(...)` 换成 `builder.define(...)`，并调用 `super.defineSynchedData(builder)` |
| `BossChildDeathLedger` | `save` | `CompoundTag` | `CompoundTag`, `HolderLookup.Provider` | 改成 `public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries)` |
| `BaseMonster` | `onAddedToWorld` | — | — | **是改名，不是删除**：1.21.1 叫 `onAddedToLevel()`（`Entity.java:3733`，调用点 `ServerLevel.java:933/:945` 等），整体改名即可，**必须保留 `super.onAddedToLevel()`** —— 它会置 `isAddedToLevel`。已固化成规则 `vanilla-onaddedtoworld-rename`。（反向教训：一度误判成「1.21 删了这个钩子」，把逻辑挪进 `tick()` 的「只跑一次」守卫里，结果丢掉了 super 的副作用。） |

## BaseBossPart

### `defineSynchedData` — **SIG_CHANGED**

- 1.20 参数：—
- 1.21 参数：`SynchedEntityData.Builder`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge
- 备注：闭包内候选: Entity(SynchedEntityData.Builder)

### `getAddEntityPacket` — **SIG_CHANGED**

- 1.20 参数：—
- 1.21 参数：`ServerEntity`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge
- 备注：闭包内候选: Entity(ServerEntity)

### `lerpTo` — **SIG_CHANGED**

- 1.20 参数：`double`, `double`, `double`, `float`, `float`, `int`, `boolean`
- 1.21 参数：`double`, `double`, `double`, `float`, `float`, `int`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge
- 备注：闭包内候选: Entity(double, double, double, float, float, int)

### `acceptsDirectHit` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `addAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `canBeCollidedWith` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `canBeHitByProjectile` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `damageRecipient` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `dedupeIdentity` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `encounterOwner` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `getDimensions` — **OK**

- 1.20 参数：`Pose`
- 1.21 参数：`Pose`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `is` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `isAttackable` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `isPickable` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `isPushable` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `push` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `readAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `remove` — **OK**

- 1.20 参数：`RemovalReason`
- 1.21 参数：`Entity.RemovalReason`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge
- 备注：类型放宽后一致（嵌套类限定前缀 / 类型变量 / 泛型实参）

### `setPos` — **OK**

- 1.20 参数：`double`, `double`, `double`
- 1.21 参数：`double`, `double`, `double`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `shouldBeSaved` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `tick` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

## BaseLivingBossPart

### `defineSynchedData` — **SIG_CHANGED**

- 1.20 参数：—
- 1.21 参数：`SynchedEntityData.Builder`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge
- 备注：闭包内候选: Mob(SynchedEntityData.Builder) ; LivingEntity(SynchedEntityData.Builder) ; Entity(SynchedEntityData.Builder)

### `acceptsDirectHit` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `addAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `damageRecipient` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `dedupeIdentity` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `encounterOwner` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `getDimensions` — **OK**

- 1.20 参数：`Pose`
- 1.21 参数：`Pose`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `hurt` — **OK**

- 1.20 参数：`DamageSource`, `float`
- 1.21 参数：`DamageSource`, `float`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `is` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `isPushable` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `knockback` — **OK**

- 1.20 参数：`double`, `double`, `double`
- 1.21 参数：`double`, `double`, `double`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `push` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `readAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `remove` — **OK**

- 1.20 参数：`RemovalReason`
- 1.21 参数：`Entity.RemovalReason`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge
- 备注：类型放宽后一致（嵌套类限定前缀 / 类型变量 / 泛型实参）

### `removeWhenFarAway` — **OK**

- 1.20 参数：`double`
- 1.21 参数：`double`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `shouldBeSaved` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `tick` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `tickHeadTurn` — **OK**

- 1.20 参数：`float`, `float`
- 1.21 参数：`float`, `float`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `travel` — **OK**

- 1.20 参数：`Vec3`
- 1.21 参数：`Vec3`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

## BossChildDeathLedger

### `save` — **SIG_CHANGED**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`, `HolderLookup.Provider`（`net.minecraft.world.level.saveddata.SavedData`）
- 来源：原版/NeoForge
- 备注：闭包内候选: SavedData(CompoundTag, HolderLookup.Provider) ; SavedData(File, HolderLookup.Provider)

## MechanicalMayhemTracker

## BaseBoss

### `addAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `canAttack` — **OK**

- 1.20 参数：`LivingEntity`
- 1.21 参数：`LivingEntity`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `causeFallDamage` — **OK**

- 1.20 参数：`float`, `float`, `DamageSource`
- 1.21 参数：`float`, `float`, `DamageSource`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `contactAttackInflation` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `contactAttackInterval` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `customServerAiStep` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `die` — **OK**

- 1.20 参数：`DamageSource`
- 1.21 参数：`DamageSource`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `displayFireAnimation` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `doPush` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `handleEntityEvent` — **OK**

- 1.20 参数：`byte`
- 1.21 参数：`byte`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `hasEntityContactAttack` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `hurt` — **OK**

- 1.20 参数：`DamageSource`, `float`
- 1.21 参数：`DamageSource`, `float`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `isInWall` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `isInvulnerableTo` — **OK**

- 1.20 参数：`DamageSource`
- 1.21 参数：`DamageSource`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `isMainBody` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `isPushable` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `knockback` — **OK**

- 1.20 参数：`double`, `double`, `double`
- 1.21 参数：`double`, `double`, `double`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `lavaHurt` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `onCreatureDefinitionReload` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `push` — **OK**

- 1.20 参数：`Entity`
- 1.21 参数：`Entity`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge

### `readAdditionalSaveData` — **OK**

- 1.20 参数：`CompoundTag`
- 1.21 参数：`CompoundTag`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `remove` — **OK**

- 1.20 参数：`RemovalReason`
- 1.21 参数：`Entity.RemovalReason`（`net.minecraft.world.entity.LivingEntity`）
- 来源：原版/NeoForge
- 备注：类型放宽后一致（嵌套类限定前缀 / 类型变量 / 泛型实参）

### `removeWhenFarAway` — **OK**

- 1.20 参数：`double`
- 1.21 参数：`double`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `setCustomName` — **OK**

- 1.20 参数：`Component`
- 1.21 参数：`Component`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `setDeltaMovement` — **OK**

- 1.20 参数：`Vec3`
- 1.21 参数：`Vec3`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `shouldEnhanceMultiplayer` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `shouldShowMessage` — **OK**

- 1.20 参数：—
- 来源：工程源码

### `startSeenByPlayer` — **OK**

- 1.20 参数：`ServerPlayer`
- 1.21 参数：`ServerPlayer`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `stopSeenByPlayer` — **OK**

- 1.20 参数：`ServerPlayer`
- 1.21 参数：`ServerPlayer`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `tick` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `tickHeadTurn` — **OK**

- 1.20 参数：`float`, `float`
- 1.21 参数：`float`, `float`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

## BossOwnedEntity

## BossChunkTicket

## EnemyTargeting

## BaseMonster

### `onAddedToWorld` — **REMOVED**

- 1.20 参数：—

### `createNavigation` — **OK**

- 1.20 参数：`Level`
- 1.21 参数：`Level`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `getAnimatableInstanceCache` — **OK**

- 1.20 参数：—
- 来源：第三方库

### `getDeathSound` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `getHurtSound` — **OK**

- 1.20 参数：`DamageSource`
- 1.21 参数：`DamageSource`（`net.minecraft.world.entity.monster.Monster`）
- 来源：原版/NeoForge

### `playStepSound` — **OK**

- 1.20 参数：`BlockPos`, `BlockState`
- 1.21 参数：`BlockPos`, `BlockState`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `registerControllers` — **OK**

- 1.20 参数：`AnimatableManager.ControllerRegistrar`
- 1.21 参数：`AnimatableManager.ControllerRegistrar`（`software.bernie.geckolib.animatable.GeoAnimatable`）
- 来源：第三方库

### `registerGoals` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

### `setTarget` — **OK**

- 1.20 参数：`LivingEntity`
- 1.21 参数：`LivingEntity`（`net.minecraft.world.entity.Mob`）
- 来源：原版/NeoForge

### `teleportTo` — **OK**

- 1.20 参数：`double`, `double`, `double`
- 1.21 参数：`double`, `double`, `double`（`net.minecraft.world.entity.Entity`）
- 来源：原版/NeoForge

### `tick` — **OK**

- 1.20 参数：—
- 来源：原版/NeoForge

## 复现命令

```powershell
python tools/port2native/entity_api_diff.py \
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java \
  --root121 ConfluenceOtherworld/src/main/java \
  --root121 Confluence-Magic-Lib/src/main/java \
  --root121 TerraEntity/src/main/java --root121 TerraGuns/src/main/java \
  --jar121  ConfluenceOtherworld/build/moddev/artifacts/neoforge-21.1.219-sources.jar \
  --lib     <geckolib-neoforge-1.21.1-4.8.4-sources.jar> \
  --out notes/WP1C-API-DIFF.md
```
