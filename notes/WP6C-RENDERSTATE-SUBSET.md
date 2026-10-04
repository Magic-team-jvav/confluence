# `client/effect/RenderStateShardAccessor` 整类搬迁（G5′ 遗留项收口）

> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**。规模：**1 新文件（106 行）+ 3 改文件**。

## 一、为什么单独做这一批

`notes/GUNS-G5P.md` 第四节把它记为**整类缺失**：1.20 的 `client/effect/RenderStateShardAccessor`（115 行、
9 个成员 + `create` 工厂）在 1.21 侧**完全不存在**，而 1.21 的多个渲染器（`GeoNegativeVolumeRenderer`、
`GeoNormalRenderer`、`GeoSpecialSlimeRenderer`、`TownSlimeRenderer`、`SwordProjectileRenderer`…）
在 1.20 都依赖它。G5′ 当时只把枪械内联所必需的 `TRAIL_RENDER_TYPE` 按 1.21 惯例塞进了 `ModClientSetups`，
并在笔记里写明「**若日后做该 WP，应把 `ModClientSetups.TRAIL_RENDER_TYPE` 一并回迁到新类**」。

本批就是那次回迁 + 整类落地。它同时是「WP3 客户端族批次」的前置之一（那批的 `GeoNormalRenderer` 需要它）。

## 二、本批内容

| 文件 | 处理 |
|---|---|
| `client/effect/RenderStateShardAccessor`（新，106 行） | 1.20 同名文件逐字搬入，转换器改写 `vanilla-api-1.20-to-1.21` 相关项；**无 PortLib 残留、无 leftover、编译一次通过** |
| `client/event/ModClientSetups` | `TERRA_SWORD_RENDER_TYPE` → **改指** `RenderStateShardAccessor.ENTITY_TRANSLUCENT_EMISSIVE`（两条内容逐项一致：同名、同 `NEW_ENTITY`/`QUADS`/1536/`true,false`、同 emissive shader、同 `textures/mask/sword.png`、同 `TRANSLUCENT_TRANSPARENCY`/`COLOR_WRITE`/`NO_CULL`/`OVERLAY`）；`GLINT_FF0000`/`GLINT_RAINBOW` → **委托**给新类（**不重建**，否则 `ColoredGlintContext.COLORED_GLINT_CONTEXTS` 会重复登记两份同名 glint）；删除 G5′ 临时加的 `TRAIL_RENDER_TYPE` |
| `client/renderer/entity/projectile/RainbowBoulderRenderer` | `ModClientSetups.TRAIL_RENDER_TYPE` → `RenderStateShardAccessor.TRAIL_RENDER_TYPE`（回到 1.20 `RainbowBoulderRenderer:43` 的原始归属） |
| `integration/terra_entity/trail/TerraSwordTrail` | `ModClientSetups.TERRA_SWORD_RENDER_TYPE` → `RenderStateShardAccessor.ENTITY_TRANSLUCENT_EMISSIVE` |

**没有第二份同名渲染类型了**：`RenderType.create` 同名调用两次会各自注册一份（1.20 只有一份），
本批之后 `entity_translucent_emissive`、`trail_render_type`、两个 glint 都只有单一来源。

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（转换后一次通过；整合后再跑一次仍 0） |
| 残留引用 | `ModClientSetups.{TRAIL_RENDER_TYPE,TERRA_SWORD_RENDER_TYPE}` → **0 处** |
| 子模块 | 未改动 |
| 待游戏内验收 | 彩虹巨石拖尾（走 `TRAIL_RENDER_TYPE`，lightning 底 + `WEATHER_TARGET`）、剑气拖尾（`ENTITY_TRANSLUCENT_EMISSIVE`）、`GuideVooDooDoll`/宝藏袋的彩色 glint |

## 四、尚未消费的成员（留给后续批次，非缺陷）

`LASER`、`HILL_OF_FLESH_BOUNDARY`、`EYES`、`entityGlow`、`entityTranslucentCullOverlay` 这 5 个成员
在 1.21 侧**暂无调用方** —— 1.20 的调用方（`LaserProjectileRenderer`、`HillOfFleshRenderer`、
`PhasebladeRenderer`、`GeoNormalRenderer`/`GeoSpecialSlimeRenderer`/`TownSlimeRenderer`、
`CrystalVileShardProjectileRenderer`）要么是 1.21 侧另写的实现，要么随
`notes/WP3-CLIENTFAMILY-HANDOFF.md` 里那批一起落地。**先整类就位、后接消费方**，与 `ModCustomRegistries`
（批次 25）同一做法。
