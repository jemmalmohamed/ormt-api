CREATE TABLE IF NOT EXISTS newsletter_subscription (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    unsubscribe_token VARCHAR(255) NOT NULL UNIQUE,
    subscribed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unsubscribed_at TIMESTAMPTZ,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_newsletter_subscription_status ON newsletter_subscription(status);
CREATE INDEX IF NOT EXISTS idx_newsletter_subscription_subscribed_at ON newsletter_subscription(subscribed_at);

CREATE TABLE IF NOT EXISTS newsletter_campaign (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content_html TEXT,
    content_text TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    sent_at TIMESTAMPTZ,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_newsletter_campaign_status ON newsletter_campaign(status);
CREATE INDEX IF NOT EXISTS idx_newsletter_campaign_sent_at ON newsletter_campaign(sent_at);

CREATE TABLE IF NOT EXISTS newsletter_event (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT REFERENCES newsletter_campaign(id) ON DELETE CASCADE,
    subscription_id BIGINT REFERENCES newsletter_subscription(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    event_message VARCHAR(500),
    provider_message_id VARCHAR(255),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_newsletter_event_campaign_id ON newsletter_event(campaign_id);
CREATE INDEX IF NOT EXISTS idx_newsletter_event_subscription_id ON newsletter_event(subscription_id);
CREATE INDEX IF NOT EXISTS idx_newsletter_event_type ON newsletter_event(event_type);