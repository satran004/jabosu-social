package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * Created by satya on 2/8/15.
 */
@Table(name = "wall_folder_posts")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallFolderPost {
//
//    @CompoundPrimaryKey
//    @JsonUnwrapped  //flatten the id object during json serialization
//    public WallFolderMessagesKey id;

    @PartitionKey(value = 0)
    public String folder;

    @PartitionKey(value = 1)
    public int bucket;

    @ClusteringColumn(value = 0)//, reversed = true)
    @Column(name = "post_id")
//    @TimeUUID
    public UUID postId;

    @Column(name = "post_metadata")
    @Frozen
//    @JSON
//    @JsonUnwrapped
    public WallPostMetaData postMetadata;

    @Transient
    public WallPost post;

//    public WallFolderMessagesKey getId() {
//        return id;
//    }
//
//    public void setId(WallFolderMessagesKey id) {
//        this.id = id;
//    }


    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getBucket() {
        return bucket;
    }

    public void setBucket(int bucket) {
        this.bucket = bucket;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public WallPostMetaData getPostMetadata() {
        return postMetadata;
    }

    public void setPostMetadata(WallPostMetaData metadata) {
        this.postMetadata = metadata;
    }

    public WallPost getPost() {
        return post;
    }

    public void setPost(WallPost post) {
        this.post = post;
    }

    //    public static class WallFolderMessagesKey {
//
//        @PartitionKey(value = 1)
//        public String folder;
//
//        @PartitionKey(value = 2)
//        public int bucket;
//
//        @ClusteringColumn(value = 1, reversed = true)
//        @TimeUUID
//        public UUID postId;
//
//        public WallFolderMessagesKey() {}
//
//        public  WallFolderMessagesKey(String folder, int bucket, UUID postId) {
//            this.folder = folder;
//            this.bucket = bucket;
//            this.postId = postId;
//        }
//
//        public String getFolder() {
//            return folder;
//        }
//
//        public void setFolder(String folder) {
//            this.folder = folder;
//        }
//
//        public int getBucket() {
//            return bucket;
//        }
//
//        public void setBucket(int bucket) {
//            this.bucket = bucket;
//        }
//
//        public UUID getPostId() {
//            return postId;
//        }
//
//        public void setPostId(UUID postId) {
//            this.postId = postId;
//        }
//    }


}
