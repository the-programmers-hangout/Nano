package me.elliott.nano.extensions

import com.gitlab.kordlib.core.entity.Member

fun Member.descriptor() = "${this.mention} :: ${this.username}#${this.discriminator} :: ID :: ${this.id.value}"