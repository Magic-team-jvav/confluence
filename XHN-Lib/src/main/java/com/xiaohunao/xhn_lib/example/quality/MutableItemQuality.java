package com.xiaohunao.xhn_lib.example.quality;

/**
 * 可变品质接口
 * 定义了可以动态更新的品质对象的方法
 */
public interface MutableItemQuality {
    
    /**
     * 从另一个品质对象更新当前品质的数据
     * 
     * @param source 源品质对象，包含新的数据
     */
    void updateFrom(ItemQuality source);
}
