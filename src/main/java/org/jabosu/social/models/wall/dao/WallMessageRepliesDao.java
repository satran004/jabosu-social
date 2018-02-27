package org.jabosu.social.models.wall.dao;

import org.jabosu.common.dao.cassandra.dao.WideRowDao;
import org.jabosu.common.exception.DataAccessException;
import org.jabosu.common.util.Tuple1;
import org.jabosu.common.util.Tuple2;
import org.jabosu.social.models.wall.WallMessage;
import org.jabosu.social.models.wall.WallMessageCounter;
import org.jabosu.social.models.wall.WallMessageReply;
import org.jabosu.social.models.wall.WallPost;

import java.util.List;
import java.util.UUID;

/**
 * Created by satya on 2/8/15.
 */
public class WallMessageRepliesDao extends WideRowDao<WallMessageReply, Tuple2<UUID, Integer>, Tuple1<UUID>> {

    //private WallFolderCounterDao wallFolderCounterDao = new WallFolderCounterDao();
    private WallMessageCounterDao wallMessageCounterDao = new WallMessageCounterDao();

    private WallMessageDao wallMessageDao = new WallMessageDao();
    private WallPostDao wallPostDao = new WallPostDao();

    private WallMessageRepliesAccessor accessor;

    public WallMessageRepliesDao() {
        super();

        accessor =  manager.createAccessor(WallMessageRepliesAccessor.class);
    }

    @Override
    public void updateKeyIfRequired(WallMessageReply bean) throws DataAccessException {

//        CounterKey key
//                = new CounterKey(bean.id.messageId.toString(), WallMessageCounter.WallMessageCounterType.SPLIT_INDEX, bean.id.bucket);
//

        wallMessageCounterDao.increment(bean.getMessageId(),  WallMessageCounter.SPLIT_INDEX, bean.getBucket(), 1);

        WallMessageCounter folderCounter = wallMessageCounterDao.get(bean.getMessageId().toString(), WallMessageCounter.SPLIT_INDEX, bean.getBucket());//manager.find(WallMessageCounter.class, key);

//        if(folderCounter == null) {
//            folderCounter = new WallMessageCounter();
//
//            folderCounter.setId(key);
//
//            folderCounter.setCounter(CounterBuilder.incr());
//            manager.insert(folderCounter);
//
//            folderCounter = manager.find(WallMessageCounter.class, key);
//        }
//
//        folderCounter.getCounter().incr();
//
//        manager.insertOrUpdate(folderCounter);

        if(folderCounter.getCounter() > maxPartitionSize()) { //If max partition size get next split id

            //add to the split index
            bean.setBucket(bean.getBucket() + 1);

            //bean.message.path = bean.message.path + ":" + bean.id.bucket; //add bucket to path
            //update message metadata bucket id
//            bean.getMessage().setBucket(String.valueOf(bean.getId().bucket));

            if(bean.message != null && bean.message.type != null && (bean.message.type.equals(WallMessage.WallMessageType.REPLY))) { //for reply search in wall_messages

                WallMessage wallMessage = wallMessageDao.get(bean.getMessageId());//manager.find(WallMessage.class, bean.id.messageId); //try to get only currentsplitid
                wallMessage.setCurrentSplitId(bean.getBucket());
                //manager.update(wallMessage);
                wallMessageDao.save(wallMessage);

            } else {
                WallPost wallPost = wallPostDao.get(bean.getMessageId());//manager.find(WallPost.class, bean.id.messageId); //try to get only currentsplitid
                wallPost.setCurrentSplitId(bean.getBucket());
                //manager.update(wallPost);
                wallPostDao.save(wallPost);
            }
        }
    }

//    public WallMessageRepliesKey getCurrentPartitionKey(Object folderId) {
//        WallPost wallPost = manager.find(WallPost.class, folderId);
//
//        WallMessageRepliesKey key = new WallMessageRepliesKey();
//        key.setMessageId((UUID) folderId);
//        key.bucket = wallPost.currentSplitId;
//
//        return key;
//    }

//    @Override
//    public WallMessageRepliesKey getPrevPartitionKey(WallMessageRepliesKey wallMessageRepliesKey, WallMessageRepliesKey firstKey) {
//
//        if(wallMessageRepliesKey == null)
//            return null;
//
//        if(wallMessageRepliesKey.getBucket() == 0)
//            return null;
//
//        WallMessageRepliesKey prevKey = new WallMessageRepliesKey(wallMessageRepliesKey.messageId, wallMessageRepliesKey.getBucket() - 1, null);
//
//        return prevKey;
//    }
//
//    @Override
//    public WallMessageRepliesKey getNextPartitionKey(WallMessageRepliesKey key, WallMessageRepliesKey lastKey) {
//        if(key == null)
//            return null;
//
//
//        WallMessageRepliesKey nextKey = new WallMessageRepliesKey(key.messageId, key.getBucket() + 1, null);
//
//        if(nextKey.getBucket() > lastKey.getBucket())
//            return null;
//
//        return nextKey;
//    }

//    @Override
//    public Object[] getPartitionKeys(WallMessageRepliesKey key) {
//        return new Object[]{key.messageId, key.bucket};
//    }
//
//    @Override
//    public Object[] getClusteringKeys(WallMessageRepliesKey key) {
//        if(key.replyId == null)
//            return null;
//
//        return new Object[] {key.replyId};
//    }


    @Override
    public Tuple2<UUID, Integer> getPrevPartitionKey(Tuple2<UUID, Integer> key, Tuple2<UUID, Integer> firstKey) {
        if(key == null)
            return null;

        if(key._2 == 0) //buckete
            return null;

        Tuple2<UUID, Integer> prevKey = new Tuple2(key._1, key._2-1);

        return prevKey;
    }

    @Override
    public Tuple2<UUID, Integer> getNextPartitionKey(Tuple2<UUID, Integer> key, Tuple2<UUID, Integer> lastKey) {
        if(key == null)
            return null;

        Tuple2<UUID, Integer> nextKey = new Tuple2(key._1, key._2  + 1);

        if(nextKey._2 > lastKey._2)
            return null;

        return nextKey;

    }

    @Override
    protected List<WallMessageReply> getPrevMessages(Tuple2<UUID, Integer> partitionKeys, Tuple1<UUID> clusteringKeys, int count) {
        if(clusteringKeys._1 == null)
            return accessor.getPrevMessages(partitionKeys._1, partitionKeys._2, count).all();
        else
            return accessor.getPrevMessages(partitionKeys._1, partitionKeys._2, clusteringKeys._1, count).all();

    }

    @Override
    protected List<WallMessageReply> getNextMessages(Tuple2<UUID, Integer> partitionKeys, Tuple1<UUID> clusteringKeys, int count) {
        if(clusteringKeys._1 == null)
            return accessor.getNextMessages(partitionKeys._1, partitionKeys._2, count).all();
        else
            return accessor.getNextMessages(partitionKeys._1, partitionKeys._2, clusteringKeys._1, count).all();
    }

    @Override
    public Class<WallMessageReply> getDomainClass() {
        return WallMessageReply.class;
    }

    public int maxPartitionSize() {
        return 5;
    }

}
