package org.deshang.content.indexing.repository;

import java.util.List;

import org.deshang.content.indexing.model.WpSignup;

public interface WpSignupRepository {

    List<WpSignup> getAllSignups();
    
    WpSignup getSignupById(long singupId);

    WpSignup getSignupByDomainAndPath(String domain, String path);
}
