package org.confluence.mod.client.renderer.entity.bullet.effect;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;

public final class PartyConfettiVfx implements BulletImpactVfx {
    public static final PartyConfettiVfx INSTANCE = new PartyConfettiVfx();

    @Override
    public void play(Vec3 position) {
        BulletVfxManager.add(new PartyConfettiEffect(position));
    }

    private PartyConfettiVfx() {}
}
