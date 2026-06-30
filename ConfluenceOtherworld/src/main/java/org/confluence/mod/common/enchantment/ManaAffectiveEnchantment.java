package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.consumer.Consumer3;
import org.confluence.mod.common.init.ModEnchantments;

public class ManaAffectiveEnchantment extends AbstractEnchantment {
    private final Consumer3<LivingEntity, Entity, Integer> affective;

    public ManaAffectiveEnchantment(int maxLevel, Consumer3<LivingEntity, Entity, Integer> affective) {
        super(ModEnchantments.Categories.MANA, LibEnchantmentUtils.SlotGroups.MAINHAND, maxLevel);
        this.affective = affective;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof ManaAffectiveEnchantment);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    public void affect(LivingEntity attacker, Entity target, int level) {
        affective.accept(attacker, target, level);
    }
}
