package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jabosu.common.util.StringUtil;
import org.jabosu.common.util.cassandra.CassandraPersistenceHelper;

import java.util.UUID;

@Table(name = "wall_messages")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallMessage {

    @PartitionKey
//    @TimeUUID
    public UUID id;

    @Column(name = "parent_id")
    public UUID parentId;

    @Column(name = "path")
    public String path;

    @Column(name = "owner_id")
    public String ownerId;

    @Column(name = "owner_email")
    public String ownerEmail;

    @Column(name = "owner_name")
    public String ownerName;

    @Column(name = "post_date")
    public long postDate;

    @Column(name = "content")
    public String content;

    @Column(name = "blocked")
    public boolean blocked;

    @Column(name = "current_split_id")
    public int currentSplitId;

    @Column(name = "has_children")
    public boolean hasChildren; //check if there is any comment or replies

    @Column(name = "type")
//    @JSON
    public WallMessageType type;


    static {
        CassandraPersistenceHelper.getInstance().registerOrdinalEnum(WallMessageType.class);
    }
      
    public String key() {
        return id.toString();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public long getPostDate() {
        return postDate;
    }

    public void setPostDate(long postDate) {
        this.postDate = postDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getCurrentSplitId() {
        return currentSplitId;
    }

    public void setCurrentSplitId(int currentSplitId) {
        this.currentSplitId = currentSplitId;
    }

    public boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public static enum WallMessageType {
        POST, COMMENT, REPLY
    }


    public WallMessageMetaData createMessageMetaData() {
        WallMessageMetaData metaData = new WallMessageMetaData();
        metaData.id = this.id;
        metaData.path = this.path;
        metaData.ownerId = this.ownerId;
        metaData.ownerName = this.ownerName;
        metaData.postDate = this.postDate;
        metaData.content = StringUtil.wrap(this.content, 200);
        metaData.blocked = this.blocked;
        metaData.type = this.type;

        return metaData;
    }
}


