# WP2 批次 19 · 动物注册层 `CritterEntities`（增量落地）+ 3 个被它挡住的动物

> **本批的关键结果是「一次落地，全族放行」**：1.20 的 `CritterEntities` 单点闭包 182 文件，
> 直接搬会拖进整条 boss/NPC/召唤链；但它**拦着 14 个自包含动物**（动物类在自己的
> `getBreedOffspring` 里回指注册条目）。按 `MonsterEntities` 的既有做法**增量落地**
> （只写「类已存在于 1.21」的条目）之后，动物种子的闭包从 **171 掉到 3**，
> 下一批只需按表逐条吃。

## 一、为什么这个类必须先行（实测数字）

本批开工时直接对 3 个自包含动物跑 `dep_subset.py`：

```powershell
python tools/port2native/dep_subset.py `
  --src120 D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --root121 TerraCurio/src/main/java --root121 TerraEntity/src/main/java --root121 TerraGuns/src/main/java `
  --seed org.confluence.mod.common.entity.animal.Cluckshroom `
  --seed org.confluence.mod.common.entity.animal.CloudSheep `
  --seed org.confluence.mod.common.entity.animal.GlowingMooshroom `
  --alias ... --out notes/WP2Q-SUBSET.md
# 落地前：{"candidates": 171, "kept": 171, "new": 171}   ← 全是 1.20 的注册层与它引用的物种
# 落地后：{"candidates": 3,   "kept": 3,   "new": 3}     ← 只剩这 3 个动物自己
```

原因是**动物类回指注册条目**（`dep_subset` 的类型级扩张会把注册类整个拉进来）：

| 动物 | 回指点 |
|---|---|
| `Cluckshroom` | `Cluckshroom.java:73` 用 `CritterEntities.CLUCKSHROOM` / `GLOWING_CLUCKSHROOM` |
| `CloudSheep` | `CloudSheep.java:56` 用 `CritterEntities.CLOUD_SHEEP` |
| `GlowingMooshroom` | `GlowingMooshroom.java:32` 用 `CritterEntities.GLOWING_MOOSHROOM` |

## 二、本批落地

### 2.1 新增注册层 `common/init/entity/CritterEntities`（1.21 侧，**增量**）

- 位置与命名沿用 1.20（`common/init/entity/`），与已有的 `MonsterEntities` 并列；
  这样待移植物种里那句 `import org.confluence.mod.common.init.entity.CritterEntities;` 可以原样照搬。
- **只写 27 条**：26 条是「类已存在于 1.21」的动物 + 本批新落的 3 个（`cloud_sheep`、
  `cluckshroom`、`glowing_cluckshroom`，其中 `glowing_cluckshroom` 与 `cluckshroom` 共用
  `Cluckshroom` 类，只是 `glowing=true`）。
- **未落地的 1.20 条目（13 条）在文件里留了注释标出**：`bunny`、`red_squirrel`、`fairy`、
  `goldfish`、`penguin`、`mystic_frog`、`snail`/`glowing_snail`/`magma_snail`、
  `jewel_bunny`、`jewel_squirrel`、`explosive_bunny`、`hostile_bunny`。
  （注：其中 10 条在本批之后**只剩一个文件就会解套**，见第五节。）
- 属性声明**并入既有的 `ModEntities.withAttributes`**（`ATTRIBUTES` 表 → `EntityAttributeCreationEvent`
  → `ModEntities.registerAttributes`），不另开一套；`withAttributes` / `register` /
  `registerInsect` / `registerCompact` / `registerHostileCompact` 全部逐条照 1.20 的辅助方法搬，
  只把 `PortDeferredRegisterExtension.register(ENTITIES, name, id -> …)` 换成 1.21 原生的
  `DeferredRegister#register(String, Function<ResourceLocation, T>)`（签名本来就是同一个形状）。
- `Confluence` 构造器里 `CritterEntities.ENTITIES.register(eventBus);`（紧挨 `MonsterEntities`）。

### 2.2 新增 3 个动物（`stage_batch` 直出 + 手改 2 处）

| 文件 | 非空行 | 关键改动 |
|---|---:|---|
| `common/entity/animal/Cluckshroom` | 113 | `IForgeShearable` → NeoForge `IShearable`（形参表改写） |
| `common/entity/animal/CloudSheep` | 81 | 转换器直出；`causeFallDamage(float,float,DamageSource)` 与 1.21 一致（同批次既有 `Worm`/`SimpleCritter` 已是这个签名） |
| `common/entity/animal/GlowingMooshroom` | 57 | 同 `Cluckshroom` |

### 2.3 两处非机械改动（都属于「语义改写，不进规则」）

```java
// 1.20（Forge）
public boolean isShearable(ItemStack stack, Level level, BlockPos pos)
public List<ItemStack> onSheared(Player player, ItemStack stack, Level level, BlockPos pos, int fortune)

// 1.21.1（NeoForge IShearable）：Player 提到第一位、去掉 fortune
public boolean isShearable(Player player, ItemStack stack, Level level, BlockPos pos)
public List<ItemStack> onSheared(Player player, ItemStack stack, Level level, BlockPos pos)
```

对照物是 **1.21 侧既有的 `RainbowSheep`**（`RainbowSheep.java:134/159`，1.20 侧那份是
`IForgeShearable`、1.21 侧那份已改成 `IShearable`）—— 也就是说这条映射在仓库里**已有先例行**，
不需要新造写法。

转换器对它的判定是 `manual`（`manual-todo.md`：*"Forge-only interface that vanilla absorbed:
1.21 has `net.minecraft.world.entity.Entity#Shearable` … re-author the implementation rather than
renaming"*）。注意**规则说明里的建议是错的**：1.21 原版 `Shearable` 只是
`shear(SoundSource)` + `readyForShearing()` 的标记接口，接不住 1.20 那套「剪羊毛掉自定义物品」的
语义；本仓库的正确对应物是 NeoForge 的 `net.neoforged.neoforge.common.IShearable`（先例已存在）。

## 三、成员级盲区的第 7 例：包路径盲区（`ModEntities` 的 import 被判漏）

`dep_subset.py` 报「3/3 零扩张」之后，编译门仍报了 **1 处**错误：

```
CritterEntities.java 215  cannot find symbol
        >>> return ModEntities.withAttributes(type, attributes);
```

根因不是成员缺失，而是**新类的所在包**：`CritterEntities` 住在
`org.confluence.mod.common.init.entity`，而 1.21 的 `ModEntities` 住在**父包**
`org.confluence.mod.common.init`（1.20 侧两者同包 —— 1.20 的 `CritterEntities.java:18` 写的是
`import static org.confluence.mod.common.init.entity.ModEntities.withAttributes;`，
同包静态导入 + 裸名调用）。搬到 1.21 后包名变了两处，本批的增量文件里写成了
`ModEntities.withAttributes(type, attributes)`，却没有对应的实例导入 → 编译门报符号缺失。
补 `import org.confluence.mod.common.init.entity.ModEntities;` 即可。

> 规律：**「1.20 同包 / 1.21 跨包」的搬迁会丢掉 import，而工具只看类型是否存在。**
> 新落注册类（`MonsterEntities` / `CritterEntities` / 以后的 `BossEntities` / `NpcEntities`）
> 时都要单独核对这一条；`dep_subset.py` 里的 `--alias`
> （`...init.entity.ModEntities=...init.ModEntities`）只修了**边的等价关系**，不修 import。

## 四、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器报告：`uncovered.json` = `[]`；`leftovers.txt` **0 行**；`manual-todo.md` 16 处
（2 处 `IForgeShearable` 本批手改；14 处是 `port-registryentry-get` 的 `\.get` 过宽规则跳过条目，
属于既有已知噪声，本批落地的文本里 `.get()` 写法与 1.20 一致）。

## 五、本批之后的收敛（`notes/WP2Q-CLOSURES.md`）

全动物目录重算逐种子闭包，**剩余 11 个种子**里 10 个已降到「闭包 1~3 个文件」：

| 种子 | 闭包 | 其中新增 | 说明 |
|---|---:|---:|---|
| `Snail` / `Fairy` / `Goldfish` / `Penguin` / `RedSquirrel` / `JewelSquirrel` | 1 | 0 | 自包含，转换器直出（`Goldfish` / `Snail` / `Fairy` 另有 API 改写） |
| `Bunny` / `HostileBunny` | 2 | 1 | 只差 `Bunny` 自己 |
| `ExplosiveBunny` / `JewelBunny` | 3 | 2 | 只差 `Bunny` |
| `MysticFrog` | 157 | 156 | **重量种子，仍推迟** |

## 六、本批未做（明确推迟）

| 推迟项 | 原因 |
|---|---|
| `MysticFrog`（157 文件闭包） | 重量级，单独成批 |
| `CreatureSpawnPlacements`（1.20 的刷怪放置层） | 该文件整体是 PortLib 词汇（`PortRegisterSpawnPlacementsEvent` / `PortSpawnPlacementTypes`）+ `SpawnPlacementChecks`，要整体按 1.21 的 `RegisterSpawnPlacementsEvent` 改写，单独成批。**不影响本批**：1.21 的 `SpawnPlacements` 对未登记的类型返回 null，`NaturalSpawner` 会跳过；且这批动物在 1.21 侧尚无生物群系刷怪条目，不会出现「注册了但刷不出来」以外的行为 |
| 动物的客户端渲染器 / GeckoLib 模型与贴图 | 1.21 侧 `client/entity/renderer` 里本就没有这些动物（`MonsterEntities` 的 30 余条注册同样还没有渲染器）—— 沿用既有做法，渲染层随 WP2 渲染器批次统一处理 |
| 1.20 的 `package-info.java`（动物包） | 无内容语义，随渲染器批次一起补 |

---

# 附：`dep_subset.py` 原始报告（本批种子 3 个，落地后重算）

# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 3 个：`Cluckshroom`, `CloudSheep`, `GlowingMooshroom`
- 扩张后候选 **3** 个 `org.confluence.*` 类型
- 本批保留 **3** 个（其中 1.21 侧**新增** 3 个），因 `--defer` 剔除 **0** 个
- `--alias` 3 条（跨分支改过包路径的等价关系，不算新增）：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.animal.CloudSheep`
- `org.confluence.mod.common.entity.animal.Cluckshroom`
- `org.confluence.mod.common.entity.animal.GlowingMooshroom`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_
