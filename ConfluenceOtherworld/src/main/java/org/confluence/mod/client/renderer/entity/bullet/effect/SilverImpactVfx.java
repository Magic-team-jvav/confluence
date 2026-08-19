package org.confluence.mod.client.renderer.entity.bullet.effect;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;

public final class SilverImpactVfx implements BulletImpactVfx {
    public static final SilverImpactVfx INSTANCE = new SilverImpactVfx();

    @Override
    public void play(Vec3 position) {
        BulletVfxManager.add(new SilverCrossEffect(position));
    }

    private SilverImpactVfx() {}
}
