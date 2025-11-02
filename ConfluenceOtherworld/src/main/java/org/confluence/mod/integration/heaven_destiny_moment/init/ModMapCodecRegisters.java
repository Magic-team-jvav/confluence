package org.confluence.mod.integration.heaven_destiny_moment.init;


import com.xiaohunao.heaven_destiny_moment.common.context.condition.ICondition;
import com.xiaohunao.heaven_destiny_moment.common.init.HDMRegistries;
import com.xiaohunao.xhn_lib.api.register.register.MapCodecFlexibleRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.heaven_destiny_moment.context.condition.EverBeneficialCondition;
import org.confluence.mod.integration.heaven_destiny_moment.context.condition.EvilBrokenCountCondition;

public class ModMapCodecRegisters {
    public static final MapCodecFlexibleRegister<ICondition> CONDITION_CODEC = MapCodecFlexibleRegister.createMapCodec(HDMRegistries.Keys.CONDITION_CODEC, Confluence.MODID).addMapCodec(
            "ever_beneficial", EverBeneficialCondition.CODEC,
            "evil_broken_count", EvilBrokenCountCondition.CODEC
    );
}
