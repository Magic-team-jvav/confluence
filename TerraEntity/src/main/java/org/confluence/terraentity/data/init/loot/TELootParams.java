package org.confluence.terraentity.data.init.loot;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.confluence.terraentity.TerraEntity;

public class TELootParams {

    public static final LootContextParam<Integer> VARIANT = create("variant");

    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(TerraEntity.space(id));
    }

}
