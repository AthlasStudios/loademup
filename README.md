# LoadEmUp
### ⚡ Safe, Smart, and Seamless Plugin Reloading for Spigot Servers

**LoadEmUp** is a powerful plugin manager designed to safely load, unload, enable, disable, and reload individual plugins on Spigot servers, even in production environments. With built-in security scans, dependency checks, and integrity validation, you can trust LoadEmUp to keep your server stable while making changes on the fly.

[![Download on Modrinth](https://img.shields.io/badge/Download%20on-Modrinth-blue?logo=modrinth)](https://modrinth.com/plugin/loademup)

## 🚀 Features

- 🛡️ **Virus & Threat Scanning**  
  Automatically inspects plugins for known signatures and suspicious behavior before reloading.

- 🔗 **Dependency Awareness**  
  Detects plugin dependencies and dependants, ensuring nothing breaks when you reload.

- 🧩 **Integrity Checks**  
  Verifies plugin structure, metadata, and file consistency to prevent corrupted or broken reloads.

- ✅ **Safe Reload Execution**  
  Only proceeds with the reload if all safety checks pass, giving you production-level confidence.

- ⚡ **Full Plugin Lifecycle Management**  
  Load new plugins, unload unwanted plugins, enable or disable plugins dynamically without server restarts.

## 📦 Installation

1. Download the latest `.jar` file from the [Modrinth LoadEmUp page](https://modrinth.com/plugin/loademup).
2. Drop it into your server's `plugins` folder.
3. Restart your server or use `/reload` (if you're brave 😅).
4. You're good to go!

## 💻 Commands

| Command                             | Description                       | Permission Node            |
|-------------------------------------|-----------------------------------|----------------------------|
| `/loademup help`                    | View this help message            | `loademup.command`         |
| `/loademup load <plugin>`           | Load a new plugin                 | `loademup.command.load`    |
| `/loademup unload <plugin> [flags]` | Unload a plugin                   | `loademup.command.unload`  |
| `/loademup reload <plugin> [flags]` | Reload a plugin                   | `loademup.command.reload`  |
| `/loademup enable <plugin>`         | Enable a plugin                   | `loademup.command.enable`  |
| `/loademup disable <plugin>`        | Disable a plugin                  | `loademup.command.disable` |
| `/loademup scan <plugin>`           | Scan a plugin for viruses/threats | `loademup.command.scan`    |
| `/loademup info <plugin>`           | Get plugin info                   | `loademup.command.info`    |

## ⚙️ Permissions

| Permission Node            | Description                          |
|----------------------------|--------------------------------------|
| `loademup.*`               | Full access to all LoadEmUp commands |
| `loademup.command.load`    | Allows loading plugins               |
| `loademup.command.unload`  | Allows unloading plugins             |
| `loademup.command.reload`  | Allows reloading plugins             |
| `loademup.command.enable`  | Allows enabling plugins              |
| `loademup.command.disable` | Allows disabling plugins             |
| `loademup.command.scan`    | Allows scanning plugins              |
| `loademup.command.info`    | Allows viewing plugin information    |

## 🛠️ Requirements

- Spigot 1.13+ (may work on older versions but not officially supported)
- Java 17+

## 🧩 Plugin Compatibility

LoadEmUp is designed to work alongside most modern plugins. However, plugins with complex stateful behavior may not fully support runtime reloads. Always check integrity status before applying reloads in production.

## 📚 Documentation

For detailed guides, configuration help, and FAQs, visit the [LoadEmUp Wiki](https://gurwiworks.gitbook.io/loademup-wiki).

## 📢 Contributing

Got a feature request or found a bug? Open an [issue](https://github.com/AthlasStudios/LoadEmUp/issues) or join our [Discord community](https://dev.athlas.club) to report bugs and discuss features! Contributions are welcome.

## 📜 License

MIT License – see [LICENSE](LICENSE) file for details.

## ❤️ Support

If you like this project, give it a ⭐ on GitHub and share it with your server community!
