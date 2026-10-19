package org.confluence.mod.common.item.bow;

import net.minecraft.world.item.Item;
import org.confluence.mod.api.item.ISoulBow;

/// 灵魂弓：满蓄力三倍伤害的常规弓体，与灵魂短弓共用「击杀额外收魂 + 灵魂高于阈值增伤」的魂师弓机制。
public class SoulBowItem extends BaseTerraBowItem implements ISoulBow {
    public SoulBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties().durability(durability));
    }
}
