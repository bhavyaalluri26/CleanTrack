const API = '/api';
const STEPS = ['PENDING','ASSIGNED','IN_PROGRESS','RESOLVED'];
const getAuth = () => sessionStorage.getItem('auth');
const me = () => JSON.parse(sessionStorage.getItem('me') || 'null');

async function api(path, method = 'GET', body) {
  const h = {'Content-Type': 'application/json'};
  if (getAuth()) h.Authorization = getAuth();
  const r = await fetch(API + path, {method, headers: h, body: body ? JSON.stringify(body) : undefined});
  const t = await r.text(); let d = null;
  try { d = t ? JSON.parse(t) : null; } catch (e) {}
  if (!r.ok) throw new Error(r.status === 401 ? 'Invalid email or password' : r.status === 403 ? 'Access denied' : (d && d.message) || 'Request failed');
  return d;
}
async function login(email, pw) {
  sessionStorage.setItem('auth', 'Basic ' + btoa(unescape(encodeURIComponent(email.trim().toLowerCase() + ':' + pw))));
  try { const u = await api('/auth/me'); sessionStorage.setItem('me', JSON.stringify(u)); return u; }
  catch (e) { sessionStorage.clear(); throw e; }
}
const homeOf = u => ({USER:'user.html', ADMIN:'admin.html', COLLECTOR:'collector.html'})[u.role];
function guard(role) { const u = me(); if (!u || u.role !== role) { location = 'login.html'; return false; } nav(u); return true; }
function logout() { sessionStorage.clear(); location = 'index.html'; }
function nav(u) {
  document.getElementById('nav').innerHTML = `<nav class="navbar bg-white shadow-sm"><div class="container">
   <a class="navbar-brand" href="index.html"><i class="bi bi-recycle"></i> CleanTrack</a>
   <div><button type="button" class="btn btn-outline-secondary btn-sm theme-toggle me-2" onclick="toggleTheme()"></button><span class="me-3 small text-muted"><i class="bi bi-person-circle"></i> ${esc(u.name)} (${u.role})</span>
   <button class="btn btn-outline-success btn-sm" onclick="logout()">Logout</button></div></div></nav>`;
}
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'})[c]);
const fmt = d => new Date(d).toLocaleString();
const label = s => s.replace('_', ' ');
function badge(s) {
  const c = {PENDING:'warning text-dark', ASSIGNED:'info text-dark', IN_PROGRESS:'primary', RESOLVED:'success'}[s];
  return `<span class="badge bg-${c}">${label(s)}</span>`;
}
function tracker(s) {
  const i = STEPS.indexOf(s);
  return STEPS.map((x, k) => `<span class="step ${k <= i ? 'done' : ''}">${label(x)}</span>`).join('');
}
function toast(msg, type = 'success', ms = 3000) {
  const el = document.createElement('div');
  el.className = `alert alert-${type} position-fixed shadow`; el.style.cssText = 'top:16px;right:16px;z-index:2000;min-width:260px';
  el.textContent = msg; document.body.appendChild(el); setTimeout(() => el.remove(), ms);
}
const empty = (icon, text) => `<div class="text-center text-muted py-5"><i class="bi ${icon} fs-1"></i><p class="mt-2 mb-0">${text}</p></div>`;
