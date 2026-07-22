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

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const q = input.value.trim();
    if (!q) return;

    setBusy(true);
    clearUI();

    try {
        const genres = [...document.querySelectorAll('.genre:checked')].map(x => x.value);

        const req = { query: q };
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
        if (!resp.ok) new Error('HTTP ' + resp.status);

        const data = await resp.json();
        if (!Array.isArray(data) || data.length === 0) {
            showAlert('No results found.', 'warning');
            return;
        }

        renderResults(data);
    } catch (err) {
        showAlert('Error fetching movies: ' + String(err?.message || err), 'danger');
    } finally {
        setBusy(false);
    }
});

function renderChips(req) {
    const items = [];

    items.push(['Search', `${req.query}`]);
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
        `<span class="badge rounded-pill text-bg-secondary filter-chip">${k}: ${v}</span>`).join('');
}

function movieFields(m) {
    return {
        title:  pick(m, ['title','name']) || '(untitled)',
        year:   pick(m, ['year','releasedYear','releaseYear']),
        plot:   pick(m, ['fullplot','fullPlot','plot']) || '',
        rating: m?.imdb?.rating,
        genres: Array.isArray(m?.genres) ? m.genres : [],
        poster: m?.poster || 'https://i.ibb.co/hRGmNYDn/No-image-Available.png',
    };
}

function renderResults(items) {
    for (const m of items) {

        const { title, year, plot, rating, genres, poster } = movieFields(m);

        const col = document.createElement('div');
        col.className = 'col-6 col-md-4 col-lg-3';

        const card = document.createElement('div');
        card.className = 'card h-100 movie-card shadow-sm';

        const img = document.createElement('img');
        img.src = poster;
        img.alt = title;
        img.className = 'movie-poster card-img-top';
        card.appendChild(img);

        const body = document.createElement('div');
        body.className = 'card-body d-flex flex-column';

        const h5 = document.createElement('h5');
        h5.className = 'movie-title';
        h5.textContent = title;
        body.appendChild(h5);

        if (year || rating != null) {
            const meta = document.createElement('div');
            meta.className = 'movie-meta';
            const parts = [];
            if (year) parts.push(String(year));
            if (rating != null) {

                parts.push(`★ ${Number(rating).toFixed(1)}`);
            }
            meta.textContent = parts.join(' • ');
            body.appendChild(meta);
        }

        if (genres.length) {
            const wrap = document.createElement('div');
            const maxToShow = 3;
            genres.slice(0, maxToShow).forEach(g => {
                const chip = document.createElement('span');
                chip.className = 'genre-chip';
                chip.textContent = g;
                wrap.appendChild(chip);
            });
            if (genres.length > maxToShow) {
                const more = document.createElement('span');
                more.className = 'genre-chip genre-more';
                more.textContent = `+${genres.length - maxToShow}`;
                wrap.appendChild(more);
            }
            body.appendChild(wrap);
        }

        if (plot) {
            const p = document.createElement('p');
            p.className = 'movie-plot';
            p.textContent = plot;
            body.appendChild(p);
        }

        const btnDetails = document.createElement('button');
        btnDetails.type = 'button';
        btnDetails.className = 'btn btn-sm btn-outline-primary mt-auto';
        btnDetails.textContent = 'Details';
        btnDetails.addEventListener('click', () => openDetails(m));
        body.appendChild(btnDetails);

        card.appendChild(body);
        col.appendChild(card);
        results.appendChild(col);
    }
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

    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}

function setBusy(isBusy) {
    btn.disabled = isBusy;
    input.disabled = isBusy;
}

function clearUI() {
    alerts.innerHTML = '';
    results.innerHTML = '';
    chips.innerHTML = '';
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