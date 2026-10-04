package org.confluence.mod.client.entity.model;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.MartianWalker;
import org.confluence.mod.common.entity.monster.WalkerGeometry;
import software.bernie.geckolib.animation.AnimationState;

//火星走妖模型，将两边激光炮拆分
public final class MartianWalkerModel extends ExplicitGeoModel<MartianWalker> {
    public MartianWalkerModel() {
        super(Confluence.asResource("geo/entity/martian_walker.geo.json"),
                Confluence.asResource("textures/entity/martian_walker.png"),
                Confluence.asResource("animations/entity/martian_walker.animation.json"));
    }
    @Override public void setCustomAnimations(MartianWalker walker, long instanceId, AnimationState<MartianWalker> state) {
        var pose = walker.animationPose(state.getPartialTick());
        for (String name : WalkerGeometry.boneNames()) {
            var bone = getAnimationProcessor().getBone(name);
            if (bone == null) continue;
            var rotation = WalkerGeometry.rotation(name, pose);
            var position = WalkerGeometry.translation(name, pose);
            bone.setRotX(rotation.x); bone.setRotY(rotation.y); bone.setRotZ(rotation.z);
            bone.setPosX(-position.x); bone.setPosY(position.y); bone.setPosZ(position.z);
            bone.setHidden(name.equals("left_fort") && walker.weaponDestroyed(0)
                    || name.equals("right_fort") && walker.weaponDestroyed(1));
        }
    }
}
