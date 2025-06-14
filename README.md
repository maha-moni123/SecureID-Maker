# 🔐 SecureID Maker

**SecureID Maker** is a Java-based mini-project that simplifies user credential management by providing automatic **User ID** and **Password** generation.

Built using **AWT (Abstract Window Toolkit)** for the GUI and **JDBC (Java Database Connectivity)** for database operations, this application enables users to create and manage secure login credentials with ease.

---

## 📌 Features

✨ **Auto User ID Generator**  
Click a button to instantly generate a unique User ID for signup.

✨ **Auto Password Generator**  
Generate a strong, random password with a single click.

📝 **Signup and Login**  
Users can sign up using the generated credentials and log in using the same.

🔒 **Integrated Password Generator Tool**  
A standalone password generator tool is embedded within the app.

💾 **Database Integration**  
User details are stored securely using JDBC with a PostgreSQL database.

---

## 🛠️ Tech Stack

- **Java AWT** — for building the graphical user interface  
- **JDBC** — for database connectivity and queries  
- **PostgreSQL** — as the backend database for storing user credentials

---

## 📖 How to Run

### 1️⃣ Clone the repository

```bash
git clone https://github.com/yourusername/SecureIDMaker.git
Create a database named: userid

### Run the following SQL to create the users table:
CREATE TABLE users (
    username VARCHAR(100),
    userid VARCHAR(20) PRIMARY KEY,
    password VARCHAR(255)
);
## 🎥 Demo

Watch the video below to see how SecureID Maker works:

[![SecureID Maker Demo](https://drive.google.com/file/d/1NodJ27lsWpkycE6J93QhsbUnCF9haid-/view?usp=sharing)

> This video shows how to generate a User ID and Password, and how signup and login functionalities work.
