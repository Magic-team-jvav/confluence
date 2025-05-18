package org.confluence.mod.common.item.sponsor;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terraentity.init.TEAttributes;

/*
 * 20档纪念彩蛋物品：帕拉多克斯·英特拉克缇福勋章
 * 用途：饰品
 * 描述：同时玩过钢铁雄心、维多利亚、欧陆风云、十字军之王、都市天际线的证明。
 * 用途：佩戴时+10%攻击伤害、+3攻击距离、+2仆从上限、+10生命上限、+6护甲值
 * 获取：工匠作坊中1天界徽章+1喜庆弹射器Mk2+1万花筒+1黑斑+1生命果+1耀斑/星旋/星云/星尘床+1夜明块，当前版本可暂时设为隐藏物品
 * 文本：暂定
 */
public class ParadoxInteractiveMedal extends BaseCurioItem {
    public ParadoxInteractiveMedal() {
        super(builder("paradox_interactive_medal").rarity(ModRarity.MASTER)
                .attribute(Attributes.ATTACK_DAMAGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                .attribute(Attributes.ENTITY_INTERACTION_RANGE, 3, AttributeModifier.Operation.ADD_VALUE)
                .attribute(TEAttributes.MINION_CAPACITY, 2, AttributeModifier.Operation.ADD_VALUE)
                .attribute(Attributes.MAX_HEALTH, 10, AttributeModifier.Operation.ADD_VALUE)
                .attribute(Attributes.ARMOR, 6, AttributeModifier.Operation.ADD_VALUE));
    }
}
