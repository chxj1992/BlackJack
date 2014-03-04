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

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class PokerServiceTest {

    @Mock
    PokerDao pokerDao;

    @InjectMocks
    PokerService pokerService;

    private int balance;
    private int bet;
    private String level;
    private List<Poker> dealerCards;
    private List<Poker> playerCards;
    private List<Poker> undealCards;
    private List<Poker> usedCards;
    private Map playerRoutine;
    private Map dealerRoutine;


    @Before
    public void setUp() throws Exception {

        balance = 1000;
        bet = 10;
        level = "beginner";
        dealerCards = Lists.newArrayList(new Poker(3), new Poker(10));
        playerCards = Lists.newArrayList(new Poker(9), new Poker(8));
        undealCards = Lists.newArrayList(new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8));
        usedCards = Lists.newArrayList(new Poker(3), new Poker(5));
        Map newRoutine = ImmutableMap.of("name", "Normal", "rate", 1.0);
        playerRoutine = Maps.newHashMap(newRoutine);
        dealerRoutine = Maps.newHashMap(newRoutine);

        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void player_normal_win() throws Exception {
        Map win = pokerService.judgeWin(bet, balance, dealerCards, playerCards, dealerRoutine, playerRoutine);
        assertThat(win.get("name").toString(), is("Normal"));
        assertThat((Integer) win.get("money"), is(1020));
    }

    @Test
    public void player_normal_lose() throws Exception {
        dealerCards = Lists.newArrayList(new Poker(10), new Poker(10));
        Map win = pokerService.judgeWin(bet, balance, dealerCards, playerCards, dealerRoutine, playerRoutine);
        assertThat(win.get("name").toString(), is("Normal"));
        assertThat((Integer) win.get("money"), is(1000));
    }

    @Test
    public void judge_win_draw() throws Exception {
        dealerCards = Lists.newArrayList(new Poker(7), new Poker(10));
        Map win = pokerService.judgeWin(bet, balance, dealerCards, playerCards, dealerRoutine, playerRoutine);
        assertThat(win.get("name").toString(), is("Draw"));
        assertThat((Integer) win.get("money"), is(1010));
    }

    @Test
    public void player_five_card_win() throws Exception {
        playerRoutine = ImmutableMap.of("name", "Five Card", "rate", 2.0);
        Map win = pokerService.judgeWin(bet, balance, dealerCards, playerCards, dealerRoutine, playerRoutine);
        assertThat(win.get("name").toString(), is("Five Card"));
        assertThat((Integer) win.get("money"), is(1030));
    }

    @Test
    public void check_player_routine_of_five_card() throws Exception {
        playerCards = Lists.newArrayList(new Poker(2), new Poker(2),  new Poker(3),  new Poker(3), new Poker(4));
        Map routine = pokerService.checkRoutine("player", playerCards, dealerCards, playerRoutine, dealerRoutine);
        assertThat(routine.get("name").toString(), is("Five Card"));
        assertThat((Double) routine.get("rate"), is(2.0));
    }

    @Test
    public void check_player_routine_of_special() throws Exception {
        playerCards = Lists.newArrayList(new Poker(7), new Poker(7),  new Poker(7));
        Map routine = pokerService.checkRoutine("player", playerCards, dealerCards, playerRoutine, dealerRoutine);
        assertThat(routine.get("name").toString(), is("Special Win"));
        assertThat((Double) routine.get("rate"), is(3.0));
    }

    @Test
    public void check_dealer_routine_of_black_jack() throws Exception {
        playerCards = Lists.newArrayList(new Poker(11), new Poker(10));
        Map routine = pokerService.checkRoutine("player", playerCards, dealerCards, playerRoutine, dealerRoutine);
        assertThat(routine.get("name").toString(), is("Black Jack"));
        assertThat((Double) routine.get("rate"), is(2.0));
    }



    @Test
    public void init_card_size_should_be_2 () throws Exception {
        pokerService.initCards(undealCards, usedCards, dealerCards, playerCards);
        assertThat(playerCards.size(), is(2));
    }

    @Test
    public void clear_cards_size_should_be_0() throws Exception {
        pokerService.clearCards(playerCards, dealerCards);
        assertThat(playerCards.size(), is(0));
        assertThat(dealerCards.size(), is(0));
    }

    @Test
    public void test_surrender_lose_bet() throws Exception {
        Integer money = pokerService.surrender(bet, balance);
        assertThat(money, is(1005));
    }

    @Test
    public void buy_insurance_success() throws Exception {
        dealerCards = Lists.newArrayList(new Poker(11), new Poker(10));
        Integer money = pokerService.insurance(bet, balance, dealerCards);
        assertThat(money, is(1020));
    }

    @Test
    public void buy_insurance_fail() throws Exception {
        Integer money = pokerService.surrender(bet, balance);
        assertThat(money, is(1005));
    }

    @Test
    public void is_black_jack() throws Exception {
        playerCards = Lists.newArrayList(new Poker(11), new Poker(10));
        Boolean blackJack = pokerService.isBlackJack("player", playerCards, dealerCards);
        assertThat(blackJack, is(true));
    }


    @Test
    public void is_not_black_jack() throws Exception {
        Boolean blackJack = pokerService.isBlackJack("player", playerCards, dealerCards);
        assertThat(blackJack, is(false));
    }

    @Test
    public void player_total_score_should_be_17() throws Exception {
        Integer playerScore = pokerService.totalScore("player", playerCards, dealerCards);
        assertThat(playerScore, is(17));
    }


}
