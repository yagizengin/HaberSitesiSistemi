/* Haber Detay Sayfası Veri Çekme ve Çizim Algoritması */
document.addEventListener('DOMContentLoaded', () => {
    // 1. URL'den 'haber' parametresini oku ona göre yazdır
    const urlParams = new URLSearchParams(window.location.search);
    const arananBaslik = urlParams.get('haber');

    const detayKonteyner = document.getElementById('detay-icerik');
    if (!detayKonteyner) return;

    if (!arananBaslik) {
        detayKonteyner.innerHTML = '<h1 style="color:#D32F2F;">Hata: Haber parametresi bulunamadı.</h1><a href="index.html">Ana Sayfaya Dön</a>';
        return;
    }

    // 2. data.json veritabanından haberi bulma kısmı
    fetch('data.json')
        .then(response => response.json())
        .then(data => {
            let secilenHaber = null;

            // Tüm modülleri tarama
            data.forEach(modul => {
                [...modul.ust_sol, ...modul.ust_sag, ...modul.alt_dortlu].forEach(haber => {
                    if (haber.baslik === arananBaslik) secilenHaber = haber;
                });
                
                if (modul.ust_orta.baslik === arananBaslik) {
                    secilenHaber = { 
                        baslik: modul.ust_orta.baslik, 
                        gorsel: modul.ust_orta.gorsel, 
                        kategori: "MANŞET",
                        ozet: modul.ust_orta.ozet
                    };
                }
            });

            // 3. Haberi Ekrana Yazdırma
            if (secilenHaber) {
                // Mock veride uzun metin olmadığı için standart bir yer tutucu (Lorem) metin eklenir
                const detayMetni = secilenHaber.ozet ? secilenHaber.ozet + "<br><br>" : "";
                const yerTutucuMetin = "Bu alan veritabanından gelecek detaylı makale metnini temsil eder. Türkiye'nin ve dünyanın önde gelen gelişmeleri, uzman muhabir kadromuz tarafından sahada teyit edilerek sizlere aktarılmaktadır. Sistem entegrasyonu tamamlandığında, bu alanda Java backend sunucusundan gelen binlerce kelimelik HTML içerikli gövde metinleri (paragraflar, ara başlıklar, tweet veya video yerleştirmeleri) görüntülenecektir.";

                detayKonteyner.innerHTML = `
                    <small style="color: #D32F2F; font-size: 0.9rem; font-weight: 700; text-transform: uppercase;">${secilenHaber.kategori}</small>
                    <h1 style="font-family: var(--font-heading); font-size: 2.8rem; line-height: 1.2; margin: 15px 0 30px 0;">${secilenHaber.baslik}</h1>
                    ${secilenHaber.gorsel ? `<img src="${secilenHaber.gorsel.replace('300/200', '900/500')}" style="width: 100%; border-radius: 8px; margin-bottom: 30px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">` : ''}
                    <div style="font-family: var(--font-body); font-size: 1.2rem; line-height: 1.8; color: #333;">
                        <p style="font-weight: bold;">${detayMetni}</p>
                        <p>${yerTutucuMetin}</p>
                        <p>${yerTutucuMetin}</p>
                    </div>
                `;
            } else {
                detayKonteyner.innerHTML = '<h1 style="color:#D32F2F;">404 - Haber bulunamadı veya yayından kaldırıldı.</h1>';
            }
        })
        .catch(error => console.error('Haber yükleme hatası:', error));
});