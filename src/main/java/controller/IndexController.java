package controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dao.PokerDao;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.AjaxReturn;
import utils.Pokers;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 扑克操作
 * Author: chen
 * DateTime: 1/7/14 10:34 AM
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private PokerDao pokerDao;

	@RequestMapping(value="", method = RequestMethod.GET)
	public String homePage() {
        return "index";
	}

    @RequestMapping(value="setLevel", method = RequestMethod.POST)
    @ResponseBody
    public Map setLevel(@RequestParam("level") String level,HttpSession session) {
        session.setAttribute("level", level);
        return AjaxReturn.success();
    }

    @RequestMapping(value="/openCards", method = RequestMethod.POST)
    @ResponseBody
    public Map openCards(HttpSession session) {

        List<Integer> undealCards = (List<Integer>) session.getAttribute("undealCards");
        String level = session.getAttribute("level") == null ? "beginner" : session.getAttribute("level").toString();

        if ( level.equals("expert") ) {
            this.initCards(session);
        } else if ( undealCards == null || undealCards.size() < 10 ) {
            this.initCards(session);
        }
        this.initOpenCards(session);

        List<Poker> dealerCards = pokerDao.getPokers( (List<Integer>)session.getAttribute("dealerCards") );
        List<Poker> playerCards = pokerDao.getPokers((List<Integer>) session.getAttribute("playerCards"));
        //隐藏庄家暗牌
        dealerCards.set(0, new Poker(0, 0, "0.jpg") );

        ImmutableMap data = ImmutableMap.of(
                "dealerCards", dealerCards,
                "playerCards", playerCards);

        return AjaxReturn.success("data", data);
    }

    @RequestMapping(value="/hit", method = RequestMethod.POST)
    @ResponseBody
    public Map hitCards(HttpSession session) {

        if( session.getAttribute("undealCards") == null )
            return AjaxReturn.fail("timeout");

        List<Integer> undealCards = (List<Integer>) session.getAttribute("undealCards");
        List<Integer> playerCards = (List<Integer>) session.getAttribute("playerCards");
        Integer pokerId = undealCards.get(0);
        undealCards.remove(0);
        playerCards.add(pokerId);

        session.setAttribute("undealCards", undealCards);
        session.setAttribute("playerCards", playerCards);

        Poker poker = pokerDao.getByPokerId(pokerId);
        if ( this.isBust("player",session) )
            return AjaxReturn.fail("bust", poker);
        else
            return AjaxReturn.success("success", poker);
    }


    @RequestMapping(value="/stand", method = RequestMethod.POST)
    @ResponseBody
    public Map standCards() {

        return AjaxReturn.success();
    }

    @RequestMapping(value="/double", method = RequestMethod.POST)
    @ResponseBody
    public Map doubleCards() {

        return AjaxReturn.success();
    }

    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    @ResponseBody
    public Map surrender() {

        return AjaxReturn.success();
    }


    /**
     * 重新洗牌
     * @param session
     */
    private void initCards(HttpSession session) {
        List<Integer> cards = Pokers.init();
        session.setAttribute("undealCards", cards);
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
    }


    /**
     * 初始化开牌session
     * @param session
     */
    private void initOpenCards(HttpSession session) {

        List<Integer> undealCards = (List<Integer>) session.getAttribute("undealCards");

        List<Integer> dealerCards = Lists.newArrayList();
        dealerCards.add(undealCards.get(0));
        dealerCards.add(undealCards.get(1));

        List<Integer> playerCards = Lists.newArrayList();
        playerCards.add(undealCards.get(2));
        playerCards.add(undealCards.get(3));

        for ( int i=0; i<4; i++ )
            undealCards.remove(0);

        session.setAttribute("undealCards", undealCards);
        session.setAttribute("dealerCards", dealerCards);
        session.setAttribute("playerCards", playerCards);
    }


    /**
     * 是否爆牌
     * @param role
     * @param session
     * @return
     */
    private Boolean isBust(String role, HttpSession session) {
        List<Integer> pokerIds;
        if ( role.equals("player") )
            pokerIds = (List<Integer>) session.getAttribute("playerCards");
        else
            pokerIds = (List<Integer>) session.getAttribute("dealerCards");

        Integer total = 0;
        for ( Integer pokerId : pokerIds )
            total += pokerDao.getByPokerId(pokerId).getValue();

        if ( total > 21 )
            return true;
        else
            return false;
    }




}
