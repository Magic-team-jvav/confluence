package org.confluence.mod.common.init;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.equipment_set.*;

@SuppressWarnings("unused")
public final class ModEquipmentSets {
    public static final EBDeferredRegister<EquipmentSet> SETS = EBDeferredRegister.create(Confluence.MODID, EquipmentSetManager.getInstance());

    public static final EBDeferredHolder<EquipmentSet> MINING_SET = SETS.register("mining_set", MiningSet::new);
    public static final EBDeferredHolder<EquipmentSet> METEOR_SET = SETS.register("meteor_set", MeteorSet::new);
    public static final EBDeferredHolder<EquipmentSet> SHADOW_SET = SETS.register("shadow_set", ShadowSet::new);
    public static final EBDeferredHolder<EquipmentSet> CRIMSON_SET = SETS.register("crimson_set", CrimsonSet::new);
    public static final EBDeferredHolder<EquipmentSet> COLD_CRYSTAL_SET = SETS.register("cold_crystal_set", ColdCrystalSet::new);
    public static final EBDeferredHolder<EquipmentSet> MEIM_SET = SETS.register("heim_set", HeimSet::new);
    public static final EBDeferredHolder<EquipmentSet> MOLTEN_SET = SETS.register("molten_set", MoltenSet::new);
    public static final EBDeferredHolder<EquipmentSet> GLADIATOR_SET = SETS.register("gladiator_set", GladiatorSet::new);
    public static final EBDeferredHolder<EquipmentSet> SPORE_ROOT_SET = SETS.register("spore_root_set", SporeRootSet::new);
    public static final EBDeferredHolder<EquipmentSet> BEE_SET = SETS.register("bee_set", BeeSet::new);
    public static final EBDeferredHolder<EquipmentSet> FOSSIL_SET = SETS.register("fossil_set", FossilSet::new);
    public static final EBDeferredHolder<EquipmentSet> NINJA_SET = SETS.register("ninja_set", NinjaSet::new);
    public static final EBDeferredHolder<EquipmentSet> CACTUS_SET = SETS.register("cactus_set", CactusSet::new);
    public static final EBDeferredHolder<EquipmentSet> SNOW_SET = SETS.register("snow_set", SnowSet::new);
    public static final EBDeferredHolder<EquipmentSet> NECRO_SET = SETS.register("necro_set", NecroSet::new);
    public static final EBDeferredHolder<EquipmentSet> SPIDER_SET = SETS.register("spider_set", SpiderSet::new);
    public static final EBDeferredHolder<EquipmentSet> WIZARD_SET = SETS.register("wizard_set", WizardSet::new);
    public static final EBDeferredHolder<EquipmentSet> JUNGLE_SET = SETS.register("jungle_set", JungleSet::new);
    public static final EBDeferredHolder<EquipmentSet> COBALT_SET = SETS.register("cobalt_set", CobaltSet::new);
    public static final EBDeferredHolder<EquipmentSet> PALLADIUM_SET = SETS.register("palladium_set", PalladiumSet::new);
    public static final EBDeferredHolder<EquipmentSet> MYTHRIL_SET = SETS.register("mythril_set", MythrilSet::new);
    public static final EBDeferredHolder<EquipmentSet> ORICHALCUM_SET = SETS.register("orichalcum_set", OrichalcumSet::new);
    public static final EBDeferredHolder<EquipmentSet> ADAMANTITE_SET = SETS.register("adamantite_set", AdamantiteSet::new);
    public static final EBDeferredHolder<EquipmentSet> TITANIUM_SET = SETS.register("titanium_set", TitaniumSet::new);
    public static final EBDeferredHolder<EquipmentSet> CRYSTAL_ASSASSIN_SET = SETS.register("crystal_assassin_set", CrystalAssassinSet::new);
    public static final EBDeferredHolder<EquipmentSet> HALLOWED_SET = SETS.register("hallowed_set", HallowedSet::new);
    public static final EBDeferredHolder<EquipmentSet> PUMPKIN_SET = SETS.register("pumpkin_set", PumpkinSet::new);
    public static final EBDeferredHolder<EquipmentSet> ASH_SET = SETS.register("ash_set", AshSet::new);
}
