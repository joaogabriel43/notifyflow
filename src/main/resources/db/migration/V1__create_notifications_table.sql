CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    preferred_channel VARCHAR(10) NOT NULL,
    fallback_channels TEXT,
    recipient_email VARCHAR(255),
    recipient_phone VARCHAR(20),
    recipient_device_token VARCHAR(500),
    template_subject VARCHAR(500),
    template_body TEXT,
    template_variables JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_tenant_id ON notifications(tenant_id);
CREATE INDEX idx_notifications_status ON notifications(status);
