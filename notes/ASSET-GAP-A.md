# 资源补漏 A 类：910 处引用里 17 处「引用得到、文件不存在」收口

> 本批**纯资源**，不改任何 java。编译门 `ConfluenceOtherworld` 0 错误 / 0 文件（形式验证）。
> 起因见 `notes/WP2-MONSTER-LEFTOVER.md` 第五节：写狼人/大风气球怪资源时做了一次全仓
> 「`Confluence.asResource("…")` 引用 × 磁盘存在性」审计，910 处引用里 **37 处文件不存在**；
> 其中狼人 2 处随物种批修掉，本批修掉可拷贝的 17 处，剩 18 处（B 类 6 + C 类 12）按结论留档。

## 一、本批拷贝清单（17 个文件，全部与 1.20 同哈希）

| 文件 | 1.20 磁盘 | 1.21 引用点 | 修好之后 |
|---|---|---|---|
| `geo/entity/demon_eye.geo.json` | ✓ | `DemonEye:252` | 恶魔眼有模型 |
| `animations/entity/demon_eye.animation.json` | ✓ | `DemonEye:271` | 恶魔眼有动画 |
| `geo/item/flail/handle.geo.json` | ✓ | `BaseFlailItemRenderer:27` | 连枷手柄有手持模型 |
| `textures/entity/flail/anchor.png` | ✓ | `FlailComponent:391` | 锚连枷球体贴图 |
| `textures/entity/flail/chain_knife.png` | ✓ | `FlailComponent:301` | 链刃 |
| `textures/entity/flail/drippler_crippler.png` | ✓ | `FlailComponent:270` | 滴滴怪连枷 |
| `textures/entity/flail/flairon.png` | ✓ | `FlailComponent:286` | 花之焰 |
| `textures/entity/flail/flower_power.png` | ✓ | `FlailComponent`、`FlailProjectileRenderer:39` | 花之力 |
| `textures/entity/animal/bird.png` | ✓ | `Bird:112` | 小鸟 |
| `textures/entity/animal/blue_jay.png` | ✓ | `BlueJay:18` | 蓝松鸦 |
| `textures/entity/animal/cardinal.png` | ✓ | `Cardinal:18` | 红衣主教鸟 |
| `textures/entity/animal/butterfly/hell_butterfly.png` | ✓ | `HellButterfly:39` | 地狱蝴蝶 |
| `textures/entity/animal/butterfly/prismatic_lacewing.png` | ✓ | `PrismaticLacewing:32` | 七彩草蛉 |
| `textures/entity/animal/fairy/faeling.png` | ✓ | `Fealing:43` | 仙灵 |
| `textures/gui/noise.png` | ✓ | `RenderStateShardAccessor:22` | 噪声贴图（474×474） |
| `textures/trail.png` | ✓ | `LyraRenderTypes:42` | 里拉琴拖尾（8×8） |
| `textures/damage_font.png` | ✓ | `Info:18` | 伤害数字字体图（99×12） |

## 二、验证

| 项 | 结果 |
|---|---|
| 拷贝方式 | Python `shutil.copy2`（二进制，不经文本编码路径） |
| 哈希 | 17/17 与 1.20 逐字节相同 |
| PNG 完整性 | 16 张 PNG 全部通过包头校验，尺寸符合预期（见上表括注） |
| `.mcmeta` 伴随文件 | 已扫描 `textures/gui/**` 等目录：本批拷贝项没有 `.mcmeta`，无遗漏 |
| 审计复算 | `missing under assets/confluence` 由 **37 → 18**（狼人 2 + 本批 17 = 19 处收口） |
| 编译门 | `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` → **0 错误 / 0 文件** |

## 三、未修的两类（留档，不做）

**B 类（6 处，1.20 自己也缺 → 上游既有缺陷）**：`textures/atlas/entity_blood.png`、
`textures/environment/sunglasses_boulder.png`、`textures/gui/information/mechanical_lens.png`、
`textures/slot/accessory.png`、`textures/entity/ghastly_projectile.png`、`textures/entity/mushroom_projectile.png`。
判据：1.20 源码引用同一路径，但 1.20 磁盘上同样没有文件，且 `git log --all -- <path>` 命中 **0 提交**
→ 该路径在 1.20 从未存在。1.21 与 1.20 行为一致，**不擅自造资源**（造了反而与 1.20 分叉）。

**C 类（12 处，1.21 独有路径）**：`geo/entity/flail/` 8 个模型 +
`textures/entity/flail/{chain_guillotines,golem_fist,ko_cannon}.png` + `geo/item/phaseblade_bar.geo.json`。
判据：这 12 个路径在 1.20 **任何提交**里都不存在，而引用它们的 1.21 代码是**新实现**
（`FlailComponent` 自述「与 1.21 DataComponent 解耦，纯 POJO，兼容 1.20.1 移植」；
1.20 HEAD 的 `FlailComponent` 只引用 `textures/entity/flail/*`、不引用 `geo/entity/flail/*`）。
→ 不属于「向 1.20 对齐」的欠账，需单独确认这批 1.21 连枷/光剑内容的资源出处。

## 四、下一批建议

回到物种：`ANGRY_DANDELION`（1.20 `MonsterEntities:685`）现在**所有代码依赖都齐了** ——
类 `AngryDandelion.java:79` 已在 1.21、`ModEntities.DANDELION_SEED` 已注册（`ModEntities:258`）、
`checkAngryDandelionSpawn` 随物种批落地。缺的是**三处接线 + 6 个资源**：

| 缺什么 | 1.20 出处 |
|---|---|
| `MonsterEntities.ANGRY_DANDELION` 注册 | `MonsterEntities:685` |
| 渲染器注册（`GeoNormalRenderer` + id） | `ModClientEvents:826` |
| **`DANDELION_SEED` 弹幕渲染器注册**（1.21 侧这个弹幕**至今没有任何渲染器**） | `ModClientEvents:465` |
| 放置规则：并进本批已建好的 `checkAngryDandelionSpawn` 那一组 | `CreatureSpawnPlacements:112-113` |
| 资源 6：`{geo,animations,textures}/entity/{angry_dandelion.*, proj/angry_dandelion.*}` | 1.20 磁盘均有（逐字节可拷） |

> ✅ 这一批已于紧随其后落地（行号勘误与明细见 `notes/WP2-MONSTER-LEFTOVER.md` 第六节）。


> ⚠️ 上一版本笔记里「本批补上了它的贴图」是**笔误**：本批 17 个文件里没有 dandelion
> （1.21 侧此前没有任何 `dandelion` 引用，所以它也没进那 37 处审计）。上面表格才是实际清单。

