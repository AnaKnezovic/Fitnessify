import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import dayjs from "dayjs";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";

const WeeklyActivityCaloriesChart = () => {
  const korisnikId = localStorage.getItem("korisnikId");
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      const today = dayjs();
      let chartData = [];
      for (let i = 6; i >= 0; i--) {
        const date = today.subtract(i, "day").format("YYYY-MM-DD");
        const res = await axiosInstance.get(`/aktivnosti/korisnik/${korisnikId}/${date}`);
        let total = 0;
        res.data.forEach(unos => {
          if (unos.potroseneKalorije) {
            total += unos.potroseneKalorije;
          }
        });
        chartData.push({
          date: dayjs(date).format("DD.MM."),
          kalorije: Math.round(total)
        });
      }
      setData(chartData);
    };
    fetchData();
  }, [korisnikId]);

  return (
    <div style={{ width: "100%", maxWidth: 600, margin: "0 auto", background: "#fff", borderRadius: 14, boxShadow: "0 2px 12px #1bbfa520", padding: 24 }}>
      <h3 style={{ color: "#1bbfa5", marginBottom: 18 }}>Potrošene kalorije kroz aktivnosti (7 dana)</h3>
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="kalorije" fill="#e05757" radius={[6, 6, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default WeeklyActivityCaloriesChart;
