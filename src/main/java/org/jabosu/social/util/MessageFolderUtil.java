package org.jabosu.social.util;

import org.elasticsearch.common.collect.Tuple;
import org.jabosu.common.MessageFolderType;
import org.jabosu.common.util.StringUtil;
import org.jabosu.common.util.Triplet;
import org.jabosu.social.services.DefaultMessageFolders;

/**
 *
 * @author satya
 */
public class MessageFolderUtil {
    
    public static String getMessageFolderKeyForUser(String userId, String folderName) {
        return userId + ":" + folderName;
    }
    
    public static String getMessageFolderKeyForService(String countryCode, String serviceId, String folderName) {
        return countryCode + ":" + serviceId + ":" + folderName;
    }
    
    public static String getUserInboxKey(String userId) {
        return getMessageFolderKeyForUser(userId, DefaultMessageFolders.INBOX);
    }
    
    public static String getUserOutboxKey(String userId) {
        return getMessageFolderKeyForUser(userId, DefaultMessageFolders.OUTBOX);
    }
    
    public static String getUserServiceInboxKey(String userId, String countryCode, String serviceId) {
        return getMessageFolderKeyForUser(userId, getUserServiceInboxFolderName(countryCode, serviceId));
    }
    
    public static String getUserServiceInboxFolderName(String countryCode, String serviceId) {
        return countryCode + "_" + serviceId + "_" + DefaultMessageFolders.INBOX;
    }
    
    public static Tuple<String, String> getServiceIdFromUserServiceInboxFolder(String folderId) {
        if(StringUtil.isEmpty(folderId))
            return null;
        
        String[] splits = folderId.split("_");
        if(splits.length != 3)
            return null;
        else {
            return new Tuple(splits[0],splits[1]);
        }
    }

    public static Triplet<String, String, String> parseServiceFolderId(String folderId) {
        if(StringUtil.isEmpty(folderId))
            return null;

        String[] splits = folderId.split(":");
        if(splits.length != 3)
            return null;
        else {
            return new Triplet(splits[0],splits[1], splits[2]);
        }
    }
    
    public static boolean isUserServiceInboxFolder(String folderId) {
        if(StringUtil.isEmpty(folderId))
            return false;
        
        String[] splits = folderId.split("_");
        if(splits.length == 3)
            return true;
        else
            return false;
    }
    
    public static String getServiceInboxKey(String countryCode, String serviceId) {
        return getMessageFolderKeyForService(countryCode, serviceId, DefaultMessageFolders.INBOX);
    }
    
    public static String getServiceOutboxKey(String countryCode, String serviceId) {
        return getMessageFolderKeyForService(countryCode, serviceId, DefaultMessageFolders.OUTBOX);
    }
    
    public static MessageFolderType getMessageFolderType(String mailBoxId, String userId) {
        if(mailBoxId != null && mailBoxId.startsWith(userId + ":"))
            return MessageFolderType.USER;
        else
            return MessageFolderType.SERVICE;
    }
    
    public static String getProfileMailBoxUser(String countryCode, String serviceId) {
        return countryCode + ":" + serviceId;
    }
    
}
