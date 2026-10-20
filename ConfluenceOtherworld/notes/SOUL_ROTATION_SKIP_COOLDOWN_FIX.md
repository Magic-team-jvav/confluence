# 魂术轮转真正跳过冷却中的技能（三处入口 + 实机验证）

上一轮只改了 `rotateToNextSkill`，实机测出来还是“停在冷却技能上”。原因是技能栏的主路径根本不走它：

- 施放**强力附魂系列**技能（强力附魂 / 魂引天星 / 灵魂突击 / 掠夺之魂 / 灵魂透支）时，
  `chainCastEnhancedSoulSeries` 会把该系列其余技能连锁放完，然后**无条件回到起手技能**——
  而起手技能刚刚才被施放，必然在冷却中，于是选中位就停在冷却技能上；
- 按施法键时，如果当前技能在冷却，`tryUseSoulSkill` 直接 `reject`，不会往后找。

## 现在的三条路径都跳过冷却

1. **轮转（非该系列技能）** `rotateToNextSkill`：改用 `findNextReadySoulSkillIndex`，
   从当前位向后找第一个「非空 / 非被动 / 当前可用 / 不在冷却」的槽，找不到就保持不动。
2. **连锁结束落点** `chainCastEnhancedSoulSeries`：连锁放完后同样从 `startIndex` 往后找可用槽，
   找到就切过去，全都在冷却才留在起手技能。
3. **按键直达**（新增）`tryUseSoulSkill`：装了魂术轮转、且当前技能还在冷却时，
   同一次按键先跳到下一个可用槽再施放，避免按键空转。`findNextCastableSoulSkillIndex`（空槽/被动跳转）
   保持原样，因此“落到冷却技能”的老行为只在没有可用技能时才会出现。

新工具方法（两版同名）：

```java
private static int findNextReadySoulSkillIndex(ServerPlayer player, int start) {
    List<SoulSkillStack> equipped = getEquippedSoulSkills(player);
    int size = equipped.size();
    for (int step = 1; step < size; step++) {          // 不含 start 本身
        int index = Math.floorMod(start + step, size);
        SoulSkillStack candidate = equipped.get(index);
        if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()) continue;
        ResourceLocation id = candidate.getSoulSkill().getId();
        if (!isSoulSkillAvailable(player, id) || getSoulSkillCooldown(player, id) > 0) continue;
        return index;
    }
    return -1;                                          // 没有可用的就保持当前
}
```

## 实机验证（专用服务器 + FakePlayer）

用临时事件钩子跑了一次真实服务端（两版各一次，跑完已删除测试类）：
装备 `[0] 魂引天星 / [1] 引魂 / [2] 魂术轮转`，主手魂武器，灵魂 200。

1.21.1 日志：

```
[SoulRotationTest] start | index=0 | starCallCd=0 | soulLureCd=0
[SoulRotationTest] cast STAR_CALL(index 0) -> result=true | index=1 | starCallCd=600 | soulLureCd=0
[SoulRotationTest] press while index 0 still cooling -> result=true | index=1 | starCallCd=600 | soulLureCd=20
```

1.20.1 日志完全一致。

两条结论：

- 施放魂引天星（系列技能）后选中位落到 **index=1（引魂）**，不再是回到冷却中的起手技能 0；
- 把选中位强行设回正在冷却的 0 再按键，按键**没有失败**：它跳过冷却，施放了引魂（`soulLureCd=20`），
  `result=true`。改动前这里会 `result=false` 且什么都不放。

## 仍未做

只验证了选中位流转与冷却落点，没测 HUD 动画、技能框冷却数字、以及“全部技能都在冷却”时的按键反馈
（后者按设计仍然静默失败，冷却数字由技能框自己画）。
