package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.AdvisorService;
import utils.AjaxReturn;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 建议系统
 * Author: chen
 * DateTime: 1/29/14 7:44 PM
 */

@Controller
@RequestMapping("/advisor")
public class AdvisorController {


    @Autowired
    private AdvisorService advisorService;


	@RequestMapping(value="bet", method = RequestMethod.POST)
    @ResponseBody
	public Map setBet(HttpSession session) {
        String advisor = advisorService.getBetAdvise(session);
        return AjaxReturn.success(advisor);
	}


	@RequestMapping(value="action", method = RequestMethod.POST)
    @ResponseBody
	public Map actionAdvisor(HttpSession session) {
        String advisor = advisorService.getActionAdvise(session);
        return AjaxReturn.success(advisor);
	}


}
