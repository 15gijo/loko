package com.team15gijo.user.domain.model;

import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "p_user_region")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_region SET deleted_at = now(), deleted_by = updated_by WHERE region_id = ?")
public class UserRegionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "region_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "region_code", nullable = false)
    private String regionCode;

    @Column(name = "region_name", nullable = false)
    private String regionName;

    //PostGis 좌표계 타입, 4326: WGS84 좌표계(카카오맵 호환)
    @Column(name = "location", nullable = false, columnDefinition = "GEOGRAPHY(POINT, 4326")
    private Point location;

    @Builder
    public UserRegionEntity(UUID id, String regionCode, String regionName, Point location) {
        this.id = id;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.location = location;
    }

    public void updateLocation(Point newLocation) {
        this.location = newLocation;
    }

    public void updateRegion(String newRegionCode, String newRegionName) {
        this.regionCode = newRegionCode;
        this.regionName = newRegionName;
    }

}
