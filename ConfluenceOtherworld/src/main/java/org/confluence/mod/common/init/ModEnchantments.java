package org.confluence.mod.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.util.LibEnchantmentUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.enchantment.*;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.common.PortTags;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, Confluence.MODID);

    public static final RegistryObject<Enchantment> MANA_REGENERATION = ENCHANTMENTS.register("mana_regeneration", () -> new ManaIOEnchantment(Categories.ARMOR_N_MANA, LibEnchantmentUtils.SlotGroups.ARMOR_N_MAINHAND, 3));
    public static final RegistryObject<Enchantment> EFFICIENT_MAGIC = ENCHANTMENTS.register("efficient_magic", () -> new ManaIOEnchantment(Categories.MANA, LibEnchantmentUtils.SlotGroups.MAINHAND, 1));
    public static final RegistryObject<Enchantment> MANA_MENDING = ENCHANTMENTS.register("mana_mending", ManaMendingEnchantment::new);
    public static final RegistryObject<Enchantment> CELESTIAL_ABSORPTION = ENCHANTMENTS.register("celestial_absorption", () -> new ManaAffectiveEnchantment(2, (attacker, victim, level) -> {
        int count = LibMathUtils.multiplyInt(1, 0.1F * level, attacker.getRandom());
        if (count < 1) return;
        ItemEntity itemEntity = new ItemEntity(attacker.level(), victim.getX(), victim.getEyeY(), victim.getZ(), ModItems.STAR.toStack(count));
        itemEntity.setNoPickUpDelay();
        attacker.level().addFreshEntity(itemEntity);
    }));
    public static final RegistryObject<Enchantment> SOOTHED_MANA = ENCHANTMENTS.register("soothed_mana", () -> new ManaAffectiveEnchantment(2, (attacker, victim, level) -> {}));
    public static final RegistryObject<Enchantment> ARCANE_PROTECTION = ENCHANTMENTS.register("arcane_protection", ArcaneProtectionEnchantment::new);
    public static final RegistryObject<Enchantment> SPELL_DESPERATION = ENCHANTMENTS.register("spell_desperation", ManaAttackEnchantment::new);
    public static final RegistryObject<Enchantment> MYSTIC_SURGE = ENCHANTMENTS.register("mystic_surge", ManaAttackEnchantment::new);

    @SuppressWarnings("deprecation")
    public static class Categories {
        public static final EnchantmentCategory ARMOR_N_MANA = EnchantmentCategory.create("CONFLUENCE_ARMOR_N_MANA", item -> {
            Holder<Item> holder = item.builtInRegistryHolder();
            return holder.is(PortTags.Items.ARMORS) || holder.is(ModTags.Items.MANA_WEAPON);
        });
        public static final EnchantmentCategory MANA = EnchantmentCategory.create("CONFLUENCE_MANA", item -> item.builtInRegistryHolder().is(ModTags.Items.MANA_WEAPON));
    }
}
