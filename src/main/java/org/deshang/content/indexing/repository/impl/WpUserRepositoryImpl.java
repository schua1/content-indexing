package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpUser;
import org.deshang.content.indexing.repository.WpUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpUserRepositoryImpl implements WpUserRepository {

    private final String USER_BASIC_QUERY_STRING = "SELECT ID, user_login, user_nicename, user_email, user_registered, user_status, display_name FROM wp_users";

    public static final class WpUserMapper implements RowMapper<WpUser> {
        @Override
        public WpUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            WpUser user = new WpUser();

            user.setId(rs.getLong("ID"));
            user.setLoginName(rs.getString("user_login"));
            user.setNiceName(rs.getString("user_nicename"));
            user.setEmail(rs.getString("user_email"));
            user.setRegisteredDate(rs.getTimestamp("user_registered"));
            user.setStatus(rs.getInt("user_status"));
            user.setDisplayName(rs.getString("display_name"));

            return user;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<WpUser> getAllUsers() {
        String sql = USER_BASIC_QUERY_STRING;
        return jdbcTemplate.query(sql, new WpUserMapper());
    }

    @Override
    public WpUser getUserById(long siteId) {
        String sql = USER_BASIC_QUERY_STRING + " WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpUserMapper());
    }

    @Override
    public WpUser getUserByLoginName(String loginName) {
        String sql = USER_BASIC_QUERY_STRING + " WHERE user_login = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { loginName }, new WpUserMapper());
    }

    @Override
    public WpUser getDefaultAdminUser() {
        String sql = USER_BASIC_QUERY_STRING + " WHERE ID = 1";
        return jdbcTemplate.queryForObject(sql, new Object[] { }, new WpUserMapper());
    }
}
