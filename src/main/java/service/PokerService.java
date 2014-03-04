package service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.swing.internal.plaf.synth.resources.synth_zh_TW;
import dao.PokerDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import sun.print.resources.serviceui_pt_BR;
import utils.Pokers;

import java.io.Serializable;
import java.util.ArrayList;
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


    public void shuffleCards(List<Poker> undealCards, List<Poker> usedCards) {
        List<Poker> cards = pokerDao.getPokers(Pokers.init(DECK_NUM));

        undealCards = cards;
        usedCards = Lists.newArrayList();
    }


    public Map judgeWin(Integer bet, Integer balance, List<Poker> dealerCards, List<Poker> playerCards, Map dealerRoutine, Map playerRoutine) {

        // 例牌胜出
        Map routine = routineWin(balance, bet, dealerRoutine, playerRoutine);
        if(routine != null)
            return routine;

        Integer playerScore = 0;
        for ( Poker poker : playerCards){
            playerScore += poker.getValue();
        }
        Integer dealerScore = 0;
        for ( Poker poker : dealerCards){
            dealerScore += poker.getValue();
        }

        if ( dealerScore > 21 )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)* bet) );
        else if ( playerScore > dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance + (NORMAL_RATE+1)* bet) );
        else if ( playerScore < dealerScore )
            return ImmutableMap.of("name", "Normal", "money", (int)(balance - (NORMAL_RATE-1)* bet) );
        else
            return ImmutableMap.of("name", "Draw", "money", balance + bet);
    }


    public Map<String, Serializable> checkRoutine(String role, List<Poker> playerCards, List<Poker> dealerCards, Map playerRoutine, Map dealerRoutine) {

        List<Poker> pokers;
        Map routine;
        if ( role.equals("player") ) {
            pokers = playerCards;
            routine = playerRoutine;
        } else {
            routine = dealerRoutine;
            pokers = dealerCards;
        }

        //五龙
        if ( pokers.size() >= 5 && totalScore(role, playerCards, dealerCards) <= 21) {
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
        if ( isBlackJack(role, playerCards, dealerCards) ) {
            routine.put("name", "Black Jack");
            routine.put("rate", BLACK_JACK_RATE);
        }

        return routine;
    }


    public void checkShuffle(String level, List<Poker> undealCards, List<Poker> usedCards) {
        if ( level.equals("expert") )
            shuffleCards(undealCards, usedCards);
        else if ( undealCards == null || (usedCards.size()/ undealCards.size()) >= 3 )
            // 剩余牌量不足1/4时洗牌
            shuffleCards(undealCards, usedCards);
    }


    public void initCards(List<Poker> undealCards, List<Poker> usedCards, List<Poker> dealerCards, List<Poker> playerCards) {

        clearCards(playerCards, dealerCards);
        dealerCards.add(undealCards.get(0));
        dealerCards.add(undealCards.get(1));
        playerCards.add(undealCards.get(2));
        playerCards.add(undealCards.get(3));

        for ( int i=0; i<4; i++ )
            usedCards.add(undealCards.get(i));

        for ( int i=0; i<4; i++ )
            undealCards.remove(0);
    }


    public void initRoutine(Map playerRoutine, Map dealerRoutine){
        Map newRoutine = ImmutableMap.of("name", "Normal", "rate", NORMAL_RATE);
        playerRoutine = Maps.newHashMap(newRoutine);
        dealerRoutine = Maps.newHashMap(newRoutine);
    }



    public void clearCards(List<Poker> playerCards, List<Poker> dealerCards){
        playerCards.clear();
        dealerCards.clear();
    }


    /**
     * 投降
     *
     * @param bet
     * @param balance
     * @return
     */
    public Integer surrender(Integer bet, Integer balance) {
        return (int)(balance + bet -SURRENDER_RATE* bet);
    }

    /**
     * 买保险
     *
     * @param bet
     * @param balance
     * @param dealer
     * @return
     */
    public Integer insurance(Integer bet, Integer balance, List<Poker> dealer) {
        if(dealer.get(0).getValue()+ dealer.get(1).getValue() == 21)
            return (int)(balance +INSURANCE_WIN* bet);
        else
            return (int)(balance -INSURANCE_LOSE* bet);
    }

    public Boolean isBlackJack(String role, List<Poker> playerCards, List<Poker> dealerCards) {
        List<Poker> pokers;
        if( role.equals("player") )
             pokers = playerCards;
        else
            pokers = dealerCards;

        if ( pokers.size() == 2 && (pokers.get(0).getValue()+pokers.get(1).getValue()) == 21 )
            return true;

        return false;
    }


    /**
     * 获取当前总分,如果爆牌则尝试转换A
     *
     * @param role
     * @param playerCards
     * @param dealerCards
     * @return
     */
    public Integer totalScore(String role, List<Poker> playerCards, List<Poker> dealerCards) {

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
            convertACard(role, playerCards, dealerCards);
        }

        return total;
    }



    /**
     * 如果有例牌则直接比较例牌
     * @param balance
     * @param bet
     * @param dealerRoutine
     * @param playerRoutine
     * @return
     */
    private Map routineWin(Integer balance, Integer bet, Map dealerRoutine, Map playerRoutine) {

        if( dealerRoutine.get("name") != "Normal" && playerRoutine.get("name") == "Normal" ) {
            return ImmutableMap.of("name", dealerRoutine.get("name"), "money", (int)(balance - ( (Double) dealerRoutine.get("rate")-1)*bet) );
        } else if( dealerRoutine.get("name") == "Normal" && playerRoutine.get("name") != "Normal" ) {
            return ImmutableMap.of("name", playerRoutine.get("name"), "money", (int)(balance + ( (Double) playerRoutine.get("rate")+1)*bet) );
        } else if( dealerRoutine.get("name") != "Normal" && playerRoutine.get("name") != "Normal") {
            Double playerRate = (Double) playerRoutine.get("rate");
            Double dealerRate = (Double) dealerRoutine.get("rate");
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
     * 转换A
     * @param role
     * @param playerCards
     * @param dealerCards
     */
    private void convertACard(String role, List<Poker> playerCards, List<Poker> dealerCards){

        List<Poker> pokers;
        if (role.equals("player"))
            pokers = playerCards;
        else
            pokers = dealerCards;

        for ( Poker poker : pokers ) {
            if( poker.getValue().equals(11) ) {
                poker.setValue(1);
                break;
            }
        }
    }

}
