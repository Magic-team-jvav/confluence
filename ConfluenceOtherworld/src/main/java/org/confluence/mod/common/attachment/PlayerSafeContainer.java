package org.confluence.mod.common.attachment;

import org.confluence.lib.common.PlayerContainer;
import org.confluence.mod.common.block.functional.SafeBlock;

public class PlayerSafeContainer extends PlayerContainer<SafeBlock.Entity> {
    public PlayerSafeContainer() {
        super(6);
    }
}
