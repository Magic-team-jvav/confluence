package org.confluence.mod.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.diff.Diff;

@Diff
public record IntVariantEntitySubPredicate(int variant) implements EntitySubPredicate {
    public static final String KEY = "confluence:int";
    public static final Type TYPE = IntVariantEntitySubPredicate::deserialize;

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (entity instanceof VariantHolder<?> holder && holder.getVariant() instanceof Integer integer) {
            return integer == variant;
        }
        return false;
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonObject json = new JsonObject();
        json.addProperty("variant", variant);
        return json;
    }

    public static IntVariantEntitySubPredicate deserialize(JsonObject json) {
        int variant = GsonHelper.getAsInt(json, "variant");
        return new IntVariantEntitySubPredicate(variant);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
