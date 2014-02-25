package service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dao.PokerDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import utils.Pokers;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author: chen
 * DateTime: 1/31/14 11:28 PM
 */
public class PokerService {

    // 6副牌
    private static final int DECK_NUM = 6 ;

    private static final double NORMAL_RATE = 1.0 ;
    private static final double BLACK_JACK_RATE = 2.0 ;
    private static final double FIVE_CARD_RATE = 2.0 ;
    private static final double SPECIAL_RATE = 3.0 ;
    private static final double INSURANCE_WIN = 2.0;
    private static final double INSURANCE_LOSE = 0.5;
    private static final double SURRENDER_RATE = 0.5;


    @Autowired
    private PokerDao pokerDao;


    /**
     * 重新洗牌
     * @param session
     */
    public void shuffleCards(HttpSession session) {
        List<Poker> cards = pokerDao.getPokers(Pokers.init(DECK_NUM));

        session.setAttribute("undealCards", cards);
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
        session.setAttribute("usedCards", Lists.newArrayList());
    }


    public Map judgeWin(HttpSession session) {

        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");

        // 例牌胜出
        Map routine = routineWin(balance, bet, session);
        if(routine != null)
            return routine;

        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        Integer playerScore = 0;
        for ( Poker poker : playerCards ){
            playerScore += poker.getValue();
        }
        Integer dealerScore = 0;
        for ( Poker poker : dealerCards ){
            dealerScore += poker.getValue();
        }

        if ( dealerScore > 21 )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)*bet) );
        else if ( playerScore > dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)*bet) );
        else if ( playerScore < dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance - (NORMAL_RATE-1)*bet) );
        else
            return ImmutableMap.of("name", "Draw", "money", balance + bet );
    }


    public Map<String, Serializable> checkRoutine(String role, HttpSession session) {

        List<Poker> pokers;
        if ( role.equals("player") )
            pokers = (List<Poker>) session.getAttribute("playerCards");
        else
            pokers = (List<Poker>) session.getAttribute("dealerCards");

        Map routine = (Map)session.getAttribute(role+"Routine");

        //五龙
        if ( pokers.size() >= 5 && totalScore(role, session) <= 21) {
            routine.put("name", "Five Card");
            routine.put("rate", FIVE_CARD_RATE);
        }
        //特奖
        Integer sevenNum = 0;
        for( Poker poker : pokers ) {
            if ( poker.getValue().equals(7) )
                sevenNum ++;
        }
        if ( sevenNum.equals(3) ) {
            routine.put("name", "Special Win");
            routine.put("rate", SPECIAL_RATE);
        }
        //黑杰克
        if ( isBlackJack(role, session) ) {
            routine.put("name", "Black Jack");
            routine.put("rate", BLACK_JACK_RATE);
        }

        session.setAttribute(role+"Routine", routine);
        return routine;
    }


    public void beforeOpenCard(Integer balance, Integer bet, HttpSession session) {

        session.setAttribute("bet", bet);
        session.setAttribute("balance", balance - bet);
        session.setAttribute("status", "playing");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        String level = session.getAttribute("level") == null ? "beginner" : session.getAttribute("level").toString();

        if ( level.equals("expert") ) {
            shuffleCards(session);
        } else if ( undealCards == null || (usedCards.size()/undealCards.size()) >= 3 ) {
            // 剩余牌量不足1/4时洗牌
            shuffleCards(session);
        }

        initCards(session);
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
    public Integer surrender(HttpSession session) {
        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");
        session.setAttribute("balance", (int)(balance+bet-SURRENDER_RATE*bet) );
        return (Integer) session.getAttribute("balance");
    }

    /**
     * 买保险
     * @param session
     * @return
     */
    public Integer insurance(HttpSession session) {
        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");
        List<Poker> dealer = (List<Poker>) session.getAttribute("dealerCards");
        if(dealer.get(0).getValue()+dealer.get(1).getValue() == 21) {
            session.setAttribute("balance", (int)(balance+INSURANCE_WIN*bet) );
            return (Integer) session.getAttribute("balance");
        } else {
            session.setAttribute("balance", (int)(balance-INSURANCE_LOSE*bet) );
            return (Integer) session.getAttribute("balance");
        }
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
     * 如果有例牌则直接比较例牌
     * @param balance
     * @param bet
     * @param session
     * @return
     */
    private Map routineWin(Integer balance, Integer bet, HttpSession session) {

        Map dealerRoutine = (Map) session.getAttribute("dealerRoutine");
        Map playerRoutine = (Map) session.getAttribute("playerRoutine");

        if( dealerRoutine.get("name") != "Normal" && playerRoutine.get("name") == "Normal" ) {
            return ImmutableMap.of("name", dealerRoutine.get("name"), "money", (int)(balance - ( (Double)dealerRoutine.get("rate")-1)*bet) );
        } else if( dealerRoutine.get("name") == "Normal" && playerRoutine.get("name") != "Normal" ) {
            return ImmutableMap.of("name", playerRoutine.get("name"), "money", (int)(balance + ( (Double)playerRoutine.get("rate")+1)*bet) );
        } else if( dealerRoutine.get("name") != "Normal" && playerRoutine.get("name") != "Normal") {
            Double playerRate = (Double)playerRoutine.get("rate");
            Double dealerRate = (Double)dealerRoutine.get("rate");
            if( playerRate - dealerRate > 0 ) {
                return ImmutableMap.of("name", playerRoutine.get("name"), "money", (int)(balance + (playerRate-dealerRate+1)*bet) );
            } else if( playerRate - dealerRate < 0 ){
                return ImmutableMap.of("name", dealerRoutine.get("name"), "money", (int)(balance - (dealerRate-playerRate-1)*bet) );
            } else {
                return ImmutableMap.of("name", "Draw", "money", balance + bet );
            }
        }

        return null;
    }


    /**
     * 初始化开牌session
     * @param session
     */
    private void initCards(HttpSession session) {

        Map newRoutine = ImmutableMap.of("name", "Normal", "rate", NORMAL_RATE);
        session.setAttribute("playerRoutine", Maps.newHashMap(newRoutine));
        session.setAttribute("dealerRoutine", Maps.newHashMap(newRoutine));

        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");

        List<Poker> dealerCards = Lists.newArrayList();
        dealerCards.add(undealCards.get(0));
        dealerCards.add(undealCards.get(1));

        List<Poker> playerCards = Lists.newArrayList();
        playerCards.add(undealCards.get(2));
        playerCards.add(undealCards.get(3));

        for ( int i=0; i<4; i++ )
            usedCards.add(undealCards.get(i));

        for ( int i=0; i<4; i++ )
            undealCards.remove(0);

        session.setAttribute("dealerCards", dealerCards);
        session.setAttribute("playerCards", playerCards);

    }


    /**
     * 转换A
     * @param role
     * @param session
     */
    private void convertACard(String role, HttpSession session){

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

}
