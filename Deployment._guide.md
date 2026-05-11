# AWS & Spring Boot Tam Kapsamlı Yönetim Rehberi

## 1. Oturum ve Bağlantı Yönetimi

Sunucuya ve veritabanına erişim sağladığın, oturumları yönettiğin temel alan.

| İşlem | Komut |
| :--- | :--- |
| **Sunucuya Bağlan** | `ssh -i "anahtar-dosyan.pem" ubuntu@[EC2-IP-ADRESI]` |
| **PostgreSQL (RDS) Bağlan** | `psql -h [RDS_ENDPOINT] -U postgres -d haber_sitesi` |
| **PostgreSQL'den Çıkış** | `\q` |
| **Sunucudan Güvenli Çıkış** | `exit` veya CTRL + D |

*   **Kopmayan Arka Plan Oturumları**: Uzun sürecek işlemler yaparken SSH bağlantın kopsa bile işlemin devam etmesi için `screen` veya `tmux` kullanabilirsin (Örn: `screen -S deploy` ile yeni oturum açıp, CTRL+A+D ile arka plana atabilirsin).

## 2. Proje, Durum ve Ağ İzleme

Uygulamanın ayakta olup olmadığını, hataları ve ağ trafiğini kontrol ettiğin teşhis alanı.

*   **Klasör İçeriğini Gör**: `ls -F` (Özellikle `pom.xml` veya `target` klasörü için).
*   **Java Uygulaması Çalışıyor mu?**: `ps aux | grep java` (PID numarasını gösterir).
*   **Port Kontrolü**: 8080 portunun dinlenip dinlenmediğini teyit eder.
    ```bash
    sudo lsof -i :8080
    ```
*   **Ağ Trafiğini İzleme**: Hangi portların dışarıya açık olduğunu ve kimlerin bağlı olduğunu listeler.
    ```bash
    sudo netstat -tulpn
    ```
*   **Canlı Log İzleme (Temel)**:
    ```bash
    tail -f spring_log.txt
    ```
*   **Profesyonel Log İzleme (Systemd)**:
    ```bash
    sudo journalctl -u haber-sitesi -f
    ```

## 3. Uygulama Derleme, Başlatma ve Servis Yönetimi

Kod değişikliklerini canlıya aldığın (deployment) ve uygulamayı yönettiğin süreç.

*   **Yeni Kodları Çek**: `git pull`
*   **Uygulamayı Derle (Build)**:
    ```bash
    ./mvnw clean package -DskipTests
    ```
*   **Çalışan Uygulamayı Durdur**: `pkill -f java`
*   **Uygulamayı Zorla Kapat**: `kill -9 PID_NUMARASI`
*   **Uygulamayı Arka Planda Başlat (Nohup)**:
    ```bash
    nohup java -jar target/*.jar > spring_log.txt 2>&1 &
    ```
*   **Profesyonel Servis Yönetimi (Systemctl)**:
    ```bash
    sudo systemctl start haber-sitesi   # Başlat
    sudo systemctl stop haber-sitesi    # Durdur
    sudo systemctl restart haber-sitesi # Yeniden başlat
    sudo systemctl status haber-sitesi  # Durumu gör
    ```

## 4. Sistem Sağlığı, Kaynak Tüketimi ve Disk Temizliği

*   **RAM Kullanımı**: `free -m` (MB cinsinden gösterir).
*   **İşlem Yöneticisi (top)**: `top` (Hangi uygulama ne kadar kaynak yiyor? Çıkış için `q`).
*   **Görsel İşlem Yöneticisi (htop)**: `sudo apt install htop && htop`
*   **Disk Alanı Kontrolü**: `df -h`
*   **Detaylı Disk Analizi (ncdu)**: `sudo apt install ncdu && ncdu /`
*   **Log Dosyasını Sıfırla**: Diskte yer açmak için içini boşaltır.
    ```bash
    truncate -s 0 spring_log.txt
    ```
*   **Derleme Dosyalarını Temizle**:
    ```bash
    ./mvnw clean
    ```

## 5. PostgreSQL Veritabanı ve Veri Yönetimi

*   **Tabloları Listele**: `\dt`
*   **Tablo Yapısını Gör**: `\d tablo_adi`
*   **Veritabanlarını Listele**: `\l`
*   **Genişletilmiş Görünüm**: `\x` (Uzun satırları okunabilir formatta listeler).
*   **Veritabanını Yedekle (Dump)**:
    ```bash
    pg_dump -U postgres -d haber_sitesi > yedek_$(date +%F).sql
    ```
*   **Yedeği Geri Yükle (Restore)**:
    ```bash
    psql -U postgres -d haber_sitesi < yedek_dosyasi.sql
    ```

## 6. Görev Otomasyonu (Cron Jobs)

*   **Zamanlanmış Görev Düzenleyici**: `crontab -e`
*   **Örnek Cron Formatı**: `0 3 * * * pg_dump -U postgres -d haber_sitesi > /home/ubuntu/yedek.sql` (Her gece 03:00'da yedek alır).

---

> **Önemli Hatırlatma:** SSH üzerinden bağlandığında `cd HaberSitesiSistemi/HaberSitesiSistemi` (veya `pom.xml` neredeyse) komutuyla doğru dizine gittiğinden emin ol. Lokal testleri kapsamlı yapıp sunucuya yansıtmak, tekrar tekrar derleme yapmanı engelleyecektir.
