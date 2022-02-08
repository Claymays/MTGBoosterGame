package com.mays.mtgboostergame.user;

import javax.persistence.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditListener {
    @PreRemove
    @PrePersist
    @PreUpdate
    private void beforeUpdate(User user) {
        if (user.getId() == null) {
            log.info("[USER AUDIT] about to add a new user");
        } else {
            log.info("[USER AUDIT] about to update/remove user: " + user.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterUpdate(User user) {
        log.info("[USER AUDIT] add/update/delete complete for user: " + user.getId());
    }

    @PostLoad
    private void afterLoad(User user) {
        log.info("[USER AUDIT] user loaded from database: " + user.getId());
    }
}
