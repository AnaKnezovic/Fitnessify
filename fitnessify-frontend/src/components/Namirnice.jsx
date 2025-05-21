import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdDelete, MdEdit, MdSave, MdCancel, MdAdd } from "react-icons/md";
import "../style/Namirnice.css";

function Namirnice() {
  const [namirnice, setNamirnice] = useState([]);
  const [search, setSearch] = useState("");
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({
    naziv: "",
    kalorije: "",
    proteini: "",
    ugljikohidrati: "",
    masti: "",
  });
  const [msg, setMsg] = useState("");

  useEffect(() => {
    fetchNamirnice();
  }, []);

  const fetchNamirnice = async () => {
    const res = await axiosInstance.get("/namirnice");
    setNamirnice(res.data);
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleEdit = n => {
    setEditId(n.id);
    setForm({
      naziv: n.naziv,
      kalorije: n.kalorije,
      proteini: n.proteini,
      ugljikohidrati: n.ugljikohidrati,
      masti: n.masti,
    });
    setMsg("");
  };

  const handleDelete = async id => {
    if (window.confirm("Obrisati namirnicu?")) {
      try {
        await axiosInstance.delete(`/namirnice/${id}`);
        fetchNamirnice();
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
    if (!form.naziv.trim()) {
      setMsg("Naziv je obavezan.");
      return;
    }
    if (form.kalorije <= 0) {
      setMsg("Kalorije moraju biti veće od 0.");
      return;
    }
    try {
      if (editId) {
        await axiosInstance.put(`/namirnice/${editId}`, form);
      } else {
        await axiosInstance.post("/namirnice", form);
      }
      setForm({ naziv: "", kalorije: "", proteini: "", ugljikohidrati: "", masti: "" });
      setEditId(null);
      fetchNamirnice();
      setMsg("");
    } catch (err) {
      const poruka = err.response?.data || "Greška pri spremanju!";
      setMsg(poruka);
    }
  };

  const handleCancel = () => {
    setEditId(null);
    setForm({ naziv: "", kalorije: "", proteini: "", ugljikohidrati: "", masti: "" });
    setMsg("");
  };

  const filtriraneNamirnice = namirnice.filter(n =>
    n.naziv.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="namirnice-bg">
      <div className="namirnice-card">
        <h2 className="namirnice-title">Namirnice</h2>
        <form className="namirnice-form" onSubmit={handleSubmit}>
          <input
            type="text"
            name="naziv"
            placeholder="Naziv"
            value={form.naziv}
            onChange={handleChange}
            className="namirnice-input"
            required
          />
          <input
            type="number"
            name="kalorije"
            placeholder="Kalorije (na 100g)"
            value={form.kalorije}
            onChange={handleChange}
            className="namirnice-input"
            required
          />
          <input
            type="number"
            name="proteini"
            placeholder="Proteini (g)"
            value={form.proteini}
            onChange={handleChange}
            className="namirnice-input"
          />
          <input
            type="number"
            name="ugljikohidrati"
            placeholder="Ugljikohidrati (g)"
            value={form.ugljikohidrati}
            onChange={handleChange}
            className="namirnice-input"
          />
          <input
            type="number"
            name="masti"
            placeholder="Masti (g)"
            value={form.masti}
            onChange={handleChange}
            className="namirnice-input"
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
          placeholder="Pretraži po nazivu..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="namirnice-search"
        />
        <table className="namirnice-table">
          <thead>
            <tr>
              <th>Naziv</th>
              <th>Kalorije</th>
              <th>Proteini</th>
              <th>Ugljikohidrati</th>
              <th>Masti</th>
              <th>Akcije</th>
            </tr>
          </thead>
          <tbody>
            {filtriraneNamirnice.length === 0 ? (
              <tr>
                <td colSpan={6} className="namirnice-empty">
                  Nema rezultata.
                </td>
              </tr>
            ) : (
              filtriraneNamirnice.map(n => (
                <tr key={n.id}>
                  <td>{n.naziv}</td>
                  <td>{n.kalorije}</td>
                  <td>{n.proteini}</td>
                  <td>{n.ugljikohidrati}</td>
                  <td>{n.masti}</td>
                  <td>
                    <button
                      className="namirnice-icon-btn"
                      onClick={() => handleEdit(n)}
                      title="Uredi"
                    >
                      <MdEdit size={22} />
                    </button>
                    <button
                      className="namirnice-icon-btn delete-btn"
                      onClick={() => handleDelete(n.id)}
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

export default Namirnice;
