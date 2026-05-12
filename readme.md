# Marmara Times / Haber Sitesi Sistemi

Marmara Times, Spring Boot ile geliştirilmiş rol bazlı bir haber sitesi sistemidir. Proje; ziyaretçilerin haberleri okuyabildiği, kayıtlı kullanıcıların yorum yapıp haber kaydedebildiği, editörlerin içerik üretebildiği ve admin kullanıcıların sistemi yönetebildiği uçtan uca bir web uygulaması olarak tasarlanmıştır.

Canlı site: [http://3.81.200.203:8080/](http://3.81.200.203:8080/)

## Geliştiriciler

- Yağız Engin
- Melih Aka
- Resul Tarık Yıldırımlı

## Proje Amacı

Bu proje, İleri Web Uygulamaları dersi kapsamında MVC mimarisi, güvenli kullanıcı yönetimi, ilişkisel veritabanı tasarımı, CRUD işlemleri, rol bazlı yetkilendirme ve cloud deployment konularını bir araya getiren modern bir haber portalı geliştirmek amacıyla hazırlanmıştır.

Sistem yalnızca haber listeleyen statik bir web sitesi değildir. Kayıt, giriş, e-posta doğrulama, şifre sıfırlama, editör paneli, admin paneli, yorum moderasyonu, medya yükleme, loglama, güvenlik kontrolleri ve AWS üzerinde canlı yayın süreçlerini birlikte ele alır.

## Temel Özellikler

- Ana sayfada manşet haberler, güncel haberler, çok okunanlar ve son dakika şeridi
- Kategori, etiket ve arama destekli haber listeleme
- Haber detay sayfası, yorumlar, görüntülenme sayısı ve kaydetme özelliği
- Kullanıcı kayıt, giriş, çıkış ve beni hatırla desteği
- E-posta doğrulama ve şifremi unuttum akışı
- Profil ekranı, kaydedilen makaleler, yorumlarım ve şifre değiştirme alanları
- Editör panelinde makale oluşturma, düzenleme, yayınlama ve medya yükleme
- Quill tabanlı zengin metin editörü
- YouTube, Twitter ve Instagram embed desteği
- Admin panelinde kullanıcı, rol, kategori, etiket, makale, yorum ve log yönetimi
- Aktif/pasif kullanıcı ve içerik durum takibi
- Hava durumu, döviz ve canlı üst bar bileşenleri
- AWS üzerinde canlı deployment

## Roller

| Rol | Yetkiler |
| --- | --- |
| USER | Haber okuma, yorum yapma, haber kaydetme, profil yönetimi |
| EDITOR | Makale oluşturma, düzenleme, yayınlama, medya ekleme |
| ADMIN | Kullanıcı, rol, kategori, etiket, makale, yorum ve log yönetimi |

## Kullanılan Teknolojiler

| Katman | Teknoloji |
| --- | --- |
| Backend | Java 21, Spring Boot 4.0.6 |
| MVC/View | Thymeleaf, HTML, CSS, JavaScript |
| Veritabanı | PostgreSQL |
| ORM | Spring Data JPA |
| Güvenlik | Spring Security, JWT, BCrypt |
| Mail | Spring Mail, JavaMailSender, SMTP |
| İçerik Temizleme | Jsoup |
| Deployment | AWS EC2 / PostgreSQL-RDS uyumlu yapı |

## Mimari Yapı

Proje MVC mimarisine uygun şekilde katmanlara ayrılmıştır:

- `Model`: User, Article, Category, Tag, Comment, Media, Role gibi entity sınıfları
- `Repository`: Spring Data JPA veri erişim katmanı
- `Service`: İş kuralları, CRUD akışları, güvenlik ve loglama işlemleri
- `Controller`: REST API endpointleri
- `PageController`: Thymeleaf sayfa yönlendirmeleri
- `Security`: JWT, session, remember-me, IP blocking ve Spring Security ayarları
- `templates`: Thymeleaf view dosyaları
- `static`: CSS, JavaScript ve statik web dosyaları

View katmanında veritabanı sorgusu yapılmaz. Controller katmanı HTML üretmez; iş kuralları servis sınıfları üzerinden yürütülür.

## Veritabanı

Sistem PostgreSQL üzerinde tasarlanmıştır ve 15 ilişkili tablo içerir. Kullanıcı-yetki, haber-içerik ve denetim kayıtları foreign key ilişkileriyle bağlanmıştır.

Başlıca tablolar:

- `users`
- `roles`
- `user_roles`
- `articles`
- `categories`
- `tags`
- `article_tags`
- `comments`
- `media`
- `saved_articles`
- `verification_tokens`
- `password_reset_tokens`
- `session_logs`
- `audit_logs`
- `editor_requests`

Veritabanı şeması için kök dizindeki `index.sql` dosyası ve ER diyagramı kullanılabilir.

## Güvenlik Özellikleri

- Şifreler BCrypt ile hashlenir.
- Web tarafında session tabanlı authentication kullanılır.
- API tarafında JWT tabanlı stateless authentication desteği vardır.
- `remember-me` özelliği Spring Security üzerinden yapılandırılmıştır.
- Oturum zaman aşımı 10 dakika olarak ayarlanmıştır.
- Admin ve editor sayfaları rol bazlı erişim kurallarıyla korunur.
- SQL Injection riskine karşı veri erişimi Spring Data JPA repository yapısı üzerinden yürütülür.
- Web formlarında CSRF token mekanizması kullanılır.
- Kullanıcıdan gelen zengin içerikler Jsoup ile temizlenerek XSS riski azaltılır.
- Başarısız giriş denemeleri session loglarına yazılır.
- Kısa sürede çok fazla başarısız giriş yapan IP adresleri geçici olarak engellenir.
- Admin işlemleri audit log yapısıyla izlenebilir.
- Beklenmeyen hatalarda kullanıcıya sistem detayı sızdırmayan kontrollü hata mesajları döndürülür.

## Kurulum

### Gereksinimler

- Java 21
- Maven Wrapper veya Maven
- PostgreSQL
- Git

### Projeyi İndirme

```bash
git clone https://github.com/yagizengin/HaberSitesiSistemi
cd HaberSitesiSistemi/HaberSitesiSistemi
```

### Veritabanı Hazırlığı

PostgreSQL üzerinde `haber_sitesi` isimli bir veritabanı oluşturun.

```sql
CREATE DATABASE haber_sitesi;
```

Ardından proje kök dizinindeki `index.sql` dosyasını veritabanına uygulayın.

```bash
psql -U postgres -d haber_sitesi -f ../index.sql
```

Komutu çalıştırdığınız dizine göre `index.sql` yolu değişebilir.

### Gizli Ayarlar

Proje `application.properties` içinde `./config/application-secret.properties` dosyasını opsiyonel olarak içe aktarır. Yerel ortamda `HaberSitesiSistemi/config/application-secret.properties` dosyası oluşturulmalıdır:

```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_DATABASE_PASSWORD

app.jwt.secret=YOUR_LONG_JWT_SECRET
app.base-url=http://localhost:8080

spring.mail.username=YOUR_MAIL_ADDRESS
spring.mail.password=YOUR_MAIL_APP_PASSWORD
```

Gerçek veritabanı, mail ve JWT bilgileri GitHub'a yüklenmemelidir.

### Lokal Çalıştırma

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Alternatif olarak jar dosyası ile çalıştırabilirsiniz:

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

Uygulama varsayılan olarak şu adreste açılır:

```text
http://localhost:8080/
```

## Deployment Özeti

Canlı sistem AWS üzerinde çalıştırılacak şekilde hazırlanmıştır.

Temel deployment akışı:

1. EC2 sunucusuna SSH ile bağlanılır.
2. Projenin güncel kodları `git pull` ile alınır.
3. Uygulama `./mvnw clean package -DskipTests` ile paketlenir.
4. PostgreSQL/RDS bağlantısı doğrulanır.
5. 8080 portu kontrol edilir.
6. Uygulama `nohup` veya `systemctl` ile arka planda başlatılır.
7. Loglar `tail` veya `journalctl` ile izlenir.
8. Veritabanı yedekleri `pg_dump` ile alınabilir.

Canlı yayın adresi:

[http://3.81.200.203:8080/](http://3.81.200.203:8080/)

## Proje Yapısı

```text
HaberSitesiSistemi/
  src/
    main/
      java/HaberSitesiSistemi/
        Config/
        Controller/
        DTO/
        Exception/
        Mapper/
        Model/
        Repository/
        Security/
        Service/
        Util/
      resources/
        static/
        templates/
        application.properties
  pom.xml
  mvnw
  mvnw.cmd
index.sql
DB-ER Diagram.png
Deployment._guide.md
```

## Rapor ve Dokümantasyon

Proje kapsamında hazırlanan temel dokümanlar:

- `rapor.pdf`: Proje raporu, görev paylaşımı, ekran görüntüleri ve değerlendirme kriterleri
- `rapor.docx`: Düzenlenebilir rapor dosyası
- `Project Database Documentation.md`: Veritabanı dokümantasyonu
- `Deployment._guide.md`: AWS ve Spring Boot deployment rehberi
- `DB-ER Diagram.png`: Veritabanı ER diyagramı
