package org.confluence.mod.common.entity.monster;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

/// 使用通用陆行行为的人形怪物。
///
/// 哥布林和装甲幻影魔在 1.21 中共享人形怪继承能力：固定十刻挥臂，
/// 受伤反击之外还会攻击铁傀儡与陆地幼龟。这里仅集中真正属于整个人形族的行为；
/// 哥布林独有的浮水规则由 {@link GoblinMonster} 单独提供，普通人形怪不会被连带修改。
///
/// 默认主手物品属于实体自身的稳定配置。注册新的人形变种时只需传入物品和
/// 音效档案，不需要再在注册事件或生成回调中重复设置装备。
public class HumanoidWarriorMonster extends BaseWarriorMonster {
    public HumanoidWarriorMonster(EntityType<? extends HumanoidWarriorMonster> type, Level level, ItemStack defaultMainHand) {
        this(type, level, defaultMainHand, LandSoundProfile.ROUTINE);
    }

    public HumanoidWarriorMonster(EntityType<? extends HumanoidWarriorMonster> type, Level level, ItemStack defaultMainHand, LandSoundProfile soundProfile) {
        this(type, level, defaultMainHand, soundProfile, LandAnimationProfile.WALK_IDLE);
    }

    /// 创建由指定客户端动画方案驱动的人形怪物。
    ///
    /// 使用原版人形桥接模型的实体传入 {@link LandAnimationProfile#NONE}；仍使用自身
    /// GeckoLib 动画资源的哥布林继续沿用 {@link LandAnimationProfile#WALK_IDLE}。
    public HumanoidWarriorMonster(EntityType<? extends HumanoidWarriorMonster> type, Level level, ItemStack defaultMainHand, LandSoundProfile soundProfile, LandAnimationProfile animationProfile) {
        super(type, level, 0.0, animationProfile, soundProfile);
        ItemStack equipment = defaultMainHand.copy();
        if (!equipment.isEmpty()) {
            setItemSlot(EquipmentSlot.MAINHAND, equipment);
        }
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
                        new VanillaGoalAction(new MeleeAttackGoal(HumanoidWarriorMonster.this, 1.2, false)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(HumanoidWarriorMonster.this, 1.0)),
                        new VanillaGoalAction(new LookAtPlayerGoal(HumanoidWarriorMonster.this, Player.class, 8.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(HumanoidWarriorMonster.this)));
            }
        };
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable net.minecraft.nbt.CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        setCanPickUpLoot(level.getRandom().nextFloat() < 0.55F * difficulty.getSpecialMultiplier());
        LocalDate date = LocalDate.now();
        if (getItemBySlot(EquipmentSlot.HEAD).isEmpty() && date.getMonthValue() == 10 && date.getDayOfMonth() == 31 && level.getRandom().nextFloat() < 0.25F) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(level.getRandom().nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
        }
        return data;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public int getCurrentSwingDuration() {
        return 10;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        swing(InteractionHand.MAIN_HAND, true);
        return super.doHurtTarget(target);
    }
}
