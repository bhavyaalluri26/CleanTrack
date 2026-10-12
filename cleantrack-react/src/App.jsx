import { useState } from 'react';
import { api } from './api';
import './App.css';

const emptyForm = { name: '', email: '', password: '' };

function App() {
  const [page, setPage] = useState('home');
  const [form, setForm] = useState(emptyForm);
  const [credentials, setCredentials] = useState(null);
  const [user, setUser] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  function changePage(nextPage) {
    setPage(nextPage);
    setMessage('');
    setError('');
    setForm(emptyForm);
  }

  function updateField(event) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  async function handleRegister(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    setMessage('');

    try {
      await api.register(form);
      setMessage('Registration successful! You can now log in.');
      setPage('login');
      setForm({ ...emptyForm, email: form.email });
    } catch (err) {
      setError(err.message || 'Registration failed.');
    } finally {
      setBusy(false);
    }
  }

  async function handleLogin(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    setMessage('');

    const loginCredentials = {
      email: form.email.trim(),
      password: form.password,
    };

    try {
      const profile = await api.me(loginCredentials);
      setCredentials(loginCredentials);
      setUser(profile);

      const role = String(profile.role || '').toUpperCase();
      if (role === 'ADMIN') setPage('admin');
      else if (role === 'COLLECTOR') setPage('collector');
      else setPage('user');

      setForm(emptyForm);
    } catch (err) {
      setError(
        err.message.includes('Failed to fetch')
          ? 'Could not reach the API. The server may be waking up or CORS may need configuration.'
          : 'Login failed. Check your email and password, then try again.'
      );
    } finally {
      setBusy(false);
    }
  }

  function logout() {
    setCredentials(null);
    setUser(null);
    changePage('home');
  }

  return (
    <div className="app">
      <header className="navbar">
        <button className="brand" onClick={() => changePage('home')}>
          <span className="brand-icon">✓</span> CleanTrack
        </button>
        <nav>
          {credentials ? (
            <>
              <span className="welcome">Hi, {user?.name || user?.email}</span>
              <button className="outline-button" onClick={logout}>Logout</button>
            </>
          ) : (
            <>
              <button className="nav-link" onClick={() => changePage('home')}>Home</button>
              <button className="nav-link" onClick={() => changePage('login')}>Login</button>
              <button onClick={() => changePage('register')}>Get Started</button>
            </>
          )}
        </nav>
      </header>

      <main>
        {page === 'home' && (
          <section className="hero">
            <p className="tagline">A Cleaner Community Starts Here</p>
            <h1>Report It. Track It. <span>Clean It.</span></h1>
            <p className="hero-copy">
              Report waste concerns in your neighborhood and follow every
              complaint from submission to resolution.
            </p>
            <div className="hero-actions">
              <button onClick={() => changePage('register')}>Create an Account →</button>
              <button className="outline-button" onClick={() => changePage('login')}>Sign In</button>
            </div>
            <div className="feature-grid">
              <article><span>01</span><h3>Report issues</h3><p>Tell the team what needs attention and where.</p></article>
              <article><span>02</span><h3>Track progress</h3><p>Follow your complaint as its status changes.</p></article>
              <article><span>03</span><h3>Cleaner streets</h3><p>Help your community move toward cleaner spaces.</p></article>
            </div>
          </section>
        )}

        {(page === 'login' || page === 'register') && (
          <section className="auth-layout">
            <div className="auth-intro">
              <p className="tagline">Welcome to CleanTrack</p>
              <h1>{page === 'login' ? 'Good to see you again.' : 'Let’s make a difference.'}</h1>
              <p>Work together for cleaner, healthier neighborhoods.</p>
            </div>
            <form className="auth-card" onSubmit={page === 'login' ? handleLogin : handleRegister}>
              <h2>{page === 'login' ? 'Sign in' : 'Create your account'}</h2>
              <p className="muted">
                {page === 'login' ? 'Enter your account details below.' : 'Register as a community member.'}
              </p>

              {page === 'register' && (
                <label>
                  Full name
                  <input name="name" value={form.name} onChange={updateField} required maxLength={100} />
                </label>
              )}
              <label>
                Email address
                <input name="email" type="email" value={form.email} onChange={updateField} required />
              </label>
              <label>
                Password
                <input name="password" type="password" value={form.password} onChange={updateField} required minLength={6} />
              </label>

              {error && <p className="alert error">{error}</p>}
              {message && <p className="alert success">{message}</p>}

              <button className="full-button" disabled={busy}>
                {busy ? 'Please wait…' : page === 'login' ? 'Sign In' : 'Create Account'}
              </button>
              <p className="switch-auth">
                {page === 'login' ? 'New to CleanTrack?' : 'Already registered?'}{' '}
                <button type="button" className="text-button" onClick={() => changePage(page === 'login' ? 'register' : 'login')}>
                  {page === 'login' ? 'Create an account' : 'Sign in'}
                </button>
              </p>
            </form>
          </section>
        )}

        {credentials && ['user', 'collector', 'admin'].includes(page) && (
          <section className="dashboard-placeholder">
            <p className="tagline">{user?.role || 'USER'} DASHBOARD</p>
            <h1>Welcome, {user?.name || user?.email}!</h1>
            <p>Your account is connected. We'll add your role-specific tools next.</p>
            <div className="role-card">
              <h3>{page === 'admin' ? 'Administration' : page === 'collector' ? 'Collection tasks' : 'My complaints'}</h3>
              <p>You're signed in successfully. Your dashboard is the next step.</p>
              <button onClick={logout}>Sign out</button>
            </div>
          </section>
        )}
      </main>

      <footer><p>© {new Date().getFullYear()} CleanTrack · Cleaner communities, together.</p></footer>
    </div>
  );
}

export default App;
