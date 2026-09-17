package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.DevelopmentSpawnPolicy;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.util.ModUtils;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class DungeonSpirit extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");

    public DungeonSpirit(EntityType<? extends DungeonSpirit> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new DirectFloatingPursuitAction(DungeonSpirit.this);
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 3, state -> state.setAndContinue(FLY)));
    }

    public static void onDeath(LivingDeathEvent event) {
        if (!DevelopmentSpawnPolicy.allowsAutomaticSpawn(MonsterEntities.DUNGEON_SPIRIT.get()))
            return;
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level) || !(victim instanceof Enemy)
                || victim instanceof DungeonSpirit || victim instanceof OwnedSummon || victim.getMaxHealth() <= 100
                || !KillBoard.INSTANCE.isDefeated(BossEntities.PLANTERA.get())
                || ModUtils.getLivingBaseMoneyDrops(victim, level) <= 0.0 || level.canSeeSky(victim.blockPosition()))
            return;
        // MC 没有背景墙层，以天然地牢的实际房间范围判定，不能只看整个结构的外包围盒。
        if (!DungeonStructure.iterateDungeon(level, victim.chunkPosition(), start -> start.getPieces().stream()
                .anyMatch(piece -> piece.getBoundingBox().isInside(victim.blockPosition()))))
            return;
        int chance = LibUtils.isAtLeastExpert(level, victim.blockPosition()) ? 9 : 13;
        if (victim.getRandom().nextInt(chance) != 0) return;
        DungeonSpirit spirit = MonsterEntities.DUNGEON_SPIRIT.get().create(level);
        if (spirit == null) return;
        spirit.moveTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), 0.0F);
        spirit.finalizeSpawn(level, level.getCurrentDifficultyAt(spirit.blockPosition()), MobSpawnType.EVENT, null, null);
        if (!level.addFreshEntity(spirit)) spirit.discard();
    }
}
