document.addEventListener('DOMContentLoaded', () => {
    // Side Panel Toggle
    const profileBtn = document.getElementById('profile-btn');
    const sidePanel = document.getElementById('side-panel');
    const closeBtn = document.getElementById('close-btn');
    const overlay = document.getElementById('overlay');

    function openPanel() {
        if (!sidePanel || !overlay) return;
        overlay.style.display = 'block';
        setTimeout(() => {
            overlay.classList.add('active');
            sidePanel.classList.add('active');
        }, 10);
        document.body.style.overflow = 'hidden';
    }

    function closePanel() {
        if (!sidePanel || !overlay) return;
        sidePanel.classList.remove('active');
        overlay.classList.remove('active');
        setTimeout(() => { overlay.style.display = 'none'; }, 300);
        document.body.style.overflow = '';
    }

    if (profileBtn) profileBtn.addEventListener('click', openPanel);
    if (closeBtn) closeBtn.addEventListener('click', closePanel);
    if (overlay) overlay.addEventListener('click', closePanel);

    // Date display
    const dateEl = document.getElementById('date-display');
    if (dateEl) {
        const now = new Date();
        const months = ['Ocak','Şubat','Mart','Nisan','Mayıs','Haziran','Temmuz','Ağustos','Eylül','Ekim','Kasım','Aralık'];
        dateEl.textContent = now.getDate() + ' ' + months[now.getMonth()] + ' ' + now.getFullYear();
    }
    // Live Search
    const searchInput = document.getElementById('global-search-input');
    const searchOverlay = document.getElementById('search-results-overlay');
    let searchTimeout;

    if (searchInput && searchOverlay) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            clearTimeout(searchTimeout);

            if (query.length >= 3) {
                searchTimeout = setTimeout(async () => {
                    try {
                        const response = await fetch(`/api/articles/search?query=${encodeURIComponent(query)}&page=0&size=5`);
                        if (response.ok) {
                            const data = await response.json();
                            if (data.success && data.data && data.data.articles.length > 0) {
                                let html = '<ul style="list-style:none; padding:0; margin:0;">';
                                data.data.articles.forEach(article => {
                                    html += `
                                        <li style="border-bottom:1px solid #eee;">
                                            <a href="/haber/${article.articleId}" style="display:block; padding:10px; text-decoration:none; color:#1A1A1A;">
                                                <div style="font-weight:600; font-size:0.9rem;">${article.title}</div>
                                                <div style="font-size:0.75rem; color:#888;">${article.category ? article.category.name : ''}</div>
                                            </a>
                                        </li>
                                    `;
                                });
                                html += `<li style="text-align:center; padding:10px; background: #f8f9fa;"><a href="/ara?q=${encodeURIComponent(query)}" style="font-size:0.8rem; color:#E2001A; font-weight:bold; text-decoration:none;">Tüm Sonuçları Gör</a></li>`;
                                html += '</ul>';
                                searchOverlay.innerHTML = html;
                                searchOverlay.style.display = 'block';
                            } else {
                                searchOverlay.innerHTML = '<div style="padding:15px; color:#888; text-align:center; font-size:0.9rem;">Sonuç bulunamadı</div>';
                                searchOverlay.style.display = 'block';
                            }
                        }
                    } catch (err) {
                        console.error('Search error:', err);
                    }
                }, 500); // 500ms debounce
            } else {
                searchOverlay.style.display = 'none';
            }
        });

        // Hide overlay on click outside
        document.addEventListener('click', (e) => {
            if (!searchInput.contains(e.target) && !searchOverlay.contains(e.target)) {
                searchOverlay.style.display = 'none';
            }
        });

        // Show overlay on focus if length >= 3 
        searchInput.addEventListener('focus', () => {
            if (searchInput.value.trim().length >= 3 && searchOverlay.innerHTML !== '') {
                searchOverlay.style.display = 'block';
            }
        });
    }

    // WEATHER WIDGET (Open-Meteo) WITH CITY DROPDOWN & GEOLOCATION
    const tempSpan = document.getElementById('weather-temp');
    const citySelect = document.getElementById('weather-city');

    if (tempSpan && citySelect) {
        async function fetchWeather(lat, lon) {
            try {
                const response = await fetch(`https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true`);
                const data = await response.json();
                if (data && data.current_weather) {
                    tempSpan.textContent = Math.round(data.current_weather.temperature);
                }
            } catch (err) {
                console.error("Weather fetch failed:", err);
            }
        }

        async function fetchLocationByIP() {
            try {
                // Using ipapi.co (free, supports HTTPS)
                const response = await fetch('https://ipapi.co/json/');
                const data = await response.json();
                
                if (data && data.latitude && data.longitude) {
                    const lat = data.latitude.toFixed(2);
                    const lon = data.longitude.toFixed(2);
                    
                    console.info("Detected location by IP:", data.city);
                    
                    // Try to match city in dropdown
                    let matched = false;
                    for (let i = 0; i < citySelect.options.length; i++) {
                        const opt = citySelect.options[i];
                        if (opt.dataset.city && data.city && opt.dataset.city.toLowerCase() === data.city.toLowerCase()) {
                            citySelect.selectedIndex = i;
                            matched = true;
                            break;
                        }
                    }

                    if (!matched) {
                        const autoOpt = document.getElementById('auto-location-option');
                        if (autoOpt) autoOpt.text = `📍 ${data.city || 'Konumum'}`;
                        citySelect.value = "auto";
                    }

                    fetchWeather(lat, lon);
                } else {
                    throw new Error("IP location data incomplete");
                }
            } catch (err) {
                console.warn("IP geolocation failed, falling back to Istanbul:", err);
                citySelect.value = "41.01,28.98"; // Istanbul default
                fetchWeather("41.01", "28.98");
            }
        }

        function loadLocationWeather() {
            // Geolocation requires Secure Context (HTTPS or localhost)
            if (navigator.geolocation && window.isSecureContext) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const lat = position.coords.latitude.toFixed(2);
                        const lon = position.coords.longitude.toFixed(2);

                        let matched = false;
                        for (let i = 0; i < citySelect.options.length; i++) {
                            const opt = citySelect.options[i];
                            if (opt.value !== 'auto') {
                                const [olat, olon] = opt.value.split(',');
                                if (Math.abs(parseFloat(olat) - lat) < 0.5 && Math.abs(parseFloat(olon) - lon) < 0.5) {
                                    citySelect.selectedIndex = i;
                                    matched = true;
                                    break;
                                }
                            }
                        }

                        if (!matched) {
                            const autoOpt = document.getElementById('auto-location-option');
                            if (autoOpt) autoOpt.text = "📍 Bulunduğum Yer";
                            citySelect.value = "auto";
                        }

                        fetchWeather(lat, lon);
                    },
                    (error) => {
                        console.warn("Geolocation denied or failed, using IP-based location");
                        fetchLocationByIP();
                    }
                );
            } else {
                console.info("Navigator geolocation not available or insecure context, using IP-based location");
                fetchLocationByIP();
            }
        }

        // On change
        citySelect.addEventListener('change', (e) => {
            const val = e.target.value;
            if (val === 'auto') {
                tempSpan.textContent = "...";
                loadLocationWeather();
            } else {
                const [lat, lon] = val.split(',');
                fetchWeather(lat, lon);
            }
        });

        // Initial load
        loadLocationWeather();
    }

    // EXCHANGE RATES WIDGET (Free Currency API)
    const usdEl = document.getElementById('rate-usd');
    const eurEl = document.getElementById('rate-eur');
    const gbpEl = document.getElementById('rate-gbp');

    if (usdEl && eurEl && gbpEl) {
        async function fetchExchange() {
            try {
                const res = await fetch('https://open.er-api.com/v6/latest/USD');
                if (!res.ok) throw new Error("API failed");
                const data = await res.json();
                
                if (data && data.rates && data.rates.TRY) {
                    const tryRate = data.rates.TRY;
                    
                    // USD
                    usdEl.textContent = tryRate.toFixed(2);
                    document.getElementById('trend-usd').className = 'exchange-trend up';
                    document.getElementById('trend-usd').textContent = '▲';

                    // EUR (Calculate from cross-rate: TRY / EUR)
                    if (data.rates.EUR) {
                        eurEl.textContent = (tryRate / data.rates.EUR).toFixed(2);
                        document.getElementById('trend-eur').className = 'exchange-trend up';
                        document.getElementById('trend-eur').textContent = '▲';
                    }

                    // GBP (Calculate from cross-rate: TRY / GBP)
                    if (data.rates.GBP) {
                        gbpEl.textContent = (tryRate / data.rates.GBP).toFixed(2);
                        document.getElementById('trend-gbp').className = 'exchange-trend up';
                        document.getElementById('trend-gbp').textContent = '▲';
                    }
                }
            } catch (err) {
                console.error("Exchange API failed:", err);
                usdEl.textContent = "-";
                eurEl.textContent = "-";
                gbpEl.textContent = "-";
            }
        }
        fetchExchange();
    }

    // 6. TICKER DATA LOADER
    const tickerWrap = document.getElementById('ticker-wrap');
    if (tickerWrap) {
        async function loadTicker() {
            try {
                const res = await fetch('/api/articles?page=0&size=10&sort=publishedAt,desc');
                const json = await res.json();

                if (json.success && json.data.articles && json.data.articles.length > 0) {
                    let html = '';
                    json.data.articles.slice(0, 10).forEach(a => {
                        html += `<span class="ticker-item"><a href="/haber/${a.articleId}">• ${a.title}</a></span>`;
                    });
                    tickerWrap.innerHTML = html + html;
                }
            } catch (e) { }
        }
        loadTicker();
    }

});
