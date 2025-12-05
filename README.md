# Java Task Management

## Deskripsi

Java Task Management adalah aplikasi desktop untuk pengelolaan tugas dan proyek yang dibangun menggunakan **JavaFX**. Aplikasi ini dirancang untuk membantu pengguna mengorganisir pekerjaan mereka dalam struktur Organisasi dan Proyek, dengan visualisasi **Kanban Board** yang interaktif untuk pelacakan status tugas.

## Teknologi yang Digunakan

- **Bahasa Pemrograman**: Java 17+
- **Framework UI**: JavaFX
- **Build Tool**: Maven
- **Database**: MySQL
- **Styling**: CSS

## Design Patterns

Aplikasi ini menerapkan berbagai Design Pattern untuk memastikan kode yang bersih, modular, dan mudah dipelihara:

1.  **MVC (Model-View-Controller)**: Memisahkan representasi data (Model), antarmuka pengguna (View), dan logika kontrol (Controller/Service).
2.  **DAO (Data Access Object)**: Mengenkapsulasi logika akses database, memisahkan operasi SQL dari logika bisnis.
3.  **Singleton**: Digunakan pada `UserSession`, `AppState`, dan kelas-kelas Service (`TaskService`, `ProjectService`, `OrganizationService`) untuk memastikan hanya ada satu instance yang berjalan selama aplikasi aktif.
4.  **Observer**: Menerapkan pola Subject-Observer pada View dan Service. View (Observer) akan otomatis diperbarui ketika data pada Service (Subject) berubah.
5.  **Factory**: `ViewFactory` digunakan untuk sentralisasi pembuatan dan manajemen instance View.
6.  **Builder**: `TaskBuilder` digunakan untuk menyederhanakan pembuatan objek `Task` yang kompleks.

## Fitur Utama

- **Autentikasi**: Sistem Login dan Register yang aman.
- **Manajemen Organisasi**:
  - Membuat Organisasi baru.
  - Bergabung ke Organisasi menggunakan **Kode Unik**.
  - Validasi untuk mencegah bergabung ke organisasi milik sendiri.
- **Manajemen Proyek**:
  - Membuat Proyek di dalam Organisasi.
  - Melihat daftar proyek dan progress pengerjaannya.
- **Kanban Board**:
  - Visualisasi tugas dalam kolom: **Todo**, **In Progress**, **Done**.
  - **Drag & Drop** tugas untuk mengubah status secara instan.
  - Menambah, mengedit, dan menghapus tugas.
- **Dashboard Interaktif**: Ringkasan aktivitas organisasi dan proyek dengan indikator progress.
- **Keamanan Sesi**: Pembersihan data otomatis saat Logout untuk menjaga privasi antar pengguna.

## Cara Menjalankan

1.  Pastikan **Java JDK 17** (atau lebih baru) dan **Maven** sudah terinstal di sistem Anda.
2.  Pastikan layanan **MySQL** berjalan dan database telah dikonfigurasi.
3.  Buka terminal di direktori root proyek.
4.  Jalankan perintah berikut untuk membangun dan menjalankan aplikasi:
    ```bash
    mvn clean javafx:run
    ```

## Kontributor

- **MaulIshak**
