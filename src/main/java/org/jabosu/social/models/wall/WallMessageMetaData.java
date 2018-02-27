package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import java.util.UUID;

/**
 * Created by satya on 12/11/17.
 */
@UDT(name="wall_message_metadata")
public class WallMessageMetaData {

    @Field(name = "id")
    public UUID id;

    @Field(name = "path")
    public String path;

    @Field(name = "owner_id")
    public String ownerId;

    @Field(name = "owner_name")
    public String ownerName;

    @Field(name="post_date")
    public long postDate;

    @Field(name = "content")
    public String content;

    @Field(name = "blocked")
    public boolean blocked;

    @Field(name="type")
    public WallMessage.WallMessageType type;


//    public static WallMessageMetaData populate(WallMessage post) {
//
//        WallMessageMetaData metaData = new WallMessageMetaData();
//        metaData.id = post.id;
//        metaData.path = post.path;
//        metaData.ownerId = post.ownerId;
//        metaData.ownerName = post.ownerName;
//        metaData.postDate = post.postDate;
//        metaData.content = StringUtil.wrap(post.content, 400);
//        metaData.blocked = post.blocked;
//        metaData.type = post.type;
//
//        return metaData;
//    }

}
