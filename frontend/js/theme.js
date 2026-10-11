(function () {
  var KEY = 'cleantrack-theme';
  function get() { try { return localStorage.getItem(KEY); } catch (e) { return null; } }
  function apply(t) {
    document.documentElement.setAttribute('data-bs-theme', t);
    document.querySelectorAll('.theme-toggle').forEach(function (b) {
      b.innerHTML = t === 'dark' ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-stars-fill"></i>';
      b.title = t === 'dark' ? 'Switch to light theme' : 'Switch to dark theme';
      b.setAttribute('aria-label', b.title);
    });
  }
  window.toggleTheme = function () {
    var t = document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    try { localStorage.setItem(KEY, t); } catch (e) {}
    apply(t);
  };
  apply(get() === 'dark' ? 'dark' : 'light');
  // Buttons may be rendered later (dashboard navbars), so re-sync icons when the DOM changes
  document.addEventListener('DOMContentLoaded', function () {
    apply(document.documentElement.getAttribute('data-bs-theme'));
    new MutationObserver(function () {
      document.querySelectorAll('.theme-toggle:empty').forEach(function () { apply(document.documentElement.getAttribute('data-bs-theme')); });
    }).observe(document.body, {childList: true, subtree: true});
  });
})();
