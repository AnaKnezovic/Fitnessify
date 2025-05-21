import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdPerson, MdCheckCircle, MdPending, MdCancel } from "react-icons/md";
import "../style/OdaberiTrenera.css";

const statusBoje = {
  "Prihvaćeno": "#4bcf5a",
  "Čeka": "#f7b731",
  "Odbijeno": "#e05757"
};

const statusIkona = {
  "Prihvaćeno": <MdCheckCircle style={{ color: "#4bcf5a", verticalAlign: "middle" }} />,
  "Čeka": <MdPending style={{ color: "#f7b731", verticalAlign: "middle" }} />,
  "Odbijeno": <MdCancel style={{ color: "#e05757", verticalAlign: "middle" }} />
};

const OdaberiTrenera = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [treneri, setTreneri] = useState([]);
  const [mojiTreneri, setMojiTreneri] = useState([]);

  useEffect(() => {
    axiosInstance.get("/treneri").then(res => setTreneri(res.data));
    axiosInstance.get(`/klijent-trener/moji?klijentId=${korisnikId}`).then(res => setMojiTreneri(res.data));
  }, [korisnikId]);

  const handlePovezi = async (trenerId) => {
    await axiosInstance.post("/klijent-trener", { klijentId: korisnikId, trenerId });
    const res = await axiosInstance.get(`/klijent-trener/moji?klijentId=${korisnikId}`);
    setMojiTreneri(res.data);
  };

  return (
    <div className="trainer-bg">
      <div className="trainer-card">
        <h2 className="trainer-title"><MdPerson style={{marginRight: 7}}/> Odaberi trenera</h2>
        <table className="trainer-table">
          <thead>
            <tr>
              <th>Ime i prezime</th>
              <th>Stručnost</th>
              <th>Godine iskustva</th>
              <th>Akcija</th>
            </tr>
          </thead>
          <tbody>
            {treneri.map(trener => {
              const veza = mojiTreneri.find(v => v.trener.id === trener.id);
              return (
                <tr key={trener.id}>
                  <td>{trener.korisnik?.ime} {trener.korisnik?.prezime}</td>
                  <td>{trener.strucnost}</td>
                  <td>{trener.godineIskustva}</td>
                  <td>
                    {veza ? (
                      <span style={{
                        color: statusBoje[veza.status] || "#23272f",
                        fontWeight: 600,
                        display: "inline-flex",
                        alignItems: "center",
                        gap: 6
                      }}>
                        {statusIkona[veza.status]} {veza.status}
                      </span>
                    ) : (
                      <button className="trainer-btn" onClick={() => handlePovezi(trener.id)}>
                        Poveži se
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default OdaberiTrenera;
