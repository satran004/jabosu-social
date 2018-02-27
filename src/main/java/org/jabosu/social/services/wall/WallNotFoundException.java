package org.jabosu.social.services.wall;

import org.jabosu.common.exception.BusinessException;

/**
 *
 * @author satya
 */
public class WallNotFoundException extends BusinessException {
    
    public WallNotFoundException(String message) {
        super(message);
    }
}
