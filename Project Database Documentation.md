# Proje Veritabanı Dökümanı

Bu döküman, proje veritabanı şemasını ve tablolarını açıklamaktadır.

## 1. Users (Kullanıcılar)

```
Sütunlar: user_id, username, email, password_hash, created_at, is_active
Primary Key (PK): user_id
```
**Not:** Şifreler düz metin değil, `password_hash` olarak tutulacak.

## 2. Roles (Roller)

```
Sütunlar: role_id, role_name (Örn: ROLE_ADMIN, ROLE_EDITOR, ROLE_USER)
Primary Key (PK): role_id
```

## 3. User_Roles (Kullanıcı-Rol İlişkisi - Çoka Çok)

```
Sütunlar: user_id, role_id
Primary Key (PK): user_id, role_id (Composite PK)
Foreign Key (FK) 1: user_id -> Users.user_id
Foreign Key (FK) 2: role_id -> Roles.role_id
```

## 4. Categories (Haber Kategorileri)

```
Sütunlar: category_id, name, description, is_active
Primary Key (PK): category_id
```

## 5. Articles (Haberler)

```
Sütunlar: article_id, title, content, published_at, view_count, is_published, category_id, author_id, cover_image_id
Primary Key (PK): article_id
Foreign Key (FK) 1: category_id -> Categories.category_id
Foreign Key (FK) 2: author_id -> Users.user_id
Foreign Key (FK) 3: cover_image_id -> Media.media_id (ALTER TABLE ile eklenir)
```
**Not:** `cover_image_id` FK'sı, Articles ve Media arasındaki döngüsel bağımlılık nedeniyle `ALTER TABLE` ile eklenmektedir.

## 6. Verification_Tokens (E-posta Doğrulama)

```
Sütunlar: token_id, token, user_id, expiry_date, created_at
Primary Key (PK): token_id
Foreign Key (FK): user_id -> Users.user_id
```

## 7. Password_Reset_Tokens (Şifre Sıfırlama)

```
Sütunlar: token_id, token, user_id, expiry_date, created_at
Primary Key (PK): token_id
Foreign Key (FK): user_id -> Users.user_id
```

## 8. Comments (Yorumlar)

```
Sütunlar: comment_id, content, created_at, is_approved, article_id, user_id
Primary Key (PK): comment_id
Foreign Key (FK) 1: article_id -> Articles.article_id
Foreign Key (FK) 2: user_id -> Users.user_id
```
**Not:** `user_id` kolonu `NOT NULL` olmalıdır, böylece sadece üyeler yorum yapabilir.

## 9. Tags (Etiketler)

```
Sütunlar: tag_id, name
Primary Key (PK): tag_id
```

## 10. Article_Tags (Haber-Etiket İlişkisi - Çoka Çok)

```
Sütunlar: article_id, tag_id
Primary Key (PK): article_id, tag_id (Composite PK)
Foreign Key (FK) 1: article_id -> Articles.article_id
Foreign Key (FK) 2: tag_id -> Tags.tag_id
```

## 11. Media (Görseller ve Dosyalar)

```
Sütunlar: media_id, file_url, file_type, uploaded_at, article_id
Primary Key (PK): media_id
Foreign Key (FK): article_id -> Articles.article_id
```
**Not:** Dosya fiziksel olarak sunucuda tutulacak, burada sadece `file_url` yolu barınacak.

## 12. Audit_Logs (Yönetim Paneli İşlem Logları)

```
Sütunlar: log_id, action_type (CREATE, UPDATE, DELETE), table_name, record_id, action_date, user_id
Primary Key (PK): log_id
Foreign Key (FK): user_id -> Users.user_id
```
**Not:** Yönergedeki "İşlem loglarının tutulması" maddesi için gereklidir. Kimin, hangi tabloda, ne zaman işlem yaptığını tutar.

## 13. Session_Logs (Oturum ve Giriş Logları)

```
Sütunlar: session_id, ip_address, login_time, logout_time, is_success, user_id
Primary Key (PK): session_id
Foreign Key (FK): user_id -> Users.user_id
```
**Not:** Güvenlik ve "brute force koruması" loglamaları için kullanılabilir.

## 14. Saved_Articles (Kaydedilen Haberler)

```
Sütunlar: save_id, saved_at, user_id, article_id
Primary Key (PK): save_id
Foreign Key (FK) 1: user_id -> Users.user_id
Foreign Key (FK) 2: article_id -> Articles.article_id
```
**Not:** Standart kullanıcıların haberleri "daha sonra oku" listesine eklemesi için tasarlandı.

## 15. Editor_Requests (Editörlük Başvuruları)

```
Sütunlar: request_id, user_id, status, created_at
Primary Key (PK): request_id
Foreign Key (FK): user_id -> Users.user_id
```
**Not:** Kullanıcıların editör olmak için yaptığı başvuruları tutar. Durumu PENDING, APPROVED, REJECTED olabilir.

## Index Tanımları (Performans Optimizasyonu)

```
idx_articles_published_date  -> articles(is_published, published_at DESC)
idx_articles_author          -> articles(author_id)
idx_articles_category        -> articles(category_id)
idx_articles_title           -> articles(title)
idx_comments_article_approved -> comments(article_id, is_approved)
idx_media_article            -> media(article_id)
idx_session_ip_success       -> session_logs(ip_address, is_success)
idx_saved_user               -> saved_articles(user_id)
```
**Not:** Sık kullanılan sorguların performansını artırmak için eklendi. Composite index'ler (birden fazla sütun içerenler) ilgili sorguların her iki koşulunu da hızlandırır.
