# WP2 批次 12 · 被 18 个重量物种共享的 5 个数据/事件文件

> 对象：`common/data/GamePhase`(60) `common/data/MoonPhase`(38) `common/data/saved/AnglerData`(83)
> `common/data/AnglerQuestLoader`(133) `common/gameevent/SolarEclipseGameEvent`(54) = 368 非空行。

## 一、为什么选这 5 个

第 8 轮测得：WP2 剩下的 18 个重量物种闭包都是 189（报告 `notes/WP2K-CLOSURES.md`），
共同前置是 NPC 层（`TownSlimeNPC` → `BaseNPC`），而 NPC 层自己也拖着 boss/枪械。
直接啃 189 文件不现实，于是按「先消公共底座」的办法找**自包含**的小片：

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed …/data/GamePhase.java --seed …/data/MoonPhase.java --seed …/data/saved/AnglerData.java `
  --seed …/data/AnglerQuestLoader.java --seed …/gameevent/SolarEclipseGameEvent.java
# {"candidates": 5, "kept": 5, "new": 5, "removed": 0}   ← 完全自包含，零扩张
```

这 5 个类型正是 `seed_closures.py` 的公共底座表里反复出现的那几个（`GamePhase` / `MoonPhase` /
`AnglerQuestLoader` / `AnglerData` 在 189 里被计入）。**实测效果**：落地后重跑同一命令，
18 个重量种子的闭包从 **189 降到 185**（`hubs` 189 → 185，`min_new`/`max_new` = 185/187）。

## 二、本批挖出的两处坑（都不是机械活）

### 1. `GamePhase` 是跨分支的**路径搬迁**，且 1.20 侧同包无 import

| | 路径 | 枚举常量 |
|---|---|---|
| 1.20 | `org.confluence.mod.common.data.GamePhase` | `BEFORE_SKELETRON(0)` … `MOON_LORD(600)` 共 7 个 |
| 1.21（本来就有） | `org.confluence.mod.common.data.saved.GamePhase`（64 行，含 NeoForge 的 `IExtensibleEnum`/`NetworkedEnum`） | **逐条相同** |

处理：**保留 1.21 的路径**（不搬、不覆盖），把 `common/data/GamePhase.java` 从暂存里删掉；
以后 `dep_subset.py` 跑 1.20 侧代码时要带
`--alias org.confluence.mod.common.data.GamePhase=org.confluence.mod.common.data.saved.GamePhase`。

由此带出第二个坑：1.20 的 `AnglerQuestLoader` 与 `GamePhase` **同包**，所以源码里**没有 import**；
搬到 1.21 后必须补 `import org.confluence.mod.common.data.saved.GamePhase;`。
**凡是 1.20 里位于 `common/data/` 且用了 `GamePhase` 的文件，逐个都要看这一点**
（批次 11 的 `PirateInvasionGameEvent` 用到了 `GamePhase`，但它自己 import 了，所以没踩到）。

### 2. 转换器漏网：ForgeRegistries 的 import 被删掉、调用点却留着，`leftovers` 还没报

`AnglerData` 里两处 `ForgeRegistries.ITEMS.getValue/getKey`：转换器**删掉了**
`net.minecraftforge.registries.ForgeRegistries` 这个 import（属于「Forge 包一律不移植」的通用处理），
但**调用点没改写**，而 `_report/leftovers.txt` 是**空的** —— 即工具在这一类上不会报警，
只有编译门槛抓到了（`AnglerData.java:73,79: package ForgeRegistries does not exist`）。
已按 1.21 原生写法改成 `BuiltInRegistries.ITEM.get(...)` / `.getKey(...)`（+ `net.minecraft.core.registries.BuiltInRegistries`）。

**没有为它加规则**：1.20 侧 `ForgeRegistries.<注册表>.<方法>` 一共 16 处、方法名各不相同
（`getValue` 9 / `getKey` 3 / `getValues` 1 / `getEntries` 1 / `getCodec` 1 / `getDelegateOrThrow` 1），
其中 `MOB_EFFECTS.getDelegateOrThrow` 在 1.21 对应的是 **Holder** 形态（`getHolderOrThrow`），
一条正则覆盖不了、写错反而把「编译器能抓」变成「静默错误」。清单如下，供后续批次按需手改：

```
4  ForgeRegistries.ENTITY_TYPES.getValue      1  ForgeRegistries.MOB_EFFECTS.getValues
2  ForgeRegistries.ITEMS.getValue             1  ForgeRegistries.ENTITY_TYPES.getKey
2  ForgeRegistries.BLOCKS.getKey              1  ForgeRegistries.SOUND_EVENTS.getValue
2  ForgeRegistries.ITEMS.getKey               1  ForgeRegistries.BLOCKS.getEntries
1  ForgeRegistries.MOB_EFFECTS.getCodec       1  ForgeRegistries.MOB_EFFECTS.getDelegateOrThrow
```

## 三、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器：`uncovered.json` = `[]`、`leftovers.txt` 空（**注意本批证明这两项为空不代表能编译**，见第二节第 2 点）。
`ForgeRegistries` 残留：0（两处已手改）。重复类自查：`GamePhase` 全仓只有
`common/data/saved/GamePhase.java` 一份（暂存时误建的 `common/data/GamePhase.java` 已删）。
闭包效果：18 个重量种子闭包 189 → **185**。
