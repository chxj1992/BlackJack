package dao;

import mapper.PokerMapper;
import model.Poker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Author: chen
 * DateTime: 1/8/14 10:41 AM
 */
public class PokerDao {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private PokerMapper pokerMapper;


    public List<Poker> getByPokerId(Integer pokerId) {
        return jdbc.query("SELECT * FROM poker WHERE poker_id = ? ", pokerMapper, pokerId);
    }


}
