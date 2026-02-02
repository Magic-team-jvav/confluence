package org.confluence.terraentity.entity.animal;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.data.init.loot.TELootParams;
import org.confluence.terraentity.init.entity.TEAnimals;

import javax.annotation.Nullable;
import java.util.Map;

public class JewelSquirrel extends Squirrel {

    public JewelSquirrel(EntityType<? extends Squirrel> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean hitByPlayer) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        LootTable loottable = this.level().getServer().reloadableRegistries().getLootTable(resourcekey);
        LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) this.level())).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, damageSource).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());
        if (hitByPlayer && this.lastHurtByPlayer != null) {
            lootparams$builder = lootparams$builder.withParameter(TELootParams.VARIANT, this.getTEVariant()).withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }

        LootParams lootparams = lootparams$builder.create(LootContextParamSets.ENTITY);
        loottable.getRandomItems(lootparams, this.getLootTableSeed(), this::spawnAtLocation);

    }

    public static final int AMBER_ID = 0;
    public static final int GOLDEN_ID = 1;
    public static final int AMETHYST_ID = 2;
    public static final int DIAMOND_ID = 3;
    public static final int EMERALD_ID = 4;
    public static final int RUBY_ID = 5;
    public static final int SAPPHIRE_ID = 6;
    public static final int TOPAZ_ID = 7;
    static final Map<Integer, ResourceLocation> textures = new Int2ObjectOpenHashMap<>(ImmutableMap.<Integer, ResourceLocation>builder()
            .put(AMBER_ID, TerraEntity.space("textures/entity/animal/squirrel/amber_squirrel.png"))
            .put(AMETHYST_ID, TerraEntity.space("textures/entity/animal/squirrel/amethyst_squirrel.png"))
            .put(DIAMOND_ID, TerraEntity.space("textures/entity/animal/squirrel/diamond_squirrel.png"))
            .put(EMERALD_ID, TerraEntity.space("textures/entity/animal/squirrel/emerald_squirrel.png"))
            .put(GOLDEN_ID, TerraEntity.space("textures/entity/animal/squirrel/golden_squirrel.png"))
            .put(RUBY_ID, TerraEntity.space("textures/entity/animal/squirrel/ruby_squirrel.png"))
            .put(SAPPHIRE_ID, TerraEntity.space("textures/entity/animal/squirrel/sapphire_squirrel.png"))
            .put(TOPAZ_ID, TerraEntity.space("textures/entity/animal/squirrel/topaz_squirrel.png"))
            .build()
    );

    @Override
    public Map<Integer, ResourceLocation> getTexturesMap() {
        return textures;
    }

    @Nullable
    public Squirrel getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return TEAnimals.JEWEL_SQUIRREL.get().create(level);
    }
}
