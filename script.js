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
