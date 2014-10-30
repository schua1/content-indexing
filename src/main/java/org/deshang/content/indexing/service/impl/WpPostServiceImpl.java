package org.deshang.content.indexing.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deshang.content.indexing.model.WpComment;
import org.deshang.content.indexing.model.WpPost;
import org.deshang.content.indexing.model.WpUser;
import org.deshang.content.indexing.repository.WpCommentRepository;
import org.deshang.content.indexing.repository.WpPostRepository;
import org.deshang.content.indexing.repository.WpUserRepository;
import org.deshang.content.indexing.service.WpPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WpPostServiceImpl implements WpPostService {

    @Autowired
    private WpPostRepository postRepository;

    @Autowired
    private WpCommentRepository commentRepository;
    
    @Autowired
    private WpUserRepository userRepository;

    @Override
    public List<WpPost> getSiteAllPublishPost(long siteId) {
        
        List<WpUser> users = userRepository.getAllUsers();
        Map<Long, WpUser> userIdx = new HashMap<Long, WpUser>();
        for (WpUser user : users) {
            userIdx.put(user.getId(), user);
        }

        List<WpComment> comments = commentRepository.getSiteAllComments(siteId);
        Map<Long, List<WpComment>> commentIdx = new HashMap<Long, List<WpComment>>();
        for (WpComment comment : comments) {
            List<WpComment> commentlist = commentIdx.get(comment.getParentId());
            if (commentlist == null) {
                commentlist = new ArrayList<WpComment>();
                commentIdx.put(comment.getPostId(), commentlist);
            }

            if ("1".equals(comment.getApproved())) {
                commentlist.add(comment);
                if (comment.getUserId() > 0) {
                    comment.setAuthorUser(userIdx.get(comment.getUserId()));
                }
            }
        }

        List<WpPost> publishPosts = postRepository.getSiteAllPublishedPosts(siteId);
        for (WpPost post : publishPosts) {
            if (post.getAuthorId() > 0) {
                post.setAuthorUser(userIdx.get(post.getAuthorId()));
            }
            
            if (commentIdx.containsKey(post.getId())) {
                List<WpComment> commentList = commentIdx.get(post.getId());
                post.addAllComments(commentList);
                for (WpComment comment : commentList) {
                    comment.setParentPost(post);
                }
            }
        }
        return publishPosts;
    }

}
