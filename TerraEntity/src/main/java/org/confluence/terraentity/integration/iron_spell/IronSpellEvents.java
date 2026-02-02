package org.confluence.terraentity.integration.iron_spell;

import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.blood.RaiseDeadSpell;
import io.redspace.ironsspellbooks.spells.evocation.SummonVexSpell;
import io.redspace.ironsspellbooks.spells.ice.SummonPolarBearSpell;
import net.neoforged.bus.api.SubscribeEvent;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.utils.TEUtils;


public class IronSpellEvents {

    @SubscribeEvent
    public static void onSummonsAttack(SpellDamageEvent event){
        AbstractSpell spell = event.getSpellDamageSource().spell();
        if(spell instanceof SummonVexSpell || spell instanceof RaiseDeadSpell
         || spell instanceof SummonPolarBearSpell){
            event.setAmount(event.getAmount() * TEUtils.getAttributePercent(TEAttributes.SUMMON_DAMAGE, event.getEntity()));
        }
//        System.out.println(event.getOriginalAmount() + " -> " + event.getAmount());
    }
}
