package org.jabosu.social.models.wall.dao;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.jabosu.social.models.wall.WallMessageReply;

import java.util.UUID;

/**
 * Created by satya on 11/11/17.
 */
@Accessor
public interface WallMessageRepliesAccessor {

    @Query("SELECT * from wall_message_replies where message_id = ? and bucket = ? ORDER BY reply_id DESC limit ?")
    Result<WallMessageReply> getPrevMessages(UUID folder, int bucket, int count);

    @Query("SELECT * from wall_message_replies where message_id = ? and bucket = ? and reply_id < ? ORDER BY reply_id DESC limit ?")
    Result<WallMessageReply> getPrevMessages(UUID folder, int bucket, UUID messageId, int count);


    @Query("SELECT * from wall_message_replies where message_id = ? and bucket = ? ORDER BY reply_id ASC limit ?")
    Result<WallMessageReply> getNextMessages(UUID folder, int bucket, int count);

    @Query("SELECT * from wall_message_replies where message_id = ? and bucket = ? and reply_id > ? ORDER BY reply_id ASC limit ?")
    Result<WallMessageReply> getNextMessages(UUID folder, int bucket, UUID messageId, int count);
}
