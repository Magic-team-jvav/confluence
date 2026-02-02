package org.confluence.terraentity.api.entity;

/**
 * 宠物接口
 */
public interface IPetMob extends ISummonMob {

    @Override
    default int getCost(){
        return 0;
    }

    @Override
    default boolean isPet(){
        return true;
    }
}
