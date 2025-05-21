import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, Legend } from "recharts";

const MjesecniGraf = ({ korisnikId }) => {
  const [data, setData] = useState([]);

  useEffect(() => {
    axiosInstance.get(`/statistika/mjesecna?korisnikId=${korisnikId}`)
      .then(res => {
        // Formatiraj datum za prikaz na X-osi
        const formatted = res.data.map(item => ({
          datum: new Date(item.datum).toLocaleDateString('hr-HR', { day: '2-digit', month: '2-digit' }),
          unosKalorija: item.unosKalorija,
          potrosnjaKalorija: item.potrosnjaKalorija,
          bilanca: item.bilanca
        }));
        setData(formatted);
      });
  }, [korisnikId]);

  return (
    <div style={{ width: "100%", maxWidth: 700, margin: "20px auto", background: "#fff", borderRadius: 14, boxShadow: "0 2px 12px #1bbfa520", padding: 24 }}>
      <h3 style={{ color: "#1bbfa5", marginBottom: 18 }}>Mjesečna statistika (zadnjih 30 dana)</h3>
      <ResponsiveContainer width="100%" height={260}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="datum" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="unosKalorija" stroke="#1bbfa5" name="Unos kalorija" />
          <Line type="monotone" dataKey="potrosnjaKalorija" stroke="#e05757" name="Potrošnja kalorija" />
          <Line type="monotone" dataKey="bilanca" stroke="#8884d8" name="Bilanca" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MjesecniGraf;
