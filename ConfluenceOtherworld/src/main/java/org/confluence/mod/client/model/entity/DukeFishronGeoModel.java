package org.confluence.mod.client.model.entity;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.DukeFishron;
import org.joml.Vector3f;
import software.bernie.geckolib.animation.AnimationState;

public class DukeFishronGeoModel extends GeoNormalModel<DukeFishron> {
    private static final float BODY_CENTER_Y = 28.0F, BODY_CENTER_Z = -9.0F;

    public DukeFishronGeoModel() {
        super(Confluence.asResource("boss/duke_fishron"));
    }

    @Override
    public void setCustomAnimations(DukeFishron duke, long instanceId, AnimationState<DukeFishron> state) {
        super.setCustomAnimations(duke, instanceId, state);
        var root = getAnimationProcessor().getBone("all");
        if (root == null) return;
        Vector3f center = new Vector3f(-root.getPivotX(), BODY_CENTER_Y - root.getPivotY(), BODY_CENTER_Z - root.getPivotZ())
                .mul(root.getScaleX(), root.getScaleY(), root.getScaleZ())
                .rotateX(root.getRotX()).rotateY(root.getRotY()).rotateZ(root.getRotZ())
                .add(root.getPivotX(), root.getPivotY(), root.getPivotZ());
        float hitboxCenterY = duke.getBbHeight() * 8.0F / duke.getScale();
        root.setPosX(center.x);
        root.setPosY(hitboxCenterY - center.y);
        root.setPosZ(-center.z);
    }
}
