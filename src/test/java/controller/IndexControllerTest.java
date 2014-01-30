package controller;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * 游戏操作测试
 * Author: chen
 * DateTime: 1/29/14 9:33 PM
 */
public class IndexControllerTest {

    IndexController controller;
    MockHttpSession session;

    @Before
    public void setUp() throws Exception {
        controller = new IndexController();
        session = new MockHttpSession();
    }

    @Test
    public void set_level_to_expert() throws Exception {

        controller.setLevel("expert", session);
        assertThat(session.getAttribute("level").toString(), is("expert"));
    }

    @Test
    public void the_first_time_open_card_undeal_card_num_should_be_48() throws Exception {

        controller.openCards(session);
        List<Integer> undealCards = (List<Integer>) session.getAttribute("undealCards");
        assertThat(undealCards.size(), is(48));
    }

    @Test
    public void dealer_open_card_num_should_be_2() throws Exception {

        controller.openCards(session);
        List<Integer> dealerCards = (List<Integer>) session.getAttribute("dealerCards");
        assertThat(dealerCards.size(), is(2));
    }

    @Test
    public void player_open_card_num_should_be_2() throws Exception {

        controller.openCards(session);
        List<Integer> playerCards = (List<Integer>) session.getAttribute("playerCards");
        assertThat(playerCards.size(), is(2));
    }

    @Test
    public void beginner_open_card_when_undeal_card_gt_10_should_reduce_4() throws Exception {

        List<Integer> undealCards = Lists.newArrayList();
        for (int i = 0; i<20; i++)
            undealCards.add(new Integer(i));
        session.setAttribute("undealCards",undealCards);
        controller.openCards(session);
        undealCards = (List<Integer>) session.getAttribute("undealCards");
        assertThat(undealCards.size(), is(16));
    }


    @Test
    public void beginner_open_card_when_undeal_card_lt_10_should_be_48() throws Exception {

        List<Integer> undealCards = Lists.newArrayList();
        for (int i = 0; i<8; i++)
            undealCards.add(new Integer(i));
        session.setAttribute("undealCards",undealCards);
        controller.openCards(session);
        undealCards = (List<Integer>) session.getAttribute("undealCards");
        assertThat(undealCards.size(), is(48));
    }


    @Test
    public void expert_open_card_undealer_card_should_be_48() throws Exception {

        session.setAttribute("level","expert");
        List<Integer> undealCards = Lists.newArrayList();
        for (int i = 0; i<20; i++)
            undealCards.add(new Integer(i));
        session.setAttribute("undealCards",undealCards);
        controller.openCards(session);
        undealCards = (List<Integer>) session.getAttribute("undealCards");
        assertThat(undealCards.size(), is(48));
    }



}
