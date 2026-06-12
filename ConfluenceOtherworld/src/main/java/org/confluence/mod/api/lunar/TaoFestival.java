package org.confluence.mod.api.lunar;

/**
 * 道历节日
 *
 * @param name   名称
 * @param remark 备注
 * @author 6tail
 */
public record TaoFestival(String name, String remark) {

    public TaoFestival(String name, String remark) {
        this.name = name;
        this.remark = null == remark ? "" : remark;
    }

    public TaoFestival(String name) {
        this(name, null);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toFullString() {
        StringBuilder s = new StringBuilder();
        s.append(name);
        if (null != remark && remark.length() > 0) {
            s.append("[");
            s.append(remark);
            s.append("]");
        }
        return s.toString();
    }
}
