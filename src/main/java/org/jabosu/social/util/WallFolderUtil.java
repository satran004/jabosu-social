package org.jabosu.social.util;

import org.jabosu.common.SystemConfig;
import org.jabosu.common.util.StringUtil;

public class WallFolderUtil {

    public final static String SERVICE_WALL_SUFFIX = "service_wall";
    public final static String CATEGORY_WALL_SUFFIX = "category_wall";
    public final static String DEAL_WALL_SUFFIX = "deal_wall";
    public final static String USER_WALL_SUFFIX = "user_wall";

    public static String getServiceWallId(String countryCode, String serviceId) {
        return countryCode + ":" + serviceId + ":" + SERVICE_WALL_SUFFIX;
    }

    public static String getDealWallId(String countryCode, String serviceId, String dealId) {
        return countryCode + ":" + serviceId + ":" + dealId + ":" + DEAL_WALL_SUFFIX;
    }

    public static String getCategoryWallId(String countryCode, String city, String category) {
        return countryCode + ":" + SystemConfig.normalizeCity(city) + ":" + category + ":" + CATEGORY_WALL_SUFFIX;
    }

    public static String getUserWallId(String userId) {
        return userId + ":" + USER_WALL_SUFFIX;
    }

    public static WallFolderType getWallType(String wallId) {
        if(StringUtil.isEmpty(wallId))
            return null;

        if(wallId.endsWith(USER_WALL_SUFFIX))
            return WallFolderType.USER_WALL;
        else if(wallId.endsWith(SERVICE_WALL_SUFFIX))
            return WallFolderType.SERIVCE_WALL;
        else if(wallId.endsWith(CATEGORY_WALL_SUFFIX))
            return WallFolderType.CATEGORY_WALL;
        else if(wallId.endsWith(DEAL_WALL_SUFFIX))
            return WallFolderType.DEAL_WALL;
        else
            return WallFolderType.UNDEFINED;
    }

    public static WallParameters getWallParameters(String wallId) {
        if(wallId == null)
            return null;

        WallFolderType type = getWallType(wallId);
        WallParameters parameters = new WallParameters();

        if(WallFolderType.SERIVCE_WALL.equals(type)) {
            String[] splits = wallId.split(":");

            if(splits.length < 3)
                return null;

            parameters.type = WallFolderType.SERIVCE_WALL;

            parameters.country = splits[0];
            parameters.serviceId = splits[1];

        } else if(WallFolderType.CATEGORY_WALL.equals(type)) {
            String[] splits = wallId.split(":");

            if(splits.length < 4)
                return null;

            parameters.type = WallFolderType.CATEGORY_WALL;

            parameters.country = splits[0];
            parameters.city = splits[1];
            parameters.category = splits[2];

        } else if(WallFolderType.DEAL_WALL.equals(type)) {
            String[] splits = wallId.split(":");

            if(splits.length < 4)
                return null;

            parameters.type = WallFolderType.DEAL_WALL;

            parameters.country = splits[0];
            parameters.serviceId = splits[1];
            parameters.dealId = splits[2];
        } else if(WallFolderType.USER_WALL.equals(type)) {

            String[] splits = wallId.split(":");

            if(splits.length < 2)
                return null;

            parameters.type = WallFolderType.USER_WALL;

            parameters.wallUserId = splits[0];
        } else
            return null;

        return parameters;
    }

}

