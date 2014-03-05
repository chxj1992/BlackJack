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
    private static final int DEFAULT_BALANCE = 1000;
    private static final int DEFAULT_BET = 10;

    private PokerService pokerService;

    private String level;
    private Integer bet;
    private Integer balance;
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
        level = (String) firstNonNull(session.getAttribute("level"), "beginner");
        bet = (Integer) firstNonNull(session.getAttribute("bet"), DEFAULT_BET);
        balance = (Integer) firstNonNull(session.getAttribute("balance"), DEFAULT_BALANCE);
        undealCards = (List<Poker>) firstNonNull(session.getAttribute("undealCards"), Lists.newArrayList());
        usedCards = (List<Poker>) firstNonNull(session.getAttribute("usedCards"), Lists.newArrayList());
        playerCards = (List<Poker>) firstNonNull(session.getAttribute("playerCards"), Lists.newArrayList());
        dealerCards = (List<Poker>) firstNonNull(session.getAttribute("dealerCards"), Lists.newArrayList());
        playerRoutine = (Map) firstNonNull(session.getAttribute("playerRoutine"), Maps.newHashMap());
        dealerRoutine = (Map) firstNonNull(session.getAttribute("dealerRoutine"), Maps.newHashMap());
    }

    private void setSession(HttpSession session) {
        session.setAttribute("level", level);
        session.setAttribute("bet", bet);
        session.setAttribute("balance", balance);
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
        this.level = level;
        balance = DEFAULT_BALANCE;
        setSession(session);
        return AjaxReturn.success();
    }


    @RequestMapping(value="/shuffle", method = RequestMethod.POST)
    @ResponseBody
    public Map shuffle(HttpSession session) {
        getSession(session);
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
        if( balance < bet )
            return AjaxReturn.fail("balance not enough");

        this.bet = bet;
        balance = balance - bet;
        pokerService.checkShuffle(level, undealCards, usedCards);
        pokerService.initCards(undealCards, usedCards, dealerCards, playerCards);
        pokerService.initRoutine(playerRoutine, dealerRoutine);
        setSession(session);
        session.setAttribute("status", "playing");

        //隐藏庄家暗牌
        List<Poker> dealerCardsCopy = Lists.newArrayList(dealerCards);
        dealerCardsCopy.set(0, new Poker(0, 0, "0.jpg"));
        pokerService.checkRoutine("dealer", playerCards, dealerCards, playerRoutine, dealerRoutine);
        Map routine = pokerService.checkRoutine("player", playerCards, dealerCards, playerRoutine, dealerRoutine);
        ImmutableMap data = ImmutableMap.of(
                "dealer", ImmutableMap.of("cards", dealerCardsCopy),
                "player", ImmutableMap.of("cards", playerCards, "routine", routine)) ;

        return AjaxReturn.success(data);
    }


    @RequestMapping(value="/hit", method = RequestMethod.POST)
    @ResponseBody
    public Map hitCards(@RequestParam("role") String role, HttpSession session) {

        getSession(session);
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
        getSession(session);
        session.setAttribute("status","stand");
        return AjaxReturn.success(ImmutableMap.of("routine", dealerRoutine));
    }


    @RequestMapping(value="/double", method = RequestMethod.POST)
    @ResponseBody
    public Map doubleCards(HttpSession session) {
        getSession(session);
        if( playerCards.size() > 2 )
            return AjaxReturn.fail();

        balance = balance - bet;
        bet = bet * 2;
        setSession(session);

        return AjaxReturn.success();
    }

    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    @ResponseBody
    public Map surrender(HttpSession session) {
        getSession(session);
        if ( playerCards.size() == 2 && dealerCards.get(1).getValue() != 11 ) {
            balance = pokerService.surrender(bet, balance);
            setSession(session);
            return AjaxReturn.success(balance);
        }

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/insurance", method = RequestMethod.POST)
    @ResponseBody
    public Map insurance(HttpSession session) {
        getSession(session);
        if ( playerCards.size() == 2 && dealerCards.get(1).getValue() == 11 ){
            balance = pokerService.insurance(bet, balance, dealerCards);
            setSession(session);
            return AjaxReturn.success(balance);
        }

        return AjaxReturn.fail();
    }


    @RequestMapping(value="/judge", method = RequestMethod.POST)
    @ResponseBody
    public Map judge(HttpSession session){

        getSession(session);
        Map win = pokerService.judgeWin(bet, balance, dealerCards, playerCards, dealerRoutine, playerRoutine);
        Map data = ImmutableMap.of("name", win.get("name"), "money", win.get("money"), "dealer", dealerCards);
        balance = (Integer) win.get("money");
        setSession(session);

        return AjaxReturn.success(data);
    }


    @RequestMapping(value="/blackJack", method = RequestMethod.POST)
    @ResponseBody
    public Map blackJack(HttpSession session) {

        getSession(session);
        if( !playerRoutine.get("name").equals("Black Jack") )
            return AjaxReturn.fail();

        balance = (int)(balance+(1+1.5) * bet);
        Map data = ImmutableMap.of("name", "Black Jack", "money", balance);
        setSession(session);

        return AjaxReturn.success(data);
    }


}
