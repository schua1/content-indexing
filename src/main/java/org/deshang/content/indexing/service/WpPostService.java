package org.deshang.content.indexing.service;

import java.util.List;

import org.deshang.content.indexing.model.WpPost;

public interface WpPostService {

    List<WpPost> getSiteAllPublishPost(long siteId);
}
