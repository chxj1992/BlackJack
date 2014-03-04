package controller;

import com.google.common.collect.ImmutableMap;
import model.Poker;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.PokerService;
import utils.AjaxReturn;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.firstNonNull;

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

    private PokerService pokerService;

    public IndexController() {}

    public IndexController(PokerService pokerService) {
        this.pokerService = pokerService;
    }


    @RequestMapping(value="", method = RequestMethod.GET)
	public String homePage() {
        return "index";
	}

    @RequestMapping(value="setLevel", method = RequestMethod.POST)
    @ResponseBody
    public Map setLevel(@RequestParam("level") String level, HttpSession session) {
        session.setAttribute("level", level);
        session.setAttribute("balance", BALANCE);
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        pokerService.shuffleCards(undealCards, usedCards);
        return AjaxReturn.success();
    }


    @RequestMapping(value="/shuffle", method = RequestMethod.POST)
    @ResponseBody
    public Map shuffle(HttpSession session) {
        String level = (String) session.getAttribute("level");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        if( StringUtils.equals(level, "beginner") ){
            pokerService.shuffleCards(undealCards, usedCards);
            return AjaxReturn.success();
        }
        return AjaxReturn.fail();
    }


    @RequestMapping(value="/openCards", method = RequestMethod.POST)
    @ResponseBody
    public Map openCards(@RequestParam("bet") Integer bet, HttpSession session) {

        Integer balance = (Integer) session.getAttribute("balance");
        if( balance == null || balance < bet )
            return AjaxReturn.fail("balance not enough");
        session.setAttribute("bet", bet);
        session.setAttribute("balance", balance - bet);

        List<Poker> dealerCards = (List < Poker >)session.getAttribute("dealerCards");
        List<Poker> playerCards = (List<Poker>) session.getAttribute("playerCards");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        Map playerRoutine = (Map) session.getAttribute("playerRoutine");
        Map dealerRoutine = (Map) session.getAttribute("dealerRoutine");
        String level = firstNonNull((String) session.getAttribute("level"), "beginner");
        pokerService.checkShuffle(level, undealCards, usedCards);
        pokerService.initCards(undealCards, usedCards, dealerCards, playerCards);
        pokerService.initRoutine(playerRoutine, dealerRoutine);
        session.setAttribute("status", "playing");

        //隐藏庄家暗牌
        dealerCards.set(0, new Poker(0, 0, "0.jpg"));
        pokerService.checkRoutine("dealer", playerCards, dealerCards, playerRoutine, dealerRoutine);
        Map routine = pokerService.checkRoutine("player", playerCards, dealerCards, playerRoutine, dealerRoutine);
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
        Map playerRoutine = (Map) session.getAttribute("playerRoutine");
        Map dealerRoutine = (Map) session.getAttribute("dealerRoutine");

        Poker poker = undealCards.get(0);
        undealCards.remove(0);

        if ( role.equals("player") )
            playerCards.add(poker);
        else
            dealerCards.add(poker);

        Integer totalScore = pokerService.totalScore(role, playerCards, dealerCards);

        if ( role.equals("player") )
            poker = playerCards.get(playerCards.size()-1);
        else
            poker = dealerCards.get(dealerCards.size()-1);

        usedCards.add(poker);

        Map routine = pokerService.checkRoutine(role, playerCards, dealerCards, playerRoutine, dealerRoutine);
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
        return AjaxReturn.success(ImmutableMap.of("routine", routine));
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
        session.setAttribute("bet", bet * 2);

        return AjaxReturn.success();
    }

    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    @ResponseBody
    public Map surrender(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player != null && player.size() == 2 && dealer.get(1).getValue() != 11 )
            return AjaxReturn.success(pokerService.surrender((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance")));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/insurance", method = RequestMethod.POST)
    @ResponseBody
    public Map insurance(HttpSession session) {

        List<Poker> player = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealer = (List<Poker>)session.getAttribute("dealerCards");
        if ( player.size() == 2 && dealer.get(1).getValue() == 11 )
            return AjaxReturn.success(pokerService.insurance((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance"), (List<Poker>) session.getAttribute("dealerCards")));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/judge", method = RequestMethod.POST)
    @ResponseBody
    public Map judge(HttpSession session){

        List<Poker> playerCards = (List<Poker>)session.getAttribute("playerCards");
        List<Poker> dealerCards = (List<Poker>)session.getAttribute("dealerCards");
        Map win = pokerService.judgeWin((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance"), (List<Poker>) session.getAttribute("dealerCards"), (List<Poker>) session.getAttribute("playerCards"), null, null);
        Map data = ImmutableMap.of("name", win.get("name"), "money", win.get("money"), "dealer", dealerCards );
        session.setAttribute("balance", win.get("money"));
        pokerService.clearCards(playerCards, dealerCards );

        return AjaxReturn.success(data);
    }


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
