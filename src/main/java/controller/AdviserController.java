package controller;

import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.AdviserService;
import utils.AjaxReturn;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 建议模块
 * Author: chen
 * DateTime: 1/29/14 7:44 PM
 */

@Controller
@RequestMapping("/adviser")
public class AdviserController {


    private AdviserService adviserService;

    public AdviserController() {}

    public AdviserController(AdviserService adviserService) {
        this.adviserService = adviserService;
    }


	@RequestMapping(value="bet", method = RequestMethod.POST)
    @ResponseBody
	public Map setBet(HttpSession session) {
        String advisor = adviserService.getBetAdvise((List<Poker>) session.getAttribute("usedCards"), (List<Poker>) session.getAttribute("undealCards"), (String) session.getAttribute("level"));
        return AjaxReturn.success(advisor);
	}


	@RequestMapping(value="action", method = RequestMethod.POST)
    @ResponseBody
	public Map actionAdvisor(HttpSession session) {
        String advisor = adviserService.getActionAdvise((List<Poker>) session.getAttribute("playerCards"), (List<Poker>) session.getAttribute("dealerCards"));
        return AjaxReturn.success(advisor);
	}


}
