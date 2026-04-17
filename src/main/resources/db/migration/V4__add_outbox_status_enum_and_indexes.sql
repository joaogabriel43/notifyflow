CREATE INDEX idx_outbox_pending ON notification_outbox(status, created_at)
    WHERE status = 'PENDING';
