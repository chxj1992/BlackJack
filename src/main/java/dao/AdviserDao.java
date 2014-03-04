package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Author: chen
 * DateTime: 1/8/14 10:41 AM
 */
public class AdviserDao {

    @Autowired
    private JdbcTemplate jdbc;

    public String getAdvisor(String playerCard, String dealerCard) {
        return jdbc.queryForObject("SELECT action FROM advisor WHERE player = ? AND dealer = ? ",new Object[]{playerCard, dealerCard}, String.class);
    }


}
