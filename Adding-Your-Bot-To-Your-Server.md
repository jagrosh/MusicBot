**If you don't have a bot application created, please reference [[Getting a Bot Token]] for how to set one up!**

1. Navigate to the [Applications page](https://discordapp.com/developers/applications) and select one of your bot applications.  
![App Page](https://i.imgur.com/Uo1si8f.png)

2. In the Settings list, select **OAuth2**.  
![OAuth2](https://i.imgur.com/SLHu8Ax.png)

3. In the **Scopes** section, check **Bot**.  
![Scopes](https://i.imgur.com/V0JnmIu.png)

4. Click **Copy** to copy the OAuth2 URL to your clipboard.  
![Copy](https://i.imgur.com/OWxYMxM.png)

5. Paste the link into your browser.  
![Paste](https://i.imgur.com/vprVIgc.png)

6. Select a server from the drop-down menu and then click **Authorize**. **You must have the Manage Server permission to add a bot to a server!** If no servers appear, you may need to [log in](https://discordapp.com/login).  
![Select Server](https://i.imgur.com/gE2nULG.png)


## Troubleshooting
* If you get a "Requires Code Grant" error, make sure that this box is **unchecked** on your application: <br>![Code Grant](http://i.imgur.com/5uOq0Ad.png)
* If you want to generate the link manually, replace the CLIENTID in the following link with your bot's client ID:
```
https://discordapp.com/oauth2/authorize?client_id=CLIENTID&scope=bot
```