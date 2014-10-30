package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpComment;
import org.deshang.content.indexing.repository.WpCommentRepository;
import org.deshang.content.indexing.util.jdbc.AbstractRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WpCommentRepositoryImpl implements WpCommentRepository {

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
        String sql = getSiteCommentBasicQuery(siteId);
        return jdbcTemplate.query(sql, new WpCommentMapper());
    }

    @Override
    public WpComment getSiteCommentById(long siteId, long commentId) {
        String sql = getSiteCommentBasicQuery(siteId) + " WHERE comment_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { commentId }, new WpCommentMapper());
    }

    private String getSiteCommentBasicQuery(long siteId) {
        String siteIdSub = "_" + siteId + "_";
        if (siteId <= 1) {
            siteIdSub = "_";
        }
        return String.format(SITE_COMMENT_BASIC_QUERY_TEMPLATE, siteIdSub);
    }
}
