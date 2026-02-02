package org.confluence.terraentity.data.mappeddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.entity.config.InitialArmors;
import org.confluence.terraentity.registries.mappeddata.MappedData;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.registries.mappeddata.MappedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class MonsterMappedDatas extends MappedData<MonsterMappedDatas.MonsterMappedDataType> {

    public static class MonsterMappedDataType extends MappedDataType<MonsterMappedDataType, MonsterMappedDatas> {
        protected MonsterMappedDataType(MapCodec<MonsterMappedDatas> codec, MonsterMappedDatas defaultData, MappedDataConstructor<MonsterMappedDataType, MonsterMappedDatas> constructor, Set<MappedKey<MonsterMappedDataType, ?>> mappedKeys, Codec<MappedKey<MonsterMappedDataType, ?>> mappedKeyCodec) {
            super(codec, defaultData, constructor, mappedKeys, mappedKeyCodec);
        }
    }

    private MonsterMappedDatas(Map<MappedKey<MonsterMappedDataType, ?>, Object> data, @Nullable String comment) {
        super(data, comment);
    }

    @Override
    public MonsterMappedDataType getType() {
        return MappedDataTypes.MONSTER_MAP_DATAS.get();
    }

    static MappedDataType.Builder<MonsterMappedDataType, MonsterMappedDatas> builder = MappedDataType.builder(MonsterMappedDataType::new)
            .setComment("This file contains various default parameters of monsters. ");

    public static MappedKey<MonsterMappedDataType, InitialArmors> MONSTER_ARMOR = builder.registerCodec("monster_initial_armors", InitialArmors.CODEC)
            .withDefaultValue(InitialArmors::getDefaultParams);

    public static MonsterMappedDataType buildType() {
        return builder.build(MonsterMappedDatas::new);
    }


}
