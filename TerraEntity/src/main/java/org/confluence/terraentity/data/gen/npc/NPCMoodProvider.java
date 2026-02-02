package org.confluence.terraentity.data.gen.npc;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.gen.AbstractExistCodecProvider;
import org.confluence.terraentity.entity.npc.mood.Mood;
import org.confluence.terraentity.entity.npc.mood.MoodInfo;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NPCMoodProvider extends AbstractExistCodecProvider<Map<EntityType<?>, NPCMood.EntityMood>> {

    public NPCMoodProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void run(HolderLookup.Provider provider) {
        Map<EntityType<?>, NPCMood.EntityMood> map = new HashMap<>();
        // goblin_tinkerer
        map.put(TENpcEntities.GOBLIN_TINKERER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("goblin1"), MoodInfo.of(TENpcEntities.DYE_TRADER.get(), "mood.terra_entity.goblin_tinkerer.like.dye_trader", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("goblin2"), MoodInfo.of(TENpcEntities.MECHANIC.get(), "mood.terra_entity.goblin_tinkerer.love.mechanic", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("goblin3"), MoodInfo.of(TENpcEntities.CLOTHIER.get(), "mood.terra_entity.goblin_tinkerer.dislike.clothier", Mood.DISLIKE))
                .build());
                // 喜欢公主
                // 讨厌发型师
        // guide
        map.put(TENpcEntities.GUIDE.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("guild1"), MoodInfo.of(TENpcEntities.PAINTER.get(), "mood.terra_entity.guide.hate.painter", Mood.HATE))
                .addMoodInfo(TerraEntity.space("guild2"), MoodInfo.of(TENpcEntities.CLOTHIER.get(), "mood.terra_entity.guide.like.clothier", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("guild3"), MoodInfo.of(TENpcEntities.ZOOLOGIST.get(), "mood.terra_entity.guide.like.zoologist", Mood.LIKE))
                .build());
                // 喜欢公主
                // 反感蒸汽朋克人
        // arms_dealer
        map.put(TENpcEntities.ARMS_DEALER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("arms_dealer1"), MoodInfo.of(TENpcEntities.DEMOLITIONIST.get(), "mood.terra_entity.arms_dealer.hate.demolitionist", Mood.HATE))
                .addMoodInfo(TerraEntity.space("arms_dealer2"), MoodInfo.of(TENpcEntities.NURSE.get(), "mood.terra_entity.arms_dealer.love.nurse", Mood.LOVER))
                .build());
                // 喜欢公主，蒸汽朋克人
                // 反感高尔夫球手
        // angler
        map.put(TENpcEntities.ANGLER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("angler1"), MoodInfo.of(TENpcEntities.DEMOLITIONIST.get(), "mood.terra_entity.angler.like.demolitionist", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("angler2"), MoodInfo.of(TENpcEntities.PARTY_GIRL.get(), "mood.terra_entity.angler.like.party_girl", Mood.LIKE))
                .build());
        map.put(TENpcEntities.FEMALE_ANGLER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("female_angler1"), MoodInfo.of(TENpcEntities.DEMOLITIONIST.get(), "mood.terra_entity.female_angler.dislike.demolitionist", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("female_angler2"), MoodInfo.of(TENpcEntities.PARTY_GIRL.get(), "mood.terra_entity.female_angler.dislike.party_girl", Mood.DISLIKE))
                .build());
                // 喜欢公主，税收官
                // 讨厌酒馆老板
        // dye_trader
        map.put(TENpcEntities.DYE_TRADER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("dye_trader1"), MoodInfo.of(TENpcEntities.ARMS_DEALER.get(), "mood.terra_entity.dye_trader.like.arms_dealer", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("dye_trader2"), MoodInfo.of(TENpcEntities.PAINTER.get(), "mood.terra_entity.dye_trader.like.painter", Mood.LIKE))
                .build());
                // 反感蒸汽朋克人
                // 讨厌海盗

        // demolitionist
        map.put(TENpcEntities.DEMOLITIONIST.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("demolitionist1"), MoodInfo.of(TENpcEntities.ARMS_DEALER.get(), "mood.terra_entity.demolitionist.dislike.arms_dealer", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("demolitionist2"), MoodInfo.of(TENpcEntities.GOBLIN_TINKERER.get(), "mood.terra_entity.demolitionist.dislike.goblin_tinkerer", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("demolitionist3"), MoodInfo.of(TENpcEntities.MECHANIC.get(), "mood.terra_entity.demolitionist.like.mechanic", Mood.LIKE))
                .build());
                 // 爱酒馆老板
                 // 喜欢公主,机械师
        // painter
        map.put(TENpcEntities.PAINTER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("painter1"), MoodInfo.of(TENpcEntities.DRYAD.get(), "mood.terra_entity.painter.love.dryad", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("painter2"), MoodInfo.of(TENpcEntities.PARTY_GIRL.get(), "mood.terra_entity.painter.like.party_girl", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("painter3"), MoodInfo.of(TENpcEntities.TRUFFLE.get(), "mood.terra_entity.painter.dislike.truffle", Mood.DISLIKE))
                .build());
                // 喜欢公主
                 // 反感机械侠
        // dryad
        map.put(TENpcEntities.DRYAD.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("dryad1"), MoodInfo.of(TENpcEntities.ANGLER.get(), "mood.terra_entity.dryad.dislike.angler", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("dryad2"), MoodInfo.of(TENpcEntities.FEMALE_ANGLER.get(), "mood.terra_entity.dryad.like.female_angler", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("dryad3"), MoodInfo.of(TENpcEntities.WITCH_DOCTOR.get(), "mood.terra_entity.dryad.like.witch_doctor", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("dryad4"), MoodInfo.of(TENpcEntities.TRUFFLE.get(), "mood.terra_entity.dryad.like.truffle", Mood.LIKE))
                .build());
                // 喜欢公主
                // 反感税收官
                // 讨厌高尔夫球手
        // merchant
        map.put(TENpcEntities.MERCHANT.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("merchant1"), MoodInfo.of(TENpcEntities.NURSE.get(), "mood.terra_entity.merchant.like.nurse", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("merchant2"), MoodInfo.of(TENpcEntities.ANGLER.get(), "mood.terra_entity.merchant.hate.angler", Mood.HATE))
                .addMoodInfo(TerraEntity.space("merchant3"), MoodInfo.of(TENpcEntities.FEMALE_ANGLER.get(), "mood.terra_entity.merchant.like.female_angler", Mood.LIKE))
                .build());
                // 喜欢公主,高尔夫球手
                // 反感税收官
        // nurse
        map.put(TENpcEntities.NURSE.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("nurse1"), MoodInfo.of(TENpcEntities.ARMS_DEALER.get(), "mood.terra_entity.nurse.love.arms_dealer", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("nurse2"), MoodInfo.of(TENpcEntities.DRYAD.get(), "mood.terra_entity.nurse.dislike.dryad", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("nurse3"), MoodInfo.of(TENpcEntities.PARTY_GIRL.get(), "mood.terra_entity.nurse.dislike.party_girl", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("nurse4"), MoodInfo.of(TENpcEntities.ZOOLOGIST.get(), "mood.terra_entity.nurse.hate.zoologist", Mood.HATE))
                .build());
                // 喜欢公主,巫师
        // mechanic
        map.put(TENpcEntities.MECHANIC.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("mechanic1"), MoodInfo.of(TENpcEntities.GOBLIN_TINKERER.get(), "mood.terra_entity.mechanic.love.goblin_tinkerer", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("mechanic2"), MoodInfo.of(TENpcEntities.ARMS_DEALER.get(), "mood.terra_entity.mechanic.dislike.arms_dealer", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("mechanic3"), MoodInfo.of(TENpcEntities.CLOTHIER.get(), "mood.terra_entity.mechanic.hate.clothier", Mood.HATE))
                .build());
                // 喜欢公主,机械侠
        // witch_doctor
        map.put(TENpcEntities.WITCH_DOCTOR.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("witch_doctor1"), MoodInfo.of(TENpcEntities.DRYAD.get(), "mood.terra_entity.witch_doctor.like.dryad", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("witch_doctor2"), MoodInfo.of(TENpcEntities.GUIDE.get(), "mood.terra_entity.witch_doctor.like.guide", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("witch_doctor3"), MoodInfo.of(TENpcEntities.NURSE.get(), "mood.terra_entity.witch_doctor.dislike.nurse", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("witch_doctor4"), MoodInfo.of(TENpcEntities.TRUFFLE.get(), "mood.terra_entity.witch_doctor.hate.truffle", Mood.HATE))
                .build());
                // 喜欢公主
        // party_girl
        map.put(TENpcEntities.PARTY_GIRL.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("party_girl1"), MoodInfo.of(TENpcEntities.MERCHANT.get(), "mood.terra_entity.party_girl.dislike.merchant", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("party_girl2"), MoodInfo.of(TENpcEntities.ZOOLOGIST.get(), "mood.terra_entity.party_girl.love.zoologist", Mood.LOVER))
                .build());
                // 爱巫师
                // 讨厌税收官
        // clothier
        map.put(TENpcEntities.CLOTHIER.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("clothier1"), MoodInfo.of(TENpcEntities.TRUFFLE.get(), "mood.terra_entity.clothier.love.truffle", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("clothier2"), MoodInfo.of(TENpcEntities.NURSE.get(), "mood.terra_entity.clothier.dislike.nurse", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("clothier3"), MoodInfo.of(TENpcEntities.MECHANIC.get(), "mood.terra_entity.clothier.hate.mechanic", Mood.HATE))
                .build());
                // 喜欢公主,税收官
        // clothier
        map.put(TENpcEntities.ZOOLOGIST.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("zoologist1"), MoodInfo.of(TENpcEntities.WITCH_DOCTOR.get(), "mood.terra_entity.zoologist.love.witch_doctor", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("zoologist2"), MoodInfo.of(TENpcEntities.ANGLER.get(), "mood.terra_entity.zoologist.dislike.angler", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("zoologist3"), MoodInfo.of(TENpcEntities.FEMALE_ANGLER.get(), "mood.terra_entity.zoologist.like.female_angler", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("zoologist4"), MoodInfo.of(TENpcEntities.ARMS_DEALER.get(), "mood.terra_entity.zoologist.hate.arms_dealer", Mood.HATE))
                .build());
        // 喜欢公主,高尔夫球手
        // truffle
        map.put(TENpcEntities.TRUFFLE.get(), new NPCMood.EntityMood.Builder()
                .addMoodInfo(TerraEntity.space("truffle1"), MoodInfo.of(TENpcEntities.GUIDE.get(), "mood.terra_entity.truffle.love.guide", Mood.LOVER))
                .addMoodInfo(TerraEntity.space("truffle2"), MoodInfo.of(TENpcEntities.DYE_TRADER.get(), "mood.terra_entity.truffle.like.dye_trader", Mood.LIKE))
                .addMoodInfo(TerraEntity.space("truffle3"), MoodInfo.of(TENpcEntities.CLOTHIER.get(), "mood.terra_entity.truffle.dislike.clothier", Mood.DISLIKE))
                .addMoodInfo(TerraEntity.space("truffle4"), MoodInfo.of(TENpcEntities.WITCH_DOCTOR.get(), "mood.terra_entity.truffle.hate.witch_doctor", Mood.HATE))
                .build());
                // 喜欢公主
        gen(TerraEntity.space("npc/moods"), map);
    }

    @Override
    protected Codec<Map<EntityType<?>, NPCMood.EntityMood>> getCodec() {
        return NPCMood.Loader.CODEC;
    }

    @Override
    public String getName() {
        return "NPC Mood";
    }
}
