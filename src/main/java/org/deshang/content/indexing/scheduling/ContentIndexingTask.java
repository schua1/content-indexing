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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

public class ContentIndexingTask implements Runnable {

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
                    System.out.println("Site with id [" + userPosts.getKey() + "] has " + userPosts.getValue().size()
                            + " post(s).");
                    for (WpPost post : userPosts.getValue()) {
                        writer.addDocument(util.covertWpPost2Document(post, userPosts.getKey()));
                    }
                }

                for (Map.Entry<Long, List<WpComment>> userComments : userComments.entrySet()) {
                    System.out.println("Site with id [" + userComments.getKey() + "] has "
                            + userComments.getValue().size() + " comment(s).");
                    for (WpComment comment : userComments.getValue()) {
                        writer.addDocument(util.covertWpComment2Document(comment, userComments.getKey()));
                    }
                }
                writer.commit();
                util.storeIndex(writer.getDirectory(), rootPath + FILE_SEPARATOR + username);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
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
            System.out.print("Site info: [id=" + site.getBlogId() + ";domain_path=" + site.getDomain() + site.getPath());
            WpUser user = site.getOwner();
            System.out.print(" User Info: [id=" + user.getId() + ";login=" + user.getLoginName() + "]");
            System.out.println();
            
            List<WpPost> publishPosts = postService.getSiteAllPublishPost(site.getBlogId());
            System.out.println("Site " + site.getPath() + " with id [" + site.getBlogId() + "] has " + publishPosts.size() + " post(s).");
            for (WpPost post : publishPosts) {
                //System.out.print("Post info: [id=" + post.getId() + ";title=" + post.getTitle() + ";content=" + post.getContent() + ";");
                if (post.getAuthorUser() != null) {
                    //System.out.print("authorUser=" + post.getAuthorUser().getLoginName() + "]");
                } else {
                    //System.out.print("authorUser=null]");
                }

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
                System.out.println("Post with id [" + post.getId() + "] has " + comments.size() + " comment(s).");
                for (WpComment comment : comments) {
                    //System.out.print("First comment info: [id=" + comment.getId() + ";content=" + comment.getContent() + ";");
                    String commentAuthorName = comment.getAuthor();
                    if (comment.getAuthorUser() != null) {
                        //System.out.println("authorUser=" +  comment.getAuthorUser().getLoginName() + "]");
                        commentAuthorName = comment.getAuthorUser().getLoginName();
                    } else {
                        //System.out.println("authorUser=null]");
                    }

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
            e.printStackTrace();
        }
        
        try {
            File rootDir = new File(rootPath);
            File[] indices = rootDir.listFiles();
            for (File index : indices) {
                System.out.println("Read index from folder " + index.getCanonicalPath());
                IndexReader reader = DirectoryReader.open(FSDirectory.open(index));
                System.out.println("Total " + reader.numDocs() + " document(s) indexed.");
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
