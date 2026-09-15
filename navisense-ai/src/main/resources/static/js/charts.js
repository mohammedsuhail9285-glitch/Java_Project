/**
 * NAVISENSE AI - Scandinavian Maritime Watchfloor Chart Engine
 * Chart.js 4.x Implementation
 */

// Global Chart theme defaults for clean Watchfloor aesthetic
if (typeof Chart !== 'undefined') {
    Chart.defaults.color = '#788c90';
    Chart.defaults.borderColor = '#edf2f0';
    Chart.defaults.font.family = "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif";
    Chart.defaults.font.size = 11;
}

/**
 * Renders the 14-Day Freight Forecast Bar Chart matching Lovable Watchfloor UI
 * Mint bars for first 10 days, deep teal bars for peak projection days
 */
function renderWatchfloorForecastBarChart(canvasId, points) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    // Default 14-day data points if not provided or empty
    const defaultLabels = ['SEP 12', '', 'SEP 14', '', 'SEP 16', '', 'SEP 18', '', 'SEP 20', '', 'SEP 22', '', 'SEP 24', ''];
    const defaultRates = [24.1, 24.8, 24.5, 25.2, 25.0, 25.5, 25.4, 26.2, 26.0, 26.8, 26.5, 28.2, 28.0, 28.9];

    let labels = defaultLabels;
    let rates = defaultRates;

    if (points && points.length > 0) {
        // Take up to 14 points
        const subset = points.slice(0, 14);
        labels = subset.map((p, idx) => {
            const dateStr = p.forecastDate || p.date || ('Day ' + (idx + 1));
            // Show every alternate label to prevent clutter
            if (idx % 2 === 0) {
                const parts = dateStr.split('-');
                if (parts.length >= 3) {
                    const months = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
                    const mIdx = parseInt(parts[1], 10) - 1;
                    return (months[mIdx] || 'DAY') + ' ' + parseInt(parts[2], 10);
                }
                return dateStr.substring(5);
            }
            return '';
        });
        rates = subset.map(p => p.predictedRateUsd || p.rateUsd || 25.0);
    }

    // Color palette: First (length - 4) are soft mint, last 4 are deep teal
    const backgroundColors = rates.map((_, i) => {
        return (i >= rates.length - 3) ? '#0d7e77' : '#c2e2dc';
    });

    const hoverColors = rates.map((_, i) => {
        return (i >= rates.length - 3) ? '#0a6660' : '#a8d5cc';
    });

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                data: rates,
                backgroundColor: backgroundColors,
                hoverBackgroundColor: hoverColors,
                borderRadius: { topLeft: 4, topRight: 4, bottomLeft: 0, bottomRight: 0 },
                borderSkipped: false,
                barPercentage: 0.82,
                categoryPercentage: 0.9
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#132228',
                    titleColor: '#ffffff',
                    bodyColor: '#daf0ec',
                    padding: 8,
                    cornerRadius: 6,
                    callbacks: {
                        label: function(context) {
                            return ' Projected: $' + context.raw.toFixed(2) + ' /t';
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    border: { display: false },
                    ticks: {
                        color: '#8f9ea1',
                        font: { size: 10, weight: '600' },
                        maxRotation: 0,
                        autoSkip: false
                    }
                },
                y: {
                    display: false,
                    min: Math.min(...rates) * 0.75,
                    max: Math.max(...rates) * 1.08,
                    grid: { display: false },
                    border: { display: false }
                }
            }
        }
    });
}

/**
 * Standard Line Forecast Chart for deep analytics pages
 */
function renderForecastChart(canvasId, historicalPoints, forecastPoints) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !historicalPoints || !forecastPoints) return;

    const histLabels = historicalPoints.map(p => p.date);
    const histData = historicalPoints.map(p => p.rateUsd);

    const fcastLabels = forecastPoints.map(p => p.forecastDate);
    const fcastPred = forecastPoints.map(p => p.predictedRateUsd);
    const fcastLower = forecastPoints.map(p => p.lowerBoundUsd);
    const fcastUpper = forecastPoints.map(p => p.upperBoundUsd);

    const allLabels = [...histLabels, ...fcastLabels];
    const histSeries = [...histData, ...new Array(fcastLabels.length).fill(null)];

    const lastHistVal = histData[histData.length - 1];
    const fcastSeries = [...new Array(histLabels.length - 1).fill(null), lastHistVal, ...fcastPred];
    const upperSeries = [...new Array(histLabels.length - 1).fill(null), lastHistVal, ...fcastUpper];
    const lowerSeries = [...new Array(histLabels.length - 1).fill(null), lastHistVal, ...fcastLower];

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: allLabels,
            datasets: [
                {
                    label: 'Historical Freight ($/t)',
                    data: histSeries,
                    borderColor: '#788c90',
                    backgroundColor: 'rgba(120, 140, 144, 0.08)',
                    borderWidth: 2,
                    pointRadius: 1,
                    tension: 0.2
                },
                {
                    label: 'Ensemble Forecast ($/t)',
                    data: fcastSeries,
                    borderColor: '#0d7e77',
                    borderDash: [4, 4],
                    borderWidth: 2.5,
                    pointRadius: 3,
                    pointBackgroundColor: '#0d7e77',
                    tension: 0.2
                },
                {
                    label: 'Upper Confidence (95%)',
                    data: upperSeries,
                    borderColor: 'rgba(13, 126, 119, 0.25)',
                    borderWidth: 1,
                    pointRadius: 0,
                    fill: '+1',
                    backgroundColor: 'rgba(13, 126, 119, 0.08)'
                },
                {
                    label: 'Lower Confidence (95%)',
                    data: lowerSeries,
                    borderColor: 'rgba(13, 126, 119, 0.25)',
                    borderWidth: 1,
                    pointRadius: 0,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            scales: {
                y: {
                    title: { display: true, text: 'USD / Tonne', color: '#0d7e77', font: { weight: '600' } },
                    grid: { color: '#edf2f0' }
                },
                x: {
                    grid: { display: false },
                    ticks: { maxTicksLimit: 12 }
                }
            }
        }
    });
}

function renderVesselChart(canvasId, rankings) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !rankings) return;

    const labels = rankings.map(r => r.vesselName + (r.compatible ? '' : ' (Incompatible)'));
    const scores = rankings.map(r => r.score);
    const colors = rankings.map(r => r.compatible ? '#0d7e77' : '#e05d44');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Suitability Score (0-100)',
                data: scores,
                backgroundColor: colors,
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { max: 100, min: 0, grid: { color: '#edf2f0' } },
                x: { grid: { display: false } }
            }
        }
    });
}

function renderContractChart(canvasId, contracts) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !contracts) return;

    const labels = contracts.map(c => c.strategyName);
    const costsCr = contracts.map(c => c.expectedCostInr ? (c.expectedCostInr / 10000000) : 0);
    const colors = contracts.map(c => c.recommended ? '#0d7e77' : '#c2e2dc');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Expected Logistics Outlay (₹ Crores)',
                data: costsCr,
                backgroundColor: colors,
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    title: { display: true, text: '₹ Crores', color: '#0d7e77', font: { weight: '600' } },
                    grid: { color: '#edf2f0' }
                },
                x: { grid: { display: false } }
            }
        }
    });
}

function renderBacktestChart(canvasId, records) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !records) return;

    const labels = records.map(r => r.decisionDate);
    let cumulative = 0;
    const cumSavingsCr = records.map(r => {
        cumulative += (r.savingsInr / 10000000);
        return Math.round(cumulative * 100) / 100;
    });

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Cumulative Simulated Savings (₹ Crores)',
                data: cumSavingsCr,
                borderColor: '#0d7e77',
                backgroundColor: 'rgba(13, 126, 119, 0.1)',
                borderWidth: 2.5,
                fill: true,
                tension: 0.2,
                pointRadius: 3,
                pointBackgroundColor: '#0d7e77'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    title: { display: true, text: 'Cumulative Savings (₹ Cr)', color: '#0d7e77', font: { weight: '600' } },
                    grid: { color: '#edf2f0' }
                },
                x: { grid: { display: false } }
            }
        }
    });
}
