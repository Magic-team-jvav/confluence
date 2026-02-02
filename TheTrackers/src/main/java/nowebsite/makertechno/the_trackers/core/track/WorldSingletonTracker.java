package nowebsite.makertechno.the_trackers.core.track;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import nowebsite.makertechno.the_trackers.api.component.ComponentBuilder;
import nowebsite.makertechno.the_trackers.api.component.SimpleComponent;
import nowebsite.makertechno.the_trackers.api.component.StaticComponent;
import nowebsite.makertechno.the_trackers.client.gui.TGui;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;
import nowebsite.makertechno.the_trackers.client.gui.provider.BuilderResultComposer;
import nowebsite.makertechno.the_trackers.core.track.states.ControllableTrackerState;
import nowebsite.makertechno.the_trackers.core.track.states.ControllableTrackerStateEx;
import nowebsite.makertechno.the_trackers.core.track.states.TickLimitedTrackerState;
import nowebsite.makertechno.the_trackers.core.track.states.TrackerStepState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
public class WorldSingletonTracker {
    private static WorldSingletonTracker INSTANCE = null;

    public static synchronized WorldSingletonTracker getInstance() {
        if (INSTANCE == null) INSTANCE = new WorldSingletonTracker();
        return INSTANCE;
    }

    public final Map<String, List<ControllableTrackerState>> entityPointers = new HashMap<>();
    public final Map<String, List<ControllableTrackerState>> staticPointers = new HashMap<>();
    public final Map<UUID, ControllableTrackerState> questSortedEntityPointers = new HashMap<>();

    private boolean isClosed;

    public void close() {
        this.clean();
        this.isClosed = true;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    /**
     * 通过预构内容添加一个UUID指针，指向世界中的实体。
     * 注意一个UUID只能注册一种对应指针。如果您希望对一个目标注册注册多个，请访问{@link #addDynamicMultiComponent(String, String, ComponentBuilder.BuilderResult[], UUID, boolean)}
     * 或{@link #attendMultiComponent(String, String, ComponentBuilder.BuilderResult[], UUID)}
     * @return 返回注册成功的指针。
     */
    public SimpleComponent addDynamicComponent(String modID, String identifyName, ComponentBuilder.BuilderResult result, UUID target, boolean isVisible) throws IllegalArgumentException {
        synchronized (questSortedEntityPointers) {
            if (questSortedEntityPointers.containsKey(target)) throw new IllegalArgumentException("UUID named " + target + " still on tracking!");
            ControllableTrackerState state = new ControllableTrackerState(identifyName, BuilderResultComposer.compose(result), isVisible, result.autoLifecycle);
            entityPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(state);
            questSortedEntityPointers.put(target, state);
            return state;
        }
    }

    /**
     * 通过预构内容添加一个计时的UUID指针，指向世界中的实体。
     * 注意一个UUID只能注册一种对应指针，切计时的指针无法转换成混合指针。
     * @return 返回注册成功的指针。
     */
    public SimpleComponent addDynamicTickLimitedComponent(String modID, String identifyName, ComponentBuilder.BuilderResult result, UUID target, int tickForTime, boolean isVisible) throws IllegalArgumentException {
        synchronized (questSortedEntityPointers) {
            if (questSortedEntityPointers.containsKey(target)) throw new IllegalArgumentException("UUID named " + target + " still on tracking!");
            ControllableTrackerState state = new TickLimitedTrackerState(identifyName, BuilderResultComposer.compose(result), tickForTime, isVisible);
            entityPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(state);
            questSortedEntityPointers.put(target, state);
            return state;
        }
    }

    /**
     * 通过预构内容添加一组UUID指针，指向世界中的实体。
     * 因为是一次性注册，同样不允许注册已在追踪的目标。如果真的需要，建议使用{@link #attendMultiComponent(String, String, ComponentBuilder.BuilderResult[], UUID)}
     * @return 当对应UUID已存在占用指针时返回null，表示注册失败; 否则返回注册成功的指针。
     * @throws IllegalFormatException 已经存在对应的指针
     */
    public SimpleComponent addDynamicMultiComponent(String modID, String identifyName, ComponentBuilder.BuilderResult[] results, UUID target, boolean isVisible) throws IllegalArgumentException {
        synchronized (questSortedEntityPointers) {
            if (questSortedEntityPointers.containsKey(target)) throw new IllegalArgumentException("UUID named " + target + " still on tracking!");
            TRenderCursor[] components = new TRenderCursor[results.length];
            for (int i = 0; i < results.length; i++) {
                components[i] = BuilderResultComposer.compose(results[i]);
            }
            ControllableTrackerStateEx state = new ControllableTrackerStateEx(identifyName, components, isVisible, results[0].autoLifecycle);
            entityPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(state);
            questSortedEntityPointers.put(target, state);
            return state;
        }
    }

    /**
     * 通过预构内容添加一组UUID指针，指向世界中的实体。
     * 警告: 该方法会立刻停止已持有的单模式指针，另外计时指针无法重组。
     * @return 返回注册成功的指针。
     */
    public SimpleComponent attendMultiComponent(String modID, String identifyName, ComponentBuilder.BuilderResult[] results, UUID target) throws IllegalArgumentException, UnsupportedOperationException {
        @Nullable ControllableTrackerState trackerState = questSortedEntityPointers.get(target);
        switch (trackerState) {
            case null -> throw new IllegalArgumentException("UUID named " + target + " not found on tracking list!");
            case TickLimitedTrackerState ignored -> throw new UnsupportedOperationException("Tick limited component couldn't be replaced");
            case ControllableTrackerStateEx ex -> {
                TRenderCursor[] components = new TRenderCursor[results.length];
                for (int i = 0; i < results.length; i++) {
                    components[i] = BuilderResultComposer.compose(results[i]);
                }
                TrackerStepState[] newCP = Arrays.copyOf(ex.getStates(), ex.getStates().length + results.length);
                for (int i = 0; i < results.length; i++) {
                    newCP[ex.getStates().length + i] = new TrackerStepState(components[i]);
                }
                ex.setStates(newCP);
                return ex;
            }
            default -> {}
        }
        synchronized (questSortedEntityPointers) {
            TRenderCursor[] components = new TRenderCursor[results.length + 1];
            for (int i = 0; i < results.length; i++) {
                components[i] = BuilderResultComposer.compose(results[i]);
            }
            components[results.length] = trackerState.getState().getComponent();
            ControllableTrackerStateEx stateEx = new ControllableTrackerStateEx(identifyName, components, trackerState.isVisible(), results[0].autoLifecycle);
            trackerState.close();
            entityPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(stateEx);
            questSortedEntityPointers.put(target, stateEx);
            return stateEx;
        }
    }

    /**
     * 添加一个静态指针，可以通过{@link StaticComponent#posUpdater(Function)}定一个位置。
     */
    public StaticComponent addStaticPosComponent(String modID, String identifyName, ComponentBuilder.BuilderResult result, boolean isVisible) {
        ControllableTrackerState state = new ControllableTrackerState(identifyName, BuilderResultComposer.compose(result), isVisible, result.autoLifecycle);
        staticPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(state);
        return state;
    }

    /**
     * 添加一个静态计时指针，可以通过{@link StaticComponent#posUpdater(Function)}定一个位置。
     */
    public StaticComponent addStaticPosTickLimitedComponent(String modID, String identifyName, ComponentBuilder.BuilderResult result, int tickForTime, boolean isVisible) {
        ControllableTrackerState state = new TickLimitedTrackerState(identifyName, BuilderResultComposer.compose(result), tickForTime, isVisible);
        staticPointers.computeIfAbsent(modID, s -> new ArrayList<>()).add(state);
        return state;
    }


    public @Nullable List<ControllableTrackerState> getEntityTrackingComponentFromModId(String modId) {
        return entityPointers.get(modId);
    }

    public @Nullable List<ControllableTrackerState> getEntityTrackingComponentFromModId(String modId, String identifyName) {
        List<ControllableTrackerState> states = getEntityTrackingComponentFromModId(modId);
        return states == null ? null : states.stream().filter(pointer -> pointer.getIdentifyName().equals(identifyName)).toList();
    }


    public void trigger(Map<UUID, Entity> cached) {
        if (!entityPointers.isEmpty()) entityPointers.forEach((id, states) -> states.removeIf(ControllableTrackerState::isClosed));
        if (!questSortedEntityPointers.isEmpty()) {
            questSortedEntityPointers.entrySet().removeIf(entry -> entry.getValue().isClosed());
            cached.forEach((uuid, entity) -> {
                ControllableTrackerState state = questSortedEntityPointers.get(uuid);
                if (state != null) state.setPosEntity(entity);
            });
            questSortedEntityPointers.values().forEach(state -> {
                if (!state.isAlive() && state.isAutoDelete()) state.close();
            });
        }

        if (!staticPointers.isEmpty()) {
            staticPointers.forEach((id, states) -> states.removeIf(ControllableTrackerState::isClosed));

            Player player = TGui.getCameraPlayer(Minecraft.getInstance());
            if (Minecraft.getInstance().level == null || player == null) return;

            staticPointers.forEach((s, states) -> states.forEach(state -> state.setPosEntity(player)));
        }
    }

    public void clean() {
        entityPointers.clear();
        staticPointers.clear();
        questSortedEntityPointers.clear();
        INSTANCE = null;
    }
    public void render(GuiGraphics graphics, float partialTick, Player player) {
        questSortedEntityPointers.values().forEach(state -> state.render(graphics, partialTick, player));
        staticPointers.values().forEach(states -> states.forEach(state -> state.render(graphics, partialTick, player)));
    }
}
