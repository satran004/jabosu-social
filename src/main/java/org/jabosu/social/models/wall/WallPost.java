package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jabosu.common.util.StringUtil;

/**
 *
 * @author satya
 */
@Table(name = "wall_posts")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallPost extends WallMessage {

    @Column(name = "wall_id")
    public String wallId;

    @Column(name = "is_private")
    public boolean isPrivate;

    @Column(name = "title")
    public String title;

    @Column(name = "video_url")
    public String videoUrl;


    @Column(name = "shared_on_category_wall")
    public boolean sharedOnCategoryWall;

    public String getWallId() {
        return wallId;
    }

    public void setWallId(String wallId) {
        this.wallId = wallId;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean getSharedOnCategoryWall() {
        return sharedOnCategoryWall;
    }

    public void setSharedOnCategoryWall(boolean sharedOnCategoryWall) {
        this.sharedOnCategoryWall = sharedOnCategoryWall;
    }

//    @Override
    public WallPostMetaData createPostMetaData() {
        WallPostMetaData metaData = new WallPostMetaData();
        metaData.id = this.id;
        metaData.path = this.path;
        metaData.ownerId = this.ownerId;
        metaData.ownerName = this.ownerName;
        metaData.postDate = this.postDate;
        metaData.content = StringUtil.wrap(this.content, 200);
        metaData.blocked = this.blocked;
        //metaData.type = this.type;

        metaData.title = this.title;
        metaData.sharedOnCategoryWall = this.sharedOnCategoryWall;
        metaData.type = this.type;

        return metaData;
    }
}
