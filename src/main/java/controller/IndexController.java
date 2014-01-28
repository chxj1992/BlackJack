package controller;

import dao.ContentDao;
import model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import utils.AjaxReturn;
import utils.Poker;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Author: chen
 * DateTime: 1/7/14 10:34 AM
 */
@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String homePage(ModelMap model, HttpServletRequest request) {
        return "index";
	}

    @RequestMapping(value="/setInfo", method = RequestMethod.POST)
    public Map setInfo() {
        return AjaxReturn.success();
    }

}
