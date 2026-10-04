# Boss 多人/难度属性强化接入（`BossMultiplayerEnhancement`，PortLib → 1.21 record）

> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**。规模：**1 新文件 + 3 改文件**。
> 本批是从 `notes/WP3-CLIENTFAMILY-HANDOFF.md` 里**单拆出来的自包含窄批**（交接单 §3.1 的 21 处错误）。

## 一、为什么单拆

它曾是「WP3 客户端族批」里错误最密集的一个文件（**15 文件并集里的 42 处错误，它独占 21 处**）。
但它**与前缀依赖（`GeoNormalRenderer`/蠕虫 boss 族）无关**，且**没有任何隐形风险**（不改客户端渲染），
所以先把它清掉最划算：交接单上少一块，且**顺带补上了一个从未接入的玩法特性**。

⚠️ **它不是纯搬文件**：`BossMultiplayerEnhancement` 在 1.21 侧**此前完全不存在**，所以
「多人/难度 Boss 属性强化」这个玩法在 1.21 一直是**关闭**的（§三）。

## 二、内容

| 文件 | 处理 |
|---|---|
| `common/entity/boss/BossMultiplayerEnhancement`（新，104 行） | 1.20 原文逐条改写（§二·1 的 5 类差异），文件头已写清每条改写依据 |
| `common/CommonConfigs` | +2 项：`BOSS_ATTRIBUTES_MULTIPLIER_HEALTH` / `_DAMAGE`（1.20 `bossAttributesMultiplierHealth/Damage`，默认 `1.0`、范围 `0.0625~10.0`）——**成员级盲区**：`apply(...)` 读 HEALTH，1.21 此前没有该配置项 |
| `common/event/game/entity/EntityEvents` | 新增 `@SubscribeEvent joinLevel(EntityJoinLevelEvent)`（1.20 `EntityEvents:52`）——**1.21 此前没有这个处理器** |
| `common/event/game/entity/LivingEntityEvents` | 在 `finalizeSpawn` 的 `GamePhase2AttributeModifiers.applyModifiers(mob)` 之后补 BMPE 调用（1.20 `:580`） |

### 2.1 1.20 → 1.21.1 的五类差异（逐条，均已写进文件头）

| 1.20 写法 | 1.21.1 实际 | 处理 |
|---|---|---|
| `PortAttributeModifier.rl2uuid(Confluence.asResource("…"))` 把 id 转成 **UUID**（×3） | 1.21.1 的 `AttributeModifier` 是 **record `(ResourceLocation id, double amount, Operation)`**；`hasModifier`/`getModifier` 也只吃 `ResourceLocation` | 三个常量直接持有 `ResourceLocation` |
| `new AttributeModifier(uuid, "显示名", amount, op)` | record **没有 name 参数** | 去掉显示名（这些修饰符挂在 Boss 实体上、不进物品 tooltip，仅影响调试输出） |
| `Operation.MULTIPLY_BASE` / `MULTIPLY_TOTAL` | 1.21.1 枚举改名 `ADD_MULTIPLIED_BASE` / `ADD_MULTIPLIED_TOTAL`（语义一致） | 改名 |
| `getAttribute(Attribute)`（PortLib 包装可直接 `.get()`） | 只接受 `Holder<Attribute>` | `copyModifier` 形参改 `Holder<Attribute>`；`LibAttributes.getAttackDamage().value()` → 直接传（1.21 的 Lib 已返回 `Holder<Attribute>`） |
| `modifier.getId()/getName()/getAmount()/getOperation()` | record 访问器 | `modifier.id()/amount()/operation()` |

## 三、接线（这是「特性从关到开」的关键）

1.20 有**两条**生成路径都会调它，1.21 **两条都没有**：

| 路径 | 1.20 位置 | 1.21 现状 | 本批 |
|---|---|---|---|
| `finalizeSpawn`（自然生成 / 召唤物） | `LivingEntityEvents:580` | 只有 `GamePhase2AttributeModifiers` | ✅ 补上 |
| 实体加入世界（**区块恢复**、脚本生成、直接 `addFreshEntity`） | `EntityEvents:52 joinLevel` | **处理器整个不存在** | ✅ 端口（Boss 部分） |

判定条件是 `boss.isMainBody() && boss.shouldEnhanceMultiplayer()`：
`Boss` 接口的 `isMainBody()`/`shouldEnhanceMultiplayer()` 默认都返回 `true`，`BaseBoss:192` 也显式覆写为 `true`
→ **对所有主身体 Boss 生效**（与 1.20 一致）。幂等性由「先查 `hasModifier` 再加」保证，
所以两条路径都调、区块反复恢复也不会叠加，也不会把受伤的 Boss 重新回满血（除配置倍率那条会 `setHealth(getMaxHealth())`，与 1.20 相同）。

⚠️ 1.20 `joinLevel` 的**前一半**是 `MonsterAttributeScaling.apply(living, !event.loadedFromDisk())`，
而 1.21 侧 `MonsterAttributeScaling` 尚未落地（属 WP2 剩余物种）→ **本批只接 Boss 部分**，
那一半随 `MonsterAttributeScaling` 批次补回（已在 `EntityEvents.joinLevel` 的 javadoc 里标注）。

## 四、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（一次通过） |
| 接线确认 | `EntityEvents:57` 与 `LivingEntityEvents:463` 各 1 处 `BossMultiplayerEnhancement.apply(...)` |
| `check_duplicates.py` | `[SELF]`（同 FQN 移植目标），0 处疑似移动/重复 |
| 子模块 | 未改动 |
| 待游戏内验收 | 经典/专家/大师难度下 Boss 血量 0.66/1.0/1.5 倍；多人时血量按参战人数（上限 8）放大；攻击伤害只随难度变化；`BOSS_ATTRIBUTES_MULTIPLIER_HEALTH` 配置生效（改配置后 Boss 血量随之变化）；**区块反复重载不叠加**；召唤物/事件脚本/直接生成的 Boss 同样吃到倍率 |

## 五、交接单状态更新

`notes/WP3-CLIENTFAMILY-HANDOFF.md` 的 **§3.1（21 处）已由本批完成**，该文件剩余部分
（22 处：`GeoNormalModel` 3、`GeoNormalRenderer` 4、`BossWormPart` 4、`EaterOfWorlds` 8、
`TheDestroyer` 1、`MissingModelRenderer` 1 + 1 处 warning）+ §4 的配套改动仍然待做。
