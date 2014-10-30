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

import org.deshang.content.indexing.model.WpUser;
import org.deshang.content.indexing.repository.WpUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpUserRepositoryImpl implements WpUserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpUserRepositoryImpl.class);

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
        LOGGER.debug("Enter getAllUsers()");

        String sql = USER_BASIC_QUERY_STRING;

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getAllUsers()");
        return jdbcTemplate.query(sql, new WpUserMapper());
    }

    @Override
    public WpUser getUserById(long siteId) {
        LOGGER.debug("Enter getAllUsers(long)");

        String sql = USER_BASIC_QUERY_STRING + " WHERE ID = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getAllUsers(long)");
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpUserMapper());
    }

    @Override
    public WpUser getUserByLoginName(String loginName) {
        LOGGER.debug("Enter getUserByLoginName(String)");

        String sql = USER_BASIC_QUERY_STRING + " WHERE user_login = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getUserByLoginName(String)");
        return jdbcTemplate.queryForObject(sql, new Object[] { loginName }, new WpUserMapper());
    }

    @Override
    public WpUser getDefaultAdminUser() {
        LOGGER.debug("Enter getDefaultAdminUser()");

        String sql = USER_BASIC_QUERY_STRING + " WHERE ID = 1";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getDefaultAdminUser()");
        return jdbcTemplate.queryForObject(sql, new Object[] { }, new WpUserMapper());
    }
}
