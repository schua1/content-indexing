package org.deshang.content.indexing.model;

import java.io.Serializable;
import java.util.Date;

public class WpUser implements Serializable {

    private static final long serialVersionUID = -417548662155428833L;

    private long id;
    private String loginName;
    private String niceName;
    private String email;
    private Date registeredDate;
    private int status;
    private String displayName;
    private WpSite site;

    /**
     * Default Constructor
     */
    public WpUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public WpSite getSite() {
        return site;
    }

    public void setSite(WpSite site) {
        this.site = site;
    }
}
