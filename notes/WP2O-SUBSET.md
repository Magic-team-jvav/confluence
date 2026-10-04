# WP2 批次 14 · 枢纽里的 boss/NPC 叶子 4 个（坐骑簇整体推迟）

> 承接批次 13 的「自包含枢纽」方法，继续吃 closure 2~4 的那批。
> 开工测得 7 个种子 → 8 个文件（含批次 13 推迟的 `AbstractMountEntity`），但**坐骑簇在本轮暴露为独立议题**，
> 于是本批只落 4 个，另 4 个（整条坐骑簇）单独推迟，理由见第三节。

## 一、本批落地 4 个 / 694 非空行

| 文件 | 非空行 | 关键改动 |
|---|---:|---|
| `common/entity/boss/DeerClops` | 491 | `setMaxUpStep(1.0F)` → `Attributes.STEP_HEIGHT` 属性；+3 条射弹注册 |
| `common/entity/npc/house/HouseValidater` | 110 | `PortTranslatableEnum` → NeoForge `TranslatableEnum`；+3 个方块标签键 |
| `common/entity/npc/mood/MoodData` | 90 | 转换器直出，无需改 |
| `common/entity/npc/dialog/NPCDialogLoader` | 54 | 转换器直出，无需改 |

既有文件改动：
- `ModEntities` **+3 条**：`THROWN_ICE_PROJECTILE` / `ICE_PILLAR` / `SHADOW_HAND`
  （1.20 `ModEntities.java:388/396/420`；三者尺寸与 `.noSave().noSummon()` 逐条照搬）
- `ModTags.Blocks` **+3 个键**：`NPC_HOUSE_CHAIR` / `NPC_HOUSE_TABLE` / `NPC_HOUSE_CONSTITUTE`
  （注册名已与 1.20 `ModTags.java:22-24` 逐个核对）。**只建键、不加 tag 内容** —— 1.20 的
  `ModBlockTagsProvider` 里这三条挂的是本模组的纸灯/家具方块，1.21 侧还没有那些方块；
  标签不存在时 `is(tag)` 恒为 false，不崩（与批次 11 的 `FLESH_ALLIANCE` 同一处理方式）。

## 二、两处 API 差异

### 1. `Entity#setMaxUpStep` 在 1.21 被删除，台阶高度改由 `Attributes.STEP_HEIGHT` 表达

`Entity#maxUpStep()` 只剩 getter（`Entity.java:3624`、`LivingEntity.java:3729`），
本仓库既有的写法是属性：`CreatureAttributeBuilder.stepHeight(double)` 就是
`add(Attributes.STEP_HEIGHT, value)`（`CreatureAttributeBuilder.java:252-255`）。
`DeerClops` 构造函数里的等价改写：

```java
var stepHeight = getAttribute(Attributes.STEP_HEIGHT);
if (stepHeight != null) stepHeight.setBaseValue(1.0F);
```

（不用 `Objects.requireNonNull` 是为了不新增 import；`getAttribute` 在 1.21 返回 `@Nullable`。）

### 2. `PortTranslatableEnum` 的转换**有规则却仍出错**（规则顺序/特异性问题）

1.20 的 `HouseValidater` 写 `import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;`。
规则表里确实有两条针对它的规则（`forge-to-neoforge.json`）：
`porttranslatableenum-native`（`org.mesdag.portlib.wrapper.common.PortTranslatableEnum`
→ `net.neoforged.neoforge.common.TranslatableEnum`）与 `porttranslatableenum-simple`（裸名）。
但生成物里却是 `import org.confluence.lib.util.PortTranslatableEnum;` —— 说明**另有规则先把
`org.mesdag.portlib.wrapper.common.*` 这个前缀改写成了 `org.confluence.lib.util.*`**，
之后那两条规则都匹配不上了，于是产出一个**根本不存在的类**。
本批手改这一处（`implements` 那侧由 `porttranslatableenum-simple` 正常处理，已 OK），
并在第四节记下这个坑；**没有现成规则能可靠修它**，因为根因是前缀改写规则先跑。

## 三、推迟了 4 个：整条坐骑簇（`AbstractMountEntity` 307 / `MountManager` 89 /
`MountItem` 52 / `MountItems` 27）

三处硬原因，合起来说明它该单独成批：

1. **`MountItems` 依赖 4 个 1.21 侧不存在的实体成员**：
   `ModEntities.RIDEABLE_SLIME` / `RIDEABLE_BEE` / `RIDEABLE_UNICORN` / `RIDEABLE_LAVA_SHARK`
   （`ModEntities` 里 `RIDEABLE` 命中 0）。这些成员指向 4 个坐骑实体类，而**它们不在本簇的闭包里**
   —— `dep_subset.py` 又一次因为「只经 `ModEntities` 成员访问」而看不见（成员级盲区的第 6 例）。
2. **`MountItems` 的注册方式要换**：1.20 写 `PortRegisterHandler.item(Confluence.MODID)`
   （PortLib）与空 `init()`；1.21 应写 `DeferredRegister.createItems(Confluence.MODID)`
   并在 `ModItems.register(eventBus)` 里注册（那是本仓库既有的集中注册点，见 `ModItems.java:159-164`）。
3. **`AbstractMountEntity` 与 `MountItem` 各有 API 不匹配**：前者 5 处（49/172 符号缺失、
   296/352 覆写签名不匹配、354 类型不匹配），后者 2 处（54 覆写不匹配、57 符号缺失）——
   1.21 的实体序列化（`RegistryFriendlyByteBuf`）与物品 API 都要 diff 之后改写。

推迟后本批其余 4 个文件编译干净，没有留半成品。

## 四、验证与收敛

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

| 时点 | 18 个重量物种闭包 | hubs |
|---|---:|---:|
| 批次 13 之后 | 163 | 161 |
| **批次 14 之后** | **159** | **157** |

转换器：`uncovered.json` = `[]`；`leftovers.txt` 有 2 行（`HouseValidater` 的 `PortTranslatableEnum`
import 与 `MountItems` 的 `PortRegisterHandler.item`）—— 前者本批手改，后者随坐骑簇一起推迟。
