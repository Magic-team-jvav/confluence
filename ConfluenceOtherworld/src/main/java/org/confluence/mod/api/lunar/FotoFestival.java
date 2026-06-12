package org.confluence.mod.api.lunar;

/**
 * 佛历因果犯忌
 *
 * @param name       是日何日，如：雷斋日
 * @param result     犯之因果，如：犯者夺纪
 * @param everyMonth 是否每月同
 * @param remark     备注，如：宜先一日即戒
 * @author 6tail
 */
public record FotoFestival(String name, String result, boolean everyMonth, String remark) {

    public FotoFestival(String name, String result, boolean everyMonth, String remark) {
        this.name = name;
        this.result = null == result ? "" : result;
        this.everyMonth = everyMonth;
        this.remark = null == remark ? "" : remark;
    }

    public FotoFestival(String name) {
        this(name, null);
    }

    public FotoFestival(String name, String result) {
        this(name, result, false);
    }

    public FotoFestival(String name, String result, boolean everyMonth) {
        this(name, result, everyMonth, null);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toFullString() {
        StringBuilder s = new StringBuilder();
        s.append(name);
        if (null != result && result.length() > 0) {
            s.append(" ");
            s.append(result);
        }
        if (null != remark && remark.length() > 0) {
            s.append(" ");
            s.append(remark);
        }
        return s.toString();
    }
}
