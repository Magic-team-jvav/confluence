# 附魂印记改为「命中/击杀给强力附魂」

## 1. 需求落地

| 项 | 旧 | 新 |
| --- | --- | --- |
| 灵魂消耗 | 3 | **2** |
| 冷却 | 15 秒 | **1 秒（20 tick）** |
| 增益时机 | **施放瞬间**挂 25 秒强力附魂 | **命中敌人**给 **3 秒**；**击杀敌人**给 **8 秒** |
| 伤害 | `ACTIVE_DAMAGE`（5） | 不变 |

## 2. 实现

### 2.1 新增 `SoulMarkSkill`

原来 `SOUL_MARK` 是直接用 `SoulProjectileSkill.builder(...).selfEffect(ENHANCED_SOUL, 25s)` 拼出来的，
增益在 `use()` 里就挂上了。现在改为专用类：

```java
public class SoulMarkSkill extends SoulProjectileSkill {
    public SoulMarkSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, builder(SoulLureProjectile.PARTICLE)
                .damage(SoulSkillParameters.ACTIVE_DAMAGE)
                .cost(SoulSkillParameters.SOUL_MARK_SOUL_COST, SoulSkillParameters.SOUL_MARK_COOLDOWN_TICKS));
    }

    @Override
    protected void modifyProjectile(SoulLureProjectile projectile) {
        projectile.setEnhancedSoulOnHit(SoulSkillParameters.SOUL_MARK_HIT_BUFF_TICKS);
    }

    @Override
    public List<BuffDuration> getBuffDurations(SoulSkillStack skillStack) {
        return List.of(new BuffDuration(ModEffects.ENHANCED_SOUL.get().getDisplayName(),
                SoulSkillParameters.SOUL_MARK_HIT_BUFF_TICKS));   // tooltip 只列命中时长，击杀时长写在说明里
    }
}
```

### 2.2 `SoulProjectileSkill` 新增弹幕定制钩子

```java
/// 生成弹幕后的定制钩子，派生类可在这里给弹幕打标记（默认不改）。
protected void modifyProjectile(SoulLureProjectile projectile) {}
```

`use()` 里先建弹幕、调钩子、再 `addFreshEntity`。这样以后任何“弹幕命中才生效”的技能都不用改实体。

### 2.3 `SoulLureProjectile` 承载标记

新增一个可序列化字段（`EnhancedSoulOnHit`，int，0 表示没有该效果）：

```java
public void setEnhancedSoulOnHit(int ticks);

private void grantEnhancedSoul(Entity victim) {
    if (enhancedSoulOnHitTicks <= 0 || !(getOwner() instanceof ServerPlayer owner)) return;
    boolean killed = victim instanceof LivingEntity living && living.getHealth() <= 0.0F;
    int ticks = killed ? SOUL_MARK_KILL_BUFF_TICKS : enhancedSoulOnHitTicks;
    owner.addEffect(new MobEffectInstance(ModEffects.ENHANCED_SOUL, ticks, 0, false, true, true));
}
```

- 在 `tick()` 的实体命中分支里，`hurt(...)` 之后立刻调用；
- 击杀判定用 **`getHealth() <= 0`** 而不是 `isAlive()`：`hurt()` 返回后实体可能还没被 `remove`，
  但血量已经归零，这个判断稳定；
- 命中 → 3 秒；同一发直接打死 → 8 秒（覆盖 3 秒，因为 `addEffect` 取更长的时长）；
- 同一发若既命中又被大招补死不算击杀（只按这一发结算），符合“这一下打死才算”。

## 3. 顺带对齐的文案

| 键 | 新文案 |
| --- | --- |
| `soul_mark.desc` | 发射灵魂弹幕，命中敌人获得 3 秒强力附魂，击杀敌人获得 8 秒。 |
| `soul_surge.desc` | 任意附魂类增益生效期间，每隔数秒自动释放一次灵魂激荡。（上一批改成“任意增益”，文案跟着改） |

## 4. 文件索引（两版同名同改）

```
common/soulcerer/skill/SoulMarkSkill.java          新增
common/soulcerer/skill/SoulProjectileSkill.java    modifyProjectile 钩子
common/soulcerer/skill/SoulSkillParameters.java    SOUL_MARK_*（消耗 2、冷却 20t、命中 60t、击杀 160t）
common/init/ModSoulSkills.java                     SOUL_MARK 改用 SoulMarkSkill（1.20.1 另加 import）
common/entity/soulcerer/SoulLureProjectile.java    命中/击杀给增益 + 序列化字段
common/data/gen/language/InterfaceLanguageSubProvider.java  两条说明
```

## 5. 仍未做

两版都没有进游戏实测：命中 3 秒 / 击杀 8 秒是否按预期切换、
1 秒冷却配上高频弹幕会不会让强力附魂几乎常驻（如果偏强，可以再调 `SOUL_MARK_HIT_BUFF_TICKS` 或冷却）。
