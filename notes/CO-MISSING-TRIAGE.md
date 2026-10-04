# 主模组 33 个 MISSING 的逐条判定与处置（终版）

**第一版口径作废**：此前按「1.21 已有等价实现 → 非缺口」判过一轮，用户裁定不成立。
正确口径是**按分叉方向同步**：

| 规则 | 说明 |
|---|---|
| 分叉 | 1.20 分支约 2026 年 4~5 月自 1.21 分出；合并基点 = `795ac9ccc`（2026-05-31） |
| 1.21 侧改动 | 分叉**之后**在 1.21 做的改动 → 要**反向同步到 1.20** |
| 1.20 侧改动 | 分叉**之后**在 1.20 做的改动 → 要**同步到 1.21** |
| 判方向 | `git cat-file -e 795ac9ccc:<path>`：基点**不存在** = 1.20 侧新增；基点**存在而现在没了** = 1.21 侧删除 |
| 例外 | 平台倒逼的写法（10 个附魔类 + `ProtectionEnchantmentMixin`、两个 `Simple*TreeGrower`）不移植 |

## 一、已落地（1.20 侧重构 → 同步进 1.21）

| 文件 | 方向依据 | 处置 | 提交 |
|---|---|---|---|
| `client/handler/ScryingOrbHandler` | 基点不存在 | 1.20 原文移植；`GameClientEvents`/`LocalPlayerMixin`/`ScryingOrb` 改回调用形态 | `f41bc6293` |
| `client/handler/SoulSkillHandler` | 基点不存在 | 同上 | `f41bc6293` |
| `client/renderer/entity/projectile/ForwardProjectileRenderer` | 基点不存在 | `sword/ForwardProjRenderer` 改名 + 回 1.20 的包与泛型 `<T, M extends EntityModel<T>>` | `f41bc6293` |
| `client/model/entity/projectile/BeeProjectileModel` | 基点不存在 | 1.20 的主模组泛型模型移植；BEE/BEE_ARROW 注册与模型层注册按 1.20 形态（贴图用 `confluence:`，两个命名空间下都有） | `f41bc6293` |
| `client/model/entity/projectile/SpearProjectileModels` | 基点不存在 | 纯客户端模型集中类移植；4 条层注册 + 4 条渲染器注册改用它 | `f41bc6293` |
| `mixin/integration/magiclib/LibEntityUtilsMixin` | 基点不存在 | 1.20 原文移植（`@WrapMethod` 包 `LibEntityUtils.getTeam`） | 本轮 |
| `Confluence-Magic-Lib` 的 `util/LibEntityUtils`（前置类，325 行） | 基点不存在 | 1.20 类移植；仅 3 处平台适配：`PortListExtension.getFirst`→`List#getFirst`、Forge `PartEntity`→NeoForge `PartEntity`、`LibMathUtils.dirToRot`→同包 `VectorUtils.dirToRot`（1.21 侧该方法搬了家，实现逐字节相同） | 子模块 `85bd6cf3e` |

## 二、反向欠账（1.21 侧改动 → 应同步到 1.20；用户裁定本轮暂缓）

| 项 | 方向依据 |
|---|---|
| 钩爪渲染层重构（`client/renderer/entity/hook/SimpleHookRenderer` + `client/model/entity/hook/` 20 个模型 + 10 个每钩爪渲染器 + 注册改造） | `SimpleHookRenderer` 与 20 个模型在基点**都不存在** → 1.21 侧新增 |
| 删除 `mixin/world/level/block/entity/BaseContainerBlockEntityMixin`（1.21 侧 `IBaseContainerBlockEntity` 已标 `@Deprecated(forRemoval=true)`） | 基点存在、现无 → 1.21 侧删除 |
| 删除 `integration/terra_curio/TCHelper` 与 `RecipeManagerMixin` 里的对应注入 | 基点存在、现无 → 1.21 侧删除 |

> 本轮曾把这三项按相反方向做进 1.21，**已全部回退到 HEAD**。
> 旁证：`notes/1.21-BRANCH-DIVERGENCE.md` 的 hook models（20 类）、`e4e56daa5`（删 BaseContainerBlockEntityMixin）、`b73eacf4a`（删 TCHelper）。

## 三、不移植

| 项 | 理由（用户裁定） |
|---|---|
| `common/enchantment/` 10 个附魔类 + `mixin/world/item/enchantment/ProtectionEnchantmentMixin` | 1.20 在 PortLib 环境下被迫写成 java 类；1.21 走数据驱动附魔 |
| `common/block/natural/SimpleTreeGrower` / `SimpleMegaTreeGrower` | 1.20 的 `AbstractTreeGrower`/`AbstractMegaTreeGrower` 只能靠子类重写传 feature；1.21.1 已合并改名 `TreeGrower`，构造器直接吃 `Optional<ResourceKey<ConfiguredFeature>>` |
| `mixin/neoforge/client/model/ForgeItemModelShaperMixin` | 只是改名：Forge 的 `ForgeItemModelShaper` 在 NeoForge 叫 `RegistryAwareItemModelShaper`，1.21 已有同逻辑的 `RegistryAwareItemModelShaperMixin` |

**曾误做后按裁定回撤**：`mixin/world/item/SnowballItemMixin`（1.20 侧 `093eda09f` 2026-06-16；1.20 全仓没有
`ModifyDefaultComponentsEvent`，只能改 mixin）、`mixin/world/item/BucketItemMixin`（1.20 侧 `1da5e0d26` 2026-09-21；
1.21 同行为已由两侧共有的 `ItemUtilsMixin` 覆盖）→ 回撤见 `25ee32487`。

## 四、遗留

1. **工具规则要收窄**：`tools/port2native/rules/vanilla-api-1.20-to-1.21.json` 的 `libentityutils-to-libutils`（70~78 行）
   与 `libentityutils-simple`（88~96 行）会把 `LibEntityUtils` 整体重写成 `LibUtils`。现在 1.21 已有真的
   `LibEntityUtils`，这两条会把后续移植的调用点继续导去 `LibUtils`（那里还留着同名副本），使 `getTeam` 的 mixin 失效。
   **需裁定后收窄**。
2. `LibEntityUtilsMixin` **只过了编译，运行时 mixin 应用未验证**（需进游戏验证）。
3. `FILE-LAG.md` 的 `MISSING` 按**文件名**匹配（`file_lag.py:144-158`），改名/合并/内联都会算成欠账，
   引用时以本表为准；其 LAGGING/PARTIAL 段每段只列前 200 行，头部汇总才是全量。
