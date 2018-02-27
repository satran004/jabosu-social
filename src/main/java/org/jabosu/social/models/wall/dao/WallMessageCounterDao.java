package org.jabosu.social.models.wall.dao;

import org.jabosu.common.dao.cassandra.dao.FolderCounterDao;
import org.jabosu.common.exception.DataAccessException;
import org.jabosu.social.models.wall.WallMessageCounter;

/**
 * Created by satya on 2/8/15.
 */
public class WallMessageCounterDao extends FolderCounterDao<WallMessageCounter> {

    private WallMessageCounterAccessor accessor;

    public WallMessageCounterDao() {
        super();

        accessor = manager.createAccessor(WallMessageCounterAccessor.class);
    }

    @Override
    public WallMessageCounter newInstance() {
        return new WallMessageCounter();
    }

    @Override
    public Class<WallMessageCounter> getDomainClass() {
        return WallMessageCounter.class;
    }

    @Override
    public void increment(Object folderId, String type, int splitId, int by) throws DataAccessException {
        accessor.increaseCounter(by, folderId.toString(), type.toString(), splitId );
    }

    @Override
    public void decrement(Object folderId, String type, int splitId, int by) throws DataAccessException {
        accessor.decreaseCounter(by, folderId.toString(), type.toString(), splitId );
    }
}
