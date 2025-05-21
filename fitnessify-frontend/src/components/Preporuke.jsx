import React from "react";
import { MdOutlineLightbulb } from "react-icons/md";

const Preporuke = ({ unosKalorija, potroseneKalorije, cilj, bmi, bilancaDanas }) => {
  let poruke = [];

  // Preporuke na temelju bilance
  if (bilancaDanas > 300) {
    poruke.push("Unijeli ste više kalorija nego što ste potrošili. Ako je cilj mršavljenje, pokušajte povećati aktivnost ili smanjiti unos.");
  } else if (bilancaDanas < -200) {
    poruke.push("Odličan posao! Danas ste u kalorijskom deficitu.");
  }

  // Preporuke na temelju BMI-a
  if (bmi && !isNaN(bmi)) {
    if (bmi < 18.5) poruke.push("Vaš BMI je nizak. Razmislite o povećanju unosa kalorija i konzultirajte se s trenerom.");
    else if (bmi > 25) poruke.push("Vaš BMI je povišen. Preporučujemo više aktivnosti i kontrolu unosa hrane.");
    else poruke.push("Vaš BMI je u preporučenim granicama. Nastavite s dobrim navikama!");
  }

  // Preporuka prema cilju
  if (cilj && cilj.toLowerCase().includes("mišić")) {
    poruke.push("Za izgradnju mišića, obratite pažnju na dovoljan unos proteina i redovitu tjelovježbu.");
  }

  // Ako nema posebnih poruka
  if (poruke.length === 0) poruke.push("Unosite podatke redovito za personalizirane preporuke!");

  return (
    <div style={{
      background: "#f8fafc",
      borderRadius: 10,
      padding: "18px 16px",
      margin: "22px 0 0 0",
      color: "#23272f",
      fontSize: "1.07em",
      boxShadow: "0 2px 12px #1bbfa520"
    }}>
      <div style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 8, display: "flex", alignItems: "center", gap: 8 }}>
        <MdOutlineLightbulb /> Preporuke i upozorenja
      </div>
      <ul style={{ margin: 0, paddingLeft: 24 }}>
        {poruke.map((msg, i) => (
          <li key={i}>{msg}</li>
        ))}
      </ul>
    </div>
  );
};

export default Preporuke;
