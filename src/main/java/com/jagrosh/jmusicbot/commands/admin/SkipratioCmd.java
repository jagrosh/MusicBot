/*
 * Copyright 2023 まったりにほんご
 * 
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SkipratioCmd extends AdminCommand {
    public SkipratioCmd(Bot bot) {
        this.name = "setskip";
        this.help = "サーバー固有の必要スキップ投票割合を指定します。";
        this.arguments = "<0 - 100>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            int val = Integer
                    .parseInt(event.getArgs().endsWith("%") ? event.getArgs().substring(0, event.getArgs().length() - 1)
                            : event.getArgs());
            if (val < 0 || val > 100) {
                event.replyError("指定する値は0から100の間でなければなりません。");
                return;
            }
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            s.setSkipRatio(val / 100.0);
            event.replySuccess("スキップ投票割合は `" + val + "%` of listeners on *" + event.getGuild().getName() + "*");
            event.replySuccess("*" + event.getGuild().getName() + "*" + "のスキップ投票割合は`" + val + "%`になりました。");
        } catch (NumberFormatException ex) {
            event.replyError("0から100の間の整数を指定してください（既定値は55です）。この数値は、曲をスキップするために投票する必要があるリスナーの割合です。");
        }
    }
}
