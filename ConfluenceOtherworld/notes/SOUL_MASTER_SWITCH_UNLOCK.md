# 魂师总开关解锁（打包环境可用）

## 原来为什么打包版没有魂师

`Confluence.SOUL_SKILLS` 是魂师的总开关，原先写的是：

```java
public static final boolean SOUL_SKILLS = LibUtils.isDev();   // = !FMLEnvironment.production
```

于是打包（production）环境里它恒为 `false`，被它守着的整块机制全部静默：

| 位置 | 被挡掉的行为 |
| --- | --- |
| `PlayerUtils#tryUseSoulSkill` / `tryUseSoulArtifact` / `switchSoulMode` | 施法、法器、灵魂↔魔力切换 |
| `PlayerUtils#tickSoulSkills` / `tickSoulSkillStates` / 收魂 / 首得解锁 | 冷却推进、魂引天星等持续效果、收魂 |
| `SoulSkillRequestPacketC2S` / `SoulSkillIndexRequestPacketC2S` | 技能请求与索引同步包 |
| `ModKeyBindings`（1.20.1 含总览、快捷切换、施法；1.21.1 含施法） | 技能相关按键注册 |
| `MinecraftMixin` 快捷法器 | 数字键使用法器 |
| `ClientConfigs` 的 `soulStyle` 分组（1.20.1） | 灵魂条样式配置 |

物品、技能、实体、标签、创造栏本来就无条件注册，所以打包版是“东西都在、但用不了”。

## 现在

```java
/// 魂师总开关：控制施法、灵魂/魔力模式切换、法器与技能同步等全部魂师机制。
/// 原先跟随 LibUtils.isDev()，打包（production）环境里整个魂师玩法不可用；
/// 现已解锁为常开，打包环境同样启用。需要临时关掉时把这里改回 LibUtils.isDev()。
public static final boolean SOUL_SKILLS = true;
```

- 两版同改（1.21.1 / 1.20.1）；
- 只动魂师这一个开关，`THE_END_BIOMES` / `UNRELEASED_SPAWNS` / `UNRELEASED_EVENTS` / `DATA_TIP`
  等其它未发布内容仍按开发开关处理；
- 这是编译期常量，因此 `if (!Confluence.SOUL_SKILLS)` 这类守卫会被直接常量折叠掉，没有运行时开销；
  代价是不能用配置在运行时开关。若之后需要给整合包一个可切换的开关，应改成
  `CommonConfigs` 条目 + 非 final 字段，并注意 `ModKeyBindings`、`ClientConfigs` 这些
  在静态初始化里读它的地方必须等配置加载完成（现在没有做，属于独立改动）。

## 打包环境需要自查的点

1. 客户端按键列表里应出现“施放魂师技能”（1.20.1 还有“魂师总览”“快捷技能切换”）；
2. 手持魂师主武器右键能施法，技能框冷却正常走；
3. 堕魂星核右键能在灵魂/魔力模式间切换；
4. 1.20.1 客户端配置里有 HUD → Soulcerer 分组与灵魂条样式项；
5. 专用服务器上不应因为魂师内容报错（本轮已在开发环境专用服务器上跑过施法与轮转，未发现 common 侧引用客户端类的问题）。

## 同步的文件

```
src/main/java/org/confluence/mod/Confluence.java   SOUL_SKILLS 解锁（两版）
notes/SOUL_DEVELOPMENT.md                          第 1、4 节措辞同步（仅 1.21.1 有该文档）
notes/SOUL_STYLE_CONFIG.md                         第 4 节补注开关已解锁（两版）
```
