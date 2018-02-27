package org.jabosu.social.models.wall;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jabosu.common.entity.BaseModel;
import org.jabosu.social.util.WallFolderType;

/**
 *
 * @author satya
 */
@Table(name = "walls")
//@Strategy(naming = NamingStrategy.SNAKE_CASE)
@Getter
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WallFolder extends BaseModel {

    @PartitionKey
    public String id;

//    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public WallFolderType type;

    @Column(name = "country_code")
    public String countryCode;

    @Column(name = "city")
    public String city;

    @Column(name = "service_id")
    public String serviceId;

    @Column(name = "deal_id")
    public String dealId;

    @Column(name = "wall_user_id")
    public String wallUserId;

    @Column(name = "category")
    public String category;

    @Column(name = "owner_id")
    public String ownerId;

    @Column(name = "current_split_id")
    public int currentSplitId;

    @Override
    public String key() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WallFolderType getType() {
        return type;
    }

    public void setType(WallFolderType type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getWallUserId() {
        return wallUserId;
    }

    public void setWallUserId(String wallUserId) {
        this.wallUserId = wallUserId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getCurrentSplitId() {
        return currentSplitId;
    }

    public void setCurrentSplitId(int currentSplitId) {
        this.currentSplitId = currentSplitId;
    }


    @Override
    public String toString() {
        return "WallFolder{" + "type=" + type + ", countryCode=" + countryCode + ", serviceId=" + serviceId + ", city=" + city + ", dealId=" + dealId + ", wallUserId=" + wallUserId + ", category=" + category + '}';
    }
    
    
}
