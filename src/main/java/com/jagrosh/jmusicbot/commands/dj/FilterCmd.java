/*
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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.filter.*;
import com.jagrosh.jmusicbot.commands.FilterCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FilterCmd extends FilterCommand {
    public FilterCmd(Bot bot) {
        super(bot);
        this.name = "filter";
        this.help = "penis";
        this.aliases = new String[]{"f"};
        this.bePlaying = false;
        this.children = new Command[]{
                new FilterStack(bot),
                new FilterTimescale(bot),
                new FilterLowPass(bot),
                new FilterVolume(bot),
                new FilterBitCrush(bot),
                new FilterPitch(bot),
                new FilterKaraoke(bot),
                new FilterUndo(bot),
                new FilterRemove(bot),
                new FilterReset(bot)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {
        StringBuilder sb = new StringBuilder("**Filter Commands:**\n");
        for (Command cmd : this.children) {
            sb.append("`").append(event.getClient().getPrefix()).append(this.getName()).append(" ").append(cmd.getName());
            if (cmd.getArguments() != null)
                sb.append(" ").append(cmd.getArguments());
            sb.append("` ").append(cmd.getHelp()).append("\n");
        }
        event.reply(sb.toString());
    }

    private class FilterStack extends FilterCommand {
        FilterStack(Bot bot) {
            super(bot);
            this.name = "stack";
            this.aliases = new String[]{"s", "list"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            StringBuilder out = new StringBuilder("Current Filter Stack:\n");
            String[] descriptions = bot.getFilterManager().get(event.getGuild().getIdLong()).getDescriptions();
            int i = 0;
            for (String filter : descriptions) {
                out.append("`").append(i++).append(".` **").append(filter).append("**\n");
            }
            event.reply(out.toString());
        }
    }

    private class FilterReset extends FilterCommand {
        FilterReset(Bot bot) {
            super(bot);
            this.name = "reset";
            this.aliases = new String[]{"r", "clear", "c"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            bot.getFilterManager().clear(event.getGuild().getIdLong());
            if (settings.getVolume() > 150) {
                handler.getPlayer().setVolume(100);
                settings.setVolume(100);
            }
            event.replySuccess("Filter stack cleared.");
        }
    }

    private class FilterTimescale extends FilterCommand {
        FilterTimescale(Bot bot) {
            super(bot);
            this.name = "timescale";
            this.aliases = new String[]{"speed"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            if (System.getProperty("java.vm.name").contains("arm")) {
                event.replyError("Native binaries for this filter could not be compiled on this platform :(");
                return;
            }
            float val = 1.5f;
            if (!event.getArgs().isEmpty())
                val = Float.parseFloat(event.getArgs());
            if (val <= 0) {
                event.replyError("Argument must be >= 0.");
                return;
            }
            AudioFilterConfig conf = new TimescaleConfig(val);
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }

    private class FilterLowPass extends FilterCommand {
        FilterLowPass(Bot bot) {
            super(bot);
            this.name = "lowpass";
            this.aliases = new String[]{"l"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            AudioFilterConfig conf = new LowPassPcmAudioFilter.Config();
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }

    private class FilterVolume extends FilterCommand {
        FilterVolume(Bot bot) {
            super(bot);
            this.name = "volume";
            this.aliases = new String[]{"v", "vol"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            float vol = 1.5f;
            if (!event.getArgs().isEmpty())
                vol = Float.parseFloat(event.getArgs());
            if (vol <= 0) {
                event.replyError("Argument must be >= 0.");
                return;
            }
            AudioFilterConfig conf = new VolumePcmAudioFilter.Config(vol);
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }

    private class FilterBitCrush extends FilterCommand {
        FilterBitCrush(Bot bot) {
            super(bot);
            this.name = "bitcrush";
            this.aliases = new String[]{"b", "bit"};
        }

        @Override
        public void doCommand(CommandEvent event) {
//            float vol = 1.5f;
//            if (!event.getArgs().isEmpty())
//                vol = Float.parseFloat(event.getArgs());
//            if (vol <= 0) {
//                event.replyError("Argument must be >= 0.");
//                return;
//            }
            AudioFilterConfig conf = new BitCrushPcmAudioFilter.Config();
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }


    private class FilterPitch extends FilterCommand {
        FilterPitch(Bot bot) {
            super(bot);
            this.name = "pitch";
            this.aliases = new String[]{"p"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            if (System.getProperty("java.vm.name").contains("arm")) {
                event.replyError("Native binaries for this filter could not be compiled on this platform :(");
                return;
            }
            float val = 1.5f;
            if (!event.getArgs().isEmpty())
                val = Float.parseFloat(event.getArgs());
            if (val <= 0) {
                event.replyError("Argument must be >= 0.");
                return;
            }
            AudioFilterConfig conf = new PitchConfig(val);
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }

    private class FilterKaraoke extends FilterCommand {
        FilterKaraoke(Bot bot) {
            super(bot);
            this.name = "karaoke";
            this.aliases = new String[]{};
        }

        @Override
        public void doCommand(CommandEvent event) {
//            float vol = 1.5f;
//            if (!event.getArgs().isEmpty())
//                vol = Float.parseFloat(event.getArgs());
//            if (vol <= 0) {
//                event.replyError("Argument must be >= 0.");
//                return;
//            }
            AudioFilterConfig conf = new KaraokeConfig();
            bot.getFilterManager().addFilter(event.getGuild().getIdLong(), conf);
            event.replySuccess("Added to filter stack: **" + conf.getDescription() + "**");
        }
    }

    private class FilterUndo extends FilterCommand {
        FilterUndo(Bot bot) {
            super(bot);
            this.name = "undo";
            this.aliases = new String[]{"u", "pop"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            try {
                AudioFilterConfig conf = bot.getFilterManager().removeLastFilter(event.getGuild().getIdLong());
                event.replySuccess("Removed from filter stack: **" + conf.getDescription() + "**");
            } catch (ArrayIndexOutOfBoundsException e) {
                event.replyWarning("The filter stack was already empty.");
            }
        }
    }

    private class FilterRemove extends FilterCommand {
        FilterRemove(Bot bot) {
            super(bot);
            this.name = "remove";
            this.aliases = new String[]{"rm"};
        }

        @Override
        public void doCommand(CommandEvent event) {
            int val = 0;
            try {
                val = Integer.parseInt(event.getArgs());
                AudioFilterConfig conf = bot.getFilterManager().removeFilter(event.getGuild().getIdLong(), val);
                event.replySuccess("Removed from filter stack: **" + conf.getDescription() + "**");
            } catch (NumberFormatException e) {
                event.replyError("Argument must be an integer.");
            } catch (ArrayIndexOutOfBoundsException e) {
                event.replyError("There is no filter at index " + val + ".");
            }
        }
    }
}
