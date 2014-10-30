/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpSignup;
import org.deshang.content.indexing.repository.WpSignupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpSignupRepositoryImpl implements WpSignupRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpSignupRepositoryImpl.class);

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
        LOGGER.debug("Enter getAllSignups()");

        String sql = SINGUP_BASIC_QUERY_STRING;

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getAllSignups()");
        return jdbcTemplate.query(sql, new WpSignupMapper());
    }

    @Override
    public WpSignup getSignupById(long siteId) {
        LOGGER.debug("Enter getSignupById(long)");

        String sql = SINGUP_BASIC_QUERY_STRING + " WHERE signup_id = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSignupById(long)");
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpSignupMapper());
    }

    @Override
    public WpSignup getSignupByDomainAndPath(String domain, String path) {
        LOGGER.debug("Enter getSignupByDomainAndPath(String, String)");

        String sql = SINGUP_BASIC_QUERY_STRING + " WHERE domain = ? AND path = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSignupByDomainAndPath(String, String)");
        return jdbcTemplate.queryForObject(sql, new Object[] { domain, path }, new WpSignupMapper());
    }
}
