---
title: Adding Your Bot To Your Server
description: "Instructions for how to add your Discord bot to your Discord server"
---

!!! help "Don't have an application?"
    If you don't have a bot application created, please reference [Getting a Bot Token](getting-a-bot-token.md) for how to set one up!

1. Navigate to the [Applications page](https://discordapp.com/developers/applications) and select one of your bot applications.  
![App Page](/assets/images/app-page.png)

2. In the Settings list, select **OAuth2**.  
![OAuth2](/assets/images/oauth.png)

3. In the **Scopes** section, check **Bot**.  
![Scopes](/assets/images/scopes.png)

4. Click **Copy** to copy the OAuth2 URL to your clipboard.  
![Copy](/assets/images/oauth-url.png)

5. Paste the link into your browser.  
![Paste](/assets/images/browser.png)

6. Select a server from the drop-down menu and then click **Authorize**. **You must have the Manage Server permission to add a bot to a server!** If no servers appear, you may need to [log in](https://discordapp.com/login).  
![Select Server](/assets/images/invite.png)


## Troubleshooting
* If you get a "Requires Code Grant" error, make sure that this box is **unchecked** on your application: <br>![Code Grant](/assets/images/code-grant.png)
* If you want to generate the link manually, replace the CLIENTID in the following link with your bot's client ID:
```
https://discordapp.com/oauth2/authorize?client_id=CLIENTID&scope=bot
```