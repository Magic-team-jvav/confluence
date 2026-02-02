package org.confluence.terraentity.registries.mappeddata;

import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.data.mappeddata.MonsterMappedDatas;
import org.confluence.terraentity.data.mappeddata.NPCMappedDatas;
import org.confluence.terraentity.data.mappeddata.WeaponMappedDatas;
import org.confluence.terraentity.registries.TEDeferredRegisters;

import java.util.function.Supplier;

public class MappedDataTypes {

    public static final TEDeferredRegisters.MappedTypeRegister TYPES = new TEDeferredRegisters.MappedTypeRegister(TerraEntity.MODID);

    public static DeferredMappedType<BossSkillMapDatas.BossSkillType> BOSS_SKILL_MAP_DATAS = TYPES.register("boss_skill_params",
            BossSkillMapDatas::buildType);

    public static DeferredMappedType<NPCMappedDatas.NPCMappedDataType> NPC_MAP_DATAS = TYPES.register("npc_params",
            NPCMappedDatas::buildType);

    public static DeferredMappedType<MonsterMappedDatas.MonsterMappedDataType> MONSTER_MAP_DATAS = TYPES.register("monster_params",
            MonsterMappedDatas::buildType);

    public static DeferredMappedType<WeaponMappedDatas.WeaponMappedDataType> WEAPON_MAP_DATAS = TYPES.register("weapon_params",
            WeaponMappedDatas::buildType);


    public static <Y extends MappedDataType<Y, T>, T extends MappedData<Y>, V> V getData(Supplier<Y> type, MappedKey<Y, V> key) {
        return type.get().getData(key);
    }
}
