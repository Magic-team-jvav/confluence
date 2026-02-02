package com.xiaohunao.enemybanner.banner;

import java.util.HashMap;
import java.util.Map;

public class BannerBehaviorProvider {
    private static final BannerBehaviorProvider INSTANCE = new BannerBehaviorProvider();

    private final Map<String, BannerBehavior> bannerBehaviorMap;

    private BannerBehaviorProvider(){
        bannerBehaviorMap = new HashMap<>();
        bannerBehaviorMap.put("basic_silks", new BasicBannerBehavior());
        bannerBehaviorMap.put("damage_silks", new BasicBannerBehavior(BasicBannerBehavior.DEFAULT_RANGE, BasicBannerBehavior.DEFAULT_DAMAGE + 0.3, BasicBannerBehavior.DEFAULT_RESIST));
        bannerBehaviorMap.put("range_silks", new BasicBannerBehavior(BasicBannerBehavior.DEFAULT_RANGE + 8, BasicBannerBehavior.DEFAULT_DAMAGE, BasicBannerBehavior.DEFAULT_RESIST));
        bannerBehaviorMap.put("resist_silks", new BasicBannerBehavior(BasicBannerBehavior.DEFAULT_RANGE, BasicBannerBehavior.DEFAULT_DAMAGE, BasicBannerBehavior.DEFAULT_RESIST + 0.2));
        bannerBehaviorMap.put("pull_silks", new PushOrPullBannerBehavior(true));
        bannerBehaviorMap.put("push_silks", new PushOrPullBannerBehavior(false));
        bannerBehaviorMap.put("loot_silks", new LootBannerBehavior());
        bannerBehaviorMap.put("inhibit_silks", new BasicBannerBehavior());
    }

    public BannerBehavior getBannerBehavior(String silksId){
        return bannerBehaviorMap.get(silksId);
    }

    public static BannerBehaviorProvider getInstance(){
        return INSTANCE;
    }
}
