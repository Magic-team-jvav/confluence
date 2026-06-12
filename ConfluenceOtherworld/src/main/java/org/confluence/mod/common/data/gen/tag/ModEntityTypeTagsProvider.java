package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Confluence.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.EntityTypes.SPAWN_AT_DUNGEON).add(
                TEMonsterEntities.ANGER_BONES.get(),
                TEMonsterEntities.SHORT_BONES.get(),
                TEMonsterEntities.BIG_BONES.get(),
                TEMonsterEntities.BIG_ANGER_BONES.get(),
                TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(),
                TEMonsterEntities.BIG_HELMET_ANGER_BONES.get()
        );
        tag(ModTags.EntityTypes.LAVA_BUG_NET_ALLOWS).add(
                // todo 熔岩萤火虫
                TEAnimals.MAGMA_SNAIL.get(),
                TEAnimals.HELL_BUTTERFLY.get()
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
                TEAnimals.EXPLOSIVE_BUNNY.get(),
                TEAnimals.BUNNY.get(),
                TEAnimals.BIRD.get(),
                TEAnimals.BLUE_JAY.get(),
                TEAnimals.CARDINAL.get(),
                TEAnimals.DUCK.get(),
                TEAnimals.SQUIRREL.get(),
                TEAnimals.HELL_BUTTERFLY.get(),
                TEAnimals.MAGMA_SNAIL.get(),
                TEAnimals.WORM.get(),
                TEAnimals.DRAGONFLY.get(),
                TEAnimals.BUTTERFLY.get(),
                TEAnimals.GRASSHOPPER.get(),
                TEAnimals.SCORPION.get(),
                TEAnimals.SLUGGY.get(),
                TEAnimals.SNAIL.get(),
                TEAnimals.GLOWING_SNAIL.get(),
                TEAnimals.MAGGOT.get(),
                TEAnimals.PRISMATIC_LACEWING.get(),
                TEAnimals.FAIRY.get()
        );
        tag(ModTags.EntityTypes.SPAWN_AT_GRAVEYARD)
                .addTag(EntityTypeTags.ZOMBIES);
        //.add(TEMonsterEntities.DEMON_EYE.get()); fixme 恶魔之眼白天会飞走
        tag(ModTags.EntityTypes.DO_NOT_DROPS_EVIL_SOUL).addTag(
                Tags.EntityTypes.BOSSES
        ).add(
                TEMonsterEntities.BLUE_SLIME.get(),
                TEMonsterEntities.GREEN_SLIME.get(),
                TEMonsterEntities.PINK_SLIME.get(),
                TEMonsterEntities.BLACK_SLIME.get(),
                TEMonsterEntities.PURPLE_SLIME.get(),
                TEMonsterEntities.RED_SLIME.get(),
                TEMonsterEntities.YELLOW_SLIME.get(),
                TEMonsterEntities.JUNGLE_SLIME.get(),
                TEMonsterEntities.SPIKED_ICE_SLIME.get(),
                TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(),
                TEMonsterEntities.SPIKED_SLIME.get()
        );
        tag(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_WHITELIST).add(
                EntityType.BAT
        );
        tag(ModTags.EntityTypes.CRITTER_COMPANIONSHIP_BLACKLIST).add(
                EntityType.HOGLIN,
                TEAnimals.CRAB.get(),
                TEMonsterEntities.PIRANHA.get()
        );
        tag(ModTags.EntityTypes.ENEMY_BANNER_BLACKLIST)
                .addTag(Tags.EntityTypes.BOSSES);
        tag(ModTags.EntityTypes.GORE_EFFECT_BLACKLIST)
                .addOptionalTag(TETags.EntityTypes.SLIME);
        IntrinsicTagAppender<EntityType<?>> npcInvulnerableToPlayer = tag(ModTags.EntityTypes.NPC_INVULNERABLE_TO_PLAYER);
        for (RegistryObject, ? extends EntityType<?>>TENpcEntities.ENTITIES.getEntries()){
            npcInvulnerableToPlayer.add(npc.get());
        }
    }
}
