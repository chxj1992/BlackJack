package controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.PokerService;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * add_description_here
 * Author: chen
 * Date&Time: 3/1/14 4:27 PM.
 */

@RunWith(MockitoJUnitRunner.class)
public class IndexControllerTest {
    @Mock
    private HttpSession session;
    @Mock
    private PokerService pokerService;
    private IndexController indexController;

    @Before
    public void setup() {
        indexController = new IndexController(pokerService);
    }

    @Test
    public void should_shuffle_cards_and_return_success_given_level_is_beginner() throws Exception {
        given(session.getAttribute("level")).willReturn("beginner");

        Map map = indexController.shuffle(session);

        assertThat((Integer) map.get("status"), is(1));
    }

    @Test
    public void should_not_shuffle_cards_and_return_fail_given_level_is_expert() throws Exception {
        given(session.getAttribute("level")).willReturn("expert");

        Map map = indexController.shuffle(session);

        assertThat((Integer) map.get("status"), is(0));
    }

    @Test
    public void should_set_level_and_balance_in_session() {
        indexController.setLevel("expert", session);

        verify(session).setAttribute("level", "expert");
        verify(session).setAttribute("balance", 1000);
    }

    @Test
    public void should_call_shuffle_cards_when_set_level() {
        indexController.setLevel("expert", session);

        verify(pokerService).shuffleCards(null, null);
    }

    @Test
    public void should_success_when_set_level() {
        Map map = indexController.setLevel("expert", session);

        assertThat((Integer) map.get("status"), is(1));
    }

    @Test
    public void should_return_fail_when_balance_is_null() {
        Map map = indexController.openCards(10, session);

        assertThat((Integer) map.get("status"), is(0));
    }



}
