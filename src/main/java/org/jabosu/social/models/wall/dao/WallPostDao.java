package org.jabosu.social.models.wall.dao;

import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import org.jabosu.common.dao.cassandra.dao.BaseDao;
import org.jabosu.common.util.StringUtil;
import org.jabosu.social.models.wall.WallMessage;
import org.jabosu.social.models.wall.WallPost;

import java.util.UUID;

/**
 * Created by satya on 2/8/15.
 */
public class WallPostDao extends BaseDao<WallPost, UUID> {

    static {
        CodecRegistry.DEFAULT_INSTANCE
                .register(new EnumNameCodec<WallMessage.WallMessageType>(WallMessage.WallMessageType.class));
    }

    @Override
    public Class<WallPost> getDomainClass() {
        return WallPost.class;
    }

    @Override
    protected void prePersist(WallPost wallPost) {
        super.prePersist(wallPost); //To change body of generated methods, choose Tools | Templates.

        if(StringUtil.isEmpty(wallPost.title)) {
            String cleanMsg = StringUtil.cleanHtmlAsSimpleText(wallPost.content);

            if(cleanMsg != null) {
                wallPost.setTitle(StringUtil.wrap(cleanMsg, 120));
            }
        }

        wallPost.setContent(StringUtil.cleanWysiwygHtml(wallPost.content));
    }
}
