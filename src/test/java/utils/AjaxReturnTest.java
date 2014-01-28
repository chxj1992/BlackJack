package utils;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Author: chen
 * DateTime: 1/9/14 11:05 PM
 */
public class AjaxReturnTest {


    @Test
    public void success_status_should_be_1() throws Exception {
        String status = AjaxReturn.success().get("status").toString();
        assertThat(status, is("1"));
    }

    @Test
    public void fail_status_should_be_0() throws Exception {
        String status = AjaxReturn.fail().get("status").toString();
        assertThat(status, is("0"));
    }


}
