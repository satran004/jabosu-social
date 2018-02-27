package org.jabosu.social.services.impl.wall;

import com.datastax.driver.core.utils.UUIDs;
import org.jabosu.common.auth.User;
import org.jabosu.common.exception.BusinessException;
import org.jabosu.common.exception.DataAccessException;
import org.jabosu.common.util.StringUtil;
import org.jabosu.common.util.Tuple1;
import org.jabosu.common.util.Tuple2;
import org.jabosu.social.models.wall.*;
import org.jabosu.social.models.wall.dao.*;
import org.jabosu.social.services.wall.WallMessageService;
import org.jabosu.social.services.wall.WallNotFoundException;
import org.jabosu.social.util.WallFolderType;
import org.jabosu.social.util.WallFolderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author satya
 */
public class WallMessageServiceImpl implements WallMessageService {

    private final static Logger logger = LoggerFactory.getLogger(WallMessageServiceImpl.class.getName());

//    @Inject
//    private UserService userService;
//
//    @Inject
//    private IDGenerationService idGenerationService;
//
//    @Inject
//    private ProfileService profileService;

    private static WallFolderDao wallDao = new WallFolderDao();
    private static WallPostDao conversationDao = new WallPostDao();
    private static WallMessageDao wallMsgDao = new WallMessageDao();
    private static WallFolderMessagesDao wallFolderMessagesDao = new WallFolderMessagesDao();
    private static WallMessageRepliesDao repliesDao = new WallMessageRepliesDao();
    //    private static WallMessageDao<WallMessage, WallMessageNotification> messageDao = new WallMessageDao();

    @Override
    public WallFolderPost postMessage(User user, String wallId, WallPost post)
            throws BusinessException, WallNotFoundException {

        if(StringUtil.isEmpty(wallId)) {
            throw new WallNotFoundException("Wall Id cannot be null. " + wallId);
        }
        WallFolder wall = getWallForder(wallId);

        if(wall == null) {
            wall = new WallFolder();
            wall.id = wallId;

            wall = createWall(user, wall);
        }

        post.id = UUIDs.timeBased(); //idGenerationService.getNextWallMessageId();

        post.ownerId = user.getUserId();
        post.ownerEmail = user.getEmail();
        post.ownerName = user.getDisplayName();

        //conversation.type = wall.type;
        post.wallId = wall.id;
//      conversation.messageFolderType = MessageFolderType.WALL;
        post.postDate = System.currentTimeMillis();
        post.type = WallMessage.WallMessageType.POST;

        try {
            post = conversationDao.save(post);
        } catch (DataAccessException ex) {
            throw new BusinessException("Unable to persist contact message ", ex);
        }

//      WallPostNotification convNotification = new WallPostNotification();
//      convNotification.id = conversation.id;

        WallFolderPost wallFolderMessages = new WallFolderPost();
//        WallFolderMessagesKey wallFolderMessagesKey = new WallFolderMessagesKey(wall.id, wall.currentSplitId, post.id);

//        wallFolderMessages.id = wallFolderMessagesKey;
        wallFolderMessages.setFolder(wall.getId());
        wallFolderMessages.setBucket(wall.getCurrentSplitId());
        wallFolderMessages.setPostId(post.getId());

        wallFolderMessages.setPostMetadata(post.createPostMetaData());
        wallFolderMessages.post = post;//.createPostMetaData();//check if this can be removed

        try {
            wallFolderMessages = wallFolderMessagesDao.add(wallFolderMessages);
        } catch (DataAccessException ex) {
            throw new BusinessException("Unable to add the conversation to wall", ex);
        }

        return wallFolderMessages;
    }

    @Override
    public WallMessageReply postComment(User user, String wallId, String conversationId, WallMessage message)
            throws BusinessException {

        if(StringUtil.isEmpty(wallId)) {
            throw new BusinessException("Invalid wall id : " + wallId);
        }

        WallPost conversation = null;

        UUID postId = null;

        try {
            postId = UUID.fromString(conversationId);
        } catch(Exception e) {
            throw new BusinessException("Invalid wall post id : " + conversationId);
        }

        try {
            conversation = conversationDao.get(postId);

            if(!conversation.hasChildren) { //update has children
                conversation.setHasChildren(true);
                conversationDao.save(conversation);
            }
        } catch (DataAccessException ex) {
            throw new BusinessException("Error getting conversation message with id : " + conversationId);
        }

        if (conversation == null) {
            throw new BusinessException("Conversation not found for conversationId: " + conversationId);
        }

        message.path = conversation.id.toString();
        message.type = WallMessage.WallMessageType.COMMENT;

        message = saveMessage(user, conversation.id, message); //save message

        //TODO WallMessage messageMetaData = message.createMetaData();
        //add to conversation.
        WallMessageReply reply = addCommentToConversation(conversation, message, user.getUserId());

        //Just to return ..add actual message body
        reply.setMessage(message);

//        try {
//            WallMessage msg = reply.comment;
//            msg.content = "";
//
//            wallMsgDao.save(msg);
//        } catch (DataAccessException e) {
//            throw new BusinessException("Could not save the message");
//        }

        //add to wall folder
        //_addMessageToWallFolder(wallId, message, user.userId);

        return reply;
    }

    @Override
    public WallMessageReply postReply(User user, String wallId, String messageId, WallMessage replyMessage)
            throws BusinessException {

        if(StringUtil.isEmpty(wallId)) {
            throw new BusinessException("Invalid wall id : " + wallId);
        }

        WallMessage mainMessage = null;

        UUID msgId = null;

        try {
            msgId = UUID.fromString(messageId);
            mainMessage = wallMsgDao.get(msgId);

            if(!mainMessage.hasChildren) { //update has children
                mainMessage.setHasChildren(true);
                wallMsgDao.save(mainMessage);
            }

        } catch (Exception ex) {
            throw new BusinessException("Error getting  message with id : " + messageId);
        }

        if (mainMessage == null) {
            throw new BusinessException("Message not found for messageId: " + messageId);
        }

//        if(mainMessage.id.equals(mainMessage.postId)) {
//            throw new BusinessException("Message id and conversation id both are same."
//                    + " Looks like this is a conversation message."
//                    + " You need to call postComment instead of postReply.");
//        }

        // WallMessageReplies.WallMessageRepliesKey commentKey = new WallMessageReplies.WallMessageRepliesKey(messageId, )


        replyMessage.path = mainMessage.path + "/" + mainMessage.id;
        replyMessage.type = WallMessage.WallMessageType.REPLY;

        WallMessage retReplyMessage = saveMessage(user, mainMessage.id, replyMessage); //save message and add to conversation

        // WallMessage replyMsgMetaData = retReplyMessage.createMetaData();
        //add reply to message
        WallMessageReply reply = addReplyToMessage(mainMessage, retReplyMessage, user.getUserId());


        //replace actual message body to return
        reply.setMessage(retReplyMessage);
//        replyMessage = reply.message;
//        replyMessage.content = "";
//
//        try {
//            wallMsgDao.save(replyMessage);
//        } catch(Exception e) {
//            throw new BusinessException("Reply message could not  be saved");
//        }


        //_addMessageToWallFolder(wallId, retReplyMessage, user.userId); //add to wall folder

        return reply;
    }

    @Override
    public WallFolder getWall(String wallId) throws BusinessException {
        return getWallForder(wallId);
    }

    @Override
    public WallFolder createWall(User user, WallFolder wall) throws BusinessException {

        if(wall == null || StringUtil.isEmpty(wall.id)) {
            throw new BusinessException("Wall Id or wall cannot be null");
        }

        WallFolder persistWall = getWallForder(wall.id);

        if (persistWall != null) //wall already exists
        {
            return persistWall;
        }

        //Wall doesnt exist create
        WallFolder wallFolder = new WallFolder();
        wallFolder.id = wall.id;
      /*  wallFolder.countryCode = country;
        wallFolder.serviceId = serviceId;
        wallFolder.type = WallFolderType.SERIVCE_WALL;*/

        wallFolder.setCreatedBy(user.getUserId());

        try {
            wall = wallDao.save(wallFolder);
        } catch (DataAccessException ex) {
            throw new BusinessException("Wall folder could not be created. " + wallFolder, ex);

        }


        /** TODO
        switch (WallFolderUtil.getWallType(wall.id)) {
            case SERIVCE_WALL:
                wall = createServiceWall(user, wall.countryCode, wall.serviceId);
                break;
            case CATEGORY_WALL:
                wall = createCategoryWall(user, wall.countryCode, wall.city, wall.category);
                break;
            case DEAL_WALL:
                wall = createDealWall(user, wall.countryCode, wall.serviceId, wall.dealId);
                break;
            case USER_WALL:
                wall = createUserWall(user);
                break;
            case UNDEFINED:
                throw new BusinessException("Undefined wall type. Wall could not be created. wallId: ", wall.id);

        }**/

        return wall;
    }
/**
    @Override
    public WallFolder createServiceWall(User user, String country, String serviceId) throws BusinessException {

        if(StringUtil.isEmpty(country) || StringUtil.isEmpty(serviceId)) {
            throw new BusinessException("Wall canont be created. Null country or serviceId. country: "
                    + country + ", serviceId: " + serviceId);
        }

        ServiceProfile profile = profileService.getProfileDetails(serviceId, country);

        if(profile == null)
            throw new BusinessException("Wall could not be created. Profile not found [country: " + country + ", serviceId: ", serviceId + "]");


        String wallId = WallFolderUtil.getServiceWallId(country, serviceId);

        WallFolder wall = getWallForder(wallId);

        if(wall != null)
            return wall;

        WallFolder wallFolder = new WallFolder();
        wallFolder.id = wallId;
        wallFolder.countryCode = country;
        wallFolder.serviceId = serviceId;
        wallFolder.type = WallFolderType.SERIVCE_WALL;

        wallFolder.setCreatedBy(user.getUserId());

        try {
            wall = wallDao.save(wallFolder);
        } catch (DataAccessException ex) {
            throw new BusinessException("Wall folder could not be created. " + wallFolder, ex);

        }

        return wall;
    }

    @Override
    public WallFolder createDealWall(User user, String country, String serviceId, String dealId) throws BusinessException {

        if(StringUtil.isEmpty(country) || StringUtil.isEmpty(serviceId) || StringUtil.isEmpty(dealId)) {
            throw new BusinessException("Wall canont be created. Null country or serviceId or dealId. country: "
                    + country + ", serviceId: " + serviceId + ", dealId: " + dealId);
        }

        String wallId = WallFolderUtil.getDealWallId(country, serviceId, dealId);

        WallFolder wall = getWallForder(wallId);

        if(wall != null)
            return wall;

        WallFolder wallFolder = new WallFolder();
        wallFolder.id = wallId;
        wallFolder.countryCode = country;
        wallFolder.serviceId = serviceId;
        wallFolder.dealId = dealId;
        wallFolder.type = WallFolderType.DEAL_WALL;

        wallFolder.setCreatedBy(user.getUserId());

        try {
            wall = wallDao.save(wallFolder);
        } catch (DataAccessException ex) {
            throw new BusinessException("Wall folder could not be created."  + wallFolder, ex);

        }

        return wall;
    }

    @Override
    public WallFolder createCategoryWall(User user, String country, String city, String category) throws BusinessException {

        if(StringUtil.isEmpty(country) || StringUtil.isEmpty(city) || StringUtil.isEmpty(category)) {
            throw new BusinessException("Wall canont be created. Null country or city or category. country: "
                    + country + ", city: " + city + ", category: " + category);
        }

        Category categoryObj = SystemConfig.getCategory(category);
        if(categoryObj == null) {
            throw new BusinessException("Wall could not be created. Invalid category: " + category);
        }

        String wallId = WallFolderUtil.getCategoryWallId(country, city, category);

        WallFolder wall = getWallForder(wallId);

        if(wall != null)
            return wall;

        WallFolder wallFolder = new WallFolder();
        wallFolder.id = wallId;
        wallFolder.countryCode = country;
        wallFolder.city = city;
        wallFolder.category = category;
        wallFolder.type = WallFolderType.CATEGORY_WALL;

        wallFolder.setCreatedBy(user.getUserId());

        try {
            wall = wallDao.save(wallFolder);
        } catch (DataAccessException ex) {
            throw new BusinessException("Wall folder could not be created. country: " + wallFolder, ex);
        }

        return wall;
    }
**/
    @Override
    public WallFolder createUserWall(User wallUser) throws BusinessException {

        if(wallUser == null || StringUtil.isEmpty(wallUser.getUserId())) {
            throw new BusinessException("Wall canont be created. Null user Id");
        }

        String wallId = WallFolderUtil.getUserWallId(wallUser.getUserId());

        WallFolder wall = getWallForder(wallId);

        if(wall != null)
            return wall;

        WallFolder wallFolder = new WallFolder();
        wallFolder.id = wallId;
        wallFolder.wallUserId = wallUser.getUserId();
        wallFolder.type = WallFolderType.USER_WALL;

        wallFolder.ownerId = wallUser.getUserId();
        wallFolder.setCreatedBy(wallUser.getUserId());

        try {
            wall = wallDao.save(wallFolder);
        } catch (DataAccessException ex) {
            throw new BusinessException("Wall folder could not be created. " + wallFolder, ex);
        }

        return wall;
    }

    @Override
    public List<WallFolderPost> getNextConversations(String wallId, int bucket, String conversationId, int count, String userId)
            throws BusinessException {

        WallFolder wall = null;

        wall = getWallForder(wallId);

        if(wall == null)
            throw new BusinessException("Wall folder not found. " + wallId);

        if(bucket == -1)
            bucket = wall.currentSplitId;

        try {
            UUID fromConversationId = null;
            if(!StringUtil.isEmpty(conversationId))
                fromConversationId = UUID.fromString(conversationId);

//            WallFolderMessagesKey key = new  WallFolderMessagesKey(wallId, bucket, fromConversationId);
//
//            WallFolderMessagesKey lastKey = new  WallFolderMessagesKey(wallId, wall.currentSplitId, null); //to set the first key

            Tuple2<String, Integer> partionKey = new Tuple2<>(wallId, bucket);
            Tuple1<UUID> clusterKey = new Tuple1<>(fromConversationId);

            Tuple2<String, Integer> lastPartKey = new Tuple2<>(wallId, wall.currentSplitId);
//            List<WallFolderPost> coll = wallFolderMessagesDao.getNextMessages(key, count, lastKey);

            List<WallFolderPost> coll = wallFolderMessagesDao.getNextMessages(partionKey, clusterKey, count, lastPartKey);

            //return coll; //.stream().map(wm -> wm.post).collect(Collectors.toList());

//            Collection<WallPostNotification> coll = wallDao.getNextItems(wall, bucket, conversationId, count);

//            List<WallPost> converstations = new ArrayList();
//
//            if(coll != null) {
//                for(WallPostNotification notification: coll) {
//                    WallPost conversation = conversationDao.get(notification.id);
//
//                    if(conversation != null)
//                         notification.message = conversation;
//                }
//            }
//
//            return new LinkedList<WallPostNotification>(coll);

            /*** TODO satya uncomment to set metadata
             if(coll != null) {
             for(WallFolderPost fp: coll) {
             try {
             WallPost conversation = conversationDao.get(fp.getPost().id);

             if (conversation != null)
             fp.setPost(conversation);
             } catch(Exception e) {}
             }
             }
             **/

            return coll;
        } catch (DataAccessException ex) {
            logger.error("Error getting message headers for " + wallId, ex);
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<WallFolderPost> getPrevConversations(String wallId, int bucket, String conversationId, int count, String userId)
            throws BusinessException {

        WallFolder wall = null;

        wall = getWallForder(wallId);

        if(wall == null)
            throw new BusinessException("Wall folder not found. " + wallId);

        if(bucket == -1)
            bucket = wall.currentSplitId;

        try {
            UUID fromConversationId = null;
            if(!StringUtil.isEmpty(conversationId))
                fromConversationId = UUID.fromString(conversationId);

//            WallFolderMessagesKey key = new  WallFolderMessagesKey(wallId, bucket, fromConversationId);
//
//            WallFolderMessagesKey firstKey = new  WallFolderMessagesKey(wallId, bucket, null); //to set the first key

            Tuple2<String, Integer> partionKey = new Tuple2<>(wallId, bucket);
            Tuple1<UUID> clusterKey = new Tuple1<>(fromConversationId);


            Tuple2<String, Integer> firstKey = new Tuple2<>(wallId, bucket);

            List<WallFolderPost> coll = wallFolderMessagesDao.getPrevMessages(partionKey, clusterKey, count, firstKey);

            //return coll; //coll.stream().map(wm -> wm.post).collect(Collectors.toList());

//            Collection<WallPostNotification> coll = wallDao.getNextItems(wall, bucket, conversationId, count);

//            List<WallPost> converstations = new ArrayList();
//

            /** Uncomments TODO satya for metadata setting

             if(coll != null) {
             for(WallFolderPost fp: coll) {
             try {
             WallPost conversation = conversationDao.get(fp.getPost().id);

             if (conversation != null)
             fp.setPost(conversation);
             } catch (Exception e) {}
             }
             }
             **/
//
//            return new LinkedList<WallPostNotification>(coll);

            return coll;

        } catch (DataAccessException ex) {
            logger.error("Error getting message headers for " + wallId, ex);
        }

        return Collections.EMPTY_LIST;

    }

    @Override
    public List<WallMessageReply> getNextComments(String postId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException {

        WallMessage wallMessage = getPost(postId);
//        UUID msgUUID = null;
//        try {
//            msgUUID = UUID.fromString(messageId);
//            wallMessage = conversationDao.get(msgUUID);
//        } catch (DataAccessException ex) {
//            throw new BusinessException("Message not found with id: " + messageId);
//        }
//
        if(wallMessage == null)
            throw new BusinessException("Message not found with id" + postId);

        return _getNextComments(wallMessage, bucket, fromCommentId, count, userId);

    }

    @Override
    public List<WallMessageReply> getPrevComments(String postId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException {

        WallMessage wallMessage = getPost(postId);
//        UUID msgUUID = null;
//        try {
//            msgUUID = UUID.fromString(messageId);
//            wallMessage = conversationDao.get(msgUUID);
//        } catch (DataAccessException ex) {
//            throw new BusinessException("Message not found with id: " + messageId);
//        }

        if(wallMessage == null)
            throw new BusinessException("Message not found with id" + postId);

        return _getPrevComments(wallMessage, bucket, fromCommentId, count, userId);

    }

    public List<WallMessageReply> getNextReplies(String commentId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException {

        WallMessage wallMessage = getComment(commentId);

        if(wallMessage == null)
            throw new BusinessException("Message not found with id" + commentId);

        return _getNextComments(wallMessage, bucket, fromCommentId, count, userId);

    }

    public List<WallMessageReply> getPrevReplies(String commentId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException {
        WallMessage wallMessage = getComment(commentId);

        if(wallMessage == null)
            throw new BusinessException("Message not found with id" + commentId);

        return _getPrevComments(wallMessage, bucket, fromCommentId, count, userId);
    }

    private List<WallMessageReply> _getNextComments(WallMessage wallMessage, int bucket, String fromCommentId, int count, String userId) throws BusinessException {

        if(bucket == -1)
            bucket = wallMessage.currentSplitId;

        try {
            UUID fromCommentIdUUID = null;
            if(!StringUtil.isEmpty(fromCommentId))
                fromCommentIdUUID = UUID.fromString(fromCommentId);

//            WallMessageRepliesKey key = new WallMessageRepliesKey(wallMessage.id, bucket, fromCommentIdUUID);
//            WallMessageRepliesKey lastKey = new WallMessageRepliesKey(wallMessage.id, wallMessage.currentSplitId, null);


            Tuple2<UUID, Integer> partionKey = new Tuple2<>(wallMessage.id, bucket);
            Tuple1<UUID> clusterKey = new Tuple1<>(fromCommentIdUUID);

            Tuple2<UUID, Integer> lastPartKey = new Tuple2<>(wallMessage.id, wallMessage.currentSplitId);


            List<WallMessageReply> coll = repliesDao.getNextMessages(partionKey, clusterKey, count, lastPartKey);

            //return coll.stream().map(wmr -> wmr.comment).collect(Collectors.toList());
//            Collection<WallMessageNotification> coll = messageDao.getNextItems(wallMessage, bucket, fromCommentId , count);

//            List<WallMessage> comments = new ArrayList();
//            if(coll != null) {
//                for(WallMessageNotification notification: coll) {
//                    WallMessage comment = messageDao.get(notification.id);
//                    notification.message = comment;
//                }
//            }
//
//            return new LinkedList<WallMessageNotification>(coll);

            if(coll != null) {
                for(WallMessageReply mr: coll) {
                    try {
                        WallMessage wallMsg = wallMsgDao.get(mr.getReplyId());

                        if (wallMsg != null)
                            mr.setMessage(wallMsg);
                    } catch (Exception e) {}
                }
            }

            return coll;

        } catch (DataAccessException ex) {
            logger.error("Error getting message headers for " + wallMessage.id, ex);
        }

        return Collections.EMPTY_LIST;
    }

    private List<WallMessageReply> _getPrevComments(WallMessage wallMessage, int bucket, String fromCommentId, int count, String userId) throws BusinessException {

        if(bucket == -1)
            bucket = wallMessage.currentSplitId;

        try {
            UUID fromCommentIdUUID = null;
            if(!StringUtil.isEmpty(fromCommentId))
                fromCommentIdUUID = UUID.fromString(fromCommentId);

//            WallMessageRepliesKey key = new WallMessageRepliesKey(wallMessage.id, bucket, fromCommentIdUUID);
//            WallMessageRepliesKey firstKey = new WallMessageRepliesKey(wallMessage.id, 0, null);

            Tuple2<UUID, Integer> partionKey = new Tuple2<>(wallMessage.id, bucket);
            Tuple1<UUID> clusterKey = new Tuple1<>(fromCommentIdUUID);

            Tuple2<UUID, Integer> firstKey = new Tuple2<>(wallMessage.id, 0);

            List<WallMessageReply> coll = repliesDao.getPrevMessages(partionKey, clusterKey, count, firstKey);

//            return coll;
            //return coll.stream().map(wmr -> wmr.comment).collect(Collectors.toList());
//            Collection<WallMessageNotification> coll = messageDao.getNextItems(wallMessage, bucket, fromCommentId , count);

//            List<WallMessage> comments = new ArrayList();
//            if(coll != null) {
//                for(WallMessageNotification notification: coll) {
//                    WallMessage comment = messageDao.get(notification.id);
//                    notification.message = comment;
//                }
//            }
//
//            return new LinkedList<WallMessageNotification>(coll);

            if(coll != null) {
                for(WallMessageReply mr: coll) {
                    try {
                        WallMessage wallMsg = wallMsgDao.get(mr.getReplyId());

                        if (wallMsg != null)
                            mr.setMessage(wallMsg);
                    } catch (Exception e) {}
                }
            }

            return coll;

        } catch (DataAccessException ex) {
            logger.error("Error getting message headers for " + wallMessage.id, ex);
        }

        return Collections.EMPTY_LIST;
    }


    @Override
    public WallMessage getMessage(String messageId, String userId) throws BusinessException {

        try {
            UUID msgUUID = UUID.fromString(messageId);

            WallMessage message = wallMsgDao.get(msgUUID);

            return message;
        } catch (DataAccessException ex) {
            throw new BusinessException("Error getting message from messages bucket.", ex);
        }
    }

    @Override
    public WallPost getPost(String postId) throws BusinessException {

        try {
            UUID postUUID = UUID.fromString(postId);

            WallPost post = conversationDao.get(postUUID);

            return post;
        } catch (DataAccessException ex) {
            throw new BusinessException("Error getting post from wallmessage bucket.", ex);
        }
    }

    @Override
    public WallMessage getComment(String commentId) throws BusinessException {

        WallMessage message = getMessage(commentId, null);

        return message;

    }


    @Override
    public void shareToCategoryWall(String postId, User user)
            throws BusinessException {
/**
        WallPost post = getPost(postId);

        if(post == null)
            throw new BusinessException("Post not found with id : " + postId);

        if(post.sharedOnCategoryWall) {
            logger.warn("Post has already been shared on category wall. Can't share again.");
            return;
        }

        String wallId = post.wallId;

        if(StringUtil.isEmpty(wallId))
            throw new BusinessException("Invalid service wallId");

        WallFolder serviceWall = getWall(wallId);

        if(serviceWall == null)
            throw new BusinessException("Service wall not found");

        String countryCode = serviceWall.countryCode;
        String serviceId = serviceWall.serviceId;

        ServiceProfile profile = profileService.getProfileDetails(serviceId, countryCode);

        if(profile == null)
            throw new BusinessException("Service Profile not found. " + serviceId + ", country: " + countryCode);

        String city = null;

        if(profile.contact() != null)
            city = profile.contact().city();

        String category = profile.category();

        City cityObj = SystemConfig.getCity(SystemConfig.getCountry(countryCode), city);
        if( cityObj == null)
            throw new BusinessException("Invalid city. ", city + ", country: " + countryCode);

        if(cityObj.isSubcity) {
            city = cityObj.metroCityName;
        }

        String categoryWallId = WallFolderUtil.getCategoryWallId(countryCode, city, category);

        WallFolder categoryWall = getWall(categoryWallId);

        if(categoryWall == null) {
            Logger.info("Category wall is not found : " + categoryWallId + ". Let's create it.");

            categoryWall = createCategoryWall(user, countryCode, city, category);
        }

        if(categoryWall != null) {

            WallFolderPost wallFolderPost = new WallFolderPost();
//            WallFolderMessagesKey wallFolderMessagesKey = new WallFolderMessagesKey(categoryWall.id, categoryWall.currentSplitId, post.id);
//
//            wallFolderPost.id = wallFolderMessagesKey;
            wallFolderPost.setFolder(categoryWall.getId());
            wallFolderPost.setBucket(categoryWall.currentSplitId);
            wallFolderPost.setPostId(post.getId());

            wallFolderPost.setPostMetadata(post.createPostMetaData());
            wallFolderPost.post = post;

//
//            WallPostNotification convNotification = new WallPostNotification();
//            convNotification.id = post.id;

            try {
//                wallDao.addMessageToFolder(categoryWall, convNotification);
//
                wallFolderMessagesDao.add(wallFolderPost);

                //update post
//                post.addSharedWall(categoryWallId);
                post.sharedOnCategoryWall = true;

                conversationDao.save(post); //update
            } catch (DataAccessException ex) {
                throw new BusinessException("Unable to share post to category wall : " + categoryWallId, ex);
            }


        }**/

    }

    private WallMessage saveMessage(User user, UUID parentId, WallMessage message) throws BusinessException {

        if (message.id == null) {
            message.id = UUIDs.timeBased(); //idGenerationService.getNextWallMessageId();
            message.parentId = parentId;
            message.postDate = System.currentTimeMillis();
            message.ownerId = user.getUserId();
            message.ownerEmail = user.getEmail();
            message.ownerName = user.getDisplayName();

        }

        try {
            message = wallMsgDao.save(message);

        } catch (DataAccessException ex) {
            throw new BusinessException("Unable to persist wall message ", ex);
        }

        return message;

    }

    private WallMessageReply addCommentToConversation(WallPost conversation, WallMessage message, String userId) throws BusinessException {

        try {
            // WallMessageNotification messageMetaData = new WallMessageNotification();

            //add message to conversation folder
//            messageMetaData.id = message.id;
//
//            conversationDao.addMessageToFolder(conversation, messageMetaData);

            WallMessageReply reply = new WallMessageReply();
//            reply.id = new WallMessageReply.WallMessageRepliesKey(conversation.id, conversation.currentSplitId, message.id);
//
            reply.setMessageId(conversation.id);
            reply.setBucket(conversation.currentSplitId);
            reply.setReplyId(message.id);

            reply.setMessageMetaData(message.createMessageMetaData());
//            reply.message = message;

            reply = repliesDao.add(reply);

            reply.setMessage(message);

            return reply;

        } catch (DataAccessException ex) {
            throw new BusinessException("Unable to persist wall message ", ex);
        }

    }

    private WallMessageReply addReplyToMessage(WallMessage message, WallMessage replyMessage, String userId) throws BusinessException {

        try {
//            WallMessageNotification replyMessageMetaData = new WallMessageNotification();
//
//            //add message to conversation folder
//            replyMessageMetaData.id = replyMessage.id;
//            messageDao.addMessageToFolder(message, replyMessageMetaData);

            WallMessageReply reply = new WallMessageReply();
//            reply.id = new WallMessageReply.WallMessageRepliesKey(message.id, message.currentSplitId, replyMessage.id);
//
            reply.setMessageId(message.getId());
            reply.setBucket(message.getCurrentSplitId());
            reply.setReplyId(replyMessage.getId());

            //reply.message = replyMessage;

            reply.setMessageMetaData(replyMessage.createMessageMetaData());

            reply = repliesDao.add(reply);

            reply.setMessage(replyMessage);

            return reply;

        } catch (DataAccessException ex) {
            throw new BusinessException("Unable to persist wall message ", ex);
        }

    }

//    private void _addMessageToWallFolder(String wallId, WallMessage message, String userId) throws BusinessException {
//        try {
//
//            WallFolder wallFolder = getWallForder(wallId);
//
//            if(wallFolder == null)
//                throw new BusinessException("Wall not found with id : " + wallId);
//
//            WallConversationNotification conversationNotification = new WallConversationNotification();
//            conversationNotification.id = message.conversationId;
//
//            wallDao.addMessageToFolder(wallFolder, conversationNotification);
//
//        } catch (DataAccessException ex) {
//
//            throw new BusinessException("WallMessage could not be posed" + wallId, ex);
//        }
//    }

    private WallFolder getWallForder(String wallId) throws BusinessException {

        if(wallId == null) {
            logger.error("Invalid wall folder id : " + wallId);
        }

        WallFolder wall = null;
        try {
            wall = wallDao.get(wallId);
        } catch (DataAccessException ex) {
            logger.error("Error getting wall with id: " + wallId);
        }

        return wall;
    }


    /*private WallRecipient createUserAsRecipient(String userId) throws BusinessException {
        User user = userService.getUserById(userId);

        if(user == null)
            throw new BusinessException("User not found with id: " + userId);

        return WallRecipient.createRecipient(user);
    }*/

}
