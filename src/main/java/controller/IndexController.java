package controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.PokerService;
import utils.AjaxReturn;

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

    //初始余额
    private static final int BALANCE = 1000;

    @Autowired
    private PokerService pokerService;

	@RequestMapping(value="", method = RequestMethod.GET)
	public String homePage() {
        return "index";
	}

    @RequestMapping(value="setLevel", method = RequestMethod.POST)
    @ResponseBody
    public Map setLevel(@RequestParam("level") String level,HttpSession session) {
        pokerService.clearAll(session);
        session.setAttribute("level", level);
        session.setAttribute("balance", BALANCE);
        return AjaxReturn.success();
    }

    @RequestMapping(value="/openCards", method = RequestMethod.POST)
    @ResponseBody
    public Map openCards(@RequestParam("bet") Integer bet, HttpSession session) {

        Integer balance = (Integer) session.getAttribute("balance");
        if( balance == null || balance < bet )
            return AjaxReturn.fail("balance not enough");

        pokerService.beforeOpenCard(balance, bet, session);

        List<Poker> dealerCards = Lists.newArrayList((List < Poker >)session.getAttribute("dealerCards"));
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        //隐藏庄家暗牌
        dealerCards.set(0, new Poker(0, 0, "0.jpg"));
        pokerService.checkRoutine("dealer", session);
        Map routine = pokerService.checkRoutine("player", session);
        ImmutableMap data = ImmutableMap.of(
                "dealer", ImmutableMap.of("cards", dealerCards),
                "player", ImmutableMap.of("cards", playerCards,
                "routine", routine)) ;

        return AjaxReturn.success(data);
    }


    @RequestMapping(value="/hit", method = RequestMethod.POST)
    @ResponseBody
    public Map hitCards(@RequestParam("role") String role, HttpSession session) {

        if( session.getAttribute("undealCards") == null )
            return AjaxReturn.fail("timeout");

        if( role.equals("player") && !session.getAttribute("status").equals("playing") )
            return AjaxReturn.fail("status error");

        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>) session.getAttribute("dealerCards");

        Poker poker = undealCards.get(0);
        undealCards.remove(0);

        if ( role.equals("player") )
            playerCards.add(poker);
        else
            dealerCards.add(poker);

        Integer totalScore = pokerService.totalScore(role, session);

        if ( role.equals("player") )
            poker = playerCards.get(playerCards.size()-1);
        else
            poker = dealerCards.get(dealerCards.size()-1);

        usedCards.add(poker);

        Map routine = pokerService.checkRoutine(role, session);
        if ( role.equals("dealer") && totalScore>16 && totalScore<=21 )
            return AjaxReturn.fail( ImmutableMap.of("info", "Stand", "poker", poker, "routine", routine));

        if (totalScore>21) {
            session.setAttribute("status", "Bust");
            return AjaxReturn.fail(ImmutableMap.of("info", "Bust", "poker", poker, "routine", routine));
        } else {
            return AjaxReturn.success(ImmutableMap.of("poker", poker, "routine", routine));
        }
    }


    @RequestMapping(value="/stand", method = RequestMethod.POST)
    @ResponseBody
    public Map standCards(HttpSession session) {
        session.setAttribute("status","stand");
        Map routine = (Map) session.getAttribute("dealerRoutine");
        return AjaxReturn.success(ImmutableMap.of("routine", routine) );
    }


    @RequestMapping(value="/double", method = RequestMethod.POST)
    @ResponseBody
    public Map doubleCards(HttpSession session) {

        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        if( playerCards.size() > 2 )
            return AjaxReturn.fail();

        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");
        session.setAttribute("balance", balance-bet);
        session.setAttribute("bet", bet*2);

        return AjaxReturn.success();
    }

    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    @ResponseBody
    public Map surrender(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player.size() == 2 && dealer.get(1).getValue() != 11 )
            return AjaxReturn.success(pokerService.surrender(session));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/insurance", method = RequestMethod.POST)
    @ResponseBody
    public Map insurance(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player.size() == 2 && dealer.get(1).getValue() == 11 )
            return AjaxReturn.success(pokerService.insurance(session));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/judge", method = RequestMethod.POST)
    @ResponseBody
    public Map judge(HttpSession session){

        List<Poker> dealerCards = (List<Poker>)session.getAttribute("dealerCards");
        Map win = pokerService.judgeWin(session);
        Map data = ImmutableMap.of("name", win.get("name"), "money", win.get("money"), "dealer", dealerCards );
        session.setAttribute("balance", win.get("money"));
        pokerService.clearAll(session);

        return AjaxReturn.success(data);
    }



    /*************************************************
     * 黑杰克先收
     ***********************************************/
    @RequestMapping(value="/blackJack", method = RequestMethod.POST)
    @ResponseBody
    public Map blackJack(HttpSession session) {

        Map playerRoutine = (Map)session.getAttribute("playerRoutine");
        if( !playerRoutine.get("name").equals("Black Jack") )
            return AjaxReturn.fail();

        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");

        balance = (int)(balance+(1+1.5) * bet);
        session.setAttribute("balance", balance);
        Map data = ImmutableMap.of("name", "Black Jack", "money", balance);
        return AjaxReturn.success(data);
    }




}
