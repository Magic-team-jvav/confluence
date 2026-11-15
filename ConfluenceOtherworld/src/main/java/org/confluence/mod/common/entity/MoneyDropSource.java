package org.confluence.mod.common.entity;

/**
 * 由实体生成来源决定是否参与统一钱币掉落，不影响普通物品掉落。
 */
public interface MoneyDropSource {
    boolean allowsMoneyDrops();
}
