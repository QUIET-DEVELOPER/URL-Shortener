// static/js/main.js
document.addEventListener('DOMContentLoaded', function() {
    // Form submission handling
    const urlForm = document.querySelector('.url-form');
    if (urlForm) {
        urlForm.addEventListener('submit', function(e) {
            const submitButton = this.querySelector('button[type="submit"]');
            submitButton.classList.add('loading');
        });
    }
    
    // Set minimum date for expiry date input
    const expiryDateInput = document.getElementById('expiryDate');
    if (expiryDateInput) {
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        expiryDateInput.min = now.toISOString().slice(0, 16);
    }
    
    // Initialize tooltips
    const tooltips = document.querySelectorAll('[data-tooltip]');
    tooltips.forEach(tooltip => {
        tooltip.addEventListener('mouseenter', showTooltip);
        tooltip.addEventListener('mouseleave', hideTooltip);
    });
});

// Copy to clipboard functionality
function copyToClipboard() {
    const shortUrlInput = document.getElementById('shortUrlInput');
    if (!shortUrlInput) return;
    
    shortUrlInput.select();
    shortUrlInput.setSelectionRange(0, 99999); // For mobile devices
    
    try {
        navigator.clipboard.writeText(shortUrlInput.value).then(() => {
            showCopySuccess();
        });
    } catch (err) {
        document.execCommand('copy');
        showCopySuccess();
    }
}

function showCopySuccess() {
    const copyButton = document.querySelector('.btn-secondary');
    const originalText = copyButton.innerHTML;
    
    copyButton.innerHTML = '<i class="fas fa-check"></i> Copied!';
    copyButton.style.backgroundColor = '#16a34a';
    copyButton.style.color = 'white';
    
    setTimeout(() => {
        copyButton.innerHTML = originalText;
        copyButton.style.backgroundColor = '';
        copyButton.style.color = '';
    }, 2000);
}

// URL validation
function isValidUrl(url) {
    try {
        new URL(url);
        return true;
    } catch {
        return false;
    }
}

// Auto-refresh for recent URLs
let autoRefreshInterval;

function startAutoRefresh() {
    if (document.querySelector('.recent-urls')) {
        autoRefreshInterval = setInterval(refreshRecentUrls, 30000); // Refresh every 30 seconds
    }
}

function stopAutoRefresh() {
    clearInterval(autoRefreshInterval);
}

async function refreshRecentUrls() {
    try {
        const response = await fetch('/api/recent-urls');
        if (response.ok) {
            const recentUrls = await response.json();
            updateRecentUrlsList(recentUrls);
        }
    } catch (error) {
        console.error('Failed to refresh recent URLs:', error);
    }
}

function updateRecentUrlsList(urls) {
    const urlList = document.querySelector('.url-list');
    if (!urlList) return;
    
    urlList.innerHTML = urls.map(url => `
        <div class="url-item">
            <div class="url-details">
                <span class="original-url">${url.originalUrl}</span>
                <span class="short-url">${window.location.origin}/${url.shortUrl}</span>
                <span class="created-at">${formatDate(url.createdAt)}</span>
                <span class="clicks">Clicks: ${url.clickCount}</span>
            </div>
        </div>
    `).join('');
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

// Handle visibility change
document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        stopAutoRefresh();
    } else {
        startAutoRefresh();
    }
});

// Initialize auto-refresh when page loads
startAutoRefresh();