package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.BowCombatAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;

/// 腐骴远程骷髅。
///
/// <p>生成时固定持弓，并使用与 1.21 远程骷髅相同的作战周期。若命令、数据包或其他
/// 模组替换了主手武器，腐骴会改用近战；重新拿到弓后无需重建实体即可恢复远程行为。</p>
public class Decayeder extends BaseMonster {
    public Decayeder(EntityType<? extends BaseMonster> type, Level level) {
        super(type, level);
        setItemSlot(EquipmentSlot.MAINHAND, Items.BOW.getDefaultInstance());
        setLeftHanded(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
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
                        new VanillaGoalAction(new AvoidEntityGoal<>(Decayeder.this, Wolf.class, 6.0F, 1.0, 1.2)),
                        new BowCombatAction(Decayeder.this, 1.0, 40, 20, 15.0, 20, 1.6F),
                        new VanillaGoalAction(new MeleeAttackGoal(Decayeder.this, 1.2, false)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(Decayeder.this, 1.0)),
                        new VanillaGoalAction(new LookAtPlayerGoal(Decayeder.this, Player.class, 8.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(Decayeder.this)));
            }
        };
    }

    @Override
    public float getWalkTargetValue(BlockPos pos) {
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

}
