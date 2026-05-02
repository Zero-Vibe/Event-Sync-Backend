CREATE TYPE lien_type AS ENUM (
    'twitter',
    'linkedin',
    'github',
    'youtube',
    'website',
    'other'
);


CREATE TABLE organisateurs (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT         NOT NULL,
    nom           VARCHAR(255),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE evenements (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    titre         VARCHAR(255) NOT NULL,
    description   TEXT,
    date_debut    TIMESTAMP    NOT NULL,
    date_fin      TIMESTAMP    NOT NULL,
    lieu          VARCHAR(255) NOT NULL,
    created_by    UUID         REFERENCES organisateurs(id) ON DELETE SET NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_evenement_dates CHECK (date_fin > date_debut)
);

CREATE TABLE salles (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE intervenants (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom_complet  VARCHAR(255) NOT NULL,
    photo_profil TEXT,
    biographie   TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE intervenant_liens (
    id             UUID       PRIMARY KEY DEFAULT gen_random_uuid(),
    intervenant_id UUID       NOT NULL REFERENCES intervenants(id) ON DELETE CASCADE,
    type           lien_type  NOT NULL DEFAULT 'other',
    url            TEXT       NOT NULL,
    label          VARCHAR(100),
    ordre          SMALLINT   NOT NULL DEFAULT 0
);

CREATE TABLE sessions (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    evenement_id UUID         NOT NULL REFERENCES evenements(id) ON DELETE CASCADE,
    salle_id     UUID         NOT NULL REFERENCES salles(id) ON DELETE RESTRICT,
    titre        VARCHAR(255) NOT NULL,
    description  TEXT,
    heure_debut  TIMESTAMP    NOT NULL,
    heure_fin    TIMESTAMP    NOT NULL,
    capacite     INTEGER      CHECK (capacite > 0),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_session_horaires CHECK (heure_fin > heure_debut)
);

CREATE TABLE session_intervenants (
    session_id     UUID NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    intervenant_id UUID NOT NULL REFERENCES intervenants(id) ON DELETE CASCADE,
    PRIMARY KEY (session_id, intervenant_id)
);

CREATE TABLE questions (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID         NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
    contenu    TEXT         NOT NULL CHECK (char_length(contenu) > 0),
    nom_auteur VARCHAR(255),
    upvotes    INTEGER      NOT NULL DEFAULT 0 CHECK (upvotes >= 0),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);