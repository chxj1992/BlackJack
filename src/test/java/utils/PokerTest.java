package utils;


import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Poker 测试
 * Author: chen
 * DateTime: 1/28/14 2:51 PM
 */
public class PokerTest {

    @Test
    public void test_init_number_is_52() throws Exception {
        assertThat(Poker.init().size(), is(52));
    }

    @Test
    public void test_init_2_number_is_104() throws Exception {
        assertThat(Poker.init(2).size(), is(104));
    }


}
