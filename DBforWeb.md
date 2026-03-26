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
Sütunlar: role_id, role_name (Örn: ADMIN, EDITOR, USER)
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
Sütunlar: article_id, title, content, published_at, view_count, is_published, category_id, author_id
Primary Key (PK): article_id
Foreign Key (FK) 1: category_id -> Categories.category_id
Foreign Key (FK) 2: author_id -> Users.user_id
```

## 6. Comments (Yorumlar)

```
Sütunlar: comment_id, content, created_at, is_approved, article_id, user_id
Primary Key (PK): comment_id
Foreign Key (FK) 1: article_id -> Articles.article_id
Foreign Key (FK) 2: user_id -> Users.user_id
```
**Not:** `user_id` kolonu `NOT NULL` olmalıdır, böylece sadece üyeler yorum yapabilir.

## 7. Tags (Etiketler)

```
Sütunlar: tag_id, name
Primary Key (PK): tag_id
```

## 8. Article_Tags (Haber-Etiket İlişkisi - Çoka Çok)

```
Sütunlar: article_id, tag_id
Primary Key (PK): article_id, tag_id (Composite PK)
Foreign Key (FK) 1: article_id -> Articles.article_id
Foreign Key (FK) 2: tag_id -> Tags.tag_id
```

## 9. Media (Görseller ve Dosyalar)

```
Sütunlar: media_id, file_url, file_type, uploaded_at, article_id
Primary Key (PK): media_id
Foreign Key (FK): article_id -> Articles.article_id
```
**Not:** Dosya fiziksel olarak sunucuda tutulacak, burada sadece `file_url` yolu barınacak.

## 10. Audit_Logs (Yönetim Paneli İşlem Logları)

```
Sütunlar: log_id, action_type (CREATE, UPDATE, DELETE), table_name, record_id, action_date, user_id
Primary Key (PK): log_id
Foreign Key (FK): user_id -> Users.user_id
```
**Not:** Yönergedeki "İşlem loglarının tutulması" maddesi için gereklidir. Kimin, hangi tabloda, ne zaman işlem yaptığını tutar.

## 11. Session_Logs (Oturum ve Giriş Logları)

```
Sütunlar: session_id, ip_address, login_time, logout_time, is_success, user_id
Primary Key (PK): session_id
Foreign Key (FK): user_id -> Users.user_id
```
**Not:** Güvenlik ve "brute force koruması" loglamaları için kullanılabilir.

## 12. Saved_Articles (Kaydedilen Haberler)

```
Sütunlar: save_id, saved_at, user_id, article_id
Primary Key (PK): save_id
Foreign Key (FK) 1: user_id -> Users.user_id
Foreign Key (FK) 2: article_id -> Articles.article_id
```
**Not:** Standart kullanıcıların haberleri "daha sonra oku" listesine eklemesi için tasarlandı.
