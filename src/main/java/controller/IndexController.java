package controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
public class IndexController{


    //初始余额
    private static final int BALANCE = 1000;

    private PokerService pokerService;

    private List<Poker> undealCards;
    private List<Poker> usedCards;
    private List<Poker> playerCards;
    private List<Poker> dealerCards;
    private Map playerRoutine;
    private Map dealerRoutine;

    public IndexController() {}

    public IndexController(PokerService pokerService) {
        this.pokerService = pokerService;
    }


    private void getSession(HttpSession session) {
        undealCards = (List<Poker>) firstNonNull(session.getAttribute("undealCards"), Lists.newArrayList());
        usedCards = (List<Poker>) firstNonNull(session.getAttribute("usedCards"), Lists.newArrayList());
        playerCards = (List<Poker>) firstNonNull(session.getAttribute("playererCards"), Lists.newArrayList());
        dealerCards = (List<Poker>) firstNonNull(session.getAttribute("dealerCards"), Lists.newArrayList());
        playerRoutine = (Map) firstNonNull(session.getAttribute("PlayerRoutine"), Maps.newHashMap());
        dealerRoutine = (Map) firstNonNull(session.getAttribute("dealerRoutine"), Maps.newHashMap());
    }

    private void setSession(HttpSession session) {
        session.setAttribute("undealCards", undealCards);
        session.setAttribute("usedCards", usedCards);
        session.setAttribute("playerCards", playerCards);
        session.setAttribute("dealerCards", dealerCards);
        session.setAttribute("playerRoutine", playerRoutine);
        session.setAttribute("dealerRoutine", dealerRoutine);
    }


    @RequestMapping(value="", method = RequestMethod.GET)
	public String homePage() {
        return "index";
	}

    @RequestMapping(value="setLevel", method = RequestMethod.POST)
    @ResponseBody
    public Map setLevel(@RequestParam("level") String level, HttpSession session) {
        getSession(session);
        session.setAttribute("level", level);
        session.setAttribute("balance", BALANCE);
        setSession(session);
        return AjaxReturn.success();
    }


    @RequestMapping(value="/shuffle", method = RequestMethod.POST)
    @ResponseBody
    public Map shuffle(HttpSession session) {
        getSession(session);
        String level = (String) session.getAttribute("level");
        List<Poker> undealCards = (List<Poker>) session.getAttribute("undealCards");
        List<Poker> usedCards = (List<Poker>) session.getAttribute("usedCards");
        if( StringUtils.equals(level, "beginner") ){
            pokerService.shuffleCards(undealCards, usedCards);
            setSession(session);
            return AjaxReturn.success();
        }
        return AjaxReturn.fail();
    }


    @RequestMapping(value="/openCards", method = RequestMethod.POST)
    @ResponseBody
    public Map openCards(@RequestParam("bet") Integer bet, HttpSession session) {

        getSession(session);
        Integer balance = (Integer) session.getAttribute("balance");
        if( balance == null || balance < bet )
            return AjaxReturn.fail("balance not enough");
        session.setAttribute("bet", bet);
        session.setAttribute("balance", balance - bet);

        Map playerRoutine = (Map) session.getAttribute("playerRoutine");
        Map dealerRoutine = (Map) session.getAttribute("dealerRoutine");
        String level = firstNonNull((String) session.getAttribute("level"), "beginner");
        pokerService.checkShuffle(level, undealCards, usedCards);
        pokerService.initCards(undealCards, usedCards, dealerCards, playerCards);
        pokerService.initRoutine(playerRoutine, dealerRoutine);
        session.setAttribute("status", "playing");

        //隐藏庄家暗牌
        dealerCards.set(0, new Poker(0, 0, "0.jpg"));
        setSession(session);
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

        getSession(session);
        Map playerRoutine = (Map) session.getAttribute("playerRoutine");
        Map dealerRoutine = (Map) session.getAttribute("dealerRoutine");

        if( undealCards.size() == 0 )
            return AjaxReturn.fail("timeout");

        if( role.equals("player") && !session.getAttribute("status").equals("playing") )
            return AjaxReturn.fail("status error");

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
        setSession(session);

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
        return AjaxReturn.success(ImmutableMap.of("routine", dealerRoutine));
    }


    @RequestMapping(value="/double", method = RequestMethod.POST)
    @ResponseBody
    public Map doubleCards(HttpSession session) {

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

        if ( playerCards.size() == 2 && dealerCards.get(1).getValue() != 11 )
            return AjaxReturn.success(pokerService.surrender((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance")));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/insurance", method = RequestMethod.POST)
    @ResponseBody
    public Map insurance(HttpSession session) {

        if ( playerCards.size() == 2 && dealerCards.get(1).getValue() == 11 )
            return AjaxReturn.success(pokerService.insurance((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance"), (List<Poker>) session.getAttribute("dealerCards")));

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/judge", method = RequestMethod.POST)
    @ResponseBody
    public Map judge(HttpSession session){

        getSession(session);
        Map win = pokerService.judgeWin((Integer) session.getAttribute("bet"), (Integer) session.getAttribute("balance"), dealerCards, playerCards, dealerRoutine, playerRoutine);
        Map data = ImmutableMap.of("name", win.get("name"), "money", win.get("money"), "dealer", dealerCards );
        session.setAttribute("balance", win.get("money"));
        pokerService.clearCards(playerCards, dealerCards );
        setSession(session);

        return AjaxReturn.success(data);
    }


    @RequestMapping(value="/blackJack", method = RequestMethod.POST)
    @ResponseBody
    public Map blackJack(HttpSession session) {

        getSession(session);
        if( !playerRoutine.get("name").equals("Black Jack") )
            return AjaxReturn.fail();

        Integer bet = (Integer) session.getAttribute("bet");
        Integer balance = (Integer) session.getAttribute("balance");

        balance = (int)(balance+(1+1.5) * bet);
        session.setAttribute("balance", balance);
        Map data = ImmutableMap.of("name", "Black Jack", "money", balance);
        setSession(session);
        return AjaxReturn.success(data);
    }


}
