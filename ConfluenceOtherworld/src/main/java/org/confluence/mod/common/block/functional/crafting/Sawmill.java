package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;

public class Sawmill extends HorizontalDirectionalWithHorizontalTwoPartBlock {
    public static final MapCodec<Sawmill> CODEC = simpleCodec(Sawmill::new);

    public Sawmill(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<Sawmill> codec() {
        return CODEC;
    }
}
