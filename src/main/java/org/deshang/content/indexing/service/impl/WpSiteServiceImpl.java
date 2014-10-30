/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WpSiteServiceImpl implements WpSiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WpSiteServiceImpl.class);

    private final String DOMAIN_PATH_DELIMITER = "/";
    
    @Autowired
    private WpSiteRepository siteRepository;

    @Autowired
    private WpSignupRepository signupRepository;

    @Autowired
    private WpUserRepository userRepository;

    public List<WpSite> getAllSites() {
        LOGGER.debug("Enter getAllSites()");

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

        LOGGER.debug("Exit getAllSites()");
        return sites;
    }
}
