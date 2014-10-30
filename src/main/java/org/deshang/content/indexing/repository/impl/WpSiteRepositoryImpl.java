package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpSite;
import org.deshang.content.indexing.repository.WpSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WpSiteRepositoryImpl implements WpSiteRepository {

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
        String sql = SITE_BASIC_QUERY_STRING;
        return jdbcTemplate.query(sql, new WpSiteMapper());
    }

    @Override
    public WpSite getSiteById(long siteId) {
        String sql = SITE_BASIC_QUERY_STRING + " WHERE blog_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { siteId }, new WpSiteMapper());
    }

}
