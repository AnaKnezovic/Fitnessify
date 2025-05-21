export function isLoggedIn() {
  const token = localStorage.getItem("token");
  if (!token) return false;
  return !isTokenExpired(token);
}

export const logout = () => {
  localStorage.clear();
  window.location.href = "/login";
};

export function parseJwt(token) {
  if (!token) return null;
  const base64Url = token.split('.')[1];
  if (!base64Url) return null;
  try {
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
}

export function isTokenExpired(token) {
  const payload = parseJwt(token);
  if (!payload || !payload.exp) return true;
  return payload.exp * 1000 < Date.now();
}