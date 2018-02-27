package org.jabosu.social.models.wall.dao;

import org.jabosu.common.dao.cassandra.dao.BaseDao;
import org.jabosu.common.util.StringUtil;
import org.jabosu.social.models.wall.WallMessage;

import java.util.UUID;

/**
 * Created by satya on 3/8/15.
 */
public class WallMessageDao extends BaseDao<WallMessage, UUID> {
    @Override
    public Class<WallMessage> getDomainClass() {
        return WallMessage.class;
    }

    @Override
    protected void prePersist(WallMessage message) {
        super.prePersist(message);

        message.setContent(StringUtil.cleanWysiwygHtml(message.content));
    }
}
