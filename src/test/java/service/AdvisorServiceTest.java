package service;

import com.google.common.collect.Lists;
import dao.AdvisorDao;
import model.Poker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * add_your_description_here
 * Author: chen
 * DateTime: 2/4/14 1:36 PM
 */
public class AdvisorServiceTest {

    @Mock
    AdvisorDao advisorDao;
    @Mock
    PokerService pokerService;

    @InjectMocks
    AdvisorService advisorService;

    MockHttpSession session;

    @Before
    public void setUp() throws Exception {

        session = new MockHttpSession();
        session.setAttribute("balance", 1000);
        session.setAttribute("bet", 10);
        session.setAttribute("dealerCards", Lists.newArrayList(new Poker(3), new Poker(10)));
        session.setAttribute("playerCards", Lists.newArrayList(new Poker(9), new Poker(8)));
        session.setAttribute("undealCards", Lists.newArrayList(new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8)));
        session.setAttribute("usedCards", Lists.newArrayList(new Poker(3), new Poker(5)));

        MockitoAnnotations.initMocks(this);

        when(advisorDao.getAdvisor(Matchers.anyString(), Matchers.anyString())).thenReturn(
                new String("Stand") );
        when(pokerService.totalScore(Matchers.anyString(), eq(session))).thenReturn(
                new Integer(15));
    }

    @Test
    public void test_get_bet_advisor() throws Exception {
        String advise = advisorService.getBetAdvise(session);
        assertThat(advise, is("I advise you to set a 'Low' bet"));
    }

    @Test
    public void test_get_bet_advisor_when_no_used_card() throws Exception {
        session.removeAttribute("usedCards");
        String advise = advisorService.getBetAdvise(session);
        assertThat(advise, is("No suggestion"));
    }

    @Test
    public void test_get_bet_advisor_when_play_expert_level() throws Exception {
        session.setAttribute("level", "expert");
        String advise = advisorService.getBetAdvise(session);
        assertThat(advise, is("No suggestion"));
    }

    @Test
    public void test_get_action_advisor() throws Exception {
        String advise = advisorService.getActionAdvise(session);
        assertThat(advise, is("I advise you to Stand"));
    }


}
