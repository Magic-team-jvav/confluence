package org.confluence.mod.common.entity.ai.goal.behavior;

import net.minecraft.world.entity.Mob;
import org.confluence.mod.api.event.RedirectBTEvent;
import org.mesdag.portlib.event.PortEventHandler;

/// 行为树根节点
public abstract class BTRoot<T extends Mob> extends BTNode {
    protected BTNode child;
    protected T mob;

    public BTRoot(T mob) {
        this.mob = mob;
    }

    /// 延迟构造行为树
    protected abstract BTNode createBehaviorTree();

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
        if (this.child == null) {
            RedirectBTEvent event = new RedirectBTEvent(mob);
            PortEventHandler.postEvent(event);
            this.child = event.getRedirectionOrDefault(this::createBehaviorTree);
        }
        child.start();
    }

    @Override
    public void tick() {
        child.tick();
    }

    @Override
    public void stop() {
        super.stop();
        child.stop();
    }

    @Override
    public BTStatus execute() {
        return child.execute();
    }

    public BTNode getChild() {
        return child;
    }
}
