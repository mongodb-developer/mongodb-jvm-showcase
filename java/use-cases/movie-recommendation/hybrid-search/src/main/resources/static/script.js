const form = document.getElementById('search-form');
const input = document.getElementById('q');
const btn = document.getElementById('btn-search');
const results = document.getElementById('results');
const alerts = document.getElementById('alerts');
const chips = document.getElementById('active-filters');

const yearFromEl = document.getElementById('yearFrom');
const yearToEl = document.getElementById('yearTo');
const minImdbEl = document.getElementById('minIMDbRating');
const excludeGenres = document.getElementById('excludeGenres');

const FALLBACK_POSTER = 'https://i.ibb.co/hRGmNYDn/No-image-Available.png';

const placeholderExamples = [
    'A shark terrorizes a small beach town...',
    'A boy discovers on his birthday that he is a wizard...',
    'Scientists bring dinosaurs back to life on an island...',
    'A ship hits an iceberg and two lovers from different worlds...',
    'A man realizes his whole life is a reality TV show...',
    'Toys come alive when their owner leaves the room...'
];
let placeholderIndex = 0;
setInterval(() => {
    if (document.activeElement === input || input.value) return;
    placeholderIndex = (placeholderIndex + 1) % placeholderExamples.length;
    input.placeholder = placeholderExamples[placeholderIndex];
}, 3500);

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const q = input.value.trim();
    setBusy(true);
    clearUI();
    renderSkeletons(8);

    try {
        const genres = [...document.querySelectorAll('.genre:checked')].map(x => x.value);

        const req = {};
        if (q) req.query = q;
        if (yearFromEl?.value) req.yearFrom = Number(yearFromEl.value);
        if (yearToEl?.value) req.yearTo = Number(yearToEl.value);
        if (genres.length) req.genres = genres;
        if (minImdbEl?.value) req.minIMDbRating = Number(minImdbEl.value);
        if (excludeGenres?.checked) req.excludeGenres = true;

        renderChips(req);

        const resp = await fetch('/movies/search', {
            method: 'POST',
            headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
            body: JSON.stringify(req)
        });
        if (!resp.ok) throw new Error('HTTP ' + resp.status);

        const data = await resp.json();
        results.innerHTML = '';
        document.getElementById('status').textContent = Array.isArray(data) && data.length ? `${data.length} results` : '';
        if (!Array.isArray(data) || data.length === 0) {
            showAlert('No results found.', 'warning');
            return;
        }

        renderResults(data);
    } catch (err) {
        results.innerHTML = '';
        showAlert('Error fetching movies: ' + String(err?.message || err), 'danger');
    } finally {
        setBusy(false);
    }
});

function renderChips(req) {
    const items = [];

    if (req.query) items.push(['Search', `${req.query}`]);
    if (req.yearFrom != null || req.yearTo != null) items.push(['Year', `${req.yearFrom ?? ''}–${req.yearTo ?? ''}`]);

    if (req.genres && req.genres.length) {
        if (req.excludeGenres) {
            items.push(['Excluding genres', req.genres.join(', ')]);
        } else {
            items.push(['Genres', req.genres.join(', ')]);
        }
    }
    if (req.minIMDbRating != null) items.push(['IMDb ≥', req.minIMDbRating]);
    chips.innerHTML = items.map(([k, v]) =>
        `<span class="filter-chip">${esc(k)}: <strong>${esc(v)}</strong></span>`).join('');
}

function movieFields(m) {
    return {
        title:  pick(m, ['title','name']) || '(untitled)',
        year:   pick(m, ['year','releasedYear','releaseYear']),
        plot:   pick(m, ['fullplot','fullPlot','plot']) || '',
        rating: m?.imdb?.rating,
        genres: Array.isArray(m?.genres) ? m.genres : [],
        poster: m?.poster || FALLBACK_POSTER,
    };
}

function renderResults(items) {
    items.forEach((m, i) => {
        const { title, year, plot, rating, genres, poster } = movieFields(m);

        const card = document.createElement('article');
        card.className = 'movie-card';
        card.tabIndex = 0;
        card.style.animationDelay = `${Math.min(i, 12) * 40}ms`;
        card.addEventListener('click', () => openDetails(m));
        card.addEventListener('keydown', e => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                openDetails(m);
            }
        });

        const posterWrap = document.createElement('div');
        posterWrap.className = 'poster-wrap';
        const img = document.createElement('img');
        img.src = poster;
        img.alt = title;
        img.loading = 'lazy';
        img.className = 'movie-poster';
        img.onerror = () => { img.onerror = null; img.src = FALLBACK_POSTER; };
        posterWrap.appendChild(img);
        if (rating != null) {
            const badge = document.createElement('span');
            badge.className = 'rating-badge';
            badge.textContent = Number(rating).toFixed(1);
            posterWrap.appendChild(badge);
        }
        card.appendChild(posterWrap);

        const body = document.createElement('div');
        body.className = 'card-content';

        const h3 = document.createElement('h3');
        h3.className = 'movie-title';
        h3.textContent = title;
        body.appendChild(h3);

        const metaParts = [];
        if (year) metaParts.push(String(year));
        if (genres.length) metaParts.push(genres.slice(0, 2).join(', '));
        if (metaParts.length) {
            const meta = document.createElement('div');
            meta.className = 'movie-meta';
            meta.textContent = metaParts.join(' · ');
            body.appendChild(meta);
        }

        if (plot) {
            const p = document.createElement('p');
            p.className = 'movie-plot';
            p.textContent = plot;
            body.appendChild(p);
        }

        card.appendChild(body);
        results.appendChild(card);
    });
}

function renderSkeletons(count) {
    results.innerHTML = Array.from({ length: count }, () =>
        '<div class="skeleton"><div class="poster-wrap"></div><div class="skeleton-line"></div><div class="skeleton-line short"></div></div>'
    ).join('');
}

function openDetails(m) {

    const { title, year, plot, rating, genres, poster } = movieFields(m);

    const cast    = Array.isArray(m.cast) ? m.cast
        : (Array.isArray(m.actors) ? m.actors : []);

    const modalEl = document.getElementById('movieModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalPoster = document.getElementById('modalPoster');
    const modalMeta = document.getElementById('modalMeta');
    const modalGenres = document.getElementById('modalGenres');
    const modalCast = document.getElementById('modalCast');
    const modalPlot = document.getElementById('modalPlot');
    const modalExtra = document.getElementById('modalExtra');

    modalTitle.textContent = title;
    modalPoster.src = poster;
    modalPoster.alt = title;

    const parts = [];
    if (year) parts.push(String(year));
    if (rating != null) {
        parts.push(`IMDb ${Number(rating).toFixed(1)}`);
    }
    modalMeta.textContent = parts.join(' • ');

    modalGenres.innerHTML = '';
    if (genres.length) {
        genres.forEach(g => {
            const chip = document.createElement('span');
            chip.className = 'genre-chip';
            chip.textContent = g;
            modalGenres.appendChild(chip);
        });
    }

    modalCast.innerHTML = '';
    if (cast.length) {
        const label = document.createElement('div');
        label.className = 'movie-cast';
        label.innerHTML = `<strong>Cast:</strong> ` +
            cast.map(a => {
                const q = encodeURIComponent(a);
                return `<a href="https://www.google.com/search?q=${q}" target="_blank" rel="noopener noreferrer">${esc(a)}</a>`;
            }).join(', ');
        modalCast.appendChild(label);
    }

    modalPlot.textContent = plot;
    modalExtra.textContent = '';

    bootstrap.Modal.getOrCreateInstance(modalEl).show();
}

function setBusy(isBusy) {
    btn.disabled = isBusy;
    input.disabled = isBusy;
}

function clearUI() {
    alerts.innerHTML = '';
    results.innerHTML = '';
    chips.innerHTML = '';
    document.getElementById('status').textContent = '';
}

function showAlert(msg, type = 'info') {
    const div = document.createElement('div');
    div.className = `alert alert-${type}`;
    div.textContent = msg;
    alerts.appendChild(div);
}

function pick(obj, keys) {
    for (const k of keys) {
        if (obj && obj[k] != null && String(obj[k]).trim() !== '') return obj[k];
    }
    return undefined;
}

function esc(str) {
    return String(str ?? '').replace(/[&<>"']/g, s => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[s]));
}