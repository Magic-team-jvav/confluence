# WP6c · 重力反转整条特性搬到 Magic-Lib（1.20 对齐）

> 起点：用户指出 `D:\Minecraft\1.20forge\confluence\Confluence-Magic-Lib\src\main\java\org\confluence\lib\client\handler\GravitationHandler.java`
> **在 1.21 侧没有移植**。核对后确认这正是 `notes/WORK-QUEUE.md` 的 **WP6c（未开工）**，
> 也是 `notes/WP6A-EFFECTS-MIGRATION.md` 第五节明确推迟的那半条特性。
>
> 本文件记录：**为什么不能单独搬一个文件**、两侧实测的归属对照、Mixin 目标审计结果、
> 分批（惰性加法 → 原子切换）的方案与执行清单、以及偏差/推迟项。

## 一、为什么这不是「搬一个文件」

`GravitationHandler` 是「重力反转」整条特性的一半，1.21 侧这条特性**整条**住在 TerraCurio：

| | 1.20（事实来源） | 1.21（本批之前） |
|---|---|---|
| 客户端处理器 | Lib `lib/client/handler/GravitationHandler`(87) | TC `terra_curio/client/handler/GravitationHandler`(98) |
| 按键 | Lib `lib/client/LibKeyBindings`（`FLIP_GRAVITATION`） | TC `client/TCKeyBindings`（`FLIP_GRAVITATION`） |
| mixed 接口 | Lib `lib/mixed/ILibEntity`（`confluence$…` 3 成员） | TC `mixed/IEntity`（6 成员，两种前缀混用） |
| 网络包 | Lib `lib/network/{c2s/GravitationPacketC2S, s2c/BroadcastGravitationRotPacketS2C}` | TC 同名两份 |
| Entity 注入 | Lib `mixin/EntityMixin` + `LivingEntityMixin` + `ServerGamePacketListenerImplMixin` + 4 个 client mixin | **合并进 TC 的 `mixin/EntityMixin`** 与 TC 的 client mixin |
| 驱动 | **Lib** `lib/client/event/LibClientGameEvents` 的 `movementInputUpdate`/`playerTick$Pre`/`logout` | **TC** `client/event/GameClientEvents` / `TCGameEvents` |

### 1.1 硬约束：两个 `@Mixin(Entity.class)` 的同名 `@Unique` 字段

1.20 Lib 的 `EntityMixin` 用的是**历史遗留命名** `terra_curio$isShouldRot` / `terra_curio$dimensionHeight`
（这条特性当年先在 TerraCurio 里写、后来才搬进 Lib，前缀没跟着改）。
而 1.21 TerraCurio 的 `mixin/EntityMixin.java:50/52` **有同名 `@Unique` 字段**。
两个 mixin 注入同一个 `Entity` 会因字段重复而崩 → 所以 notes 记的「不能先加一套再拆另一套」。

**本批的解法**：1.21 Lib 这份把字段改名为 `confluence$isShouldRot` / `confluence$dimensionHeight`
（与 `ILibEntity` 方法前缀一致；`@Unique` 私有字段不对外可见，改名零外部影响）。
这样 Lib 那份在**切换完成前**可以安全地与 TC 那份共存（互不感知对方的字段），
本批因此能拆成「惰性加法 → 原子切换」两步、每步都能过编译门；
切换完成后字段名保持 `confluence$…`，不需要再改回来。

### 1.2 一条比 notes 更清楚的事实链：`forceEnable`/`forceCancel` 就是被内联掉的耦合

1.21 TerraCurio 的 `GravitationHandler` 比 1.20 Lib 那份多了 `hasGlobe` 字段、
并且在 `handle`/`force` 里直接调 `StepStoolHandler.onStool()`。看起来像「1.21 独有内容」，实测是**同一套耦合的两种写法**：

| 耦合 | 1.20（Lib 提供通道） | 1.21（内联进 TC） |
|---|---|---|
| 台阶凳上禁用重力翻转 | TC `StepStoolHandler:82` → `GravitationHandler.setForceCancel(onStool())`；Lib 的 `handle`/`force` 里 `if (isForceCancel()) return;` | TC `GravitationHandler.handle` 里 `if (StepStoolHandler.onStool()) return;` |
| 持有重力球时允许按键翻转 | TC `TCClientPacketHandler:119` → `setForceEnable(isCuriosExists(GRAVITY_GLOBE))`；Lib 驱动里 `else if (isForceEnable()) handle(player)` | TC `GravitationHandler.hasGlobe` 字段 + `GameClientEvents:68` 的 `isHasGlobe()` 分支 |
| 谁驱动 | **Lib** `LibClientGameEvents.movementInputUpdate` | **TC** `GameClientEvents.movementInputUpdate` |

所以本批**按 1.20 恢复通道**：`forceEnable`/`forceCancel` 回到 Lib，
TC 侧改成「写这两个开关」，驱动权回 Lib。这不是吃掉 1.21 独有内容，而是把 1.21 因跨模块而内联的耦合还原成 1.20 的归属。

### 1.3 `isPlayer` 是 1.21 独有成员，可安全消解

1.21 TC 的 `IEntity` 有 6 个成员，1.20 只对应到 5 个：
- 重力 3 个（`setShouldRot`/`isShouldRot`/`getDimensionHeight`）→ 1.20 在 Lib 的 `ILibEntity`
- 克苏鲁 2 个（`getCthulhuSprintingTime`/`setCthulhuSprintingTime`）→ 1.20 在 TC 的 `mixed/ITCEntity`
- **`isPlayer` 1.20 没有** —— 1.20 的 `EntityMixin`/`TCUtils` 直接用 `instanceof Player`
  实测：1.21 全仓对 `terra_curio$isPlayer` 的**调用点**只有 `EntityMixin`/`TCUtils`/`ShinnyStone`/
  `LivingEntityClientMixin`/`GravitationHandler` 这几处，**全部可以换成 `instanceof Player`**（就是 1.20 的写法）

## 二、Mixin 目标审计（纪律要求：混入层逐目标核实）

7 个要搬的 mixin，逐个把注入目标拿到 1.21 源码（`build/moddev/artifacts/neoforge-21.1.219-sources.jar`）核对：

| 目标 | 结论 |
|---|---|
| `Entity#getOnPosLegacy()` | OK（1.21 是**无参**；1.20 mixin 的 `@Inject(method="getOnPosLegacy", at=RETURN)` 不带描述符，仍然命中） |
| `Entity#getOnPos(float)` | OK（`protected`） |
| `Entity#checkSupportingBlock(boolean, Vec3)` | OK（`protected`） |
| `Entity#spawnSprintParticle()` | OK（`protected`；`Level#addParticle(ParticleOptions, DDDDDD)` 实参序 `(粒子,x,y,z,dx,dy,dz)` → index 2 = y、5 = dy） |
| `Entity#checkFallDamage(double, boolean, BlockState, BlockPos)` | OK（`protected`，@TAIL + 两个 `@Local(argsOnly=true)`） |
| `Entity#move(MoverType, Vec3)` | OK；`verticalCollisionBelow` 的 PUTFIELD 在 1.21 **只出现一次** → ordinal 唯一 |
| `Entity#baseTick()` 的 `isInLava()` INVOKE `ordinal = 1` | OK（1.21 `baseTick` 里 `isInLava()` 出现两次：火焰判定与 `lavaHurt`；TC 1.21 现有的同款注入就用 `ordinal=1` 且工作正常） |
| `Entity#verticalCollisionBelow/verticalCollision/fallDistance` 字段 | OK（都是 `public`） |
| `Entity#getDimensions(Pose)` / `getPose()` | OK（`public`，非 final） |
| `Entity#getEyeHeight()` | OK（**`public final`** —— `@Inject(at=RETURN, cancellable)` 仍合法，注入不等于覆写） |
| `LivingEntity#checkFallDamage` | OK |
| `Player#maybeBackOffFromEdge(Vec3, MoverType)` | OK（`protected`；`Vec3.y` 的 GETFIELD ordinal 0 对应 1.20 那个表达式位置） |
| `Player#maxUpStep()` | OK（声明在 `Entity`，`Player` 继承；`maybeBackOffFromEdge` 内的接收者静态类型是 `Player`） |
| `Player#jumpFromGround()` | OK |
| `ServerGamePacketListenerImpl#handleMovePlayer(...)` + 其内部 `ServerPlayer#resetFallDistance()` 调用 | OK |
| `LivingEntityRenderer#isEntityUpsideDown(LivingEntity)` | OK（`public static`；1.20 mixin 也是 `private static` 注入 RETURN ordinal 1） |

**结论：21/21 目标 OK，无 `TARGET-MISSING` / `METHOD-MISSING`。**
（1.20 的 `TickEvent.PlayerTickEvent` / `MovementInputUpdateEvent` 属事件层不属 mixin 目标，
已单独核实 1.21 原生对应物：`net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre`、
`net.neoforged.neoforge.client.event.MovementInputUpdateEvent`（`extends PlayerEvent` → `getEntity()`）。）

**一条被纠正的平台假设**：我原先以为 NeoForge 21.1 删掉了 `KeyConflictContext`。
**实测是错的** —— `net.neoforged.neoforge.client.settings.KeyConflictContext` 仍在，
`KeyMapping` 也保留了吃 `IKeyConflictContext` 的 4/5/6 参构造器（主模组 `ModKeyBindings` 就在用）。
因此 `LibKeyBindings` **保留 1.20 的 `KeyConflictContext.IN_GAME` 五参构造器**，不做「原生化」改写。

**另一条要核实的平台事实（注册总线）**：`RegisterKeyMappingsEvent` 是 `IModBusEvent`
（`extends Event implements IModBusEvent`，javadoc 写明 "fired on the mod-specific event bus"）→
挂错总线**能编译但静默不注册**。本批的落地方式是**注解式**：
`LibClientModEvents`（`@EventBusSubscriber(modid = LIB_ID, value = Dist.CLIENT)`）新增一个
`@SubscribeEvent registerKeyMappings(...)`。这条**没写 `bus = Bus.MOD` 也成立**的证据来自仓库内先例：
主模组 `common/event/ModEvents` 同样是无 `bus=` 的 `@EventBusSubscriber(modid = MODID)`，
而它处理的 `FMLCommonSetupEvent` 正是 `IModBusEvent`，且该类的 `commonSetup` 确实生效
（`ModGunProperties.init()` / `GunSounds.init()` 就在里面）。因此 NeoForge 21.1 会按
`IModBusEvent` 把这类订阅者路由到模组总线。
→ 结论：1.20 `LibKeyBindings.init()`（PortLib 时代「自己去找总线」的钩子）在 1.21 **不需要**，
本批**没有**保留它（保留即死代码）；按键注册的唯一入口是 `LibClientModEvents#registerKeyMappings`。

## 三、执行方案：惰性加法 → 原子切换

### 第一步（WP6c-1）Lib 侧加法，**不接线**
新增：`lib/mixed/ILibEntity`、`lib/client/LibKeyBindings`、`lib/client/handler/GravitationHandler`、
`lib/network/c2s/GravitationPacketC2S`、`lib/network/s2c/BroadcastGravitationRotPacketS2C`、
`lib/mixin/{EntityMixin, ServerGamePacketListenerImplMixin}`、
`lib/mixin/client/{ClientEntityMixin, ClientLivingEntityMixin, ClientPlayerMixin, LivingEntityRendererMixin, LocalPlayerAccessor}`，
并把重力注入合并进 Lib 既有的 `mixin/LivingEntityMixin`，登记进 `confluence_magic_lib.mixins.json`。

**为什么这样是安全的**：Lib 的 `confluence$isShouldRot` 唯一的写入入口是 `GravitationPacketC2S`
（本步不注册）与 `GravitationHandler.handle`（本步无人调用）→ 该值恒为 `false` →
所有注入都是 no-op，而 TC 那份用自己的 `terra_curio$` 字段继续独立工作 → **用户可见行为零变化**。

### 第二步（WP6c-2）原子切换
1. **Lib 接线**：`LibClientGameEvents` 补 `movementInputUpdate`/`playerTick$Pre`/logout 的
   `GravitationHandler` 调用（1.20 归属）；`LibKeyBindings.init(...)` 挂到 Lib 的**模组总线**
   （`RegisterKeyMappingsEvent` 是 `IModBusEvent`）；两个包登记进 Lib 的 payload 注册入口。
2. **主模组集成**：删 `mixin/integration/terracurio/TCKeyBindingsMixin` → 新增
   `mixin/integration/magiclib/LibKeyBindingsMixin`（1.20 逐字）+ mixin 配置改指向；
   `GameClientEvents:548` 的 `TCKeyBindings.FLIP_GRAVITATION` → `LibKeyBindings.FLIP_GRAVITATION`；
   lang 键 `key.terra_curio.flip_gravitation` → `key.confluence_magic_lib.flip_gravitation`
   （主模组 `es_es`/`lzh`/`pt_br` 三份 + TC 的 `TCLanguageProvider`）。
   **外加一处不在原清单里的调用点**：主模组 `common/entity/InverseEnderMan:19` 用的是
   TC 的 `IEntity.of(this).terra_curio$setShouldRot(true)` → 按 1.20 换成
   `ILibEntity.of(this).confluence$setShouldRot(true)`（1.20 同名文件 `:20` 逐字）。
3. **TC 拆除**：删 `client/handler/GravitationHandler`、两个网络包、`mixed/IEntity`（改立 `mixed/ITCEntity`）、
   `TCKeyBindings.FLIP_GRAVITATION`；`mixin/EntityMixin` 换成 1.20 TC 形态（只剩克苏鲁 + 岩浆免疫 + 克苏鲁触碰，
   用 `instanceof Player`）；`GameClientEvents`/`TCGameEvents` 去掉驱动；`StepStoolHandler` 改 `setForceCancel(...)`；
   `TCClientPacketHandler` 改 `setForceEnable(...)`；`GravityGlobe` 的按键引用改指 Lib；
   **约 54 处**重力判据改指 `ILibEntity.of(...).confluence$…`，克苏鲁判据改 `ITCEntity.of(...)`；
   TC 的 client mixin 按「1.20 Lib 有对应物就删、1.21 独有就保留并改指」逐 hook 处置；TC mixin 配置同步。

**为什么必须先加后切**：`RegisterKeyMappingsEvent` 上两个同名默认键（都是 `GLFW_KEY_UP`）
会在过渡期同时触发两套 handler，两个标志同时置位 → 两个 mixin 同时翻转 = **双重翻转**。
所以「激活 Lib」与「拆除 TC」必须在**同一次提交序列**里完成（本批的 3 个仓库提交在本地连续完成，
中间态不作为可运行状态交付）。

## 四、校验

| 项 | 命令 / 结论 |
|---|---|
| Mixin 目标审计 | 21/21 OK（第二节） |
| 三模块编译门 | `build_errors.py --module {Confluence-Magic-Lib, TerraCurio, ConfluenceOtherworld} --repo .` |
| 跨模块完整性 | 全仓 `terra_curio$` 残留只剩注释；`terra_curio.client.handler.GravitationHandler` 的外部引用清零 |
| 残留自查 | `terra_curio.mixed.IEntity` / `TCKeyBindings.FLIP_GRAVITATION` / `hasGlobe` / `terra_curio$isPlayer` 零命中 |

### 4.1 ⚠️ 编译门**查不到**的收口项（必须在提交前手工核）

1. **mixin 配置里不能残留已删类**：`required: true` 的配置写了不存在的 mixin 类 → **启动即崩**，
   而编译门完全看不见。本批 TC 侧删了 3 个 mixin 类，必须在 `terra_curio.mixins.json` 里同步删条目：
   - `mixins` 段：`ServerGamePacketListenerImplMixin`
   - `client` 段：`client.EntityClientMixin`、`client.PlayerClientMixin`
   （`mixin/EntityMixin` 仍存在——已换成 1.20 TC 形态，条目保留；
   `client/{LivingEntityClientMixin,LivingEntityRendererMixin,LocalPlayerMixin,MouseHandlerMixin}` 都被「改指后保留」，条目保留。）
2. **Lib 侧的配置只增不减**：`confluence_magic_lib.mixins.json` 新增 7 条目（已核对 JSON 有效、
   `mixins`/`client` 分段正确、对应文件都存在）。
3. **按键 lang 键两侧一致**：`key.confluence_magic_lib.flip_gravitation` 必须同时存在于
   主模组 3 个 lang 文件与 TC 的 `TCLanguageProvider`（生成器），否则按键名会显示成原始 key。
4. **`hasGlobe` 语义承接**：TC 侧 `TCClientPacketHandler` 必须真的调 `setForceEnable(...)`
   （否则「持有重力球可按键翻转」这个 1.21 特性会静默失效），`StepStoolHandler` 必须调
   `setForceCancel(...)`（否则台阶凳上会错误地允许翻转）。这两条都是**行为回归**，编译门同样看不见。

## 五、偏差与推迟（逐条写明归属）
1. **`@Unique` 字段改名**（`terra_curio$…` → `confluence$…`）：1.20 是历史遗留命名，
   改名是为了能分批、且不与 TC 同名字段相撞（私有字段，外部不可见）。
2. **`LibKeyBindings` 保留 `KeyConflictContext`**：NeoForge 21.1.219 仍提供它（见第二节末），
   所以这不是「未原生化」，而是 1.20 原文本来就正确。
3. **4 个「反转 AI」mixin**（`GroundPathNavigation`/`LandRandomPos`/`MoveControl`/`WalkNodeEvaluator`）：
   1.20 **无对应物**（1.20 Lib 的 `EntityMixin` 里只留 `// todo 反转AI`）→ 属 **1.21 独有实现**，
   本批**保留在 TerraCurio**，只把判据改指 Lib 的 `ILibEntity`。（若日后 1.20 补上反转 AI，再按那时的归属处理。）
4. **TC 的 `client/LocalPlayerMixin`、`client/MouseHandlerMixin` 里的重力读取**：1.20 Lib 无对应 mixin
   → 同上保留在 TC，改指 Lib 的 `GravitationHandler`。

   ⚠️ **修正（WP6c-1 落地时才发现）**：1.20 Lib **确实有**这两个类，只是我最初用
   `ILibEntity` 找引用点，而它们读的是 `GravitationHandler.isShouldRot()` 静态量，所以漏掉了。
   1.20 Lib 版本**不能照搬**：
   - `lib/mixin/client/LocalPlayerMixin` 的 `sinkUpFluid` 用的是 **Forge 专属**
     `LocalPlayer#sinkInFluid(FluidType)`（NeoForge 无此方法）；另一处 `skipSlowdown` 1.21 已有
     （且 `ordinal` 从 0 变 1）。其中唯一真正属于重力的是 `flip`（`getFlyingSpeed()F` ×
     `GravitationHandler.getJumpDir()`）—— **1.21 TerraCurio 的 `client/LocalPlayerMixin:38/47` 就是它的 1.21 适配版**。
   - `lib/mixin/client/MouseHandlerMixin` 的目标 `LocalPlayer#turn(DD)` 在 1.21 **不存在**
     （`turn` 声明在 `Entity`）→ 要照搬必须重新核实 owner（本批不做）。
   → 因此这两个类**保留在 TC 并改指 Lib 的 `GravitationHandler`**（行为与 1.20 等价，只是宿主类归 TC）；
   这与第 3 条（四个反转 AI mixin）同一处置口径：**「1.20 的对应物无法 1:1 照搬 → 保留 1.21 适配版 + 改指」**，
   而不是删功能或硬搬。
5. **`hasGlobe` 字段消失**：语义并入 Lib 的 `forceEnable`（第二节 1.2），不是丢功能。
6. **datagen 产物**：lang 键改名后 `src/generated/**` 里的旧键要 `runData` 重生成（WP7），不手改生成物。
7. **实机验收**：Mixins 与按键只在编译门层面成立；翻转手感/相机 180°/台阶凳/重力球分支需 `runClient` 验收。

## 六、收口记录（实际落地结果与三处「动手时才发现」）

### 6.1 三模块编译门

| 模块 | 结果 |
|---|---|
| `Confluence-Magic-Lib` | **0 错误 / 0 文件** |
| `TerraCurio` | **0 错误 / 0 文件** |
| `ConfluenceOtherworld` | **0 错误 / 0 文件** |

### 6.2 「双侧同命中」核验（编译门查不到，逐点实测）

Lib 与 TC 现在**都有** mixin 注入到同名方法，必须逐点判断是「互补」还是「重复变换」：

| 方法 | Lib 侧 | TC 侧 | 判定 |
|---|---|---|---|
| `LocalPlayer#aiStep` | `@ModifyExpressionValue(isPassenger ordinal=1)`（skipSlowdown） | `@ModifyExpressionValue(onGround ordinal=0)`、`@WrapWithCondition(sinkInFluid)`、`@ModifyExpressionValue(getFlyingSpeed)` | **不同注入点**，无冲突 |
| `Entity#baseTick` 的 `isInLava ordinal=1` | `@Inject`（缓存 dimensionHeight，只读） | `@ModifyExpressionValue`（岩浆免疫，改布尔） | **互补**，正是 1.20 的设计 |
| `LivingEntity#travel` | `@ModifyVariable` = `reversed`（重力 `-x`） | `@ModifyVariable` = `confused`（迷乱 `reverse()`） | **两个独立关注点**，链式应用与 1.20 一致 |

→ **结论：无双重变换**。三处正是「1.20 里同在 Lib、1.21 拆到两个模块」的落点。

### 6.3 动手时才发现、并当场处置的四件事

| 发现 | 处置 |
|---|---|
| **Lib 自己的 lang 两条键缺失**（1.20 在 `LibLanguageProvider:67/68`：分类键 + 按键键）→ en_us/zh_cn 下按键名会显示原始键 | 补进 Lib 的 provider；**并把 TC provider 里那条改名改成删除**（1.20 的 TC provider 没有这条，两边都写会生成重复键 —— 与 WP6a 同一口径） |
| **`LocalPlayerAccessor` 重复**：Lib 新增的那份与 TC 已有的那份**逐字相同**（同目标 `LocalPlayer`、同 `setCrouching`） | TC 那份已**零使用**（唯一用户是被删的 `GravitationHandler`）且 1.20 归 Lib → **删除 TC 副本 + 配置条目** |
| **`LibKeyBindings.init(IEventBus)` 成为死代码**（注解式订阅替代它） | 删除该方法与随之无用的 import，把「注册入口在哪」写进类 javadoc |
| **主模组 `InverseEnderMan`、TC `mixin/integration/sable/EntityMixin`** 两份清单都没覆盖 | 前者按 1.20 换 `ILibEntity`；后者是 1.21 独有的 Sable 联动 → **保留 + 改指**（已写进类 javadoc） |

### 6.4 保留在 TerraCurio 的清单（都有「1.20 无法 1:1 照搬 / 1.20 无对应物」的依据）

四个反转 AI mixin、`client/LocalPlayerMixin`（sinkUpFluid/flip/floating）、`client/MouseHandlerMixin`、
`mixin/integration/sable/EntityMixin`、`client/LivingEntityClientMixin` 的非重力 hook、
`LivingEntityRendererMixin.couldRender`、`LivingEntityMixin` 的 `confused` 半。
**判据写进了各文件 javadoc**：若日后 Lib 按 1.20 补上 `LocalPlayerMixin` 的重力两半与 `MouseHandlerMixin`，
**必须删掉 TC 这些对应 hook**（否则鼠标取反两次 = 负负得正、`getJumpDir()` 乘两次）。

### 6.5 本批新增的推迟项（都属「1.21 那份更窄」，与重力范围无关）

1. `mixed/IClientLivingEntity`、`ILivingEntity` 的 1.20 名字是 `ITCClientLivingEntity`、`ITCLivingEntity` → 未改名。
2. 1.20 Lib 的 `LivingEntityMixin.confused` hook 未落地到 Lib（行为由 TC 的合并 hook 保留）。
3. `GravityGlobe` 提示颜色：1.20 用 `LibClientUtils.keyMappingComponent(key, ChatFormatting.WHITE)`（2 参重载），
   1.21 Lib 只有 1 参（GRAY）→ 现状 GRAY。
