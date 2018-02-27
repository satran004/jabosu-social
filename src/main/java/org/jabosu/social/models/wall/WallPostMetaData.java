package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;
import org.jabosu.common.util.StringUtil;

import java.util.UUID;

/**
 * Created by satya on 2/8/15.
 */
@UDT(name = "post_metadata")
public class WallPostMetaData  {

    @Field
    public UUID id;

    @Field
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

    @Field(name = "title")
    public String title;

    @Field(name="type")
    public WallMessage.WallMessageType type;

    @Field(name = "shared_on_category_wall")
    public boolean sharedOnCategoryWall;

    public static WallPostMetaData populate(WallPost post) {

        WallPostMetaData metaData = new WallPostMetaData();
        metaData.id = post.id;
        metaData.path = post.path;
        metaData.ownerId = post.ownerId;
        metaData.ownerName = post.ownerName;
        metaData.postDate = post.postDate;
        metaData.content = StringUtil.wrap(post.content, 400);
        metaData.blocked = post.blocked;
        metaData.title = post.title;
        metaData.type = post.type;
        metaData.sharedOnCategoryWall = post.sharedOnCategoryWall;

        return metaData;
    }
}
