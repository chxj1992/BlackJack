package service;

import com.google.common.collect.Lists;
import dao.PokerDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import utils.Pokers;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * add_your_description_here
 * Author: chen
 * DateTime: 1/31/14 11:28 PM
 */
public class PokerService {


    @Autowired
    private PokerDao pokerDao;

    public String judgeWinner(HttpSession session) {
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");
        return "player";
    }


    public double judgeBetRate(HttpSession session) {
        return 1.0;
    }


    /**
     * 重新洗牌
     * @param session
     */
    public void initCards(HttpSession session) {
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
    public void initOpenCards(HttpSession session) {

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

        session.setAttribute("undealCards", undealCards);
        session.setAttribute("dealerCards", dealerCards);
        session.setAttribute("playerCards", playerCards);
        session.setAttribute("usedCards", usedCards);
    }


    /**
     * 是否爆牌,如果爆牌则尝试转换A
     * @param role
     * @param session
     * @return
     */
    public Boolean isBust(String role, HttpSession session) {

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

        if ( total > 21 )
            return true;
        else
            return false;
    }


    /**
     * 转换A
     * @param role
     * @param session
     */
    public void convertACard(String role, HttpSession session){

        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");
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

        if (role.equals("player"))
            session.setAttribute("playerCards", pokers);
        else
            session.setAttribute("dealerCards", pokers);

    }


    /**
     * 清牌
     * @param session
     */
    public void clearAll(HttpSession session){
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
        session.setAttribute("usedCards", Lists.newArrayList());
    }


}
