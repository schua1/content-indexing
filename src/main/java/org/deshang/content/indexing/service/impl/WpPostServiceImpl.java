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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WpPostServiceImpl implements WpPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpPostServiceImpl.class);

    @Autowired
    private WpPostRepository postRepository;

    @Autowired
    private WpCommentRepository commentRepository;
    
    @Autowired
    private WpUserRepository userRepository;

    @Override
    public List<WpPost> getSiteAllPublishPost(long siteId) {

        LOGGER.debug("Enter getSiteAllPublishPost(long)");
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

        LOGGER.debug("Exit getSiteAllPublishPost(long)");
        return publishPosts;
    }

}
