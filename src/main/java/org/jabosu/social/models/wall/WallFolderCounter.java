package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jabosu.common.CounterModel;

/**
 * Created by satya on 2/8/15.
 */
@Table(name = "wall_counters")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallFolderCounter extends CounterModel {

    //split index type
    public static String SPLIT_INDEX = "SPLIT_INDEX";


//    static {
//        CodecRegistry.DEFAULT_INSTANCE
//                .register(new EnumNameCodec<WallFolderCounterType>(WallFolderCounterType.class));
//    }

//    @Override
//    public CounterType getCounterType() {
//        return counterType;
//    }
//
//    @Override
//    public void setCounterType(WallFolderCounterType counterType) {
//        this.counterType = counterType;
//    }
//
//    public static class WallFolderCounterType {
//        public static String SPLIT_INDEX = "SPLIT_INDEX";
//    }

}



