package org.confluence.mod.common.summon;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.LibDamageTypes;

// 将召唤时保存的穿甲值随伤害传递，不读取主人当前手持武器。
public final class SummonDamageSource extends DamageSource {
    private final float armorPenetration;

    public SummonDamageSource(Player owner, float armorPenetration) {
        super(LibDamageTypes.of(owner.level(), LibDamageTypes.SUMMONER, owner).typeHolder(), owner);
        this.armorPenetration = armorPenetration;
    }

    public float armorPenetration() {return armorPenetration;}
}
