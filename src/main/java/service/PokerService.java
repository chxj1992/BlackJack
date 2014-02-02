package service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dao.PokerDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import sun.print.resources.serviceui_es;
import utils.Pokers;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Author: chen
 * DateTime: 1/31/14 11:28 PM
 */
public class PokerService {

    private static final double NORMAL_RATE = 1.0 ;
    private static final double BLACK_JACK_RATE = 2.0 ;
    private static final double FIVE_CARD_RATE = 2.0 ;
    private static final double SPECIAL_RATE = 3.0 ;
    private static final double INSURANCE_WIN = 2.0;
    private static final double INSURANCE_LOSE = -0.5;
    private static final double SURRENDER_RATE = -0.5;


    @Autowired
    private PokerDao pokerDao;

    public Map judgeWin(HttpSession session) {

        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");

        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");

        Integer playerScore = 0;
        for ( Poker poker : playerCards ){
            playerScore += poker.getValue();
        }
        Integer dealerScore = 0;
        for ( Poker poker : dealerCards ){
            dealerScore += poker.getValue();
        }

        if( isBlackJack("player", session) && !isBlackJack("dealer", session) ) {
            return ImmutableMap.of("name", "Black Jack", "money", (int)(balance + (BLACK_JACK_RATE+1)*bet) );
        } else if( isBlackJack("dealer", session) && !isBlackJack("player", session) ) {
            return ImmutableMap.of("name", "Black Jack", "money", (int)(balance - (BLACK_JACK_RATE-1)*bet) );
        }

        if( dealerCards.size()>=5 && dealerScore<=21 )
            return ImmutableMap.of("name", "Five Card", "money", -(int)(balance - (FIVE_CARD_RATE-1)*bet) );

        if ( dealerScore > 21 )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)*bet) );
        else if ( playerScore > dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)*bet) );
        else if ( playerScore < dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance - (NORMAL_RATE-1)*bet) );
        else
            return ImmutableMap.of("name", "Draw", "money", balance + bet );

    }


    public Map checkRoutine(String role, HttpSession session) {

        List<Poker> pokers;
        if ( role.equals("player") )
            pokers = (List<Poker>) session.getAttribute("playerCards");
        else
            pokers = (List<Poker>) session.getAttribute("dealerCards");

        Map newRoutine = Maps.newHashMap();
        newRoutine.put("name", "Normal");
        newRoutine.put("rate", NORMAL_RATE);
        Map routine = session.getAttribute(role+"Routine") != null ? (Map)session.getAttribute(role+"Routine") : newRoutine;
        //特奖
        Integer sevenNum = 0;
        for( Poker poker : pokers ) {
            if ( poker.getValue().equals(7) )
                sevenNum ++;
        }
        if ( sevenNum.equals(7) ) {
            routine.put("name", "Special Win");
            routine.put("rate", SPECIAL_RATE);
        }
        //黑杰克
        if ( isBlackJack(role, session) ) {
            routine.put("name", "Black Jack");
            routine.put("rate", BLACK_JACK_RATE);
        }
        //五龙
        if ( pokers.size() >= 5 ) {
            routine.put("name", "Five Card");
            routine.put("rate", FIVE_CARD_RATE);
        }

        session.setAttribute(role+"Routine", routine);
        return routine;
    }


    /**
     * 重新洗牌
     * @param session
     */
    public void shuffleCards(HttpSession session) {
        List<Poker> cards = pokerDao.getPokers(Pokers.init());

        session.setAttribute("undealCards", cards);
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
        session.setAttribute("usedCards", Lists.newArrayList());
    }


    /**
     * 初始化开牌session
     * @param session
     */
    public void initCards(HttpSession session) {

        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");

        List<Poker> dealerCards = Lists.newArrayList();
        dealerCards.add(undealCards.get(0));
        dealerCards.add(undealCards.get(1));

        List<Poker> playerCards = Lists.newArrayList();
        playerCards.add(undealCards.get(2));
        playerCards.add(undealCards.get(3));

        List<Poker> usedCards = Lists.newArrayList();
        for ( int i=0; i<4; i++ )
            usedCards.add(undealCards.get(i));

        for ( int i=0; i<4; i++ )
            undealCards.remove(0);

        session.setAttribute("dealerCards", dealerCards);
        session.setAttribute("playerCards", playerCards);
        session.setAttribute("usedCards", usedCards);

    }


    /**
     * 获取当前总分,如果爆牌则尝试转换A
     * @param role
     * @param session
     * @return
     */
    public Integer totalScore(String role, HttpSession session) {

        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");

        List<Poker> pokers;
        if ( role.equals("player") )
            pokers = playerCards;
        else
            pokers = dealerCards;

        Integer total = 0;
        Integer aNum = 0;
        for ( Poker poker : pokers ) {
            if ( poker.getValue().equals(11) )
                aNum++;
            total += poker.getValue();
        }

        // 爆牌但是有A时，将A算小
        while (aNum>0 && total>21 ) {
            total -= aNum * 10;
            aNum--;
            convertACard(role,session);
        }

        return total;

    }

    /**
     * 转换A
     * @param role
     * @param session
     */
    public void convertACard(String role, HttpSession session){

        List<Poker> pokers;
        if (role.equals("player"))
            pokers = (List<Poker>) session.getAttribute("playerCards");
        else
            pokers = (List<Poker>) session.getAttribute("dealerCards");

        for ( Poker poker : pokers ) {
            if( poker.getValue().equals(11) ) {
                poker.setValue(1);
                break;
            }
        }
    }

    /**
     * 清牌
     * @param session
     */
    public void clearAll(HttpSession session){
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
    }

    /**
     * 投降
     * @param session
     * @return
     */
    public int surrender(HttpSession session) {
        Integer bet = (Integer) session.getAttribute("bet");
        return (int)(SURRENDER_RATE * bet);
    }

    /**
     * 买保险
     * @param session
     * @return
     */
    public int insurance(HttpSession session) {
        Integer bet = (Integer) session.getAttribute("bet");
        List<Poker> dealer = (List<Poker>) session.getAttribute("dealerCards");
        if(dealer.get(0).getValue()+dealer.get(1).getValue() == 21)
            return (int)(INSURANCE_WIN * bet);
        else
            return (int)(INSURANCE_LOSE * bet);
    }

    public Boolean isBlackJack(String role, HttpSession session) {
        List<Poker> pokers;
        if( role.equals("player") )
             pokers = (List<Poker>) session.getAttribute("playerCards");
        else
            pokers = (List<Poker>) session.getAttribute("dealerCards");

        if ( pokers.size() == 2 && (pokers.get(0).getValue()+pokers.get(1).getValue()) == 21 )
            return true;

        return false;
    }

}
