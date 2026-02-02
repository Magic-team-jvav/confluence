package org.confluence.terraentity.integration.jade;

import org.confluence.terraentity.init.entity.TESummonEntities;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class TEJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.hideTarget(TESummonEntities.TERRAPRISMA.get()); // 泰拉棱镜不阻挡Jade

//        registration.registerEntityComponent(TerraprismaJadeProvider.INSTANCE, SummonSword.class);
    }
}
