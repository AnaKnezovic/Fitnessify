import {
  MdHelp,
  MdFastfood,
  MdFitnessCenter,
  MdBarChart,
  MdAccountCircle,
  MdEdit,
  MdDelete,
  MdOutlineLightbulb,
  MdLocalFireDepartment,
  MdTimer,
  MdPersonSearch,
  MdAssignmentTurnedIn,
  MdPeopleAlt
} from "react-icons/md";

const HelpPage = () => (
  <div style={{
    maxWidth: 740,
    margin: "40px auto",
    background: "#fff",
    borderRadius: 18,
    boxShadow: "0 2px 12px #1bbfa520",
    padding: 38
  }}>
    <h2 style={{ color: "#1bbfa5", marginBottom: 18, display: "flex", alignItems: "center", gap: 10 }}>
      <MdHelp /> Korisnički priručnik
    </h2>
    <div style={{ fontSize: "1.15em", color: "#23272f", lineHeight: 1.7 }}>
      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdAccountCircle /> Prvi koraci
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Registrirajte se i prijavite u aplikaciju.</li>
          <li>Unesite osnovne podatke (spol, dob, visina, težina, cilj) u svom profilu.</li>
          <li>Odaberite svoju ulogu: <b>klijent</b> (praćenje prehrane i napretka) ili <b>trener</b> (upravljanje klijentima i planovima).</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdFastfood /> Praćenje prehrane
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Na stranici <b>Prehrambeni dnevnik</b> odaberite datum, obrok i željenu namirnicu iz baze.</li>
          <li>Unesite količinu i kliknite <b>Dodaj</b>. Automatski će se izračunati kalorije i makronutrijenti.</li>
          <li>Možete uređivati ili brisati unos klikom na ikone <MdEdit style={{ verticalAlign: "middle" }}/> ili <MdDelete style={{ verticalAlign: "middle" }}/> .</li>
          <li>Ponovite postupak za svaki obrok u danu.</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdFitnessCenter /> Praćenje aktivnosti
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Na stranici <b>Aktivnosti</b> odaberite vrstu aktivnosti (npr. hodanje, trčanje, plivanje), unesite trajanje i datum pa kliknite <b>Dodaj</b>.</li>
          <li>Automatski će se izračunati potrošene kalorije na temelju vrste i trajanja aktivnosti.</li>
          <li>Možete uređivati ili brisati aktivnosti klikom na ikone <MdEdit style={{ verticalAlign: "middle" }}/> ili <MdDelete style={{ verticalAlign: "middle" }}/> .</li>
          <li>Ispod forme vidjet ćete pregled svih aktivnosti za odabrani dan, ukupno trajanje <MdTimer style={{ verticalAlign: "middle" }}/> i ukupno potrošene kalorije <MdLocalFireDepartment style={{ verticalAlign: "middle" }}/> .</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdPersonSearch /> Povezivanje s trenerom
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Na stranici <b>Odaberi trenera</b> pregledajte popis dostupnih trenera i kliknite <b>Poveži se</b> pokraj željenog trenera.</li>
          <li>Status zahtjeva (Čeka, Prihvaćeno, Odbijeno) jasno je prikazan uz svakog trenera.</li>
          <li>Možete biti povezani s jednim ili više trenera, ovisno o pravilima aplikacije.</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdPeopleAlt /> Upravljanje klijentima (za trenere)
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Na stranici <b>Moji klijenti</b> trener vidi sve klijente koji su poslali zahtjev ili su povezani.</li>
          <li>Zahtjev možete <b>prihvatiti</b> ili <b>odbijati</b> klikom na odgovarajuće gumbe.</li>
          <li>Odbijeni klijenti se više ne prikazuju na popisu.</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdAssignmentTurnedIn /> Planovi i dodjela planova
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Trener može kreirati planove i dodijeliti ih svojim klijentima na stranici <b>Dodijeli plan</b>.</li>
          <li>Klijent na stranici <b>Moji planovi</b> vidi sve planove koje mu je trener dodijelio, s nazivom, opisom, statusom i datumom dodjele.</li>
          <li>Plan se može dodijeliti samo klijentima s aktivnom vezom.</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdBarChart /> Praćenje napretka i grafovi
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Na početnoj stranici i u grafovima možete pratiti svoj dnevni i tjedni unos kalorija iz prehrane, potrošene kalorije kroz aktivnosti te ukupnu bilancu.</li>
          <li>Pregledajte BMI, cilj i napredak prema preporučenim vrijednostima.</li>
          <li>Koristite grafove za motivaciju i praćenje ostvarenja ciljeva kroz vrijeme.</li>
        </ul>
      </section>

      <section style={{ marginBottom: 28 }}>
        <h3 style={{ color: "#1bbfa5", fontWeight: 600, marginBottom: 10, display: "flex", alignItems: "center", gap: 7 }}>
          <MdOutlineLightbulb /> Savjeti i preporuke
        </h3>
        <ul style={{ margin: 0, paddingLeft: 22 }}>
          <li>Aplikacija prikazuje preporuke i upozorenja prema vašim unosima i ciljevima.</li>
          <li>Redovito pratite preporuke za poboljšanje prehrane i aktivnosti.</li>
          <li>Za optimalne rezultate, unosite podatke svakodnevno i kombinirajte prehranu s fizičkom aktivnošću.</li>
        </ul>
      </section>

    </div>
  </div>
);

export default HelpPage;
