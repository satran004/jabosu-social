package org.jabosu.social.models.wall.dao;


import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import org.jabosu.common.dao.cassandra.dao.BaseDao;
import org.jabosu.social.models.wall.WallFolder;
import org.jabosu.social.util.WallFolderType;

/**
 * Created by satya on 2/8/15.
 */
public class WallFolderDao extends BaseDao<WallFolder, String> {

    static {
        CodecRegistry.DEFAULT_INSTANCE
                .register(new EnumNameCodec<WallFolderType>(WallFolderType.class));
    }

    public WallFolderDao() {
        super();

    }

    @Override
    public Class<WallFolder> getDomainClass() {
        return WallFolder.class;
    }
}
