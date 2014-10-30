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

import org.deshang.content.indexing.model.WpComment;
import org.deshang.content.indexing.repository.WpCommentRepository;
import org.deshang.content.indexing.util.jdbc.AbstractRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WpCommentRepositoryImpl implements WpCommentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpCommentRepositoryImpl.class);

    private final String SITE_COMMENT_BASIC_QUERY_TEMPLATE = "SELECT comment_ID, comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id FROM wp%scomments";

    public static final class WpCommentMapper extends AbstractRowMapper<WpComment> {
        @Override
        public WpComment mapRow(ResultSet rs, int rowNum) throws SQLException {
            WpComment comment = new WpComment();
            
            comment.setId(rs.getLong("comment_id"));
            comment.setPostId(rs.getLong("comment_post_ID"));
            comment.setAuthor(getBlobContent(rs.getBlob("comment_author")));
            comment.setAuthorEmai(rs.getString("comment_author_email"));
            comment.setAuthorUrl(rs.getString("comment_author_url"));
            comment.setAuthorIp(rs.getString("comment_author_IP"));
            comment.setCommentDate(rs.getTimestamp("comment_date"));
            comment.setCommentGmtDate(rs.getTimestamp("comment_date_gmt"));
            comment.setContent(getBlobContent(rs.getBlob("comment_content")));
            comment.setKarma(rs.getInt("comment_karma"));
            comment.setApproved(rs.getString("comment_approved"));
            comment.setAgent(rs.getString("comment_agent"));
            comment.setType(rs.getString("comment_type"));
            comment.setParentId(rs.getLong("comment_parent"));
            comment.setUserId(rs.getLong("user_id"));

            return comment;
        }
        
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<WpComment> getSiteAllComments(long siteId) {
        LOGGER.debug("Enter getSiteAllComments(long)");

        String sql = getSiteCommentBasicQuery(siteId);

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSiteAllComments(long)");
        return jdbcTemplate.query(sql, new WpCommentMapper());
    }

    @Override
    public WpComment getSiteCommentById(long siteId, long commentId) {
        LOGGER.debug("Enter getSiteCommentById(long, long)");

        String sql = getSiteCommentBasicQuery(siteId) + " WHERE comment_id = ?";

        LOGGER.debug("Executing SQL: " + sql);
        LOGGER.debug("Exit getSiteCommentById(long, long)");
        return jdbcTemplate.queryForObject(sql, new Object[] { commentId }, new WpCommentMapper());
    }

    private String getSiteCommentBasicQuery(long siteId) {
        LOGGER.debug("Enter getSiteCommentBasicQuery(long)");

        String siteIdSub = "_" + siteId + "_";
        if (siteId <= 1) {
            siteIdSub = "_";
        }
        LOGGER.debug("Site ID substitute: " + siteIdSub);

        LOGGER.debug("Exit getSiteCommentBasicQuery(long)");
        return String.format(SITE_COMMENT_BASIC_QUERY_TEMPLATE, siteIdSub);
    }
}
