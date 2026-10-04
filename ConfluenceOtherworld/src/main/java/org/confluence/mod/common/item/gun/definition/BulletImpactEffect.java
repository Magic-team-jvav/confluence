package org.confluence.mod.common.item.gun.definition;

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
