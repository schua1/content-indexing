package org.deshang.content.indexing.repository;

import java.util.List;

import org.deshang.content.indexing.model.WpComment;

public interface WpCommentRepository {

    List<WpComment> getSiteAllComments(long siteId);
    
    WpComment getSiteCommentById(long siteId, long commentId);
}
