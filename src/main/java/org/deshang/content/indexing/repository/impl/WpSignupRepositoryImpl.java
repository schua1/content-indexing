package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpSignup;
import org.deshang.content.indexing.repository.WpSignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpSignupRepositoryImpl implements WpSignupRepository {

    private final String SINGUP_BASIC_QUERY_STRING = "SELECT signup_id, domain, path, title, user_login, registered, activated, active FROM wp_signups";
    public static final class WpSignupMapper implements RowMapper<WpSignup> {
        @Override
        public WpSignup mapRow(ResultSet rs, int rowNum) throws SQLException {
            WpSignup signup = new WpSignup();

            signup.setId(rs.getLong("signup_id"));
            signup.setDomain(rs.getString("domain"));
            signup.setPath(rs.getString("path"));
            signup.setTitle(rs.getString("title"));
            signup.setUserLoginName(rs.getString("user_login"));
            signup.setRegisteredDate(rs.getTimestamp("registered"));
            signup.setActive(rs.getBoolean("active"));
            if (signup.isActive()) {
                signup.setActivatedDate(rs.getTimestamp("activated"));
            }

            return signup;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<WpSignup> getAllSignups() {
        String sql = SINGUP_BASIC_QUERY_STRING;
        return jdbcTemplate.query(sql, new WpSignupMapper());
    }

    @Override
    public WpSignup getSignupById(long siteId) {
        String sql = SINGUP_BASIC_QUERY_STRING + " WHERE signup_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpSignupMapper());
    }

    @Override
    public WpSignup getSignupByDomainAndPath(String domain, String path) {
        String sql = SINGUP_BASIC_QUERY_STRING + " WHERE domain = ? AND path = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { domain, path }, new WpSignupMapper());
    }
}
