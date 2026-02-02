package nowebsite.makertechno.the_trackers.client.gui.provider;

import nowebsite.makertechno.the_trackers.api.component.ComponentBuilder;
import nowebsite.makertechno.the_trackers.client.gui.components.IRenderElement;
import nowebsite.makertechno.the_trackers.client.gui.cursors.*;
import nowebsite.makertechno.the_trackers.client.gui.components.BaseComponent;
import nowebsite.makertechno.the_trackers.client.gui.components.BasicComponentFactory;
import org.jetbrains.annotations.Nullable;

public final class BuilderResultComposer {
    private BuilderResultComposer() {}

    public static TRenderCursor compose(ComponentBuilder.BuilderResult result) {
        TRenderCursor component = switch (result.type) {
            case RELATIVE -> composeRelativeComponent(result);
            case DIRECT -> composeDirectComponent(result);
            case HEAD_TAG -> composeRelativeComponent(result); // TODO
        };
        component.setSmoothMove(result.isSmoothMove);
        component.setAffectedByPlayerScale(result.affectedBySettings);
        component.setRescale(result.rescale);
        component.setTransformAlpha(result.transformAlpha);
        return component;
    }

    public static TRenderCursor composeRelativeComponent(ComponentBuilder.BuilderResult result) {
        return new TRelativeCursor(
            getIconComponent(result.component1Pattern, result.element1.get()),
            getIconComponent(result.component2Pattern, result.element2.get())
        );
    }

    public static TRenderCursor composeDirectComponent(ComponentBuilder.BuilderResult result) {
        TAbstractCursor cursor;
        if (result.cursorPattern != null) {
            String[] patterns = result.cursorPattern.split(",");
            if (patterns[0].equals("3body")) {
                if (result.component2Pattern == null){
                    cursor = new TDir3BodyCursor(
                        getIconComponent(result.component1Pattern, result.element1.get()),
                        getIconComponent(result.component1Pattern, result.element1.get()),
                        getIconComponent(result.component1Pattern, result.element1.get())
                    );
                } else {
                    cursor = new TDir3BodyCursor(
                        getIconComponent(result.component1Pattern, result.element1.get()),
                        getIconComponent(result.component2Pattern, result.element2.get()),
                        getIconComponent(result.component3Pattern, result.element3.get())
                    );
                }
                if (patterns.length > 1 && patterns[1].equals("faceCenter")) ((TDir3BodyCursor)cursor).setFaceCenter(true);
            }
            else cursor = new TDirectProjCursor(getIconComponent(result.component1Pattern, result.element1.get()));
        }
        else cursor = new TDirectProjCursor(getIconComponent(result.component1Pattern, result.element1.get()));
        return cursor;
    }
    /* TODO:public static TRenderCursor composeHeadTagComponent(ComponentBuilder.BuilderResult result) {
        return null;
    }*/


    private static BaseComponent getIconComponent(@Nullable String pattern, IRenderElement element) {
        return pattern == null
                ? BasicComponentFactory.getDefault(element).get()
                : BasicComponentFactory.getElementComponent(element, pattern).get();
    }
}
