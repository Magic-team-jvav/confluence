package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/// 魂灯主武器：在通用灵魂武器之上提供物品自身的基础暴击率，使魂灯一系共享同一套灵魂资源与施法入口。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulLanternItem extends SoulWeaponItem {
    private static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");

    protected final float criticalChance;

    public SoulLanternItem(Item.Properties properties, ModRarity rarity, float damage, float criticalChance) {
        this(properties, rarity, damage, criticalChance, List.of());
    }

    /// 手持该魂灯时临时授予的灵魂技能，沿用通用灵魂武器的授予入口。
    public SoulLanternItem(Item.Properties properties, ModRarity rarity, float damage, float criticalChance, List<ResourceLocation> grantedSoulSkills, Trait... traits) {
        super(properties, rarity, damage, grantedSoulSkills, traits);
        this.criticalChance = criticalChance;
        addAttributeModifiers(builder -> builder.add(
                LibAttributes.getCriticalChance(),
                new AttributeModifier(BASE_CRITICAL_CHANCE_ID, criticalChance, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ));
    }

    /// 返回物品自身的基础暴击率，不含玩家装备与状态加成。
    public float getCriticalChance() {
        return criticalChance;
    }
}
