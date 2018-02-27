package org.jabosu.social.services;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author satya
 */
public class Followers {

    public String itemId;
    public String country;
    public String type;
    public long followersCount = 0;
    public Set<String> followers;
   
    public Followers() {
        followers = new HashSet();
    }
    
    public boolean alreadyFollowing(String id) {
        if(followers == null)
            return false;
        else
            return followers.contains(id);
    }
}
