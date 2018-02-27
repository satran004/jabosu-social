package org.jabosu.social.models.wall.dao;

import org.jabosu.common.dao.cassandra.dao.WideRowDao;
import org.jabosu.common.exception.DataAccessException;
import org.jabosu.common.util.Tuple1;
import org.jabosu.common.util.Tuple2;
import org.jabosu.social.models.wall.WallFolder;
import org.jabosu.social.models.wall.WallFolderCounter;
import org.jabosu.social.models.wall.WallFolderPost;

import java.util.List;
import java.util.UUID;

/**
 * Created by satya on 2/8/15.
 */
public class WallFolderMessagesDao extends WideRowDao<WallFolderPost, Tuple2<String, Integer>, Tuple1<UUID>> {

    private WallFolderCounterDao wallFolderCounterDao = new WallFolderCounterDao();
    private WallFolderDao wallFolderDao = new WallFolderDao();
    private WallFolderPostAccessor accessor;


    public WallFolderMessagesDao() {
        super();

        accessor = manager.createAccessor(WallFolderPostAccessor.class);
    }

    @Override
    public void updateKeyIfRequired(WallFolderPost bean) throws DataAccessException {

       // CounterModel.CounterKey key
        //        = new CounterModel.CounterKey(bean.id.folder, WallFolderCounter.WallFolderCounterType.SPLIT_INDEX, bean.id.bucket);

       // WallFolderCounter folderCounter = manager.find(WallFolderCounter.class, key);

        wallFolderCounterDao.increment(bean.getFolder(), WallFolderCounter.SPLIT_INDEX, bean.getBucket(), 1);

        WallFolderCounter wallFolderCounter = wallFolderCounterDao.get(bean.getFolder(), WallFolderCounter.SPLIT_INDEX, bean.getBucket());

//        if (folderCounter == null) {
//            folderCounter = new WallFolderCounter();
//
//            folderCounter.setId(key);
//
//            folderCounter.setCounter(CounterBuilder.incr());
//            manager.insert(folderCounter);
//
//            folderCounter = manager.find(WallFolderCounter.class, key);
//        }
//
//        folderCounter.getCounter().incr();
//
//        manager.insertOrUpdate(folderCounter);

        if (wallFolderCounter.getCounter() > maxPartitionSize()) { //If max partition size get next split id

            //add to the split index
            bean.setBucket(bean.getBucket() + 1);

            //update message metadata bucket id
//            bean.getMessage().setBucket(String.valueOf(bean.getId().bucket));

            WallFolder wallFolder = wallFolderDao.get(bean.getFolder());//manager.find(WallFolder.class, bean.id.folder); //try to get only currentsplitid

            wallFolder.setCurrentSplitId(bean.getBucket());

            wallFolderDao.save(wallFolder);

//            manager.update(wallFolder);
        }
    }

//    public WallFolderMessagesKey getCurrentPartitionKey(Object folderId) {
//        WallFolder wallFolder = manager.find(WallFolder.class, folderId);
//
//        WallFolderMessagesKey key = new WallFolderMessagesKey();
//        key.setFolder((String) folderId);
//        key.bucket = wallFolder.currentSplitId;
//
//        return key;
//    }
//    @Override
//    public WallFolderMessagesKey getPrevPartitionKey(WallFolderMessagesKey key, WallFolderMessagesKey firstKey) {
//        if(key == null)
//            return null;
//
//        if(key.getBucket() == 0)
//            return null;
//
//        WallFolderMessagesKey prevKey = new WallFolderMessagesKey(key.folder, key.getBucket() - 1, null);
//
//        return prevKey;
//    }
//
//    @Override
//    public WallFolderMessagesKey getNextPartitionKey(WallFolderMessagesKey key, WallFolderMessagesKey lastKey) {
//        if(key == null)
//            return null;
//
//
//        WallFolderMessagesKey nextKey = new WallFolderMessagesKey(key.folder, key.getBucket() + 1, null);
//
//        if(nextKey.getBucket() > lastKey.getBucket())
//            return null;
//
//        return nextKey;
//    }
//
//    @Override
//    public Object[] getPartitionKeys(WallFolderMessagesKey key) {
//        return new Object[]{key.folder, key.bucket};
//    }
//
//    @Override
//    public Object[] getClusteringKeys(WallFolderMessagesKey key) {
//
//        if(key.postId == null)
//            return null;
//
//        return new Object[]{key.postId};
//    }


    @Override
    public Tuple2<String, Integer> getPrevPartitionKey(Tuple2<String, Integer> key, Tuple2<String, Integer> firstKey) {
        if(key == null)
            return null;

        if(key._2 == 0) //buckete
            return null;

        Tuple2<String, Integer> prevKey = new Tuple2(key._1, key._2 - 1);

        return prevKey;
    }

    @Override
    public Tuple2<String, Integer> getNextPartitionKey(Tuple2<String, Integer> key, Tuple2<String, Integer> lastKey) {
        if(key == null)
            return null;

        Tuple2<String, Integer> nextKey = new Tuple2(key._1, key._2  + 1);

        if(nextKey._2 > lastKey._2)
            return null;

        return nextKey;

    }

//    @Override
//    public List<WallFolderPost> getPrevMessages(Tuple2<String, Integer> partition, int count, Tuple2<String, Integer> firstKey) throws DataAccessException {
//        return super.getPrevMessages(stringIntegerTuple2, count, firstKey);
//    }

    @Override
    protected List<WallFolderPost> getPrevMessages(Tuple2<String, Integer> partitionKeys, Tuple1<UUID> clusteringKeys, int count) {
        if(clusteringKeys._1 == null)
            return accessor.getPrevMessages(partitionKeys._1, partitionKeys._2, count).all();
        else
            return accessor.getPrevMessages(partitionKeys._1, partitionKeys._2, clusteringKeys._1, count).all();

    }

    @Override
    protected List<WallFolderPost> getNextMessages(Tuple2<String, Integer> partitionKeys, Tuple1<UUID> clusteringKeys, int count) {
        if(clusteringKeys._1 == null)
            return accessor.getNextMessages(partitionKeys._1, partitionKeys._2, count).all();
        else
            return accessor.getNextMessages(partitionKeys._1, partitionKeys._2, clusteringKeys._1, count).all();
    }
    @Override
    public Class<WallFolderPost> getDomainClass() {
        return WallFolderPost.class;
    }

    public int maxPartitionSize() {
        return 5;
    }
}
