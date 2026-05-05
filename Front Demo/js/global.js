/* Gündem Türkiye - Global JS (V5.0 - Personalization Update) */
document.addEventListener('DOMContentLoaded', () => {
    
    const panelContainer = document.getElementById('panel-container');
    const overlay = document.getElementById('overlay');
    const profileBtn = document.getElementById('profile-btn');

    if (panelContainer) {
        fetch('bilesenler/sag_profil_paneli.html')
            .then(response => {
                if (!response.ok) throw new Error('HTML yüklenemedi.');
                return response.text();
            })
            .then(html => {
                panelContainer.innerHTML = html;
                initPanelEvents();
            })
            .catch(error => console.error('Bileşen Hatası:', error));
    }

    function initPanelEvents() {
        const sidePanel = document.getElementById('side-panel');
        const closeBtn = document.getElementById('close-btn');
        
        const panelTitle = sidePanel.querySelector('h2'); 
        const panelDesc = sidePanel.querySelector('.panel-desc'); 
        const mainBtn = sidePanel.querySelector('.subscribe-btn'); 
        const subText = sidePanel.querySelector('.login-link'); 
        const logoutLink = sidePanel.querySelector('.logout-link'); 
        
        const menuItems = sidePanel.querySelectorAll('.panel-menu li');
        const hesapLink = menuItems[0];
        const kayitliLink = menuItems[2];
        const bultenLink = menuItems[3];

        const updateUIByState = () => {
            const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
            const userName = localStorage.getItem('userName') || "Kullanıcı"; // İsmi çek

            if (isLoggedIn) {
                panelTitle.innerText = `Hoş Geldin, ${userName}.`; // Kişiselleştirilmiş karşılama
                panelDesc.innerText = "Gündemi sınırsız takip edebilirsiniz. Reklamsız bir deneyim için abone olabilirsiniz.";;
                mainBtn.innerText = "ABONE OL";
                mainBtn.className = "subscribe-btn subscribe-btn-active";
                subText.innerHTML = 'Abonelik durumunuzu kontrol edin.';
                logoutLink.innerText = "Çıkış yap";
                [hesapLink, kayitliLink, bultenLink].forEach(el => el.classList.remove('disabled-element'));
                hesapLink.querySelector('a').href = "profil.html";
            } else {
                panelTitle.innerText = "Günaydın, Kullanıcı.";
                panelDesc.innerText = "Şu anda herhangi bir hesaba bağlı bulunmamaktasınız.";
                mainBtn.innerText = "DAHA FAZLA ERİŞİM İÇİN GİRİŞ YAP";
                mainBtn.className = "subscribe-btn login-btn-active";
                subText.innerHTML = 'Zaten abone misiniz? <a href="login.html">Giriş yapmayı deneyin</a>';
                logoutLink.innerText = "Giriş yap";
                [hesapLink, kayitliLink, bultenLink].forEach(el => el.classList.add('disabled-element'));
            }
        };

        if (profileBtn) {
            profileBtn.addEventListener('click', () => {
                updateUIByState();
                overlay.style.display = 'block';
                setTimeout(() => {
                    sidePanel.classList.add('active');
                    overlay.classList.add('active');
                }, 10);
                document.body.style.overflow = 'hidden';
            });
        }

        const closePanel = () => {
            sidePanel.classList.remove('active');
            overlay.classList.remove('active');
            setTimeout(() => { overlay.style.display = 'none'; }, 300);
            document.body.style.overflow = '';
        };

        if (closeBtn) closeBtn.addEventListener('click', closePanel);
        if (overlay) overlay.addEventListener('click', closePanel);
        document.addEventListener('keydown', (e) => { if(e.key === "Escape") closePanel(); });

        // GİRİŞ/ÇIKIŞ YÖNETİMİ (Tekil ve rasyonel hale getirildi)
        logoutLink.addEventListener('click', (e) => {
            e.preventDefault();
            if (localStorage.getItem('isLoggedIn') === 'true') {
                localStorage.setItem('isLoggedIn', 'false');
                localStorage.removeItem('userName');
                window.location.reload();
            } else {
                window.location.href = 'login.html';
            }
        });

        mainBtn.addEventListener('click', () => {
            if (localStorage.getItem('isLoggedIn') !== 'true') {
                window.location.href = 'login.html';
            } else {
                console.log("Abonelik işlemleri tetiklendi.");
            }
        });
    }

    // 2. HABER VERİ TABANI VE RENDERING
    let globalHaberVerisi = [];
    fetch('data.json')
        .then(response => response.json())
        .then(data => {
            globalHaberVerisi = data; 
            renderAnaSayfa(data);     
        })
        .catch(err => console.error(err));

    function renderAnaSayfa(data) {
        const container = document.getElementById('news-container');
        if (!container) return; 
        let htmlContent = '';
        data.forEach(modul => {
            htmlContent += `
            <section class="news-module">
                <div class="grid-3-6-3">
                    <div class="left-col">
                        ${modul.ust_sol.map(h => `<article class="news-item"><small>${h.kategori}</small><h4>${h.baslik}</h4></article>`).join('')}
                    </div>
                    <div class="center-col">
                        <article class="hero-item">
                            <img src="${modul.ust_orta.gorsel}">
                            <h2>${modul.ust_orta.baslik}</h2>
                            <p>${modul.ust_orta.ozet}</p>
                        </article>
                    </div>
                    <div class="right-col">
                        ${modul.ust_sag.map(h => `<article class="news-item"><small>${h.kategori}</small><h4>${h.baslik}</h4></article>`).join('')}
                    </div>
                </div>
            </section>
            <div class="module-divider"></div>`;
        });
        container.innerHTML = htmlContent;
    }

    // 3. ARAMA MOTORU VE YÖNLENDİRME
    const aramaInput = document.getElementById('aramaInput');
    if (aramaInput) {
        aramaInput.addEventListener('input', function(e) {
            const arananKelime = e.target.value.toLowerCase().trim();
            const mainContainer = document.getElementById('news-container');
            const searchContainer = document.getElementById('search-results-container');
            const searchGrid = document.getElementById('search-results-grid');

            if (arananKelime.length < 3) {
                mainContainer.style.display = 'block';
                if (searchContainer) searchContainer.style.display = 'none';
                return;
            }

            mainContainer.style.display = 'none';
            if (searchContainer) searchContainer.style.display = 'block';

            let bulunanHaberler = [];
            globalHaberVerisi.forEach(modul => {
                [...modul.ust_sol, ...modul.ust_sag, ...modul.alt_dortlu].forEach(haber => {
                    if (haber.baslik.toLowerCase().includes(arananKelime)) bulunanHaberler.push(haber);
                });
                if (modul.ust_orta.baslik.toLowerCase().includes(arananKelime)) {
                    bulunanHaberler.push({ gorsel: modul.ust_orta.gorsel, kategori: "MANŞET", baslik: modul.ust_orta.baslik });
                }
            });

            if (searchGrid) {
                searchGrid.innerHTML = bulunanHaberler.map(h => `
                    <article class="news-item">
                        <small>${h.kategori}</small>
                        <h4>${h.baslik}</h4>
                    </article>
                `).join('');
            }
        });
    }

    document.body.addEventListener('click', function(e) {
        const tiklananKart = e.target.closest('.news-item') || e.target.closest('.hero-item');
        if (tiklananKart) {
            const baslikEtiketi = tiklananKart.querySelector('h4') || tiklananKart.querySelector('h2');
            if (baslikEtiketi) {
                const haberBasligi = baslikEtiketi.innerText;
                window.location.href = `haber_detay.html?haber=${encodeURIComponent(haberBasligi)}`;
            }
        }
    });
});