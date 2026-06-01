// =============================================
// Reaction Time Game
// =============================================
(function () {
  const box = document.getElementById('reaction-box');
  const label = document.getElementById('reaction-label');
  const bestEl = document.getElementById('reaction-best');
  const lastEl = document.getElementById('reaction-last');
  const attemptsEl = document.getElementById('reaction-attempts');

  let state = 'idle'; // idle | waiting | ready | done
  let timer = null;
  let waitStart = 0;
  let best = Infinity;
  let last = null;
  let attempts = 0;

  function reset() {
    state = 'idle';
    box.className = 'reaction-box reaction-box-large';
    label.textContent = 'Click to start';
    if (timer) { clearTimeout(timer); timer = null; }
  }

  function startWait() {
    state = 'waiting';
    box.className = 'reaction-box reaction-box-large waiting';
    label.textContent = 'Wait for green...';
    const delay = 1500 + Math.random() * 2500;
    timer = setTimeout(() => {
      state = 'ready';
      box.className = 'reaction-box reaction-box-large ready';
      label.textContent = 'CLICK NOW!';
      waitStart = performance.now();
    }, delay);
  }

  box.addEventListener('click', () => {
    if (state === 'idle') {
      startWait();
    } else if (state === 'waiting') {
      clearTimeout(timer);
      timer = null;
      state = 'done';
      box.className = 'reaction-box reaction-box-large too-early';
      label.textContent = 'Too early! Click to retry';
      setTimeout(reset, 1200);
    } else if (state === 'ready') {
      const reaction = Math.round(performance.now() - waitStart);
      last = reaction;
      lastEl.textContent = reaction;
      attempts++;
      attemptsEl.textContent = attempts;
      if (reaction < best) {
        best = reaction;
        bestEl.textContent = reaction;
      }
      state = 'done';
      box.className = 'reaction-box reaction-box-large ready';
      label.textContent = reaction + ' ms — Click to retry';
      setTimeout(() => { if (state === 'done') reset(); }, 2000);
    }
  });
})();
