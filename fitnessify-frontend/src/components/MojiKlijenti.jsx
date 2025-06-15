import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdPerson, MdCheckCircle, MdPending, MdCancel, MdSearch, MdRemoveCircleOutline } from "react-icons/md";
import { Link } from "react-router-dom";
import "../style/MojiKlijenti.css";

const statusBoje = {
  "PRIHVACENO": "#4bcf5a",
  "Čeka": "#f7b731",
  "ODBIJENO": "#e05757",
  "TIMEOUT": "#e05757"
};

const statusIkona = {
  "PRIHVACENO": <MdCheckCircle style={{ color: "#4bcf5a", verticalAlign: "middle" }} />,
  "Čeka": <MdPending style={{ color: "#f7b731", verticalAlign: "middle" }} />,
  "ODBIJENO": <MdCancel style={{ color: "#e05757", verticalAlign: "middle" }} />,
  "TIMEOUT": <MdCancel style={{ color: "#e05757", verticalAlign: "middle" }} />
};

const MojiKlijenti = () => {
  const trenerId = localStorage.getItem("korisnikId");
  const [klijenti, setKlijenti] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [search, setSearch] = useState("");
  const [poruka, setPoruka] = useState("");

  useEffect(() => {
    fetchKlijenti();
    fetchTasks();
    // eslint-disable-next-line
  }, [trenerId]);

  const fetchKlijenti = async () => {
    try {
      const res = await axiosInstance.get(`/klijent-trener/moji-klijenti?trenerId=${trenerId}`);
      setKlijenti(res.data);
    } catch {
      setKlijenti([]);
    }
  };

  const fetchTasks = async () => {
    try {
      const res = await axiosInstance.get(`/camunda-tasks/for-trainer?trenerId=${trenerId}`);
      setTasks(res.data);
    } catch {
      setTasks([]);
    }
  };

  const completeTask = async (taskId, status) => {
    setPoruka("");
    try {
      await axiosInstance.post(`/camunda-tasks/${taskId}/complete`, { statusZahtjeva: status });
      setPoruka("Zadatak odrađen.");
      fetchTasks();
      fetchKlijenti();
    } catch {
      setPoruka("Greška pri odrađivanju zadatka.");
    }
  };

  const prekiniOdnos = async (klijentId) => {
    setPoruka("");
    if (!window.confirm("Jeste li sigurni da želite prekinuti odnos s ovim klijentom?")) return;
    try {
      await axiosInstance.delete(`/klijent-trener`, {
        params: { klijentId, trenerId }
      });
      await fetchKlijenti();
      setPoruka("Odnos s klijentom je prekinut.");
    } catch {
      setPoruka("Greška pri prekidu odnosa.");
    }
  };

  const filtriraniKlijenti = klijenti.filter(({ klijent }) =>
    (klijent.korisnik?.ime + " " + klijent.korisnik?.prezime + " " + klijent.korisnik?.email)
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div className="trainer-table-bg">
      <h2 className="trainer-table-title">
        <MdPerson style={{marginRight: 7}}/> Moji klijenti
      </h2>
      <div className="trainer-table-controls">
        <MdSearch style={{ color: "#1bbfa5", fontSize: "1.3em", marginRight: 6, verticalAlign: "middle" }} />
        <input
          type="text"
          placeholder="Pretraži klijente..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="trainer-search-input"
        />
      </div>

      <table className="trainer-table-minimal">
        <thead>
          <tr>
            <th>Ime i prezime</th>
            <th>Email</th>
            <th>Status veze</th>
            <th>Datum povezivanja</th>
            <th>Akcija</th>
          </tr>
        </thead>
        <tbody>
          {filtriraniKlijenti.length === 0 ? (
            <tr>
              <td colSpan={5} style={{ textAlign: "center", color: "#888" }}>
                Nema klijenata koji odgovaraju pretrazi.
              </td>
            </tr>
          ) : (
            filtriraniKlijenti.map(({ klijent, status, datumPovezivanja }, idx) => {
              // Pronađi task za ovog klijenta (ako postoji)
              const task = tasks.find(t => t.klijentId === String(klijent.id));
              return (
                <tr key={idx}>
                  <td>{klijent.korisnik?.ime} {klijent.korisnik?.prezime}</td>
                  <td>{klijent.korisnik?.email}</td>
                  <td>
                    <span style={{
                      color: statusBoje[status] || "#23272f",
                      fontWeight: 600,
                      display: "inline-flex",
                      alignItems: "center",
                      gap: 6
                    }}>
                      {statusIkona[status]} {status}
                    </span>
                  </td>
                  <td>{datumPovezivanja}</td>
                  <td>
                    {task ? (
                      <div className="trainer-action-group">
                        <button
                          className="trainer-btn accept-btn"
                          onClick={() => completeTask(task.id, "PRIHVACENO")}
                        >
                          Prihvati
                        </button>
                        <button
                          className="trainer-btn reject-btn"
                          style={{ marginLeft: 8 }}
                          onClick={() => completeTask(task.id, "ODBIJENO")}
                        >
                          Odbij
                        </button>
                      </div>
                    ) : status === "PRIHVACENO" ? (
                      <div className="trainer-action-group">
                        <Link to={`/klijenti/${klijent.id}`}>
                          <button className="trainer-btn details-btn">
                            Detalji
                          </button>
                        </Link>
                        <button
                          className="trainer-btn reject-btn"
                          style={{ marginLeft: 8, display: "flex", alignItems: "center", gap: 4 }}
                          onClick={() => prekiniOdnos(klijent.id)}
                          title="Prekini odnos"
                        >
                          <MdRemoveCircleOutline style={{ fontSize: "1.2em" }} />
                          Prekini odnos
                        </button>
                      </div>
                    ) : null}
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>
      {poruka && <div className="trainer-table-msg">{poruka}</div>}
    </div>
  );
};

export default MojiKlijenti;
