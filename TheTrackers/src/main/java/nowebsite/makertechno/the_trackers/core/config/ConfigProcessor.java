package nowebsite.makertechno.the_trackers.core.config;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TDir3BodyCursor;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TDirectProjCursor;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRelativeCursor;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;
import nowebsite.makertechno.the_trackers.client.gui.components.Icon;
import nowebsite.makertechno.the_trackers.client.gui.components.BasicComponentFactory;
import nowebsite.makertechno.the_trackers.client.gui.components.BaseComponent;
import nowebsite.makertechno.the_trackers.client.gui.provider.TextureCache;
import nowebsite.makertechno.the_trackers.core.event.TModClient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ConfigProcessor {

    private static final Pair<EntityType<?>, Supplier<? extends TRenderCursor>> OF_EMPTY =new Pair<>(null, TRenderCursor::ofNull);

    public static boolean isValidEntityBindCRCursor(Object o) {
        /*   entityType|pointer|entityIcon(|optionalPatterns)   */

        if (!(o instanceof String s)) return false;
        String[] split = s.split("\\|");
        if (split.length < 3 || split.length > 4) return false;

        Optional<EntityType<?>> typeOpt = EntityType.byString(split[0]);
        if (typeOpt.isEmpty()) return false;

        if (TModClient.isLoaded && !split[1].equals("none") && TextureCache.getIcon(split[1]).equals(Icon.NONE)) return false;
        if (TModClient.isLoaded && !split[2].equals("none") && TextureCache.getIcon(split[2]).equals(Icon.NONE)) return false;

        if (split.length == 4) {
            String[] patterns = split[3].split("&");
            if (patterns.length != 2) return false;
            return BasicComponentFactory.hasElementPattern(patterns[0]) && BasicComponentFactory.hasElementPattern(patterns[1]);
        }

        return true;
    }

    @NotNull
    public static Set<Pair<EntityType<?>, Supplier<? extends TRenderCursor>>> collectCREntityBindCursor(@NotNull List<? extends String> list) {
        return list.stream()
                .map(s -> {
                    String[] split = s.split("\\|");
                    if (split.length < 3 || split.length > 4) return OF_EMPTY;

                    Optional<EntityType<?>> typeOpt = EntityType.byString(split[0]);
                    if (typeOpt.isEmpty()) return OF_EMPTY;
                    EntityType<?> entityType = typeOpt.get();

                    Icon pointerIcon = TextureCache.getIcon(split[1]);
                    if (!split[1].equals("none") && pointerIcon.equals(Icon.NONE)) return OF_EMPTY;

                    Icon icon = TextureCache.getIcon(split[2]);
                    if (!split[2].equals("none") && icon.equals(Icon.NONE)) return OF_EMPTY;

                    Supplier<? extends TRenderCursor> supplier;

                    if (split.length == 4) {
                        String[] patterns = split[3].split("&");
                        if (patterns.length == 2 &&
                                BasicComponentFactory.hasElementPattern(patterns[0]) &&
                                BasicComponentFactory.hasElementPattern(patterns[1])) {
                            supplier = () -> new TRelativeCursor(
                                    BasicComponentFactory.getElementComponent(pointerIcon, patterns[0]).get(),
                                    BasicComponentFactory.getElementComponent(icon, patterns[1]).get()
                            );
                            return new Pair<EntityType<?>, Supplier<? extends TRenderCursor>>(entityType, supplier);
                        }
                    }

                    supplier = () -> new TRelativeCursor(
                            BasicComponentFactory.getDefault(pointerIcon).get(),
                            BasicComponentFactory.getDefault(icon).get()
                    );
                    return new Pair<EntityType<?>, Supplier<? extends TRenderCursor>>(entityType, supplier);
                })
                .filter(p -> p != OF_EMPTY)
                .collect(Collectors.toSet());
    }

    public static boolean isValidCREntityBindDTCursor(Object o) {
        /*    entityType|type:icon(|optionalPattern)    */
        /*    optionalPattern = opt:type    */

        if (!(o instanceof String s)) return false;
        String[] split = s.split("\\|");
        if (split.length < 2 || split.length > 3) return false;

        Optional<EntityType<?>> typeOpt = EntityType.byString(split[0]);
        if (typeOpt.isEmpty()) return false;

        String[] style = split[1].split(":");
        if (style.length != 2) return false;

        if (TModClient.isLoaded) {
            if (!style[1].equals("none") && TextureCache.getIcon(style[1]).equals(Icon.NONE))
                if (TextureCache.getIcon(style[1]).equals(Icon.NONE)) return false;
        }

        if (!(style[0].equals("normal") || style[0].equals("3body"))) return false;

        if (split.length == 3) return BasicComponentFactory.hasElementPattern(split[2]);

        return true;
    }
    @NotNull
    public static Set<Pair<EntityType<?>, Supplier<? extends TRenderCursor>>> collectDTEntityBindCursor(@NotNull List<? extends String> list) {
        return list.stream()
                .map(s -> {
                    String[] split = s.split("\\|");
                    if (split.length < 2 || split.length > 3) return OF_EMPTY;

                    Optional<EntityType<?>> typeOpt = EntityType.byString(split[0]);
                    if (typeOpt.isEmpty()) return OF_EMPTY;
                    EntityType<?> entityType = typeOpt.get();

                    String[] style = split[1].split(":");
                    if (style.length != 2) return OF_EMPTY;

                    Icon icon = TextureCache.getIcon(style[1]);
                    if (!style[1].equals("none") && icon.equals(Icon.NONE)) {
                        icon = TextureCache.getIcon(style[1]);
                        if (icon.equals(Icon.NONE)) return OF_EMPTY;
                    }
                    Supplier<BaseComponent> cps;
                    if (split.length == 3) cps = BasicComponentFactory.getElementComponent(icon, split[2]);
                    else cps = BasicComponentFactory.getDefault(icon);

                    Supplier<? extends TRenderCursor> supplier;
                    if (style[0].equals("3body")){
                        supplier = () -> new TDir3BodyCursor(cps.get(), cps.get(), cps.get());
                    } else if (style[0].equals("normal")){
                        supplier = () -> new TDirectProjCursor(cps.get());
                    } else return OF_EMPTY;

                    return new Pair<EntityType<?>, Supplier<? extends TRenderCursor>>(entityType, supplier);
                })
                .filter(p -> p != OF_EMPTY)
                .collect(Collectors.toSet());
    }

}
