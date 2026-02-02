package com.xiaohunao.enemybanner.gui.widget;

public class ScrollValue {
    double scrollValue;
    public ScrollValue(){
        scrollValue = 0;
    }

    public double getScrollValue(){
        return this.scrollValue;
    }

    public double getScrollValue(double totalLength){
        return totalLength * scrollValue;
    }

    public void addScrollValue(double addOffs, double length){
        this.scrollValue = limit(addOffs / length + this.scrollValue);
    }

    public void setScrollValue(double offs, double length){
        this.scrollValue = limit(offs / length);
    }

    private double limit(double scrollValue){
        scrollValue = Math.min(scrollValue, 1);
        scrollValue = Math.max(scrollValue, 0);
        return scrollValue;
    }
}
