// =============================================
// Number Guesser Game
// =============================================
(function () {
  const hint = document.getElementById('guess-hint');
  const input = document.getElementById('guess-input');
  const guessBtn = document.getElementById('guess-btn');
  const startBtn = document.getElementById('guess-start');
  const history = document.getElementById('guess-history');
  const remaining = document.getElementById('guess-remaining');

  let target = null;
  let tries = 0;
  const maxTries = 7;

  function resetGame() {
    target = null;
    tries = 0;
    input.value = '';
    input.disabled = true;
    guessBtn.disabled = true;
    hint.textContent = 'Press start to play';
    hint.className = 'guess-display guess-display-large';
    history.innerHTML = '';
    remaining.innerHTML = '';
    startBtn.textContent = 'Start';
  }

  function startGame() {
    target = Math.floor(Math.random() * 100) + 1;
    tries = 0;
    input.value = '';
    input.disabled = false;
    guessBtn.disabled = false;
    hint.textContent = 'I have a number between 1 and 100...';
    hint.className = 'guess-display guess-display-large';
    history.innerHTML = '';
    remaining.innerHTML = '<span>' + maxTries + ' tries remaining</span>';
    startBtn.textContent = 'Restart';
    input.focus();
  }

  function addChip(value, type) {
    const chip = document.createElement('span');
    chip.className = 'guess-chip ' + type;
    chip.textContent = value;
    history.appendChild(chip);
  }

  function makeGuess() {
    const val = parseInt(input.value, 10);
    if (isNaN(val) || val < 1 || val > 100) {
      hint.textContent = 'Please enter a number between 1 and 100.';
      hint.className = 'guess-display guess-display-large wrong';
      return;
    }

    tries++;
    input.value = '';
    input.focus();

    if (val === target) {
      hint.textContent = '🎉 Correct! The number was ' + target + '. You got it in ' + tries + ' ' + (tries === 1 ? 'try' : 'tries') + '!';
      hint.className = 'guess-display guess-display-large correct';
      addChip(val, 'hit');
      remaining.innerHTML = '';
      input.disabled = true;
      guessBtn.disabled = true;
      startBtn.textContent = 'Play Again';
      return;
    }

    if (tries >= maxTries) {
      hint.textContent = '💀 Game Over! The number was ' + target + '.';
      hint.className = 'guess-display guess-display-large wrong';
      if (val < target) addChip(val + ' ↑', 'low');
      else addChip(val + ' ↓', 'high');
      remaining.innerHTML = '';
      input.disabled = true;
      guessBtn.disabled = true;
      startBtn.textContent = 'Retry';
      return;
    }

    const left = maxTries - tries;
    if (val < target) {
      hint.textContent = '⬆ Too low!';
      hint.className = 'guess-display guess-display-large wrong';
      addChip(val + ' ↑', 'low');
    } else {
      hint.textContent = '⬇ Too high!';
      hint.className = 'guess-display guess-display-large wrong';
      addChip(val + ' ↓', 'high');
    }
    remaining.innerHTML = '<span>' + left + ' ' + (left === 1 ? 'try' : 'tries') + ' remaining</span>';
  }

  guessBtn.addEventListener('click', makeGuess);
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') makeGuess();
  });
  startBtn.addEventListener('click', startGame);

  resetGame();
})();
