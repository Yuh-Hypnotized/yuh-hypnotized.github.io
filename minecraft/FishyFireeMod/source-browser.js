// =============================================
// Source File Browser
// =============================================
(function () {
  var sourceDataEl = document.getElementById('source-data');
  var codeContent = document.getElementById('code-content');
  var codePre = codeContent.parentElement;
  var codeFilename = document.getElementById('code-filename');
  var codeBadge = document.getElementById('code-badge');
  var copyBtn = document.getElementById('copy-btn');
  var treeItems = document.querySelectorAll('.tree-file');

  var sources = {};
  try {
    sources = JSON.parse(sourceDataEl.textContent);
  } catch (e) {
    console.error('Failed to parse source data', e);
  }

  function getLanguage(key) {
    if (key === 'README-md') return { lang: 'markdown', badge: 'Markdown' };
    var k = key.toLowerCase();
    if (k.endsWith('-java')) return { lang: 'java', badge: 'Java' };
    if (k.endsWith('-json') || k.endsWith('-info')) return { lang: 'json', badge: 'JSON' };
    if (k.endsWith('-gradle') || k.endsWith('-properties')) return { lang: 'groovy', badge: 'Gradle' };
    return { lang: 'java', badge: 'Java' };
  }

  function loadFile(key) {
    treeItems.forEach(function(item) { item.classList.remove('active'); });
    var activeItem = document.querySelector('[data-file="' + key + '"]');
    if (activeItem) activeItem.classList.add('active');

    codeFilename.textContent = activeItem ? activeItem.textContent.trim() : key;

    var info = getLanguage(key);
    codeBadge.textContent = info.badge;
    codeContent.className = 'language-' + info.lang;

    copyBtn.textContent = '📋 Copy';
    copyBtn.classList.remove('copied');

    if (sources[key]) {
      codeContent.textContent = sources[key];
    } else {
      codeContent.textContent = '// Source not available.';
    }

    var oldRows = codePre.querySelector('.line-numbers-rows');
    if (oldRows) oldRows.remove();

    if (typeof Prism !== 'undefined') {
      Prism.highlightElement(codeContent);
    }
  }

  copyBtn.addEventListener('click', function() {
    var text = codeContent.textContent;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(function() {
        copyBtn.textContent = '✓ Copied!';
        copyBtn.classList.add('copied');
        setTimeout(function() {
          copyBtn.textContent = '📋 Copy';
          copyBtn.classList.remove('copied');
        }, 2000);
      });
    } else {
      var ta = document.createElement('textarea');
      ta.value = text;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
      copyBtn.textContent = '✓ Copied!';
      copyBtn.classList.add('copied');
      setTimeout(function() {
        copyBtn.textContent = '📋 Copy';
        copyBtn.classList.remove('copied');
      }, 2000);
    }
  });

  treeItems.forEach(function(item) {
    item.addEventListener('click', function() {
      var key = item.getAttribute('data-file');
      if (key) loadFile(key);
    });
  });

  loadFile('README-md');
})();
