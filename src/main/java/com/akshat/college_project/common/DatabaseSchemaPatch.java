package com.akshat.college_project.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaPatch {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaPatch.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applyOtpAndVerificationColumnPatch() {
        // Backward-compatible patch for older databases before OTP/account-type rollout.
        run("ALTER TABLE IF EXISTS otps ADD COLUMN IF NOT EXISTS account_type varchar(20)");
        run("UPDATE otps SET account_type = 'STUDENT' WHERE account_type IS NULL");
        run("ALTER TABLE IF EXISTS otps ALTER COLUMN account_type SET NOT NULL");

        run("ALTER TABLE IF EXISTS admin ADD COLUMN IF NOT EXISTS otp_verified boolean not null default false");
        run("ALTER TABLE IF EXISTS students ADD COLUMN IF NOT EXISTS otp_verified boolean not null default false");
        run("ALTER TABLE IF EXISTS supervisor ADD COLUMN IF NOT EXISTS otp_verified boolean not null default false");
    }

    private void run(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            log.warn("Schema patch skipped/failed for SQL: {}. Reason: {}", sql, ex.getMessage());
        }
    }
}
