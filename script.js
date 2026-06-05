// =============================================
// Dynamic Year
// =============================================
document.getElementById('year').textContent = new Date().getFullYear();

// =============================================
// Smooth Scroll for Anchor Links
// =============================================
document.querySelectorAll('a[href^="#"]').forEach(link => {
  link.addEventListener('click', (event) => {
    const target = document.querySelector(link.getAttribute('href'));
    if (target) {
      event.preventDefault();
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  });
});

// =============================================
// Clickable Category Panels
// =============================================
document.querySelectorAll('.project-panel[data-href]').forEach(panel => {
  panel.addEventListener('click', (e) => {
    // Don't navigate if user clicked an internal link or button
    if (e.target.closest('a')) return;
    window.location.href = panel.getAttribute('data-href');
  });
});
