package mapper;

import model.Poker;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: chen
 * DateTime: 1/8/14 10:11 AM
 */
public class PokerMapper implements RowMapper{

    @Override
    public Poker mapRow(ResultSet rs, int rowNum) throws SQLException {
        Poker poker = new Poker(
                rs.getInt("poker_id"),
                rs.getInt("value"),
                rs.getString("file_name")
        );
        return poker;
    }

}
