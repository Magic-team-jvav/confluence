package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.EntityTypes.SPAWN_AT_DUNGEON).add(
                MonsterEntities.ANGER_BONES.get(),
                MonsterEntities.SHORT_BONES.get(),
                MonsterEntities.BIG_BONES.get(),
                MonsterEntities.BIG_ANGER_BONES.get(),
                MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                MonsterEntities.BIG_HELMET_ANGER_BONES.get()
        );
        tag(ModTags.EntityTypes.LAVA_BUG_NET_ALLOWS).add(
                // todo 熔岩萤火虫
                CritterEntities.MAGMA_SNAIL.get(),
                CritterEntities.HELL_BUTTERFLY.get()
        );
        tag(ModTags.EntityTypes.FEALING_TRANSMUTATION).add(
                EntityType.DOLPHIN,
                EntityType.PARROT,
                EntityType.RABBIT,
                EntityType.COD,
                EntityType.SALMON,
                EntityType.TROPICAL_FISH,
                EntityType.TURTLE,
                EntityType.FROG,
                CritterEntities.EXPLOSIVE_BUNNY.get(),
                CritterEntities.BUNNY.get(),
                CritterEntities.BIRD.get(),
                CritterEntities.BLUE_JAY.get(),
                CritterEntities.CARDINAL.get(),
                CritterEntities.DUCK.get(),
                CritterEntities.SQUIRREL.get(),
                CritterEntities.HELL_BUTTERFLY.get(),
                CritterEntities.MAGMA_SNAIL.get(),
                CritterEntities.WORM.get(),
                CritterEntities.DRAGONFLY.get(),
                CritterEntities.BUTTERFLY.get(),
                CritterEntities.GRASSHOPPER.get(),
                CritterEntities.SCORPION.get(),
                CritterEntities.SLUGGY.get(),
                CritterEntities.SNAIL.get(),
                CritterEntities.GLOWING_SNAIL.get(),
                CritterEntities.MAGGOT.get(),
                CritterEntities.PRISMATIC_LACEWING.get(),
                CritterEntities.FAIRY.get()
        );
        tag(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)
                .addTag(PortTags.EntityTypes.ZOMBIES);
        //.add(MonsterEntities.DEMON_EYE.get()); fixme 恶魔之眼白天会飞走
        tag(ModTags.EntityTypes.DO_NOT_DROPS_EVIL_SOUL).addTag(
                Tags.EntityTypes.BOSSES
        ).add(
                MonsterEntities.BLUE_SLIME.get(),
                MonsterEntities.GREEN_SLIME.get(),
                MonsterEntities.PINK_SLIME.get(),
                MonsterEntities.BLACK_SLIME.get(),
                MonsterEntities.PURPLE_SLIME.get(),
                MonsterEntities.RED_SLIME.get(),
                MonsterEntities.YELLOW_SLIME.get(),
                MonsterEntities.JUNGLE_SLIME.get(),
                MonsterEntities.SPIKED_ICE_SLIME.get(),
                MonsterEntities.SPIKED_JUNGLE_SLIME.get(),
                MonsterEntities.SPIKED_SLIME.get()
        );
        tag(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_WHITELIST).add(
                EntityType.BAT
        );
        tag(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_BLACKLIST).add(
                EntityType.HOGLIN,
                CritterEntities.CRAB.get(),
                MonsterEntities.PIRANHA.get()
        );
        tag(ModTags.EntityTypes.ENEMY_BANNER_BLACKLIST)
                .addTag(Tags.EntityTypes.BOSSES);
        tag(ModTags.EntityTypes.GORE_EFFECT_BLACKLIST)
                .addTag(LibTags.EntityTypes.SLIME);
        IntrinsicTagAppender<EntityType<?>> npcInvulnerableToPlayer = tag(ModTags.EntityTypes.NPC_INVULNERABLE_TO_PLAYER);
        for (RegistryObject<? extends EntityType<?>> npc : NpcEntities.ENTITIES.getEntries()) {
            npcInvulnerableToPlayer.add(npc.get());
        }

        tag(LibTags.EntityTypes.SLIME).add(
                MonsterEntities.BLUE_SLIME.get(),
                MonsterEntities.GREEN_SLIME.get(),
                MonsterEntities.PINK_SLIME.get(),
                MonsterEntities.DUNGEON_SLIME.get(),
                MonsterEntities.CORRUPT_SLIME.get(),
                MonsterEntities.DESERT_SLIME.get(),
                MonsterEntities.JUNGLE_SLIME.get(),
                MonsterEntities.EVIL_SLIME.get(),
                MonsterEntities.ICE_SLIME.get(),
                MonsterEntities.LAVA_SLIME.get(),
                MonsterEntities.LUMINOUS_SLIME.get(),
                MonsterEntities.CRIMSLIME.get(),
                MonsterEntities.PURPLE_SLIME.get(),
                MonsterEntities.RED_SLIME.get(),
                MonsterEntities.TROPIC_SLIME.get(),
                MonsterEntities.YELLOW_SLIME.get(),
                MonsterEntities.HONEY_SLIME.get(),
                MonsterEntities.BLACK_SLIME.get(),
                MonsterEntities.GOLDEN_SLIME.get(),
                MonsterEntities.SPIKED_JUNGLE_SLIME.get(),
                MonsterEntities.SPIKED_ICE_SLIME.get(),
                MonsterEntities.SPIKED_SLIME.get(),
                EntityType.SLIME
        );

        tag(ModTags.EntityTypes.CORRUPT).add(
                MonsterEntities.EATER_OF_SOULS.get(),
                MonsterEntities.DECAYEDER.get(),
                MonsterEntities.DEVOURER.get()
        );

        tag(Tags.EntityTypes.BOSSES).add(
                BossEntities.EYE_OF_CTHULHU.get(),
                BossEntities.KING_SLIME.get(),
                BossEntities.EATER_OF_WORLDS.get(),
//                BossEntities.EATER_OF_WORLDS_SEGMENT.get(),
                BossEntities.BRAIN_OF_CTHULHU.get(),
//                BossEntities.BRAIN_FAKE.get(),
                BossEntities.QUEEN_BEE.get(),
                BossEntities.SKELETRON.get(),
                BossEntities.SKELETRON_HAND.get(),
                BossEntities.WALL_OF_FLESH.get(),
                BossEntities.HILL_OF_FLESH.get()
        );
    }
}
