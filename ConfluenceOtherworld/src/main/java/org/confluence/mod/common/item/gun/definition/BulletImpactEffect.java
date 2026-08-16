package org.confluence.mod.common.item.gun.definition;

/// 客户端收到命中反馈后播放的视觉效果类型。
public enum BulletImpactEffect {
    NONE(0),
    SILVER_CROSS(1),
    PARTY_CONFETTI(2),
    CRYSTAL_IMPACT(3),
    LUMINITE_IMPACT(4),
    CHLOROPHYTE_IMPACT(5);

    private final int id;

    BulletImpactEffect(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static BulletImpactEffect byId(int id) {
        for (BulletImpactEffect effect : values()) {
            if (effect.id == id) return effect;
        }
        return NONE;
    }
}
