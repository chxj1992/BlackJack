package service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dao.PokerDao;
import model.Poker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Author: chen
 * DateTime: 2/4/14 10:56 AM
 */
public class PokerServiceTest {

    @Mock
    PokerDao pokerDao;

    @InjectMocks
    PokerService pokerService;

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
        Map newRoutine = ImmutableMap.of("name", "Normal", "rate", 1.0);
        session.setAttribute("playerRoutine", Maps.newHashMap(newRoutine));
        session.setAttribute("dealerRoutine", Maps.newHashMap(newRoutine));

        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void player_normal_win() throws Exception {
        Map win = pokerService.judgeWin(session);
        assertThat(win.get("name").toString(), is("Normal"));
        assertThat((Integer) win.get("money"), is(1020));
    }

    @Test
    public void player_normal_lose() throws Exception {
        session.setAttribute("dealerCards", Lists.newArrayList(new Poker(10), new Poker(10)));
        Map win = pokerService.judgeWin(session);
        assertThat(win.get("name").toString(), is("Normal"));
        assertThat((Integer) win.get("money"), is(1000));
    }

    @Test
    public void judge_win_draw() throws Exception {
        session.setAttribute("dealerCards", Lists.newArrayList(new Poker(7), new Poker(10)));
        Map win = pokerService.judgeWin(session);
        assertThat(win.get("name").toString(), is("Draw"));
        assertThat((Integer) win.get("money"), is(1010));
    }

    @Test
    public void player_five_card_win() throws Exception {
        session.setAttribute("playerRoutine", ImmutableMap.of("name", "Five Card", "rate", 2.0));
        Map win = pokerService.judgeWin(session);
        assertThat(win.get("name").toString(), is("Five Card"));
        assertThat((Integer) win.get("money"), is(1030));
    }

    @Test
    public void check_player_routine_of_five_card() throws Exception {
        session.setAttribute("playerCards", Lists.newArrayList(new Poker(2), new Poker(2),  new Poker(3),  new Poker(3), new Poker(4)));
        Map routine = pokerService.checkRoutine("player", session);
        assertThat(routine.get("name").toString(), is("Five Card"));
        assertThat((Double) routine.get("rate"), is(2.0));
    }

    @Test
    public void check_player_routine_of_special() throws Exception {
        session.setAttribute("playerCards", Lists.newArrayList(new Poker(7), new Poker(7),  new Poker(7)));
        Map routine = pokerService.checkRoutine("player", session);
        assertThat(routine.get("name").toString(), is("Special Win"));
        assertThat((Double) routine.get("rate"), is(3.0));
    }

    @Test
    public void check_dealer_routine_of_black_jack() throws Exception {
        session.setAttribute("playerCards", Lists.newArrayList(new Poker(11), new Poker(10)));
        Map routine = pokerService.checkRoutine("player", session);
        assertThat(routine.get("name").toString(), is("Black Jack"));
        assertThat((Double) routine.get("rate"), is(2.0));
    }

    @Test
    public void open_card_status_should_be_playing () throws Exception {
        pokerService.beforeOpenCard(1000, 10, session);
        String status = (String) session.getAttribute("status");
        assertThat(status, is("playing"));
    }

    @Test
    public void open_card_balance_right () throws Exception {
        pokerService.beforeOpenCard(1000, 10, session);
        Integer balance= (Integer) session.getAttribute("balance");
        assertThat(balance, is(990));
    }

    @Test
    public void open_card_size_should_be_2 () throws Exception {
        pokerService.beforeOpenCard(1000, 10, session);
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        assertThat(playerCards.size(), is(2));
    }

    @Test
    public void clear_all() throws Exception {
        pokerService.clearAll(session);
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");
        assertThat(playerCards.size(), is(0));
        assertThat(dealerCards.size(), is(0));
    }

    @Test
    public void test_surrender_lose_bet() throws Exception {
        Integer bet = pokerService.surrender(session);
        assertThat(bet, is(-5));
    }

    @Test
    public void buy_insurance_success() throws Exception {
        session.setAttribute("dealerCards", Lists.newArrayList(new Poker(11), new Poker(10)));
        Integer bet = pokerService.insurance(session);
        assertThat(bet, is(20));
    }

    @Test
    public void buy_insurance_fail() throws Exception {
        Integer bet = pokerService.surrender(session);
        assertThat(bet, is(-5));
    }

    @Test
    public void is_black_jack() throws Exception {
        session.setAttribute("playerCards", Lists.newArrayList(new Poker(11), new Poker(10)));
        Boolean blackJack = pokerService.isBlackJack("player", session);
        assertThat(blackJack, is(true));
    }


    @Test
    public void is_not_black_jack() throws Exception {
        Boolean blackJack = pokerService.isBlackJack("player", session);
        assertThat(blackJack, is(false));
    }

    @Test
    public void player_total_score_should_be_17() throws Exception {
        Integer playerScore = pokerService.totalScore("player", session);
        assertThat(playerScore, is(17));
    }


}
