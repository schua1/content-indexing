package org.deshang.content.indexing.repository;

import java.util.List;

import org.deshang.content.indexing.model.WpPost;

public interface WpPostRepository {

    List<WpPost> getSiteAllPosts(long siteId);
    
    List<WpPost> getSiteAllPublishedPosts(long siteId);

    WpPost getSitePostById(long siteId, long id);
}
