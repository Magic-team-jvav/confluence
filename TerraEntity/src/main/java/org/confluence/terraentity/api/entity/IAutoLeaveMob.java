package org.confluence.terraentity.api.entity;

import org.confluence.terraentity.config.ServerConfig;

public interface IAutoLeaveMob {

    default boolean shouldLeave(){
        return ServerConfig.BOSS_LEAVE_ON_DAY.get();
    }

    void doLeave();

}
