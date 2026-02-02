package org.confluence.terraentity.api.entity;

import net.minecraft.world.entity.PlayerRideableJumping;

/**
 * 坐骑扩展接口
 */
public interface IFlyRideableMob extends PlayerRideableJumping {

    /**
     * 计算客户端的跳跃进度条
     */
    float calJumpingScale(float jumpTick, float orientation);

    /**
     * 当客户端按下空格时，在服务端触发
     */
    void onLocalStartInputJump();

    /**
     * 当客户端松开空格时，在服务端触发
     */
    void onLocalStopInputJump();

}
