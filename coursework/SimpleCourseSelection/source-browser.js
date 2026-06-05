// =============================================
// Source File Browser
// =============================================
(function () {
  const sourceDataEl = document.getElementById('source-data');
  const codeContent = document.getElementById('code-content');
  const codePre = codeContent.parentElement;
  const codeFilename = document.getElementById('code-filename');
  const codeBadge = document.getElementById('code-badge');
  const copyBtn = document.getElementById('copy-btn');
  const treeItems = document.querySelectorAll('.tree-file');

  let sources = {};
  try {
    sources = JSON.parse(sourceDataEl.textContent);
  } catch (e) {
    console.error('Failed to parse source data', e);
  }

  function getLanguage(key) {
    if (key === 'README-md') return { lang: 'markdown', badge: 'Markdown' };
    var k = key.toLowerCase();
    if (k.endsWith('-ui')) return { lang: 'markup', badge: 'XML/UI' };
    if (k.endsWith('-h') || k.endsWith('-cpp') || k.startsWith('main-') || k.startsWith('student') || k.startsWith('classes') || k.startsWith('mainwindow')) return { lang: 'cpp', badge: 'C++' };
    if (k.endsWith('-pro') || k.endsWith('-pro-user')) return { lang: 'makefile', badge: 'Makefile' };
    return { lang: 'cpp', badge: 'C++' };
  }

  function loadFile(key) {
    // Update active state in tree
    treeItems.forEach(item => item.classList.remove('active'));
    const activeItem = document.querySelector('[data-file="' + key + '"]');
    if (activeItem) activeItem.classList.add('active');

    // Update filename
    codeFilename.textContent = activeItem
      ? activeItem.textContent.trim()
      : key;

    // Update language badge
    var info = getLanguage(key);
    codeBadge.textContent = info.badge;
    codeContent.className = 'language-' + info.lang;

    // Reset copy button
    copyBtn.textContent = '📋 Copy';
    copyBtn.classList.remove('copied');

    // Update code content
    if (sources[key]) {
      codeContent.textContent = sources[key];
    } else {
      codeContent.textContent = '// Source not available for this file.\n// View the raw file in the project directory.';
    }

    // Remove existing line-numbers rows so Prism regenerates them
    const oldRows = codePre.querySelector('.line-numbers-rows');
    if (oldRows) oldRows.remove();

    // Re-highlight with Prism (triggers line-numbers via 'complete' hook)
    if (typeof Prism !== 'undefined') {
      Prism.highlightElement(codeContent);
    }
  }

  // --- Copy Button ---
  copyBtn.addEventListener('click', () => {
    const text = codeContent.textContent;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(() => {
        copyBtn.textContent = '✓ Copied!';
        copyBtn.classList.add('copied');
        setTimeout(() => {
          copyBtn.textContent = '📋 Copy';
          copyBtn.classList.remove('copied');
        }, 2000);
      });
    } else {
      // Fallback
      const ta = document.createElement('textarea');
      ta.value = text;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
      copyBtn.textContent = '✓ Copied!';
      copyBtn.classList.add('copied');
      setTimeout(() => {
        copyBtn.textContent = '📋 Copy';
        copyBtn.classList.remove('copied');
      }, 2000);
    }
  });

  // Click handler for tree files
  treeItems.forEach(item => {
    item.addEventListener('click', () => {
      const key = item.getAttribute('data-file');
      if (key) loadFile(key);
    });
  });

  // Load README by default
  loadFile('README-md');
})();
