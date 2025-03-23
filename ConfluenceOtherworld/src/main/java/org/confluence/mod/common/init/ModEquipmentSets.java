package org.confluence.mod.common.init;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.equipment_set.*;

@SuppressWarnings("unused")
public final class ModEquipmentSets {
    public static final EBDeferredRegister<EquipmentSet> EQUIPMENT_SET = EBDeferredRegister.create(Confluence.MODID, EquipmentSetManager.getInstance());

    public static final EBDeferredHolder<EquipmentSet> MINING_SET = EQUIPMENT_SET.register("mining_set", MiningSet::new);
    public static final EBDeferredHolder<EquipmentSet> METEOR_SET = EQUIPMENT_SET.register("meteor_set", MeteorSet::new);
    public static final EBDeferredHolder<EquipmentSet> SHADOW_SET = EQUIPMENT_SET.register("shadow_set", ShadowSet::new);
    public static final EBDeferredHolder<EquipmentSet> CRIMSON_SET = EQUIPMENT_SET.register("crimson_set", CrimsonSet::new);
    public static final EBDeferredHolder<EquipmentSet> COLD_CRYSTAL_SET = EQUIPMENT_SET.register("cold_crystal_set", ColdCrystalSet::new);
    //public static final EBDeferredHolder<EquipmentSet> MEIM_SET = EQUIPMENT_SET.register("heim_set", HeimSet::new);
    public static final EBDeferredHolder<EquipmentSet> MOLTEN_SET = EQUIPMENT_SET.register("molten_set", MoltenSet::new);
    public static final EBDeferredHolder<EquipmentSet> GLADIATOR_SET = EQUIPMENT_SET.register("gladiator_set", GladiatorSet::new);
    public static final EBDeferredHolder<EquipmentSet> SPORE_ROOT_SET = EQUIPMENT_SET.register("spore_root_set", SporeRootSet::new);
    public static final EBDeferredHolder<EquipmentSet> BEE_SET = EQUIPMENT_SET.register("bee_set", BeeSet::new);
    public static final EBDeferredHolder<EquipmentSet> FOSSIL_SET = EQUIPMENT_SET.register("fossil_set", FossilSet::new);
    public static final EBDeferredHolder<EquipmentSet> NINJA_SET = EQUIPMENT_SET.register("ninja_set", NinjaSet::new);
    public static final EBDeferredHolder<EquipmentSet> CACTUS_SET = EQUIPMENT_SET.register("cactus_set", CactusSet::new);
    public static final EBDeferredHolder<EquipmentSet> SNOW_SET = EQUIPMENT_SET.register("snow_set", SnowSet::new);
    public static final EBDeferredHolder<EquipmentSet> NECRO_SET = EQUIPMENT_SET.register("necro_set", NecroSet::new);
    public static final EBDeferredHolder<EquipmentSet> WIZARD_SET = EQUIPMENT_SET.register("wizard_set", WizardSet::new);
    public static final EBDeferredHolder<EquipmentSet> JUNGLE_SET = EQUIPMENT_SET.register("jungle_set", JungleSet::new);
}
