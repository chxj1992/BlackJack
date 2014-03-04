package service;

import com.google.common.collect.Lists;
import dao.AdviserDao;
import model.Poker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * add_your_description_here
 * Author: chen
 * DateTime: 2/4/14 1:36 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class AdviserServiceTest {

    @Mock
    AdviserDao adviserDao;
    @Mock
    PokerService pokerService;

    @InjectMocks
    AdviserService adviserService;

    private List<Poker> dealerCards;
    private List<Poker> playerCards;
    private List<Poker> undealCards;
    private List<Poker> usedCards;
    private String level;

    @Before
    public void setUp() throws Exception {

        level = "beginner";
        dealerCards = Lists.newArrayList(new Poker(3), new Poker(10));
        playerCards = Lists.newArrayList(new Poker(9), new Poker(8));
        undealCards = Lists.newArrayList(new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8), new Poker(9), new Poker(8));
        usedCards = Lists.newArrayList(new Poker(3), new Poker(5));


        when(adviserDao.getAdvisor(Matchers.anyString(), Matchers.anyString())).thenReturn("Stand");
        when(pokerService.totalScore(Matchers.anyString(), eq(playerCards), eq(dealerCards))).thenReturn(15);
    }

    @Test
    public void test_get_bet_advisor() throws Exception {
        String advise = adviserService.getBetAdvise(usedCards, undealCards, level);
        assertThat(advise, is("I advise you to set a 'High' bet, Sir(Coefficient:13.00)" ));
    }
    @Test
    public void test_get_bet_advisor_when_no_used_card() throws Exception {
        String advise = adviserService.getBetAdvise(null, undealCards, level);
        assertThat(advise, is("No suggestion, Sir"));
    }

    @Test
    public void test_get_bet_advisor_when_play_expert_level() throws Exception {
        level = "expert";
        String advise = adviserService.getBetAdvise(usedCards, undealCards, level);
        assertThat(advise, is("No suggestion, Sir"));
    }

    @Test
    public void test_get_action_advisor() throws Exception {
        String advise = adviserService.getActionAdvise(playerCards, dealerCards);
        assertThat(advise, is("I advise you to Stand, Sir"));
    }


}
