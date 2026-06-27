package org.confluence.mod.api.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

/// 受伤或再次生成时自动切换状态
public interface IStateChangeableMob {
    EntityDataAccessor<Integer> getDataStateStatus();

    /// 当受伤或生成时触发
    void changeState();

    private Entity self() {
        return (Entity) this;
    }

    default int getStage() {
        return self().getEntityData().get(getDataStateStatus());
    }

    default void setStage(int stage) {
        self().getEntityData().set(getDataStateStatus(), stage);
    }

    default void syncStatus(int status) {
        self().getEntityData().set(getDataStateStatus(), status);
    }
}
