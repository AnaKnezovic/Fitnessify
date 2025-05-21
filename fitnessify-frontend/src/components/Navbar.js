import React from "react";
import { Link } from "react-router-dom";
import "../style/Navbar.css"; 
import { logout } from "../utils";

function Navbar() {
  const role = localStorage.getItem("role");

  return (
    <nav className="navbar">
      <div className="navbar-brand">FITNESSIFY</div>
      <ul className="navbar-links">
        <li>
          <Link to="/home" className="navbar-link">Početna</Link>
        </li>
        {role === "KLIJENT" && (
          <>
            <li><Link to="/treneri" className="navbar-link">Odaberi trenera</Link></li>
            <li><Link to="/moji-planovi" className="navbar-link">Moji planovi</Link></li>
          </>
        )}
        {role === "TRENER" && (
          <>
            <li><Link to="/moji-klijenti" className="navbar-link">Moji klijenti</Link></li>
            <li><Link to="/dodijeli-plan" className="navbar-link">Dodijeli plan</Link></li>
          </>
        )}
        <li>
          <Link to="/profile" className="navbar-link">Moj Profil</Link>
        </li>
        <li>
          <Link to="/prehrambeni-dnevnik" className="navbar-link">Prehrambeni dnevnik</Link>
        </li>
        <li>
          <Link to="/moje-aktivnosti" className="navbar-link">Moje aktivnosti</Link>
        </li>
        <li>
          <Link to="/aktivnosti" className="navbar-link">Aktivnosti</Link>
        </li>
        <li>
          <Link to="/namirnice" className="navbar-link">Namirnice</Link>
        </li>
        <li>
          <Link to="/faq" className="navbar-link">FAQ</Link>
        </li>
        <li>
          <button className="navbar-logout" onClick={logout}>
            Odjavi se
          </button>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;
