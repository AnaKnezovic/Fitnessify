import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdAssignmentTurnedIn, MdPersonAddAlt1, MdAddCircleOutline } from "react-icons/md";
import "../style/DodijeliPlan.css";

const DodijeliPlan = () => {
  const trenerId = localStorage.getItem("korisnikId");
  const [planovi, setPlanovi] = useState([]);
  const [klijenti, setKlijenti] = useState([]);
  const [planId, setPlanId] = useState("");
  const [klijentId, setKlijentId] = useState("");
  const [poruka, setPoruka] = useState("");

  // Za kreiranje plana
  const [nazivPlana, setNazivPlana] = useState("");
  const [opisPlana, setOpisPlana] = useState("");
  const [porukaKreiranje, setPorukaKreiranje] = useState("");

  useEffect(() => {
    axiosInstance.get(`/planovi/${trenerId}`).then(res => setPlanovi(res.data));
    axiosInstance.get(`/klijent-trener/moji-klijenti?trenerId=${trenerId}`)
      .then(res => setKlijenti(res.data.map(v => v.klijent)));
  }, [trenerId]);

  const handleDodijeli = async () => {
    setPoruka("");
    if (!planId || !klijentId) {
      setPoruka("Odaberite klijenta i plan!");
      return;
    }
    try {
      await axiosInstance.post("/planovi/dodijeli", { klijentId, planId });
      setPoruka("Plan uspješno dodijeljen! ✅");
      setPlanId("");
      setKlijentId("");
    } catch (e) {
      setPoruka("Greška pri dodjeli plana! (Plan možda već postoji za klijenta)");
    }
  };

  const handleKreirajPlan = async () => {
    setPorukaKreiranje("");
    if (!nazivPlana || !opisPlana) {
      setPorukaKreiranje("Unesite naziv i opis plana!");
      return;
    }
    try {
      await axiosInstance.post("/planovi", {
        nazivPlana,
        opis: opisPlana,
        trenerId
      });
      setPorukaKreiranje("Plan uspješno kreiran! ✅");
      setNazivPlana("");
      setOpisPlana("");
      // Osvježi popis planova
      const res = await axiosInstance.get(`/planovi/${trenerId}`);
      setPlanovi(res.data);
    } catch (e) {
      setPorukaKreiranje("Greška pri kreiranju plana!");
    }
  };

  return (
    <div className="assign-plan-bg">
      <div className="assign-plan-card">
        <h2 className="assign-plan-title">
          <MdAssignmentTurnedIn style={{marginRight: 7}} />
          Dodijeli plan klijentu
        </h2>
        {/* Forma za kreiranje plana */}
        <div className="assign-plan-form" style={{marginBottom: 34, borderBottom: "1.5px solid #e6e6e6", paddingBottom: 22}}>
          <h4 style={{color: "#1bbfa5", marginBottom: 12, display: "flex", alignItems: "center", gap: 7}}>
            <MdAddCircleOutline /> Kreiraj novi plan
          </h4>
          <input
            type="text"
            placeholder="Naziv plana"
            value={nazivPlana}
            onChange={e => setNazivPlana(e.target.value)}
            className="assign-plan-input"
          />
          <textarea
            placeholder="Opis plana"
            value={opisPlana}
            onChange={e => setOpisPlana(e.target.value)}
            className="assign-plan-input"
            style={{minHeight: 48, resize: "vertical"}}
          />
          <button className="assign-plan-btn" onClick={handleKreirajPlan} style={{marginTop: 6}}>
            <MdAddCircleOutline style={{marginRight: 5, fontSize: "1.2em"}}/>
            Kreiraj plan
          </button>
          {porukaKreiranje && <div className="assign-plan-msg">{porukaKreiranje}</div>}
        </div>
        {/* Forma za dodjelu plana */}
        <div className="assign-plan-form">
          <div>
            <label>Klijent:</label>
            <select value={klijentId} onChange={e => setKlijentId(e.target.value)}>
              <option value="">Odaberi klijenta</option>
              {klijenti.map(k => (
                <option key={k.id} value={k.id}>
                  {k.korisnik?.ime} {k.korisnik?.prezime}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label>Plan:</label>
            <select value={planId} onChange={e => setPlanId(e.target.value)}>
              <option value="">Odaberi plan</option>
              {planovi.map(p => (
                <option key={p.id} value={p.id}>{p.nazivPlana}</option>
              ))}
            </select>
          </div>
          <button className="assign-plan-btn" onClick={handleDodijeli}>
            <MdPersonAddAlt1 style={{marginRight: 5, fontSize: "1.2em"}}/>
            Dodijeli plan
          </button>
        </div>
        {poruka && <div className="assign-plan-msg">{poruka}</div>}
      </div>
    </div>
  );
};

export default DodijeliPlan;
