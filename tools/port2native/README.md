# tools/port2native —— PortLib → NeoForge 1.21.1 源码转换器

计划（`notes/PORTLIB_ARCH_MIGRATION_PLAN.md`）**Phase 2** 的核心工程产物：把 1.20.1
分支上"用 `org.mesdag.portlib.*` 词汇写的源码"转换成 1.21.1 上的**原生 NeoForge / Minecraft
代码**，让"每轮整体回移"从"人工重写上千文件"降级为"跑脚本 + 收敛编译错误"。

## 为什么可行

1.20 侧自 `part1`–`part25` 起已把源码统一到 Port 词汇（主模组 474/2496 文件、Magic-Lib 59/266、
TerraCurio 53/206、TerraFurniture 20/114 直接 import PortLib），因此语义到类型的映射是**有限且可枚举**的，
不会退化成"同一件事有十种写法"的自由发挥。

## 用法

```powershell
python tools/port2native/port2native.py `
    --source D:\Minecraft\1.20forge\confluence\TerraFurniture\src `
    --out    $env:TEMP\port2native\TerraFurniture `
    --rules  tools\port2native\rules
```

可选参数：`--only <路径子串>`、`--limit N`、`--report <目录>`（默认 `<out>/../_report`）。

## 三层输出（验收口径）

| 产物 | 含义 | 通过标准 |
|---|---|---|
| `<out>/` 转换后的源码树 | 只写出**有改动**的文件，未改动的文件不落盘 | 人工抽查 + 编译 |
| `_report/uncovered.json` | **没有规则可依**的 PortLib 导入、通配包、缺少 native 的规则、import 同名冲突 | **必须为空**才能进入编译阶段 |
| `_report/manual-todo.md` | `manual`/`drop` 类型、`review` 调用点、**非 high 置信度**的类型映射 | 逐条人工裁决，改完后应清空 |
| `_report/leftovers.txt` | 转换后仍残留 `org.mesdag.portlib` / `PortLib.*` / `Port*`/`IPort*` 标识符的位置 | 与 manual-todo 对齐（残留=待人工处理处） |
| `_report/stats.json` | 文件数、重写计数（按规则分类） | 用于估算工作量与回归 |

## 规则目录

引擎加载 `rules/` 下**所有** `*.json` 并按内容识别：

| 键 | 用途 |
|---|---|
| `types: [...]` | 类型级规则：`port` → `kind` + `native` |
| `rules: [...]` | 调用点级正则规则（`pattern`/`replace`/`kind`/`typeImports`） |
| `events: [...]` | `Port*Event` → 原生事件 + 总线归属（`game`/`mod`） |
| `imports: {...}` | 需要额外补的 import |

`kind` 取值：`alias`（换成 native FQN）、`static-alias`（换静态成员宿主）、`shared`（原样复制到 1.21 侧）、
`drop`（1.20 专有，直接删引用）、`manual`（人工裁决）、`unknown`。

### 规则文件现状

| 文件 | 状态 |
|---|---|
| `_pilot.json` | 我写的 40 条试点规则，覆盖 TerraFurniture 用到的 31 个 PortLib 类型 + 6 条调用点规则。**全量 `types.json` 落地后删除** |
| `types.json` / `callsites.json` / `event-bus.json` / `forge-to-neoforge.json` | 正在由规则抽取任务产出（全量 PortLib 类型 + 事件总线表 + Forge→NeoForge 直连映射） |

## 已发现的规则面（实测）

| 类别 | 规模 |
|---|---|
| PortLib 类型（含 `PortLib.extensions.*` 静态工具类） | 主模组用到 159 个去重类型；PortLib 共 645 个类 |
| **不经 PortLib 的直接 Forge import** | 四模块合计 **665 处 / 199 个去重类型**（`net.minecraftforge.eventbus.api` 62、`RegistryObject` 53、`common.data` 52、`event.entity` 39、`registries.DeferredRegister` 38、`client.*` 104、`ForgeRegistries` 26、`fml.event` 24 …） |
| 调用点规则 | 网络包/注册/事件/附件/数据组件等 API 形状差异 |

`RegistryObject`（53 处）没有同名对应物，需逐种用法映射到 `DeferredHolder`/`DeferredItem`/`DeferredBlock`/`Holder`。

## 姊妹工具二：`commit_inventory.py`（逐提交移植台账）

**移植方式是逐提交比对，不是整树转换**（决策 Q10）——因为 1.20 侧有大量类的位置变动。
本工具把分叉点之后的提交整理成可勾选、可续做的台账：

```powershell
# 1) 取日志（在 1.20 仓库里；注意 PowerShell 重定向是 UTF-16，工具已能自动识别）
git -C D:\Minecraft\1.20forge\confluence log --reverse --no-merges `
    --name-status -M -C --date=short --pretty="C|%H|%ad|%s" 795ac9ccc..a75bda140 > $env:TEMP\gitlog.txt

# 2) 生成台账
python tools/port2native/commit_inventory.py --log $env:TEMP\gitlog.txt --out notes `
    --base 795ac9ccc --head a75bda140
```

产出 `notes/PORT-LEDGER.md`（时间正序 = 移植顺序，可直接勾状态）与 `notes/port-ledger.json`。

**首次实测（`795ac9ccc..a75bda140`）**：

| 项 | 数值 |
|---|---|
| 非 merge 提交 | **380** |
| 含改名的提交 / 改名事件 | 39 / **609** |
| 类别 | `content` 186、`content+submodule` 115、**`submodule-only` 52**、`assets/other` 17、`assets+submodule` 8、`platform` 2 |
| 子模块指针跳变 | PortLib 115（不移植）、Magic-Lib 98、TerraCurio 85、TerraFurniture 35、TerraEntity 5、TerraGuns 6 |
| 疑似「搬进子模块」 | **37 个提交**（删 java + 子模块指针跳变） |

典型移动族：`common/entity/ai/goal` → `util/entity/ai/goal`（41 处）、`data/create/recipe` →
`data/confluence_create/recipes`（63 处）、`data/confluence/structure` → `.../structures`、`textures/item/phaseblade` → `phasesaber`。

⚠️ **工具链顺序**：`submodule_link_table.py`（子模块链接表）→ `commit_inventory.py`（台账，可带
`--submodule-links` 标注）→ `commit_coverage.py`（第一轮分诊：行级覆盖率）→ `file_lag.py` + `submodule_numstat.py`
（第二轮分诊：Port 免疫的文件级欠账）→ `triage_pass2.py`（出结论写状态）→ 逐提交移植循环里按需调
`port2native.py`（词汇转换，`--only` 限定文件）与 `check_mixin_targets.py`（Mixin 目标审计，编译前门槛）。

## 姊妹工具四：`file_lag.py` + `submodule_numstat.py`（Port 免疫的文件级欠账）

**为什么还要一个覆盖率工具**：`commit_coverage.py` 只要一行提到 Port 类型就不计入分母
（`org.mesdag.portlib` / `IPort*` / `Port*`），而 1.20 后期几乎**全部**新增代码都引用 Port 类型 →
386 个提交里有 **124 个直接没有覆盖率条目**（包括 `f4ffadaa7` 这种 +10081 行的整子系统迁移）。
换一个不依赖 Port 词汇的度量：

    overlap = |有效行(1.20 文件) ∩ 有效行(1.21 映射文件)| / |有效行(1.20 文件)|

「有效行」= 去掉 import/package、纯符号行、Port 引用行、平台 API 引用行（Forge/NeoForge/Mojang/maven 库）之后的归一化行。
路径映射：精确同路径 → 同模块同名 → **跨模块同名**（1.20 把 TerraEntity/TerraGuns 内联进主模组，跨模块命中是正常情况，不是错误）。

```powershell
# 1) 主仓库 numstat（UTF-16 也能读）
git -C D:\Minecraft\1.20forge\confluence log --no-merges --numstat --format="C|%H" `
    795ac9ccc..18221c338 | Out-File -Encoding utf8 "$env:TEMP\port-ledger\numstat.txt"
# 2) 子模块净改动（主仓库对子模块只显示一行 gitlink，看不到里面的文件）
python tools/port2native/submodule_numstat.py --super D:\Minecraft\1.20forge\confluence `
    --range 795ac9ccc..18221c338 --out "$env:TEMP\port-ledger\sub-numstat.txt"
# 3) 拼接后出报告
python tools/port2native/file_lag.py --src D:\Minecraft\1.20forge\confluence `
    --ref D:\Minecraft\1.21neoforge\confluence --out notes `
    --log "$env:TEMP\port-ledger\numstat-all.txt" --range 795ac9ccc..18221c338
# 4) 提交级结论（A1a/A1b/A2/A3/A4/A5 规则）
python tools/port2native\triage_pass2.py --repo . --out notes
```

**三个必须记住的坑（都踩过）**：

1. **子模块内容必须走工作树枚举**，不能用 `git ls-files`——子模块在主仓库里只是 gitlink，
   `ls-files` 看不到里面的文件，1.21 的实体又都住在 `TerraEntity` 里，于是 114 个 `common/entity/monster/*.java`
   被误判成 `MISSING`（`file_lag.py` 里已改成 `rglob`，并在注释里记了这件事）。
2. **日志文件的 UTF-8 BOM**：PowerShell `Out-File -Encoding utf8` 写的是带 BOM 的 UTF-8，
   先按 `utf-8` 解会把 BOM 留在首字符，**第一条** `C|<hash>` 头永远匹配不上（丢过 `18221c338` 整条提交）。
   读的时候 `utf-8-sig` 必须排在 `utf-8` 前面。
3. **`DEAD-ONLY` 要拆成两类**：`PORTLIB-ONLY`（整提交只动 PortLib，永远不移植）与
   `DEAD-ONLY`（改动文件在 1.20 HEAD 已不存在，1.20 自己后来删了）。混在一起会把
   「PortLib 独有」错记成「1.20 撤销」。

`triage_pass2.py` 只对**还没有状态**的行出结论，并把命中规则、欠账权重、幽灵文件数一起写进
`notes/TRIAGE-PASS2.md`；已判定的行只做展示。工作包划分见 `notes/WORK-QUEUE.md`。

## 姊妹工具五：`batch_progress.py`（每批搬完之后的收敛度）

台账是「逐提交判定」，但执行按工作包走，一个工作包横跨很多提交（编译依赖不允许按提交顺序切）。
本工具回答执行侧的问题：**这个 1.20 提交碰过的文件，现在还有多少没和 1.21 一致？**

```powershell
python tools/port2native/batch_progress.py --repo . --numstat "$env:TEMP\port-ledger\numstat-all.txt" --out notes
```

按「该提交对每个文件的新增行数」加权，把每个文件当前的 file-lag verdict 归成
`done`(IN-SYNC) / `part`(PARTIAL) / `left`(LAGGING+MISSING)，给出完成度，写
`notes/BATCH-PROGRESS.md`。完成度 100% 的行即可据以在台账记 `COVERED`/`PORTED`；
**它的作用是把"我感觉搬完了"变成"机器说这行没欠账了"**。

实测（批次 1a 之后）：349 个提交里 100% 已搬完 8 个、≥50% 19 个、<50% 149 个、未动 147 个。
另一个用途是**反向验证转换的忠实度**：新建的 42 个文件落地后，其有效行与 1.20 的 overlap
回到 100%（逐行一致），说明改写没有引入语义漂移。

## 姊妹工具六：`entity_api_diff.py`（覆写点的两侧 API 对照）

移植某一批类之前，先回答「这批类覆写的父类方法，在 1.21 侧签名还是不是同一个」。
本工具扫 1.20 侧目标类的 `@Override` 成员，在 **1.21 侧的继承闭包**里按「方法名 + 参数**类型**表」匹配。

```powershell
python tools/port2native/entity_api_diff.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java `
  --root121 Confluence-Magic-Lib/src/main/java `
  --root121 TerraEntity/src/main/java --root121 TerraGuns/src/main/java `
  --jar121  ConfluenceOtherworld/build/moddev/artifacts/neoforge-21.1.219-sources.jar `
  --lib     "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\software.bernie.geckolib\geckolib-neoforge-1.21.1\4.8.4\<hash>\geckolib-neoforge-1.21.1-4.8.4-sources.jar" `
  --out notes/WP1C-API-DIFF.md
```

判定：`OK` / `SIG_CHANGED` / `REMOVED` / `UNRESOLVED`（含义见脚本 docstring）。

**三条口径是踩过坑才定下来的，改之前请读 `notes/WP1C-API-DIFF.md` 开头那张表**：

1. **只比参数类型，丢掉形参名** —— 两侧形参名来自不同映射（1.21 反编译常是 `p_xxxxx_`），
   按「类型+名字」整体比会把 `remove(RemovalReason)`、`readAdditionalSaveData(CompoundTag)` 全部误报。
2. **只认类体层级（大括号深度 1）成员** —— 否则匿名内部类里的方法会被算成目标类的覆写点
   （`BaseMonster` 的 `createTree`/`execute`/`createPathFinder` 就是这么被误收的）。
3. **在继承闭包里找，闭包要并入工程源码与第三方库** —— 只查 `net.minecraft.*` 会把
   `PartHitTarget`、`org.confluence.lib.api.entity.Boss`、geckolib `GeoEntity` 声明的接口方法
   误判成「1.21 无此方法」。同批待移植的类（如 `BaseBoss -> BaseMonster`）会被作为虚拟类挂进闭包，
   否则子类会因父类「1.21 侧不存在」而整表 `UNRESOLVED`。

实测（1c 批次，9 个类）：83 个覆写点里 `OK` 77、`SIG_CHANGED` 5、`REMOVED` 1；
首版口径的 44 个「需处理」有 38 个是假阳性。

> ⚠️ **`REMOVED` 不等于「被删除」，也可能只是改名 —— 每个 `REMOVED` 都要按「会不会是改名」再查一遍新名字。**
> 踩过的实例：`Entity#onAddedToWorld()` 在 1.21.1 是改名成 `onAddedToLevel()`
> （`Entity.java:3733`，调用点 `ServerLevel.java:933/:945`、`ClientLevel.java:355`、
> `PersistentEntitySectionManager.java:115/:122/:248`）。当时只在 `Entity.java` / `Level.java`
> 里 grep 旧名、得到 0 次就判定「被删除」，于是把 4 个类的逻辑挪进 `tick()` 的「只跑一次」守卫，
> **丢掉了 `super` 里的 `isAddedToLevel = true`**。正确做法是照旧覆写并保留 `super` 调用；
> 1.21 分支自身已有 6 个文件（`SwordProjectile`/`BeeKeeperProjectile`/`SporeCloudProjectile`/
> `SpearProjectile`/`NorthPoleSubProjectile`/`BaseArrowEntity`）就是这么写的，**先看仓库里已有的同类写法**能省掉这类错。
> 已固化成规则 `vanilla-onaddedtoworld-rename`（1.20 侧还有 36 处使用）。

## 姊妹工具七：批次流水线（`dep_subset` 的配套四件）

移植每一批都要走同一条流水线，这几个工具是它的每一步；命令与踩坑见各文件的 docstring。

| 工具 | 这一步做什么 | 关键提醒 |
|---|---|---|
| `missing_files.py` | 列出 1.20 有、1.21 没有的 java 文件（FQN，一行一个），喂给 `dep_subset.py --seed` | `--sub common/entity/monster` 是**片段**匹配，不是前缀 |
| `seed_closures.py` | **切批次边界前先量**：一张依赖图跑一趟，给出**每个种子各自的**闭包（互不污染）+ 公共底座排行 | `dep_subset.py` 量的是**并集**，一把种子塞进去会互相污染（`slime/*` 10 个类合量 10 个文件，但 `MotherSlime` 单点就带全族）。`--seed-dir` 含子目录、可用相对路径或 FQN；`--detail` 出逐种子明细 |
| `dep_subset.py` | 算「最大可编译子集」：扩张不动点 + `--defer` 传递闭包 + 最短依赖链 | **只往 1.21 侧还没有的类型扩张**；`--alias` 处理改包路径（如 `init.entity.ModEntities` → `init.ModEntities`）。**粒度是类型级**：1.21 有同名类型 ≠ 有同名成员（曾因此漏判 `ModEntities.NIMBUS_RAIN`） |
| `stage_batch.py` | 把「暂存 → 转换 → 合并」三步合成一条命令（标准循环第 2 步） | 见下面「过程纪律」：它的 `cwd` 曾写错一级，把文件写到隔壁同名项目 |
| `summarize_subset.py` | 把报告按子系统分组看体量，决定批次切多大 | 报告标题带后缀，找小节必须按**前缀**匹配（`list.index` 要整行相等） |
| `error_detail.py` | 把 javac 日志解析成「文件:行 + 出错源码行」，逐个修 | 依赖 `build_errors.py` 的折行拼接 |
| `port_sounds.py` | 音效层专用：`ModSoundEvents` + `sounds.json` + `.ogg` + 字幕 lang | 默认只出报告，`--apply` 才写盘；两侧资源布局不同（1.21 扁平），**不覆盖**已有条目 |
| `fix_eol.py` | **每批的最后一步**：把工作树里被写成 LF 的文本文件改回 CRLF | 见下面「换行约定」；**只处理已被 git 跟踪的文件**，新文件要先 `git add` 再跑一次 |

## 过程纪律（两起真实事故换来的）

### 1. 写盘工具必须在目标可疑时**拒绝执行**，不能默默造树

`stage_batch.py` 刚写出来时 `cwd` 用的是 `dirname(dirname(dirname(HERE)))` —— **多写了一级**，
于是 `--dest ConfluenceOtherworld/src/main/java` 被解析成
`D:\Minecraft\1.21neoforge\ConfluenceOtherworld\src\main\java`（隔壁另一个同名项目），
而 `apply_batch.py` 里的 `makedirs(exist_ok=True)` 不报错，静悄悄建了整棵树、写进 15 个文件。

- 事后确认无损失的依据：该目录**各级子目录的创建时间全是本次运行时刻**，证明原本不存在。
- `apply_batch.py` 已加安全闸：`--dest` 必须落在某个 git 仓库内、且形如 `.../src/main/java`，
  否则 exit=2 拒绝（`--allow-outside-repo` 可显式绕过）。
- **一般规律**：`Path.makedirs(exist_ok=True)` + 相对路径，是「写错目录但不报错」的经典组合；
  凡是要落盘的工具，都该先断言目标在预期位置。

### 2. `Select-Object -First N` 会**提前终止上游命令**

第一次跑 `stage_batch.py --convert --apply` 时输出被 `Select-Object -First 6` 截断，
PowerShell 在拿到 6 行后就把上游进程掐了，`--apply` 根本没执行；
而我看到的「0 errors」是**假结论**（文件还没进树），差点当成「本批一次编译通过」写进 notes。
后来靠 `git status` 核对文件是否真的在树里才发现。

**规矩：会产生副作用的命令绝不接 `Select-Object -First`**（要看尾部用 `-Last`，或用
`Tee-Object -Variable log` 先收全再筛）。同理，任何「编译通过」的结论都必须先确认**文件真的落盘了**。

**粒度提醒的实测出处**：WP2 批次 3 里 `dep_subset.py` 报「闭包 17 个、14 个 `--defer` 未触发」，
但编译时 `Harpy` / `AngryNimbus` 仍因 `ModEntities.HARPY_FEATHER` / `ModEntities.NIMBUS_RAIN`
不存在而失败 —— 类型级满足骗过了工具。**所以「闭包通过」不能替代编译门。**
（WP2 批次 7 是同一件事的第三例：19 个种子的类型级闭包**扩张为 0**，
实际有 4 条 `ModEntities` 注册缺失，仍是编译器发现的。）

### 3. 编译门槛一律走 `build_errors.py`，不要手敲 `gradlew`

WP2 批次 7 我第一次手敲 `.\gradlew :ConfluenceOtherworld:compileJava --console=plain`，
得到 `BUILD FAILED in 9s` 且 `build_errors.py` 报「总错误数 0」—— 看着像代码全对却构建失败。
真相是两件事叠在一起：

1. **没加 `--offline`**：`maven.bawnorton.com` 当时返回 `530`（Cloudflare 源站错误），
   `org.mesdag:ParticleStorm-neoforge-1.21.1:1.4.4` 解析失败 → 构建在**配置阶段**就挂了。
   `build_errors.py` 自己带 `--offline`（依赖在本地 Gradle 缓存里），重跑即正常。
2. **没设 `JAVA_TOOL_OPTIONS=-Duser.language=en -Duser.country=US`**：javac 因系统区域打的是
   **中文** `错误:`，而 `build_errors.py` 的解析正则只认 `error:` / `warning:`，
   于是「总错误数 0」是解析不到，不是真没有（实际有 11 处）。

**规矩：编译门槛用 `python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .`。**
手敲 gradlew 的日志既解析不了、又容易被仓库外故障误读成代码错。

### 3b. ⚠️ javac 默认只报 **100** 条错误，大批次必须带 `--maxerrs`

`-Xmaxerrs` 的默认值是 100：**超出部分既不打印也不计数**。批次 25 因此被误导了整整 4 轮 ——
`build_errors.py` 一直报「100 错误 / 46 文件」，看着像巧合，实际是**被截断的下界**；
加 `--maxerrs 2000` 重测，真实规模是 **150 错误 / 65 文件**（多出的 19 个文件里含整个贸易注册簇）。

```powershell
# 大批次（几十个文件以上）一律这么跑
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000
```

`--maxerrs` 会往临时目录写一个 `allprojects { tasks.withType(JavaCompile) { options.compilerArgs << '-Xmaxerrs' << 'N' } }`
的 Gradle init 脚本、并把 `-I <脚本>` 传给 gradlew（**不进仓库、不改 `build.gradle`**）。
判定「清单是不是被截断」的简易办法：看总错误数**是不是恰好 100**（或 100 的整数倍）。

### 4. 提交信息文件**只用写盘工具生成**，不要用 PowerShell 的 `Set-Content`

同一个坑的两面（都已实际踩到）：

- `Set-Content -Encoding utf8` 在 Windows PowerShell 5.1 下写的是**带 BOM** 的 UTF-8，
  于是 `git commit -F` 会把 BOM 吃进标题 —— 标题变成 `﻿docs(port): …`（`git log` 里肉眼可见一个多余字符）。
- `Set-Content -Encoding ascii` 会把**所有中文替换成 `?`**：一次 `docs(port): WORK-QUEUE ? WP6a ????? WP6c ????`
  就是这么来的，标题里的信息全丢了。

**规矩：提交信息先写进文件时用 `write` 工具（UTF-8 无 BOM、LF），再 `git commit -F`。**
核对办法：`git log -1 --format="%s"` 之后检查首字符不是 `\uFEFF`、且不含 `?`。

## 换行约定（`core.autocrlf=true`：工作树 CRLF / 索引 LF）

两个仓库都是 `core.autocrlf=true`：

| 位置 | 换行 |
|---|---|
| 工作树 | **CRLF**（1.21 侧 5900+ 个文件都是 `i/lf + w/crlf`） |
| 索引 / 提交对象 | LF（`git add` 时由 autocrlf 转换） |

**容易踩的坑**：Python 脚本与本项目的写文件工具默认写 LF，于是新生成的文件在
`git ls-files --eol` 里显示成 `w/lf`，与仓库其余文件不一致；更糟的是「往 CRLF 文件里插入 LF 行」
会变成 `w/mixed`（本项目 `.gitignore` 就中过一次）。

**做法**：

1. 写文件的工具显式用 CRLF：`port2native.py` 的核心写盘点用
   `write_text(..., newline="\r\n")`（它读入时已由 `read_text` 做过通用换行，内存里只含 `\n`，
   所以不会写出 `\r\r\n`）；`dep_subset.py` / `entity_api_diff.py` / `port_sounds.py`
   的 `--out` 同样是 `newline="\r\n"`。
2. **每批提交前跑一次 `python tools/port2native/fix_eol.py --repo .`**：
   它用 `git ls-files --eol` 挑出 `w/lf` 与 `w/mixed` 的文本文件改写为 CRLF
   （二进制按内容里的 NUL 判定，不靠扩展名）。`--check` 只报告；文件还没 `git add` 时加 `--paths <路径>`。
3. 改完换行后 `git add -A` —— `git status` 会先显示一批 ` M`（stat 缓存变脏），`git add` 会刷新它。
   **用 `git diff --cached --numstat` 确认没有换行噪音**：2026-09-27 那次核对的结果是
   只有 4 个工具文件、共 11 增 6 删，102 个被改换行的文件**零内容差异**。

**顺序坑（2026-09-27 WP2 批次 6 实测）**：工具只认 `git ls-files --eol` 里的 `w/lf`，
所以**新文件必须先 `git add`、再跑一次**。批次 6 里先跑工具（候选 0 个，因为
`notes/WP2F-SUBSET.md` 与 `tools/port2native/gen_sounds_rule.py` 还是未跟踪的）后提交，
两个文件就以 `w/lf` 落进了索引。补跑后 `git status` 仍显示 ` M`，但
`git hash-object` 与 `git rev-parse :<path>` **两两相等**、`git diff --numstat` 为空 ——
这是 stat 缓存没刷新，`git update-index --refresh` 也没清掉，最后靠
`git add -- <这两个文件>` 才回到干净。所以核对换行是否真的生效，不要看 `git status`，
要看 `git ls-files --eol <path>` 的 `w/crlf`。

## 姊妹工具八：`port_sounds.py` 的落地口径

音效层是 WP2 的扇出最大依赖（99 个待移植怪物类里 49 个引用缺失音效），单独成批：
`0~- modsoundevents` + `sounds.json` + `.ogg` + `confluence.subtitle.*`。
落地前后各跑一次本工具即可（第二次会显示待补 0），报告写进 `notes/SOUND-PORT.md`。

## 姊妹工具三：`submodule_link_table.py`（子模块链接表）

`submodule-only`（52 个）与 `content+submodule`（115 个）提交的代码改动**在子模块仓库里**：
主仓库只看到一行 `M <子模块>`。本工具把每个主仓库提交映射到「子模块 + 旧SHA..新SHA +
子模块侧提交列表」：

```powershell
python tools/port2native/submodule_link_table.py `
    --super D:\Minecraft\1.20forge\confluence --base 795ac9ccc --head a75bda140 --out notes
```

产出 `notes/SUBMODULE-LINKS.md` + `notes/submodule-links.json`；再把它喂给台账工具即可在每行标注：

```powershell
python tools/port2native/commit_inventory.py --log $env:TEMP\gitlog.txt --out notes `
    --base 795ac9ccc --head a75bda140 --submodule-links notes\submodule-links.json
```

**用法要点（实测得出）**：

- **`part N` 是同步波**：同一逻辑步骤在主仓库与各子模块各有一个同名提交（row2 = 主仓库 `part2` + Magic-Lib/TerraCurio/TerraFurniture 的 `part2`）；波里常夹带真内容。
- **看「区间净改动」列，别只看提交列表**：区间里可能存在"加了又删/改名"的提交（row1 的 TerraFurniture 区间就加过 `models/block/oak/{bed,candle}`，两侧现在都没有）。
- **`/integration/` 默认是忽略路径**（决策 Q12：1.20 刻意删集成、1.21 保留），台账会把它单列出来。
- **新提交一律排在 backlog 之后**（决策 Q13）：1.20.1 后续新增的提交依赖 1.20.1 的新架构（PortLib / 模块搬迁 / 事件体系），台账的时间正序天然满足这一点。1.20 每次更新后重跑本工具即可把新行追加到末尾（老行与状态文件都不受影响，状态键是完整 hash）。
- **移植状态存在 `notes/port-ledger-status.json`**（`hash → {status,target,note}`），重新生成台账不会丢；台账最后一列从它读取。
- 状态取值：`TODO` / `PORTED` / `SKIP-PLATFORM` / `SKIP-1.21-KEEPS`（integration）/ `COVERED` / `MOVED` / `LOST?`。

**首次实测**：

| 子模块 | 指针跳变 | 子模块侧提交 | 读取失败 |
|---|---|---|---|
| PortLib | 116 | 115 | 0（按 Q2 不移植，属噪音） |
| Confluence-Magic-Lib | 99 | 98 | 4（对象缺失） |
| TerraCurio | 83 | 83 | 0 |
| TerraFurniture | 34 | 34 | 5（对象缺失） |
| TerraEntity / TerraGuns | 5 / 6 | 0 / 0 | 目录已移除（按 Q3 退役） |

→ 三个在役模块共枚举出 **215 个子模块提交**；台账里 **166 行**带上了「子模块侧提交」标注。
9 个读取失败是本地子模块仓库缺少那些较新的对象（工作副本停在旧指针），报告里直接给了补救命令
（`git -C <子模块> fetch --all` 后重跑）。

### 范例：一次逻辑搬迁被拆成 3 个主仓库提交 + 4 个子模块提交

「饰品的药水效果转移至 lib」：

| 主仓库提交 | 子模块侧 |
|---|---|
| `1c012ccb1` | Magic-Lib `9de76c9df`、TerraCurio `ddfcd27e0`、TerraFurniture `f6a10ca45` |
| `100f6e0f2` | Magic-Lib `2c66dc065`、TerraCurio `b38d16d66` |
| `2a4dfce2c` | Magic-Lib `1802b4488`、`1c97b2e51`、**`0718c593a`**、`5f2d48bbf` |

真正的搬迁内容在 Magic-Lib `0718c59`：新增 `lib/common/effect/{GravitationEffect,HoneyEffect}.java`、
`lib/common/LibEffects.java`、`lib/client/handler/GravitationHandler.java`、`lib/mixed/ILibEntity.java`
与若干 mixin，同时把 `LibEffects`、`LibGameEvents` 拆位（`LibModEvents`→`LibClientModEvents`，R082）。
主仓库那侧只有调用点更新 + 指针跳变 —— **只看主仓库的 diff 会完全错过这次搬迁**。

## 姊妹工具一：`check_mixin_targets.py`（Mixin 目标审计）

Mixin 层**不能**靠词汇转换解决——它的 `@Mixin`/`@Inject` 目标直指原版内部，而 1.20.1↔1.21.1 之间
方法改名/合并/删除、lambda 重排、加载器类不同都会让注入点在**启动期**才炸（编译期看不出来）。
本工具把 1.20 侧 mixin 的目标类与方法名拿去和 1.21 的源码对账，输出三分类：

```powershell
python tools/port2native/check_mixin_targets.py `
    --source  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
    --reference build\_nfsrc_219 `
    --project-ref ConfluenceOtherworld\src\main\java --project-ref Confluence-Magic-Lib\src\main\java `
    --out $env:TEMP\mixin-audit
```

结论取值：`TARGET-OK` / `TARGET-MISSING` / `METHOD-OK` / `METHOD-MISSING` / `LAMBDA-RISKY` /
`MULTI-TARGET` / `UNKNOWN` / `CTOR-OK` / `LOADER-CLASS`（加载器类，天然单版本专有）。

> ⚠️ 参照树只有原版时，第三方模组内部类（Curios、geckolib）与**本工程自己的类**会被误判成 `TARGET-MISSING`，
> 所以务必用 `--project-ref` 传入 1.21 侧各模块源码；第三方模组需另找对应版本源码。

**首次审计结果（1.20 侧 200 个 mixin 文件）**：目标类 178 存在 / 13 缺失，方法点 194 存在 / 4 缺失，
λ 风险 7、多目标 15、UNKNOWN 14。13 个缺失分五类：第三方模组内部类 5、本工程自混入但改名 4、
加载器（Forge）专有 2、原版真删除 1（`ProtectionEnchantment`）、其余待判。
→ 结论：**Mixin 层绝大多数是"逻辑可搬、目标需核对"，而不是"整层不可移植"**；
但每次回移都必须跑这个审计并把 `TARGET-MISSING`/`LOADER-CLASS`/`METHOD-MISSING` 全部处置掉。

## 规则安全闸（**改这个工具前必读**）

**教训**：全量自动生成的规则里存在**不能机械应用**的条目，而且 `kind` 标着 `safe` 也未必安全。实跑踩到的两个：

| 规则 | 后果 |
|---|---|
| `pattern=\.get` → `replace=.get()` | `.getKey()` 被改成 `.get()Key()` |
| `pattern=\.getOrDefault\(|\.has\(|\.get\(` → `replace="SAME"`（占位符） | `.getName()` 被改成 `SAME)Name()`、`NULL_RIDE.get()` 被改成 `NULL_RIDESAME)()` |

因此引擎有三道闸 + 两道自检，**任何一条被违反都不写文件**：

1. `kind == review` 不自动应用（只进人工清单）；
2. `replace` 是占位符（`SAME`/`TODO`/空…）且非删除语义 → 拒绝；
3. `pattern` 的最长字面量 < 8 字符（如 `\.get`、`\.build\(`）视为过宽 → 拒绝；
4. **括号配平自检**：改写后 `(){}[]` 不配平 → 拒写该文件、保留原文、列入报告；
5. **占位符残留自检**：改写后仍含 `BUS` → 拒写（总线没解析就写出去＝能编译但静默不触发）。

诚实说明：第 4 条的自检函数早期版本自己会误报（逐字符处理 `'` 遇到中文注释就失准，把 252/252 配平的好文件拒写了）；现已改为"先剥文本块/字符串/字符字面量/注释再配平"。**故意写坏一条规则验证过：160 处命中导致 21 个文件被拒写并逐个报告，其余正常输出。**

## 规则治理：`rules/overrides.json`

人工裁决文件，优先级最高，四类动作（每条都写明依据）：

| 动作 | 含义 | 当前内容 |
|---|---|---|
| `disable` | 明确禁用的规则（无效/过宽/已被类型别名取代） | 10 条（`.get`、`.build(`、`.codec(`、`.getId()`→`.id()`、`SAME` 占位符、与别名重复的几条） |
| `promote` | 把已核实的 `review` 提升为可自动应用 | 11 条（6 条 `List#getFirst/addFirst/...`（Java 21 原生有）、5 条 `PayloadRegistrar` 方法改名） |
| `busAware` | `replace` 里的 `BUS` 需按事件类型解析 | 4 条（`PortEventHandler.addListener/postEvent`） |
| `extraRules` | 追加人工核实的新规则 | `PortSoundEvents.X.get()` → `SoundEvents.X`（26 个常量已核实是 1.21.1 原生）等 |

**总线解析**（`resolve_bus`）：从调用点附近 12 行里找已知事件类型 →
- 全部是**游戏总线**事件 → 自动改写成 `NeoForge.EVENT_BUS.addListener(...)`；
- 含**模组总线**事件（如 `RegisterCapabilitiesEvent`、`EntityRenderersEvent.RegisterRenderers`）→ **不改写**，进人工清单并说明"必须注册到 `@Mod` 构造器拿到的 `IEventBus`"——1.21 的模组总线是构造器参数（`public Confluence(IEventBus eventBus, ModContainer container)`），写成 `NeoForge.EVENT_BUS` 能编译但静默不触发；
- 判定不出 → 进人工清单。

## 设计原则（改这个工具时请遵守）

1. **规则数据驱动**：映射一律写在 JSON 里，便于逐条 review；引擎不硬编码业务映射。
2. **不猜**：`manual`/`drop`/`unknown` 的类型**不改写方法体**，只记录——宁可留下可见的编译错误，也不要静默改写成语义不同的代码。
3. **不静默**：任何跳过的东西都必须出现在 `uncovered.json` 或 `manual-todo.md` 里。
4. 只改代码行，纯注释行不动；保留原换行风格；读写显式 UTF-8（1.20 源码里有大量中文 javadoc）。

## 试点结果（TerraFurniture，2026-09-27）

- 114 个文件扫描，20 个含 PortLib，**20 个已转换**；重写计数：简单名 92、类型别名 27、静态成员宿主 5、调用点 6
- `uncovered` = 3（均为 `PortLib.extensions.*` 缺少 `shared` 规则）
- `manual-todo` = 22 处（`IPortPacket`/`PortEventHandler`/`PortVehicleEntity`/`IPortBlockSetTypeExtension`/`PortTags`/`PortSoundEvents`/`PortItemRegistration` + 中置信度类型映射）
- `leftovers` = 10 个文件有残留 —— 全部对得上 `manual`/`drop` 清单，无"莫名其妙"的残留
- 已修的三个引擎缺陷：① 原 import 既保留又重排导致重复 import；② `PortLib.extensions.*` 命名空间未被识别为 Port 命名空间；③ 中低置信度类型映射没有进人工裁决清单

## 过程纪律 5：**staging 之前必须跑「移动 vs 新增」审计**（`check_duplicates.py`）

> 起因是一次真实事故（用户当面指出：「你怎么把类的移动移植成了新建类，原类还在」）。
> 详见 `notes/MOVE-VS-NEW-AUDIT.md`。

`dep_subset.py` 只判**类型级**：1.20 把类从 A 包移到 B 包，它只看到「A 包的类在 1.21 缺失」，
于是把**移动**报成**新类**。照单在 B 包新建 → 树里出现**两个同名类、两套实现**，新的那份是死代码。

**规矩：每个批次 staging 之前，对待落的每个文件先跑**

```powershell
python tools/port2native/check_duplicates.py `
    --src120 <1.20 src/main/java> --ref . --file <相对路径> ...   # 或 --all
```

- 扫描范围是**整个 1.21 仓库、含全部子模块**（这个工具诞生前，我只搜了主模组源码树，
  因而漏看了 `common/data/saved/` 的同名类与 TerraGuns 子模块里的同名类）；
- 判定 `SAME`/`MOVE`/`NEAR`/`DIFF`，`SAME`/`MOVE`/`NEAR` 一律**不许新建副本**：
  要么登记 `--alias` 等价、要么整批 `git mv` + 改引用（两者都写进批次笔记）；
- `DIFF` 要确认它是不是既定的「先加后删」（模块退役/架构对齐），并在提交信息里写明
  **旧同名类在哪、何时删** —— 不能说成「1.21 侧此前不存在」。

