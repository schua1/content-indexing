package org.deshang.content.indexing.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deshang.content.indexing.model.WpSignup;
import org.deshang.content.indexing.model.WpSite;
import org.deshang.content.indexing.model.WpUser;
import org.deshang.content.indexing.repository.WpSignupRepository;
import org.deshang.content.indexing.repository.WpSiteRepository;
import org.deshang.content.indexing.repository.WpUserRepository;
import org.deshang.content.indexing.service.WpSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WpSiteServiceImpl implements WpSiteService {

    private final String DOMAIN_PATH_DELIMITER = System.getProperty("file.separator");
    
    @Autowired
    private WpSiteRepository siteRepository;

    @Autowired
    private WpSignupRepository signupRepository;

    @Autowired
    private WpUserRepository userRepository;

    public List<WpSite> getAllSites() {
        
        List<WpSignup> signups = signupRepository.getAllSignups();
        Map<String, Integer> signupIdx = new HashMap<String, Integer>();
        for (int i = 0; i < signups.size(); i++) {
            WpSignup signup = signups.get(i);
            String index = signup.getDomain() + DOMAIN_PATH_DELIMITER + signup.getPath();
            signupIdx.put(index, i);
        }

        List<WpUser> users = userRepository.getAllUsers();
        Map<String, Integer> userIdx = new HashMap<String, Integer>();
        for (int i = 0; i < users.size(); i++) {
            userIdx.put(users.get(i).getLoginName(), i);
        }

        WpUser defaultAdmin = userRepository.getDefaultAdminUser();

        List<WpSite> sites = siteRepository.getAllSites();
        for (WpSite site : sites) {
            String singupIdxKey = site.getDomain() + DOMAIN_PATH_DELIMITER + site.getPath();
            if (signupIdx.containsKey(singupIdxKey)) {
                WpSignup signup = signups.get(signupIdx.get(singupIdxKey));
                
                if (userIdx.containsKey(signup.getUserLoginName())) {
                    WpUser user = users.get(userIdx.get(signup.getUserLoginName()));
                    user.setSite(site);
                    site.setOwner(user);
                }
            } else {
                site.setOwner(defaultAdmin);
            }
        }

        return sites;
    }
}
