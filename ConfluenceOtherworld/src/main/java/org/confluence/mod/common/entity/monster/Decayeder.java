package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.BowCombatAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.MonsterEntities;

/// 腐骴远程骷髅。
///
/// 生成时固定持弓，并使用远程骷髅的作战周期。若命令、数据包或其他
/// 模组替换了主手武器，腐骴会改用近战；重新拿到弓后无需重建实体即可恢复远程行为。
public class Decayeder extends BaseMonster {
    public Decayeder(EntityType<? extends BaseMonster> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, Items.BOW.getDefaultInstance());
        setLeftHanded(false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new BowCombatAction(Decayeder.this, 1.0, 40, 20, 15.0, 20, 1.6F),
                        new VanillaGoalAction(new MeleeAttackGoal(Decayeder.this, 1.2, false)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(Decayeder.this, 1.0)),
                        new VanillaGoalAction(new LookAtPlayerGoal(Decayeder.this, Player.class, 8.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(Decayeder.this)));
            }
        };
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.DECAYEDER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DECAYEDER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DECAYEDER_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos position, BlockState state) {
        playSound(ModSoundEvents.DECAYEDER_STEP.get(), 0.15F, 1.0F);
    }

    public void onDamageDealt(ServerLevel serverLevel, LivingEntity victim, DamageSource damageSource) {
        if (!victim.hasEffect(ModEffects.DEMONIC_THOUGHTS)) {
            victim.addEffect(new MobEffectInstance(ModEffects.DEMONIC_THOUGHTS, 200), this);
        } else {
            victim.removeEffect(ModEffects.DEMONIC_THOUGHTS);
            victim.hurt(damageSource, 6.0F);
            EaterOfSouls eater = MonsterEntities.EATER_OF_SOULS.get().create(serverLevel);
            if (eater != null) {
                eater.setPos(victim.getEyePosition());
                eater.setTarget(victim);
                serverLevel.addFreshEntity(eater);
            }
            victim.removeEffect(ModEffects.DEMONIC_THOUGHTS);
        }
    }
}
