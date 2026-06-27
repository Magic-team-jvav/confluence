package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.entity.ISharedFlagControllerHolder;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.Blackboard;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.IBlackboardHolder;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.KeyType;
import org.confluence.terraentity.entity.util.SharedFlagController;

public abstract class BaseBehaviorTreeMob extends AbstractTerraBossBase implements ISharedFlagControllerHolder, IBlackboardHolder {

    protected static final EntityDataAccessor<Integer> DATA_SHARE_FLAG = SynchedEntityData.defineId(BaseBehaviorTreeMob.class, EntityDataSerializers.INT);

    protected Blackboard blackboard;
    protected final SharedFlagController sharedFlagController;
    public BaseBehaviorTreeMob(EntityType<? extends BaseBehaviorTreeMob> type, Level level) {
        super(type, level);
        this.blackboard = Blackboard.create();
        this.blackboard.put(KeyType.STAGE, 1);
        this.sharedFlagController = new SharedFlagController(this.getEntityData(), DATA_SHARE_FLAG);

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, this.createBT());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHARE_FLAG, 0);

    }

    @Override
    public SharedFlagController getSharedFlagController() {
        return this.sharedFlagController;
    }

    @Override
    public Blackboard getBlackboard() {
        return this.blackboard;
    }

    protected abstract BTRoot<? extends Mob> createBT();

}
