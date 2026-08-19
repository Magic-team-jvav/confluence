package org.confluence.mod.common.item.gun.definition.behavior;

public final class PartyBulletBehavior extends AbstractBulletBehavior {
    public static final PartyBulletBehavior INSTANCE = new PartyBulletBehavior();

    private PartyBulletBehavior() {
        super("tooltip.confluence.ability.party_confetti");
    }
}
