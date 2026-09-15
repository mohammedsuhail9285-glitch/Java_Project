/**
 * NAVISENSE AI - Client Application Scripts
 * Smart India Hackathon 2026 (SIH26006)
 */

// Toggle sidebar on mobile and desktop
function toggleSidebar() {
    const sidebar = document.getElementById('watchfloorSidebar');
    if (sidebar) {
        sidebar.classList.toggle('open');
    }
}

// Close sidebar when clicking outside on mobile
document.addEventListener('click', function(event) {
    const sidebar = document.getElementById('watchfloorSidebar');
    const toggleBtn = document.querySelector('.mobile-nav-toggle');
    if (sidebar && sidebar.classList.contains('open')) {
        if (!sidebar.contains(event.target) && (!toggleBtn || !toggleBtn.contains(event.target))) {
            sidebar.classList.remove('open');
        }
    }
});

// Scenario Autofill for Charter Analysis
function loadDemoScenario(num) {
    const scenarios = {
        1: {
            cargo: "Coking Coal",
            qty: 150000,
            origin: "Australia",
            loadPort: "Hay Point",
            destPort: "Paradip",
            vessel: "Panamax",
            budget: 500000000,
            rate: 26.00
        },
        2: {
            cargo: "Coking Coal",
            qty: 80000,
            origin: "Mozambique",
            loadPort: "Maputo",
            destPort: "Dhamra",
            vessel: "Supramax",
            budget: 280000000,
            rate: 27.00
        },
        3: {
            cargo: "Thermal Coal",
            qty: 55000,
            origin: "Indonesia",
            loadPort: "Tanjung Bara",
            destPort: "Haldia",
            vessel: "Handysize",
            budget: 180000000,
            rate: 29.00
        },
        4: {
            cargo: "Hard Coking Coal",
            qty: 180000,
            origin: "United States",
            loadPort: "Hampton Roads",
            destPort: "Visakhapatnam",
            vessel: "Capesize",
            budget: 750000000,
            rate: 50.00
        }
    };

    const s = scenarios[num];
    if (!s) return;

    if (document.getElementById('cargoType')) document.getElementById('cargoType').value = s.cargo;
    if (document.getElementById('quantityTonnes')) document.getElementById('quantityTonnes').value = s.qty;
    if (document.getElementById('originCountry')) document.getElementById('originCountry').value = s.origin;
    if (document.getElementById('loadingPort')) document.getElementById('loadingPort').value = s.loadPort;
    if (document.getElementById('destinationPort')) document.getElementById('destinationPort').value = s.destPort;
    if (document.getElementById('preferredVessel')) document.getElementById('preferredVessel').value = s.vessel;
    if (document.getElementById('budgetInr')) document.getElementById('budgetInr').value = s.budget;
    if (document.getElementById('expectedFreightRateUsd')) document.getElementById('expectedFreightRateUsd').value = s.rate;

    // Visual feedback
    const banner = document.getElementById('scenarioAlert');
    if (banner) {
        banner.innerHTML = `<strong>Scenario ${num} Loaded:</strong> ${s.cargo} (${s.qty.toLocaleString()} t) via ${s.origin} (${s.loadPort}) ➔ ${s.destPort}. Target Budget: ₹${(s.budget / 10000000).toFixed(1)} Cr. Ready to analyze!`;
        banner.className = 'alert alert-info py-2 px-3 mt-3 mb-0 small d-block';
    }
}

// Copilot Chat Interaction
function sendCopilotQuestion() {
    const input = document.getElementById('copilotInput');
    const container = document.getElementById('chatContainer');
    if (!input || !container) return;

    const query = input.value.trim();
    if (!query) return;

    // Append User Message
    const userDiv = document.createElement('div');
    userDiv.className = 'wf-chat-msg-user';
    userDiv.innerText = query;
    container.appendChild(userDiv);
    input.value = '';
    container.scrollTop = container.scrollHeight;

    // Show typing state
    const botDiv = document.createElement('div');
    botDiv.className = 'wf-chat-msg-bot';
    botDiv.innerHTML = '<span class="spinner-border spinner-border-sm text-teal me-2"></span>Evaluating domain maritime rules...';
    container.appendChild(botDiv);
    container.scrollTop = container.scrollHeight;

    fetch('/api/copilot', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query: query })
    })
    .then(res => res.json())
    .then(data => {
        botDiv.innerHTML = `<div class="d-flex justify-content-between align-items-center mb-1">
            <strong class="text-teal small">NaviSense Copilot</strong>
            <span class="badge bg-light text-muted border" style="font-size: 0.7rem;">${data.category}</span>
        </div>
        <div>${data.answer}</div>`;
        container.scrollTop = container.scrollHeight;
    })
    .catch(err => {
        botDiv.innerHTML = `<span class="text-coral small">Unable to reach copilot intelligence service. Please try again.</span>`;
        container.scrollTop = container.scrollHeight;
    });
}

// Copy Fingerprint to Clipboard
function copyFingerprint(fp) {
    if (!navigator.clipboard) return;
    navigator.clipboard.writeText(fp).then(() => {
        const toast = document.createElement('div');
        toast.className = 'position-fixed bottom-0 end-0 m-3 p-3 bg-white text-teal border rounded shadow-sm small';
        toast.style.zIndex = '9999';
        toast.innerText = '✓ Decision Fingerprint copied: ' + fp;
        document.body.appendChild(toast);
        setTimeout(() => toast.remove(), 2500);
    });
}
