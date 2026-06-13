package org.confluence.mod.common.worldgen.secret_seed;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.AttributeInstance.PortAttributeInstanceExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.init.TCItems;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

public class ReallySmall extends SecretSeed {
    public static final ResourceLocation ID = Confluence.asResource("really_small");
    public static final UUID UUID = PortAttributeModifier.rl2uuid(ID);

    public ReallySmall(long flag, ResourceLocation id) {
        super(flag, id);
    }

    @Override
    public boolean match(String seed) {
        return "really small".equals(seed);
    }

    @Override
    public boolean isHided() {
        return true;
    }

    public static void giveStepStool(ServerPlayer player) {
        CompoundTag tag = LibEntityUtils.getOrCreatePersistedData(player);
        if (!tag.getBoolean("confluence:initial_step_stool")) {
            player.addItem(TCItems.STEP_STOOL.get().getDefaultInstance());
            tag.putBoolean("confluence:initial_step_stool", true);
        }
    }

    public static void scalePlayer(ServerPlayer player) {
        AttributeInstance instance = player.getAttribute(PortAttributesExtension.scale().value());
        AttributeModifier div16 = new AttributeModifier(UUID, ID.getPath(), -0.9375, AttributeModifier.Operation.MULTIPLY_TOTAL);
        AttributeModifier div8 = new AttributeModifier(UUID, ID.getPath(), -0.875, AttributeModifier.Operation.MULTIPLY_TOTAL);
        if (instance != null) {
            PortAttributeInstanceExtension.addOrReplacePermanentModifier(instance, div16);
        }
        instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            PortAttributeInstanceExtension.addOrReplacePermanentModifier(instance, div8);
        }
        instance = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (instance != null) {
            PortAttributeInstanceExtension.addOrReplacePermanentModifier(instance, div8);
        }
        instance = player.getAttribute(PortAttributesExtension.gravity().value());
        if (instance != null) {
            PortAttributeInstanceExtension.addOrReplacePermanentModifier(instance, div8);
        }
    }
}
