# FreshLaundry – Aplikasi Antar Jemput Laundry

**FreshLaundry** adalah aplikasi Android untuk layanan **antar-jemput laundry** yang memudahkan pelanggan dalam melakukan pemesanan, penjadwalan, dan pelacakan status secara real-time.  
Aplikasi ini juga dirancang untuk membantu operasional laundry agar lebih cepat, rapi, dan efisien.

---

## Fitur Utama

### 👤 Pelanggan
- Registrasi & login
- Pemesanan layanan laundry
- Pilih jadwal antar & jemput
- Menentukan lokasi menggunakan GPS ( Pembatasan Radius Max 30 KM dari lokasi Store Laundry )
- Melihat status pesanan secara real-time
- Chat dengan admin/kurir
- Notifikasi

### 🚚 Kurir
- Melihat daftar pesanan
- Menampilkan rute tercepat ke lokasi pelanggan
- Update status pengambilan & pengantaran
- Navigasi berbasis lokasi
- Chat dengan Pelanggan

### 🧑‍💼 Admin
- Kelola data pesanan
- Kelola data pelanggan & kurir
- Monitoring proses laundry
- Melihat aktivitas sistem secara real-time
- Chat dengan Pelanggan

---

## 🖼️ Tampilan Aplikasi (Halaman Pelanggan)

### Login & Register
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/login.jpg" width="240"/><br/>
      <b>Login</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/register.jpg" width="240"/><br/>
      <b>Register</b>
    </td>
  </tr>
</table>

---

### Form & Proses Pemesanan
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/formpemesanan.jpg" width="240"/><br/>
      <b>Form Pemesanan</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/pemesanan.jpg" width="240"/><br/>
      <b>Data Pemesanan</b>
    </td>
  </tr>
</table>

---

### Penentuan Lokasi & Jadwal
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/menentukantitikkoordinat.jpg" width="240"/><br/>
      <b>Titik Koordinat</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/setwaktupenjemputan.jpg" width="240"/><br/>
      <b>Set Waktu Penjemputan</b>
    </td>
  </tr>
</table>

---

### Pesanan Saya
<p align="center">
  <img src="assets/screenshots/pesanansaya.jpg" width="240"/><br/>
  <b>Pesanan Saya</b>
</p>

---

### Chat & Notifikasi
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/chat.jpg" width="240"/><br/>
      <b>Chat</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/notifikasi.jpg" width="240"/><br/>
      <b>Notifikasi</b>
    </td>
  </tr>
</table>

---

### Status Bar Notifikasi
<p align="center">
  <img src="assets/screenshots/notifstatusbar.jpg" width="240"/><br/>
  <b>Status Bar Notification</b>
</p>

---

## 🖼️ Tampilan Aplikasi (Admin & Kurir)

---

## Tampilan Admin

### Login Admin
<p align="center">
  <img src="assets/screenshots/loginadmin.jpg" width="240"/><br/>
  <b>Login Admin</b>
</p>

---

### Kelola & Konfirmasi Pesanan
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/kelolapesanan.jpg" width="240"/><br/>
      <b>Kelola Pesanan</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/konfirmasipesanan.jpg" width="240"/><br/>
      <b>Konfirmasi Pesanan</b>
    </td>
  </tr>
</table>

---

### Chat Admin dengan Pelanggan
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/listchatpelanggandiadmin.jpg" width="240"/><br/>
      <b>List Chat Pelanggan</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/chatdenganpelanggan.jpg" width="240"/><br/>
      <b>Chat dengan Pelanggan</b>
    </td>
  </tr>
</table>

---

### Laporan
<p align="center">
  <img src="assets/screenshots/laporan.jpg" width="240"/><br/>
  <b>Laporan</b>
</p>

---

## Tampilan Kurir

### Daftar Penjemputan
<p align="center">
  <img src="assets/screenshots/kurirlistpenjemputan.jpg" width="240"/><br/>
  <b>List Penjemputan Kurir</b>
</p>

---

### Navigasi Kurir
<p align="center">
  <img src="assets/screenshots/navigasikurir.jpg" width="240"/><br/>
  <b>Navigasi Kurir</b>
</p>


## Sistem Navigasi

Aplikasi ini menggunakan:
- **GPS** untuk menentukan posisi pelanggan dan kurir
- **Algoritma A-Star (A\*)** untuk menghitung rute tercepat
- Perhitungan rute dilakukan berdasarkan koordinat lokasi

---

## Teknologi yang Digunakan

- **Android (Native)**  
- **Firebase Realtime Database**  
- **Firebase Authentication**
- **FCM**
- **GPS / Location Service**
- **Algoritma A-Star (A\*)**
- **OpenStreetMap**

---

## Download Aplikasi

FreshLaundry terdiri dari **2 aplikasi Android terpisah**

---

### FreshLaundry – Aplikasi Pelanggan

<p align="center">
   <a href="https://drive.google.com/file/d/1EnAGuutx6ny9vrv4daoWonfO8RwJvMG8/view?usp=sharing" target="_blank">
    <b>Download APK FreshLaundry Pelanggan</b>
  </a>
</p>

---

### FreshLaundry – Aplikasi Admin & Kurir

<p align="center">
   <a href="https://drive.google.com/file/d/1UTPqvDqdEq12h5cAxYyKnEFl_5bXkfAz/view?usp=sharing" target="_blank">
    <b>Download APK FreshLaundry Admin & Kurir</b>
  </a>
</p>

---

> ⚠️ **Catatan Instalasi**
> - Aktifkan **Install from Unknown Sources** di pengaturan Android

## License

This project is licensed under **All Rights Reserved**.

The APK file is provided for **demo and personal use only**.
All source code, assets, and documentation in this repository are the exclusive property of the author.  
No part of this project may be copied, modified, or used for commercial purposes without explicit permission.

© 2026 Zainul Muhajir
