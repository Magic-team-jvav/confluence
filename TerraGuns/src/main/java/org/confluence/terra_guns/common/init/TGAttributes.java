package org.confluence.terra_guns.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_guns.TerraGuns;

public class TGAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TerraGuns.MODID);
}
