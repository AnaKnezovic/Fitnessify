import React, { useState, useEffect } from "react";
import axiosInstance from "../axiosInstance";
import dayjs from "dayjs";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Cancel";
import "../style/Prehrana.css";

const obroci = ["Dorucak", "Rucak", "Vecera", "Meduobrok"];

const PrehrambeniDnevnik = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [namirnice, setNamirnice] = useState([]);
  const [unosi, setUnosi] = useState([]);
  const [loading, setLoading] = useState(true);
  const [datum, setDatum] = useState(dayjs().format("YYYY-MM-DD"));
  const [vrstaObroka, setVrstaObroka] = useState("Dorucak");
  const [odabranaNamirnica, setOdabranaNamirnica] = useState("");
  const [kolicina, setKolicina] = useState("");
  const [editDnevnikId, setEditDnevnikId] = useState(null);
  const [editNamirnicaId, setEditNamirnicaId] = useState(null);
  const [poruka, setPoruka] = useState("");
  const [searchNamirnica, setSearchNamirnica] = useState("");
  const [searchObrok, setSearchObrok] = useState("");

  useEffect(() => {
    axiosInstance.get("/namirnice")
      .then(res => setNamirnice(res.data))
      .catch(() => setNamirnice([]));
  }, []);

  useEffect(() => {
    fetchUnosi();
    // eslint-disable-next-line
  }, [datum, korisnikId]);

  const fetchUnosi = () => {
    if (!korisnikId) return;
    setLoading(true);
    axiosInstance
      .get(`/dnevnik/svi-unosi/${korisnikId}/${datum}`)
      .then(res => setUnosi(res.data))
      .catch(() => setUnosi([]))
      .finally(() => setLoading(false));
  };

  const handleDodajUnos = async (e) => {
    e.preventDefault();
    setPoruka("");
    if (!odabranaNamirnica || !kolicina || !korisnikId) return;

    if (
      unosi.some(
        u =>
          u.namirnica?.id === odabranaNamirnica &&
          u.dnevnik?.vrstaObroka === vrstaObroka &&
          u.dnevnik?.datumObroka === datum &&
          !(
            editDnevnikId &&
            editNamirnicaId &&
            u.dnevnik?.id === editDnevnikId &&
            u.namirnica?.id === editNamirnicaId
          )
      )
    ) {
      setPoruka("Ova namirnica je već unesena za ovaj obrok i datum!");
      return;
    }

    if (parseFloat(kolicina) <= 0) {
      setPoruka("Količina mora biti veća od 0!");
      return;
    }

    try {
      if (editDnevnikId && editNamirnicaId) {
        // EDIT
        await axiosInstance.put(
          `/dnevnik/unos/${editDnevnikId}/${editNamirnicaId}`,
          { kolicina: parseFloat(kolicina) }
        );
        setEditDnevnikId(null);
        setEditNamirnicaId(null);
      } else {
        // DODAJ
        const unosDTO = {
          korisnikId,
          datumObroka: datum,
          vrstaObroka,
          namirnice: [
            {
              namirnicaId: odabranaNamirnica,
              kolicina: parseFloat(kolicina)
            }
          ]
        };
        await axiosInstance.post("/dnevnik", unosDTO);
      }
      fetchUnosi();
      setOdabranaNamirnica("");
      setKolicina("");
      setPoruka("");
    } catch (error) {
      // Prikaz backend poruke korisniku
      const msg =
        error.response?.data && typeof error.response.data === "string"
          ? error.response.data
          : "Greška pri unosu!";
      setPoruka(msg);
    }
  };

  const handleDelete = async (dnevnikId, namirnicaId) => {
    if (window.confirm("Obrisati unos?")) {
      await axiosInstance.delete(`/dnevnik/unos/${dnevnikId}/${namirnicaId}`);
      fetchUnosi();
    }
  };

  const handleEdit = (unos) => {
    setEditDnevnikId(unos.dnevnik?.id);
    setEditNamirnicaId(unos.namirnica?.id);
    setOdabranaNamirnica(unos.namirnica?.id || "");
    setKolicina(unos.kolicina?.toString() || "");
    setVrstaObroka(unos.dnevnik?.vrstaObroka || "Dorucak");
  };

  // Statistika
  const ukupno = unosi.reduce((acc, unos) => {
    const k = Number(unos.kolicina) || 0;
    const n = unos.namirnica || {};
    acc.kalorije += n.kalorije ? n.kalorije * k / 100 : 0;
    acc.proteini += n.proteini ? n.proteini * k / 100 : 0;
    acc.uh += n.ugljikohidrati ? n.ugljikohidrati * k / 100 : 0;
    acc.masti += n.masti ? n.masti * k / 100 : 0;
    return acc;
  }, { kalorije: 0, proteini: 0, uh: 0, masti: 0 });

  // Pretraga po nazivu namirnice i po obroku
  const filtriraniUnosi = unosi.filter(unos =>
    unos.namirnica?.naziv?.toLowerCase().includes(searchNamirnica.toLowerCase()) &&
    unos.dnevnik?.vrstaObroka?.toLowerCase().includes(searchObrok.toLowerCase())
  );

  if (!korisnikId) {
    return <div style={{ padding: 40, color: "red" }}>Niste prijavljeni.</div>;
  }

  return (
    <div className="dnevnik-bg-wide">
      <div className="dnevnik-card-wide">
        <h2 className="dnevnik-title">Prehrambeni dnevnik</h2>
        <div className="dnevnik-master-wide">
          <div><b>Datum:</b> {datum}</div>
          <div><b>Vrsta obroka:</b> {vrstaObroka}</div>
        </div>
        <div className="dnevnik-controls-wide">
          <input
            type="date"
            value={datum}
            onChange={e => setDatum(e.target.value)}
            className="dnevnik-date"
          />
          <select
            value={vrstaObroka}
            onChange={e => setVrstaObroka(e.target.value)}
            disabled={!!editDnevnikId}
            className="dnevnik-select"
          >
            {obroci.map(obrok => (
              <option key={obrok} value={obrok}>{obrok}</option>
            ))}
          </select>
        </div>
        <form className="unos-form-wide" onSubmit={handleDodajUnos}>
          <select
            value={odabranaNamirnica}
            onChange={e => setOdabranaNamirnica(e.target.value)}
            required
            disabled={!!editDnevnikId}
            className="dnevnik-select"
          >
            <option value="">Odaberi namirnicu</option>
            {namirnice.map(n => (
              <option key={n.id} value={n.id}>{n.naziv}</option>
            ))}
          </select>
          <input
            type="number"
            placeholder="Količina (g/ml/kom)"
            value={kolicina}
            min="1"
            onChange={e => setKolicina(e.target.value.replace(/^0+/, ""))}
            required
            className="dnevnik-input"
          />
          <Tooltip title={editDnevnikId ? "Spremi promjene" : "Dodaj unos"}>
            <span>
              <IconButton
                color={editDnevnikId ? "success" : "primary"}
                type="submit"
                size="large"
                style={{ marginLeft: 6 }}
                disabled={!odabranaNamirnica || !kolicina}
              >
                {editDnevnikId ? <SaveIcon /> : <AddCircleIcon />}
              </IconButton>
            </span>
          </Tooltip>
          {editDnevnikId && (
            <Tooltip title="Odustani">
              <span>
                <IconButton
                  color="secondary"
                  size="large"
                  onClick={() => {
                    setEditDnevnikId(null);
                    setEditNamirnicaId(null);
                    setOdabranaNamirnica("");
                    setKolicina("");
                  }}
                >
                  <CancelIcon />
                </IconButton>
              </span>
            </Tooltip>
          )}
        </form>
        {poruka && (
          <div className="dnevnik-msg">{poruka}</div>
        )}

        {/* SEARCH polja tik iznad tablice */}
        <div style={{ display: "flex", gap: 18, margin: "10px 0 6px 0", alignItems: "center" }}>
          <input
            type="text"
            placeholder="Pretraži po nazivu namirnice..."
            value={searchNamirnica}
            onChange={e => setSearchNamirnica(e.target.value)}
            className="dnevnik-search-input"
            style={{ width: 220, padding: "7px 11px", borderRadius: 7, border: "1.5px solid #b0d9d7" }}
          />
          <input
            type="text"
            placeholder="Pretraži po obroku..."
            value={searchObrok}
            onChange={e => setSearchObrok(e.target.value)}
            className="dnevnik-search-input"
            style={{ width: 180, padding: "7px 11px", borderRadius: 7, border: "1.5px solid #b0d9d7" }}
          />
        </div>

        <h3 style={{ marginTop: "20px" }}>Unosi za <span className="dnevnik-datum">{datum}</span></h3>
        <div className="statistika-box-wide">
          <b>Ukupno:</b>
          <span className="statistika kalorije">Kalorije: {Math.round(ukupno.kalorije)} kcal</span>
          <span className="statistika proteini">Proteini: {ukupno.proteini.toFixed(1)} g</span>
          <span className="statistika uh">UH: {ukupno.uh.toFixed(1)} g</span>
          <span className="statistika masti">Masti: {ukupno.masti.toFixed(1)} g</span>
        </div>
        {loading ? (
          <div className="ucitavanje">Učitavanje...</div>
        ) : filtriraniUnosi.length === 0 ? (
          <div className="nema-unosa">Nema unosa koji odgovaraju pretrazi.</div>
        ) : (
          <table className="dnevnik-table-wide">
            <thead>
              <tr>
                <th>Obrok</th>
                <th>Namirnica</th>
                <th>Količina</th>
                <th>Kalorije</th>
                <th>Proteini</th>
                <th>UH</th>
                <th>Masti</th>
                <th>Akcije</th>
              </tr>
            </thead>
            <tbody>
              {filtriraniUnosi.map(unos => (
                <tr key={`${unos.dnevnik?.id}_${unos.namirnica?.id}`}>
                  <td>{unos.dnevnik?.vrstaObroka}</td>
                  <td>{unos.namirnica?.naziv}</td>
                  <td>{unos.kolicina}</td>
                  <td>{unos.namirnica?.kalorije ? Math.round(unos.namirnica.kalorije * unos.kolicina / 100) : '-'}</td>
                  <td>{unos.namirnica?.proteini ? (unos.namirnica.proteini * unos.kolicina / 100).toFixed(1) : '-'}</td>
                  <td>{unos.namirnica?.ugljikohidrati ? (unos.namirnica.ugljikohidrati * unos.kolicina / 100).toFixed(1) : '-'}</td>
                  <td>{unos.namirnica?.masti ? (unos.namirnica.masti * unos.kolicina / 100).toFixed(1) : '-'}</td>
                  <td>
                    <div style={{ display: "flex", gap: "8px", justifyContent: "center" }}>
                      <Tooltip title="Uredi">
                        <span>
                          <IconButton
                            color="primary"
                            size="small"
                            onClick={() => handleEdit(unos)}
                          >
                            <EditIcon />
                          </IconButton>
                        </span>
                      </Tooltip>
                      <Tooltip title="Obriši">
                        <span>
                          <IconButton
                            color="error"
                            size="small"
                            onClick={() => handleDelete(unos.dnevnik?.id, unos.namirnica?.id)}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </span>
                      </Tooltip>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default PrehrambeniDnevnik;
