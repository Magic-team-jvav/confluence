package org.confluence.mod.common.item.sword;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;

public class ZenithItem extends BaseSwordItem implements ILeftClickStateItem {

    public ZenithItem(Tier tier, ModRarity red, int rawDamage, float rawSpeed) {
        super(tier, red, rawDamage, rawSpeed);
    }

    @Override
    public void onLeftClick(Player player, ItemStack stack) {
        player.getData(SummonerAttachmentTypes.ZENITH_DATA).update(this);
    }

    @Override
    public void onLeftRelease(Player player, ItemStack stack) {
        player.getData(SummonerAttachmentTypes.ZENITH_DATA).reset();
    }

    public Zenith createZenith(Player owner) {
        Zenith zenith = new Zenith();
        zenith.setOwner(owner);
        zenith.setDamage((float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE));
        zenith.setKnockback(1);
        if (owner.getRandom().nextFloat() < 0.33) {
            zenith.renderType = Zenith.RenderType.ZENITH;
        }
        return zenith;
    }
}
