# NGP Admin App 📢

An Android application built for college administrators to manage notices, images, PDFs, and faculty records — all in one place, powered by Firebase.

---

## 📱 Screenshots

><img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 23 33 31" src="https://github.com/user-attachments/assets/8802eda1-6637-418d-a20a-16810bbdee47" />
<img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 23 33 31 (1)" src="https://github.com/user-attachments/assets/c9f0c78d-815a-4781-a117-fa650d42498e" />
<img width="720" height="1600" alt="WhatsApp Image 2026-06-12 at 23 33 32" src="https://github.com/user-attachments/assets/ed78a729-e270-47db-8ce8-744aa599692c" />


---

## 🚀 Features

| Feature | Description |
|---|---|
| 📋 Upload Notice | Post text notices with a title instantly to Firebase |
| 🖼️ Upload Image | Upload categorized images for students to view |
| 📄 Upload PDF | Share PDF documents (timetables, circulars, etc.) |
| 👥 Add Faculty | Add and manage faculty member records |
| 🗑️ Delete Notice | Remove outdated notices from the system |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Platform:** Android (Android Studio)
- **Backend & Database:** Firebase Realtime Database
- **Storage:** Firebase Storage
- **Min SDK:** 21 (Android 5.0+)

---

## 🔧 How to Run Locally

1. Clone the repository
   ```bash
   git clone https://github.com/Simonarti/CollegeAdminApp.git
   ```
2. Open the project in **Android Studio**
3. Connect your own Firebase project:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project
   - Download `google-services.json` and place it in the `/app` folder
4. Build and run on an emulator or physical Android device

---

## 📦 Download APK

👉 [Download latest APK](../../releases/latest)

> Install on any Android device (enable "Install from unknown sources" in settings)

---

## 🧠 What I Learned

- Structuring Firebase Realtime Database for a real-world admin use case
- Handling Firebase async callbacks correctly with Android activity lifecycle
- Debugging app crashes caused by loading Firebase data before the UI was fully initialized
- Building for real users — thinking about edge cases, not just happy paths

---

## 🗺️ Roadmap

- [ ] Admin login / authentication
- [ ] Student-facing companion app
- [ ] Push notifications when a new notice is posted
- [ ] Notice categories and filters

---

## 👤 Author

**Your Name**
- GitHub: [@Simonarti](https://github.com/Simonarti)

---

