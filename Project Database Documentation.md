# Project Database Documentation

[TR](#proje-veritabanı-dokümantasyonu) / [EN](#project-database-documentation-en)

---

## Proje Veritabanı Dokümantasyonu

Bu belge, `index.sql` ve `pom.xml` dosyalarına dayanarak **Haber Sitesi Sistemi** projesinin veritabanı yapısını, bağımlılıklarını ve mevcut durumunu açıklamaktadır.

### 1. Veritabanı Genel Bakış
- **Veritabanı Yönetim Sistemi:** PostgreSQL (`org.postgresql:postgresql` bağımlılığı ve `BIGSERIAL` / `TIMESTAMP` tiplerine göre).
- **ORM / Veri Erişimi:** Spring Data JPA (`spring-boot-starter-data-jpa`).
- **Başlatma Betiği:** [index.sql](../index.sql)

### 2. Bağımlılık Analizi
`pom.xml` içerisinde tanımlanan veritabanı ile ilgili bağımlılıklar:
- **Postgres Sürücüsü:** `org.postgresql:postgresql` (Runtime kapsamı)
- **Spring Data JPA:** Soyutlama ve repository yönetimi için.
- **Doğrulama (Validation):** Entity kısıtlamaları için `spring-boot-starter-validation`.

### 3. Veritabanı Şeması (Tablolar)
Veritabanı, tam ölçekli bir haber portalı için tasarlanmış 15 ana tablodan oluşmaktadır.

#### Çekirdek Kullanıcı ve Yetkilendirme Tabloları
1.  **`users`**: Kullanıcı kimlik bilgileri, e-posta ve hesap durumu.
2.  **`roles`**: Erişim seviyeleri (`ROLE_ADMIN`, `ROLE_EDITOR`, `ROLE_USER`).
3.  **`user_roles`**: Kullanıcılar ve roller arasındaki Çok-tan-Çoğa eşleme.
4.  **`verification_tokens`**: E-posta doğrulama yönetimi.
5.  **`password_reset_tokens`**: Güvenli şifre sıfırlama işlemi.
6.  **`session_logs`**: Giriş denemeleri ve IP adresleri (güvenlik/brute-force koruması için).
7.  **`editor_requests`**: Kullanıcıların editörlük başvurularının yönetimi.

#### Haber ve İçerik Tabloları
8.  **`categories`**: Haber kategorileri (Siyaset, Spor vb.).
9.  **`articles`**: Haber içeriği için merkezi tablo. `view_count`, `is_published` ve yazar/kategori referanslarını içerir.
10. **`tags`**: Makaleler için benzersiz anahtar kelimeler.
11. **`article_tags`**: İçerik etiketleme için Çok-tan-Çoğa eşleme.
12. **`comments`**: Makalelere ve kullanıcılara bağlı onaylı veya bekleyen yorumlar.
13. **`media`**: Makalelerle ilişkili resim veya videoların dosya yolları.
14. **`saved_articles`**: Kullanıcıların haberi daha sonra okumak üzere kaydettiği kütüphane.

#### Sistem Denetimi
15. **`audit_logs`**: Şeffaflık için admin işlemlerinin (Ekleme, Güncelleme, Silme) detaylı günlükleri.

### 4. Varlık İlişkileri
- **Çok-tan-Çoğa (Many-to-Many):**
    - `users` ↔ `roles` (`user_roles` aracılığıyla)
    - `articles` ↔ `tags` (`article_tags` aracılığıyla)
- **Bir-den-Çoğa (One-to-Many):**
    - `categories` ➔ `articles`
    - `users` ➔ `articles` (Yazarlık)
    - `articles` ➔ `comments`
    - `articles` ➔ `media`
- **Bir-den-Bire / Çoğ-dan-Bire:**
    - `articles` ➔ `media` (`cover_image_id` için)

### 5. Performans Optimizasyonları
Sistem, yüksek trafikli senaryolar için çeşitli indeksler içerir:
- `idx_articles_published_date`: En son yayınlanan haberlerin listelenmesi için.
- `idx_articles_title`: Arama işlevselliği için.
- `idx_session_ip_success`: IP adresleri üzerinden güvenlik kontrolleri için.

### 6. Mevcut Durum
- **Şema Hazırlığı:** Kısıtlamalar (`ON DELETE CASCADE`, `UNIQUE`, `NOT NULL`) ile tam olarak tanımlanmıştır.
- **Başlangıç Verisi:** Temel roller betik aracılığıyla eklenmiştir.
- **Döngüsel Bağımlılık Çözümü:** `articles` ve `media` arasındaki ilişki, tablo oluşturma sonrası kısıtlama ile çözülmüştür.

---

<div id="project-database-documentation-en"></div>

## Project Database Documentation (EN)

This document describes the database structure, dependencies, and current state of the database for the **Haber Sitesi Sistemi** (News Site System) project.

### 1. Database Overview
- **Database Management System:** PostgreSQL.
- **ORM / Data Access:** Spring Data JPA.
- **Initialization Script:** [index.sql](../index.sql)

### 2. Dependency Analysis
The following database-related dependencies are defined in `pom.xml`:
- **Postgres Driver:** `org.postgresql:postgresql`
- **Spring Data JPA:** `spring-boot-starter-data-jpa`
- **Validation:** `spring-boot-starter-validation`

### 3. Database Schema (Tables)
The database consist of 15 main tables designed for a full-scale news portal.

#### Core User & Auth Tables
1.  **`users`**: User credentials and account status.
2.  **`roles`**: Access levels (`ROLE_ADMIN`, `ROLE_EDITOR`, `ROLE_USER`).
3.  **`user_roles`**: Many-to-Many mapping.
4.  **`verification_tokens`**: Email verification.
5.  **`password_reset_tokens`**: Password reset tokens.
6.  **`session_logs`**: Login tracking and security.
7.  **`editor_requests`**: Editor application management.

#### News & Content Tables
8.  **`categories`**: Content categories.
9.  **`articles`**: Central content table.
10. **`tags`**: Article keywords.
11. **`article_tags`**: Content tagging mapping.
12. **`comments`**: User comments.
13. **`media`**: Media file URLs.
14. **`saved_articles`**: User reading list.

#### System Audit
15. **`audit_logs`**: Admin action logging.

### 4. Entity Relationships
- **Many-to-Many:** Users/Roles, Articles/Tags.
- **One-to-Many:** Categories/Articles, Users/Articles, Articles/Comments, Articles/Media.

### 5. Performance Optimizations
Indexes for:
- Latest news sorting.
- Content search.
- Security/IP checks.

### 6. Current Status
- **Schema Readiness:** Fully defined with constraints.
- **Initial Data:** Basic roles seeded.
- **Circular Dependency Handling:** Resolved via post-creation constraints.

---
*Created on 2026-05-11*