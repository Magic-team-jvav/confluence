package org.confluence.mod.common.entity;

import net.minecraft.world.entity.Entity;

/**
 * 定义多部件实体参与武器命中时的三个独立身份。
 *
 * <p>伤害接收者负责执行部件自身的护甲、倍率和受击反馈；遭遇主体用于阵营、
 * 索敌和命中特效；去重身份决定一次穿透或连续碰撞能否同时命中多个部件。</p>
 */
public interface PartHitTarget {
    Entity damageRecipient();

    Entity encounterOwner();

    Entity dedupeIdentity();

    boolean acceptsDirectHit();
}
