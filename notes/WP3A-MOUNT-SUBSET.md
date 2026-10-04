# 批次 23 · 坐骑子树（`common/entity/mount/**` + 坐骑物品 + `MountManager`）

> 起因：WP4 闸门（`notes/WP4-NPC-GATE.md`）量出「Boss 切片与 WP4 是同一个 155 文件强连通团」，
> 唯一能拆开的路线 B 需要先裁决。本轮先做**同一大团里真正自包含、且能清掉一个已知成员级盲区**
> 的那一块 —— 坐骑。它同时也是任务书第七节第 2 条点名的盲区（`ModEntities.RIDEABLE_*`）。

## 一、开工测量（先量再动手）

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed org.confluence.mod.common.entity.mount.AbstractMountEntity `
  --seed org.confluence.mod.common.entity.mount.RideableBeeMountEntity `
  --seed org.confluence.mod.common.entity.mount.RideableLavaSharkMountEntity `
  --seed org.confluence.mod.common.entity.mount.RideableSlimeMountEntity `
  --seed org.confluence.mod.common.entity.mount.RideableUnicornMountEntity `
  --seed org.confluence.mod.common.item.mount.MountItem `
  --seed org.confluence.mod.common.mount.MountManager
# {"candidates": 7, "kept": 7, "new": 7, "removed": 0, "unused_defer": 0}
```

**7 个文件完全自包含**（类型级边全部已在 1.21 侧满足）。加上物品注册层与 package-info 共 **9 个新文件 / 973 非空行**。

`check_duplicates.py`（8 个文件）→ **0 处「疑似移动/重复」**。但要注意 1.21 侧**有**一套旧形态坐骑，
只是名字/命名空间都不同，所以闸门没报：

| 1.20（事实来源，内联进主模组） | 1.21 旧形态（TerraEntity） | 性质 |
|---|---|---|
| `common/entity/mount/AbstractMountEntity` | `terraentity/entity/rideable/AbstractRideableEntity` | 不同 FQN、不同实现 |
| `RideableSlimeMountEntity` / `RideableBeeMountEntity` | `RideableSlime` / `RideableBee` | 同上 |
| `common/init/item/MountItems`（`confluence:*`） | `terraentity/init/item/TERideableItems`（`terraentity:*`） | 同上 |
| `common/entity/mount/*` 的 4 个实体 | `terraentity/init/entity/TERideableEntities` | 同上 |

**命名空间不同（`confluence:rideable_slime` vs `terraentity:rideable_slime`），不存在 id 冲突**
（落地前专门查过：这是「重复注册同 id → 启动期崩」的高危点）。旧形态属既定的
**TerraEntity 退役**过渡态，随退役批次删除。

## 二、落地清单

| 文件 | 动作 | 说明 |
|---|---|---|
| `common/entity/mount/{AbstractMountEntity, RideableBee/LavaShark/Slime/Unicorn MountEntity}` | 新增 5 | 转换器直出 + 6 处 API 修正 |
| `common/entity/mount/package-info` | 新增 | 随包 |
| `common/item/mount/MountItem` | 新增 | 坐骑物品基类 |
| `common/mount/MountManager` | 新增 | **与 1.20 逐字相同**（`git diff --ignore-all-space` = 0/0） |
| `common/init/item/MountItems` | 新增 | `PortRegisterHandler.item` → `DeferredRegister.createItems`；5 个物品 |
| `common/init/ModEntities` | 改（+4 成员） | **补上盲区**：`RIDEABLE_UNICORN/LAVA_SHARK/SLIME/BEE`（1.20 `ModEntities:622-640` 逐字，`noSummon().noSave()`） |
| `common/attachment/ExtraInventory` | 改（+5 行） | 补 `getMount(boolean)`（`MOUNT_INDEX` 常量本来就在，只缺取用方法） |
| `client/ModKeyBindings` | 改（+14 行） | 补 `MOUNT` / `MOUNT_DESCEND`（1.20 `:69/70`） |
| `util/PrefixUtils` | 改（+28 行） | 补 1.21 侧缺失的 `attributeWithoutHeldItem` / `heldItemContribution`（1.20 `:95-115`） |
| `Confluence` / `TickEvents` / `PlayerEvents` | 改 | 接线：注册物品表、`MountManager.validate`（玩家 tick）、`MountManager.dismiss` ×3（登出/重生/换维度，位置与 1.20 一致） |

## 三、挖到的 1.21 API 差异（全部实测，编译门从 22 → 13 → 4 → 0）

| 位置 | 1.20 | 1.21 实测 |
|---|---|---|
| `AbstractMountEntity:49` | `setMaxUpStep(1.0F)` | **方法已删**。⚠️ 坐骑 `extends Entity`（**不是** `LivingEntity`）→ **不能**用 `Attributes.STEP_HEIGHT`（那是 LivingEntity 的），只能覆写 `Entity#maxUpStep()`（`Entity.java:3624`，默认 0）返回 1.0F |
| `AbstractMountEntity:172` | `Attributes.JUMP_STRENGTH.getDefaultValue()` | `Holder<Attribute>` 没有该方法 → `Attributes.JUMP_STRENGTH.value().getDefaultValue()` |
| 三个子类 + 基类 | `getPassengersRidingOffset()`（返回 double） | 改为 `getPassengerAttachmentPoint(Entity, EntityDimensions, float)` 返回 **`Vec3`**（`Entity.java:2085`）；子类的「基值 + 偏移」改写成 `.add(0, offset, 0)` |
| `AbstractMountEntity:352` | `getAddEntityPacket()` | 多一个 `ServerEntity` 形参（先例 `BaseBossPart.java:328`） |
| 三个子类 | `defineMountSynchedData()` 无参 | ⚠️ **转换器缺陷**：1.20 靠 `this.entityData` 写同步字段，1.21 改成 `defineSynchedData(SynchedEntityData.Builder builder)`、字段没了；转换器把子类的 `entityData.define(...)` 改写成 `builder.define(...)`，**却没法给这个无参钩子传入 `builder`** → 3 个子类同时报「cannot find symbol」。修法：把钩子签名补成 `(SynchedEntityData.Builder builder)`（基类 + 3 子类同步改） |
| `RideableBeeMountEntity:71` | `ForgeMod.EMPTY_TYPE.get()` | `NeoForgeMod.EMPTY_TYPE.value()`（`net.neoforged.neoforge.common.NeoForgeMod`） |
| `MountItem:56` | `appendHoverText(ItemStack, @Nullable Level, …)` | 第 2 参 `Item.TooltipContext` |
| `PrefixUtils`（新搬的两方法） | 修饰符表键是裸 `Attribute`；`Operation.ADDITION/MULTIPLY_BASE/MULTIPLY_TOTAL`；`getAmount()/getOperation()` | 键是 **`Holder<Attribute>`**（TerraCurio `AttributeModifiersValue:16`）；枚举改名 **`ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL`**（`AttributeModifier.java:64-66`）；`AttributeModifier` 是 record → **`amount()/operation()`** |

## 四、校验

| 检查 | 结果 |
|---|---|
| 移动 vs 新增审计 | 8 个文件 0 处「疑似移动/重复」（旧形态在 TerraEntity，FQN/命名空间都不同，已在上表登记） |
| 闭包 | 7 文件自包含；`stage_batch --convert --apply` 新增 9、**覆盖 0**、`uncovered=[]` |
| leftovers | 1 行（`MountItems` 的 `PortRegisterHandler.item`）→ 已改为 `DeferredRegister.createItems` |
| 编译门 | **第 1 次 22 处 → 13 → 4 → 0**（4 轮，全部是上表的 API 差异与那个转换器缺陷） |
| 忠实度 | `MountManager` 与 1.20 **逐字相同**；其余文件的 `--ignore-all-space` 差异 5~34 行，全部是上表的 API 适配 + 我加的说明注释 |
| 换行 | fix_eol 后全部 w/crlf |

## 五、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| 坐骑**客户端渲染器** | 没有渲染器时坐骑仍能骑（逻辑在服务端），但客户端看不到模型；属客户端批次，与动物/怪物渲染器同一条既有约定 |
| `MountTogglePacketC2S`（按键召唤/收回） | **1.21 侧根本没有这个包**（1.20 `network/c2s/MountTogglePacketC2S`）；它要配 `MOUNT` 按键的客户端 handler 一起做 —— 本批只落了按键定义与 `MountManager.toggleFromSlot` 的服务端能力 |
| 创造模式标签页条目 | 1.20 是 `ModTabs:1916 acceptAll(MountItems.ITEMS, output)`；1.21 的 `ModTabs` 需要按它的 `DeferredRegister.Items` 遍历写法接一次（属标签页批次） |
| 数据生成（lang / 价值 / 礼物 / 战利品 / 标签 / 模型） | WP7；1.20 侧散在 `Mod{Chinese,English}Provider`、`ValueSubProvider`、`GiftSubProvider`、`ModItemTagsProvider`、`ModItemModelProvider` 里 |
| `AnglerNPC` 的 `FUZZY_CARROT` 产出 | 依赖 WP4 的 NPC 层 |

**本批的行为变化**：5 个坐骑物品与 4 个坐骑实体从此刻起真实注册（`confluence:rideable_*` / `confluence:slimy_saddle` 等），
`MountManager` 接进了玩家 tick 与登出/重生/换维度三处清理。
