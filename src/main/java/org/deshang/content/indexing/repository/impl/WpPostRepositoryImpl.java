package org.deshang.content.indexing.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.deshang.content.indexing.model.WpPost;
import org.deshang.content.indexing.repository.WpPostRepository;
import org.deshang.content.indexing.util.jdbc.AbstractRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WpPostRepositoryImpl implements WpPostRepository {

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
        String sql = getSitePostBasicQuery(siteId);
        return jdbcTemplate.query(sql, new WpPostMapper());
    }

    @Override
    public List<WpPost> getSiteAllPublishedPosts(long siteId) {
        String sql = getSitePostBasicQuery(siteId) + " WHERE post_status = 'publish'";
        return jdbcTemplate.query(sql, new WpPostMapper());
    }

    @Override
    public WpPost getSitePostById(long siteId, long postId) {
        String sql = getSitePostBasicQuery(siteId) + " WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { postId }, new WpPostMapper());
    }

    private String getSitePostBasicQuery(long siteId) {
        String siteIdSub = "_" + siteId + "_";
        if (siteId <= 1) {
            siteIdSub = "_";
        }
        return String.format(SITE_POST_BASIC_QUERY_TEMPLATE, siteIdSub);
    }
}
