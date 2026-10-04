# WP7 批次 B · 4 个自包含工具/数据叶子 + ⚠️ 「`AbstractTreeGrower` 在 1.21 已被删除」的实测

## 一、本批落地（4 个新文件 / 172 非空行，转换器直出，无既有文件改动）

| 文件 | 非空行 | 内容 |
|---|---:|---|
| `common/data/BrushData` | 111 | 刷扫（考古）时的方块旋转/镜像数据，纯 vanilla + `ShimmerDecompositionInputs` 之外的零外部依赖 |
| `common/data/DateStamp` | 28 | 日期戳（`RecordCodecBuilder`，纯 mojang codec） |
| `common/data/StarPhase` | 30 | 星相（`ByteBuf` + `CompoundTag` 序列化） |
| `common/recipe/ShimmerDecompositionInputs` | 34 | 微光分解的输入集合（`ModTags` + `ItemStack`） |

```powershell
python tools/port2native/stage_batch.py --name wp7b ... --convert --apply
# 暂存 6 个文件 / 252 非空行；filesChanged=0；uncovered.json = []、leftovers.txt 0 行
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 第一轮：7 处错误，全部在下面那 2 个文件里（见第二节），本批已移除
# 第二轮：总错误数 0，涉及 0 个文件；[build] exit=0
```

## 二、⚠️ `AbstractTreeGrower` / `AbstractMegaTreeGrower` 在 1.21 **已被删除**

本批原计划 6 个文件，其中 `common/block/natural/SimpleTreeGrower`(17) 与
`SimpleMegaTreeGrower`(32) 编译失败（`cannot find symbol` 在 import 行 + 覆写签名不匹配）：

```java
import net.minecraft.world.level.block.grower.AbstractTreeGrower;      // 1.21 不存在
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;  // 1.21 不存在
```

实测 1.21 的 `build/_nfsrc_219/net/minecraft/world/level/block/grower/` 目录里**只有** `TreeGrower.java`，
而且是 **`public final class TreeGrower`**（`TreeGrower.java:24`）—— 1.20 那套「继承抽象基类 + 在
`getConfiguredFeature(RandomSource, boolean)` 里按随机数挑特征」的写法**整体没了**；1.21 改成一个
由构造器参数（name + mega/tree/flowers 三组 `Optional<ResourceKey<ConfiguredFeature<?,?>>>`）声明的
**实例**，外加静态注册表 `GROWERS` + `Codec.stringResolver`。

**结论：这两个文件不能按 1.20 形态移植**，必须在「1.21 的 `TreeGrower` 实例 + 数据驱动」这一层重新表达
（每个 1.20 自定义 grower 的随机多特征逻辑，要逐个决定映射到哪几个 `ConfiguredFeature` 键）。
本批把它们**从工作树移除**（不留半成品），并把结论记在这里，供树苗/世界生成批次（WP7）按 1.21 形态重做。

> 这是本会话第 3 处「1.21 侧机制整体换掉、不能照搬」的实测（前两处：附魔 `EnchantmentCategory` →
> 数据驱动；`BaseEntityBlock#codec()` 变抽象）。

## 三、本批的边界

4 个叶子都是**自包含数据/工具类型**（闭包 1、零扩张、无 PortLib 词汇），没有注册与接线需求；
它们的使用方（微光分解配方、刷扫系统、日期/星相判定）随对应内容批次接入，
本批不含任何既有文件改动、编译 0 错误、无半成品。

## 四、下一步（顺序不变）

1. **WP5 核心 28 文件**（`notes/WP5B-SUBSET.md`）：前置三块已就位（`VEC_3`@`95133c3`、
   `Immunity.isActive/apply`@`e3820ac55`、召唤粒子层@`d4c9d56df`）；开工清单剩 3 步。
2. **`CreatureSpawnPlacements` 动物半边**（口径见 `notes/WP5C-SUBSET.md` 第三节）。
3. **WP4 NPC 基座**；4. WP7 数据生成批次（含树苗/树生成按 1.21 `TreeGrower` 形态重做、接 `notes/WP7A` 的两个战利品条件）；5. 枪械功能批次。
