package org.deshang.content.indexing.model;

import java.io.Serializable;
import java.util.Date;

public class WpComment implements Serializable {

    private static final long serialVersionUID = 3359715645771254796L;

    private long id;
    private long postId;
    private String author;
    private String authorEmai;
    private String authorUrl;
    private String authorIp;;
    private Date commentDate;
    private Date commentGmtDate;
    private String content;
    private int karma;
    private String approved;
    private String agent;
    private String type;
    private long parentId;
    private long userId;

    private WpPost parentPost;
    private WpComment parentComment;
    private WpUser authorUser;

    public WpComment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmai() {
        return authorEmai;
    }

    public void setAuthorEmai(String authorEmai) {
        this.authorEmai = authorEmai;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorIp() {
        return authorIp;
    }

    public void setAuthorIp(String authorIp) {
        this.authorIp = authorIp;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Date getCommentGmtDate() {
        return commentGmtDate;
    }

    public void setCommentGmtDate(Date commentGmtDate) {
        this.commentGmtDate = commentGmtDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public WpPost getParentPost() {
        return parentPost;
    }

    public void setParentPost(WpPost parentPost) {
        this.parentPost = parentPost;
    }

    public WpComment getParentComment() {
        return parentComment;
    }

    public void setParentComment(WpComment parentComment) {
        this.parentComment = parentComment;
    }

    public WpUser getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(WpUser authorUser) {
        this.authorUser = authorUser;
    }
}
