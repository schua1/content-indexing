package org.deshang.content.indexing.repository;

import java.util.List;

import org.deshang.content.indexing.model.WpUser;

public interface WpUserRepository {

    List<WpUser> getAllUsers();
    
    WpUser getUserById(long userId);

    WpUser getUserByLoginName(String loginName);

    WpUser getDefaultAdminUser();
}
