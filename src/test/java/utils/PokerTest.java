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
        assertThat(Poker.init().size(),is(52));
    }

    @Test
    public void test_init_cards_right() throws Exception {
        assertThat(Poker.init(2).get(62),is(11));
    }

    @Test
    public void test_shuffled_cards_number_not_change() throws Exception {
        List poker = Poker.init();
        Poker.shuffle(poker);
        assertTrue(poker.size() == Poker.init().size());
    }

    @Test
    public void test_shuffled_cards_change() throws Exception {
        List poker = Poker.init();
        Poker.shuffle(poker);
        assertFalse(poker.equals(Poker.init()));
    }

}
