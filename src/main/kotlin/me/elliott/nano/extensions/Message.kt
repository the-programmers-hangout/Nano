package me.elliott.nano.extensions

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed

fun MessageEmbed.toEmbedBuilder() =
        EmbedBuilder().apply {
            setTitle(title)
            setDescription(description)
            setFooter(footer?.text, footer?.iconUrl)
            setThumbnail(thumbnail?.url)
            setTimestamp(timestamp)
            setImage(image?.url)
            setColor(colorRaw)
            setAuthor(author?.name)
            fields.addAll(this@toEmbedBuilder.fields)
        }