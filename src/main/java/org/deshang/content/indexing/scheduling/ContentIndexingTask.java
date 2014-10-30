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
package org.deshang.content.indexing.scheduling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.deshang.content.indexing.model.WpComment;
import org.deshang.content.indexing.model.WpPost;
import org.deshang.content.indexing.model.WpSite;
import org.deshang.content.indexing.model.WpUser;
import org.deshang.content.indexing.service.WpPostService;
import org.deshang.content.indexing.service.WpSiteService;
import org.deshang.content.indexing.util.lucene.LuceneIndexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

public class ContentIndexingTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentIndexingTask.class);

    private final String FILE_SEPARATOR = System.getProperty("file.separator");
    private class UserContentIndexingTask implements Runnable {
        
        private String username;
        Map<Long, List<WpPost>> userPosts;
        private Map<Long, List<WpComment>> userComments;

        UserContentIndexingTask(String username, Map<Long, List<WpPost>> userPosts, Map<Long, List<WpComment>> userComments) {
            this.username = username;
            this.userPosts = userPosts;
            this.userComments = userComments;
        }

        @Override
        public void run() {
            try {
                IndexWriter writer = util.buildRAMIndexWriter();

                for (Map.Entry<Long, List<WpPost>> userPosts : userPosts.entrySet()) {
                    LOGGER.debug("Site with id [" + userPosts.getKey() + "] has " + userPosts.getValue().size() + " post(s).");
                    for (WpPost post : userPosts.getValue()) {
                        writer.addDocument(util.covertWpPost2Document(post, userPosts.getKey()));
                    }
                }

                for (Map.Entry<Long, List<WpComment>> userComments : userComments.entrySet()) {
                    LOGGER.debug("Site with id [" + userComments.getKey() + "] has " + userComments.getValue().size() + " comment(s).");
                    for (WpComment comment : userComments.getValue()) {
                        writer.addDocument(util.covertWpComment2Document(comment, userComments.getKey()));
                    }
                }

                writer.commit();
                util.storeIndex(writer.getDirectory(), rootPath + FILE_SEPARATOR + username);
                writer.close();
            } catch (IOException e) {
                LOGGER.error("Writing index for user [" + username + "] error", e);
            }
            countDown.countDown();
        }
    }

    private String rootPath;
    private CountDownLatch countDown;
    private LuceneIndexUtil util = new LuceneIndexUtil();

    @Autowired
    private WpSiteService siteService;

    @Autowired
    private WpPostService postService;

    @Autowired
    private TaskScheduler taskScheduler;
    
    public ContentIndexingTask(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void run() {
        List<WpSite> sites = siteService.getAllSites();
        Map<WpUser, Map<Long, List<WpPost>>> allUserPosts = new HashMap<WpUser, Map<Long, List<WpPost>>>();
        Map<String, Map<Long, List<WpComment>>> allUserComments = new HashMap<String, Map<Long, List<WpComment>>>();
        Map<Long, List<WpPost>> allSitePosts = new HashMap<Long, List<WpPost>>();
        Map<Long, List<WpComment>> allSiteComments = new HashMap<Long, List<WpComment>>();
        for (WpSite site : sites) {
            LOGGER.debug("Site info: [id=" + site.getBlogId() + ";domain_path=" + site.getDomain() + site.getPath());
            WpUser user = site.getOwner();
            LOGGER.debug(" User Info: [id=" + user.getId() + ";login=" + user.getLoginName() + "]");
            
            List<WpPost> publishPosts = postService.getSiteAllPublishPost(site.getBlogId());
            LOGGER.debug("Site " + site.getPath() + " with id [" + site.getBlogId() + "] has " + publishPosts.size() + " post(s).");
            for (WpPost post : publishPosts) {
                LOGGER.debug("Post info: [id=" + post.getId()
                                      + ";title=" + post.getTitle()
                                      + ";content=" + post.getContent()
                                      + ";authorUser=" + (post.getAuthorUser() != null
                                                                  ? post.getAuthorUser().getLoginName()
                                                                  : "null") + "]");

                // add post into user post map
                Map<Long, List<WpPost>> userPostMap = allUserPosts.get(post.getAuthorUser());
                if (userPostMap == null) {
                    userPostMap = new HashMap<Long, List<WpPost>>();
                    userPostMap.put(site.getBlogId(), new ArrayList<WpPost>());
                    allUserPosts.put(post.getAuthorUser(), userPostMap);
                }
                List<WpPost> userPosts = userPostMap.get(site.getBlogId());
                if (userPosts == null ) {
                    userPosts = new ArrayList<WpPost>();
                    userPostMap.put(site.getBlogId(), userPosts);
                }
                userPosts.add(post);
                
                // add post into site post map
                List<WpPost> sitePosts = allSitePosts.get(site.getBlogId());
                if (sitePosts == null) {
                    sitePosts = new ArrayList<WpPost>();
                    allSitePosts.put(site.getBlogId(), sitePosts);
                }
                sitePosts.add(post);

                List<WpComment> comments = post.getAllComments();
                LOGGER.debug("Post with id [" + post.getId() + "] has " + comments.size() + " comment(s).");
                for (WpComment comment : comments) {
                    String commentAuthorName = comment.getAuthor();
                    LOGGER.debug("Comment info: [id=" + comment.getId()
                                             + ";content=" + comment.getContent()
                                             + ";authorUser=" + (comment.getAuthorUser() != null
                                                                         ? comment.getAuthorUser().getLoginName()
                                                                         : "null") + "]");

                    Map<Long, List<WpComment>> userCommentMap = allUserComments.get(commentAuthorName);
                    if (userCommentMap == null) {
                        userCommentMap = new HashMap<Long, List<WpComment>>();
                        userCommentMap.put(site.getBlogId(), new ArrayList<WpComment>());
                        allUserComments.put(commentAuthorName, userCommentMap);
                    }
                    List<WpComment> userComments = userCommentMap.get(site.getBlogId());
                    if (userComments == null) {
                        userComments = new ArrayList<WpComment>();
                        userCommentMap.put(site.getBlogId(), userComments);
                    }
                    userComments.add(comment);
                    
                    // add comment into site comment map
                    List<WpComment> siteComments = allSiteComments.get(site.getBlogId());
                    if (siteComments == null) {
                        siteComments = new ArrayList<WpComment>();
                        allSiteComments.put(site.getBlogId(), siteComments);
                    }
                    siteComments.add(comment);
                }
            }
        }

        countDown = new CountDownLatch(allUserPosts.size() + 1);

        taskScheduler.schedule(new UserContentIndexingTask("root", allSitePosts, allSiteComments) , new Date());
        
        for (Map.Entry<WpUser, Map<Long, List<WpPost>>> userPostMapEntry : allUserPosts.entrySet()) {
            Map<Long, List<WpPost>> userPosts = userPostMapEntry.getValue();
            WpUser user = userPostMapEntry.getKey();
            Map<Long, List<WpComment>> userComments = allUserComments.get(user.getLoginName());
            if (userComments == null) {
                userComments = new HashMap<Long, List<WpComment>>();
            }
            taskScheduler.schedule(new UserContentIndexingTask(user.getLoginName(), userPosts, userComments) , new Date());
        }

        try {
            countDown.await();
        } catch (InterruptedException e) {
            LOGGER.error("Waiting all count down latch error", e);
            e.printStackTrace();
        }

        File rootDir = new File(rootPath);
        File[] indices = rootDir.listFiles();
        for (File index : indices) {
            try {
                LOGGER.debug("Read index from folder " + index.getCanonicalPath());
                IndexReader reader = DirectoryReader.open(FSDirectory.open(index));
                LOGGER.debug("Total " + reader.numDocs() + " document(s) indexed.");
                reader.close();
            } catch (IOException e) {
                LOGGER.error("Reading index for user error", e);
            }
        }
    }
}
