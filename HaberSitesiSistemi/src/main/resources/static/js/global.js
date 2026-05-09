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
                                html += `<li style="text-align:center; padding:10px;"><a href="/ara?q=${encodeURIComponent(query)}" style="font-size:0.8rem; color:#D32F2F; font-weight:bold; text-decoration:none;">Tüm Sonuçları Gör</a></li>`;
                                html += '</ul>';
                                searchOverlay.innerHTML = html;
                                searchOverlay.style.display = 'block';
                            } else {
                                searchOverlay.innerHTML = '<div style="padding:10px; color:#888; text-align:center; font-size:0.9rem;">Sonuç bulunamadı</div>';
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
});
