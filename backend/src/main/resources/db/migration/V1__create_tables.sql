CREATE TABLE matches (
    match_id            VARCHAR(32) NOT NULL,
    game_datetime       BIGINT      NOT NULL,
    queue_id            INT         NOT NULL,
    set_number          INT         NOT NULL,
    game_length_seconds INT         NOT NULL,
    raw_json            LONGTEXT    NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (match_id),
    INDEX idx_matches_game_datetime (game_datetime)
);

CREATE TABLE match_participants (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    match_id  VARCHAR(32)  NOT NULL,
    puuid     VARCHAR(100) NOT NULL,
    placement INT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_participant UNIQUE (match_id, puuid),
    CONSTRAINT fk_participant_match FOREIGN KEY (match_id) REFERENCES matches (match_id),
    INDEX idx_participant_puuid (puuid)
);