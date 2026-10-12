const API_BASE = 'https://cleantrack-qz3p.onrender.com/api';

function getAuthHeaders(email, password) {
  return {
    'Content-Type': 'application/json',
    Authorization: `Basic ${btoa(`${email}:${password}`)}`,
  };
}

async function request(path, options = {}, credentials = null) {
  const headers = {
    'Content-Type': 'application/json',
    ...(credentials
      ? getAuthHeaders(credentials.email, credentials.password)
      : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed (${response.status})`);
  }

  if (response.status === 204) return null;

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  register: (data) =>
    request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  me: (credentials) =>
    request('/auth/me', {}, credentials),

  myComplaints: (credentials) =>
    request('/complaints/my', {}, credentials),

  createComplaint: (data, credentials) =>
    request('/complaints', {
      method: 'POST',
      body: JSON.stringify(data),
    }, credentials),

  submitFeedback: (id, data, credentials) =>
    request(`/complaints/${id}/feedback`, {
      method: 'POST',
      body: JSON.stringify(data),
    }, credentials),

  adminStats: (credentials) =>
    request('/admin/stats', {}, credentials),

  adminComplaints: (credentials, filters = {}) => {
    const params = new URLSearchParams();
    if (filters.status) params.set('status', filters.status);
    if (filters.q) params.set('q', filters.q);

    const query = params.toString();
    return request(`/admin/complaints${query ? `?${query}` : ''}`, {}, credentials);
  },

  updateComplaint: (id, data, credentials) =>
    request(`/admin/complaints/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }, credentials),

  assignCollector: (id, collectorId, credentials) =>
    request(`/admin/complaints/${id}/assign/${collectorId}`, {
      method: 'PUT',
    }, credentials),

  users: (credentials) =>
    request('/admin/users', {}, credentials),

  addCollector: (data, credentials) =>
    request('/admin/collectors', {
      method: 'POST',
      body: JSON.stringify(data),
    }, credentials),

  deleteUser: (id, credentials) =>
    request(`/admin/users/${id}`, {
      method: 'DELETE',
    }, credentials),

  collectorComplaints: (credentials) =>
    request('/collector/complaints', {}, credentials),

  updateStatus: (id, status, credentials) =>
    request(`/collector/complaints/${id}/status?status=${encodeURIComponent(status)}`, {
      method: 'PUT',
    }, credentials),
};
