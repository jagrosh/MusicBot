/*
 * Copyright 2021 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.Permission;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ClearCmd extends MusicCommand {
	public ClearCmd(Bot bot) {
		super(bot);
		this.name = "clear";
		this.help = "removes all songs from the queue";
		this.arguments = "";
		this.aliases = bot.getConfig().getAliases(this.name);
		// this.beListening = true;
		// this.bePlaying = true;
	}

	@Override
	public void doCommand(CommandEvent event) {
		AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
		if (handler.getQueue().isEmpty()) {
			event.replyError("There is nothing in the queue!");
			return;
		}

		Settings settings = event.getClient().getSettingsFor(event.getGuild());
		boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
		if (!isDJ) {
			isDJ = event.getMember().getRoles().contains(settings.getRole(event.getGuild()));
		}
		
		if(isDJ) {
			int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
			if (count == 0) {
				event.replyWarning("You don't have any songs in the queue!");
			} else {
				event.replySuccess("Successfully removed your " + count + " entries.");
			}
			if(handler.isMusicPlaying(bot.getJDA())) {
				handler.getPlayer().stopTrack();
			}
		}else {
			event.replyError("You do not have the required permissions to perform this command.");
		}

	}
}
