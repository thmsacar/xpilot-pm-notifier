# xPM - xPilot Message Monitor

xPM is a simple tool that sends your **xPilot Private Messages** directly to your phone via **Telegram**. It's designed for VATSIM pilots who don't want to stare at the screen for hours just to catch a "Contact Me" or a PM from ATC.

### Why xPM?
If you use vPilot on Windows, you have plenty of options for notifications because those tools can "inject" themselves into the app. But for **macOS users** and **xPilot** flyers, we were out of luck.

Since xPilot doesn't allow easy injection on Mac, I built xPM to work differently. It simply watches your log files in the background. It’s **Zero-Injection**, meaning it doesn't touch your game files or memory. It just reads what xPilot writes and pushes it to your pocket.

### Setup

#### 1. Find your xPilot Path
- Open **xPilot**.
- Type `.appdata` in the chat/command box and hit Enter.
- Copy the path of the folder that opens. Ensure you see a folder named **NetworkLogs** inside—this confirms you are in the right place.

#### 2. Create your Telegram Bot
- Find [@BotFather](https://t.me/botfather) on Telegram, send `/newbot` and get your **API Token**. It should look like a long string of numbers and letters with a colon in the middle (e.g., `12345:AAHtN...`). Make sure to copy the entire thing.
- Find [@userinfobot](https://t.me/userinfobot) to get your **Chat ID**.
- **Important:** Open your new bot and click **Start**. The bot can't message you until you do this.

#### 3. Run it
- Launch the `.jar` file and paste your Path, Token, and ID.
- **Make sure xPilot is running and you are connected to VATSIM.** - Hit **Start Monitoring** and you're good to go.
- **Important:** Keep xPM open in the background. It will only monitor and send notifications as long as the application is running.

### Good to know
- **Safe:** No DLLs, no hooks, no risk of crashing your sim.
- **Secure:** This tool only reads local text logs and does not interfere with the xPilot process or memory. It is 100% compliant with VATSIM rules, so there is no risk to your account.
- **Multi-platform:** Since it's built with Java, it runs on Windows, macOS.