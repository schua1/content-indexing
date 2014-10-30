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

import org.deshang.content.indexing.model.WpPost;
import org.deshang.content.indexing.repository.WpPostRepository;
import org.deshang.content.indexing.util.jdbc.AbstractRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WpPostRepositoryImpl implements WpPostRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpPostRepositoryImpl.class);

    private final String SITE_POST_BASIC_QUERY_TEMPLATE = "SELECT ID, post_author, post_date, post_date_gmt, post_content, post_title, post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, post_mime_type, comment_count FROM wp%sposts";

    public static final class WpPostMapper extends AbstractRowMapper<WpPost> {
        @Override
        public WpPost mapRow(ResultSet rs, int rowNum) throws SQLException {
            WpPost post = new WpPost();
            
            post.setId(rs.getLong("ID"));
            post.setAuthorId(rs.getLong("post_author"));
            post.setSubmitDate(rs.getTimestamp("post_date"));
            post.setSubmitGmtDate(rs.getTimestamp("post_date_gmt"));
            post.setContent(getBlobContent(rs.getBlob("post_content")));
            post.setTitle(getBlobContent(rs.getBlob("post_title")));
            post.setExcerpt(getBlobContent(rs.getBlob("post_excerpt")));
            post.setPostStatus(rs.getString("post_status"));
            post.setCommentStatus(rs.getString("comment_status"));
            post.setPingStatus(rs.getString("ping_status"));
            post.setPostPassword(rs.getString("post_password"));
            post.setPostName(rs.getString("post_name"));
            post.setToPing(getBlobContent(rs.getBlob("to_ping")));
            post.setPinged(getBlobContent(rs.getBlob("pinged")));
            post.setModifiedDate(rs.getTimestamp("post_modified"));
            post.setModifiedGmtDate(rs.getTimestamp("post_modified_gmt"));
            post.setContentFiltered(getBlobContent(rs.getBlob("post_content_filtered")));
            post.setParentId(rs.getLong("post_parent"));
            post.setGuid(rs.getString("guid"));
            post.setMenuOrder(rs.getInt("menu_order"));
            post.setType(rs.getString("post_type"));
            post.setMimeType(rs.getString("post_mime_type"));
            post.setCommentCount(rs.getLong("comment_count"));

            return post;
        }
        
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<WpPost> getSiteAllPosts(long siteId) {
        LOGGER.debug("Enter getSiteAllPosts(long)");

        String sql = getSitePostBasicQuery(siteId);

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSiteAllPosts(long)");
        return jdbcTemplate.query(sql, new WpPostMapper());
    }

    @Override
    public List<WpPost> getSiteAllPublishedPosts(long siteId) {
        LOGGER.debug("Enter getSiteAllPublishedPosts(long)");

        String sql = getSitePostBasicQuery(siteId) + " WHERE post_status = 'publish'";
        
        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSiteAllPublishedPosts(long)");
        return jdbcTemplate.query(sql, new WpPostMapper());
    }

    @Override
    public WpPost getSitePostById(long siteId, long postId) {
        LOGGER.debug("Enter getSitePostById(long, long)");

        String sql = getSitePostBasicQuery(siteId) + " WHERE ID = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSitePostById(long, long)");
        return jdbcTemplate.queryForObject(sql, new Object[] { postId }, new WpPostMapper());
    }

    private String getSitePostBasicQuery(long siteId) {
        LOGGER.debug("Enter getSitePostBasicQuery(long)");

        String siteIdSub = "_" + siteId + "_";
        if (siteId <= 1) {
            siteIdSub = "_";
        }
        LOGGER.debug("Site ID substitute: " + siteIdSub);

        LOGGER.debug("Exit getSitePostBasicQuery(long)");
        return String.format(SITE_POST_BASIC_QUERY_TEMPLATE, siteIdSub);
    }
}
