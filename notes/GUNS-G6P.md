# 枪械内联 G6′（TerraGuns 退役）：落地记录

> **枪械内联迁移**（`notes/GUNS-INLINE-MIGRATION.md`）的第 6 批、也是**最后一批**。
> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**、`Confluence-Magic-Lib` **0 错误 / 0 文件**
> （`build_errors.py --maxerrs 2000`）。**TerraGuns 子模块 gitlink 已从 index 移除**，全仓对
> `org.confluence.terra_guns.*` 的**编译期引用归零**。
>
> 规模：根仓库 **9 文件改动 + 1 个 gitlink 删除**；子模块 Lib `c519b6e`（1 文件）。

## 一、为什么 G6′ 不是「删一个 gitlink」那么简单

清点发现退役会**连带打破两处运行时行为**，都必须在同一批补齐：

| # | 连带影响 | 处理 |
|---|---|---|
| 1 | TerraGuns 的 `data/minecraft/tags/damage_type/is_projectile.json` 是 `#minecraft:is_projectile` 里 `gun_bullet` 的**唯一来源**（值是 TG 时代的 `terra_guns:bullet_damage`）。模块一删，子弹伤害就不再是「弹射物伤害」 | 按 1.20 `LibDamageTypeTagsProvider:31` 在 **Lib** 补 `tag(IS_PROJECTILE).add(GUN_BULLET)`（子模块提交 `c519b6e`） |
| 2 | 三份手写 lang 里整块 **TG 时代死键**（`entity/item/key/tooltip.terra_guns.*`，共 101 行）：枪械/子弹的物品名、实体名、键位、tooltip 全部**指不到实际注册名**（玩家看到的是英文回退或原始键） | 按 1.20 的键名整块改指为 `confluence.*`（见 §三） |

> 换句话说：G6′ 之前「枪械内联」在**代码层**已完成（G4′/G5′），但**数据/标签/i18n 层**还挂在 TG 命名空间上；
> 本批是这三层一起落地。

## 二、本批内容（根仓库 9 改 + 1 删）

### 2.1 编译期引用（4 处，1.20 无对应物 → 纯移除）

| 文件 | 1.21 原状 | 处理 |
|---|---|---|
| `mixin/client/resources/model/ModelBakeryMixin:8/20` | `import org.confluence.terra_guns.TerraGuns;` + `confluence$skipSet` 含 `TerraGuns.MODID` | 删 import；集合收为 `Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID)`（枪械模型现在就是 `confluence` 命名空间，本就在集合里，**跳过日志的行为不变**） |
| `mixin/client/resources/model/ModelManagerMixin:8/20` | 同上 | 同上 |
| `util/ModUtils:80/95` | `CONFLUENCE_NAMESPACES` 含 `TerraGuns.MODID` | 删 import + 集合去掉该元素 |

### 2.2 构建与子模块（1 改 + 1 删）

| 文件 | 处理 |
|---|---|
| `settings.gradle:18` | 项目列表去掉 `"TerraGuns"` |
| `ConfluenceOtherworld/build.gradle:12` | 同一列表去掉 `"TerraGuns"`（该列表被 `projectName.forEach { implementation jarJar(project(":" + name)) }` 消费） |
| `.gitmodules` | 删除 `[submodule "TerraGuns"]` 段 |
| **gitlink** | `D TerraGuns`（`160000 ea14bb6…` 从 index 移除）；同时清理 `.git/modules/TerraGuns` 与 `git config submodule.TerraGuns.*`（`git rm` 因 `.gitmodules` 未暂存而拒绝，改为手动 `git add` 删除项 + 手清元数据） |

> 工作目录已一并删除（566 文件）。上游为 `github.com/XiaoHuNao/TerraGuns @ ea14bb6`（退役前工作树干净、无未提交内容），
> 需要回查可重新 clone。
> **1.20 侧本就没有 TerraGuns 子模块**（`.gitmodules` 仅 TerraCurio / TerraFurniture / Confluence-Magic-Lib / PortLib），
> 故本批正是向 1.20 对齐。

### 2.3 资源与 i18n（4 改）

| 文件 | 1.21 原状 | 处理（1.20 依据） |
|---|---|---|
| `data/confluence/advancement/achievements/completely_awesome.json` | `"id": "terra_guns:minishark"` + `"items": "terra_guns:minishark"` | → `confluence:minishark`（1.20 `advancements/achievements/completely_awesome.json:9/25` 即 `confluence:minishark`） |
| `lang/{es_es,lzh,pt_br}.json` | 见 §三 | 92 处改名 + 9 处删除 |

## 三、i18n 改指（92 改名 / 9 删除；键名按 1.20，**值一字未动**）

1.20 的三份手写 lang **完全没有 `terra_guns` 键**，对应内容全部在 `confluence.*` 下，值与本批改指前的 1.21 侧逐字相同。

| 类别 | 条数 | 映射 | 1.20 依据 |
|---|---:|---|---|
| `entity.terra_guns.*` | 2 | → `entity.confluence.*`（`base_bullet`/`gravity_bullet`） | 1.20 `es_es.json:2914/2915` |
| `item.terra_guns.*` | 27 | → `item.confluence.*` | 1.20 `es_es.json:4822` 一带 |
| `tooltip.terra_guns.critical` | 1 | → `tooltip.confluence.critical_chance` | 1.20 `:6522`（键名不同，非简单前缀替换） |
| `tooltip.terra_guns.damage` | 1 | → `tooltip.confluence.ranged_damage` | 1.20 `:6523` |
| `tooltip.terra_guns.knockback` | 1 | → **删除**（`tooltip.confluence.knockback` 已存在且值相同） | 1.20 `:5723` |
| `key.terra_guns.{aim,shoot}` | 2 | → **删除**（G5′ 已按 1.20 `:4967/4968` 补入 `key.confluence.{aim,shoot}`，值相同） | 1.20 `:4967/4968` |
| `creative_tab.terra_guns.gun_tab` | 1 | → `creative_tab.confluence.gun_tab` | 1.20 三份均为此键 |
| `death.attack.bullet_damage` | 1 | → `death.attack.gun_bullet` | 1.20 `:2150` / Lib `LibLanguageProvider:37` |

* 判定方式：对每条键先查「目标键是否已存在」——已存在且值相同者**删除**（否则会产生重复键），否则**改名**。
  三份文件的判定结果一致（改名 30~31 / 删除 3）。
* `key.terra_guns.*` 是**活缺陷**：1.21 的键位表在 G5′ 已改指 `key.confluence.*`，旧键永远不会被查询。
* `death.attack.bullet_damage` 同理：伤害类型早已是 `confluence_magic_lib:gun_bullet`（message id `gun_bullet`），
  旧键对应的死亡提示**永远不会显示**。

## 四、**故意保留**的 `terra_guns` 字符串（不是漏项）

| 位置 | 内容 | 为什么留 |
|---|---|---|
| `i18n/{en_us,zh_cn}.json` | 102 处 `terra_guns` 键 | 这是**翻译工作集**（`notes/1.21-BRANCH-DIVERGENCE.md` C13 引入的 3 个 json）。**1.20 侧同样有 70 处**，即两侧都停在 TG 时代 → 属「翻译同步」任务，改它要重写 567KB 的 dump，不属模块退役；已记队列 |
| `assets/confluence/ageratum/zh_cn/confluence_changelog/rename.md:139` | `` `terra_guns:blowpipe` `` → `` `terra_guns:blowgun` `` | 版本变更日志的**历史记录**（1.20 无该文件）。改它等于改写已发布的历史 |
| `notes/**.md`、`tools/port2native/check_duplicates.py:17` | 迁移过程说明 | 文档；本轮批次记录亦引用它们 |
| 各 java 文件的 `///` / `//` 注释 | 「1.21 侧此前绑 TG 的 X」 | **溯源注释**，是这批迁移的可审计证据（G4′/G5′ 已建立的惯例） |

## 五、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（在 TerraGuns 已从 gradle 配置中移除的状态下跑） |
| `build_errors.py --module Confluence-Magic-Lib --repo . --maxerrs 2000` | **0 错误 / 0 文件** |
| `TerraCurio` | 本批未改动；WP6c 已验证 0/0，且 Lib 改动仅为 datagen provider 体（无 API 变化），不受影响 |
| 编译期引用 | `org.confluence.terra_guns.*` / `TerraGuns.MODID` / `TGItems` 等 → **0 处**（java 全仓，排除注释） |
| 资源引用 | 手写资源中 `terra_guns` → **0 处**（仅剩 §四 的 doc/i18n/历史） |
| gitlink | index 中无 `TerraGuns`；`git submodule status` 只剩 4 个子模块 |
| `check_duplicates.py`（3 个改动 java） | 0 处「疑似移动/重复」 |
| 待游戏内验收 | 枪械/子弹的物品名与实体名本地化、`#minecraft:is_projectile` 生效（Projectile Protection 对子弹伤害生效）、成就 `completely_awesome` 用 `confluence:minishark` 解锁 |

## 六、枪械内联（G0~G6′）全链收口

| 批次 | 内容 | 提交 |
|---|---|---|
| G0 | Magic-Lib 手部动画 API | 子模块 `a19d894` + 根 `cde95394d` |
| G1′ | `GunDefinition` + 标签层 | `20cbc0204` |
| G2′ | `BaseGun` + `GunEvent` | `3a9048386` |
| G3′ | 子弹层（含在批次 25 内） | `fc680ba14` |
| G4′ | 注册层 + 服务端管线 + 网络层 | `4000bc65b` |
| G5′ | 客户端层（可开火 + 渲染 + 特效 + 资源回迁） | `35585db9a` |
| **G6′** | **TerraGuns 退役（本批）** | 见提交 |

**遗留（与枪械无关，另立 WP）**：
* `ModParticleTypes.NO_TRAIL` 成员 + `SpearProjectile:137`/`SwordProjectile:358` 的尾迹（G5′ 记录）；
* `client/effect/RenderStateShardAccessor` 整类搬迁（G5′ 记录）；
* 1.21 Lib 的伤害类型只有 5 个（1.20 有 13 个：缺 `MAGICAL_PROJECTILE`/`SWORD_PROJECTILE`/`SUMMON`/`BOULDER`/`HELLFIRE` 等），
  故 `LibDamageTypeTagsProvider` 仍有 4 条 1.20 tag 行无法照搬（`IS_MAGIC`/`BYPASSES_ARMOR`/`IS_PLAYER_ATTACK` 与 `AS_MELEE_ATTACK` 的 3 个成员）；
* `i18n/` 的 TG 时代键（§四）。
