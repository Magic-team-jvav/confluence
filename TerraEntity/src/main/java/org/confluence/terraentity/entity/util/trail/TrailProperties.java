package org.confluence.terraentity.entity.util.trail;

/**
 * 拖尾属性，每种拖尾不是所有的属性都有用
 * @param size 暂时没用
 * @param widthScale 拖尾宽度，剑越长越宽
 * @param fadeWidthFactor 外边缘缩放
 * @param colorFrom 头颜色
 * @param colorTo 尾颜色
 */
public record TrailProperties(float size, float widthScale, float fadeWidthFactor, int colorFrom, int colorTo) {
}
