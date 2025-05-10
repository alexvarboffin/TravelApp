package com.travelapp.config

import com.travelapp.sdk.config.AppTabs
import com.travelapp.sdk.R as sdkR

object AppTabsList {

    fun get(): List<AppTabs> {
        return listOf<AppTabs>(
            AppTabs.Flights,
            AppTabs.Hotels,

            AppTabs.Other(
                idRes = sdkR.id.ta_other1,
                graphId = sdkR.navigation.ta_other1,
                title = sdkR.string.ta_title_other1,
                icon = sdkR.drawable.ta_ic_other_1,
                url = sdkR.string.ta_url_other1
            ),


            AppTabs.Other(
                idRes = sdkR.id.ta_other2,
                graphId = sdkR.navigation.ta_other2,
                title = sdkR.string.ta_title_other2,
                icon = com.www.avia.kg.app.R.drawable.ic_kwk_message,
                url = sdkR.string.ta_url_other2
            ),

            AppTabs.Info,
//            AppTabs.Other(
//                idRes = com.www.avia.kg.app.R.id.nav_info,
//                graphId = com.www.avia.kg.app.R.navigation.ta_profile,
//                title = sdkR.string.ta_title_other2,
//                icon = com.www.avia.kg.app.R.drawable.ic_kwk_message,
//                url = sdkR.string.ta_url_other2
//            ),
        )
    }


}