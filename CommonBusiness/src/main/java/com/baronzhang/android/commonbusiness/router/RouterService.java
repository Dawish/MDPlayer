package com.baronzhang.android.commonbusiness.router;

import com.baronzhang.android.commonbusiness.model.HouseDetail;
import com.baronzhang.android.router.annotation.router.CombinationUri;
import com.baronzhang.android.router.annotation.router.FullUri;
import com.baronzhang.android.router.annotation.router.IntentExtrasParam;
import com.baronzhang.android.router.annotation.router.UriParam;

import java.util.ArrayList;

/**
 * @author baronzhang (baron[dot]zhanglei[at]gmail[dot]com ==>> baronzhang.com)
 *         2017/3/6
 */
public interface RouterService {

    @FullUri("router://com.baronzhang.android.newhouse")
    void startNewHouseActivity(@UriParam("cityId") String cityId, @IntentExtrasParam("houseDetail") HouseDetail houseDetail);

    @CombinationUri(scheme = "router", host = "com.baronzhang.android.secondhouse")
    void startSecondHouseActivity(@IntentExtrasParam("cityId") String cityId, @IntentExtrasParam("houseDetailList") ArrayList<HouseDetail> houseDetailList);

    @CombinationUri(scheme = "router", host = "com.baronzhang.android.im", port = "6666", path = "/im/home")
    void startIMActivity(@UriParam("cityId") String cityId, @IntentExtrasParam("brokerIdList") ArrayList<Integer> brokerIdList);
}
