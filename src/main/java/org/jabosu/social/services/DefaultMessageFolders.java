package org.jabosu.social.services;


import org.jabosu.social.models.messages.FolderName;

/**
 *
 * @author satya
 */
public class DefaultMessageFolders {
    
    public final static String INBOX = "inbox";
    public final static String OUTBOX = "outbox";
    
    public final static FolderName INBOX_FOLDER = new FolderName(INBOX, "Inbox", "Inbox");
    public final static FolderName OUTBOX_FOLDER = new FolderName(OUTBOX, "Outbox", "Outbox", true);
}
