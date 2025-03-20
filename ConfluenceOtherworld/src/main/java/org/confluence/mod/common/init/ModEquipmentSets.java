package org.confluence.mod.common.init;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.equipment_set.MeteorSet;
import org.confluence.mod.common.equipment_set.MiningSet;
import org.confluence.mod.common.equipment_set.ShadowSet;

@SuppressWarnings("unused")
public final class ModEquipmentSets {
    public static final EBDeferredRegister<EquipmentSet> EQUIPMENT_SET = EBDeferredRegister.create(Confluence.MODID, EquipmentSetManager.getInstance());

    public static final EBDeferredHolder<EquipmentSet> MINING_SET = EQUIPMENT_SET.register("mining_set", MiningSet::new);
    public static final EBDeferredHolder<EquipmentSet> METEOR_SET = EQUIPMENT_SET.register("meteor_set", MeteorSet::new);
    public static final EBDeferredHolder<EquipmentSet> SHADOW_SET = EQUIPMENT_SET.register("shadow_set", ShadowSet::new);
}
