package org.deshang.content.indexing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WpPost implements Serializable {

    private static final long serialVersionUID = 1279116458019892948L;

    private long id;
    private long authorId;
    private Date submitDate;
    private Date submitGmtDate;
    private String content;
    private String title;
    private String excerpt;
    private String postStatus;
    private String commentStatus;
    private String pingStatus;
    private String postPassword;
    private String postName;
    private String toPing;
    private String pinged;
    private Date modifiedDate;
    private Date modifiedGmtDate;
    private String contentFiltered;
    private long parentId;
    private String guid;
    private int menuOrder;
    private String type;
    private String mimeType;
    private long commentCount;

    private WpUser authorUser;
    private List<WpComment> comments = new ArrayList<WpComment>();

    public WpPost() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Date getSubmitGmtDate() {
        return submitGmtDate;
    }

    public void setSubmitGmtDate(Date submitGmtDate) {
        this.submitGmtDate = submitGmtDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getPostPassword() {
        return postPassword;
    }

    public void setPostPassword(String postPassword) {
        this.postPassword = postPassword;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getToPing() {
        return toPing;
    }

    public void setToPing(String toPing) {
        this.toPing = toPing;
    }

    public String getPinged() {
        return pinged;
    }

    public void setPinged(String pinged) {
        this.pinged = pinged;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getModifiedGmtDate() {
        return modifiedGmtDate;
    }

    public void setModifiedGmtDate(Date modifiedGmtDate) {
        this.modifiedGmtDate = modifiedGmtDate;
    }

    public String getContentFiltered() {
        return contentFiltered;
    }

    public void setContentFiltered(String contentFiltered) {
        this.contentFiltered = contentFiltered;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(int menuOrder) {
        this.menuOrder = menuOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public WpUser getAuthorUser() {
        return authorUser;
    }

    public void setAuthorUser(WpUser authorUser) {
        this.authorUser = authorUser;
    }

    public void addComment(WpComment comment) {
        comments.add(comment);
    }

    public void addAllComments(List<WpComment> commentSet) {
        comments.addAll(commentSet);
    }

    public List<WpComment> getAllComments() {
        return comments;
    }
}
