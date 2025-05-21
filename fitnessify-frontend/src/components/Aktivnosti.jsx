import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdDelete, MdEdit, MdSave, MdCancel, MdAdd } from "react-icons/md";
import "../style/Namirnice.css";

function Aktivnosti() {
  const [aktivnosti, setAktivnosti] = useState([]);
  const [search, setSearch] = useState("");
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({
    vrstaAktivnosti: "",
    kalorijePoSatu: "",
  });
  const [msg, setMsg] = useState("");

  useEffect(() => {
    fetchAktivnosti();
  }, []);

  const fetchAktivnosti = async () => {
    const res = await axiosInstance.get("/sifrarnik-aktivnosti");
    setAktivnosti(res.data);
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleEdit = a => {
    setEditId(a.id);
    setForm({
      vrstaAktivnosti: a.vrstaAktivnosti,
      kalorijePoSatu: a.kalorijePoSatu,
    });
    setMsg("");
  };

  const handleDelete = async id => {
    if (window.confirm("Obrisati aktivnost?")) {
      try {
        await axiosInstance.delete(`/sifrarnik-aktivnosti/${id}`);
        fetchAktivnosti();
        setMsg("");
      } catch (err) {
        const poruka = err.response?.data || "Greška pri brisanju!";
        setMsg(poruka);
      }
    }
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setMsg("");
    if (!form.vrstaAktivnosti.trim()) {
      setMsg("Vrsta aktivnosti je obavezna.");
      return;
    }
    if (form.kalorijePoSatu <= 0) {
      setMsg("Kalorije po satu moraju biti veće od 0.");
      return;
    }
    try {
      if (editId) {
        await axiosInstance.put(`/sifrarnik-aktivnosti/${editId}`, form);
      } else {
        await axiosInstance.post("/sifrarnik-aktivnosti", form);
      }
      setForm({ vrstaAktivnosti: "", kalorijePoSatu: "" });
      setEditId(null);
      fetchAktivnosti();
      setMsg("");
    } catch (err) {
      const poruka = err.response?.data || "Greška pri spremanju!";
      setMsg(poruka);
    }
  };

  const handleCancel = () => {
    setEditId(null);
    setForm({ vrstaAktivnosti: "", kalorijePoSatu: "" });
    setMsg("");
  };

  const filtriraneAktivnosti = aktivnosti.filter(a =>
    a.vrstaAktivnosti.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="namirnice-bg">
      <div className="namirnice-card">
        <h2 className="namirnice-title">Aktivnosti</h2>
        <form className="namirnice-form" onSubmit={handleSubmit}>
          <input
            type="text"
            name="vrstaAktivnosti"
            placeholder="Vrsta aktivnosti"
            value={form.vrstaAktivnosti}
            onChange={handleChange}
            className="namirnice-input"
            required
          />
          <input
            type="number"
            name="kalorijePoSatu"
            placeholder="Kalorije po satu"
            value={form.kalorijePoSatu}
            onChange={handleChange}
            className="namirnice-input"
            required
          />
          <button type="submit" className="namirnice-btn">
            {editId ? <><MdSave /> Spremi</> : <><MdAdd /> Dodaj</>}
          </button>
          {editId && (
            <button type="button" className="namirnice-btn cancel-btn" onClick={handleCancel}>
              <MdCancel /> Odustani
            </button>
          )}
        </form>
        {msg && <div className="namirnice-msg">{msg}</div>}
        <input
          type="text"
          placeholder="Pretraži po vrsti aktivnosti..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="namirnice-search"
        />
        <table className="namirnice-table">
          <thead>
            <tr>
              <th>Vrsta aktivnosti</th>
              <th>Kalorije po satu</th>
              <th>Akcije</th>
            </tr>
          </thead>
          <tbody>
            {filtriraneAktivnosti.length === 0 ? (
              <tr>
                <td colSpan={3} className="namirnice-empty">
                  Nema rezultata.
                </td>
              </tr>
            ) : (
              filtriraneAktivnosti.map(a => (
                <tr key={a.id}>
                  <td>{a.vrstaAktivnosti}</td>
                  <td>{a.kalorijePoSatu}</td>
                  <td>
                    <button
                      className="namirnice-icon-btn"
                      onClick={() => handleEdit(a)}
                      title="Uredi"
                    >
                      <MdEdit size={22} />
                    </button>
                    <button
                      className="namirnice-icon-btn delete-btn"
                      onClick={() => handleDelete(a.id)}
                      title="Obriši"
                    >
                      <MdDelete size={22} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Aktivnosti;
