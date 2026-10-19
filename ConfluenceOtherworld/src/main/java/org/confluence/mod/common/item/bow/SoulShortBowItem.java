package org.confluence.mod.common.item.bow;

import org.confluence.mod.api.item.ISoulBow;

/// 灵魂短弓：短弓手感，与灵魂弓共用「击杀额外收魂 + 灵魂高于阈值增伤」的魂师弓机制。
public class SoulShortBowItem extends ShortBowItem implements ISoulBow {
    public SoulShortBowItem(float baseDamage, int durability) {
        super(baseDamage, durability);
    }
}
