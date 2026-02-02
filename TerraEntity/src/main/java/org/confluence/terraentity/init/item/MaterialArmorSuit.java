package org.confluence.terraentity.init.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MaterialArmorSuit extends ArmorSuit{

    public Holder<ArmorMaterial> materialHolder;

    public MaterialArmorSuit(DeferredRegister.Items registry, String name, DeferredRegister<ArmorMaterial> armorMaterialRegister,
                             Supplier<ArmorMaterial> material, int[] durability){
        super(registry, name, armorMaterialRegister.register(name, material), durability);

    }

    @Override
    protected void init(DeferredRegister.Items registry, String name, Supplier<ArmorMaterial> material, int[] durability){
        materialHolder = (Holder<ArmorMaterial>) material;
        this.registerArmors(registry, name, materialHolder, durability);
    }
}
