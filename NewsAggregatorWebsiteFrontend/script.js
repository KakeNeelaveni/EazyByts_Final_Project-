const API_BASE = 'http://localhost:8080';

// LOGIN
document.getElementById('loginForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();
    const username = document.getElementById("username").value.trim().toLowerCase();
    const password = document.getElementById("password").value;
    const messageArea = document.getElementById("messageArea");

    try {
        const res = await fetch(`${API_BASE}/api/users/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) throw new Error("Login failed");

        const user = await res.json();
        localStorage.setItem("user", JSON.stringify(user));
        window.location.href = "dashboard.html";
    } catch (err) {
        if (messageArea) messageArea.textContent = "Invalid username or password";
    }
});

// REGISTER
document.getElementById('registerForm')?.addEventListener('submit', async function (e) {
    e.preventDefault();
    const username = document.getElementById("regUsername").value.trim().toLowerCase();
    const password = document.getElementById("regPassword").value;
    const messageArea = document.getElementById("messageArea");

    const preferences = Array.from(document.querySelectorAll('input[name="preferences"]:checked'))
        .map(cb => cb.value).join(',');

    try {
        const res = await fetch(`${API_BASE}/api/users/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password, preferences })
        });

        if (res.status === 409) throw new Error("Username already exists");
        if (!res.ok) throw new Error("Registration failed");

        alert("Registration successful! Redirecting to login...");
        window.location.href = "index.html";
    } catch (err) {
        if (messageArea) messageArea.textContent = err.message;
    }
});

// DASHBOARD: Load News
window.onload = function () {
    if (window.location.pathname.includes("dashboard.html")) {
        loadNews();
        const searchInput = document.getElementById("searchInput");
        if (searchInput) {
            searchInput.addEventListener("input", searchNews);
        }
    }
};

async function loadNews() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.id) {
        alert("User not logged in");
        window.location.href = "index.html";
        return;
    }

    document.getElementById("greeting").textContent = `Welcome To News Aggregator App, ${user.username}!`;

    const categoryFilters = document.getElementById("category-filters");
    if (user.preferences && categoryFilters) {
        categoryFilters.innerHTML = ""; // Clear existing
        const categories = [...new Set(user.preferences.split(",").map(c => c.trim().toLowerCase()))].sort();
        categories.forEach(category => {
            const btn = document.createElement("button");
            btn.className = "btn btn-outline-secondary btn-sm me-2 mb-2";
            btn.textContent = category;
            btn.onclick = () => filterByCategory(category);
            categoryFilters.appendChild(btn);
        });
    }

    const container = document.getElementById("news-container");
    if (!container) return;

    try {
        const response = await fetch(`${API_BASE}/api/news?userId=${user.id}`);
        if (!response.ok) throw new Error("Failed to load news");

        const newsList = await response.json();
        container.innerHTML = "";

        if (newsList.length === 0) {
            container.innerHTML = "<p class='text-muted'>No news articles available based on your preferences.</p>";
            return;
        }

        newsList.forEach(news => {
            container.innerHTML += renderNewsCard(news);
        });
    } catch (err) {
        alert(err.message);
    }
}

// CATEGORY FILTER (fixed: uses same endpoint as loadNews but adds category param)
async function filterByCategory(category) {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.id) return;

    const container = document.getElementById("news-container");
    if (!container) return;

    try {
        const response = await fetch(`${API_BASE}/api/news?userId=${user.id}&category=${category}`);
        if (!response.ok) throw new Error("Failed to load filtered news");

        const newsList = await response.json();
        container.innerHTML = "";

        if (newsList.length === 0) {
            container.innerHTML = "<p class='text-muted'>No news articles in this category.</p>";
            return;
        }

        newsList.forEach(news => {
            container.innerHTML += renderNewsCard(news);
        });
    } catch (err) {
        alert(err.message);
    }
}

// Render a single news card
function renderNewsCard(news) {
    return `
        <div class="card mb-3 shadow-sm" data-category="${news.category || ''}">
            <div class="card-body">
                <h5 class="card-title">${news.title}</h5>
                <p class="card-text">${news.description}</p>
                <a href="${news.url}" class="btn btn-sm btn-outline-primary" target="_blank">Read more</a>
            </div>
        </div>`;
}

// LOGOUT
function logout() {
    localStorage.removeItem("user");
    window.location.href = "index.html";
}

// SEARCH with Debounce — wired to input onload
let debounceTimer;
function searchNews() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        const query = document.getElementById("searchInput")?.value.toLowerCase();
        const cards = document.querySelectorAll("#news-container .card");

        cards.forEach(card => {
            const text = card.innerText.toLowerCase();
            card.style.display = text.includes(query) ? "block" : "none";
        });
    }, 300);
}
