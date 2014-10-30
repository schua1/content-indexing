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

import org.deshang.content.indexing.model.WpSite;
import org.deshang.content.indexing.repository.WpSiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpSiteRepositoryImpl implements WpSiteRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpSiteRepositoryImpl.class);

    private final String SITE_BASIC_QUERY_STRING = "SELECT blog_id, site_id, domain, path, registered, last_updated FROM wp_blogs";

    public static final class WpSiteMapper implements RowMapper<WpSite> {
        @Override
        public WpSite mapRow(ResultSet rs, int rowNum) throws SQLException {
            WpSite site = new WpSite();

            site.setBlogId(rs.getLong("blog_id"));
            site.setSiteId(rs.getLong("site_id"));
            site.setDomain(rs.getString("domain"));
            site.setPath(rs.getString("path"));
            site.setRegisteredDate(rs.getTimestamp("registered"));
            site.setLastUpdateDate(rs.getTimestamp("last_updated"));

            return site;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<WpSite> getAllSites() {
        LOGGER.debug("Enter getAllSites()");

        String sql = SITE_BASIC_QUERY_STRING;

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getAllSites()");
        return jdbcTemplate.query(sql, new WpSiteMapper());
    }

    @Override
    public WpSite getSiteById(long siteId) {
        LOGGER.debug("Enter getSiteById(long)");

        String sql = SITE_BASIC_QUERY_STRING + " WHERE blog_id = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSiteById(long)");
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpSiteMapper());
    }

}
