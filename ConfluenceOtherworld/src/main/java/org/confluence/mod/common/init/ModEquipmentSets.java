package org.confluence.mod.common.init;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.equipment_set.MiningSet;
import org.confluence.mod.common.init.item.ArmorItems;

public class ModEquipmentSets {
    public static final EBDeferredRegister<EquipmentSet> EQUIPMENT_SET = EBDeferredRegister.create(
            Confluence.MODID,
            EquipmentSetManager.getInstance()
    );

    //Mining armor
    public static final EBDeferredHolder<EquipmentSet> MINING_SET = EQUIPMENT_SET.register("mining_set", MiningSet::new);


//    public static final EBDeferredHolder<EquipmentSet> DIAMOND_SET = EQUIPMENT_SET.register("diamond_set", DiamondSet::new);
//    public static final EBDeferredHolder<EquipmentSet> GOLD_SET = EQUIPMENT_SET.register("gold_set", DiamondSet::new);
}