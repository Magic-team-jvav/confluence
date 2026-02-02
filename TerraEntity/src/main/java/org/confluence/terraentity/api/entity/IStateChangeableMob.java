package org.confluence.terraentity.api.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

/**
 * 受伤或再次生成时自动切换状态
 */
public interface IStateChangeableMob {

    EntityDataAccessor<Integer> get_DATA_STATUS_STATUS();

    /**
     * 当受伤或生成时触发
     */
    void changeState();

    private Entity getSelf() {
        return (Entity) this;
    }

    default int getStage(){
        return getSelf().getEntityData().get(get_DATA_STATUS_STATUS());
    }

    default void setStage(int stage) {
        getSelf().getEntityData().set(get_DATA_STATUS_STATUS(), stage);
    }

    default void syncStatus(int status) {
        getSelf().getEntityData().set(get_DATA_STATUS_STATUS(), status);
    }

}
