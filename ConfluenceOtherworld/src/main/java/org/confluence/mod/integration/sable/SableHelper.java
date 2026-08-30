package org.confluence.mod.integration.sable;

import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;

public class SableHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("sable");
    private static final boolean PASS_TO_ORIGINAL = true;
    private static final boolean SKIP_ORIGINAL = false;

    public static boolean doAbstractHookEntity$onHitBlock(AbstractHookEntity thiz, BlockHitResult result) {
        if (IS_LOADED && SableAbstractHookEntity.onHitBlock(thiz, result)) {
            return SKIP_ORIGINAL;
        }
        return PASS_TO_ORIGINAL;
    }

    public static void doAbstractHookEntity$tick(AbstractHookEntity thiz) {
        if (IS_LOADED) {
            SableAbstractHookEntity.tick(thiz);
        }
    }
}
