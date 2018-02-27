import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.jabosu.common.auth.User;
import org.jabosu.common.exception.BusinessException;
import org.jabosu.common.services.impl.ConfigServiceImpl;
import org.jabosu.common.util.ConfigLoader;
import org.jabosu.social.models.wall.WallFolderPost;
import org.jabosu.social.models.wall.WallMessage;
import org.jabosu.social.models.wall.WallPost;
import org.jabosu.social.services.impl.wall.WallMessageServiceImpl;
import org.jabosu.social.services.wall.WallMessageService;
import org.junit.Test;


public class WallTest {

    @Test
    public void postWallTest() throws BusinessException {

        ConfigLoader.setConfigService(new ConfigServiceImpl());

        WallMessageService wallMessageService = new WallMessageServiceImpl();

        for(int i =0 ;i<10;i++) {
            User user = new User();
            user.setUserId("1");
            user.setEmail("test@satyaranjan.com");
            WallPost post = new WallPost();
            post.title = "First wall post";
            post.content = "This is a test wall with test content." + i;
            WallFolderPost folderPost = wallMessageService.postMessage(user, "testwall", post);

            WallMessage comment = new WallMessage();
            comment.ownerId = "2";
            comment.ownerName  = "name2";
            comment.ownerEmail = "name2@satyranajn.com";
            comment.setContent("Its a comment for " + i);

            wallMessageService.postComment(user, "testwall", folderPost.postId.toString(), comment);
        }


    }
}