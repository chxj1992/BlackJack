package utils;

import com.google.common.collect.ImmutableMap;


/**
 * Ajax 返回状态
 * Author: chen
 * DateTime: 1/8/14 12:03 PM
 */
public class AjaxReturn {

    public static ImmutableMap success( String info, Object data ) {
        return ImmutableMap.of("status", "1", "info", info, "data", data);
    }

    public static ImmutableMap success( String info ) {
        return ImmutableMap.of("status", "1", "info", info);
    }

    public static ImmutableMap success() {
        return AjaxReturn.success("success");
    }

    public static ImmutableMap fail( String info ) {
        return ImmutableMap.of("status", "0", "info", info);
    }

    public static ImmutableMap fail() {
        return AjaxReturn.fail("fail");
    }

}
