import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import dayjs from "dayjs";
import { MdFitnessCenter, MdDelete, MdEdit, MdSave, MdCancel, MdTimer, MdLocalFireDepartment } from "react-icons/md";
import "../style/Aktivnosti.css";

const MojeAktivnosti = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [aktivnosti, setAktivnosti] = useState([]);
  const [unosi, setUnosi] = useState([]);
  const [aktivnostId, setAktivnostId] = useState("");
  const [trajanje, setTrajanje] = useState("");
  const [datum, setDatum] = useState(dayjs().format("YYYY-MM-DD"));
  const [loading, setLoading] = useState(true);

  // Edit state
  const [editId, setEditId] = useState(null); // {aktivnostId, datum}
  const [editTrajanje, setEditTrajanje] = useState("");

  useEffect(() => {
    axiosInstance.get("/aktivnosti")
      .then(res => setAktivnosti(res.data));
  }, []);

  const fetchUnosi = () => {
    setLoading(true);
    axiosInstance.get(`/aktivnosti/korisnik/${korisnikId}/${datum}`)
      .then(res => setUnosi(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUnosi();
    // eslint-disable-next-line
  }, [datum]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!aktivnostId || !trajanje) return;
    await axiosInstance.post("/aktivnosti/korisnik", {
      korisnikId,
      aktivnostId,
      trajanje,
      datum
    });
    setAktivnostId("");
    setTrajanje("");
    fetchUnosi();
  };

  const handleDelete = async (aktivnostId, datum) => {
    await axiosInstance.delete(`/aktivnosti/korisnik`, {
      params: { korisnikId, aktivnostId, datum }
    });
    fetchUnosi();
  };

  const handleEdit = (aktivnostId, trajanje, datum) => {
    setEditId({ aktivnostId, datum });
    setEditTrajanje(trajanje);
  };

  const handleSaveEdit = async () => {
    await axiosInstance.put("/aktivnosti/korisnik", {
      korisnikId,
      aktivnostId: editId.aktivnostId,
      trajanje: editTrajanje,
      datum: editId.datum
    });
    setEditId(null);
    setEditTrajanje("");
    fetchUnosi();
  };

  const handleCancelEdit = () => {
    setEditId(null);
    setEditTrajanje("");
  };

  // Izračun ukupnog trajanja i ukupno potrošenih kalorija
  const ukupnoTrajanje = unosi.reduce((acc, unos) => acc + (unos.trajanje || 0), 0);
  const ukupnoKalorija = unosi.reduce((acc, unos) => acc + (unos.potroseneKalorije || 0), 0);

  return (
    <div className="activity-bg">
      <div className="activity-card">
        <h2 className="activity-title"><MdFitnessCenter /> Unos aktivnosti</h2>
        <form className="activity-form" onSubmit={handleSubmit}>
          <select value={aktivnostId} onChange={e => setAktivnostId(e.target.value)} required>
            <option value="">Odaberi aktivnost</option>
            {aktivnosti.map(a => (
              <option key={a.id} value={a.id}>{a.vrstaAktivnosti}</option>
            ))}
          </select>
          <input
            type="number"
            placeholder="Trajanje (min)"
            value={trajanje}
            min="1"
            onChange={e => setTrajanje(e.target.value)}
            required
          />
          <input
            type="date"
            value={datum}
            onChange={e => setDatum(e.target.value)}
          />
          <button type="submit" className="activity-btn main-btn">Dodaj</button>
        </form>

        <div style={{
          display: "flex",
          justifyContent: "center",
          gap: "32px",
          margin: "22px 0 0 0",
          fontSize: "1.12em",
          color: "#23272f",
          fontWeight: 500
        }}>
          <span title="Ukupno trajanje">
            <MdTimer style={{ color: "#1bbfa5", verticalAlign: "middle", marginRight: 4 }} />
            {ukupnoTrajanje} min
          </span>
          <span title="Ukupno potrošene kalorije">
            <MdLocalFireDepartment style={{ color: "#e05757", verticalAlign: "middle", marginRight: 4 }} />
            {ukupnoKalorija.toFixed(0)} kcal
          </span>
        </div>

        <h3 style={{ marginTop: 22 }}>Vaše aktivnosti za {datum}</h3>
        {loading ? (
          <div>Učitavanje...</div>
        ) : unosi.length === 0 ? (
          <div style={{ color: "#aaa", marginTop: 16 }}>Nema aktivnosti za ovaj dan.</div>
        ) : (
          <table className="activity-table">
            <thead>
              <tr>
                <th>Aktivnost</th>
                <th>Trajanje (min)</th>
                <th>Potrošene kalorije</th>
                <th>Akcija</th>
              </tr>
            </thead>
            <tbody>
              {unosi.map(unos => {
                const isEditing = editId && editId.aktivnostId === unos.aktivnost?.id && editId.datum === datum;
                return (
                  <tr key={unos.aktivnost?.id}>
                    <td>{unos.aktivnost?.vrstaAktivnosti}</td>
                    <td>
                      {isEditing ? (
                        <input
                          type="number"
                          value={editTrajanje}
                          min="1"
                          onChange={e => setEditTrajanje(e.target.value)}
                          style={{ width: 70 }}
                        />
                      ) : (
                        unos.trajanje
                      )}
                    </td>
                    <td>
                      {isEditing
                        ? (unos.aktivnost?.kalorijePoSatu * editTrajanje / 60).toFixed(0)
                        : (unos.potroseneKalorije ? unos.potroseneKalorije.toFixed(0) : "-")}
                    </td>
                    <td>
                      {isEditing ? (
                        <div className="activity-action-group">
                          <button className="activity-btn save-btn" onClick={handleSaveEdit} type="button">
                            <MdSave /> Spremi
                          </button>
                          <button className="activity-btn cancel-btn" onClick={handleCancelEdit} type="button">
                            <MdCancel /> Odustani
                          </button>
                        </div>
                      ) : (
                        <div className="activity-action-group">
                          <button
                            className="activity-btn edit-btn"
                            onClick={() => handleEdit(unos.aktivnost?.id, unos.trajanje, datum)}
                            type="button"
                          >
                            <MdEdit />
                          </button>
                          <button
                            className="activity-btn delete-btn"
                            onClick={() => handleDelete(unos.aktivnost?.id, datum)}
                            title="Obriši"
                            type="button"
                          >
                            <MdDelete />
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default MojeAktivnosti;
