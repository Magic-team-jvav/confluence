package org.confluence.terraentity.data.mappeddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.confluence.terraentity.entity.boss.*;
import org.confluence.terraentity.entity.boss.hillofflesh.HillOfFlesh;
import org.confluence.terraentity.entity.boss.plantera.Plantera;
import org.confluence.terraentity.entity.boss.thetwins.TheTwins;
import org.confluence.terraentity.registries.mappeddata.MappedData;
import org.confluence.terraentity.registries.mappeddata.MappedDataType;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.registries.mappeddata.MappedKey;

import java.util.Map;
import java.util.Set;

public final class BossSkillMapDatas extends MappedData<BossSkillMapDatas.BossSkillType> {

    public static class BossSkillType extends MappedDataType<BossSkillType, BossSkillMapDatas> {
        protected BossSkillType(MapCodec<BossSkillMapDatas> codec, BossSkillMapDatas defaultData, MappedDataConstructor<BossSkillType, BossSkillMapDatas> constructor, Set<MappedKey<BossSkillType, ?>> mappedKeys, Codec<MappedKey<BossSkillType, ?>> mappedKeyCodec) {
            super(codec, defaultData, constructor, mappedKeys, mappedKeyCodec);
        }
    }

    private BossSkillMapDatas(Map<MappedKey<BossSkillType, ?>, Object> data, String comment) {
        super(data, comment);
    }

    @Override
    public BossSkillType getType() {
        return MappedDataTypes.BOSS_SKILL_MAP_DATAS.get();
    }

    static MappedDataType.Builder<BossSkillType, BossSkillMapDatas> builder = MappedDataType.builder(BossSkillType::new)
            .setComment("This file contains all default values for boss parameters. This can be customized by data packs. For lists containing 4 elements, " +
                    "they may express the values different from the difficulty levels: (classic, expert, master, ftw), equivalent to (simple, normal, hard, ftw) .");

    public static MappedKey<BossSkillType, KingSlime.SkillParams> KING_SLIME_PARAMS = builder
            .registerCodec("king_slime_params", KingSlime.SkillParams.CODEC)
            .withDefaultValue(KingSlime.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, EyeOfCthulhu.SkillParams> EYE_OF_CTHULHU_PARAMS = builder
            .registerCodec("eye_of_cthulhu_params", EyeOfCthulhu.SkillParams.CODEC)
            .withDefaultValue(EyeOfCthulhu.SkillParams::getDefaultParams)
//            .withOnReload(data->System.out.println(data.toString()))
            ;
    public static MappedKey<BossSkillType, BrainOfCthulhu.SkillParams> BRAIN_OF_CTHULHU_PARAMS = builder
            .registerCodec("brain_of_cthulhu_params", BrainOfCthulhu.SkillParams.CODEC)
            .withDefaultValue(BrainOfCthulhu.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, EaterOfWorlds.SkillParams> EATER_OF_WORLDS_PARAMS = builder
            .registerCodec("eater_of_worlds_params", EaterOfWorlds.SkillParams.CODEC)
            .withDefaultValue(EaterOfWorlds.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, Deerclops.SkillParams> DEERCLOPS_PARAMS = builder
            .registerCodec("deerclops_params", Deerclops.SkillParams.CODEC)
            .withDefaultValue(Deerclops.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, QueenBee.SkillParams> QUEEN_BEE_PARAMS = builder
            .registerCodec("queen_bee_params", QueenBee.SkillParams.CODEC)
            .withDefaultValue(QueenBee.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, Skeletron.SkillParams> SKELETRON_PARAMS = builder
            .registerCodec("skeletron_params", Skeletron.SkillParams.CODEC)
            .withDefaultValue(Skeletron.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, HillOfFlesh.SkillParams> HILL_OF_FLESH_PARAMS = builder
            .registerCodec("hill_of_flesh_params", HillOfFlesh.SkillParams.CODEC)
            .withDefaultValue(HillOfFlesh.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, TheTwins.SkillParams> THE_TWINS_PARAMS = builder
            .registerCodec("the_twins_params", TheTwins.SkillParams.CODEC)
            .withDefaultValue(TheTwins.SkillParams::getDefaultParams);

    public static MappedKey<BossSkillType, Plantera.SkillParams> PLANTERA_PARAMS = builder
            .registerCodec("plantera_params", Plantera.SkillParams.CODEC)
            .withDefaultValue(Plantera.SkillParams::getDefaultParams);


    public static BossSkillType buildType() {
        return builder.build(BossSkillMapDatas::new);
    }


}
