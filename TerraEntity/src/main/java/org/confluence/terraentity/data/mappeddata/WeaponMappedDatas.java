package org.confluence.terraentity.data.mappeddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.data.mappeddata.data.WhipPathManager;
import org.confluence.terraentity.registries.mappeddata.MappedData;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.registries.mappeddata.MappedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class WeaponMappedDatas extends MappedData<WeaponMappedDatas.WeaponMappedDataType> {

    public static class WeaponMappedDataType extends MappedDataType<WeaponMappedDatas.WeaponMappedDataType, WeaponMappedDatas> {
        protected WeaponMappedDataType(MapCodec<WeaponMappedDatas> codec, WeaponMappedDatas defaultData, MappedDataConstructor<WeaponMappedDatas.WeaponMappedDataType, WeaponMappedDatas> constructor, Set<MappedKey<WeaponMappedDatas.WeaponMappedDataType, ?>> mappedKeys, Codec<MappedKey<WeaponMappedDatas.WeaponMappedDataType, ?>> mappedKeyCodec) {
            super(codec, defaultData, constructor, mappedKeys, mappedKeyCodec);
        }
    }

    private WeaponMappedDatas(Map<MappedKey<WeaponMappedDatas.WeaponMappedDataType, ?>, Object> data, @Nullable String comment) {
        super(data, comment);
    }

    @Override
    public WeaponMappedDatas.WeaponMappedDataType getType() {
        return MappedDataTypes.WEAPON_MAP_DATAS.get();
    }

    static MappedDataType.Builder<WeaponMappedDatas.WeaponMappedDataType, WeaponMappedDatas> builder = MappedDataType.builder(WeaponMappedDatas.WeaponMappedDataType::new)
            .setComment("This file contains various default parameters of weapons. ");

    public static MappedKey<WeaponMappedDatas.WeaponMappedDataType, WhipPathManager> WHIP_PATHS = builder.registerCodec("whip_path_config", WhipPathManager.CODEC)
            .withDefaultValue(WhipPathManager::getDefaultParams)
            .withOnReload(WhipPathManager::onLoad);

    public static WeaponMappedDatas.WeaponMappedDataType buildType() {
        return builder.build(WeaponMappedDatas::new);
    }


}
