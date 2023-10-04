/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.general;

import java.time.temporal.ChronoUnit;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;

/**
 *
 * @author John Grosh (jagrosh)
 */
@CommandInfo(name = { "Ping", "Pong" }, description = "ボットのピングを確認する。")
@Author("John Grosh (jagrosh)")
public class PingCmd extends Command {

    public PingCmd() {
        this.name = "ping";
        this.help = "ボットのピングを確認する。";
        this.guildOnly = false;
        this.aliases = new String[] { "pong" };
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("ピング: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("ポング！\n Ping: " + ping + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
    }

}
