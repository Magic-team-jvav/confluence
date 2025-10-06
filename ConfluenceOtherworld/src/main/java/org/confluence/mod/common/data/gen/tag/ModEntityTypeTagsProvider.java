package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.api.event.ShimmerEntityTransmutationEvent.addEntity;

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
                TEAnimals.BOOM_BUNNY.get(),
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
    }
}
