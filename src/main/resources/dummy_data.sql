-- EventSync Dummy Data

-- ============ USERS ============
INSERT INTO users (id, is_admin, email, password_hash, name, join_date)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', TRUE, 'admin@eventsync.com',
     '$2b$10$DpCQMfkI3HPnuzV9lVkcMeqYjoD4ceatzqz5gIEggqX5Ig0FzCqIS', 'Admin', NOW()),
    -- ^ password: admin123
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', FALSE, 'john@eventsync.com',
     '$2b$10$DpCQMfkI3HPnuzV9lVkcMeqYjoD4ceatzqz5gIEggqX5Ig0FzCqIS', 'John Doe', NOW());
    -- ^ password: admin123 (same hash for dev purposes)

-- ============ EVENTS ============
INSERT INTO events (id, title, description, start_date_time, end_date_time, location, user_id)
VALUES
    ('c3d4e5f6-a7b8-9012-cdef-123456789012',
     'DevConf 2026',
     'Annual developer conference covering web technologies, cloud, and AI.',
     '2026-09-15 09:00:00', '2026-09-17 18:00:00',
     'Paris Convention Center',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    ('d4e5f6a7-b8c9-0123-defa-234567890123',
     'TechSummit 2025',
     'A summit on emerging technologies and industry best practices.',
     '2025-03-10 10:00:00', '2025-03-11 17:00:00',
     'Lyon Tech Hub',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

-- ============ ROOMS ============
INSERT INTO rooms (id, name)
VALUES
    ('e5f6a7b8-c9d0-1234-efab-345678901234', 'Grand Hall'),
    ('f6a7b8c9-d0e1-2345-fabc-456789012345', 'Room A'),
    ('a7b8c9d0-e1f2-3456-abcd-567890123456', 'Workshop Room');

-- ============ SPEAKERS ============
INSERT INTO speakers (id, first_name, last_name, picture_url, biography)
VALUES
    ('b8c9d0e1-f2a3-4567-bcde-678901234567', 'Jane', 'Smith',
     'https://i.pravatar.cc/150?u=jane', 'Full-stack developer and open-source enthusiast. Core maintainer of several Node.js projects.'),
    ('c9d0e1f2-a3b4-5678-cdef-789012345678', 'Bob', 'Johnson',
     'https://i.pravatar.cc/150?u=bob', 'Cloud architect with 10+ years of experience in distributed systems and Kubernetes.'),
    ('d0e1f2a3-b4c5-6789-defa-890123456789', 'Alice', 'Lee',
     'https://i.pravatar.cc/150?u=alice', 'AI researcher specializing in NLP and LLMs. PhD in Computer Science from MIT.'),
    ('e1f2a3b4-c5d6-7890-efab-901234567890', 'Tom', 'Chen',
     'https://i.pravatar.cc/150?u=tom', 'DevOps engineer and Rust advocate. Building the next generation of tooling.');

-- ============ SPEAKER LINKS ============
INSERT INTO speaker_links (id, speaker_id, platform, url, label)
VALUES
    ('f2a3b4c5-d6e7-8901-fabc-012345678901', 'b8c9d0e1-f2a3-4567-bcde-678901234567',
     'TWITTER', 'https://twitter.com/janesmith', '@janesmith'),
    ('a3b4c5d6-e7f8-9012-abcd-123456789012', 'c9d0e1f2-a3b4-5678-cdef-789012345678',
     'LINKEDIN', 'https://linkedin.com/in/bobjohnson', 'Bob Johnson'),
    ('b4c5d6e7-f8a9-0123-bcde-234567890123', 'd0e1f2a3-b4c5-6789-defa-890123456789',
     'GITHUB', 'https://github.com/alicelee', 'alicelee'),
    ('c5d6e7f8-a9b0-1234-cdef-345678901234', 'e1f2a3b4-c5d6-7890-efab-901234567890',
     'WEBSITE', 'https://tomchen.dev', 'Personal site');

-- ============ SESSIONS ============
INSERT INTO sessions (id, event_id, room_id, title, description, start_time, end_time, capacity)
VALUES
    ('d6e7f8a9-b0c1-2345-defa-456789012345',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     'e5f6a7b8-c9d0-1234-efab-345678901234',
     'Opening Keynote: The Future of Web Development',
     'A look at emerging trends in web technologies and what the next decade holds.',
     '2026-09-15 09:00:00', '2026-09-15 10:30:00', 500),
    ('e7f8a9b0-c1d2-3456-efab-567890123456',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     'f6a7b8c9-d0e1-2345-fabc-456789012345',
     'Building with Web3: Hands-On Workshop',
     'An interactive workshop on smart contracts, dApps, and decentralized storage.',
     '2026-09-15 14:00:00', '2026-09-15 16:00:00', 50),
    ('f8a9b0c1-d2e3-4567-fabc-678901234567',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     'a7b8c9d0-e1f2-3456-abcd-567890123456',
     'Microservices Deep Dive',
     'From monoliths to microservices: patterns, pitfalls, and practical advice.',
     '2026-09-16 10:00:00', '2026-09-16 12:00:00', 200),
    ('a9b0c1d2-e3f4-5678-abcd-789012345678',
     'd4e5f6a7-b8c9-0123-defa-234567890123',
     'e5f6a7b8-c9d0-1234-efab-345678901234',
     'Closing Panel: What''s Next in Tech',
     'Industry leaders discuss the future of AI, cloud, and developer experience.',
     '2025-03-11 15:00:00', '2025-03-11 17:00:00', 300);

-- ============ SESSION-SPEAKER ASSIGNMENTS ============
INSERT INTO sessions_speakers (session_id, speakers_id)
VALUES
    ('d6e7f8a9-b0c1-2345-defa-456789012345', 'b8c9d0e1-f2a3-4567-bcde-678901234567'), -- Keynote → Jane
    ('e7f8a9b0-c1d2-3456-efab-567890123456', 'c9d0e1f2-a3b4-5678-cdef-789012345678'), -- Web3 → Bob
    ('e7f8a9b0-c1d2-3456-efab-567890123456', 'd0e1f2a3-b4c5-6789-defa-890123456789'), -- Web3 → Alice
    ('f8a9b0c1-d2e3-4567-fabc-678901234567', 'e1f2a3b4-c5d6-7890-efab-901234567890'), -- Microservices → Tom
    ('a9b0c1d2-e3f4-5678-abcd-789012345678', 'b8c9d0e1-f2a3-4567-bcde-678901234567'), -- Panel → Jane
    ('a9b0c1d2-e3f4-5678-abcd-789012345678', 'e1f2a3b4-c5d6-7890-efab-901234567890'); -- Panel → Tom

-- ============ QUESTIONS ============
INSERT INTO questions (id, session_id, user_id, content, upvotes, created_at)
VALUES
    ('b0c1d2e3-f4a5-6789-bcde-890123456789',
     'd6e7f8a9-b0c1-2345-defa-456789012345',
     'b2c3d4e5-f6a7-8901-bcde-f12345678901',
     'Will the slides be shared after the talk?', 2, '2026-09-15 09:15:00'),
    ('c1d2e3f4-a5b6-7890-cdef-901234567890',
     'd6e7f8a9-b0c1-2345-defa-456789012345',
     NULL,
     'Is this session being recorded?', 5, '2026-09-15 09:20:00'),
    ('d2e3f4a5-b6c7-8901-defa-012345678901',
     'e7f8a9b0-c1d2-3456-efab-567890123456',
     'b2c3d4e5-f6a7-8901-bcde-f12345678901',
     'What framework do you recommend for beginners?', 3, '2026-09-15 14:10:00'),
    ('e3f4a5b6-c7d8-9012-efab-123456789012',
     'e7f8a9b0-c1d2-3456-efab-567890123456',
     NULL,
     'Are there any prerequisites for this workshop?', 1, '2026-09-15 14:15:00'),
    ('f4a5b6c7-d8e9-0123-fabc-234567890123',
     'a9b0c1d2-e3f4-5678-abcd-789012345678',
     'b2c3d4e5-f6a7-8901-bcde-f12345678901',
     'When will the recording be published online?', 4, '2025-03-11 15:30:00');
