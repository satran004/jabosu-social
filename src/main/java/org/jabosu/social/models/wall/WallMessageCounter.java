package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jabosu.common.CounterModel;

/**
 * Created by satya on 2/8/15.
 */
@Table(name = "wall_message_counters")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallMessageCounter extends CounterModel {

    public final static String SPLIT_INDEX = "SPLIT_INDEX";
    public final static String REPLIES = "REPLIES";


//    public static enum WallMessageCounterType implements CounterType {
//        SPLIT_INDEX, REPLIES
//    }
}
