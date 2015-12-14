/**
 *
 */
package com.wpmac.appversionupdata;

import android.os.Bundle;


/**
 * @author T0227
 * @date 2015-12-1
 */
public class VersionParam extends RequestParam {

    public final static String method = "/app/selectAppByPlatForm";
    public String platform;

    @Override
    public Bundle getParam() {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putString("method", method);
        bundle.putString("platform", platform);
        return bundle;
    }

}
