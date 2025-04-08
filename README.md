# Mail Helper
A Java-based utility to interact with Gmail via IMAP, extract email content, and manage messages.

## Features
- Connects to Gmail using IMAP

- Downloads email content (Subject, Sender, Content)

- Prints the SPAM email content into the console

## Installation
Clone the repository:

```bash
git clone https://github.com/devpuppet/mail-helper.git
```

```bash
cd mail-helper
```
Add your Gmail credentials in the `.env` file (see `example.env` for format).

Build the project:

```bash
mvn clean install
```

## Configuration
Make sure to enable IMAP in your Gmail account settings.

Create an App Password if using 2-step verification.

## License
This project is licensed under the MIT License.