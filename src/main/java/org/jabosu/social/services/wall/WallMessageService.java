package org.jabosu.social.services.wall;

import org.jabosu.common.auth.User;
import org.jabosu.common.exception.BusinessException;
import org.jabosu.social.models.wall.*;

import java.util.List;

/**
 *
 * @author satya
 */
public interface WallMessageService {

    public WallFolderPost postMessage(User user, String wallId, WallPost conversation)
            throws BusinessException, WallNotFoundException;

    public WallMessageReply postComment(User user, String wallId, String conversationId, WallMessage message)
            throws BusinessException;

    public WallMessageReply postReply(User user, String wallId, String messageId, WallMessage replyMessage)
            throws BusinessException;

    public WallFolder getWall(String wallId) throws BusinessException;

    public WallFolder createWall(User user, WallFolder wall) throws BusinessException;

//    public WallFolder createServiceWall(User user, String country, String serviceId) throws BusinessException;
//
//    public WallFolder createDealWall(User user, String country, String serviceId, String dealId) throws BusinessException;
//
//    public WallFolder createCategoryWall(User user, String country, String city, String category) throws BusinessException;

    public WallFolder createUserWall(User user) throws BusinessException;


    //    public String createConversionId(String countryCode, String serviceId, String userId) throws BusinessException;
//
////    public List<MessageMetaData> getMessageHeaders(String mailboxId, int startIndex, int endIndex, String userId) throws BusinessException;
    public List<WallFolderPost> getNextConversations(String wallId, int bucket, String conversationId, int count, String userId) throws BusinessException;

    public List<WallFolderPost> getPrevConversations(String wallId, int bucket, String conversationId, int count, String userId) throws BusinessException;

    public List<WallMessageReply> getNextComments(String postId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException;

    public List<WallMessageReply> getPrevComments(String postId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException;

    public List<WallMessageReply> getNextReplies(String messageId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException;

    public List<WallMessageReply> getPrevReplies(String messageId, int bucket, String fromCommentId, int count, String userId)
            throws BusinessException;

    public WallMessage getMessage(String messageId, String userId) throws BusinessException;

    public WallPost getPost(String postId) throws BusinessException;

    public WallMessage getComment(String commentId) throws BusinessException;

    public void shareToCategoryWall(String postId, User user) throws BusinessException;
//
//    public WallMessage getMessage(String messageId, String userId) throws BusinessException;
//
//     public List<ConversationNotification> getMessagesForConversation(String conversationId, int bucket,
//                                                        String fromMessageId, int count, String userId) throws BusinessException;
//
//    public Conversation getConversation(String conversationId) throws BusinessException;
//
//    public void deleteMessages(String mailBoxId, List<String> msgIds, String userId) throws BusinessException;
//    
//    public void markRead(String mailBoxId, List<String> msgIds, String userId) throws BusinessException;
}
