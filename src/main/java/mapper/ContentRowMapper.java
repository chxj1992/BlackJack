package mapper;

import model.Content;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: chen
 * DateTime: 1/8/14 10:11 AM
 */
public class ContentRowMapper implements RowMapper{

    @Override
    public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
        Content content = new Content(
                rs.getInt("content_id"),
                rs.getInt("user_id"),
                rs.getString("context"),
                rs.getTimestamp("update_time")
        );
        return content;
    }

}
