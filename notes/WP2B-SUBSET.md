# WP2 批次 2 · 战士系 / 宝箱怪基类层（7 文件）

上一批（`notes/WP2A-SUBSET.md`）把这一批**推迟**了，原因是缺音效；
音效层已在 `09f8a23d0` 落地（`ModSoundEvents` 32 → 501），本批因此解锁。

## 一、边界

`dep_subset.py` 从 7 个种子算出的 `org.confluence.*` 闭包**恰好是这 7 个**（候选 7、无需 `--defer`）：

| 文件 | 非空行 | 用途 |
|---|---|---|
| `BaseMimic` | 334 | 宝箱怪基类（姿态同步、潜行/追击状态机） |
| `BaseWarriorMonster` | 274 | 近战战士基类（追击加速、动画/音效 profile、开门行为） |
| `MeleeSkeleton` | 176 | 骷髅战士（血月事件联动） |
| `HumanoidWarriorMonster` | 77 | 人形战士中间基类 |
| `ChargingMonster` | 56 | 冲锋型战士（ChargeAttackAction 行为树） |
| `RangedMonster` | 49 | 远程战士（RangedWindupAction + 射弹） |
| `JumpingWarriorMonster` | 48 | 跳跃型战士（EnemyOpenDoorGoal） |

合计 **1014 非空行**。`ModSoundEvents` / `BloodMoonGameEvent` / `ModTags` / BT 节点
等依赖在 1a~1c 与音效批之后**全部已满足**，所以闭包不再外溢。

## 二、本批的 API 手术（3 处，全是已固化的老面孔）

1. **`BaseMimic#defineSynchedData()` → `(SynchedEntityData.Builder builder)`**
   （1.21.1 `Entity.java:342` 是 `protected abstract void defineSynchedData(SynchedEntityData.Builder)`），
   `entityData.define(...)` → `builder.define(...)`。
2. **`BaseMimic#onAddedToWorld()` → `onAddedToLevel()`**（**原先误判成「1.21 删除了该钩子」，
   已更正**）：1.21.1 只是改名（`Entity.java:3733`，调用点 `ServerLevel.java:945` 等）。
   本类的顺序是「先做事、再 `super`」，搬过去后保持同样顺序，`super.onAddedToLevel()` 照旧调用
   （它会置 `isAddedToLevel`）。详见 `notes/WP1C-API-DIFF.md` 顶部的警告框。
3. **`BaseWarriorMonster` 的 `AttributeModifier`**：去掉 PortLib 包装的 `.unwrap()`，
   `pursuitSpeedModifier.getId()` → `pursuitSpeedModifier.id()`（1.21 是 record，
   构造签名 `(ResourceLocation, double, Operation)`）。

## 三、仍未落地的（WP2 后续）

| 项 | 卡在哪 |
|---|---|
| `slime/BaseSlime`（420） | 需 `MonsterEntities` / `ModTags` / `SweetSlime` / `TownSlimeNPC` |
| `SimpleWormMonster`（220） | 需 `MonsterEntities` / `SandstormGameEvent` / `OverworldUtils` |
| `MonsterAttributeScaling`（106） | 需 `BaseCritter` / `BaseNPC` / `SweetSlime` |
| `PirateRangedMonster`（156） | 需 `MonsterEntities` / `PirateInvasionGameEvent` / `PirateShot` |
| 具体物种 | 108 个 `common/entity/monster/**` 缺文件里剩下的（约 10400 非空行） |

共同点是都要 `MonsterEntities`/`ModTags`/其他物种——即**注册层 + 物种互相依赖**，
按依赖序下一批应该先处理「不依赖注册层的独立物种」，再回头做注册层。

## 四、复现命令

```powershell
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias   "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --seed    org/confluence/mod/common/entity/monster/BaseMimic.java `
  --seed    org/confluence/mod/common/entity/monster/BaseWarriorMonster.java `
  --seed    org/confluence/mod/common/entity/monster/MeleeSkeleton.java `
  --seed    org/confluence/mod/common/entity/monster/HumanoidWarriorMonster.java `
  --seed    org/confluence/mod/common/entity/monster/ChargingMonster.java `
  --seed    org/confluence/mod/common/entity/monster/RangedMonster.java `
  --seed    org/confluence/mod/common/entity/monster/JumpingWarriorMonster.java `
  --out     notes/WP2B-SUBSET.md
```
