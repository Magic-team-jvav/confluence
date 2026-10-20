# 魂引天星改用星怒坠星外观 / 魂术轮转跳过冷却

## 1. 魂引天星：弹幕外观换成星怒的坠星，去掉粒子

以前 `PlayerUtils#tickStarCall` 生成的是通用引魂弹幕，并传 `SoulLureProjectile.STAR_PARTICLE`（`confluence:falling_star` 粒子特效）。

现在：

- `SoulLureProjectile` 新增常量 `STAR_APPEARANCE`，参数与 `SwordItems#STARFURY` 的 `SwordProjectileAppearance.Cross` 完全一致：
  - 贴图 `confluence:textures/entity/star_fury_projectile.png`
  - 颜色 `0xFFFF9696`、缩放 `2.0`、自转 `18°/tick`、方块光 `10`
- 新增同步标记 `DATA_STAR_APPEARANCE`（`EntityDataAccessor<Boolean>`），由 `star(...)` 工厂方法在生成时置位；
  引魂弹幕本身没有 spawn 同步数据，所以外观必须走 `SynchedEntityData` 才能在客户端生效。
- 客户端新增 `SoulLureProjectileRenderer`（替换原来的 `NoopRenderer`）：只在标记为真时绘制那对交叉面片；
  其他引魂弹幕仍然不画任何模型（外观继续交给粒子风暴）。
- `tick()` 里客户端粒子生成器多了 `!isStarAppearance()` 判定，魂引天星不再有任何粒子，
  原来的 `falling_star` 粒子一并去掉。
- `STAR_PARTICLE` 常量删除，避免误用；`FIREBALL_PARTICLE`（亵渎之魂）保持原样。
- 伤害、耗魂、命中判定、射程与年龄全部不变：仍然是 `LibDamageTypes.soul` 的灵魂伤害，不是剑气伤害。

渲染复用：`SwordProjectileRenderer#renderCross` 抽成 `public static`，剑气与坠星共用同一段面片代码。

## 2. 魂术轮转：自动跳过冷却中的技能

`PlayerUtils#rotateToNextSkill` 开始过滤「空槽 / 被动 / 冷却中」。

> **后续修正**：这一轮只改了 `rotateToNextSkill`，实机验证后发现技能栏主路径（强力附魂系列连锁结束回到起手技能、
> 以及按键时当前技能在冷却）仍然停在冷却技能上。现在的完整实现见
> `SOUL_ROTATION_SKIP_COOLDOWN_FIX.md`（三处入口都跳过冷却，并附实机日志）。

```java
if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()) continue;
/// 冷却中的技能直接跳过，轮转始终落在能马上施放的槽位上。
if (getSoulSkillCooldown(player, candidate.getSoulSkill().getId()) > 0) continue;
setCurrentSoulSkillIndex(player, index);
```

- 全部技能都在冷却时，循环不会命中任何槽位（`step` 绕回自己时同样被冷却挡住），选中位保持不动；
- 「强力附魂系列」的连锁（`chainCastEnhancedSoulSeries → castSoulSkillIfReady`）本来就跳过冷却，行为不变；
- 「当前槽为空/被动时按施法键跳到下一个」（`findNextCastableSoulSkillIndex`）**没有改动**，
  仍按 `SOUL_ROTATION_MEMORY_HOTBAR.md` 的设计落在冷却中的技能上。

## 3. 两版同步的文件

```
common/entity/soulcerer/SoulLureProjectile.java                       星怒外观常量 + 同步标记 + star() 工厂
client/renderer/entity/projectile/SoulLureProjectileRenderer.java     新增，替换 NoopRenderer
client/renderer/entity/projectile/sword/SwordProjectileRenderer.java  renderCross 抽成 public static
client/event/ModClientEvents.java                                     SOUL_LURE_PROJECTILE 注册新渲染器
util/PlayerUtils.java                                                 tickStarCall 用 star()；rotateToNextSkill 跳过冷却
```

两版的差异只有 `defineSynchedData` 签名（1.21.1 带 `SynchedEntityData.Builder`，1.20.1 直接写 `entityData`）
和 `vertex()` 的顶点 API（1.21.1 `addVertex`/`setUv`，1.20.1 `vertex`/`endVertex`）。

## 4. 仍未做

两版都没有进游戏实测星怒面片：它在魂师弹幕上的大小/自转观感、坠星从 8 格高处落下时的可见距离。
（轮转跳冷却部分已在专用服务器上验证，见 `SOUL_ROTATION_SKIP_COOLDOWN_FIX.md`。）
