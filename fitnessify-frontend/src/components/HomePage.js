import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import dayjs from "dayjs";
import Preporuke from "./Preporuke";
import {
  MdPerson,
  MdHeight,
  MdMonitorWeight,
  MdFitnessCenter,
  MdFastfood,
  MdFlag
} from "react-icons/md";
import "../style/HomePage.css";
import MjesecniGraf from "./MjesecniGraf";
import WeeklyActivityCaloriesChart from "./WeeklyActivityCaloriesChart";
import WeeklyCaloriesChart from './WeeklyCaloriesChart';
import CalorieBalanceChart from "./CalorieBalanceChart";

const PREPORUCENI_UNOS = 2000;

function kategorijaBMI(bmi) {
  if (!bmi || isNaN(bmi)) return "";
  if (bmi < 18.5) return "Pothranjenost";
  if (bmi < 25) return "Normalna težina";
  if (bmi < 30) return "Prekomjerna težina";
  return "Pretilost";
}

const HomePage = () => {
  const [user, setUser] = useState(null);
  const [unosKalorija, setUnosKalorija] = useState(0);
  const [ukupnoTrajanje, setUkupnoTrajanje] = useState(0);
  const [ukupnoAktivKalorija, setUkupnoAktivKalorija] = useState(0);
  const [loading, setLoading] = useState(true);
  const korisnikId = localStorage.getItem("korisnikId");
  const role = localStorage.getItem("role");
  useEffect(() => {
    const fetchData = async () => {
      const danas = dayjs().format("YYYY-MM-DD");
      const userRes = await axiosInstance.get("/user/me");
      setUser(userRes.data);
      // Dohvati unos kalorija iz prehrane
      const prehranaRes = await axiosInstance.get(`/dnevnik/svi-unosi/${korisnikId}/${danas}`);
      let ukupno = 0;
      prehranaRes.data.forEach(unos => {
        if (unos.namirnica && unos.kolicina) {
          ukupno += (unos.namirnica.kalorije * unos.kolicina / 100);
        }
      });
      setUnosKalorija(Math.round(ukupno));
      // Dohvati aktivnosti
      const aktivnostRes = await axiosInstance.get(`/aktivnosti/korisnik/${korisnikId}/${danas}`);
      let ukupnoTraj = 0, ukupnoKal = 0;
      aktivnostRes.data.forEach(a => {
        ukupnoTraj += a.trajanje || 0;
        ukupnoKal += a.potroseneKalorije || 0;
      });
      setUkupnoTrajanje(ukupnoTraj);
      setUkupnoAktivKalorija(Math.round(ukupnoKal));
      setLoading(false);
    };
    fetchData();
    // eslint-disable-next-line
  }, []);

  if (loading || !user) {
    return (
      <div className="homepage-bg">
        <div className="homepage-card">Učitavanje...</div>
      </div>
    );
  }

  const ime = user.ime || "";
  const cilj = user.cilj || "Nema cilja";
  const visina = user.visina;
  const tezina = user.tezina;
  const bmi = visina && tezina ? (tezina / ((visina / 100) ** 2)).toFixed(1) : "--";
  const bmiKategorija = bmi !== "--" ? kategorijaBMI(bmi) : "";
  const bilancaDanas = unosKalorija - ukupnoAktivKalorija;

  return (
    <div className="homepage-bg">
      <div className="homepage-main-grid">
        <div className="homepage-card homepage-card-left">
          <h1 className="homepage-title">
            <MdPerson className="homepage-title-icon" />
            Pozdrav, {ime}!
          </h1>
          {role === "KLIJENT" && ( 
            <>
          <div className="homepage-info-row">
            <span><MdHeight className="homepage-info-icon" /> Visina:</span>
            <span>{visina ? `${visina} cm` : "--"}</span>
          </div>
          <div className="homepage-info-row">
            <span><MdMonitorWeight className="homepage-info-icon" /> Težina:</span>
            <span>{tezina ? `${tezina} kg` : "--"}</span>
          </div>
          <div className="homepage-info-row">
            <span><MdFitnessCenter className="homepage-info-icon" /> BMI:</span>
            <span>{bmi} {bmiKategorija && <span className="homepage-bmi-kat">({bmiKategorija})</span>}</span>
          </div>
          <div className="homepage-info-row">
            <span><MdFlag className="homepage-info-icon" /> Cilj:</span>
            <span>{cilj}</span>
          </div>
          </>
          )}
          <div className="homepage-progress-section">
            <div className="homepage-progress-label">
              <MdFastfood className="homepage-info-icon" />
              Dnevni unos kalorija
            </div>
            <div className="homepage-progress-bar-bg">
              <div
                className="homepage-progress-bar-fill"
                style={{
                  width: `${Math.min(100, (unosKalorija / PREPORUCENI_UNOS) * 100)}%`
                }}
              />
            </div>
            <div className="homepage-progress-text">
              {unosKalorija} / {PREPORUCENI_UNOS} kcal
            </div>
          </div>

          {/* DODATNE BRZE STATISTIKE */}
          <div className="homepage-quick-stats">
            <div>
              <span role="img" aria-label="fire" style={{ color: "#e05757", fontSize: 22, verticalAlign: "middle" }}>🔥</span>
              <span style={{ fontWeight: 500, marginLeft: 6 }}>Potrošeno kroz aktivnosti:</span>
              <span style={{ marginLeft: 8 }}>{ukupnoAktivKalorija} kcal</span>
            </div>
            <div>
              <span role="img" aria-label="timer" style={{ color: "#1bbfa5", fontSize: 22, verticalAlign: "middle" }}>⏱️</span>
              <span style={{ fontWeight: 500, marginLeft: 6 }}>Ukupno trajanje aktivnosti:</span>
              <span style={{ marginLeft: 8 }}>{ukupnoTrajanje} min</span>
            </div>
            <div>
              <span role="img" aria-label="balance" style={{ color: bilancaDanas < 0 ? "#1bbfa5" : "#e05757", fontSize: 22, verticalAlign: "middle" }}>⚖️</span>
              <span style={{ fontWeight: 500, marginLeft: 6 }}>Bilanca danas:</span>
              <span style={{ marginLeft: 8, color: bilancaDanas < 0 ? "#1bbfa5" : "#e05757", fontWeight: 600 }}>
                {bilancaDanas} kcal
              </span>
            </div>
          </div>

          {/* MOTIVACIJSKA PORUKA */}
          {bilancaDanas < 0 && (
            <div style={{color: "#1bbfa5", fontWeight: 600, marginTop: 10}}>
              Super! Danas ste u kalorijskom deficitu. Nastavite tako!
            </div>
          )}
          {bilancaDanas > 0 && (
            <div style={{color: "#e05757", fontWeight: 600, marginTop: 10}}>
              Pazite! Danas ste u kalorijskom suficitu.
            </div>
          )}
          <Preporuke
            unosKalorija={unosKalorija}
            potroseneKalorije={ukupnoAktivKalorija}
            cilj={cilj}
            bmi={bmi}
            bilancaDanas={bilancaDanas}
          />

        </div>
        <div className="homepage-card homepage-card-right">
          <WeeklyCaloriesChart />
          <div style={{ height: 24 }} />
          <WeeklyActivityCaloriesChart />
          <div style={{ height: 24 }} />
          <CalorieBalanceChart />
          <div style={{ height: 24 }} />
          <MjesecniGraf korisnikId={korisnikId} />
        </div>
      </div>
    </div>
  );
};

export default HomePage;
