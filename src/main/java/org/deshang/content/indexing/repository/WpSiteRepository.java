package org.deshang.content.indexing.repository;

import java.util.List;

import org.deshang.content.indexing.model.WpSite;

public interface WpSiteRepository {

    List<WpSite> getAllSites();
    
    WpSite getSiteById(long siteId);
}
