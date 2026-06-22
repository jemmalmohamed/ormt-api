package ma.org.ormt.modules.analytics.shared;

import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

public final class AnalyticsMapperSupport {

    private AnalyticsMapperSupport() {
    }

    public static void copyBase(BaseEntity entity, BaseDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        dto.setId(entity.getId());
        dto.setStatusCode(entity.getStatusCode());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
    }
}
