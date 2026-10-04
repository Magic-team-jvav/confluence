# 接手提示词（Confluence 1.20.1 Forge → 1.21.1 NeoForge 移植）

> 本文件由上一轮会话生成，可直接整段粘进新对话作为开场提示词。所有行号/哈希/SHA 都是生成时实测值。

## 0. 一句话任务
把 `D:\Minecraft\1.20forge\confluence`（分支 `forge-dev/1.20.1`）的改动，按"台账逐行行走"的方式移植到 `D:\Minecraft\1.21neoforge\confluence`（分支 `neoforge-dev/1.21.1`）。1.21 侧**保持原生 NeoForge**，不移植 PortLib 集成桥。

## 1. 铁律（口径，违反即为错的移植）
1. **方向规则**：分叉点 = 主仓 `795ac9ccc`（2026-05-31）。**分叉后 1.20 的改动 ⇒ 1.21 对齐 1.20**；**分叉后 1.21 的改动 ⇒ 记入"1.20 反向对齐待办"**（不在 1.21 回退）。判定"是不是分叉后"的方法：`git cat-file -t <hash>` 在另一仓是否存在 + `git merge-base --is-ancestor <hash> <另一分支>`。
2. **毒提交口径**：`dfcc5c041`（1.20 侧）是毒提交，revert 家族 `dc57ba5c2`/`4af532ed1`/`a8e0b487f`/`1e0393178`。
   - 归属必须是**路径级** `git log -S <token> -- <path>`，**不能用全仓 `-S`**（同名符号在共享史里会误判）。
   - 毒落点粒度 = **(标识符 + 路径)**，**绝不是整个文件**。
   - **毒落点要"查毒"**：逐行看 `dfcc5c041^` 是否已有同一行/同一逻辑：
     - 同逻辑覆写 或 **正常且必要的功能**（如 `ModEntityTypeTagsProvider` 的标签成员/块）⇒ **照 1.20 落**；
     - 真正的新错误内容 ⇒ `DO-NOT-PORT`。
   - 落地前**必须核被调方**是否也是毒（如 `hasHandwrittenModel` 归毒但属必要功能 ⇒ 照落）。
3. **风格**：1.21 侧代码以 **1.20 逐字为准**，只允许"编译/语义必需"的适配；**1.21 既有的措辞差异只登记、不重写**；**不加新的解释性注释**；只抄 1.20 的"缺少未加入物品"类欠账注释（"已齐全"类注释不抄）。
4. **API 方向坑（高频出错点，务必按侧使用）**：
   - `MobEffect`：1.21 `hasEffect/getEffect/removeEffect/addEffect` 与 `MobEffectInstance` 构造器收 **`Holder<MobEffect>`** ⇒ 1.21 写 `ModEffects.X`（**不加 `.get()`**）；1.20 Forge/PortLib 收 raw `MobEffect` ⇒ 写 `ModEffects.X.get()`。datagen 自建 `addEffect(MobEffect,…)` 助手与 NeoForge `registerMobEffect(..., MobEffect...)` 是**例外**，仍要 `.get()`。
   - 随机数：1.20 用 `getRandom1211()`（PortLib shim）；1.21 用 `getRandom()`。
   - 事件名：1.20 `damage$Pre`/`damage$Post`（`PortLivingDamageEvent`）↔ 1.21 `livingDamage$Pre`/`$Post`（NeoForge `LivingDamageEvent`）。
   - 平台重命名例：`BlockPathTypes`→`PathType`、`VanillaGuiOverlay`→`VanillaGuiLayers`、`LootTableReference`→`NestedLootTable`、`PotionUtils`→`PotionContents`、`EnchantmentCategory`→数据驱动附魔、`Attributes.JUMP_STRENGTH_1211`→原生 `JUMP_STRENGTH`。
5. **禁改 10 文件**：`Confluence.java`、`common/init/ModEntities.java`（曾按裁定临时解锁一次注册 2 个抛射物）、`common/init/item/ModItems.java`、`common/init/ModDataComponentTypes.java`、`common/component/{Gun,Bullet}PropertyComponent.java`、`common/item/fishing/BaitItem.java`、`common/block/functional/network/PathService.java`、`common/event/game/ServerEvents.java`、`common/init/item/SummonItems.java`。
6. **架构原则（用户）**：模组内生物的攻击效果写在**生物自己的类**里，不写成事件处理。已落地：`Decayeder.onDamageDealt(ServerLevel, LivingEntity, DamageSource)`（远程生物，事件只做薄派发）+ `CursedSkull.doHurtTarget` 里的 33% 诅咒。**两侧签名/取法必须一致**（两侧只允许差 `.get()` 与 `getRandom1211()`）。`ModUtils.applyCursedSkullDebuff` 已删。
7. **提交纪律**：一律 `git commit -F <UTF-8无BOM消息文件> -- <路径列表>` **逐路径限定**；禁止 `add -A`、`reset`、`stash`、`checkout`；提交后必须 `git show --stat HEAD` 自查；**不得夹带他人条目**（见 §5 未提交清单）。
8. **行尾/编码**：改动的文件保持 CRLF；`fix_eol.py --check`（1.21 仓）必须 = **候选 0**。**1.20 仓有 57 个预存候选，永不全局归一**，只保证自己改的文件干净。写文件用字节级 UTF-8（PowerShell `Set-Content -Encoding ascii` 会毁中文）。
9. **加密资源**：`assets/confluence/geo/**/*.geo.json` 中首字节不是 `{` 的是 **Huffman+Vigenère 密文**，**禁止**做 EOL/BOM/格式化。
10. **不跑编译**（用户裁定：浪费时间）。只做静态证据（`build/_nfsrc_219/` 是解压的 NeoForge 源码，可查 API 签名）。编译由用户自己跑。
11. **子代理提问必须转达用户**：写手（subagent）有问题时，由主代理问用户，再把答复回给写手。

## 2. 仓与当前基线（生成时实测）
| 位置 | 分支 | HEAD | 备注 |
|---|---|---|---|
| `D:\Minecraft\1.21neoforge\confluence` | `neoforge-dev/1.21.1` | `218805a8b`（docs §四十六） | 主工作仓 |
| `D:\Minecraft\1.20forge\confluence` | `forge-dev/1.20.1` | `90a4da11b` | 1.20 源 |
| `Confluence-Magic-Lib`（子模块，两仓共用同一 origin） | 1.21 侧 `neoforge-dev/1.21.1`（`e433b85`）/ 1.20 侧 `forge-dev/1.20.1`（`595159d`） | — | 两分支共享历史，分叉基点 `154d1cf` |

## 3. 记录与工具（都在 1.21 仓）
- `notes/PORT-LANDING-RECORD.md`（**CRLF**，§一–§四十六）：每一批的裁定与证据；**下一节从 §四十七 开始**。
- `notes/PORT-LEDGER.md`（400 行，末列 = 状态）+ `notes/port-ledger-status.json`（键为 1.20 提交全 hash，**214** 条状态）。写状态用：
  `python tools/port2native/ledger_status.py <行>=<状态> [<行>=<状态>...] [--force]`（拒绝覆盖非 `TODO` 行；要改旧判才加 `--force`，且**改前必须有新证据**）。查：`--list <起> <止>`、`--check`。
- **下一开放行 = 231**（231–400 中仍有 **122** 个 TODO）。
- 工具（`tools/port2native/`）：`file_lag.py`、`residual_file.py`（逐文件标识符残留）、`row_attr.py`、`rw_rowscan.py`+`rw_attr.py`（行级粗筛/路径级归属）、`poison_residue_check.py`（`--worktree`/`--commit`/`--symbol [--path]`）、`registry_id_diff.py --verify-121`、`loot_coverage.py`、`ledger_status.py`、`commit_inventory.py`、`fix_eol.py`（`--repo`/`--paths`/`--check`）、`check_duplicates.py`、`tag_delta.py`（1.20↔1.21 实体类型 tag 逐块/逐成员差表）、`tag_member_check.py`。
- 状态词表：`PORTED`/`COVERED`/`SKIP-PLATFORM`/`SKIP-1.21-KEEPS`/`SKIP-PORTLIB`/`SKIP-1.20-REVERTED`/`MOVED`/`DEFER-ASSETS`/`DEFER-ARCH`/`DO-NOT-PORT`/`LOST?`/`REVERSE-ALIGNED`/`TODO`。

## 4. 本会话已落地（全部经过主代理独立复核）
- **架构重构双侧一致**：1.21 `97f18d9e0`+`9792fddd7`+`6e182eb75`；1.20 `b408792b1`（vfx 6 张 PNG）、`2e2192236`（重构镜像，含用户 `processCriticalDamage` WIP 一并提交）、`6cc9131af`。两侧 `onDamageDealt` 归一化**0 差异**。
- **tag 家族**：`8d8c70b05`（BOSSES 20/20 + `LibTags.SLIME` 30/30 + `DO_NOT_DROPS` 成员）、`8f6b8dbe4`（`BiomeTags.IS_OVERWORLD` 3→10）、`8fd7830f1`+`34ecd6caa`（13 块家族 + 新注册 `ModTags.EntityTypes.JELLY_FISH`）、`0e54b8a88`（`SPAWN_AT_GRAVEYARD` 补 `GHOST`；`terra_curio:slime` 改引用 `#confluence_magic_lib:slime`）。1.20 28 块 ↔ 1.21 27 块，成员零剔除。
- **行 198 `addOverrides()`** `578d805b3`（与 1.20 **38 行逐字相同**）；**decoBlockSet 同步** `6a02ba877`（8 成员 + 两循环体改委托；torch 家族留行 370）。
- **structure_set 族改 datagen** `197b11c19`（助手 + 17 调用 + 删 17 个手写 json；17 调用行与 1.20 **in-order 逐字相同**）。
- **Lib 归位**：子模块 `e433b85`（`EnchantmentUtils`→`LibEnchantmentUtils`，`enchantedBook`/`runIterationOnHand`/`SlotGroups` 搬回 Lib）+ 主仓 `fa48638cb`；行 221 三条 `enchantedBook` 已用 `LibEnchantmentUtils.enchantedBook(registryLookup, ResourceKey, n)` 形态。
- **文档**：§三十七–§四十六（最近提交 `218805a8b`）。

## 5. 上手第一件事：核对工作树（**这些都不是你的，别碰、别提交**）
1.21 仓 `git status --porcelain` 应含：
```
 M Confluence-Magic-Lib                    # 子模块内 gradle.properties 代理 WIP
 M .../event/game/entity/LivingEntityEvents.java   # 他人 BeeKeeper 暴击 WIP（未提交）
 M .../mod/util/EnchantmentUtils.java      # 他人的 javadoc→/// 风格改动（未提交）
A  .../assets/confluence/LICENSE-CC-BY-NC-SA-4.0.txt
A  .../assets/confluence/license.bin
M  .../textures/vfx/{heads/circle_05,heads/flare_01,particles/star_06,trails/trace_01,trails/trace_05,trails/trace_07}.png   # 暂存
 M TerraCurio / M TerraFurniture           # 子模块 gradle.properties 代理 WIP
?? .../textures/item/egg/demon_eye_spawn_egg.png
```
1.20 仓只应有 `D  .../vfx/licenses/kenney-particle-pack.txt`（暂存删除）。
**提交前若发现其它改动 ⇒ 停下报告，先问用户。**

## 6. 未决与待办
1. **行走**：写手已按用户指令结束（`a8f2ac59-def1-4a9f-a1cd-e2786dc88ffe`，可用 `send_message` 重新拉起，或新起一个）。**从行 231 继续**行走（`0ceb3c251`「portlib 升级为 1.2.2」初判 `SKIP-PORTLIB`）。
2. **行 370 火把家族**（`registerTorch`/`registerWallTorch`/`torchModel`/`torchTexture`）—— 走到那一行时落地；decoBlockSet 批次里已刻意排除。
3. **行 225 待裁**：1.20 `ServerGamePacketListenerImplMixin#extendCreativeStackLimit`（Bigger Stacks 创造模式堆叠同步兼容）依赖 PortLib 的 `@Diff` 注解，1.21 只剩 `captureSpeed`。若要恢复需去 `@Diff` 用原生 mixin 重写（尚未征询用户结论）。
4. **1.20 反向对齐待办**（1.21 分叉后改动，记着别在 1.21 回退）：`GORE_EFFECT_BLACKLIST` 的 `addOptionalTag`（`b3e73dfda`）、`processFlailWindBurst`/`WIND_BURST_AT_HIT`（`b9bca2ee1`）等。
5. **行 256 大行**（9745 新增行 / 193 STRONG）需独立专批；行 170 标 `LOST?` 待复核。
6. 已登记缺口：`martian_electric_bolt` 无中文名、3 条 bestiary 文本缺失、`ITEM_SOUL_GUI`/`test_soul_gui`（落在禁改的 `ModItems.java` 上）、`SpearProjectile` 组件化 = `DEFER-ARCH`；**datagen 未重跑**（编译受限）。

## 7. 已知坑（血泪，别再踩）
- `git log -S` 的**最早命中只说明"引入"**，判前必须核**目标文件当前状态**（`Snail`/`NPCReforgeMenu`/`WormPartGeoModel` 那三处都是 1.20 后来自己删的 ⇒ `SKIP-1.20-REVERTED`）。
- `git grep -l`（只列文件名）会把**注释/javadoc 里提到**该名字的文件算进来 ⇒ 判"是否仍在使用某平台"必须看**匹配行内容**或查 `import`/构建文件（TerraBlender 就是被这个坑出来的假缺口）。
- `git commit -F msg -- <paths>` 是 `--only` 语义；**省略路径列表会扫走已暂存的他人物**。
- PowerShell 不支持 heredoc；消息文件必须写完再 `git commit -F`；`git commit -- <未跟踪路径>` 会失败（先 `add`）。
- 判断"某段代码是 1.20 还是 1.21 主导"要看**分叉后**谁改的（`1516cbd2f`「part fluid type」这类 1.20 侧重组最容易误判方向）。
- 提取 1.20/1.21 文件用 `git show <rev>:<path> > file` 后在 Python 里读；别用 `Get-Content -Raw` 往返（会毁中文）。

## 8. 工作方式建议
- 主代理负责：定裁定、维护 `notes/` 与台账、**独立复核**写手的每一笔（推荐写一次性 Python 脚本抽取两仓方法体做归一化 diff，`this`/`.get()`/缩进归一后再比）。
- 写手（subagent）：执行落地，逐条给出 HEAD 实测证据（行号/计数/`--numstat`），不要把结论当前提。
- 每个批次结束后：`fix_eol --check` 必须候选 0 → 台账双写 → `notes/PORT-LANDING-RECORD.md` 追加一节 → 路径限定提交。
