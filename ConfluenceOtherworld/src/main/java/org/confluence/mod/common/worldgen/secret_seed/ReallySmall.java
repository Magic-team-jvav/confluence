package org.confluence.mod.common.worldgen.secret_seed;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.common.init.TCItems;

public class ReallySmall extends SecretSeed {
    public static final ResourceLocation ID = Confluence.asResource("really_small");

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
        CompoundTag tag = LibUtils.getOrCreatePersistedData(player);
        if (!tag.getBoolean("confluence:initial_step_stool")) {
            player.addItem(TCItems.STEP_STOOL.toStack());
            tag.putBoolean("confluence:initial_step_stool", true);
        }
    }

    public static void scalePlayer(ServerPlayer player) {
        AttributeInstance instance = player.getAttribute(Attributes.SCALE);
        AttributeModifier div16 = new AttributeModifier(ID, -0.9375, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        AttributeModifier div8 = new AttributeModifier(ID, -0.875, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (instance != null) {
            instance.addOrReplacePermanentModifier(div16);
        }
        instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            instance.addOrReplacePermanentModifier(div8);
        }
        instance = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (instance != null) {
            instance.addOrReplacePermanentModifier(div8);
        }
        instance = player.getAttribute(Attributes.GRAVITY);
        if (instance != null) {
            instance.addOrReplacePermanentModifier(div8);
        }
    }
}
