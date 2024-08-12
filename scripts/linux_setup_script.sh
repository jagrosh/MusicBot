#!/bin/bash

echo -e "\e[93mJMusic Bot Setup\e[0m"

# Ask for user input
if command -v java > /dev/null; then
  echo -e "\033[32mJava is already installed.\033[0m"
else
  echo -e "\033[31mJava is not installed.\033[0m"
  read -p "Do you want to install Java now? [y/n]: " install_java
  if [ "$install_java" = "y" ]; then
    sudo apt-get update
    sudo apt install -y openjdk-18-jdk
  else
    echo "Exiting script..."
    exit 1
  fi
fi

read -p "Enter the name for the service: " service_name
service_name="$service_name.service"

if [ -n "$service_name" ]; then
    read -p "Enter the user the service will be executed as: " service_user
    read -p "Enter the group the service will be executed as: " service_group
fi

read -p "Enter a description for the service: " description

read -p "Enter the path to the JMusicBot files: " files_path
if [ -z "$files_path" ]; then
    echo -e "\e[91mFiles path cannot be empty. Exiting script...\e[0m"
    exit 1
fi

read -p "Enter the name of the config file (default: config.txt): " config_file
config_file=${config_file:-config.txt}

# Download the latest version of the app
 URL=$(curl -s https://api.github.com/repos/jagrosh/MusicBot/releases/latest \
           | grep -i "browser_download_url.*\.jar" \
           | sed 's/.*\(http.*\)"/\1/')
        FILENAME=$(echo "$URL" | sed 's/.*\/\([^\/]*\)/\1/')
        if [ -f "$FILENAME" ]; then
            echo "Latest version already downloaded (${FILENAME})"
        else
            curl -L "$URL" -o "$FILENAME"
        fi
    fi

# Run initial configuration
echo -e "\e[93mRunning initial configuration... PLEASE QUIT WITH CTRL+C AFTER INSERTING YOUR BOT TOKEN AND USER ID.\e[0m"
/usr/bin/java -Dnogui=true -Dconfig=$files_path/$config_file -jar $files_path/$FILENAME

echo -e "\e[32mInitial configuration completed.\e[0m"

# Create the service file if service_name is provided
if [ -n "$service_name" ]; then
    # Create/update the service file
    echo -e "[Unit]\nDescription=$description\nAfter=network.target network-online.target\n\n[Service]\nExecStart=/usr/bin/java -Dnogui=true -Dconfig=$files_path/$config_file -jar $files_path/$FILENAME\nType=simple\nUser=$service_user\nGroup=$service_group\nRestart=on-failure\nRestartSec=5\nStartLimitInterval=60s\nStartLimitBurst=3\n\n[Install]\nWantedBy=multi-user.target" > "/etc/systemd/system/$service_name"

    # Reload system manager configuration
    sudo systemctl daemon-reload

    # Show preview of service file
    echo -e "\e[93mPreview of $service_name:\e[0m"
    cat "/etc/systemd/system/$service_name"

    # Ask to enable and start the service
    read -p "Do you want to enable and start the service now? [y/n]: " start_service
    if [ "$start_service" = "y" ]; then
        sudo systemctl enable "$service_name"
        sudo systemctl start "$service_name"
        echo -e "\e[32mService enabled and started successfully.\e[0m"
    else
        echo -e "Service was not enabled or started."
    fi
fi

# Create/update the update script
echo -e "\e[93mCreating/updating the update script...\e[0m"
echo -e "#!/bin/bash\n\nGREEN=\"\\033[32m\"\nRED=\"\\033[31m\"\nYELLOW=\"\\033[33m\"\nWHITE=\"\\033[0m\"\n\nURL=\$(curl -s https://api.github.com/repos/jagrosh/MusicBot/releases/latest \\
       | grep -i browser_download_url.*\\.jar \\
       | sed 's/.*\\(http.*\\)\"/\\1/')\nFILENAME=\$(echo \$URL | sed 's/.*\\/\\([^\\/]*\\)/\\1/')\n\nif [ -f $files_path/\$FILENAME ]; then\n    echo -e \"\${GREEN}Latest version already downloaded (\$FILENAME)\${WHITE}\"\nelse\n    echo -e \"\${YELLOW}Stopping the service...\${WHITE}\"\n    sudo systemctl stop \"$service_name\"\n    curl -L \$URL -o $files_path/\$FILENAME\n    if [ -f \"/etc/systemd/system/$service_name\" ]; then\n        sed -i \"s|ExecStart=.*|ExecStart=/usr/bin/java -Dnogui=true -Dconfig=$files_path/config.txt -jar $files_path/\$FILENAME|\" \"/etc/systemd/system/$service_name\"\n        sudo systemctl daemon-reload\n        echo -e \"\${GREEN}Service file updated.\${WHITE}\"\n    fi\n    echo -e \"\${YELLOW}Starting the service...\${WHITE}\"\n    sudo systemctl start \"$service_name\"\nfi\n\necho -e \"\${YELLOW}Current version: \$FILENAME \${WHITE}\"\n" > "$files_path/update_jmusicbot.sh"
chmod +x "$files_path/update_jmusicbot.sh"

echo -e "\e[32mSetup completed.\e[0m"
