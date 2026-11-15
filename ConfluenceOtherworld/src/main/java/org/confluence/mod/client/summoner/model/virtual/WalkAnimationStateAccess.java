package org.confluence.mod.client.summoner.model.virtual;

import net.minecraft.world.entity.WalkAnimationState;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class WalkAnimationStateAccess {
    private static final Field SPEED_OLD = findField("speedOld", 1);
    private static final Field POSITION = findField("position", 2);

    private WalkAnimationStateAccess() {
    }

    public static void apply(WalkAnimationState state, float speed, float position) {
        state.setSpeed(speed);
        set(SPEED_OLD, state, speed);
        set(POSITION, state, position);
    }

    private static Field findField(String name, int fallbackIndex) {
        try {
            Field field = WalkAnimationState.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
            Field[] floatFields = Arrays.stream(WalkAnimationState.class.getDeclaredFields())
                    .filter(field -> field.getType() == float.class)
                    .toArray(Field[]::new);
            if (fallbackIndex >= floatFields.length) {
                throw new ExceptionInInitializerError("Missing WalkAnimationState field " + name);
            }
            Field field = floatFields[fallbackIndex];
            field.setAccessible(true);
            return field;
        }
    }

    private static void set(Field field, WalkAnimationState state, float value) {
        try {
            field.setFloat(state, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to update " + field.getName(), exception);
        }
    }
}
