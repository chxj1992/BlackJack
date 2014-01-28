package dao;

import mapper.ContentRowMapper;
import model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Author: chen
 * DateTime: 1/8/14 10:41 AM
 */
public class ContentDao {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private ContentRowMapper contentRowMapper;
    @Autowired
    private HttpServletRequest request;

    public List<Content> getListByUserId(Integer userId) {
        return jdbc.query("SELECT * FROM content WHERE user_id = ? ORDER BY update_time DESC", contentRowMapper, userId);
    }

    public Boolean addContent(String context) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        Integer ret = jdbc.update("INSERT INTO content (user_id, context) VALUES (?, ?)", userId, context);
        if ( ret == 0 )
            return false;
        else
            return true;
    }

    public Boolean removeContent(Integer content_id) {
        Integer ret = jdbc.update("DELETE FROM content WHERE content_id = ?", content_id);
        if ( ret == 0 )
            return false;
        else
            return true;
    }

    public Boolean updateContent(Integer content_id, String context) {
        Integer ret =  jdbc.update("UPDATE content set context = ? WHERE content_id = ?", context, content_id);
        if ( ret == 0 )
            return false;
        else
            return true;
    }

}
