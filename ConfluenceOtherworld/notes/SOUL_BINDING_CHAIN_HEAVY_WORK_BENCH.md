# 束魂锁链：配方改到重型工作台（无序、不需要灵雾）

## 改动

原配方在**工匠作坊**（`workshop`，`terra_curio:workshop`）里做，并且要求灵雾/墓地环境；
现在挪到**重型工作台**（`confluence:heavy_work_bench`）、**无序**，并且**去掉了环境要求**：

```java
// ModRecipeProvider：原行已删除（工匠作坊 + 灵雾）
workshop(..., AccessoryItems.SOUL_BINDING_CHAIN.toStack(), EnvironmentLevelAccess.matcher(null, null, true), ...);

// HeavyWorkBenchProvider：新增（位于冰冷魂戒之后）
shapeless(recipeOutput, AccessoryItems.SOUL_BINDING_CHAIN.toStack(),
        Ingredient.of(Items.CHAIN), Ingredient.of(FunctionalBlocks.SOUL_BOTTLE), Ingredient.of(SoulItems.FALLEN_SOUL_CORE));
```

- 材料不变：锁链 ×1 + 魂瓶 ×1 + 堕魂星核 ×1 → 束魂锁链 ×1；
- 合成站改为重型工作台，配方类型 `confluence:heavy_work_bench`，`ingredients` 是无序列表；
- **不再带 `environment` 字段**，任何地方都能做（`EnvironmentLevelAccess.Matcher.EMPTY`）；
- 配方 ID 从 `confluence:workshop/soul_binding_chain` 变为 `confluence:heavy_work_bench/soul_binding_chain`。

## 验证

两版都跑了 `runData`，生成结果一致（1.21.1 的路径是 `recipe/`，1.20.1 是 `recipes/`）：

```json
{
  "type": "confluence:heavy_work_bench",
  "ingredients": [
    { "item": "minecraft:chain" },
    { "item": "confluence:soul_bottle" },
    { "item": "confluence:fallen_soul_core" }
  ],
  "result": { "count": 1, "id": "confluence:soul_binding_chain" }
}
```

旧的 `workshop/soul_binding_chain.json` 在 1.21.1 的 `runData` 中被当作过期文件清除（日志 `removed stale: 1`）。

注意 `src/generated/` 在两个版本都被 `.gitignore` 忽略，所以生成物不进仓库，改动以 provider 代码为准。
