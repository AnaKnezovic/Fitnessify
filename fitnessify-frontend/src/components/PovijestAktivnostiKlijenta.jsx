import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdDirectionsRun } from "react-icons/md";
import "../style/PovijestAktivnosti.css";

const PovijestAktivnostiKlijenta = ({ klijentId }) => {
  const [aktivnosti, setAktivnosti] = useState([]);

  useEffect(() => {
    axiosInstance.get(`/aktivnosti/klijent/${klijentId}`)
      .then(res => setAktivnosti(res.data));
  }, [klijentId]);

  return (
    <div className="povijest-aktivnosti-container">
      <h3 className="povijest-aktivnosti-title">
        <MdDirectionsRun style={{ color: "#1bbfa5", marginRight: 7, fontSize: "1.1em" }} />
        Povijest aktivnosti
      </h3>
      {aktivnosti.length === 0 ? (
        <div className="povijest-aktivnosti-empty">Nema unesenih aktivnosti za ovog klijenta.</div>
      ) : (
        <table className="povijest-aktivnosti-table">
          <thead>
            <tr>
              <th>Datum</th>
              <th>Vrsta aktivnosti</th>
              <th>Trajanje (min)</th>
              <th>Potrošene kalorije</th>
            </tr>
          </thead>
          <tbody>
            {aktivnosti.map((a, idx) => (
              <tr key={idx}>
                <td>{a.id.datumAktivnosti}</td>
                <td>{a.aktivnost.vrstaAktivnosti}</td>
                <td>{a.trajanje}</td>
                <td>{a.potroseneKalorije.toFixed(1)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default PovijestAktivnostiKlijenta;
