CREATE TYPE link_type AS ENUM (
    'TWITTER',
    'LINKEDIN',
    'GITHUB',
    'YOUTUBE',
    'WEBSITE',
    'OTHER'
);

CREATE TABLE organizers (
                            id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                            email         VARCHAR(255) NOT NULL UNIQUE,
                            password_hash TEXT         NOT NULL,
                            name          VARCHAR(255),
                            created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
                            updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE events (
                        id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                        title         VARCHAR(255) NOT NULL,
                        description   TEXT,
                        start_date    TIMESTAMP    NOT NULL,
                        end_date      TIMESTAMP    NOT NULL,
                        location      VARCHAR(255) NOT NULL,
                        created_by    UUID         REFERENCES organizers(id) ON DELETE SET NULL,
                        created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
                        updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
                        CONSTRAINT chk_event_dates CHECK (end_date > start_date)
);

CREATE TABLE rooms (
                       id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                       name       VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE speakers (
                          id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                          full_name       VARCHAR(255) NOT NULL,
                          profile_picture TEXT,
                          biography       TEXT,
                          created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
                          updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE speaker_links (
                               id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               speaker_id  UUID        NOT NULL REFERENCES speakers(id) ON DELETE CASCADE,
                               type        link_type   NOT NULL DEFAULT 'other',
                               url         TEXT        NOT NULL,
                               label       VARCHAR(100),
                               "order"     SMALLINT    NOT NULL DEFAULT 0
);

CREATE TABLE sessions (
                          id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                          event_id     UUID         NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                          room_id      UUID         NOT NULL REFERENCES rooms(id) ON DELETE RESTRICT,
                          title        VARCHAR(255) NOT NULL,
                          description  TEXT,
                          start_time   TIMESTAMP    NOT NULL,
                          end_time     TIMESTAMP    NOT NULL,
                          capacity     INTEGER      CHECK (capacity > 0),
                          created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
                          updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
                          CONSTRAINT chk_session_times CHECK (end_time > start_time)
);

CREATE TABLE session_speakers (
                                  session_id  UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
                                  speaker_id  UUID NOT NULL REFERENCES speakers(id) ON DELETE CASCADE,
                                  PRIMARY KEY (session_id, speaker_id)
);

CREATE TABLE questions (
                           id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                           session_id  UUID         NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
                           content     TEXT         NOT NULL CHECK (char_length(content) > 0),
                           author_name VARCHAR(255),
                           upvotes     INTEGER      NOT NULL DEFAULT 0 CHECK (upvotes >= 0),
                           created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);