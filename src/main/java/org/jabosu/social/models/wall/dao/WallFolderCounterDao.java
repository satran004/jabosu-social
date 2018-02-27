package org.jabosu.social.models.wall.dao;


import org.jabosu.common.dao.cassandra.dao.FolderCounterDao;
import org.jabosu.common.exception.DataAccessException;
import org.jabosu.social.models.wall.WallFolderCounter;

/**
 * Created by satya on 2/8/15.
 */
public class WallFolderCounterDao extends FolderCounterDao<WallFolderCounter> {

    private WallFolderCounterAccessor accessor;

    public WallFolderCounterDao() {

        super();

        accessor = manager.createAccessor(WallFolderCounterAccessor.class);
    }
    @Override
    public WallFolderCounter newInstance() {
        return new WallFolderCounter();
    }

    @Override
    public Class<WallFolderCounter> getDomainClass() {
        return WallFolderCounter.class;
    }

    @Override
    public void increment(Object folderId, String type, int splitId, int by) throws DataAccessException {
        accessor.increaseCounter(by, (String)folderId, type.toString(), splitId);
    }

    @Override
    public void decrement(Object folderId, String type, int splitId, int by) throws DataAccessException {
        accessor.decreaseCounter(by, (String)folderId, type.toString(), splitId);
    }
}
