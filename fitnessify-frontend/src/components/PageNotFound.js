import React from "react";
import { useNavigate } from "react-router-dom";
import { MdErrorOutline } from "react-icons/md";
import "../style/PageNotFound.css";

const PageNotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="notfound-bg">
      <div className="notfound-card">
        <MdErrorOutline style={{ color: "#d94444", fontSize: "2.5em", marginBottom: 10 }} />
        <h2>404: Stranica nije pronađena</h2>
        <p>Stranica koju tražite ne postoji ili je uklonjena.</p>
        <button className="notfound-btn" onClick={() => navigate("/home")}>
          Vrati se na početnu
        </button>
      </div>
    </div>
  );
};

export default PageNotFound;
