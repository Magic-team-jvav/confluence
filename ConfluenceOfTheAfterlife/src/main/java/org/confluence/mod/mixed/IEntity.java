package org.confluence.mod.mixed;

import net.minecraft.world.phys.Vec3;

public interface IEntity {
    void confluence$entity_setCoolDown(int ticks);

    void confluence$setOriginalNoGravity(boolean bool);

    boolean confluence$isInShimmer();

    Vec3 confluence$deathMotion(Vec3... motion);

    byte HAD_SETUP = -1;
    byte HAS_GRAVITY = 0;
    byte NO_GRAVITY = 1;
}
