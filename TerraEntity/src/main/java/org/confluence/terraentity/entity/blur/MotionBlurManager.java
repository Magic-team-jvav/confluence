package org.confluence.terraentity.entity.blur;

import org.confluence.terraentity.api.entity.blur.IMotionBlurContext;
import org.confluence.terraentity.api.entity.blur.IMotionBlurHolder;
import org.confluence.terraentity.api.entity.blur.IMotionBlurManager;
import org.confluence.terraentity.config.ClientConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MotionBlurManager<C extends IMotionBlurContext> implements IMotionBlurManager<C> {

    private final Deque<C> trails = new ArrayDeque<>();
    private final int maxTrailLength; // 最大残影数量
    private int dynamicMaxTrailLength = 0; // 动态残影数量

    public MotionBlurManager(int maxTrailLength) {
        this.maxTrailLength = Math.max(1, maxTrailLength);
    }

    @Override
    public @NotNull Iterator<C> iterator() {
        Iterator<C> descendingIterator = trails.descendingIterator();
        if(dynamicMaxTrailLength >= trails.size()){
            return descendingIterator;
        }

        int len = dynamicMaxTrailLength;
        int skipCount = trails.size() - len;

        return new Iterator<>() {
            private int remaining = len;

            @Override
            public boolean hasNext() {
                return remaining > skipCount && descendingIterator.hasNext();
            }

            @Override
            public C next() {
                if (remaining <= 0) throw new NoSuchElementException();
                remaining--;
                return descendingIterator.next();
            }
        };
    }
    /**
     * 只允许客户端调用
     */
    public void update(IMotionBlurHolder<C> entity, C trail) {

        if(entity.isMotionBlurEnabled() && dynamicMaxTrailLength <= maxTrailLength){
            dynamicMaxTrailLength++;
        }else if(dynamicMaxTrailLength > 0){
            dynamicMaxTrailLength--;
        }

        if(ClientConfig.ENABLE_ENTITY_MOTION_BLUR.get()) {
            trails.addLast(trail);
        }else{
            trails.pollFirst();
        }

        while (trails.size() > dynamicMaxTrailLength) {
            trails.pollFirst();
        }

    }

    @Override
    public Deque<C> getTrails() {
        return trails;
    }

}