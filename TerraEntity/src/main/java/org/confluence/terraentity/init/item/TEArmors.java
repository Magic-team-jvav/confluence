package org.confluence.terraentity.init.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;

public class TEArmors {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraEntity.MODID);
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, TerraEntity.MODID);


    public static final MaterialArmorSuit POSSESSED_ARMOR = ArmorSuit.createSimpleWithMaterial(ITEMS, "possessed_armor", MATERIALS,
            SoundEvents.ARMOR_EQUIP_NETHERITE, ()-> Items.AMETHYST_CLUSTER,
            new int[]{3, 6, 6, 3}, new int[]{100,200,200,100}, 10, 2.0f, 0.1f);

    public static final MaterialArmorSuit WRAITH_ARMOR = ArmorSuit.createSimpleWithMaterial(ITEMS, "wraith_armor", MATERIALS,
            SoundEvents.ARMOR_EQUIP_NETHERITE, ()-> Items.DIAMOND,
            new int[]{3, 5, 5, 3}, new int[]{300,500,500,300}, 10, 0.0f, 0.0f);


    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
