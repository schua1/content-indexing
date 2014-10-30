package org.deshang.content.indexing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    private static final String APP_ROOT_PACKAGE_NAME = App.class.getPackage().getName();
    public static final String APP_CONFIG_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".config";
    public static final String APP_REPOSITORY_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".repository.impl";
    public static final String APP_SERVICE_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".service.impl";
    public static final String APP_MODEL_PACKAGE_NAME = APP_ROOT_PACKAGE_NAME + ".model";

    public static final String APP_ACTIVE_PROFILE_NAMES = "default";

    private AnnotationConfigApplicationContext context;

    private static class AppHolder {
        private final static App instance = new App();
    }

    private App() {
        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("APP_ACTIVE_PROFILE_NAMES");
        context.scan(APP_CONFIG_PACKAGE_NAME, APP_SERVICE_PACKAGE_NAME, APP_REPOSITORY_PACKAGE_NAME);
        context.refresh();
    }

    public static ApplicationContext getContext() {
        return AppHolder.instance.context;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = App.getContext();

        WpSiteService siteService = ctx.getBean(WpSiteService.class);
        WpPostService postService = ctx.getBean(WpPostService.class);
        List<WpSite> sites = siteService.getAllSites();
        Map<WpUser, Map<Long, List<WpPost>>> allUserPosts = new HashMap<WpUser, Map<Long, List<WpPost>>>();
        Map<String, Map<Long, List<WpComment>>> allUserComments = new HashMap<String, Map<Long, List<WpComment>>>();
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
                }
            }
        }

        LuceneIndexUtil util = new LuceneIndexUtil();
        try {
            IndexWriter writer = util.buildRAMIndexWriter();
            
            for (Map<Long, List<WpPost>> userPostMap : allUserPosts.values()) {
                for (Map.Entry<Long, List<WpPost>> userPosts : userPostMap.entrySet()) {
                    System.out.println("Site with id [" + userPosts.getKey() + "] has " + userPosts.getValue().size() + " post(s).");
                    for (WpPost post : userPosts.getValue()) {
                        writer.addDocument(util.covertWpPost2Document(post, userPosts.getKey()));
                    }
                }
            }

            for (Map<Long, List<WpComment>> userCommentMap : allUserComments.values()) {
                for (Map.Entry<Long, List<WpComment>> userComments : userCommentMap.entrySet()) {
                    System.out.println("Site with id [" + userComments.getKey() + "] has " + userComments.getValue().size() + " comment(s).");
                    for (WpComment comment : userComments.getValue()) {
                        writer.addDocument(util.covertWpComment2Document(comment, userComments.getKey()));
                    }
                }
            }
            writer.commit();
            util.storeIndex(writer.getDirectory(), "f:\\content indices");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("f:\\content indices")));
            System.out.println("Total " + reader.numDocs() + " document(s) indexed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
