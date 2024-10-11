---
title: Signing into YouTube
description: "Information regarding signing into a Google Account, to help with YouTube playback"
---

## Foreword

You can give JMusicBot access your Google account to help with
playing back YouTube tracks.

This is only need to sign in if you wish to play age-restricted videos,
or are experiencing the "Sign in to confirm you're not a bot" error.
Outside of that, signing in is unnecessary.    

!!! danger

    **DO NOT USE YOUR MAIN GOOGLE ACCOUNT!**

    Your Google account can get terminated, as this is breaking
    Google's Terms of Service.

    **CREATE A NEW GOOGLE ACCOUNT FOR JMUSICBOT!**

## Setting up

1. Enable `youtubeoauth2=true` in your `config.txt`.
    - You may already have `youtubeoauth2=false` somewhere in your config.
      If you do, you can replace `false` with `true`.
    - Otherwise, create a new line anywhere in your config with
      `youtubeoauth2=true`.

2. Restart JMusicBot.

3. JMusicBot will now send the following
   to the owner's direct messages and to the console:
    ```
    [INFO] [YoutubeOauth2Handler]: ==================================================
    [INFO] [YoutubeOauth2Handler]: !!! DO NOT AUTHORISE WITH YOUR MAIN ACCOUNT, USE A BURNER !!!
    [INFO] [YoutubeOauth2Handler]: OAUTH INTEGRATION: To give youtube-source access to your account, go to https://www.google.com/device and enter code GXN-YJC-BQNY
    [INFO] [YoutubeOauth2Handler]: !!! DO NOT AUTHORISE WITH YOUR MAIN ACCOUNT, USE A BURNER !!!
    [INFO] [YoutubeOauth2Handler]: ==================================================
    ```

    ![Direct Message from JMusicBot on Discord](/assets/images/youtube-oauth2-dm.png)

4. Head to the shown URL (https://www.google.com/device)
   and enter the code prompted by JMusicBot:

    ![Screenshot of Google's page with the code filled in](/assets/images/youtube-oauth2-code.png)

5. Select your Google account

    !!! danger

        Reminder, **DO NOT USE YOUR MAIN GOOGLE ACCOUNT FOR SIGNING IN!**

        Please make sure you are using a new Google account!

    ![Screenshot of Google's choose an account page](/assets/images/youtube-oauth2-choose-account.png)

6. In the authorisation page, click "Allow"
   to authorise JMusicBot to your Google account.

    ![Screenshot of Google's authorisation page](/assets/images/youtube-oauth2-authorisation.png)


After the authorisation has completed, the following message should get logged
to the console a few seconds later:

`[INFO] [YoutubeOauth2TokenHandler]: Authorization successful & retrieved token! Storing the token in /path/to/jmusicbot/youtubetoken.txt`

JMusicBot is now signed in and will store the credentials
in a new `youtubetoken.txt` file. 

## Signing out

To sign out, manually delete the `youtubetoken.txt` and restart JMusicBot.

Additionally, you may want to go to
[Your Google Account -> Security -> See all connections](https://myaccount.google.com/connections)
to remove the "YouTube on TV" connection.
Note that this will also sign out any TV you were signed in to with that
Google account.

![Screenshot of Google's connections page](/assets/images/youtube-oauth2-connections.png)

![Screenshot of YouTube on TV connection page, highlighting the "Delete all connections you have with YouTube on TV" button](/assets/images/youtube-oauth2-connections-youtube-tv.png)