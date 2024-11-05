# Camunda BPMN Editor Plugin for IntelliJ IDEA

This project is a BPMN Editor plugin for IntelliJ IDEA. It provides a custom editor for BPMN files with features such as Camunda BPMN Modeler UI, validation, and integration with the system clipboard.

## Features

- Custom BPMN file editor (Classic BPMN Modeler, Camunda 7 and Camunda 8)
- Integration with system clipboard
- For Camunda 7 added working with 'inline script' in external script editor
- Supported *.bpmn and *.bpmn20.xml files
- Color themes

## Requirements

- IntelliJ IDEA 2023.2.6 or later
- JDK 17 or later

## Getting Started

### Building the Project

To build the project, follow these steps:

1. **Clone the repository:**

    ```sh
    git clone https://github.com/yourusername/bpmn-editor-plugin.git
    cd bpmn-editor-plugin
    ```

2. **Open the project in IntelliJ IDEA:**

    - Open IntelliJ IDEA.
    - Select `File > Open...` and choose the `bpmn-editor-plugin` directory.

3. **Configure the JDK:**

    - Go to `File > Project Structure...`.
    - In the `Project` section, set the `Project SDK` to JDK 17 or later.

4. **Build the project:**

    - ```sh
       ./gradlew clean build
       ```

### Installing the Plugin

To install the plugin in IntelliJ IDEA, follow these steps:

1. **Prepare the plugin .zip file:**

    -  ```sh
       ./gradlew clean build buildPlugin
       ```
    - This will generate a .zip file in the `build/distributions` directory.

2. **Install the plugin in IntelliJ IDEA:**

    - Open IntelliJ IDEA.
    - Go to `File > Settings...` (or `IntelliJ IDEA > Preferences...` on macOS).
    - Select `Plugins` from the left-hand menu.
    - Click on the gear icon at the top right and select `Install Plugin from Disk...`.
    - Navigate to the `build/distributions` directory and select the generated .zip file.
    - Click `OK` and restart IntelliJ IDEA (if you need) when prompted.

## Usage

Once the plugin is installed, you can use it to open and edit BPMN files:

1. **Open a BPMN file:**

    - In IntelliJ IDEA, open a project that contains BPMN files.
    - Double-click on a BPMN file to open it in the custom BPMN editor.

2. **Configure the editor settings:**

    - Go to `File > Settings...` (or `IntelliJ IDEA > Preferences...` on macOS).
    - Select `Tools > Camunda BPMN Editor Settings` from the left-hand menu.
    - Configure the color theme and other settings as desired.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author

Oleksandr Havrysh

## Acknowledgements

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Camunda BPMN Modeler](https://github.com/camunda/camunda-modeler)