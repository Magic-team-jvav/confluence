package org.confluence.mod.common.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.confluence.lib.util.consumer.Consumer3;
import org.confluence.mod.common.init.ModEnchantments;

public class ManaAffectiveEnchantment extends AbstractEnchantment {
    private final Consumer3<LivingEntity, Entity, Integer> postAttack;

    public ManaAffectiveEnchantment(int maxLevel, Consumer3<LivingEntity, Entity, Integer> postAttack) {
        super(ModEnchantments.Categories.MANA, ModEnchantments.SlotGroups.MAINHAND, maxLevel);
        this.postAttack = postAttack;
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

    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        postAttack.accept(attacker, target, level);
    }
}
