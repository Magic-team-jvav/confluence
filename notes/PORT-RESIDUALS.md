# 微量残留台账（够不上一条移植提交，但不该丢）

来源：第二轮分诊（`notes/TRIAGE-PASS2.md`）对 **A3 COVERED** 提案做行级复核时抠出来的差异。
复核方法：取该提交自己的新增行（`git log -p --unified=0`，剔除 Port 引用行与 import/package 行），
逐行在 1.21 侧映射到的同名文件里查找；未命中的行再人工判定是「平台 API 差异 / 纯风格差异」还是「真欠账」。

性质分三类：
- **真欠账**：1.21 确实缺这段逻辑 → 见下表，逐条待补
- **平台差异**（不算欠账）：1.20.1 与 1.21.1 的签名/类型不同，1.21 侧本来就是等价写法
- **风格差异**（不算欠账）：语义等价的写法差别

---

## 一、真欠账（待补，逐条很小）

### 1. `ExtraInventory#getMount(boolean dye)`

| 项 | 内容 |
|---|---|
| 来源提交 | `810fb6b14`（2026-09-13「版本更新」） |
| 1.20 侧 | `common/attachment/ExtraInventory.java:183` `public ItemStack getMount(boolean dye) { return getEquipment(MOUNT_INDEX, dye); }` |
| 1.21 现状 | 同类文件有 `getPet`(171) / `getLightPet`(175) / `getMinecart`(179) / `getHook`(183)，**唯独没有 `getMount`**；`MOUNT_INDEX = 4` 两侧都有（1.21:63、1.20:59） |
| 补法 | 在 1.21 的 `getHook` 旁补一个同形方法；补前先确认 1.21 侧没人调用（`grep getMount`） |
| 状态 | 待办 |

### 2. `MineTunnelsStructure` 的「世界最低点」取值 —— **需先在 1.20 侧确认是不是写错了**

| 项 | 内容 |
|---|---|
| 来源提交 | `b9ccf1061`（2026-09-19「Worldgen fix」） |
| 1.20 侧 | `common/worldgen/structure/MineTunnelsStructure.java:75` `int worldMinY = context.chunkGenerator().getGenDepth();` |
| 1.21 现状 | `:74` `int worldMinY = context.heightAccessor().getMinBuildHeight();` |
| 为什么不当欠账处理 | 紧随其后的分支是 `worldMinY < 0 ? 20 : 50`（maxY）与 `worldMinY < 0 ? -40 : 10`（minY），
字面意思（字段名 `worldMinY`）要求「世界最低点」。1.21 的 `getMinBuildHeight()` 在扩展高度世界里是 **-64**（<0 → 走负 Y 分支）；
1.20 的 `ChunkGenerator#getGenDepth()` 是**总高度**（如 384，恒为正 → 负 Y 分支永不触发）。
即 1.20 的写法会让「世界向下扩展到 Y<0」时隧道反而只在 Y≥10 生成，语义与字段名矛盾 |
| 结论 | 疑似 1.20 侧 bug，不宜按「1.20 为准」（Q1）机械搬。**待在 1.20 侧确认意图**：若确认是修复（有意让隧道不下探），再移植；若是笔误，应反向修 1.20 |
| 状态 | 待确认（不阻塞） |

---

## 二、已核实**不是**欠账（避免重复怀疑）

### 3. `BiomeRegionTable` 的两处写法差异 —— 纯风格

- 1.21：`new BiomeRegionTable(type, allocator, List.copyOf(regions), List.copyOf(entries), List.copyOf(biomes))` + 下标循环
- 1.20：`new BiomeRegionTable(type, allocator, regions, entries, List.copyOf(biomes))` + 增强 for
- 判定：两者都是防御性拷贝 / 等价遍历，无行为差异。此前 `notes/WIP-WORLDGEN-CLOSEOUT.md` 里记的
  「2 处未移植的美化 hunk」即指此处，现可销案。

### 4. `AskForSoftcoreScreen` 的 render 覆写点 —— 平台 API 差异

- 1.21：覆写 `renderBackground(GuiGraphics, mouseX, mouseY, partialTick)` + `PacketDistributor.sendToServer(...)`
- 1.20：覆写 `render(GuiGraphics, mouseX, mouseY, partialTick)`，内部 `super.renderBackground(guiGraphics)`、`super.render(...)` + `PortPacketDistributor`
- 判定：1.21.1 的 `Screen#renderBackground` 带鼠标坐标、1.20.1 的不带，覆写点因此不同；1.21 的形态就是原生等价物。
  此前记为「`AskForSoftcoreScreen` 行为差异」的问题销案。

### 5. 其余 A3 行抽查到的「未命中行」

| 提交 | 未命中 | 判定 |
|---|---|---|
| `1ab2b43ac` | 18 / 128 | 中文注释改写 + `for (Entry entry : list)` 风格 → 已由 `adb78dbae` 移植，非欠账 |
| `ac0d4a88d` | 4 / 108 | 1.20 侧 `GuiGraphics#render/renderBackground` 签名差异 → 非欠账 |
| `55262a7f2` | 14 / 34 | `vertex(...).color(255,255,...)`（1.20 int RGBA）对 1.21 float RGBA → 非欠账 |
| `ff7a62eea` | 6 / 23 | 1.21 的 `GlobalCloakData` 本来就是 `Reference2ObjectOpenHashMap`，差异只是 1.20 顺手加的 `final` → 非欠账 |
| `fc4498b8b` | 0 / 101 | 全命中 |
| `c22ea51d4` | 0 / 8 | 全命中 |
| `4557b85fc` | 0 / 0 | 新增行全部引用 Port 类型，可计数新增为 0 |

---

## 三、工具侧自检结论

`notes/COMMIT-LAG.md` + `TRIAGE-PASS2.md` 的 A3（COVERED）判定在这 9 行上**全部经得起行级复核**，
其中 `1ab2b43ac` 是本轮唯一「已知答案」的锚点：它已由 `adb78dbae` 手工移植，工具独立给出 COVERED +
相关文件 overlap 98~100%，与人工结论一致。
