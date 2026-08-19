package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
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
                MonsterEntities.GREEN_DUMPLING_SLIME.get(),
                MonsterEntities.SWAMP_SLIME.get(),
                MonsterEntities.SLIMELING.get(),
                MonsterEntities.FLESH_SLIME.get(),
                EntityType.SLIME
        );

        tag(PortTags.EntityTypes.ARTHROPOD).add(
                BossEntities.QUEEN_BEE.get(),
                MonsterEntities.HORNET.get(),
                MonsterEntities.LITTLE_HORNET.get(),
                MonsterEntities.BLOOD_CRAWLER.get(),
                MonsterEntities.DERPLING.get(),
                MonsterEntities.SAND_POACHER.get(),
                MonsterEntities.CRAWDAD.get(),
                CritterEntities.CRAB.get(),
                MonsterEntities.GIANT_SHELLY.get()
        );
        tag(ModTags.EntityTypes.FLESH_ALLIANCE).add(
                MonsterEntities.LEECH.get(),
                MonsterEntities.FLESH_SLIME.get(),
                MonsterEntities.THE_HUNGRY.get(),
                MonsterEntities.HILL_HUNGRY.get(),
                BossEntities.HILL_OF_FLESH.get(),
                BossEntities.WALL_OF_FLESH.get()
        );
        tag(PortTags.EntityTypes.AQUATIC).add(
                MonsterEntities.PIRANHA.get(),
                MonsterEntities.BLUE_JELLYFISH.get(),
                MonsterEntities.PINK_JELLYFISH.get(),
                MonsterEntities.GREEN_JELLYFISH.get(),
                MonsterEntities.ARAPAIMA.get(),
                CritterEntities.DUCK.get()
        );
        tag(PortTags.EntityTypes.ZOMBIES).add(
                MonsterEntities.FACE_MONSTER.get(),
                MonsterEntities.SPORE_ZOMBIE.get(),
                MonsterEntities.BLOOD_ZOMBIE.get(),
                MonsterEntities.MUMMY.get(),
                MonsterEntities.DARK_MUMMY.get(),
                MonsterEntities.BLOOD_MUMMY.get(),
                MonsterEntities.LIGHT_MUMMY.get(),
                MonsterEntities.GHOUL.get(),
                MonsterEntities.TAINTED_GHOUL.get(),
                MonsterEntities.DREAMER_GHOUL.get(),
                MonsterEntities.VILE_GHOUL.get(),
                MonsterEntities.HAT_SPORE_ZOMBIE.get()
        );
        tag(EntityTypeTags.SKELETONS).add(
                MonsterEntities.DECAYEDER.get(),
                MonsterEntities.SPORE_SKELETON.get(),
                MonsterEntities.CURSED_SKULL.get(),
                MonsterEntities.BASE_BONES.get(),
                MonsterEntities.BIG_BONES.get(),
                MonsterEntities.ANGER_BONES.get(),
                MonsterEntities.BIG_ANGER_BONES.get(),
                MonsterEntities.BIG_HELMET_ANGER_BONES.get(),
                MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                MonsterEntities.UNDEAD_VIKING.get()
        );
        tag(PortTags.EntityTypes.UNDEAD).add(
                MonsterEntities.SPORE_ZOMBIE.get(),
                MonsterEntities.BLOOD_ZOMBIE.get(),
                MonsterEntities.HAT_SPORE_ZOMBIE.get(),
                MonsterEntities.DECAYEDER.get(),
                MonsterEntities.SPORE_SKELETON.get(),
                MonsterEntities.CURSED_SKULL.get(),
                MonsterEntities.BASE_BONES.get(),
                MonsterEntities.BIG_BONES.get(),
                MonsterEntities.ANGER_BONES.get(),
                MonsterEntities.BIG_ANGER_BONES.get(),
                MonsterEntities.BIG_HELMET_ANGER_BONES.get(),
                MonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                MonsterEntities.MUMMY.get(),
                MonsterEntities.DARK_MUMMY.get(),
                MonsterEntities.BLOOD_MUMMY.get(),
                MonsterEntities.LIGHT_MUMMY.get(),
                MonsterEntities.GHOUL.get(),
                MonsterEntities.TAINTED_GHOUL.get(),
                MonsterEntities.DREAMER_GHOUL.get(),
                MonsterEntities.VILE_GHOUL.get(),
                MonsterEntities.UNDEAD_VIKING.get()
        );
        tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(MonsterEntities.SNOW_FLINX.get(), MonsterEntities.ICE_MIMIC.get(), MonsterEntities.UNDEAD_VIKING.get());
        tag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(
                MonsterEntities.PIRANHA.get(),
                MonsterEntities.BLUE_JELLYFISH.get(),
                MonsterEntities.PINK_JELLYFISH.get(),
                MonsterEntities.GREEN_JELLYFISH.get(),
                MonsterEntities.CRAWDAD.get(),
                MonsterEntities.ARAPAIMA.get()
        );
        tag(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add(MonsterEntities.PIRANHA.get(), MonsterEntities.ARAPAIMA.get(), MonsterEntities.CRAWDAD.get()).addTag(ModTags.EntityTypes.JELLY_FISH);
        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(MonsterEntities.SNOW_FLINX.get(), MonsterEntities.ICE_MIMIC.get(), MonsterEntities.UNDEAD_VIKING.get());
        tag(PortTags.EntityTypes.CAN_BREATHE_UNDER_WATER).add(MonsterEntities.PIRANHA.get(), MonsterEntities.ARAPAIMA.get(), MonsterEntities.CRAWDAD.get()).addTag(ModTags.EntityTypes.JELLY_FISH);
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(
                MonsterEntities.GIANT_SHELLY.get(),
                MonsterEntities.POSSESS_ARMOR.get(),
                MonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(),
                MonsterEntities.DERPLING.get(),
                MonsterEntities.SAND_POACHER.get(),
                MonsterEntities.HERPLING.get()
        );
        tag(ModTags.EntityTypes.JELLY_FISH).add(MonsterEntities.PINK_JELLYFISH.get(), MonsterEntities.GREEN_JELLYFISH.get(), MonsterEntities.BLUE_JELLYFISH.get());

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
                BossEntities.HILL_OF_FLESH.get(),
                BossEntities.DUNGEON_GUARDIAN.get(),
                BossEntities.THE_DESTROYER.get(),
                BossEntities.THE_TWINS.get(),
                BossEntities.RETINAZER.get(),
                BossEntities.SPAZMATISM.get(),
                BossEntities.SKELETRON_PRIME.get(),
                BossEntities.PLANTERA.get(),
                BossEntities.LUNATIC_CULTIST.get(),
                BossEntities.DEERCLOPS.get()
        );
    }
}
