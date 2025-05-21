import React, { useState } from "react";
import axiosInstance from "../axiosInstance";
import { useNavigate } from "react-router-dom";
import { MdFitnessCenter, MdPerson, MdVisibility, MdVisibilityOff } from "react-icons/md";
import "../style/LoginPage.css"; 

const PASSWORD_HINT = "Lozinka mora imati barem 8 znakova, jedno veliko i malo slovo, broj i poseban znak.";
const EMAIL_HINT = "Unesite ispravan email (npr. korisnik@mail.com).";

function validatePassword(password) {
  if (!password || password.length < 8)
    return "Lozinka mora imati minimalno 8 znakova.";
  if (!/[A-Z]/.test(password))
    return "Lozinka mora sadržavati barem jedno veliko slovo.";
  if (!/[a-z]/.test(password))
    return "Lozinka mora sadržavati barem jedno malo slovo.";
  if (!/[0-9]/.test(password))
    return "Lozinka mora sadržavati barem jedan broj.";
  if (!/[!@#$%^&*()_+\-=[\]{};':\"\\|,.<>/?]/.test(password))
    return "Lozinka mora sadržavati barem jedan poseban znak.";
  return null;
}

function validateEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return "Unesite ispravan email.";
  }
  return null;
}

const RegistrationForm = () => {
  const [step, setStep] = useState(1);
  const [role, setRole] = useState("");
  const [form, setForm] = useState({
    ime: "",
    prezime: "",
    email: "",
    lozinka: "",
    spol: "",
    dob: "",
    visina: "",
    tezina: "",
    cilj: "",
    strucnost: "",
    godineIskustva: ""
  });
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [generalError, setGeneralError] = useState("");
  const navigate = useNavigate();
  const [passwordFocused, setPasswordFocused] = useState(false);
  const [registrationSuccess, setRegistrationSuccess] = useState(false);

  const handleRoleSelect = (selectedRole) => {
    setRole(selectedRole);
    setStep(2);
    setErrors({});
    setGeneralError("");
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    setGeneralError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let newErrors = {};

    const emailError = validateEmail(form.email);
    if (emailError) newErrors.email = emailError;

    const passError = validatePassword(form.lozinka);
    if (passError) newErrors.lozinka = passError;

    setErrors(newErrors);
    setGeneralError("");

    if (Object.keys(newErrors).length > 0) return;

    try {
      const body = { ...form, role };
      await axiosInstance.post("/auth/register", body);
      setRegistrationSuccess(true);
      //navigate("/login");
    } catch (err) {
      setGeneralError(
        err.response?.data ||
        "Došlo je do greške pri registraciji. Provjerite podatke."
      );
    }
  };

  return (
    <div className="login-bg">
      <div className="login-container">
        {registrationSuccess ? (
          <div className="registration-success">
            <h2 style={{ marginBottom: 16 }}>Uspješno ste registrirani!</h2>
            <p style={{ marginBottom: 30 }}>Sada se možete prijaviti koristeći svoj email i lozinku.</p>
            <button className="login-btn" onClick={() => navigate("/login")}>
              Idi na prijavu
            </button>
          </div>
        ) : step === 1 ? (
          <>
            <h2 className="login-title">Registracija</h2>
            <h4 style={{ marginBottom: 24, color: "#253647" }}>
              Odaberi svoj profil
            </h4>
            <div className="role-choices">
              <div
                className="role-card"
                onClick={() => handleRoleSelect("KLIJENT")}
              >
                <MdPerson size={46} color="#253647" />
                <span>Klijent</span>
              </div>
              <div
                className="role-card"
                onClick={() => handleRoleSelect("TRENER")}
              >
                <MdFitnessCenter size={46} color="#253647" />
                <span>Trener</span>
              </div>
            </div>
          </>
        ) : (
          <form onSubmit={handleSubmit}>
            <h2 className="login-title">
              Registracija – {role === "KLIJENT" ? "Klijent" : "Trener"}
            </h2>
            <div className="input-wrapper">
              <input
                className="login-input"
                name="ime"
                placeholder="Ime"
                onChange={handleChange}
                required
              />
            </div>
            <div className="input-wrapper">
              <input
                className="login-input"
                name="prezime"
                placeholder="Prezime"
                onChange={handleChange}
                required
              />
            </div>
            <div className="input-wrapper">
              <input
                className="login-input"
                name="email"
                type="email"
                placeholder="korisnik@mail.com"
                value={form.email}
                onChange={handleChange}
                required
              />
              <div className={errors.email ? "field-error" : "field-hint"}>
                {errors.email ? errors.email : EMAIL_HINT}
              </div>
            </div>
            <div className="password-wrapper">
              <div className="password-inner">
                <input
                  className={`login-input${errors.lozinka ? " error" : ""}`}
                  name="lozinka"
                  type={showPassword ? "text" : "password"}
                  placeholder="Lozinka"
                  value={form.lozinka}
                  onChange={handleChange}
                  onFocus={() => setPasswordFocused(true)}
                  onBlur={() => setPasswordFocused(false)}
                  required
                  autoComplete="new-password"
                  aria-describedby={errors.lozinka ? "lozinka-error" : passwordFocused ? "lozinka-hint" : undefined}
                />
                <span
                  className="eye-icon"
                  onClick={() => setShowPassword((v) => !v)}
                  tabIndex={0}
                  onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') setShowPassword(v => !v) }}
                  title={showPassword ? "Sakrij lozinku" : "Prikaži lozinku"}
                  style={{ cursor: "pointer" }}
                >
                  {showPassword ? <MdVisibilityOff size={22} /> : <MdVisibility size={22} />}
                </span>
              </div>
              {passwordFocused && !errors.lozinka && (
                <div className="popup-hint" id="lozinka-hint">{PASSWORD_HINT}</div>
              )}
              {errors.lozinka && (
                <div className="field-error" id="lozinka-error">{errors.lozinka}</div>
              )}
            </div>
            <div className="input-wrapper" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
              <span style={{ minWidth: 44, fontWeight: 500 }}>Spol:</span>
              <label className="gender-radio-label">
                <input
                  type="radio"
                  name="spol"
                  value="Zensko"
                  checked={form.spol === "Zensko"}
                  onChange={handleChange}
                  required
                />
                Žensko
              </label>
              <label className="gender-radio-label">
                <input
                  type="radio"
                  name="spol"
                  value="Musko"
                  checked={form.spol === "Musko"}
                  onChange={handleChange}
                  required
                />
                Muško
              </label>
            </div>
            <div className="input-wrapper">
              <input
                className="login-input"
                name="dob"
                type="number"
                placeholder="Dob"
                onChange={handleChange}
              />
            </div>
            {role === "KLIJENT" && (
              <>
                <div className="input-wrapper">
                  <input
                    className="login-input"
                    name="visina"
                    type="number"
                    placeholder="Visina (cm)"
                    onChange={handleChange}
                  />
                </div>
                <div className="input-wrapper">
                  <input
                    className="login-input"
                    name="tezina"
                    type="number"
                    placeholder="Težina (kg)"
                    onChange={handleChange}
                  />
                </div>
                <div className="input-wrapper">
                  <input
                    className="login-input"
                    name="cilj"
                    placeholder="Cilj"
                    onChange={handleChange}
                  />
                </div>
              </>
            )}
            {role === "TRENER" && (
              <>
                <div className="input-wrapper">
                  <input
                    className="login-input"
                    name="strucnost"
                    placeholder="Stručnost"
                    onChange={handleChange}
                  />
                </div>
                <div className="input-wrapper">
                  <input
                    className="login-input"
                    name="godineIskustva"
                    type="number"
                    placeholder="Godine iskustva"
                    onChange={handleChange}
                  />
                </div>
              </>
            )}
            <button className="login-btn" type="submit">
              Registriraj se
            </button>
            <button
              type="button"
              style={{
                marginLeft: 10,
                background: "#e0e8ef",
                color: "#253647",
                border: "none",
                borderRadius: 10,
                padding: "0.7em 1.4em",
                fontWeight: "bold",
                cursor: "pointer",
                marginTop: "0.5em"
              }}
              onClick={() => setStep(1)}
            >
              Nazad
            </button>
            {generalError && (
              <div className="login-error" style={{ marginTop: 12 }}>
                {generalError}
              </div>
            )}
          </form>
        )}
        {!registrationSuccess && (
          <div className="login-register-link" style={{ marginTop: 22 }}>
            Već imate račun? <a href="/login">Prijavite se</a>
          </div>
        )}
      </div>
    </div>
  );

};

export default RegistrationForm;
