package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.BowCombatAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;

/// 哥布林弓箭手。
///
/// 持弓时使用与 1.21 一致的拉弓、瞄准、难度冷却和射后走位；武器被替换后自动
/// 回退到人形怪物的近战追击。两条分支共用现有行为树，不需要在装备变化时重新注册
/// 原版目标。
public class GoblinArcher extends GoblinMonster {
    public GoblinArcher(EntityType<? extends GoblinArcher> type, Level level) {
        super(type, level, Items.BOW.getDefaultInstance(), LandAnimationProfile.NONE);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new BowCombatAction(GoblinArcher.this, 1.0, 40, 20, 15.0, 20, 1.6F),
                        new VanillaGoalAction(new MeleeAttackGoal(GoblinArcher.this, 1.2, false)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(GoblinArcher.this, 1.0)),
                        new VanillaGoalAction(new LookAtPlayerGoal(GoblinArcher.this, Player.class, 8.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(GoblinArcher.this)));
            }
        };
    }
}
