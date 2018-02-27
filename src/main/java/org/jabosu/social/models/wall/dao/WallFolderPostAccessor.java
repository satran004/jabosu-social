package org.jabosu.social.models.wall.dao;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.jabosu.social.models.wall.WallFolderPost;

import java.util.UUID;

/**
 * Created by satya on 11/11/17.
 */
@Accessor
public interface WallFolderPostAccessor {

    @Query("SELECT * from wall_folder_posts where folder = ? and bucket = ? ORDER BY post_id DESC limit ?")
    Result<WallFolderPost> getPrevMessages(String folder, int bucket, int count);

    @Query("SELECT * from wall_folder_posts where folder = ? and bucket = ? and post_id < ? ORDER BY post_id DESC limit ? ")
    Result<WallFolderPost> getPrevMessages(String folder, int bucket, UUID postId, int count);


    @Query("SELECT * from wall_folder_posts where folder = ? and bucket = ? ORDER BY post_id ASC limit ?")
    Result<WallFolderPost> getNextMessages(String folder, int bucket, int count);

    @Query("SELECT * from wall_folder_posts where folder = ? and bucket = ? and post_id > ?  ORDER BY post_id ASC limit ?")
    Result<WallFolderPost> getNextMessages(String folder, int bucket, UUID postId, int count);
}
