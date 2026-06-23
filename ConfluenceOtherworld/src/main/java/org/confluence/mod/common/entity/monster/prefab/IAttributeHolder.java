package org.confluence.mod.common.entity.monster.prefab;

/**
 * 使用属性修饰器的生物，用来整体调整属性和控制白天生成
 */
public interface IAttributeHolder {
    AttributeBuilder getAttributeBuilder();
}
