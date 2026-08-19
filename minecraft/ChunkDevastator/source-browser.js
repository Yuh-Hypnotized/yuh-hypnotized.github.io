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
  const mdRender = document.getElementById('md-render');
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
    if (k.endsWith('-md')) return { lang: 'markdown', badge: 'Markdown' };
    if (k.endsWith('-java')) return { lang: 'java', badge: 'Java' };
    if (k.endsWith('-json') || k.endsWith('-info')) return { lang: 'json', badge: 'JSON' };
    if (k.endsWith('-gradle') || k.endsWith('-properties')) return { lang: 'groovy', badge: 'Gradle' };
    if (k.endsWith('-txt')) return { lang: 'plain', badge: 'Text' };
    return { lang: 'java', badge: 'Java' };
  }

  // Render relative links/images inside README against the sourcecode/ folder
  function fixRelativePaths(html) {
    return html.replace(/(src|href)="(?!(?:https?:|#|mailto:|data:))/g, '$1="sourcecode/');
  }

  function showMarkdown(text) {
    if (typeof marked !== 'undefined') {
      mdRender.innerHTML = fixRelativePaths(marked.parse(text));
      codePre.hidden = true;
      mdRender.hidden = false;
    } else {
      // marked failed to load — fall back to plain code view
      codePre.hidden = false;
      mdRender.hidden = true;
    }
  }

  function showCode(key) {
    mdRender.hidden = true;
    codePre.hidden = false;
    var info = getLanguage(key);
    codeContent.className = 'language-' + info.lang;

    // Remove existing line-numbers rows so Prism regenerates them
    var oldRows = codePre.querySelector('.line-numbers-rows');
    if (oldRows) oldRows.remove();

    if (typeof Prism !== 'undefined') {
      Prism.highlightElement(codeContent);
    }
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

    // Reset copy button
    copyBtn.textContent = '📋 Copy';
    copyBtn.classList.remove('copied');

    // Update code content (always keep raw source for copy)
    if (sources[key]) {
      codeContent.textContent = sources[key];
    } else {
      codeContent.textContent = '// Source not available for this file.\n// View the raw file in the project directory.';
    }

    // Markdown files render as formatted README; everything else is code
    if (info.lang === 'markdown') {
      showMarkdown(codeContent.textContent);
    } else {
      showCode(key);
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

  // Load README by default (rendered)
  loadFile('README-md');
})();
