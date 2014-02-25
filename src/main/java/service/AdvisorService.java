package service;

import com.google.common.collect.Maps;
import dao.AdvisorDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * add_your_description_here
 * Author: chen
 * DateTime: 2/3/14 10:49 AM
 */
public class AdvisorService {

    @Autowired
    private PokerService pokerService;
    @Autowired
    private AdvisorDao advisorDao;



    private Map<Integer, Integer> highLow = Maps.newHashMap();

    private void init() {
        highLow.put(2, 1);
        highLow.put(3, 1);
        highLow.put(4, 1);
        highLow.put(5, 1);
        highLow.put(6, 1);
        highLow.put(7, 0);
        highLow.put(8, 0);
        highLow.put(9, 0);
        highLow.put(10, -1);
        highLow.put(11, -1);
        highLow.put(1, -1);
    }


    public String getBetAdvise(HttpSession session){
        init();
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        if(usedCards == null || session.getAttribute("level").equals("expert") )
            return "No suggestion, Sir";

        Double total = 0.0;
        for( Poker poker : usedCards ) {
            total += Double.valueOf(highLow.get(poker.getValue()));
        }

        total = total*52/undealCards.size();
        String coefficient = String.format("%.2f", total);
        if ( total < -2 )
            return "I advise you to 'Shuffle', Sir";
        else if( total >= -2 && total < 2 )
            return "I advise you to set a 'Low' bet, Sir(Coefficient:"+coefficient+")";
        else if ( total >= 2 && total < 6 )
            return  "I advise you to set a 'Medium' bet, Sir(Coefficient:"+coefficient+")";
        else
            return "I advise you to set a 'High' bet, Sir(Coefficient:"+coefficient+")";
    }


    public String getActionAdvise(HttpSession session){

        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");
        if(playerCards == null)
            return "No Suggestion, Sir";

        String dealer = String.valueOf(dealerCards.get(1).getValue());
        if ( dealer.equals("11") )
            dealer = "A";

        String player;
        if( playerCards.size() == 2 && (playerCards.get(0).equals(11)||playerCards.get(1).equals(11)) ) {
            player = "A"+String.valueOf( pokerService.totalScore("player", session)-11 );
        } else {
            player = "H"+String.valueOf( pokerService.totalScore("player", session) );
        }

        String advisor = advisorDao.getAdvisor(player, dealer);
        if(playerCards.size()>2 && advisor.equals("Double"))
            advisor = "Hit";

        return "I advise you to "+advisor+", Sir";
    }


}
