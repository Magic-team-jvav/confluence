# 1.21.1 群系注入 WIP 收尾记录与逐处判定

- 对象：`D:\Minecraft\1.21neoforge\confluence` 分支 `neoforge-dev/1.21.1` 上未提交的工作区改动
- 对应 1.20 侧提交：**`1ab2b43ac`「修复与TerraBlender的兼容」（2026-09-22）**
- 结论：**这份 WIP 就是把 `1ab2b43ac` 前向搬运到 1.21**——两者触及的文件集合完全一致（12 个），1.21 侧另含版本号与环境依赖差异
- 依据：`git show 1ab2b43ac --numstat` 的文件清单 vs `git diff HEAD --stat` 的文件清单；以及 14 个注入器文件的**字节级**对比

## 1. `1ab2b43ac` 做了什么（1.20 侧，供对照）

| 文件 | 改动 | 目的 |
|---|---|---|
| `BiomeSourceMixin.java` | **新增 +109** | 在 `BiomeSource#possibleBiomes` **读端**做幂等并集 |
| `InjectionProbe.java` | **删除 −41**（连同两处调用） | 调试自检用完即删 |
| `BiomeSourceHandler.java` | +10/−5 | `extraBiomes()` 由 `Stream` 改 `List`，并补文档 |
| `RegionBiomeHandler.java` | +7/−5 | 预物化 `extras` |
| `TheEndBiomeHolder.java` | +4/−4 | `Stream.of` → `List.of` |
| `BiomeRegionTable.java` | +1/−7 | 删 `biomeStream()`；构造不再 `List.copyOf(regions/entries)`；查询改 for-each |
| `ConfluenceBiomeInjector.java` | +1/−3 | 删一行 `LOGGER.info`；`regionsOf` 不再 `List.copyOf` |
| `MultiNoiseBiomeSourceMixin.java` | +10/−8 | 移除 `collectPossibleBiomes` 追加 |
| `TheEndBiomeSourceMixin.java` | +5/−9 | 移除同上 |
| `NoiseBasedChunkGeneratorMixin.java` | −3 | 删 probe 调用 |
| `confluence.mixins.json` | +1 | 注册 `BiomeSourceMixin` |
| `AskForSoftcoreScreen.java` | +5 | `isPauseScreen() → false` |
| `build.gradle` | +1 | 把 TerraBlender 运行时依赖**注释掉** |

## 2. 14 个注入器文件的逐处判定（1.20 当前状态 vs 1.21 WIP）

| 文件 | 1.20 | 1.21 WIP | 判定 |
|---|---|---|---|
| `BiomeRegion.java` | — | — | ✅ **字节一致**，无需动作 |
| `BiomeRegionAllocator.java` | — | — | ✅ 字节一致 |
| `BiomeRegionType.java` | — | — | ✅ 字节一致 |
| `BiomeSourceInjector.java` | — | — | ✅ 字节一致 |
| `IMultiNoiseBiomeSource.java` | — | — | ✅ 字节一致 |
| `BiomeSourceHandler.java` | List 化 | 同 | ✅ 字节一致 |
| `RegionBiomeHandler.java` | 预物化 extras | 同 | ✅ 字节一致 |
| `TheEndBiomeHolder.java` | `List.of` | 同 | ✅ 字节一致 |
| `BiomeSourceMixin.java` | +109（注释里引用 1.20.1 行号与实测日志） | +110（行号改 1.21.1、去掉日志引用、import 展开） | 🟦 **平台适配 P** — 同一逻辑，注释按 1.21 校正 |
| `BannedBiomeMultiNoiseBiomeSource.java` | `net.minecraftforge…ServerLifecycleHooks` + `new BlockPos(getXSpawn(), getYSpawn(), getZSpawn())` | `net.neoforged.neoforge…ServerLifecycleHooks` + `overworldData().getSpawnPos()` | 🟦 **平台适配 P** — 不要回流 |
| `NoiseBasedChunkGeneratorMixin.java` | 删 probe 调用 | 删 probe 调用（另含注解换行格式化） | ✅ 一致（含无意义格式化） |
| `MultiNoiseBiomeSourceMixin.java` | 注释写在方法上 | 注释**提升到类顶部并扩写**（TB 共存机制、为何必须 `@WrapMethod`、副作用说明） | 🟩 **1.21 改进（文档）** — 建议收割回 1.20 |
| `TheEndBiomeSourceMixin.java` | `@Mixin(TheEndBiomeSource.class)`，**无 priority** | `@Mixin(value = …, priority = 1100)` + TB 4.x 新增 `MixinTheEndBiomeSource` 的完整说明 | 🟦 **1.21 平台必需 P** — 1.20 的 TB 依赖已注释；若 1.20 恢复 TB 需同步加 |
| `BiomeRegionTable.java` | 无 `List.copyOf(regions/entries)`；查询 for-each | 仍有 `List.copyOf` ×2；查询索引循环 | 🟨 **漏搬 C** — 同属 `1ab2b43ac`，1.21 未跟进（见 §4） |
| `ConfluenceBiomeInjector.java` | 无 `LOGGER.info`；`regionsOf` 直接返回静态列表；注释写 `Forge` | 保留 `LOGGER.info`；`regionsOf` 返回 `List.copyOf`；注释写 `NeoForge` | 🟨 **漏搬 C** + 注释平台适配（见 §4） |

**注**：1.20 侧 `ConfluenceBiomeInjector.java:66` 的 `}` 缩进错位（顶格），是 `1ab2b43ac` 删日志行时留下的排版缺陷——1.21 侧是正常的。

## 3. 功能完整性核对（`1ab2b43ac` 的功能性内容是否都到了 1.21）

| 功能点 | 是否到位 |
|---|---|
| 新增 `BiomeSourceMixin`（读端并集、弱引用缓存、身份判定热路径） | ✅ 到位 |
| 在 `confluence.mixins.json` 注册 | ✅ 到位 |
| `extraBiomes()` 全部 `Stream` → `List`（3 个实现/接口一处） | ✅ 到位 |
| 两个 mixin 移除 `collectPossibleBiomes` 追加 | ✅ 到位 |
| 删除 `InjectionProbe` 及其两处调用 | ✅ 到位 |
| `NoiseBasedChunkGeneratorMixin` 清理 | ✅ 到位 |
| `AskForSoftcoreScreen` 不暂停屏幕 | ⚠️ **实现不同**（1.20 恒 `false`；1.21 前 200 tick 不暂停） |
| `build.gradle` 世界生成测试依赖 | ⚠️ **方向相反**（1.20 注释掉 TB；1.21 启用 terralith + lithostitched） |

→ 结论：**功能性内容已全部到位**；两处是 1.21 侧有意的不同实现。

## 4. 收尾时补齐 vs 留作待议

**已按"忠实搬运"补齐的项**：无（见下）。

**留作待议的两项（`BiomeRegionTable` / `ConfluenceBiomeInjector` 的漏搬 hunk）**：

这两处在 1.20 是**同一个提交**里的附带清理：
- `List.copyOf(regions)/List.copyOf(entries)` 被 1.20 删除（构造参数直接存字段）
- 索引循环改回 for-each
- 删掉一行 `LOGGER.info`
- `regionsOf` 不再返回副本

两者都不影响功能（1.21 的写法更保守：多一层防御性拷贝）。**是否补齐取决于口径**：
- 若按 Q1「1.20 是唯一事实来源、1.21 尽量零漂移」→ 应补齐（10 行以内的改动）；
- 若认为 1.21 的防御性拷贝与诊断日志更有价值 → 反向收割回 1.20，并在丢弃清单里记录。

## 5. 版本与环境差异（同属这份 WIP）

| 项 | 1.21 WIP | 说明 |
|---|---|---|
| 根 `gradle.properties` | `particlestorm_version` 1.4.3.1 → **1.4.4** | 与 3 个子模块的 `gradle.properties` 同步（子模块各自一行改动，需在其仓库内提交） |
| `ConfluenceOtherworld/gradle.properties` | `mod_version` 1.2.6 → **1.2.7** | ⚠️ 与 Q7「以 1.20 版本线为准」待对齐（后续统一处理，本次不阻塞） |
| `ConfluenceOtherworld/build.gradle` | 启用 `terralith` + `lithostitched` 的 `runtimeOnly` | 世界生成兼容性测试用。⚠️ **建议改为按 Gradle 属性开关**，否则所有人 `runClient/runServer` 都会被加载这两个模组 |

## 6. 验证结果

| 关卡 | 结果 |
|---|---|
| `:ConfluenceOtherworld:compileJava`（全部 6 个模块） | ✅ **BUILD SUCCESSFUL**（`--offline`，1m8s；在线模式会因 `maven.bawnorton.com` 返回 530 而失败，而 1.4.4 已在本地缓存） |
| 依赖解析 | ⚠️ 在线构建失败于 `ParticleStorm-neoforge-1.21.1:1.4.4` 的远程 HEAD（HTTP 530）；本地缓存已具备 `.jar/.pom/.module`，`--offline` 可正常构建 |
| 服务端运行时（全新世界） | ✅ **通过**，见 §7 |
| 客户端群系实际生成 | ⏳ 需人工（见 §7 末尾） |

## 7. 运行时验证（服务端，已完成）

命令（用 `run/` 里换 `level-name` 的方式造全新世界，跑完已还原）：
```powershell
# run/ 与 run-data/ 都在 .gitignore 中；备份 server.properties → 改 level-name=wip-injector-test → 跑 → 还原
.\gradlew.bat :ConfluenceOtherworld:runServer --console=plain --offline
```

| 观察项 | 结果 |
|---|---|
| `BiomeSourceMixin` 是否挂上 `BiomeSource` | ✅ `Mixing world.level.biome.BiomeSourceMixin from confluence.mixins.json into net.minecraft.world.level.biome.BiomeSource` |
| 另两个 mixin | ✅ 同样挂上 `MultiNoiseBiomeSource` / `TheEndBiomeSource` |
| 嵌套 record `Entry` 在 mixin 里能否成立（本次最大未知数） | ✅ Mixin 正确识别并映射：`Inner class BiomeSourceMixin$Entry … gets unique name BiomeSource$Entry$2a507a5d…` / `Generating mapped inner class …` |
| mixin 应用失败 / 注入点异常 | ✅ 无（`apply failed` / `MixinApplyError` / `InvalidInjectionException` 零命中） |
| 注入器 bootstrap | ✅ `[Confluence/]: Registered 3 overworld biome regions and 2 nether biome regions` |
| 区域接管是否真的发生 | ✅ 三条：`glowing_mushroom took over at block (15640, 0, 7256)`、`the_crimson took over at block (-352, 0, 20872)`、`the_corruption took over at block (7536, 0, -30264)` |
| 回落分支 | ✅ `the_crimson owns column (19576, 6776) but no parameter box covers …; falling back to vanilla` |
| 世界生成完成 | ✅ `Preparing spawn area: … 51%` → `Time elapsed: 24610 ms` → `Done (54.601s)!` |
| 异常 | ✅ 无本项目相关异常（日志里的 NPE/IllegalState 均来自 moonlight/quark/supplementaries 的颜色集与 promo 缓存噪音） |

**尚未直接观测的一项**：自定义群系内**地物是否生成**（即 `possibleBiomes()` 并集是否真的让 `applyBiomeDecoration` 的 `retainAll` 保留我们的群系）。原因是出生点（±176 格）内是原版群系，而日志里三次接管发生在 (15640, 7256)、(15640 之外) 等远处。推荐人工复核方式：
1. `runClient` 进新世界，`/locate biome confluence:the_corruption`（能返回坐标即说明并集生效——这正是修复前会失败的场景）；
2. 或飞到日志里那些接管坐标附近，确认自定义群系内有地物与地表规则。

**本次验证用的临时改动均已还原**：`run/server.properties` 的 `level-name` 恢复为 `world`；测试世界 `run/wip-injector-test/` 与临时 `run/eula.txt` 已删除。
