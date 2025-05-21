import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../axiosInstance";
import { MdVisibility, MdVisibilityOff } from "react-icons/md";
import "../style/LoginPage.css";

const LoginPage = () => {
  const [form, setForm] = useState({ email: "", lozinka: "" });
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await axiosInstance.post("/auth/login", form);
      localStorage.setItem("token", res.data.token);
      if (res.data.user && res.data.user.role) {
        localStorage.setItem("korisnikId", res.data.user.id);
        localStorage.setItem("role", res.data.user.role);
      }
      navigate("/home");
    } catch {
      setError("Neispravni podatci! Pokušajte ponovno.");
    }
  };

  return (
    <div className="login-bg">
      <div className="login-container">
        <div className="login-logo">
          {/* SVG logo */}
          <svg width="50" height="50" viewBox="0 0 24 24" fill="#41c9c3" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 12L12 4L21 12" stroke="#41c9c3" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <rect x="7" y="13" width="10" height="7" rx="2" fill="#fff" stroke="#41c9c3" strokeWidth="2"/>
            <rect x="9" y="16" width="2" height="2" rx="1" fill="#41c9c3"/>
            <rect x="13" y="16" width="2" height="2" rx="1" fill="#41c9c3"/>
          </svg>
        </div>
        <h2 className="login-title">FITNESSIFY</h2>
        <form onSubmit={handleSubmit}>
          <div className="input-wrapper">
            <input
              className="login-input"
              name="email"
              type="email"
              placeholder="Email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="password-wrapper">
            <input
              className="login-input"
              name="lozinka"
              type={showPassword ? "text" : "password"}
              placeholder="Lozinka"
              value={form.lozinka}
              onChange={handleChange}
              required
              autoComplete="current-password"
            />
            <span
              className="eye-icon"
              onClick={() => setShowPassword((v) => !v)}
              title={showPassword ? "Sakrij lozinku" : "Prikaži lozinku"}
            >
              {showPassword ? <MdVisibilityOff size={22} /> : <MdVisibility size={22} />}
            </span>
          </div>
          <button className="login-btn input-wrapper" type="submit">Prijavi se</button>
          {error && <div className="login-error">{error}</div>}
        </form>

        <div className="login-register-link">
          Nemate račun? <a href="/register">Registrirajte se</a>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
