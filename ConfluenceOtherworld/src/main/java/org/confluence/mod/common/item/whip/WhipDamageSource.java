package org.confluence.mod.common.item.whip;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.LibDamageTypes;

/** 传递给鞭子爆炸的召唤伤害源，保存本次爆炸的护甲穿透值。 */
public final class WhipDamageSource extends DamageSource {
    private final float armorPenetration;

    public WhipDamageSource(Player owner, float armorPenetration) {
        super(LibDamageTypes.of(owner.level(), LibDamageTypes.SUMMONER, owner).typeHolder(), owner);
        this.armorPenetration = armorPenetration;
    }

    public float armorPenetration() {
        return armorPenetration;
    }
}
