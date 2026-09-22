package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.leaf.CasterCycleAction;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

/// 共用法师状态机的地牢与哥布林法师。
///
/// 注册入口通过 {@link Profile} 明确声明弹幕和施法时序，本类不再反查注册 ID 猜测行为。
public class DarkCaster extends BaseCasterMonster {
    private static final CasterCycleAction.Timing NECROMANCER_TIMING = new CasterCycleAction.Timing(1, 150, 26, 17, 8, 20);
    private static final CasterCycleAction.Timing DIABOLIST_TIMING = new CasterCycleAction.Timing(1, 133, 26, 33, 8, 1);
    private static final CasterCycleAction.Timing RAGGED_CASTER_TIMING = new CasterCycleAction.Timing(13, 180, 26, 34, 8, 47);
    private static final CasterCycleAction.Timing TIM_TIMING = new CasterCycleAction.Timing(50, 217, 18, 33, 15, 83);
    private static final CasterCycleAction.Timing RUNE_WIZARD_TIMING = new CasterCycleAction.Timing(50, 217, 5, 30, 25, 67);
    private final Profile profile;

    public DarkCaster(EntityType<? extends BaseCasterMonster> type, Level level) {
        this(type, level, Profile.DARK_CASTER);
    }

    public DarkCaster(EntityType<? extends BaseCasterMonster> type, Level level, Profile profile) {
        super(type, level, profile.hurtResponse);
        this.profile = profile;
    }

    /// 各类法师共享施法时序，但必须生成各自具有独立碰撞规则的弹幕。
    @Override
    protected EntityType<HostileParticleProjectile> projectileType() {
        return switch (profile) {
            case DARK_CASTER -> ModEntities.DARK_CASTER_PROJECTILE.get();
            case GOBLIN_SORCERER, TIM -> ModEntities.CHAOS_BALL_PROJECTILE.get();
            case RUNE_WIZARD -> ModEntities.RUNE_BLAST.get();
            case NECROMANCER -> ModEntities.SHADOW_BEAM_PROJECTILE.get();
            case DIABOLIST -> ModEntities.INFERNO_BOLT_PROJECTILE.get();
            case RAGGED_CASTER -> ModEntities.LOST_SOUL_PROJECTILE.get();
        };
    }

    /// 褴褛邪教徒法师每次挥手连续射出三枚亡魂射弹；三次施法共形成九枚弹幕。
    @Override
    protected int projectilesPerVolley() {
        return profile.projectilesPerVolley;
    }

    /// 弹幕伤害按 wiki 专家值 × 26% 固定：提姆的混沌球 38、符文法师的符文爆破 42、死灵法师的暗影束 25；
    /// 其余法师（暗黑法师、哥布林术士、魔教徒、褴褛邪教徒法师）仍沿用接触伤害口径。
    @Override
    protected float projectileDamage() {
        return switch (profile) {
            case TIM -> LibUtils.isMaster(level(), blockPosition()) ? 57.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 38.0F : 19.0F;
            case RUNE_WIZARD -> LibUtils.isMaster(level(), blockPosition()) ? 63.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 42.0F : 21.0F;
            case NECROMANCER -> LibUtils.isMaster(level(), blockPosition()) ? 38.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 25.0F : 13.0F;
            default -> super.projectileDamage();
        };
    }

    /// 死灵法师每次传送后连续施放五次暗影束，其他法师保持各自原有的三次周期。
    @Override
    protected int castsPerCycle() {
        return profile.castsPerCycle;
    }

    @Override
    protected int projectileIntervalTicks() {
        return profile.projectileIntervalTicks;
    }

    /// 肉后地牢法师分别使用自己的首次传送、施法间隔与完整周期。
    @Override
    protected CasterCycleAction.Timing casterTiming() {
        return profile.timing == null ? super.casterTiming() : profile.timing;
    }

    /// 死灵法师只有一半概率因受击中断当前轮次，其他法师沿用各自确定的响应。
    @Override
    protected boolean shouldInterruptCastingAfterHurt() {
        return profile != Profile.NECROMANCER || random.nextBoolean();
    }

    /// 同一法师状态机中确实存在的攻击档案，不承载属性或外观数据。
    public enum Profile {
        DARK_CASTER(CasterCycleAction.HurtResponse.CONTINUE_CYCLE, null, 3, 1, 1),
        GOBLIN_SORCERER(CasterCycleAction.HurtResponse.CONTINUE_CYCLE, null, 3, 1, 1),
        TIM(CasterCycleAction.HurtResponse.PAUSE_THEN_TELEPORT, TIM_TIMING, 3, 1, 1),
        RUNE_WIZARD(CasterCycleAction.HurtResponse.PAUSE_THEN_TELEPORT, RUNE_WIZARD_TIMING, 6, 1, 1),
        NECROMANCER(CasterCycleAction.HurtResponse.PAUSE_THEN_TELEPORT, NECROMANCER_TIMING, 5, 1, 1),
        DIABOLIST(CasterCycleAction.HurtResponse.TELEPORT_IMMEDIATELY, DIABOLIST_TIMING, 3, 1, 1),
        RAGGED_CASTER(CasterCycleAction.HurtResponse.PAUSE_THEN_TELEPORT, RAGGED_CASTER_TIMING, 3, 3, 7);

        private final CasterCycleAction.HurtResponse hurtResponse;
        private final CasterCycleAction.Timing timing;
        private final int castsPerCycle;
        private final int projectilesPerVolley;
        private final int projectileIntervalTicks;

        Profile(CasterCycleAction.HurtResponse hurtResponse, CasterCycleAction.Timing timing,
                int castsPerCycle, int projectilesPerVolley, int projectileIntervalTicks) {
            this.hurtResponse = hurtResponse;
            this.timing = timing;
            this.castsPerCycle = castsPerCycle;
            this.projectilesPerVolley = projectilesPerVolley;
            this.projectileIntervalTicks = projectileIntervalTicks;
        }
    }


    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.TR_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

}
