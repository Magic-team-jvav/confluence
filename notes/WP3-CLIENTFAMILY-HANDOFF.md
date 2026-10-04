# WP3 客户端族批次：✅ **全部完成**（2026-09-27）

> **服务端半**：`notes/WP3-WORM-SERVER-SUBSET.md`（Lib `a762fe6` + 根仓库）；**客户端半**：`notes/WP3-CLIENTFAMILY-SUBSET.md`。
> **42 处编译错全部清零**，5 条渲染器注册与克苏鲁之眼 8 处改指已接线，资源已补齐。
> 本文件保留作**溯源与执行记录**（含当初逐条诊断的修法，以及「批次启动前先空白编译估错误量」的经验）。
> 仍待做（另立批次）：史莱姆族特效渲染尾巴、毁灭者专用模型（1.20 亦为 todo）、WP7 datagen。

> ✅ **2026-09-27 更新**：**服务端半已落地**（Lib `a762fe6` + 根仓库 `notes/WP3-WORM-SERVER-SUBSET.md`）——
> 即 15 文件里的 5 个服务端文件（`BaseWormBoss`/`BossWormPart`/`EaterOfWorlds`/`TheDestroyer`/`TheDestroyerProbe`）
> 与 §3.2/§3.3/§3.4(lib 部分)/§3.6 的 17 处已清零，另含 §4 的 `BossEntities` 蠕虫 5 成员、
> `ModEntities.DESTROYER_LASER`、`MonsterEntities.EATER_OF_SOULS`、Lib `interpolateBasis` 家族。
> **本文件现在只剩「客户端半」**：`GeoNormalModel`(3) / `GeoNormalRenderer`(4) / `MissingModelRenderer`(1) + 1 warning，
> 以及 §4 的渲染器注册与改指、资源。

> 本文件是**交接单**，不是落地记录。2026-09-27 第 20 轮我启动了这一批、量清了边界、跑出并逐条诊断了
> **42 处编译错**，但判断「在本会话剩余资源内无法安全完成」→ **主动回退工作树到上一个能编译的提交**，
> 改为先落地它的一块前置（`RenderStateShardAccessor` 整类，见 `notes/WP6C-RENDERSTATE-SUBSET.md`）。
> **工作树是干净的、编译是 0 错误的**；下面是可直接照做的完整交接。

## 一、这一批要做什么（目标）

一次性解锁三件事：

1. **克苏鲁之眼 / 克苏鲁之仆**的渲染器与 8 处改指（`notes/WP3-EYE-KING-SUBSET.md` 第四节的延期项）；
2. **史莱姆族的特效渲染尾巴**（`GeoSpecialSlimeRenderer` 的尖刺×3 + `Slimer`、`TownSlimeRenderer` +
   `DivaSlimeVertexConsumer`）——与前者共用 `GeoNormalRenderer` 家族；
3. **`BossMultiplayerEnhancement`**（PortLib 的 UUID `AttributeModifier` → 1.21 record）。

## 二、精确文件表（15 个，实测并集）

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities `
  --seed …/boss/BaseWormBoss.java --seed …/boss/BossWormPart.java --seed …/boss/EaterOfWorlds.java `
  --seed …/boss/TheDestroyer.java --seed …/boss/TheDestroyerProbe.java `
  --seed …/client/entity/renderer/GeoNormalRenderer.java --seed …/client/entity/renderer/BossGeoRenderer.java `
  --seed …/client/entity/renderer/BossWormPartRenderer.java --seed …/client/entity/renderer/EyeOfCthulhuRenderer.java `
  --seed …/client/entity/renderer/MissingModelRenderer.java
# {"candidates": 15, "kept": 15, "new": 15, "removed": 0}
```

| # | 文件 | 页数/规模 |
|---|---|---:|
| 1 | `client/effect/RenderStateShardAccessor` | ✅ **已单独落地**（`notes/WP6C-RENDERSTATE-SUBSET.md`） |
| 2 | `client/entity/model/GeoNormalModel` | 3 错 |
| 3 | `client/entity/model/WormPartGeoModel` | 0 错 |
| 4 | `client/entity/renderer/BossGeoRenderer` | 0 错（但依赖 2/6/9） |
| 5 | `client/entity/renderer/BossWormPartRenderer` | 0 错 |
| 6 | `client/entity/renderer/EntityLightSampler` | 0 错 |
| 7 | `client/entity/renderer/EyeOfCthulhuRenderer` | 0 错 |
| 8 | `client/entity/renderer/GeoNormalRenderer` | 4 错 |
| 9 | `client/entity/renderer/MissingModelRenderer` | 1 错 |
| 10 | `common/entity/boss/BaseWormBoss` | 0 错 |
| 11 | `common/entity/boss/BossMultiplayerEnhancement` | **21 错** |
| 12 | `common/entity/boss/BossWormPart` | 4 错 |
| 13 | `common/entity/boss/EaterOfWorlds` | 8 错 |
| 14 | `common/entity/boss/TheDestroyer` | 1 错 |
| 15 | `common/entity/boss/TheDestroyerProbe` | 0 错 |

（staging 命令：`tools/port2native/stage_batch.py --name wp3-clientfamily … --convert --apply`；
当时输出 `0 处未覆盖规则 / 206 处人工判据 / 0 处 PortLib 残留`——206 处的绝大多数是
`.get`/`.getId` 那条过宽规则的误报。）

## 三、42 处错误与**已诊断的修法**（逐条）

### 3.1 `BossMultiplayerEnhancement`（21 处）—— ✅ **已由窄批完成**（2026-09-27，见 `notes/BOSS-MULTIPLAYER-SUBSET.md`）

> 已落地：文件改写 + `CommonConfigs` 补 `BOSS_ATTRIBUTES_MULTIPLIER_{HEALTH,DAMAGE}` +
> `EntityEvents` 补 `joinLevel` 处理器 + `LivingEntityEvents` 补 `finalizeSpawn` 调用；编译门 0/0。
> **下面的诊断保留作溯源**（它是那次改写的依据）。

1.20 版是 **PortLib 形状**，1.21 侧要整体改写（✅ 已完成，见文件头顶注）：

| 1.20 写法 | 1.21.1 实际 | 修法 |
|---|---|---|
| `private static final UUID HEALTH_MODIFIER_ID = AttributeModifier.rl2uuid(Confluence.asResource("…"))` ×3（:28/29/30） | `AttributeModifier.rl2uuid` **不存在** | 直接持有 `ResourceLocation` 常量：`private static final ResourceLocation HEALTH_MODIFIER_ID = Confluence.asResource("boss_difficulty_player_count_max_health");` ×3 |
| `new AttributeModifier(uuid, "名字", amount, op)`（:67/74/80） | 1.21 `AttributeModifier` 是 **record**：`(ResourceLocation id, double amount, Operation operation)`——**没有 name 参数** | `new AttributeModifier(ID, amount, Operation)`（顺手把 `Operation.MULTIPLY_BASE/MULTIPLY_TOTAL` 改成 `ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL` —— 这正是本批 `manual-todo` 里 `port-operation-legacy-MULTIPLY_*` 那 3 条） |
| `hasModifier(uuid)` / `getModifier(uuid)`（:66/73/79/95/97） | 吃 `ResourceLocation` | 传上面的 `ResourceLocation` 常量即可 |
| `modifier.getId()` / `.getName()` / `.getAmount()` / `.getOperation()`（:100，4 个 cannot-find-symbol） | record 访问器 `id()` / `amount()` / `operation()`（**无 name**） | 改写这行构造 `new AttributeModifier(modifier.id(), modifier.amount(), modifier.operation())` |
| `copyModifier(source, target, Attribute, UUID)`（:86/87/88 传 `Attributes.MAX_HEALTH`、`LibAttributes.getAttackDamage()`） | `getAttribute` 吃 `Holder<Attribute>`；`Attributes.MAX_HEALTH` 是 `Holder<Attribute>` ✓，但形参声明成了 `Attribute` | 把形参类型改成 `Holder<Attribute>`（:93/94 的 `source.getAttribute(attribute)` 随之通过） |

### 3.2 `EaterOfWorlds`（8 处）

| 行 | 错 | 修法 |
|---|---|---|
| 139/144 | `Holder<Attribute>` ↔ `Attribute` | `segmentAttribute(...)` 辅助的形参/返回类型改成 `Holder<Attribute>`（1.21 `getAttribute` 只吃 Holder） |
| 212 | `CombatRules.getDamageAfterAbsorb(float,float,float)` 不存在 | 1.21 签名是 `getDamageAfterAbsorb(LivingEntity entity, float damage, DamageSource source, float armorValue, float armorToughness)` → 传 `segment, amount, source, armor, 0.0F` |
| 254/255 | `LootContextParams.{DIRECT_KILLER_ENTITY,KILLER_ENTITY}` 不存在 | 1.21 改名为 `DIRECT_ATTACKING_ENTITY` / `ATTACKING_ENTITY`（`LootContextParams.java` 字段表已核对） |
| 259 | `getServer().getLootData().getLootTable(...)` | 1.21：`serverLevel.getServer().reloadableRegistries().getLootTable(...)` |
| 438 | `MonsterEntities.EATER_OF_SOULS` | **1.21 的 `MonsterEntities` 没有该成员**（类 `EaterOfSouls` 已存在）→ 按 1.20 `MonsterEntities`（「腐化：噬魂怪与腐化者」段）补 `EATER_OF_SOULS`（+ 建议同段补 `CORRUPTOR`） |
| 1013 | `lerpTo(…, int steps, boolean teleport)` 不能覆写 | 1.21 `Entity.lerpTo(double,double,double,float yRot,float xRot,int steps)`：**去掉 `boolean teleport`**，方法体里 `teleport` 分支删掉（`if (!level().isClientSide \|\| distanceToSqr(x,y,z) > 4096.0D)`） |

### 3.3 `BossWormPart`（4 处）

| 行 | 错 | 修法 |
|---|---|---|
| 404/406 | `getAddEntityPacket()` 无参不能覆写 | 1.21：`public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) { return new ClientboundAddEntityPacket(this, entity); }` |
| 409/410 | 同 `lerpTo` | 去掉 `boolean teleport` |
| 445 | `getDimensions(Pose)` final | 改覆写 `protected EntityDimensions getDefaultDimensions(Pose)`，并按 `BaseSlime.java:166` 的口径**去掉原作里多乘的 `getScale()`**（`EntityDimensions.fixed(3.0F,3.0F)` / `getType().getDimensions()` 那段） |

### 3.4 `GeoNormalModel`（3 处）

`software.bernie.geckolib.core.animatable.model.CoreGeoBone` —— **该类在 geckolib 4.8.4 已删除**
（与 G5′ 的 `GunRenderer` 同一问题）：`import` 改 `software.bernie.geckolib.cache.object.GeoBone`，
`protected CoreGeoBone head` / `getHead()` 同步改 `GeoBone`（注意 4.8.4 的 `GeoBone.getParent()` 返回具体 `GeoBone`，
若后续有 `instanceof GeoBone` 需要简化）。

### 3.5 `GeoNormalRenderer`（4 处）

| 行 | 错 | 修法 |
|---|---|---|
| 110 | `@Override` 不成立 | `preRender` 在 4.8.4 是 `(PoseStack, T, BakedGeoModel, MultiBufferSource, VertexConsumer, boolean, float, int, int, int colour)` → **把四个颜色 float 收成一个 `int colour`**（1.20 原文是 `float red, green, blue, alpha`） |
| 139 | `super.preRender(...)` 参数不符 | 同上，透传 `colour` |
| 184/198 | `applyRotations(…5 参)` **@Deprecated(forRemoval)**，而构建把 removal 当错误 | 4.8.4 同时有 **6 参**重载 `applyRotations(T, PoseStack, float, float, float, float)`，且基类渲染路径调用的是**6 参**那一版（已用 `javap -c` 确认：`invokevirtual applyRotations:(…FFFF)V`）→ **必须改覆写 6 参版**并把最后一个 float 透传给 super；只加 `@SuppressWarnings("removal")` 会让覆写变成死代码（worm 的偏航/俯仰修正会静默失效） |

### 3.6 `TheDestroyer`（1 处）

`:228` `LibMathUtils.interpolateBasis(getDeltaMovement(), targetVelocity, angle -> …, difference -> …)`：
**1.21 的 Lib 没有这个方法**。1.20 `Confluence-Magic-Lib/.../LibMathUtils.java:385` 有完整实现，
且依赖同文件的 `getLerp(double)`（:451）、`getThresholdInterpolator(double)`（:465）、`vectorProjection(Vec3, Vec3)`（:474）。
→ **需要先给 Lib 子模块补 4 个方法**（逐字搬 1.20），**Lib 提交要先于根仓库提交**。
1.21 的 `LibMathUtils` 已有 `cubicBezier`/`criticalDamageTotal` 等，但**没有** `interpolateBasis` 家族（已核对）。

### 3.7 `MissingModelRenderer`（1 处）

`:25` `renderNameTag` 在 1.21 多一个 `float partialTick` 尾参：
`renderNameTag(T entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick)`
（`EntityRenderer.java:188`）。

## 四、除 15 个文件之外的配套改动（本批已写过、**已随回退一并撤销**，需重做）

1. `common/init/entity/BossEntities`：+5 成员（1.20 `BossEntities:44/48/116/120/122`）
   —— `EATER_OF_WORLDS`、`EATER_OF_WORLDS_SEGMENT`（注册 id **`boss_worm_segment`**）、
   `THE_DESTROYER`、`THE_DESTROYER_PART`、`THE_DESTROYER_PROBE`；照 1.20 原文写属性
   （含 `LibAttributes.getArmorPenetration()` 不加 `.get()`）。
2. `common/init/ModEntities`：+`DESTROYER_LASER`（1.20 `common/init/entity/ModEntities:250`）——
   `DestroyerLaserProjectile` 类在 1.21 早已存在，**注册条目一直缺**（成员级盲区）。
3. `common/init/entity/MonsterEntities`：+`EATER_OF_SOULS`（+ 建议 `CORRUPTOR`，同在 1.20「腐化」段）。
4. `ModClientEvents`（1.20 对应行）：
   `:714` `EATER_OF_WORLDS_SEGMENT` → `BossWormPartRenderer`、
   `:715` `THE_DESTROYER_PART` → `MissingModelRenderer`（1.20 注释：专用资源待适配）、
   `:716` `EATER_OF_WORLDS` → `new BossGeoRenderer<>(c, Confluence.asResource("boss/eater_of_worlds"), true, 2.2F, 0.0F)`、
   `:723` `THE_DESTROYER` → `MissingModelRenderer`、
   `:724` `THE_DESTROYER_PROBE` → `new BossGeoRenderer<>(c, new ExplicitGeoModel<>(…))`、
   `:712` `EYE_OF_CTHULHU` → `EyeOfCthulhuRenderer`、`:713` `SERVANT_OF_CTHULHU` → `new BossGeoRenderer<>(c, Confluence.asResource("servant_of_cthulhu"))`。
5. 克苏鲁之眼的 **8 处改指**（本批需一并接上，否则 boss 会隐形）：
   `BossDelaySpawner:84/92`、`BloodMoonGameEvent:81`、`BasePotBlock:415`、`MoneyTradeHealthFull:64`、
   `ModClientBestiaryEntryProvider:574`、`LivingInvulnerableEffectsSubProvider:125`、`TreasureBagSubProvider:15`
   —— 全部 `TEBossEntities.EYE_OF_CTHULHU` → `BossEntities.EYE_OF_CTHULHU`；
   另有 `BossDelaySpawner` 的类型对齐（`AbstractTerraBossBase` → `org.confluence.mod.common.entity.boss.BaseBoss`，
   队列/迭代器/`pushBoss` 签名 4 处）与 `TEBossEntities.DEERCLOPS` → `BossEntities.DEERCLOPS`（2 处）。
6. 资源：`geo/animations/textures` 中 `eye_of_cthulhu`、`servant_of_cthulhu`、`eater_of_worlds*`、
   `the_destroyer*`（1.21 侧缺者）——**先按 1.20 全量比对再复制**（G5′/WP2 两批同法）。
7. 顺带可一起收的两项（本批不动也能独立做）：
   `CLUMSY_BALLOON_SLIME` 的 `MissingModelRenderer` 注册（1.20 `ModClientEvents:835`）、
   `GeoSpecialSlimeRenderer`/`TownSlimeRenderer`/`DivaSlimeVertexConsumer`（史莱姆尾巴）。

## 五、执行建议顺序

1. **Lib 先做**：补 `interpolateBasis` + `getLerp` + `getThresholdInterpolator` + `vectorProjection`（逐字 1.20），
   单独提交 Lib（`port(1.20 WP3 客户端族批): LibMathUtils 补 interpolateBasis 家族`）。
2. 根仓库：按 §3 逐文件修（建议顺序：`GeoNormalModel` → `MissingModelRenderer` → `GeoNormalRenderer` →
   `BossWormPart` → `EaterOfWorlds` → `TheDestroyer` → `BossMultiplayerEnhancement`），
   每修完一批跑 `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000`。
3. §4 的注册层与改指（**客户端渲染器与改指必须同批**，否则出现隐形 boss）。
4. `check_duplicates.py`（15 个新增）→ `fix_eol.py` → 提交。
5. 笔记：本文件可重写为落地记录，或新建 `notes/WP3-CLIENTFAMILY-SUBSET.md` 并在
   `notes/WORK-QUEUE.md` 的 WP3 段收口。

## 六、经验（写给下一个执行者）

* **批次启动前先量「编译错预估」**：本批 15 文件的并集看起来很便宜（服务端 5 文件），
  但把「1.20 → 1.21 的 API 变更密度」乘上去之后是 42 处，其中 21 处集中在一个 PortLib 形状的文件上。
  下次在 `dep_subset.py` 之后，可先对 seed 文件跑一次「staging + 空白编译」把错误量先露出来，
  再决定是否切批 —— 这比写完才发现划算。
* **PowerShell 不要读写 UTF-8 源码**（本会话已踩：`Get-Content -Raw` + `WriteAllText` 把中文注释按 GBK 读入 →
  乱码并吞换行）；批量改写一律 Python（显式 `encoding="utf-8"`）或 `edit` 工具。
