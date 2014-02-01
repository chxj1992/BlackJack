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
    public Map openCards(HttpSession session) {

        session.setAttribute("status","playing");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        String level = session.getAttribute("level") == null ? "beginner" : session.getAttribute("level").toString();

        if ( level.equals("expert") ) {
            pokerService.initCards(session);
        } else if ( undealCards == null || undealCards.size() < 10 ) {
            pokerService.initCards(session);
        }
        pokerService.initOpenCards(session);

        List<Poker> dealerCards = Lists.newArrayList((List < Poker >)session.getAttribute("dealerCards"));
        //隐藏庄家暗牌
        dealerCards.set(0, new Poker(0, 0, "0.jpg"));

        ImmutableMap data = ImmutableMap.of(
                "dealerCards", dealerCards,
                "playerCards", (List<Poker>) session.getAttribute("playerCards"));

        return AjaxReturn.success("data", data);
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

        if ( role.equals("dealer") && totalScore>16 && totalScore<=21 )
            return AjaxReturn.fail("stop", poker);

        if (totalScore>21) {
            session.setAttribute("status", "bust");
            return AjaxReturn.fail("bust", poker);
        } else {
            return AjaxReturn.success("success", poker);
        }
    }


    @RequestMapping(value="/stand", method = RequestMethod.POST)
    @ResponseBody
    public Map standCards(HttpSession session) {
        session.setAttribute("status","stand");
        return AjaxReturn.success();
    }

    @RequestMapping(value="/double", method = RequestMethod.POST)
    @ResponseBody
    public Map doubleCards(HttpSession session) {

        session.setAttribute("status","double");
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        if( playerCards.size() == 2 )
            return hitCards("player", session);

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/judge", method = RequestMethod.POST)
    @ResponseBody
    public Map judge(HttpSession session){

        if( session.getAttribute("status").equals("playing") )
            return AjaxReturn.fail("status error");

        List<Poker> dealerCards = (List<Poker>)session.getAttribute("dealerCards");
        Map win = pokerService.judgeWinner(session);
        Map data = ImmutableMap.of("win", win.get("name"), "rate", win.get("rate"), "winner", win.get("winner"), "dealer", dealerCards );
        pokerService.clearAll(session);

        return AjaxReturn.success("success", data);
    }


    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    @ResponseBody
    public Map surrender(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player.size() == 2 && dealer.get(1).getValue() != 11 ) {
            pokerService.surrender(session);
            return AjaxReturn.success();
        }

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/insurance", method = RequestMethod.POST)
    @ResponseBody
    public Map insurance(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player.size() == 2 && (dealer.get(0).getValue()+dealer.get(1).getValue()) == 21 )
            return AjaxReturn.success(pokerService.insurance(session).toString());

        return AjaxReturn.fail();
    }



    @RequestMapping(value="/five", method = RequestMethod.POST)
    @ResponseBody
    public Map fiveCardWin(HttpSession session) {

        List<Poker> pokers = (List<Poker>)session.getAttribute("playerCards");
        if ( pokers.size() == 5 ) {
            return AjaxReturn.success("five card", ImmutableMap.of("rate", 0.5) );
        }

        return AjaxReturn.fail();
    }



}
