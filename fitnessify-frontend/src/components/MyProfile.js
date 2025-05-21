import React, { useEffect, useState } from "react";
import axiosInstance from "../axiosInstance";
import {
  MdPerson,
  MdMail,
  MdTransgender,
  MdCake,
  MdHeight,
  MdMonitorWeight,
  MdFlag,
  MdSchool,
  MdWork,
  MdDateRange,
  MdEdit,
  MdSave,
  MdCancel
} from "react-icons/md";
import "../style/MyProfile.css";

const initialEditState = {
  ime: "",
  prezime: "",
  dob: "",
  spol: "",
  visina: "",
  tezina: "",
  cilj: "",
  strucnost: "",
  godineIskustva: "",
};

const MyProfile = () => {
  const [profile, setProfile] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState(initialEditState);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [role, setRole] = useState(localStorage.getItem("role") || "");

  useEffect(() => {
    axiosInstance.get("/user/me")
      .then(res => {
        setProfile(res.data);
        setForm({ ...initialEditState, ...res.data });
        if (res.data.role) {
          setRole(res.data.role);
          localStorage.setItem("role", res.data.role);
        }
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleEdit = () => setEditMode(true);

  const handleCancel = () => {
    setEditMode(false);
    setForm({ ...form, ...profile });
    setMessage("");
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setMessage("");
    try {
      await axiosInstance.patch("/user/me", form);
      setProfile({ ...profile, ...form });
      setEditMode(false);
      setMessage("Podaci su uspješno ažurirani!");
    } catch {
      setMessage("Greška prilikom ažuriranja podataka.");
    }
  };

  if (loading) return <div className="profile-bg"><div className="profile-card clean">Učitavanje...</div></div>;
  if (!profile) return <div className="profile-bg"><div className="profile-card clean">Nema podataka.</div></div>;

  const roleDisplay = role === "KLIJENT" ? "Klijent" : role === "TRENER" ? "Trener" : "Korisnik";

  return (
    <div className="profile-bg">
      <div className="profile-card clean">
        <div className="profile-basic-info">
          <div className="profile-title"><MdPerson className="profile-icon" />{profile.ime} {profile.prezime}</div>
          <div className="profile-email"><MdMail className="profile-icon" />{profile.email}</div>
          <div className="profile-role"><MdFlag className="profile-icon" />{roleDisplay}</div>
        </div>
        <form className="profile-form" onSubmit={handleSave} autoComplete="off">
          <div className="profile-grid wide">
            <div>
              <label><MdPerson className="profile-icon" />Ime</label>
              {editMode ?
                <input name="ime" value={form.ime} onChange={handleChange} required /> :
                <div className="profile-value">{profile.ime}</div>
              }
            </div>
            <div>
              <label><MdPerson className="profile-icon" />Prezime</label>
              {editMode ?
                <input name="prezime" value={form.prezime} onChange={handleChange} required /> :
                <div className="profile-value">{profile.prezime}</div>
              }
            </div>
            <div>
              <label><MdTransgender className="profile-icon" />Spol</label>
              {editMode ?
                <select name="spol" value={form.spol} onChange={handleChange} required>
                  <option value="">Odaberi</option>
                  <option value="Zensko">Žensko</option>
                  <option value="Musko">Muško</option>
                </select>
                : <div className="profile-value">{profile.spol}</div>
              }
            </div>
            <div>
              <label><MdCake className="profile-icon" />Dob</label>
              {editMode ?
                <input name="dob" type="number" value={form.dob} onChange={handleChange} required /> :
                <div className="profile-value">{profile.dob}</div>
              }
            </div>
            {role === "KLIJENT" && (
              <>
                <div>
                  <label><MdHeight className="profile-icon" />Visina (cm)</label>
                  {editMode ?
                    <input name="visina" type="number" value={form.visina || ""} onChange={handleChange} /> :
                    <div className="profile-value">{profile.visina} cm</div>
                  }
                </div>
                <div>
                  <label><MdMonitorWeight className="profile-icon" />Težina (kg)</label>
                  {editMode ?
                    <input name="tezina" type="number" value={form.tezina || ""} onChange={handleChange} /> :
                    <div className="profile-value">{profile.tezina} kg</div>
                  }
                </div>
                <div>
                  <label><MdFlag className="profile-icon" />Cilj</label>
                  {editMode ?
                    <input name="cilj" value={form.cilj || ""} onChange={handleChange} /> :
                    <div className="profile-value">{profile.cilj}</div>
                  }
                </div>
              </>
            )}
            {role === "TRENER" && (
              <>
                <div>
                  <label><MdSchool className="profile-icon" />Stručnost</label>
                  {editMode ?
                    <input name="strucnost" value={form.strucnost || ""} onChange={handleChange} /> :
                    <div className="profile-value">{profile.strucnost}</div>
                  }
                </div>
                <div>
                  <label><MdWork className="profile-icon" />Godine iskustva</label>
                  {editMode ?
                    <input name="godineIskustva" type="number" value={form.godineIskustva || ""} onChange={handleChange} /> :
                    <div className="profile-value">{profile.godineIskustva}</div>
                  }
                </div>
              </>
            )}
            <div>
              <label><MdDateRange className="profile-icon" />Datum registracije</label>
              <div className="profile-value">{profile.datumRegistracije}</div>
            </div>
          </div>
          {editMode ? (
            <div className="profile-btns">
              <button type="submit" className="profile-btn"><MdSave className="profile-icon" /> Spremi</button>
              <button type="button" className="profile-btn cancel" onClick={handleCancel}><MdCancel className="profile-icon" /> Odustani</button>
            </div>
          ) : (
            <button type="button" className="profile-btn" onClick={handleEdit}><MdEdit className="profile-icon" /> Uredi profil</button>
          )}
          {message && <div className="profile-message">{message}</div>}
        </form>
      </div>
    </div>
  );
};

export default MyProfile;
