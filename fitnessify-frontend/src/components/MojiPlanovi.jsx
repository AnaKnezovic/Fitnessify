import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdDescription, MdCheckCircle, MdHourglassBottom } from "react-icons/md";
import "../style/MojiPlanovi.css";

const statusBoje = {
  "Aktivan": "#1bbfa5",
  "Završen": "#4bcf5a",
  "Čeka": "#f7b731"
};

const statusIkona = {
  "Aktivan": <MdHourglassBottom style={{ color: "#1bbfa5", verticalAlign: "middle" }} />,
  "Završen": <MdCheckCircle style={{ color: "#4bcf5a", verticalAlign: "middle" }} />,
  "Čeka": <MdHourglassBottom style={{ color: "#f7b731", verticalAlign: "middle" }} />
};

const MojiPlanovi = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [planovi, setPlanovi] = useState([]);

  useEffect(() => {
    axiosInstance.get(`/planovi/moji?klijentId=${korisnikId}`).then(res => setPlanovi(res.data));
  }, [korisnikId]);

  return (
    <div className="plan-bg">
      <div className="plan-card">
        <h2 className="plan-title"><MdDescription style={{marginRight: 7}}/> Moji planovi</h2>
        {planovi.length === 0 ? (
          <div style={{color: "#888", margin: "30px 0", fontSize: "1.13em"}}>Nemate dodijeljenih planova.</div>
        ) : (
          <table className="plan-table">
            <thead>
              <tr>
                <th>Naziv plana</th>
                <th>Opis</th>
                <th>Status</th>
                <th>Datum dodjele</th>
              </tr>
            </thead>
            <tbody>
              {planovi.map(p => (
                <tr key={p.plan.id}>
                  <td style={{fontWeight: 600, color: "#1bbfa5"}}>{p.plan.nazivPlana}</td>
                  <td style={{textAlign: "left"}}>{p.plan.opis}</td>
                  <td>
                    <span style={{
                      color: statusBoje[p.status] || "#23272f",
                      fontWeight: 600,
                      display: "inline-flex",
                      alignItems: "center",
                      gap: 6
                    }}>
                      {statusIkona[p.status]} {p.status}
                    </span>
                  </td>
                  <td>{p.datumDodjele}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default MojiPlanovi;
