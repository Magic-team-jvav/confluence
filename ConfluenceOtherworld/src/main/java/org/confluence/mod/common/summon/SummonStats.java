package org.confluence.mod.common.summon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.util.PrefixUtils;

public record SummonStats(float baseDamage, float weaponDamageMultiplier, float armorPenetration,
                          float tagDamage, float knockbackMultiplier) {
    public SummonStats(float baseDamage, float weaponDamageMultiplier) {
        this(baseDamage, weaponDamageMultiplier, 0, 0, 1);
    }
    public SummonStats {
        if (!Float.isFinite(armorPenetration) || armorPenetration < 0 || !Float.isFinite(tagDamage) || tagDamage < 0
                || !Float.isFinite(knockbackMultiplier) || knockbackMultiplier < 0)
            throw new IllegalArgumentException("Invalid summon modifier stats");
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Summon damage must be non-negative");
        }
        if (!Float.isFinite(weaponDamageMultiplier) || weaponDamageMultiplier < 0.0F) {
            throw new IllegalArgumentException("Summon weapon damage multiplier must be non-negative");
        }
    }

    public static SummonStats from(ItemStack stack, float baseDamage) {
        var component = PrefixUtils.getPrefix(stack);
        ModPrefix.Summon prefix = component == null ? null : ModPrefix.Summon.VALUES.get(component.name());
        float multiplier = (float) PrefixUtils.heldItemContribution(stack, 1.0D, LibAttributes.getSummonDamage().value());
        if (prefix != null)
            return new SummonStats(baseDamage, multiplier, prefix.armorPenetration(), prefix.tagDamage(), 1 + prefix.knockBack());
        ModPrefix.Universal universal = component == null ? null : ModPrefix.Universal.VALUES.get(component.name());
        return new SummonStats(baseDamage, multiplier, 0, 0, universal == null ? 1 : 1 + universal.knockBack());
    }

    /// 召唤伤害 = 基础伤害 × 不含手持武器贡献的召唤加成 × 召唤武器自身倍率。
    public float damage(ServerPlayer owner) {
        double ownerMultiplier = PrefixUtils.attributeWithoutHeldItem(owner, LibAttributes.getSummonDamage(), owner.getMainHandItem());
        return (float) (baseDamage * ownerMultiplier * weaponDamageMultiplier);
    }
}
