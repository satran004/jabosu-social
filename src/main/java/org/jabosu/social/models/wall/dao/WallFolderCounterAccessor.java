package org.jabosu.social.models.wall.dao;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

/**
 * Created by satya on 11/11/17.
 */
@Accessor
public interface WallFolderCounterAccessor {


    @Query("UPDATE wall_counters set counter = counter + ? where folder = ? and counter_type = ? and split_id = ? ")
    public ResultSet increaseCounter(long by, String folder, String counterType, int splitId);


    @Query("UPDATE wall_counters set counter = counter - ? where folder = ? and counter_type = ? and split_id = ? ")
    public ResultSet decreaseCounter(long by, String folder, String counterType, int splitId);
}
