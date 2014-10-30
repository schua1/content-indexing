package org.deshang.content.indexing.model;

import java.io.Serializable;
import java.util.Date;

public class WpSite implements Serializable {

    private static final long serialVersionUID = 7869381476079055417L;

    private long blogId;
    private long siteId;
    private String domain;
    private String path;
    private Date registeredDate;
    private Date lastUpdateDate;
    private WpUser owner;

    /**
     * Default constructor
     */
    public WpSite() {
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public WpUser getOwner() {
        return owner;
    }

    public void setOwner(WpUser owner) {
        this.owner = owner;
    }
}
