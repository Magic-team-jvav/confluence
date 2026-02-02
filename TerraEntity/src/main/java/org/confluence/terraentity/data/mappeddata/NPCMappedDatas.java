package org.confluence.terraentity.data.mappeddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.entity.config.InitialWeapons;
import org.confluence.terraentity.registries.mappeddata.MappedData;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.registries.mappeddata.MappedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class NPCMappedDatas extends MappedData<NPCMappedDatas.NPCMappedDataType> {

    public static class NPCMappedDataType extends MappedDataType<NPCMappedDataType, NPCMappedDatas> {
        protected NPCMappedDataType(MapCodec<NPCMappedDatas> codec, NPCMappedDatas defaultData, MappedDataConstructor<NPCMappedDataType, NPCMappedDatas> constructor, Set<MappedKey<NPCMappedDataType, ?>> mappedKeys, Codec<MappedKey<NPCMappedDataType, ?>> mappedKeyCodec) {
            super(codec, defaultData, constructor, mappedKeys, mappedKeyCodec);
        }
    }

    private NPCMappedDatas(Map<MappedKey<NPCMappedDataType, ?>, Object> data, @Nullable String comment) {
        super(data, comment);
    }

    @Override
    public NPCMappedDataType getType() {
        return MappedDataTypes.NPC_MAP_DATAS.get();
    }

    static MappedDataType.Builder<NPCMappedDataType, NPCMappedDatas> builder = MappedDataType.builder(NPCMappedDataType::new)
            .setComment("This file contains various default parameters of npc");

    public static MappedKey<NPCMappedDataType, InitialWeapons> NPC_WEAPON = builder.registerCodec("npc_initial_weapon", InitialWeapons.CODEC)
            .withDefaultValue(InitialWeapons::getDefaultParams);

    public static NPCMappedDataType buildType() {
        return builder.build(NPCMappedDatas::new);
    }


}
