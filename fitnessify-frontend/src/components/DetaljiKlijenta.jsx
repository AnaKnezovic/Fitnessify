import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import MjesecniGraf from "./MjesecniGraf";
import PovijestAktivnostiKlijenta from "./PovijestAktivnostiKlijenta";
import { MdPerson, MdEmail, MdHeight, MdMonitorWeight } from "react-icons/md";
import "../style/DetaljiKlijenta.css";

const DetaljiKlijenta = ({ klijentId }) => {
  const [klijent, setKlijent] = useState(null);

  useEffect(() => {
    axiosInstance.get(`/klijenti/${klijentId}`).then(res => setKlijent(res.data));
  }, [klijentId]);

  return (
    <div className="detalji-klijenta-bg">
      <div className="detalji-klijenta-card">
        {klijent && (
          <div className="detalji-klijent-info">
            <div className="detalji-klijent-avatar">
              <MdPerson style={{ fontSize: "2.4em", color: "#1bbfa5" }} />
            </div>
            <div>
              <h2 className="detalji-klijent-ime">
                {klijent.korisnik?.ime} {klijent.korisnik?.prezime}
              </h2>
              <div className="detalji-klijent-atribut">
                <MdEmail style={{ color: "#1bbfa5", marginRight: 6 }} />
                {klijent.korisnik?.email}
              </div>
              <div className="detalji-klijent-atribut">
                <MdHeight style={{ color: "#1bbfa5", marginRight: 6 }} />
                Visina: <b>{klijent.visina} cm</b>
              </div>
              <div className="detalji-klijent-atribut">
                <MdMonitorWeight style={{ color: "#1bbfa5", marginRight: 6 }} />
                Težina: <b>{klijent.tezina} kg</b>
              </div>
            </div>
          </div>
        )}
      </div>
      <div className="detalji-klijent-section">
        <MjesecniGraf korisnikId={klijentId} />
      </div>
      <div className="detalji-klijent-section">
        <PovijestAktivnostiKlijenta klijentId={klijentId} />
      </div>
    </div>
  );
};

export default DetaljiKlijenta;
