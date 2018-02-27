package org.jabosu.social.models.messages;

import org.jabosu.common.MessageFolderType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author satya
 */
public class FolderName implements Serializable {
    public String id;
    public String name;
    public String title; //title in message window
    public boolean isOutbox = false;
    public MessageFolderType folderType;

    public List<FolderName> subFolders = new ArrayList();

    public long count;

    public FolderName() {

    }

    public FolderName(String id, String name, String title) {
        this.id = id;
        this.name = name;
    }

    public FolderName(String id, String name, String title, boolean isOutbox) {
        this(id, name, title);
        this.isOutbox = isOutbox;
    }

    public void addFolder(FolderName folderName) {
        subFolders.add(folderName);
    }
}
