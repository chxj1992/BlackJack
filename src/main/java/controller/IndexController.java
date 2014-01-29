package controller;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.AjaxReturn;
import utils.Poker;

import javax.servlet.http.HttpServletRequest;
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

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String homePage() {
        return "index";
	}

    @RequestMapping(value="/openCards", method = RequestMethod.GET)
    @ResponseBody
    public Map openCards(@RequestParam("level") String level,HttpSession session) {

        List<Integer> undealCards = (List<Integer>) session.getAttribute("undealCards");

        if ( level == "expert" ) {
            this.initCards(session);
        } else if ( undealCards == null || undealCards.size() < 10 ) {
            this.initCards(session);
        }
        this.initOpenCards(session);

        return AjaxReturn.success("test",session.getAttribute("undealCards"));
    }

    @RequestMapping(value="/hit", method = RequestMethod.POST)
    public Map hitCards() {

        return AjaxReturn.success();
    }

    @RequestMapping(value="/stand", method = RequestMethod.POST)
    public Map standCards() {

        return AjaxReturn.success();
    }

    @RequestMapping(value="/double", method = RequestMethod.POST)
    public Map doubleCards() {

        return AjaxReturn.success();
    }

    @RequestMapping(value="/surrender", method = RequestMethod.POST)
    public Map surrender() {

        return AjaxReturn.success();
    }


    private void initCards(HttpSession session) {
        List<Integer> cards = Poker.init();
        session.setAttribute("undealCards", cards);
        session.setAttribute("dealerCards", Lists.newArrayList());
        session.setAttribute("playerCards", Lists.newArrayList());
    }


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

        session.setAttribute("undealCards",undealCards);
    }




}
