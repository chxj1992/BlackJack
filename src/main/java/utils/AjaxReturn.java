package utils;

import com.google.common.collect.ImmutableMap;


/**
 * Ajax 返回状态
 * Author: chen
 * DateTime: 1/8/14 12:03 PM
 */
public class AjaxReturn {

    public static ImmutableMap success( Object data ) {
        return ImmutableMap.of("status", 1, "data", data);
    }

    public static ImmutableMap success() {
        return AjaxReturn.success("success");
    }

    public static ImmutableMap fail( Object data ) {
        return ImmutableMap.of("status", 0, "data", data);
    }

    public static ImmutableMap fail() {
        return AjaxReturn.fail("fail");
    }

}
