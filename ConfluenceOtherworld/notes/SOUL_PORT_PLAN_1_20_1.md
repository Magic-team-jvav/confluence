# 魂师内容 1.21.1 → 1.20.1 Forge 迁移清单

来源：1.21.1 NeoForge 侧本对话的全部魂师改动（`confluence/ConfluenceOtherworld`）。
目标：`confluence-1.20.1/ConfluenceOtherworld`（Forge 47.x + PortLib + ParticleStorm-forge + Curios）。

## 0. 现状判定

1.20.1 侧**已有魂师基础框架**，且与 1.21.1 改动前基本同构：`ModSoulSkills`（PortLib 自定义注册 + `sync(true)`）、`SoulStorage`/`SoulSkillStorage`、6 个灵魂事件、`SoulSkillParameters`、5 个技能类（引魂/附魂/激荡/求生欲）、`SoulLureProjectile`、`SpiritSurgeEntity`、`SoulItems`、`SoulOverviewScreen`、`SoulSkillBox`、`SoulLanguageSubProvider`、`ModSoulSkillTagsProvider`、`EnhancedSoulEffect`、`SOUL_SKILL` 数据组件、`ModRarity`。

因此本次是**补齐 1.21.1 的增量**，不是从零搭框架。

## 1. 缺口清单

### 1.1 需要新建（18 个 Java 文件）

```
common/effect/soulcerer/GoOverEffect.java
common/item/soulcerer/SoulBooks.java
common/item/soulcerer/SoulLanternItem.java
common/item/soulcerer/SoulKnifeItem.java
common/entity/soulcerer/SoulStingProjectile.java
common/soulcerer/skill/SoulProjectileSkill.java
common/soulcerer/skill/SoulBuffSkill.java
common/soulcerer/skill/SoulSurgeSkill.java
common/soulcerer/skill/SurgeBlastSkill.java
common/soulcerer/skill/PassiveSoulSkill.java
common/soulcerer/skill/KarmaFlameSkill.java
common/soulcerer/skill/SoulGunnerSkill.java
common/soulcerer/skill/BoilingBloodSkill.java
common/soulcerer/skill/StatefulSoulSkill.java
common/soulcerer/skill/NaturesWrathSkill.java
common/soulcerer/skill/ProfaneSoulSkill.java
client/model/entity/projectile/SoulStingProjectileModel.java
client/renderer/entity/projectile/SoulStingProjectileRenderer.java
```

### 1.2 需要改动（44 个 Java 文件）

分五组，按组推进可以保证每步都能编译：

| 组 | 文件 | 内容 |
| --- | --- | --- |
| A 核心 | `common/soulcerer/skill/SoulSkill.java`、`SoulSkillParameters.java`、`common/item/soulcerer/SoulSkillBookItem.java`、`ModSoulSkills`、`ModEffects`、`ModTags`、`ModEntities` | 进度类型 → `Kind`（主动/附魂/被动）、说明 tooltip、31 技能注册、新效果、附魂/被动标签、毒刺实体 |
| B 玩家数据 | `common/attachment/SoulStorage.java`、`SoulSkillStorage.java`、`util/PlayerUtils.java` | 仪式次数/栏位、套装字段、技能状态、被动钩子、创造模式豁免、栏位派生、第一/三/五次仪式解锁栏位 |
| C 事件 | `common/event/game/TickEvents.java`、`entity/LivingEntityEvents.java`、`common/event/ModEvents.java` | 技能状态 tick、召唤物被动、血液沸腾、天穹倒转、生命归还、饰品战斗组 |
| D 内容 | `common/init/item/{SoulItems,AccessoryItems,ArmorItems}.java`、`common/init/armor/ModArmorBonus.java`、`common/init/{ModCommands,ModTabs,ModVillagers}.java` | 8 魂灯、祈灵之刃、5 饰品、7 套装甲与套装、3 条指令、创造栏、交易 |
| E 数据生成与客户端 | `common/data/gen/**`（tag/model/lang/loot/recipe）、`client/event/{ModClientEvents,ModClientSetups}.java`、`client/gui/hud/TerraStyleManaHud.java`、`client/gui/container/soulcerer/SoulOverviewScreen.java` | 标签、模型覆盖、中英文、战利品、配方、书皮属性、灵魂条配色、总览节点 |

### 1.3 资源（纯拷贝）

- `textures/item/soul_lantern/*`（8 张 + 7 份 `.mcmeta`）
- `textures/item/skill_book/{psychopomp,raiders_path,soul_surge,harvester,firmament_reversed,blood_world_wrath}.png`
- `textures/item/sword/spiritward_blade.png`
- `textures/item/accessory/*`（5 张 + 1 份 `.mcmeta`）
- `textures/mob_effect/{go_over,blood_rage,soul_plunder,soul_drain,soul_drain_lock,star_call,life_return,firmament_reversed,boiling_blood}.png`
- `textures/gui/sprites/hud/soulcerer_skill/*`（新增 8 张问号占位 + 既有 23 张比对）
- `textures/particle/soul_projectile.png`
- `particle_definitions/{soul_projectile,soul_expiration}.particle.json`
- `ageratum/zh_cn/detailed_information/command/command_list.md` 的指令段落

## 2. 版本 API 差异（迁移时必改点）

| 1.21.1 NeoForge | 1.20.1 Forge | 说明 |
| --- | --- | --- |
| `net.neoforged.neoforge.*` | `net.minecraftforge.*` | 包名整体替换 |
| `NeoForge.EVENT_BUS` | `MinecraftForge.EVENT_BUS` | 事件总线 |
| `LivingIncomingDamageEvent` | `LivingHurtEvent` + `LivingDamageEvent` | 1.20.1 没有统一入口，需要拆成“伤害前倍率”和“伤害后结算”两处；魂师被动里的召唤物增伤、血液沸腾走 Hurt，生命归还记录走 Hurt |
| `AttackEntityEvent` | 同名（`net.minecraftforge.event.entity.player`） | 祈灵之刃贯穿可直接搬 |
| `MobEffect#addAttributeModifier(Holder, ResourceLocation, double, Operation)` | `addAttributeModifier(Attribute, UUID, double, Operation)` | 效果属性修饰符签名不同，`ModEffects` 新效果按 1.20.1 写法重写 |
| `guiGraphics.blitSprite(...)` | 1.20.1 无 `blitSprite` | `SoulSkillBox` / HUD / 总览的图标绘制要改成 `blit(texture, x, y, u, v, w, h)` |
| `DeferredHolder`、`DeferredRegister` | 同名（Forge） | 仅 import 变化 |
| `IAttachmentHolder` 附件 | 1.20.1 已通过 PortLib 提供同款附件 | 直接沿用现有 `SoulStorage` 写法 |
| 自定义注册 `makeRegistry(sync)` | `PortCustomRegistration`（PortLib） | `ModSoulSkills` 的注册方式保持不变 |
| `SharedConstants.TICKS_PER_SECOND` | 同名 | 无差异 |

## 3. 建议推进顺序（每步结束都能编译）

1. **阶段 1 — 纯新增类**：18 个新文件（只 import 改名）+ 资源拷贝。不动注册表，编译应保持绿色。
2. **阶段 2 — 核心注册**：`SoulSkill` 的 `Kind` 与说明 tooltip、`SoulSkillParameters`、`ModSoulSkills`（31 技能）、`ModEffects`（8 效果）、`ModTags` + 标签生成器、`ModEntities`（毒刺）。
3. **阶段 3 — 玩家数据与逻辑**：`SoulStorage`、`SoulSkillStorage`、`PlayerUtils`（仪式、栏位、被动、创造模式豁免、状态 tick）。
4. **阶段 4 — 事件层**：`TickEvents`、`LivingEntityEvents`（把 NeoForge 的两个伤害事件拆成 Forge 的三段）、`ModEvents`。
5. **阶段 5 — 内容**：魂灯 8 件、祈灵之刃、5 饰品、7 套装甲与套装、3 条指令、创造栏、交易。
6. **阶段 6 — 数据生成与客户端**：tag/model/lang/loot/recipe、书皮 item property、灵魂条配色、总览 7 个新节点；最后 `runData` + `compileJava` 双绿。

## 4. 风险与注意

- `runData` 产物（`src/generated`）在 1.20.1 同样是构建产物，不要入库。
- 1.20.1 的物品栏/快捷栏 HUD 绘制与 1.21.1 差异最大的是 `blitSprite`，阶段 6 单独处理，避免污染核心逻辑。
- 1.20.1 的 `SoulSkillBookItem` 目前仍是“用一次消耗一本”，迁移到“可反复阅读 + 首用解锁/后续复习”时注意同步 `SoulSkillStorage` 的 `learnedSoulBooks` 字段。
- 1.20.1 没有 1.21.1 的 `DataComponents.ATTRIBUTE_MODIFIERS` 同款写法差异（PortLib 已提供），但 `ModItems.attributes(...)` 的工具方法需要逐一确认存在。
- 全部内容迁移完成后仍需游戏内实测（1.21.1 侧也未实测）。
