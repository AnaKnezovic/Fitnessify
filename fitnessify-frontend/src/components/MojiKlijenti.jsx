import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { MdPerson, MdCheckCircle, MdPending, MdCancel, MdSearch, MdRemoveCircleOutline } from "react-icons/md";
import { Link } from "react-router-dom";
import "../style/MojiKlijenti.css";

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

const MojiKlijenti = () => {
  const trenerId = localStorage.getItem("korisnikId");
  const [klijenti, setKlijenti] = useState([]);
  const [search, setSearch] = useState("");
  const [poruka, setPoruka] = useState("");

  useEffect(() => {
    fetchKlijenti();
    // eslint-disable-next-line
  }, [trenerId]);

  const fetchKlijenti = async () => {
    const res = await axiosInstance.get(`/klijent-trener/moji-klijenti?trenerId=${trenerId}`);
    setKlijenti(res.data);
  };

  const potvrdiVezu = async (klijentId, status) => {
    setPoruka("");
    try {
      await axiosInstance.patch("/klijent-trener/potvrdi", {
        klijentId,
        trenerId,
        status
      });
      await fetchKlijenti();
      setPoruka(status === "Prihvaćeno" ? "Veza prihvaćena." : "Veza odbijena.");
    } catch (e) {
      setPoruka("Greška pri potvrdi veze.");
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
    } catch (e) {
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
            filtriraniKlijenti.map(({ klijent, status, datumPovezivanja }, idx) => (
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
                  {status === "Čeka" && (
                    <div className="trainer-action-group">
                      <button
                        className="trainer-btn accept-btn"
                        onClick={() => potvrdiVezu(klijent.id, "Prihvaćeno")}
                      >
                        Prihvati
                      </button>
                      <button
                        className="trainer-btn reject-btn"
                        onClick={() => potvrdiVezu(klijent.id, "Odbijeno")}
                      >
                        Odbij
                      </button>
                    </div>
                  )}
                  {status === "Prihvaćeno" && (
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
                  )}
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
      {poruka && <div className="trainer-table-msg">{poruka}</div>}
    </div>
  );
};

export default MojiKlijenti;
