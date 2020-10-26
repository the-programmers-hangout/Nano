package me.elliott.nano.extensions

import com.gitlab.kordlib.core.entity.User

fun User.descriptor() = "${this.mention} :: ${this.username}#${this.discriminator} :: ID :: ${this.id.value}"

fun User.workingWidth() = 2000 - "**${this.username}** :".length