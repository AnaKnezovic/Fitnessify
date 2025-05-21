import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import dayjs from "dayjs";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend } from "recharts";

const CalorieBalanceChart = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const today = dayjs();
      let chartData = [];
      for (let i = 6; i >= 0; i--) {
        const date = today.subtract(i, "day").format("YYYY-MM-DD");
        const unosiRes = await axiosInstance.get(`/dnevnik/svi-unosi/${korisnikId}/${date}`);
        const aktivnostiRes = await axiosInstance.get(`/aktivnosti/korisnik/${korisnikId}/${date}`);

        let totalUnos = 0;
        unosiRes.data.forEach(unos => {
          if (unos.namirnica && unos.kolicina) {
            totalUnos += (unos.namirnica.kalorije * unos.kolicina / 100);
          }
        });

        let totalPotrosnja = 0;
        aktivnostiRes.data.forEach(aktivnost => {
          if (aktivnost.potroseneKalorije) {
            totalPotrosnja += aktivnost.potroseneKalorije;
          }
        });

        chartData.push({
          date: dayjs(date).format("DD.MM."),
          unos: Math.round(totalUnos),
          potrosnja: Math.round(totalPotrosnja),
          bilanca: Math.round(totalUnos - totalPotrosnja)
        });
      }
      setData(chartData);
    };
    fetchData();
  }, [korisnikId]);

  return (
    <div style={{ width: "100%", maxWidth: 600, margin: "0 auto", background: "#fff", borderRadius: 14, boxShadow: "0 2px 12px #1bbfa520", padding: 24 }}>
      <h3 style={{ color: "#1bbfa5", marginBottom: 18 }}>Bilanca kalorija (unos vs potrošnja) - zadnjih 7 dana</h3>
      <ResponsiveContainer width="100%" height={260}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="unos" stackId="a" fill="#1bbfa5" name="Unos kalorija" />
          <Bar dataKey="potrosnja" stackId="a" fill="#e05757" name="Potrošnja kalorija" />
          <Bar dataKey="bilanca" fill="#8884d8" name="Bilanca kalorija" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default CalorieBalanceChart;
