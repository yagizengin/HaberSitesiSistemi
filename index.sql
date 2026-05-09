-- 1. Users Tablosu
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 2. Roles Tablosu
CREATE TABLE roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE NOT NULL
);

-- 3. User_Roles (Kullanıcı-Rol Çoka Çok İlişki)
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 4. Categories Tablosu
CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- 5. Articles Tablosu (cover_image_id FK aşağıda ALTER TABLE ile eklenir)
CREATE TABLE articles (
    article_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    published_at TIMESTAMP,
    view_count INT DEFAULT 0,
    is_published BOOLEAN DEFAULT FALSE,
    category_id BIGINT REFERENCES categories(category_id) ON DELETE SET NULL,
    author_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    cover_image_id BIGINT
);

-- 6. Comments Tablosu (user_id NOT NULL yapılarak anonim yorum engellendi)
CREATE TABLE comments (
    comment_id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT FALSE,
    article_id BIGINT REFERENCES articles(article_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. Tags Tablosu
CREATE TABLE tags (
    tag_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- 8. Article_Tags (Haber-Etiket Çoka Çok İlişki)
CREATE TABLE article_tags (
    article_id BIGINT REFERENCES articles(article_id) ON DELETE CASCADE,
    tag_id BIGINT REFERENCES tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);

-- 9. Media Tablosu (Sadece dosya yolu tutuluyor)
CREATE TABLE media (
    media_id BIGSERIAL PRIMARY KEY,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    article_id BIGINT REFERENCES articles(article_id) ON DELETE CASCADE
);

-- Articles <-> Media döngüsel bağımlılık çözümü: cover_image_id FK burada ekleniyor
ALTER TABLE articles ADD CONSTRAINT fk_articles_cover_image
    FOREIGN KEY (cover_image_id) REFERENCES media(media_id) ON DELETE SET NULL;

-- 10. Audit_Logs Tablosu (Admin Paneli İşlem Logları)
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    action_type VARCHAR(20) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id BIGINT,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL
);

-- 11. Session_Logs Tablosu (Giriş Logları ve Brute Force Koruması İçin)
CREATE TABLE session_logs (
    session_id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(45),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP,
    is_success BOOLEAN DEFAULT FALSE,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE
);

-- 12. Saved_Articles Tablosu (Kullanıcıların Kaydettiği Haberler)
CREATE TABLE saved_articles (
    save_id BIGSERIAL PRIMARY KEY,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    article_id BIGINT REFERENCES articles(article_id) ON DELETE CASCADE
);

-- Backend geliştiricisi için varsayılan roller:
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_EDITOR'), ('ROLE_USER');

-- =============================================
-- INDEX TANIMLARI (Performans Optimizasyonu)
-- =============================================

-- Articles: Yayınlanmış haberleri tarihe göre sıralama (en sık kullanılan sorgu)
CREATE INDEX idx_articles_published_date ON articles(is_published, published_at DESC);

-- Articles: Yazara göre haberleri listeleme
CREATE INDEX idx_articles_author ON articles(author_id);

-- Articles: Kategoriye göre haberleri listeleme
CREATE INDEX idx_articles_category ON articles(category_id);

-- Articles: Başlığa göre arama
CREATE INDEX idx_articles_title ON articles(title);

-- Comments: Bir haberin onaylı yorumlarını çekme
CREATE INDEX idx_comments_article_approved ON comments(article_id, is_approved);

-- Media: Bir habere ait görselleri çekme
CREATE INDEX idx_media_article ON media(article_id);

-- Session_Logs: Brute-force kontrolü (IP + başarı durumu)
CREATE INDEX idx_session_ip_success ON session_logs(ip_address, is_success);

-- Saved_Articles: Kullanıcının kaydettiği haberleri listeleme
CREATE INDEX idx_saved_user ON saved_articles(user_id);