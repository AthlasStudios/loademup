# LoadEmUp
### ⚡ Safe, Smart, and Seamless Plugin Reloading for Spigot Servers

**LoadEmUp** is a powerful plugin manager designed to safely reload individual plugins on Spigot servers — even in production environments. With built-in security scans, dependency checks, and integrity validation, you can trust LoadEmUp to keep your server stable while making changes on the fly.

---

## 🚀 Features

- 🛡️ **Virus & Threat Scanning**  
  Automatically inspects plugins for known signatures and suspicious behavior before reloading.

- 🔗 **Dependency Awareness**  
  Detects plugin dependencies and dependants, ensuring nothing breaks when you reload.

- 🧩 **Integrity Checks**  
  Verifies plugin structure, metadata, and file consistency to prevent corrupted or broken reloads.

- ✅ **Safe Reload Execution**  
  Only proceeds with the reload if all safety checks pass, giving you production-level confidence.

---

## 📦 Installation

1. Download the latest `.jar` file from the [Releases](https://github.com/your-username/loademup/releases) page.
2. Drop it into your server's `plugins` folder.
3. Restart your server or use `/reload` (if you're brave 😅).
4. You're good to go!

---

## 💻 Commands

| Command                     | Description                                     |
|-----------------------------|-------------------------------------------------|
| `/loademup reload <plugin>` | Safely reload a plugin using all checks         |
| `/loademup scan <plugin>`   | Scan a plugin for viruses and threats           |
| `/loademup status <plugin>` | Show reload readiness, dependencies, and status |

---

## ⚙️ Permissions

| Permission Node     | Description                          |
|---------------------|--------------------------------------|
| `loademup.reload`   | Allows reloading plugins             |
| `loademup.scan`     | Allows scanning plugins              |
| `loademup.admin`    | Full access to all LoadEmUp features |

---

## 🛠️ Requirements

- Spigot 1.13+ (may work on older versions but not officially supported)
- Java 11+

---

## 🧩 Plugin Compatibility

LoadEmUp is designed to work alongside most modern plugins. However, plugins with complex stateful behavior may not fully support runtime reloads. Always check integrity status before applying reloads in production.

---

## 📢 Contributing

Got a feature request or found a bug? Open an [issue](https://github.com/AthlasStudios/LoadEmUp/issues) or submit a pull request! Contributions are welcome.

---

## 📜 License

MIT License – see [LICENSE](LICENSE) file for details.

---

## ❤️ Support

If you like this project, give it a ⭐ on GitHub and share it with your server community!
