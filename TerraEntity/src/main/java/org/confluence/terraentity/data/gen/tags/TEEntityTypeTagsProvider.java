package org.confluence.terraentity.data.gen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TEEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public TEEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, TerraEntity.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Stream.of(TETags.EntityTypes.SLIME, EntityTypeTags.NON_CONTROLLING_RIDER)
                .forEach(type -> tag(type).add(
                        TEMonsterEntities.BLUE_SLIME.get(),
                        TEMonsterEntities.GREEN_SLIME.get(),
                        TEMonsterEntities.PINK_SLIME.get(),
                        TEMonsterEntities.DUNGEON_SLIME.get(),
                        TEMonsterEntities.CORRUPT_SLIME.get(),
                        TEMonsterEntities.DESERT_SLIME.get(),
                        TEMonsterEntities.JUNGLE_SLIME.get(),
                        TEMonsterEntities.EVIL_SLIME.get(),
                        TEMonsterEntities.ICE_SLIME.get(),
                        TEMonsterEntities.LAVA_SLIME.get(),
                        TEMonsterEntities.LUMINOUS_SLIME.get(),
                        TEMonsterEntities.CRIMSLIME.get(),
                        TEMonsterEntities.PURPLE_SLIME.get(),
                        TEMonsterEntities.RED_SLIME.get(),
                        TEMonsterEntities.TROPIC_SLIME.get(),
                        TEMonsterEntities.YELLOW_SLIME.get(),
                        TEMonsterEntities.HONEY_SLIME.get(),
                        TEMonsterEntities.BLACK_SLIME.get(),
                        TEMonsterEntities.GOLDEN_SLIME.get(),
                        TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(),
                        TEMonsterEntities.SPIKED_ICE_SLIME.get(),
                        TEMonsterEntities.SPIKED_SLIME.get(),
                        EntityType.SLIME)
                );
        tag(TETags.EntityTypes.CORRUPT).add(
                TEMonsterEntities.EATER_OF_SOULS.get(),
                TEMonsterEntities.DECAYEDER.get(),
                TEMonsterEntities.DEVOURER.get()
        );


        EntityType<?>[] bosses = {
                TEBossEntities.EYE_OF_CTHULHU.get(),
                TEBossEntities.KING_SLIME.get(),
                TEBossEntities.EATER_OF_WORLDS.get(),
                TEBossEntities.EATER_OF_WORLDS_SEGMENT.get(),
                TEBossEntities.BRAIN_OF_CTHULHU.get(),
                TEBossEntities.BRAIN_FAKE.get(),
                TEBossEntities.QUEEN_BEE.get(),
                TEBossEntities.SKELETRON.get(),
                TEBossEntities.SKELETRON_HAND.get(),
                TEBossEntities.WALL_OF_FLESH.get(),
                TEBossEntities.HILL_OF_FLESH.get()
        };
        tag(Tags.EntityTypes.BOSSES).add(bosses);
        tag(TagKey.create(Registries.ENTITY_TYPE, TerraEntity.fromSpaceAndPath("ars_nouveau", "jar_blacklist"))).add(bosses);
        tag(EntityTypeTags.ARTHROPOD).add(
                TERideableEntities.RIDEABLE_BEE.get(),
                TESummonEntities.SUMMON_HORNET.get(),
                TEBossEntities.QUEEN_BEE.get(),
                TEMonsterEntities.HORNET.get(),
                TEMonsterEntities.LITTLE_HORNET.get(),
                TEMonsterEntities.BLOOD_CRAWLER.get(),
                TEMonsterEntities.DERPLING.get(),
                TEMonsterEntities.SAND_POACHER.get(),
                TEMonsterEntities.CRAWDAD.get(),
                TEAnimals.CRAB.get(),
                TEMonsterEntities.GIANT_SHELLY.get()
        );
        tag(TETags.EntityTypes.FLESH_ALLIANCE).add(
                TEMonsterEntities.LEECH.get(),
                TEMonsterEntities.FLESH_SLIME.get(),
                TEMonsterEntities.THE_HUNGRY.get(),
                TEMonsterEntities.HILL_HUNGRY.get(),
                TEBossEntities.HILL_OF_FLESH.get(),
                TEBossEntities.WALL_OF_FLESH.get()
        );
        tag(EntityTypeTags.AQUATIC).add(
                TEMonsterEntities.PIRANHA.get(),
                TEMonsterEntities.BLUE_JELLYFISH.get(),
                TEMonsterEntities.PINK_JELLYFISH.get(),
                TEMonsterEntities.GREEN_JELLYFISH.get(),
                TEMonsterEntities.ARAPAIMA.get(),
                TEAnimals.DUCK.get()
        );
        tag(EntityTypeTags.ZOMBIES).add(
                TEMonsterEntities.FACE_MONSTER.get(),
                TEMonsterEntities.SPORE_ZOMBIE.get(),
                TEMonsterEntities.BLOOD_ZOMBIE.get(),
                TEMonsterEntities.MUMMY.get(),
                TEMonsterEntities.DARK_MUMMY.get(),
                TEMonsterEntities.BLOOD_MUMMY.get(),
                TEMonsterEntities.LIGHT_MUMMY.get(),
                TEMonsterEntities.GHOUL.get(),
                TEMonsterEntities.TAINTED_GHOUL.get(),
                TEMonsterEntities.DREAMER_GHOUL.get(),
                TEMonsterEntities.VILE_GHOUL.get(),
                TEMonsterEntities.HAT_SPORE_ZOMBIE.get()
        );
        tag(EntityTypeTags.SKELETONS).add(
                TEMonsterEntities.DECAYEDER.get(),
                TEMonsterEntities.SPORE_SKELETON.get(),
                TEMonsterEntities.CURSED_SKULL.get(),
                TEMonsterEntities.BASE_BONES.get(),
                TEMonsterEntities.BIG_BONES.get(),
                TEMonsterEntities.ANGER_BONES.get(),
                TEMonsterEntities.BIG_ANGER_BONES.get(),
                TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(),
                TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                TEMonsterEntities.UNDEAD_VIKING.get()
        );
        tag(EntityTypeTags.UNDEAD).add(
                TEMonsterEntities.SPORE_ZOMBIE.get(),
                TEMonsterEntities.BLOOD_ZOMBIE.get(),
                TEMonsterEntities.HAT_SPORE_ZOMBIE.get(),
                TEMonsterEntities.DECAYEDER.get(),
                TEMonsterEntities.SPORE_SKELETON.get(),
                TEMonsterEntities.CURSED_SKULL.get(),
                TEMonsterEntities.BASE_BONES.get(),
                TEMonsterEntities.BIG_BONES.get(),
                TEMonsterEntities.ANGER_BONES.get(),
                TEMonsterEntities.BIG_ANGER_BONES.get(),
                TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(),
                TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                TEMonsterEntities.MUMMY.get(),
                TEMonsterEntities.DARK_MUMMY.get(),
                TEMonsterEntities.BLOOD_MUMMY.get(),
                TEMonsterEntities.LIGHT_MUMMY.get(),
                TEMonsterEntities.GHOUL.get(),
                TEMonsterEntities.TAINTED_GHOUL.get(),
                TEMonsterEntities.DREAMER_GHOUL.get(),
                TEMonsterEntities.VILE_GHOUL.get(),
                TEMonsterEntities.UNDEAD_VIKING.get()
        );
        tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(
                TEMonsterEntities.SNOW_FLINX.get(),
                TEMonsterEntities.ICE_MIMIC.get(),
                TEMonsterEntities.UNDEAD_VIKING.get()
        );
        tag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(
                TEMonsterEntities.PIRANHA.get(),
                TEMonsterEntities.BLUE_JELLYFISH.get(),
                TEMonsterEntities.PINK_JELLYFISH.get(),
                TEMonsterEntities.GREEN_JELLYFISH.get(),
                TEMonsterEntities.CRAWDAD.get(),
                TEMonsterEntities.ARAPAIMA.get()
        );
        tag(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add(
                TEMonsterEntities.PIRANHA.get(),
                TEMonsterEntities.BLUE_JELLYFISH.get(),
                TEMonsterEntities.PINK_JELLYFISH.get(),
                TEMonsterEntities.ARAPAIMA.get(),
                TEMonsterEntities.CRAWDAD.get(),
                TEMonsterEntities.GREEN_JELLYFISH.get()
        );
        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(
                TEMonsterEntities.SNOW_FLINX.get(),
                TEMonsterEntities.ICE_MIMIC.get(),
                TEMonsterEntities.UNDEAD_VIKING.get()
        );
        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(
                TEMonsterEntities.PIRANHA.get(),
                TEMonsterEntities.BLUE_JELLYFISH.get(),
                TEMonsterEntities.PINK_JELLYFISH.get(),
                TEMonsterEntities.ARAPAIMA.get(),
                TEMonsterEntities.CRAWDAD.get(),
                TEMonsterEntities.GREEN_JELLYFISH.get()
        );
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(
                TEMonsterEntities.GIANT_SHELLY.get(),
                TEMonsterEntities.POSSESS_ARMOR.get(),
                TEMonsterEntities.POSSESS_ARMOR_VOID_VESSEL.get(),
                TEMonsterEntities.DERPLING.get(),
                TEMonsterEntities.SAND_POACHER.get(),
                TEMonsterEntities.HERPLING.get()
                );

    }
}
