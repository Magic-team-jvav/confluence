# 灵魂不足以支付时，叠加样式生命条染淡蓝

## 1. 触发条件

「灵魂不足以支付下一次施法」= 会走生命代偿（扣血施法）。判定新入口：

```java
/// 下一次施法的灵魂是否不够付：为 true 时这一下会走生命代偿（扣血）。
public static boolean isSoulOverdraftPending(@Nullable Player player) {
    if (player == null || !isSoulMode(player) || player.isCreative()) return false;
    SoulSkillStack stack = getCurrentSkillStack(player);
    if (stack.isEmpty() || stack.getSoulSkill().isPassive()) return false;
    float soulCost = stack.getSoulSkill().getSoulCost(stack);
    return soulCost > 0.0F && getSoul(player) < soulCost;
}
```

- 只看**当前选中技能**的灵魂消耗 vs 当前灵魂，这两个值客户端都有（已同步），因此纯客户端判定，不需要新包；
- 创造模式、非法术状态（魔力模式）、空槽/被动槽、零消耗技能都不触发；
- 这是**预测**：真正的结算在服务端 `prepareSoulConsumption`，
  自然法则的 20% 免消耗 / 灵魂透支期间免消耗仍可能让它实际不扣血，警示偏保守。

## 2. 表现

`TerraStyleHealthHud.OVERLAY` 在绘制生命心之前按灵魂条的淡蓝 `#ABFFFE` 上色，画完立刻还原：

```java
boolean soulOverdraft = PlayerUtils.isSoulOverdraftPending(player);
if (soulOverdraft) {
    guiGraphics.setColor(SOUL_TINT_RED, SOUL_TINT_GREEN, SOUL_TINT_BLUE, 1.0F);
}
colorDraw(..., HEALTH, HEALTH_HIGH, HEALTH_LOW, ...);
if (soulOverdraft) {
    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
}
```

- 只染**红心血量主体**；吸收生命（金色）与中毒/凋零等覆盖心不在染色范围内，避免把警示色扩散到别的状态；
- `setColor` 走 `PositionColorShader` 的 ColorModulator，与灵魂条用同一套染色方式，颜色完全一致。

## 3. 调色板单点化

淡蓝三通道原本是 `TerraStyleManaHud` 里的私有常量，现在提到 `ClientUtils`：

```java
public static final float SOUL_TINT_RED = 0xAB / 255.0F;
public static final float SOUL_TINT_GREEN = 1.0F;
public static final float SOUL_TINT_BLUE = 0xFE / 255.0F;
```

灵魂条与生命条警示共用同一份值，以后改色只改一处。

## 4. 只做了叠加样式

| 样式 | 是否染色 |
| --- | --- |
| 叠加（`healthStyle=overlay`，默认） | ✅ 本轮实现 |
| 精致（`healthStyle=legacy`） | ❌ 未改：该样式的心是图集里的彩色精灵，不是 `colorDraw` 调色板，染色会把红心整体压成蓝色，观感与叠加样式不一致 |

如果也要精致样式跟随，需要另画一套淡蓝心精灵，或接受整体 `setColor` 压色，可以下一轮再定。

## 5. 文件索引（两版同名同改）

```
common/attachment/../util/PlayerUtils.java        isSoulOverdraftPending
util/ClientUtils.java                             SOUL_TINT_R/G/B 公共常量
client/gui/hud/TerraStyleHealthHud.java           叠加样式生命条染色
client/gui/hud/TerraStyleManaHud.java             改用公共常量（去重）
```

## 6. 仍未做

两版都没有进游戏实测：触发时机（切技能、灵魂刚好不够）、与吸收生命的叠放、退出灵魂模式后是否及时复原。
