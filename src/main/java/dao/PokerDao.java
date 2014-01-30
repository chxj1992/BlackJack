package dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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


    public Poker getByPokerId(Integer pokerId) {
        return (Poker) jdbc.queryForObject("SELECT * FROM poker WHERE poker_id = ? ", pokerMapper, pokerId);
    }


    public List<Poker> getPokers(List<Integer> pokerIds) {

        List<Poker> pokers = Lists.newArrayList();
        for( Integer pokerId : pokerIds ) {
            pokers.add(this.getByPokerId(pokerId));
        }

        return pokers;
    }
}
