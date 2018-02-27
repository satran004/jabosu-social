package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jabosu.common.Model;

import java.util.UUID;

//import info.archinnov.achilles.annotations.*;
//import info.archinnov.achilles.type.NamingStrategy;

@Table(name = "wall_message_replies")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallMessageReply implements Model {

    @PartitionKey(value = 0)
    @Column(name = "message_id")
    public UUID messageId;

    @PartitionKey(value = 1)
    @Column(name = "bucket")
    public int bucket;

    @ClusteringColumn(value = 0)//, reversed = true)
    @Column(name = "reply_id")
//    @TimeUUID
    public UUID replyId;

    @Column(name = "message_metadata")
    @Frozen
    public WallMessageMetaData messageMetaData;

    //@Column(name = "message")
//    @JSON
    @Transient
    public WallMessage message;

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public int getBucket() {
        return bucket;
    }

    public void setBucket(int bucket) {
        this.bucket = bucket;
    }

    public UUID getReplyId() {
        return replyId;
    }

    public void setReplyId(UUID replyId) {
        this.replyId = replyId;
    }

    //    @Override
    public String key() {
        return messageId + ":" + bucket + replyId;
    }
//
//    public WallMessageRepliesKey getId() {
//        return id;
//    }
//
//    public void setId(WallMessageRepliesKey id) {
//        this.id = id;
//    }

    public WallMessage getMessage() {
        return message;
    }

    public void setMessage(WallMessage message) {
        this.message = message;
    }

    public WallMessageMetaData getMessageMetaData() {
        return messageMetaData;
    }

    public void setMessageMetaData(WallMessageMetaData messageMetaData) {
        this.messageMetaData = messageMetaData;
    }

    //    public static class WallMessageRepliesKey {
//
//        @PartitionKey(value = 1)
//        public UUID messageId;
//
//        @PartitionKey(value = 2)
//        public int bucket;
//
//        @ClusteringColumn(value = 1, reversed = true)
//        @TimeUUID
//        public UUID replyId;
//
//        public WallMessageRepliesKey() {
//
//        }
//
//        public WallMessageRepliesKey(UUID messageId, int bucket, UUID replyId) {
//            this.messageId = messageId;
//            this.bucket = bucket;
//            this.replyId = replyId;
//        }
//
//        public UUID getMessageId() {
//            return messageId;
//        }
//
//        public void setMessageId(UUID messageId) {
//            this.messageId = messageId;
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
//        public UUID getReplyId() {
//            return replyId;
//        }
//
//        public void setReplyId(UUID replyId) {
//            this.replyId = replyId;
//        }
//    }
}
